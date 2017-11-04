package com.example.ankit.camera;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ankit on 27/10/17.
 */

public class MySingleton {

    private static MySingleton mInstance;
    private RequestQueue requestQueue ;
    private static Context context ;

    private MySingleton(Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public  static synchronized MySingleton getmInstance(Context context){
        if(mInstance == null){
            mInstance = new MySingleton(context);
        }
        return mInstance ;
    }

    public RequestQueue getRequestQueue(){

        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public  <T>void addTorequestque(Request<T> request )
    {
        requestQueue.add(request);
    }

}

