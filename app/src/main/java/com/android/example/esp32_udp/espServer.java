package com.android.example.esp32_udp;

import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class espServer {
    private String ipadress;
    public String rcv_msg;
    private DatagramSocket dSocket = null;
    private Socket socket = null;
    private InputStream reader = null;
    private OutputStream writer = null;

    public espServer() {
        ;
    }

    public void UDP_makesocket(int port,TextView text_tmp) {
        text_tmp.setText("Accept in");
        try{
            if( dSocket != null ) {
                dSocket.close();
                dSocket = null;
            }
            dSocket = new DatagramSocket(50000);
        }catch(Exception ex){
        }
    }

    public boolean DisConnect() {
        try {
            socket.close();
            socket = null;
            writer.close();
            reader.close();
        } catch (Exception e) {
        }
        return true;
    }

    public boolean RcvMessage(TextView text_tmp) {

        String temp = "";
        byte w[] = new byte[4048];
        int size;

        if (socket.isConnected() == false) {
            return false;
        }
        try {
            reader = socket.getInputStream();
            size = reader.read(w);
            if (size <= 0) {
                return false;
            } else {
                temp = new String(w, 0, size, "UTF-8");
            }
            rcv_msg += "受信<<<：" + temp + "\n";
            text_tmp.setText("at kansu");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean SendMessage() {
        String str = "私はサーバーです";
        if (socket.isConnected() == false) {
            return false;
        }
        try {
            writer = socket.getOutputStream();
            writer.write(str.getBytes("UTF-8"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
