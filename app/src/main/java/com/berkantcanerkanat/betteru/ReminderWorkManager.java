package com.berkantcanerkanat.betteru;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.berkantcanerkanat.betteru.singleton.User;

import java.util.Set;

public class ReminderWorkManager extends Worker {
    User user;
    SQLiteDatabase db;
    Context context;
    public ReminderWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        user = User.getInstance();
        String title,content;
        Data data = getInputData();
        String deger = "";
        Set<String> tags = getTags();
        String dbTag = null;
        //dependency unutma
        for(String tag: tags){
            if(tag.contains("-")){
                deger = data.getString(tag);
                dbTag = tag;
                break;
            }
        }
        String[] degerArr = deger.split("-");
        title = degerArr[1];
        content = degerArr[0];

        Intent intent = new Intent(getApplicationContext(),MenuFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // api 26 ve ustu ıcın
            CharSequence channel_name = "channel_name";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("sample_channel_id",channel_name,importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"sample_channel_id")
                .setSmallIcon(R.drawable.ic_done)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound( RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        notificationManager.notify(5,builder.build());
        //db den sil
        try {
            db = context.openOrCreateDatabase("BetterU",Context.MODE_PRIVATE,null);
            db.delete("reminders","uid = ?",new String[] {dbTag});
        }catch (Exception e){
            e.printStackTrace();
        }
        user.reminders_listuid.remove(dbTag);
        return Result.success();
    }
}
