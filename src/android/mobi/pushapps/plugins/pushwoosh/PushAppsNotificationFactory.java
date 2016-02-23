package mobi.pushapps.plugins.pushwoosh;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.pushwoosh.PushManager;
import com.pushwoosh.internal.PushManagerImpl;
import com.pushwoosh.notification.AbsNotificationFactory;
import com.pushwoosh.notification.PushData;

import mobi.pushapps.PABuildNotificationListener;
import mobi.pushapps.PushApps;
import mobi.pushapps.models.PANotification;
import mobi.pushapps.utils.PADeviceUtils;

public class PushAppsNotificationFactory extends AbsNotificationFactory {
    
    @Override
    public Notification onGenerateNotification(PushData pushData) {
        return null;
    }
    
    @Override
    public void onPushHandle(Activity arg0) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onPushReceived(final PushData pushData) {
        Bundle extras = pushData.getExtras();
        
        String campaignId = null;
        String image = null;
        String url = null;
        
        if (extras != null) {
            if (extras.containsKey("pa_c")) {
                campaignId = extras.getString("pa_c");
            }
            if (extras.containsKey("pa_i")) {
                image = extras.getString("pa_i");
            }
            if (extras.containsKey("pa_u")) {
                url = extras.getString("pa_u");
            }
        }
        
        final String notificationMessage = pushData.getMessage();
        final String notificationTitle = pushData.getHeader() != null ? pushData.getHeader() : PADeviceUtils.getApplicationName(getContext());
        final String notificationImage = image;
        final String notificationSound = pushData.getSound();
        final String notificationUrl = url;
        
        final PushData pushDataFallback = pushData;
        
        PANotification notification = new PANotification.Builder()
        .campaignId(campaignId)
        .text(notificationMessage)
        .sound(notificationSound)
        .title(notificationTitle)
        .url(notificationUrl)
        .imageUrl(notificationImage)
        .build();
        PushApps.buildNotification(getContext(), getNotifyIntent(), notification, new PABuildNotificationListener() {
            
            @Override
            public void onPushAppsBuildNotificationFailure(Context arg0, Intent arg1, String arg2) {
                
                // Regular notification
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(arg0);
                notificationBuilder.setContentTitle(getContentFromHtml(pushDataFallback.getHeader()));
                notificationBuilder.setContentText(getContentFromHtml(pushDataFallback.getMessage()));
                notificationBuilder.setSmallIcon(pushDataFallback.getSmallIcon());
                notificationBuilder.setTicker(getContentFromHtml(pushDataFallback.getTicker()));
                notificationBuilder.setWhen(System.currentTimeMillis());
                
                try {
                    Intent layoutIntent = null;
                    layoutIntent = new Intent(arg0.getApplicationContext(), Class.forName("com.pushwoosh.PushHandlerActivity"));
                    layoutIntent.setAction(arg0.getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
                    
                    layoutIntent.putExtra(PushManager.PUSH_RECEIVE_EVENT, PushManagerImpl.bundleToJSON(pushDataFallback.getExtras()).toString());
                    layoutIntent.putExtras(pushDataFallback.getExtras());
                    layoutIntent.putExtra("pushBundle", pushDataFallback.getExtras());
                    PendingIntent layoutPendingIntent = PendingIntent.getActivity(arg0, 0, layoutIntent,
                                                                                  PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder.setContentIntent(layoutPendingIntent);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                if (pushDataFallback.getBigPicture() != null) {
                    notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(pushDataFallback.getBigPicture()).setSummaryText(getContentFromHtml(pushDataFallback.getMessage())));
                }
                else {
                    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(getContentFromHtml(pushDataFallback.getMessage())));
                }
                if (pushDataFallback.getIconBackgroundColor() != null) {
                    notificationBuilder.setColor(pushDataFallback.getIconBackgroundColor());
                }
                if (null != pushDataFallback.getLargeIcon()) {
                    notificationBuilder.setLargeIcon(pushDataFallback.getLargeIcon());
                }
                final Notification notification = notificationBuilder.build();
                addSound(notification, pushDataFallback.getSound());
                addVibration(notification, pushDataFallback.getVibration());
                addCancel(notification);
                
                int notId = (int)(Math.random() * 230485 + 1);
                NotificationManager nm = (NotificationManager) arg0.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.notify(notId, notification);
            }
        });
    }
    
}
