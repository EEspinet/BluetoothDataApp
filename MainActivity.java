package kikenet.mywirelesscharge;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Button connectButton;
    private TextView connectStatus;
    private TextView dataText;
    private TextView dataText2;
    private TextView dataText3;
    private TextView dataText4;
    boolean deviceConnected=false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    private int counter = 0;
    private String temp = "";
//    private final String DEVICE_ADDRESS="00:13:EF:02:1C:9B";
    private final String DEVICE_ADDRESS="00:18:EF:00:1F:00";
//    private final String DEVICE_ADDRESS="00:21:13:02:BD:56";
//    private UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private UUID PORT_UUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = (Button)findViewById(R.id.connect_button);
        connectStatus = (TextView)findViewById(R.id.connect_status);
        dataText = (TextView)findViewById(R.id.data_text);
        dataText2 = (TextView)findViewById(R.id.data_text2);
        dataText3 = (TextView)findViewById(R.id.data_text3);
        dataText4 = (TextView)findViewById(R.id.data_text4);

        setUiEnabled(false);
    }

    public void onClickStart(View view) {
        if(BTinit())
        {
            if(BTconnect())
            {
                setUiEnabled(true);
                deviceConnected=true;
                connectStatus.setText("\nConnection Opened!\n");
                beginListenForData();
            }
            else
            {
                connectStatus.setText("\nConnection Closed!\n");
            }
        }
    }

    public boolean BTconnect()
    {
        boolean connected=true;
        try {
            String temp = device.getUuids()[0].toString();
            PORT_UUID = UUID.fromString(temp);
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return connected;
    }

    public void setUiEnabled(boolean bool)
    {
        connectButton.setEnabled(!bool);
    }

    public boolean BTinit()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device does not Support Bluetooth",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please Pair the Device first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
        return found;
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[8192];
//        counter = 0;
        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try
                    {
                        int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string=new String(rawBytes,"UTF-8");
                            final StringTokenizer st = new StringTokenizer(string,",");
                            String newString = string.replace(",","-");
                            final String[] valuesTemp = string.split(",");
//                            final String[] valuesTemp = newString.split("-");
                            handler.post(new Runnable() {
                                public void run()
                                {
                                    if(valuesTemp.length == 4)
                                    {
                                        dataText.setText("Connection Status: " + valuesTemp[0]);
                                        dataText2.setText("Voltage: " + valuesTemp[1]);
                                        dataText3.setText("Current: " + valuesTemp[2]);
                                        dataText4.setText("mW: " + valuesTemp[3]);
                                    }
//                                    dataText.setText("Array length: " + st.countTokens());
//                                    dataText2.setText(string);
//                                    if(!dataText.getText().toString().equalsIgnoreCase("Connection Status: " + valuesTemp[0]))
//                                    {
//                                        dataText.setText("Connection Status: " + valuesTemp.length);
//                                    }

//                                    dataText2.setText(valuesTemp.length);

//                                    if(!dataText2.getText().toString().equalsIgnoreCase("Voltage: " + valuesTemp[1]))
//                                    {
//                                        dataText2.setText("Voltage: " + valuesTemp[1]);
//                                    }
////
//                                    if(!dataText3.getText().toString().equalsIgnoreCase(valuesTemp[2]))
//                                    {
//                                        dataText3.setText("Current: " + valuesTemp[2]);
//                                    }
//
//                                    if(!dataText4.getText().toString().equalsIgnoreCase(valuesTemp[3]))
//                                    {
//                                        dataText4.setText("mW: " + valuesTemp[3]);
//                                    }

//                                    try {
//                                        Thread.sleep(250);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }


//                                    if(temp.equalsIgnoreCase(string))
//                                    {
//                                        //Do Nothing
//                                    }
//                                    else
//                                    {
//                                        dataText.setText("");
//                                        dataText.append(string);
//                                    }

//                                    try {
//                                        Thread.sleep(500);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }

//                                    if(counter > 3)
//                                    {
//                                        dataText.setText("");
//                                        dataText.append(string);
//                                        counter = 1;
//                                    }
//
//                                    else
//                                    {
//                                        dataText.append(string);
//                                        counter++;
//                                    }

//                                    if(dataText.equals(string))
//                                    {
//                                        //Do Nothing
//                                    }
//                                    else
//                                    {
//                                        dataText.setText("");
//                                        dataText.append(string);
//                                    }
//                                    dataText.append(string);
//                                    dataText.append(string);
//                                    String temp = string.toLowerCase();
//                                    if(temp.contains("2"))
//                                    {
//                                        dataText.setText("CHARGING: DISCONNECTED");
//                                    }
//                                    else
//                                    {
//                                        dataText.setText("CHARGING: CONNECTED");
//                                    }
//                                    else
//                                    {
//                                        dataText.setText("Error: Please check the Arduino and Bluetooth Connection..");
//                                    }
//                                    dataText.setText("");
//                                    dataText.append(string);
                                }
                            });

                        }
                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }

    public void onClickStop(View view) throws IOException {
        stopThread = true;
        outputStream.close();
        inputStream.close();
        socket.close();
        setUiEnabled(false);
        deviceConnected=false;
        connectStatus.setText("\nConnection Closed!\n");
        dataText.setText("No Readable Data");
        dataText2.setText("No Readable Data");
        dataText3.setText("No Readable Data");
        dataText4.setText("No Readable Data");
    }
}
