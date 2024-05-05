#include <WiFi.h>     //WiFiに関しての処理をまとめたヘッダファイル
#include <WiFiUdp.h>  //UDP通信に関しての処理をまとめたヘッダファイル

// 受信：$ nc -u -l 10000
// 送信：$ echo "hoge" | nc  -u 192.168.100.18 5000

WiFiUDP wifiUdp; //UDP通信の準備
const IPAddress remoteIP(172, 20, 10, 13); // 送信先のIPアドレス
const int remotePort = 50000;             // 送信先のポート番号

const int analogPin = 32;  
// 初期起動時にsetup内の処理を実行
//  WiFi.begin("654413250133", "50277490"); //SSIDとパスワードを登録

void setup(){
  Serial.begin(9600);                 //シリアル通信を115200bpsで開始
  // WiFi.begin("HUMAX-8A9BC", "MEdjX5NjM5FJd");           //SSIDとパスワードを登録
  WiFi.begin("安田航大のiPhone", "sakai0001"); 
  WiFi.setHostname(String("my_esp32").c_str()); //ホスト名を設定
  while(WiFi.status() != WL_CONNECTED){ //接続されてない間続ける
    Serial.print(".");  //「.」を表示
    delay(500);         //500ms待つ
  }
  Serial.println("CONNECTED!"); //接続完了&改行を表示
  Serial.print("ローカルIPアドレス : ");
  Serial.println(WiFi.localIP()); 
  wifiUdp.begin(5000);          //自身のポート5000番を開放
}

void loop() {
 int sensorValues[15]; // センサー値を格納する配列
  int sum = 0; // センサー値の合計

  // 15回のループでセンサー値を読み取り、合計を計算する
  for (int i = 0; i < 10; i++) {
    // アナログ値を読み取る
    int sensorValue = analogRead(analogPin);
    sensorValues[i] = sensorValue;
    sum += sensorValue;
    delay(8); // 100ms待つ
  }

  // 平均値を計算する
  int average = sum / 15;

  // 平均値を文字列に変換する
  String msg = String(average);

  // 送信先にUDPでデータを送信
  wifiUdp.beginPacket(remoteIP, remotePort);
  wifiUdp.print(msg);
  wifiUdp.endPacket();

  // シリアルモニタに送信したデータを表示
  Serial.println("Data sent: " + String(average));

  delay(5); // 1秒待つ
}                               //UDP通信終了
