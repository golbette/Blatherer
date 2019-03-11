package team7.tcss450.uw.edu.tcss_750_t7_2.utils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import me.pushy.sdk.Pushy;
import team7.tcss450.uw.edu.tcss_750_t7_2.MainActivity;
import team7.tcss450.uw.edu.tcss_750_t7_2.R;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class PushReceiver extends BroadcastReceiver {

    public static final String RECEIVED_NEW_MESSAGE = "New message on Blatherer";
    public static final String RECEIVED_NEW_CONN_REQUEST = "New connection";
    public static final String RECEIVED_NEW_CONVO_REQUEST = "New conversation";

    /** Stored as a chatid in the chats relation in the database. Reserved for request related push notifications. */
    public static final int REQUEST_ID = 21;

    private String mChatid ;

    @Override
    public void onReceive(Context context, Intent intent) {
        //the following variables are used to store the information sent from Pushy
        //In the WS, you define what gets sent. You can change it there to suit your needs
        //Then here on the Android side, decide what to do with the message you got

        //for the lab, the WS is only sending chat messages so the type will always be msg
        //for your project, the WS need to send different types of push messages.
        //perform so logic/routing based on the "type"
        //feel free to change the key or type of values. You could use numbers like HTTP: 404 etc
        String typeOfMessage = intent.getStringExtra("type");
        String sender = intent.getStringExtra("sender"); //The WS sent us the name of the sender
        String messageText = intent.getStringExtra("message");
        int chatId = intent.getExtras().getInt("chatid");

        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) { // In app notification
            //app is in the foreground so send the message to the active Activities
            Log.d("Blatherer", "Message received in foreground: " + messageText);

            //create an Intent to broadcast a message to other parts of the app.
            Intent i = new Intent(RECEIVED_NEW_MESSAGE);
            i.putExtra("CHATID", chatId);
            i.putExtra("MSGTYPE", typeOfMessage);
            i.putExtra("SENDER", sender);
            i.putExtra("MESSAGE", messageText);
            i.putExtras(intent.getExtras());

            context.sendBroadcast(i);

        } else { // Out-of-app notification
            //app is in the background so create and post a notification
            Log.d("Blatherer", "Message received in background: " + messageText);

            Intent i = new Intent(context, MainActivity.class);
            i.putExtras(intent.getExtras());

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    i, PendingIntent.FLAG_UPDATE_CURRENT);

            //research more on notifications the how to display them
            //https://developer.android.com/guide/topics/ui/notifiers/notifications
            NotificationCompat.Builder builder;

            if (chatId == REQUEST_ID) {
                builder = new NotificationCompat.Builder(context, mChatid)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_requests)
                        .setContentTitle("Connection request from: " + sender)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent);
            } else {
                builder = new NotificationCompat.Builder(context, mChatid)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_chat_black)
                        .setContentTitle("Message from: " + sender)
                        .setContentText(messageText)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent);
            }
            // Automatically configure a Notification Channel for devices running Android O+
            Pushy.setNotificationChannel(builder, context);

            // Get an instance of the NotificationManager service
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            // Build the notification and display it
            notificationManager.notify(1, builder.build());
        }
    }
}
