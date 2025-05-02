package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.enums.SituacaoItemCertame;
import br.com.webpublico.enums.TipoAvaliacaoLicitacao;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.enums.TipoSituacaoLicitacao;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MapaComparativoTecnicaPrecoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 18/09/14
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-mapa-comparativo-tecnica-preco", pattern = "/mapa-comparativo-tecnica-preco/novo/", viewId = "/faces/administrativo/licitacao/mapacomparativotecnicapreco/edita.xhtml"),
        @URLMapping(id = "editar-mapa-comparativo-tecnica-preco", pattern = "/mapa-comparativo-tecnica-preco/editar/#{mapaComparativoTecnicaPrecoControlador.id}/", viewId = "/faces/administrativo/licitacao/mapacomparativotecnicapreco/edita.xhtml"),
        @URLMapping(id = "ver-mapa-comparativo-tecnica-preco", pattern = "/mapa-comparativo-tecnica-preco/ver/#{mapaComparativoTecnicaPrecoControlador.id}/", viewId = "/faces/administrativo/licitacao/mapacomparativotecnicapreco/visualizar.xhtml"),
        @URLMapping(id = "listar-mapa-comparativo-tecnica-preco", pattern = "/mapa-comparativo-tecnica-preco/listar/", viewId = "/faces/administrativo/licitacao/mapacomparativotecnicapreco/lista.xhtml")
})
public class MapaComparativoTecnicaPrecoControlador extends PrettyControlador<MapaComparativoTecnicaPreco> implements Serializable, CRUD {

