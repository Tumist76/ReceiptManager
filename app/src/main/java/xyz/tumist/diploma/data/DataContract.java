package xyz.tumist.diploma.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class DataContract {

    public static final String PATH_GOODS = "goods";
    public static final String PATH_PURCHASES = "purchases";
    public static final String PATH_POINTS = "points";
    public static final String PATH_STORES = "stores";

    //Класс, определяющий содержимое таблицы с долгами
    public static abstract class GoodEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GOODS);
        public static final String TABLE_NAME = "goods"; //название таблицы

        public static final String COLUMN_GOOD_ID = "id_good";
        public static final String COLUMN_GOOD_NAME = "good_name";
        public static final String COLUMN_GOOD_NDS10 = "good_nds10";
        public static final String COLUMN_GOOD_NDS18 = "good_nds18";
        public static final String COLUMN_GOOD_NDSCALCULATED10 = "good_ndsCalculated10";
        public static final String COLUMN_GOOD_NDSCALCULATED18 = "good_ndsCalculated18";
        public static final String COLUMN_GOOD_NDSNO = "good_ndsNo";
        public static final String COLUMN_GOOD_PRICE = "good_price";
        public static final String COLUMN_GOOD_QUANTITY = "good_quantity";
        public static final String COLUMN_GOOD_SUM = "good_sum";
        public static final String COLUMN_GOOD_STORNO = "good_storno";
        public static final String COLUMN_GOOD_PURCHASE_ID_FK = "purchase_id_FK";
    }

    public static abstract class PurchaseEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PURCHASES);
        public static final String TABLE_NAME = "purchases"; //название таблицы

        //Данные из фискального чека
        public static final String COLUMN_PURCHASE_ID = "purchase_id";
        public static final String COLUMN_PURCHASE_FISCALDOCUMENTNUMBER = "purchase_fiscalDocumentNumber";
        public static final String COLUMN_PURCHASE_FISCALDRIVENUMBER = "purchase_fiscalDriveNumber";
        public static final String COLUMN_PURCHASE_CASHTOTALSUM = "purchase_cashTotalSum";
        public static final String COLUMN_PURCHASE_DATETIME = "purchase_dateTime";
        public static final String COLUMN_PURCHASE_DISCOUNT = "purchase_discount";
        public static final String COLUMN_PURCHASE_ECASHTOTALSUM = "purchase_ecashTotalSum";
        public static final String COLUMN_PURCHASE_FISCALSIGN = "purchase_fiscalSign";
        public static final String COLUMN_PURCHASE_ITEMSAMOUNT = "purchase_itemsAmount";
        public static final String COLUMN_PURCHASE_OPERATOR = "purchase_operator";
        public static final String COLUMN_PURCHASE_REQUESTNUMBER = "purchase_requestNumber";
        public static final String COLUMN_PURCHASE_SHIFTNUMBER = "purchase_shiftNumber";
        public static final String COLUMN_PURCHASE_KKTREGID = "purchase_kktRegID";
        public static final String COLUMN_PURCHASE_NDS0 = "purchase_nds0";
        public static final String COLUMN_PURCHASE_NDS10 = "purchase_nds10";
        public static final String COLUMN_PURCHASE_NDS18 = "purchase_nds18";
        public static final String COLUMN_PURCHASE_NDSCALCULATED10 = "purchase_ndsCalculated10";
        public static final String COLUMN_PURCHASE_NDSCALCULATED18 = "purchase_ndsCalculated18";
        public static final String COLUMN_PURCHASE_NDSNO = "purchase_ndsNo";
        public static final String COLUMN_PURCHASE_TAXATIONTYPE = "purchase_taxationType";
        public static final String COLUMN_PURCHASE_OPERATIONTYPE = "purchase_operationType";
        public static final String COLUMN_PURCHASE_POINT_ID_FK = "point_id_FK";

        //Дополнительные данные
        public static final String COLUMN_PURCHASE_JSONNAME = "purchase_jsonName";
        public static final String COLUMN_PURCHASE_TAGS = "purchase_tags";

    }

    public static abstract class PointEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_POINTS);
        public static final String TABLE_NAME = "Points"; //название таблицы

        public static final String COLUMN_POINT_ID = "point_id";
        public static final String COLUMN_POINT_ADDRESS = "point_address";
        public static final String COLUMN_POINT_NICKNAME = "point_nickname";
        public static final String COLUMN_STORE_ID_FK = "store_id_FK";
    }

    public static abstract class StoreEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STORES);
        public static final String TABLE_NAME = "Stores"; //название таблицы

        public static final String COLUMN_STORE_ID = "store_id";
        public static final String COLUMN_STORE_NAME = "store_name";
        public static final String COLUMN_STORE_INN = "store_inn";
        public static final String COLUMN_STORE_NICKNAME = "store_nickname";
    }

        public static final String CONTENT_AUTHORITY = "xyz.tumist.diploma";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


        //Я даже не знаю, юзается ли это
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PURCHASES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PURCHASES;


}
