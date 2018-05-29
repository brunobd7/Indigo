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

public class ProfileActivity extends AppCompatActivity {

    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private ImageView mProfileImage;
    private Button mProfileSendReqBtn;

    private DatabaseReference mUserDataBase;
    private DatabaseReference mFriendRequestDataBase;
    private FirebaseUser mCurrent_user;
    private ProgressDialog mProgressDialog;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        //Pegando dados da view
        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_friends_count);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_btn_send_req);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);

        mCurrent_state = "not_friends";
        mFriendRequestDataBase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");

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

                //FECHANDO O POPUP AO FINAL DO CARREGAMENTO
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mCurrent_state.equals("not_friends")){

                    //CRIA A REFERENCIA DA SOLICITACAO DE AMIZADE NO BANCO E CAPTURA O 'ID' DO USUARIO QUE A FEZ A SOLICITACAO
                    // EM SEGUIDA SETA O TIPO DE REQUISIÇÃO (ENVIADO) E O ID DO USUARIO QUE RECEBEU A SOLICITACAO.
                    mFriendRequestDataBase.child(mCurrent_user.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                //CAPTURA O ID DO USUARIO QUE FOI ALVO DA SOLICITACAO DE AMIZADE E SETA ISSO NO BANCO
                                //EM SEGUIDA SETA O TIPO DE REQUISICAO (RECEBIDO) E O USUARIO(ID) DE ORIGEM QUE FEZ A SOLICITACAO DE AMIZADE
                                mFriendRequestDataBase.child(user_id).child(mCurrent_user.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(ProfileActivity.this, "Solicitação Enviada", Toast.LENGTH_SHORT).show();


                                    }
                                });

                            }
                            else{

                                Toast.makeText(ProfileActivity.this, "Falha no envio da solicitação", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }
                else{

                }

            }
        });

    }
}
