package xyz.tumist.diploma.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

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
    }

    /** Дальше пойдёт громоздкий ужас по четырём основным методам взаимодействия с БД  */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Берём БД на чтение
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // Этот курсор после выполнения запроса будет получать и возвращать данные в точку вызова
        Cursor cursor = null;
        // Выясняется, подходит ли URI запроса под одну из таблиц
        int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                cursor = database.query(
                        DataContract.GoodEntry.TABLE_NAME,  // Таблица, к которой обращён запрос
                        projection,                               // Колонки, которые требуется вернуть
                        selection,                                // Колонки для WHERE
                        selectionArgs,                            // Значения для WHERE
                        null,                                     // Не группировать ряды
                        null,                                     // Не фильтровать сгруппированные ряды
                        sortOrder                                   // Порядок сортировки
                );
                break;
            case GOOD_ID:
                // Для конкретного идентификатора нужно извлечь этот ID из URI.
                // Мы ставим знак вопроса для WHERE. Количество знаков вопроса - это количество аргументов,
                // которые должны заполнить потом эти знаки вопроса.
                selection = DataContract.GoodEntry.COLUMN_GOOD_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(
                        DataContract.GoodEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PURCHASES:
                cursor = database.query(
                        DataContract.PurchaseEntry.TABLE_NAME,  // Таблица, к которой обращён запрос
                        projection,                               // Колонки, которые требуется вернуть
                        selection,                                // Колонки для WHERE
                        selectionArgs,                            // Значения для WHERE
                        null,                                     // Не группировать ряды
                        null,                                     // Не фильтровать сгруппированные ряды
                        sortOrder                                   // Порядок сортировки
                );
                break;
            case PURCHASE_ID:
                selection = DataContract.PurchaseEntry.COLUMN_PURCHASE_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(
                        DataContract.PurchaseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case POINTS:
                cursor = database.query(
                        DataContract.PointEntry.TABLE_NAME,  // Таблица, к которой обращён запрос
                        projection,                               // Колонки, которые требуется вернуть
                        selection,                                // Колонки для WHERE
                        selectionArgs,                            // Значения для WHERE
                        null,                                     // Не группировать ряды
                        null,                                     // Не фильтровать сгруппированные ряды
                        sortOrder                                   // Порядок сортировки
                );
                break;
            case POINT_ID:
                selection = DataContract.PointEntry.COLUMN_POINT_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(
                        DataContract.PointEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case STORES:
                cursor = database.query(
                        DataContract.StoreEntry.TABLE_NAME,  // Таблица, к которой обращён запрос
                        projection,                               // Колонки, которые требуется вернуть
                        selection,                                // Колонки для WHERE
                        selectionArgs,                            // Значения для WHERE
                        null,                                     // Не группировать ряды
                        null,                                     // Не фильтровать сгруппированные ряды
                        sortOrder                                   // Порядок сортировки
                );
                break;
            case STORE_ID:
                selection = DataContract.StoreEntry.COLUMN_STORE_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(
                        DataContract.StoreEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Невозможно запросить неизвестный URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        // Установить URI уведомлений на курсор для того,
        // чтобы мы знали, для какого URI контенты был создан курсор.
        // Если данные у этого URI изменятся, то мы узнаем, что нужно обновить курсор.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                return insertGood(uri, contentValues);
            case PURCHASES:
                return insertPurchase(uri, contentValues);
            case POINTS:
                return insertPoint(uri, contentValues);
            case STORES:
                return insertStore(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                return updateGood(uri, contentValues, selection, selectionArgs);
            case GOOD_ID:
                selection = DataContract.GoodEntry.COLUMN_GOOD_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateGood(uri, contentValues, selection, selectionArgs);
            case PURCHASES:
                return updatePurchase(uri, contentValues, selection, selectionArgs);
            case PURCHASE_ID:
                selection = DataContract.PurchaseEntry.COLUMN_PURCHASE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePurchase(uri, contentValues, selection, selectionArgs);
            case POINTS:
                return updatePoint(uri, contentValues, selection, selectionArgs);
            case POINT_ID:
                selection = DataContract.PointEntry.COLUMN_POINT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePoint(uri, contentValues, selection, selectionArgs);
            case STORES:
                return updateStore(uri, contentValues, selection, selectionArgs);
            case STORE_ID:
                selection = DataContract.StoreEntry.COLUMN_STORE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateStore(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                rowsDeleted = database.delete(DataContract.GoodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GOOD_ID:
                selection = DataContract.GoodEntry.COLUMN_GOOD_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(DataContract.GoodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PURCHASES:
                rowsDeleted = database.delete(DataContract.GoodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PURCHASE_ID:
                selection = DataContract.PurchaseEntry.COLUMN_PURCHASE_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(DataContract.PurchaseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POINTS:
                rowsDeleted = database.delete(DataContract.PointEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case POINT_ID:
                selection = DataContract.PointEntry.COLUMN_POINT_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(DataContract.PointEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STORES:
                rowsDeleted = database.delete(DataContract.StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STORE_ID:
                selection = DataContract.StoreEntry.COLUMN_STORE_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(DataContract.StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Удаление не поддерживается для " + uri);
        }
        // Уведомить listeners, если данные изменились
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Вернуть количество удалённых записей
        return rowsDeleted;
    }
    /** Возвращает MIME тип данных для URI контента **/
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                return DataContract.CONTENT_LIST_TYPE;
            case GOOD_ID:
                return DataContract.CONTENT_ITEM_TYPE;
            case PURCHASES:
                return DataContract.CONTENT_LIST_TYPE;
            case PURCHASE_ID:
                return DataContract.CONTENT_ITEM_TYPE;
            case POINTS:
                return DataContract.CONTENT_LIST_TYPE;
            case POINT_ID:
                return DataContract.CONTENT_ITEM_TYPE;
            case STORES:
                return DataContract.CONTENT_LIST_TYPE;
            case STORE_ID:
                return DataContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
    /** Методы добавления записей **/
    private Uri insertGood(Uri uri, ContentValues values) {
        Log.v(LOG_TAG, "Метод вставки товара");
        // Получить базу данных на запись
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Вставить запись о товаре с полученными значениями
        long id = database.insert(DataContract.GoodEntry.TABLE_NAME, null, values);
        // Если ID = -1, то записать в лог сообщение об ошибке
        if (id == -1) {
            Log.e(LOG_TAG, "Вставка новой строки провалена " + uri);
            return null;
        } else {
            Log.v(LOG_TAG, "Товар вставлен с ID=" + id);
        }
        // Уведомить listeners, что данные изменились
        getContext().getContentResolver().notifyChange(uri, null);
        // Вернуть новый URI с ID записи, которая была добавлена
        return ContentUris.withAppendedId(uri, id);
    }
    private Uri insertPurchase(Uri uri, ContentValues values) {
        Log.v(LOG_TAG, "Метод вставки покупки");
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(DataContract.PurchaseEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Вставка новой строки провалена " + uri);
            return null;
        }
        else {
            Log.v(LOG_TAG, "Покупка вставлена с ID=" + id);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    private Uri insertPoint(Uri uri, ContentValues values) {
        Log.v(LOG_TAG, "Метод вставки точки продаж");
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(DataContract.PointEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Вставка новой строки провалена " + uri);
            return null;
        }
        else {
            Log.v(LOG_TAG, "Точка продаж вставлена с ID=" + id);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    private Uri insertStore(Uri uri, ContentValues values) {
        Log.v(LOG_TAG, "Метод вставки магазина");
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(DataContract.StoreEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Вставка новой строки провалена " + uri);
            return null;
        }else {
            Log.v(LOG_TAG, "Магазин вставлен с ID=" + id);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    /** Методы обновления записей **/
    private int updateGood(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Если нет значений на обновление, ничего не делать
        if (values.size() == 0) {
            return 0;
        }
        // Иначе, получить БД на запись
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Выполнить обновление БД
        int rowsUpdated = database.update(DataContract.GoodEntry.TABLE_NAME, values, selection, selectionArgs);
        // Уведомить listeners, что данные изменились
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Вернуть количество обновлеённых строк
        return rowsUpdated;
    }
    private int updatePurchase(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(DataContract.PurchaseEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
    private int updatePoint(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(DataContract.PointEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
    private int updateStore(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(DataContract.StoreEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
