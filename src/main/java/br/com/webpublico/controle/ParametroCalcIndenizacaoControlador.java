/*
 * Codigo gerado automaticamente em Wed Feb 15 16:56:26 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoReferenciaFP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroCalcIndenizacaoFacade;
import br.com.webpublico.negocios.ReferenciaFPFacade;
import br.com.webpublico.negocios.ValorReferenciaFPFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@ManagedBean(name = "parametroCalcIndenizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoParametroCalcIndenizacao", pattern = "/rh/parametro-de-calculo-da-indenizacao/novo/", viewId = "/faces/rh/administracaodepagamento/parametrocalcindenizacao/edita.xhtml"),
        @URLMapping(id = "editarParametroCalcIndenizacao", pattern = "/rh/parametro-de-calculo-da-indenizacao/editar/#{parametroCalcIndenizacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/parametrocalcindenizacao/edita.xhtml"),
        @URLMapping(id = "listarParametroCalcIndenizacao", pattern = "/rh/parametro-de-calculo-da-indenizacao/listar/", viewId = "/faces/rh/administracaodepagamento/parametrocalcindenizacao/lista.xhtml"),
        @URLMapping(id = "verParametroCalcIndenizacao", pattern = "/rh/parametro-de-calculo-da-indenizacao/ver/#{parametroCalcIndenizacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/parametrocalcindenizacao/visualizar.xhtml")
})
public class ParametroCalcIndenizacaoControlador extends PrettyControlador<ParametroCalcIndenizacao> implements Serializable, CRUD {

    @EJB
    private ParametroCalcIndenizacaoFacade parametroCalcIndenizacaoFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    private ConverterAutoComplete converterReferenciaFP;
    @EJB
    private ValorReferenciaFPFacade valorReferenciaFPFacade;
    private BigDecimal valorReferencia;
    private ReferenciaFP referenciaFPSelecionado;

    public ParametroCalcIndenizacaoControlador() {
        super(ParametroCalcIndenizacao.class);
    }

    public ParametroCalcIndenizacaoFacade getFacade() {
        return parametroCalcIndenizacaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroCalcIndenizacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/parametro-de-calculo-da-indenizacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verParametroCalcIndenizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroCalcIndenizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        setReferenciaFPSelecionado();
    }

    @URLAction(mappingId = "novoParametroCalcIndenizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        valorReferencia = new BigDecimal(BigInteger.ZERO);
    }

    public Boolean validaCampos() {
        if (parametroCalcIndenizacaoFacade.existeCodigo(selecionado)) {
            FacesUtil.addError("Atenção !", "O Código do Parâmetro já existe em um outro parâmetro !");
            return false;
        }
        return true;
    }

    public BigDecimal getValorReferencia() {
        return recuperaValorReferenciaFP(referenciaFPSelecionado);
    }

    public void setValorReferencia(BigDecimal valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    public ReferenciaFP getReferenciaFPSelecionado() {
        return referenciaFPSelecionado;
    }

    public void setReferenciaFPSelecionado() {
        referenciaFPSelecionado = selecionado.getReferenciaFP();
    }

    public List<ReferenciaFP> completaReferenciaFP(String parte) {
        return referenciaFPFacade.listaFiltrandoPorTipoDescricao(TipoReferenciaFP.VALOR_VALOR, parte.trim());
    }

    public Converter getConverterReferenciaFP() {
        if (converterReferenciaFP == null) {
            converterReferenciaFP = new ConverterAutoComplete(ReferenciaFP.class, referenciaFPFacade);
        }
        return converterReferenciaFP;
    }

    public void setaReferenciaSelecionado(SelectEvent item) {
        referenciaFPSelecionado = (ReferenciaFP) item.getObject();
    }

    public BigDecimal recuperaValorReferenciaFP(ReferenciaFP referenciaFP) {
        try {
            ValorReferenciaFP valor = valorReferenciaFPFacade.recuperaValorReferenciaFPVigente(referenciaFP);
            return valor.getValor();
        } catch (Exception e) {
            return new BigDecimal(BigInteger.ZERO);
        }
    }

    @Override
    public void cancelar() {
        super.cancelar();
        setReferenciaFPSelecionado();
    }
}