    @EJB
    private MapaComparativoTecnicaPrecoFacade mapaComparativoTecnicaPrecoFacade;
    private List<PropostaFornecedor> propostasProcessadas = Lists.newArrayList();
    private List<ItemPropostaFornecedor> itensPropostaFornecedor = Lists.newArrayList();
    private ItemMapaComparativoTecnicaPreco itemMapaSelecionado;
    private Boolean habilitarEdicaoJustificativa;

    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public MapaComparativoTecnicaPrecoControlador() {
        super(MapaComparativoTecnicaPreco.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return mapaComparativoTecnicaPrecoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/mapa-comparativo-tecnica-preco/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-mapa-comparativo-tecnica-preco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    private void atribuirDataDoMapa() {
        if (selecionado.getLicitacao().isLicitacaoAberta()) {
            selecionado.setData(selecionado.getLicitacao().getAbertaEm());
        } else {
            selecionado.setData(mapaComparativoTecnicaPrecoFacade.getSistemaFacade().getDataOperacao());
        }
    }

    @Override
    @URLAction(mappingId = "ver-mapa-comparativo-tecnica-preco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        itensPropostaFornecedor = new ArrayList<>();
    }

    @Override
    @URLAction(mappingId = "editar-mapa-comparativo-tecnica-preco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        recuperarDados();
    }

    private void recuperarDados() {
        recuperarPropostasProcessadas();
    }

    private void recuperarPropostasProcessadas() {
        propostasProcessadas = getPropostasDaLicitacaoSelecionada();
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            if (selecionado.getLicitacao().isTipoApuracaoPrecoItem()) {
                validarItensMapaPorItensProcessoDeCompra();
                super.salvar();
            }
            if (selecionado.getLicitacao().isTipoApuracaoPrecoLote()) {
                validarItensMapaPorLotesProcessoDeCompra();
                buscarItensLoteVencedorWithValorDiferenteCotacao();
                validarValorDotadoToItensPropostaFornecedor();
                salvarSelecionadoAndJustificativasToItens();

            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarValorDotadoToItensPropostaFornecedor() {
        if (!itensPropostaFornecedor.isEmpty()) {
            habilitarEdicaoJustificativa = true;
            FacesUtil.atualizarComponente("form-itens-lote");
            FacesUtil.executaJavaScript("dialogItensLote.show()");
        }
    }

    private void salvarSelecionadoAndJustificativasToItens() {
        if (itensPropostaFornecedor.isEmpty()) {
            salvarSelecionadoAndJustificativas();
        }
    }


    public void salvarSelecionadoAndJustificativas() {
        try {
            validarJustificativaLotes();
            FacesUtil.executaJavaScript("dialogItensLote.hide();");
            mapaComparativoTecnicaPrecoFacade.getCertameFacade().getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            selecionado = mapaComparativoTecnicaPrecoFacade.salvarAlternativo(selecionado);
            salvarItensPropostaFornecedor();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarVisualizar();
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception exc) {
            FacesUtil.addOperacaoNaoRealizada(exc.getMessage());
        }
    }

    private void redirecionarVisualizar() {
        FacesUtil.redirecionamentoInterno("/mapa-comparativo-tecnica-preco/ver/" + selecionado.getId() + "/");
    }

    private void validarJustificativaLotes() {
        ValidacaoException ve = new ValidacaoException();
        for (ItemPropostaFornecedor itemPropostaFornecedor : itensPropostaFornecedor) {
            if (StringUtils.isEmpty(itemPropostaFornecedor.getJustificativa().trim())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Justificativa é obrigatório para o item <b>" + itemPropostaFornecedor.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao() + "</b>");
            }
        }

        ve.lancarException();
    }

    private void buscarItensLoteVencedorWithValorDiferenteCotacao() {
        itensPropostaFornecedor = Lists.newArrayList();
        for (ItemMapaComparativoTecnicaPreco itemMapaComparativoTecnicaPreco : selecionado.getItens()) {
            if (itemMapaComparativoTecnicaPreco.isSituacao(SituacaoItemCertame.VENCEDOR)) {
                buscarItensPropostaFornecedorPorItemMapaComparativo(itemMapaComparativoTecnicaPreco);
            }
        }


    }

    private void buscarItensPropostaFornecedorPorItemMapaComparativo(ItemMapaComparativoTecnicaPreco itemMapaComparativoTecnicaPreco) {
        List<ItemPropostaFornecedor> itensPropostaFornecedor = mapaComparativoTecnicaPrecoFacade
                .getLicitacaoFacade()
                .recuperarLotePropostaFornecedor(itemMapaComparativoTecnicaPreco
                        .getItemMapaComparativoTecnicaPrecoLoteProcesso()
                        .getLotePropostaVencedor()
                        .getId())
                .getItens();


        for (ItemPropostaFornecedor itemPropostaFornecedor : itensPropostaFornecedor) {
            if (itemPropostaFornecedor.getPrecoTotal().compareTo(itemPropostaFornecedor.getItemProcessoDeCompra()
                    .getItemSolicitacaoMaterial()
                    .getPrecoTotal()) > 0) {
                getItensPropostaFornecedor().add(itemPropostaFornecedor);

            }


        }

    }

    private void validarItensMapaPorLotesProcessoDeCompra() throws ValidacaoException {
        for (LoteProcessoDeCompra loteProcessoDeCompra : selecionado.getLotesProcessoDeCompra()) {
            List<ItemMapaComparativoTecnicaPreco> itensMapa = selecionado.getItensMapaPorLoteProcessoDeCompra(loteProcessoDeCompra);
            selecionado.hasMaisDeUmVencedorPorItem(itensMapa);
        }
    }

    private void validarItensMapaPorItensProcessoDeCompra() throws ValidacaoException {
        List<ItemProcessoDeCompra> itensProcessoDeCompra = selecionado.getItensProcessoDeCompra();
        for (ItemProcessoDeCompra itemProcesso : itensProcessoDeCompra) {
            List<ItemMapaComparativoTecnicaPreco> itensMapa = selecionado.getItensMapaPorItemProcessoDeCompra(itemProcesso);
            selecionado.hasMaisDeUmVencedorPorItem(itensMapa);
        }
    }

    private void salvarItensPropostaFornecedor() {
        for (ItemPropostaFornecedor itemPropostaFornecedor : itensPropostaFornecedor) {
            mapaComparativoTecnicaPrecoFacade.getCertameFacade().salvarItemPropostaFornecedor(itemPropostaFornecedor);
        }
    }

    @Override
    public void excluir() {
        if (!selecionado.getLicitacao().isTipoApuracaoPrecoItem()) {
            limparJustificativaDosItens();
        }
        super.excluir();
    }

    @Override
    public boolean validaRegrasParaExcluir() {
        if (selecionado.getLicitacao().isTipoApuracaoPrecoItem()) {
            AtaRegistroPreco ataRegistroPreco = mapaComparativoTecnicaPrecoFacade.getAtaRegistroPrecoFacade().buscarPorLicitacao(selecionado.getLicitacao());
            if (ataRegistroPreco != null) {
                FacesUtil.addOperacaoNaoPermitida("Foi encontrado ata de registro de preço para esta licitação. Este mapa comparativo não pode ser excluído.");
                return false;
            }
            List<StatusFornecedorLicitacao> status = mapaComparativoTecnicaPrecoFacade.getStatusFornecedorLicitacaoFacade().buscarPorLicitacao(selecionado.getLicitacao());
            if (status != null && !status.isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("Foi encontrado adjudicação ou homologação para esta licitação. Este mapa comparativo não pode ser excluído.");
                return false;
            }
        }
        return super.validaRegrasParaExcluir();
    }

    private void limparJustificativaDosItens() {
        itensPropostaFornecedor = new ArrayList<>();
        percorrerItensDoMapaComparativo();
        for (ItemPropostaFornecedor itemPropostaFornecedor : itensPropostaFornecedor) {
            itemPropostaFornecedor.setJustificativa(null);
        }
        salvarItensPropostaFornecedor();
    }

    private void percorrerItensDoMapaComparativo() {
        for (ItemMapaComparativoTecnicaPreco itemMapaComparativo : selecionado.getItens()) {
            if (SituacaoItemCertame.CLASSIFICADO.equals(itemMapaComparativo.getSituacaoItem()) || SituacaoItemCertame.VENCEDOR.equals(itemMapaComparativo.getSituacaoItem())) {
                buscarItensPropostaFornecedorDoItemCertame(itemMapaComparativo);
            }
        }
    }

    private void buscarItensPropostaFornecedorDoItemCertame(ItemMapaComparativoTecnicaPreco itemMapaComparativoTecnicaPreco) {
        for (ItemPropostaFornecedor itemPropostaFornecedor : mapaComparativoTecnicaPrecoFacade.getLicitacaoFacade().recuperarLotePropostaFornecedor(itemMapaComparativoTecnicaPreco.getItemMapaComparativoTecnicaPrecoLoteProcesso().getLotePropostaVencedor().getId()).getItens()) {
            itensPropostaFornecedor.add(itemPropostaFornecedor);
        }
    }

    public void buscarItensPropostaFornecedor(ItemMapaComparativoTecnicaPreco itemMapaComparativoTecnicaPreco) {

        List<ItemPropostaFornecedor> itensPropostas = mapaComparativoTecnicaPrecoFacade.getLicitacaoFacade().recuperarLotePropostaFornecedor(itemMapaComparativoTecnicaPreco.getItemMapaComparativoTecnicaPrecoLoteProcesso().getLotePropostaVencedor().getId()).getItens();
        itensPropostaFornecedor = new ArrayList<>();
        itensPropostaFornecedor.addAll(itensPropostas);
    }

    public Boolean isApuracaoPrecoLote(){
        return selecionado.getLicitacao().isTipoApuracaoPrecoLote();
    }

    public Boolean isLicitacaoAdjudicadaAndHomologada() {
        try {

            Licitacao licitacao = mapaComparativoTecnicaPrecoFacade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId());

            boolean hasHomologada = false;
            boolean hasAdjudicada = false;

            for (StatusLicitacao statusLicitacao : licitacao.getListaDeStatusLicitacao()) {
                if (statusLicitacao.isTipoHomologada()) {
                    hasHomologada = true;
                } else if (statusLicitacao.isTipoAdjudicada()) {
                    hasAdjudicada = true;
                }
            }

            return hasAdjudicada && hasHomologada;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public List<PropostaFornecedor> getPropostasProcessadas() {
        return propostasProcessadas;
    }

    public void setPropostasProcessadas(List<PropostaFornecedor> propostasProcessadas) {
        this.propostasProcessadas = propostasProcessadas;
    }

    public List<Licitacao> buscarLicitacoes(String parte) {
        return mapaComparativoTecnicaPrecoFacade.getLicitacaoFacade().buscarLicitacaoPorTipoAvaliacaoAndNumeroOrDescricaoOrExercico(parte.trim(), TipoAvaliacaoLicitacao.TECNICA_PRECO);
    }

    public List<SelectItem> getSituacoesItem() {
        return Util.getListSelectItem(SituacaoItemCertame.values());
    }

    public void processarLicitacaoSelecionada() {
        carregarListasLicitacaoSelecionada();
        atribuirDataDoMapa();
        selecionado.limparItens();
        limparPropostasAnalisadas();
        if (isLicitacaoValida()) {
            propostasProcessadas = getPropostasDaLicitacaoSelecionada();
            criarItensDoMapa();
            definirSituacaoDosItens();
        }
    }

    private void definirSituacaoDosItens() {
        if (selecionado.getLicitacao().isTipoApuracaoPrecoItem()) {
            definirSituacaoDosItensMapaPorItemProcessoDeCompra();
        }
        if (selecionado.getLicitacao().isTipoApuracaoPrecoLote()) {
            definirSituacaoDosItensMapaPorLoteProcessoDeCompra();
        }
    }

    private void definirSituacaoDosItensMapaPorLoteProcessoDeCompra() {
        List<LoteProcessoDeCompra> lotesProcessoDeCompra = selecionado.getLotesProcessoDeCompra();
        for (LoteProcessoDeCompra loteProcessoDeCompra : lotesProcessoDeCompra) {
            List<ItemMapaComparativoTecnicaPreco> itensMapaPorLoteProcessoDeCompra = selecionado.getItensMapaPorLoteProcessoDeCompra(loteProcessoDeCompra);

            for (ItemMapaComparativoTecnicaPreco itemMapa : itensMapaPorLoteProcessoDeCompra) {
                ItemMapaComparativoTecnicaPreco itemComMaiorClassificacaoFinal = selecionado.getItemComMaiorClassificacaoFinalPorLoteProcessoDeCompra(loteProcessoDeCompra);
                itemMapa.setSituacaoItem(SituacaoItemCertame.CLASSIFICADO);
                if (itemMapa.equals(itemComMaiorClassificacaoFinal)) {
                    itemMapa.setSituacaoItem(SituacaoItemCertame.VENCEDOR);
                }
                if (itemMapa.hasPrecoPropostoMaiorQueReservado()) {
                    itemMapa.setSituacaoItem(SituacaoItemCertame.DESCLASSIFICADO);
                    itemMapa.setJustificativa(getMensagemSituacaoDesabilitada(itemMapa));
                }
                if (!itemMapa.hasPreco()) {
                    itemMapa.setSituacaoItem(SituacaoItemCertame.NAO_COTADO);
                    itemMapa.setJustificativa(getMensagemSituacaoNaoCotado());
                }
            }
        }
    }

    private void definirSituacaoDosItensMapaPorItemProcessoDeCompra() {
        List<ItemProcessoDeCompra> itensProcessoDeCompra = selecionado.getItensProcessoDeCompra();
        for (ItemProcessoDeCompra itemProcessoDeCompra : itensProcessoDeCompra) {
            List<ItemMapaComparativoTecnicaPreco> itensMapaPorItemProcessoDeCompra = selecionado.getItensMapaPorItemProcessoDeCompra(itemProcessoDeCompra);
            for (ItemMapaComparativoTecnicaPreco itemMapa : itensMapaPorItemProcessoDeCompra) {
                ItemMapaComparativoTecnicaPreco itemMapaComMaiorNotaClassificacaoFinal = selecionado.getItemComMaiorClassificacaoFinalPorItemProcessoDeCompra(itemProcessoDeCompra);
                itemMapa.setSituacaoItem(SituacaoItemCertame.CLASSIFICADO);
                if (itemMapa.equals(itemMapaComMaiorNotaClassificacaoFinal)) {
                    itemMapa.setSituacaoItem(SituacaoItemCertame.VENCEDOR);
                }
                if (itemMapa.hasPrecoPropostoMaiorQueReservado()) {
                    itemMapa.setSituacaoItem(SituacaoItemCertame.DESCLASSIFICADO);
                    itemMapa.setJustificativa(getMensagemSituacaoDesabilitada(itemMapa));
                }
                if (!itemMapa.hasPreco()) {
                    itemMapa.setSituacaoItem(SituacaoItemCertame.NAO_COTADO);
                    itemMapa.setJustificativa(getMensagemSituacaoNaoCotado());
                }
            }
        }
    }

    private void criarItensDoMapa() {
        for (PropostaFornecedor proposta : propostasProcessadas) {
            proposta = getPropostaFornecedorRecuperada(proposta);

            for (LotePropostaFornecedor loteProposta : proposta.getLotes()) {
                if (selecionado.getLicitacao().isTipoApuracaoPrecoItem()) {
                    for (ItemPropostaFornecedor itemProposta : loteProposta.getItens()) {
                        itemProposta.setItemProcessoDeCompra(mapaComparativoTecnicaPrecoFacade.getProcessoDeCompraFacade().buscarItensPropostaFornecedorPorItemProcessoDeCompra(itemProposta.getItemProcessoDeCompra().getId()));
                        selecionado.criarItemPorItemProposta(itemProposta, getIndiceTecnico(itemProposta.getFornecedor()));
                    }
                }
                if (selecionado.getLicitacao().isTipoApuracaoPrecoLote()) {
                    loteProposta.setLoteProcessoDeCompra(mapaComparativoTecnicaPrecoFacade.getLoteProcessoDeCompraFacade().recuperar(loteProposta.getLoteProcessoDeCompra().getId()));
                    selecionado.criarItemPorLoteProposta(loteProposta, getIndiceTecnico(loteProposta.getFornecedor()));
                }
            }
        }
    }

    private PropostaFornecedor getPropostaFornecedorRecuperada(PropostaFornecedor proposta) {
        return mapaComparativoTecnicaPrecoFacade.getPropostaFornecedorFacade().recuperar(proposta.getId());
    }

    public List<ItemMapaComparativoTecnicaPreco> getItensMapaPorFornecedor(Pessoa fornecedor) {
        List<ItemMapaComparativoTecnicaPreco> itens = Lists.newArrayList();
        for (ItemMapaComparativoTecnicaPreco itemMapa : selecionado.getItens()) {
            if (selecionado.getLicitacao().isTipoApuracaoPrecoItem() && itemMapa.getItemMapaComparativoTecnicaPrecoItemProcesso().getItemPropostaVencedor().getPropostaFornecedor().getFornecedor().equals(fornecedor)) {
                itens.add(itemMapa);
            }
            if (selecionado.getLicitacao().isTipoApuracaoPrecoLote() && itemMapa.getItemMapaComparativoTecnicaPrecoLoteProcesso().getLotePropostaVencedor().getPropostaFornecedor().getFornecedor().equals(fornecedor)) {
                itens.add(itemMapa);
            }
        }
        return itens;
    }

    private void limparPropostasAnalisadas() {
        if (propostasProcessadas != null) {
            propostasProcessadas.clear();
        }
    }

    public BigDecimal retornarNotaTecnicaDaProposta(Pessoa fornecedor) {
        return mapaComparativoTecnicaPrecoFacade.getPropostaTecnicaFacade().recuperarNotaTecnicaDoFornecedor(fornecedor, selecionado.getLicitacao());
    }

    public BigDecimal getIndiceTecnico(Pessoa fornecedor) {
        BigDecimal notaTecnica = retornarNotaTecnicaDaProposta(fornecedor);
        List<PropostaTecnica> propostas = mapaComparativoTecnicaPrecoFacade.getPropostaTecnicaFacade().buscarPorLicitacao(selecionado.getLicitacao());
        ComparatorPropostaTecnica.ordenarPorNotaTecnicaDesc(propostas);
        BigDecimal maiorNotaTecnica = propostas.get(0).getNotaTecnica();

        return notaTecnica.divide(maiorNotaTecnica, 2, BigDecimal.ROUND_HALF_UP);
    }

    private boolean isLicitacaoValida() {
        if (!validaStatusLicitacao()) {
            cancelarLicitacaoSelecionada();
            return false;
        }
        if (!validaPropostaFornecedor()) {
            cancelarLicitacaoSelecionada();
            return false;
        }
        if (!validaVinculoDaLicitacaoComMapaComparativoTecnicaPreco()) {
            cancelarLicitacaoSelecionada();
            return false;
        }
        return true;
    }

    private void cancelarLicitacaoSelecionada() {
        selecionado.setLicitacao(null);
    }

    private boolean validaStatusLicitacao() {
        if (!TipoSituacaoLicitacao.ANDAMENTO.equals(selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao())
                && !TipoSituacaoLicitacao.EM_RECURSO.equals(selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao())) {
            FacesUtil.addOperacaoNaoPermitida("A licitação deve estar em andamento ou em recurso. Status atual da licitação informada: " + selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao().getDescricao());
            return false;
        }
        return true;
    }

    private boolean validaPropostaFornecedor() {
        if (getPropostasDaLicitacaoSelecionada().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório fazer a(s) proposta(s) de preço do(s) fornecedor(es) antes de gerar o mapa comparativo.");
            return false;
        }
        return true;
    }

    private List<PropostaFornecedor> getPropostasDaLicitacaoSelecionada() {
        return mapaComparativoTecnicaPrecoFacade.getPropostaFornecedorFacade().buscarPorLicitacao(selecionado.getLicitacao());
    }

    private boolean validaVinculoDaLicitacaoComMapaComparativoTecnicaPreco() {
        if (mapaComparativoTecnicaPrecoFacade.licitacaoTemVinculoEmUmMapaComparativoTecnicaPreco(selecionado.getLicitacao())) {
            FacesUtil.addOperacaoNaoPermitida("Esta licitação já está vinculada em um mapa comparativo de técnica e preço.");
            return false;
        }
        return true;
    }

    private void carregarListasLicitacaoSelecionada() {
        selecionado.setLicitacao(mapaComparativoTecnicaPrecoFacade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId()));
    }

    public String getMensagemTipoApuracao() {
        if (selecionado.getLicitacao().isTipoApuracaoPrecoItem()) {
            return "Esta licitação é do tipo apuração por item.";
        }
        return "Esta licitação é do tipo apuração por lote.";
    }

    public List<ItemPropostaFornecedor> getItensPropostaFornecedor() {
        return itensPropostaFornecedor;
    }

    public void setItensPropostaFornecedor(List<ItemPropostaFornecedor> itensPropostaFornecedor) {
        this.itensPropostaFornecedor = itensPropostaFornecedor;
    }

    public String getMensagemSituacaoDesabilitada(ItemMapaComparativoTecnicaPreco itemMapa) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        return "Item desclassificado devido o valor da proposta ser maior que o valor reservado na dotação. "
                + "Valor proposta: R$ " + df.format(itemMapa.getPrecoItem())
                + ". Valor reservado: R$ " + df.format(itemMapa.getPrecoReservado()) + ".";
    }

    public String getMensagemSituacaoNaoCotado() {
        return "Item não cotado.";
    }

    public void abrirJustificativa(ItemMapaComparativoTecnicaPreco itemMapa) {
        selecionarItemMapa(itemMapa);
        FacesUtil.atualizarComponente("formJustificativa");
        FacesUtil.executaJavaScript("dialogJustificativa.show()");
    }

    public ItemMapaComparativoTecnicaPreco getItemMapaSelecionado() {
        return itemMapaSelecionado;
    }

    public void setItemMapaSelecionado(ItemMapaComparativoTecnicaPreco itemMapaSelecionado) {
        this.itemMapaSelecionado = itemMapaSelecionado;
    }

    public void selecionarItemMapa(ItemMapaComparativoTecnicaPreco itemMapa) {
        itemMapaSelecionado = itemMapa;
    }

    public void confirmarJustificativa() {
        if (!itemMapaSelecionado.hasJustificativa()) {
            FacesUtil.addOperacaoNaoPermitida("O campo justificativa deve ser informado.");
            return;
        }
        FacesUtil.executaJavaScript("dialogJustificativa.hide()");
    }

    public Boolean getHabilitarEdicaoJustificativa() {
        return habilitarEdicaoJustificativa;
    }

    public void setHabilitarEdicaoJustificativa(Boolean habilitarEdicaoJustificativa) {
        this.habilitarEdicaoJustificativa = habilitarEdicaoJustificativa;
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.MAPA_COMPARATVO_TECNICA_PRECO);
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


class ComparatorPropostaTecnica {
    public static void ordenarPorNotaTecnicaDesc(List<PropostaTecnica> propostas) {
        Comparator<PropostaTecnica> comparator = new Comparator<PropostaTecnica>() {
            @Override
            public int compare(PropostaTecnica o1, PropostaTecnica o2) {
                return o2.getNotaTecnica().compareTo(o1.getNotaTecnica());
            }
        };
        Collections.sort(propostas, comparator);
    }
}
