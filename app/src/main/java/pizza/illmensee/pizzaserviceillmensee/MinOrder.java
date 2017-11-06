package pizza.illmensee.pizzaserviceillmensee;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by multani on 01/09/17.
 */

public class MinOrder extends AppCompatActivity {

    MinOrder(Context context, String clickOn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(clickOn.equals("MinOrder")) {
            builder.setTitle("Mindestbestellwert");
            builder.setMessage("ab 5 km:\t10,€\n" +
                    "ab 10 km:\t15,€\n" +
                    "ab 15 km:\t20,€");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        else if(clickOn.equals("ShopTime")) {
            builder.setTitle("Lieferzeiten");
            builder.setMessage("Mo-Sa:\t11.00 - 14.00\t17.00 - 23.00\n" +
                    "So & Feiertage:\t11.00 - 23.00");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        /*else if(clickOn.equals("T&C")) {
            builder.setTitle("Impressung & AGB");
            builder.setPositiveButton("", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }*/
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
