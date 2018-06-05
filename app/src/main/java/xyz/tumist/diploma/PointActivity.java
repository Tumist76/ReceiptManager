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

public class PointActivity extends AppCompatActivity {

    private long pointID;
    private long storeID;
    Cursor pointCursor;
    Cursor storeCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
        ReceiptsDBHelper handler = new ReceiptsDBHelper(this);
        Locale myLocale = new Locale("ru","RU");

        TextView tvPointPrimaryName = (TextView) findViewById(R.id.point_name);
        TextView tvPointAddress = (TextView) findViewById(R.id.point_address);
        TextView tvPointTotalSum = (TextView) findViewById(R.id.point_card_view_purchases_sum);
        TextView tvPointPurchasesAmount = (TextView) findViewById(R.id.point_card_view_purchases_amount);

        Intent intent = getIntent();
        Uri currentPointUri = intent.getData();
        pointCursor = getContentResolver().query(currentPointUri, null, null, null, null);
        pointCursor.moveToFirst();

        pointID = pointCursor.getLong(pointCursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_ID));
        storeID = pointCursor.getLong(pointCursor.getColumnIndex(DataContract.PointEntry.COLUMN_STORE_ID_FK));

        String storeSelection = DataContract.StoreEntry.COLUMN_STORE_ID + " LIKE " + storeID;
        storeCursor = getContentResolver().query(DataContract.StoreEntry.CONTENT_URI, null, storeSelection, null, null);
        storeCursor.moveToFirst();
        String storeName = null;
        String storeSecondName = null;
        int purchasesCount = 0;
        if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)) != null){
            tvPointPrimaryName.setText(storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)));
        }
        else if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null){
            tvPointPrimaryName.setText(storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)));
        }
        else {
            tvPointPrimaryName.setText("ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN)));
        }

        if (pointCursor.getString(pointCursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_NICKNAME)) != null){
            tvPointAddress.setText(pointCursor.getString(pointCursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_NICKNAME)));
        }
        else if (pointCursor.getString(pointCursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_ADDRESS)) != null){
            tvPointAddress.setText(pointCursor.getString(pointCursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_ADDRESS)));
        }
        else {
            tvPointAddress.setText("Точка без адреса");
        }

        long purchasesPointCount = handler.getPurchasesCount(
                DataContract.PurchaseEntry.COLUMN_PURCHASE_POINT_ID_FK + " LIKE " + pointID,
                null);
        if (purchasesPointCount % 10 == 1) tvPointPurchasesAmount.setText(String.valueOf(purchasesPointCount) + " покупка");
        else if (purchasesPointCount % 10 == 2) tvPointPurchasesAmount.setText(String.valueOf(purchasesPointCount) + " покупки");
        else if (purchasesPointCount % 10 == 3) tvPointPurchasesAmount.setText(String.valueOf(purchasesPointCount) + " покупки");
        else if (purchasesPointCount % 10 == 4) tvPointPurchasesAmount.setText(String.valueOf(purchasesPointCount) + " покупки");
        else tvPointPurchasesAmount.setText(String.valueOf(purchasesPointCount) + " покупок");

        SQLiteDatabase db = handler.getReadableDatabase();
        Cursor purchaseSumCursor = db.rawQuery("select sum(" + DataContract.PurchaseEntry.COLUMN_PURCHASE_TOTALSUM +
                ") from " + DataContract.PurchaseEntry.TABLE_NAME +
                " where " + DataContract.PurchaseEntry.COLUMN_PURCHASE_POINT_ID_FK + " = '" + pointID + "' ;", null);
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
        tvPointTotalSum.setText(str);



        Cursor purchasePointCursor = db.rawQuery("SELECT  * FROM " + DataContract.PurchaseEntry.TABLE_NAME + " WHERE " + DataContract.PurchaseEntry.COLUMN_PURCHASE_POINT_ID_FK + " LIKE " + pointID, null);
        // Find ListView to populate
        ListView lvPoint = (ListView) findViewById(R.id.point_listview);
// Setup cursor adapter using cursor from last step
        PurchasePointCursorAdapter purchasePointAdapter = new PurchasePointCursorAdapter(this, purchasePointCursor);
// Attach cursor adapter to the ListView
        lvPoint.setAdapter(purchasePointAdapter);
        lvPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PurchaseActivity.class);
                Uri currentPurchaseUri = ContentUris.withAppendedId(DataContract.PurchaseEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentPurchaseUri);
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
        final TextView tvPointAddress = (TextView) findViewById(R.id.point_address);

        dialogBuilder.setTitle("Переименовать точку");
        dialogBuilder.setMessage("Введите новое название");
        dialogBuilder.setPositiveButton("Готово", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ContentValues pointValues = new ContentValues();
                pointValues.put(DataContract.PointEntry.COLUMN_POINT_NICKNAME, edt.getText().toString());
                int numOfUpdatedRows = getContentResolver().update(DataContract.PointEntry.CONTENT_URI, pointValues,
                        DataContract.PointEntry.COLUMN_POINT_ID + " LIKE " + pointID, null);
                Log.v("Количество обновленных", String.valueOf(numOfUpdatedRows) );
                tvPointAddress.setText(edt.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        if (pointCursor.getString(pointCursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_NICKNAME)) != null) {
            dialogBuilder.setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ContentValues pointValues = new ContentValues();
                    pointValues.putNull(DataContract.PointEntry.COLUMN_POINT_NICKNAME);
                    int numOfUpdatedRows = getContentResolver().update(DataContract.PointEntry.CONTENT_URI, pointValues,
                            DataContract.PointEntry.COLUMN_POINT_ID + " LIKE " + pointID, null);
                    Log.v("Количество обновленных", String.valueOf(numOfUpdatedRows));
                    if (pointCursor.getString(pointCursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_ADDRESS)) != null) {
                        tvPointAddress.setText(pointCursor.getString(pointCursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_ADDRESS)));
                    } else {
                        tvPointAddress.setText("Точка без адреса");
                    }
                }
            });
        }
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
