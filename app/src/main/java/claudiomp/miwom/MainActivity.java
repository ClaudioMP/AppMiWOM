package claudiomp.miwom;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    WebView web;
    RelativeLayout view;
    ConnectivityManager connMgr;
    WifiManager manager;
    boolean WIFI_ON=false;
    int check = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        view = (RelativeLayout)findViewById(R.id.view);
        web = (WebView)findViewById(R.id.web);
        checkNetwork();
    }

    private void checkNetwork() {
        connMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo!=null) { //Connected to Internet
            if(networkInfo.isConnectedOrConnecting()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    loadWeb();
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    WIFI_ON = true;
                    warnWiFi();
                }
            }
        }
        else{
            check++;
            if(check<3){
                new WaitTask().execute();
            }
            else{
                Snackbar.make(view,"Sin conexión a internet",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Salir", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (WIFI_ON){
                                    manager.setWifiEnabled(true);
                                }
                                finishAndRemoveTask();
                            }
                        })
                        .setActionTextColor(Color.RED)
                        .show();
            }
        }
    }

    private void warnWiFi() {
        Snackbar.make(view,"Estás usando una conexión WiFi, se desactivará pero volverá cuando salgas",Snackbar.LENGTH_LONG).show();
        manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        manager.setWifiEnabled(false);
        new WaitTask().execute();
    }

    private void loadWeb() {
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        this.getWindow().setStatusBarColor(Color.parseColor("#551d73"));
        web.loadUrl("http://www.wom.cl/miwom");
    }

    @Override
    public void onBackPressed() {
        int index = web.copyBackForwardList().getCurrentIndex();
        if(web.canGoBack() && index>1){
            web.goBack();
        }
        else {
            if(WIFI_ON){
                manager.setWifiEnabled(true);
            }
            finishAndRemoveTask();
        }
    }

    class WaitTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            checkNetwork();
        }
    }
}
