package com.example.tp_kasbi_mohammed;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private TextView textView;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private Sensor accelerometerSensor;
    private Sensor temperatureSensor;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private Sensor magneticFieldSensor;

    private float[] gyroscopeValues = new float[3];
    private float[] accelerometerValues = new float[3];
    private float temperatureValue = Float.NaN;
    private float lightValue = Float.NaN;
    private float proximityValue = Float.NaN;
    private float[] magneticFieldValues = new float[3];

    private LocationManager locationManager;
    private double latitude = Double.NaN;
    private double longitude = Double.NaN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.lux_value);

        // Initialisation des capteurs
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (gyroscopeSensor == null) {
            textView.append("Le capteur de gyroscope n'est pas disponible sur ce périphérique.\n");
        }

        if (accelerometerSensor == null) {
            textView.append("Le capteur d'accéléromètre n'est pas disponible sur ce périphérique.\n");
        }

        if (temperatureSensor == null) {
            textView.append("Le capteur de température n'est pas disponible sur ce périphérique.\n");
        }

        if (lightSensor == null) {
            textView.append("Le capteur de luminosité n'est pas disponible sur ce périphérique.\n");
        }

        if (proximitySensor == null) {
            textView.append("Le capteur de proximité n'est pas disponible sur ce périphérique.\n");
        }

        if (magneticFieldSensor == null) {
            textView.append("Le capteur de champ magnétique n'est pas disponible sur ce périphérique.\n");
        }

        // Initialisation du GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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
        } else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                }
            } else {
                Toast.makeText(this, "Autorisation refusée pour accéder à la localisation.", Toast.LENGTH_SHORT).show();
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
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (magneticFieldSensor != null) {
            sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Désenregistrer le listener pour économiser la batterie
        sensorManager.unregisterListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeValues = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            temperatureValue = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightValue = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximityValue = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values.clone();
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

        if (!Float.isNaN(lightValue)) {
            sensorData.append(String.format("Luminosité: %.2f lx\n", lightValue));
        } else {
            sensorData.append("Luminosité: Capteur non disponible\n");
        }

        if (!Float.isNaN(proximityValue)) {
            sensorData.append(String.format("Proximité: %.2f cm\n", proximityValue));
        } else {
            sensorData.append("Proximité: Capteur non disponible\n");
        }

        sensorData.append(String.format("Champ magnétique\nx: %.2f\ny: %.2f\nz: %.2f\n\n",
                magneticFieldValues[0], magneticFieldValues[1], magneticFieldValues[2]));

        if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
            sensorData.append(String.format("Latitude: %.6f\nLongitude: %.6f\n", latitude, longitude));
        } else {
            sensorData.append("Localisation: Non disponible\n");
        }

        textView.setText(sensorData.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Vous pouvez gérer les changements de précision ici si nécessaire
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        displaySensorValues();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Non utilisé, mais nécessaire pour l'interface LocationListener
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Non utilisé, mais nécessaire pour l'interface LocationListener
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Non utilisé, mais nécessaire pour l'interface LocationListener
    }
}
