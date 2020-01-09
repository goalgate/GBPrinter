package cn.cbsd.gbprinter;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;
import com.mining.app.zxing.activity.MipcaActivityCapture;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import java.util.Vector;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.cbsd.cbsdprintersupport.Event.PrintStatusEvent;
import cn.cbsd.cbsdprintersupport.Label.CBSDLabelCommand;
import cn.cbsd.cbsdprintersupport.UI.BluetoothDeviceList;
import cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncLogic;
import cn.cbsd.cbsdprintersupport.PrintLogic.PrintLogic;
import cn.cbsd.cbsdprintersupport.UI.AskPermissionActivity;
import cn.cbsd.gbprinter.Tool.DAInfo;
import cn.cbsd.gbprinter.Tool.ServerConnectionUtil;

import static cn.cbsd.cbsdprintersupport.PrintLogic.BluetoothFuncConstant.PrinterStatus.STATE_DISCONNECTED;


public class IndexActivity extends AskPermissionActivity {

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

    private Bitmap mBitmap = null;
    private boolean netSendOk_ = false;
    private boolean printOk_ = false;
    private int printLabelW = 70;
    private int printLabelH = 40;

    private String codeData_ = "";

    @BindView(R.id.tinfo)
    TextView tinfo;

    @BindView(R.id.QRCode)
    ImageView QRCode;

    @BindView(R.id.spLabelSize)
    Spinner spLabelSize;

    @BindView(R.id.cbone)
    CheckBox cbPrintOneLabel;

    @OnClick(R.id.conn_dev)
    void conn_dev() {
        startActivity(new Intent(this, BluetoothDeviceList.class));
    }

    @OnClick(R.id.btn_print_label)
    void print_label() {
        if (mBitmap != null) {
            if (!netSendOk_) {
                ServerConnectionUtil scu = new ServerConnectionUtil();
                //scu.https("http://192.168.12.169:7001/daServer/devRecord?method=devRecord&data="+codeData_, mCallback);
                scu.https("http://124.172.232.89:8050/daServer/devRecord.do?method=devRecord&data=" + codeData_, mCallback);
            } else {
                if (printOk_) {
                    clear();
                } else {
                    printLabel();
                }
            }
        } else {
            Toast.makeText(this,"请先制作二维码",Toast.LENGTH_SHORT).show();
        }

    }

    private ServerConnectionUtil.Callback mCallback = new ServerConnectionUtil.Callback() {
        @Override
        public void onResponse(String response) {
            //Toast.makeText(this.getApplication(), response, Toast.LENGTH_LONG).show();
            if (response != null && response.equals("true")) {
                tinfo.setText("数据已提交");
                netSendOk_ = true;
                printLabel();
            } else {
                tinfo.setText("数据提交服务器失败！");
            }
        }
    };

    public void clear() {
        mBitmap = null;
        //printOk_=false;
        //netSendOk_=false;
        QRCode.setImageBitmap(null);
    }

    //打印
    private void printLabel() {
        LabelCommand tsc = new CBSDLabelCommand(printLabelW,printLabelH,mBitmap,0);
        if(cbPrintOneLabel.isChecked()) {
            tsc.addPrint(1, 1); // 打印标签
        }else {
            tsc.addPrint(1, 2); // 打印标签
        }
        Vector<Byte> datas = tsc.getCommand(); //发送数据
        BluetoothFuncLogic.getInstance().getStatusImmediately(new BluetoothFuncLogic.StatusCallback() {
            @Override
            public void onSuccess() {
                PrintLogic.getInstance().print(datas);
                printOk_ = true;
                clear();
                tinfo.setText("打印标签成功。" + codeData_);
            }

            @Override
            public void onFailed() {
                tinfo.setText("打印标签失败!");
            }
        });


    }


    @OnClick(R.id.btn_make_code)
    void make_code() {
        Intent intent = new Intent();
        intent.setClass(this, MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
    }

    @OnClick(R.id.check_state)
    void check_state() {
        BluetoothFuncLogic.getInstance().getStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        spLabelSize.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                createLabel();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
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

    public void createLabel() {
        String[] ss=codeData_.split(";");
        if(ss.length<5)
        {
            codeData_="";
            return;
        }
        DAInfo di=new DAInfo();
        di.setLabelType("PR");
        di.setMacid(ss[0]);
        di.setModel(ss[1]);
        di.setVer(ss[2]);
        di.setProject(ss[3]);
        di.setPower(ss[4]);


        di.setDate();
        if(codeData_.toUpperCase().indexOf("CBDI-RK3368")>0||codeData_.toUpperCase().indexOf("CBDI-RK3288")>0)
        {
            di.setModel("CBDI-RK3368");
            di.setName("网络数据采集仪");
            di.setId("");
        }else
        {
            di.setName("数据采集器");
            di.setId(ss[7]);
        }

        if(codeData_.toUpperCase().indexOf("RL:")>0)
        {
            String srl=codeData_.substring(codeData_.toUpperCase().indexOf("RL:")-1);
            di.setOtherData(srl);
        }

        try {
            if(spLabelSize.getSelectedItem().toString().equals("40x30标签"))
            {
                printLabelW=40;
                printLabelH=30;
                if(codeData_.toUpperCase().indexOf("CBDI-RK3368")>0||codeData_.toUpperCase().indexOf("CBDI-RK3288")>0) {
                    mBitmap = di.daInfoBmp40x30(20);
                }else
                {
                    mBitmap = di.daInfoBmp40x30(24);
                }
            }else if(spLabelSize.getSelectedItem().toString().equals("60x40标签"))
            {
                printLabelW=60;
                printLabelH=40;
                mBitmap = di.daInfoBmp60x40();
            }else
            {
                printLabelW=70;
                printLabelH=40;
                mBitmap = di.daInfoBmp70x40();
            }

        }catch (Exception ex){}
        if(mBitmap!=null)
        {
            QRCode.setImageBitmap(mBitmap);
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    tinfo.setText(bundle.getString("result"));
                    codeData_=bundle.getString("result");
                    netSendOk_=false;
                    printOk_=false;
                    createLabel();
                }
                break;
        }
    }

}
