package  com.example.android.treasuryserv;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.security.cert.Extension;

import treasuryaidl.Extension;

public class MainActivity extends Service
{

    private final Extension.Stub serviceConnector = new Extension.Stub() {


        public  int getMonthlyCash(int year)
        {
            return 1234;
        }


    };


    public IBinder onBind(Intent intent) {
        return serviceConnector;
    }


}