/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ParametrosDividaAtivaFacade;
import br.com.webpublico.negocios.TipoDoctoOficialFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wellington
 */
@ManagedBean(name = "parametrosDividaAtivaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametro", pattern = "/parametro-de-divida-ativa/novo/", viewId = "/faces/tributario/dividaativa/parametrosdividaativa/edita.xhtml"),
    @URLMapping(id = "editarParametro", pattern = "/parametro-de-divida-ativa/editar/#{parametrosDividaAtivaControlador.id}/", viewId = "/faces/tributario/dividaativa/parametrosdividaativa/edita.xhtml"),
    @URLMapping(id = "listarParametro", pattern = "/parametro-de-divida-ativa/listar/", viewId = "/faces/tributario/dividaativa/parametrosdividaativa/lista.xhtml"),
    @URLMapping(id = "verParametro", pattern = "/parametro-de-divida-ativa/ver/#{parametrosDividaAtivaControlador.id}/", viewId = "/faces/tributario/dividaativa/parametrosdividaativa/visualizar.xhtml")
})
public class ParametrosDividaAtivaControlador extends PrettyControlador<ParametrosDividaAtiva> implements Serializable, CRUD {

    @EJB
    private ParametrosDividaAtivaFacade parametrosDividaAtivaFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private DividaFacade dividaFacade;
    private ConverterAutoComplete converterTipoDoctoFical;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterExercicio converterExercicio;
    private ParamDividaAtivaDivida divida;
    private AgrupadorCDA agrupador;
    private AgrupadorCDADivida agrupadorDivida;

    public ParametrosDividaAtivaControlador() {
        super(ParametrosDividaAtiva.class);
    }


    @URLAction(mappingId = "novoParametro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        newDivida();
        agrupador = null;
    }

    private void newDivida() {
        divida = new ParamDividaAtivaDivida();
        agrupadorDivida = new AgrupadorCDADivida();
    }

