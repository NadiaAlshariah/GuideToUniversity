package com.example.andriodassignment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.Manifest;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double userLongitude;
    private double userLatitude;
    protected List<JSONObject> universitiesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);

        try{
            CheckBox gbaCheckBox = findViewById(R.id.gba_check_box);

            gbaCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                LinearLayout gbaLinearLayout = findViewById(R.id.gba_layout);
                if(isChecked){
                    gbaLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    gbaLinearLayout.setVisibility(View.GONE);
                }
            });

            CheckBox majorCheckBox = findViewById(R.id.major_check_box);

            majorCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                LinearLayout majorLinearLayout = findViewById(R.id.major_layout);
                if(isChecked){
                    majorLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    majorLinearLayout.setVisibility(View.GONE);
                }
            });

            Set<String> allMajors = new HashSet<>();

            JSONArray universitiesArray = getJsonDataFile().getJSONArray("universities");
            universitiesList = new ArrayList<>();

            for (int i = 0; i < universitiesArray.length(); i++) {
                universitiesList.add(universitiesArray.getJSONObject(i));
            }

            for(JSONObject university : universitiesList){
                JSONArray majorsArray = university.getJSONArray("majors");
                for (int i = 0; i < majorsArray.length(); i++) {
                    allMajors.add(majorsArray.getString(i));
                }
            }

            List<String> allMajorsList = new ArrayList<>();
            allMajorsList.addAll(allMajors);
            Collections.sort(allMajorsList);

            Spinner majorSpinner = findViewById(R.id.major_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allMajorsList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            majorSpinner.setAdapter(adapter);
        }
         catch (Exception e) {
             System.out.println(e.getMessage());
             Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void submit(View view) throws Exception {
        JSONArray universitiesArray = getJsonDataFile().getJSONArray("universities");
        universitiesList = new ArrayList<>();

        for (int i = 0; i < universitiesArray.length(); i++) {
            universitiesList.add(universitiesArray.getJSONObject(i));
        }

        CheckBox gbaCheckBox = findViewById(R.id.gba_check_box);

        if(gbaCheckBox.isChecked()){
            EditText gbaEditText = findViewById(R.id.gba_edit_text);
            double userGBA = Double.parseDouble(gbaEditText.getText().toString());
            universitiesList = getMinimumGBALessThanUser(universitiesList, userGBA);
        }

        CheckBox majorCheckBox = findViewById(R.id.major_check_box);
        if(majorCheckBox.isChecked()){
            Spinner majorSpinner = findViewById(R.id.major_spinner);
            String userMajor = majorSpinner.getSelectedItem().toString();
            universitiesList = getUniversityByMajor(universitiesList, userMajor);
        }

        CheckBox distanceCheckBox = findViewById(R.id.distance_check_box);
        if(distanceCheckBox.isChecked()){
            try {
                quickSort(universitiesList, 0, universitiesList.size()-1);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        String[] universityNames = new String[universitiesList.size()];
        int counter = 0;
        for(JSONObject university : universitiesList){
            universityNames[counter] = (university.getString("name"));
            counter++;
        }

        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("universityNames", universityNames);
        startActivity(intent);

    }

    private List<JSONObject> getUniversityByMajor(List<JSONObject> universitiesList, String userMajor) throws JSONException {
        List<JSONObject> newUniversitiesList = new ArrayList<>();
        for(JSONObject university : universitiesList){
            JSONArray majorsList = university.getJSONArray("majors");
            for(int i = 0; i < majorsList.length(); i++){
                if(majorsList.getString(i).equals(userMajor)){
                    newUniversitiesList.add(university);
                    break;
                }
            }
        }
        return newUniversitiesList;
    }

    private List<JSONObject> getMinimumGBALessThanUser(List<JSONObject> universitiesList, double userGBA) throws JSONException {
        List<JSONObject> newUniversitiesList = new ArrayList<>();
        for(JSONObject university : universitiesList){
            if(userGBA >= university.getDouble("lowest_gba")){
                newUniversitiesList.add(university);
            }
        }
        return newUniversitiesList;
    }

    private JSONObject getJsonDataFile() throws Exception {
        InputStream inputStream = getResources().openRawResource(R.raw.universities);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }

        reader.close();

        JSONObject jsonObject = new JSONObject(content.toString());
        return jsonObject;
    }

    public void quickSort(List<JSONObject> array, int low, int high) throws JSONException {
        if (low < high) {
            int pivotIndex = partition(array, low, high);
            quickSort(array, low, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, high);
        }
    }

    private int partition(List<JSONObject> array, int low, int high) throws JSONException {
        JSONObject pivot = array.get(high);
        int i = low - 1;
        double pivotLatitude = pivot.getDouble("latitude");
        double pivotLongitude = pivot.getDouble("longitude");

        double userLatitude = getUserLatitude();
        double userLongitude = getUserLongitude();

        for (int j = low; j < high; j++) {
            double universityLatitude = array.get(j).getDouble("latitude");
            double universityLongitude = array.get(j).getDouble("longitude");

            double distanceToUser = getDistanceBetweenTwoPoints(universityLatitude, universityLongitude, userLatitude, userLongitude);
            double pivotDistanceToUser = getDistanceBetweenTwoPoints(pivotLatitude, pivotLongitude, userLatitude, userLongitude);

            if (distanceToUser <= pivotDistanceToUser) {
                i++;

                JSONObject temp = array.get(i);
                array.set(i, array.get(j));
                array.set(j, temp);
            }
        }

        JSONObject temp = array.get(i + 1);
        array.set(i + 1, array.get(high));
        array.set(high, temp);

        return i + 1;
    }

    private double getDistanceBetweenTwoPoints(double firstLatitude, double firstLongitude, double secondLatitude, double secondLongitude) {
        final int R = 6371; // Radius of the Earth in kilometers

        double latDistance = Math.toRadians(secondLatitude - firstLatitude);
        double lonDistance = Math.toRadians(secondLongitude - firstLongitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(firstLatitude)) * Math.cos(Math.toRadians(secondLatitude)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in kilometers
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        setUserLatitude(location.getLatitude());
                        setUserLongitude(location.getLongitude());
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }
}
