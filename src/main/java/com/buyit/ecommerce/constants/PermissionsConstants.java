package com.buyit.ecommerce.constants;

public class PermissionsConstants {

    private PermissionsConstants(){}

    //Categories Permissions
    public static final String CATEGORIES_CREATE = "CATEGORIES.CREATE";
    public static final String CATEGORIES_UPDATE = "CATEGORIES.UPDATE";
    public static final String CATEGORIES_DELETE = "CATEGORIES.DELETE";

    public static final String CATEGORIES_ATTACH_ATTRIBUTES = "CATEGORIES.ATTACH_CATEGORY_ATTRIBUTES";
    public static final String CATEGORIES_UPDATE_CATEGORIES_ATTRIBUTES = "CATEGORIES.UPDATE_CATEGORIES_ATTRIBUTES";


    //ORDER PERMISSIONS
    public static final String ORDERS_GET_ORDERS = "ORDERS.GET_ORDERS";
    public static final String ORDERS_GET_SPECIFIC_ORDER = "ORDERS.GET_SPECIFIC_ORDER";
    public static final String ORDERS_CREATE_ORDER = "ORDERS.CREATE_ORDER";
    public static final String ORDERS_GET_ORDER_VOUCHER = "ORDERS.GET_ORDER_VOUCHER";


    //Payment Permissions
    public static final String PAYMENT_CREATE_INTENT = "PAYMENT.CREATE_INTENT";


    //Product Permissions
    public static final String PRODUCTS_CREATE = "PRODUCTS.CREATE";
    public static final String PRODUCTS_UPDATE = "PRODUCTS.UPDATE";
    public static final String PRODUCTS_DELETE = "PRODUCTS.DELETE";
    public static final String PRODUCTS_ATTACH_IMAGES = "PRODUCTS.ATTACH_IMAGES";
    public static final String PRODUCTS_GET_PRODUCT_IMAGES = "PRODUCTS.GET_PRODUCT_IMAGES";
    public static final String PRODUCTS_DELETE_PRODUCT_IMAGE = "PRODUCTS.DELETE_PRODUCT_IMAGE";
    public static final String PRODUCTS_GET_PRODUCT_REVIEWS = "PRODUCTS.GET_PRODUCT_REVIEWS";
    public static final String PRODUCTS_ADD_PRODUCT_ATTRIBUTES = "PRODUCTS.ADD_PRODUCT_ATTRIBUTES";
    public static final String PRODUCTS_GET_PRODUCT_ATTRIBUTES = "PRODUCTS.GET_PRODUCT_ATTRIBUTES";


    //Recommendation Permissions
    public static final String RECOMMENDATION_POPULAR = "RECOMMENDATION.POPULAR";
    public static final String RECOMMENDATION_SIMILAR = "RECOMMENDATION.SIMILAR";

    //REVIEWS Permissions
    public static final String REVIEWS_CREATE = "REVIEWS.CREATE";
    public static final String REVIEWS_DELETE = "REVIEWS.DELETE";
    public static final String REVIEWS_GET_REVIEW = "REVIEWS.GET_REVIEW";


    //Roles Permissions
    public static final String ROLES_SYNCHRONIZE_ROLES = "ROLES.SYNCHRONIZE_ROLES";
    public static final String ROLES_ASSIGN_PERMISSIONS = "ROLES.ASSIGN_PERMISSIONS";

    //User Address Permissions
    public static final String ADDRESS_SEARCH = "ADDRESS.SEARCH";
    public static final String ADDRESS_GET_MY_ADDRESS = "ADDRESS.GET_MY_ADDRESS";
    public static final String ADDRESS_CREATE = "ADDRESS.CREATE";
    public static final String ADDRESS_UPDATE = "ADDRESS.UPDATE";
    public static final String ADDRESS_DELETE = "ADDRESS.DELETE";

    //User Permissions
    public static final String USERS_MY_PROFILE = "USERS.MY_PROFILE";




}
