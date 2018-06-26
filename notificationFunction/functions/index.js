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

 //USAR PARAMETROS "CHANGE E CONTEXT" para correçao do problema com "event" como parametro
exports.sendNotification = functions.database.ref('Notifications/{user_id}/{notification_id}').onWrite((change,context) => {


  /*
   * You can store values as variables from the 'database.ref'
   * Just like here, I've done for 'user_id' and 'notification'
   */
  
  //const afterData=change.after.val();
  const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;

  console.log('Nós temos uma notificação de : ', user_id);

  /*
   * Stops proceeding to the rest of the function if the entry is deleted from database.
   * If you want to work with what should happen when an entry is deleted, you can replace the
   * line from "return console.log.... "
   */

  //USANDO METODO "AFTER" para a exibir o resultado depois do evento 
  if(!change.after.val()){

    return console.log('A notificação foi deletada do banco de dados : ', notification_id);

  }

  /*
   * 'fromUser' query retreives the ID of the user who sent the notification
   */

  const fromUser = admin.database().ref(`/Notifications/${user_id}/${notification_id}`).once('value');

  return fromUser.then(fromUserResult => {

    const from_user_id = fromUserResult.val().from;

    console.log('Você tem uma notificação de : ', from_user_id);

    /*
     * The we run two queries at a time using Firebase 'Promise'.
     * One to get the name of the user who sent the notification
     * another one to get the devicetoken to the device we want to send notification to
     */
    
    //Usando once()obtém o valor do banco de dados uma vez, enquanto usa on()continua a ouvir as alterações nos dados até que você ligue off().

    const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');
    const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

    return Promise.all([userQuery, deviceToken]).then(result => {

      const userName = result[0].val();
      const token_id = result[1].val();

      /*
       * We are creating a 'payload' to create a notification to be sent.
       */

      const payload = {
        notification: {
          title : "Nova Solicitação de Amizade",
          body: `${userName} enviou uma solicitação de amizade`,
          icon: "default",
        click_action : "pedro.com.br.indigo_TARGET_NOTIFICATION"
        },
        data : {
          from_user_id : from_user_id
        }
      };

      /*
       * Then using admin.messaging() we are sending the payload notification to the token_id of
       * the device we retreived.
       */

      return admin.messaging().sendToDevice(token_id, payload).then(response => {

        console.log('Isso é o recurso de notificação');

      });

    });

  });

});