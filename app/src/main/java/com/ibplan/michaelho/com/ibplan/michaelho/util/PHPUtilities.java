package com.ibplan.michaelho.com.ibplan.michaelho.util;

import android.util.Log;

import com.ibplan.michaelho.com.ibplan.michaelho.constants.URLConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by MichaelHo on 2015/4/7.
 */
public class PHPUtilities  implements URLConstants{

    public PHPUtilities(){
    }

    public String getBuildingDetail(String... arg0) {
        try {
            String stringToQuery = (String) arg0[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL_GET_BUILDING_DETAIl);
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("query_string", stringToQuery));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();

            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            return builder.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public String getLabDetail(String... arg0) {
        try {
            String stringToQuery = (String) arg0[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL_GET_LAB_DETAIl);
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("query_string", stringToQuery));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();

            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            return builder.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public String getLabStats(String... arg0) {
        try {
            String name = (String) arg0[0];
            String data = URLEncoder.encode("name", "UTF-8") + "="
                    + URLEncoder.encode(name, "UTF-8");
            URL url = new URL(URL_GET_LAB_STATS);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public String checkMacExist(String... params) {
        try {
            String mac = (String) params[0];
            String data = URLEncoder.encode("mac", "UTF-8") + "="
                    + URLEncoder.encode(mac, "UTF-8");
            URL url = new URL(URL_CHECK_MAC);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public String regMember(String... arg0) {
        try {
            String name = (String) arg0[0];
            String department = (String) arg0[1];
            String email = (String) arg0[2];
            String mac = (String) arg0[3];
            String regId = (String) arg0[4];
            String image = (String) arg0[5];
            String data = URLEncoder.encode("name", "UTF-8") + "="
                    + URLEncoder.encode(name, "UTF-8");
            data += "&" + URLEncoder.encode("department", "UTF-8") + "="
                    + URLEncoder.encode(department, "UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "="
                    + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("mac", "UTF-8") + "="
                    + URLEncoder.encode(mac, "UTF-8");
            data += "&" + URLEncoder.encode("regId", "UTF-8") + "="
                    + URLEncoder.encode(regId, "UTF-8");
            data += "&" + URLEncoder.encode("image", "UTF8") + "="
                    + URLEncoder.encode(image, "UTF-8");
            URL url = new URL(URL_REGISTER_MEMBER);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                Log.i("status", sb + "");
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public String postEvent(String... arg0) {
        try {
            String mac = (String) arg0[0];
            String location = (String) arg0[1];
            String event = (String) arg0[2];
            String time = (String) arg0[3];
            String x_pos = (String) arg0[4];
            String y_pos = (String) arg0[5];
            String image = (String) arg0[6];
            String if_fixed = (String) arg0[7];
            String data = URLEncoder.encode("mac", "UTF-8") + "="
                    + URLEncoder.encode(mac, "UTF-8");
            data += "&" + URLEncoder.encode("location", "UTF-8") + "="
                    + URLEncoder.encode(location, "UTF-8");
            data += "&" + URLEncoder.encode("event", "UTF-8") + "="
                    + URLEncoder.encode(event, "UTF-8");
            data += "&" + URLEncoder.encode("time", "UTF-8") + "="
                    + URLEncoder.encode(time, "UTF-8");
            data += "&" + URLEncoder.encode("x_pos", "UTF-8") + "="
                    + URLEncoder.encode(x_pos, "UTF-8");
            data += "&" + URLEncoder.encode("y_pos", "UTF-8") + "="
                    + URLEncoder.encode(y_pos, "UTF-8");
            data += "&"+ URLEncoder.encode("image", "UTF-8") + "="
                    + URLEncoder.encode(image,"UTF-8");
            data += "&"+ URLEncoder.encode("if_fixed", "UTF-8") + "="
                    + URLEncoder.encode(if_fixed,"UTF-8");
            URL url = new URL(URL_POST_EVENTS);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                Log.i("status", sb + "");
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }
    public String getEvent(String... arg0) {
        try {
            String stringToQuery = (String) arg0[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL_GET_EVENTS);
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("query_string", stringToQuery));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            return builder.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public String postPortrait(String... arg0) {
        try {
            String mac = (String) arg0[0];
            String image = (String) arg0[1];
            String data = URLEncoder.encode("mac", "UTF-8") + "="
                    + URLEncoder.encode(mac, "UTF-8");
            data += "&"
                    + URLEncoder.encode("image", "UTF-8")
                    + "="
                    + URLEncoder.encode(image, "UTF-8");
            URL url = new URL(URL_POST_PORTRAIT);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                Log.i("status", sb + "");
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    public String getMyPortrait(String... arg0) {
        try {
            String mac = (String) arg0[0];
            String data = URLEncoder.encode("mac", "UTF-8") + "="
                    + URLEncoder.encode(mac, "UTF-8");
            URL url = new URL(URL_GET_MY_PORTRAIT);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                Log.i("status", sb + "");
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }
    }
}
