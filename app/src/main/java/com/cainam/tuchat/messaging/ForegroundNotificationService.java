package com.cainam.tuchat.messaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.NotificationCompat;
import android.view.View;

import com.cainam.tuchat.R;
import com.firebase.client.Firebase;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by cainam on 8/24/17.
 */

public class ForegroundNotificationService extends com.google.firebase.messaging.FirebaseMessagingService{

    Context context = this;
    int mNotificationID = 1;
    int mMessageCount = 0;
    String notificationTitle;
    String notificationBody;
    String messages[] = new String[100];
    String mSharedMessage[] = new String[100];
    NotificationCompat.InboxStyle inboxStyle;
    int i = 1;

    RemoteMessage mRemoteMessage;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        this.mRemoteMessage = remoteMessage;

        NotificationOperationMessage(mNotificationID, mMessageCount);

        if (mNotificationID == 1){
            mMessageCount++;
            UpdateNotificationMessage(mNotificationID, mMessageCount);

        }

    }

    public void NotificationOperationMessage(int mNotificationID, int mMessageCount){

        notificationTitle = mRemoteMessage.getNotification().getTitle();
        notificationBody =mRemoteMessage.getNotification().getBody();
        String clickAction = mRemoteMessage.getNotification().getClickAction();
        String fromUID = mRemoteMessage.getData().get("fromUID");

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.friend_request_notification_1)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_MAX)
                .setOnlyAlertOnce(true)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true);

        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("userID", fromUID);

        NotificationCompat.InboxStyle inboxStyle = new android.support.v4.app.NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("TuChat APP Messages");

        final SharedPreferences sharedPreferences = getSharedPreferences("TuChat APP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(messages[i], notificationBody);
        editor.commit();

        mSharedMessage[i] = sharedPreferences.getString(messages[i], "n/a");
        inboxStyle.addLine(mSharedMessage[i]);
        inboxStyle.addLine(mSharedMessage[i]);
        inboxStyle.setSummaryText("You have " + mMessageCount + " New Messages");
        mBuilder.setStyle(inboxStyle);

        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationID, mBuilder.build());

    }


    public void UpdateNotificationMessage(int mNotificationID, int mMessageCount){
        mMessageCount++;
        NotificationOperationMessage(mNotificationID, mMessageCount);
    }
}

