package cn.cbsd.cbsdprintersupport.PrintLogic;

import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import cn.cbsd.cbsdprintersupport.Event.PrintStatusEvent;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PrintLogic {

    private static PrintLogic printLogic;


    private Disposable disposable;

    private PrintLogic() {
    }

    public static PrintLogic getInstance() {
        if (printLogic == null) {
            printLogic = new PrintLogic();
        }
        return printLogic;
    }

    public void print(Vector<Byte> datas) {
        if (BluetoothFuncLogic.getInstance().isConnected()) {
            BluetoothFuncLogic.getInstance().sendDataImmediately(datas);
        }else{
            EventBus.getDefault().post(new PrintStatusEvent(BluetoothFuncConstant.PrinterStatus.STATE_DISCONNECTED));
        }
    }

    public void ContinuityPrint(boolean ContinuityStart, int delay, int period, Vector<Byte> datas) {
        if (BluetoothFuncLogic.getInstance().isConnected()) {
            if (ContinuityStart) {
                Observable.interval(delay, period, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                disposable = d;
                            }

                            @Override
                            public void onNext(Long aLong) {
                                print(datas);

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            } else {
                if (disposable != null) {
                    disposable.dispose();
                }
            }
        } else {
            EventBus.getDefault().post(new PrintStatusEvent(BluetoothFuncConstant.PrinterStatus.STATE_DISCONNECTED));
        }
    }

    public void CountDownPrint(int Count, Vector<Byte>[] datas) {
        if (BluetoothFuncLogic.getInstance().isConnected()) {

            Observable.interval(1, 3, TimeUnit.SECONDS)
                .take(Count + 1)
                .map((aLong) -> {
                            return Count - aLong;
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        print(datas[(int)(Count - aLong)]);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e("Print","打印完毕");
                    }
                });
        } else {
            EventBus.getDefault().post(new PrintStatusEvent(BluetoothFuncConstant.PrinterStatus.STATE_DISCONNECTED));
        }
    }




}
