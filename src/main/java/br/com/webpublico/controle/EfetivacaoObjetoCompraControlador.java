package br.com.webpublico.controle;

import br.com.webpublico.entidades.EfetivacaoObjetoCompra;
import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.SituacaoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoObjetoCompraFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HardRock on 30/03/2017.
 */


@ManagedBean(name = "efetivacaoObjetoCompraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-efetivacao-objeto-compra", pattern = "/efetivacao-objeto-compra/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaoobjetocompra/edita.xhtml"),
    @URLMapping(id = "editar-efetivacao-objeto-compra", pattern = "/efetivacao-objeto-compra/editar/#{efetivacaoObjetoCompraControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoobjetocompra/edita.xhtml"),
    @URLMapping(id = "ver-efetivacao-objeto-compra", pattern = "/efetivacao-objeto-compra/ver/#{efetivacaoObjetoCompraControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoobjetocompra/visualizar.xhtml"),
    @URLMapping(id = "listar-efetivacao-objeto-compra", pattern = "/efetivacao-objeto-compra/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaoobjetocompra/lista.xhtml"),
})

public class EfetivacaoObjetoCompraControlador extends PrettyControlador<EfetivacaoObjetoCompra> implements Serializable, CRUD {

    @EJB
    private EfetivacaoObjetoCompraFacade facade;
    private GrupoObjetoCompra grupoObjetoCompra;
    private String palavraFiltro;
    private SituacaoObjetoCompra situacaoObjetoCompra;
    private ObjetoCompra objetoCompraSelecinado;
    private List<ObjetoCompra> objetosCompraSemelhantes;
    private List<ObjetoCompra> objetosCompraSelecionados;

