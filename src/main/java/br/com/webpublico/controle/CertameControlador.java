/*
 * Codigo gerado automaticamente em Fri Jan 20 10:46:54 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CertameFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
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
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean(name = "certameControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-certame", pattern = "/mapa-comparativo/novo/", viewId = "/faces/administrativo/licitacao/certame/edita.xhtml"),
    @URLMapping(id = "editar-certame", pattern = "/mapa-comparativo/editar/#{certameControlador.id}/", viewId = "/faces/administrativo/licitacao/certame/edita.xhtml"),
    @URLMapping(id = "ver-certame", pattern = "/mapa-comparativo/ver/#{certameControlador.id}/", viewId = "/faces/administrativo/licitacao/certame/visualizar.xhtml"),
    @URLMapping(id = "listar-certame", pattern = "/mapa-comparativo/listar/", viewId = "/faces/administrativo/licitacao/certame/lista.xhtml")
})
public class CertameControlador extends PrettyControlador<Certame> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CertameControlador.class);

    @EJB
    private CertameFacade facade;
    private ItemCertame itemCertameSelecionado;
    private LotePropostaFornecedor lotePropostaFornecedor;
    private BigDecimal percentualDescontoLote;
    private BigDecimal valorDescontoLote;
    private AmparoLegal amparoLegal;
    private String membrosComissaoEmString;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public CertameControlador() {
        super(Certame.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-certame", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-certame", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        amparoLegal = facade.getAmparoLegalFacade().buscarAmparoLegalPorLicitacao(selecionado.getLicitacao());
        membrosComissaoEmString = getMembrosComissaoEmString(facade.getLicitacaoFacade().buscarMembrosComissaoSelecionados(selecionado.getLicitacao()));
    }

    @URLAction(mappingId = "editar-certame", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        ordenaListaItemCertamePorNumeroAsc(selecionado.getListaItemCertame());
        try {
            facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/mapa-comparativo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarMapaComparativo();
            facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            if (selecionado.isTipoApuracaoPrecoLote()) {
                lotePropostaFornecedor = getLotePropostaVencedor();
                atribuirValorUnitarioMaiorDescontoItemProposta();
            } else {
                salvarSelecionado();
            }
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar mapa comparativo", e);
            logger.debug("Problema ao salvar mapa comparativo {}", e);
            descobrirETratarException(e);
        }
    }

    public void confirmarDistruicaoLote() {
        try {
            validarDistribuicaoLote();
            salvarSelecionado();
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar mapa comparativo", e);
            logger.debug("Problema ao salvar mapa comparativo {}", e);
            descobrirETratarException(e);
        }
    }

    private void salvarSelecionado() {
        selecionado = facade.salvarRetornando(selecionado, lotePropostaFornecedor);
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        redirecionarParaVerOrEditar(selecionado.getId(), "ver");
    }

    @Override
    public void excluir() {
        try {
            validarExcluir();
            facade.remover(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<Licitacao> buscarLicitacoes(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacaoParaCertameFiltrandoNumeroOrDescricaoOrExercico(parte.trim());
    }

    public List<SelectItem> getSituacaoItemCertame() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (SituacaoItemCertame object : SituacaoItemCertame.values()) {
            if (object.isRecebeAlteracao()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public void preencherLista() {
        try {
            if (selecionado.getLicitacao().isTipoApuracaoPrecoItem()) {
                carregarItensDoCertameAPartirDosItensDaProposta();
            } else if (selecionado.getLicitacao().isTipoApuracaoPrecoLote()) {
                carregarItensDoCertameAPartirDosLotesDaProposta();
            }
            processarSituacaoDeCadaItem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void processarSituacaoDeCadaItem() {
        selecionado.processarSituacaoDosItens();
        verificarItemComValorDiferenteDaCotacao();
    }

    private void verificarItemComValorDiferenteDaCotacao() {
        for (ItemCertame itemCertame : selecionado.getListaItemCertame()) {
            if (!selecionado.isMaiorDesconto()) {
                if (selecionado.isTipoApuracaoPrecoItem()) {
                    BigDecimal valorItemLicitacao = itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco();
                    if (itemCertame.getPrecoItem().compareTo(valorItemLicitacao) > 0 && !SituacaoItemCertame.NAO_COTADO.equals(itemCertame.getSituacaoItemCertame())) {
                        itemCertame.setMotivo("O item possui um valor maior do que cotado na licitação!");
                        itemCertame.setSituacaoItemCertame(SituacaoItemCertame.DESCLASSIFICADO);
                    }
                } else if (selecionado.isTipoApuracaoPrecoLote()) {
                    BigDecimal valorItemLoteLicitacao = itemCertame.getItemCertameLoteProcesso().getLoteProcessoDeCompra().getValor();
                    if (itemCertame.getValorTotal().compareTo(valorItemLoteLicitacao) > 0 && !SituacaoItemCertame.NAO_COTADO.equals(itemCertame.getSituacaoItemCertame())) {
                        itemCertame.setMotivo("O lote possui um valor maior do que cotado na licitação!");
                        itemCertame.setSituacaoItemCertame(SituacaoItemCertame.DESCLASSIFICADO);
                    }
                }
            }
        }
    }

    public void atribuirValorUnitarioMaiorDescontoItemProposta() {
        BigDecimal valorLote = lotePropostaFornecedor.getLoteProcessoDeCompra().getValor();
        if (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
            TipoControleLicitacao tipoControle = facade.getProcessoDeCompraFacade().getTipoControleProcesso(lotePropostaFornecedor.getLoteProcessoDeCompra());
            if (tipoControle != null && tipoControle.isTipoControlePorQuantidade()) {
                BigDecimal valorDesconto = valorLote.multiply(lotePropostaFornecedor.getPercentualDesconto()).divide(new BigDecimal("100"), 8, RoundingMode.HALF_EVEN);
                lotePropostaFornecedor.setValor(valorLote.subtract(valorDesconto));
            }
        }
        BigDecimal diferenca = valorLote.subtract(lotePropostaFornecedor.getValor());
        percentualDescontoLote = diferenca.divide(valorLote, 10, RoundingMode.HALF_EVEN).multiply(new BigDecimal("100"));
        valorDescontoLote = valorLote.subtract(lotePropostaFornecedor.getValor());

        lotePropostaFornecedor.setItens(facade.getItemPropostaFornecedorFacade().buscarPorLoteProposta(lotePropostaFornecedor));
        aplicarValorAosItens();
        FacesUtil.executaJavaScript("dlgDistribuirValor.show()");
        FacesUtil.atualizarComponente("formDistribuirValor");
    }

    public void iniciarDistribuicaoValoresItem(ItemCertame itemCertame) {
        lotePropostaFornecedor = itemCertame.getLotePropostaFornecedorVencedor();
        atribuirValorUnitarioMaiorDescontoItemProposta();
    }

    public LotePropostaFornecedor getLotePropostaVencedor() {
        Optional<ItemCertame> first = selecionado.getListaItemCertame()
            .stream()
            .filter(item -> item.getLotePropostaFornecedorVencedor() != null && item.isVencedor()).findFirst();
        if (first.isPresent()) {
            return first.get().getLotePropostaFornecedorVencedor();
        }
        throw new ValidacaoException("Lote vencedor não encontrado para distribuir o valor aos itens da proposta.");
    }

    public void aplicarValorAosItens() {
        for (ItemPropostaFornecedor item : lotePropostaFornecedor.getItens()) {
            int scale = item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida() != null
                ? item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida().getMascaraValorUnitario().getQuantidadeCasasDecimais()
                : 2;
            BigDecimal valorUnitario = item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco().setScale(scale, RoundingMode.HALF_EVEN);
            BigDecimal percentualPorItem = valorUnitario.multiply(percentualDescontoLote).divide(new BigDecimal("100"), 8, RoundingMode.HALF_EVEN).setScale(scale, RoundingMode.HALF_EVEN);
            BigDecimal valor = percentualPorItem.compareTo(BigDecimal.ZERO) > 0 ? valorUnitario.subtract(percentualPorItem) : valorUnitario;
            item.setPreco(valor);
        }
    }

    public void cancelarDistruicaoLote() {
        try {
            itemCertameSelecionado = null;
            FacesUtil.executaJavaScript("dlgDistribuirValor.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarDistribuicaoLote() {
        ValidacaoException ve = new ValidacaoException();
        for (ItemPropostaFornecedor item : lotePropostaFornecedor.getItens()) {
            if (item.getPreco() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Preço Unitário do item: <b>" + item.getDescricaoItem() + "</b> deve ser informado.");

            } else if (item.getPreco().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do item de nº " + item.getNumeroItem() + " deve ser maior ou igual a Zero.");
            }
            if (item.getPreco().compareTo(item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco()) > 0
                && (Strings.isNullOrEmpty(item.getJustificativa()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do item de nº " + item.getNumeroItem() + " é maior que o valor cotado, portanto o campo justificativa deve ser informado.");
            }
        }
        if (lotePropostaFornecedor.getValorTotalItens().compareTo(lotePropostaFornecedor.getValor()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O preço total dos itens do lote deve ser igual ao valor negociado com o fornecedor." +
                " Valor Negociado: <b> " + Util.formataValor(lotePropostaFornecedor.getValor()) + "</b>" +
                " Valor Informado: <b> " + Util.formataValor(lotePropostaFornecedor.getValorTotalItens()) + "</b>");
        }
        ve.lancarException();
    }


    public boolean bloquearCampoSituacaoQuandoDesclassificado(ItemCertame item) {
        if (selecionado.isTipoApuracaoPrecoItem()) {
            if (selecionado.isMaiorDesconto()) {
                BigDecimal percentualDescontoProcesso = item.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPercentualDescontoMinimo();
                return item.getPercentualDesconto().compareTo(percentualDescontoProcesso) < 0 && !SituacaoItemCertame.NAO_COTADO.equals(item.getSituacaoItemCertame());
            }
            BigDecimal valorItemLicitacao = item.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco();
            if (item.getPrecoItem().compareTo(valorItemLicitacao) > 0 && !SituacaoItemCertame.NAO_COTADO.equals(item.getSituacaoItemCertame())) {
                return true;
            }
        } else if (selecionado.isTipoApuracaoPrecoLote()) {
            if (selecionado.isMaiorDesconto()) {
                BigDecimal percentualDescontoProcesso = item.getItemCertameLoteProcesso().getLoteProcessoDeCompra().getLoteSolicitacaoMaterial().getPercentualDescontoMinimo();
                return item.getPercentualDesconto().compareTo(percentualDescontoProcesso) < 0 && !SituacaoItemCertame.NAO_COTADO.equals(item.getSituacaoItemCertame());
            }
            BigDecimal valorItemLoteLicitacao = item.getItemCertameLoteProcesso().getLoteProcessoDeCompra().getValor();
            if (item.getValorTotal().compareTo(valorItemLoteLicitacao) > 0 && !SituacaoItemCertame.NAO_COTADO.equals(item.getSituacaoItemCertame())) {
                return true;
            }
        }
        return false;
    }

    public boolean isItensAdjudicados() {
        List<ItemProcessoDeCompra> itens = facade.getLicitacaoFacade().recuperarItensDoProcessoDeCompraPorLicitacaoAndSituacaoDoItem(selecionado.getLicitacao(), SituacaoItemProcessoDeCompra.ADJUDICADO);
        return !itens.isEmpty();
    }

    public boolean isBloquearEdicaoItem(ItemCertame itemCertame) {
        if (selecionado.isTipoApuracaoPrecoLote() && !SituacaoItemCertame.NAO_COTADO.equals(itemCertame.getSituacaoItemCertame())) {
            itemCertame.getItemCertameLoteProcesso().getLoteProcessoDeCompra().setItensProcessoDeCompra(facade.getProcessoDeCompraFacade().buscarItensProcessoCompraPorLote(itemCertame.getItemCertameLoteProcesso().getLoteProcessoDeCompra()));
            for (ItemProcessoDeCompra itemProcessoDeCompra : itemCertame.getItemCertameLoteProcesso().getLoteProcessoDeCompra().getItensProcessoDeCompra()) {
                if (itemProcessoDeCompra.getSituacaoItemProcessoDeCompra().equals(SituacaoItemProcessoDeCompra.ADJUDICADO)) {
                    return true;
                }
            }
        }

        if (selecionado.isTipoApuracaoPrecoItem()
            && !SituacaoItemCertame.NAO_COTADO.equals(itemCertame.getSituacaoItemCertame())
            && isSituacaoAdjudicado(itemCertame)) {
            return true;
        }
        return false;
    }

    private static boolean isSituacaoAdjudicado(ItemCertame itemCertame) {
        return itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra().getSituacaoItemProcessoDeCompra().equals(SituacaoItemProcessoDeCompra.ADJUDICADO);
    }

    public void processarSituacao(ItemCertame item) {
        itemCertameSelecionado = item;
        if (itemCertameSelecionado.isVencedor()) {
            selecionado.classificarItensEmpatadosComMenorPreco(itemCertameSelecionado);
            FacesUtil.executaJavaScript("dialogAlteracaoSituacao.show()");
        }
        if (itemCertameSelecionado.isClassificado() || itemCertameSelecionado.isDesclassificado() || itemCertameSelecionado.isFrustrado() || itemCertameSelecionado.isInexequivel()) {
            FacesUtil.executaJavaScript("dialogAlteracaoSituacao.show()");
        }
    }

    private void validarMapaComparativo() throws ValidacaoException {
        Util.validarCampos(selecionado);
        validarRegrasDeNegocio();
    }

    private void validarRegrasDeNegocio() throws ValidacaoException {
        validarLicitacaoHomologada();
        validarItemEmpatado();
        validarItemComApenasUmVencedor();
        validarVencedorQuandoTiverAlgumClassificado();
    }

    private void validarVencedorQuandoTiverAlgumClassificado() {
        for (ItemCertame itemClassificado : selecionado.getItensClassificados()) {
            selecionado.validarVencedorQuandoTiverAlgumClassificado(itemClassificado);
        }
    }

    private void validarItemComApenasUmVencedor() throws ValidacaoException {
        try {
            selecionado.validarItemComApenasUmVencedor();
        } catch (ValidacaoException ve) {
            throw new ValidacaoException(ve.getMessage() + ". Informe apenas um vencedor para salvar!");
        }
    }

    private void validarItemEmpatado() throws ValidacaoException {
        try {
            selecionado.validarItemEmpatado();
        } catch (ValidacaoException ve) {
            throw new ValidacaoException(ve.getMessage() + ". Resolva o empate para salvar!");
        }
    }

    private void validarLicitacaoHomologada() throws ValidacaoException {
        if (isOperacaoEditar()) {
            facade.getLicitacaoFacade().validaExclusaoLicitacaoHomologada(selecionado.getLicitacao());
        }
    }


    public void carregarItensDoCertame() {
        if (isLicitacaoValida()) {
            limparListaItensCertame();
            setarDataDoMapaComparativo();
            preencherLista();
            ordenaListaItemCertamePorNumeroAsc(selecionado.getListaItemCertame());
        }
    }

    private void limparListaItensCertame() {
        selecionado.setListaItemCertame(new ArrayList<ItemCertame>());
    }

    private void setarDataDoMapaComparativo() {
        if (selecionado.getLicitacao().isLicitacaoAberta()) {
            selecionado.setDataRealizado(selecionado.getLicitacao().getAbertaEm());
        } else {
            selecionado.setDataRealizado(facade.getSistemaFacade().getDataOperacao());
        }
    }

    public void ordenaListaItemCertamePorNumeroAsc(List<ItemCertame> itens) {
        Collections.sort(itens, new Comparator<ItemCertame>() {
            @Override
            public int compare(ItemCertame o1, ItemCertame o2) {
                int retorno = 0;
                if (o1.getItemCertameItemProcesso() != null && o2.getItemCertameItemProcesso() != null) {
                    retorno = o1.getItemCertameItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero()
                        .compareTo(o2.getItemCertameItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero());
                }
                if (o1.getItemCertameLoteProcesso() != null && o2.getItemCertameLoteProcesso() != null) {
                    retorno = o1.getItemCertameLoteProcesso().getNumeroLote()
                        .compareTo(o2.getItemCertameLoteProcesso().getNumeroLote());
                }

                if (retorno == 0) {
                    if (selecionado.isTipoApuracaoPrecoItem()) {
                        retorno = o1.getNumeroItem().compareTo(o2.getNumeroItem());
                    } else {
                        retorno = o1.getNumeroLote().compareTo(o2.getNumeroLote());
                    }
                }
                return retorno;
            }
        });
    }

    public void ordenarListaItemPropostaFornecedorCrescente(List<ItemPropostaFornecedor> lista) {
        Collections.sort(lista, new Comparator<ItemPropostaFornecedor>() {
            @Override
            public int compare(ItemPropostaFornecedor o1, ItemPropostaFornecedor o2) {
                if (o1.getPreco() != null && o2.getPreco() != null) {
                    return o1.getPreco().compareTo(o2.getPreco());
                }
                return 0;
            }
        });
    }

    public void ordenarListaLotePropostaFornecedorCrescente(List<LotePropostaFornecedor> lotes) {
        Collections.sort(lotes, new Comparator<LotePropostaFornecedor>() {
            @Override
            public int compare(LotePropostaFornecedor o1, LotePropostaFornecedor o2) {
                return o1.getValor().compareTo(o2.getValor());
            }
        });
    }

    public void carregarItensDoCertameAPartirDosItensDaProposta() {
        List<ItemPropostaFornecedor> todosItensDasPropostasDaLicitacao = facade.getLicitacaoFacade().recuperaItensProposta(selecionado.getLicitacao());
        ordenarListaItemPropostaFornecedorCrescente(todosItensDasPropostasDaLicitacao);
        for (ItemPropostaFornecedor itemPropostaFornecedor : todosItensDasPropostasDaLicitacao) {
            selecionado.setListaItemCertame(Util.adicionarObjetoEmLista(selecionado.getListaItemCertame(), getNovoItemCertame(itemPropostaFornecedor)));
        }
    }

    public void carregarItensDoCertameAPartirDosLotesDaProposta() {
        List<LotePropostaFornecedor> todosLotesDasPropostasDaLicitacao = facade.getLicitacaoFacade().recuperaLotesDaProposta(selecionado.getLicitacao());
        validarValorLote(todosLotesDasPropostasDaLicitacao);
        ordenarListaLotePropostaFornecedorCrescente(todosLotesDasPropostasDaLicitacao);
        for (LotePropostaFornecedor lotePropostaFornecedor : todosLotesDasPropostasDaLicitacao) {
            selecionado.setListaItemCertame(Util.adicionarObjetoEmLista(selecionado.getListaItemCertame(), getNovoLoteCertame(lotePropostaFornecedor)));
        }
    }

    private void validarValorLote(List<LotePropostaFornecedor> lotes) {
        ValidacaoException ex = new ValidacaoException();
        for (LotePropostaFornecedor lote : lotes) {
            if (lote.getValor() == null) {
                ex.adicionarMensagemDeOperacaoNaoRealizada("O lote: <b>" + lote.getDescricaoLote() + "</b> do fornecedor: <b>" + lote.getFornecedor().getNome() + "</b> está com o valor vazio!");
            }
        }
        if (ex.temMensagens()) {
            throw ex;
        }
    }

    private ItemCertame getNovoItemCertame(ItemPropostaFornecedor itemPropostaFornecedor) {
        ItemCertame itemCertame = new ItemCertame();
        itemCertame.setCertame(selecionado);
        itemCertame.setSituacaoItemCertame(SituacaoItemCertame.CLASSIFICADO);
        itemCertame.setItemCertameItemProcesso(new ItemCertameItemProcesso());
        itemCertame.getItemCertameItemProcesso().setItemCertame(itemCertame);
        itemCertame.getItemCertameItemProcesso().setItemPropostaVencedor(itemPropostaFornecedor);
        itemCertame.getItemCertameItemProcesso().setItemProcessoDeCompra(itemPropostaFornecedor.getItemProcessoDeCompra());
        itemCertame.getItemCertameItemProcesso().setValorItem(itemPropostaFornecedor.getPreco());
        itemCertame.setValorItem(itemPropostaFornecedor.getPreco());
        return itemCertame;
    }

    public ItemCertame getNovoLoteCertame(LotePropostaFornecedor lotePropostaFornecedor) {
        ItemCertame itemCertame = new ItemCertame();
        itemCertame.setCertame(selecionado);
        itemCertame.setSituacaoItemCertame(SituacaoItemCertame.CLASSIFICADO);
        itemCertame.setItemCertameLoteProcesso(new ItemCertameLoteProcesso());
        itemCertame.getItemCertameLoteProcesso().setItemCertame(itemCertame);
        itemCertame.getItemCertameLoteProcesso().setLotePropFornecedorVencedor(lotePropostaFornecedor);
        itemCertame.getItemCertameLoteProcesso().setLoteProcessoDeCompra(lotePropostaFornecedor.getLoteProcessoDeCompra());
        itemCertame.setValorItem(lotePropostaFornecedor.getValor());
        return itemCertame;
    }

    private boolean validaStatusLicitacao(Licitacao licitacao) {
        licitacao.setListaDeStatusLicitacao(facade.getLicitacaoFacade().recuperarListaDeStatus(licitacao));

        if (!licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.ANDAMENTO)
            && !licitacao.getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.EM_RECURSO)) {
            return false;
        }
        return true;
    }

    private void validarExcluir() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.setLicitacao(facade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId()));
        if (TipoSituacaoLicitacao.ADJUDICADA.equals(selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A licitação do mapa comparativo está adjudicada.");
        }
        if (TipoSituacaoLicitacao.HOMOLOGADA.equals(selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A licitação do mapa comparativo está homologada.");
        }
        ve.lancarException();
    }


    public boolean isLicitacaoValida() {
        if (!facade.getLicitacaoFacade().licitacaoTemPropostaFornecedor(selecionado.getLicitacao())) {
            FacesUtil.addOperacaoNaoPermitida("A licitação selecionada deve ter pelo menos uma proposta de fornecedor realizada.");
            cancelarLicitacaoSelecionada();
            return false;
        }
        if (!facade.getLicitacaoFacade().validaSeLicitacaoNaoPossuiOutroCertame(selecionado.getLicitacao())) {
            FacesUtil.addOperacaoNaoPermitida("A licitação selecionada já está vinculada a um mapa comparativo.");
            cancelarLicitacaoSelecionada();
            return false;
        }
        if (!validaStatusLicitacao(selecionado.getLicitacao())) {
            FacesUtil.addOperacaoNaoPermitida("A licitação deve estar com status atual em andamento ou em recurso. Status atual da licitação informada: " + selecionado.getLicitacao().getStatusAtualLicitacao());
            cancelarLicitacaoSelecionada();
            return false;
        }
        return true;
    }

    private void cancelarLicitacaoSelecionada() {
        selecionado.setLicitacao(null);
    }

    public boolean isEditar() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public boolean isVisualizar() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public String getMensagemTipoApuracao() {
        if (selecionado.isTipoApuracaoPrecoItem()) {
            return "Esta licitação é do tipo apuração por item.";
        }
        return "Esta licitação é do tipo apuração por lote.";
    }

    public boolean isLicitacaoInformada() {
        try {
            return selecionado.getLicitacao() != null;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void confirmarMotivoAlteracaoSituacaoItem() {
        if (podeConfirmarAlteracaoSituacaoItem()) {
            selecionado.setListaItemCertame(Util.adicionarObjetoEmLista(selecionado.getListaItemCertame(), itemCertameSelecionado));
            FacesUtil.executaJavaScript("dialogAlteracaoSituacao.hide()");
            cancelarItemCertameSelecionado();
        }
    }

    public void cancelarItemCertameSelecionado() {
        itemCertameSelecionado = null;
    }

    private boolean podeConfirmarAlteracaoSituacaoItem() {
        if (itemCertameSelecionado.getMotivo() == null || itemCertameSelecionado.getMotivo().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O campo Motivo deve ser informado.");
            return false;
        }
        return true;
    }

    public String getStyleCor(ItemCertame item) {
        try {
            if (item.isVencedor()) {
                return "verde";
            }
            if (item.isEmpate()) {
                return "marrom";
            }
            if (item.isDesclassificado() || item.isInexequivel() || item.isFrustrado() || item.isNaoCotado()) {
                return "vermelho-escuro";
            }
            return "#000000 !important;";
        } catch (Exception ex) {
            return "#000000 !important;";
        }
    }

    public String toStringItemSelecionado() {
        String retorno = "";
        try {
            retorno = itemCertameSelecionado.getDescricaoItem();
            if (itemCertameSelecionado.isTipoApuracaoPrecoItem()) {
                retorno += selecionado.isMaiorDesconto()
                    ? " - " + Util.formataPercentual(itemCertameSelecionado.getPercentualDesconto())
                    : " - " + Util.formataValor(itemCertameSelecionado.getPrecoItem());
            }
            return retorno;
        } catch (Exception ex) {
            return retorno;
        }
    }

    public boolean validarValorNoItem(ItemCertame itemCertame) {
        BigDecimal precoOrDesconto = selecionado.isMaiorDesconto() ? itemCertame.getPercentualDesconto() : itemCertame.getPrecoItem();
        return BigDecimal.ZERO.compareTo(precoOrDesconto) < 0;
    }

    public LotePropostaFornecedor getLotePropostaFornecedor() {
        return lotePropostaFornecedor;
    }

    public void recuperarItensPropostaFornecedor(ItemCertame itemCertameSelecionado) {
        this.itemCertameSelecionado = itemCertameSelecionado;
        lotePropostaFornecedor = itemCertameSelecionado.getLotePropostaFornecedorVencedor();
        lotePropostaFornecedor.setItens(facade.getItemPropostaFornecedorFacade().buscarPorLoteProposta(lotePropostaFornecedor));
    }

    public String getDescricaoLicitacao() {
        return selecionado.getLicitacao().getModalidadeLicitacao().getDescricao() + " - "
                + selecionado.getLicitacao().getNaturezaDoProcedimento().getDescricao() + " - "
                + selecionado.getLicitacao().getNumeroAnoLicitacao();
    }

    private String getMembrosComissaoEmString(List<MembroComissao> membros) {
        return membros.stream()
                .map(membro -> membro.getPessoaFisica().getNome())
                .collect(Collectors.joining(", "));
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            String nomeRelatorio = "Relatório do Mapa Comparativo Certame";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente().getDescricao());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("MODALIDADE", selecionado.getLicitacao().getModalidadeLicitacao().getDescricao());
            dto.adicionarParametro("idCertame", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/certame/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.MAPA_COMPARATVO);
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

    public BigDecimal getPercentualDescontoLote() {
        return percentualDescontoLote;
    }

    public void setPercentualDescontoLote(BigDecimal percentualDescontoLote) {
        this.percentualDescontoLote = percentualDescontoLote;
    }

    public BigDecimal getValorDescontoLote() {
        return valorDescontoLote;
    }

    public void setValorDescontoLote(BigDecimal valorDescontoLote) {
        this.valorDescontoLote = valorDescontoLote;
    }

    public AmparoLegal getAmparoLegal() {
        return amparoLegal;
    }

    public void setAmparoLegal(AmparoLegal amparoLegal) {
        this.amparoLegal = amparoLegal;
    }

    public String getMembrosComissaoEmString() {
        return membrosComissaoEmString;
    }

    public void setMembrosComissaoEmString(String membrosComissaoEmString) {
        this.membrosComissaoEmString = membrosComissaoEmString;
    }

    public ItemCertame getItemCertameSelecionado() {
        return itemCertameSelecionado;
    }

    public void setItemCertameSelecionado(ItemCertame itemCertameSelecionado) {
        this.itemCertameSelecionado = itemCertameSelecionado;
    }
}
