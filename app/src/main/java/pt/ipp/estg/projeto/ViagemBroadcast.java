package pt.ipp.estg.projeto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ViagemBroadcast extends BroadcastReceiver {

    private String morada;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify")
                .setSmallIcon(R.drawable.ic_navigation_black_24dp)
                .setContentTitle("Em viagem")
                .setContentText(morada)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }
}
