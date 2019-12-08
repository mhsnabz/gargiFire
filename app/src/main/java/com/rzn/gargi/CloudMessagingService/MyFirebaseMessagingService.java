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
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String type,user_id,name,title,body;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

           showNotification(remoteMessage.getData());
    }


    private void showNotification(Map<String, String> data)
    {

        String type= data.get("type");
        String body = "";
        String name = data.get("name");
        String gender =data.get("gender");
        String rate =data.get("rate");

        if (type.equals("msg")){
            type=getResources().getString(R.string.yeni_bir_mesaj_var);

            body = name +"  "+ getResources().getString(R.string.sana_yeni_bir_mesaj_gonderdi);
        }
       else if (type.equals("match")){
            type=getResources().getString(R.string.yeni_bir_eslesmen_var_ );

            body = name ;
        }else if (type.equals("rate")){
           if (gender.equals("MAN"))
           {
               type=getResources().getString(R.string.rate);
               body=getResources().getString(R.string.bir_beyefendi_size)+" "+rate+" "+getResources().getString(R.string.puan_verdi);

           }else if (gender.equals("WOMAN")){
               type=getResources().getString(R.string.rate);
               body=getResources().getString(R.string.bir_hanımefendi_size)+" "+rate+" "+getResources().getString(R.string.puan_verdi);
           }
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
            configureIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channel);
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.notification_logo);

            builder.setWhen(System.currentTimeMillis());
            builder.setContentTitle(type);
            builder.setContentText(body);
            builder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.sound));
            builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
           PendingIntent pendingClearScreenIntent = PendingIntent.getActivity(getApplicationContext(), (int)(Math.random() * 100), configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingClearScreenIntent);
            builder.setContentInfo("info");
         //   manager.cancelAll();
            manager.notify(new Random().nextInt(),builder.build());

        }
        else{
            Intent configureIntent = new Intent(getApplicationContext(), Messege.class);
            configureIntent.putExtra("extra", "123123");
            configureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channel);
            builder.setAutoCancel(true);
            builder.setWhen(System.currentTimeMillis());
            builder.setSmallIcon(R.drawable.notification_logo);
            builder.setContentTitle(type);
            builder.setContentText(body);
            //  builder.setDefaults(Notification.DEFAULT_VIBRATE);

            builder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.sound));
            PendingIntent pendingClearScreenIntent = PendingIntent.getActivity(getApplicationContext(), (int)(Math.random() * 100), configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingClearScreenIntent);

            builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
           // manager.cancelAll();


            manager.notify(new Random().nextInt(),builder.build());

        }
    }

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.e("TOKEN",mToken);
    }

}
