package xyz.tumist.diploma;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView navigation;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_items:
//                    //mTextMessage.setText(R.string.title_goods);
//                    return true;
//                case R.id.navigation_purchases:
//                    //mTextMessage.setText(R.string.title_purchases);
//                    return true;
//                case R.id.navigation_stores:
//                    //mTextMessage.setText(R.string.title_stores);
//                    return true;
//            }
//            return false;
//        }
//    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//    }

}
