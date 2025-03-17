var NotificationChannel = function(channelJSON) {
    for (var property in channelJSON) {
        if (channelJSON.hasOwnProperty(property)) {
            this[property] = channelJSON[property];
        }
    }

    this.openSettings = function() {
        return NotificationManager.openNotificationChannelSettings(this.id);
    };

    this._openSettings = function(onSuccess, onFail) {
        NotificationManager._openNotificationChannelSettings(this.id, onSuccess, onFail);
    };
};

var NotificationManager = function() { };

function statusSuccess(status) {
    console.log(status);
};

function statusError() {
    console.log("Something went wrong");
};

NotificationManager.SERVICE_NAME = 'NotificationManagerPlugin';

/**
 * Notification Importance Levels (Android NotificationManager)
 */
NotificationManager.IMPORTANCE_DEFAULT = 3;
NotificationManager.IMPORTANCE_HIGH = 4;
NotificationManager.IMPORTANCE_LOW = 2;
NotificationManager.IMPORTANCE_MAX = 5;
NotificationManager.IMPORTANCE_MIN = 1;
NotificationManager.IMPORTANCE_NONE = 0;
NotificationManager.IMPORTANCE_UNSPECIFIED = -1000;

/**
 * Get notification channel details
 * @param {string} channelId
 * @returns {Promise<NotificationChannel>}
 */
NotificationManager.getNotificationChannel = function(channelId) {
    return new Promise(function(onSuccess, onFail) {
        NotificationManager._getNotificationChannel(channelId, onSuccess, onFail);
    });
};

/**
 * Open App Notification Settings
 * @returns {Promise}
 */
NotificationManager.openAppNotificationSettings = function() {
    return new Promise(function(onSuccess, onFail) {
        NotificationManager._openAppNotificationSettings(onSuccess, onFail);
    });
};

/**
 * Open Notification Channel Settings
 * @param {string} channelId
 * @returns {Promise}
 */
NotificationManager.openNotificationChannelSettings = function(channelId) {
    return new Promise(function(onSuccess, onFail) {
        NotificationManager._openNotificationChannelSettings(channelId, onSuccess, onFail);
    });
};

/**
 * Create a new Notification Channel
 * @param {string} channelId - Unique ID for the channel
 * @param {string} name - Name of the channel
 * @param {string} description - Description of the channel
 * @param {number} importance - Importance level of the channel (see NotificationManager.IMPORTANCE_* constants)
 * @returns {Promise}
 */
NotificationManager.createNotificationChannel = function(channelId, name, description, importance) {
    return new Promise(function(onSuccess, onFail) {
        NotificationManager._createNotificationChannel(channelId, name, description, importance, onSuccess, onFail);
    });
};

/**
 * Check if notifications are enabled
 * @returns {Promise}
 */
NotificationManager.areNotificationsEnabled = function() {
    return new Promise(function(onSuccess, onFail) {
        NotificationManager._areNotificationsEnabled(onSuccess, onFail);
    });
};

/**
 * Private methods for Cordova exec calls
 */
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

NotificationManager._createNotificationChannel = function(channelId, name, description, importance, onSuccess, onFail) {
    cordova.exec(onSuccess, onFail, NotificationManager.SERVICE_NAME, 'createNotificationChannel', [channelId, name, description, importance]);
};

NotificationManager._areNotificationsEnabled = function(onSuccess, onFail) {
    cordova.exec(onSuccess, onFail, NotificationManager.SERVICE_NAME, 'areNotificationsEnabled');
};

module.exports = NotificationManager;
