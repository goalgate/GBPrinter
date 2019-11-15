package cn.cbsd.cbsdprintersupport.Event;

import android.bluetooth.BluetoothDevice;

public class BluetoothDevice_ACTION_FOUND_Event {
    BluetoothDevice device;

    public BluetoothDevice_ACTION_FOUND_Event(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }
}
