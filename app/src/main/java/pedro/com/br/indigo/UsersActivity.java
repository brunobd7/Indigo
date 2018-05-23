package pedro.com.br.indigo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private DatabaseReference mDataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Usuarios");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.users_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //READ FROM DATABASE
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Users");


    }

    @Override
    protected void onStart() {
        super.onStart();
        startListening();

    }

    private void startListening() {

        Query query = FirebaseDatabase.getInstance().getReference().child("Users").limitToLast(50);

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options){

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int position, @NonNull Users users) {
                usersViewHolder.setUserName(users.getName());
                usersViewHolder.setUserStatus(users.getStatus());
                //usersViewHolder.setUserImage(users.getImage())
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUserName(String userName) {
            TextView name = (TextView) mView.findViewById(R.id.user_single_name);
            name.setText(userName);
        }

        public void setUserStatus(String userStatus) {
            TextView status = (TextView) mView.findViewById(R.id.user_single_status);
            status.setText(userStatus);
        }

        public void setUserImage(String userImage, Context context) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.get().load(userImage).into(image);
        }
    }
}

