package sg.tgonet.singtransport.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import sg.tgonet.singtransport.R;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String BusNumber = intent.getStringExtra("BusNumber");
        String message = "Bus " + BusNumber + " is arriving soon";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Notifications notifications = new Notifications(context);
            Notification.Builder builder = notifications.getOreoNotification("Bus Arrival",message,defaultSound, R.drawable.ic_baseline_directions_bus_24);
            notificationManager.notify(0, builder.build());
        }
        else{
            Notification builder = new Notification.Builder(context)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setSmallIcon(R.drawable.ic_baseline_directions_bus_24)
                    .setContentTitle("Bus Arrival")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setShowWhen(true)
                    .setSound(defaultSound).build();
            notificationManager.notify(0, builder);
        }
    }
}
