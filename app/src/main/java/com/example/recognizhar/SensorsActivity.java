package com.example.recognizhar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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


public class SensorsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private AlertDialog mDialog = null;

    Receiver receiver = null;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        drawerLayout = findViewById(R.id.id_drawer_sensors);
        drawerLayout.setStatusBarBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Status_bar, null));
        navigationView = findViewById(R.id.nav_view_sensors);
        toolbar = findViewById(R.id.id_toolbar_sensors);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_data);

        i = new Intent(SensorsActivity.this, SensorsService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        receiver = new SensorsActivity.Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SensorsService.MY_ACTION);
        startService(i);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(receiver);
    }

    public void openMainActivity() {
        Intent intent = new Intent(SensorsActivity.this, MainActivity.class);
        startActivity(intent);

        Animatoo.animateSlideLeft(SensorsActivity.this);
    }

    public void openSensorsGraficiActivity() {
        Intent intent = new Intent(this, SensorsGraficiActivity.class);
        startActivity(intent);

        Animatoo.animateSlideLeft(SensorsActivity.this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) { //se il menu è aperto e viene cliccato il tasto indietro, il menu viene chiuso
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            openMainActivity();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                openMainActivity();
                break;
            case R.id.nav_data:
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

    public void showAboutDialog() {
        @SuppressLint("InflateParams") View messageView = getLayoutInflater().inflate(R.layout.about_app, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(messageView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                    navigationView.setCheckedItem(R.id.nav_data);
                }
            }
        });
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public class Receiver extends BroadcastReceiver {

        private TextView mTextSensorAccX = findViewById(R.id.label_acc_x);
        private TextView mTextSensorAccY = findViewById(R.id.label_acc_y);
        private TextView mTextSensorAccZ = findViewById(R.id.label_acc_z);

        private TextView mTextSensorGyrX = findViewById(R.id.label_gyr_x);
        private TextView mTextSensorGyrY = findViewById(R.id.label_gyr_y);
        private TextView mTextSensorGyrZ = findViewById(R.id.label_gyr_z);

        private TextView mTextSensorLinAccX = findViewById(R.id.label_linacc_x);
        private TextView mTextSensorLinAccY = findViewById(R.id.label_linacc_y);
        private TextView mTextSensorLinAccZ = findViewById(R.id.label_linacc_z);

        @Override
        public void onReceive(Context context, Intent intent) {
            float misurazioni_accx = intent.getFloatExtra("misurazioni_accx", 0.0f);
            mTextSensorAccX.setText(getString(R.string.string_acc_x, misurazioni_accx));
            float misurazioni_accy = intent.getFloatExtra("misurazioni_accy", 0.0f);
            mTextSensorAccY.setText(getString(R.string.string_acc_y, misurazioni_accy));
            float misurazioni_accz = intent.getFloatExtra("misurazioni_accz", 0.0f);
            mTextSensorAccZ.setText(getString(R.string.string_acc_z, misurazioni_accz));

            float misurazioni_gyrx = intent.getFloatExtra("misurazioni_gyrx", 0.0f);
            mTextSensorGyrX.setText(getString(R.string.string_gyr_x, misurazioni_gyrx));
            float misurazioni_gyry = intent.getFloatExtra("misurazioni_gyry", 0.0f);
            mTextSensorGyrY.setText(getString(R.string.string_gyr_y, misurazioni_gyry));
            float misurazioni_gyrz = intent.getFloatExtra("misurazioni_gyrz", 0.0f);
            mTextSensorGyrZ.setText(getString(R.string.string_gyr_z, misurazioni_gyrz));

            float misurazioni_linaccx = intent.getFloatExtra("misurazioni_linaccx", 0.0f);
            mTextSensorLinAccX.setText(getString(R.string.string_linacc_x, misurazioni_linaccx));
            float misurazioni_linaccy = intent.getFloatExtra("misurazioni_linaccy", 0.0f);
            mTextSensorLinAccY.setText(getString(R.string.string_linacc_y, misurazioni_linaccy));
            float misurazioni_linaccz = intent.getFloatExtra("misurazioni_linaccz", 0.0f);
            mTextSensorLinAccZ.setText(getString(R.string.string_linacc_z, misurazioni_linaccz));

        }
    }
}
