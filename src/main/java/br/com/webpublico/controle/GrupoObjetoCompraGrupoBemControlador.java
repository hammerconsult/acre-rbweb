package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.entidades.GrupoObjetoCompraGrupoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoObjetoCompraGrupoBemFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ROBSONLUIS-MGA
 * Date: 27/11/13
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "grupoObjetoCompraGrupoBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-associacao-do-grupo-de-objeto-compra-com-grupo-bem", pattern = "/associacao-grupo-de-objeto-de-compra-e-grupo-patrimonial/novo/",
        viewId = "/faces/administrativo/patrimonio/grupoobjetocompragrupobem/edita.xhtml"),
    @URLMapping(id = "editar-associacao-do-grupo-de-objeto-compra-com-grupo-bem", pattern = "/associacao-grupo-de-objeto-de-compra-e-grupo-patrimonial/editar/#{grupoObjetoCompraGrupoBemControlador.id}/",
        viewId = "/faces/administrativo/patrimonio/grupoobjetocompragrupobem/edita.xhtml"),
    @URLMapping(id = "ver-associacao-do-grupo-de-objeto-compra-com-grupo-bem", pattern = "/associacao-grupo-de-objeto-de-compra-e-grupo-patrimonial/ver/#{grupoObjetoCompraGrupoBemControlador.id}/",
        viewId = "/faces/administrativo/patrimonio/grupoobjetocompragrupobem/visualizar.xhtml"),
    @URLMapping(id = "listar-associacao-do-grupo-de-objeto-compra-com-grupo-bem", pattern = "/associacao-grupo-de-objeto-de-compra-e-grupo-patrimonial/listar/",
        viewId = "/faces/administrativo/patrimonio/grupoobjetocompragrupobem/lista.xhtml")
})
public class GrupoObjetoCompraGrupoBemControlador extends PrettyControlador<GrupoObjetoCompraGrupoBem> implements Serializable, CRUD {

    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;
    private List<GrupoObjetoCompraGrupoBem> listaGruposAssociados;
    private SistemaControlador sistemaControlador;

    public GrupoObjetoCompraGrupoBemControlador() {
        super(GrupoObjetoCompraGrupoBem.class);
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    private void inicializarAtributos() {
        listaGruposAssociados = new ArrayList<>();
    }

    @URLAction(mappingId = "novo-associacao-do-grupo-de-objeto-compra-com-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributos();
    }

    @URLAction(mappingId = "ver-associacao-do-grupo-de-objeto-compra-com-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-associacao-do-grupo-de-objeto-compra-com-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void excluir() {
        if (!grupoObjetoCompraGrupoBemFacade.grupoBemJaEstaEmUso(selecionado.getGrupoBem())) {
            super.excluir();
        } else {
            FacesUtil.addOperacaoNaoPermitida("A associação entre o grupo de bem " + selecionado.getGrupoBem() + " e o grupo de objeto de compra " + selecionado.getGrupoObjetoCompra() + " já está sendo usada e não poderá ser excluída.");
        }
    }

    @Override
    public void salvar() {
        try {
            if (isOperacaoNovo()) {
                validarSalvar();
                grupoObjetoCompraGrupoBemFacade.salvar(listaGruposAssociados);
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                redireciona();
            } else {
                super.salvar();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (listaGruposAssociados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Deve haver pelo menos uma associação de grupos.");
        }
        ve.lancarException();
    }

    public void atribuirGrupoBem(SelectEvent evt) {
        selecionado.setGrupoBem((GrupoBem) evt.getObject());
        selecionado.setGrupoObjetoCompra(null);
        RequestContext.getCurrentInstance().update("Formulario");
    }

    @Override
    public void redireciona() {
        if (isSessao()) {
            Web.limpaNavegacao();
        }
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/associacao-grupo-de-objeto-de-compra-e-grupo-patrimonial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return grupoObjetoCompraGrupoBemFacade;
    }

    public void adicionar() {
        try {
            if (validarVigencia()) {
                validarAssociacaoEntreGrupoBemEGrupoObjetoDeCompra();
                adicionarSelecionadoNaListaDosAssociados();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void adicionarSelecionadoNaListaDosAssociados() {
        Util.adicionarObjetoEmLista(listaGruposAssociados, selecionado);
        selecionado = new GrupoObjetoCompraGrupoBem();
    }

    private void validarAssociacaoEntreGrupoBemEGrupoObjetoDeCompra() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (grupoObjetoCompraGrupoBemFacade.jaExisteEstaAssociacao(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A associação entre o grupo de bem " + selecionado.getGrupoBem() + " e o grupo de objeto de compra " + selecionado.getGrupoObjetoCompra() + " já existe.");
        }
        if (!listaGruposAssociados.isEmpty()) {
            for (GrupoObjetoCompraGrupoBem grupo : listaGruposAssociados) {
                if (!grupo.equals(selecionado) && (grupo.getGrupoBem().equals(selecionado.getGrupoBem())) && (grupo.getGrupoObjetoCompra().equals(selecionado.getGrupoObjetoCompra()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um registro com o Grupo Patrimonial " + selecionado.getGrupoBem() +
                        "  e Grupo de Objeto de Compra " + selecionado.getGrupoObjetoCompra() + " adicionado");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    private boolean validarVigencia() {
        if (!DataUtil.isVigenciaValida(selecionado,
            grupoObjetoCompraGrupoBemFacade.recuperarGrupoObjetoCompraGrupoBem(selecionado.getGrupoBem(),
                selecionado.getGrupoObjetoCompra()))) {
            return false;
        }
        return true;
    }

    public void editarGrupo(GrupoObjetoCompraGrupoBem grupo) {
        selecionado = grupo;
    }

    public void removerGrupo(GrupoObjetoCompraGrupoBem grupo) {
        listaGruposAssociados.remove(grupo);
    }

    public List<GrupoObjetoCompraGrupoBem> getListaGruposAssociados() {
        return listaGruposAssociados;
    }

    public void setListaGruposAssociados(List<GrupoObjetoCompraGrupoBem> listaGruposAssociados) {
        this.listaGruposAssociados = listaGruposAssociados;
    }

    public void encerarVigencia() {
        selecionado.setFimVigencia(sistemaControlador.getDataOperacao());
        grupoObjetoCompraGrupoBemFacade.salvar(selecionado);
        FacesUtil.addOperacaoRealizada("Vigência encerrada com Sucesso!");
        redireciona();
    }

    public String getDataFormatada() {
        return DataUtil.getDataFormatada(sistemaControlador.getDataOperacao());
    }

    public String buscarNameTipoBemOuStringVazia() {
        if (selecionado.getGrupoBem() != null && selecionado.getGrupoBem().getTipoBem() != null) {
          return selecionado.getGrupoBem().getTipoBem().name();
        }
        return "";
    }

    public List<GrupoBem> completarGrupoBemFolhas(String parte) {
        return grupoObjetoCompraGrupoBemFacade.getGrupoBemFacade().buscarGrupoBemFolhasPorCodigoOrDescricao(parte);
    }

    public boolean temGrupoPatrimonialDefinido(){
        return selecionado.getGrupoBem() != null;
    }

}
