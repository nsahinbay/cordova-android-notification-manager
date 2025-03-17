package su.scat.calltaxi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.media.AudioAttributes;
import android.util.Log; // 📌 Eksik olan import eklendi

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationManagerPlugin extends CordovaPlugin {

    private static final String TAG = "NotificationManagerPlugin"; // 📌 Eksik TAG değişkeni eklendi

    @TargetApi(26)
    private void openAppNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Activity activity = this.cordova.getActivity();
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
            activity.startActivity(intent);
        }
    }

    @TargetApi(24)
    private boolean areNotificationsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Activity activity = this.cordova.getActivity();
            NotificationManager notificationManager = (NotificationManager) activity
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager != null && notificationManager.areNotificationsEnabled();
        }
        return true;
    }

    @TargetApi(26)
    private void openNotificationChannelSettings(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Activity activity = this.cordova.getActivity();
            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
            activity.startActivity(intent);
        }
    }

    @TargetApi(26)
    private JSONObject getNotificationChannel(String channelId) throws JSONException {
        JSONObject channelJSON = new JSONObject();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Activity activity = this.cordova.getActivity();
            NotificationManager manager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = manager.getNotificationChannel(channelId);

            if (channel != null) {
                channelJSON.put("id", channel.getId());
                channelJSON.put("group", channel.getGroup());
                channelJSON.put("description", channel.getDescription());
                channelJSON.put("importance", channel.getImportance());
                channelJSON.put("lightColor", channel.getLightColor());
                channelJSON.put("lockscreenVisibility", channel.getLockscreenVisibility());
            }
        }
        return channelJSON;
    }

    @TargetApi(26)
    private static void createChannel(Context context, String channelId, String name, String description, int importance, String soundFileName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.w(TAG, "Notification channels are not supported below Android 8.0 (API 26)");
            return;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            Log.e(TAG, "Failed to get NotificationManager instance");
            return;
        }

        // Kanal zaten var mı kontrol et
        NotificationChannel existingChannel = notificationManager.getNotificationChannel(channelId);
        if (existingChannel != null) {
            Log.i(TAG, "Notification channel already exists: " + channelId);
            return;
        }

        Log.d(TAG, "Creating notification channel: " + channelId);

        // Yeni kanal oluştur
        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        channel.enableLights(true);
        channel.enableVibration(true);

        // Özel ses dosyasını URI ile bağlama
        if (soundFileName != null && !soundFileName.isEmpty()) {
            String soundUriString = "android.resource://" + context.getPackageName() + "/raw/" + soundFileName;
            Uri soundUri = Uri.parse(soundUriString);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(soundUri, attributes);
            Log.d(TAG, "Custom sound set for channel: " + soundUriString);
        } else {
            Log.d(TAG, "No custom sound set for channel");
        }

        notificationManager.createNotificationChannel(channel);
        Log.i(TAG, "Notification channel created successfully: " + channelId);
    }


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            switch (action) {
                case "getNotificationChannel":
                    callbackContext.success(getNotificationChannel(args.getString(0)));
                    return true;

                case "openNotificationChannelSettings":
                    openNotificationChannelSettings(args.getString(0));
                    callbackContext.success();
                    return true;

                case "openAppNotificationSettings":
                    openAppNotificationSettings();
                    callbackContext.success();
                    return true;

                case "areNotificationsEnabled":
                    JSONObject s = new JSONObject();
                    s.put("status", areNotificationsEnabled());
                    callbackContext.success(s);
                    return true;

                case "createNotificationChannel":
                    String channelId = args.getString(0);
                    String name = args.getString(1);
                    String description = args.getString(2);
                    int importance = args.getInt(3);
                    String soundFileName = args.optString(4, null); // 📌 Null kontrolü eklendi
                    
                    createChannel(this.cordova.getActivity(), channelId, name, description, importance, soundFileName);
                    callbackContext.success();
                    return true;

                default:
                    return false;
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception: " + e.getMessage(), e);
            callbackContext.error("JSON error: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
            callbackContext.error("Unexpected error: " + e.getMessage());
        }

        return false;
    }
}
