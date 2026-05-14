package com.example.localisationtempsreel;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int HC_PERM_CODE = 100;
    private TextView tvLat, tvLon;
    private RequestQueue hcRequestQueue;
    private LocationManager hcLocationManager;

    private final String hcInsertUrl = "http://10.0.2.2/localisation/createPosition.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLat = findViewById(R.id.tvLat);
        tvLon = findViewById(R.id.tvLon);
        MaterialButton btnMap = findViewById(R.id.btnMap);

        hcRequestQueue = Volley.newRequestQueue(getApplicationContext());
        hcLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnMap.setOnClickListener(v ->
                startActivity(new Intent(this, MapsActivity.class)));

        hcCheckPermissions();
    }

    private void hcCheckPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    HC_PERM_CODE);
        } else {
            hcStartGpsUpdates();
        }
    }

    private void hcStartGpsUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        hcLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                60000,
                150,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        double alt = location.getAltitude();
                        float acc = location.getAccuracy();

                        tvLat.setText("📍 Latitude : " + lat);
                        tvLon.setText("📍 Longitude : " + lon);

                        String msg = String.format(
                                getString(R.string.new_location),
                                lat, lon, alt, acc);

                        Toast.makeText(getApplicationContext(),
                                msg, Toast.LENGTH_LONG).show();

                        hcAddPosition(lat, lon);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        String newStatus;
                        switch (status) {
                            case LocationProvider.OUT_OF_SERVICE:
                                newStatus = "Hors service";
                                break;
                            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                                newStatus = "Indisponible";
                                break;
                            default:
                                newStatus = "Disponible";
                        }
                        Toast.makeText(getApplicationContext(),
                                provider + " : " + newStatus,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                        Toast.makeText(getApplicationContext(),
                                "✅ " + getString(R.string.provider_enabled, provider),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        Toast.makeText(getApplicationContext(),
                                "❌ " + getString(R.string.provider_disabled, provider),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void hcAddPosition(final double lat, final double lon) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                hcInsertUrl,
                response -> Toast.makeText(getApplicationContext(),
                        "✅ " + response, Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(getApplicationContext(),
                        "❌ Erreur réseau", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lon));
                params.put("date", sdf.format(new Date()));
                params.put("imei", Settings.Secure.getString(
                        getContentResolver(), Settings.Secure.ANDROID_ID));

                return params;
            }
        };

        hcRequestQueue.add(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == HC_PERM_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hcStartGpsUpdates();
        } else {
            Toast.makeText(this, "❌ Permission refusée", Toast.LENGTH_LONG).show();
        }
    }
}