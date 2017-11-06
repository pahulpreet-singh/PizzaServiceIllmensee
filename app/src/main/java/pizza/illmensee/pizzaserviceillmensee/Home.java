package pizza.illmensee.pizzaserviceillmensee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listView;
    //ArrayList<String> id,categories,name,content,price;
    ProgressDialog progressDialog;
    String[] cats = {"Menüs","Vorspeisen","Salate","Pizza","Pizza, Groß","Pizza, Familien","Pizza, " +
            "Party","Ital. Nudelgerichte","Al Forno","Aufläufe","Fleischgerichte","Thai " +
            "Spezialitäten","Thai Nudeln und Basmatireis","Indische Gerichte","Getränke","Dessert"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AddToCartHelper.flagAddToCart)
                    startActivity(new Intent(Home.this, TheCart3.class));
                else
                    Toast.makeText(Home.this, "Einkaufswagen ist leer", Toast.LENGTH_SHORT).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setScrimColor(getResources().getColor(android.R.color.transparent));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initComponents();
        initHelperArrayList();

        new GetData().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: add put extra
                Intent next = new Intent(Home.this,MenuExpand.class);
                next.putExtra("clickOn",cats[position]);
                startActivity(next);
            }
        });

    }

    private void initComponents() {
        listView = (ListView) findViewById(R.id.list_home);
    }

    class GetData extends AsyncTask<Void, Void, String>{

        String response = "";
        @Override
        protected void onPreExecute() {
            DataArrayListHelper.id = new ArrayList<>();
            DataArrayListHelper.categories = new ArrayList<>();
            DataArrayListHelper.name = new ArrayList<>();
            DataArrayListHelper.content = new ArrayList<>();
            DataArrayListHelper.price = new ArrayList<>();
            progressDialog = new ProgressDialog(Home.this);
            progressDialog.setMessage("Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String link = "http://rupinder2531.000webhostapp.com/GetData.php";

            try {

                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                String line = "";
                while((line = bufferedReader.readLine()) != null) {
                    response = response + line;
                }
                bufferedReader.close();
                inputStream.close();

            } catch (Exception e) {
                Log.e("TAAG", "Error in doInBackground",e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    DataArrayListHelper.id.add(jsonObj.getString("id"));
                    DataArrayListHelper.categories.add(jsonObj.getString("category"));
                    DataArrayListHelper.name.add(jsonObj.getString("name"));
                    DataArrayListHelper.content.add(jsonObj.getString("content"));
                    DataArrayListHelper.price.add(jsonObj.getString("price"));
                }
                listView.setAdapter(new CustomAdapter(Home.this, android.R.layout.simple_list_item_1, cats));
                //Log.e("check",categories.get(0));
            } catch (Exception e) {
                Log.e("taag","Error in post exec",e);
                Toast.makeText(Home.this, "Error. No Ads", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class CustomAdapter extends ArrayAdapter<String>
    {
        Context context;
        public CustomAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater li= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v=li.inflate(R.layout.list_home_content,parent,false);

            TextView tv = (TextView) v.findViewById(R.id.textViewListHome);
            tv.setText(cats[position]);

            return v;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_cart) {
            if(AddToCartHelper.flagAddToCart)
                startActivity(new Intent(Home.this, TheCart3.class));
            else
                Toast.makeText(Home.this, "Einkaufswagen ist leer", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_minOrder) {
            //Toast.makeText(this, "Minimum Order", Toast.LENGTH_SHORT).show();
            new MinOrder(Home.this,"MinOrder");
        } else if (id == R.id.nav_timings) {
            //Toast.makeText(this, "Shop timings", Toast.LENGTH_SHORT).show();
            new MinOrder(Home.this,"ShopTime");
        } else if (id == R.id.nav_reachus) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/place/Pizza+Service+Illmensee/@47.8622106,9.3753481,18z/"));
            startActivity(intent);
        } else if (id == R.id.nav_call) {
            //Toast.makeText(this, "Call us", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+4975589382767"));
            startActivity(intent);
        } else if (id == R.id.nav_terms) {
            startActivity(new Intent(Home.this,TermsConditions.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void initHelperArrayList() {
        AddToCartHelper.id = new ArrayList<>();
        AddToCartHelper.categories = new ArrayList<>();
        AddToCartHelper.name = new ArrayList<>();
        AddToCartHelper.content = new ArrayList<>();
        AddToCartHelper.price = new ArrayList<>();
        AddToCartHelper.quantity = new ArrayList<>();
        AddToCartHelper.extras = new ArrayList<>();
    }
}
