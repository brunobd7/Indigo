/*
 * Functions SDK : is required to work with firebase functions.
 * Admin SDK : is required to send Notification using functions.
 */

'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


/*
 * 'OnWrite' works as 'addValueEventListener' for android. It will fire the function
 * everytime there is some item added, removed or changed from the provided 'database.ref'
 * 'sendNotification' is the name of the function, which can be changed according to
 * your requirement
 */

exports.envioDeNotificacao = functions.database.ref(`notification/{user_id}/{notification_id}`).onWrite((change, context) => {


  /*
   * You can store values as variables from the 'database.ref'
   * Just like here, I've done for 'user_id' and 'notification'
   */

   const user_id = context.params.user_id;
   const notification_id = context.params.notification_id;

  console.log('ID do usuário', context.params.user_id);

  /*
   * Stops proceeding to the rest of the function if the entry is deleted from database.
   * If you want to work with what should happen when an entry is deleted, you can replace the
   * line from "return console.log.... "
   */

  if(!change.data.val()){

    return console.log('Notificação deletada da base de dados : ', notification_id);

  }


  const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');

  return deviceToken.then(result => {

    const token_id = result.val();

    const payload = {
      notification: {
        title : "Solicitação de Amizade",
        body: "Você tem uma solicitação",
        icon: "default"
      }
    };

    return admin.messaging().sendToDevice(token_id, payload).then(response => {

      return console.log('Esses são os recursos de notificação');

    });

  });

});