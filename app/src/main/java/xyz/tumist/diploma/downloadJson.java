package xyz.tumist.diploma;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class downloadJson extends AsyncTask<String, String, String> {
    Context mContext;
    String mFN, mFD, mFPD;
    public downloadJson (Context context, String FN, String FD, String FPD){
        mContext = context.getApplicationContext();
        mFN = FN;
        mFD = FD;
        mFPD = FPD;
    }
    HttpURLConnection urlConnection;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        ProgressDialog progDailog = new ProgressDialog(mContext);
//        progDailog.setMessage("Загрузка чека");
//        progDailog.setIndeterminate(false);
//        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progDailog.setCancelable(true);
//        progDailog.show();
    }
    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();

        try {
            String fullURL = "http://proverkacheka.nalog.ru:8888/v1/inns/*/kkts/*/fss/" + mFN + "/tickets/" + mFD + "?fiscalSign=" + mFPD + "&sendToEmail=no";
            URL url = new URL(fullURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty ("Authorization", "Basic Kzc5OTk0NzYyNjc2OjcyNDEzMQ==");
            urlConnection.setRequestProperty ("Device-Id", "0008cccc-eeeeeeee-vvvvvvvv-gggggggg");
            urlConnection.setRequestProperty ("Device-OS", "Android 5.1");
            urlConnection.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            Log.v("downloadJSON", result.toString());
            StringBuilder sb = new StringBuilder(result.toString());
            sb.delete(0, 23);
            sb.delete(sb.length() - 2, sb.length());
            String trimmedResult = sb.toString();
            String timeHuman = trimmedResult.substring(trimmedResult.indexOf("Time\":\"") + 7, trimmedResult.indexOf("Time\":\"") + 26);
            Log.v("downloadJSON", trimmedResult);
            Long millis = 0L;
            try {
                millis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(timeHuman).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String finalResult = trimmedResult.replaceAll(timeHuman, String.valueOf(millis));
            Intent i = new Intent(mContext, jsonParser.class);
            i.putExtra("jsonString", "true");
            i.putExtra("result", finalResult);
            mContext.startActivity(i);
        }
        catch (Exception e)
        {
            Toast toast = Toast.makeText(mContext.getApplicationContext(), "Ошибка получения чека с сервера", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
