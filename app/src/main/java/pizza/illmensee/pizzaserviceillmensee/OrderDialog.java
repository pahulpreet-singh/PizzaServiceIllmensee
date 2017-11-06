package pizza.illmensee.pizzaserviceillmensee;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by multani on 03/08/17.
 */

public class OrderDialog extends DialogFragment {

    TextView order,quantity,price,content,pizzaTopTV,noodleTV,saladDressTv;
    ImageView addq,removeq;
    Button addtocart;
    GridView pizzaTopGV;
    Spinner saladDressS,noodlesS;
    CheckBox cheeseBake;
    EditText comments;
    HashMap<String, String> getOrder;
    double theprice;
    int pizzaCount = 0;
    int noodleCheck;
    boolean[] cbCheck = {false, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false};
    ArrayList<String> selectedPizzaExtra;

    String[] pizzaTopArray = {"Vorderschinken","Speck","Salami","Mais","Champignons","Schafskäse","Artischocken","Kapern","Thunfisch","" +
            "Sardellen","Tintenfisch","Muscheln","Ei","Hackfleisch","Peperoni","Paprika","Jalapenos","Zwiebeln","Oliven","Ananas","Gorgonzola","" +
            "Extra-Käse","frischen Tomaten","Knoblauch","Thunfisch","Krabben","Mozzarella","Broccoli","Bohnen","Spinat"};

    String[] saladDressArray = {"hausgemachtes Joghurt-Dressing (Standard)","Essig & Öl"};

    String[] noodlesArray = {"Spaghetti", "Rigatoni", "Tortellini", "Gnocchi"};

    static OrderDialog newInstance(HashMap<String, String> order) {
        OrderDialog f = new OrderDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("order", order);
        f.setArguments(args);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_orderdialog,container,false);

        getOrder = new HashMap<>();
        getOrder = (HashMap<String, String>) getArguments().getSerializable("order");

        order = (TextView) v.findViewById(R.id.ordertextview);
        quantity = (TextView) v.findViewById(R.id.quantitytextView);
        price = (TextView) v.findViewById(R.id.pricetextViewod);
        content = (TextView) v.findViewById(R.id.contenttextViewOD);
        addq = (ImageView) v.findViewById(R.id.addimageView7);
        removeq = (ImageView) v.findViewById(R.id.removeimageView9);
        addtocart = (Button) v.findViewById(R.id.addtocartbutton);
        pizzaTopTV = (TextView) v.findViewById(R.id.pizzaToptextView2COD);
        pizzaTopGV = (GridView) v.findViewById(R.id.pizzaTopListCOD);
        saladDressTv = (TextView) v.findViewById(R.id.saladDresstextView3COD);
        saladDressS = (Spinner) v.findViewById(R.id.saladDressSpinnerCOD);
        noodleTV = (TextView) v.findViewById(R.id.noodlestextView5COD);
        noodlesS = (Spinner) v.findViewById(R.id.noodlesSpinner2COD);
        cheeseBake = (CheckBox) v.findViewById(R.id.cheeseBakecheckBoxCOD);
        comments = (EditText) v.findViewById(R.id.commentsEditTextCOD);
        selectedPizzaExtra = new ArrayList<>();
        cheeseBake.setVisibility(View.GONE);
        pizzaTopGV.setVisibility(View.GONE);
        pizzaTopTV.setVisibility(View.GONE);
        noodlesS.setVisibility(View.GONE);
        noodleTV.setVisibility(View.GONE);
        saladDressS.setVisibility(View.GONE);
        saladDressTv.setVisibility(View.GONE);


        //check if clicked on pizza, noodles, menus or salad category.
        //if ^ so, then set visibility of views accordingly
        setViews();

        //set order details
        order.setText(getOrder.get("name") + " (#" + getOrder.get("id") + ")");
        setPrice();
        content.setText(getOrder.get("content"));

