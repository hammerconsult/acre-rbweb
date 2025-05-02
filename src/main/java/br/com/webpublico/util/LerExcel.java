package br.com.webpublico.util;

import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class LerExcel {

    private static final Logger logger = LoggerFactory.getLogger(LerExcel.class);

    public static void lerExcelGerandoInsertTxt() throws IOException {
        String txt = "/home/pc-mga/Downloads/lotes_alagacao_rioacre_2023.txt";
        String excel = "/home/pc-mga/Downloads/lotes_alagacao_rioacre_2023.xlsx";

        File file = new File(txt);
        if (file.exists()) {
            file.delete();
        }
        FileInputStream fis = new FileInputStream(new File(excel));
        FileWriter writer = new FileWriter(txt);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> itr = sheet.iterator();
        List<String> inserts = Lists.newArrayList();
        while (itr.hasNext()) {
            Row row = itr.next();
            if (row.getCell(0) == null)
                break;

            String distrito = String.format("%.0f", row.getCell(1).getNumericCellValue());
            String setor = String.format("%.0f", row.getCell(2).getNumericCellValue());
            String quadra = String.format("%.0f", row.getCell(3).getNumericCellValue());
            String lote = String.format("%.0f", row.getCell(4).getNumericCellValue());

            inserts.add(" INSERT INTO ALAGACAO2023LOTES (DISTRITO, SETOR, QUADRA, LOTE) " +
                " VALUES ('" + distrito + "', '" + setor + "', '" + quadra + "', '" + lote + "'); ");
        }
        for (String value : inserts) {
            writer.write(value);
            writer.write(System.lineSeparator());
        }
        writer.close();
    }


    public static void lerExcelGerandoInsertBanco() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@//172.16.0.148:1521/wp12ch",
                "preproducao", "senha10");
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(" INSERT INTO chamado343842 (inscricao) " +
                " VALUES (?) ");

            String excel = "/home/dev/Downloads/Relação de isentos automaticamente IPTU alagação.xlsx";

            FileInputStream fis = new FileInputStream(new File(excel));
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(4);
            Iterator<Row> itr = sheet.iterator();
            if (itr.hasNext()) {
                itr.next();
            }
            int count = 0;
            while (itr.hasNext()) {
                Row row = itr.next();
                if (row.getCell(0) == null)
                    break;

                String inscricao = String.format("%.0f", row.getCell(16).getNumericCellValue());

                ps.clearParameters();
                ps.setString(1, inscricao);
                ps.addBatch();
                count++;
                if (count % 100 == 1) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fecharConnection(connection);
            fecharPreparedStatement(ps);
        }
    }

    private static void fecharConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    private static void fecharPreparedStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {

            }
        }
    }

    public static void main(String[] args) {
        lerExcelGerandoInsertBanco();
    }
}
