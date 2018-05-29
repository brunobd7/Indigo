package pedro.com.br.indigo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String user_id = getIntent().getStringExtra("user_id");

        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        //Pegando dados da view
        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_friends_count);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_btn_send_req);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);

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


    }
}
