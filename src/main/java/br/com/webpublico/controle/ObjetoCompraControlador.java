/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoObjetoCompra;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ObjetoCompraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author Romanini
 */
@ManagedBean(name = "objetoCompraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-objeto-Compra", pattern = "/objeto-compra/novo/", viewId = "/faces/administrativo/licitacao/objetocompra/edita.xhtml"),
    @URLMapping(id = "editar-objeto-compra", pattern = "/objeto-compra/editar/#{objetoCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/objetocompra/edita.xhtml"),
    @URLMapping(id = "ver-objeto-compra", pattern = "/objeto-compra/ver/#{objetoCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/objetocompra/visualizar.xhtml"),
    @URLMapping(id = "listar-objeto-compra", pattern = "/objeto-compra/listar/", viewId = "/faces/administrativo/licitacao/objetocompra/lista.xhtml")
})
public class ObjetoCompraControlador extends PrettyControlador<ObjetoCompra> implements Serializable, CRUD {

    @EJB
    private ObjetoCompraFacade facade;
    private ObjetoDeCompraEspecificacao objetoCompraEspecificacao;
    private Boolean bloquearEdicao = Boolean.FALSE;
    private ObjetoCompra objetoCompraComparativo;
    private List<DerivacaoObjetoCompraComponente> componentesDerivacao;

