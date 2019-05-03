package kz.iitu.culto;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailLogin;
    private EditText mPasswordLogin;

    private Button mLoginButton;
    private Button mSignUpButton;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;

    private ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mUser = mFirebaseAuth.getCurrentUser();

        initUI();

    }

    private void initUI(){

        mEmailLogin = findViewById(R.id.email_login);
        mPasswordLogin = findViewById(R.id.password_login);

        mSignUpButton = findViewById(R.id.sign_up_button);
        mLoginButton = findViewById(R.id.login_button);

        mLoadingBar = new ProgressDialog(this);

        mLoginButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_button:
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                break;
            case R.id.login_button:
                mLoadingBar.setTitle("Logging in...");
                mLoadingBar.setMessage("Please wait until you logged in");
                mLoadingBar.show();
                mLoadingBar.setCanceledOnTouchOutside(true);
                validateAndSignIn(mEmailLogin.getText().toString(),
                        mPasswordLogin.getText().toString());

                break;

        }
    }

    private void validateAndSignIn(String email,String password){
        if(isValid(email,password)){
        mFirebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mLoadingBar.dismiss();
                    startActivity(new Intent(LoginActivity.this, SideMenuActivity.class));
                    Toast.makeText(LoginActivity.this, R.string.success_login_message,
                            Toast.LENGTH_SHORT).show();

                    return;
                }
              mLoadingBar.dismiss();
              Toast.makeText(LoginActivity.this,task.getException().getMessage(),
                       Toast.LENGTH_SHORT).show();
            }
        });

        }
    }

    private boolean isValid(String email, String password){
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, R.string.empty_valid_error, Toast.LENGTH_SHORT).show();

            return  false;
        }
        return true;
    }

}
