//import firebase functions modules
const functions = require('firebase-functions');
//import admin module
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.pushNotification = functions.database.ref('/posts').onWrite((change, context) => {
    console.log('Push notification event triggered');

    //  Get the current value of what was written to the Realtime Database.
    const valueObject = change.after.val();

    // Create a notification
    const payload = {
        notification: {
            title: valueObject.title,
            body: valueObject.message,
            sound: "default"
        }
    };

    //Create an options object that contains the time to live for the notification and the priority
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };

    return admin.messaging().sendToTopic("pushNotifications", payload, options);
});