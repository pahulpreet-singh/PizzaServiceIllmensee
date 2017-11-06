package pizza.illmensee.pizzaserviceillmensee;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuExpand extends AppCompatActivity {

    String clickOn;
    RecyclerView rview;
    ArrayList<String> iName,iContent,iPrice,iID;
    Button order;
    //String flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_expand);
        clickOn = getIntent().getStringExtra("clickOn");
        setTitle(clickOn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initComponents();
        initData();
        rview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rview.setAdapter(new CustomAdapter(MenuExpand.this));
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AddToCartHelper.flagAddToCart)
                    startActivity(new Intent(MenuExpand.this, TheCart3.class));
                else
                    Toast.makeText(MenuExpand.this, "Einkaufswagen ist leer", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initData() {
        for(int i = 0; i < DataArrayListHelper.categories.size();i++)
        {
            Log.e("data",DataArrayListHelper.categories.get(i));
            Log.e("mata",clickOn);
            if(DataArrayListHelper.categories.get(i).equals(clickOn))
            {
                iName.add(DataArrayListHelper.name.get(i));
                iPrice.add(DataArrayListHelper.price.get(i));
                iContent.add(DataArrayListHelper.content.get(i));
                iID.add(DataArrayListHelper.id.get(i));
            }
        }
        if(iName.size()-1<1)
        {
            iName.add("Sorry No Data Available");
            iPrice.add(" ");
            iContent.add(" ");
            iID.add(" ");
            Toast.makeText(MenuExpand.this, "SORRY NO DATA AVAILABLE", Toast.LENGTH_SHORT).show();
        }
    }

    private void initComponents() {
        rview = (RecyclerView) findViewById(R.id.rviewME);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(MenuExpand.this);
        rview.setLayoutManager(mLayoutManager);

        iName = new ArrayList<>();
        iPrice = new ArrayList<>();
        iContent = new ArrayList<>();
        iID = new ArrayList<>();

        order = (Button) findViewById(R.id.orderButtonMe);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>
    {
        Context context;
        LayoutInflater li;
        CustomAdapter(Context context) {
            this.context = context;
            li = LayoutInflater.from(context);
        }

        @Override
        public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = li.inflate(R.layout.rview_me_content, parent, false);
            CustomAdapter.MyViewHolder holder = new CustomAdapter.MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(CustomAdapter.MyViewHolder holder, int position) {
            //holder.name.setText(iName.get(position) + " (#" + iID.get(position) + ")");
            holder.name.setText(iID.get(position) + "   " + iName.get(position));
            holder.content.setText(iContent.get(position));
            holder.price.setText(iPrice.get(position));
            RecyclerViewClickSupport.addTo(rview).setOnItemClickListener(new RecyclerViewClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    //Toast.makeText(MenuExpand.this, "xyz" + position, Toast.LENGTH_SHORT).show();
                    HashMap<String, String> setOrder = new HashMap<String, String>();
                    setOrder.put("id",iID.get(position));
                    setOrder.put("category",clickOn);
                    setOrder.put("name",iName.get(position));
                    setOrder.put("content",iContent.get(position));
                    setOrder.put("price",iPrice.get(position));

                    FragmentManager fm = getFragmentManager();
                    OrderDialog orderDialog = OrderDialog.newInstance(setOrder);
                    orderDialog.show(fm, "OrderDialog");
                }
            });
        }

        @Override
        public int getItemCount() {
            return iName.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name,price,content;
            public MyViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.NametextViewMe);
                price = (TextView) itemView.findViewById(R.id.pricetextViewME);
                content = (TextView) itemView.findViewById(R.id.contenttextViewME);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
