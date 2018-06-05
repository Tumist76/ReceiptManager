package xyz.tumist.diploma;

import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;

public class PurchaseActivity extends AppCompatActivity {
    private long purchaseID;
    private long storeID;
    private long pointID;
    final String LOG_TAG = PurchaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        Locale myLocale = new Locale("ru","RU");

        TextView tvPurchaseDate = (TextView) findViewById(R.id.purchase_date);
        TextView tvPurchaseStoreName = (TextView) findViewById(R.id.purchase_store_name);
        TextView tvPurchaseTotalSum = (TextView) findViewById(R.id.purchase_total_sum);

        Intent intent = getIntent();
        Uri currentPurchaseUri = intent.getData();

        Cursor purchaseCursor = getContentResolver().query(currentPurchaseUri, null, null, null, null);
        purchaseCursor.moveToNext();
        //присваиваем глобальной переменной ID покупки, так как это понадобится для удаления.
        purchaseID = purchaseCursor.getLong(purchaseCursor.getColumnIndex(DataContract.PurchaseEntry.COLUMN_PURCHASE_ID));
        storeID = purchaseCursor.getLong(purchaseCursor.getColumnIndex(DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK));
        pointID = purchaseCursor.getLong(purchaseCursor.getColumnIndex(DataContract.PurchaseEntry.COLUMN_PURCHASE_POINT_ID_FK));
        Log.v(LOG_TAG, "Проверим выдачу ID покупки, да? ID покупки равен " + purchaseID);
        NumberFormat n = NumberFormat.getCurrencyInstance(myLocale);
        String formatedTotalSum = n.format(purchaseCursor.getLong(purchaseCursor.getColumnIndex(DataContract.PurchaseEntry.COLUMN_PURCHASE_TOTALSUM)) / 100.0);
        /** Делаем целую часть суммы жирной **/
        SpannableStringBuilder str = new SpannableStringBuilder(formatedTotalSum);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, formatedTotalSum.length()-4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPurchaseTotalSum.setText(str);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formattedDate = sdf.format(purchaseCursor.getLong(purchaseCursor.getColumnIndexOrThrow(DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME))*1000L);
        tvPurchaseDate.setText(formattedDate);

        String[] storeProjection = {
                DataContract.StoreEntry.COLUMN_STORE_INN,
                DataContract.StoreEntry.COLUMN_STORE_NICKNAME,
                DataContract.StoreEntry.COLUMN_STORE_NAME
        };
        String storeSelection = DataContract.StoreEntry.COLUMN_STORE_ID + " LIKE ?";
        String[] storeSelectionArgs = {purchaseCursor.getString(purchaseCursor.getColumnIndexOrThrow(DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK))};
        Cursor storeCursor = getContentResolver().query(DataContract.StoreEntry.CONTENT_URI,
                storeProjection,
                storeSelection,
                storeSelectionArgs,
                null
        );
        storeCursor.moveToNext();
        String storeName = null;
        int purchasesCount = 0;
        if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)) != null){
            storeName = storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME));
        }
        else if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null){
            storeName = storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME));
        }
        else storeName = "ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN));
        tvPurchaseStoreName.setText(storeName);

        ReceiptsDBHelper handler = new ReceiptsDBHelper(this);
        // Get access to the underlying writeable database
        SQLiteDatabase db = handler.getWritableDatabase();
        // Query for items from the database and get a cursor back
        String[] itemProjection = {
                DataContract.GoodEntry.COLUMN_GOOD_ID,
                DataContract.GoodEntry.COLUMN_GOOD_NAME,
                DataContract.GoodEntry.COLUMN_GOOD_QUANTITY,
                DataContract.GoodEntry.COLUMN_GOOD_SUM,
                DataContract.GoodEntry.COLUMN_GOOD_PRICE
        };
        String itemSelection = DataContract.GoodEntry.COLUMN_GOOD_PURCHASE_ID_FK + " LIKE ?";
        String[] itemSelectionArgs = {purchaseCursor.getString(purchaseCursor.getColumnIndexOrThrow(DataContract.PurchaseEntry.COLUMN_PURCHASE_ID))};
        Cursor itemCursor = db.query(DataContract.GoodEntry.TABLE_NAME ,
                itemProjection,
                itemSelection,
                itemSelectionArgs,
                null,
                null,
                null
        );
        Log.v(LOG_TAG, "Количество товаров в покупке" + String.valueOf(itemCursor.getCount()));
         //db.rawQuery("SELECT  * FROM " + DataContract.GoodEntry.TABLE_NAME , null);
        // Find ListView to populate
        ListView lvItems = (ListView) findViewById(R.id.fragment_listview);
        Log.v(LOG_TAG, "ListView is set");
        // Setup cursor adapter using cursor from last step
        ItemCursorAdapter itemAdapter = new ItemCursorAdapter(this, itemCursor);
        // Attach cursor adapter to the ListView
        lvItems.setAdapter(itemAdapter);
        itemAdapter.changeCursor(itemCursor);
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
                Uri currentDebtUri = ContentUris.withAppendedId(DataContract.GoodEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentDebtUri);
                //intent.putExtra("id", debtID);
                startActivity(intent);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchase, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            new AlertDialog.Builder(this)
                    .setTitle("Удаление")
                    .setMessage("Вы действительно хотите удалить эту покупку?")
                    //.setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            deletePurchase();
                            Toast toast = Toast.makeText(getApplicationContext(), "Покупка удалена", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();


            return true;
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(this, WebViewActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void deletePurchase() {
        //Создаём хендлер для вызова метода счёта покупок
        ReceiptsDBHelper handler = new ReceiptsDBHelper(this);

        int itemRowsDeleted = getContentResolver().delete(
                DataContract.GoodEntry.CONTENT_URI,
                DataContract.GoodEntry.COLUMN_GOOD_PURCHASE_ID_FK + " LIKE " + purchaseID,
                null);
        Log.v(LOG_TAG, "Количество удалённых товаров " + itemRowsDeleted);

        if (itemRowsDeleted > 0) {
            int purchaseRowsDeleted = getContentResolver().delete(
                    DataContract.PurchaseEntry.CONTENT_URI,
                    DataContract.PurchaseEntry.COLUMN_PURCHASE_ID + " LIKE " + purchaseID,
                    null);
            Log.v(LOG_TAG, "Количество удалённых покупок " + purchaseRowsDeleted);
        }

        long purchasesPointCount = handler.getPurchasesCount(
                DataContract.PurchaseEntry.COLUMN_PURCHASE_POINT_ID_FK + " LIKE " + storeID,
                null);
        if (purchasesPointCount == 0) {
            int pointRowsDeleted = getContentResolver().delete(
                    DataContract.PointEntry.CONTENT_URI,
                    DataContract.PointEntry.COLUMN_POINT_ID + " LIKE " + pointID,
                    null);
            Log.v(LOG_TAG, "Количество удалённых точек продаж " + pointRowsDeleted);

            long purchasesStoreCount = handler.getPurchasesCount(
                    DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK + " LIKE " + storeID,
                    null);
            if (purchasesStoreCount == 0) {
                int storeRowsDeleted = getContentResolver().delete(
                        DataContract.StoreEntry.CONTENT_URI,
                        DataContract.StoreEntry.COLUMN_STORE_ID + " LIKE " + storeID,
                        null);
                Log.v(LOG_TAG, "Количество удалённых магазинов " + storeRowsDeleted);
            }
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        setResult(Activity.RESULT_OK);
    }
}
