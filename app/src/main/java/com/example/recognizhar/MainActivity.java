package com.example.recognizhar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button startButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private AlertDialog mDialog = null;

    Animation topAnim, bottomAnim;
    ImageView imageView;
    TextView tv_benv, tv_nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        imageView = findViewById(R.id.id_immagine);
        tv_benv = findViewById(R.id.id_benv);
        tv_nome = findViewById(R.id.id_nomeapp);
        startButton = findViewById(R.id.btn_start);
        drawerLayout = findViewById(R.id.id_drawer_main);
        drawerLayout.setStatusBarBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Status_bar, null));
        navigationView = findViewById(R.id.id_nav_view);
        toolbar = findViewById(R.id.id_toolbar_main);

        imageView.setAnimation(topAnim);
        tv_benv.setAnimation(topAnim);
        tv_nome.setAnimation(bottomAnim);
        startButton.setAnimation(bottomAnim);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRecognitionActivity();
            }
        });


        setSupportActionBar(toolbar);
        navigationView.bringToFront(); //porta in primo piano
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close); //implementa l'apertura e la chiusura della navigationView
        drawerLayout.addDrawerListener(toggle); //collega il toggle al drawer layout dell'activity_main
        toggle.syncState(); // sincronizzato il comportamento del drawer
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_data:
                openSensorsActivity();
                break;
            case R.id.nav_chart:
                openSensorsGraficiActivity();
                break;
            case R.id.nav_infoapp:
                showAboutDialog();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openRecognitionActivity() {
        Intent intent = new Intent(this, RecognitionActivity.class);
        startActivity(intent);

        Animatoo.animateFade(MainActivity.this);
    }

    public void openSensorsActivity() {
        Intent intent = new Intent(this, SensorsActivity.class);
        startActivity(intent);

        Animatoo.animateSlideLeft(MainActivity.this);
    }

    public void openSensorsGraficiActivity() {
        Intent intent = new Intent(this, SensorsGraficiActivity.class);
        startActivity(intent);

        Animatoo.animateSlideLeft(MainActivity.this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) { //se il menu è aperto e viene cliccato il bottone back, il menu viene chiuso
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            this.finishAffinity();
        }
    }

    public void showAboutDialog() {
        @SuppressLint("InflateParams")
        View messageView = getLayoutInflater().inflate(R.layout.about_app, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(messageView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                    navigationView.setCheckedItem(R.id.nav_home);
                }
            }
        });
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false); //se clicco al di fuori del dialog non succede nulla
        mDialog.setCancelable(false); // se clicco il bottone back non succede nulla
        mDialog.show();
    }
}