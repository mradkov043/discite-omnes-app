package com.matey.disciteomnesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.matey.disciteomnesapp.R;
import com.matey.disciteomnesapp.models.User;

/**
 * RegisterActivity is responsible for registering a new user using Firebase Authentication.
 * After registration, the user's data (name + email) is stored in the Firebase Realtime Database.
 *
 * ✅ All Firebase logic and user creation are manually implemented and verified.
 * ⚠️ Some UI handling suggestions and Toast messages were guided by AI assistance.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, passwordEditText;
    private MaterialButton registerButton;
    private FirebaseAuth mAuth;

    /**
     * Initializes UI components and Firebase auth instance.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> attemptRegister());
    }

    /**
     * Handles form input validation, Firebase account creation, and saving user info to the DB.
     */
    private void attemptRegister() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Basic input validation (AI-suggested block structure)
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password required");
            return;
        }

        registerButton.setEnabled(false);

        // 🔐 Firebase account creation
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    registerButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // 🎯 Create user entry in Firebase Realtime Database
                        String userId = mAuth.getCurrentUser().getUid();
                        User newUser = new User(userId, name, email);

                        DatabaseReference userRef = FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(userId);

                        userRef.setValue(newUser)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Account created & saved", Toast.LENGTH_SHORT).show();

                                    // 🔒 Log out newly registered user to force login
                                    mAuth.signOut();

                                    // 🔁 Return to login screen
                                    Intent intent = new Intent(this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to save user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
