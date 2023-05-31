package com.example.productswarehouse.controller;

import com.example.productswarehouse.model.Product;

import com.example.productswarehouse.repository.ProductRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


@Controller
public class FileController {
    @Autowired
    private ProductRepository repository;
    private static final int BUFFER_SIZE = 4096;
    private String filePathPDF = "PDFFile.pdf";
    private String filePathExcel = "ExcelFile.xlsx";

    private enum TypeFile {PDF, EXCEL}


    @GetMapping("/api/download/pdf")
    public void getPDFFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            doDownload(request, response, filePathPDF, TypeFile.PDF);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @GetMapping("/api/download/excel")

    public void getExcelFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            doDownload(request, response, filePathExcel, TypeFile.EXCEL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void doDownload(HttpServletRequest request, HttpServletResponse response, String filePath,
                           TypeFile typeFile) throws IOException, FileNotFoundException {
        if (typeFile == TypeFile.PDF) {
            createPDFFile(filePath);
        } else if (typeFile == TypeFile.EXCEL) {
            createExcelFile(filePath);
        } else
            return;
        ServletContext context = request.getServletContext();
        String appPath = context.getRealPath("");    // construct the complete absolute path of the file
        String fullPath = filePath;
        File downloadFile = new File(fullPath);
        FileInputStream inputStream = new FileInputStream(downloadFile);   // get MIME type of the file
        String mimeType = context.getMimeType(fullPath);
        if (mimeType == null) {       // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
        System.out.println("MIME type: " + mimeType);    // set content attributes for the response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());    // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);    // get output stream of the response
        OutputStream outStream = response.getOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;    // write bytes read from the input stream into the output stream
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        outStream.close();
    }


    private void createExcelFile(String filePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        // spreadsheet object

        XSSFSheet spreadsheet = workbook.createSheet(" Product Data ");    // creating a row object
        XSSFRow row;   // This data needs to be written (Object[])
        Map<String, Object[]> productData
                = new TreeMap<String, Object[]>();

        productData.put(
                "1",
                new Object[]{"Name", "Quantity" ,"Kilogram per piece" ,"Invoice Number", "Expiration Date"});

        int i = 1;
        for (
                Product product : repository.findAll()) {
            productData.put(String.valueOf(i + 1), new Object[]{});
            i++;
        }

        Set<String> keyid = productData.keySet();
        int rowid = 0;
        for (String key : keyid) {
            row = spreadsheet.createRow(rowid++);
            Object[] objectArr = productData.get(key);
            int cellid = 0;
            for (Object obj : objectArr) {
                Cell cell = row.createCell(cellid++);
                cell.setCellValue((String) obj);
            }
        }

        FileOutputStream out = new FileOutputStream(filePath);
        workbook.write(out);
        out.close();
    }

    private void createPDFFile(String filePath) throws IOException {
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(filePath));
            doc.open();
            String text = "Report\n" +
                    "from 07/28/23 to 08/08/23 cadets have a rest\n";
            Paragraph paragraph = new Paragraph(text);
            paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
            paragraph.setFont(new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL));
            doc.add(paragraph);
            PdfPTable table = new PdfPTable(3);
            PdfPCell cell1 = new PdfPCell(new Phrase("Name"));
            PdfPCell cell2 = new PdfPCell(new Phrase("Quantity"));
            PdfPCell cell3 = new PdfPCell(new Phrase("Piece"));
            PdfPCell cell4 = new PdfPCell(new Phrase("Kilograms"));
            PdfPCell cell5 = new PdfPCell(new Phrase("Invoice Number"));
            PdfPCell cell6 = new PdfPCell(new Phrase("Expiration Date"));


            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);


            for (Product product : repository.findAll()) {
                PdfPCell cellName = new PdfPCell(new Phrase(product.getName()));
                PdfPCell cellExpirationDate = new PdfPCell(new Phrase(product.getExpirationDate()));
                PdfPCell cellInvoiceNumber = new PdfPCell(new Phrase(product.getInvoiceNumber()));
                PdfPCell cellQuantity = new PdfPCell(new Phrase(String.valueOf(product.getQuantity())));
                PdfPCell cellKilograms = new PdfPCell(new Phrase(String.valueOf(product.getKilograms())));

                table.addCell(cellName);
                table.addCell(cellExpirationDate);
                table.addCell(cellInvoiceNumber);

                table.addCell(cellQuantity);
                table.addCell(cellKilograms);

            }
            doc.add(table);
        } catch (DocumentException |
                 FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            doc.close();
        }
    }


}


