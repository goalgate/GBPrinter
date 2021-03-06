package cn.cbsd.gbprinter.Tool;

import android.os.Handler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by zbsz on 2017/12/2.
 */

public class ServerConnectionUtil {

    private  int TIME_OUT = 20 * 1000;   //超时时间
    private String CHARSET = "utf-8";
    private BufferedReader in = null;
    private String result = null;
    private String BOUNDARY = UUID.randomUUID().toString();  //边界标识   随机生成
    private String CONTENT_TYPE = "multipart/form-data";
    private  int bufferSize = 2048;


    static Handler handler = new Handler();

    public void post(final String baseUrl, final byte[] bs, final Callback callback) {

        new Thread() {
            @Override
            public void run() {
                final String response = sendPost(baseUrl, bs);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }.start();
    }

    public void https(final String baseUrl, final Callback callback) {
        new Thread() {
            @Override
            public void run() {
                final String response = sendHttps(baseUrl);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }.start();
    }

    private String sendHttps(String baseUrl) {

        try {
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (200 == conn.getResponseCode()){
                in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result = line;
                }
            }else{
                result= null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String sendPost(String baseUrl, byte[] bs) {
        DataOutputStream ds = null;
        ByteArrayInputStream bin = null;
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false);  //不允许使用缓存
            conn.setRequestMethod("POST");  //请求方式
            conn.setRequestProperty("Charset", CHARSET);  //设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            ds = new DataOutputStream(conn.getOutputStream());
            bin = new ByteArrayInputStream(bs);
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length = bin.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result = line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ds != null) {
                    ds.flush();
                    ds.close();
                }
                if (bin != null) {
                    bin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }


    public interface Callback {
        void onResponse(String response);
    }


}
