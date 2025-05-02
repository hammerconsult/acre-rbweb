package br.com.webpublico.util;

import br.com.webpublico.DateUtils;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellFormatType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LeitorExcel {

    private static final Logger logger = LoggerFactory.getLogger(LeitorExcel.class);

    public static void lerExcelGerandoTxt() throws IOException {
        String txt = "/home/dev/db_changelog_nfse_03_id346944-6_wellington.sql";
        String excel = "/home/dev/CONTASCOSIF2025.xlsx";

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
        if (itr.hasNext()) itr.next();
        while (itr.hasNext()) {
            Row row = itr.next();
            if (row.getCell(0) == null)
                break;

            String codigoDoc = getString(row.getCell(6));
            if (!codigoDoc.equals("40100")) continue;
            String codigo = getString(row.getCell(1));
            codigo = StringUtil.retornaApenasNumeros(codigo);
            String descricao = row.getCell(2).getStringCellValue();
            descricao = descricao.replaceAll("[\\s\u00A0]+", " ");
            descricao = descricao.replace("(-)", "");
            descricao = descricao.replace("(+)", "");
            descricao = descricao.trim().toUpperCase();

            inserts.add(String.format("INSERT INTO COSIF (ID, CONTA, DESCRICAO, VERSAO) VALUES (HIBERNATE_SEQUENCE.NEXTVAL, '%s', '%s', 2); \n",
                codigo, descricao));
        }
        for (String value : inserts) {
            writer.write(value);
            writer.write(System.lineSeparator());
        }
        writer.close();
    }

    public static String getString(Cell cell) {
        if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return cell.getStringCellValue();
        } else {
            return new Double(cell.getNumericCellValue()).longValue() + "";
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.10:1521/wpub2", "atualizacaosemanal", "senha10");
    }

    public static void main(String[] args) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = null;
        Integer ultimoExercicio = null;
        Integer rowNum = 0;
        String SQL = "select " +
            "       c.id as cadastro_id," +
            "       c.INSCRICAOCADASTRAL as numero_cmc, " +
            "       coalesce(pf.cpf, pj.cnpj) as cpf_cnpj, " +
            "       coalesce(pf.nome, pj.razaosocial) as nome_razao_social, " +
            "       ec.logradouro||', '||ec.numero||', '||ec.complemento||', '||ec.bairro " +
            "       ||', '||ec.localidade||', '||ec.uf||', '||ec.cep as endereco_completo, " +
            "       case when ef.REGIMETRIBUTARIO = 'SIMPLES_NACIONAL' then 'Sim' else 'Não' end as optacao_simples_nacional, " +
            "       ef.tipoissqn as tipo_issqn, " +
            "       extract(year from sce.DATAALTERACAO) as exercicio, " +
            "       sce.DATAALTERACAO as data_alteracao, " +
            "       (select sum(deb.valororiginal) as total " +
            "           from vwconsultadedebitossemvalores deb " +
            "        where deb.cadastro_id = c.id " +
            "          and deb.situacaoparcela = 'EM_ABERTO') as total_debitos, " +
            "       sce.SITUACAOCADASTRAL as situacao_cadastral " +
            "   from CADASTROECONOMICO c " +
            "  left join pessoafisica pf on pf.id = c.PESSOA_ID " +
            "  left join pessoajuridica pj on pj.id = c.PESSOA_ID " +
            "  left join enderecocadastroeconomico ec on ec.CADASTROECONOMICO_ID = c.ID " +
            "                                        and ec.TIPOENDERECO = 'COMERCIAL' " +
            "  left join enquadramentofiscal ef on ef.CADASTROECONOMICO_ID = c.ID " +
            "                                  and ef.FIMVIGENCIA is null  " +
            "  inner join ce_situacaocadastral rel on rel.CADASTROECONOMICO_ID = c.id " +
            "  inner join SITUACAOCADASTROECONOMICO sce on sce.id = rel.SITUACAOCADASTROECONOMICO_ID " +
            "where extract(year from sce.dataalteracao) between 2020 and 2025 " +
            "  and sce.SITUACAOCADASTRAL in ( '" + SituacaoCadastralCadastroEconomico.BAIXADA.name() + "', '" +
            SituacaoCadastralCadastroEconomico.INATIVO.name() + "', '" + SituacaoCadastralCadastroEconomico.INAPTA.name() + "', '"+
            SituacaoCadastralCadastroEconomico.SUSPENSA.name()+"', '"+SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name()+"') "+
            "order by extract(year from sce.DATAALTERACAO), c.inscricaocadastral, sce.DATAALTERACAO";

        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(SQL);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Integer exercicio = resultSet.getInt("exercicio");
                    if (ultimoExercicio == null || !ultimoExercicio.equals(exercicio)) {
                        ultimoExercicio = exercicio;
                        rowNum = 0;
                        if (sheet != null) {
                            for (int i = 0; i <= 8; i++) {
                                sheet.autoSizeColumn(i);
                            }
                        }
                        sheet = wb.createSheet(exercicio.toString());
                        Row headerRow = sheet.createRow(rowNum++);
                        headerRow.createCell(0).setCellValue("Número do CMC");
                        headerRow.createCell(1).setCellValue("CPF/CNPJ");
                        headerRow.createCell(2).setCellValue("Nome/Razão Social");
                        headerRow.createCell(3).setCellValue("Endereço");
                        headerRow.createCell(4).setCellValue("Optante do Simples Nacional");
                        headerRow.createCell(5).setCellValue("Tipo de ISSQN");
                        headerRow.createCell(6).setCellValue("Data da Alteração");
                        headerRow.createCell(7).setCellValue("Valor Total Original dos Débitos");
                        headerRow.createCell(8).setCellValue("Situação Cadastral");
                    }

                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(resultSet.getString("numero_cmc"));
                    row.createCell(1).setCellValue(resultSet.getString("cpf_cnpj"));
                    row.createCell(2).setCellValue(resultSet.getString("nome_razao_social"));
                    row.createCell(3).setCellValue(resultSet.getString("endereco_completo"));
                    row.createCell(4).setCellValue(resultSet.getString("optacao_simples_nacional"));
                    row.createCell(5).setCellValue(resultSet.getString("tipo_issqn"));
                    row.createCell(6).setCellValue(DateUtils.getDataFormatada(resultSet.getDate("data_alteracao")));
                    row.createCell(7).setCellValue(resultSet.getDouble("total_debitos"));
                    row.createCell(8).setCellValue(SituacaoCadastralCadastroEconomico.valueOf(resultSet.getString("situacao_cadastral")).getDescricao());
                }
                if (sheet != null) {
                    for (int i = 0; i <= 8; i++) {
                        sheet.autoSizeColumn(i);
                    }
                }
            }

            sheet = wb.createSheet("Query");

            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue(SQL);

            try (FileOutputStream fileOut = new FileOutputStream("/home/dev/alteracoesSituacaoCmc.xlsx")) {
                wb.write(fileOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
