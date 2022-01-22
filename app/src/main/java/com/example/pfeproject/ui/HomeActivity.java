package com.example.pfeproject.ui;

import android.os.Bundle;
import android.widget.Toast;

import com.example.pfeproject.R;
import com.example.pfeproject.utils.Types;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity {
    private long backPressedTime;
    private Toast BackToast;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupFragment();

    }

    private void setupNavController() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_store, R.id.navigation_publicity, R.id.navigation_profile)
                .build();


        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    private void setupFragment() {
        setupNavController();
        if (getIntent().hasExtra(Types.Fragment)) {
            int pos = getIntent().getIntExtra(Types.Fragment, 0);
            switch (pos) {
                case 0:
                    navController.navigate(R.id.navigation_publicity);
                    break;
                case 1:
                    navController.navigate(R.id.navigation_store);
                    break;
                case 2:
                    navController.navigate(R.id.navigation_profile);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            BackToast.cancel();
            finishAffinity();
            //super.onBackPressed();
            //return;
        } else {
            BackToast = Toast.makeText(this, ""+getString(R.string.toast_finish), Toast.LENGTH_SHORT);
            BackToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}