package com.android.example.esp32_udp;

import static java.lang.Thread.sleep;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int rcv_voltage,count;
    private TextView udp_port, udp_rcv, q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, q15;

    TextView[] textViewQs = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, q11, q12, q13, q14, q15};
    private String port;
    private TextView udp_ip,condition;
    private EditText IpAdress,portnum,y_max,y_min;
    private float y_max_value,y_min_value;
    private Button connect,Apply;
    private espServer espSvr;
    private boolean taskrun = false;
    private boolean timephase=true;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private DatagramSocket socket;
    private boolean running=true;
    private int state;
    Deque<Integer> msgQueue = new ArrayDeque<>(15);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IpAdress=findViewById(R.id.ipadress);
        portnum=findViewById(R.id.port_num);
        connect = findViewById(R.id.connect);
        connect.setOnClickListener(this);
        udp_ip=findViewById(R.id.udp_msg);
        condition=findViewById(R.id.condition);
        udp_port=findViewById(R.id.udp_port);
        udp_rcv=findViewById(R.id.udp_rcv);
        count=1;
        udp_rcv.setText("start");
        state=1;
        Apply=findViewById(R.id.apply);
        Apply.setOnClickListener(this);
        y_max=findViewById(R.id.max_axis);
        y_min=findViewById(R.id.min_axis);
        y_max_value=4000f;
        y_min_value=500f;

        for (int i = 0; i < 15; i++) {
            msgQueue.offer(9999999);
        }

        int[] msgQ_ids = {R.id.q1, R.id.q2, R.id.q3, R.id.q4, R.id.q5, R.id.q6, R.id.q7, R.id.q8, R.id.q9, R.id.q10, R.id.q11, R.id.q12, R.id.q13,R.id.q14,R.id.q15};
        for (int i = 0; i < msgQ_ids.length; i++) {
            textViewQs[i] = findViewById(msgQ_ids[i]);
            textViewQs[i].setText("111188");
        }
    }
    @Override
    public void onClick(View view) {
        Button clickedButton = (Button) view;
        if(view.getId()==R.id.connect){
        String tmp_ip = String.valueOf(IpAdress.getText());
        String tmp_port = String.valueOf(portnum.getText());
//        connect.setEnabled(false);
//        connect.setBackgroundResource(R.drawable.button_state1);
        if (state == 1) {

            Thread thread = new Thread(() -> {
                try {
                    // ポート5050でUDPソケットを作成
                    socket = new DatagramSocket(50000);
                    byte[] buffer = new byte[1024];

                    running = true;
                    while (running) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet); // UDPパケットを受信
                        InetAddress senderAddress = packet.getAddress();
                        int senderPort = packet.getPort();

                        final String message = new String(packet.getData(), 0, packet.getLength());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                udp_rcv.setText(message); // UIスレッドでTextViewを更新
                                udp_ip.setText(String.valueOf(senderAddress));
                                udp_port.setText("port: " + String.valueOf(senderPort));
                                msgQueue.offer(Integer.valueOf(String.valueOf(message)));
                                count = Integer.valueOf(String.valueOf(message));
                                String currentTime = sdf.format(new Date());
                                textViewQs[14].setText(String.valueOf(count));
                                List<Float> x = new ArrayList<>();
                                List<Float> y = new ArrayList<>();


                                for (int i = 0; i < textViewQs.length - 1; i++) {
                                    String tmp = String.valueOf(textViewQs[i + 1].getText());
                                    y.add((float) Integer.parseInt(tmp));
                                    x.add((float) i);
                                    textViewQs[i].setText(String.valueOf(tmp));
                                    if (i > 14) {
                                        break;
                                    }
                                }
                                y.add((float) count);
                                x.add(14F);

                                List<Entry> entryList = new ArrayList<>();
                                for (int i = 0; i < x.size(); i++) {
                                    entryList.add(new Entry(x.get(i), y.get(i)));
                                }

                                // LineDataSetのList
                                List<ILineDataSet> lineDataSets = new ArrayList<>();

                                // DataSetにデータ格納
                                LineDataSet lineDataSet = new LineDataSet(entryList, "square");
                                lineDataSet.setColor(Color.BLUE);
                                lineDataSet.setDrawValues(false);
                                lineDataSet.setLineWidth(3f);
                                lineDataSet.setDrawFilled(true); // 下のエリアを塗りつぶす
                                lineDataSet.setFillAlpha(30); // 塗りつぶしの透明度を設定
                                lineDataSet.setFillColor(Color.BLUE); // 塗りつぶしの色を設定
                                // リストに格納
                                lineDataSets.add(lineDataSet);

                                // LineDataにLineDataSet格納
                                LineData lineData = new LineData(lineDataSets);

                                // LineChartにLineData格納
                                lineChart = findViewById(R.id.lineChartExample);
                                lineChart.setData(lineData);


                                // Chartのフォーマット指定
                                // X軸の設定
                                XAxis xAxis = lineChart.getXAxis();
                                xAxis.setEnabled(false);
                                xAxis.setTextColor(Color.BLACK);
                                YAxis yAxis_l = lineChart.getAxisLeft(); // 左側のY軸を取得
                                yAxis_l.setAxisMaximum(y_max_value); // 上限を4500に設定
                                yAxis_l.setAxisMinimum(y_min_value); // 下限を500に設定
                                YAxis yAxis_r = lineChart.getAxisRight(); // 左側のY軸を取得
                                yAxis_r.setAxisMaximum(y_max_value); // 上限を4500に設定
                                yAxis_r.setAxisMinimum(y_min_value); // 下限を500に設定


                                // LineChart更新
                                lineChart.invalidate();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            thread.start();

            state = 2;
            connect.setText("stop");
            condition.setText("UDP Receiving");
        } else if (state == 2) {
            running = false; // 受信ループを停止するためのフラグを設定
            socket.close(); // ソケットを閉じて受信を停止

            state = 1; // 状態を更新
            connect.setText("start"); // ボタンのテキストを更新
            condition.setText("Stop");
        }
    }
        if(view.getId()==R.id.apply){
            String tmp_max=String.valueOf(y_max.getText());
            String tmp_min=String.valueOf(y_min.getText());
            if(tmp_max.equals("")){
                tmp_max=String.valueOf(y_max_value);
            }
            if(tmp_min.equals("")){
                tmp_min=String.valueOf(y_min_value);
            }
            y_max_value=Float.parseFloat(tmp_max);
            y_min_value=Float.parseFloat(tmp_min);
            if(y_max_value<y_min_value){
                float tmp=y_max_value;
                y_max_value=y_min_value;
                y_min_value=tmp;
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        if (socket != null) {
            socket.close();
        }
    }
}
