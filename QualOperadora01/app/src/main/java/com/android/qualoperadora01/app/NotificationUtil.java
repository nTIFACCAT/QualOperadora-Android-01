package com.android.qualoperadora01.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by RICARDO on 06/07/2014.
 */
public class NotificationUtil {
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")

    // Cria uma notificação que chama um serviço de atualização da agenda.
    public static void create(Context context, CharSequence tickerText, CharSequence title, CharSequence message, int icon, int id, Intent intent){
        // Pending para executar a intent ao selecionar a notificação
        PendingIntent p = PendingIntent.getService(context, 0, intent,0);
        Notification n = null;
        int apiLevel = Build.VERSION.SDK_INT;
        if(apiLevel>=11){
            //Notification Builder, utilizada para versões acima de 3.x
            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle(tickerText)
                    .setContentText(message)
                    .setSmallIcon(icon)
                    .setContentIntent(p);

            if(apiLevel>=17){
                //Android 4.2
                n=builder.build();
            }else{
                //Android 3.x
                n=builder.getNotification();
            }
        }else{
            // Em deprecated, utilizada nas versões mais inferiores do android
            //Android 2.2
            n = new Notification(icon,tickerText,System.currentTimeMillis());
            //Informações
            n.setLatestEventInfo(context,title,message,p);
        }
        //id numero único que identifica esta notificação
        NotificationManager nm = (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
        nm.notify(id,n);
    }

    public static void cancell(Context context, int id){
        NotificationManager nm = (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
        nm.cancel(id);
    }




}
