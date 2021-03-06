package xyz.tumist.diploma;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;

public class ItemCursorAdapter extends CursorAdapter {
    private ReceiptsDBHelper mDbHelper;

    public ItemCursorAdapter(Context context, Cursor cursor) {

        super(context, cursor, 0);
        final String LOG_TAG = ItemCursorAdapter.class.getSimpleName();
        Log.v(LOG_TAG, "ItemCursorAdapter инициализирован");
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.item_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String LOG_TAG = ItemCursorAdapter.class.getSimpleName();
        Log.v(LOG_TAG, "вызван BindView");
        //Открываем dbHelper
        // Find fields to populate in inflated template
        TextView tvItemName = (TextView) view.findViewById(R.id.items_list_item_name);
        TextView tvItemAmount = (TextView) view.findViewById(R.id.items_list_item_amount);
        TextView tvItemPrice = (TextView) view.findViewById(R.id.items_list_item_price);
        TextView tvItemTotalSum = (TextView) view.findViewById(R.id.items_list_item_totalSum);
        // Extract properties from cursor
        //ReceiptsProvider recProv = new ReceiptsProvider();
        /** Чтобы вытащить название магазина, предстоит долгий путь:
         * по внешнему ключу точки, который лежит в курсоре покупки получить курсор точки.
         * По внешнему ключу магазина, который лежит в курсоре точки получить три поля:
         * никнейм магазина, название магазина и инн.
         * Если есть никнейм, отобразить его. Если его нет, отобразить название. Если нет названия, отобразить ИНН.
         */
        //cursor.moveToNext();
        String itemName = cursor.getString(cursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_NAME));
        Log.v(LOG_TAG, itemName);
        String itemAmount = cursor.getString(cursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_QUANTITY));
        if (itemAmount.contains(".")){
            itemAmount +=" кг";
        } else {
            int itemIntAmount = cursor.getInt(cursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_QUANTITY));
            if (itemIntAmount % 10 == 1) itemAmount += " штука";
            else if (itemIntAmount % 10 == 2) itemAmount += " штуки";
            else if (itemIntAmount % 10 == 3) itemAmount += " штуки";
            else if (itemIntAmount % 10 == 4) itemAmount += " штуки";
            else itemAmount += " штук";
        }
        Locale myLocale = new Locale("ru","RU");
        NumberFormat n = NumberFormat.getCurrencyInstance(myLocale);
        String formatedPrice = n.format(cursor.getLong(cursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_PRICE)) / 100.0);
        String formatedSum = n.format(cursor.getLong(cursor.getColumnIndex(DataContract.GoodEntry.COLUMN_GOOD_SUM)) / 100.0);
       /** Делаем целую часть суммы жирной **/
        SpannableStringBuilder str = new SpannableStringBuilder(formatedSum);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, formatedSum.length()-4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvItemTotalSum.setText(str);

        tvItemName.setText(itemName);
        tvItemAmount.setText(itemAmount);
        tvItemPrice.setText(formatedPrice);
        //tvItemTotalSum.setText(formatedSum);
    }
}
