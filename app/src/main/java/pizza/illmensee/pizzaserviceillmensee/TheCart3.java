package pizza.illmensee.pizzaserviceillmensee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 * Created by multani on 09/08/17.
 */

public class TheCart3 extends AppCompatActivity {
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
                if(AddToCartHelper.flagAddToCart)
                    startActivity(new Intent(TheCart3.this,ContactDetails.class).putExtra("total",totalPrice.getText().toString()));
//                    new CreatePDF().execute();
                else
                    Toast.makeText(TheCart3.this, "Einkaufswagen ist leer", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initComponents() {

        rview = (RecyclerView) findViewById(R.id.rviewtc);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(TheCart3.this);
        rview.setLayoutManager(mLayoutManager);
        rview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        rview.setAdapter(new CustomAdapterTc(TheCart3.this));

        proceed = (Button) findViewById(R.id.orderButtonTC);
        totalPrice = (TextView) findViewById(R.id.totalpricetextViewTC);

    }
    private void calcPrice() {
        sum = 0;
        if(AddToCartHelper.flagAddToCart) {
            for (int i = 0; i < AddToCartHelper.price.size(); i++) {
                String[] price = AddToCartHelper.price.get(i).split("\\s", 0);
                sum = sum + Double.parseDouble(price[1]);
            }
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            totalPrice.setText("Gesamtbetrag: € " + decimalFormat.format(sum));
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
            holder.extras.setText(AddToCartHelper.extras.get(position));
            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TheCart3.this);
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
                            AddToCartHelper.extras.remove(pos);
                            if(AddToCartHelper.id.isEmpty()) {
                                AddToCartHelper.flagAddToCart = false;
                                Toast.makeText(context, "Einkaufswagen ist leer", Toast.LENGTH_SHORT).show();
                            }
                            rview.setAdapter(new CustomAdapterTc(TheCart3.this));
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

            TextView category,name,quantity,price,content,extras;
            ImageView remove;
            public ViewHolder(View itemView) {
                super(itemView);
                category = (TextView) itemView.findViewById(R.id.categorytextViewtc);
                name = (TextView) itemView.findViewById(R.id.nametextViewtc);
                quantity = (TextView) itemView.findViewById(R.id.quantitytextViewtc);
                price = (TextView) itemView.findViewById(R.id.pricetextViewtc);
                content = (TextView) itemView.findViewById(R.id.contenttextViewtc);
                remove = (ImageView) itemView.findViewById(R.id.removeimageViewtc);
                extras = (TextView) itemView.findViewById(R.id.extraTextViewtc);
            }
        }
    }
    /*class CreatePDF extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TheCart3.this,"Schicke Nachricht",
                    "Bitte warten..",false,false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Dismissing the progress dialog
            progressDialog.dismiss();
            //Showing a success message
            Toast.makeText(TheCart3.this,"Nachricht gesendet",Toast.LENGTH_LONG).show();
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

                ByteArrayOutputStream outputStream = null;

                //construct the text body part
                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText("clicksend");

                //now write the PDF content to the output stream
                outputStream = new ByteArrayOutputStream();
                //writePdf(outputStream);
                Document document = new Document();
                PdfWriter.getInstance(document, outputStream);

                document.open();

                document.addTitle("PIZZA_ORDER");
                document.addSubject("Person Info");
                document.addKeywords("PIZZA, ORDER ONLINE, ILLMENSEE");
                document.addAuthor("TAG");
                document.addCreator("TAG");

                Paragraph paragraph = new Paragraph();

                *//*StringBuffer order = new StringBuffer();
                for(int i = 0; i < AddToCartHelper.id.size(); i++)
                {
                    order.append(String.valueOf(i+1) + ". ");
                    order.append(AddToCartHelper.categories.get(i) + "\n");
                    order.append("       " + AddToCartHelper.name.get(i) + " (#" + AddToCartHelper.id.get(i) +
                            ")   " + "x" + AddToCartHelper.quantity.get(i) + "   --- " + AddToCartHelper.price.get(i) + "\n");
                }
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                order.append("\n******    Total: € " + decimalFormat.format(sum) + "    ******");
                paragraph.add(order.toString());

                document.add(paragraph);*//*

                float[] columnWidths = {2f, 5f, 2f};
                PdfPTable table = new PdfPTable(columnWidths);
                table.setWidthPercentage(90f);
                table.getDefaultCell().setBorder(0);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase("ANZAHL"));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("PRODUKT"));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("PREIS"));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
//                table.addCell("ANZAHL");
//                table.addCell("");
//                table.addCell("PREIS");
                for(int i = 0; i<AddToCartHelper.id.size(); i++) {
                    cell = new PdfPCell(new Phrase(AddToCartHelper.quantity.get(i) + "x"));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);
//                    table.addCell(AddToCartHelper.quantity.get(i) + "x");
                    Paragraph paragraph1 = new Paragraph("[" + AddToCartHelper.categories.get(i) + "]  " +
                            "" + AddToCartHelper.name.get(i) + " " + "(" + AddToCartHelper.id.get(i) + ")\n" +
                            "" + AddToCartHelper.extras.get(i));
                    cell = new PdfPCell();
                    cell.addElement(paragraph1);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);
//                    table.addCell("[" + AddToCartHelper.categories.get(i) + "]  " + AddToCartHelper.name.get(i) + " (" +
//                            AddToCartHelper.id.get(i) + ")\n" + AddToCartHelper.extras.get(i));
                    cell = new PdfPCell(new Phrase(AddToCartHelper.price.get(i)));
//                    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);
//                    table.addCell(AddToCartHelper.price.get(i));
                }
                document.add(table);

                document.close();
                byte[] bytes = outputStream.toByteArray();

                DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
                MimeBodyPart pdfBodyPart = new MimeBodyPart();
                pdfBodyPart.setDataHandler(new DataHandler(dataSource));
                pdfBodyPart.setFileName("print_it.pdf");

                //construct the mime multi part
                MimeMultipart mimeMultipart = new MimeMultipart();
                mimeMultipart.addBodyPart(textBodyPart);
                mimeMultipart.addBodyPart(pdfBodyPart);

                //create the sender/recipient addresses
                InternetAddress iaSender = new InternetAddress("pahul0957@gmail.com");
                InternetAddress iaRecipient = new InternetAddress("wlubana@gmail.com");

                //construct the mime message
                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setSender(iaSender);
                mimeMessage.setSubject("Forward this fax please");
                mimeMessage.setRecipient(Message.RecipientType.TO, iaRecipient);
                mimeMessage.setContent(mimeMultipart);

                //send off the email
                Transport.send(mimeMessage);

            }
            catch (Exception e)
            {
                Log.e("asdfAsync","Error in creating/sending pdf",e);
            }
            return null;
        }

        private void insertCell() {
            //PdfPCell cell =  new PdfPCell(new Phrase())
        }
    }*/
}
