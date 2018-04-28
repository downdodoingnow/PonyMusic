package me.wcy.music.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import me.wcy.music.constants.Keys;

public class ChatClientSocketUtil {
    private static final int PORT = 5000;
    private static Socket mSocket;
    private BufferedReader inputStream;
    private BufferedWriter outputStream;
    private static final String TAG = "ChatClientSocketUtil";

    private Handler mHandler;

    public ChatClientSocketUtil(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(Keys.HOST, PORT);
                    outputStream = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
                    inputStream = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "GBK"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void receiveMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Message msg = new Message();
                        String s = inputStream.readLine();
                        Log.i(TAG, "run: " + s);
                        msg.obj = s;
                        mHandler.sendMessage(msg);
                    } catch (IOException i) {
                        i.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }

    public void sendMsg(JSONObject joo) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "run: 开始发送数据");
                    outputStream.write(joo.toString() + "\n");
                    outputStream.flush();
                    Log.i(TAG, "run: 发送数据完成");
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        }).start();
    }

    public void close() {
        try {
            if (null != mSocket) {
                mSocket.close();
            }
            if (null != inputStream) {
                inputStream.close();
            }
            if (null != outputStream) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
