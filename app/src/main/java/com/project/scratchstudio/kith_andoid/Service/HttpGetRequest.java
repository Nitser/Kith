package com.project.scratchstudio.kith_andoid.Service;

import android.os.AsyncTask;

import com.project.scratchstudio.kith_andoid.Activities.HomeActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetRequest extends AsyncTask<String, Void, String> {

    private int code;
    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;
    private String[] input_keys;
    private HttpGetRequest.AsynResponse asynResponse;

    public interface AsynResponse {
        void processFinish(Boolean output, String resultJSON, int code);
    }

    HttpGetRequest(AsynResponse asynResponse){
        this.asynResponse = asynResponse;
    }

    HttpGetRequest(String[] input_keys, AsynResponse asynResponse){
        this.input_keys = input_keys;
        this.asynResponse = asynResponse;
    }

    @Override
    protected String doInBackground(String... strings) {
        String stringUrl = strings[0];
        String inputLine;

        String resultJSON;
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            if(input_keys != null){
                connection.setDoInput(true);
                connection.setRequestProperty(input_keys[0], HomeActivity.getMainUser().getToken());
            }
            connection.connect();

            code = connection.getResponseCode();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }

                reader.close();
                streamReader.close();

                resultJSON = stringBuilder.toString();
            } else {
                resultJSON = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
//            Log.i("ERROR", e.getMessage());
            resultJSON = null;
        }

        return resultJSON;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        asynResponse.processFinish(true, s, code);
    }

}