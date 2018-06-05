package xyz.tumist.diploma;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import xyz.tumist.diploma.main_page.ItemsFragment;
import xyz.tumist.diploma.main_page.PurchasesFragment;
import xyz.tumist.diploma.main_page.StoresFragment;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView navigation;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), QRScanner.class);
                startActivity(intent);
            }
        });
        checkCameraPermissions();
        checkStorageReadPermission();
        checkStorageWritePermission();
        navigation = (BottomNavigationView)findViewById(R.id.navigation);
//        inflatemenu убивает приложение, хммм. Но я и через XML его забил.
//        bottomNavigation.inflateMenu(R.menu.navigation);
        fragmentManager = getSupportFragmentManager();

        //Поставить по умолчанию страницу с долгами мне, чтобы сразу иинициализировать фрагмент, ну и потому что хочу этот экран стартовым
        navigation.setSelectedItemId(R.id.navigation_purchases); //выбор в bottomnavigationview
//        fragment = new PurchasesFragment(); //задание фрагмента
//        final FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.main_container, fragment).commit(); //установка фрагмента
//        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.main_container, new PurchasesFragment());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.navigation_purchases:
                        fragment = new PurchasesFragment();
                        Log.v(LOG_TAG, "Тыкнуты покупки в навигации");
                        break;
                    case R.id.navigation_items:
                        fragment = new ItemsFragment();
                        break;
                    case R.id.navigation_stores:
                        fragment = new StoresFragment();
                        break;
                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });
//        // Создаём или открываем базу данных для чтения
//        DebtDBHelper mDbHelper = new DebtDBHelper(this);
//
//
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//        // Выполнить эту SQL-команду "SELECT * FROM debts"
//        // чтобы получить курсор, который содержить все строки из таблицы debts
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DataContract.DebtEntry.TABLE_NAME, null);
//
//        try{
//            //Отобразить количество строк в Cursor (который отражает количество
//            //записей в базе данных).
//            TextView displayNumberOfRows = (TextView) findViewById(R.id.databasetest);
//            displayNumberOfRows.setText("Количество строк в базе данных: " + cursor.getCount());
//        } finally {
//            //Всегда нужнл закрывать курсор когда чтение из него закончено.
//            //Это освобождает ресурсы и отключает курсор.
//            cursor.close();
//        }
    }

    private void checkCameraPermissions() {
        final int MY_CAMERA_REQUEST_CODE = 100;

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_CAMERA_REQUEST_CODE);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted");
        }
    }

    public  boolean checkStorageReadPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG,"Permission is granted");
                return true;
            } else {

                Log.v(LOG_TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted");
            return true;
        }
    }

    public  boolean checkStorageWritePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(LOG_TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted2");
            return true;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(this, WebViewActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
