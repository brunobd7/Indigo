package pedro.com.br.indigo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    //ANDROID LAYOUT

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    private Button mStatusBtn;
    private Button mImageBtn;

    private static final int GALLERY_PICK = 1;

    //STORAGE FIREBASE
    private StorageReference mImageStorage;

    //PROGRESS

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.settings_name);
        mStatus = (TextView) findViewById(R.id.settings_status);

        mStatusBtn = (Button)  findViewById(R.id.settings_status_btn);
        mImageBtn = (Button) findViewById((R.id.settings_image_btn));

        mImageStorage = FirebaseStorage.getInstance().getReference();



        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true); //MANTEM A REFERENCIA LOCAL SINCRONIZADA COMA  REMOTA

        mUserDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

                if(!image.equals("default")) {
                    //PlaceHolder mantem um mascara ate imagem ser carregada do firebase
                    //Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status_value = mStatus.getText().toString();

                Intent status_intent = new Intent(SettingsActivity.this, StatusActivity.class);
                status_intent.putExtra("status_value", status_value);
                startActivity(status_intent);
            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT_IMAGE"), GALLERY_PICK);

                /*// start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG);
            CropImage.activity(imageUri)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Upload da imagem");
                mProgressDialog.setMessage("Aguarde o processamento da imagem");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();



                Uri resultUri = result.getUri();

                //
                final File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                //COMPRIMINDO IMAGEM PARA USAR MINIATURA
                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100,baos);
                final byte[] thumb_byte = baos.toByteArray();


                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id +".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            final String downlaod_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()) {

                                        Map updateHashMap = new HashMap();
                                        updateHashMap.put("image", downlaod_url);
                                        updateHashMap.put("thumb_image", thumb_downloadUrl);

                                        mUserDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Upload finalizado", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });

                                    }else{
                                        Toast.makeText(SettingsActivity.this, "Erro no Upload thumbmail", Toast.LENGTH_LONG).show();

                                    }


                                }
                            });


                        }else{

                            Toast.makeText(SettingsActivity.this, "Erro no Upload", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }

/*    //NOMES ALEATORIOS PARA AS IMAGENS NO FIREBASE
    public static String random(){
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLenght = generator.nextInt(10);
        char tempchar;
        for (int i=0;i<randomLenght;i++){
            tempchar = (char)(generator.nextInt(96)+32);
            randomStringBuilder.append(tempchar);
        }

        return randomStringBuilder.toString();
    }*/
}
