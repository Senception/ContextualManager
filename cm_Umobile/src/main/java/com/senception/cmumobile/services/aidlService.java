package com.senception.cmumobile.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.senception.cmumobile.aidl.CManagerInterface;

/**
 * Created by Senception on 20/02/2018.
 */
public class aidlService extends Service{

    private final IBinder localBinder = new LocalBinder();

    final CManagerInterface.Stub mBinder = new CManagerInterface.Stub(){

        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            //does nothing
        }

        @Override
        public int test() throws RemoteException {
            Log.d("Resource","Entrou no test()");
            //Toast.makeText(aidlService.this, "ENtrou no test()", Toast.LENGTH_SHORT).show();
            return 12345;
        }

        public int[] getAvailability(int [] peerList){

            int [] availability = new int[24];
            for (int i = 0; i < peerList.length; i++){
                availability[i] = peerList[i]+10;
                //check the peer's availability and add it to the array list
                //availability[count] = getAvailability(id);
            }
            return availability;
        }
    };

    public class LocalBinder extends Binder {
        public aidlService getService(){
            return aidlService.this;
        }
    }

    @Override
    public void onCreate() {

        Log.d("Resource", "AIDL SERVICE ENTROU NO ONCREATE");

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Resource", "Entrou no bind");
        /*if (intent.getExtras() != null){*/
            return mBinder;
        /*}
        return localBinder;*/
    }

    public void stopForeGround(){
        stopForeground(true);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Aidl service entered on destroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
