/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.negocios.ProcessoCalculoIPTUFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author WebPublico07
 */


@ManagedBean(name = "relatorioGraficoDeCalculosImobiliarios")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoGraficoDeCalculosImobiliarios", pattern = "/tributario/cadastromunicipal/relatorio/relatoriograficodecalculosimobiliarios/", viewId = "/faces/tributario/cadastromunicipal/relatorio/relatoriograficodecalculosimobiliarios.xhtml"),
})
public class RelatorioGraficoDeCalculosImobiliarios extends AbstractReport implements Serializable {

    @Limpavel(Limpavel.NULO)
    private ProcessoCalculoIPTU processoCalculoIPTUSelecionado;
    @Limpavel(Limpavel.ZERO)
    private Integer exercicio;
    private Converter converterProcessoCalculo;
    @EJB
    private ProcessoCalculoIPTUFacade processoCalculoIPTUFacade;

    public Converter getConverterProcessoCalculo() {
        if (converterProcessoCalculo == null) {
            converterProcessoCalculo = new ConverterAutoComplete(ProcessoCalculoIPTU.class, processoCalculoIPTUFacade);
        }
        return converterProcessoCalculo;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public ProcessoCalculoIPTU getProcessoCalculoIPTUSelecionado() {
        return processoCalculoIPTUSelecionado;
    }

    public void setProcessoCalculoIPTUSelecionado(ProcessoCalculoIPTU processoCalculoIPTUSelecionado) {
        this.processoCalculoIPTUSelecionado = processoCalculoIPTUSelecionado;
    }

    public List<ProcessoCalculoIPTU> completaProcessoCalculo(String parte) {
        return processoCalculoIPTUFacade.listaProcessosPorDescricao(parte.trim());
    }

    @URLAction(mappingId = "novoGraficoDeCalculosImobiliarios", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        Util.limparCampos(this);
    }

    public void geraRelatorioViaCalculo(ProcessoCalculoIPTU processo, Exercicio exercicio) throws JRException, IOException {
        processoCalculoIPTUSelecionado = processo;
        this.exercicio = exercicio.getAno();
        gerarRelatorioGrafico();
    }

    public void gerarRelatorioGrafico() throws JRException, IOException {
        if (validaCampos()) {
            String arquivoJasper = "RelatorioGraficoDeCalculosImobiliarios.jasper";
            HashMap parameters = new HashMap();
            parameters.put("PROCESSO", processoCalculoIPTUSelecionado.getId());
            parameters.put("EXERCICIO", exercicio);
            parameters.put("USUARIO", usuarioLogado().getPessoaFisica().getNome());
            //System.out.println(processoCalculoIPTUSelecionado);
            //System.out.println(exercicio);
            if (processoCalculoIPTUSelecionado != null && exercicio != null) {
                gerarRelatorio(arquivoJasper, parameters);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Todos os campos devem conter dados válidos", "Insira os dados nos respectivos campos");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    private boolean validaCampos() {
        boolean valida = true;
        if (processoCalculoIPTUSelecionado == null) {
            FacesUtil.addMessageError("Campo Obrigatório", "Informe o processo de calculo para continuar");
            valida = false;
        }
        if (exercicio == null) {
            FacesUtil.addMessageError("Campo Obrigatório", "Informe o exercicio para continuar");
            valida = false;
        }
        return valida;
    }
}
