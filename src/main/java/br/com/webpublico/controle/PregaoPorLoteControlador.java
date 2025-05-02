/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.licitacao.ItemPregaoLanceVencedor;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PregaoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author lucas
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-pregao-por-lote", pattern = "/pregao/por-lote/novo/",
        viewId = "/faces/administrativo/licitacao/pregao/por-lote/edita.xhtml"),

    @URLMapping(id = "editar-pregao-por-lote", pattern = "/pregao/por-lote/editar/#{pregaoPorLoteControlador.id}/",
        viewId = "/faces/administrativo/licitacao/pregao/por-lote/edita.xhtml"),

    @URLMapping(id = "ver-pregao-por-lote", pattern = "/pregao/por-lote/ver/#{pregaoPorLoteControlador.id}/",
        viewId = "/faces/administrativo/licitacao/pregao/por-lote/visualizar.xhtml"),

    @URLMapping(id = "listar-pregao-por-lote", pattern = "/pregao/por-lote/listar/",
        viewId = "/faces/administrativo/licitacao/pregao/por-lote/lista.xhtml")
})
public class PregaoPorLoteControlador extends PrettyControlador<Pregao> implements Serializable, CRUD {

    @EJB
    private PregaoFacade facade;
    private ItemPregao itemPregaoSelecionado;
    private RodadaPregao rodadaPregaoSelecionada;
    private int indiceAba;
    private List<LicitacaoFornecedor> fornecedoresParticipantes;
    private List<ItemProcessoDeCompra> itensLoteProcessoCompra;
    private BigDecimal percentualDiferencaLote;

    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public PregaoPorLoteControlador() {
        super(Pregao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/pregao/por-lote/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-pregao-por-lote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setRealizadoEm(facade.getLicitacaoFacade().getSistemaFacade().getDataOperacao());
        rodadaPregaoSelecionada = new RodadaPregao();
    }

    @URLAction(mappingId = "ver-pregao-por-lote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarPregaoPorLote(getId());
        for (ItemPregao item : selecionado.getListaDeItemPregao()) {
            item.setLancesVencedores(facade.buscarItensPregaoLanceVencedor(item));
        }
    }

    @URLAction(mappingId = "editar-pregao-por-lote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        rodadaPregaoSelecionada = new RodadaPregao();
        Object itemPregao = Web.pegaDaSessao("ITEM_PREGAO");
        Object fornecedores = Web.pegaDaSessao("FORNECEDORES");
        if (itemPregao != null && fornecedores != null) {
            criarRodadaAndLancePregaoRecuperadosDaSessao((ItemPregao) itemPregao, (List<LicitacaoFornecedor>) fornecedores);
        }
        try {
            selecionado = facade.recuperarPregaoPorLote(getId());
            facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            validarEdicaoPregao();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            redirecionarParaVer();
        }
    }

    private void criarRodadaAndLancePregaoRecuperadosDaSessao(ItemPregao itemPregao, List<LicitacaoFornecedor> fornecedores) {
        fornecedoresParticipantes = Lists.newArrayList();
        fornecedoresParticipantes = fornecedores;
        itemPregaoSelecionado = facade.recuperarItemPregao(itemPregao.getId());
        itemPregaoSelecionado = itemPregao;
        selecionado = itemPregaoSelecionado.getPregao();

        novaRodada();
        for (LicitacaoFornecedor licitacaoFornecedor : fornecedoresParticipantes) {
            novoLancePregao(licitacaoFornecedor);
        }
        adicionarRodadaPregaoSelecionadoNaListaItemPregaoSelecionado();
        atribuirValorInicialDoItemPregao(rodadaPregaoSelecionada);
        indiceAba = 1;
        FacesUtil.atualizarComponente("Formulario:tabGeral");
    }

    private void novoLancePregao(LicitacaoFornecedor licitacaoFornecedor) {
        LancePregao lancePregao = new LancePregao();
        lancePregao.setPropostaFornecedor(getPropostaFornecedor(licitacaoFornecedor));
        lancePregao.setRodadaPregao(rodadaPregaoSelecionada);
        lancePregao.setStatusLancePregao(StatusLancePregao.ATIVO);
        rodadaPregaoSelecionada.setListaDeLancePregao(Util.adicionarObjetoEmLista(rodadaPregaoSelecionada.getListaDeLancePregao(), lancePregao));
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarSePregaoPossuiItensComRodadas();
            verificarValoresItensLote();
            facade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            selecionado = facade.salvarPregaoPorLote(selecionado);
            redirecionarParaVer();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("salvar{}", e);
            descobrirETratarException(e);
        }
    }

    private void verificarValoresItensLote() {
        ValidacaoException ve = new ValidacaoException();
        for (ItemPregao itemPregao : selecionado.getListaDeItemPregao()) {
            validarRegrasItensLote(itemPregao);
        }
        ve.lancarException();
    }

    private void validarSePregaoPossuiItensComRodadas() {
        ValidacaoException ve = new ValidacaoException();
        List<ItemPregao> itensSemRodadas = Lists.newArrayList();
        List<ItemPregao> itensPregao = isOperacaoNovo() ? selecionado.getListaDeItemPregao() : facade.buscarItensPregao(selecionado);
        for (ItemPregao itemPregao : itensPregao) {
            List<RodadaPregao> rodadas = isOperacaoNovo() ? itemPregao.getListaDeRodadaPregao() : facade.buscarRodadasPregao(itemPregao);
            if (rodadas.isEmpty()) {
                itensSemRodadas.add(itemPregao);
            }
        }
        if (itensSemRodadas.size() == itensPregao.size()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Os lotes desse pregão não possuem rodadas de lance. Realizar rodada para ao menos um lote.");
        }
        ve.lancarException();
    }

    public void buscarItensLoteProcessoCompra(ItemPregao itemPregao) {
        itemPregaoSelecionado = itemPregao;
        buscarItensLoteProcessoCompra();
    }

    public void buscarItensLoteProcessoCompra() {
        itensLoteProcessoCompra = Lists.newArrayList();
        List<ItemProcessoDeCompra> itens = facade.getProcessoDeCompraFacade().buscarItensProcessoCompraPorLote(itemPregaoSelecionado.getItemPregaoLoteProcesso().getLoteProcessoDeCompra());
        if (itens != null && !itens.isEmpty()) {
            itensLoteProcessoCompra = itens;
        }
        FacesUtil.atualizarComponente("formItensLote");
        FacesUtil.executaJavaScript("dlgItensLote.show()");
    }

    public List<Licitacao> completarLicitacao(String parte) {
        return facade.getLicitacaoFacade().buscarLicitacaoPorTipoApuracaoAndTipoSolicitacao(TipoApuracaoLicitacao.LOTE, parte.trim());
    }

