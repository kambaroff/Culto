package kz.iitu.culto;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailRegistration;
    private EditText mPasswordRegistration;
    private EditText mPasswordConfirm;

    private Button mSignUpRegistration;
    private Button mBackRegistration;

    private FirebaseAuth mFirebaseAuth;

    private ProgressDialog mLoadingBar;


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
        mPasswordConfirm = findViewById(R.id.confirm_password);

        mSignUpRegistration = findViewById(R.id.sign_up_button_reg);
        mBackRegistration = findViewById(R.id.back_button_reg);

        mLoadingBar = new ProgressDialog(this);



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
                CreateAccount();
                break;
        }

    }

    private void CreateAccount() {
        String email = mEmailRegistration.getText().toString();
        String password = mPasswordRegistration.getText().toString();
        String confirmPassword = mPasswordConfirm.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please write your email..", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Please write your password ..", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this,"Please confirm your password ..", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(confirmPassword))
        {
            Toast.makeText(this,"Password is not confirmed ..", Toast.LENGTH_SHORT).show();
        }
        else{

            mLoadingBar.setTitle("Creating New Account");
            mLoadingBar.setMessage("Please wait, while we are creating your new account");
            mLoadingBar.show();
            mLoadingBar.setCanceledOnTouchOutside(true);


            mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                SendUserToSetupActivity();

                                Toast.makeText(RegistrationActivity.this, "Authenticated succesfully!",
                                        Toast.LENGTH_SHORT).show();
                                mLoadingBar.dismiss();
                            }
                            else
                            {
                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                mLoadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegistrationActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
