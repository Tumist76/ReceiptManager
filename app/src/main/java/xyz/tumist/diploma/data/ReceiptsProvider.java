package xyz.tumist.diploma.data;

import android.content.ContentProvider;
import android.content.UriMatcher;
import android.net.Uri;

public class ReceiptsProvider extends ContentProvider {

    /**
     * Тег для сообщений в логе
     */
    public static final String LOG_TAG = ReceiptsProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */
    /**
     * Database helper object
     */

    private ReceiptsDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ReceiptsDBHelper(getContext());
        return true;
    }

    /** Код URI матчера для самих таблиц */
    private static final int GOODS = 100;
    private static final int PURCHASES = 200;
    private static final int POINTS = 300;
    private static final int STORES = 400;
    /** Код URI сопоставителя для отдельной записи в таблице */
    private static final int GOOD_ID = 101;
    private static final int PURCHASE_ID = 201;
    private static final int POINT_ID = 301;
    private static final int STORE_ID = 401;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    // Static инициализатор. Запускается первым, когда что-то вызывается из этого класса
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_GOODS, GOODS);
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_GOODS + "/#", GOOD_ID);
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_PURCHASES, PURCHASES);
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_PURCHASES + "/#", PURCHASE_ID);
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_POINTS, POINTS);
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_POINTS + "/#", POINT_ID);
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_STORES, STORES);
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_STORES + "/#", STORE_ID);
        // TODO: Add 2 content URIs to URI matcher
    }

    /** Дальше пойдёт громоздкий ужас по четырём основным методам взаимодействия с БД  */

    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DEBTS:
                return DataContract.CONTENT_LIST_TYPE;
            case DEBT_ID:
                return DataContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
}
