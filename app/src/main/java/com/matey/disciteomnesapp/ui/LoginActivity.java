package com.matey.disciteomnesapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matey.disciteomnesapp.R;
import com.matey.disciteomnesapp.models.User;

/**
 * LoginActivity handles the user login flow using Firebase Authentication.
 * It also retrieves and stores the corresponding Firebase `userId` locally.
 *
 * ⚠️ This class includes logic assisted by AI-generated suggestions (e.g., inline error checks, comment structure).
 * ✅ SharedPreferences logic and Firebase queries were added manually and tested.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton loginButton;
    private FirebaseAuth mAuth;

    /**
     * Initializes login screen and checks if user is already logged in.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // 🔐 Skip login screen if already authenticated
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // 👤 Login button handler
        loginButton.setOnClickListener(v -> attemptLogin());

        // 🔁 Switch to registration activity
        findViewById(R.id.registerTextView).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Validates input and performs Firebase login. On success, stores userId in SharedPreferences.
     */
    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Basic input validation
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password required");
            return;
        }

        loginButton.setEnabled(false);

        // 🔐 Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loginButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        // 🔍 Look up userId based on email
                        FirebaseDatabase.getInstance().getReference("users")
                                .orderByChild("email").equalTo(email)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                            User user = snap.getValue(User.class);
                                            if (user != null) {
                                                // 💾 Save userId for use in other activities
                                                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                                                prefs.edit().putString("userId", user.id).apply();

                                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                                finish();
                                                return;
                                            }
                                        }

                                        Toast.makeText(LoginActivity.this, "User not found in database", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
