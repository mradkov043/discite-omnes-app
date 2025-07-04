package com.matey.disciteomnesapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.matey.disciteomnesapp.R;
import com.matey.disciteomnesapp.network.DisciteOmnesApi;
import com.matey.disciteomnesapp.network.MessageResponse;
import com.matey.disciteomnesapp.network.RetrofitClient;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DashboardActivity serves as the main screen after login.
 * Displays a welcome message using Firebase and integrates
 * an API call via Retrofit for demonstration.
 *
 * ✅ Firebase integration and UI logic written manually.
 * ⚠️ Inline documentation and some structural suggestions assisted by AI.
 */
public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private MaterialButton groupBtn, tasksBtn, logoutBtn;

    /**
     * Initializes the dashboard, loads the username, triggers a mock API call,
     * and sets up navigation to Group and Task sections.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // UI element binding
        welcomeText = findViewById(R.id.welcomeText);
        groupBtn = findViewById(R.id.groupBtn);
        tasksBtn = findViewById(R.id.tasksBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        // ✅ Load username from Firebase Realtime Database
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        if (name != null && !name.isEmpty()) {
                            welcomeText.setText("Welcome, " + name);
                        } else {
                            welcomeText.setText("Welcome!");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        welcomeText.setText("Welcome!");
                    }
                });

        // ⚙️ Retrofit API test call to show welcome message from remote source
        DisciteOmnesApi api = RetrofitClient.getClient().create(DisciteOmnesApi.class);
        api.getWelcomeMessage().enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DashboardActivity.this, "API: " + response.body().message, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DashboardActivity.this, "API response error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "API failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // 🔗 Navigation to Group screen
        groupBtn.setOnClickListener(v ->
                startActivity(new Intent(this, GroupListActivity.class)));

        // 🔗 Navigation to Task screen
        tasksBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskListActivity.class);
            intent.putExtra("groupId", "default"); // Optional param if needed
            startActivity(intent);
        });

        // 🔒 Logout logic
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
