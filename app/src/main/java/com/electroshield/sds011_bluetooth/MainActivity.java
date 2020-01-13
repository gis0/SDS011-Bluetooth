package com.electroshield.sds011_bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

//import com.example.bluetooth1.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {
    private static final int MESSAGE_READ=1;
    private static final String TAG = "MainActivity_TAG";
    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ArrayList mArrayAdapter = new ArrayList<>();
    final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    private UUID myUUID;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;
    private StringBuilder sb = new StringBuilder();
    public TextView textPM2_5;
    public TextView textPM10 ;
    public TextView textBuffer;
    Handler mHandler;

    private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // TextView textPM2_5 = findViewById(R.id.textViewPM2_5);
        //TextView textPM10 = findViewById(R.id.textViewPM10);
       // TextView textBuffer = findViewById(R.id.textBuffer);
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this hardware platform", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if(!mBluetoothAdapter.isEnabled())

        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        setup();

        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
            if (msg.what==1){
                TextView textPM2_5 = findViewById(R.id.textViewPM2_5);
                TextView textPM10 = findViewById(R.id.textViewPM10);
                TextView textBuffer = findViewById(R.id.textBuffer);
               // String buffer= msg.obj.toString();
                byte[] readBuf = (byte[]) msg.obj;

               if (readBuf[0]==-64)
               {
                   textPM2_5.setText ((((float)((((int)readBuf[2]&0xff) * 256) + ((int)readBuf[1]&0xff))) / 10)+ "");
                   textPM10.setText ((((float)((((int)readBuf[4]&0xff) * 256) + ((int)readBuf[3]&0xff))) / 10) + "");
                   textBuffer.append("\n"+bytesToHex(readBuf));
               }
               else
                   {
                       textBuffer.setText(bytesToHex(readBuf));
               }
                String strIncom = new String(readBuf, 0, msg.arg1);
             // String hex=  String.format("%x", new BigInteger(1, strIncom.getBytes()));
               //int i = readBuf[0];
                  //Log.d(TAG, hex);
                  // text.append("\n"+(int)readBuf[0]);
                sb.append(strIncom);


                // формируем строку
                //text.append(strIncom);// определяем символы конца строки
                int endOfLineIndex = sb.indexOf("\r\n");

                if (endOfLineIndex > 0) {
                    String sbprint = sb.substring(0, endOfLineIndex);
                  //  Log.d(TAG, "AAAAAAAAAAAAAAAAAA "+ sbprint);
                 //   text.append(sbprint);
                    sb.delete(0, sb.length());
                }                                       // если встречаем конец строки,


                }
            }
        };
    }
/*
    @Override
    public void onStart() {
        super.onStart();
        BluetoothAdapter bluetooth= BluetoothAdapter.getDefaultAdapter();
        if(bluetooth!=null)
        {
// С Bluetooth все в порядке.
        }
        if (bluetooth.isEnabled()) {
            // Bluetooth включен. Работаем.
            String status;
            if(bluetooth.isEnabled()){
                String mydeviceaddress= bluetooth.getAddress();
                String mydevicename= bluetooth.getName();
                int state= bluetooth.getState();
              //  status= mydevicename+" : "+ mydeviceaddress+" : "+ state;

                Toast.makeText(this, status, Toast.LENGTH_LONG).show();
                Set<BluetoothDevice> pairedDevices= mBluetoothAdapter.getBondedDevices();
// Если список спаренных устройств не пуст

                if(pairedDevices.size()>0){
// проходимся в цикле по этому списку
                    for(BluetoothDevice device: pairedDevices){
// Добавляем имена и адреса в mArrayAdapter, чтобы показать
// через ListView
                        mArrayAdapter.add(device.getName()+"\n"+ device.getAddress());
                        //  text.setText(device.getName()+"\n"+ device.getAddress()+"\n");
                        text.append(device.getName()+"\n"+ device.getAddress()+"\n");

                    }
                }
            }
            else
            {
               // status="Bluetooth выключен";
            }


        }
        else
        {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


    }
*/

    @Override
    protected void onStart(){  // Запрос на включение Bluetooth
        super.

    onStart();


}
    private void setup() { // Создание списка сопряжённых Bluetooth-устройств

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) { // Если есть сопряжённые устройства


            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("FC:A8:9A:00:3F:4D");

            myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
            myThreadConnectBTdevice.start();  // Запускаем поток для подключения Bluetooth


        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();


    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myThreadConnectBTdevice!=null) myThreadConnectBTdevice.cancel();

    }

    private class ThreadConnectBTdevice extends Thread { // Поток для коннекта с Bluetooth

        private BluetoothSocket bluetoothSocket = null;

        private ThreadConnectBTdevice(BluetoothDevice device) {

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() { // Коннект

            boolean success = false;

            try {
                bluetoothSocket.connect();
                success = true;
            }

            catch (IOException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Нет коннекта, проверьте Bluetooth-устройство с которым хотите соединица!", Toast.LENGTH_LONG).show();
                      //  listViewPairedDevice.setVisibility(View.VISIBLE);
                    }
                });

                try {
                    bluetoothSocket.close();
                }

                catch (IOException e1) {

                    e1.printStackTrace();
                }
            }

            if(success) {  // Если законнектились, тогда открываем панель с кнопками и запускаем поток приёма и отправки данных

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                       // ButPanel.setVisibility(View.VISIBLE); // открываем панель с кнопками
                    }
                });

                myThreadConnected = new ThreadConnected(bluetoothSocket);
                myThreadConnected.start(); // запуск потока приёма и отправки данных
            }
        }



        public void cancel() {

            Toast.makeText(getApplicationContext(), "Close - BluetoothSocket", Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }

    } // END ThreadConnectBTdevice:

        private class ThreadConnected extends Thread {    // Поток - приём и отправка данных

            private final InputStream connectedInputStream;
            private final OutputStream connectedOutputStream;

            private String sbprint;

            public ThreadConnected(BluetoothSocket socket) {

                InputStream in = null;
                OutputStream out = null;

                try {
                    in = socket.getInputStream();
                    out = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                connectedInputStream = in;
                connectedOutputStream = out;
            }

            @Override
            public void run() { // Приём данных

                while (true) {
                    try {
                        byte[] buffer = new byte[10];


                        int bytes = connectedInputStream.read(buffer);
                        //if (buffer[0]==0xAA)
                          Log.d(TAG,  "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+bytes);
                            String strIncom = new String(buffer, 0, bytes);
                      //  String strIncom = buffer.toString();
                       //float i= (float)((buffer[3] * 256) + buffer[2]);
                       // String strIncom= buffer.toString() ;

// посылаем прочитанные байты главной деятельности


                        sbprint = strIncom;
                      //  Log.d(TAG,  sbprint);
                        //Log.d(TAG,  Float.toString(i));
                        mHandler.obtainMessage(MESSAGE_READ, bytes,-1, buffer)
                                .sendToTarget();



/*
                           runOnUiThread(new Runnable() { // Вывод данных

                                @Override
                                public void run() {
                                    Log.d(TAG,  sbprint);
                                    text.append(sbprint+ " %");

                                }
                            });
*/

                    } catch (IOException e) {
                        break;
                    }
                }
            }

            public void write(byte[] buffer) {
                try {
                    connectedOutputStream.write(buffer);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }





    }