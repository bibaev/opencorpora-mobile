package org.opencorpora;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AuthService extends Service {

    final private String LOG_TAG = "[Service]";

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "Started");
        final String login = intent.getStringExtra("login");
        String password = intent.getStringExtra("password");

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("login", login);
        params.add("password", password);
        client.post(BuildConfig.server_address + "api/login.php",
                params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            Log.i(LOG_TAG, "Request successful");
                            Intent sendedIntent = new Intent("My login action");
                            sendedIntent.putExtra("isSuccess", true);
                            sendedIntent.putExtra("token", response.getString("token"));
                            sendBroadcast(sendedIntent);
                            stopSelf();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.i(LOG_TAG, "Request failed");
                        intent.putExtra("isSuccess", false);
                        sendBroadcast(intent);
                        stopSelf();
                    }
                }
                );

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
