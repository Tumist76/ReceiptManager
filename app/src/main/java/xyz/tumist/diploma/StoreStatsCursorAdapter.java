package xyz.tumist.diploma;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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

public class StoreStatsCursorAdapter extends CursorAdapter {
    private ReceiptsDBHelper mDbHelper;

    public StoreStatsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.store_stats_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Открываем dbHelper
        ReceiptsDBHelper receiptsDBHelper = new ReceiptsDBHelper(context);
        SQLiteDatabase database = receiptsDBHelper.getReadableDatabase();
        // Find fields to populate in inflated template
        TextView tvStoreNumber = (TextView) view.findViewById(R.id.store_stats_number);
        TextView tvStoreName = (TextView) view.findViewById(R.id.store_stats_name);
        TextView tvStoreSum = (TextView) view.findViewById(R.id.store_stats_amount);
        // Extract properties from cursor
        //ReceiptsProvider recProv = new ReceiptsProvider();
        /** Чтобы вытащить название магазина, предстоит долгий путь:
         * по внешнему ключу точки, который лежит в курсоре покупки получить курсор точки.
         * По внешнему ключу магазина, который лежит в курсоре точки получить три поля:
         * никнейм магазина, название магазина и инн.
         * Если есть никнейм, отобразить его. Если его нет, отобразить название. Если нет названия, отобразить ИНН.
         */
//        String[] pointProjection = {
//                DataContract.PointEntry.COLUMN_STORE_ID_FK
//        };
//        String pointSelection = DataContract.PointEntry.COLUMN_POINT_ID + " LIKE ?";
//        String[] pointSelectionArgs = {cursor.getString(cursor.getColumnIndexOrThrow(DataContract.PurchaseEntry.COLUMN_PURCHASE_POINT_ID_FK))};
//        Cursor pointCursor = database.query(
//                DataContract.PointEntry.TABLE_NAME,
//                pointProjection,
//                pointSelection,
//                pointSelectionArgs,
//                null,
//                null,
//                null
//        );
//        pointCursor.moveToNext();
//        String[] storeProjection = {
//                DataContract.StoreEntry.COLUMN_STORE_NAME,
//                DataContract.StoreEntry.COLUMN_STORE_NICKNAME,
//                DataContract.StoreEntry.COLUMN_STORE_INN
//        };
        String storeSelection = DataContract.StoreEntry.COLUMN_STORE_ID + " LIKE ?";
        String[] storeSelectionArgs = {cursor.getString(cursor.getColumnIndexOrThrow(DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK))};
        Cursor storeCursor = database.query(DataContract.StoreEntry.TABLE_NAME,
                null,
                storeSelection,
                storeSelectionArgs,
                null,
                null,
                null
        );
        storeCursor.moveToNext();
        String storeName = null;
        if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)) != null){
            storeName = storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME));
        }
        else if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null){
            storeName = storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME));
        }
        else storeName = "ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN));
        tvStoreNumber.setText(String.valueOf(cursor.getPosition() + 1));
        tvStoreName.setText(storeName);
        CurrencyConverter CC = new CurrencyConverter();
        tvStoreSum.setText(CC.getConvertedFormattedCurrency(cursor.getLong(1)));
    }
}
