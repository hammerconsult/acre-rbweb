package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.joda.time.DateTime;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Juliano
 * Date: 26/01/15
 * Time: 17:44
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-resumo-geracao-arquivo-retorno-econsig", pattern = "/relatorio/relatorio-resumo-geracao-arquivo-retorno-econsig/", viewId = "/faces/rh/relatorios/relatorioresumogeracaoarquivoretornoeconsig.xhtml")
})
public class RelatorioResumoGeracaoArquivoEConsigControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Integer mes;
    private Integer ano;

    public RelatorioResumoGeracaoArquivoEConsigControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public void gerarRelatorio() throws JRException, IOException {
        try {
            if (validaCampos()) {
                String caminhoBrasao = getCaminhoImagem();
                String arquivoJasper = "RelatorioResumoGeracaoArquivoEConsig.jasper";
                HashMap parameters = new HashMap();

                DateTime dateTime = new DateTime(new Date());
                DateTime dateMes = new DateTime(getSistemaFacade().getDataOperacao());
                dateTime = dateTime.withMonthOfYear(mes);
                dateTime = dateTime.withYear(ano);
                if (dateMes.getMonthOfYear() == dateTime.getMonthOfYear()) {
                    dateTime.withDayOfMonth(dateMes.getDayOfMonth());
                } else {
                    dateTime = dateTime.withDayOfMonth(dateTime.dayOfMonth().getMaximumValue());
                }

                parameters.put("BRASAO", caminhoBrasao);
                parameters.put("MODULO", "RECURSOS HUMANOS");
                parameters.put("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
                parameters.put("MES", (mes - 1));
                parameters.put("ANO", ano);
                parameters.put("MESANO", getMesAno());
                parameters.put("NOMERELATORIO", "RESUMO DA GERAÇÃO DO ARQUIVO DE RETORNO E-CONSIG ");
                if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                    parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
                } else {
                    parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
                }
                gerarRelatorio(arquivoJasper, parameters);
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    private String getMesAno() {
        return ((String.valueOf(mes).length() <= 1 ? "0" + mes : mes) + "/" + ano);
    }

    @URLAction(mappingId = "relatorio-resumo-geracao-arquivo-retorno-econsig", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.mes = null;
        this.ano = null;
    }

    public boolean validaCampos() {
        if (this.mes == null) {
            FacesUtil.addCampoObrigatorio("Favor informar o campo mês.");
            return false;
        }
        if (this.ano == null) {
            FacesUtil.addCampoObrigatorio("Favor informar o campo ano.");
            return false;
        }
        if (mes == null || (mes != null && mes < 1 || mes > 12)) {
            FacesUtil.addCampoObrigatorio("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
            return false;
        }
        return true;
    }

}
