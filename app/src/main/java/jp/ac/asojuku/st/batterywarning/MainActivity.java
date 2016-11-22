
package jp.ac.asojuku.st.batterywarning;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private MyBroadcastReceiver mReceiver;
    private String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent){

            //複数のIntentを受信する場合はif文を使う
            if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                int scale = intent.getIntExtra("scale",0);
                int level = intent.getIntExtra("level",0);
                int status = intent.getIntExtra("status",0);
                String statusString = "";
                switch(status){
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        statusString = "unknown";
                        break;
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        statusString = "charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        statusString = "discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        statusString = "not charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        statusString = "full";
                        break;
                }
                final Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(calendar.HOUR_OF_DAY);
                final int minute = calendar.get(calendar.MINUTE);
                final int second = calendar.get(calendar.SECOND);

                String title = "BatteryWatch";
                String message = " " + hour + ":" + minute + ":" + second + " " + statusString + " " + level + "/" + scale;
                Log.v(title,message);

                if(level == 15){
                    int notificationId = intent.getIntExtra("notificationId",0);
                    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent bootIntent = new Intent(context, MainActivity.class);
                    Notification.Builder builder = new Notification.Builder(context);
                    builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setContentTitle("バッテリー残量")
                            .setContentText("１５％切っちゃった！")
                            .setWhen(System.currentTimeMillis())
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_SOUND);

                    notificationManager.notify(notificationId,builder.build());
                }

            }
        }

    };

}