    @URLAction(mappingId = "verParametro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        newDivida();
        agrupador = null;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-de-divida-ativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ConverterAutoComplete getConverterTipoDoctoFical() {
        if (converterTipoDoctoFical == null) {
            converterTipoDoctoFical = new ConverterAutoComplete(TipoDoctoOficial.class, tipoDoctoOficialFacade);
        }
        return converterTipoDoctoFical;
    }

    @Override
    public void salvar() {
        if (valida()) {
            super.salvar();
        }
    }

    private boolean valida() {
        boolean valida = true;
        ParametrosDividaAtiva p = parametrosDividaAtivaFacade.parametrosDividaAtivaPorExercicio(selecionado.getExercicio());
        if (p != null && !selecionado.equals(p)) {
            valida = false;
            FacesUtil.addError("Não é possível continuar", "Já existe um Parâmetro cadastrado para o exercício informado, não é possível existir dois parâmetros para o mesmo exercício");
        }
        return valida;
    }

    @Override
    public AbstractFacade getFacede() {
        return parametrosDividaAtivaFacade;
    }

    public AgrupadorCDA getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(AgrupadorCDA agrupador) {
        this.agrupador = agrupador;
    }

    public AgrupadorCDADivida getAgrupadorDivida() {
        return agrupadorDivida;
    }

    public void setAgrupadorDivida(AgrupadorCDADivida agrupadorDivida) {
        this.agrupadorDivida = agrupadorDivida;
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String filtro) {
        return tipoDoctoOficialFacade.listaFiltrando(filtro.trim(), "descricao");
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroPessoaFisica(String parte) {
        return parametrosDividaAtivaFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.PESSOAFISICA, ModuloTipoDoctoOficial.TERMODIVIDAATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroPessoaJuridica(String parte) {
        return parametrosDividaAtivaFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.PESSOAJURIDICA, ModuloTipoDoctoOficial.TERMODIVIDAATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroImobiliario(String parte) {
        return parametrosDividaAtivaFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTROIMOBILIARIO, ModuloTipoDoctoOficial.TERMODIVIDAATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroEconomico(String parte) {
        return parametrosDividaAtivaFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTROECONOMICO, ModuloTipoDoctoOficial.TERMODIVIDAATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroRural(String parte) {
        return parametrosDividaAtivaFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTRORURAL, ModuloTipoDoctoOficial.TERMODIVIDAATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroCertidaoImobiliario(String parte) {
        return parametrosDividaAtivaFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTROIMOBILIARIO, ModuloTipoDoctoOficial.CERTIDADIVIDAATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroCertidaoMobiliario(String parte) {
        return parametrosDividaAtivaFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTROECONOMICO, ModuloTipoDoctoOficial.CERTIDADIVIDAATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroCertidaoRural(String parte) {
        return parametrosDividaAtivaFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.CADASTRORURAL, ModuloTipoDoctoOficial.CERTIDADIVIDAATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroCertidaoPF(String parte) {
        return parametrosDividaAtivaFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.PESSOAFISICA, ModuloTipoDoctoOficial.CERTIDADIVIDAATIVA);
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialDoParametroCertidaoPJ(String parte) {
        return parametrosDividaAtivaFacade.completaTipoDoctoOficialPorTipo(parte.trim(), TipoCadastroDoctoOficial.PESSOAJURIDICA, ModuloTipoDoctoOficial.CERTIDADIVIDAATIVA);
    }

    public List<Exercicio> completaExercicio(String parte) {
        return parametrosDividaAtivaFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    public ConverterExercicio converteExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(parametrosDividaAtivaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, parametrosDividaAtivaFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return parametrosDividaAtivaFacade.getAtoLegalFacade().listaFiltrando(parte.trim(), "numero", "nome");
    }

    public void removeDivida(ParamDividaAtivaDivida divida) {
        if (selecionado.getDividasEnvioCDA().contains(divida)) {
            selecionado.getDividasEnvioCDA().remove(divida);
        }
    }

    public void removerDividaAgrupadorCDA(AgrupadorCDADivida divida) {
        if (agrupador.getDividas().contains(divida)) {
            agrupador.getDividas().remove(divida);
        }
    }

    public ParamDividaAtivaDivida getDivida() {
        return divida;
    }

    public void setDivida(ParamDividaAtivaDivida divida) {
        this.divida = divida;
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        List<Divida> dividas = dividaFacade.lista();
        for (Divida divida : dividas) {
            toReturn.add(new SelectItem(divida, divida.getDescricao()));
        }
        return toReturn;
    }

    public void addDivida() {
        divida.setParametrosDividaAtiva(selecionado);
        selecionado.getDividasEnvioCDA().add(divida);
        newDivida();

    }

    public void adicionarDividaAgrupador() {
        try {
            validarAgrupadorDivida();
            agrupadorDivida.setAgrupadorCDA(agrupador);
            agrupador.getDividas().add(agrupadorDivida);
            newDivida();
        } catch (ValidacaoException e) {
            agrupadorDivida.setDivida(null);
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }
    }

    public void novoAgrupador() {
        agrupador = new AgrupadorCDA();
    }

    private void validarAgrupadorDivida() {
        ValidacaoException ve = new ValidacaoException();
        if (agrupadorDivida.getDivida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione a dívida para agrupar");
        } else {
            for (AgrupadorCDADivida agrupadorCDADivida : agrupador.getDividas()) {
                if (agrupadorCDADivida.getDivida().equals(agrupadorDivida.getDivida())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A dívida informada já foi adicionada neste agrupador");
                    break;
                }
            }
            for (AgrupadorCDA agrupadorCDA : selecionado.getAgrupadoresCDA()) {
                for (AgrupadorCDADivida agrupadorCDADivida : agrupadorCDA.getDividas()) {
                    if (agrupadorCDA.equals(agrupador)) {
                        continue;
                    }
                    if (agrupadorCDADivida.getDivida().equals(agrupadorDivida.getDivida())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A dívida informada já foi adicionada no agrupador " + agrupadorCDA.getTitulo());
                        break;
                    }
                }
            }

        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void cancelarAgrupador() {
        agrupador = null;
    }

    public void adicionarAgrupador() {
        try {
            validarAgrupador();
            if (selecionado.getAgrupadoresCDA().contains(agrupador)) {
                selecionado.getAgrupadoresCDA().remove(selecionado.getAgrupadoresCDA().indexOf(agrupador));
            }
            agrupador.setParametrosDividaAtiva(selecionado);
            selecionado.getAgrupadoresCDA().add(agrupador);
            newDivida();
            agrupador = null;
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }
    }

    private void validarAgrupador() {
        ValidacaoException ve = new ValidacaoException();
        if (agrupador.getTitulo().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o titulo do agrupador");
        }
        if (agrupador.getDividas().isEmpty() || agrupador.getDividas().size() < 2) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos duas dívida para agrupar");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void editarAgrupador(AgrupadorCDA agrupador) {
        this.agrupador = (AgrupadorCDA) Util.clonarEmNiveis(agrupador, 2);
        newDivida();
    }
}
