/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.IndiceEconomico;
import br.com.webpublico.entidades.MultaFiscalizacao;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.enums.FormaCalculoMultaFiscalizacao;
import br.com.webpublico.enums.IncidenciaMultaFiscalizacao;
import br.com.webpublico.enums.TipoCalculoMultaFiscalizacao;
import br.com.webpublico.enums.TipoMultaFiscalizacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MultaFiscalizacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@ManagedBean(name = "multaFiscalizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMultaProcessoFiscal", pattern = "/multa-processo-fiscal/novo/", viewId = "/faces/tributario/fiscalizacao/multa/edita.xhtml"),
        @URLMapping(id = "listarMultaProcessoFiscal", pattern = "/multa-processo-fiscal/listar/", viewId = "/faces/tributario/fiscalizacao/multa/lista.xhtml"),
        @URLMapping(id = "verMultaProcessoFiscal", pattern = "/multa-processo-fiscal/ver/#{multaFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacao/multa/visualizar.xhtml"),
        @URLMapping(id = "editarMultaProcessoFiscal", pattern = "/multa-processo-fiscal/editar/#{multaFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacao/multa/edita.xhtml")
})
public class MultaFiscalizacaoControlador extends PrettyControlador<MultaFiscalizacao> implements Serializable, CRUD {


    @EJB
    private MultaFiscalizacaoFacade multaFiscalizacaoFacade;
    private ConverterAutoComplete converterTributo;

    public MultaFiscalizacaoControlador() {
        super(MultaFiscalizacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return multaFiscalizacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/multa-processo-fiscal/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @URLAction(mappingId = "novoMultaProcessoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        this.selecionado.setCodigo(multaFiscalizacaoFacade.ultimoCodigoMaisUm());
    }

    @Override
    @URLAction(mappingId = "verMultaProcessoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarMultaProcessoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getIncidenciasMultaFiscalizacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (IncidenciaMultaFiscalizacao object : IncidenciaMultaFiscalizacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposMultaFiscalizacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoMultaFiscalizacao object : TipoMultaFiscalizacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposCalculoMultaFiscalizacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoCalculoMultaFiscalizacao object : TipoCalculoMultaFiscalizacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getFormasCalculoMultaFiscalizacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (FormaCalculoMultaFiscalizacao object : FormaCalculoMultaFiscalizacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getBasesCalculoMultaFiscalizacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (MultaFiscalizacao.BaseCalculo object : MultaFiscalizacao.BaseCalculo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    private boolean validaCampos() {
        if (selecionado.getValorMulta() == null) {
            selecionado.setValorMulta(BigDecimal.ZERO);
        }

        if (selecionado.getAliquotaMulta() == null) {
            selecionado.setAliquotaMulta(BigDecimal.ZERO);
        }

        boolean retorno = true;
        if (selecionado.getCodigo() == null || selecionado.getCodigo() <= 0) {
            FacesUtil.addCampoObrigatorio("Informe o Código.");
            retorno = false;
        } else if (selecionado.getId() == null && multaFiscalizacaoFacade.existeCodigo(selecionado.getCodigo())) {
            selecionado.setCodigo(multaFiscalizacaoFacade.ultimoCodigoMaisUm());
            FacesUtil.addAtencao("O Código informado já está em uso em outro registro. O sistema gerou um novo código. Por favor, pressione o botão SALVAR novamente.");
            retorno = false;
        } else if (selecionado.getId() != null && !multaFiscalizacaoFacade.existeCodigoMultaFiscalizacao(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("O Código informado já existe.");
            retorno = false;
        }
        if (selecionado.getArtigo() == null || "".equals(selecionado.getArtigo())) {
            FacesUtil.addCampoObrigatorio("Informe a Descrição.");
            retorno = false;
        }
        if (selecionado.getIncidenciaMultaFiscalizacao() == null) {
            FacesUtil.addCampoObrigatorio("Informe a Incidência da Multa.");
            retorno = false;
        }
        if (selecionado.getTipoMultaFiscalizacao() == null) {
            FacesUtil.addCampoObrigatorio("Informe o Tipo da Multa.");
            retorno = false;
        }
        if (selecionado.getTipoCalculoMultaFiscalizacao() == null) {
            FacesUtil.addCampoObrigatorio("Informe o Tipo de Cálculo da Multa.");
            retorno = false;
        }
        if (selecionado.getFormaCalculoMultaFiscalizacao() == null) {
            FacesUtil.addCampoObrigatorio("Informe a Forma de Cálculo da Multa.");
            retorno = false;
        }
        if (selecionado.getBaseCalculo() == null) {
            FacesUtil.addCampoObrigatorio("Informe a Base de Cálculo da Multa.");
            retorno = false;
        }
        if (selecionado.getValorMulta().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addOperacaoNaoPermitida("O Valor da Multa não pode ser menor que zero.");
            retorno = false;
        }
        if (selecionado.getAliquotaMulta().compareTo(BigDecimal.ZERO) < 0) {
            FacesUtil.addOperacaoNaoPermitida("A Alíquota da Multa não pode ser menor que zero.");
            retorno = false;
        }
        if (selecionado.getTributo() == null) {
            FacesUtil.addCampoObrigatorio("Informe a receita.");
            retorno = false;
        }
        if ("".equals(selecionado.getEmbasamento())) {
            FacesUtil.addCampoObrigatorio("Informe o Embasamento Legal.");
            retorno = false;
        }
        if (IncidenciaMultaFiscalizacao.MULTA_PUNITIVA.equals(selecionado.getIncidenciaMultaFiscalizacao())
                && TipoCalculoMultaFiscalizacao.ANUAL.equals(selecionado.getTipoCalculoMultaFiscalizacao())) {
            FacesUtil.addError("Atenção!", "Não é possível cadastrar uma " + IncidenciaMultaFiscalizacao.MULTA_PUNITIVA.getDescricao() + " com tipo de cálculo " + TipoCalculoMultaFiscalizacao.ANUAL.getDescricao() + ".");
            retorno = false;
        }
        return retorno;
    }

    public IndiceEconomico getIndiceEconomico() {
        return multaFiscalizacaoFacade.getIndiceEconomicoFacade().recuperaPorDescricao("UFM");
    }

    public List<Tributo> completaTributo(String parte) {
        return multaFiscalizacaoFacade.getTributoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public Converter getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, multaFiscalizacaoFacade.getTributoFacade());
        }
        return converterTributo;
    }
}
