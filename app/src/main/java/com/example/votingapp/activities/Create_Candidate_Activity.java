 package com.example.votingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.votingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

 public class Create_Candidate_Activity extends AppCompatActivity {
    private CircleImageView candidateImg;
    private EditText candidateName,candidateParty;
    private Spinner candidateSpinner;
    private Button submitBtn;
    private Uri mainUri = null;
    private String[] candPost = {"President","Vice-President"};
     StorageReference reference;
     FirebaseFirestore firebaseFirestore;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_candidate);

         reference = FirebaseStorage.getInstance().getReference();
         firebaseFirestore = FirebaseFirestore.getInstance();

        candidateImg = findViewById(R.id.candidate_image);
        candidateName = findViewById(R.id.candidate_name);
        candidateParty = findViewById(R.id.candidate_party_name);
        candidateSpinner = findViewById(R.id.candidate_spinner);
        submitBtn = findViewById(R.id.candidate_submit_btn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,candPost);
         candidateSpinner.setAdapter(adapter);
        candidateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            cropImage();

            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             String name = candidateName.getText().toString().trim();
             String party = candidateParty.getText().toString().trim();
             String post = candidateSpinner.getSelectedItem().toString();

             if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(party) && !TextUtils.isEmpty(post) && mainUri!=null){

                 String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                 StorageReference imagePath = reference.child("candidate_img").child(uid + ".jpg");
                 imagePath.putFile(mainUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         if (task.isSuccessful()) {
                             imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                     Map<String, Object> map = new HashMap<>();
                                     map.put("name", name);
                                     map.put("party", party);
                                     map.put("post", post);
                                     map.put("image", uri.toString());
                                     map.put("timestamp", FieldValue.serverTimestamp());


                                     firebaseFirestore.collection("Candidate")
                                             .add(map)
                                             .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<DocumentReference> task) {
                                                  if(task.isSuccessful()){
                                                      startActivity(new Intent(Create_Candidate_Activity.this, HomeActivity.class));
                                                     finish();
                                                  }else{
                                                      Toast.makeText(Create_Candidate_Activity.this, "Data not Store", Toast.LENGTH_SHORT).show();
                                                  }
                                                 }
                                             });

                                 }
                             });
                         } else {
                             Toast.makeText(Create_Candidate_Activity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                         }
                     }
                 });




             }else{
                 Toast.makeText(Create_Candidate_Activity.this, "Enter details", Toast.LENGTH_SHORT).show();
             }



            }
        });

    }


     private void cropImage() {
         CropImage.activity()
                 .setGuidelines(CropImageView.Guidelines.ON)
                 .setAspectRatio(1,1)
                 .start(this);

     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
             CropImage.ActivityResult result = CropImage.getActivityResult(data);
             if (resultCode == RESULT_OK) {
                 mainUri = result.getUri();
                 candidateImg.setImageURI(mainUri);

             } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                 Exception error = result.getError();
             }
         }
     }


}