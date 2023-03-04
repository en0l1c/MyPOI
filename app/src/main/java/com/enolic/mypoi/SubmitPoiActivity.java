package com.enolic.mypoi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SubmitPoiActivity extends AppCompatActivity implements LocationListener {
    public static final int LOCATION_REQUEST_CODE = 123;

    static int poiId = 0;
    DatabaseHelper db;

    EditText poiTitleEditText;
    EditText descriptionEditText;
    Spinner poiCategorySpinner;
    Button submitPoiBtn; // SUBMIT BUTTON
    Button refreshTimestampBtn; // REFRESH TIMESTAMP BUTTON
    Button refreshGpsBtn; // REFRESH GPS BUTTON
    Button deletePoiButton;
    TextView timestampTextView; // TIMESTAMP TEXT
    TextView gpsTextView; // GPS TEXT
    LocationManager locationManager; // simple get info from the android os, does not need instanciation

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_poi);


        db = new DatabaseHelper(this);


        poiTitleEditText = findViewById(R.id.poiTitleET);
        descriptionEditText = findViewById(R.id.descriptionET);
        poiCategorySpinner = findViewById(R.id.poiCatSpinner);
        timestampTextView = findViewById(R.id.timestampTV);
        gpsTextView = findViewById(R.id.gpsTV);
        submitPoiBtn = findViewById(R.id.submitPoiBtn);
        refreshTimestampBtn = findViewById(R.id.refreshTsBtn);
        refreshGpsBtn = findViewById(R.id.refreshGpsBtn);
        deletePoiButton = findViewById(R.id.deletePoiBtn);

        gpsTextView.setTextColor(Color.RED);


        // CHECK HOW USERS CAME TO SECOND ACTIVITY / TRUE->CAME FOR ADD NEW POI, FALSE->CAME FOR EDIT EXISTING POI
        if(MainActivity.editMode) {
            // GET RIGHT TEXT FOR 'SUBMIT' BUTTON / IF isNewOrEdit == true, button text set to 'SUBMIT NEW POI'
            submitPoiBtn.setText(getIntent().getStringExtra("SUBMIT")); // get the text from the intent coming to this activity with key="SUBMIT"
        }
        else {
            // make delete button visible
            deletePoiButton.setVisibility(View.VISIBLE);
            // if false
            // GET POI ID
            poiId = Integer.parseInt(getIntent().getStringExtra("POI_ID_EDIT"));
            // GET TITLE
            poiTitleEditText.setText(getIntent().getStringExtra("POI_TITLE_EDIT"));
            // GET TIMESTAMP
            timestampTextView.setText(getIntent().getStringExtra("POI_TIMESTAMP_EDIT"));
            // GET LOCATION
            gpsTextView.setText(getIntent().getStringExtra("POI_LOCATION_EDIT"));
            gpsTextView.setTextColor(Color.GREEN);
            // GET DESCRIPTION
            descriptionEditText.setText(getIntent().getStringExtra("POI_DESCRIPTION_EDIT"));
            // GET CATEGORY
            ArrayAdapter myAdapter = (ArrayAdapter) poiCategorySpinner.getAdapter();
            int poiCategoryPosition = myAdapter.getPosition(getIntent().getStringExtra("POI_CATEGORY_EDIT")); // get the position of poiCategory based on string saved on db
            poiCategorySpinner.setSelection(poiCategoryPosition);

            // GET RIGHT TEXT FOR 'SUBMIT' BUTTON / IF isNewOrEdit == false, button text set to 'EDIT'
            submitPoiBtn.setText(getIntent().getStringExtra("EDIT"));

        }

        // SUBMIT BUTTON
        if(MainActivity.editMode) {

            //Toast.makeText(this, "check of submit button(if) " + MainActivity.isNewOrEdit, Toast.LENGTH_SHORT).show();
            submitPoiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!poiTitleEditText.getText().toString().equals("") &&
                            !timestampTextView.getText().toString().equals("") &&
                            !gpsTextView.getText().toString().equals("") &&
                            !descriptionEditText.getText().toString().equals("") &&
                            !poiCategorySpinner.getSelectedItem().toString().equals("")
                            &&
                            db.insertData(poiTitleEditText.getText().toString(),
                                    timestampTextView.getText().toString(),
                                    gpsTextView.getText().toString(),
                                    descriptionEditText.getText().toString(),
                                    poiCategorySpinner.getSelectedItem().toString() ))
                    {
                        Toast.makeText(SubmitPoiActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                        MainActivity.editMode = false;
                        //deletePoiButton.setVisibility(View.INVISIBLE);

                        Intent intent = new Intent(SubmitPoiActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(SubmitPoiActivity.this, "Data not added. Please fill carefully all the fields", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        else {
            // EDIT - UPDATE ENTRY
            submitPoiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!poiTitleEditText.getText().toString().equals("") &&
                            !timestampTextView.getText().toString().equals("") &&
                            !gpsTextView.getText().toString().equals("") &&
                            !descriptionEditText.getText().toString().equals("") &&
                            !poiCategorySpinner.getSelectedItem().toString().equals("")
                            &&
                            db.updateData(poiId, poiTitleEditText.getText().toString(),
                                    timestampTextView.getText().toString(),
                                    gpsTextView.getText().toString(),
                                    descriptionEditText.getText().toString(),
                                    poiCategorySpinner.getSelectedItem().toString() ))
                    {
                        Toast.makeText(SubmitPoiActivity.this, "Data Updated", Toast.LENGTH_SHORT).show();
                        MainActivity.editMode = false;


                        Intent intent = new Intent(SubmitPoiActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(SubmitPoiActivity.this, "Data not updated", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(SubmitPoiActivity.this, "YOU ARE ON EDIT MODE", Toast.LENGTH_SHORT).show();
                    MainActivity.editMode = true;
                    Intent intent = new Intent(SubmitPoiActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });


            deletePoiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.deleteData(poiId);
                    MainActivity.editMode = true;
                    Intent intent = new Intent(SubmitPoiActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            });

        }



        //// TIMESTAMP ////
        ///////////////////

        // Set timestamp at onCreate of activity
        if(MainActivity.editMode) {
            Long tsLong = System.currentTimeMillis() / 1000; //timestamp
            timestampTextView.setText(tsLong.toString());
        }


        // Refresh timestamp at clicking the refresh button

        refreshTimestampBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshTimestampAndGps();
            }
        });

        refreshGpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshTimestampAndGps();
            }
        });


        /////// GPS ///////
        ///////////////////

        // set gps location at onCreate of activity
        if(MainActivity.editMode) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // request location

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // we ask for the permission before the return statement because if we ask for this after the return its compile error
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            //locationManager.removeUpdates(this); // this stops the requestLocationUpdates

            // kanoume implement ton listener wste na ananeonoume sunexos to location tou locationManager.requestLocationUpdates
            // tha valoume tin idia tin activity na kanei implement ton LocationListener
        }
        else {
            Toast.makeText(this, "EDIT MODE - onCreate", Toast.LENGTH_SHORT).show();
        }



    }

    // otan kleisei to parathyro pou zitaei to permission apo ton xristi tote tha treksei o kodikas tis onRequestPermissionResult
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // need to check if requestCode is the same as LOCATION_REQUEST_CODE

        // ksanavazoume to check gia to permission stin onrequestpermissionresult wste na min xreiastei na patithei duo fores to koumpi gia na travixei to location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);



    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // set gps location at onCreate of activity
        gpsTextView.setText(location.getLatitude() + "," + location.getLongitude());
        gpsTextView.setTextColor(Color.GREEN);

        locationManager.removeUpdates(this); // this stops the requestLocationUpdates
    }

    private void refreshTimestampAndGps() {
        // Method to refresh timestamp and gps location
        Toast.makeText(this, "Timestamp and Location updated", Toast.LENGTH_SHORT).show();
        // Timestamp
        Long tsLong = System.currentTimeMillis() / 1000; //timestamp
        timestampTextView.setText(tsLong.toString());

        // GPS
        //generate once again the lat and long when refresh button is clicked

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(SubmitPoiActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SubmitPoiActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        gpsTextView.setTextColor(Color.YELLOW);
    }
}






















































