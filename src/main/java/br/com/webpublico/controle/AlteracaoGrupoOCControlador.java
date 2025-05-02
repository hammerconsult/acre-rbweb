/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AlteracaoGrupoOC;
import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.entidades.ItemAlteracaoGrupoOC;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlteracaoGrupoOCFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "alteracaoGrupoOCControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAlteracaoGrupoOC", pattern = "/alteracao-grupo-objeto-compra/novo/", viewId = "/faces/administrativo/licitacao/alteracaogrupooc/edita.xhtml"),
    @URLMapping(id = "listarAlteracaoGrupoOC", pattern = "/alteracao-grupo-objeto-compra/listar/", viewId = "/faces/administrativo/licitacao/alteracaogrupooc/lista.xhtml"),
    @URLMapping(id = "verAlteracaoGrupoOC", pattern = "/alteracao-grupo-objeto-compra/ver/#{alteracaoGrupoOCControlador.id}/", viewId = "/faces/administrativo/licitacao/alteracaogrupooc/visualizar.xhtml")
})
public class AlteracaoGrupoOCControlador extends PrettyControlador<AlteracaoGrupoOC> implements Serializable, CRUD {

    @EJB
    private AlteracaoGrupoOCFacade alteracaoGrupoOCFacade;
    private ItemAlteracaoGrupoOC itemAlteracaoGrupoOC;

    @Override
    public AbstractFacade getFacede() {
        return alteracaoGrupoOCFacade;
    }

    public AlteracaoGrupoOCControlador() {
        super(AlteracaoGrupoOC.class);
    }

    @URLAction(mappingId = "novoAlteracaoGrupoOC", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        itemAlteracaoGrupoOC = new ItemAlteracaoGrupoOC();
        selecionado.setTipoObjetoCompra(TipoObjetoCompra.CONSUMO);
        selecionado.setDataAlteracao(alteracaoGrupoOCFacade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(alteracaoGrupoOCFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setItensAlteracaoGrupoOCS(new ArrayList<ItemAlteracaoGrupoOC>());
    }

    @URLAction(mappingId = "verAlteracaoGrupoOC", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        for (ItemAlteracaoGrupoOC item : selecionado.getItensAlteracaoGrupoOCS()) {
            atribuirGrupoContaDespesa(item);
        }
    }

    public void atribuirGrupoObjetoCompraOrigem() {
        itemAlteracaoGrupoOC.setGrupoObjetoCompraOrigem(itemAlteracaoGrupoOC.getObjetoCompra().getGrupoObjetoCompra());
        atribuirGrupoContaDespesa(itemAlteracaoGrupoOC);
    }

    public void atribuirGrupoContaDespesa(ItemAlteracaoGrupoOC item) {
        try {
            if (item.getObjetoCompra() != null) {
                item.setGrupoContaDespesaOrigem(alteracaoGrupoOCFacade.getObjetoCompraFacade().criarGrupoContaDespesa(item.getObjetoCompra().getTipoObjetoCompra(), item.getGrupoObjetoCompraOrigem()));
            }

            if (item.getGrupoObjetoCompraDestino() != null) {
                item.setGrupoContaDespesaDestino(alteracaoGrupoOCFacade.getObjetoCompraFacade().criarGrupoContaDespesa(selecionado.getTipoObjetoCompra(), item.getGrupoObjetoCompraDestino()));
            }
        } catch (ExcecaoNegocioGenerica e) {
            logger.debug(e.getMessage());
        }
    }

    public List<ObjetoCompra> buscarObjetoCompraTipo(String codigoOrDescricao) {
        if (selecionado.getTipoObjetoCompra() != null) {
            return alteracaoGrupoOCFacade.getObjetoCompraFacade().buscarObjetoCompraPorCodigoOrDescricaoAndTipoObjetoCompra(codigoOrDescricao, selecionado.getTipoObjetoCompra());
        }
        return null;
    }

    public List<GrupoObjetoCompra> buscarGrupoObjetoCompraPorTipoObjetoCompra(String codigoOrDescricao) {
        if (selecionado.getTipoObjetoCompra() != null) {
            return alteracaoGrupoOCFacade.getObjetoCompraFacade().getGrupoObjetoCompraFacade().buscarFiltrandoGrupoObjetoCompraPorTipoObjetoCompra(codigoOrDescricao, selecionado.getTipoObjetoCompra());
        }
        FacesUtil.addCampoObrigatorio("Informe o tipo do objeto de compra.");
        return null;
    }

    public List<SelectItem> buscarTiposObjetoCompra() {
        return Util.getListSelectItem(Arrays.asList(TipoObjetoCompra.values()));
    }

    public void removerGrupoObjetoDeCompra() {
        itemAlteracaoGrupoOC = new ItemAlteracaoGrupoOC();
    }

    private void validarListaVazia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItensAlteracaoGrupoOCS().isEmpty() || selecionado.getItensAlteracaoGrupoOCS() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existem Objetos de Compras para serem alterados.");
        }
        ve.lancarException();
    }

