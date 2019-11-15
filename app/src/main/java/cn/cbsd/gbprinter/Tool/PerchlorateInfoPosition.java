package cn.cbsd.gbprinter.Tool;

public class PerchlorateInfoPosition {

    private int labelWidth;
    private int labelHeight;
    private int QRCodePositonX;
    private int QRCodePositonY;
    private int QRCodeSize;
    private int NamePositionX;
    private int NamePositionY;
    private int NameSize;
    private int KindPositionX;
    private int KindPositionY;
    private int KindSize;
    private int BarCodePositionX;
    private int BarCodePositionY;
    private int BarCodeSize;

    public PerchlorateInfoPosition() {

    }




    public PerchlorateInfoPosition(int labelWidth, int labelHeight, int QRCodePositonX, int QRCodePositonY, int QRCodeSize, int namePositionX, int namePositionY, int nameSize, int kindPositionX, int kindPositionY, int kindSize, int barCodePositionX, int barCodePositionY, int barCodeSize) {
        this.labelWidth = labelWidth;
        this.labelHeight = labelHeight;
        this.QRCodePositonX = QRCodePositonX;
        this.QRCodePositonY = QRCodePositonY;
        this.QRCodeSize = QRCodeSize;
        NamePositionX = namePositionX;
        NamePositionY = namePositionY;
        NameSize = nameSize;
        KindPositionX = kindPositionX;
        KindPositionY = kindPositionY;
        KindSize = kindSize;
        BarCodePositionX = barCodePositionX;
        BarCodePositionY = barCodePositionY;
        BarCodeSize = barCodeSize;
    }

    public int getLabelWidth() {
        return labelWidth;
    }

    public int getLabelHeight() {
        return labelHeight;
    }


    public int getQRCodePositonX() {
        return QRCodePositonX;
    }

    public int getQRCodePositonY() {
        return QRCodePositonY;
    }

    public int getNamePositionX() {
        return NamePositionX;
    }

    public int getNamePositionY() {
        return NamePositionY;
    }

    public int getKindPositionX() {
        return KindPositionX;
    }

    public int getKindPositionY() {
        return KindPositionY;
    }

    public int getBarCodePositionX() {
        return BarCodePositionX;
    }

    public int getBarCodePositionY() {
        return BarCodePositionY;
    }

    public int getQRCodeSize() {
        return QRCodeSize;
    }

    public int getNameSize() {
        return NameSize;
    }

    public int getKindSize() {
        return KindSize;
    }
    public int getBarCodeSize() {
        return BarCodeSize;
    }
}
