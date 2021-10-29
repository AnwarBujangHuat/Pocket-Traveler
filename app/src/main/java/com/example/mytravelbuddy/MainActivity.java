package com.example.mytravelbuddy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mytravelbuddy.ui.camera_translate;
import com.example.mytravelbuddy.ui.home.HomeFragment;
import com.example.mytravelbuddy.ui.map.Map_search;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton camera;
    TextView textView3;
    MLTextAnalyzer analyzer;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int LOCATION_PERMISSION_CODE = 101;
    private static final int LOCATION_COURSE_CODE = 102;
    private static final int WIFI_PERMISSION_CODE = 103;
    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        MLApplication.getInstance().setApiKey(getResources().getString(R.string.api_key));

        //loading the default fragment
        loadFragment(new HomeFragment());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setBackground(null);
        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        break;

                    case R.id.navigation_map:
                        fragment = new Map_search();
                        break;

                }

                return loadFragment(fragment);
            }
        });


        //for mic
        camera=findViewById(R.id.camera);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cam=new Intent(MainActivity.this, camera_translate.class);
                startActivity(cam);


            }
        });


        //Permission Req
        PermissionHandler(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        PermissionHandler(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE);
        PermissionHandler(Manifest.permission.ACCESS_COARSE_LOCATION, LOCATION_COURSE_CODE);
        PermissionHandler(Manifest.permission.ACCESS_WIFI_STATE, WIFI_PERMISSION_CODE);


    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }



    //Permission Method
        public void PermissionHandler(String permission, int requestCode) {

//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk < 28 Q");
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {

                // Requesting the permission
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

            } else {
                Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            }
        }
//    }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            // This function is called when user accept or decline the permission.
            // Request Code is used to check which permission called this function.
            // This request code is provided when user is prompt for permission.
            if (requestCode == CAMERA_PERMISSION_CODE) {

                // Checking whether user granted the permission or not.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Showing the toast message
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == LOCATION_PERMISSION_CODE) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == LOCATION_COURSE_CODE) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }   else if (requestCode == WIFI_PERMISSION_CODE) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Wifi Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Wifi Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }



