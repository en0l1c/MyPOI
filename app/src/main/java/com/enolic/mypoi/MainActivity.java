package com.enolic.mypoi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static boolean editMode; // true to declare that we are going to new form for submit, false for edit
    FloatingActionButton goSubmitPoiBtn; // from MainActivity
    Button refreshPoinListBtn; // from MainActivity
    ListView pointsList;
    ArrayList<String> listItem;
    ArrayAdapter adapter;
    DatabaseHelper db;
    Button viewAllPoisBtn;
    EditText searchPoiEditText;
    Spinner searchFilterSpinner;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editMode = true; // true default, so it means that we are going to second form for submititng

        db = new DatabaseHelper(this);

        listItem = new ArrayList<>();

        pointsList = findViewById(R.id.listView);

        searchPoiEditText = findViewById(R.id.searchPoiET);
        searchFilterSpinner = findViewById(R.id.searchSpinner);

        pointsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editMode = false;
                String itemText = pointsList.getItemAtPosition(i).toString();
                //Toast.makeText(MainActivity.this, ""+poi_title, Toast.LENGTH_SHORT).show();



                Intent intent = new Intent(getApplicationContext(), SubmitPoiActivity.class);

                intent.putExtra("EDIT", "EDIT POI");



                String POI_TIMESTAMP_EDIT = null;
                String POI_LOCATION_EDIT = null;
                String POI_DESCRIPTION_EDIT = null;
                String POI_CATEGORY_EDIT = null;


                // extract all entry info by id
                Cursor cursor = db.viewDataById(Integer.parseInt(itemText.substring(0,1))); // extract id from itemText

                if(cursor.getCount() == 0) {
                    Toast.makeText(MainActivity.this, ""+Integer.parseInt(itemText.substring(0,1).trim()), Toast.LENGTH_SHORT).show();
                }
                else {
                    while(cursor.moveToNext()) {
                        POI_TIMESTAMP_EDIT = cursor.getString(2);// get timestamp
                        POI_LOCATION_EDIT = cursor.getString(3);
                        POI_DESCRIPTION_EDIT = cursor.getString(4);
                        POI_CATEGORY_EDIT = cursor.getString(5);
                    }

                }

                cursor.close();
                // intent values to the second activity
                intent.putExtra("POI_ID_EDIT", itemText.substring(0,1).trim());
                intent.putExtra("POI_TITLE_EDIT", itemText.substring(3,itemText.length())); // delete the first 3 chars from poi title - this chars contain id
                intent.putExtra("POI_TIMESTAMP_EDIT", POI_TIMESTAMP_EDIT);
                intent.putExtra("POI_LOCATION_EDIT", POI_LOCATION_EDIT);
                intent.putExtra("POI_DESCRIPTION_EDIT", POI_DESCRIPTION_EDIT);
                intent.putExtra("POI_CATEGORY_EDIT", POI_CATEGORY_EDIT);


                startActivity(intent);
            }
        });

        viewAllPoisBtn = findViewById(R.id.viewAllPoisBtn);
        refreshPoinListBtn = findViewById(R.id.refreshPointListBtn);
        viewAllData();
        viewAllPoisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pointsList.setVisibility(View.VISIBLE);
                refreshPoinListBtn.setVisibility(View.VISIBLE);
                // TODO: keep only one button. Make view data button turn into refresh button
            }
        });

        refreshPoinListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    adapter.clear(); // clear the list
                    viewAllData();
                }
                catch(Exception e) {

                }

            }
        });


        goSubmitPoiBtn = findViewById(R.id.goSubmitPoiBtn); // btn from mainactivity

        // Go to Submit a new POI
        goSubmitPoiBtn.setOnClickListener(new View.OnClickListener() {


            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                editMode = true;
                Intent intent = new Intent(getApplicationContext(), SubmitPoiActivity.class);
                intent.putExtra("SUBMIT", "SUBMIT NEW POI"); // when clicked the button "add new poi" the button on the second activity become "SUMBIT NEW POI"


                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Waiting to get GPS Location", Toast.LENGTH_LONG).show();
            }
        });



        searchPoiEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    // Perform action on key press
                    Toast.makeText(MainActivity.this, searchPoiEditText.getText(), Toast.LENGTH_SHORT).show();
                    if(String.valueOf(searchFilterSpinner.getSelectedItem()).equals("Title")) {
                        adapter.clear();
                        Cursor cursor = db.viewDataByTitle(String.valueOf(searchPoiEditText.getText()));

                        if(cursor.getCount() == 0) {
                            Toast.makeText(MainActivity.this, ""+searchPoiEditText.getText(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            while(cursor.moveToNext()) {
                                listItem.add(cursor.getString(0) + ". " + cursor.getString(1) );
                            }

                        }
                        pointsList.setVisibility(View.VISIBLE);
                        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, listItem);
                        pointsList.setAdapter(adapter);
                        cursor.close();
                    }
                    else if(String.valueOf(searchFilterSpinner.getSelectedItem()).equals("Category")) {
                        adapter.clear();
                        Cursor cursor = db.viewDataByCategory(String.valueOf(searchPoiEditText.getText()));

                        if(cursor.getCount() == 0) {
                            Toast.makeText(MainActivity.this, ""+searchPoiEditText.getText(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            while(cursor.moveToNext()) {
                                listItem.add(cursor.getString(0) + ". " + cursor.getString(1) );
                            }

                        }
                        pointsList.setVisibility(View.VISIBLE);
                        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, listItem);
                        pointsList.setAdapter(adapter);
                        cursor.close();
                    }
                    return true;
                }
                return false;
            }
        });

    }

    private void viewAllData() {
        Cursor cursor = db.viewAllData();

        if(cursor.getCount() == 0) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        }
        else {
            while(cursor.moveToNext()) {
                listItem.add(cursor.getString(0) + ". " + cursor.getString(1) );
            }

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItem);
            pointsList.setAdapter(adapter);
        }

        cursor.close();
    }


}








































