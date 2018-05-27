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

public class PurchasePointCursorAdapter extends CursorAdapter {
    private ReceiptsDBHelper mDbHelper;

    public PurchasePointCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.purchases_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Открываем dbHelper
        ReceiptsDBHelper receiptsDBHelper = new ReceiptsDBHelper(context);
        SQLiteDatabase database = receiptsDBHelper.getReadableDatabase();
        // Find fields to populate in inflated template
        TextView purchaseItemAmount = (TextView) view.findViewById(R.id.purchases_list_item_store_name);
        TextView purchaseDateTime = (TextView) view.findViewById(R.id.purchases_list_item_dateTime);
        TextView purchaseTotalSum = (TextView) view.findViewById(R.id.purchases_list_item_totalSum);
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
//        String storeSelection = DataContract.StoreEntry.COLUMN_STORE_ID + " LIKE ?";
//        String[] storeSelectionArgs = {cursor.getString(cursor.getColumnIndexOrThrow(DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK))};
//        Cursor storeCursor = database.query(DataContract.StoreEntry.TABLE_NAME,
//                storeProjection,
//                storeSelection,
//                storeSelectionArgs,
//                null,
//                null,
//                null
//        );
//        storeCursor.moveToNext();
//        String storeName = null;
//        if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME)) != null){
//            storeName = storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NICKNAME));
//        }
//        else if (storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) != null){
//            storeName = storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME));
//        }
//        else storeName = "ИНН " + storeCursor.getString(storeCursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_INN));

        /** А тут мы делаем новую локаль, потому что её нет по умолчанию. А нам нужен символ рубля при выводе суммы без лишней мороки.
         *  А еще форматируем дату. А так как в чеке не указывается часовой пояс, со стандартным барнаульским сдвигом получается неправильное время.
         *  Убираем сдвиг, выставляя гринвич
         */
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formattedDate = sdf.format(cursor.getLong(cursor.getColumnIndexOrThrow(DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME))*1000L);

        Locale myLocale = new Locale("ru","RU");
        NumberFormat n = NumberFormat.getCurrencyInstance(myLocale);
        String formatedTotalSum = n.format(cursor.getLong(cursor.getColumnIndex(DataContract.PurchaseEntry.COLUMN_PURCHASE_TOTALSUM)) / 100.0);
        /** Делаем целую часть суммы жирной **/
        SpannableStringBuilder str = new SpannableStringBuilder(formatedTotalSum);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, formatedTotalSum.length()-4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        purchaseTotalSum.setText(str);


        long purchaseItemCount = receiptsDBHelper.getItemsCount(DataContract.GoodEntry.COLUMN_GOOD_PURCHASE_ID_FK + " LIKE " + cursor.getLong(cursor.getColumnIndex(DataContract.PurchaseEntry.COLUMN_PURCHASE_ID)), null);
        purchaseDateTime.setText(formattedDate);
        if (purchaseItemCount % 10 == 1) purchaseItemAmount.setText(purchaseItemCount + " позиция");
        else if (purchaseItemCount % 10 == 2) purchaseItemAmount.setText(purchaseItemCount + " позиции");
        else if (purchaseItemCount % 10 == 3) purchaseItemAmount.setText(purchaseItemCount + " позиции");
        else if (purchaseItemCount % 10 == 4) purchaseItemAmount.setText(purchaseItemCount + " позиции");
        else purchaseItemAmount.setText(purchaseItemCount + " позиций");
    }
}
