package pedro.com.br.indigo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private ImageView mProfileImage;
    private Button mProfileSendReqBtn, mDeclineBtn;

    private DatabaseReference mUserDataBase;
    private DatabaseReference mFriendRequestDataBase;
    private DatabaseReference mFriendDataBase;
    private DatabaseReference mNotificationDataBase;
    private DatabaseReference mRootRef;

    private FirebaseUser mCurrent_user;
    private ProgressDialog mProgressDialog;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendDataBase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDataBase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mFriendRequestDataBase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mCurrent_state = "not_friends";

        //Pegando dados da view
        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_friends_count);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);

        mProfileSendReqBtn = (Button) findViewById(R.id.profile_btn_send_req);
        mDeclineBtn = (Button) findViewById(R.id.profile_btn_decline_req);

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);

        //CAPTURANDO USUARIO ATUAL
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        //POP UP DE CARREGAMENTO DOS DADOS
        mProgressDialog = new ProgressDialog(ProfileActivity.this);
        mProgressDialog.setTitle("Carregando Dados");
        mProgressDialog.setMessage("Por favor aguarde o processamento do perfil");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        //PREENCHENDO COM DADOS DO BANCO
        mUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(name);
                mProfileStatus.setText(status);

                //SETANDO IMAGEM NA VIEW COM O PICASSO
                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);



                //-------Lista de amigos / Solicitacoes

                mFriendRequestDataBase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //VERIFICANDO SE O USUARIO LOGADO TEM ALGUMA SOLICITAÇAO PENDENTE SENDO SOLICITACAO OU CANCELAMENTO DE SOLICITACAO
                       if (dataSnapshot.hasChild(user_id)){

                           //DEPENDENDO DO TIPO DE SOLICITACAO MUDAMOS O  TEXTO E FUNCAO DO BOTAO PARA "ACEITAR" OU "CANCELAR" A SOLICITACAO
                           String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                           if (req_type.equals("received")){


                               mCurrent_state="req_received";
                               mProfileSendReqBtn.setText("Aceitar Solicitação de Amizade");

                               mDeclineBtn.setVisibility(View.VISIBLE);
                               mDeclineBtn.setEnabled(true);

                           }else if(req_type.equals("sent")){

                               mCurrent_state = "req_sent";
                               mProfileSendReqBtn.setText("Cancelar Solicitação de Amizade");

                               //DESABILITANDO BOTAO DE RECUSAR PARA O USUARIO QUE ESTA ENVIANDO

                               mDeclineBtn.setVisibility(View.INVISIBLE);
                               mDeclineBtn.setEnabled(false);

                           }

                           //FECHANDO O POPUP AO FINAL DO CARREGAMENTO
                           mProgressDialog.dismiss();

                       } else {

                           mFriendDataBase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {

                                   if(dataSnapshot.hasChild(user_id)){

                                       mCurrent_state="friends";
                                       mProfileSendReqBtn.setText("Desfazer Amizade");

                                       mDeclineBtn.setVisibility(View.INVISIBLE);
                                       mDeclineBtn.setEnabled(false);

                                   }
                                   //FECHANDO O POPUP AO FINAL DO CARREGAMENTO
                                   mProgressDialog.dismiss();
                               }

                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                                   //FECHANDO O POPUP AO FINAL DO CARREGAMENTO
                                   mProgressDialog.dismiss();

                               }
                           });

                       }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                //---------- ESTADO DE USUARIO SEM AMIGOS -----------------
                //METODO MELHORADO COM MELHOR PERFORMACE

                if(mCurrent_state.equals("not_friends")){

                    DatabaseReference newNotificationref = mRootRef.child("Notifications").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();

                    //NOTIFICACOES DE SOLICITACAO DE AMIZADE
                    HashMap<String,String> notificationsData = new HashMap<>();
                    notificationsData.put("from", mCurrent_user.getUid());
                    notificationsData.put("type","request");

                    //HASHMAP ALIMENTANDO AS SOLICITACOES DE AMIZADE E DEVIDAS NOTIFICACOES
                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Request/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_Request/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
                    requestMap.put("Notifications/" + user_id + "/" + newNotificationId, notificationsData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Toast.makeText(ProfileActivity.this,"Há algum erro no envio da solicitação", Toast.LENGTH_SHORT).show();
                            }

                            mProfileSendReqBtn.setEnabled(true);

                            mCurrent_state= "req_sent";
                            mProfileSendReqBtn.setText("Cancelar Solicitação de Amizade");
                        }
                    });

                }

                //------ CANCELAR SOLICITACAO DE AMIZADE

                if (mCurrent_state.equals("req_sent")){

                    mFriendRequestDataBase.child(mCurrent_user.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            //PEGANDO A REFERENCIA E ID DO USUARIO SELECIONADO , O ID DO USUARIO LOGADO E OS REMOVENDO DO BANCO
                            mFriendRequestDataBase.child(user_id).child(mCurrent_user.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Enviar Solicitação de Amizade");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);

                                }
                            });
                        }
                    });
                }

                //----------ESTADO DE SOLICITACAO RECEBIDA--------------

                if(mCurrent_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
                    friendsMap.put("Friends/" + user_id + "/"  + mCurrent_user.getUid() + "/date", currentDate);


                    friendsMap.put("Friend_Request/" + mCurrent_user.getUid() + "/" + user_id, null);
                    friendsMap.put("Friend_Request/" + user_id + "/" + mCurrent_user.getUid(), null);


                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state = "friends";
                                mProfileSendReqBtn.setText("Desfazer Amizade ");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });

                }

                //-----------------DESFAZER AMIZADE--------------------

                if(mCurrent_state.equals("friends")){

                    Map desfazerAmizadeMap = new HashMap();

                    desfazerAmizadeMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
                    desfazerAmizadeMap.put("Friends/"+ user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(desfazerAmizadeMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if(databaseError == null){

                                mCurrent_state = "not_friends";
                                mProfileSendReqBtn.setText("Enviar Solicitação de Amizade");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                            }

                            mProfileSendReqBtn.setEnabled(true);

                        }
                    });

                }



            }

        });

    }
}
