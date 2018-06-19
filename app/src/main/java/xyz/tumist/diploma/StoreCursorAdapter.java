package xyz.tumist.diploma;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;

public class StoreCursorAdapter extends CursorAdapter {
    private ReceiptsDBHelper mDbHelper;

    public StoreCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.stores_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Открываем dbHelper
        ReceiptsDBHelper receiptsDBHelper = new ReceiptsDBHelper(context);
        SQLiteDatabase database = receiptsDBHelper.getReadableDatabase();
        // Find fields to populate in inflated template
        TextView tvstoreName = (TextView) view.findViewById(R.id.stores_list_item_store_name);
        TextView tvstoreSecondName = (TextView) view.findViewById(R.id.stores_list_item_store_second_name);
        TextView tvstoreAmount = (TextView) view.findViewById(R.id.stores_list_item_Amount);
        TextView tvstoreWord = (TextView) view.findViewById(R.id.stores_list_item_word_purchase);
        // Extract properties from cursor
        //ReceiptsProvider recProv = new ReceiptsProvider();
        /** Чтобы вытащить название магазина, предстоит долгий путь:
         * по внешнему ключу точки, который лежит в курсоре покупки получить курсор точки.
         * По внешнему ключу магазина, который лежит в курсоре точки получить три поля:
         * никнейм магазина, название магазина и инн.
         * Если есть никнейм, отобразить его. Если его нет, отобразить название. Если нет названия, отобразить ИНН.
         */

        String storeName = null;
        String storeSecondName = null;
        int purchasesCount = 0;
        if (cursor.getString(cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)) != null){
            storeName = cursor.getString(cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME));
            if (cursor.getString(cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null) {
                storeSecondName = cursor.getString(cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME));
            } else
            {
                storeSecondName = "ИНН " + cursor.getString(cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN));
            }
        }
        else if (cursor.getString(cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null){
            storeName = cursor.getString(cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME));
            storeSecondName = "ИНН " + cursor.getString(cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN));
        }
        else storeName = "ИНН " + cursor.getString(cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN));

        String[] purchaseProjection = {
                DataContract.PurchaseEntry.COLUMN_PURCHASE_ID
        };
        String purchaseSelection = DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK + " LIKE ?";
        String[] purchaseSelectionArgs = {cursor.getString(cursor.getColumnIndexOrThrow(DataContract.StoreEntry.COLUMN_STORE_ID))};
        Cursor purchaseCursor = database.query(DataContract.PurchaseEntry.TABLE_NAME,
                purchaseProjection,
                purchaseSelection,
                purchaseSelectionArgs,
                null,
                null,
                null
        );
        purchasesCount = purchaseCursor.getCount();
        Log.v("storeCursor.getCount()", String.valueOf(purchaseCursor.getCount()));

        tvstoreName.setText(storeName);
        tvstoreSecondName.setText(storeSecondName);
        tvstoreAmount.setText(String.valueOf(purchasesCount));
        if (purchasesCount % 10 == 1) tvstoreWord.setText("покупка");
        else if (purchasesCount % 10 == 2) tvstoreWord.setText("покупки");
        else if (purchasesCount % 10 == 3) tvstoreWord.setText("покупки");
        else if (purchasesCount % 10 == 4) tvstoreWord.setText("покупки");
        else tvstoreWord.setText("покупок");

    }
}
