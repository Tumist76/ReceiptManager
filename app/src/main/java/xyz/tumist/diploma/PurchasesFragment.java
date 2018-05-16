package xyz.tumist.diploma;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PurchasesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PurchasesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PurchasesFragment extends Fragment {
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
        Cursor purchaseCursor = db.rawQuery("SELECT  * FROM " + DataContract.PurchaseEntry.TABLE_NAME, null);
        // Find ListView to populate
        ListView lvPurchases = (ListView) view.findViewById(R.id.fragment_list);
// Setup cursor adapter using cursor from last step
        PurchaseCursorAdapter purchaseAdapter = new PurchaseCursorAdapter(getContext(), purchaseCursor);
// Attach cursor adapter to the ListView
        lvPurchases.setAdapter(purchaseAdapter);
    }
}
