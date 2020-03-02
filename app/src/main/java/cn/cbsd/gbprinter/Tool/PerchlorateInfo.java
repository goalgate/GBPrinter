package cn.cbsd.gbprinter.Tool;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

import static android.graphics.Color.BLACK;

public class PerchlorateInfo {

    public enum LabelSize {
//        label_40x30(new PerchlorateInfoPosition(40, 30, 96, 112, 160, 0, 128, 22, 0, 160, 32, 0, 256, 18)),
        label_40x30(new PerchlorateInfoPosition(40, 30, 96, 115, 160, 0, 128, 22, 0, 160, 30, 0, 256, 18)),

        label_30x20(new PerchlorateInfoPosition(30,20,144,184,104,56,192,18,64,208,24,56,272,15)),


        label_70x40(new PerchlorateInfoPosition(70, 40, 320, 50, 240, 40, 64, 32, 40, 180, 60, 40, 290, 25)),
        label_100x50(new PerchlorateInfoPosition(100, 50, 340, 0, 256, 0, 32, 32, 0, 160, 65, 0, 256, 30));

        private PerchlorateInfoPosition perchlorateInfoPosition;

        public PerchlorateInfoPosition getPerchlorateInfoPosition() {
            return perchlorateInfoPosition;
        }

        LabelSize(PerchlorateInfoPosition perchlorateInfoPosition) {
            this.perchlorateInfoPosition = perchlorateInfoPosition;
        }
    }


    private String title;

    private String kind;

    private String Code;

    public PerchlorateInfo(String title, String kind, String code) {
        this.title = title;
        this.kind = kind;
        Code = code;
    }

