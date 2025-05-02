package br.com.webpublico.controle.contabil;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidades.contabil.NotaExplicativa;
import br.com.webpublico.entidades.contabil.NotaExplicativaItem;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.execucao.NotaExplicativaFacade;
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
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoNotaExplicativa", pattern = "/contabil/nota-explicativa/novo/", viewId = "/faces/financeiro/nota-explicativa/edita.xhtml"),
    @URLMapping(id = "editarNotaExplicativa", pattern = "/contabil/nota-explicativa/editar/#{notaExplicativaControlador.id}/", viewId = "/faces/financeiro/nota-explicativa/edita.xhtml"),
    @URLMapping(id = "listarNotaExplicativa", pattern = "/contabil/nota-explicativa/listar/", viewId = "/faces/financeiro/nota-explicativa/lista.xhtml"),
    @URLMapping(id = "verNotaExplicativa", pattern = "/contabil/nota-explicativa/ver/#{notaExplicativaControlador.id}/", viewId = "/faces/financeiro/nota-explicativa/visualizar.xhtml")
})
public class NotaExplicativaControlador extends PrettyControlador<NotaExplicativa> implements Serializable, CRUD {
    @EJB
    private NotaExplicativaFacade facade;
    private TipoRelatorioItemDemonstrativo tipoRelatorio;
    private NotaExplicativaItem notaItem;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public NotaExplicativaControlador() {
        super(NotaExplicativa.class);
    }

    @URLAction(mappingId = "novoNotaExplicativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarVariaveisNovoEditar();
    }

    @URLAction(mappingId = "editarNotaExplicativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarVariaveisNovoEditar();
        tipoRelatorio = selecionado.getRelatoriosItemDemonst().getTipoRelatorioItemDemonstrativo();
    }

    @URLAction(mappingId = "verNotaExplicativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/nota-explicativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void inicializarVariaveisNovoEditar() {
        tipoRelatorio = null;
        novoNotaItem();
    }

    public void adicionarNotaItem() {
        try {
            Util.validarCampos(notaItem);
            notaItem.setNotaExplicativa(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getItens(), notaItem);
            novoNotaItem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void editarNotaItem(NotaExplicativaItem notaExplicativaItem) {
        notaItem = (NotaExplicativaItem) Util.clonarObjeto(notaExplicativaItem);
    }

    public void removerNotaItem(NotaExplicativaItem notaExplicativaItem) {
        selecionado.getItens().remove(notaExplicativaItem);
    }

    public void novoNotaItem() {
        notaItem = new NotaExplicativaItem();
    }

    public void limparRelatorio() {
        selecionado.setRelatoriosItemDemonst(null);
    }

    public void limparItens() {
        selecionado.setItens(Lists.newArrayList());
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItem(TipoRelatorioItemDemonstrativo.values(), false);
    }

    public List<RelatoriosItemDemonst> completarRelatorios(String parte) {
        return facade.buscarRelatorios(parte, tipoRelatorio);
    }

    public List<ItemDemonstrativo> completarItensPorRelatorio(String parte) {
        return facade.buscarItensPorRelatorio(parte, selecionado.getRelatoriosItemDemonst());
    }

    public TipoRelatorioItemDemonstrativo getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorioItemDemonstrativo tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public NotaExplicativaItem getNotaItem() {
        return notaItem;
    }

    public void setNotaItem(NotaExplicativaItem notaItem) {
        this.notaItem = notaItem;
    }
}
