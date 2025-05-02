/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @author WebPublico07
 */
//
//
//        /tributario/cadastromunicipal/relatorio/relatoriodocalculodadivida.xhtml


@ManagedBean(name = "controleRelatorioDoCalculoDaDivida")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoControleRelatorioDoCalculoDaDivida", pattern = "/tributario/cadastromunicipal/relatorio/relatorio-do-calculo-da-divida/", viewId = "/faces/tributario/cadastromunicipal/relatorio/relatoriodocalculodadivida.xhtml"),
})
public class ControleRelatorioDoCalculoDaDivida extends AbstractReport implements Serializable {

    private Divida dividaSelecionada;
    @EJB
    private DividaFacade dividaFacade;
    private List<Divida> listaDividas;
    private Converter converterDivida;

    public List<Divida> listaDividas(String parte) {
        return dividaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public Converter getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, dividaFacade);
        }
        return converterDivida;
    }

    public Divida getDividaSelecionada() {
        return dividaSelecionada;
    }

    public void setDividaSelecionada(Divida dividaSelecionada) {
        this.dividaSelecionada = dividaSelecionada;
    }

    @URLAction(mappingId = "novoControleRelatorioDoCalculoDaDivida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        dividaSelecionada = null;
    }

    public void gerarRelatorioDoCalculo() throws JRException, IOException {
//        String arquivoJasper = "RelatorioDoCalculoDaDivida.jasper";
//
//        String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
//        subReport += "/report/";
//
//        HashMap parameters = new HashMap();
//        parameters.put("DIVIDA_ID", dividaSelecionada.getId());
//        parameters.put("SUB", subReport);
//
//        //System.out.println(dividaSelecionada.getId());
//
//        gerarRelatorio(arquivoJasper, parameters);
    }
}
