package br.com.webpublico.report;


import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;

import java.io.File;

public class JasperDecompile {

    public static void main(String[] args) {
        final File folder = new File("D:/jasper");
        for (final File fileEntry : folder.listFiles()) {
            try {
                System.out.println(fileEntry.getAbsolutePath());
                JasperReport report = (JasperReport) JRLoader.loadObject(fileEntry.getAbsolutePath());
                JRXmlWriter.writeReport(report, fileEntry.getAbsolutePath().replace(".jasper", ".jrxml"), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
