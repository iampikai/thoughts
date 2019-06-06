package com.suvankar.thoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Gson gson;
    private SharedPreferences sharedPreferences;
    private List<UserModel> usersData;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        gson = new Gson();
        String json = sharedPreferences.getString("users", "[]");
        Log.e("LOGIN", json);
        Type type = new TypeToken<List<UserModel>>() {
        }.getType();
        usersData = gson.fromJson(json, type);

        final EditText email_field = findViewById(R.id.login_email);
        final EditText password_field = findViewById(R.id.login_password);
        Button login = findViewById(R.id.login_button);
        Button register = findViewById(R.id.register_button);

        if (isLoggedIn) {
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(main);
            finish();
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = email_field.getText().toString();
                password = password_field.getText().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (usersData.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "User doesn't exist. Please Register.", Toast.LENGTH_SHORT).show();
                } else {
                    for (UserModel user : usersData) {
                        Log.e("CALLED", "CALLED");
                        if (user.getEmail().equals(email)) {
                            if (user.getPassword().equals(password)) {
                                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                                editor.putBoolean("isLoggedIn", true);
                                editor.putString("active_user", gson.toJson(user));
                                editor.commit();
                                startActivity(main);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Email address or password is incorrect.", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }
                    }
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register);
                finish();
            }
        });
    }
}
