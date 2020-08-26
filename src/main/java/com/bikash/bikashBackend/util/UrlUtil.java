package com.bikash.bikashBackend.util;

public class UrlUtil {
    private static String allPrefix = "/*";
    public static String permitAllUrl[] = {
            UrlConstraint.AuthManagement.ROOT + allPrefix,
            UrlConstraint.UserManagement.ROOT + UrlConstraint.UserManagement.CREATE,
    };
}
