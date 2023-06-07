package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    FrameLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.btnLogIn);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        root = findViewById(R.id.root_element);

        btnRegister.setOnClickListener(v -> showRegisterWindow());
        btnSignIn.setOnClickListener(v -> showLogInWindow());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            auth.signInWithEmailAndPassword(savedEmail, savedPassword)
                    .addOnSuccessListener(authResult -> {
                        Intent myIntent = new Intent(MainActivity.this, SwapActivity.class);
                        startActivity(myIntent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Snackbar.make(root, "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    });
        } else {
            showLogInWindow();
        }

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null && user.isEmailVerified()) {
                Intent intent = new Intent(MainActivity.this, SwapActivity.class);
                startActivity(intent);
                finish();
            }
        });
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null && user.isEmailVerified()) {
                String username = user.getEmail(); // Получение имени пользователя (в данном случае используется email)
                Intent intent = new Intent(MainActivity.this, AccActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showLogInWindow() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            Intent myIntent = new Intent(MainActivity.this, SwapActivity.class);
            startActivity(myIntent);
            finish();
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Log In");
        dialog.setMessage("Enter your login credentials");

        LayoutInflater inflater = LayoutInflater.from(this);
        View loginWindow = inflater.inflate(R.layout.sing_in_window, null);
        dialog.setView(loginWindow);

        final EditText email = loginWindow.findViewById(R.id.emailField);
        final EditText password = loginWindow.findViewById(R.id.passField);
        CheckBox savePasswordCheckbox = loginWindow.findViewById(R.id.save_password_checkbox);

        dialog.setNegativeButton("Back", (dialogInterface, which) -> dialogInterface.dismiss());
        dialog.setPositiveButton("Log In", (dialogInterface, which) -> {
            if (TextUtils.isEmpty(email.getText().toString())) {
                Snackbar.make(root, "Enter your email", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (password.getText().toString().length() < 8) {
                Snackbar.make(root, "Enter password more than 8 characters", Snackbar.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            if (savePasswordCheckbox.isChecked()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", email.getText().toString());
                                editor.putString("password", password.getText().toString());
                                editor.apply();
                            } else {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("password");
                                editor.apply();
                            }
                            Intent myIntent = new Intent(MainActivity.this, SwapActivity.class);
                            startActivity(myIntent);
                            finish();
                        } else {
                            Snackbar.make(root, "Email not verified. Please check your email for verification.", Snackbar.LENGTH_SHORT).show();
                            // Добавьте код для повторной отправки письма для подтверждения по электронной почте
                            // Например:
                            user.sendEmailVerification()
                                    .addOnSuccessListener(aVoid -> {
                                        Snackbar.make(root, "Verification email has been sent", Snackbar.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Snackbar.make(root, "Failed to send verification email: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Snackbar.make(root, "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }


    private void showRegisterWindow() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Register");
        dialog.setMessage("Enter your registration details");

        LayoutInflater inflater = LayoutInflater.from(this);
        View registerWindow = inflater.inflate(R.layout.windows1, null);
        dialog.setView(registerWindow);

        final EditText email = registerWindow.findViewById(R.id.emailField);
        final EditText password = registerWindow.findViewById(R.id.passField);
        final EditText name = registerWindow.findViewById(R.id.nameField);
        final EditText phone = registerWindow.findViewById(R.id.phoneField);
        final EditText repeatPassword = registerWindow.findViewById(R.id.repeatPasswordField);

        dialog.setNegativeButton("Back", (dialogInterface, which) -> dialogInterface.dismiss());
        dialog.setPositiveButton("Register", (dialogInterface, which) -> {
            if (TextUtils.isEmpty(email.getText().toString())) {
                Snackbar.make(root, "Enter your email", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (password.getText().toString().length() < 8) {
                Snackbar.make(root, "Enter password more than 8 characters", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (!password.getText().toString().equals(repeatPassword.getText().toString())) {
                Snackbar.make(root, "Passwords do not match", Snackbar.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        firebaseUser.sendEmailVerification()
                                .addOnSuccessListener(aVoid -> {
                                    Snackbar.make(root, "Verification email has been sent", Snackbar.LENGTH_SHORT).show();
                                    // Добавьте код для перехода к окну подтверждения по электронной почте
                                    // после отправки электронного письма для подтверждения
                                    // Например:
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Snackbar.make(root, "Failed to send verification email: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                });

                        User user = new User();
                        user.setEmail(email.getText().toString());
                        user.setPassword(password.getText().toString());
                        user.setName(name.getText().toString());
                        user.setPhone(phone.getText().toString());

                        users.child(firebaseUser.getUid()).setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    Snackbar.make(root, "User registered successfully", Snackbar.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Snackbar.make(root, "Failed to register user: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Snackbar.make(root, "Failed to create user: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }
}
