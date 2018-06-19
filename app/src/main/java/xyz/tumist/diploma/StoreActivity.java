package xyz.tumist.diploma;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;

public class StoreActivity extends AppCompatActivity {
    private long storeID;
    Cursor storeCursor;
    Uri currentStoreUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ReceiptsDBHelper handler = new ReceiptsDBHelper(this);
        Locale myLocale = new Locale("ru","RU");

        TextView tvStorePrimaryName = (TextView) findViewById(R.id.store_primary_name);
        TextView tvStoreSecondName = (TextView) findViewById(R.id.store_second_name);
        TextView tvStoreTotalSum = (TextView) findViewById(R.id.item_card_view_purchases_sum);
        TextView tvStorePurchasesAmount = (TextView) findViewById(R.id.item_card_view_purchases_quantity);

        Intent intent = getIntent();
        currentStoreUri = intent.getData();
        storeCursor = getContentResolver().query(currentStoreUri, null, null, null, null);
        storeCursor.moveToNext();

        storeID = storeCursor.getLong(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_ID));

        String storeName = null;
        String storeSecondName = null;
        int purchasesCount = 0;
        if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)) != null){
            tvStorePrimaryName.setText(storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)));
            if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null) {
                tvStoreSecondName.setText(storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)));
            } else tvStoreSecondName.setText("ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN)));

        }
        else if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null){
            tvStorePrimaryName.setText(storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)));
            tvStoreSecondName.setText("ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN)));
        }
        else {
            tvStorePrimaryName.setText("ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN)));
            tvStoreSecondName.setVisibility(View.GONE);
        }

        long purchasesStoreCount = handler.getPurchasesCount(
                DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK + " LIKE " + storeID,
                null);
        if (purchasesStoreCount % 10 == 1) tvStorePurchasesAmount.setText(String.valueOf(purchasesStoreCount) + " покупка");
        else if (purchasesStoreCount % 10 == 2) tvStorePurchasesAmount.setText(String.valueOf(purchasesStoreCount) + " покупки");
        else if (purchasesStoreCount % 10 == 3) tvStorePurchasesAmount.setText(String.valueOf(purchasesStoreCount) + " покупки");
        else if (purchasesStoreCount % 10 == 4) tvStorePurchasesAmount.setText(String.valueOf(purchasesStoreCount) + " покупки");
        else tvStorePurchasesAmount.setText(String.valueOf(purchasesStoreCount) + " покупок");

        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor purchaseSumCursor = db.rawQuery("select sum(" + DataContract.PurchaseEntry.COLUMN_PURCHASE_TOTALSUM +
                ") from " + DataContract.PurchaseEntry.TABLE_NAME +
                " where " + DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK + " = '" + storeID + "' ;", null);
        long amount;
        if(purchaseSumCursor.moveToFirst())
            amount = purchaseSumCursor.getLong(0);
        else
            amount = -1;
        purchaseSumCursor.close();

        NumberFormat n = NumberFormat.getCurrencyInstance(myLocale);
        String formatedTotalSum = n.format(amount / 100.0);
        /** Делаем целую часть суммы жирной **/
        SpannableStringBuilder str = new SpannableStringBuilder(formatedTotalSum);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, formatedTotalSum.length()-4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvStoreTotalSum.setText(str);



        Cursor purchaseCursor = db.rawQuery("SELECT  * FROM " + DataContract.PointEntry.TABLE_NAME + " WHERE " + DataContract.PointEntry.COLUMN_STORE_ID_FK + " LIKE " + storeID, null);
        // Find ListView to populate
        ListView lvPoint = (ListView) findViewById(R.id.store_listview);
// Setup cursor adapter using cursor from last step
        PointCursorAdapter pointAdapter = new PointCursorAdapter(this, purchaseCursor);
// Attach cursor adapter to the ListView
        lvPoint.setAdapter(pointAdapter);
        lvPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PointActivity.class);
                Uri currentPointUri = ContentUris.withAppendedId(DataContract.PointEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentPointUri);
                //intent.putExtra("id", debtID);
                startActivity(intent);
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_rename) {
            showStoreRenameDialog();
            return true;
        }
        if (id == R.id.action_help) {
            Intent intent = new Intent(this, WebViewActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showStoreRenameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rename_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.editText);
        final TextView tvStorePrimaryName = (TextView) findViewById(R.id.store_primary_name);
        final TextView tvStoreSecondName = (TextView) findViewById(R.id.store_second_name);

        dialogBuilder.setTitle("Переименовать магазин");
        dialogBuilder.setMessage("Введите новое название");
        dialogBuilder.setPositiveButton("Готово", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ContentValues storeValues = new ContentValues();
                storeValues.put(DataContract.StoreEntry.COLUMN_STORE_NICKNAME, edt.getText().toString());
                int numOfUpdatedRows = getContentResolver().update(DataContract.StoreEntry.CONTENT_URI, storeValues,
                        DataContract.StoreEntry.COLUMN_STORE_ID + " LIKE " + storeID, null);
                Log.v("Количество обновленных", String.valueOf(numOfUpdatedRows) );
                storeCursor = getContentResolver().query(currentStoreUri, null, null, null, null);
                storeCursor.moveToNext();

                //TODO: Это повторяющийся код. Вынести в отдельный метод и поработать с излишним вызовом курсоров
                if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)) != null){
                    tvStorePrimaryName.setText(storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)));
                    if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null) {
                        tvStoreSecondName.setText(storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)));
                    } else tvStoreSecondName.setText("ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN)));

                }
                else if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null){
                    tvStorePrimaryName.setText(storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)));
                    tvStoreSecondName.setText("ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN)));
                }
                else {
                    tvStorePrimaryName.setText("ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN)));
                    tvStoreSecondName.setVisibility(View.GONE);
                }

                tvStorePrimaryName.setPaintFlags(0);
                tvStoreSecondName.setVisibility(View.VISIBLE);
            }
        });
        dialogBuilder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        storeCursor = getContentResolver().query(currentStoreUri, null, null, null, null);
        storeCursor.moveToNext();
        if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)) != null) {
            edt.setText(storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)));
            dialogBuilder.setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ContentValues pointValues = new ContentValues();
                    pointValues.putNull(DataContract.StoreEntry.COLUMN_STORE_NICKNAME);
                    int numOfUpdatedRows = getContentResolver().update(DataContract.StoreEntry.CONTENT_URI, pointValues,
                            DataContract.StoreEntry.COLUMN_STORE_ID + " LIKE " + storeID, null);
                    Log.v("Количество обновленных", String.valueOf(numOfUpdatedRows));
                    if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null){
                        tvStorePrimaryName.setText(storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)));
                        tvStoreSecondName.setText("ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN)));
                    }
                    else {
                        tvStorePrimaryName.setText("ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN)));
                        tvStoreSecondName.setVisibility(View.GONE);
                    }
                }
            });
        }
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

}
