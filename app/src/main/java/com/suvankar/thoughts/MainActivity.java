package com.suvankar.thoughts;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

    private List<UserModel> users;
    private ConstraintLayout constraintLayout;
    private RecyclerView recyclerView;
    private ThoughtsAdapter thoughtsAdapter;
    private Gson gson;
    private UserModel activeUser;
    private DateFormat dateFormat;
    private Dialog dialog;
    private SharedPreferences sharedPreferences;
    private List<ThoughtModel> thoughtList;
    private List<String> colorList;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        users = gson.fromJson(usersJson, users_type);

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

        thoughtList = activeUser.getThoughts();
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

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        final View dialogView = View.inflate(this, R.layout.dialog, null);
        dialog = new Dialog(this, R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(dialogView);
        FloatingActionButton fab_2 = dialog.findViewById(R.id.save_thought_fab);
        fab_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Thought saved.", Toast.LENGTH_SHORT).show();
                saveThought();
                revealShow(dialogView, false, dialog);
            }
        });
        ImageView imageView = (ImageView) dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Thought discarded.", Toast.LENGTH_SHORT).show();
                revealShow(dialogView, false, dialog);
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogView, false, dialog);
                    return true;
                }
                return false;
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

//    public static void updateThought() {
//        EditText editText = dialog.findViewById(R.id.thoughtEdit);
//
//        if (!editText.getText().toString().isEmpty()) {
//
//            constraintLayout.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//
//            ThoughtModel thoughtModel = new ThoughtModel(editText.getText().toString(),
//                    dateFormat.format(Calendar.getInstance().getTime()));
//            String json = sharedPreferences.getString("users", "");
//            Type type = new TypeToken<List<UserModel>>() {
//            }.getType();
//            List<UserModel> users = gson.fromJson(json, type);
//
//            activeUser.getThoughts().add(thoughtModel);
//
//            for (UserModel user : users) {
//                if (user.getEmail().equals(activeUser.getEmail())) {
//                    user.setThoughts(activeUser.getThoughts());
//                }
//            }
//
//            thoughtsAdapter.notifyDataSetChanged();
//            Log.e("USERS", users.toString());
//            sharedPreferences.edit().putString("users", gson.toJson(users)).commit();
//        } else
//            Toast.makeText(MainActivity.this, "Thought discarded.", Toast.LENGTH_SHORT).show();
//    }

    public void saveThought() {

        EditText editText = dialog.findViewById(R.id.thoughtEdit);

        if (!editText.getText().toString().isEmpty()) {

            constraintLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            ThoughtModel thoughtModel = new ThoughtModel(editText.getText().toString(),
                    dateFormat.format(Calendar.getInstance().getTime()));
            String json = sharedPreferences.getString("users", "");
            Type type = new TypeToken<List<UserModel>>() {
            }.getType();
            List<UserModel> users = gson.fromJson(json, type);

            activeUser.getThoughts().add(thoughtModel);

            for (UserModel user : users) {
                if (user.getEmail().equals(activeUser.getEmail())) {
                    user.setThoughts(activeUser.getThoughts());
                }
            }

            thoughtsAdapter.notifyDataSetChanged();
            Log.e("USERS", users.toString());
            sharedPreferences.edit().putString("users", gson.toJson(users)).commit();
        } else
            Toast.makeText(MainActivity.this, "Thought discarded.", Toast.LENGTH_SHORT).show();

    }

    private void revealShow(View dialogView, boolean b, final Dialog dialog) {
        final View view = dialogView.findViewById(R.id.dialog);
        int w = view.getWidth();
        int h = view.getHeight();
        int endRadius = (int) Math.hypot(w, h);
        int cx = (int) (fab.getX() + (fab.getWidth() / 2));
        int cy = (int) (fab.getY()) + fab.getHeight() + 56;
        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();
        } else {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                }
            });
            anim.setDuration(700);
            anim.start();
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

//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("Today I am sick", dateFormat.format(Calendar.getInstance().getTime())));
//        thoughtList.add(new ThoughtModel("I think I should die. Or maybe not?", dateFormat.format(Calendar.getInstance().getTime())));
    }
}