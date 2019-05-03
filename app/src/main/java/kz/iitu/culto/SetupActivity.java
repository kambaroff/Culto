package kz.iitu.culto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText mUserName, mAgeName, mCountryName, mFullName;
    private Button mSaveInformationButton;
    private CircleImageView mProfileImage;

    private ProgressDialog mLoadingBar;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUsersRef;
    private StorageReference mUserProfileImageRef;

    String currentUserID;
    final static  int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = mFirebaseAuth.getCurrentUser().getUid();
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        mUserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mUserName = findViewById(R.id.username_setup);
        mAgeName = findViewById(R.id.age_setup);
        mCountryName = findViewById(R.id.country_setup);
        mFullName = findViewById(R.id.fullname_setup);
        mSaveInformationButton = findViewById(R.id.save_button);
        mProfileImage = findViewById(R.id.profile_image_setup);

        mLoadingBar = new ProgressDialog(this);

        mSaveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick );
            }
        });

        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profileimage").getValue().toString();

                    Picasso.with(SetupActivity.this).load(image).placeholder(R.drawable.profile).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }


        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mLoadingBar.setTitle("Profile Image");
                mLoadingBar.setMessage("Please wait, while we updating your profile image...");
                mLoadingBar.show();
                mLoadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                StorageReference filePath = mUserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {

                            Toast.makeText(SetupActivity.this, "Profile Image stored successfully in Firebase Storage", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    mUsersRef.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                        startActivity(selfIntent);

                                                        Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase Database successfully..", Toast.LENGTH_SHORT).show();
                                                        mLoadingBar.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                        mLoadingBar.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
            else {
                Toast.makeText(SetupActivity.this, "Error: Image can't be cropped try again.", Toast.LENGTH_SHORT).show();
                mLoadingBar.dismiss();
            }
        }
    }

    private void SaveAccountSetupInformation() {

        String username = mUserName.getText().toString();
        String fullname = mFullName.getText().toString();
        String countryname = mCountryName.getText().toString();
        String agename = mAgeName.getText().toString();
        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Please write your username", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this, "Please write your full name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(agename))
        {
            Toast.makeText(this, "Please write your date of birth", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(countryname))
        {
            Toast.makeText(this, "Please write your Country's name", Toast.LENGTH_SHORT).show();
        }
        else
        {

            mLoadingBar.setTitle("Saving information");
            mLoadingBar.setMessage("Please wait, while we are saving your new Account");
            mLoadingBar.show();
            mLoadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("countryname", countryname);
            userMap.put("status", "Hey there, something about me");
            userMap.put("gender", "none");
            userMap.put("dob", agename);

            mUsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        SendUserToSideMenuActivity();
                        Toast.makeText(SetupActivity.this, "Your account is created successfully", Toast.LENGTH_SHORT).show();
                        mLoadingBar.dismiss();
                    }
                    else
                    {
                        Toast.makeText(SetupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mLoadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToSideMenuActivity() {
        Intent sideMenuIntent = new Intent(SetupActivity.this, SideMenuActivity.class);
        sideMenuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(sideMenuIntent);
        finish();
    }
}
