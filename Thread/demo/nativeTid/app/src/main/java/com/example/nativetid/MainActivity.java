package com.example.nativetid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.nativetid.databinding.ActivityMainBinding;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private ActivityMainBinding binding;
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
        try {
            init();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void init() throws IllegalAccessException {
        Thread thread = new Thread("demo_thread"){
            @Override
            public void run() {
                try {
                    sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "->>>Thread对象的run方法被执行了");
            }
        };
        //线程启动
        thread.start();
        long peer = getNativePeer(thread);
        Log.d(TAG, "->>>tid = " + getTid(peer));

    }

    public static final long getNativePeer(Thread t)throws IllegalAccessException{
        try {
            Field nativePeerField = Thread.class.getDeclaredField("nativePeer");
            nativePeerField.setAccessible(true);
            Long nativePeer = (Long) nativePeerField.get(t);
            return nativePeer;
        } catch (NoSuchFieldException e) {
            throw new IllegalAccessException("failed to get nativePeer value");
        } catch (IllegalAccessException e) {
            throw e;
        }
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public static native int getTid(long nativePeer);
}