    public ObjetoCompraControlador() {
        super(ObjetoCompra.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-objeto-Compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        objetoCompraEspecificacao = new ObjetoDeCompraEspecificacao(Boolean.TRUE, selecionado);
        selecionado.setSituacaoObjetoCompra(SituacaoObjetoCompra.AGUARDANDO);
    }

    @URLAction(mappingId = "ver-objeto-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        popularGrupoContaDespesa();
        buscarComonentesDerivacao();
    }

    @URLAction(mappingId = "editar-objeto-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        objetoCompraEspecificacao = new ObjetoDeCompraEspecificacao(Boolean.TRUE, selecionado);
        bloquearEdicao = facade.utilizadoEmProcessoLicitatorio(selecionado);
        objetoCompraComparativo = (ObjetoCompra) Util.clonarObjeto(selecionado);
        popularGrupoContaDespesa();
        buscarComonentesDerivacao();
    }

    @Override
    public void salvar() {
        try {
            validarRegrasAoSalvar();
            if (hasAlteracaoObjetoCompra()) {
                selecionado.setSituacaoObjetoCompra(SituacaoObjetoCompra.AGUARDANDO);
            }
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void limparDadosGrupoObjetoCompra() {
        selecionado.setGrupoContaDespesa(null);
    }

    public void limparDadosTipoObjetoCompra() {
        selecionado.setGrupoContaDespesa(null);
        selecionado.setGrupoObjetoCompra(null);
        selecionado.setDescricao(null);
    }

    private boolean hasAlteracaoObjetoCompra() {
        return objetoCompraComparativo != null &&
            (!objetoCompraComparativo.getTipoObjetoCompra().equals(selecionado.getTipoObjetoCompra())
                || !objetoCompraComparativo.getGrupoObjetoCompra().equals(selecionado.getGrupoObjetoCompra())
                || !objetoCompraComparativo.getAtivo().equals(selecionado.getAtivo())
                || !objetoCompraComparativo.getReferencial().equals(selecionado.getReferencial())
                || !objetoCompraComparativo.getDescricao().equals(selecionado.getDescricao()));
    }

    private void validarRegrasAoSalvar() {
        Util.validarCampos(selecionado);
        if (selecionado.getGrupoContaDespesa() != null) {
            selecionado.getGrupoContaDespesa().lancarMensagens();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/objeto-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<GrupoObjetoCompra> completarGrupoObjetoCompra(String parte) {
        if (selecionado != null && selecionado.getTipoObjetoCompra() != null) {
            return facade.getGrupoObjetoCompraFacade().buscarFiltrandoGrupoObjetoCompraPorTipoObjetoCompra(parte.trim(), selecionado.getTipoObjetoCompra());
        }
        FacesUtil.addCampoObrigatorio("Informe o tipo do objeto de compra.");
        return null;
    }

    public String buscarNameTipoObjetoCompra() {
        if (selecionado != null && selecionado.getTipoObjetoCompra() != null) {
            return selecionado.getTipoObjetoCompra().name();
        }
        FacesUtil.addCampoObrigatorio("Informe o tipo do objeto de compra.");
        return null;
    }

    public void popularGrupoContaDespesa() {
        if (selecionado.getTipoObjetoCompra() != null && selecionado.getGrupoObjetoCompra() != null) {
            selecionado.setGrupoContaDespesa(facade.criarGrupoContaDespesa(selecionado.getTipoObjetoCompra(), selecionado.getGrupoObjetoCompra()));
        }
    }

    public List<ObjetoCompra> buscarObjetoCompraTipoPermanente(String codigoOrDescricao) {
        return facade.buscarObjetoCompraPorCodigoOrDescricaoAndListTipoObjetoCompra(codigoOrDescricao, TipoObjetoCompra.getTiposObjetoCompraPermanente());
    }

    public List<ObjetoCompra> buscarObjetoCompraTipoPermanenteMovel(String codigoOrDescricao) {
        return facade.buscarObjetoCompraPorCodigoOrDescricaoAndListTipoObjetoCompra(codigoOrDescricao, Lists.newArrayList(TipoObjetoCompra.PERMANENTE_MOVEL));
    }

    public List<ObjetoCompra> buscarObjetoCompraTipoPermanenteAlternativo(String codigoOrDescricao) {
        return facade.buscarObjetoCompraPorDescricaoOrcodigoAndListTipoObjetoCompra(codigoOrDescricao, TipoObjetoCompra.getTiposObjetoCompraPermanente());
    }

    public List<ObjetoCompra> completaObjetoCompraTipoConsumo(String parte) {
        return facade.completaObjetoCompraTipoConsumo(parte);
    }

    public List<SelectItem> buscarTiposObjetoCompra() {
        return Util.getListSelectItem(Arrays.asList(TipoObjetoCompra.values()));
    }

    public ObjetoDeCompraEspecificacao getObjetoCompraEspecificacao() {
        return objetoCompraEspecificacao;
    }

    public void setObjetoCompraEspecificacao(ObjetoDeCompraEspecificacao objetoCompraEspecificacao) {
        this.objetoCompraEspecificacao = objetoCompraEspecificacao;
    }


    public void adicionarEspecificacao() {
        if (objetoCompraEspecificacao.getTexto().isEmpty()) {
            FacesUtil.addCampoObrigatorio("Informe uma especificação para objeto de compra.");
            return;
        }
        Util.adicionarObjetoEmLista(selecionado.getEspecificacaoes(), objetoCompraEspecificacao);
        objetoCompraEspecificacao = new ObjetoDeCompraEspecificacao(Boolean.TRUE, selecionado);
    }

    public void limparEditorDeEspecificacao() {
        objetoCompraEspecificacao.setTexto("");
    }

    public void editarEspecificacao(ObjetoDeCompraEspecificacao especificacao) {
        objetoCompraEspecificacao = (ObjetoDeCompraEspecificacao) Util.clonarObjeto(especificacao);
    }

    public void removerEspecificacao(ObjetoDeCompraEspecificacao especificacao) {
        selecionado.getEspecificacaoes().remove(especificacao);
    }

    public void visualizarEspecificacao(ObjetoDeCompraEspecificacao especificacao) {
        objetoCompraEspecificacao = especificacao;
    }

    public String especificacaoAtiva(ObjetoDeCompraEspecificacao especificacao) {
        if (especificacao == null) {
            return "Não";
        }
        return Util.converterBooleanSimOuNao(especificacao.getAtivo());
    }

    public List<DerivacaoObjetoCompra> completarDerivacoesObjetoCompra(String parte) {
        return facade.getDerivacaoObjetoCompraFacade().buscarDerivacoesObjetoCompra(parte.trim());
    }

    private void buscarComonentesDerivacao() {
        if (selecionado.getDerivacaoObjetoCompra() != null) {
            componentesDerivacao = facade.getDerivacaoObjetoCompraFacade().buscarComponentesPorDerivacao(selecionado.getDerivacaoObjetoCompra());
        }
    }

    public void listenerDerivacao() {
        buscarComonentesDerivacao();
    }

    public Boolean getBloquearEdicao() {
        return bloquearEdicao;
    }

    public void setBloquearEdicao(Boolean bloquearEdicao) {
        this.bloquearEdicao = bloquearEdicao;
    }

    public Boolean isTipoObjetoCompralDefinido() {
        return selecionado.getTipoObjetoCompra() != null;
    }

    public List<DerivacaoObjetoCompraComponente> getComponentesDerivacao() {
        return componentesDerivacao;
    }

    public void setComponentesDerivacao(List<DerivacaoObjetoCompraComponente> componentesDerivacao) {
        this.componentesDerivacao = componentesDerivacao;
    }
}
