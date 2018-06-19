package xyz.tumist.diploma.main_page;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import xyz.tumist.diploma.CurrencyConverter;
import xyz.tumist.diploma.R;
import xyz.tumist.diploma.StoreActivity;
import xyz.tumist.diploma.StoreCursorAdapter;
import xyz.tumist.diploma.StoreStatsCursorAdapter;
import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;


public class StatsFragment extends Fragment {
    ListView lvStoreStats;
    TextView tvThisMonthQuantity, tvThisMonthSum, tvLastMonthQuantity, tvLastMonthSum;
    CardView cvThisMonth, cvLastMonth;
    LinearLayout emptyLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        lvStoreStats = view.findViewById(R.id.store_stats_list_view);
        tvThisMonthQuantity = view.findViewById(R.id.item_card_view_this_month_purchases_quantity);
        tvThisMonthSum = view.findViewById(R.id.item_card_view_this_month_purchases_sum);
        tvLastMonthQuantity = view.findViewById(R.id.item_card_view_last_month_purchases_quantity);
        tvLastMonthSum = view.findViewById(R.id.item_card_view_last_month_purchases_sum);
        cvThisMonth = view.findViewById(R.id.card_view_this_month_purchases);
        cvLastMonth = view.findViewById(R.id.card_view_last_month_purchases);
        emptyLayout = view.findViewById(R.id.stats_empty_layout);
        Cursor allPurchases = getActivity().getContentResolver().query(DataContract.PurchaseEntry.CONTENT_URI, null, null, null, null);
        if (!allPurchases.moveToFirst()){
            cvLastMonth.setVisibility(View.GONE);
            cvThisMonth.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            ReceiptsDBHelper handler = new ReceiptsDBHelper(getContext());
            SQLiteDatabase db = handler.getWritableDatabase();
            String sumClause = "SUM(" + DataContract.PurchaseEntry.COLUMN_PURCHASE_TOTALSUM + ")";
            // Query for items from the database and get a cursor back
            //Получаем и ставим данные за последний месяц
            Cursor thisMonthPurchasesSumCursor = db.rawQuery("SELECT " + sumClause + " FROM " + DataContract.PurchaseEntry.TABLE_NAME +
                    " WHERE " + DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME + " > " + getFirstDateOfCurrentMonth() + "; ", null);
            Cursor thisMonthPurchaseQuantityCursor = db.rawQuery("SELECT * FROM " + DataContract.PurchaseEntry.TABLE_NAME +
                    " WHERE " + DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME + " > " + getFirstDateOfCurrentMonth() + "; ", null);
            if (thisMonthPurchaseQuantityCursor.moveToFirst()) {
                thisMonthPurchasesSumCursor.moveToFirst();
                CurrencyConverter CC = new CurrencyConverter();
                tvThisMonthSum.setText(CC.getConvertedFormattedCurrency(thisMonthPurchasesSumCursor.getLong(0)));
                tvThisMonthQuantity.setText(getFormattedQuantity(thisMonthPurchaseQuantityCursor.getCount()) + " за этот месяц");
                thisMonthPurchaseQuantityCursor.close();
                thisMonthPurchasesSumCursor.close();
            } else {
                cvThisMonth.setVisibility(View.GONE);
            }
            //А теперь за прошлый
            Cursor lastMonthPurchasesSumCursor = db.rawQuery("SELECT " + sumClause + " FROM " + DataContract.PurchaseEntry.TABLE_NAME +
                    " WHERE " + DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME + " > " + getFirstDateOfLastMonth() +
                    " AND " + DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME + " < " + getFirstDateOfCurrentMonth() + "; ", null);
            Cursor lastMonthPurchaseQuantityCursor = db.rawQuery("SELECT * FROM " + DataContract.PurchaseEntry.TABLE_NAME +
                    " WHERE " + DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME + " > " + getFirstDateOfLastMonth() +
                    " AND " + DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME + " < " + getFirstDateOfCurrentMonth() + "; ", null);
            if (lastMonthPurchaseQuantityCursor.moveToFirst()) {
                lastMonthPurchasesSumCursor.moveToFirst();
                CurrencyConverter CC = new CurrencyConverter();
                tvLastMonthSum.setText(CC.getConvertedFormattedCurrency(lastMonthPurchasesSumCursor.getLong(0)));
                tvLastMonthQuantity.setText(getFormattedQuantity(lastMonthPurchaseQuantityCursor.getCount()) + " за предыдущий месяц");
                lastMonthPurchaseQuantityCursor.close();
                lastMonthPurchasesSumCursor.close();
            } else {
                cvLastMonth.setVisibility(View.GONE);
            }

            Cursor thisMonthStoreStatsCursor = db.rawQuery("SELECT " + DataContract.PurchaseEntry.COLUMN_PURCHASE_ID + ", " + sumClause + ", " + DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK +
                    " FROM " + DataContract.PurchaseEntry.TABLE_NAME +
                    " WHERE " + DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME + " > " + getFirstDateOfCurrentMonth() + " GROUP BY " + DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK +
                    " ORDER BY " + sumClause + " DESC LIMIT 3;", null);

            StoreStatsCursorAdapter storeStatsAdapter = new StoreStatsCursorAdapter(getContext(), thisMonthStoreStatsCursor);
            // Attach cursor adapter to the ListView
            lvStoreStats.setAdapter(storeStatsAdapter);

            lvStoreStats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(getContext(), StoreActivity.class);
                    String currentPurchaseSelection = DataContract.PurchaseEntry.COLUMN_PURCHASE_ID + " = " + id;
                    Cursor currentPurchaseCursor = getActivity().getContentResolver().query(DataContract.PurchaseEntry.CONTENT_URI, null, currentPurchaseSelection, null, null);
                    currentPurchaseCursor.moveToFirst();
                    Uri currentStoreUri = ContentUris.withAppendedId(DataContract.StoreEntry.CONTENT_URI, currentPurchaseCursor.getLong(currentPurchaseCursor.getColumnIndex(DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK)));
                    // Set the URI on the data field of the intent
                    intent.setData(currentStoreUri);
                    //intent.putExtra("id", debtID);
                    getActivity().startActivityForResult(intent, 10004);
                }
            });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10003)
        {
            // recreate your fragment here
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }
    private long getFirstDateOfCurrentMonth() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of the month
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Log.v("Epoch firstday","Start of the month:       " + cal.getTimeInMillis()/1000);
        return cal.getTimeInMillis()/1000L;
    }
    private long getFirstDateOfLastMonth() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of the month
        cal.set(Calendar.DAY_OF_MONTH, 1);

        Log.v("Epoch firstday of last","Start of the month:       " + String.valueOf(cal.getTimeInMillis()/1000-2629743L));
        return cal.getTimeInMillis()/1000L-2629743L;
    }
    private String getFormattedQuantity(int count){
        if (count % 10 == 1) return count + " покупка";
        else if (count % 10 == 2) return count + " покупки";
        else if (count % 10 == 3) return count + " покупки";
        else if (count % 10 == 4) return count + " покупки";
        else return count + " покупок";
    }
}
