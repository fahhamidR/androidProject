package com.example.fahim;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fahim.controllers.AuthController;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login, signup;
    private RadioGroup radioGroup;
    private RadioButton radioUser, radioAdmin;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize AuthController
        authController = new AuthController(this);

        // Initialize views
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.signupBtn);
        radioGroup = findViewById(R.id.radioGroup);
        radioUser = findViewById(R.id.radioUser);
        radioAdmin = findViewById(R.id.radioAdmin);

        // Set default radio button
        radioUser.setChecked(true);

        login.setOnClickListener(v -> handleLogin());
        signup.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignupActivity.class)));
    }

    private void handleLogin() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        boolean isAdminLogin = radioAdmin.isChecked();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (authController.loginUser(userEmail, userPassword, isAdminLogin)) {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
