package xyz.tumist.diploma;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

import xyz.tumist.diploma.data.DataContract;
import xyz.tumist.diploma.data.ReceiptsDBHelper;

public class jsonParser extends AppCompatActivity {
    Purchase purchase;
    Gson gson = new Gson();
    public static final String LOG_TAG = jsonParser.class.getSimpleName();
    Uri receivedUri;
    public Long StoreID;
    public Long PointID;
    public Long PurchaseID;
    private ReceiptsDBHelper mDbHelper;
    public String receiptJSON;
    //public Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDbHelper = new ReceiptsDBHelper(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_parser);
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        //Проверям, есть ли права на хранилище
        isStoragePermissionGranted();
        Log.v("Тип ", type);
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            //получаем сслыку на файл в URI
            receivedUri = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
        }
        try {
            //Открываем буферное чтение
            BufferedReader bufferedReader = new BufferedReader(new FileReader(receivedUri.getPath()));
            //Переводим JSON-файл в GSON-объект, ориентируясь на класс
            purchase = gson.fromJson(bufferedReader, Purchase.class);
            Log.v(LOG_TAG, "Активирован считывающий Штайнер");
            Log.v(LOG_TAG, purchase.operator);
            Log.v(LOG_TAG, purchase.items[0].name);
            Log.v(LOG_TAG, String.valueOf(purchase.nds0));
            //Вызываем методы по порядку
            storeCheck();
            pointCheck();
            purchaseCheck();
            //itemsAdd();
            //jsonReader.close();
        }
        catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Невозможно открыть файл", Toast.LENGTH_SHORT).show();
        }

}
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(LOG_TAG,"Permission is granted");
                return true;
            } else {

                Log.v(LOG_TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(LOG_TAG,"Permission is granted");
            return true;
        }
    }

    public void storeCheck() {
        //Проверка на наличие в БД магазина из GSON-объекта.
        //Если магазина нет, то он добавляется в БД
        Cursor cursor;
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String[] projection = {
                DataContract.StoreEntry.COLUMN_STORE_ID,
                DataContract.StoreEntry.COLUMN_STORE_NAME
        };
        String selection = DataContract.StoreEntry.COLUMN_STORE_INN + " LIKE ?";
        String[] selectionArgs = {String.valueOf(purchase.userInn)};
        cursor = database.query(
                DataContract.StoreEntry.TABLE_NAME,  // Таблица, к которой обращён запрос
                projection,                               // Колонки, которые требуется вернуть
                selection,                                // Колонки для WHERE
                selectionArgs,                            // Значения для WHERE
                null,                                     // Не группировать ряды
                null,                                     // Не фильтровать сгруппированные ряды
                null);                                   // Порядок сортировки
        if (cursor.getCount() == 0) {
            SQLiteDatabase dbWrite = mDbHelper.getWritableDatabase();
            ContentValues storeValues = new ContentValues();
            storeValues.put(DataContract.StoreEntry.COLUMN_STORE_INN, purchase.userInn);
            if (purchase.user != null) {
                storeValues.put(DataContract.StoreEntry.COLUMN_STORE_NAME, purchase.user);
            }
            Uri insertedDebtUri = getContentResolver().insert(DataContract.StoreEntry.CONTENT_URI, storeValues);
            StoreID = ContentUris.parseId(insertedDebtUri);
        } else {
            cursor.moveToFirst();
            int storeIdColumnIndex = cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_ID);
            StoreID = cursor.getLong(storeIdColumnIndex);
            if (cursor.getString(cursor.getColumnIndex(DataContract.StoreEntry.COLUMN_STORE_NAME)) == null) {
                if (purchase.user != null){
                    ContentValues storeValues = new ContentValues();
                    storeValues.put(DataContract.StoreEntry.COLUMN_STORE_NAME, purchase.user);
                    int numOfUpdatedRows = getContentResolver().update(DataContract.StoreEntry.CONTENT_URI, storeValues,
                            DataContract.StoreEntry.COLUMN_STORE_ID + " LIKE " + StoreID, null);
                }
            }
        }
        cursor.close();
    }

        public void pointCheck() {
            //Проверка на наличие в БД точки продаж из GSON-объекта.
            //Если точки нет, то она добавляется в БД
            if (purchase.retailPlaceAddress == null){
                //Если нет адреса точки, то она добавляется без адреса
                long noAddressPointCount = mDbHelper.getPointsCount(
                        DataContract.PointEntry.COLUMN_POINT_ADDRESS + " IS NULL AND " + DataContract.PointEntry.COLUMN_STORE_ID_FK + " LIKE " + StoreID,
                        null);
                if (noAddressPointCount != 0) return;
            }
            Cursor cursor;
            SQLiteDatabase database = mDbHelper.getReadableDatabase();
            String[] projection = {
                    DataContract.PointEntry.COLUMN_POINT_ID,
                    DataContract.PointEntry.COLUMN_POINT_ADDRESS,
                    DataContract.PointEntry.COLUMN_STORE_ID_FK
            };
            String selection = DataContract.PointEntry.COLUMN_POINT_ADDRESS + " LIKE ?" + " AND " + DataContract.PointEntry.COLUMN_STORE_ID_FK + " LIKE ?";
            String[] selectionArgs = {String.valueOf(purchase.retailPlaceAddress), String.valueOf(StoreID)};
            cursor = database.query(
                    DataContract.PointEntry.TABLE_NAME,  // Таблица, к которой обращён запрос
                    projection,                               // Колонки, которые требуется вернуть
                    selection,                                // Колонки для WHERE
                    selectionArgs,                            // Значения для WHERE
                    null,                                     // Не группировать ряды
                    null,                                     // Не фильтровать сгруппированные ряды
                    null);                                   // Порядок сортировки
            if (cursor.getCount() == 0){
                SQLiteDatabase dbWrite = mDbHelper.getWritableDatabase();
                ContentValues pointValues = new ContentValues();
                //pointValues.put(DataContract.StoreEntry.COLUMN_STORE_INN, purchase.userInn);
                if (purchase.retailPlaceAddress != null)
                {
                    pointValues.put(DataContract.PointEntry.COLUMN_POINT_ADDRESS, purchase.retailPlaceAddress);
                }
                pointValues.put(DataContract.PointEntry.COLUMN_STORE_ID_FK, StoreID);
                Uri insertedDebtUri = getContentResolver().insert(DataContract.PointEntry.CONTENT_URI, pointValues);
                PointID = ContentUris.parseId(insertedDebtUri);
            }
            else {
                cursor.moveToNext();
                int pointIdColumnIndex = cursor.getColumnIndex(DataContract.PointEntry.COLUMN_POINT_ID);
                PointID = cursor.getLong(pointIdColumnIndex);
            }
            cursor.close();
    }
    public void purchaseCheck() {
        Cursor cursor;
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String[] projection = {
                DataContract.PurchaseEntry.COLUMN_PURCHASE_FISCALDOCUMENTNUMBER,
                DataContract.PurchaseEntry.COLUMN_PURCHASE_FISCALDRIVENUMBER
        };
        String selection = DataContract.PurchaseEntry.COLUMN_PURCHASE_FISCALDOCUMENTNUMBER + " LIKE ?" + " AND " + DataContract.PurchaseEntry.COLUMN_PURCHASE_FISCALDRIVENUMBER + " LIKE ?";
        String[] selectionArgs = {String.valueOf(purchase.fiscalDocumentNumber), String.valueOf(purchase.fiscalDriveNumber)};
        cursor = database.query(
                DataContract.PurchaseEntry.TABLE_NAME,  // Таблица, к которой обращён запрос
                projection,                               // Колонки, которые требуется вернуть
                selection,                                // Колонки для WHERE
                selectionArgs,                            // Значения для WHERE
                null,                                     // Не группировать ряды
                null,                                     // Не фильтровать сгруппированные ряды
                null);                                   // Порядок сортировки
        if (cursor.getCount() == 0){
            SQLiteDatabase dbWrite = mDbHelper.getWritableDatabase();
            ContentValues purchaseValues = new ContentValues();
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_TAXATIONTYPE, purchase.taxationType);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_SHIFTNUMBER, purchase.shiftNumber);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_REQUESTNUMBER,purchase.requestNumber);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_DISCOUNTSUM, purchase.discountSum);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_POINT_ID_FK, PointID);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_STORE_ID_FK, StoreID);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_OPERATOR, purchase.operator);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_OPERATIONTYPE, purchase.operationType);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_NDSNO, purchase.ndsNo);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_NDSCALCULATED18, purchase.ndsCalculated18);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_NDSCALCULATED10, purchase.ndsCalculated10);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_NDS18, purchase.nds18);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_NDS10, purchase.nds10);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_NDS0, purchase.nds0);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_KKTREGID, purchase.kktRegId);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_FISCALSIGN, purchase.fiscalSign);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_KKTNUMBER, purchase.kktNumber);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_FISCALDRIVENUMBER, purchase.fiscalDriveNumber);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_FISCALDOCUMENTNUMBER, purchase.fiscalDocumentNumber);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_ECASHTOTALSUM, purchase.ecashTotalSum);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_CASHTOTALSUM, purchase.cashTotalSum);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_TOTALSUM, purchase.totalSum);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_DISCOUNT, purchase.discount);
            purchaseValues.put(DataContract.PurchaseEntry.COLUMN_PURCHASE_DATETIME, purchase.dateTime);
            Uri insertedPurchaseUri = getContentResolver().insert(DataContract.PurchaseEntry.CONTENT_URI, purchaseValues);
            PurchaseID = ContentUris.parseId(insertedPurchaseUri);
            cursor.close();
            itemsAdd();
        }
        else {
//            cursor.moveToNext();
//            int purchaseIdColumnIndex = cursor.getColumnIndex(DataContract.PurchaseEntry.COLUMN_PURCHASE_ID);
//            PurchaseID = cursor.getLong(purchaseIdColumnIndex);
            Toast toast = Toast.makeText(getApplicationContext(), "Такой чек уже добавлен", Toast.LENGTH_SHORT);
            toast.show();
            cursor.close();
            kill_activity();
        }

    }
    public void itemsAdd() {
        if (purchase.items.length == 0){
            return;
        }
        //Cursor cursor;
        for (int i = 0; i < purchase.items.length; i++ ){
            ContentValues itemValues = new ContentValues();
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_NAME, purchase.items[i].name);
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_NDS10, purchase.items[i].nds10);
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_NDS18, purchase.items[i].nds18);
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_NDSCALCULATED10, purchase.items[i].ndsCalculated10);
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_NDSCALCULATED18, purchase.items[i].ndsCalculated18);
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_NDSNO, purchase.items[i].ndsNo);
            //itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_MODIFIERS, purchase.items[i].modifiers);
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_NDS0, purchase.items[i].nds0);
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_PRICE, purchase.items[i].price);
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_QUANTITY, purchase.items[i].quantity);
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_PURCHASE_ID_FK, PurchaseID);
//            if (purchase.items[i].storno == "true"){
//                itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_STORNO, 1);
//            }
//            else
//            {
//                itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_STORNO, 0);
//            }
            itemValues.put(DataContract.GoodEntry.COLUMN_GOOD_SUM, purchase.items[i].sum);
            Uri insertedItemUri = getContentResolver().insert(DataContract.GoodEntry.CONTENT_URI, itemValues);
        }
        Toast toast = Toast.makeText(getApplicationContext(), "Покупка добавлена", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        kill_activity();
    }
    void kill_activity()
    {
        finish();
    }
    /** И в классе покупки, и в классе товара убрал несколько полей,
     * потому что они почти не используются, и где-то идут пустыми массивами, а где-то значением false.
     * Лень пока делать обработку этого, всё равно абсолютно не нужно.
     */
    public class Purchase {
        public Long cashTotalSum;
        public Long dateTime;
        public Long discount;
        public Long discountSum;
        public Long ecashTotalSum;
        public String fiscalDocumentNumber;
        public String fiscalDriveNumber;
        public String fiscalSign;
        public String kktNumber;
        public String kktRegId;
        public Long nds0;
        public Long nds10;
        public Long nds18;
        public Long ndsCalculated10;
        public Long ndsCalculated18;
        public Long ndsNo;
        public int operationType;
        public String operator;
        public int requestNumber;
        public String retailPlaceAddress;
        public int shiftNumber;
        //public Item[] stornoItems;
        public int taxationType;
        public Long totalSum;
        public String user;
        public String userInn;
        public Item[] items;
    }
    static class Item {
        //public String modifiers;
        public String name;
        public Long nds0;
        public Long nds10;
        public Long nds18;
        public Long ndsCalculated10;
        public Long ndsCalculated18;
        public Long ndsNo;
        public Long price;
        public double quantity;
        public Long sum;
        //public String storno;
    }
    }
