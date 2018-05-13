package balasangum.sharm.balasangum.system.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import balasangum.sharm.balasangum.R;
import balasangum.sharm.balasangum.system.activity.system.model.UserRegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    //EditText's for Form
    EditText emailEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;

    //Store values of EditText's into strings
    String email;
    String password;
    String confirmPassword;

    private final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        passwordConfirmEditText = (EditText) findViewById(R.id.editTextConfirmPassword);


    }

    public void registerNewUser(View view) {
        if (emailEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_LONG).show();
            return;
        } else if (passwordEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Please enter a valid password.", Toast.LENGTH_LONG).show();
            return;
        } else if (passwordConfirmEditText.getText().toString().equals("")) {
            Toast.makeText(this, "Please make sure passwords match.", Toast.LENGTH_LONG).show();
            return;
        }

        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        confirmPassword = passwordConfirmEditText.getText().toString();


        UserRegisterViewModel userRegisterViewModel = new UserRegisterViewModel();
        userRegisterViewModel.setEmail(email);
        userRegisterViewModel.setPassword(password);
        userRegisterViewModel.setConfirmPassword(confirmPassword);

        if (checkCredentials(userRegisterViewModel)) {
            registerNewEmail(userRegisterViewModel.getEmail(), userRegisterViewModel.getPassword());
        } else {
            return;
        }


    }

    private void registerNewEmail(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser());
                    sendVerificationEmail();
                    FirebaseAuth.getInstance().signOut();
                    redirectToLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to Register New User", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void redirectToLogin()
    {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
    public boolean checkCredentials(UserRegisterViewModel userRegisterViewModel) {
        //Check for valid Email
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern emailPatern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPatern.matcher(userRegisterViewModel.getEmail());
        if (!emailMatcher.matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (userRegisterViewModel.getPassword().toString().length() < 8) {
            Toast.makeText(this, "Please make sure password has at least 8 characters", Toast.LENGTH_LONG).show();
            return false;
        }

        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(password);
        boolean b = m.find();

        if (!b) {
            Toast.makeText(this, "Please make sure password has one special character.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!userRegisterViewModel.getPassword().equals(userRegisterViewModel.getConfirmPassword())) {
            Toast.makeText(this, "Please make sure passwords are equal.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Sent Verification Email", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to send Verification Email", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}
