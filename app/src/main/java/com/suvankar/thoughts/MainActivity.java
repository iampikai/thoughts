package com.suvankar.thoughts;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    public static boolean updateThoughtFlag;
    public static int thoughtIndex = 0;

    private static Context context;
    public static ConstraintLayout constraintLayout;
    public static RecyclerView recyclerView;
    private static ThoughtsAdapter thoughtsAdapter;
    private static Gson gson;
    private static UserModel activeUser;
    private static DateFormat dateFormat;
    private static SharedPreferences sharedPreferences;
    private List<String> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm aaa");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String activeUserJson = sharedPreferences.getString("active_user", "");
        String usersJson = sharedPreferences.getString("users", "");

        gson = new Gson();
        Type user_type = new TypeToken<UserModel>() {
        }.getType();
        Type users_type = new TypeToken<List<UserModel>>() {
        }.getType();
        activeUser = gson.fromJson(activeUserJson, user_type);
        List<UserModel> users = gson.fromJson(usersJson, users_type);

        for (UserModel userModel : users) {
            if (userModel.getEmail().equals(activeUser.getEmail())) {
                activeUser = userModel;
            }
        }

        TextView textView = findViewById(R.id.header);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/indieflower.ttf");
        textView.setTypeface(typeface);
        textView.setText("Thoughts");

        constraintLayout = findViewById(R.id.no_thoughts_image);
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);

        final List<ThoughtModel> thoughtList = activeUser.getThoughts();
        colorList = new ArrayList<>();

        initLists();

        thoughtsAdapter = new ThoughtsAdapter(thoughtList, colorList);
        recyclerView = findViewById(R.id.thought_list);
        if (!thoughtList.isEmpty()) {
            constraintLayout.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        recyclerView.setAdapter(thoughtsAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int index = viewHolder.getAdapterPosition(); //swiped position
                if (direction == ItemTouchHelper.RIGHT) {//swipe right
                    thoughtList.remove(index);

                    String json = sharedPreferences.getString("users", "");
                    Type type = new TypeToken<List<UserModel>>() {
                    }.getType();
                    List<UserModel> users = gson.fromJson(json, type);
                    for (UserModel user : users) {
                        if (user.getEmail().equals(activeUser.getEmail())) {
                            user.setThoughts(activeUser.getThoughts());
                        }
                    }
                    sharedPreferences.edit().putString("users", gson.toJson(users)).commit();

                    thoughtsAdapter.notifyItemRemoved(index);
                    thoughtsAdapter.notifyDataSetChanged();

                    if (thoughtList.isEmpty()) {
                        constraintLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditThoughtActivity.class);
                startActivity(intent);
            }
        });
    }

    public static void saveThought(String thought) {

        if (!thought.isEmpty()) {

            constraintLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            ThoughtModel thoughtModel = new ThoughtModel(thought,
                    dateFormat.format(Calendar.getInstance().getTime()));

            String json = sharedPreferences.getString("users", "");
            Type type = new TypeToken<List<UserModel>>() {
            }.getType();
            List<UserModel> users = gson.fromJson(json, type);

            if (updateThoughtFlag)
                activeUser.getThoughts().set(thoughtIndex, thoughtModel);
            else
                activeUser.getThoughts().add(thoughtModel);

            for (UserModel user : users) {
                if (user.getEmail().equals(activeUser.getEmail())) {
                    user.setThoughts(activeUser.getThoughts());
                }
            }

            thoughtsAdapter.notifyDataSetChanged();
            sharedPreferences.edit().putString("users", gson.toJson(users)).commit();
            if (MainActivity.updateThoughtFlag)
                Toast.makeText(context, "Thought updated.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Thought saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Thought discarded.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int option = item.getItemId();
        switch (option) {
            case R.id.action_signout:
                Intent login = new Intent(this, LoginActivity.class);
                sharedPreferences.edit().putBoolean("isLoggedIn", false).commit();
                startActivity(login);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initLists() {
        colorList.add("#b3e5fc");
        colorList.add("#ccff90");
        colorList.add("#f4ff81");
        colorList.add("#ffd180");
        colorList.add("#ffccbc");
    }

    @Override
    protected void onResume() {
        updateThoughtFlag = false;
        super.onResume();
    }

}