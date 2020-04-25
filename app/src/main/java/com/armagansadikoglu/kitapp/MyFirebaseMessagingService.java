package com.armagansadikoglu.kitapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
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


import static com.armagansadikoglu.kitapp.App.CHANNEL_1_ID;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private NotificationManagerCompat notificationManagerCompat;
    @Override
    public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        final String bildirimTitle = remoteMessage.getNotification().getTitle();
        final String bildirimBody = remoteMessage.getNotification().getBody(); // bu bizim notification_idmiz


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(bildirimBody);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                com.armagansadikoglu.kitapp.Notification n = dataSnapshot.getValue(com.armagansadikoglu.kitapp.Notification.class);

                String senderName = n.getSenderName();
                String message = n.getMessage();


                // Notificationa tıklandığında MainActivity'yi açacak. MainActivity'e messages bilgisini göndererek bildirimden geldiğini anlatıyoruz
                Intent notificationIntent = new Intent(getApplicationContext(),MainActivity.class);
                notificationIntent.putExtra("menuFragment","messages");

                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),0,notificationIntent,0);

                    notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_book_black_24dp) // düşük api tel için
                            .setContentTitle(bildirimTitle)
                            .setContentText(senderName + ": " + message)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(contentIntent)
                            .setAutoCancel(true)
                            .build();
                   notificationManagerCompat.notify(1,notification);

                    Log.e("FCM", "başlık: " + bildirimTitle+" body : " + bildirimBody );



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d("TAG", "onNewToken: "+s);
        if(FirebaseAuth.getInstance().getCurrentUser() != null ) {
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("fcm_token").setValue(s);
        }
    }


}
