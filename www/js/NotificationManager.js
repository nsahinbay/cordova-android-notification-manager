var NotificationChannel = function(channelJSON) {

        for (var property in channelJSON)
        {
            if (channelJSON.hasOwnProperty(property))
            {
                this[property] = channelJSON[property];
            }
        }

        this.openSettings = function()
        {
            return NotificationManager.openNotificationChannelSettings(this.id);
        };

        this._openSettings = function(onSuccess, onFail) {
            NotificationManager._openNotificationChannelSettings(this.id, onSuccess, onFail);
        };
    },
    NotificationManager = function() {
    };

function statusSuccess(status) {
    console.log(status);
};

function statusError() {
    console.log("something went wrong");
};

NotificationManager.SERVICE_NAME = 'NotificationManagerPlugin';
/**
 * @see https://developer.android.com/reference/android/app/NotificationManager.html#IMPORTANCE_DEFAULT
 * @type {number}
 */
NotificationManager.IMPORTANCE_DEFAULT = 3;
/**
 * @see https://developer.android.com/reference/android/app/NotificationManager.html#IMPORTANCE_HIGH
 * @type {number}
 */
NotificationManager.IMPORTANCE_HIGH = 4;
/**
 * @see https://developer.android.com/reference/android/app/NotificationManager.html#IMPORTANCE_LOW
 * @type {number}
 */
NotificationManager.IMPORTANCE_LOW = 2;
/**
 * @see https://developer.android.com/reference/android/app/NotificationManager.html#IMPORTANCE_MAX
 * @type {number}
 */
NotificationManager.IMPORTANCE_MAX = 5;
/**
 * @see https://developer.android.com/reference/android/app/NotificationManager.html#IMPORTANCE_MIN
 * @type {number}
 */
NotificationManager.IMPORTANCE_MIN = 1;
/**
 * @see https://developer.android.com/reference/android/app/NotificationManager.html#IMPORTANCE_NONE
 * @type {number}
 */
NotificationManager.IMPORTANCE_NONE = 0;
/**
 * @see https://developer.android.com/reference/android/app/NotificationManager.html#IMPORTANCE_UNSPECIFIED
 * @type {number}
 */
NotificationManager.IMPORTANCE_UNSPECIFIED = -1000;


NotificationManager.getNotificationChannel = function(channelId) {
    return new Promise(function(onSuccess, onFail) {
        NotificationManager._getNotificationChannel(channelId, onSuccess, onFail);
    });
};


NotificationManager.openAppNotificationSettings = function(channelId) {
    return new Promise(function(onSuccess, onFail) {
        NotificationManager._openAppNotificationSettings(channelId, onSuccess, onFail);
    });
};


NotificationManager.openNotificationChannelSettings = function(channelId) {
    return new Promise(function(onSuccess, onFail) {
        NotificationManager._openNotificationChannelSettings(channelId, onSuccess, onFail);
    });
};

NotificationManager._getNotificationChannel = function(channelId, onSuccess, onFail) {
    cordova.exec(function(channelJSON) {
        onSuccess(new NotificationChannel(channelJSON));
    }, onFail, NotificationManager.SERVICE_NAME, 'getNotificationChannel', [channelId]);
};

NotificationManager._openAppNotificationSettings = function(onSuccess, onFail) {
    cordova.exec(onSuccess, onFail, NotificationManager.SERVICE_NAME, 'openAppNotificationSettings');
};

NotificationManager._openNotificationChannelSettings = function(channelId, onSuccess, onFail) {
    cordova.exec(function(channelJSON) {
        onSuccess(new NotificationChannel(channelJSON));
    }, onFail, NotificationManager.SERVICE_NAME, 'openNotificationChannelSettings', [channelId]);
};

NotificationManager.areNotificationsEnabled = function(onSuccess, onFail) {
    cordova.exec(onSuccess, onFail, NotificationManager.SERVICE_NAME, 'areNotificationsEnabled');
};


module.exports = NotificationManager;
