package com.bikash.bikashBackend.util;

public class UrlConstraint {
    private UrlConstraint() {
    }
    public static final String ADMIN = "/admin";
    public static final String USER = "/user";
    public static final String MERCHANT = "/merchant";
    public static final String AGENT = "/agent";
    public static final String RECHARGE = "/recharge";
    private static final String VERSION = "/v1";
    private static final String API = "/api";

    public static class MerchantManagement {
        public static final String ROOT = API + VERSION + "/Merchant";
        public static final String CREATE = "/create";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String GET = "/{id}";
        public static final String GET_ALL = "/all";
    }
    public static class AgentManagement {
        public static final String ROOT = API + VERSION + "/Agent";
        public static final String CREATE = "/create";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String GET = "/{id}";
        public static final String GET_ALL = "/all";
    }

    public static class AdminManagement {
        public static final String ROOT = API + VERSION + "/Admin";
        public static final String RECHARGE = "/recharge";
        public static final String CREATE = "/create";
        public static final String UPDATE = "/{id}";
        public static final String DELETE = "/{id}";
        public static final String GET = "/{id}";
        public static final String GET_ALL = "/all";
    }
    public static class UserManagement {
        public static final String ROOT = API + VERSION + "/users";
        public static final String GET_USER = "/all-User";
        public static final String CREATE = "/create";
    }
    public static class AuthManagement {
        public static final String ROOT = API + "/auth";
        public static final String LOGIN = "/login";
    }
}
