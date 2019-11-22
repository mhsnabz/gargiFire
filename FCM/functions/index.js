'use-strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firestore);
exports.sendNotification =functions.firestore.document("/notification/{user_id}/notification/{notification_id}").onWrite((change, context) =>{
    const user_id = context.params.user_id;
    const notification_id = context.params.notification_id;


    console.log("UserId: " + user_id + " | Notification ID : " + notification_id);

    return admin.firestore()
    .collection("notification")
    .doc(user_id)
    .collection("notification").doc(notification_id).get()
    .then(queryResult =>{
        const from_user_id=queryResult.data().from; 
        const type = queryResult.data().type;
        const tokenId = queryResult.data().tokenID;
        const title = queryResult.data().title;
        console.log("type = " +type +"  from_user"+from_user_id+"to_user    "+user_id +"  tokenID "+ tokenId);
        const from_data = admin.firestore()

        .collection("allUser").doc(from_user_id).get();
        const to_data = admin.firestore().collection("allUser")
        .doc(user_id).get();
        const result = Promise.all([from_data],[to_data]);
        //const from_name = result[0];
        //const to_name = result[1].name;
        console.log(result[0] +":  "+result[1]+":   " + type);
        const payload = {
            notification: {
              title: title,
              body: type,
              icon: "default",
              sound : "sound.mp3",
             

            },
            data : {
            "body" : type,
            "title": title,
            "userID" : user_id,
            "type" : type
            }
                };
                return admin.messaging().sendToDevice(tokenId, payload);
      
       

    });



});