    public void carregarItensDoPregao() {
        selecionado.setListaDeItemPregao(new ArrayList<ItemPregao>());
        selecionado.setLicitacao(facade.getLicitacaoFacade().recuperarLote(selecionado.getLicitacao().getId()));
        for (LoteProcessoDeCompra loteProcesso : selecionado.getLicitacao().getProcessoDeCompra().getLotesProcessoDeCompra()) {
            ItemPregao itemPregao = new ItemPregao();
            itemPregao.setPregao(selecionado);

            itemPregao.setItemPregaoLoteProcesso(new ItemPregaoLoteProcesso());
            itemPregao.getItemPregaoLoteProcesso().setItemPregao(itemPregao);
            itemPregao.getItemPregaoLoteProcesso().setLoteProcessoDeCompra(loteProcesso);

            List<ItemProcessoDeCompra> itens = facade.getProcessoDeCompraFacade().buscarItensProcessoCompraPorLote(loteProcesso);
            for (ItemProcessoDeCompra itemProcesso : itens) {
                itemPregao.setItemPregaoItemProcesso(new ItemPregaoItemProcesso());
                itemPregao.getItemPregaoItemProcesso().setItemPregao(itemPregao);
                itemPregao.getItemPregaoItemProcesso().setItemProcessoDeCompra(itemProcesso);
            }
            selecionado.setListaDeItemPregao(Util.adicionarObjetoEmLista(selecionado.getListaDeItemPregao(), itemPregao));
        }
        Util.ordenarListas(selecionado.getListaDeItemPregao());
    }

    private List<LicitacaoFornecedor> getFornecedoresDaLicitacaoSelecionada() {
        if (selecionado.getLicitacao() != null) {
            return facade.getLicitacaoFacade().buscarFornecedoresLicitacao(selecionado.getLicitacao());
        }
        return new ArrayList<>();
    }

    private List<PropostaFornecedor> getPropostasDaLicitacaoSelecionada() {
        return facade.getPropostaFornecedorFacade().buscarPropostaFornecedorPorLicitacao(selecionado.getLicitacao());
    }

    public void selecionarItemLancesVencedores(ItemPregao item) {
        this.itemPregaoSelecionado = item;
        Collections.sort(item.getLancesVencedores());
    }

    public void selecionarItemPregao(ItemPregao item) {
        itemPregaoSelecionado = item;
        if (isOperacaoEditar()) {
            itemPregaoSelecionado = facade.buscarRodadasAndLancesPregao(itemPregaoSelecionado);
        }
        if (itemPregaoSelecionado.getListaDeRodadaPregao() == null || itemPregaoSelecionado.getListaDeRodadaPregao().isEmpty()) {
            popularFornecedoresQuePodemParticiparDaRodada();
            FacesUtil.atualizarComponente("form-fornecedor");
            FacesUtil.executaJavaScript("dlgFornecedores.show()");
            itemPregaoSelecionado.setTipoStatusItemPregao(TipoStatusItemPregao.DESERTO);
        } else {
            popularFornecedoresParticipantesRodada();
            rodadaPregaoSelecionada = retornarUltimaRodadaDoPregao();
            for (RodadaPregao rodadaPregao : itemPregaoSelecionado.getListaDeRodadaPregao()) {
                atribuirValorInicialDoItemPregao(rodadaPregao);
                selecionado.atribuirValorLanceRodadaAnterior(itemPregaoSelecionado, rodadaPregao);
            }
            indiceAba = 1;
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    private void popularFornecedoresParticipantesRodada() {
        fornecedoresParticipantes = Lists.newArrayList();
        List<LicitacaoFornecedor> fornecedoresLicitacao = getFornecedoresDaLicitacaoSelecionada();
        for (LancePregao lance : itemPregaoSelecionado.getListaDeRodadaPregao().get(0).getListaDeLancePregao()) {
            for (LicitacaoFornecedor fornecedor : fornecedoresLicitacao) {
                if (lance.getPropostaFornecedor().getFornecedor().equals(fornecedor.getEmpresa())) {
                    fornecedor.setSelecionado(Boolean.TRUE);
                    fornecedoresParticipantes.add(fornecedor);
                }
            }
        }
        ordenarFornecedoresPorValorLotePropostaDesc(fornecedoresParticipantes);
    }

    private void popularFornecedoresQuePodemParticiparDaRodada() {
        fornecedoresParticipantes = Lists.newArrayList();
        List<LicitacaoFornecedor> fornecedores = getFornecedoresDaLicitacaoSelecionada();
        for (LicitacaoFornecedor licitacaoFornecedor : fornecedores) {
            BigDecimal valorLote = facade.getPropostaFornecedorFacade().buscarValorPropostaFornecedorPorLoteProcessoCompra(licitacaoFornecedor.getEmpresa(), selecionado.getLicitacao(), itemPregaoSelecionado.getItemPregaoLoteProcesso().getLoteProcessoDeCompra());
            if (valorLote != null && valorLote.compareTo(BigDecimal.ZERO) > 0) {
                licitacaoFornecedor.setValorDoLoteLancadoNaProposta(valorLote);
                licitacaoFornecedor.setSelecionado(Boolean.TRUE);
                fornecedoresParticipantes.add(licitacaoFornecedor);
            }
        }
        ordenarFornecedoresPorValorLotePropostaDesc(fornecedoresParticipantes);
    }

    private List<LicitacaoFornecedor> getFornecedoresSelecionadosParaRodada() {
        List<LicitacaoFornecedor> selecionados = Lists.newArrayList();
        for (LicitacaoFornecedor licitacaoFornecedor : fornecedoresParticipantes) {
            if (licitacaoFornecedor.getSelecionado() != null && licitacaoFornecedor.getSelecionado()) {
                selecionados.add(licitacaoFornecedor);
            }
        }
        return selecionados;
    }

    public PropostaFornecedor getPropostaFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        for (PropostaFornecedor propostaFornecedor : getPropostasDaLicitacaoSelecionada()) {
            if (propostaFornecedor.getFornecedor().equals(licitacaoFornecedor.getEmpresa())) {
                return propostaFornecedor;
            }
        }
        return null;
    }

    private void ordenarFornecedoresPorValorLotePropostaDesc(List<LicitacaoFornecedor> fornecedoresDaLicitacaoSelecionada) {
        Collections.sort(fornecedoresDaLicitacaoSelecionada, new Comparator<LicitacaoFornecedor>() {
            @Override
            public int compare(LicitacaoFornecedor o1, LicitacaoFornecedor o2) {
                return o2.getValorDoLoteLancadoNaProposta().compareTo(o1.getValorDoLoteLancadoNaProposta());
            }
        });
    }

    public void cancelarItemPregaoSelecionado() {
        itemPregaoSelecionado = null;
    }

    public void cancelarRodadaPregaoSeleciondo() {
        indiceAba = 0;
    }

    public BigDecimal getMenorValorLotePropostasIniciais(RodadaPregao rodadaPregao) {
        BigDecimal menorValor = BigDecimal.ZERO;
        for (LotePropostaFornecedor lotePropostaFornecedor : getPropostasIniciaisLote(rodadaPregao)) {
            LancePregao lancePregaoDaPropostaFornecedor = getLancePregaoReferenteLotePropostaFornecedor(rodadaPregao, lotePropostaFornecedor);
            if (lancePregaoDaPropostaFornecedor != null && lancePregaoDaPropostaFornecedor.getStatusLancePregao().isAtivoOrVencedor()) {
                if (menorValor.compareTo(BigDecimal.ZERO) == 0 || lotePropostaFornecedor.getValor().compareTo(menorValor) < 0) {
                    menorValor = lotePropostaFornecedor.getValor();
                }
            }
        }
        return menorValor;
    }

    private BigDecimal getMaiorDescontoLotePropostasIniciais(RodadaPregao rodadaPregao) {
        BigDecimal maiorDesconto = BigDecimal.ZERO;
        for (LotePropostaFornecedor lotePropostaFornecedor : getPropostasIniciaisLote(rodadaPregao)) {
            LancePregao lancePregaoDaPropostaFornecedor = getLancePregaoReferenteLotePropostaFornecedor(rodadaPregao, lotePropostaFornecedor);
            if (lancePregaoDaPropostaFornecedor != null && lancePregaoDaPropostaFornecedor.getStatusLancePregao().isAtivoOrVencedor()) {
                if (lotePropostaFornecedor.getPercentualDesconto().compareTo(maiorDesconto) > 0) {
                    maiorDesconto = lotePropostaFornecedor.getPercentualDesconto();
                }

            }
        }
        return maiorDesconto;
    }

    private List<LotePropostaFornecedor> getPropostasIniciaisLote(RodadaPregao rodadaPregao) {
        List<LotePropostaFornecedor> toReturn = Lists.newArrayList();

        for (PropostaFornecedor propostaFornecedor : getPropostasDaLicitacaoSelecionada()) {
            for (LotePropostaFornecedor lotePropostaFornecedor : propostaFornecedor.getLotes()) {
                if (lotePropostaFornecedor.getLoteProcessoDeCompra().equals(rodadaPregao.getItemPregao().getItemPregaoLoteProcesso().getLoteProcessoDeCompra())) {
                    toReturn.add(lotePropostaFornecedor);
                }
            }
        }
        return toReturn;
    }

    private LancePregao getLancePregaoDoFornecedorNaRodada(RodadaPregao rodadaPregao, Pessoa fornecedor) {
        for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
            if (lancePregao.getPropostaFornecedor().getFornecedor().equals(fornecedor)) {
                return lancePregao;
            }
        }
        return null;
    }

