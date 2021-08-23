package com.example.myrecyclerview

import android.Manifest
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.RecyclerView
import com.iflytek.cyber.iot.show.core.utils.WifiUtils
import java.util.*
import kotlin.Comparator

class WifiSettings : AppCompatActivity() {
    companion object {
        private const val TAG = "WifiSettings"
        private const val REQUEST_LOCATION_CODE = 10423

    }

    private val scanReceiver = ScanReceiver()

    private var wm: WifiManager? = null

    private var scans: List<ScanResult>? = null
    private val configs = HashMap<String, WifiConfiguration>()

    private var connected: String? = null

    private var adapter: WifiAdapter? = null
    private var wifiSwitch: Switch? = null
    private var ivRefresh: ImageView? = null
    private var refreshContainer: View? = null

    private var progressDialog: ProgressDialog? = null

    private var isScanning = false

    private var context: Context? = null

    @Suppress("DEPRECATION")
    private val connectionReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ConnectivityManager.CONNECTIVITY_ACTION -> {
                    val network = intent.getParcelableExtra<NetworkInfo>(
                        ConnectivityManager.EXTRA_NETWORK_INFO
                    )
                    val detailed = network?.detailedState

                    if (detailed == NetworkInfo.DetailedState.CONNECTED) {
                        WifiUtils.getConnectedSsid(context)?.let { ssid ->
                            if (ssid.isNotEmpty()) {
                                connected = ssid
                            } else {
                                connected = null
                            }
                        } ?: run {
                            connected = null
                        }
                    } else {
                        connected = null
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }
    private var networkCallback: Any? = null // 不声明 NetworkCallback 的类，否则 L 以下会找不到类

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_settings)
        context = this

