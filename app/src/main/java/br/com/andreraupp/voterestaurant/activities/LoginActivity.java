package br.com.andreraupp.voterestaurant.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.andreraupp.voterestaurant.R;

/**
 * Created by andre on 10/06/2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            openRestaurantsList();
        }

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonSignup = (Button) findViewById(R.id.email_sign_in_button);

        progressDialog = new ProgressDialog(this);
        buttonSignup.setOnClickListener(this);
    }

    private void loginUser() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            openRestaurantsList();
                        } else {
                            registerUser();
                        }
                    }
                });
    }

    private void registerUser() {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, getString(R.string.success_registration), Toast.LENGTH_LONG).show();
                            openRestaurantsList();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.error_registration), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    @Override
    public void onClick(View view) {
        email = editTextEmail.getText().toString().trim();
        password  = editTextPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            editTextPassword.setError(getString(R.string.error_invalid_password));
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.error_field_required));
            return;
        } else if (!isEmailValid(email)) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            return;
        }

        progressDialog.setMessage(getString(R.string.registering_plase_wait));
        progressDialog.show();

        loginUser();
    }

    private void openRestaurantsList() {
        Intent intent = new Intent(this, RestaurantsListActivity.class);
        startActivity(intent);
        finish();
    }
}
