package com.suvankar.thoughts;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

import static com.suvankar.thoughts.MainActivity.saveThought;

public class EditThoughtActivity extends AppCompatActivity {

    public static final String CURRENT_THOUGHT = "currentThought";
    EditText editText;
    Activity activity;
    static FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_thought);

        final EditText editText = findViewById(R.id.thoughtEdit);
        FloatingActionButton fab_2 = findViewById(R.id.save_thought_fab);
        ImageView imageView = findViewById(R.id.closeDialogImg);
        String currentThought = getIntent().getStringExtra(CURRENT_THOUGHT);

        if (MainActivity.updateThoughtFlag) {
            editText.setText(currentThought);
        }

        fab_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String thought = editText.getText().toString();
                MainActivity.saveThought(thought);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.saveThought("");
                finish();
            }
        });
    }


}
