/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoIncidenciaAcrescimo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoAcrescimosFacade;
import br.com.webpublico.negocios.IndiceEconomicoFacade;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.singletons.CacheTributario;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
import java.util.Date;
import java.util.List;

/**
 * @author gustavo
 */
@ManagedBean(name = "configuracaoAcrescimosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConfiguracaoAcrescimos", pattern = "/tributario/acrescimos/configuracoes/novo/", viewId = "/faces/tributario/acrescimos/edita.xhtml"),
    @URLMapping(id = "editarConfiguracaoAcrescimos", pattern = "/tributario/acrescimos/configuracoes/editar/#{configuracaoAcrescimosControlador.id}/", viewId = "/faces/tributario/acrescimos/edita.xhtml"),
    @URLMapping(id = "listarConfiguracaoAcrescimos", pattern = "/tributario/acrescimos/configuracoes/listar/", viewId = "/faces/tributario/acrescimos/lista.xhtml"),
    @URLMapping(id = "verConfiguracaoAcrescimos", pattern = "/tributario/acrescimos/configuracoes/ver/#{configuracaoAcrescimosControlador.id}/", viewId = "/faces/tributario/acrescimos/visualizar.xhtml")
})
public class ConfiguracaoAcrescimosControlador extends PrettyControlador<ConfiguracaoAcrescimos> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoAcrescimosFacade configuracaoAcrescimosFacade;
    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    private ItemConfiguracaoAcrescimos itemConfiguracaoAcrescimos;
    private HonorariosConfiguracaoAcrescimos honorariosConfiguracaoAcrescimos;
    private MultaConfiguracaoAcrescimo multaConfiguracaoAcrescimo;
    private String caminho;
    private ConverterGenerico converterIndice;
    private TipoIncidenciaAcrescimo tipoIncidenciaJuros;
    private TipoIncidenciaAcrescimo tipoIncidenciaMulta;
    private TipoIncidenciaAcrescimo tipoIncidenciaCorrecao;
    @EJB
    private CacheTributario cacheTributario;
    private NaturezaJuridicaIsencao naturezaJuridicaIsencao;

    public ConfiguracaoAcrescimosControlador() {
        super(ConfiguracaoAcrescimos.class);
        naturezaJuridicaIsencao = new NaturezaJuridicaIsencao();
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoAcrescimosFacade;
    }

    public TipoIncidenciaAcrescimo getTipoIncidenciaCorrecao() {
        return tipoIncidenciaCorrecao;
    }

    public void setTipoIncidenciaCorrecao(TipoIncidenciaAcrescimo tipoIncidenciaCorrecao) {
        this.tipoIncidenciaCorrecao = tipoIncidenciaCorrecao;
    }

    public TipoIncidenciaAcrescimo getTipoIncidenciaMulta() {
        return tipoIncidenciaMulta;
    }

    public void setTipoIncidenciaMulta(TipoIncidenciaAcrescimo tipoIncidenciaMulta) {
        this.tipoIncidenciaMulta = tipoIncidenciaMulta;
    }

    public TipoIncidenciaAcrescimo getTipoIncidenciaJuros() {
        return tipoIncidenciaJuros;
    }

    public void setTipoIncidenciaJuros(TipoIncidenciaAcrescimo tipoIncidenciaJuros) {
        this.tipoIncidenciaJuros = tipoIncidenciaJuros;
    }

    public Converter getConverterIndice() {
        if (this.converterIndice == null) {
            this.converterIndice = new ConverterGenerico(IndiceEconomico.class, indiceEconomicoFacade);
        }
        return this.converterIndice;

    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public ItemConfiguracaoAcrescimos getItemConfiguracaoAcrescimos() {
        return itemConfiguracaoAcrescimos;
    }

    public void setItemConfiguracaoAcrescimos(ItemConfiguracaoAcrescimos itemConfiguracaoAcrescimos) {
        this.itemConfiguracaoAcrescimos = itemConfiguracaoAcrescimos;
    }

    public HonorariosConfiguracaoAcrescimos getHonorariosConfiguracaoAcrescimos() {
        return honorariosConfiguracaoAcrescimos;
    }

    public void setHonorariosConfiguracaoAcrescimos(HonorariosConfiguracaoAcrescimos honorariosConfiguracaoAcrescimos) {
        this.honorariosConfiguracaoAcrescimos = honorariosConfiguracaoAcrescimos;
    }

    public MultaConfiguracaoAcrescimo getMultaConfiguracaoAcrescimo() {
        return multaConfiguracaoAcrescimo;
    }

    public void setMultaConfiguracaoAcrescimo(MultaConfiguracaoAcrescimo multaConfiguracaoAcrescimo) {
        this.multaConfiguracaoAcrescimo = multaConfiguracaoAcrescimo;
    }

    public String caminho() {
        return caminho;
    }

    public List<ConfiguracaoAcrescimos> getListaConfiguracao() {
        return this.configuracaoAcrescimosFacade.lista();
    }

    public List<SelectItem> getListaTipoAplicacaoAcrescimoCorrecao() {
        List<SelectItem> itens = new ArrayList<>();
        itens.add(new SelectItem(null, " "));
        for (TipoIncidenciaAcrescimo tipo : TipoIncidenciaAcrescimo.getAplicaveisSobreCorrecao()) {
            itens.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return itens;
    }

    public List<SelectItem> getListaTipoAplicacaoAcrescimoMulta() {
        List<SelectItem> itens = new ArrayList<>();
        itens.add(new SelectItem(null, " "));
        for (TipoIncidenciaAcrescimo tipo : TipoIncidenciaAcrescimo.getAplicaveisSobreMulta()) {
            itens.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return itens;
    }

    public List<SelectItem> getListaTipoAplicacaoAcrescimoJuros() {
        List<SelectItem> itens = new ArrayList<>();
        itens.add(new SelectItem(null, " "));
        for (TipoIncidenciaAcrescimo tipo : TipoIncidenciaAcrescimo.getAplicaveisSobreJuros()) {
            itens.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return itens;
    }

    public List<SelectItem> getListaIndice() {
        List<SelectItem> itens = new ArrayList<>();
        for (IndiceEconomico ie : indiceEconomicoFacade.lista()) {
            itens.add(new SelectItem(ie, ie.getDescricao()));
        }
        return itens;
    }

    public List<SelectItem> getTiposMovimentoMensal() {
        return Util.getListSelectItem(TipoMovimentoMensal.values());
    }

    public NaturezaJuridicaIsencao getNaturezaJuridicaIsencao() {
        return naturezaJuridicaIsencao;
    }

    public void setNaturezaJuridicaIsencao(NaturezaJuridicaIsencao naturezaJuridicaIsencao) {
        this.naturezaJuridicaIsencao = naturezaJuridicaIsencao;
    }

    @URLAction(mappingId = "novaConfiguracaoAcrescimos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.itemConfiguracaoAcrescimos = new ItemConfiguracaoAcrescimos();
        this.honorariosConfiguracaoAcrescimos = new HonorariosConfiguracaoAcrescimos();
        this.multaConfiguracaoAcrescimo = new MultaConfiguracaoAcrescimo();
        selecionado.setMultas(new ArrayList<MultaConfiguracaoAcrescimo>());
        selecionado.setHonorarios(new ArrayList<HonorariosConfiguracaoAcrescimos>());
    }

    @URLAction(mappingId = "verConfiguracaoAcrescimos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarConfiguracaoAcrescimos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        this.itemConfiguracaoAcrescimos = new ItemConfiguracaoAcrescimos();
        this.honorariosConfiguracaoAcrescimos = new HonorariosConfiguracaoAcrescimos();
        this.multaConfiguracaoAcrescimo = new MultaConfiguracaoAcrescimo();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (selecionado.getId() == null) {
                configuracaoAcrescimosFacade.salvarNovo(selecionado);
            } else {
                configuracaoAcrescimosFacade.salvar(selecionado);
            }
            cacheTributario.init();
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarListaItem() {
        if (this.itemConfiguracaoAcrescimos != null) {
            try {
                validarItemConfiguracao(multaConfiguracaoAcrescimo);
                this.itemConfiguracaoAcrescimos.setConfiguracaoAcrescimos(multaConfiguracaoAcrescimo);
                multaConfiguracaoAcrescimo.getDiasAtraso().add(this.itemConfiguracaoAcrescimos);
                this.itemConfiguracaoAcrescimos = new ItemConfiguracaoAcrescimos();
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    private void validarItemConfiguracao(MultaConfiguracaoAcrescimo multa) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (itemConfiguracaoAcrescimos.getQntDias() == null || itemConfiguracaoAcrescimos.getQntDias() <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a quantidade de dias!");
        }
        if (itemConfiguracaoAcrescimos.getValor() == null || itemConfiguracaoAcrescimos.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o percentual de multa!");
        }
        for (ItemConfiguracaoAcrescimos ita : multa.getDiasAtraso()) {
            if (ita.getQntDias() == this.itemConfiguracaoAcrescimos.getQntDias()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Essa quantidade de dias já foi adicionada.");
                break;
            }
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void removerItem(ItemConfiguracaoAcrescimos aRemover) {
        if (this.multaConfiguracaoAcrescimo != null) {
            this.multaConfiguracaoAcrescimo.getDiasAtraso().remove(aRemover);
        }
    }

    public void adicionarListaMulta() {
        if (this.multaConfiguracaoAcrescimo != null) {
            if (selecionado.getMultas() != null) {
                try {
                    validarMultasConfiguracao();
                    if (DataUtil.isVigenciaValida(multaConfiguracaoAcrescimo, selecionado.getMultas(), false)) {
                        this.multaConfiguracaoAcrescimo.setConfiguracaoAcrescimos(selecionado);
                        selecionado.getMultas().add(this.multaConfiguracaoAcrescimo);
                        this.multaConfiguracaoAcrescimo = new MultaConfiguracaoAcrescimo();
                    }
                } catch (ValidacaoException ve) {
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                }
            }
        }
    }

    private void validarMultasConfiguracao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (multaConfiguracaoAcrescimo.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o início de vigência!");
        }
        if (multaConfiguracaoAcrescimo.getIncidencias().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a incidência da Multa!");
        }
        if (multaConfiguracaoAcrescimo.getDiasAtraso().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Multa Sobre Débitos do Exercício por Quantidade de Dias em Atraso!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void removerMulta(MultaConfiguracaoAcrescimo aRemover) {
        selecionado.getMultas().remove(aRemover);
    }

    public void encerrarVigenciaMulta(MultaConfiguracaoAcrescimo aEncerrar) {
        aEncerrar.setFimVigencia(new Date());
    }

    public void adicionarListaHonorarios() {
        if (selecionado.getHonorarios() != null) {
            try {
                validarHonorariosConfiguracao();
                if (DataUtil.isVigenciaValida(honorariosConfiguracaoAcrescimos, selecionado.getHonorarios(), false)) {
                    this.honorariosConfiguracaoAcrescimos.setConfiguracaoAcrescimos(selecionado);
                    selecionado.getHonorarios().add(this.honorariosConfiguracaoAcrescimos);
                    this.honorariosConfiguracaoAcrescimos = new HonorariosConfiguracaoAcrescimos();
                }
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    private void validarHonorariosConfiguracao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (honorariosConfiguracaoAcrescimos.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o início de vigência!");
        }
        if (honorariosConfiguracaoAcrescimos.getHonorariosAdvocaticios() == null || honorariosConfiguracaoAcrescimos.getHonorariosAdvocaticios().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o percentual para adicionar!");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void removerHonorarios(HonorariosConfiguracaoAcrescimos aRemover) {
        selecionado.getHonorarios().remove(aRemover);
    }

    public void encerrarVigenciaHonorarios(HonorariosConfiguracaoAcrescimos aEncerrar) {
        aEncerrar.setFimVigencia(new Date());
    }

    private void validarCampos() {
        ValidacaoException validacaoException = new ValidacaoException();
        if ("".equals(selecionado.getDescricao())) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Informe a Descrição da Configuração de Acréscimos!");
        }
        if (selecionado.getValorJurosExercicio() == null || selecionado.getValorJurosExercicio().compareTo(BigDecimal.ZERO) < 0) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Informe a Alíquota de Juros!");
        }
        if (selecionado.getCalculaCorrecao() == null || (selecionado.getCalculaCorrecao() && selecionado.getIncidenciasCorrecao() == null || selecionado.getIncidenciasCorrecao().isEmpty())) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Informe a incidência da Correção monetária!");
        }
        if (selecionado.getIncidenciasJuros() == null || selecionado.getIncidenciasJuros().isEmpty()) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Informe a incidência do Juros de Mora!");
        }
        if (validacaoException.temMensagens()) {
            throw validacaoException;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/acrescimos/configuracoes/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void adicionarIncidenciaJuros() {
        try {
            validarIncidencia(IncidenciaAcrescimo.TipoAcrescimo.JUROS, tipoIncidenciaJuros);
            IncidenciaAcrescimo incidenciaAcrescimo = recuperarIncidenciaAcrescimo(IncidenciaAcrescimo.TipoAcrescimo.JUROS);
            selecionado.getIncidencias().add(incidenciaAcrescimo);
            tipoIncidenciaJuros = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerIncidenciaJuros() {
        List<IncidenciaAcrescimo> aRemover = selecionado.getIncidenciasJuros();
        selecionado.getIncidencias().removeAll(aRemover);
    }

    public void adicionarIncidenciaMulta() {
        if (multaConfiguracaoAcrescimo != null) {
            IncidenciaAcrescimoMulta incidencia = new IncidenciaAcrescimoMulta();
            incidencia.setMulta(multaConfiguracaoAcrescimo);
            incidencia.setIncidencia(tipoIncidenciaMulta);
            multaConfiguracaoAcrescimo.getIncidencias().add(incidencia);
            tipoIncidenciaMulta = null;
        }
    }

    public void removerIncidenciaMulta() {
        if (multaConfiguracaoAcrescimo != null) {
            List<IncidenciaAcrescimoMulta> aRemover = multaConfiguracaoAcrescimo.getIncidencias();
            multaConfiguracaoAcrescimo.getIncidencias().removeAll(aRemover);
        }
    }

    public void adicionarIncidenciaCorrecao() {
        try {
            validarIncidencia(IncidenciaAcrescimo.TipoAcrescimo.CORRECAO, tipoIncidenciaCorrecao);
            IncidenciaAcrescimo incidenciaAcrescimo = recuperarIncidenciaAcrescimo(IncidenciaAcrescimo.TipoAcrescimo.CORRECAO);
            selecionado.getIncidencias().add(incidenciaAcrescimo);
            tipoIncidenciaMulta = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerIncidenciaCorrecao() {
        List<IncidenciaAcrescimo> aRemover = selecionado.getIncidenciasCorrecao();
        selecionado.getIncidencias().removeAll(aRemover);
    }

    private IncidenciaAcrescimo recuperarIncidenciaAcrescimo(IncidenciaAcrescimo.TipoAcrescimo tipo) {
        IncidenciaAcrescimo incidenciaAcrescimo = new IncidenciaAcrescimo();
        incidenciaAcrescimo.setConfiguracaoAcrescimos(selecionado);
        switch (tipo) {
            case JUROS:
                incidenciaAcrescimo.setIncidencia(tipoIncidenciaJuros);
                break;
            case MULTA:
                incidenciaAcrescimo.setIncidencia(tipoIncidenciaMulta);
                break;
            case CORRECAO:
                incidenciaAcrescimo.setIncidencia(tipoIncidenciaCorrecao);
                break;
        }
        incidenciaAcrescimo.setTipoAcrescimo(tipo);
        return incidenciaAcrescimo;
    }

    public void validarIncidencia(IncidenciaAcrescimo.TipoAcrescimo tipo, TipoIncidenciaAcrescimo incidenciaAcrescimo) {
        ValidacaoException validacaoException = new ValidacaoException();
        if (incidenciaAcrescimo == null) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Informe o tipo de incidência!");
            throw validacaoException;
        }
        for (IncidenciaAcrescimo acrescimo : selecionado.getIncidencias()) {
            if (acrescimo.getTipoAcrescimo().equals(tipo)
                && acrescimo.getIncidencia().equals(incidenciaAcrescimo)) {
                validacaoException.adicionarMensagemDeOperacaoNaoPermitida("Esse tipo de incidência já foi informado!");
                throw validacaoException;
            }
        }
    }

    public List<SelectItem> getBasesCalculoCorrecao() {
        List<SelectItem> itens = new ArrayList<>();
        itens.add(new SelectItem(null, " "));
        for (ConfiguracaoAcrescimos.DataBaseCalculo tipo : ConfiguracaoAcrescimos.DataBaseCalculo.values()) {
            itens.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return itens;
    }

    public void adicionarIsencaoNfse() {
        try {
            selecionado.validarNovaIsencaoNfse(naturezaJuridicaIsencao);
            naturezaJuridicaIsencao.setConfiguracaoAcrescimos(selecionado);
            selecionado.getNaturezasIsencao().add(naturezaJuridicaIsencao);
            naturezaJuridicaIsencao = new NaturezaJuridicaIsencao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void removerIsencaoNfse(NaturezaJuridicaIsencao naturezaJuridicaIsencao) {
        selecionado.getNaturezasIsencao().remove(naturezaJuridicaIsencao);
    }
}
