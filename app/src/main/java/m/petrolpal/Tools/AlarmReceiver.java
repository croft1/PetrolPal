package m.petrolpal.Tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;

import m.petrolpal.R;
import m.petrolpal.TabsActivity;

/**
 * Created by m on 6/13/2016.
 */
public class AlarmReceiver extends BroadcastReceiver{

    int nId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, TabsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context);

        notifyBuilder.setSmallIcon(R.drawable.pp_logo)
                .setContentTitle("Fuel Prices: ")
                .setContentText(context.getString(R.string.placeholder_fuelprice))
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        notificationManager.notify(nId, notifyBuilder.build());
        nId++;
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }

    public AlarmReceiver() {
        super();
    }
}
