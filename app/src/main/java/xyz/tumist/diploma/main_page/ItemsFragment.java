package xyz.tumist.diploma.main_page;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        Cursor itemCursor = db.rawQuery("SELECT  * FROM " + DataContract.GoodEntry.TABLE_NAME + " NATURAL JOIN " + DataContract.PurchaseEntry.TABLE_NAME +
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
                startActivity(intent);
            }
        });
    }
}
