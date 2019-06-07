package com.suvankar.thoughts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private RelativeLayout rlayout;
    private Animation animation;
    Boolean newUserFlag = true;
    Intent next_activity;
    List<UserModel> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView tv = findViewById(R.id.tvSignUp);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/indieflower.ttf");
        tv.setTypeface(typeface);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userData = new ArrayList<>();

        next_activity = new Intent(this, MainActivity.class);
        final EditText name_field = findViewById(R.id.Name);
        final EditText email_field = findViewById(R.id.Email);
        final EditText password_field = findViewById(R.id.Password);
        final EditText confirm_password_field = findViewById(R.id.confirm_password);

        Button registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_field.getText().toString().trim();
                String email = email_field.getText().toString().trim();
                String password_1 = password_field.getText().toString().trim();
                String password_2 = confirm_password_field.getText().toString().trim();
                if (name.isEmpty() || email.isEmpty() || password_1.isEmpty() || password_2.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "All fields are mandatory.", Toast.LENGTH_LONG).show();
                } else {
                    if (password_1.equals(password_2)) {
                        Gson gson = new Gson();
                        String json = sharedPreferences.getString("users", "[]");
                        Type type = new TypeToken<List<UserModel>>() {
                        }.getType();
                        userData = gson.fromJson(json, type);

                        for (UserModel user : userData) {
                            if (user.getEmail().equals(email)) {
                                newUserFlag = false;
                                Toast.makeText(RegisterActivity.this, "User already exists. Please Log in.", Toast.LENGTH_LONG).show();
                                break;
                            }
                        }

                        if (newUserFlag) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            userData.add(new UserModel(name, email, password_1, new ArrayList<ThoughtModel>()));
                            String data = gson.toJson(userData);
                            editor.putString("users", data);
                            editor.putString("active_user", gson.toJson(userData.get(userData.size() - 1)));
                            editor.putBoolean("isLoggedIn", true);
                            editor.commit();
                            next_activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(next_activity);
                        }
                    } else
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
