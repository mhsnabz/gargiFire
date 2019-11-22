package com.rzn.gargi.CloudMessagingService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rzn.gargi.R;
import com.rzn.gargi.chat.Messege;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().isEmpty())
            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        else
            showNotification(remoteMessage.getData());
    }
    private void showNotification(Map<String, String> data)
    {
        final String baslik;
        final String[] gonderen = new String[1];
        final String tipi;
        String title= data.get("title");
        String body = data.get("body");
        String type = data.get("type");
        String userId = data.get("user_id");
        if (!title.isEmpty()){
            if (title.equals("yeni mesaj")){
                baslik=getResources().getString(R.string.yeni_bir_mesaj_var);
            }else if (title.equals("esleme")){
                baslik=getResources().getString(R.string.yeni_bir_eslesmen_var_);
            }
        }
        if (!userId.isEmpty()){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("allUser")
                    .document(userId).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot!=null){
                        if (documentSnapshot.getString("name")!=null){
                            gonderen[0] =documentSnapshot.getString("name");
                        }
                    }
                }
            });
        }
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channel = "com.gargi";
        String channel_2 = "com.gargii";
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1
                    = new NotificationChannel(channel,"bildirim",
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Gargii");
            channel1 .enableLights(true);
            channel1.setLightColor(Color.BLUE);
            //   channel1.setVibrationPattern(new long[]{0,1000,500,1000});

            manager.createNotificationChannel(channel1);
            Intent configureIntent = new Intent(getApplicationContext(), Messege.class);
            configureIntent.putExtra("extra", "123123");
            configureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channel);
            builder.setAutoCancel(true);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.notification_logo);
            builder.setContentTitle(title);
            builder.setContentText(body);
            builder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.sound));
            builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            PendingIntent pendingClearScreenIntent = PendingIntent.getActivity(getApplicationContext(), 0, configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingClearScreenIntent);
            builder.setContentInfo("info");
            manager.cancelAll();
            manager.notify(new Random().nextInt(),builder.build());

        }
        else{
            Intent configureIntent = new Intent(getApplicationContext(), Messege.class);
            configureIntent.putExtra("extra", "123123");
            configureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channel);
            builder.setAutoCancel(true);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.notification_logo);
            builder.setContentTitle(title);
            builder.setContentText(body);
            //  builder.setDefaults(Notification.DEFAULT_VIBRATE);

            builder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.sound));
            PendingIntent pendingClearScreenIntent = PendingIntent.getActivity(getApplicationContext(), 0, configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingClearScreenIntent);

            builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            manager.cancelAll();


            manager.notify(new Random().nextInt(),builder.build());

        }
    }

    private void showNotification(String title,String body){



        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channel = "com.gargi";
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel  channel1
                    = new NotificationChannel(channel,"bildirim",
                    NotificationManager.IMPORTANCE_UNSPECIFIED);
            Intent configureIntent = new Intent(getApplicationContext(), Messege.class);
            configureIntent.putExtra("extra", "123123");
            configureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingClearScreenIntent = PendingIntent.getActivity(getApplicationContext(), 0, configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            channel1.setDescription("Gargii");
            channel1 .enableLights(true);
            channel1.setLightColor(Color.BLUE);
            // channel1.setVibrationPattern(new long[]{0,1000,500,1000});
            manager.createNotificationChannel(channel1);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channel);
            builder.setAutoCancel(true);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.notification_logo);
            builder.setContentTitle(title);
            builder.setContentText(body);
            builder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.sound));
            builder.setContentIntent(pendingClearScreenIntent);
            builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
            manager.cancelAll();

            manager.notify(new Random().nextInt(),builder.build());

        }
        else{
            Intent configureIntent = new Intent(getApplicationContext(), Messege.class);
            configureIntent.putExtra("extra", "123123");
            configureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingClearScreenIntent = PendingIntent.getActivity(getApplicationContext(), 0, configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channel);
            builder.setAutoCancel(true);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.mipmap.notification_logo);
            builder.setContentTitle(title);
            builder.setContentText(body);
            //  builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.sound));
            builder.setContentIntent(pendingClearScreenIntent);
            builder.build().flags |= Notification.FLAG_AUTO_CANCEL;


         /*   NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this,channel);
            builder2.setAutoCancel(true);
            builder2.setWhen(System.currentTimeMillis());
            builder2.setSmallIcon(R.mipmap.logo);
            builder2.setContentTitle(title);
            builder2.setContentText(body);
            builder2.setVibrate(new long[]{0,1000,500,1000});
            builder2.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.sound));
            builder2.setContentIntent(pendingClearScreenIntent);
            builder2.build().flags |= Notification.FLAG_AUTO_CANCEL;
            Notification summaryNotification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                    .setSmallIcon(R.mipmap.logo)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine("Yeni Mesajın Var")
                            .addLine("Yeni Mesajın Var")
                            .setBigContentTitle("Mesajların Var")
                            .setSummaryText("Gargii"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setGroup("example_group")
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setGroupSummary(true)
                    .build();*/
            manager.cancelAll();
            manager.notify(new Random().nextInt(),builder.build());
            // manager.notify(new Random().nextInt(),builder2.build());
            // manager.notify(new Random().nextInt(),summaryNotification);


        }
    }
    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.e("TOKEN",mToken);
    }

}
