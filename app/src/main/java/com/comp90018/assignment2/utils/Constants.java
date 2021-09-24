package com.comp90018.assignment2.utils;

public class Constants {

    public static final String JPUSH_APPKEY = "4787283def67f5866a0a1451";

    /** gender */
    public static final int GENDER_UNKNOWN = 0;
    public static final int FEMALE = 1;
    public static final int MALE = 2;

    /** order status */
    public static final int WAITING_DELIVERY = 0;
    public static final int DELIVERING = 1;
    public static final int SUCCESSFUL_NOT_COMMENT = 2;
    public static final int SUCCESSFUL_COMMENT = 3;
    public static final int CANCELED = 4;
    public static final int ON_REFUND = 5;
    public static final int ON_REFUND_DELIVERING = 6;
    public static final int REFUNDED = 7;

    /** product status */
    public static final int PUBLISHED = 0;
    public static final int SOLD_OUT = 1;
    public static final int UNDERCARRIAGE = 2;
    public static final int REMOVED = 3;

    /** product 品相 */
    public static final int HEAVILY_USED = 0;
    public static final int WELL_USED = 1;
    public static final int AVERAGE_CONDITION = 2;
    public static final int SLIGHTLY_USED = 3;
    public static final int EXCELLENT = 4;

    /** firebase storage bucket name */
    public static final String STORAGE_ROOT_PATH = "gs://comp90018-mobile-caa7c.appspot.com";

    /** default image path */
    public static final String DEFAULT_IMAGE_PATH = "gs://comp90018-mobile-caa7c.appspot.com/public/default.png";
    public static final String DEFAULT_AVATAR_PATH = "gs://comp90018-mobile-caa7c.appspot.com/users/default_avatar.jpg";

    /** firestore db collection names */
    public static final String CATEGORIES_COLLECTION = "categories";
    public static final String ORDERS_COLLECTION = "orders";
    public static final String PRODUCT_COMMENTS_COLLECTION = "product_comments";
    public static final String PRODUCT_COLLECTION = "products";
    public static final String SUB_CATEGORIES_COLLECTION = "sub_categories";
    public static final String USERS_COLLECTION = "users";

    /** currency type */
    public static final Integer AUS_DOLLAR = 0;

    /** activity jumping state code */
    // open the activity with refresh
    public final static int REQUEST_CODE_A = 10101;
    public final static int REQUEST_CODE_B = 10102;

    // used for intent bundle name
    public final static String DATA_A = "DATA_A";
    public final static String DATA_B = "DATA_B";

    /** handler control code */
    public final static int SCROLL_BOTTOM = 10103;
    public final static int HIDDEN_BOTTOM = 10104;
    public final static int SHOW_BOTTOM = 10105;

    /** IM Message custom type */
    public final static String TYPE = "TYPE";
    public final static int SINGLE_CHAT = 0;
    public final static int GROUP_CHAT= 1;

    public final static String ADDRESS = "ADDRESS";
    public final static String NEW_MESSAGE = "NEW_MESSAGE";

    /** message type */
    public final static String IMG_TYPE = "IMG";
    public final static String VIDEO_TYPE = "VIDEO";

}
