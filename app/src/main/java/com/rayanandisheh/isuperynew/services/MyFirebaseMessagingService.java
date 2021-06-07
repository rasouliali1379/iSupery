package com.rayanandisheh.isuperynew.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rayanandisheh.isuperynew.R;


/**
 * FirebaseInstanceIdService Gets FCM instance ID token from Firebase Cloud Messaging Server
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "FCM Service";
    private static int count = 0;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e(TAG, "onNewToken: " + s);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        sendNotification(intent);
    }

    private void sendNotification(Intent intent) {
        String title = intent.getExtras().getString("title");
        String messageBody = intent.getExtras().getString("message");
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "iSupery");

        mBuilder.setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.ic_note)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_note))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setColor(Color.parseColor("#FFD600"))
                .setChannelId("iSupery")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (intent.getExtras().containsKey("link")){
            Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getExtras().getString("link")));
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
            mBuilder.setContentIntent(contentIntent);
        }

        mNotifyManager.notify(count, mBuilder.build());
        count++;
    }
}