        if (ActivityCompat.checkSelfPermission(this!!, Manifest.permission.ACCESS_FINE_LOCATION)
            != PermissionChecker.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_CODE
            )
        }
        wm = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        var scanIntentFilter = IntentFilter()
        scanIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        scanIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        registerReceiver(scanReceiver, scanIntentFilter)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {

            var intentFilter = IntentFilter()
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(connectionReceiver, intentFilter)
        } else {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    if (network != null) {
                        super.onAvailable(network)
                    }

                    WifiUtils.getConnectedSsid(context)?.let { ssid ->
                        if (ssid.isNotEmpty()) {
                            connected = ssid
                        } else {
                            connected = null
                        }
                    } ?: run {
                        connected = null
                    }

                    Handler().postDelayed({adapter?.notifyDataSetChanged()},0)

                }

                override fun onLost(network: Network) {
                    super.onLost(network)

                    connected = null

                    Handler().postDelayed({adapter?.notifyDataSetChanged()},0)

                }
            }
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager?.registerNetworkCallback(request, networkCallback)

            this.networkCallback = networkCallback
        }

        initView()
    }

    private fun initView() {
        adapter = context?.let { WifiAdapter(it) }

        val list =findViewById<RecyclerView>(R.id.wifi_list)
        list.addItemDecoration(
            DividerItemDecoration.Builder(list.context)
                .setPadding(resources.getDimensionPixelSize(R.dimen.dp_40))
                .setDividerColor(ContextCompat.getColor(list.context, R.color.dividerLight))
                .setDividerWidth(resources.getDimensionPixelSize(R.dimen.dp_1))
                .build()
        )
        list.adapter = adapter

       findViewById<View>(R.id.back).setOnClickListener {
           finish()
        }

        wifiSwitch = findViewById(R.id.wifi_enabled)
        refreshContainer =findViewById(R.id.refresh_container)
        ivRefresh = findViewById(R.id.refresh)

        ivRefresh?.setOnClickListener {
            if (!isScanning) {
                startScan()
            }
        }

        Handler().postDelayed({
                startScan()
        }, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PermissionChecker.PERMISSION_GRANTED &&
            permissions.isNotEmpty() && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            startScan()
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(scanReceiver)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            unregisterReceiver(connectionReceiver)
        } else {
            (context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
                ?.let { connectivityManager ->
                    val networkCallback =
                        (this.networkCallback as? ConnectivityManager.NetworkCallback)
                            ?: return
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                }
        }
    }

    private fun handleScanResult() {
        configs.clear()
        wm?.let {
            val configuredNetworks = if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }else
                it.configuredNetworks
            if (configuredNetworks?.isNotEmpty() == true)
                for (config in it.configuredNetworks) {
                    val ssid = config.SSID.substring(1, config.SSID.length - 1)
                    configs[ssid] = config
                }

            connected = WifiUtils.getConnectedSsid(context)

            val map = HashMap<String, ScanResult>()
            for (o1 in it.scanResults) {
                val o2 = map[o1.SSID]
                if ((o2 == null || o2.level < o1.level) && o1.level != 0) {
                    map[o1.SSID] = o1
                }
            }
            val list = ArrayList(map.values)
            Collections.sort(list, Comparator { o1, o2 ->
                val c1 = configs[o1.SSID]
                val c2 = configs[o2.SSID]

                if (c1 != null && c1.status == WifiConfiguration.Status.CURRENT) {
                    return@Comparator -1
                } else if (c2 != null && c2.status == WifiConfiguration.Status.CURRENT) {
                    return@Comparator 1
                }

                if (c1 != null && c2 == null) {
                    return@Comparator -1
                } else if (c1 == null && c2 != null) {
                    return@Comparator 1
                }

                o2.level - o1.level
            })
            scans = list
            adapter?.notifyDataSetChanged()
        }
    }

    private fun handleOnItemClick(scan: ScanResult) {
        val context = applicationContext ?: return
        if (connected != scan.SSID) {
            configs[scan.SSID]?.let { config ->
                WifiUtils.connect(context, config.networkId)
                Log.d(TAG, "->clicked connect saved wifi")
                Toast.makeText(context!!, "connect saved wifi clicked", Toast.LENGTH_LONG).show()
            } ?: run {
                if (!WifiUtils.isEncrypted(scan)) {
                    Log.d(TAG, "->clicked no key wifi")
                    Toast.makeText(context!!, "no key wifi clicked", Toast.LENGTH_LONG).show()
                } else {
                    Log.d(TAG, "->clicked no connected wifi")
                    Toast.makeText(context!!, "no connected wifi clicked", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Log.d(TAG ,"->clicked connected wifi")
            Toast.makeText(context!!, "connected wifi clicked", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleOpenWifiInfo(scan: ScanResult) {
        Log.d(TAG, "->handleOpenWifiInfo")
        Toast.makeText(context!!, "wifi info clicked", Toast.LENGTH_LONG).show()
    }

    private fun handleAddManual() {
        Log.d(TAG, "->handleAddManual")
        Toast.makeText(context!!, "wifi handleAddManual clicked", Toast.LENGTH_LONG).show()
    }


    @Suppress("DEPRECATION")
    private fun startScan() {
        if (wm?.isWifiEnabled != true)
            return
        wm?.startScan()

        isScanning = true
    }

    private inner class WifiAdapter internal constructor(context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun getItemViewType(position: Int): Int {
            return when (position) {
                0 -> 2
                itemCount - 1 -> 1
                else -> 0
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                0 ->
                    ItemViewHolder(inflater.inflate(R.layout.item_access_point_2, parent, false))
                1 ->
                    ManualViewHolder(
                        inflater.inflate(
                            R.layout.item_access_point_manual,
                            parent,
                            false
                        )
                    )
                2 -> {
                    val holder = WifiSwitchViewHolder(
                        inflater.inflate(
                            R.layout.item_wifi_switch,
                            parent,
                            false
                        )
                    )
                    wifiSwitch = holder.itemView.findViewById(R.id.wifi_enabled)
                    wifiSwitch?.let { wifiSwitch ->
                        wifiSwitch.isChecked = wm?.isWifiEnabled == true
                        refreshContainer?.visibility =
                            if (wifiSwitch.isChecked) View.VISIBLE else View.GONE
                        Log.d(TAG, "->wifi_enabled once")
                        wifiSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                            Log.d(TAG, "->wifiSwitch|isChecked = $isChecked")
                            if (refreshContainer?.visibility == View.GONE && isChecked) {
                                ivRefresh?.alpha = 0f
                            }
                            refreshContainer?.visibility =
                                if (isChecked) View.VISIBLE else View.GONE

                            progressDialog?.dismiss()
                            progressDialog = ProgressDialog.show(
                                context, null,
                                if (isChecked) "正在开启" else "正在关闭", false, true
                            )

                            Handler().postDelayed({
                                val target = wifiSwitch.isChecked
                                val result = wm?.setWifiEnabled(target)
                                Log.d(TAG, "->target= $target, result = $result")
                                progressDialog?.dismiss()

                                scans = null
                                adapter?.notifyDataSetChanged()

                                if ( wifiSwitch.isChecked == true) {
                                    refreshContainer?.visibility = View.VISIBLE

                                    startScan()
                                } else {
                                    refreshContainer?.visibility = View.GONE
                                }
                            },1000)
                        }
                    }

                    holder
                }
                else ->
                    ItemViewHolder(inflater.inflate(R.layout.item_access_point_2, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ItemViewHolder) {
                scans?.get(position - 1)?.let { scan ->
                    holder.bind(scan)
                }
            }
        }

        override fun getItemCount(): Int {
            val isWifiEnabled = wifiSwitch?.isChecked == true
            return if (isWifiEnabled) (scans?.size ?: 0) + 2 else 1
        }

        internal inner class ManualViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
                    handleAddManual()
                }
            }
        }

        internal inner class WifiSwitchViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView)

        internal inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val icon: ImageView = itemView.findViewById(R.id.icon)
            private val ssid: TextView = itemView.findViewById(R.id.ssid)
            private val status: TextView = itemView.findViewById(R.id.status)
            private val info: ImageView = itemView.findViewById(R.id.info)

            init {
                itemView.setOnClickListener { handleOnItemClick(scans!![this@ItemViewHolder.layoutPosition - 1]) }
                info.setOnClickListener { handleOpenWifiInfo(scans!![this@ItemViewHolder.layoutPosition - 1]) }
            }

            fun bind(scan: ScanResult) {
                if (WifiUtils.isEncrypted(scan)) {
                    icon.setImageResource(R.drawable.ic_wifi_locked_black_24dp)
                } else {
                    icon.setImageResource(R.drawable.ic_wifi_black_24dp)
                }

                ssid.text = scan.SSID

                when {
                    connected == scan.SSID -> {
                        status.visibility = View.VISIBLE
                        status.text = "已连接"
                        info.visibility = View.VISIBLE
                    }
                    configs[scan.SSID] != null -> {
                        status.visibility = View.VISIBLE
                        status.text = "已保存"
                        info.visibility = View.VISIBLE
                    }
                    else -> {
                        status.visibility = View.GONE
                        info.visibility = View.GONE
                    }
                }
            }
        }
    }

    private inner class ScanReceiver
    internal constructor() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                    handleScanResult()
                    isScanning = false
                }
                WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)

                    if (state == WifiManager.WIFI_STATE_ENABLED) {
                        if (wifiSwitch?.isChecked != true)
                            wifiSwitch?.isChecked = true
                    } else if (state == WifiManager.WIFI_STATE_DISABLED) {
                        if (wifiSwitch?.isChecked != false)
                            wifiSwitch?.isChecked = false
                    }
                }
            }
        }
    }
}