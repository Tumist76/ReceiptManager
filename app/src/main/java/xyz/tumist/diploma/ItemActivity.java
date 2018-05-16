package xyz.tumist.diploma;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import xyz.tumist.diploma.data.DataContract;

public class ItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        TextView tvItemName = (TextView) findViewById(R.id.item_name);
        TextView tvItemSum = (TextView) findViewById(R.id.item_sum);
        TextView tvItemPrice = (TextView) findViewById(R.id.item_price);
        TextView tvItemQuantity = (TextView) findViewById(R.id.item_quantity);
        TextView tvItemNds10Label = (TextView) findViewById(R.id.item_nds10_label);
        TextView tvItemNds18Label = (TextView) findViewById(R.id.item_nds18_label);
        TextView tvItemNds10 = (TextView) findViewById(R.id.item_nds10);
        TextView tvItemNds18 = (TextView) findViewById(R.id.item_nds18);


        Intent intent = getIntent();
        Uri currentItemUri = intent.getData();
        String[] itemProjection = {
                DataContract.GoodEntry.COLUMN_GOOD_NAME,
                DataContract.GoodEntry.COLUMN_GOOD_SUM,
                DataContract.GoodEntry.COLUMN_GOOD_QUANTITY,
                DataContract.GoodEntry.COLUMN_GOOD_PRICE,
                DataContract.GoodEntry.COLUMN_GOOD_NDS18,
                DataContract.GoodEntry.COLUMN_GOOD_NDS10
        };
        Cursor itemCursor = getContentResolver().query(currentItemUri, null, null, null, null);
        itemCursor.moveToNext();

        Locale myLocale = new Locale("ru","RU");
        NumberFormat n = NumberFormat.getCurrencyInstance(myLocale);

        tvItemName.setText(itemCursor.getString(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NAME)));
        tvItemSum.setText(n.format(itemCursor.getLong(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_SUM)) / 100.0));
        tvItemPrice.setText(n.format(itemCursor.getLong(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_PRICE))/100.0));
        tvItemQuantity.setText(itemCursor.getString(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_QUANTITY)));

        if (itemCursor.getString(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NDS10)) != null){
            tvItemNds10.setText(n.format(itemCursor.getLong(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NDS10))/100.0));
        } else {
            tvItemNds10.setVisibility(View.GONE);
            tvItemNds10Label.setVisibility(View.GONE);
        }
        if (itemCursor.getString(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NDS18)) != null){
            tvItemNds18.setText(n.format(itemCursor.getLong(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NDS18))/100.0));
        } else {
            tvItemNds18.setVisibility(View.GONE);
            tvItemNds18Label.setVisibility(View.GONE);
        }
//        String[] itemProjection = {
//                DataContract.StoreEntry.COLUMN_STORE_NAME,
//                DataContract.StoreEntry.COLUMN_STORE_NICKNAME,
//                DataContract.StoreEntry.COLUMN_STORE_INN
//        };
//        String itemSelection = DataContract.StoreEntry.COLUMN_STORE_ID + " LIKE ?";
//        String[] itemSelectionArgs = {};
//        Cursor storeCursor = database.query(DataContract.StoreEntry.TABLE_NAME,
//                storeProjection,
//                storeSelection,
//                storeSelectionArgs,
//                null,
//                null,
//                null
//        );
    }
}
