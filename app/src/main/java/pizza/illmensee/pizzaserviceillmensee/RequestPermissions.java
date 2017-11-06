package pizza.illmensee.pizzaserviceillmensee;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by multani on 07/08/17.
 */

public class RequestPermissions extends AppCompatActivity{
    Context context;
    RequestPermissions(Context con) {
        context = con;
    }
    public boolean storagePermits() {
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        int storage = ActivityCompat.checkSelfPermission((Activity)context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readStorage = ActivityCompat.checkSelfPermission((Activity)context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions((Activity) context,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1)
        {

            if(grantResults.length>0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                storagePermits();
            }
            else
            {
                Toast.makeText(context, "Please give both permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
