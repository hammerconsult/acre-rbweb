package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoAlienacao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoAlienacao;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LeilaoAlienacaoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 12/11/14
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "leilaoAlienacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLeilaoAlienacao", pattern = "/efetivacao-de-alienacao/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaoalienacao/edita.xhtml"),
    @URLMapping(id = "editarLeilaoAlienacao", pattern = "/efetivacao-de-alienacao/editar/#{leilaoAlienacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoalienacao/edita.xhtml"),
    @URLMapping(id = "listarLeilaoAlienacao", pattern = "/efetivacao-de-alienacao/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaoalienacao/lista.xhtml"),
    @URLMapping(id = "verLeilaoAlienacao", pattern = "/efetivacao-de-alienacao/ver/#{leilaoAlienacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaoalienacao/visualizar.xhtml")
})
public class LeilaoAlienacaoControlador extends PrettyControlador<LeilaoAlienacao> implements Serializable, CRUD {

    @EJB
    private LeilaoAlienacaoFacade facade;
    private DetentorDocto doctoSelecionado;
    private LeilaoAlienacaoLote leilaoLote;
    private Future<List<LeilaoAlienacaoLote>> futureLotes;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private Future<LeilaoAlienacao> futureSalvarAndConcluir;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public LeilaoAlienacaoControlador() {
        super(LeilaoAlienacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-de-alienacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public LeilaoAlienacaoLote getLeilaoLote() {
        return leilaoLote;
    }

    public void setLeilaoLote(LeilaoAlienacaoLote leilaoLote) {
        this.leilaoLote = leilaoLote;
    }

    @URLAction(mappingId = "novoLeilaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            selecionado.setDataEfetivacao(getDataOperacao());
            selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
            selecionado.setSituacaoAlienacao(SituacaoAlienacao.EM_ELABORACAO);
            iniciarAssistenteMovimentacao();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataEfetivacao(), OperacaoMovimentacaoBem.EFETIVACAO_ALIENACAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    @URLAction(mappingId = "verLeilaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependenciaArquivo(getId());
        for (LeilaoAlienacaoLote loteEfetivacao : selecionado.getLotesLeilaoAlienacao()) {
            List<VOItemSolicitacaoAlienacao> itensRecuperadosDoLote = facade.buscarItensSolicitacaoArrematadosNoLeilao(loteEfetivacao);
            loteEfetivacao.getItensAvaliados().addAll(itensRecuperadosDoLote);
            arrematarProporcionalmenteOsBensDoLote(loteEfetivacao);
        }
    }

    @URLAction(mappingId = "editarLeilaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarComDependenciaArquivo(getId());
        if (selecionado.getCodigo() != null) {
            FacesUtil.addAtencao("Esta Efetivação já foi concluída, portanto não poderá sofrer alterações.");
            redirecionarParaVer();
        }
        for (LeilaoAlienacaoLote loteEfetivacao : selecionado.getLotesLeilaoAlienacao()) {
            List<VOItemSolicitacaoAlienacao> itensRecuperadosDoLote = facade.buscarItensSolicitacaoArrematadosNoLeilao(loteEfetivacao);
            loteEfetivacao.getItensAvaliados().addAll(itensRecuperadosDoLote);
            arrematarProporcionalmenteOsBensDoLote(loteEfetivacao);
        }
    }

    public void processarDadosAoSelecionarAvaliacao() {
        try {
            if (selecionado.getAvaliacaoAlienacao() != null) {
                iniciarAssistenteMovimentacao();
                validarDataLancamentoAndDataOperacaoBem();
                futureLotes = facade.pesquisarAndGerarLotesLeilaoAlienacao(selecionado, assistenteMovimentacao);
                FacesUtil.executaJavaScript("iniciarPesquisa()");
            } else {
                selecionado.setLotesLeilaoAlienacao(Lists.<LeilaoAlienacaoLote>newArrayList());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public BigDecimal getValorTotalProporciaonalArrematado(LeilaoAlienacaoLote loteLeilao) {
        BigDecimal total = BigDecimal.ZERO;
        for (VOItemSolicitacaoAlienacao itemLeilao : loteLeilao.getItensAvaliados()) {
            total = total.add(itemLeilao.getValorArrematado());
        }
        return total;
    }

    public BigDecimal buscarValorTotalAvaliadoDosItens(LoteAvaliacaoAlienacao loteAvaliacaoAlienacao) {
        BigDecimal total = BigDecimal.ZERO;
        if (loteAvaliacaoAlienacao != null) {
            total = facade.buscarValorTotalAvaliadoDosItens(loteAvaliacaoAlienacao);
        }
        return total;
    }

    public void formatarValor(BigDecimal valor) {
        Util.formataValor(valor);
    }

    public void acompanharPesquisa() {
        if (futureLotes != null && futureLotes.isDone()) {
            if (!assistenteMovimentacao.getMensagens().isEmpty()) {
                futureLotes = null;
                ValidacaoException validacao = new ValidacaoException();
                for (String mensagem : assistenteMovimentacao.getMensagens()) {
                    validacao.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
                }

                FacesUtil.printAllFacesMessages(validacao.getAllMensagens());
            }
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    public void finalizarPesquisa() throws ExecutionException, InterruptedException {
        if (futureLotes != null) {
            selecionado.setLotesLeilaoAlienacao(futureLotes.get());
            if (isOperacaoEditar()) {
                for (LeilaoAlienacaoLote leilaoAlienacaoLote : selecionado.getLotesLeilaoAlienacao()) {
                    arrematarProporcionalmenteOsBensDoLote(leilaoAlienacaoLote);
                }
            }
        }
    }

    private void inicializarArremateLote(LeilaoAlienacaoLote loteLeilao) {
        loteLeilao.setValorArrematado(BigDecimal.ZERO);
        for (VOItemSolicitacaoAlienacao itemAvaliadao : loteLeilao.getItensAvaliados()) {
            itemAvaliadao.setValorArrematado(BigDecimal.ZERO);
        }
    }

    public void processaAlteracaoDeArremate(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        inicializarArremateLote(leilaoAlienacaoLote);
    }

    public void arrematarProporcionalmenteOsBensDoLote(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        if (leilaoAlienacaoLote.getValorArrematado() == null) {
            leilaoAlienacaoLote.setValorArrematado(BigDecimal.ZERO);
        }
        BigDecimal valorLote = facade.buscarValorTotalAvaliadoDosItens(leilaoAlienacaoLote.getLoteAvaliacaoAlienacao());
        BigDecimal porcentagemDoBem;
        BigDecimal valorArrematado = leilaoAlienacaoLote.getValorArrematado();
        for (VOItemSolicitacaoAlienacao item : leilaoAlienacaoLote.getItensAvaliados()) {
            BigDecimal valorAvaliado = null;
            if (valorLote.compareTo(BigDecimal.ZERO) <= 0) {
                porcentagemDoBem = BigDecimal.ZERO;
            } else {
                valorAvaliado = item.getValorAvaliado();
                porcentagemDoBem = valorAvaliado.divide(valorLote, 9, BigDecimal.ROUND_HALF_UP);
            }
            if (leilaoAlienacaoLote.getValorArrematado() != null) {
                BigDecimal valorProporcional = valorArrematado.multiply(porcentagemDoBem).setScale(2, RoundingMode.HALF_UP);
                item.setValorArrematado(valorProporcional);

                valorLote = valorLote.subtract(valorAvaliado);
                valorArrematado = valorArrematado.subtract(valorProporcional);
            }
        }
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (configMovimentacaoBem !=null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataEfetivacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private boolean validarLeilaoAlienacaoLote(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        boolean validou = true;
        if (leilaoAlienacaoLote.getArrematado()) {
            if (leilaoAlienacaoLote.getValorArrematado() == null ||
                leilaoAlienacaoLote.getValorArrematado().compareTo(BigDecimal.ZERO) <= 0) {
                validou = false;
                FacesUtil.addOperacaoNaoPermitida("O Valor Arrematado do Lote Nº " + leilaoAlienacaoLote.getLoteAvaliacaoAlienacao().getNumero() + " deve ser informado com um valor maior que 0(zero).");
            }
        }
        return validou;
    }

    private boolean validarArremates() {
        boolean validou = true;
        if (selecionado.getLotesLeilaoAlienacao() != null) {
            for (LeilaoAlienacaoLote leilaoAlienacaoLote : selecionado.getLotesLeilaoAlienacao()) {
                if (!validarLeilaoAlienacaoLote(leilaoAlienacaoLote)) {
                    validou = false;
                }
            }
        }
        return validou;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        return validarArremates();
    }

    public void novoDocumento(LeilaoAlienacaoLote leilao) {
        try {
            leilaoLote = leilao;
            if (leilaoLote.getDetentDoctoFiscalLiquidacao() == null) {
                leilaoLote.setDetentDoctoFiscalLiquidacao(new DetentorDoctoFiscalLiquidacao());
            }
            validarNovoDocumento();
            doctoSelecionado = new DetentorDocto(leilaoLote.getDetentDoctoFiscalLiquidacao());
            doctoSelecionado.getDoctoFiscalLiquidacao().setTotal(leilaoLote.getValorArrematado());
            doctoSelecionado.getDoctoFiscalLiquidacao().setDataDocto(getDataOperacao());
            FacesUtil.executaJavaScript("dialogDocs.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarNovoDocumento() {
        ValidacaoException ve = new ValidacaoException();
        if (leilaoLote == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo leilão do lote deve ser informado.");
        } else {
            if (leilaoLote.getArrematado() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote: " + leilaoLote + " não foi arrematado'. Marque a opção desejada: (<b>Sim</b> / <b>Não</b>) para continuar.");
            }
            ve.lancarException();
            if (leilaoLote.getArrematado() && leilaoLote.getValorArrematado().compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote: " + leilaoLote + " deve ter valor maior que zero(0).");
            }
            if (leilaoLote.getDetentDoctoFiscalLiquidacao() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo detentor documento fiscal deve ser informado.");
            }
        }
        ve.lancarException();
    }

    public BigDecimal valorTotalDocumentoLote() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (leilaoLote != null && leilaoLote.getDetentDoctoFiscalLiquidacao() != null) {
            for (DetentorDocto detentorDocto : leilaoLote.getDetentDoctoFiscalLiquidacao().getDoctoLista()) {
                valorTotal = valorTotal.add(detentorDocto.getDoctoFiscalLiquidacao().getTotal());
            }
        }
        return valorTotal;
    }

    public void adicionarDocumento() {
        try {
            Util.validarCampos(doctoSelecionado.getDoctoFiscalLiquidacao());
            verificarValorDocumento();
            if (leilaoLote.getDetentDoctoFiscalLiquidacao() != null && doctoSelecionado != null) {
                Util.adicionarObjetoEmLista(leilaoLote.getDetentDoctoFiscalLiquidacao().getDoctoLista(), doctoSelecionado);
                novoDocumento(leilaoLote);
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    private void verificarValorDocumento() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (doctoSelecionado.getDoctoFiscalLiquidacao().getTotal().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O Valor do Documento deve ser maior que zero(0).");
        }
        ve.lancarException();
    }

    public void removerDocumento(DetentorDocto detentorDocto) {
        leilaoLote.getDetentDoctoFiscalLiquidacao().getDoctoLista().remove(detentorDocto);
    }

    public void editarDocumento(DetentorDocto detentorDocto) {
        doctoSelecionado = (DetentorDocto) Util.clonarObjeto(detentorDocto);
    }

    public DetentorDocto getDoctoSelecionado() {
        return doctoSelecionado;
    }

    public void setDoctoSelecionado(DetentorDocto doctoSelecionado) {
        this.doctoSelecionado = doctoSelecionado;
    }

    public void atribuirDetentorSelecionado(LeilaoAlienacaoLote leilao) {
        try {
            leilaoLote = leilao;
            if (leilaoLote.getDetentDoctoFiscalLiquidacao().getId() != null) {
                leilaoLote.setDetentDoctoFiscalLiquidacao(facade.recuperarDetentorDoctoFiscalLiquidacao(leilaoLote.getDetentDoctoFiscalLiquidacao()));
                leilaoLote.getDetentDoctoFiscalLiquidacao().getDoctoLista().size();
                FacesUtil.executaJavaScript("dialogDocs.show()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void concluirEfetivacao() {
        try {
            iniciarAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimentoSingleton();
            futureSalvarAndConcluir = facade.concluirEfetivacao(selecionado, assistenteMovimentacao);
            FacesUtil.executaJavaScript("openDialog(dlgConcluir)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (MovimentacaoBemException ex) {
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            iniciarAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimentoSingleton();
            selecionado = facade.salvarEfetivacaoAlienacao(selecionado);
            salvarItensLeilao();
        } catch (MovimentacaoBemException ex) {
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void bloquearMovimentoSingleton() {
        for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(LeilaoAlienacao.class, unidade, assistenteMovimentacao);
        }
    }

    private void desbloquearMovimentoSingleton() {
        if (assistenteMovimentacao != null && !assistenteMovimentacao.getUnidades().isEmpty()) {
            for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
                facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(LeilaoAlienacao.class, unidade);
            }
        }
    }

    public void finalizarSalvar() {
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        redirecionarParaVer();
    }


    private void validarSalvar() {
        selecionado.realizarValidacoes();
        verificarSeLoteFoiArrematado();
        validarDocumentos();
    }

    private void salvarItensLeilao() {
        if (selecionado != null) {
            List<LeilaoAlienacaoLote> lotesSalvos = facade.salvarLoteEfetivacaoAlienacao(selecionado);
            if (lotesSalvos != null && !lotesSalvos.isEmpty()) {
                iniciarAssistenteMovimentacao();
                futureSalvarAndConcluir = facade.salvarItensEfetiacaoAlienacao(selecionado, assistenteMovimentacao, lotesSalvos);
                FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
                FacesUtil.executaJavaScript("acompanharSalvar()");
            }
        }
    }

    @Override
    public void excluir() {
        try {
            facade.removerSelecionado(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada("Registro excluído com sucesso.");
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void consultarFutureSalvar() {
        try {
            if (futureSalvarAndConcluir != null && futureSalvarAndConcluir.isDone()) {
                selecionado = futureSalvarAndConcluir.get();
                if (selecionado != null) {
                    futureSalvarAndConcluir = null;
                    FacesUtil.executaJavaScript("finalizarSalvar()");
                    desbloquearMovimentoSingleton();
                }
            }
        } catch (Exception e) {
            assistenteMovimentacao.setBloquearAcoesTela(true);
            assistenteMovimentacao.descobrirETratarException(e);
            futureSalvarAndConcluir = null;
            desbloquearMovimentoSingleton();
            redirecionarParaVer();
        }
    }

    private void iniciarAssistenteMovimentacao() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
        assistenteMovimentacao.zerarContadoresProcesso();
        assistenteMovimentacao.setSelecionado(selecionado);
        if (configMovimentacaoBem == null) {
            recuperarConfiguracaoMovimentacaoBem();
        }
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);

        if (selecionado.getAvaliacaoAlienacao() != null) {
            List<SolicitacaoAlienacao> solicitacoes = facade.getSolicitacaoAlienacaoFacade().buscarSolicitacaoPorAvaliacaoAlienacao(selecionado.getAvaliacaoAlienacao());
            Set<UnidadeOrganizacional> unidades = new HashSet<>();
            for (SolicitacaoAlienacao sol : solicitacoes) {
                unidades.add(sol.getUnidadeAdministrativa());
            }
            assistenteMovimentacao.getUnidades().addAll(unidades);
        }
    }

    private void verificarSeLoteFoiArrematado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLotesLeilaoAlienacao() == null || selecionado.getLotesLeilaoAlienacao().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A efetivação não possui lote adicionado.");
        }
        ve.lancarException();
        for (LeilaoAlienacaoLote leilao : selecionado.getLotesLeilaoAlienacao()) {
            if (leilao.getArrematado() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote: " + leilao + ", não foi arrematado'. Marque a opção desejada: (<b>Sim</b> / <b>Não</b>) para continuar.");
            }
            ve.lancarException();
            if (leilao.getArrematado() && leilao.getValorArrematado().compareTo(BigDecimal.ZERO) < 1) {
                ve.adicionarMensagemDeCampoObrigatorio("O Valor Arrematado (R$) deve ser maior que zero.");
            }
        }
        ve.lancarException();
    }

    private void validarDocumentos() {
        if (selecionado.getLotesLeilaoAlienacao() != null) {
            for (LeilaoAlienacaoLote leilao : selecionado.getLotesLeilaoAlienacao()) {
                validarDocumentosLote(leilao);
            }
        }
    }

    private void validarDocumentosLote(LeilaoAlienacaoLote leilao) {
        ValidacaoException ve = new ValidacaoException();
        if (leilao.getArrematado()) {
            BigDecimal total = BigDecimal.ZERO;
            if (leilao.getDetentDoctoFiscalLiquidacao() == null || leilao.getDetentDoctoFiscalLiquidacao().getDoctoLista().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote nº " + leilao.getLoteAvaliacaoAlienacao().getNumero()
                    + " foi arrematado e não teve documento(s) adicionado(s).");
            }
            ve.lancarException();
            for (DetentorDocto detentor : leilao.getDetentDoctoFiscalLiquidacao().getDoctoLista()) {
                total = total.add(detentor.getDoctoFiscalLiquidacao().getTotal());
            }
            if (leilao.getDetentDoctoFiscalLiquidacao().getDoctoLista().size() >= 1 && !(leilao.getValorArrematado().compareTo(total) == 0)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote nº " + leilao.getLoteAvaliacaoAlienacao().getNumero() +
                    " tem valor diferente da soma de seu(s) documento(s). Valor Arrematado: <strong>" + Util.formataValor(leilao.getValorArrematado()) + " </strong>" +
                    " . Valor do(s) documento(s): <strong>" + Util.formataValor(total) + "</strong>.");
            }
        } else {
            if (leilao.getDetentDoctoFiscalLiquidacao() != null && !leilao.getDetentDoctoFiscalLiquidacao().getDoctoLista().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote n° " + leilao.getLoteAvaliacaoAlienacao().getNumero()
                    + " não foi arrematado, e teve documento(s) adicionado(s).");
            }
        }
        ve.lancarException();
    }

    public void confirmarDocumentos() {
        try {
            validarDocumentosLote(leilaoLote);
            FacesUtil.executaJavaScript("dialogDocs.hide();");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void gerarRelatorioConferencia() {
        try {
            String nomeRelatorio = "RELATÓRIO DE EFETIVAÇÃO DE ALIENAÇÃO DE BENS MÓVEIS";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + facade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("idLeilaoAlienacao", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/leilao-alienacao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    public void redirecionarParaEfetivacaoAlienacao(LeilaoAlienacao selecionado) {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void seAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteMovimentacao = assistenteBarraProgresso;
    }
}

