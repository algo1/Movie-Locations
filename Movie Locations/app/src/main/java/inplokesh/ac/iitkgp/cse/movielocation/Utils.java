package inplokesh.ac.iitkgp.cse.movielocation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by lokeshponnada on 5/22/16.
 */
public class Utils {


    public static boolean isConnectedToInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) MovieLocation.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // Since NetworkInfo is null incase of no current network
        boolean isNetworkConnected = false;
        if (networkInfo != null) {
            isNetworkConnected = networkInfo.isConnected();
        }
        return isNetworkConnected;
    }

    public static String capitalize(String str) {
        return capitalize(str, null);
    }

    public static String capitalize(String str, char[] delimiters) {
        int delimLen = (delimiters == null ? -1 : delimiters.length);
        if (str == null || str.length() == 0 || delimLen == 0) {
            return str;
        }
        int strLen = str.length();
        StringBuffer buffer = new StringBuffer(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);

            if (isDelimiter(ch, delimiters)) {
                buffer.append(ch);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (int i = 0, isize = delimiters.length; i < isize; i++) {
            if (ch == delimiters[i]) {
                return true;
            }
        }
        return false;
    }
}
