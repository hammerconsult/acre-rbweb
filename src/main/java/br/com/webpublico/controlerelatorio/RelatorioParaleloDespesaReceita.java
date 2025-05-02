/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRException;

/**
 * @author reidocrime
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-paralelo-despesa-receita", pattern = "/relatorio/paralelo-despesa-receira/", viewId = "/faces/financeiro/relatorio/relatorioparaleloreceitadespesa.xhtml")
})
public class RelatorioParaleloDespesaReceita extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    SistemaControlador sistemaControlador;

    @URLAction(mappingId = "relatorio-paralelo-despesa-receita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCamposRelatorioParalelo() {
    }

    public void gerarRelatorioParaleloDeDespesaSaldos() throws JRException, IOException {
        try {
            String arquivoJasper = "RelParaleloDespesaReceita.jasper";
            HashMap parameters = new HashMap();
            parameters.put("NOME_PREFEITURA", "MUNICÍPIO DE RIO BRANCO - AC");
            parameters.put("ANO_EXERCICIO", "Exercício: " + sistemaControlador.getExercicioCorrente().getAno().toString());
            parameters.put("EXERCICIO_ID", sistemaControlador.getExercicioCorrente().getId());
            parameters.put("USUARIO", sistemaControlador.getUsuarioCorrente().getNome());
            parameters.put("IMAGEM", getCaminhoImagem());
            gerarRelatorio(arquivoJasper, parameters);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
