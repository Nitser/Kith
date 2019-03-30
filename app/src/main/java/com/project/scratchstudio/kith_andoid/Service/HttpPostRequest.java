package com.project.scratchstudio.kith_andoid.Service;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class HttpPostRequest extends AsyncTask<String, Void, String> {

    private int code;
    private static final String REQUEST_METHOD = "POST";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;
    private String photo;
    private String[] header_keys;
    private String[] body_keys;
    private String[] header_data;
    private String[] body_data;
    private AsynResponse asynResponse;

    public interface AsynResponse {
        void processFinish(Boolean output, String resultJSON, int code);
    }

    HttpPostRequest(String[] body_keys, String[] body_data, AsynResponse asynResponse){
        this.body_keys = body_keys;
        this.body_data = body_data;
        this.asynResponse = asynResponse;
    }

    HttpPostRequest(String[] body_keys, String[] body_data, String photo, AsynResponse asynResponse){
        this.photo = photo;
        this.body_keys = body_keys;
        this.body_data = body_data;
        this.asynResponse = asynResponse;
    }

    HttpPostRequest(String[] header_keys, String[] body_keys, String[] header_data, String[] body_data, AsynResponse asynResponse){
        this.header_keys = header_keys;
        this.body_keys = body_keys;
        this.header_data = header_data;
        this.body_data = body_data;
        this.asynResponse = asynResponse;
    }

    @Override
    protected String doInBackground(String... strings) {
        String stringUrl = strings[0];
        JSONObject postDataParams;

        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            if(header_keys != null)
                for(int i=0; i<header_keys.length; i++){
                    if(header_data[i]!= null)
                        connection.setRequestProperty(header_keys[i], header_data[i]);
                }


            postDataParams = new JSONObject();

            if(photo != null){
                postDataParams.put("photo", photo);
            }

            for(int i=0; i<body_keys.length; i++){
                if(body_data[i] != null)
                    postDataParams.put(body_keys[i], body_data[i]);
            }

            try{
                connection.connect();
            } catch (Exception e){
//                Log.i("CONNECTION ERROR: ", e.getMessage());
                code = connection.getResponseCode();
                return null;
            }

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            code = connection.getResponseCode();

            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK ) {

                BufferedReader in = new BufferedReader( new InputStreamReader( connection.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line;

                while((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            } else if( connection.getResponseCode() == 405 ){
                BufferedReader in = new BufferedReader( new InputStreamReader( connection.getErrorStream()));
                StringBuffer sb = new StringBuffer("");
                String line;

                while((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();
            }
            else{
//                Log.i("SERVER ERROR:", "No JSON. Bad connection code");
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        asynResponse.processFinish(true, s, code);
    }

    private String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}
