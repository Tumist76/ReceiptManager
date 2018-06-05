package xyz.tumist.diploma.main_page;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import xyz.tumist.diploma.ItemActivity;
import xyz.tumist.diploma.ItemCursorAdapter;
import xyz.tumist.diploma.R;
import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;



public class ItemsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);

    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // TodoDatabaseHandler is a SQLiteOpenHelper class connecting to SQLite
        ReceiptsDBHelper handler = new ReceiptsDBHelper(getContext());
        // Get access to the underlying writeable database
        SQLiteDatabase db = handler.getWritableDatabase();
        // Query for items from the database and get a cursor back
        Cursor itemCursor = db.rawQuery("SELECT  " +
                        DataContract.GoodEntry.TABLE_NAME+"."+ DataContract.GoodEntry.COLUMN_GOOD_ID+", " +
                        DataContract.GoodEntry.COLUMN_GOOD_NAME + ", " +
                        DataContract.GoodEntry.COLUMN_GOOD_SUM + ", " +
                        DataContract.GoodEntry.COLUMN_GOOD_PRICE + ", " +
                        DataContract.GoodEntry.COLUMN_GOOD_QUANTITY + ", " +
                        DataContract.GoodEntry.COLUMN_GOOD_NDS10 + ", " +
                        DataContract.GoodEntry.COLUMN_GOOD_NDS18 + ", " +
                        DataContract.GoodEntry.COLUMN_GOOD_NDSNO + ", " +
                        DataContract.GoodEntry.COLUMN_GOOD_NDS0 + ", " +
                        DataContract.GoodEntry.COLUMN_GOOD_STORNO + ", " +
                        DataContract.GoodEntry.COLUMN_GOOD_PURCHASE_ID_FK + ", " +
                        DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME + " " +
                        "FROM " + DataContract.GoodEntry.TABLE_NAME + " INNER JOIN " + DataContract.PurchaseEntry.TABLE_NAME +
                        " ON " + DataContract.GoodEntry.TABLE_NAME + "." + DataContract.GoodEntry.COLUMN_GOOD_PURCHASE_ID_FK + " = " + DataContract.PurchaseEntry.TABLE_NAME + "." + DataContract.PurchaseEntry.COLUMN_PURCHASE_ID +
                        " ORDER BY "+ DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME + " DESC"
                , null, null);
        // Find ListView to populate
        ListView lvItems = (ListView) view.findViewById(R.id.fragment_listview);
        // Setup cursor adapter using cursor from last step
        ItemCursorAdapter itemAdapter = new ItemCursorAdapter(getContext(), itemCursor);
        // Attach cursor adapter to the ListView
        lvItems.setAdapter(itemAdapter);
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ItemActivity.class);
                Uri currentDebtUri = ContentUris.withAppendedId(DataContract.GoodEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentDebtUri);
                //intent.putExtra("id", debtID);
                startActivityForResult(intent, 10002);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10002)
        {
            // recreate your fragment here
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }
}
