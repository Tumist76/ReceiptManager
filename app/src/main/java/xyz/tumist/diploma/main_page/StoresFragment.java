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

import xyz.tumist.diploma.R;
import xyz.tumist.diploma.StoreActivity;
import xyz.tumist.diploma.StoreCursorAdapter;
import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;



public class StoresFragment extends Fragment {
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
        Cursor storeCursor = db.rawQuery("SELECT  * FROM " + DataContract.StoreEntry.TABLE_NAME, null);
        // Find ListView to populate
        ListView lvStores = (ListView) view.findViewById(R.id.fragment_listview);
        // Setup cursor adapter using cursor from last step
        StoreCursorAdapter storeAdapter = new StoreCursorAdapter(getContext(), storeCursor);
        // Attach cursor adapter to the ListView
        lvStores.setAdapter(storeAdapter);

        lvStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getContext(), StoreActivity.class);
                Uri currentStoreUri = ContentUris.withAppendedId(DataContract.StoreEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentStoreUri);
                //intent.putExtra("id", debtID);
                startActivity(intent);
            }
        });
    }
}
