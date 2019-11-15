package cn.cbsd.cbsdprintersupport.PrintLogic;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.gprinter.io.BluetoothPort;
import com.gprinter.io.PortManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import cn.cbsd.cbsdprintersupport.Event.PrintStatusEvent;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncConstant.PrinterStatus.STATE_CANNOTCONNECT;
import static cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncConstant.PrinterStatus.STATE_CONNECTED;
import static cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncConstant.PrinterStatus.STATE_DISCONNECTED;

public class BluetoothFuncLogic {

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private byte[] tscStatus = {0x1B, 0x21, 0x3F};

    private byte[] tscReboot = {0x1B, 0x21, 0x52};

    private static final int READ_DATA = 10000;

    private static final String READ_DATA_CNT = "read_data_cnt";

    private static final String READ_BUFFER_ARRAY = "read_buffer_array";

    private PortManager mPort;

    private boolean isConnected = false;

    private static BluetoothFuncLogic logic;

    private BluetoothFuncLogic() {
    }

    public static BluetoothFuncLogic getInstance() {
        if (logic == null) {
            logic = new BluetoothFuncLogic();
        }
        return logic;
    }


    public boolean BluetoothOpen(Activity activity) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(activity, "该设备不支持蓝牙", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
            return true;
        }
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }


    public void BluetoothDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        } else {
            mBluetoothAdapter.startDiscovery();
        }
    }


    public boolean BluetoothBond(final String macAddress, String pin, final Activity activity) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddress);
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            try {
                Toast.makeText(activity, "配对设备中...", Toast.LENGTH_LONG).show();
//                ClsUtils.setPin(device.getClass(), device, pin); // 手机和蓝牙采集器配对
                ClsUtils.createBond(device.getClass(), device);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return true;
        }

    }


    public void BluetoothConnect(final String macAddress) {
        if (!isConnected) {
            Observable.create((emitter) -> {
                mPort = new BluetoothPort(macAddress);
                emitter.onNext(isConnected = mPort.openPort());
            })
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe((l) -> {
                        Boolean status = (boolean) l;
                        if (status) {
                            EventBus.getDefault().post(new PrintStatusEvent(STATE_CONNECTED));
                            reader = new PrinterReader();
                            reader.start();
                        } else {
                            EventBus.getDefault().post(new PrintStatusEvent(STATE_CANNOTCONNECT));
                        }
                    });
        } else {
            BluetoothDisConnect();
        }


    }

    public void BluetoothDisConnect() {
        if (mPort != null && reader != null) {
            if (mPort.closePort()) {
                isConnected = false;
                EventBus.getDefault().post(new PrintStatusEvent(STATE_DISCONNECTED));
            }
            reader.cancel();
            mPort = null;
        }
        if (listener != null) {
            listener = null;
        }
    }

    Timer timer;

    class MyTask extends TimerTask {
        @Override
        public void run() {
            EventBus.getDefault().post(new PrintStatusEvent(STATE_CANNOTCONNECT));
            if (listener != null) {
                handler.post(() -> listener.onFailed());
            }
        }
    }

    public void getStatus() {
        if (isConnected) {
            Vector<Byte> data = new Vector<>(tscStatus.length);
            for (int i = 0; i < tscStatus.length; i++) {
                data.add(tscStatus[i]);
            }
            sendDataImmediately(data);
            timer = new Timer();
            timer.schedule(new MyTask(), 5000);

        } else {
            EventBus.getDefault().post(new PrintStatusEvent(STATE_DISCONNECTED));
        }
    }

    public void getStatusImmediately(StatusCallback listener) {
        this.listener = listener;
        if (isConnected) {
            Vector<Byte> data = new Vector<>(tscStatus.length);
            for (int i = 0; i < tscStatus.length; i++) {
                data.add(tscStatus[i]);
            }
            sendDataImmediately(data);
            timer = new Timer();
            timer.schedule(new MyTask(), 5000);

        } else {
            EventBus.getDefault().post(new PrintStatusEvent(STATE_DISCONNECTED));
            this.listener.onFailed();
        }
    }

//
//    public void reboot(){
//        if (isConnected) {
//            Vector<Byte> data = new Vector<>(tscReboot.length);
//            for (int i = 0; i < tscReboot.length; i++) {
//                data.add(tscReboot[i]);
//            }
//            sendDataImmediately(data);
//        } else {
//            EventBus.getDefault().post(new PrintStatusEvent(STATE_DISCONNECTED));
//        }
//    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case READ_DATA:
                    if (timer != null) {
                        timer.cancel();
                    }
                    if (listener != null) {
                        listener.onSuccess();
                    }
                    int cnt = msg.getData().getInt(READ_DATA_CNT); //数据长度 >0;
                    byte[] buffer = msg.getData().getByteArray(READ_BUFFER_ARRAY);  //数据
                    if (cnt == 1) {//查询打印机实时状态
                        for (BluetoothFuncConstant.PrinterStatus printerStatus : BluetoothFuncConstant.PrinterStatus.values()) {
                            if ((buffer[0] & printerStatus.getIndex()) > 0) {
                                EventBus.getDefault().post(new PrintStatusEvent(printerStatus));
                                return;
                            }
                        }
                        EventBus.getDefault().post(new PrintStatusEvent(STATE_CONNECTED));
                    }
                    break;
                default:
                    break;
            }

        }
    };

    public void sendDataImmediately(final Vector<Byte> data) {
        if (this.mPort == null) {
            return;
        }
        try {
            this.mPort.writeDataImmediately(data, 0, data.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int readDataImmediately(byte[] buffer) throws IOException {
        return this.mPort.readData(buffer);
    }

    public boolean isConnected() {
        return isConnected;
    }

    private PrinterReader reader;

    class PrinterReader extends Thread {
        private boolean isRun = false;

        private byte[] buffer = new byte[100];

        public PrinterReader() {
            isRun = true;
        }

        @Override
        public void run() {
            try {
                while (isRun) {
                    //读取打印机返回信息
                    int len = readDataImmediately(buffer);
                    if (len > 0) {
                        Message message = Message.obtain();
                        message.what = READ_DATA;
                        Bundle bundle = new Bundle();
                        bundle.putInt(READ_DATA_CNT, len); //数据长度
                        bundle.putByteArray(READ_BUFFER_ARRAY, buffer); //数据
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                Log.e("Exception", "PrinterReader");
            }
        }

        public void cancel() {
            isRun = false;
        }
    }


    private StatusCallback listener;


    public interface StatusCallback {
        void onSuccess();

        void onFailed();
    }
}
