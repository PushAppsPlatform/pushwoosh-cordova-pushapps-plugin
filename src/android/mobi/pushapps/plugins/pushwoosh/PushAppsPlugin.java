package mobi.pushapps.plugins.pushwoosh;

import android.content.Context;
import mobi.pushapps.PushApps;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import com.pushwoosh.PushManager;

/**
 * @author PushApps
 */

public class PushAppsPlugin extends CordovaPlugin {
    
    public static final String TAG = "PushAppsPlugin";
    
    private Context getApplicationContext() {
        return this.cordova.getActivity().getApplicationContext();
    }
    
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) {
        
        boolean result = false;
        
        return result;
    }
    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        
        PushApps.register(getApplicationContext());
        
        PushManager pushManager = PushManager.getInstance(getApplicationContext());
        pushManager.setNotificationFactory(new PushAppsNotificationFactory());
    }
    
    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
    }
    
    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
}