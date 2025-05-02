/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.negocios.MoedaFacade;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author Andre
 */
//
//


@ManagedBean(name = "controleRelatorioDadosEstatisticosDeLancamentoDeIPTU")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoControleRelatorioDadosEstatisticosDeLancamentoDeIPTU", pattern = "/tributario/cadastromunicipal/relatorio/relatorio-dados-estatisticos-de-lancamento-de-iptu/", viewId = "/faces/tributario/cadastromunicipal/relatorio/relatoriodadosestatisticosdelancamentodeiptu.xhtml"),
})
public class ControleRelatorioDadosEstatisticosDeLancamentoDeIPTU extends AbstractReport {

    @EJB
    private MoedaFacade moedaFacade;
    @Limpavel(Limpavel.NULO)
    private Exercicio exercicio;
    @Limpavel(Limpavel.NULO)
    private String inscricaoInicial;
    @Limpavel(Limpavel.NULO)
    private String inscricaoFinal;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public void gerarRelatorio() throws IOException, JRException {
        if (validaCampos()) {
            String subReport = getCaminho();
            String arquivoJasper = "RelatorioDadosEstatisticosLancamentoIPTU.jasper";
            String caminhoBrasao = getCaminhoImagem();
            HashMap parametros = new HashMap();
            parametros.put("FILTRO_EXERCICIO", exercicio.getAno());
            parametros.put("VALOR_INDICE", moedaFacade.recuperaValorPorExercicio(exercicio.getAno(), 1));
            parametros.put("FILTRO_INSCRICAO_INICIAL", "'" + inscricaoInicial + "'");
            parametros.put("FILTRO_INSCRICAO_FINAL", "'" + inscricaoFinal + "'");
            parametros.put("BRASAO_RIO_BRANCO", caminhoBrasao);
            parametros.put("SUBREPORT_DIR", subReport);
            parametros.put("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin());
            gerarRelatorio(arquivoJasper, parametros);
        }
    }

    private boolean validaCampos() {
        if (exercicio == null || exercicio.getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "impossível continuar", "Exercício não informada"));
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoControleRelatorioDadosEstatisticosDeLancamentoDeIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        exercicio = sistemaControlador.getExercicioCorrente();
        inscricaoInicial = "1";
        inscricaoFinal = "99999999999999";
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
