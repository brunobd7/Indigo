package pedro.com.br.indigo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mStatus;
    private Button mSaveBtn;

    //FIREBASE
    private DatabaseReference mStatusDataBase;
    private FirebaseUser mCurrentUser;

    //PROGRESS
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mStatusDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar = findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Configuracoes da Conta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status_value");

        mProgress = new ProgressDialog(this);

        mStatus = (EditText) findViewById(R.id.status_edit_text);
        mSaveBtn = (Button) findViewById(R.id.status_btn_save);

        mStatus.setText(status_value);


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = mStatus.getText().toString();

                //PROGRESS
                //mProgress = new ProgressDialog(getApplicationContext());
                mProgress.setTitle("Salvando Alteracoes");
                mProgress.setMessage("Aguarde enquanto salvamos as alteracoes");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();


                mStatusDataBase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            mProgress.dismiss();
                            Intent mainIntent = new Intent(StatusActivity.this, SettingsActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Erro ao salvar as alteracoes", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
