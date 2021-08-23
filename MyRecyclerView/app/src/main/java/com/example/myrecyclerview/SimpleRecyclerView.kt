package com.example.myrecyclerview

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *简单的rv举例，把adapter写在一起
 */
class SimpleRecyclerView : AppCompatActivity() {
    companion object {
        private const val TAG = "SimpleRecyclerView"

    }
    //kotlin中要使用list.add,需要使用MutableList
    private var mList: MutableList<SettingItem> = ArrayList<SettingItem>()
    private var recyclerView: RecyclerView? = null
    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_recycler_view)
        context = this
        initView()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.moresettings)
        var ll: LinearLayoutManager = LinearLayoutManager(this)
        ll.orientation = LinearLayoutManager.VERTICAL
        recyclerView?.layoutManager = ll
        recyclerView?.adapter = SimpleAdapter(mList)
    }

    private fun initView() {
        var TxName: Array<String> = resources.getStringArray(R.array.settingsname)
        for(i in TxName.indices){
            mList.add(SettingItem(TxName[i], resources.obtainTypedArray(R.array.settingsim).getResourceId(i, 0)))
        }
    }

    private inner class SimpleAdapter constructor(list :List<SettingItem>) : RecyclerView.Adapter<SimpleAdapter.Holder>() {
        private var mSettingItem: List<SettingItem>? = null

        inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
            var textView : TextView? = null
            var imageView : ImageView? = null

            //Hoder构造器初始化
            init {
                textView = itemView.findViewById(R.id.settingname)
                imageView = itemView.findViewById(R.id.settinginfo)
                imageView?.setOnClickListener(View.OnClickListener {
                    Log.d(TAG, "->onclicked imageview")
                    //可以直接通过getposition获取到postion的值
                    Toast.makeText(context!!, "onclicked imageview pos = $position", Toast.LENGTH_LONG).show()
                })
                itemView?.setOnClickListener(View.OnClickListener {
                    Log.d(TAG, "->onclicked item")
                    Toast.makeText(context!!, "onclicked item pos = $position", Toast.LENGTH_LONG).show()
                })
            }

        }
        //SimpleAdapter构造器初始化
        init {
            mSettingItem = list
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_settings, parent, false)
            var viewHolder: Holder = Holder(view)
            return viewHolder
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            var settingItem = mSettingItem?.get(position)
            holder.textView?.text = settingItem?.getName()
            settingItem?.getInfo()?.let { holder.imageView?.setImageResource(it) }

        }

        override fun getItemCount(): Int {
            return mSettingItem?.let { it.size } ?: run { 0 }
        }
    }
}
