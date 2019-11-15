package cn.cbsd.cbsdprintersupport.UI;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncLogic;
import cn.cbsd.cbsdprintersupport.Event.BluetoothAdapter_ACTION_DISCOVERY_FINISHED_Event;
import cn.cbsd.cbsdprintersupport.Event.BluetoothDevice_ACTION_FOUND_Event;
import cn.cbsd.cbsdprintersupport.Event.PrintStatusEvent;
import cn.cbsd.cbsdprintersupport.PrintLogic.ClsUtils;

public class AskPermissionActivity extends Activity {

    private PermissionListener mListener;

    private static final int PERMISSION_REQUESTCODE = 100;

    public TextView tv_status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        this.registerReceiver(mFindBlueToothReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFindBlueToothReceiver != null) {
            unregisterReceiver(mFindBlueToothReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothFuncLogic.getInstance().BluetoothDisConnect();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetPrintStatus(PrintStatusEvent event) {
        Toast.makeText(this, event.getStatus().getDescribe(), Toast.LENGTH_LONG).show();
        tv_status.setText(event.getStatus().getDescribe());
    }

    public void requestRunPermisssion(String[] permissions, PermissionListener listener) {
        mListener = listener;
        List<String> permissionLists = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionLists.add(permission);
            }
        }

        if (!permissionLists.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionLists.toArray(new String[permissionLists.size()]), PERMISSION_REQUESTCODE);
        } else {
            mListener.onGranted();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUESTCODE:
                if (grantResults.length > 0) {
                    //存放没授权的权限
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        mListener.onGranted();
                    } else {
                        mListener.onDenied(deniedPermissions);
                    }
                }
                break;
            default:
                break;
        }
    }

    public interface PermissionListener {

        void onGranted();//已授权

        void onDenied(List<String> deniedPermission);//未授权

    }

    private final BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:  //发现蓝牙
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    EventBus.getDefault().post(new BluetoothDevice_ACTION_FOUND_Event(device));
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    EventBus.getDefault().post(new BluetoothAdapter_ACTION_DISCOVERY_FINISHED_Event());
                    break;
                case BluetoothDevice.ACTION_PAIRING_REQUEST:
//                    BluetoothDevice btDevice = intent
//                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    Log.i("tag11111", "ddd");
//                    try {
//                        ClsUtils.setPin(btDevice.getClass(), btDevice, "0000"); // 手机和蓝牙采集器配对
//                        ClsUtils.createBond(btDevice.getClass(), btDevice);
//                        ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
                    break;
                default:
                    break;
            }
        }
    };

}
