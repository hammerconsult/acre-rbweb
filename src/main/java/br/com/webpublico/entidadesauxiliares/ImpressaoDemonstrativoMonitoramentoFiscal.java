package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.util.FacesUtil;
import net.sf.jasperreports.engine.JRException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author octavio
 */
public class ImpressaoDemonstrativoMonitoramentoFiscal extends AbstractReport {

    private String semDados = "Não foram encontrados registros para os parâmetros informados.";

    public void imprime(String where, String cmcInicial, String cmcFinal, String monitoramentoInicial, String monitoramentoFinal, String loginUsuario) throws JRException, IOException {
        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport += "/report/";
        String caminhoBrasao = getCaminhoImagem();

        String arquivoJasper = "DemonstrativoMonitoramentoFiscal.jasper";

        HashMap parameters = new HashMap();

        parameters.put("WHERE", where);
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("SEMDADOS", semDados.toString());
        parameters.put("CMCINICIAL", cmcInicial);
        parameters.put("CMCFINAL", cmcFinal);
        parameters.put("MONITORAMENTOINICIAL", monitoramentoInicial);
        parameters.put("MONITORAMENTOFINAL", monitoramentoFinal);
        parameters.put("USUARIO", loginUsuario);
        gerarRelatorio(arquivoJasper, parameters);
    }

    public void imprimeViaMonitoramento(Long id, String loginUsuario) {
        try {
            StringBuilder where = new StringBuilder(" where monitoramento.id = ").append(id.toString());
            geraNoDialog = true;
            imprime(where.toString(), "", "", "", "", loginUsuario);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addError("Atenção!", "Não foi possível gerar o relatório, verifique se o monitoramento fiscal foi devidamente salvo antes de ser impresso");
        }
    }

    public void imprimeViaRegistro(Long id, String loginUsuario) {
        try {
            StringBuilder where = new StringBuilder(" where reg.id = ").append(id.toString());
            geraNoDialog = true;
            imprime(where.toString(), "", "", "", "", loginUsuario);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addError("Atenção!", "Não foi possível gerar o relatório, verifique se o monitoramento fiscal foi devidamente salvo antes de ser impresso");
        }
    }
}
