package cn.cbsd.cbsdprintersupport.PrintLogic;

public class BluetoothFuncConstant {

    public static int BLUETOOTH_PRINTERTYPE = 1664;  //蓝牙打印机的类型，确保搜索出来的都是蓝牙打印机

    public enum PrinterStatus {
        STATE_DISCONNECTED("打印机断开连接",0),
        STATE_Paired("已配对完毕",0),
        STATE_CANNOTCONNECT("无法连接打印机",0),
        STATE_CONNECTEDING("正在连接打印机",0),
        STATE_CONNECTED("打印机连接成功",0x00),
        STATE_COVER_OPEN("打印机处于开盖状态",0x01),
        STATE_PAPER_JAM("打印机处于卡纸状态",0x02),
        STATE_PAPER_JAM_OPEN("打印机处于卡纸、开盖状态",0x03),
        STATE_PAPER_ERR("打印机处于缺纸状态",0x04),
        STATE_PAPER_ERR_OPEN("打印机处于缺纸、开盖状态",0x05),
        STATE_NOCARBON("打印机处于无碳带状态",0x08),
        STATE_NOCARBON_OPNE("打印机处于无碳带、开盖状态",0x09),
        STATE_NOCARBON_JAM("打印机处于无碳带、卡纸状态",0x0A),
        STATE_NOCARBON_JAM_OPEN("打印机处于无碳带、卡纸、开盖状态",0x0B),
        STATE_PAPER_ERR_NOCARBON("打印机处于无碳带、缺纸状态",0x0C),
        STATE_PAPER_ERR_NOCARBON_OPEN("打印机处于无碳带、缺纸、开盖状态",0x0D),
        STATE_PRINT_PAUSE("打印机暂停打印",0x10),
        STATE_PRINTING("打印机打印中",0x20),
        STATE_ERR("打印机其他错误",0x80);


        private String describe;

        public String getDescribe() {
            return describe;
        }

        public int getIndex() {
            return index;
        }

        private int index;

        PrinterStatus(String describe, int index) {
            this.describe = describe;
            this.index = index;
        }
    }
    
    
}
