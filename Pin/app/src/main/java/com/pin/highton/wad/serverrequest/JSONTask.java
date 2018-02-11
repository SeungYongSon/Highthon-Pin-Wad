package com.pin.highton.wad.serverrequest;

/**
 * Created by Seungyong Son on 2018-02-11.
 */

import android.os.AsyncTask;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

public class JSONTask extends AsyncTask<Object, Object, String> {
    public static String accessToken = null;

    @Override
    protected String doInBackground(Object ... args) {
        HttpURLConnection conn = null;

        BufferedReader reader = null;
        BufferedWriter writer = null;

        String result = null;

        try  {
            URL url = new URL(args[1].toString());
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(args[0].toString());
            conn.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
            conn.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송

            if (accessToken != null) conn.setRequestProperty("X-Access-Token", accessToken);

            conn.setDoInput(true);

            if (args[0] != "GET") conn.setDoOutput(true);

            conn.connect();

            if (args[0] != "GET") {
                writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

                writer.write(args[2].toString());
                writer.flush();
            }

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuffer buffer = new StringBuffer();

            while((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            result = conn.getResponseCode() + " " + buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();

            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //tvData.setText(result);//서버로 부터 받은 값을 출력해주는 부
    }
}