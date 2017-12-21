package com.omneagate.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by user1 on 5/10/16.
 */
public class GetResponse {

    public String getresponseData(org.apache.http.HttpResponse response) {
        String strresponse = null;
        if (response != null && response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = null;
            StringBuffer sb = new StringBuffer("");
            try {
                in = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent()));
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                strresponse = sb.toString();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                strresponse = null;
            }
        }
        return strresponse;
    }
}