        addq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addquantity();
            }
        });
        removeq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removequantity();
            }
        });
        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddToCartHelper.flagAddToCart = true;
                AddToCartHelper.id.add(getOrder.get("id"));
                AddToCartHelper.categories.add(getOrder.get("category"));
                AddToCartHelper.content.add(content.getText().toString());
                AddToCartHelper.name.add(getOrder.get("name"));
                AddToCartHelper.quantity.add(quantity.getText().toString());
                AddToCartHelper.price.add(price.getText().toString());
                if(getOrder.get("category").equals("Menüs")) {
                    AddToCartHelper.extras.add(getSelectedPizzaExtras() +
                            "\nSalatsoße: " + saladDressS.getSelectedItem().toString() + ".\n" +
                            "" + getComments());
                }
                else if(getOrder.get("category").equals("Salate")) {
                    AddToCartHelper.extras.add("Salatsoße: " + saladDressS.getSelectedItem().toString() + ".\n" +
                            "" + getComments());
                }
                else if(getOrder.get("category").equals("Pizza") ||
                        getOrder.get("category").equals("Pizza, Groß") ||
                        getOrder.get("category").equals("Pizza, Familien") ||
                        getOrder.get("category").equals("Pizza, Party")) {
                    AddToCartHelper.extras.add(getSelectedPizzaExtras() + "\n" + getComments());
                }
                else if(getOrder.get("category").equals("Ital. Nudelgerichte")) {
                    if(noodleCheck == 1) {
                        AddToCartHelper.extras.add("mit Käse überbacken.\nNudeltyp: " + noodlesS.getSelectedItem().toString()
                                + ".\n" + getComments());
                    }
                    else
                        AddToCartHelper.extras.add("Nudeltyp: " + noodlesS.getSelectedItem().toString()
                                + ".\n" + getComments());
                }
                else {
                    AddToCartHelper.extras.add(getComments());
                }
                Toast.makeText(getActivity(), "Zum Einkaufswagen hinzugefügt", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        /*pizzaTopGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkBox = (CheckBox) view.findViewById(R.id.pizzaToppingcheckBoxCPT);
                //Toast.makeText(getActivity(), "click", Toast.LENGTH_SHORT).show();
                if(checkBox.isChecked())
                    checkBox.setChecked(false);
                else
                    checkBox.setChecked(true);
            }
        });*/



        return v;
    }

    private void setViews() {
        if(getOrder.get("category").equals("Menüs")) {
            saladDressS.setVisibility(View.VISIBLE);
            saladDressTv.setVisibility(View.VISIBLE);
            pizzaTopGV.setVisibility(View.VISIBLE);
            pizzaTopTV.setVisibility(View.VISIBLE);
            setPizzaView();
            ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,saladDressArray);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            saladDressS.setAdapter(aa);
        }
        else if(getOrder.get("category").equals("Salate")) {
            saladDressS.setVisibility(View.VISIBLE);
            saladDressTv.setVisibility(View.VISIBLE);
            ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,saladDressArray);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            saladDressS.setAdapter(aa);
        }
        else if(getOrder.get("category").equals("Pizza") ||
                getOrder.get("category").equals("Pizza, Groß") ||
                getOrder.get("category").equals("Pizza, Familien") ||
                getOrder.get("category").equals("Pizza, Party")) {
            pizzaTopGV.setVisibility(View.VISIBLE);
            pizzaTopTV.setVisibility(View.VISIBLE);
            setPizzaView();
        }
        else if(getOrder.get("category").equals("Ital. Nudelgerichte")) {
            noodlesS.setVisibility(View.VISIBLE);
            noodleTV.setVisibility(View.VISIBLE);
            cheeseBake.setVisibility(View.VISIBLE);
            cheeseBake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        noodleCheck = 1;
                        priceChangeCount(Integer.parseInt(quantity.getText().toString()));
                    }
                    else if(!isChecked) {
                        noodleCheck = 0;
                        priceChangeCount(Integer.parseInt(quantity.getText().toString()));
                    }
                }
            });
            ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,noodlesArray);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            noodlesS.setAdapter(aa);
        }
        if(getOrder.get("id").equals("16")) {
            pizzaTopGV.setVisibility(View.GONE);
            pizzaTopTV.setVisibility(View.GONE);
            noodlesS.setVisibility(View.GONE);
            noodleTV.setVisibility(View.GONE);
            saladDressS.setVisibility(View.GONE);
            saladDressTv.setVisibility(View.GONE);
        }
    }

    void setPizzaView() {
        pizzaTopGV.setAdapter(new GVAdap(getActivity(),android.R.layout.simple_list_item_1,pizzaTopArray));
    }

    class GVAdap extends ArrayAdapter<String> {

        Context context;
        private final boolean[] mCheckedState;
        private final Context mContext;
        public GVAdap(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            mCheckedState = new boolean[objects.length];
            mContext = context;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater li= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v=li.inflate(R.layout.content_pizza_topping,parent,false);

            final CheckBox topping = (CheckBox) v.findViewById(R.id.pizzaToppingcheckBoxCPT);
            topping.setText(pizzaTopArray[position]);
            topping.setChecked(cbCheck[position]);

            topping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        cbCheck[position] = true;
                        selectedPizzaExtra.add(topping.getText().toString());
                        pizzaCount++;
                        priceChangeCount(Integer.parseInt(quantity.getText().toString()));
                    }
                    else
                    {
                        cbCheck[position] = false;
                        selectedPizzaExtra.remove(topping.getText().toString());
                        pizzaCount--;
                        priceChangeCount(Integer.parseInt(quantity.getText().toString()));
                    }
                }
            });
            return v;
        }
    }

    private void removequantity() {
        int count = Integer.parseInt(quantity.getText().toString());
        count--;
        if(count<=1)
        {
            quantity.setText("1");
            priceChangeCount(1);
        }
        else {
            quantity.setText(String.valueOf(count));
            priceChangeCount(count);
        }
    }

    private void addquantity() {
        int count = Integer.parseInt(quantity.getText().toString());
        count++;
        priceChangeCount(count);
        quantity.setText(String.valueOf(count));
    }
    void priceChangeCount(int count) {
        String price1 = getOrder.get("price");
        String[] price2 = price1.split("\\s",0);
        String[] price3 = price2[1].split(",");
        theprice = Double.parseDouble((new StringBuilder()).append(price3[0]).append(".")
                .append(price3[1]).toString());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        theprice = theprice * count;
        double finalPrice = 0;
        //price.setText("€ " + decimalFormat.format(theprice*count));
        if(getOrder.get("category").equals("Pizza") ||
                getOrder.get("category").equals("Pizza, Groß")) {
            finalPrice = theprice + (count * 0.5 * pizzaCount);
        }
        else if(getOrder.get("category").equals("Pizza, Familien") ||
                getOrder.get("category").equals("Pizza, Party")) {
            finalPrice = theprice + (count * pizzaCount);
        }
        else if(getOrder.get("category").equals("Ital. Nudelgerichte")) {
            finalPrice = theprice + (count * noodleCheck);
        }
        else {
            finalPrice = theprice;
        }
        price.setText("€ " + decimalFormat.format(finalPrice));

    }
    String getComments() {
        if(comments.getText().toString().length()<1) {
            return "";
        } else {
            return "Kommentare: " + comments.getText().toString();
        }
    }
    String getSelectedPizzaExtras() {
        if (selectedPizzaExtra.size() > 0) {
            StringBuilder extra = new StringBuilder();
            extra.append("Pizza Zutaten:");
            for (int i = 0; i<selectedPizzaExtra.size();i++) {
                extra.append(" + " + selectedPizzaExtra.get(i));
            }
            extra.append(".");
            return extra.toString();
        }
        else
            return "";
    }
    void setPrice() {
        String price1 = getOrder.get("price");
        String[] price2 = price1.split("\\s",0);
        String[] price3 = price2[1].split(",");
        price.setText("€ " + new StringBuilder().append(price3[0]).append(".")
                .append(price3[1]).toString());
    }


}
