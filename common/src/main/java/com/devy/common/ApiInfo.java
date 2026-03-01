package com.devy.common;

public class ApiInfo {

    public static class ORDERS {
        public static final String SCHEME = "http://";
        public static final String HOST = "localhost";
        public static final String PORT = "8080";
        public static final String VERSION = "v1";
        public static final String BASE_PATH = "/api/" + VERSION;
        public static final String BASE_URL = SCHEME + HOST + ":" + PORT + "/api/" + VERSION;


    }

    public static class INVENTORY {
        public static final String SCHEME = "http://";
        public static final String HOST = "localhost";
        public static final String PORT = "8081";
        public static final String VERSION = "v1";
        public static final String BASE_PATH = "/api/" + VERSION;
        public static final String BASE_URL = SCHEME + HOST + ":" + PORT + "/api/" + VERSION;
    }

    public static class PAYMENTS {
        public static final String SCHEME = "http://";
        public static final String HOST = "localhost";
        public static final String PORT = "8082";
        public static final String VERSION = "v1";
        public static final String BASE_PATH = "/api/" + VERSION;
        public static final String BASE_URL = SCHEME + HOST + ":" + PORT + "/api/" + VERSION;

        public static final String PAYMENT_PATH = "/payments";
        public static final String PAYMENT_CANCEL_PATH = "/payments/cancel";
    }
}
