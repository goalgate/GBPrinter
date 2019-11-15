package cn.cbsd.cbsdprintersupport.UI;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Set;

import cn.cbsd.cbsdprintersupport.Event.BluetoothAdapter_ACTION_DISCOVERY_FINISHED_Event;
import cn.cbsd.cbsdprintersupport.Event.BluetoothDevice_ACTION_FOUND_Event;
import cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncLogic;
import cn.cbsd.cbsdprintersupport.R;

import static cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncConstant.BLUETOOTH_PRINTERTYPE;

public class BluetoothDeviceList extends Activity {

    private ListView lvPairedDevice = null;
    private Button btDeviceScan = null;
    private ArrayAdapter<String> DevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.dialog_bluetooth_list);
        lvPairedDevice = (ListView) findViewById(R.id.lvPairedDevices);
        btDeviceScan = (Button) findViewById(R.id.btBluetoothScan);
        btDeviceScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                v.setVisibility(View.GONE);
                discoveryDevice();
            }
        });
        EventBus.getDefault().register(this);

        initBluetooth();
    }

    private void initBluetooth() {
        if (BluetoothFuncLogic.getInstance().BluetoothOpen(this)) {
            BluetoothFuncLogic.getInstance().BluetoothDisConnect();
            getDeviceList();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (BluetoothFuncLogic.getInstance().getmBluetoothAdapter() != null) {
            BluetoothFuncLogic.getInstance().getmBluetoothAdapter().cancelDiscovery();

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetBluetoothDevice_ACTION_FOUND_Event(BluetoothDevice_ACTION_FOUND_Event event){
        BluetoothDevice device = event.getDevice();
        if (device.getBondState() != BluetoothDevice.BOND_BONDED && device.getBluetoothClass().getDeviceClass() == BLUETOOTH_PRINTERTYPE) {
            DevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetBluetoothAdapter_ACTION_DISCOVERY_FINISHED_Event(BluetoothAdapter_ACTION_DISCOVERY_FINISHED_Event event){
        setProgressBarIndeterminateVisibility(false);
        setTitle(R.string.select_bluetooth_device);
        if (DevicesArrayAdapter.getCount() == 0) {
            String noDevices = getResources().getText(
                    R.string.none_bluetooth_device_found).toString();
            DevicesArrayAdapter.add(noDevices);
        }
    }

    protected void getDeviceList() {
        DevicesArrayAdapter = new ArrayAdapter<>(this,
                R.layout.bluetooth_device_name_item);
        lvPairedDevice.setAdapter(DevicesArrayAdapter);
        lvPairedDevice.setOnItemClickListener(mDeviceClickListener);
        Set<BluetoothDevice> pairedDevices = BluetoothFuncLogic.getInstance().getmBluetoothAdapter().getBondedDevices();
        DevicesArrayAdapter.add(getString(R.string.str_title_pairedev));
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                DevicesArrayAdapter.add(device.getName() + "\n"
                        + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            DevicesArrayAdapter.add(noDevices);
        }
    }

//    private final BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            switch (action) {
//                case BluetoothDevice.ACTION_FOUND:  //发现蓝牙
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    if (device.getBondState() != BluetoothDevice.BOND_BONDED && device.getBluetoothClass().getDeviceClass() == BLUETOOTH_PRINTERTYPE) {
//                        DevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//                    }
//                    break;
//                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                    setProgressBarIndeterminateVisibility(false);
//                    setTitle(R.string.select_bluetooth_device);
//                    if (DevicesArrayAdapter.getCount() == 0) {
//                        String noDevices = getResources().getText(
//                                R.string.none_bluetooth_device_found).toString();
//                        DevicesArrayAdapter.add(noDevices);
//                    }
//                    break;
//                case BluetoothDevice.ACTION_PAIRING_REQUEST:
//                    Log.d("BlueToothTestActivity", "ACTION_PAIRING_REQUEST");
//                    BluetoothDevice btDevice = intent
//                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    try {
//                        ClsUtils.setPin(btDevice.getClass(), btDevice, "0000");
//                        ClsUtils.createBond(btDevice.getClass(), btDevice);
//                        ClsUtils.cancelPairingUserInput(btDevice.getClass(),btDevice);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    private void discoveryDevice() {
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scaning);
        DevicesArrayAdapter.add(getString(R.string.str_title_newdev));
        BluetoothFuncLogic.getInstance().BluetoothDiscovery();
    }

    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            String info = ((TextView) v).getText().toString();
            String noDevices = getResources().getText(R.string.none_paired).toString();
            String noNewDevice = getResources().getText(R.string.none_bluetooth_device_found).toString();
            Log.i("tag", info);
            if (!info.equals(noDevices) && !info.equals(noNewDevice) && !info.equals(getString(R.string.str_title_newdev)) && !info.equals(getString(R.string.str_title_pairedev))) {
                BluetoothFuncLogic.getInstance().getmBluetoothAdapter().cancelDiscovery();
                String address = info.substring(info.length() - 17);
                if(BluetoothFuncLogic.getInstance().BluetoothBond(address,"0000",BluetoothDeviceList.this)){
                    BluetoothFuncLogic.getInstance().BluetoothConnect(address);
                }
                finish();
            }
        }
    };

}