    private void validarAlteracaoGrupoObjetoCompra() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoObjetoCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O Tipo de Objeto de Compra é obrigatório.");
        }
        if (itemAlteracaoGrupoOC.getObjetoCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Objeto de Compra é obrigatório.");
        }
        if (itemAlteracaoGrupoOC.getGrupoObjetoCompraDestino() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Grupo de Objeto de Compra Destino é obrigatório.");
        }
        ve.lancarException();

        if (alteracaoGrupoOCFacade.getObjetoCompraFacade().utilizadoEmProcessoLicitatorio(itemAlteracaoGrupoOC.getObjetoCompra())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido 'De / Para' onde o objeto de compra já está em uso no processo.");

        } else if (itemAlteracaoGrupoOC.getGrupoContaDespesaOrigem() != null
            && itemAlteracaoGrupoOC.getGrupoContaDespesaDestino() != null
            && !itemAlteracaoGrupoOC.getGrupoContaDespesaOrigem().getIdGrupo().equals(itemAlteracaoGrupoOC.getGrupoContaDespesaDestino().getIdGrupo())) {
            ve.adicionarMensagemDeCampoObrigatorio("Não é permitido 'De / Para' para " + selecionado.getTipoObjetoCompra().getDescricaoGrupo() + " diferentes.");
        }
        ve.lancarException();

        if (!selecionado.getItensAlteracaoGrupoOCS().isEmpty() || selecionado.getItensAlteracaoGrupoOCS() != null) {
            for (ItemAlteracaoGrupoOC item : selecionado.getItensAlteracaoGrupoOCS()) {
                if (!item.equals(itemAlteracaoGrupoOC) && item.getObjetoCompra().getId().compareTo(itemAlteracaoGrupoOC.getObjetoCompra().getId()) == 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe este Objeto de Compra adicionado na lista.");
                }
            }
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarListaVazia();
            for (ItemAlteracaoGrupoOC iagoc : selecionado.getItensAlteracaoGrupoOCS()) {
                iagoc.getObjetoCompra().setGrupoObjetoCompra(iagoc.getGrupoObjetoCompraDestino());
                alteracaoGrupoOCFacade.getObjetoCompraFacade().salvar(iagoc.getObjetoCompra());
            }
            alteracaoGrupoOCFacade.salvarNovo(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada("Grupo objeto de compra alterado com sucesso.");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public void adicionarItemAlteracaoGrupoOC() {
        try {
            validarAlteracaoGrupoObjetoCompra();
            itemAlteracaoGrupoOC.setAlteracaoGrupoOC(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getItensAlteracaoGrupoOCS(), itemAlteracaoGrupoOC);
            itemAlteracaoGrupoOC = new ItemAlteracaoGrupoOC();
            FacesUtil.executaJavaScript("setaFoco('Formulario:objetoDeCompra_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void reverterAlteracaoGrupoObjetoCompra(ItemAlteracaoGrupoOC item) {
        try {
            ObjetoCompra objetoCompra = item.getObjetoCompra();
            GrupoObjetoCompra grupoOrigem = item.getGrupoObjetoCompraOrigem();
            GrupoObjetoCompra grupoDestino = item.getGrupoObjetoCompraDestino();

            item.setGrupoObjetoCompraOrigem(grupoDestino);
            item.setGrupoObjetoCompraDestino(grupoOrigem);
            alteracaoGrupoOCFacade.salvarItem(item);

            objetoCompra.setGrupoObjetoCompra(grupoOrigem);
            alteracaoGrupoOCFacade.getObjetoCompraFacade().salvar(objetoCompra);

            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarItemAlteracaoGrupoOC(ItemAlteracaoGrupoOC itemAltGoc) {
        itemAlteracaoGrupoOC = (ItemAlteracaoGrupoOC) Util.clonarObjeto(itemAltGoc);
    }

    public void removerItemAlteracaoGrupoOC(ItemAlteracaoGrupoOC item) {
        selecionado.getItensAlteracaoGrupoOCS().remove(item);
    }

    public ItemAlteracaoGrupoOC getItemAlteracaoGrupoOC() {
        return itemAlteracaoGrupoOC;
    }

    public void setItemAlteracaoGrupoOC(ItemAlteracaoGrupoOC itemAlteracaoGrupoOC) {
        this.itemAlteracaoGrupoOC = itemAlteracaoGrupoOC;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/alteracao-grupo-objeto-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
