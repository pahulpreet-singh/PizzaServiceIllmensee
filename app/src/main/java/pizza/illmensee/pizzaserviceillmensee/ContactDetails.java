package pizza.illmensee.pizzaserviceillmensee;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class ContactDetails extends AppCompatActivity {

    EditText name, address, number;
    Button order;
    String totalPrice, nam, adrs, nmbr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        setTitle("Lieferadresse");
        initComponents();
        Intent intent = getIntent();
        String pricex[] = intent.getStringExtra("total").split("\\s",0);
        totalPrice = pricex[1] + " " + pricex[2];
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validations()) {
                    nam = name.getText().toString();
                    adrs = address.getText().toString();
                    nmbr = number.getText().toString();
                    new CreatePDF().execute();
                }
            }
        });
    }

    class CreatePDF extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ContactDetails.this,"Schicke Nachricht",
                    "Bitte warten..",false,false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Dismissing the progress dialog
            progressDialog.dismiss();
            //Showing a success message
            Toast.makeText(ContactDetails.this,"Nachricht gesendet",Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetails.this);
            builder.setMessage("Ihre Bestellung wurde hinzugefügt.\nEs würde Ihnen in Kürze geliefert werden.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddToCartHelper.extras.clear();
                            AddToCartHelper.id.clear();
                            AddToCartHelper.categories.clear();
                            AddToCartHelper.name.clear();
                            AddToCartHelper.content.clear();
                            AddToCartHelper.quantity.clear();
                            AddToCartHelper.price.clear();
                            if(AddToCartHelper.id.isEmpty()) {
                                AddToCartHelper.flagAddToCart = false;
                            }
                            startActivity(new Intent(ContactDetails.this,Home.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            dialog.dismiss();
                            finish();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
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

                Font boldItalic = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLDITALIC);
                Font dateFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                Font dateFont1 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
                Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
                //Font bold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

                Paragraph paragraph = new Paragraph("PizzaService Illmensee\n\n", boldItalic);
                document.add(paragraph);

                paragraph = new Paragraph("BESTELLDATUM",dateFont1);
                document.add(paragraph);
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy 'um' HH:mm:ss"); //like "HH:mm" or just "mm", whatever you want
                paragraph = new Paragraph(sdf.format(date) + "\n\n",normalFont);
                document.add(paragraph);

                float[] columnWid = {5f, 2f, 2f};
                PdfPTable customerTable = new PdfPTable(columnWid);
                customerTable.setWidthPercentage(90f);
                PdfPCell cell1 = new PdfPCell(new Phrase("KUNDENDATEN",dateFont));
                customerTable.addCell(cell1);
                cell1 = new PdfPCell(new Phrase("BESTELLÜBERSICHT",dateFont));
                cell1.setColspan(2);
                customerTable.addCell(cell1);
                paragraph = new Paragraph(nmbr + "\n" + nam + "\n" + adrs);
                cell1 = new PdfPCell();
                cell1.addElement(paragraph);
                customerTable.addCell(cell1);
                cell1 = new PdfPCell(new Phrase("Gesamtbetrag"));
                customerTable.addCell(cell1);
                cell1 = new PdfPCell(new Phrase(totalPrice));
                customerTable.addCell(cell1);
                document.add(customerTable);

                paragraph = new Paragraph("\n");
                LineSeparator lineSeparator = new LineSeparator();
                lineSeparator.setOffset(-2);
                paragraph.add(lineSeparator);

                /*StringBuffer order = new StringBuffer();
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

                document.add(paragraph);*/

                float[] columnWidths = {2f, 5f, 2f};
                PdfPTable table = new PdfPTable(columnWidths);
                table.setWidthPercentage(90f);
                table.getDefaultCell().setBorder(0);
                PdfPCell cell;
                cell = new PdfPCell(new Phrase("ANZAHL", dateFont));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("PRODUKT", dateFont));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase("PREIS", dateFont));
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
                table.addCell("");
                cell = new PdfPCell(new Phrase("GESAMT", dateFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                cell = new PdfPCell(new Phrase(totalPrice));
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
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
    }

    private boolean validations() {
        if(name.getText().length()<1) {
            name.setError("Bitte geben Sie Ihren Namen ein");
            return false;
        }
        if(address.getText().length()<5) {
            name.setError("Bitte geben Sie eine gültige Adresse ein");
            return false;
        }
        if(number.getText().length()<10) {
            number.setError("Bitte geben Sie gültige Nummer ein");
            return false;
        }
        return true;
    }

    private void initComponents() {
        name = (EditText) findViewById(R.id.nameeditTextCD);
        address = (EditText) findViewById(R.id.addresseditText2CD);
        number = (EditText) findViewById(R.id.numbereditText3CD);
        order = (Button) findViewById(R.id.orderbuttonCD);
    }
}
