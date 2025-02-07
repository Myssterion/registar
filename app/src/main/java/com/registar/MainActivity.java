package com.registar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.registar.adapter.EmployeeAdapter;
import com.registar.data.RegistarDatabase;
import com.registar.data.dao.EmployeeDao;
import com.registar.data.dao.LocationDao;
import com.registar.data.model.Employee;
import com.registar.data.model.Location;
import com.registar.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // loadLocale();
        super.onCreate(savedInstanceState);
        loadLocale();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


       /* RegistarDatabase db = RegistarDatabase.getInstance(this);
        db.clearAllTables();
        EmployeeDao dao = db.getEmployeeDao();
        LocationDao locationDao = db.getLocationDao();
        insertLocations(locationDao);
        for(int i=0;i<10;i++)
            dao.insertEmployee(new Employee("Name " + (i+1),"Surname " + (i+1), 30));   */ }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_languages){
            showLanguageDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
/*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_languages){
            showLanguageDialog();
        }
        return super.onOptionsItemSelected(item);
    }
*/
    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_language);
        String[] options = { getString(R.string.english), getString(R.string.serbian)};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0)
                changeLocale("en");
            else if (which == 1)
                changeLocale("sr");
        });
        builder.show();
    }

    private void changeLocale(String langCode) {
        // Save selected language to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("Language", langCode);
        editor.apply();

        // Restart activity to apply changes
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void loadLocale() {
        SharedPreferences preferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String languageCode = preferences.getString("Language", "en");
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void insertLocations(LocationDao locationDao) {
        List<Location> locations = new ArrayList<>();

        locations.add(new Location("Central Park", "New York, NY", 40.785091, -73.968285));
        locations.add(new Location("Eiffel Tower", "Paris, France", 48.858844, 2.294350));
        locations.add(new Location("Great Wall of China", "Beijing, China", 40.431908, 116.570374));
        locations.add(new Location("Sydney Opera House", "Sydney, Australia", -33.856784, 151.215297));
        locations.add(new Location("Colosseum", "Rome, Italy", 41.890210, 12.492231));
        locations.add(new Location("Machu Picchu", "Cusco Region, Peru", -13.163068, -72.545128));
        locations.add(new Location("Taj Mahal", "Agra, India", 27.175144, 78.042142));
        locations.add(new Location("Mount Fuji", "Honshu, Japan", 35.360624, 138.727363));
        locations.add(new Location("Christ the Redeemer", "Rio de Janeiro, Brazil", -22.951916, -43.210487));
        locations.add(new Location("Pyramids of Giza", "Giza, Egypt", 29.979235, 31.134202));

        // Insert the locations on a background thread
        new Thread(() -> {
            for(int i = 0; i < locations.size(); i++)
            locationDao.insertLocation(locations.get(i));
        }).start();
    }
}