package mobi.pushapps.plugins.pushwoosh.CustomPushWooshIntentService;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import mobi.pushapps.PABuildNotificationListener;
import mobi.pushapps.PushApps;
import mobi.pushapps.models.PANotification;
import mobi.pushapps.utils.PADeviceUtils;
import mobi.pushapps.utils.PALogger;

import com.arellomobile.android.push.PushGCMIntentService;

public class CustomPushWooshIntentService extends PushGCMIntentService {
	
	@Override
	protected void onMessage(Context context, Intent intent) { 
		
		// Extract the push notification details
		Bundle extras = intent.getExtras();
		
		String notificationCustomJSON = extras.getString("u");
		JSONObject json = null;
		
		String campaignId = null;
				
		if (notificationCustomJSON != null) {
			try {
				json = new JSONObject(notificationCustomJSON);
								
				if (json.has("c")) {
					campaignId = json.getString("c");
				}
				
			} catch (Exception e) {
				PALogger.log("Could not parse the notification custom JSON");
			}
		}

		String notificationMessage = extras.getString("title");
		String notificationTitle = extras.containsKey("header") ? extras.getString("header") : PADeviceUtils.getApplicationName(context);
		String notificationImage = extras.getString("i");
		String notificationSound = extras.getString("s");
		String notificationUrl = extras.getString("u");
		
		PANotification notification = new PANotification.Builder()
                .campaignId(campaignId)
                .text(notificationMessage)  
                .sound(notificationSound) // optional
                .title(notificationTitle) // optional
                .url(notificationUrl)
                .imageUrl(notificationImage)
                .build();
        PushApps.buildNotification(context, intent, notification, new PABuildNotificationListener() {
            @Override
            public void onPushAppsBuildNotificationFailure(Context context, Intent intent, String message) {
            	CustomPushWooshIntentService.super.onMessage(context, intent);
            }
        });
				
	}

}
