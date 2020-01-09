package cn.cbsd.gbprinter;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.cbsd.cbsdprintersupport.Event.PrintStatusEvent;
import cn.cbsd.cbsdprintersupport.Label.CBSDLabelCommand;
import cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncLogic;
import cn.cbsd.cbsdprintersupport.PrintLogic.PrintLogic;
import cn.cbsd.cbsdprintersupport.UI.AskPermissionActivity;
import cn.cbsd.cbsdprintersupport.UI.BluetoothDeviceList;
import cn.cbsd.gbprinter.Tool.PerchlorateInfo;

import static cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncConstant.PrinterStatus.STATE_DISCONNECTED;
import static java.lang.Enum.valueOf;

public class TESTActivity extends AskPermissionActivity {
    private final static int SCANNIN_GREQUEST_CODE = 1;

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.INTERNET,
            Manifest.permission.VIBRATE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @OnClick(R.id.conn_dev)
    void conn_dev() {
        startActivity(new Intent(this, BluetoothDeviceList.class));
    }

    @OnClick(R.id.check_state)
    void check_state() {
        BluetoothFuncLogic.getInstance().getStatus();
    }

    @BindView(R.id.QRCode)
    ImageView img_QRCode;

    @OnClick(R.id.btn_print_label)
    void print_Lable() {
        PerchlorateInfo.LabelSize labelSize = PerchlorateInfo.LabelSize.label_70x40;
        Bitmap mBitmap = new PerchlorateInfo("高氯酸[浓度50%～72%]", "易制爆标识", "44011200010104201911070000001000010").createLable(labelSize);
        img_QRCode.setImageBitmap(mBitmap);
        LabelCommand tsc = new CBSDLabelCommand(labelSize.getPerchlorateInfoPosition().getLabelWidth(), labelSize.getPerchlorateInfoPosition().getLabelHeight(), mBitmap, 1);
        Vector<Byte> datas = tsc.getCommand();
//        Bitmap mBitmap1 = new PerchlorateInfo("高氯酸[浓度60%～72%]", "易制爆标识", "44011200010104201911070000001000010").createLable(labelSize);
//        LabelCommand tsc1 = new CBSDLabelCommand(labelSize.getPerchlorateInfoPosition().getLabelWidth(), labelSize.getPerchlorateInfoPosition().getLabelHeight(), mBitmap1, 1);
//        Vector<Byte> datas1 = tsc1.getCommand();
//        Bitmap mBitmap2 = new PerchlorateInfo("高氯酸[浓度70%～72%]", "易制爆标识", "44011200010104201911070000001000010").createLable(labelSize);
//        LabelCommand tsc2 = new CBSDLabelCommand(labelSize.getPerchlorateInfoPosition().getLabelWidth(), labelSize.getPerchlorateInfoPosition().getLabelHeight(), mBitmap2, 1);
//        Vector<Byte> datas2 = tsc2.getCommand();
//
//        Vector<Byte>[] list = new Vector[]{datas,datas1,datas2};
        BluetoothFuncLogic.getInstance().getStatusImmediately(new BluetoothFuncLogic.StatusCallback() {
            @Override
            public void onSuccess() {
                PrintLogic.getInstance().print(datas);
            }

            @Override
            public void onFailed() {
            }
        });
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        tv_status = (TextView) findViewById(R.id.status);
        EventBus.getDefault().post(new PrintStatusEvent(STATE_DISCONNECTED));
        requestRunPermisssion(permissions, new PermissionListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> deniedPermission) {

            }
        });

    }
}
