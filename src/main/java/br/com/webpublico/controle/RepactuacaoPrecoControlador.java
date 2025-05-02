package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.ItemRepactuacaoPreco;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.ParecerRepactuacaoPreco;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RepactuacaoPrecoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 14/04/14
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-repactuacao-preco", pattern = "/repactuacao-preco/novo/", viewId = "/faces/administrativo/licitacao/repactuacaodepreco/edita.xhtml"),
    @URLMapping(id = "editar-repactuacao-preco", pattern = "/repactuacao-preco/editar/#{repactuacaoPrecoControlador.id}/", viewId = "/faces/administrativo/licitacao/repactuacaodepreco/edita.xhtml"),
    @URLMapping(id = "ver-repactuacao-preco", pattern = "/repactuacao-preco/ver/#{repactuacaoPrecoControlador.id}/", viewId = "/faces/administrativo/licitacao/repactuacaodepreco/visualizar.xhtml"),
    @URLMapping(id = "listar-repactuacao-preco", pattern = "/repactuacao-preco/listar/", viewId = "/faces/administrativo/licitacao/repactuacaodepreco/lista.xhtml")
})
public class RepactuacaoPrecoControlador extends PrettyControlador<RepactuacaoPreco> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(RepactuacaoPrecoControlador.class);

    @EJB
    private RepactuacaoPrecoFacade facade;
    private List<ItemRepactuacaoPreco> itens;

    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public RepactuacaoPrecoControlador() {
        super(RepactuacaoPreco.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/repactuacao-preco/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-repactuacao-preco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        iniciarItens();
    }

    @Override
    @URLAction(mappingId = "ver-repactuacao-preco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        iniciarItens();
        criarItemRepactuacaoAPartirObjetoRepactuacao();
    }

    @Override
    @URLAction(mappingId = "editar-repactuacao-preco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        criarItemRepactuacaoAPartirObjetoRepactuacao();
        try {
            facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getAtaRegistroPreco().getLicitacao());
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            redirecionarParaVer();
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getAtaRegistroPreco().getLicitacao());
            selecionado = facade.salvarRetornando(selecionado, itens);
            redirecionarParaVer();
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            criarItemRepactuacaoAPartirObjetoRepactuacao();
            facade.remover(selecionado, itens);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void iniciarItens() {
        itens = Lists.newArrayList();
    }

    private void criarItemRepactuacaoAPartirObjetoRepactuacao() {
        iniciarItens();
        if (isPregao()) {
            List<ItemPregao> itensPregao = facade.getPregaoFacade().buscarItensPregaoVencedorPorFornecedor(selecionado.getAtaRegistroPreco().getLicitacao(), selecionado.getFornecedor().getEmpresa());
            for (ObjetoCompraRepactuacao itemObjeto : selecionado.getItens()) {
                ItemPregao itemPregaoVencedor = getItemPregaoVencedor(itensPregao, itemObjeto);
                if (itemPregaoVencedor != null) {
                    ItemRepactuacaoPreco itemRepactuacao = new ItemRepactuacaoPreco(itemPregaoVencedor);
                    if (!isOperacaoNovo()) {
                        itemRepactuacao.setPrecoAtual(itemObjeto.getPrecoAtual());
                        itemRepactuacao.setPrecoProposto(itemObjeto.getPrecoProposto());
                        itemRepactuacao.atribuirQuantidadeOrValorRestante(facade.getQtdeOrValorUtilizadoEmContrato(selecionado, itemPregaoVencedor.getItemPregaoItemProcesso().getItemProcessoDeCompra()));
                        atribuirSitucaoPreco(itemRepactuacao);
                    }
                    Util.adicionarObjetoEmLista(itens, itemRepactuacao);
                }
            }
        } else {
            for (ObjetoCompraRepactuacao itemObjeto : selecionado.getItens()) {
                ItemCertame itemCertameVencedor = getItemCertameVencedor(getCertame().getListaItemCertame(), itemObjeto);
                if (itemCertameVencedor != null) {
                    ItemRepactuacaoPreco itemRepactuacao = new ItemRepactuacaoPreco(itemCertameVencedor);
                    if (!isOperacaoNovo()) {
                        itemRepactuacao.setPrecoAtual(itemObjeto.getPrecoAtual());
                        itemRepactuacao.setPrecoProposto(itemObjeto.getPrecoProposto());
                        itemRepactuacao.atribuirQuantidadeOrValorRestante(facade.getQtdeOrValorUtilizadoEmContrato(selecionado, itemCertameVencedor.getItemCertameItemProcesso().getItemProcessoDeCompra()));
                    }
                    Util.adicionarObjetoEmLista(itens, itemRepactuacao);
                }
            }
        }
    }

    private ItemPregao getItemPregaoVencedor(List<ItemPregao> itensPregao, ObjetoCompraRepactuacao item) {
        for (ItemPregao itemPregao : itensPregao) {
            if (isMesmoItemObjetoCompra(item.getDescricaoComplementar(), item.getObjetoCompra(),
                itemPregao.getDescricaoComplementar(), itemPregao.getObjetoCompra())) {
                return itemPregao;
            }
        }
        return null;
    }

    private ItemCertame getItemCertameVencedor(List<ItemCertame> itensPregao, ObjetoCompraRepactuacao item) {
        for (ItemCertame itemCertame : itensPregao) {
            if (isMesmoItemObjetoCompra(item.getDescricaoComplementar(), item.getObjetoCompra(),
                itemCertame.getDescricaoProdutoItem(), itemCertame.getObjetoCompra())) {
                return itemCertame;
            }
        }
        return null;
    }

    public boolean isMesmoItemObjetoCompra(String especif1, ObjetoCompra objetoCompra1, String
        especif2, ObjetoCompra objetoCompra2) {
        if (!Strings.isNullOrEmpty(especif1) && !Strings.isNullOrEmpty(especif2)) {
            return objetoCompra1.equals(objetoCompra2) && especif1.trim().equals(especif2.trim());
        }
        return objetoCompra1.equals(objetoCompra2);
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void validarSalvar() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (!hasItemSelecionado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar é necessário ter ao menos um item com o valor unitário proposto preenchido.");
        }
        ve.lancarException();
        for (ItemRepactuacaoPreco item : itens) {
            if (item.getPrecoProposto() != null && item.getPrecoProposto().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor unitário proposto deve ser maior que 0 (zero).");
            }
        }
        ve.lancarException();
    }

    private boolean hasItemSelecionado() {
        boolean selecionou = false;
        for (ItemRepactuacaoPreco item : itens) {
            if (item.hasPrecoProposto()) {
                selecionou = true;
                break;
            }
        }
        return selecionou;
    }


    public void listenerFornecedor() {
        try {
            validarRegrasParaPreencherItensRepactuacao();
            iniciarItens();
            itens = facade.criarItensRepactuacao(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarRegrasParaPreencherItensRepactuacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAtaRegistroPreco().getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido repactuar o preço para processo de maior desconto.");
        }
        if (selecionado.getAtaRegistroPreco().getLicitacao().isTipoApuracaoPrecoLote()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido repactuar o preço para processo de apuração por lote.");
        }
        ve.lancarException();
    }

    private Certame getCertame() {
        return facade.getCertame(selecionado.getAtaRegistroPreco().getLicitacao());
    }

    public boolean isPregao() {
        return selecionado.getAtaRegistroPreco().getLicitacao().isPregao() || selecionado.getAtaRegistroPreco().getLicitacao().isRDCPregao();
    }

    public void atribuirSitucaoPreco(ItemRepactuacaoPreco item) {
        if (item.hasPrecoProposto() && item.getPrecoAtual().compareTo(item.getPrecoProposto()) > 0) {
            item.setSituacaoPrecoItem(ItemRepactuacaoPreco.SituacaoPrecoItem.DIMINUIU);
        } else if (item.hasPrecoProposto() && item.getPrecoAtual().compareTo(item.getPrecoProposto()) == 0) {
            item.setSituacaoPrecoItem(ItemRepactuacaoPreco.SituacaoPrecoItem.PERMANECEU);
        } else if (item.hasPrecoProposto() && item.getPrecoAtual().compareTo(item.getPrecoProposto()) < 0) {
            item.setSituacaoPrecoItem(ItemRepactuacaoPreco.SituacaoPrecoItem.AUMENTOU);
        } else {
            item.setSituacaoPrecoItem(ItemRepactuacaoPreco.SituacaoPrecoItem.NAO_INFORMADO);
        }
    }

    public List<SelectItem> getParecer() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ParecerRepactuacaoPreco parecer : ParecerRepactuacaoPreco.values()) {
            toReturn.add(new SelectItem(parecer, parecer.getDescricao()));
        }
        return toReturn;
    }

    public List<Pessoa> completarPessoaFisica(String parte) {
        return facade.getPessoaFacade().listaPessoasFisicas(parte.trim());
    }

    public void atribuirNullAtaRegistroDePreco() {
        selecionado.setAtaRegistroPreco(null);
        selecionado.setFornecedor(null);
        iniciarItens();
    }

    public List<ItemRepactuacaoPreco> getItens() {
        return itens;
    }

    public void setItens(List<ItemRepactuacaoPreco> itens) {
        this.itens = itens;
    }

    public List<AtaRegistroPreco> getListaAtaDeRegistroDePreco() {
        return facade.getAtaRegistroPrecoFacade().listaDecrescente();
    }

    public List<LicitacaoFornecedor> getListaLicitacaoFornecedor() {
        return facade.getLicitacaoFacade().getFornecedoresVencedoresHabilitados(selecionado.getAtaRegistroPreco().getLicitacao());
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.REPACTUACAO_PRECO);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

}
