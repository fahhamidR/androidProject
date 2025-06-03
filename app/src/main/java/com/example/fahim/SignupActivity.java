package com.example.fahim;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fahim.controllers.AuthController;

public class SignupActivity extends AppCompatActivity {
    private EditText email, password;
    private Button signup;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize AuthController
        authController = new AuthController(this);

        // Initialize views
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signupBtn);

        signup.setOnClickListener(v -> handleSignup());
    }

    private void handleSignup() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (authController.registerUser(userEmail, userPassword)) {
            Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();
            finish(); // Go back to Login
        } else {
            Toast.makeText(this, "Signup Failed - Email may already exist", Toast.LENGTH_SHORT).show();
        }
    }
}