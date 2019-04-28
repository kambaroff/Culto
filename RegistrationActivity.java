package kz.iitu.culto;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailRegistration;
    private EditText mPasswordRegistration;

    private TextView mEmailView;
    private TextView mNameView;
    private TextView mSurnameView;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private  EditText mNameRegistration;
    private EditText mSurnameRegistration;

    private Button mSignUpRegistration;
    private Button mBackRegistration;

    private FirebaseAuth mFirebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFirebaseAuth = FirebaseAuth.getInstance();

        initUI();
    }

    private void initUI(){

        mEmailRegistration = findViewById(R.id.email_registration);
        mPasswordRegistration = findViewById(R.id.password_registration);
        mNameRegistration = findViewById(R.id.name_registration);
        mSurnameRegistration = findViewById(R.id.surname_registration);

        mNameView = findViewById(R.id.name_view);
        mSurnameView = findViewById(R.id.surname_view);
        mEmailView = findViewById(R.id.email_view);

        mSignUpRegistration = findViewById(R.id.sign_up_button_reg);
        mBackRegistration = findViewById(R.id.back_button_reg);

        setContentView(R.layout.nav_header);



        mSignUpRegistration.setOnClickListener(this);
        mBackRegistration.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button_reg:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.sign_up_button_reg:
                validateAndSignUp(mEmailRegistration.getText().toString(),
                        mPasswordRegistration.getText().toString());
                break;
        }


    }

    private void validateAndSignUp(final String email, String password) {
        if(isValid(email,password)){
            mFirebaseAuth.createUserWithEmailAndPassword(email,password).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                finish();
                                startActivity(new Intent(RegistrationActivity.this, SideMenuActivity.class));

                                sendUserData();


                                Toast.makeText(RegistrationActivity.this, R.string.succes_reg_message,
                                        Toast.LENGTH_SHORT).show();

                                return;
                            }

                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean isValid(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.empty_valid_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void sendUserData(){

        String user_id = mFirebaseAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(user_id);

        String name = mNameRegistration.getText().toString();
        String surname = mSurnameRegistration.getText().toString();
        String email = mEmailRegistration.getText().toString();

        Map newPost = new HashMap();
        newPost.put("email",email);
        newPost.put("name",name);
        newPost.put("surname",surname);

        current_user_db.setValue(newPost);

    }

}
