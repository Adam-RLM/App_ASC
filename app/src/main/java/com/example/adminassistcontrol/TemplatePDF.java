package com.example.adminassistcontrol;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TemplatePDF {
    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private Font fSubTitle1 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLUE);
    private Font fSubTitle = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.WHITE);
    private Font fSubTitle2 = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private Font fTextJustificaciones = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    private Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.BLUE);

    public TemplatePDF(Context context) {
        this.context = context;
    }

    public void openDocument(String fecha) {
        createFile(fecha);

        try {
            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

        } catch (Exception e) {
            Log.e("OpenDocument", e.toString());
        }
    }

    public void createFile(String nombre) {
        Log.d("CreateFile", nombre + ".pdf");

        File folder = new File(Environment.getExternalStorageDirectory().toString(), "AssistControlPDFs");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        pdfFile = new File(folder, nombre + ".pdf");
    }

    public void closeDocument() {
        document.close();
    }

    public void addMetaData(String title, String subject, String author) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void addTitles(String title, String subTitle, String date, String hour) {
        try {
            paragraph = new Paragraph();
            addChildP(new Paragraph(title, fTitle));
            addChildP(new Paragraph(subTitle, fSubTitle2));
            addChildP(new Paragraph("Generado: " + date + " | " + hour, fHighText));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addTitles", e.toString());
        }
    }

    private void addChildP(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text) {
        try {
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
        }
    }

    public void createTable(String[] header, ArrayList<String[]> clients) {
        paragraph = new Paragraph();
        paragraph.setFont(fText);
        PdfPTable pdfPTable = new PdfPTable(header.length);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setSpacingBefore(20);
        PdfPCell pdfPCell;
        int indexC = 0;

        while (indexC < header.length) {
            pdfPCell = new PdfPCell(new Phrase(header[indexC++], fSubTitle1));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.GREEN);
            pdfPTable.addCell(pdfPCell);
        }

        for (int indexRow = 0; indexRow < clients.size(); indexRow++) {
            String[] row = clients.get(indexRow);
            for (int indexColumn = 0; indexColumn < header.length; indexColumn++) {
                pdfPCell = new PdfPCell(new Phrase(row[indexColumn]));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                //pdfPCell.setBackgroundColor(BaseColor.RED);
                //pdfPCell.setBorderColor(BaseColor.WHITE);
                pdfPCell.setFixedHeight(40);
                pdfPTable.addCell(pdfPCell);
            }
        }

        try {
            paragraph.add(pdfPTable);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("createTable", e.toString());
        }
    }

    private static boolean compararEntrada1(String hora_Entrada1) {
        String hora = "00:00:00";
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            Date HoraInicio;
            Date HoraEntrada1;

            HoraInicio = dateFormat.parse(hora);
            HoraEntrada1 = dateFormat.parse(hora_Entrada1);

            if (HoraEntrada1.compareTo(HoraInicio) < 8) {
                Log.d("Entrada1", "Entrada1 a tiempo");
                return true;
            } else {
                Log.d("Entrada1", "Entrada1 Tarde");
                return false;
            }

        } catch (ParseException e) {
            Log.d("errorParsear", "no se obtuvo la hora" + e.toString());
            return false;
        }
    }

    public void createTableNoMarca(String[] header2, ArrayList<String[]> usuariosNoMarca) {
        paragraph = new Paragraph();
        paragraph.setFont(fText);
        PdfPTable pdfPTable = new PdfPTable(header2.length);
        pdfPTable.setWidthPercentage(70);
        pdfPTable.setSpacingBefore(5);
        PdfPCell pdfPCell;
        int indexC = 0;

        while (indexC < header2.length) {
            pdfPCell = new PdfPCell(new Phrase(header2[indexC++], fSubTitle));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.RED);
            pdfPTable.addCell(pdfPCell);
        }

        for (int indexRow = 0; indexRow < usuariosNoMarca.size(); indexRow++) {
            String[] row = usuariosNoMarca.get(indexRow);
            for (int indexColumn = 0; indexColumn < header2.length; indexColumn++) {
                pdfPCell = new PdfPCell(new Phrase(row[indexColumn]));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setFixedHeight(40);
                pdfPTable.addCell(pdfPCell);
            }
        }

        try {
            paragraph.add(pdfPTable);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("createTable UNM", e.toString());
        }
    }

    public void createTableJustificaciones(String[] header3, ArrayList<String[]> justificaciones) {
        paragraph = new Paragraph();
        paragraph.setFont(fTextJustificaciones);
        PdfPTable pdfPTable = new PdfPTable(header3.length);
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setSpacingBefore(10);
        PdfPCell pdfPCell;
        int indexC = 0;

        while (indexC < header3.length) {
            pdfPCell = new PdfPCell(new Phrase(header3[indexC++], fSubTitle));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.BLUE);
            pdfPTable.addCell(pdfPCell);
        }

        for (int indexRow = 0; indexRow < justificaciones.size(); indexRow++) {
            String[] row = justificaciones.get(indexRow);
            for (int indexColumn = 0; indexColumn < header3.length; indexColumn++) {
                pdfPCell = new PdfPCell(new Phrase(row[indexColumn]));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setFixedHeight(240);
                pdfPTable.addCell(pdfPCell);
            }
        }

        try {
            paragraph.add(pdfPTable);
            document.add(paragraph);
        } catch (Exception e) {
            Log.e("createTableJustifi", e.toString());
        }
    }

    public void viewPDF() {
        Intent intent = new Intent(context, ViewPDFActivity.class);
        intent.putExtra("path", pdfFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void appViewPDF(Activity activity) {
        if (pdfFile.exists()) {
            Uri uri = Uri.fromFile(pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");

            try {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                activity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                Toast.makeText(activity.getApplicationContext(), "No cuentas con una aplicación para visualizar PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity.getApplicationContext(), "No se encontró el archivo", Toast.LENGTH_SHORT).show();
        }
    }
}
