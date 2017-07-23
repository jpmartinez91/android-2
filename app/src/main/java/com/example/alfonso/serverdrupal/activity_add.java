package com.example.alfonso.serverdrupal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;

public class activity_add extends Activity implements View.OnClickListener {
    public String session_id;
    public String session_name;
    Button btn_cargar;
    Button btn_registrar;

    //LocationManager lm = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //retrieve the session_id and session_id passed by the previous activity
            session_id = extras.getString("SESSION_ID");
            session_name = extras.getString("SESSION_NAME");
        }

        btn_cargar = (Button) findViewById(R.id.buttonSave);
        btn_cargar.setOnClickListener(this);
        btn_registrar = (Button) findViewById(R.id.bCll);
        btn_registrar.setOnClickListener(this);

        LocationManager milocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        LocationListener milocListener = new MiLocationListener();

        milocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, milocListener);



        /*lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updatePosition();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(lm.NETWORK_PROVIDER, 0, 0, ll);*/
    }

    public class MiLocationListener implements LocationListener {

        public void onLocationChanged(Location loc)

        {

            loc.getLatitude();
            loc.getLongitude();
            String coordenadas = "Mis coordenadas son: " + "Latitud = " + loc.getLatitude() + "Longitud = " + loc.getLongitude();
            EditText et = (EditText) findViewById(R.id.etll);
            et.setText(loc.getLatitude()+ " / " +loc.getLongitude());
            Toast.makeText( getApplicationContext(),coordenadas,Toast.LENGTH_LONG).show();
        }
        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(),"Gps Desactivado",Toast.LENGTH_SHORT ).show();
        }
        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(),"Gps Activo",Toast.LENGTH_SHORT ).show();
        }
        public void onStatusChanged(String provider, int status, Bundle extras){}

    }

    /*evento de clic en botones*/
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSave) {
            new addArticleTask().execute(session_name, session_id);
        }
        if (view.getId() == R.id.bCll) {
            Toast.makeText(getApplicationContext(), ((Button) view).getText(), Toast.LENGTH_SHORT).show();
            //updatePosition();
        }
    }

    /*private void updatePosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(lm.NETWORK_PROVIDER);
        updatePosition(location);
    }

    private void updatePosition(Location location){
        if (location != null){
            EditText et = (EditText) findViewById(R.id.etll);
            et.setText(location.getLatitude()+ " / " +location.getLongitude());
        }
    }*/

    //click listener for addArticle button
    /*public void addArticleButton_click(View view){
        //initiate the background process to post the article to the Drupal endpoint.
        //pass session_name and session_id
        new addArticleTask().execute(session_name,session_id);
    }*/

    //asynchronous task to add the article into Drupal
    private class addArticleTask extends AsyncTask<String, Void, Integer> {

        protected Integer doInBackground(String... params) {

            //read session_name and session_id from passed parameters
            String session_name=params[0];
            String session_id=params[1];

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://192.168.0.104/Furnicon/test/node");

            try {

                //get title and body UI elements
                TextView txtName = (TextView) findViewById(R.id.etN);
                TextView txtLast = (TextView) findViewById(R.id.etA);
                TextView txtNumber = (TextView) findViewById(R.id.etNu);

                //extract text from UI elements and remove extra spaces
                String name=txtName.getText().toString().trim();
                String last=txtLast.getText().toString().trim();
                String number=txtNumber.getText().toString().trim();

                //add raw json to be sent along with the HTTP POST request
                StringEntity se = new StringEntity(" { \"title\":\""+name+"\",\"type\":\"registro\",\"field_apellido\":{\"und\":[{ \"value\":\""+last+"\"}]},\"field_numero\":{\"und\":[{  \"value\":\""+number+"\"}]}} ");
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se);

                //" { \"title\":\""+name+"\",\"type\":\"registro\",\"body\":{\"und\":[{ \"value\":\""+body+"\"}]}}"

                BasicHttpContext mHttpContext = new BasicHttpContext();
                CookieStore mCookieStore      = new BasicCookieStore();

                //create the session cookie
                BasicClientCookie cookie = new BasicClientCookie(session_name, session_id);
                cookie.setVersion(0);
                cookie.setDomain("192.168.0.104");
                cookie.setPath("/");
                mCookieStore.addCookie(cookie);
                cookie = new BasicClientCookie("has_js", "1");
                mCookieStore.addCookie(cookie);
                mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

                httpclient.execute(httppost,mHttpContext);

                return 0;

            }catch (Exception e) {
                Log.v("Error adding article",e.getMessage());
            }

            return 0;
        }


        protected void onPostExecute(Integer result) {

            //start the List Activity and pass back the session_id and session_name
            Intent intent = new Intent(activity_add.this, ListActivity.class);
            intent.putExtra("SESSION_ID", session_id);
            intent.putExtra("SESSION_NAME", session_name);
            startActivity(intent);

            //stop the current activity
            finish();
        }
    }
}
