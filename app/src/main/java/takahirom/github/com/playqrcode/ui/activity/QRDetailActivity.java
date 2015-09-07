package takahirom.github.com.playqrcode.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import takahirom.github.com.playqrcode.R;

public class QRDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BARCODE = "barcode";

    public static Intent getQRDetailIntent(Context context, Barcode barcode) {
        Intent intent = new Intent(context, QRDetailActivity.class);
        intent.putExtra(EXTRA_BARCODE, barcode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdetail);
        Barcode barcode = getIntent().getExtras().getParcelable(EXTRA_BARCODE);
        barcodeToString(barcode);
        TextView detail = (TextView) findViewById(R.id.detail);
        detail.setText(barcodeToString(barcode));
    }

    private String barcodeToString(Barcode barcode){
        StringBuilder sb = new StringBuilder();

        Class<?> thisClass = null;
        try {
            thisClass = Class.forName(barcode.getClass().getName());

            Field[] aClassFields = thisClass.getDeclaredFields();
            sb.append(barcode.getClass().getSimpleName() + " [\n ");
            for(Field f : aClassFields){
                String fName = f.getName();
                if (!Modifier.isPublic(f.getModifiers())) {
                    continue;
                }
                if (Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                if (f.get(barcode) == null) {
                    continue;
                }
                sb.append("(" + f.getType().getSimpleName() + ") " + fName + " = " + f.get(barcode) + "\n");
            }
            sb.append("]\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qrdetail, menu);
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
    }
}
