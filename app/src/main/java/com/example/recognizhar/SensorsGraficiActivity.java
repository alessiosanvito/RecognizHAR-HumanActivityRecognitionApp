package com.example.recognizhar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.navigation.NavigationView;

public class SensorsGraficiActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public LineChart accGrafico;
    public LineChart gyrGrafico;
    public LineChart linaccGrafico;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private AlertDialog mDialog = null;

    ReceiverChart receiver = null;
    Intent i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors_grafici);

        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        accGrafico = findViewById(R.id.id_plot_acc);
        gyrGrafico = findViewById(R.id.id_plot_gyr);
        linaccGrafico = findViewById(R.id.id_plot_lin_acc);

        onInitGrafico(SensorsGraficiActivity.this, accGrafico, 20);
        onInitGrafico(SensorsGraficiActivity.this, gyrGrafico, 20);
        onInitGrafico(SensorsGraficiActivity.this, linaccGrafico, 20);

        drawerLayout = findViewById(R.id.id_drawer_grafici);
        drawerLayout.setStatusBarBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.Status_bar, null));
        navigationView = findViewById(R.id.nav_view_grafici);
        toolbar = findViewById(R.id.id_toolbar_grafici);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_chart);

        i = new Intent(SensorsGraficiActivity.this, SensorsService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        receiver = new SensorsGraficiActivity.ReceiverChart();
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
                break;
            case R.id.nav_infoapp:
                showAboutDialog();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openMainActivity() {
        Intent intent = new Intent(SensorsGraficiActivity.this, MainActivity.class);
        startActivity(intent);

        Animatoo.animateSlideLeft(SensorsGraficiActivity.this);
    }

    public void openSensorsActivity() {
        Intent intent = new Intent(this, SensorsActivity.class);
        startActivity(intent);

        Animatoo.animateSlideLeft(SensorsGraficiActivity.this);
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
                    navigationView.setCheckedItem(R.id.nav_chart);
                }
            }
        });
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void onInitGrafico(Context context, LineChart lineChart, int num) {
        //interazione con il grafico
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);

        //impostazioni generali e impostazioni sullo stile
        lineChart.setNoDataText("Nessun grafico disponibile");
        lineChart.setDrawGridBackground(true);
        lineChart.setGridBackgroundColor(Color.WHITE);
        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.BLACK);
        lineChart.setBorderWidth(2);

        //impostazioni asse x
        XAxis asseX = lineChart.getXAxis();
        asseX.setEnabled(true);
        asseX.setPosition(XAxis.XAxisPosition.BOTTOM);
        asseX.setDrawAxisLine(true);
        asseX.setDrawGridLines(true);
        asseX.setDrawLabels(false);

        // impostazioni asse y
        YAxis asseY = lineChart.getAxisLeft();
        lineChart.getAxisRight().setEnabled(false);
        asseY.setTextSize(10f);
        asseY.setTextColor(Color.BLACK);
        asseY.setDrawAxisLine(true);
        asseY.setDrawGridLines(true);
        asseY.setAxisMaximum(num);
        asseY.setAxisMinimum(-num);

        // linedata
        LineData data = new LineData();
        //data.setValueTextColor(Color.WHITE);
        lineChart.setData(data);

        // legenda
        Legend legenda = lineChart.getLegend();
        legenda.setEnabled(true);
        legenda.setTextColor(Color.BLACK);
        legenda.setTextSize(12);
        legenda.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legenda.setForm(Legend.LegendForm.LINE);
        legenda.setFormSize(12);
    }


    public class ReceiverChart extends BroadcastReceiver {

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

            tipo_sens = intent.getIntExtra("tipo_sens", 100);

            if (tipo_sens == Sensor.TYPE_ACCELEROMETER) {
                misurazioni_accx = intent.getFloatExtra("misurazioni_accx", 0.0f);
                misurazioni_accy = intent.getFloatExtra("misurazioni_accy", 0.0f);
                misurazioni_accz = intent.getFloatExtra("misurazioni_accz", 0.0f);
                addItem(SensorsGraficiActivity.this, accGrafico);
            } else if (tipo_sens == Sensor.TYPE_GYROSCOPE) {
                misurazioni_gyrx = intent.getFloatExtra("misurazioni_gyrx", 0.0f);
                misurazioni_gyry = intent.getFloatExtra("misurazioni_gyry", 0.0f);
                misurazioni_gyrz = intent.getFloatExtra("misurazioni_gyrz", 0.0f);
                addItem(SensorsGraficiActivity.this, gyrGrafico);
            } else if (tipo_sens == Sensor.TYPE_LINEAR_ACCELERATION) {
                misurazioni_linaccx = intent.getFloatExtra("misurazioni_linaccx", 0.0f);
                misurazioni_linaccy = intent.getFloatExtra("misurazioni_linaccy", 0.0f);
                misurazioni_linaccz = intent.getFloatExtra("misurazioni_linaccz", 0.0f);
                addItem(SensorsGraficiActivity.this, linaccGrafico);
            }
        }

        public LineDataSet createDataSet(int color, String asse) {
            LineDataSet lineDataSet = new LineDataSet(null, asse);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setHighlightEnabled(false);
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setDrawValues(false);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setLineWidth(2);
            lineDataSet.setColor(color);

            return lineDataSet;
        }

        public void addItem(Context context, LineChart lineChart) {
            LineData data = lineChart.getData();
            if (data != null) {
                ILineDataSet setX = data.getDataSetByIndex(0);
                ILineDataSet setY = data.getDataSetByIndex(1);
                ILineDataSet setZ = data.getDataSetByIndex(2);
                if (setX == null) {
                    setX = createDataSet(ContextCompat.getColor(context, R.color.Rosso), "Asse X");
                    data.addDataSet(setX);
                    setY = createDataSet(ContextCompat.getColor(context, R.color.Verde), "Asse Y");
                    data.addDataSet(setY);
                    setZ = createDataSet(ContextCompat.getColor(context, R.color.Blu_elet), "Asse Z");
                    data.addDataSet(setZ);
                }

                if (tipo_sens == Sensor.TYPE_ACCELEROMETER) {
                    Entry entryAccX = new Entry(setX.getEntryCount(), misurazioni_accx);
                    data.addEntry(entryAccX, 0);
                    Entry entryAccY = new Entry(setY.getEntryCount(), misurazioni_accy);
                    data.addEntry(entryAccY, 1);
                    Entry entryAccZ = new Entry(setZ.getEntryCount(), misurazioni_accz);
                    data.addEntry(entryAccZ, 2);
                } else if (tipo_sens == Sensor.TYPE_GYROSCOPE) {
                    Entry entryGyrX = new Entry(setX.getEntryCount(), misurazioni_gyrx);
                    data.addEntry(entryGyrX, 0);
                    Entry entryGyrY = new Entry(setY.getEntryCount(), misurazioni_gyry);
                    data.addEntry(entryGyrY, 1);
                    Entry entryGyrZ = new Entry(setZ.getEntryCount(), misurazioni_gyrz);
                    data.addEntry(entryGyrZ, 2);
                } else if (tipo_sens == Sensor.TYPE_LINEAR_ACCELERATION) {
                    Entry entryLinAccX = new Entry(setX.getEntryCount(), misurazioni_linaccx);
                    data.addEntry(entryLinAccX, 0);
                    Entry entryLinAccY = new Entry(setY.getEntryCount(), misurazioni_linaccy);
                    data.addEntry(entryLinAccY, 1);
                    Entry entryLinAccZ = new Entry(setZ.getEntryCount(), misurazioni_linaccz);
                    data.addEntry(entryLinAccZ, 2);
                }

            }

            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();

            lineChart.setVisibleXRangeMaximum(50);
            lineChart.moveViewToX(data.getEntryCount());
        }
    }
}