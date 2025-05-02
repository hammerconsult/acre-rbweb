package br.com.webpublico.report;


import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JasperCompileManager;

import java.io.File;
import java.util.List;

public class JasperCompile {
   // C:\tools\projetos\webpublico\src\main\webapp\WEB-INF\report
    //C:\tools\projetos\webpublico\src\main\resources\br\com\webpublico\report
    public static void main(String[] args) {
        final String folderDestino = "C:\\tools\\projetos\\webpublico\\src\\main\\webapp\\WEB-INF\\report\\";
        final File folderOrigem = new File("C:\\tools\\projetos\\webpublico\\src\\main\\resources\\br\\com\\webpublico\\report");
        List<String> naoCompilados = Lists.newArrayList();
        for (final File fileEntry : folderOrigem.listFiles()) {
            try {
                System.out.println(fileEntry.getAbsolutePath());
                JasperCompileManager.compileReportToFile(
                    fileEntry.getAbsolutePath(),
                    folderDestino + fileEntry.getName().replace(".jrxml", ".jasper")
                );
            } catch (Exception e) {
                e.printStackTrace();
                naoCompilados.add(fileEntry.getAbsolutePath());
            }
        }

        System.out.println("Finalizou!");
        for (String naoCompilado : naoCompilados) {
            System.out.println("Não pode ser compilado:: " +naoCompilado);
        }


    }
}
