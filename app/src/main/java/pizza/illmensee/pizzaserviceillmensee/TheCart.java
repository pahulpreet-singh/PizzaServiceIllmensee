package pizza.illmensee.pizzaserviceillmensee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class TheCart extends AppCompatActivity {

    RecyclerView rview;
    Button proceed;
    String FILE;
    TextView totalPrice;
    double sum = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_cart);
        setTitle("Einkaufswagen");
        initComponents();
        calcPrice();

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(new RequestPermissions(TheCart.this).storagePermits())
                    createPDF();
                else
                    Toast.makeText(TheCart.this, "NO permissions", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initComponents() {

        rview = (RecyclerView) findViewById(R.id.rviewtc);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(TheCart.this);
        rview.setLayoutManager(mLayoutManager);
        rview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rview.setAdapter(new CustomAdapterTc(TheCart.this));

        proceed = (Button) findViewById(R.id.orderButtonTC);
        totalPrice = (TextView) findViewById(R.id.totalpricetextViewTC);

    }
    private void calcPrice() {
        if(AddToCartHelper.flagAddToCart) {
            for (int i = 0; i < AddToCartHelper.price.size(); i++) {
                String[] price = AddToCartHelper.price.get(i).split("\\s", 0);
                sum = sum + Double.parseDouble(price[1]);
            }
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            totalPrice.setText("Total: € " + decimalFormat.format(sum));
        }
        else
            totalPrice.setText("Einkaufswagen ist leer");
    }
    class CustomAdapterTc extends RecyclerView.Adapter<CustomAdapterTc.ViewHolder>
    {
        Context context;
        LayoutInflater li;
        CustomAdapterTc(Context context) {
            this.context = context;
            li = LayoutInflater.from(context);
        }

        @Override
        public CustomAdapterTc.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = li.inflate(R.layout.rview_cart_content, parent, false);
            CustomAdapterTc.ViewHolder holder = new CustomAdapterTc.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(CustomAdapterTc.ViewHolder holder, final int position) {
            final int pos = position;
            holder.category.setText(AddToCartHelper.categories.get(position));
            holder.name.setText(AddToCartHelper.name.get(position) + " (#" + AddToCartHelper.id.get(position) + ")");
            holder.quantity.setText("x" + AddToCartHelper.quantity.get(position));
            holder.price.setText(AddToCartHelper.price.get(position));
            holder.content.setText(AddToCartHelper.content.get(position));
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TheCart.this);
                    builder.setMessage("Sind Sie sicher, aus dem Einkaufswagen zu entfernen?")
                            .setTitle("Warnung!");
                    builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddToCartHelper.id.remove(pos);
                            AddToCartHelper.categories.remove(pos);
                            AddToCartHelper.name.remove(pos);
                            AddToCartHelper.price.remove(pos);
                            AddToCartHelper.quantity.remove(pos);
                            AddToCartHelper.content.remove(pos);
                            if(AddToCartHelper.id.isEmpty()) {
                                AddToCartHelper.flagAddToCart = false;
                                Toast.makeText(context, "Einkaufswagen ist leer", Toast.LENGTH_SHORT).show();
                            }
                            rview.setAdapter(new CustomAdapterTc(TheCart.this));
                            calcPrice();
                        }
                    });
                    builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return AddToCartHelper.id.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {

            TextView category,name,quantity,price,content;
            ImageView remove;
            public ViewHolder(View itemView) {
                super(itemView);
                category = (TextView) itemView.findViewById(R.id.categorytextViewtc);
                name = (TextView) itemView.findViewById(R.id.nametextViewtc);
                quantity = (TextView) itemView.findViewById(R.id.quantitytextViewtc);
                price = (TextView) itemView.findViewById(R.id.pricetextViewtc);
                content = (TextView) itemView.findViewById(R.id.contenttextViewtc);
                remove = (ImageView) itemView.findViewById(R.id.removeimageViewtc);
            }
        }
    }

    void createPDF() {
//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/PDF");
//        myDir.mkdir();
//
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        FILE = Environment.getExternalStorageDirectory().toString()
//                +"/PDF/"+ timeStamp +"Name.pdf";

        File storageDir = new File(Environment.getExternalStorageDirectory(), "Pizzeria");
        storageDir.mkdir();
        try {
            File FILEact = File.createTempFile(
                    timeStamp + "psi",  /* prefix */
                    ".pdf",         /* suffix */
                    storageDir);
            FILE = FILEact.getAbsolutePath();
        } catch (IOException e) {
            Log.e("asdfIOFILE","ERROR IN CREATING FILE",e);
        }

        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Open Document for Writting into document
        document.open();

         // User Define Method
        addMetaData(document);
        addTitlePage(document);
        // Close Document after writting all content
        document.close();
        new SendEmailAsync().execute();
    }

    private void addTitlePage(Document document) {
        Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
        Paragraph prHead = new Paragraph();
        prHead.setFont(normal);
        prHead.add(TheOrder());
        try {
            document.add(prHead);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.newPage();
    }

    private String TheOrder() {
        StringBuffer order = new StringBuffer();
        for(int i = 0; i < AddToCartHelper.id.size(); i++)
        {
            order.append(String.valueOf(i+1) + ". ");
            order.append(AddToCartHelper.categories.get(i) + "\n");
            order.append("       " + AddToCartHelper.name.get(i) + " (#" + AddToCartHelper.id.get(i) +
                    ")   " + "x" + AddToCartHelper.quantity.get(i) + "   --- " + AddToCartHelper.price.get(i) + "\n");
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        order.append("\n******    Total: € " + decimalFormat.format(sum) + "    ******");
        Log.e("asdf",order.toString());
        return order.toString();
    }

    private void addMetaData(Document document) {
        document.addTitle("PIZZA_ORDER");
        document.addSubject("Person Info");
        document.addKeywords("PIZZA, ORDER ONLINE, ILLMENSEE");
        document.addAuthor("TAG");
        document.addCreator("TAG");
    }

    class SendEmailAsync extends AsyncTask<Void,Void,Void>
    {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TheCart.this,"Schicke Nachricht",
                    "Bitte warten..",false,false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Dismissing the progress dialog
            progressDialog.dismiss();
            //Showing a success message
            Toast.makeText(TheCart.this,"Nachricht gesendet",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(
                                        "pahul0957@gmail.com", "qqwweerrttyy");
                            }
                        });
                // TODO Auto-generated method stub
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("pahul0957@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("wlubana@gmail.com"));
                message.setSubject("Fax it please");
                message.setText("HI,"
                        + "\n\n great");
                if (!"".equals(FILE)) {
                    Multipart _multipart = new MimeMultipart();
                    BodyPart messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(FILE);

                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(FILE);

                    _multipart.addBodyPart(messageBodyPart);
                    message.setContent(_multipart);
                }
                Transport.send(message);
                Log.e("asdf","Gyi u");
//                    Toast.makeText(TheCart.this, "GYI U", Toast.LENGTH_SHORT).show();
                System.out.println("Done");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}
