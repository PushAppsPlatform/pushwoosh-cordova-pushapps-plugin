package mobi.pushapps.plugins.pushwoosh;

import com.pushwoosh.notification.AbsNotificationFactory;
import com.pushwoosh.notification.PushData;

import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.app.NotificationManager;

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
	public void onPushReceived(PushData pushData) {
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
