package com.example.android.fedcash;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.cert.Extension;
import java.util.ArrayList;

import treasuryaidl.extension;

public class MainActivity extends AppCompatActivity {

    EditText year;
    Button MonthlyCashButton;
    Button ClearHistory;

    private String file = "DataFile.txt";

    private ArrayList<String> DataList;
    private ArrayAdapter<String> array_adapter;



    private ListView reqHistory;

    private boolean firstInteraction;

    private Extension aidl_file;

    private boolean Bound = false;
    private boolean ishowinh = false;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        getUIElements();

        recordHistory();


    }


    protected void onResume()
    {
        super.onResume();
        if (!Bound) {
            boolean b = false;

            Intent i = new Intent(extension.class.getName());
            i.setComponent(new ComponentName("com.example.android.tresuryserv", "com.example.android.tresuryserv.MainActivity"));
            b = bindService(i, this.myConnection, Context.BIND_AUTO_CREATE);

            if (b) {
                Toast toast = Toast.makeText(getApplicationContext(), "Bound to Service", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Binding Failed", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }





    private void recordHistory()
    {

        if(getFileStreamPath(file).exists())
            firstInteraction=true;
        else
            firstInteraction=false;

        if(firstInteraction)
        {
            try {
                loadPrevData();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("this file doesnt exits");
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.list_item, DataList);
        reqHistory.setAdapter(listAdapter);

    }

    private void loadPrevData() throws FileNotFoundException
    {

        FileInputStream input = openFileInput(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        String line  = "";

        try {
            //Loop through each line of file
            while(null!=(line=reader.readLine()))
            {
                System.out.println(line);
                //Add it to history list
                DataList.add(line);
            }
            for(int i=0;i<DataList.size();i++)
            {
                System.out.println(DataList.get(i));
            }
            reader.close();
        }
        catch (IOException e) {
            System.out.println("Error loading history...");
        }


    }

    private void getUIElements()
    {

        MonthlyCashButton = (Button)findViewById(R.id.MonthlyCash);
        year = (EditText)findViewById(R.id.UserInput);
        reqHistory = (ListView)findViewById(R.id.history_view);
        ClearHistory=(Button)findViewById(R.id.prevDataClear);

        DataList= new ArrayList<String>();

        ClearHistory.setOnClickListener(clearHistoryListener);
        MonthlyCashButton.setOnClickListener(getMonthlyDataListener);

    }



    public View.OnClickListener clearHistoryListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            deleteHistory();
        }
    };


    public View.OnClickListener getMonthlyDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i("you clicked monthy data","");


            String entered_year = year.getText().toString();
            int yearInt= Integer.parseInt(entered_year);

            if((yearInt < 2016) || (yearInt > 2006))
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Year within the range",Toast.LENGTH_LONG);
                toast.show();
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(),"Year not in the range",Toast.LENGTH_LONG);
                toast.show();

            }

                if(Bound)
                {
                    int n = Integer.parseInt(year.getText().toString());
                    System.out.println("this is my n "+n);

                    Toast toast = Toast.makeText(getApplicationContext(),"Now Bounded and year shown correct",Toast.LENGTH_LONG);

                    //Add to history
                    try {
                        writeHistory(year.getText().toString());
                    }
                    catch (FileNotFoundException e) {
                        System.out.println("Error writing line to history..");
                    }
                }
                else
                {
                    System.out.println("Error writing service error to history..");

                }

        }
    };


    private final ServiceConnection myConnection = new ServiceConnection()
    {

        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            aidl_file = Extension.Stub.asInterface(iBinder);
            Bound = true;
            System.out.println("Services is connected with the AIDL file!");
        }

        public void onServiceDisconnected(ComponentName componentName)
        {
            aidl_file = null;
            Bound = false;
            System.out.println("Services is not connected with the AIDL file!");

        }
    };


    protected void onDestroy()
    {
        if(Bound)
        {
            //funConnection will handle the rest via OnServiceDisconnected
            unbindService(this.myConnection);
        }
        super.onDestroy();
    }


    private void deleteHistory()
    {
        if(firstInteraction) {
            getFileStreamPath(file).delete();
            firstInteraction = false;
            DataList.clear();
            reqHistory.invalidateViews();
            System.out.println(" History Deleted");
        }
    }



    private void writeHistory(String s) throws FileNotFoundException
    {
        System.out.println(getFileStreamPath(file));
        FileOutputStream output = openFileOutput(file,MODE_APPEND);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)));

        writer.println(s);
        writer.close();

        DataList.add(s);
        firstInteraction = true;
        reqHistory.invalidateViews();
    }





}
