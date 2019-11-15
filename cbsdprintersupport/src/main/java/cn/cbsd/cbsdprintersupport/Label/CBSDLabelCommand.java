package cn.cbsd.cbsdprintersupport.Label;

import android.graphics.Bitmap;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

public class CBSDLabelCommand extends LabelCommand {

    public CBSDLabelCommand(int width, int height, Bitmap bitmap,int n) {
        addGap(2);
        addDirection(LabelCommand.DIRECTION.BACKWARD, LabelCommand.MIRROR.NORMAL);
        addReference(0, 0);
        addTear(EscCommand.ENABLE.ON);
        addCls();
        addSound(2, 100);
        addSize(width,height);
        addBitmap(0, 0, LabelCommand.BITMAP_MODE.OVERWRITE, bitmap.getWidth(), bitmap);
        addPrint(1, n);
    }
}
