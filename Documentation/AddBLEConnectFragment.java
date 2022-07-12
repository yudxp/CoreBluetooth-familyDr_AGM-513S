package com.allmedicus.linkdr.linkdr20;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.allmedicus.linkdr.linkdr20.BluetoothLeService;
import com.allmedicus.linkdr.linkdr20.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class AddBLEConnectFragment extends Fragment implements MainActivity.OnBackPressedListener {
    private static final String DeviceAddress = "saveAddress";
    private static final String DeviceName = "saveName";
    /* access modifiers changed from: private */
    public static final String TAG = "AddBLEConnectFragment";
    TextView deviceConnectState;
    TextView deviceConnectStatePercent;
    /* access modifiers changed from: private */
    public int glucose;
    /* access modifiers changed from: private */
    public int hour;
    ArrayList<String> logdata;
    /* access modifiers changed from: private */
    public BluetoothLeService mBluetoothLeService;
    /* access modifiers changed from: private */
    public StringBuilder mBuff = new StringBuilder();
    /* access modifiers changed from: private */
    public boolean mConnected = false;
    /* access modifiers changed from: private */
    public DBOpenHelper mDBOpenHelper;
    /* access modifiers changed from: private */
    public String mDeviceAddress;
    private String mDevieName;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                    boolean unused = AddBLEConnectFragment.this.mConnected = false;
                    AddBLEConnectFragment.this.updateConnectionState(C0469R.string.ble_disconnect);
                } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    AddBLEConnectFragment.this.CharacteristiscRead();
                    AddBLEConnectFragment.this.updateConnectionState(C0469R.string.ble_connect);
                } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                    String stringExtra = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                    AddBLEConnectFragment addBLEConnectFragment = AddBLEConnectFragment.this;
                    StringBuilder access$500 = AddBLEConnectFragment.this.mBuff;
                    access$500.append(stringExtra);
                    StringBuilder unused2 = addBLEConnectFragment.mBuff = access$500;
                    String sb = AddBLEConnectFragment.this.mBuff.toString();
                    AddBLEConnectFragment.this.mBuff.setLength(0);
                    if (!sb.equals("0000002000000000000000000000200000002000") && sb.length() != 2 && sb.length() != 1 && sb.length() == 40) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        int i = 0;
                        while (i < sb.length()) {
                            int i2 = i + 2;
                            byteArrayOutputStream.write(Integer.parseInt(sb.substring(i, i2), 16));
                            i = i2;
                        }
                        String str = new String(byteArrayOutputStream.toByteArray(), Charset.forName("UTF-8"));
                        String substring = str.substring(4, 14);
                        AddBLEConnectFragment.this.stNumberic = str.substring(0, 3);
                        String unused3 = AddBLEConnectFragment.this.stYear = str.substring(4, 6);
                        String unused4 = AddBLEConnectFragment.this.stMonth = str.substring(6, 8);
                        String unused5 = AddBLEConnectFragment.this.stDay = str.substring(8, 10);
                        String unused6 = AddBLEConnectFragment.this.stHour = str.substring(10, 12);
                        String unused7 = AddBLEConnectFragment.this.stMinute = str.substring(12, 14);
                        AddBLEConnectFragment.this.stGlucose = str.substring(15, 18);
                        AddBLEConnectFragment.this.stEvent = str.substring(19, 20);
                        if (substring.equals("TotalValue")) {
                            AddBLEConnectFragment.this.stTotalNum = str.substring(15, 18);
                            return;
                        }
                        float parseInt = (float) Integer.parseInt(AddBLEConnectFragment.this.stTotalNum);
                        if (String.format("%03d", new Object[]{Integer.valueOf(AddBLEConnectFragment.this.num)}).equals(AddBLEConnectFragment.this.stNumberic)) {
                            int unused8 = AddBLEConnectFragment.this.hour = Integer.parseInt(AddBLEConnectFragment.this.stHour);
                            int unused9 = AddBLEConnectFragment.this.glucose = Integer.parseInt(AddBLEConnectFragment.this.stGlucose);
                            AddBLEConnectFragment.this.stGlucose = String.valueOf(AddBLEConnectFragment.this.glucose);
                            AddBLEConnectFragment.this.dateSetting();
                            AddBLEConnectFragment.this.timeSetting();
                            AddBLEConnectFragment.this.setStEvent(AddBLEConnectFragment.this.stEvent);
                            AddBLEConnectFragment.this.logdata.add(AddBLEConnectFragment.this.stDate + ", " + (AddBLEConnectFragment.this.stHour + ":" + AddBLEConnectFragment.this.stMinute) + ", " + AddBLEConnectFragment.this.stGlucose + ", " + AddBLEConnectFragment.this.stEvent);
                            if (AddBLEConnectFragment.this.mDBOpenHelper.selectAll()) {
                                AddBLEConnectFragment.this.mDBOpenHelper.insertGlucoseColumn(AddBLEConnectFragment.this.stDate, AddBLEConnectFragment.this.saveMode, AddBLEConnectFragment.this.stTime, AddBLEConnectFragment.this.stGlucose, AddBLEConnectFragment.this.stEvent, "0", "", "0", "", "", "", "");
                            } else if (AddBLEConnectFragment.this.mDBOpenHelper.checkResult(AddBLEConnectFragment.this.saveMode, AddBLEConnectFragment.this.stDate, AddBLEConnectFragment.this.stTime, AddBLEConnectFragment.this.stGlucose, AddBLEConnectFragment.this.stEvent)) {
                                AddBLEConnectFragment.this.mDBOpenHelper.insertGlucoseColumn(AddBLEConnectFragment.this.stDate, AddBLEConnectFragment.this.saveMode, AddBLEConnectFragment.this.stTime, AddBLEConnectFragment.this.stGlucose, AddBLEConnectFragment.this.stEvent, "0", "", "0", "", "", "", "");
                            }
                            AddBLEConnectFragment.this.deviceConnectStatePercent.setText(String.valueOf(Math.round((((float) AddBLEConnectFragment.this.num) / parseInt) * 100.0f)) + " %");
                            if (AddBLEConnectFragment.this.stTotalNum.equals(AddBLEConnectFragment.this.stNumberic)) {
                                Toast.makeText(context, C0469R.string.ble_completed, 0).show();
                                AddBLEConnectFragment.this.saveLogFile();
                                ((BottomNavigationView) AddBLEConnectFragment.this.getActivity().findViewById(C0469R.C0471id.navigation)).setSelectedItemId(C0469R.C0471id.navigation_report);
                                AddBLEConnectFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(C0469R.C0471id.content, ReportLogBookFragment.newInstance()).commit();
                                AddBLEConnectFragment.this.mDBOpenHelper.close();
                                AddBLEConnectFragment.this.mBluetoothLeService.disconnect();
                                AddBLEConnectFragment.this.mBluetoothLeService.close();
                                int unused10 = AddBLEConnectFragment.this.num = 1;
                            }
                            int unused11 = AddBLEConnectFragment.this.num = AddBLEConnectFragment.this.num + 1;
                        }
                    }
                }
            }
        }
    };
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothLeService unused = AddBLEConnectFragment.this.mBluetoothLeService = ((BluetoothLeService.LocalBinder) iBinder).getService();
            if (!AddBLEConnectFragment.this.mBluetoothLeService.initialize()) {
                Log.e(AddBLEConnectFragment.TAG, "Unable to initialize Bluetooth");
                AddBLEConnectFragment.this.getActivity().finish();
            }
            AddBLEConnectFragment.this.mBluetoothLeService.connect(AddBLEConnectFragment.this.mDeviceAddress);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            BluetoothLeService unused = AddBLEConnectFragment.this.mBluetoothLeService = null;
        }
    };
    /* access modifiers changed from: private */
    public int num = 1;
    /* access modifiers changed from: private */
    public String saveMode = "1";
    String stChangeEvent;
    String stDate;
    /* access modifiers changed from: private */
    public String stDay;
    String stEvent;
    String stGlucose;
    /* access modifiers changed from: private */
    public String stHour;
    /* access modifiers changed from: private */
    public String stMinute;
    /* access modifiers changed from: private */
    public String stMonth;
    String stNumberic;
    String stTime;
    String stTotalNum;
    /* access modifiers changed from: private */
    public String stYear;

    public static AddBLEConnectFragment newInstance(String str, String str2) {
        AddBLEConnectFragment addBLEConnectFragment = new AddBLEConnectFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DeviceName, str);
        bundle.putString(DeviceAddress, str2);
        addBLEConnectFragment.setArguments(bundle);
        return addBLEConnectFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.logdata = new ArrayList<>();
        this.logdata.clear();
    }

    /* access modifiers changed from: private */
    public void saveLogFile() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "/temp/Allmedicus");
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        String str = "allmedicusLogRecord" + ".csv";
        File file2 = new File(file, str);
        try {
            if (!file2.exists()) {
                file2.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(file + "/" + str));
            String str2 = "";
            fileOutputStream.write("No.,Date ,Time ,Glucose,Event\n".getBytes());
            if (this.logdata.size() != 0) {
                int i = 0;
                while (i < this.logdata.size()) {
                    int i2 = i + 1;
                    str2 = str2 + i2 + "," + this.logdata.get(i) + "\n";
                    i = i2;
                }
            }
            fileOutputStream.write(str2.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException unused) {
        }
    }

    /* access modifiers changed from: package-private */
    public void dateSetting() {
        this.stDate = "20" + this.stYear + "-" + this.stMonth + "-" + this.stDay;
    }

    /* access modifiers changed from: package-private */
    public void timeSetting() {
        int parseInt = Integer.parseInt(this.stMinute);
        this.stTime = String.format(Locale.KOREA, "%02d:%02d", new Object[]{Integer.valueOf(this.hour), Integer.valueOf(parseInt)});
    }

    /* access modifiers changed from: private */
    public void setStEvent(String str) {
        if (str.equals("0")) {
            this.stChangeEvent = "0";
        } else if (str.equals("1")) {
            this.stChangeEvent = "3";
        } else if (str.equals("2")) {
            this.stChangeEvent = "2";
        } else if (str.equals("3")) {
            this.stChangeEvent = "4";
        }
        this.stEvent = this.stChangeEvent;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        InputStream inputStream;
        ViewGroup viewGroup2 = (ViewGroup) layoutInflater.inflate(C0469R.layout.add_bluetooth_connected, viewGroup, false);
        this.mDBOpenHelper = new DBOpenHelper(getActivity());
        this.mDBOpenHelper.open();
        this.deviceConnectState = (TextView) viewGroup2.findViewById(C0469R.C0471id.textView_connect);
        this.deviceConnectStatePercent = (TextView) viewGroup2.findViewById(C0469R.C0471id.textView_connect_percent);
        try {
            inputStream = getResources().getAssets().open("ble_connect.png");
            try {
                ((ImageView) viewGroup2.findViewById(C0469R.C0471id.bluetooth_Connect)).setImageBitmap(BitmapFactory.decodeStream(inputStream));
                inputStream.close();
            } catch (Exception unused) {
            }
        } catch (Exception unused2) {
            inputStream = null;
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (getArguments() != null) {
            this.mDevieName = getArguments().getString(DeviceName);
            this.mDeviceAddress = getArguments().getString(DeviceAddress);
        }
        Intent intent = new Intent(getActivity(), BluetoothLeService.class);
        FragmentActivity activity = getActivity();
        ServiceConnection serviceConnection = this.mServiceConnection;
        getActivity();
        activity.bindService(intent, serviceConnection, 1);
        return viewGroup2;
    }

    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (this.mBluetoothLeService != null) {
            boolean connect = this.mBluetoothLeService.connect(this.mDeviceAddress);
            PrintStream printStream = System.out;
            printStream.println("Connect request result=" + connect);
            if (!connect) {
                this.mBluetoothLeService.disconnect();
                this.mBluetoothLeService.close();
            }
        }
    }

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mGattUpdateReceiver);
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(this.mServiceConnection);
        this.mBluetoothLeService = null;
    }

    /* access modifiers changed from: private */
    public void updateConnectionState(final int i) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                AddBLEConnectFragment.this.deviceConnectState.setText(i);
            }
        });
    }

    public void CharacteristiscRead() {
        BluetoothGattCharacteristic characteristic = this.mBluetoothLeService.mBluetoothGatt.getService(UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb")).getCharacteristic(UUID.fromString(BluetoothLeSampleGattAttributes.IVT_MEASUREMENT));
        if (this.mNotifyCharacteristic != null) {
            this.mBluetoothLeService.setCharacteristicNotification(this.mNotifyCharacteristic, false);
            this.mNotifyCharacteristic = null;
        }
        this.mBluetoothLeService.readCharacteristic(characteristic);
        this.mNotifyCharacteristic = characteristic;
        this.mBluetoothLeService.setCharacteristicNotification(characteristic, true);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        onBack();
        return true;
    }

    public void onBack() {
        try {
            ((MainActivity) getActivity()).setOnBackPressedListener((MainActivity.OnBackPressedListener) null);
            getActivity().getSupportFragmentManager().beginTransaction().replace(C0469R.C0471id.content, AddBLEFragment.newInstance()).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).setOnBackPressedListener(this);
    }
}
