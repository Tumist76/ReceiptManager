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

import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;

public class PointCursorAdapter extends CursorAdapter {
    public PointCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.points_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Открываем dbHelper
        ReceiptsDBHelper handler = new ReceiptsDBHelper(context);
        // Find fields to populate in inflated template
        TextView tvPointName = (TextView) view.findViewById(R.id.point_list_item_name);
        TextView tvPointAmount = (TextView) view.findViewById(R.id.point_list_item_amount);
        TextView tvPointWord = (TextView) view.findViewById(R.id.point_list_item_word_purchase);
        //cursor.moveToFirst();
        long pointID = cursor.getLong(cursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_ID));
        int purchasesCount;
        if (cursor.getString(cursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_NICKNAME)) != null){
            tvPointName.setText(cursor.getString(cursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_NICKNAME)));
        }
        else if (cursor.getString(cursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_ADDRESS)) != null){
            tvPointName.setText(cursor.getString(cursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_ADDRESS)));
        }
        else tvPointName.setText("Точка без адреса");

        long purchasesPointCount = handler.getPurchasesCount(
                DataContract.PurchaseEntry.COLUMN_PURCHASE_POINT_ID_FK + " LIKE " + pointID,
                null);
        Log.v("purchasesPointCount", String.valueOf(purchasesPointCount));
        tvPointAmount.setText(String.valueOf(purchasesPointCount));
        if (purchasesPointCount % 10 == 1) tvPointWord.setText("покупка");
        else if (purchasesPointCount % 10 == 2) tvPointWord.setText("покупки");
        else if (purchasesPointCount % 10 == 3) tvPointWord.setText("покупки");
        else if (purchasesPointCount % 10 == 4) tvPointWord.setText("покупки");
        else tvPointWord.setText("покупок");

    }
}