    private LancePregao getLancePregaoReferenteLotePropostaFornecedor(RodadaPregao rodadaPregao, LotePropostaFornecedor lotePropostaFornecedor) {
        for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
            if (lancePregao.getPropostaFornecedor().equals(lotePropostaFornecedor.getPropostaFornecedor()) &&
                lancePregao.getRodadaPregao().getItemPregao().getItemPregaoLoteProcesso().getLoteProcessoDeCompra().equals(lotePropostaFornecedor.getLoteProcessoDeCompra())) {
                return lancePregao;
            }
        }
        return null;
    }

    public boolean todosFornecedoresMarcados() {
        try {
            for (LicitacaoFornecedor fornecedor : fornecedoresParticipantes) {
                if (fornecedor.getSelecionado() == null || !fornecedor.getSelecionado()) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void marcarTodosFornecedores() {
        for (LicitacaoFornecedor fornecedor : fornecedoresParticipantes) {
            marcarFornecedor(fornecedor);
        }
    }

    public void desmarcarTodosFornecedores() {
        for (LicitacaoFornecedor fornecedor : fornecedoresParticipantes) {
            desmarcarFornecedor(fornecedor);
        }
    }

    public void desmarcarFornecedor(LicitacaoFornecedor fornecedor) {
        itemPregaoSelecionado.setStatusFornecedorVencedor(TipoClassificacaoFornecedor.INABILITADO);
        fornecedor.setSelecionado(Boolean.FALSE);
    }

    public void marcarFornecedor(LicitacaoFornecedor fornecedor) {
        itemPregaoSelecionado.setStatusFornecedorVencedor(fornecedor.getTipoClassificacaoFornecedor());
        fornecedor.setSelecionado(Boolean.TRUE);
    }

    public void iniciarRodadas() {
        if (isOperacaoNovo() && selecionado.getId() == null) {
            iniciarPregaoSalvandoOperacaoNovo();
        } else {
            novaRodada();
            for (LicitacaoFornecedor licitacaoFornecedor : getFornecedoresSelecionadosParaRodada()) {
                novoLancePregao(licitacaoFornecedor);
            }
            adicionarRodadaPregaoSelecionadoNaListaItemPregaoSelecionado();
            atribuirValorInicialDoItemPregao(rodadaPregaoSelecionada);
            indiceAba = 1;
            FacesUtil.atualizarComponente("Formulario:tabGeral");
            String campoValor = selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() ? "descontoAtual" : "valorLanceAtual";
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:tabelaFornecedorRodada:0:" + campoValor + "')");
            FacesUtil.executaJavaScript("dlgFornecedores.hide()");
        }
    }

    private void iniciarPregaoSalvandoOperacaoNovo() {
        List<ItemPregao> itensPregao = Lists.newArrayList(selecionado.getListaDeItemPregao());
        Pregao pregaoSalvo = facade.salvarPregao(selecionado);
        pregaoSalvo.setListaDeItemPregao(itensPregao);
        itemPregaoSelecionado = facade.salvarItemPregaoPorLote(pregaoSalvo, itemPregaoSelecionado);
        selecionado = pregaoSalvo;

        List<LicitacaoFornecedor> fornecedoresSelecionados = getFornecedoresSelecionadosParaRodada();
        Web.poeNaSessao("ITEM_PREGAO", itemPregaoSelecionado);
        Web.poeNaSessao("FORNECEDORES", fornecedoresSelecionados);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
    }

    private void atribuirValorInicialDoItemPregao(RodadaPregao rodadaPregao) {
        for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
            BigDecimal valorLote = facade.getPropostaFornecedorFacade().buscarValorPropostaFornecedorPorLoteProcessoCompra(
                lancePregao.getPropostaFornecedor().getFornecedor(),
                selecionado.getLicitacao(),
                itemPregaoSelecionado.getItemPregaoLoteProcesso().getLoteProcessoDeCompra());
            lancePregao.setValorPropostaInicial(valorLote);
        }
        ordenarLancesPorPrecoInicialDesc(rodadaPregao.getListaDeLancePregao());
    }

    private void ordenarLancesPorPrecoInicialDesc(List<LancePregao> lances) {
        Collections.sort(lances, new Comparator<LancePregao>() {
            @Override
            public int compare(LancePregao o1, LancePregao o2) {
                return o2.getValorPropostaInicial().compareTo(o1.getValorPropostaInicial());
            }
        });
    }

    private void ordenarLancesPorPrecoNaRodadaAnteriorDesc(List<LancePregao> lances) {
        Collections.sort(lances, new Comparator<LancePregao>() {
            @Override
            public int compare(LancePregao o1, LancePregao o2) {
                return o2.getValorNaRodadaAnterior().compareTo(o1.getValorNaRodadaAnterior());
            }
        });
    }

    private void adicionarRodadaPregaoSelecionadoNaListaItemPregaoSelecionado() {
        itemPregaoSelecionado.setListaDeRodadaPregao(Util.adicionarObjetoEmLista(itemPregaoSelecionado.getListaDeRodadaPregao(), rodadaPregaoSelecionada));
    }

    private void novaRodada() {
        rodadaPregaoSelecionada = new RodadaPregao();
        rodadaPregaoSelecionada.setItemPregao(itemPregaoSelecionado);
        rodadaPregaoSelecionada.setNumero(retornarProximoNumeroDaRodada());
    }

    public void novaRodadaValidando() {
        try {
            validarLancesRodadaSelecionada();
            String campoValor = selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() ? "descontoAtual" : "valorLanceAtual";
            RodadaPregao rodadaPregao = rodadaPregaoSelecionada;
            facade.salvarItemRodadaEspecifica(itemPregaoSelecionado, rodadaPregao);
            novaRodada();
            novoLanceClonandoAnterior(rodadaPregao);
            adicionarRodadaPregaoSelecionadoNaListaItemPregaoSelecionado();
            atribuirStatusLanceAtualRecuperandoStatusDaRodadaAnterior();
            removerLancesInativos();
            ordenarLancesPorPrecoNaRodadaAnteriorDesc(rodadaPregaoSelecionada.getListaDeLancePregao());
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:tabelaFornecedorRodada:0:" + campoValor + "')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.debug("novaRodadaValidando {}", e);
        }
    }

    public void visualizarRodadasAndLances(ItemPregao itemPregao) {
        try {
            itemPregaoSelecionado = facade.buscarRodadasAndLancesPregao(itemPregao);
            RodadaPregao rodadaPregao = retornarUltimaRodadaDoPregao();
            for (RodadaPregao rodada : itemPregao.getListaDeRodadaPregao()) {
                atribuirValorInicialDoItemPregao(rodada);
                selecionado.atribuirValorLanceRodadaAnterior(itemPregaoSelecionado, rodada);
            }
            if (rodadaPregao != null) {
                rodadaPregaoSelecionada = rodadaPregao;
                FacesUtil.executaJavaScript("dialogRodadas.show()");
            }
        } catch (ArrayIndexOutOfBoundsException a) {
            FacesUtil.addAtencao("O lote: " + itemPregaoSelecionado.getItemPregaoLoteProcesso().getLoteProcessoDeCompra() + " não possui rodadas para serem exibidas.");
        }
    }

    private void novoLanceClonandoAnterior(RodadaPregao rodadaPregao) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        for (LancePregao lancePregao : rodadaPregao.getListaDeLancePregao()) {
            if (!lancePregao.getStatusLancePregao().isInativo()) {
                LancePregao novoLance = (LancePregao) BeanUtils.cloneBean(lancePregao);
                novoLance.setId(null);
                novoLance.setRodadaPregao(rodadaPregaoSelecionada);
                novoLance.setValor(BigDecimal.ZERO);
                novoLance.setPercentualDesconto(BigDecimal.ZERO);
                novoLance.setValorNaRodadaAnterior(lancePregao.getValor());
                rodadaPregaoSelecionada.getListaDeLancePregao().add(novoLance);
            }
        }
    }

    private void removerLancesInativos() {
        for (LancePregao lanceParaRemocao : getLancesInativos()) {
            if (rodadaPregaoSelecionada.getListaDeLancePregao().contains(lanceParaRemocao)) {
                rodadaPregaoSelecionada.getListaDeLancePregao().remove(lanceParaRemocao);
            }
        }
    }

    private List<LancePregao> getLancesInativos() {
        List<LancePregao> lancesInativos = new ArrayList<>();
        for (LancePregao lancePregaoRodadaAtual : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (isLanceAtualEstaInativoEmAlgumaRodadaAnterior(lancePregaoRodadaAtual)) {
                lancesInativos.add(lancePregaoRodadaAtual);
            }
        }
        return lancesInativos;
    }

    private boolean isLanceAtualEstaInativoEmAlgumaRodadaAnterior(LancePregao lancePregaoRodadaAtual) {
        for (RodadaPregao rodadaPregao : itemPregaoSelecionado.getListaDeRodadaPregao()) {
            for (LancePregao lancePregaoRodadaPassada : rodadaPregao.getListaDeLancePregao()) {
                if (lancePregaoRodadaPassada.getPropostaFornecedor().equals(lancePregaoRodadaAtual.getPropostaFornecedor())) {
                    if (lancePregaoRodadaPassada.getStatusLancePregao().isInativo()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void atribuirStatusLanceAtualRecuperandoStatusDaRodadaAnterior() {
        RodadaPregao rodadaAnteriorAEsta = itemPregaoSelecionado.getRodadaAnteriorAEsta(rodadaPregaoSelecionada);
        if (rodadaAnteriorAEsta != null) {
            for (LancePregao lanceRodadaAnterior : rodadaAnteriorAEsta.getListaDeLancePregao()) {
                for (LancePregao lanceAtual : rodadaPregaoSelecionada.getListaDeLancePregao()) {
                    if (lanceAtual.getPropostaFornecedor().equals(lanceRodadaAnterior.getPropostaFornecedor())) {
                        lanceAtual.setStatusLancePregao(lanceRodadaAnterior.getStatusLancePregao());
                    }
                }
            }
        }
    }

    public boolean isPermitidoFinalizarRodadas() {
        if (itemPregaoSelecionado != null && itemPregaoSelecionado.getListaDeRodadaPregao() != null) {
            for (RodadaPregao rodadaPregao : itemPregaoSelecionado.getListaDeRodadaPregao()) {
                if (rodadaPregao.getLanceVencedor() != null || rodadaPregao.getLancesAtivo().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void excluirRodada() {
        try {
            itemPregaoSelecionado.validarExclusaoRodada();
            RodadaPregao rodadaParaExcluir = rodadaPregaoSelecionada;
            itemPregaoSelecionado.getListaDeRodadaPregao().remove(rodadaPregaoSelecionada);
            if (itemPregaoSelecionado.getListaDeRodadaPregao().size() > 1) {
                rodadaPregaoSelecionada = retornarUltimaRodadaDoPregao();
            } else {
                rodadaPregaoSelecionada = retornarPrimeiraRodadaDoPregao();
            }
            if (rodadaParaExcluir.getLanceVencedor() != null) {
                itemPregaoSelecionado.getItemPregaoLanceVencedor().setLancePregao(null);
                itemPregaoSelecionado.setTipoStatusItemPregao(itemPregaoSelecionado.definirStatus());
                itemPregaoSelecionado.setStatusFornecedorVencedor(null);
            }
            facade.excluirRodadaAndLances(rodadaParaExcluir, itemPregaoSelecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void finalizarRodadas() {
        try {
            for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
                validarLanceZerado(lancePregao);
                validarValorLanceAtual(lancePregao);
            }
            LancePregao lanceVencedor = recuperarLanceVencedorDoItemPregao(rodadaPregaoSelecionada);
            definirLanceVencedorParaItemPregao(lanceVencedor);
            itemPregaoSelecionado.setTipoStatusItemPregao(itemPregaoSelecionado.definirStatus());
            validarLanceMaiorQueValorReservadoNaDotacao(itemPregaoSelecionado.getItemPregaoLanceVencedor());
            ItemPregao itemPregaoSalvo = facade.salvarItemRodadaEspecifica(itemPregaoSelecionado, rodadaPregaoSelecionada);
            indiceAba = 0;
            selecionado = facade.recuperarPregaoPorLote(itemPregaoSalvo.getPregao().getId());
        } catch (ValidacaoException ve) {
            itemPregaoSelecionado.getItemPregaoLanceVencedor().setLancePregao(null);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private LancePregao recuperarLanceVencedorDoItemPregao(RodadaPregao rodadaPregaoSelecionada) {
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (StatusLancePregao.VENCEDOR.equals(lancePregao.getStatusLancePregao())) {
                return lancePregao;
            }
        }
        return null;
    }

    private void definirLanceVencedorParaItemPregao(LancePregao lanceVencedor) {
        if (lanceVencedor != null) {
            if (itemPregaoSelecionado.getItemPregaoLanceVencedor() == null) {
                itemPregaoSelecionado.setItemPregaoLanceVencedor(new ItemPregaoLanceVencedor());
                itemPregaoSelecionado.getItemPregaoLanceVencedor().setStatus(lanceVencedor.getStatusLancePregao());
                itemPregaoSelecionado.getItemPregaoLanceVencedor().setOrigem(OrigemItemPregaoLanceVencedor.PREGAO);
                itemPregaoSelecionado.getItemPregaoLanceVencedor().setItemPregao(itemPregaoSelecionado);
            }
            itemPregaoSelecionado.getItemPregaoLanceVencedor().setValor(lanceVencedor.getValor());
            itemPregaoSelecionado.getItemPregaoLanceVencedor().setPercentualDesconto(lanceVencedor.getPercentualDesconto());
            itemPregaoSelecionado.getItemPregaoLanceVencedor().setLancePregao(lanceVencedor);
            itemPregaoSelecionado.setStatusFornecedorVencedor(TipoClassificacaoFornecedor.HABILITADO);
        }
    }

    public void reiniciarRodadas() {
        for (RodadaPregao rodadaPregao : itemPregaoSelecionado.getListaDeRodadaPregao()) {
            itemPregaoSelecionado.setTipoStatusItemPregao(TipoStatusItemPregao.EM_ANDAMENTO);
            itemPregaoSelecionado.setStatusFornecedorVencedor(null);
            facade.excluirRodadaAndLances(rodadaPregao, itemPregaoSelecionado);
        }
        limparListaDeRodadasDoItemPregaoSelecionado();
        novaRodada();
        for (LicitacaoFornecedor licitacaoFornecedor : fornecedoresParticipantes) {
            if (licitacaoFornecedor.getSelecionado()) {
                novoLancePregao(licitacaoFornecedor);
            }
        }
        adicionarRodadaPregaoSelecionadoNaListaItemPregaoSelecionado();
        atribuirValorInicialDoItemPregao(rodadaPregaoSelecionada);
    }

    private void limparListaDeRodadasDoItemPregaoSelecionado() {
        itemPregaoSelecionado.getListaDeRodadaPregao().clear();
    }

    public Integer numeroDaRodadaAtual() {
        try {
            return itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) + 1;
        } catch (Exception ex) {
            return 1;
        }
    }

    public Integer totalDeRodadasRealizadas() {
        try {
            if (itemPregaoSelecionado.getListaDeRodadaPregao().isEmpty()) {
                return 1;
            } else {
                return itemPregaoSelecionado.getListaDeRodadaPregao().size();
            }
        } catch (Exception ex) {
            return 1;
        }
    }

    private Integer retornarProximoNumeroDaRodada() {
        if (itemPregaoSelecionado.getListaDeRodadaPregao() != null && !itemPregaoSelecionado.getListaDeRodadaPregao().isEmpty()) {
            return itemPregaoSelecionado.getListaDeRodadaPregao().size() + 1;
        }
        return 1;
    }

    private BigDecimal getMenorValorLoteRodadaAnterior(RodadaPregao rodadaPregao) {
        BigDecimal menorValor = BigDecimal.ZERO;
        int indiceRodadaAnterior = getIndiceDaRodadaAnterior();
        for (LancePregao lanceAnterior : itemPregaoSelecionado.getListaDeRodadaPregao().get(indiceRodadaAnterior).getListaDeLancePregao()) {
            LancePregao lanceAtual = getLancePregaoDoFornecedorNaRodada(rodadaPregao, lanceAnterior.getPropostaFornecedor().getFornecedor());
            if (lanceAnterior.getStatusLancePregao().isAtivoOrVencedor() &&
                lanceAtual.getStatusLancePregao().isAtivoOrVencedor()) {
                if (menorValor.compareTo(BigDecimal.ZERO) == 0 || lanceAnterior.getValor().compareTo(menorValor) < 0) {
                    menorValor = lanceAnterior.getValor();
                }
            }
        }
        return menorValor;
    }

    private BigDecimal getMaiorDescontoLoteRodadaAnterior(RodadaPregao rodadaPregao) {
        BigDecimal maiorDesconto = BigDecimal.ZERO;
        int indiceRodadaAnterior = getIndiceDaRodadaAnterior();
        for (LancePregao lanceAnterior : itemPregaoSelecionado.getListaDeRodadaPregao().get(indiceRodadaAnterior).getListaDeLancePregao()) {
            LancePregao lanceAtual = getLancePregaoDoFornecedorNaRodada(rodadaPregao, lanceAnterior.getPropostaFornecedor().getFornecedor());
            if (lanceAnterior.getStatusLancePregao().isAtivoOrVencedor() &&
                lanceAtual.getStatusLancePregao().isAtivoOrVencedor()) {
                if (lanceAnterior.getPercentualDesconto().compareTo(maiorDesconto) > 0) {
                    maiorDesconto = lanceAnterior.getPercentualDesconto();
                }
            }
        }
        return maiorDesconto;
    }

    public List<SelectItem> getStatusLancePregao() {
        List<SelectItem> retorno = new ArrayList<>();
        for (StatusLancePregao statusLancePregao : StatusLancePregao.values()) {
            if (rodadaPregaoSelecionada.getListaDeLancePregao().size() == 1) {
                retorno.add(new SelectItem(statusLancePregao, statusLancePregao.getDescricao()));

            } else if (!statusLancePregao.isVencedor()) {
                retorno.add(new SelectItem(statusLancePregao, statusLancePregao.getDescricao()));
            }

        }
        return retorno;
    }

    public List<SelectItem> getStatusLancePregaoExcetoAtivo() {
        List<SelectItem> retorno = new ArrayList<>();
        for (StatusLancePregao statusLancePregao : StatusLancePregao.values()) {
            if (!statusLancePregao.isAtivo()) {
                retorno.add(new SelectItem(statusLancePregao, statusLancePregao.getDescricao()));
            }
        }
        return retorno;
    }

    public void navegarPrimeiraRodada() {
        rodadaPregaoSelecionada = retornarPrimeiraRodadaDoPregao();
    }

    private RodadaPregao retornarPrimeiraRodadaDoPregao() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().get(0);
    }

    public void navegarRodadaAnterior() {
        try {
            if (itemPregaoSelecionado.getListaDeRodadaPregao().size() == 1) {
                rodadaPregaoSelecionada = retornarPrimeiraRodadaDoPregao();
            } else {
                rodadaPregaoSelecionada = getRodadaAnteriorDoPregao();
            }
        } catch (Exception ex) {
            rodadaPregaoSelecionada = retornarPrimeiraRodadaDoPregao();
        }
    }

    public void navegarProximaRodada() {
        try {
            if (itemPregaoSelecionado.getListaDeRodadaPregao().size() == 1) {
                rodadaPregaoSelecionada = retornarUltimaRodadaDoPregao();
            } else {
                rodadaPregaoSelecionada = retornarRodadaSeguinteDoPregao();
            }
        } catch (Exception ex) {
            rodadaPregaoSelecionada = retornarUltimaRodadaDoPregao();
        }
    }

    private RodadaPregao getRodadaAnteriorDoPregao() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().get(getIndiceDaRodadaAnterior());
    }

    private RodadaPregao retornarRodadaSeguinteDoPregao() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().get(retornarIndiceDaRodadaSeguinte());
    }

    public void navegarUltimaRodada() {
        rodadaPregaoSelecionada = retornarUltimaRodadaDoPregao();
    }

    private RodadaPregao retornarUltimaRodadaDoPregao() {
        Collections.sort(itemPregaoSelecionado.getListaDeRodadaPregao());
        return itemPregaoSelecionado.getListaDeRodadaPregao().get(itemPregaoSelecionado.getListaDeRodadaPregao().size() - 1);
    }

    public boolean isPermitidoNovaRodada() {
        return rodadaEditavel() || (rodadaPregaoSelecionada != null && rodadaPregaoSelecionada.getLancesAtivo().isEmpty());
    }

    private void validarLancesRodadaSelecionada() {
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            validarLancePregao(lancePregao);
        }
    }

    private void validarLancePregao(LancePregao lance) {
        validarLanceZerado(lance);
        validarValorLanceAtual(lance);
    }

    public void executarLance(LancePregao lance, int linha) {
        int indice = linha + 1;
        String campoValor = selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() ? "descontoAtual" : "valorLanceAtual";
        try {
            validarValorLanceAtual(lance);
            FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:tabelaFornecedorRodada:" + indice + ":" + campoValor + "')");
        } catch (ValidacaoException ve) {
            lance.setValor(BigDecimal.ZERO);
            lance.setPercentualDesconto(BigDecimal.ZERO);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        if (isTodosLancesAtivosEComPreco() && !isExisteVencedorRodada(rodadaPregaoSelecionada)) {
            novaRodadaValidando();
        }
    }

    private boolean isTodosLancesAtivosEComPreco() {
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (StatusLancePregao.ATIVO.equals(lancePregao.getStatusLancePregao())
                &&
                ((!selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() && lancePregao.getValor() != null && lancePregao.getValor().compareTo(BigDecimal.ZERO) == 0)
                    ||
                    (selecionado.getLicitacao().getTipoAvaliacao().isMaiorDesconto() && lancePregao.getPercentualDesconto() != null && lancePregao.getPercentualDesconto().compareTo(BigDecimal.ZERO) == 0))) {
                return false;
            }
        }
        return true;
    }

    private boolean isExisteVencedorRodada(RodadaPregao rodadaPregaoSelecionada) {
        for (LancePregao lancePregao : rodadaPregaoSelecionada.getListaDeLancePregao()) {
            if (lancePregao.getStatusLancePregao().equals(StatusLancePregao.VENCEDOR)) {
                return true;
            }
        }
        return false;
    }

    private void validarLanceZerado(LancePregao lance) {
        ValidacaoException ve = new ValidacaoException();
        if (lance.getStatusLancePregao().isAtivoOrVencedor()) {
            if (selecionado.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto()) {
                if (lance.getPercentualDesconto() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O desconto do lance do fornecedor " + lance.getPropostaFornecedor().getFornecedor().getNome()
                        + " deve ser informado. ");
                }
                ve.lancarException();
                if (lance.getPercentualDesconto().compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O desconto do lance do fornecedor " + lance.getPropostaFornecedor().getFornecedor().getNome()
                        + " não pode ser menor ou igual a zero(0). ");
                }
            } else {
                if (lance.getValor() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do lance do fornecedor " + lance.getPropostaFornecedor().getFornecedor().getNome()
                        + " deve ser informado. ");
                }
                ve.lancarException();
                if (lance.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do lance do fornecedor " + lance.getPropostaFornecedor().getFornecedor().getNome()
                        + " não pode ser menor ou igual a zero(0). ");
                }
            }
        }
        ve.lancarException();
    }

    private void validarLanceMaiorQueValorReservadoNaDotacao(ItemPregaoLanceVencedor itemPregaoLanceVencedor) {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto()) {
            BigDecimal valorReservado = rodadaPregaoSelecionada.getItemPregao().getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getValor();
            if (itemPregaoLanceVencedor != null && itemPregaoLanceVencedor.getValor().compareTo(valorReservado) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do lance do fornecedor " + itemPregaoLanceVencedor.getLancePregao().getPropostaFornecedor().getFornecedor().getNome()
                    + " é maior que o valor reservado. Reserva de Dotação: " + Util.formataValor(valorReservado));
            }
        }
        ve.lancarException();
    }

    private void validarValorLanceAtual(LancePregao lance) {
        ValidacaoException ve = new ValidacaoException();
        if (lance.getStatusLancePregao().isAtivoOrVencedor()) {
            if (selecionado.getLicitacao().getProcessoDeCompra().getSolicitacaoMaterial().getTipoAvaliacao().isMaiorDesconto()) {
                BigDecimal maiorDesconto = getMaiorDescontoPropostoRodadaAtual(rodadaPregaoSelecionada);
                if (lance.getPercentualDesconto() != null && lance.getPercentualDesconto().compareTo(maiorDesconto) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O desconto do lance atual do fornecedor " + lance.getPropostaFornecedor().getFornecedor() + " deve ser maior ou igual ao maior desconto proposto para este lote. " +
                        " Desconto Proposto: " + Util.formataPercentual(maiorDesconto) + ".");
                }
            } else {
                BigDecimal menorValor = getMenorValorPropostoRodadaAtual(rodadaPregaoSelecionada);
                if (lance.getValor() != null && lance.getValor().compareTo(menorValor) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do lance atual do fornecedor " + lance.getPropostaFornecedor().getFornecedor() + " deve ser menor ou igual ao menor valor proposto para este lote. " +
                        " Valor Proposto: " + Util.formataValor(menorValor) + ".");
                }
            }
        }
        ve.lancarException();
    }

    public void processarAlteracaoStatusNaoVencedor(LancePregao lanceManipuladoNaTela) {
        if (lanceManipuladoNaTela.getStatusLancePregao().isAtivo()) {
            alterarLanceVencedorParaAtivo(rodadaPregaoSelecionada.getListaDeLancePregao());
        }
        definirStatusVencedor();
    }

    private void definirStatusVencedor() {
        if (rodadaPregaoSelecionada.getLancesAtivo().size() == 1) {
            LancePregao lancePregaoVencedor = rodadaPregaoSelecionada.getLancesAtivo().get(0);
            lancePregaoVencedor.setStatusLancePregao(StatusLancePregao.VENCEDOR);
            if (lancePregaoVencedor.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                RodadaPregao rodadaAnteriorAEsta = itemPregaoSelecionado.getRodadaAnteriorAEsta(rodadaPregaoSelecionada);
                if (rodadaAnteriorAEsta != null) {
                    lancePregaoVencedor.setValor(lancePregaoVencedor.getValorNaRodadaAnterior());
                } else {
                    lancePregaoVencedor.setValor(lancePregaoVencedor.getValorPropostaInicial());
                }
            }
        }
    }

    private void alterarLanceVencedorParaAtivo(List<LancePregao> lances) {
        for (LancePregao lancePregao : lances) {
            if (lancePregao.getStatusLancePregao().isVencedor()) {
                lancePregao.setStatusLancePregao(StatusLancePregao.ATIVO);
            }
        }
    }

    private BigDecimal getMenorValorPropostoRodadaAtual(RodadaPregao rodadaPregao) {
        return getIndiceDaRodadaSelecionada() == 0 ? getMenorValorLotePropostasIniciais(rodadaPregao) : getMenorValorLoteRodadaAnterior(rodadaPregao);
    }

    private BigDecimal getMaiorDescontoPropostoRodadaAtual(RodadaPregao rodadaPregao) {
        return getIndiceDaRodadaSelecionada() == 0 ? getMaiorDescontoLotePropostasIniciais(rodadaPregao) : getMaiorDescontoLoteRodadaAnterior(rodadaPregao);
    }

    private int getIndiceDaRodadaSelecionada() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada);
    }

    private int getIndiceDaRodadaAnterior() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) - 1;
    }

    private int retornarIndiceDaRodadaSeguinte() {
        return itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) + 1;
    }

    public boolean desabilitarLanceAtual(LancePregao lancePregao) {
        return rodadaEditavel() || lancePregao.getStatusLancePregao().isInativo();
    }

    public boolean rodadaEditavel() {
        Boolean validou = true;
        if (itemPregaoSelecionado != null && rodadaPregaoSelecionada != null && itemPregaoSelecionado.getListaDeRodadaPregao() != null) {
            if ((itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) == itemPregaoSelecionado.getListaDeRodadaPregao().size() - 1)
                || ((itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) == itemPregaoSelecionado.getListaDeRodadaPregao().size() - 1)
                && (itemPregaoSelecionado.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionada) == 0))) {
                validou = false;
            }
        }
        return validou;
    }

    @Override
    public void excluir() {
        try {
            validarExclusaoPregao();
            facade.excluirPregao(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarExclusaoPregao() {
        ValidacaoException ve = new ValidacaoException();
        if (isLicitacaoHomologada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O pregão não pode ser removido, pois sua licitação encontra-se homoloagada.");
        }
        ve.lancarException();
    }

    private void validarEdicaoPregao() {
        ValidacaoException ve = new ValidacaoException();
        if (isLicitacaoHomologada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O pregão não pode ser alterado, pois sua licitação encontra-se homoloagada.");
        }
        ve.lancarException();
    }

    private boolean isLicitacaoHomologada() {
        selecionado.setLicitacao(facade.getLicitacaoFacade().recuperarSomenteStatus(selecionado.getLicitacao().getId()));
        return selecionado.getLicitacao().isHomologada();
    }

    public List<LicitacaoFornecedor> getFornecedoresParticipantes() {
        return fornecedoresParticipantes;
    }

    public void setFornecedoresParticipantes(List<LicitacaoFornecedor> fornecedoresParticipantes) {
        this.fornecedoresParticipantes = fornecedoresParticipantes;
    }

    public boolean desabilitaInicioDeRodada() {
        return getFornecedoresSelecionadosParaRodada().size() == 0;
    }

    public boolean verificarSeTemFornecedorVencedor(ItemPregao itemPregao) {
        try {
            return itemPregao.getItemPregaoLanceVencedor().getStatus().isVencedor();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void criarItensPregaoLote(ItemPregao itemPregao) {
        itemPregaoSelecionado = facade.buscarRodadasAndLancesPregao(itemPregao);
        percentualDiferencaLote = facade.criarItensPregaoLoteRetornandoPercentualDiferencaoLote(itemPregaoSelecionado, selecionado);
        FacesUtil.atualizarComponente(":FormularioValorPorItem");
    }

    public void visualizarItensPregaoLoteItemProcesso(ItemPregao itemPregao) {
        itemPregaoSelecionado = itemPregao;
        List<ItemPregaoLoteItemProcesso> itensPregaoLoteItemProcesso = facade.buscarItensPregaoLoteItemProcesso(itemPregaoSelecionado);
        itemPregaoSelecionado.getItemPregaoLoteProcesso().setItensPregaoLoteItemProcesso(itensPregaoLoteItemProcesso);
        FacesUtil.atualizarComponente("FormularioValorPorItem");
        FacesUtil.executaJavaScript("valorPorItem.show()");
    }

    public void confirmarInformacoesLote() {
        try {
            facade.validarRegrasItensLote(itemPregaoSelecionado);
            selecionado = facade.salvarPregaoPorLote(selecionado);
            FacesUtil.executaJavaScript("valorPorItem.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarRegrasItensLote(ItemPregao itemPregao) {
        ValidacaoException ve = new ValidacaoException();
        List<ItemPregaoLoteItemProcesso> itens = itemPregao.getItemPregaoLoteProcesso().getItensPregaoLoteItemProcesso();
        for (ItemPregaoLoteItemProcesso item : itens) {
            if (item.getValor().compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do item de nº " + item.getItemProcessoDeCompra().getNumero() + " deve ser maior ou igual a Zero.");
            }
            if (item.getValor().compareTo(item.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco()) > 0
                && (item.getJustificativa() == null || "".equals(item.getJustificativa().trim()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do item de nº " + item.getItemProcessoDeCompra().getNumero() + " é maior que o valor cotado, portanto é necessário acrescentar a justificativa.");
            }
        }
        if (itemPregao.getItemPregaoLanceVencedor() != null
            && itemPregao.getItemPregaoLoteProcesso().getPrecoTotalLote().compareTo(itemPregao.getItemPregaoLanceVencedor().getValor()) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O preço total dos itens do lote deve ser igual ao valor negociado com o fornecedor." +
                " Valor Negociado: <b> " + Util.formataValor(itemPregao.getItemPregaoLanceVencedor().getValor()) + "</b>" +
                " Valor Informado: <b> " + Util.formataValor(itemPregao.getItemPregaoLoteProcesso().getPrecoTotalLote()) + "</b>");
        }
        ve.lancarException();
    }

    public void aplicarPercentualParaItens() {
        facade.aplicarPercentualParaItens(itemPregaoSelecionado, percentualDiferencaLote);
    }

    public int getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(int indiceAba) {
        this.indiceAba = indiceAba;
    }

    public ItemPregao getItemPregaoSelecionado() {
        return itemPregaoSelecionado;
    }

    public void setItemPregaoSelecionado(ItemPregao itemPregaoSelecionado) {
        this.itemPregaoSelecionado = itemPregaoSelecionado;
    }

    public RodadaPregao getRodadaPregaoSelecionada() {
        return rodadaPregaoSelecionada;
    }

    public void setRodadaPregaoSelecionada(RodadaPregao rodadaPregaoSelecionada) {
        this.rodadaPregaoSelecionada = rodadaPregaoSelecionada;
    }

    public List<ItemProcessoDeCompra> getItensLoteProcessoCompra() {
        return itensLoteProcessoCompra;
    }

    public void setItensLoteProcessoCompra(List<ItemProcessoDeCompra> itensLoteProcessoCompra) {
        this.itensLoteProcessoCompra = itensLoteProcessoCompra;
    }

    public BigDecimal getPercentualDiferencaLote() {
        return percentualDiferencaLote;
    }

    public void setPercentualDiferencaLote(BigDecimal percentualDiferencaLote) {
        this.percentualDiferencaLote = percentualDiferencaLote;
    }

    public BigDecimal diferencaEmPorcentagem(BigDecimal valorReferencia, BigDecimal valorLance) {
        BigDecimal retorno = BigDecimal.ZERO;
        if (valorReferencia != null && valorLance != null && valorReferencia.compareTo(BigDecimal.ZERO) != 0 && valorLance.compareTo(BigDecimal.ZERO) != 0) {
            retorno = valorLance.multiply(new BigDecimal(100)).divide(valorReferencia, 8, RoundingMode.FLOOR);
            retorno = new BigDecimal(100).subtract(retorno).setScale(3, RoundingMode.HALF_EVEN);
        }
        return retorno;
    }

    public void cancelarAnulacaolotePregao() {
        itemPregaoSelecionado = null;
    }

    public void anularLotePregao() {
        try {
            validarAnulacaoLotePregao();
            salvarPregaoAndLotesOperacaoNovo();
            itemPregaoSelecionado.setTipoStatusItemPregao(itemPregaoSelecionado.getTipoStatusItemPregao());
            facade.anularLotesPregao(itemPregaoSelecionado);
            FacesUtil.atualizarComponente("Formulario:tabGeral:tabela-item");
            FacesUtil.executaJavaScript("dlgAnularLote.hide()");
            if (isOperacaoNovo()) {
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
            }
            FacesUtil.addOperacaoRealizada("Lote anulado com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarAnulacaoLotePregao() {
        ValidacaoException ve = new ValidacaoException();
        if (itemPregaoSelecionado.getTipoStatusItemPregao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Situação do Lote deve ser informado.");
        }
        ve.lancarException();
    }

    private void salvarPregaoAndLotesOperacaoNovo() {
        if (isOperacaoNovo() && selecionado.getId() == null) {
            List<ItemPregao> itensPregao = Lists.newArrayList(selecionado.getListaDeItemPregao());
            Pregao pregaoSalvo = facade.salvarPregao(selecionado);
            pregaoSalvo.setListaDeItemPregao(itensPregao);
            itemPregaoSelecionado = facade.salvarItemPregaoPorLote(pregaoSalvo, itemPregaoSelecionado);
            selecionado = pregaoSalvo;
        }
    }

    public List<SelectItem> buscarTiposStatusItemParaAnulacao() {
        return Util.getListSelectItem(Lists.newArrayList(
            TipoStatusItemPregao.DESERTO,
            TipoStatusItemPregao.PREJUDICADO,
            TipoStatusItemPregao.DECLINADO,
            TipoStatusItemPregao.INEXEQUIVEL,
            TipoStatusItemPregao.CANCELADO
        ));
    }

    public boolean hasFornecedorParaRodada(ItemPregao itemPregao) {
        if (itemPregao != null && itemPregao.getItemPregaoLoteProcesso() != null && itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra() != null) {
            return facade.hasLoteProcessoDeCompraFornecedor(itemPregao.getItemPregaoLoteProcesso().getLoteProcessoDeCompra());
        }
        return false;
    }

    public void selecionaItemParaAnulacao(ItemPregao itemPregao) {
        itemPregaoSelecionado = itemPregao;
        if (!isOperacaoNovo()) {
            itemPregaoSelecionado = facade.buscarRodadasAndLancesPregao(itemPregao);
        }
    }

    public void gerarRelatorio(Pregao pregao) {
        if (pregao != null) {
            selecionado = pregao;
            gerarRelatorio("PDF");
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", "Relatório Pregão Por Lote");
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("SECRETARIA", facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente().getDescricao());
            dto.adicionarParametro("idPregao", selecionado.getId());
            dto.setNomeRelatorio("Relatório Pregão Por Lote");
            dto.setApi("administrativo/pregao-por-lote/");
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
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.PREGAO_POR_LOTE);
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
