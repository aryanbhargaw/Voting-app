package com.example.votingapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.votingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    public static final String PREFERENCES = "prefKey";
    SharedPreferences sharedPreferences;
    public static final String IsLogIn = "isLogin";

    private CircleImageView circleImg;
    private TextView nameTxt, nationalIdTxt;
    private String uid;
    private FirebaseFirestore firebaseFirestore;
    private Button createBtn, voteBtn;
    //public static  int show_result = 1000023;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseFirestore = FirebaseFirestore.getInstance();
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        circleImg = findViewById(R.id.circle_image);
        nameTxt = findViewById(R.id.name);
        nationalIdTxt = findViewById(R.id.national_id);
        createBtn = findViewById(R.id.admin_btn);
        voteBtn = findViewById(R.id.give_vote);


        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor pref = sharedPreferences.edit();
        pref.putBoolean(IsLogIn, true);
        pref.commit();
        //     findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
        //           @Override
        //          public void onClick(View view) {
        //            FirebaseAuth.getInstance().signOut();
        //          pref.putBoolean(IsLogIn,false);
        //               pref.commit();
        //              startActivity(new Intent(HomeActivity.this,LoginActivity.class) );
        //            finish();
        //       }
        //   });
        firebaseFirestore.collection("Users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            String name = task.getResult().getString("name");
                            String nationalId = task.getResult().getString("nationalId");
                            String image = task.getResult().getString("image");
                            assert name != null;
                            if (name.equals("admin")) {
                                createBtn.setVisibility(View.VISIBLE);
                            } else {
                                createBtn.setVisibility(View.GONE);
                            }

                            nameTxt.setText(name);
                            nationalIdTxt.setText(nationalId);
                            Glide.with(HomeActivity.this).load(image).into(circleImg);


                        } else {
                            Toast.makeText(HomeActivity.this, "User not Found", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this, Create_Candidate_Activity.class));

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        SharedPreferences.Editor pref = sharedPreferences.edit();
        if (id == R.id.show_result){
            return true;
        }else if(id ==  R.id.log_out){
            FirebaseAuth.getInstance().signOut();
            pref.putBoolean(IsLogIn, false);
            pref.commit();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return true;

        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}
