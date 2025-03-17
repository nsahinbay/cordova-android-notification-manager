package su.scat.calltaxi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationManagerPlugin extends CordovaPlugin {

    @TargetApi(26)
    private void openAppNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Activity activity = this.cordova.getActivity();
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
            activity.startActivity(intent);
        }
    }

    @TargetApi(24)
    private boolean areNotificationsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Activity activity = this.cordova.getActivity();
            final NotificationManager notificationManager = (NotificationManager) activity
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            return notificationManager != null && notificationManager.areNotificationsEnabled();
        }
        return true;
    }

    @TargetApi(26)
    private void openNotificationChannelSettings(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Activity activity = this.cordova.getActivity();
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
            final Activity activity = this.cordova.getActivity();
            final NotificationManager manager = (NotificationManager) activity
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationChannel channel = manager.getNotificationChannel(channelId);

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
    private void createNotificationChannel(String channelId, String name, String description, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Activity activity = this.cordova.getActivity();
            final NotificationManager notificationManager = (NotificationManager) activity
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(channelId, name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if ("getNotificationChannel".equals(action)) {
                callbackContext.success(getNotificationChannel(args.getString(0)));
                return true;
            }

            if ("openNotificationChannelSettings".equals(action)) {
                openNotificationChannelSettings(args.getString(0));
                callbackContext.success();
                return true;
            }

            if ("openAppNotificationSettings".equals(action)) {
                openAppNotificationSettings();
                callbackContext.success();
                return true;
            }

            if ("areNotificationsEnabled".equals(action)) {
                JSONObject s = new JSONObject();
                s.put("status", areNotificationsEnabled());
                callbackContext.success(s);
                return true;
            }

            if ("createNotificationChannel".equals(action)) {
                String channelId = args.getString(0);
                String name = args.getString(1);
                String description = args.getString(2);
                int importance = args.getInt(3);
                createNotificationChannel(channelId, name, description, importance);
                callbackContext.success();
                return true;
            }

        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
        }

        return false;
    }
}