    public EfetivacaoObjetoCompraControlador() {
        super(EfetivacaoObjetoCompra.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-objeto-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    @URLAction(mappingId = "nova-efetivacao-objeto-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataRegistro(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        if (selecionado.getObjetosCompra() == null) {
            selecionado.setObjetosCompra(new ArrayList<ObjetoCompra>());
        }
        objetoCompraSelecinado = null;
        instanciarObjetosCompraSemelhantes();
        objetosCompraSelecionados = Lists.newArrayList();
    }

    @Override
    @URLAction(mappingId = "editar-efetivacao-objeto-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        objetoCompraSelecinado = null;
        atribuirGrupoContaDespesa(selecionado.getObjetosCompra());
    }

    @Override
    @URLAction(mappingId = "ver-efetivacao-objeto-compra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(SituacaoObjetoCompra.AGUARDANDO, SituacaoObjetoCompra.AGUARDANDO.getDescricao()));
        toReturn.add(new SelectItem(SituacaoObjetoCompra.INDEFERIDO, SituacaoObjetoCompra.INDEFERIDO.getDescricao()));
        return toReturn;
    }


    public void pesquisarObjetosCompra() {
        List<ObjetoCompra> objetos = facade.getObjetoCompraFacade().buscarObjetoCompraPorGrupoOrSituacao(grupoObjetoCompra, situacaoObjetoCompra, objetoCompraSelecinado);
        atribuirGrupoContaDespesa(objetos);
        selecionado.setObjetosCompra(objetos);
    }

    private void atribuirGrupoContaDespesa(List<ObjetoCompra> objetos) {
        for (ObjetoCompra objeto : objetos) {
            if (objeto.getTipoObjetoCompra() != null && objeto.getGrupoObjetoCompra() != null) {
                try {
                    objeto.setGrupoContaDespesa(facade.getObjetoCompraFacade().criarGrupoContaDespesa(objeto.getTipoObjetoCompra(), objeto.getGrupoObjetoCompra()));
                } catch (ExcecaoNegocioGenerica e) {
                    continue;
                }
            }
        }
    }

    public void pesquisarObjetosCompraSemelhantes(ObjetoCompra objetoCompra) {
        objetoCompraSelecinado = objetoCompra;
        palavraFiltro = "";
        objetosCompraSemelhantes = facade.getObjetoCompraFacade().buscarObjetoCompraPorDescricaoAndSituacao(objetoCompra.getDescricao(), SituacaoObjetoCompra.DEFERIDO);
    }

    public void instanciarObjetosCompraSemelhantes() {
        objetosCompraSemelhantes = Lists.newArrayList();
    }

    @Override
    public void salvar() {
        selecionado.setObjetosCompra(objetosCompraSelecionados);
        super.salvar();
    }

    public void deferir() {
        try {
            validarProcesso();
            selecionado.setObjetosCompra(objetosCompraSelecionados);
            if (isOperacaoNovo()) {
                facade.deferir(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Objeto de compra deferidos com sucesso.");
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void indeferir() {
        try {
            selecionado.setObjetosCompra(objetosCompraSelecionados);
            validarProcesso();
            if (isOperacaoNovo()) {
                facade.indeferir(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Objeto de compra indeferidos com sucesso.");
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarProcesso() {
        Util.validarCampos(selecionado);
        validarDeferirIndeferir();
    }

    private void validarDeferirIndeferir() {
        ValidacaoException ve = new ValidacaoException();
        if (objetosCompraSelecionados == null || objetosCompraSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum objeto de compra foi selecionado.");
        }
        ve.lancarException();
    }

    public void selecionar(ObjetoCompra objetoCompra) {
        if (objetosCompraSelecionados.contains(objetoCompra)) {
            objetosCompraSelecionados.remove(objetoCompra);
        } else {
            objetosCompraSelecionados.add(objetoCompra);
        }

    }

    public void selecionarTodos() {
        if (objetosCompraSelecionados.size() == selecionado.getObjetosCompra().size()) {
            objetosCompraSelecionados.removeAll(selecionado.getObjetosCompra());
        } else {
            objetosCompraSelecionados.removeAll(selecionado.getObjetosCompra());
            for (ObjetoCompra objetoCompra : selecionado.getObjetosCompra()) {
                selecionar(objetoCompra);
            }
        }
    }

    public void visualizarObjetoCompra(ObjetoCompra objetoCompra) {
        ObjetoCompraControlador controlador = (ObjetoCompraControlador) Util.getControladorPeloNome("objetoCompraControlador");
        if (controlador != null) {
            FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + objetoCompra.getId() + "/");
        }
    }

    public void pesquisarPorPalavraEspecificaObjetoCompraSemelhante() {
        List<ObjetoCompra> list = Lists.newArrayList();
        if (palavraFiltro != null && !palavraFiltro.isEmpty()) {
            for (ObjetoCompra obj : objetosCompraSemelhantes) {
                if (obj.getCodigo().toString().trim().contains(palavraFiltro.trim())
                    || obj.getDescricao().trim().toUpperCase().contains(palavraFiltro.trim().toUpperCase())
                    || obj.getGrupoObjetoCompra().getCodigo().trim().contains(palavraFiltro.trim())
                    || obj.getGrupoObjetoCompra().getDescricao().trim().toUpperCase().contains(palavraFiltro.trim().toUpperCase())
                ) {
                    list.add(obj);
                }
            }
        }
        if (!list.isEmpty()) {
            objetosCompraSemelhantes = list;
            palavraFiltro = "";
            return;
        }
        pesquisarObjetosCompraSemelhantes(objetoCompraSelecinado);
    }

    public String icone(ObjetoCompra objetoCompra) {
        return objetosCompraSelecionados.contains(objetoCompra) ? "ui-icon-check" : "ui-icon-none";
    }

    public String title(ObjetoCompra objetoCompra) {
        return objetosCompraSelecionados.contains(objetoCompra) ? "Clique para desselecionar este objeto de compra." : "Clique para selecionar este objeto de compra.";
    }

    public String iconeTodos() {
        return (objetosCompraSelecionados.size() == selecionado.getObjetosCompra().size()) ? "ui-icon-check" : "ui-icon-none";
    }

    public String titleTodos() {
        return (objetosCompraSelecionados.size() == selecionado.getObjetosCompra().size()) ? "Clique para desselecionar todos objetos de compra." : "Clique para selecionar todos objetos de compra.";
    }

    public List<GrupoObjetoCompra> completarGrupoOjetoCompra(String parte) {
        return facade.getGrupoObjetoCompraFacade().buscarFiltrandoGrupoObjetoCompraPorCodigoOrDescricao(parte.trim());
    }

    public List<ObjetoCompra> completarObjetoCompra(String parte) {
        SituacaoObjetoCompra situacao = situacaoObjetoCompra != null ? situacaoObjetoCompra : SituacaoObjetoCompra.AGUARDANDO;
        return facade.getObjetoCompraFacade().buscarObjetoCompraPorSituacao(parte.trim(), situacao);
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public SituacaoObjetoCompra getSituacaoObjetoCompra() {
        return situacaoObjetoCompra;
    }

    public void setSituacaoObjetoCompra(SituacaoObjetoCompra situacaoObjetoCompra) {
        this.situacaoObjetoCompra = situacaoObjetoCompra;
    }

    public List<ObjetoCompra> getObjetosCompraSemelhantes() {
        return objetosCompraSemelhantes;
    }

    public void setObjetosCompraSemelhantes(List<ObjetoCompra> objetosCompraSemelhantes) {
        this.objetosCompraSemelhantes = objetosCompraSemelhantes;
    }

    public List<ObjetoCompra> getObjetosCompraSelecionados() {
        return objetosCompraSelecionados;
    }

    public void setObjetosCompraSelecionados(List<ObjetoCompra> objetosCompraSelecionados) {
        this.objetosCompraSelecionados = objetosCompraSelecionados;
    }

    public ObjetoCompra getObjetoCompraSelecinado() {
        return objetoCompraSelecinado;
    }

    public void setObjetoCompraSelecinado(ObjetoCompra objetoCompraSelecinado) {
        this.objetoCompraSelecinado = objetoCompraSelecinado;
    }

    public String getPalavraFiltro() {
        return palavraFiltro;
    }

    public void setPalavraFiltro(String palavraFiltro) {
        this.palavraFiltro = palavraFiltro;
    }
}
