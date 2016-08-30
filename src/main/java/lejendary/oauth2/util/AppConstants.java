package lejendary.oauth2.util;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 1:54 PM
 */

public class AppConstants {

    public final static String PACKAGE_NAME = "lejendary.oauth2";
    public final static String LOGIN_REGEX = "^[_'.@A-Za-z0-9-]*$";
    public final static String SPRING_PROFILE_DEVELOPMENT = "dev";
    public final static String SPRING_PROFILE_PRODUCTION = "prod";

    public class Authorities {
        public static final String ADMIN = "ADMIN";
        public static final String ANONYMOUS = "ANONYMOUS";
    }

}
