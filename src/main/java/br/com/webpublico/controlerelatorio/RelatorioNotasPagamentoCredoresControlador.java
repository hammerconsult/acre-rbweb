/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author wiplash
 */
@ManagedBean
@SessionScoped
public class RelatorioNotasPagamentoCredoresControlador extends AbstractReport implements Serializable {

//    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
//    private SistemaControlador sistemaControlador;
    private String observacao;
    private Date dataInicial;
    private Date dataFinal;
    private List<PropostaDeConcessaoDeDiarias> listaProposta;

    public void limpaCampos() {
    }

    public void gerarRelatorio() {
        try {
            String nomeArquivo = "RelatorioBorderoPrincipalDiaria.jasper";
            HashMap parameters = new HashMap();
            parameters.put("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO - AC");
            parameters.put("IMAGEM", getCaminhoImagem());
            parameters.put("VIAS", 1);
            parameters.put("SUBREPORT_DIR", getCaminho());
            parameters.put("OBSERVACAO", observacao);
            gerarRelatorio(nomeArquivo, parameters);
        } catch (JRException | IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
