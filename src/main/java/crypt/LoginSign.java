package crypt;

import org.apache.http.message.BasicNameValuePair;
import java.util.*;

public class LoginSign {
    public static String getLoginSign(List<BasicNameValuePair> pairs) {
        String result = loginSign(pairs);
        return result;
    }
    private static String loginSign(List<BasicNameValuePair> pairs) {
        Map<String, String> unsortMap = new HashMap<String, String>();
        for (int j = 0; j < pairs.size(); j++) {
            BasicNameValuePair bbs = pairs.get(j);
            unsortMap.put(bbs.getName(), bbs.getValue());
        }
        String str = null;
        ArrayList mapset = new ArrayList(unsortMap.keySet());
        Collections.sort(((List) mapset));
        StringBuilder mapstr = new StringBuilder("");
        int i = 0;
        while (i < ((List) mapset).size()) {
            Object v0 = ((List) mapset).get(i);
            Object v1 = unsortMap.get(v0);
            if (v1 != null) {
                str = v1.toString();
            } else {
                str = "";
            }
            mapstr.append(str);
            ++i;
        }
        String ms = mapstr.toString();
        String key = "2a7ee43463114270bf2620ae5d6d59c4";
        ms = ms + key;
        String result = MD5.md5Utils(ms);
        return result;
    }
}
