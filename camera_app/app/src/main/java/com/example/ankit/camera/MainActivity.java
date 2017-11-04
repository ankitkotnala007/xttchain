package com.example.ankit.camera;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CAPTURE = 1;
    ImageView result_photo;

    String server_url = "http://192.168.43.195:8080/HashHacks/reg";
    AlertDialog.Builder builder ;
    StringBuilder strb ;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        Button click = (Button)findViewById(R.id.takephoto);
        result_photo = (ImageView)findViewById(R.id.imageView);

        spinner = (Spinner)findViewById(R.id.spinner);

        if(!hasCamera())
        {
            click.setEnabled(false);
        }

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        spin();


    }

    private void spin(){
        List<String> spin = new ArrayList<String>();
        spin.add("Delhi");
        spin.add("Agra");
        spin.add("Bombay");
        spin.add("Meerut");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                 this,android.R.layout.simple_spinner_item,spin);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private boolean hasCamera() {
       return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void launchCamera()
    {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i , REQUEST_CAPTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
           Bitmap  photo = (Bitmap) extras.get("data");
            result_photo.setImageBitmap(photo);

            TextRecognizer txtRecog = new TextRecognizer.Builder(getApplicationContext()).build();

            if(txtRecog.isOperational())
            {
                Log.i("Text Recog","is Operational");

                Frame frame = new Frame.Builder().setBitmap(photo).build();
                SparseArray<TextBlock> items = txtRecog.detect(frame);
                 strb = new StringBuilder();

                for(int j=0 ; j<items.size() ; j++)
                {
                    TextBlock item = items.valueAt(j);
                    strb.append(item.getValue());
                }
                Toast.makeText(this, strb.toString(), Toast.LENGTH_SHORT).show();
                sendData();
            }
            else{
                Log.i("Text Recog","is not Recognised");
            }


        }
    }

    private void sendData() {

                final String name ;
                name = strb.toString();
       final String loc = spinner.getSelectedItem().toString();
        builder = new AlertDialog.Builder(MainActivity.this);
        Toast.makeText(this, strb.toString(), Toast.LENGTH_SHORT).show();

                Toast.makeText(MainActivity.this, "Above -- RequestMethod", Toast.LENGTH_SHORT).show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                builder.setTitle("Server Response");
                                builder.setMessage("Respone :"+response);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(MainActivity.this, "Error.. ", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {


                        Map<String,String> params = new HashMap<String, String>();
                        params.put("name",name);
                        params.put("loc",loc);

                        //result_photo.setImageBitmap(null);
                        return params;
                    }

                };

                MySingleton.getmInstance(MainActivity.this).addTorequestque(stringRequest);


        }

}




