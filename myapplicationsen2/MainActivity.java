package com.example.myapplicationsen2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textView;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private Sensor accelerometerSensor;
    private Sensor temperatureSensor;

    private float[] gyroscopeValues = new float[3];
    private float[] accelerometerValues = new float[3];
    private float temperatureValue = Float.NaN; // Initialisation à NaN pour indiquer l'absence de valeur initiale

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        // Initialisation des capteurs
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (gyroscopeSensor == null) {
            textView.append("Le capteur de gyroscope n'est pas disponible sur ce périphérique.\n");
        }

        if (accelerometerSensor == null) {
            textView.append("Le capteur d'accéléromètre n'est pas disponible sur ce périphérique.\n");
        }

        if (temperatureSensor == null) {
            textView.append("Le capteur de température n'est pas disponible sur ce périphérique.\n");
        }

        // Vérification des permissions pour les capteurs corporels
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Autorisation accordée pour utiliser les capteurs corporels.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Autorisation refusée pour utiliser les capteurs corporels.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enregistrer le listener pour les capteurs
        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Désenregistrer le listener pour économiser la batterie
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeValues = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            temperatureValue = event.values[0];
        }

        displaySensorValues();
    }

    private void displaySensorValues() {
        StringBuilder sensorData = new StringBuilder();
        sensorData.append(String.format("Gyroscope\nx: %.2f\ny: %.2f\nz: %.2f\n\n",
                gyroscopeValues[0], gyroscopeValues[1], gyroscopeValues[2]));
        sensorData.append(String.format("Accéléromètre\nx: %.2f\ny: %.2f\nz: %.2f\n\n",
                accelerometerValues[0], accelerometerValues[1], accelerometerValues[2]));

        if (!Float.isNaN(temperatureValue)) {
            sensorData.append(String.format("Température ambiante: %.2f °C\n", temperatureValue));
        } else {
            sensorData.append("Température ambiante: Capteur non disponible\n");
        }

        textView.setText(sensorData.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Vous pouvez gérer les changements de précision ici si nécessaire
    }
}
