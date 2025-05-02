/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ProvAtuarialMatematicaFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */
@ManagedBean(name = "provAtuarialMatematicaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoprovAtuarialMatematica", pattern = "/provisao-matematica-previdenciaria/novo/", viewId = "/faces/financeiro/lancamentocontabilmanual/provisaomatematica/edita.xhtml"),
        @URLMapping(id = "editarprovAtuarialMatematica", pattern = "/provisao-matematica-previdenciaria/editar/#{provAtuarialMatematicaControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/provisaomatematica/edita.xhtml"),
        @URLMapping(id = "verprovAtuarialMatematica", pattern = "/provisao-matematica-previdenciaria/ver/#{provAtuarialMatematicaControlador.id}/", viewId = "/faces/financeiro/lancamentocontabilmanual/provisaomatematica/visualizar.xhtml"),
        @URLMapping(id = "listarprovAtuarialMatematica", pattern = "/provisao-matematica-previdenciaria/listar/", viewId = "/faces/financeiro/lancamentocontabilmanual/provisaomatematica/lista.xhtml")
})
public class ProvAtuarialMatematicaControlador extends PrettyControlador<ProvAtuarialMatematica> implements Serializable, CRUD {

    @EJB
    private ProvAtuarialMatematicaFacade provAtuarialMatematicaFacade;
    private ConverterGenerico converterTipoPassivoAtuarial;
    private ConverterAutoComplete converterDividaPublica;
    private MoneyConverter moneyConverter;
    private List<TipoPassivoAtuarial> listaTipoPassivoAtuarial;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public ProvAtuarialMatematicaControlador() {
        super(ProvAtuarialMatematica.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return provAtuarialMatematicaFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    private boolean validaHierarquiasAdministrativasOrcamentaria(ProvAtuarialMatematica prov) {
        HierarquiaOrganizacional ho = null;
        try {
            UnidadeOrganizacional undAd = sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente();
            UnidadeOrganizacional undOrc = sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente();
            Date dataSistema = sistemaControlador.getDataOperacao();
            ho = provAtuarialMatematicaFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(undAd, undOrc, prov.getDataLancamento());

        } catch (ExcecaoNegocioGenerica eng) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, eng.getMessage(), " "));
        } catch (Exception exception) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, exception.getMessage(), " "));
        }
        return ho != null;
    }

    @Override
    public void salvar() {
        ProvAtuarialMatematica prov = ((ProvAtuarialMatematica) selecionado);
        boolean unidadesValidas = validaHierarquiasAdministrativasOrcamentaria(prov);
        if (validaCampos() && unidadesValidas) {
            try {
                prov.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
                if (operacao.equals(Operacoes.NOVO)) {
//                    prov.setNumero(provAtuarialMatematicaFacade.getUltimoNumero().toString());
                    provAtuarialMatematicaFacade.salvarNovo(prov);
                } else {
                    provAtuarialMatematicaFacade.salvar(prov);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Operação Realizada! ", " Registro salvo com sucesso. "));
                redireciona();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Operação não Realizada! ", e.getMessage()));
                return;
            }
        }
    }

    public Boolean verificaEdicao() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    public String setaEvento() {
        ProvAtuarialMatematica prov = ((ProvAtuarialMatematica) selecionado);
        ConfigProvMatematicaPrev configuracao;
        try {
            if (prov.getTipoLancamento() != null && prov.getTipoOperacaoAtuarial() != null
                    && prov.getTipoPlano() != null && prov.getTipoProvisao() != null) {
                configuracao = provAtuarialMatematicaFacade.getConfigProvMatematicaFacade().recuperaEvento(prov);
                prov.setEventoContabil(configuracao.getEventoContabil());
                return prov.getEventoContabil().toString();
            } else {
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    public Boolean validaCampos() {
        ProvAtuarialMatematica prov = ((ProvAtuarialMatematica) selecionado);
        boolean retorno = Util.validaCampos(prov);
        if (prov.getValorLancamento().compareTo(BigDecimal.ZERO) <= 0) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O valor deve maior que 0 (zero).");
        }
        if (prov.getEventoContabil() == null) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Evento não encontrado! ", " Nenhum evento contábil encontrado para este lançamento!"));
        }
        if (prov.getTipoLancamento().equals(TipoLancamento.ESTORNO)) {
            BigDecimal saldo = provAtuarialMatematicaFacade.getSaldoProvAtuarialMatematica(((ProvAtuarialMatematica) selecionado));
            if (saldo.compareTo(((ProvAtuarialMatematica) selecionado).getValorLancamento()) < 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O valor a ser estornado de <b>" + Util.formataValor(selecionado.getValorLancamento()) + "</b> é maior que o saldo de <b>" + Util.formataValor(saldo) + "</b> disponível para esse lançamento.");
                return false;
            }
        }
        return retorno;
    }

    public List<SelectItem> getListaTipoLancamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoLancamento tipo : TipoLancamento.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoOperacaoAtuarial() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoOperacaoAtuarial tipo : TipoOperacaoAtuarial.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoPlano() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPlano tipo : TipoPlano.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoProvisao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoProvisao tipo : TipoProvisao.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<TipoPassivoAtuarial> getListaTipoPassivoAtuarial() {
        return listaTipoPassivoAtuarial;
    }

    public ConverterGenerico getConverterTipoPassivoAtuarial() {
        if (converterTipoPassivoAtuarial == null) {
            converterTipoPassivoAtuarial = new ConverterGenerico(TipoPassivoAtuarial.class, provAtuarialMatematicaFacade.getTipoPassivoAtuarialFacade());
        }
        return converterTipoPassivoAtuarial;
    }

    public void atualizaTipoPassivo() {
        if (((ProvAtuarialMatematica) selecionado).getTipoOperacaoAtuarial() != null
                && ((ProvAtuarialMatematica) selecionado).getTipoPlano() != null
                && ((ProvAtuarialMatematica) selecionado).getTipoProvisao() != null) {
            listaTipoPassivoAtuarial = provAtuarialMatematicaFacade.getTipoPassivoAtuarialFacade().getTipoPassivoPorOperacaoPlanoProvisao(((ProvAtuarialMatematica) selecionado).getTipoOperacaoAtuarial(),
                    ((ProvAtuarialMatematica) selecionado).getTipoPlano(),
                    ((ProvAtuarialMatematica) selecionado).getTipoProvisao());
        }
        setaEvento();
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }


    private boolean existeProvAtuarialMatematicaComNumero() {
        return provAtuarialMatematicaFacade.existeProvAtuarialMatematicaComNumero(((ProvAtuarialMatematica) selecionado).getNumero());
    }

    public List<DividaPublica> completaDividaPublica(String parte) {
        return provAtuarialMatematicaFacade.getDividaPublicaFacade().completaDividaPublicaPorNatureza(parte.trim(), NaturezaDividaPublica.PASSIVO_ATUARIAL);
    }

    public ConverterAutoComplete getConverterDividaPublica() {
        if (converterDividaPublica == null) {
            converterDividaPublica = new ConverterAutoComplete(DividaPublica.class, provAtuarialMatematicaFacade.getDividaPublicaFacade());
        }
        return converterDividaPublica;
    }

    @URLAction(mappingId = "novoprovAtuarialMatematica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        listaTipoPassivoAtuarial = new ArrayList<TipoPassivoAtuarial>();
        ((ProvAtuarialMatematica) selecionado).setDataLancamento(sistemaControlador.getDataOperacao());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        ((ProvAtuarialMatematica) selecionado).setValorLancamento(BigDecimal.ZERO);
        ((ProvAtuarialMatematica) selecionado).setTipoLancamento(TipoLancamento.NORMAL);

        if (provAtuarialMatematicaFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", provAtuarialMatematicaFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "verprovAtuarialMatematica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarprovAtuarialMatematica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/provisao-matematica-previdenciaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
