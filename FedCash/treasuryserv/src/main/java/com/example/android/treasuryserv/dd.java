package com.example.android.treasuryserv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class dd extends AppCompatActivity {


    TextView jsonText;
    String stream="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        jsonText= (TextView)findViewById(R.id.textJSON);
        jsonText.setText(stream);
        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task


                URL url = null;
                try {
                    url = new URL("http://api.treasury.io/cc7znvq/47d80ae900e04f2/sql/?q=select%20*%20from%20t1%20where%20year%20=2017%20limit%205");
                    Log.i("recieved data ", "got teh url");

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    Log.i("recieved data ", "got open connection url");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    //System.out.println("Test2");
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        sb.append(line);
                    }
                    stream = sb.toString();
                    //System.out.println("Stream:" + stream);

                    JSONArray myJOSN = new JSONArray(stream);

                    //Log.d("JSON OBJECT RECIEVED", myJOSN.toString());



                    for(int i =0 ;i< myJOSN.length();i++)
                    {
                        //Log.i("Array is = ", myJOSN.get(i).toString());
                        JSONObject jb = (JSONObject) myJOSN.getJSONObject(i);
                        Log.i("Account is =" ,jb.getString("account"));


                        // Log.i("this is teh url " , urlArray.toString());




                    }



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }


            }
        }).start();





    }
}
