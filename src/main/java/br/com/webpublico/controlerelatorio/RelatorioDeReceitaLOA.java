/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ReceitaLOA;
import br.com.webpublico.negocios.ReceitaLOAFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.SelectEvent;

/**
 * @author major
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-receita-loa", pattern = "/relatorio/receita-loa/", viewId = "/faces/financeiro/relatorio/relatorioreceitaloa.xhtml")
})
public class RelatorioDeReceitaLOA extends AbstractReport implements Serializable {

    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterReceitaLoa;
    private ReceitaLOA receitaLOA;

    @URLAction(mappingId = "relatorio-receita-loa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        receitaLOA = null;
    }

    public List<ReceitaLOA> completaReceitaLOA(String parte) {
        return receitaLOAFacade.listaContaDeReceitaExercLOA(parte, sistemaControlador.getExercicioCorrente());
    }

    public String geraRelatorio() throws JRException, IOException {
        if (receitaLOA == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo Obrigatório.", "Por favor informe o campo Receita Loa para continuar."));
            return "relatorioreceitaloa.xhtml";
        }

        Exercicio e = sistemaControlador.getExercicioCorrente();
        String relatorio = "relatorioReceitaLOA.jasper";
        String subReport1 = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
        subReport1 += "/report/";
        HashMap parameters = new HashMap();
        parameters.put("IDRECEITALOA", receitaLOA.getId());
        parameters.put("SUBREPORT_DIR", subReport1);
        parameters.put("ANO_EXERCICIO", e.getAno().toString());
        parameters.put("MUNICIPIO", "Prefeitura Municipal de Rio Branco - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        gerarRelatorio(relatorio, parameters);

        return "relatorioreceitaloa.xhtml";
    }

    public ConverterAutoComplete getConverterReceitaLoa() {
        if (converterReceitaLoa == null) {
            converterReceitaLoa = new ConverterAutoComplete(ReceitaLOA.class, receitaLOAFacade);
        }
        return converterReceitaLoa;
    }

    public RelatorioDeReceitaLOA() {
    }

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void setaReceitaLoa(SelectEvent evt) {
        ReceitaLOA rLOA = (ReceitaLOA) evt.getObject();
        receitaLOA = rLOA;
    }
}
