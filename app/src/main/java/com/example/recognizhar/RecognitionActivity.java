package com.example.recognizhar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
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

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RecognitionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView bikeTv;
    private TextView downstTv;
    private TextView joggingTv;
    private TextView sitTv;
    private TextView standTv;
    private TextView upstTv;
    private TextView walkTv;

    private TableRow bikeRiga;
    private TableRow downstRiga;
    private TableRow joggingRiga;
    private TableRow sitRiga;
    private TableRow standRiga;
    private TableRow upstRiga;
    private TableRow walkRiga;

    private static final int N_SAMPLES = 100;
    private static int previousIndex = -1;

    private static List<Float> accx;
    private static List<Float> accy;
    private static List<Float> accz;

    private static List<Float> linaccx;
    private static List<Float> linaccy;
    private static List<Float> linaccz;

    private static List<Float> gyrx;
    private static List<Float> gyry;
    private static List<Float> gyrz;

    private static List<Float> acc_tot;
    private static List<Float> linacc_tot;
    private static List<Float> gyr_tot;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private AlertDialog mDialog = null;

    private TensorFlowClassifier classifier;

    private float[] results;

    ReceiverRec receiver = null;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        accx = new ArrayList<>();
        accy = new ArrayList<>();
        accz = new ArrayList<>();
        linaccx = new ArrayList<>();
        linaccy = new ArrayList<>();
        linaccz = new ArrayList<>();
        gyrx = new ArrayList<>();
        gyry = new ArrayList<>();
        gyrz = new ArrayList<>();
        acc_tot = new ArrayList<>();
        linacc_tot = new ArrayList<>();
        gyr_tot = new ArrayList<>();

        bikeTv = (TextView) findViewById(R.id.prob_bike);
        downstTv = (TextView) findViewById(R.id.prob_downst);
        joggingTv = (TextView) findViewById(R.id.prob_jogging);
        sitTv = (TextView) findViewById(R.id.prob_sit);
        standTv = (TextView) findViewById(R.id.prob_stand);
        upstTv = (TextView) findViewById(R.id.prob_upst);
        walkTv = (TextView) findViewById(R.id.prob_walk);

        bikeRiga = (TableRow) findViewById(R.id.riga_bike);
        downstRiga = (TableRow) findViewById(R.id.riga_downst);
        joggingRiga = (TableRow) findViewById(R.id.riga_jogging);
        sitRiga = (TableRow) findViewById(R.id.riga_sit);
        standRiga = (TableRow) findViewById(R.id.riga_stand);
        upstRiga = (TableRow) findViewById(R.id.riga_upst);
        walkRiga = (TableRow) findViewById(R.id.riga_walk);

        drawerLayout = findViewById(R.id.id_drawer_ric);
        drawerLayout.setStatusBarBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Status_bar, null));
        navigationView = findViewById(R.id.nav_view_ric);
        toolbar = findViewById(R.id.id_toolbar_ric);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        classifier = new TensorFlowClassifier(getApplicationContext());

        i = new Intent(RecognitionActivity.this, SensorsService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        receiver = new RecognitionActivity.ReceiverRec();
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

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openSensorsActivity() {
        Intent intent = new Intent(this, SensorsActivity.class);
        startActivity(intent);

        Animatoo.animateSlideLeft(RecognitionActivity.this);
    }

    public void openSensorsGraficiActivity() {
        Intent intent = new Intent(this, SensorsGraficiActivity.class);
        startActivity(intent);

        Animatoo.animateSlideLeft(RecognitionActivity.this);
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
                    navigationView.setCheckedItem(R.id.nav_home);
                }
            }
        });
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private float[] toFloatArray(List<Float> list) {
        float[] array = ArrayUtils.toPrimitive(list.toArray(new Float[0]), 0.0F);
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public class ReceiverRec extends BroadcastReceiver {

        float misurazioni_accx;
        float misurazioni_accy;
        float misurazioni_accz;

        float misurazioni_gyrx;
        float misurazioni_gyry;
        float misurazioni_gyrz;

        float misurazioni_linaccx;
        float misurazioni_linaccy;
        float misurazioni_linaccz;

        int tipo_sens;

        @Override
        public void onReceive(Context context, Intent intent) {
            activityPrediction();

            misurazioni_accx = intent.getFloatExtra("misurazioni_accx", 0.0f);
            misurazioni_accy = intent.getFloatExtra("misurazioni_accy", 0.0f);
            misurazioni_accz = intent.getFloatExtra("misurazioni_accz", 0.0f);

            misurazioni_gyrx = intent.getFloatExtra("misurazioni_gyrx", 0.0f);
            misurazioni_gyry = intent.getFloatExtra("misurazioni_gyry", 0.0f);
            misurazioni_gyrz = intent.getFloatExtra("misurazioni_gyrz", 0.0f);

            misurazioni_linaccx = intent.getFloatExtra("misurazioni_linaccx", 0.0f);
            misurazioni_linaccy = intent.getFloatExtra("misurazioni_linaccy", 0.0f);
            misurazioni_linaccz = intent.getFloatExtra("misurazioni_linaccz", 0.0f);

            tipo_sens = intent.getIntExtra("tipo_sens", 100);

            if (tipo_sens == Sensor.TYPE_ACCELEROMETER) {
                accx.add(misurazioni_accx);
                accy.add(misurazioni_accy);
                accz.add(misurazioni_accz);
            }

            if (tipo_sens == Sensor.TYPE_GYROSCOPE) {
                gyrx.add(misurazioni_gyrx);
                gyry.add(misurazioni_gyry);
                gyrz.add(misurazioni_gyrz);
            }

            if (tipo_sens == Sensor.TYPE_LINEAR_ACCELERATION) {
                linaccx.add(misurazioni_linaccx);
                linaccy.add(misurazioni_linaccy);
                linaccz.add(misurazioni_linaccz);
            }
        }

        public void activityPrediction() {

            List<Float> data = new ArrayList<>();

            if (accx.size() >= N_SAMPLES && accy.size() >= N_SAMPLES && accz.size() >= N_SAMPLES
                    && gyrx.size() >= N_SAMPLES && gyry.size() >= N_SAMPLES && gyrz.size() >= N_SAMPLES
                    && linaccx.size() >= N_SAMPLES && linaccy.size() >= N_SAMPLES && linaccz.size() >= N_SAMPLES) {
                double acctotValue;
                double gyrtotValue;
                double linacctotValue;

                for (int i = 0; i < N_SAMPLES; i++) {
                    acctotValue = Math.sqrt(Math.pow(accx.get(i), 2) + Math.pow(accy.get(i), 2) + Math.pow(accz.get(i), 2));
                    gyrtotValue = Math.sqrt(Math.pow(gyrx.get(i), 2) + Math.pow(gyry.get(i), 2) + Math.pow(gyrz.get(i), 2));
                    linacctotValue = Math.sqrt(Math.pow(linaccx.get(i), 2) + Math.pow(linaccy.get(i), 2) + Math.pow(linaccz.get(i), 2));

                    acc_tot.add((float) acctotValue);
                    gyr_tot.add((float) gyrtotValue);
                    linacc_tot.add((float) linacctotValue);
                }

                data.addAll(accx.subList(0, N_SAMPLES));
                data.addAll(accy.subList(0, N_SAMPLES));
                data.addAll(accz.subList(0, N_SAMPLES));

                data.addAll(gyrx.subList(0, N_SAMPLES));
                data.addAll(gyry.subList(0, N_SAMPLES));
                data.addAll(gyrz.subList(0, N_SAMPLES));

                data.addAll(linaccx.subList(0, N_SAMPLES));
                data.addAll(linaccy.subList(0, N_SAMPLES));
                data.addAll(linaccz.subList(0, N_SAMPLES));

                data.addAll(acc_tot.subList(0, N_SAMPLES));
                data.addAll(gyr_tot.subList(0, N_SAMPLES));
                data.addAll(linacc_tot.subList(0, N_SAMPLES));

                results = classifier.predictProbabilities(toFloatArray(data));

                float max = -1;
                int idx = -1;
                for (int i = 0; i < results.length; i++) {
                    if (results[i] > max) {
                        idx = i;
                        max = results[i];
                    }
                }

                bikeTv.setText(Float.toString(round(results[0] * 100, 1)) + " %");
                downstTv.setText(Float.toString(round(results[1] * 100, 1)) + " %");
                joggingTv.setText(Float.toString(round(results[2] * 100, 1)) + " %");
                sitTv.setText(Float.toString(round(results[3] * 100, 1)) + " %");
                standTv.setText(Float.toString(round(results[4] * 100, 1)) + " %");
                upstTv.setText(Float.toString(round(results[5] * 100, 1)) + " %");
                walkTv.setText(Float.toString(round(results[6] * 100, 1)) + " %");

                bikeRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Trasparente, null));
                downstRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Trasparente, null));
                joggingRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Trasparente, null));
                sitRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Trasparente, null));
                standRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Trasparente, null));
                upstRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Trasparente, null));
                walkRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Trasparente, null));

                if (idx == 0) {
                    bikeRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Blu_riga_tabella, null));
                } else if (idx == 1) {
                    downstRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Blu_riga_tabella, null));
                } else if (idx == 2) {
                    joggingRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Blu_riga_tabella, null));
                } else if (idx == 3) {
                    sitRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Blu_riga_tabella, null));
                } else if (idx == 4) {
                    standRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Blu_riga_tabella, null));
                } else if (idx == 5) {
                    upstRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Blu_riga_tabella, null));
                } else if (idx == 6) {
                    walkRiga.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Blu_riga_tabella, null));
                }

                accx.clear();
                accy.clear();
                accz.clear();

                gyrx.clear();
                gyry.clear();
                gyrz.clear();

                linaccx.clear();
                linaccy.clear();
                linaccz.clear();

                acc_tot.clear();
                gyr_tot.clear();
                linacc_tot.clear();
            }
        }
    }
}