    private Bitmap createQRCode(String info, int length) {
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix matrix = new MultiFormatWriter().encode(info,
                    BarcodeFormat.QR_CODE, length, length);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            //画黑点
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = BLACK; //0xff000000
                    } else {
                        pixels[y * width + x] = Color.WHITE; //0xFFFFFFFF
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.WHITE);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException we) {
            Log.e("WriterException", we.toString());
        }
        return null;
    }


    public Bitmap createLable(LabelSize labelSize) {
        PerchlorateInfoPosition mPerchlorateInfoPosition = labelSize.getPerchlorateInfoPosition();

        Bitmap qr = createQRCode(Code, mPerchlorateInfoPosition.getQRCodeSize());

        Bitmap newb = null;

        Canvas canvas;

        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

        paint.setAntiAlias(true);

        paint.setColor(Color.BLACK);

        paint.setTypeface(Typeface.DEFAULT_BOLD);

        switch (labelSize) {
            case label_100x50:
            case label_70x40:

                newb = Bitmap.createBitmap(mPerchlorateInfoPosition.getLabelWidth() * 8, mPerchlorateInfoPosition.getLabelHeight() * 8, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图

                newb.eraseColor(Color.WHITE);

                canvas = new Canvas(newb);

                canvas.drawBitmap(qr, mPerchlorateInfoPosition.getQRCodePositonX(), mPerchlorateInfoPosition.getQRCodePositonY(), null);

                paint.setTextSize(mPerchlorateInfoPosition.getNameSize());

                canvas.drawText(title, mPerchlorateInfoPosition.getNamePositionX(), mPerchlorateInfoPosition.getNamePositionY(), paint);

                paint.setTextSize(mPerchlorateInfoPosition.getKindSize());

                canvas.drawText(kind, mPerchlorateInfoPosition.getKindPositionX(), mPerchlorateInfoPosition.getKindPositionY(), paint);

                paint.setTextSize(mPerchlorateInfoPosition.getBarCodeSize());

                canvas.drawText(Code, mPerchlorateInfoPosition.getBarCodePositionX(), mPerchlorateInfoPosition.getBarCodePositionY(), paint);

                canvas.save();

                canvas.restore();

                break;

            case label_40x30:

                newb = Bitmap.createBitmap(75 * 8, 40 * 8, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图

                newb.eraseColor(Color.WHITE);

                canvas = new Canvas(newb);

                canvas.drawBitmap(qr, mPerchlorateInfoPosition.getQRCodePositonX(), mPerchlorateInfoPosition.getQRCodePositonY(), null);

                canvas.drawBitmap(qr, mPerchlorateInfoPosition.getQRCodePositonX()+336, mPerchlorateInfoPosition.getQRCodePositonY(), null);

                paint.setTextSize(mPerchlorateInfoPosition.getNameSize());

                canvas.drawText(title, mPerchlorateInfoPosition.getNamePositionX(), mPerchlorateInfoPosition.getNamePositionY(), paint);

                paint.setTextSize(mPerchlorateInfoPosition.getNameSize());

                canvas.drawText(title, mPerchlorateInfoPosition.getNamePositionX()+336, mPerchlorateInfoPosition.getNamePositionY(), paint);

                print4030WithNewLine(mPerchlorateInfoPosition, newb, true,0);

                print4030WithNewLine(mPerchlorateInfoPosition, newb, false,0);

                print4030WithNewLine(mPerchlorateInfoPosition, newb, true,336);

                print4030WithNewLine(mPerchlorateInfoPosition, newb, false,336);

                break;

            case label_30x20:
                newb = Bitmap.createBitmap(75 * 8, 40 * 8, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图

                newb.eraseColor(Color.WHITE);

                canvas = new Canvas(newb);

                canvas.drawBitmap(qr, mPerchlorateInfoPosition.getQRCodePositonX(), mPerchlorateInfoPosition.getQRCodePositonY(), null);

                canvas.drawBitmap(qr, mPerchlorateInfoPosition.getQRCodePositonX() + 256, mPerchlorateInfoPosition.getQRCodePositonY(), null);

                paint.setTextSize(mPerchlorateInfoPosition.getNameSize());

                canvas.drawText(title, mPerchlorateInfoPosition.getNamePositionX(), mPerchlorateInfoPosition.getNamePositionY(), paint);

                canvas.drawText(title, mPerchlorateInfoPosition.getNamePositionX() + 256, mPerchlorateInfoPosition.getNamePositionY(), paint);

                print3020WithNewLine(mPerchlorateInfoPosition, newb, true,0);

                print3020WithNewLine(mPerchlorateInfoPosition, newb, false,0);

                print3020WithNewLine(mPerchlorateInfoPosition, newb, true,256);

                print3020WithNewLine(mPerchlorateInfoPosition, newb, false,256);

                canvas.save();

                canvas.restore();

                break;

            default:
                break;
        }
        return newb;
    }

    private void print4030WithNewLine(PerchlorateInfoPosition mPerchlorateInfoPosition,Bitmap newb ,boolean iskind,int deviation) {
        Canvas canvas = new Canvas(newb);

        TextPaint textPaint = new TextPaint();

        textPaint.setColor(BLACK);

        textPaint.setAntiAlias(true);

        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        if (iskind) {
            textPaint.setTextSize(mPerchlorateInfoPosition.getKindSize());

            StaticLayout layout = new StaticLayout(kind, textPaint, 90,
                    Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);

            canvas.translate(mPerchlorateInfoPosition.getKindPositionX()+deviation, mPerchlorateInfoPosition.getKindPositionY());

            layout.draw(canvas);

            canvas.save();

            canvas.restore();
        } else {
            textPaint.setTextSize(mPerchlorateInfoPosition.getBarCodeSize());

            StaticLayout layout = new StaticLayout(Code, textPaint, 232,
                    Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);

            canvas.translate(mPerchlorateInfoPosition.getBarCodePositionX()+deviation, mPerchlorateInfoPosition.getBarCodePositionY());

            layout.draw(canvas);

            canvas.save();

            canvas.restore();
        }

    }


    private void print3020WithNewLine(PerchlorateInfoPosition mPerchlorateInfoPosition,Bitmap newb ,boolean iskind,int deviation) {
        Canvas canvas = new Canvas(newb);

        TextPaint textPaint = new TextPaint();

        textPaint.setColor(BLACK);

        textPaint.setTypeface(Typeface.DEFAULT_BOLD);

        if (iskind) {
            textPaint.setTextSize(mPerchlorateInfoPosition.getKindSize());

            StaticLayout layout = new StaticLayout(kind, textPaint, 80,
                    Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);

            canvas.translate(mPerchlorateInfoPosition.getKindPositionX()+deviation, mPerchlorateInfoPosition.getKindPositionY());

            layout.draw(canvas);

            canvas.save();

            canvas.restore();
        } else {
            textPaint.setTextSize(mPerchlorateInfoPosition.getBarCodeSize());

            StaticLayout layout = new StaticLayout(Code, textPaint, 190,
                    Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);

            canvas.translate(mPerchlorateInfoPosition.getBarCodePositionX()+deviation, mPerchlorateInfoPosition.getBarCodePositionY());

            layout.draw(canvas);

            canvas.save();

            canvas.restore();
        }

    }

}
