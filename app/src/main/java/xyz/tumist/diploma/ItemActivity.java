package xyz.tumist.diploma;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;

import static android.view.View.GONE;

public class ItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        ReceiptsDBHelper handler = new ReceiptsDBHelper(this);
        SQLiteDatabase db = handler.getReadableDatabase();
        Locale myLocale = new Locale("ru","RU");
        NumberFormat n = NumberFormat.getCurrencyInstance(myLocale);

        TextView tvItemName = (TextView) findViewById(R.id.item_name);
        TextView tvItemSum = (TextView) findViewById(R.id.item_sum);
        TextView tvItemPrice = (TextView) findViewById(R.id.item_price);
        TextView tvItemQuantity = (TextView) findViewById(R.id.item_quantity);
        TextView tvItemNds10Label = (TextView) findViewById(R.id.item_nds10_label);
        TextView tvItemNds18Label = (TextView) findViewById(R.id.item_nds18_label);
        TextView tvItemNds10 = (TextView) findViewById(R.id.item_nds10);
        TextView tvItemNds18 = (TextView) findViewById(R.id.item_nds18);
        TextView tvItemMinPrice = (TextView) findViewById(R.id.item_card_view_min_price);
        TextView tvItemMinPriceDate = (TextView) findViewById(R.id.item_card_view_min_price_date);
        TextView tvItemMaxPriceDate = (TextView) findViewById(R.id.item_card_view_max_price_date);
        TextView tvItemMaxPrice = (TextView) findViewById(R.id.item_card_view_max_price);
        TextView tvItemPurchasesSum = (TextView) findViewById(R.id.item_card_view_purchases_sum);
        TextView tvItemPurchasesQuantity = (TextView) findViewById(R.id.item_card_view_purchases_quantity);
        TextView tvItemPriceNoDifference = (TextView) findViewById(R.id.item_card_view_price_no_difference);
        LinearLayout llStats = (LinearLayout) findViewById(R.id.layout_stats);
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
        Log.v("itemID", String.valueOf(currentItemUri));
        String itemName = itemCursor.getString(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NAME));

        String itemArraySelection = DataContract.GoodEntry.COLUMN_GOOD_NAME + " LIKE '" + itemName + "'";
        Cursor itemArray = getContentResolver().query(DataContract.GoodEntry.CONTENT_URI, null, itemArraySelection, null, null);
        if (itemArray.getCount() > 1) {
            if (itemArray.getCount() % 10 == 1) tvItemPurchasesQuantity.setText(itemArray.getCount() + " покупка");
            else if (itemArray.getCount() % 10 == 2) tvItemPurchasesQuantity.setText(itemArray.getCount() + " покупки");
            else if (itemArray.getCount() % 10 == 3) tvItemPurchasesQuantity.setText(itemArray.getCount() + " покупки");
            else if (itemArray.getCount() % 10 == 4) tvItemPurchasesQuantity.setText(itemArray.getCount() + " покупки");
            else tvItemPurchasesQuantity.setText(itemArray.getCount() + " покупок");
            Cursor sumCursor = db.rawQuery("SELECT SUM(" + DataContract.GoodEntry.COLUMN_GOOD_SUM + ") as Total FROM " + DataContract.GoodEntry.TABLE_NAME + " WHERE " + DataContract.GoodEntry.COLUMN_GOOD_NAME + " = '" + itemName + "'", null);

            if (sumCursor.moveToFirst()) {
                long total = sumCursor.getLong(sumCursor.getColumnIndex("Total"));// get final total
                Log.v("TotalSum", String.valueOf(total));
                tvItemPurchasesSum.setText(n.format(total / 100.00));
            }
            long itemMinPrice;
            long itemMaxPrice;

            Cursor itemMaxPriceCursor = db.query(DataContract.GoodEntry.TABLE_NAME, null, DataContract.GoodEntry.COLUMN_GOOD_PRICE + " =(SELECT MAX(" + DataContract.GoodEntry.COLUMN_GOOD_PRICE + ") FROM " + DataContract.GoodEntry.TABLE_NAME+ " WHERE " + DataContract.GoodEntry.COLUMN_GOOD_NAME + " = '" + itemName + "')", null, null, null, null);
            itemMaxPriceCursor.moveToFirst();
            itemMaxPrice = itemMaxPriceCursor.getLong(itemMaxPriceCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_PRICE));

            Cursor itemMinPriceCursor = db.query(DataContract.GoodEntry.TABLE_NAME, null, DataContract.GoodEntry.COLUMN_GOOD_PRICE + " =(SELECT MIN(" + DataContract.GoodEntry.COLUMN_GOOD_PRICE + ") FROM " + DataContract.GoodEntry.TABLE_NAME+ " WHERE " + DataContract.GoodEntry.COLUMN_GOOD_NAME + " = '" + itemName + "')", null, null, null, null);
            itemMinPriceCursor.moveToFirst();
            itemMinPrice = itemMinPriceCursor.getLong(itemMinPriceCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_PRICE));

            if (itemMaxPrice != itemMinPrice){
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm"); // the format of your date
                sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

                String purchaseItemMaxSelection = DataContract.PurchaseEntry.COLUMN_PURCHASE_ID + " = " + itemMaxPriceCursor.getString(itemMaxPriceCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_PURCHASE_ID_FK));
                Cursor purchaseItemMax = getContentResolver().query(DataContract.PurchaseEntry.CONTENT_URI,null, purchaseItemMaxSelection, null, null);
                purchaseItemMax.moveToFirst();
                tvItemMaxPrice.setText(n.format(itemMaxPrice/100.00));
                tvItemMaxPriceDate.setText(sdf.format(purchaseItemMax.getLong(purchaseItemMax.getColumnIndexOrThrow(DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME))*1000L));
                purchaseItemMax.close();
                itemMaxPriceCursor.close();

                String purchaseItemMinSelection = DataContract.PurchaseEntry.COLUMN_PURCHASE_ID + " = " + itemMinPriceCursor.getString(itemMinPriceCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_PURCHASE_ID_FK));
                Cursor purchaseItemMin = getContentResolver().query(DataContract.PurchaseEntry.CONTENT_URI,null, purchaseItemMinSelection, null, null);
                purchaseItemMin.moveToFirst();
                tvItemMinPrice.setText(n.format(itemMinPriceCursor.getLong(itemMinPriceCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_PRICE))/100.00));
                tvItemMinPriceDate.setText(sdf.format(purchaseItemMin.getLong(purchaseItemMin.getColumnIndexOrThrow(DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME))*1000L));
                purchaseItemMin.close();
                itemMinPriceCursor.close();

                tvItemPriceNoDifference.setVisibility(GONE);
            }
            else
            {
                llStats.setVisibility(GONE);
            }

        }
        else
        {
            tvItemPurchasesQuantity.setText("1 покупка");
            tvItemPurchasesSum.setText(n.format(itemCursor.getLong(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_SUM))/100.00));
            llStats.setVisibility(GONE);
            tvItemPriceNoDifference.setVisibility(GONE);
        }




        tvItemName.setText(itemCursor.getString(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NAME)));
        tvItemSum.setText(n.format(itemCursor.getLong(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_SUM)) / 100.0));
        tvItemPrice.setText(n.format(itemCursor.getLong(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_PRICE))/100.0));
        tvItemQuantity.setText(itemCursor.getString(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_QUANTITY)));

        if (itemCursor.getString(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NDS10)) != null){
            tvItemNds10.setText(n.format(itemCursor.getLong(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NDS10))/100.0));
        } else {
            tvItemNds10.setVisibility(GONE);
            tvItemNds10Label.setVisibility(GONE);
        }
        if (itemCursor.getString(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NDS18)) != null){
            tvItemNds18.setText(n.format(itemCursor.getLong(itemCursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NDS18))/100.0));
        } else {
            tvItemNds18.setVisibility(GONE);
            tvItemNds18Label.setVisibility(GONE);
        }
//        String[] itemProjection = {
//                DataContract.StoreEntry.COLUMN_STORE_NAME,
//                DataContract.StoreEntry.COLUMN_ST ORE_NICKNAME,
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
