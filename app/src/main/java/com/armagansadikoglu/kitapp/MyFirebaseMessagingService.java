package com.armagansadikoglu.kitapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Map;

import static com.armagansadikoglu.kitapp.App.CHANNEL_1_ID;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private NotificationManagerCompat notificationManagerCompat;

    @Override
    public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        final String title = data.get("title");
       final String body = data.get("body");
        


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(body);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                com.armagansadikoglu.kitapp.Notification n = dataSnapshot.getValue(com.armagansadikoglu.kitapp.Notification.class);

                String senderName = n.getSenderName();
                String message = n.getMessage();


                // Notificationa tıklandığında MainActivity'yi açacak. MainActivity'e messages bilgisini göndererek bildirimden geldiğini anlatıyoruz
                Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                notificationIntent.putExtra("menuFragment", "messages");

                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_book_black_24dp) // düşük api tel için
                        .setContentTitle(title)
                        .setContentText(senderName + ": " + message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .build();
                notificationManagerCompat.notify(1, notification);

                Log.e("FCM", "başlık: " + title + " body : " + body);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d("TAG", "onNewToken: " + s);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("fcm_token").setValue(s);
        }
    }


}
