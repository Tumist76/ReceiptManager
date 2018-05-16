package xyz.tumist.diploma.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReceiptsDBHelper extends SQLiteOpenHelper {
    //Тег для логов
    public static final String LOG_TAG = ReceiptsDBHelper.class.getSimpleName();
    //Название базы данных
    private static final String DATABASE_NAME = "checks.db";
    //Версия базы данных. При изменении схемы БД нужно увеличивать версию БД на 1.
    private static final int DATABASE_VERSION = 1;
    //Конструктор класса
    public ReceiptsDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Используется при первом создании базы данных
    @Override
    public void onCreate(SQLiteDatabase db){
        //Строки с SQL-командой для создания таблиц persons, debts и transactions
        String SQL_CREATE_GOODS_TABLE = "CREATE TABLE " + DataContract.GoodEntry.TABLE_NAME + " (" +
                DataContract.GoodEntry.COLUMN_GOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataContract.GoodEntry.COLUMN_GOOD_MODIFIERS + " TEXT, " +
                DataContract.GoodEntry.COLUMN_GOOD_NAME + " TEXT NOT NULL," +
                DataContract.GoodEntry.COLUMN_GOOD_NDS0 + " INTEGER, " +
                DataContract.GoodEntry.COLUMN_GOOD_QUANTITY + " INTEGER, " +
                DataContract.GoodEntry.COLUMN_GOOD_SUM + " INTEGER NOT NULL, " +
                DataContract.GoodEntry.COLUMN_GOOD_NDS10 + " INTEGER, " +
                DataContract.GoodEntry.COLUMN_GOOD_NDS18 + " INTEGER, " +
                DataContract.GoodEntry.COLUMN_GOOD_NDSCALCULATED10 + " INTEGER, " +
                DataContract.GoodEntry.COLUMN_GOOD_NDSCALCULATED18 + " INTEGER, " +
                DataContract.GoodEntry.COLUMN_GOOD_NDSNO + " INTEGER, " +
                DataContract.GoodEntry.COLUMN_GOOD_PRICE + " INTEGER NOT NULL, " +
                DataContract.GoodEntry.COLUMN_GOOD_STORNO + " TEXT," +
                DataContract.GoodEntry.COLUMN_GOOD_PURCHASE_ID_FK + " INTEGER, " +
                //Внешний ключ
                "FOREIGN KEY(" + DataContract.GoodEntry.COLUMN_GOOD_PURCHASE_ID_FK + ") REFERENCES " +
                DataContract.PurchaseEntry.TABLE_NAME + "(" + DataContract.PurchaseEntry.COLUMN_PURCHASE_ID + ")" +
                ");";

        String SQL_CREATE_PURCHASES_TABLE = "CREATE TABLE " + DataContract.PurchaseEntry.TABLE_NAME + " (" +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_FISCALDOCUMENTNUMBER + " TEXT NOT NULL, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_FISCALDRIVENUMBER + " TEXT NOT NULL, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_CASHTOTALSUM + " INTEGER NOT NULL," +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_ECASHTOTALSUM + " INTEGER NOT NULL, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_TOTALSUM + " INTEGER NOT NULL, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME + " INTEGER NOT NULL, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_DISCOUNT + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_DISCOUNTSUM + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_FISCALSIGN + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_ITEMSAMOUNT + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_OPERATOR + " TEXT, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_REQUESTNUMBER + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_SHIFTNUMBER + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_KKTREGID + " TEXT, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_KKTNUMBER + " TEXT, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_NDS0 + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_NDS10 + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_NDS18 + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_NDSCALCULATED10 + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_NDSCALCULATED18 + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_NDSNO + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_TAXATIONTYPE + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_OPERATIONTYPE + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_POINT_ID_FK + " INTEGER, " +
                DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK + " INTEGER, " +
                //Внешний ключ к точкам и теги
                "FOREIGN KEY(" + DataContract.PurchaseEntry.COLUMN_PURCHASE_POINT_ID_FK + ") REFERENCES " +
                DataContract.PointEntry.TABLE_NAME + "(" + DataContract.PointEntry.COLUMN_POINT_ID + ")," +
                "FOREIGN KEY(" + DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK + ") REFERENCES " +
                DataContract.StoreEntry.TABLE_NAME + "(" + DataContract.StoreEntry.COLUMN_STORE_ID + ")" +
                ");";

        String SQL_CREATE_POINTS_TABLE = "CREATE TABLE " + DataContract.PointEntry.TABLE_NAME + " (" +
                DataContract.PointEntry.COLUMN_POINT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataContract.PointEntry.COLUMN_POINT_ADDRESS + " TEXT, " +
                DataContract.PointEntry.COLUMN_POINT_NICKNAME + " TEXT, " +
                DataContract.PointEntry.COLUMN_STORE_ID_FK + " INTEGER, " +
                "FOREIGN KEY(" + DataContract.PointEntry.COLUMN_STORE_ID_FK + ") REFERENCES " +
                DataContract.StoreEntry.TABLE_NAME + "(" + DataContract.StoreEntry.COLUMN_STORE_ID + ")" +
                ");";

        String SQL_CREATE_STORES_TABLE = "CREATE TABLE " + DataContract.StoreEntry.TABLE_NAME + " (" +
                DataContract.StoreEntry.COLUMN_STORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataContract.StoreEntry.COLUMN_STORE_INN + " INTEGER NOT NULL, " +
                DataContract.StoreEntry.COLUMN_STORE_NAME + " TEXT , " +
                DataContract.StoreEntry.COLUMN_STORE_NICKNAME + " TEXT" +
                ");";


        Log.v(LOG_TAG, SQL_CREATE_GOODS_TABLE);
        Log.v(LOG_TAG, SQL_CREATE_PURCHASES_TABLE);
        Log.v(LOG_TAG, SQL_CREATE_POINTS_TABLE);
        Log.v(LOG_TAG, SQL_CREATE_STORES_TABLE);


        db.execSQL(SQL_CREATE_STORES_TABLE);
        db.execSQL(SQL_CREATE_POINTS_TABLE);
        db.execSQL(SQL_CREATE_PURCHASES_TABLE);
        db.execSQL(SQL_CREATE_GOODS_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
