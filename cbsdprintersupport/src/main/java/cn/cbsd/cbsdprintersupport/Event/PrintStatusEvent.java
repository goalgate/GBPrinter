package cn.cbsd.cbsdprintersupport.Event;

import cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncConstant.PrinterStatus;
public class PrintStatusEvent {

    private PrinterStatus status;

    public PrintStatusEvent(PrinterStatus status) {
        this.status = status;
    }

    public PrinterStatus getStatus() {
        return status;
    }
}
