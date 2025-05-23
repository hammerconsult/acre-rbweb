package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ItemLicitatorioContrato;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlteracaoContratualFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AlteracaoContratualControlador extends PrettyControlador<AlteracaoContratual> implements Serializable, CRUD {

    private static final BigDecimal METADE = new BigDecimal("0.50");
    private static final BigDecimal UM_QUARTO = new BigDecimal("0.25");

    @EJB
    private AlteracaoContratualFacade facade;
    private Contrato contrato;
    private AtaRegistroPreco ataRegistroPreco;
    private ConverterAutoComplete converterFonteDespesaORC;
    private MovimentoAlteracaoContratual movimentoAlteracaoContratual;
    private HierarquiaOrganizacional hierarquiaTransferenciaUnidade;
    private HierarquiaOrganizacional hierarquiaProcesso;
    private DotacaoAlteracaoContratualVO dotacaoSolicitacaoCompraSelecionada;
    private BigDecimal saldoFonteDespesaORC;
    private DespesaORC despesaORC;
    private SaldoProcessoLicitatorioVO saldoAta;
    private List<AlteracaoContratual> alteracoesContratuais;
    private List<VOExecucaoReservadoPorLicitacao> voReservasPorLicitacao;
    private List<ItemContrato> itensContrato;
    private List<DotacaoAlteracaoContratualVO> dotacoesSolicitacaoCompra;
    private List<SaldoItemContratoOrigemVO> movimentosItemOrigemSupressao;
    private List<Pessoa> fornecedores;
    private List<OperacaoMovimentoAlteracaoContratual> operacoesMovimento;

    public AlteracaoContratualControlador() {
        super(AlteracaoContratual.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "";
    }

    public String getCaminhoListar() {
        return "";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @Override
    public void editar() {
        super.editar();
        if (selecionado.getSituacao().isDeferido()) {
            FacesUtil.addOperacaoNaoPermitida("Não é permtido realizar alterações em um " + selecionado.getTipoAlteracaoContratual().getDescricao() + " deferido.");
            redirecionarParaVer();
        }
        recuperarProcesso();
        atribuirValorUnitarioProcessoNoMovimentoItem();
        if (selecionado.isTransferenciaDotacao()) {
            preencherDotacaoSolicitacaoCompra();
        }
        Util.ordenarListas(selecionado.getMovimentos());
        buscarAlteracoesContratuais();
        operacoesMovimento = Lists.newArrayList();
        definirOperacoesMovimento();
    }

    public void visualizar() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        recuperarProcesso();
        atribuirValorUnitarioProcessoNoMovimentoItem();
        Util.ordenarListas(selecionado.getMovimentos());
        buscarAlteracoesContratuais();
        atribuiMovimenotOrigemSupressao();
        Util.ordenarListas(selecionado.getMovimentos());
    }

    private void recuperarProcesso() {
        if (selecionado.getTipoCadastro().isContrato()) {
            contrato = recuperarContratoComDependenciasFornecedores(selecionado.getContrato());
            if (isOperacaoEditar()) {
                itensContrato = facade.getContratoFacade().buscarItensContrato(contrato);
            }
            setHierarquiaProcesso(facade.getContratoFacade().buscarHierarquiaVigenteContrato(selecionado.getContrato(), selecionado.getDataLancamento()));

        } else if (selecionado.getTipoCadastro().isAta()) {
            ataRegistroPreco = selecionado.getAtaRegistroPreco();
            setHierarquiaProcesso(facade.getHierarquiaOrganizacionalPorUnidade(ataRegistroPreco.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
            buscarItensAta();
        }
    }

    private void atribuirValorUnitarioProcessoNoMovimentoItem() {
        selecionado.getMovimentos().stream().flatMap(mov -> mov.getItens().stream()).forEach(item -> {
            if (item.getItemContrato() != null) {
                item.setValorUnitarioProcesso(item.getItemContrato().getValorUnitario());
                item.setValorTotalProcesso(item.getItemContrato().getValorTotal());
            } else {
                saldoAta.getSaldosAgrupadosPorOrigem().stream().flatMap(saldo -> saldo.getItens().stream())
                    .filter(itemSaldo -> item.getItemProcessoCompra().equals(itemSaldo.getItemProcessoCompra()))
                    .forEach(itemSaldo -> {
                        item.setValorUnitarioProcesso(itemSaldo.getValorUnitarioHomologado());
                        item.setValorTotalProcesso(itemSaldo.getValorlHomologado());
                    });
            }
        });
    }

    private Contrato recuperarContratoComDependenciasFornecedores(Contrato contrato) {
        return facade.getContratoFacade().recuperarContratoComDependenciasFornecedores(contrato.getId());
    }

    public List<TipoObjetoCompra> getTiposObjetoCompraDotacao(TipoObjetoCompra tipoObjetoCompra) {
        return Lists.newArrayList(tipoObjetoCompra);
    }

    public void onRowSelectDotacao(SelectEvent event) {
        dotacaoSolicitacaoCompraSelecionada = ((DotacaoAlteracaoContratualVO) event.getObject());
        movimentoAlteracaoContratual.setValor(dotacaoSolicitacaoCompraSelecionada.getSaldo());
    }

    public boolean hasDotacaoSolicitacaoCompraSelecionada() {
        return dotacaoSolicitacaoCompraSelecionada != null;
    }

    public void recuperarDadosAta() {
        try {
            selecionado.setAlteracaoContratualAta(null);
            selecionado.setAlteracaoContratualContrato(null);
            validarExecucaoContratoEmpenhada();
            if (ataRegistroPreco != null && isOperacaoNovo()) {
                popularAlteracaoContratual();
                validarSeUltimaAlteracaoEstaDeferida();
                setHierarquiaProcesso(facade.getHierarquiaOrganizacionalPorUnidade(ataRegistroPreco.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));

                AlteracaoContratualAta aca = new AlteracaoContratualAta();
                aca.setAlteracaoContratual(selecionado);
                aca.setAtaRegistroPreco(ataRegistroPreco);
                selecionado.setAlteracaoContratualAta(aca);
            }
            novoMovimento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void recuperarDadosContrato() {
        try {
            contrato = recuperarContratoComDependenciasFornecedores(contrato);
            selecionado.setAlteracaoContratualAta(null);
            selecionado.setAlteracaoContratualContrato(null);
            validarExecucaoContratoEmpenhada();
            if (contrato != null && isOperacaoNovo()) {
                popularAlteracaoContratual();
                setHierarquiaProcesso(facade.getContratoFacade().buscarHierarquiaVigenteContrato(contrato, selecionado.getDataLancamento()));
                validarSeUltimaAlteracaoEstaDeferida();

                AlteracaoContratualContrato acc = new AlteracaoContratualContrato();
                acc.setAlteracaoContratual(selecionado);
                acc.setContrato(contrato);
                selecionado.setAlteracaoContratualContrato(acc);
            }
            novoMovimento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            ex.printStackTrace();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void buscarItensAta() {
        FiltroSaldoProcessoLicitatorioVO filtro = new FiltroSaldoProcessoLicitatorioVO(TipoExecucaoProcesso.ATA_REGISTRO_PRECO);
        filtro.setProcessoIrp(facade.getAtaRegistroPrecoFacade().getLicitacaoFacade().isLicitacaoIRP(ataRegistroPreco.getLicitacao()));
        filtro.setIdProcesso(ataRegistroPreco.getLicitacao().getId());
        filtro.setTipoAvaliacaoLicitacao(ataRegistroPreco.getLicitacao().getTipoAvaliacao());
        filtro.setUnidadeOrganizacional(ataRegistroPreco.getUnidadeOrganizacional());
        saldoAta = facade.getSaldoProcessoLicitatorioFacade().getSaldoProcesso(filtro);
    }

    public Long getIdProcesso() {
        if (selecionado.getTipoCadastro().isContrato()) {
            return contrato.getId();
        }
        return ataRegistroPreco.getId();
    }

    public BigDecimal getValorAtualProcesso() {
        if (selecionado.getTipoCadastro().isContrato()) {
            return contrato.getValorAtualContrato();
        }
        return saldoAta.getValorAtualAta();
    }

    public BigDecimal getSaldoAtualProcesso() {
        if (selecionado.getTipoCadastro().isContrato()) {
            return contrato.getSaldoAtualContrato();
        }
        return saldoAta.getSaldoDisponivelComAcrescimo();
    }

    private void validarSeUltimaAlteracaoEstaDeferida() {
        ValidacaoException ve = new ValidacaoException();
        List<AlteracaoContratual> alteracoes = selecionado.getTipoCadastro().isContrato()
            ? facade.buscarAlteracoesContratuaisPorContrato(contrato)
            : facade.buscarAlteracoesContratuaisPorAta(ataRegistroPreco);

        if (!Util.isListNullOrEmpty(alteracoes)) {
            alteracoes.stream()
                .filter(alt -> !alt.getSituacao().isDeferido())
                .map(alt -> "O " + alt.getDescricaoTipoTermo() + " nº " + alt.getNumeroAnoTermo()
                    + " encontra-se " + alt.getSituacao().getDescricao() + ". Para continuar é necessário deferi-lo primeiro.")
                .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        }
        if (selecionado.getTipoCadastro().isContrato()) {
            List<AjusteContrato> ajustes = facade.getAjusteContratoFacade().buscarAjusteContratoPorSituacao(contrato, SituacaoAjusteContrato.EM_ELABORACAO);
            if (!Util.isListNullOrEmpty(ajustes)) {
                ajustes.stream()
                    .map(alt -> "O Ajuste nº " + alt.getNumeroAjuste() + " encontra-se " + alt.getSituacao().getDescricao() + ". Para continuar é necessário aprová-lo primeiro.")
                    .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
            }
        }
        ve.lancarException();
    }

    public void buscarAlteracoesContratuais() {
        alteracoesContratuais = selecionado.isAditivo()
            ? facade.buscarAlteracoesContratuaisAndDependecias(getIdProcesso(), TipoAlteracaoContratual.ADITIVO)
            : facade.buscarAlteracoesContratuaisAndDependecias(getIdProcesso(), TipoAlteracaoContratual.APOSTILAMENTO);
    }

    private void validarExecucaoContratoEmpenhada() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoCadastro().isContrato()) {
            List<ExecucaoContratoEmpenho> execucoesEmpenho = facade.getContratoFacade().getExecucaoContratoFacade().buscarExecucaoContratoEmpenhoPorContrato(contrato);
            if (execucoesEmpenho.stream().anyMatch(exEmp -> exEmp.getEmpenho() == null && !exEmp.getSolicitacaoEmpenho().getEstornada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar um " + selecionado.getTipoAlteracaoContratual().getDescricao()
                    + " antes de empenhar a(s) execução(ções) existente(s) do contrato.");
                contrato = null;
            }
        } else if (selecionado.getTipoCadastro().isAta()) {
            List<ExecucaoProcessoEmpenho> execucoesEmpenhoAta = facade.getAtaRegistroPrecoFacade().getExecucaoProcessoFacade().buscarExecucaoProcessoEmpenhoPorAta(ataRegistroPreco);
            if (execucoesEmpenhoAta.stream().anyMatch(exEmp -> exEmp.getEmpenho() == null && !exEmp.getSolicitacaoEmpenho().getEstornada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar um " + selecionado.getTipoAlteracaoContratual().getDescricao()
                    + " antes de empenhar a(s) execução(ções) existente(s) da ata registro de preço.");
                ataRegistroPreco = null;
            }
        }
        ve.lancarException();
    }

    public void definirAlteracaoContratualComoNull() {
        contrato = null;
        ataRegistroPreco = null;
    }

    public void definirVariaveisTipoTermo() {
        if (movimentoAlteracaoContratual.getOperacao() != null && movimentoAlteracaoContratual.isOperacoesPrazo()) {
            definirCamposDataComoNull();
            movimentoAlteracaoContratual.setInicioVigencia(DataUtil.adicionaDias(selecionado.getTerminoVigenciaAtual(), 1));
            if (selecionado.getTipoCadastro().isContrato()) {
                movimentoAlteracaoContratual.setInicioExecucao(DataUtil.adicionaDias(contrato.getTerminoExecucaoAtual(), 1));
            }
        }
    }

    public boolean isEmElaboracao() {
        return selecionado.getSituacao().isEmElaboracao();
    }

    public boolean isAprovado() {
        return selecionado.getSituacao().isAprovado();
    }

    public boolean isApostilamentoOperacaoOutro() {
        return movimentoAlteracaoContratual != null && movimentoAlteracaoContratual.isApostilamentoOperacaoOutro();
    }

    public boolean hasMovimentos() {
        return selecionado != null && selecionado.getMovimentos() != null && !selecionado.getMovimentos().isEmpty();
    }

    public boolean hasVoReservadoPorLicitacao() {
        return voReservasPorLicitacao != null && !voReservasPorLicitacao.isEmpty();
    }

    public boolean hasSupressaoValorNegativoReservadoLicitacao() {
        for (VOExecucaoReservadoPorLicitacao movimento : voReservasPorLicitacao) {
            if (movimento.hasValorFinalNegativo()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTransferenciaUnidade() {
        return selecionado.getMovimentos().stream().anyMatch(MovimentoAlteracaoContratual::isTransferenciaUnidade);
    }

    public void editarMovimento(MovimentoAlteracaoContratual movimento) {
        movimentoAlteracaoContratual = (MovimentoAlteracaoContratual) Util.clonarObjeto(movimento);
        selecionarReservaDotacaoAoEditarMovimentoTransferenciaDotacao();

        if (movimentoAlteracaoContratual.getFonteDespesaORC() != null) {
            despesaORC = movimentoAlteracaoContratual.getFonteDespesaORC().getDespesaORC();
            buscarSaldoFonteDespesaORC();
        }
        buscarMovimentosOrigemSupressao();
        if (movimentosItemOrigemSupressao != null) {
            movimentosItemOrigemSupressao
                .stream()
                .filter(mov -> mov.getIdMovimentoOrigem().equals(movimentoAlteracaoContratual.getIdMovimentoOrigem()))
                .findFirst()
                .ifPresent(mov -> this.movimentoAlteracaoContratual.setMovimentoOrigemExecucaoContratoVO(mov));
        }
        operacoesMovimento = Lists.newArrayList();
        definirOperacoesMovimento();
    }

    public UnidadeOrganizacional getUnidadeOrcamentariaDespesaOrc() {
        if (despesaORC != null) {
            return despesaORC.getProvisaoPPADespesa().getUnidadeOrganizacional();
        }
        return null;
    }

    private void selecionarReservaDotacaoAoEditarMovimentoTransferenciaDotacao() {
        selecionado.getMovimentos().forEach(mov -> {
            if (mov.isTransferenciaDotacao() && mov.getFinalidade().isSupressao()) {
                dotacoesSolicitacaoCompra
                    .stream()
                    .filter(dotSolMat -> dotSolMat.getFonteDespesaORC().equals(mov.getFonteDespesaORC()))
                    .findFirst()
                    .ifPresent(dotPresent -> dotacaoSolicitacaoCompraSelecionada = dotPresent);
            }
        });
    }

    private void atribuiMovimenotOrigemSupressao() {
        if (isOperacaoVer() && selecionado.getTipoAlteracaoContratual().isAditivo()) {
            selecionado.getMovimentos().stream()
                .filter(mov -> mov.getOrigemSupressao() != null && mov.getIdMovimentoOrigem() != null)
                .forEach(mov -> {
                    SaldoItemContratoOrigemVO item = new SaldoItemContratoOrigemVO();
                    String descricao = mov.getOrigemSupressao().getDescricao() + ": ";
                    switch (mov.getOrigemSupressao()) {
                        case CONTRATO:
                            Contrato contrato = facade.getContratoFacade().recuperarSemDependencias(mov.getIdMovimentoOrigem());
                            descricao += contrato.toString();
                            break;
                        case ADITIVO:
                            AlteracaoContratual alt = facade.recuperarSemDependencias(mov.getIdMovimentoOrigem());
                            descricao += alt.toString();
                            break;
                    }
                    item.setDescricaoMovimento(descricao);
                    mov.setMovimentoOrigemExecucaoContratoVO(item);
                });
        }
    }

    public boolean hasItemMovimentosSelecionado() {
        for (MovimentoAlteracaoContratual mov : selecionado.getMovimentos()) {
            if (mov.getItens().stream().anyMatch(MovimentoAlteracaoContratualItem::hasValorTotalMaiorQueZero)) {
                return true;
            }
        }
        return false;
    }

    public void removerMovimento(MovimentoAlteracaoContratual mov) {
        selecionado.getMovimentos().remove(mov);
        if (mov.isTransferenciaDotacao()) {
            selecionado.getMovimentos().clear();
        }
        definirTipoTermo();
    }

    public void cancelarMovimento() {
        movimentoAlteracaoContratual = null;
        dotacaoSolicitacaoCompraSelecionada = null;
    }

    public void atribuirIdMovimentoOrigemSupressao() {
        if (movimentoAlteracaoContratual.getMovimentoOrigemExecucaoContratoVO() != null) {
            movimentoAlteracaoContratual.setIdMovimentoOrigem(movimentoAlteracaoContratual.getMovimentoOrigemExecucaoContratoVO().getIdMovimentoOrigem());
            movimentoAlteracaoContratual.setOrigemSupressao(movimentoAlteracaoContratual.getMovimentoOrigemExecucaoContratoVO().getOrigem());
        }
    }

    public void buscarMovimentosOrigemSupressao() {
        if (movimentoAlteracaoContratual != null) {
            movimentoAlteracaoContratual.setMovimentoOrigemExecucaoContratoVO(null);
            if (movimentoAlteracaoContratual.getFinalidade() != null && movimentoAlteracaoContratual.getFinalidade().isSupressao()) {
                SubTipoSaldoItemContrato subTipo = movimentoAlteracaoContratual.getOperacao().isOutro() ? SubTipoSaldoItemContrato.EXECUCAO : SubTipoSaldoItemContrato.VARIACAO;
                movimentosItemOrigemSupressao = facade.getSaldoItemContratoFacade().buscarSaldoOrigemVO(contrato, subTipo);
            }
        }
    }

    public List<SelectItem> getOperacoes() {
        return Util.getListSelectItem(operacoesMovimento.toArray(), false);
    }

    public void definirOperacoesMovimento() {
        if (!hasMovimentos() && contrato != null && contrato.isContratoAprovado()) {
            operacoesMovimento.addAll(OperacaoMovimentoAlteracaoContratual.getOperacoesContratoAprovado(selecionado.getTipoAlteracaoContratual()));

        } else if (!hasMovimentos() && selecionado.isApostilamento()) {
            operacoesMovimento.addAll(OperacaoMovimentoAlteracaoContratual.getOperacoesApostilamento(selecionado.getTipoCadastro()));

        } else if (!hasMovimentos() && selecionado.isAditivo()) {
            operacoesMovimento.addAll(Lists.newArrayList(OperacaoMovimentoAlteracaoContratual.getOperacoesAditivo(selecionado.getTipoCadastro())));

        } else {
            operacoesMovimento.addAll(selecionado.isAditivo()
                ? Lists.newArrayList(OperacaoMovimentoAlteracaoContratual.getOperacoesAditivo(selecionado.getTipoCadastro()))
                : OperacaoMovimentoAlteracaoContratual.getOperacoesApostilamento(selecionado.getTipoCadastro()));
            for (MovimentoAlteracaoContratual movList : selecionado.getMovimentos()) {
                if (movList.getOperacao().isReducaoPrazo()) {
                    operacoesMovimento.removeIf(op -> op.equals(OperacaoMovimentoAlteracaoContratual.DILACAO_PRAZO));
                    operacoesMovimento.removeIf(op -> OperacaoMovimentoAlteracaoContratual.getOperacoesTransferencia().contains(op));
                }
                if (movList.getOperacao().isDilacaoPrazo()) {
                    operacoesMovimento.removeIf(op -> op.equals(OperacaoMovimentoAlteracaoContratual.REDUCAO_PRAZO));
                    operacoesMovimento.removeIf(op -> OperacaoMovimentoAlteracaoContratual.getOperacoesTransferencia().contains(op));
                }
                if (movList.getOperacao().isRedimensionamentoObjeto()) {
                    operacoesMovimento.clear();
                    operacoesMovimento.add(OperacaoMovimentoAlteracaoContratual.REDIMENSIONAMENTO_OBJETO);
                    operacoesMovimento.add(movList.getFinalidade().isAcrescimo() ? OperacaoMovimentoAlteracaoContratual.DILACAO_PRAZO : OperacaoMovimentoAlteracaoContratual.REDUCAO_PRAZO);
                }
                if (movList.getOperacao().isReajustePreExecucao() || movList.getOperacao().isReajustePosExecucao()) {
                    operacoesMovimento.clear();
                    operacoesMovimento.add(movList.getFinalidade().isAcrescimo() ? OperacaoMovimentoAlteracaoContratual.DILACAO_PRAZO : OperacaoMovimentoAlteracaoContratual.REDUCAO_PRAZO);
                    operacoesMovimento.add(OperacaoMovimentoAlteracaoContratual.REAJUSTE_PRE_EXECUCAO);
                    if (selecionado.isAditivo()) {
                        operacoesMovimento.add(OperacaoMovimentoAlteracaoContratual.REAJUSTE_POS_EXECUCAO);
                    }
                }
                if (movList.getOperacao().isReequilibrioPreExecucao() || movList.getOperacao().isReequilibrioPosExecucao()) {
                    operacoesMovimento.clear();
                    operacoesMovimento.add(OperacaoMovimentoAlteracaoContratual.REEQUILIBRIO_PRE_EXECUCAO);
                    operacoesMovimento.add(OperacaoMovimentoAlteracaoContratual.REEQUILIBRIO_POS_EXECUCAO);
                    operacoesMovimento.add(movList.getFinalidade().isAcrescimo() ? OperacaoMovimentoAlteracaoContratual.DILACAO_PRAZO : OperacaoMovimentoAlteracaoContratual.REDUCAO_PRAZO);
                }
                if (movList.getOperacao().isOutro()) {
                    operacoesMovimento.clear();
                    operacoesMovimento.add(movList.getFinalidade().isAcrescimo() ? OperacaoMovimentoAlteracaoContratual.DILACAO_PRAZO : OperacaoMovimentoAlteracaoContratual.REDUCAO_PRAZO);
                    operacoesMovimento.add(OperacaoMovimentoAlteracaoContratual.OUTRO);
                }
                if (movList.getOperacao().isTransferenciaDotacao()){
                    operacoesMovimento.clear();
                    operacoesMovimento.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_DOTACAO);
                }
                if (movList.getOperacao().isTransferenciaUnidade()){
                    operacoesMovimento.clear();
                    operacoesMovimento.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_UNIDADE);
                }
                if (movList.getOperacao().isTransferenciaFornecedor()){
                    operacoesMovimento.clear();
                    operacoesMovimento.add(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_FORNECEDOR);
                }
            }
        }
    }

    public List<SelectItem> getFinalidades() {
        if (movimentoAlteracaoContratual.getOperacao() != null && !hasMovimentos()) {
            return Util.getListSelectItem(FinalidadeMovimentoAlteracaoContratual.retornaFinalidadesPorOperacao(movimentoAlteracaoContratual.getOperacao(), selecionado.getTipoAlteracaoContratual()));

        } else if (hasMovimentos()) {
            MovimentoAlteracaoContratual movList = selecionado.getMovimentos().get(0);
            return Util.getListSelectItem(Lists.newArrayList(movList.getFinalidade()));
        }
        return Lists.newArrayList();
    }

    private void definirTipoTermo() {
        Optional<MovimentoAlteracaoContratual> firstTransf = selecionado.getMovimentos().stream().filter(MovimentoAlteracaoContratual::isOperacoesTransferencias).findFirst();
        if (firstTransf.isPresent()) {
            selecionado.setTipoTermo(TipoTermoAlteracaoContratual.ALTERACAO_CADASTRAL);
            return;
        }
        Optional<MovimentoAlteracaoContratual> firstPrazo = selecionado.getMovimentos().stream().filter(MovimentoAlteracaoContratual::isOperacoesPrazo).findFirst();
        Optional<MovimentoAlteracaoContratual> firstValor = selecionado.getMovimentos().stream().filter(MovimentoAlteracaoContratual::isOperacoesValor).findFirst();
        if (firstPrazo.isPresent() && firstValor.isPresent()) {
            selecionado.setTipoTermo(TipoTermoAlteracaoContratual.PRAZO_VALOR);
        } else if (firstPrazo.isPresent()) {
            selecionado.setTipoTermo(TipoTermoAlteracaoContratual.PRAZO);
        } else if (firstValor.isPresent()) {
            selecionado.setTipoTermo(TipoTermoAlteracaoContratual.VALOR);
        } else if (isApostilamentoOperacaoOutro()) {
            selecionado.setTipoTermo(TipoTermoAlteracaoContratual.NAO_APLICAVEL);
        }
    }

    public List<Pessoa> completarResponsavelFornecedor(String parte) {
        return facade.getPessoaFacade().listaPessoasFisicas(parte.trim());
    }

    public List<Pessoa> completarFornecedor(String parte) {
        return facade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<Contrato> completarContratos(String parte) {
        return facade.getContratoFacade().buscarFiltrandoPorSituacao(parte.trim(), SituacaoContrato.APROVADO, SituacaoContrato.DEFERIDO);
    }

    public List<AtaRegistroPreco> completarAtaRegistroPreco(String parte) {
        return facade.getAtaRegistroPrecoFacade().buscarAtaRegistroPrecoVigenteOndeUsuarioGestorLicitacao(parte.trim());
    }

    public void listenerOperacaoMovimento() {
        if (movimentoAlteracaoContratual.getOperacao() != null) {
            try {
                validarOperacaoTransferenciaDotacaoEmExercicioAnterior();

                if (Util.isListNullOrEmpty(itensContrato) && selecionado.getTipoCadastro().isContrato()) {
                    itensContrato = facade.getContratoFacade().buscarItensContrato(contrato);
                }
                if (selecionado.getTipoCadastro().isAta()) {
                    buscarItensAta();
                }
                criarItemMovimento();
                movimentoAlteracaoContratual.setValorVariacaoContrato(false);
                if (movimentoAlteracaoContratual.getOperacao().isRedimensionamentoObjeto()) {
                    movimentoAlteracaoContratual.setValorVariacaoContrato(true);

                } else if (movimentoAlteracaoContratual.isApostilamentoOperacaoOutro()) {
                    movimentoAlteracaoContratual.setFinalidade(FinalidadeMovimentoAlteracaoContratual.NAO_SE_APLICA);

                } else if (movimentoAlteracaoContratual.getOperacao().isTransferenciaUnidade()) {
                    movimentoAlteracaoContratual.setFinalidade(FinalidadeMovimentoAlteracaoContratual.NAO_SE_APLICA);

                } else if (movimentoAlteracaoContratual.isTransferenciaFornecedor()) {
                    movimentoAlteracaoContratual.setFinalidade(FinalidadeMovimentoAlteracaoContratual.NAO_SE_APLICA);
                    FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:fornecedor_input')");

                } else if (movimentoAlteracaoContratual.getOperacao().isDilacaoPrazo()
                    || movimentoAlteracaoContratual.getOperacao().isReajustePreExecucao()
                    || movimentoAlteracaoContratual.getOperacao().isReajustePosExecucao()
                    || movimentoAlteracaoContratual.getOperacao().isReequilibrioPreExecucao()
                    || movimentoAlteracaoContratual.getOperacao().isReequilibrioPosExecucao()) {
                    movimentoAlteracaoContratual.setFinalidade(FinalidadeMovimentoAlteracaoContratual.ACRESCIMO);

                } else if (movimentoAlteracaoContratual.getOperacao().isReducaoPrazo()) {
                    movimentoAlteracaoContratual.setFinalidade(FinalidadeMovimentoAlteracaoContratual.SUPRESSAO);

                } else if (movimentoAlteracaoContratual.isTransferenciaDotacao()) {
                    movimentoAlteracaoContratual.setFinalidade(FinalidadeMovimentoAlteracaoContratual.ACRESCIMO);
                    boolean isProcessoComReservaDotacaoInicial = contrato.getObjetoAdequado().getProcessoDeCompra() != null && facade.getProcessoDeCompraFacade().hasProcessoComReservaDotacao(contrato.getObjetoAdequado().getProcessoDeCompra());
                    if (!isProcessoComReservaDotacaoInicial) {
                        FacesUtil.addOperacaoNaoPermitida("O contrato/processo não possui reserva de dotação inicial.");
                        movimentoAlteracaoContratual.setOperacao(null);
                        return;
                    }
                    preencherDotacaoSolicitacaoCompra();
                }
                definirVariaveisTipoTermo();
                definirTipoTermo();
                FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:tipo-mov')");
            } catch (ValidacaoException ve) {
                movimentoAlteracaoContratual.setOperacao(null);
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }
        }
    }

    private void validarOperacaoTransferenciaDotacaoEmExercicioAnterior() {
        ValidacaoException ve = new ValidacaoException();
        if (movimentoAlteracaoContratual.getOperacao().isTransferenciaDotacao() && Util.isExercicioLogadoDiferenteExercicioAtual(facade.getSistemaFacade().getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido realizar uma transferência de dotação reservada com exercício diferente do exercício atual.");
        }
        ve.lancarException();
    }

    private void definirCamposDataComoNull() {
        movimentoAlteracaoContratual.setInicioExecucao(null);
        movimentoAlteracaoContratual.setTerminoExecucao(null);
        movimentoAlteracaoContratual.setInicioVigencia(null);
        movimentoAlteracaoContratual.setTerminoVigencia(null);
    }

    private void preencherDotacaoSolicitacaoCompra() {
        if (contrato.getObjetoAdequado().getProcessoDeCompra() != null) {
            Set<TipoObjetoCompra> tiposObjetosCompra = itensContrato.stream()
                .map(ItemContrato::getItemAdequado)
                .map(ItemLicitatorioContrato::getObjetoCompra)
                .map(ObjetoCompra::getTipoObjetoCompra).collect(Collectors.toSet());

            dotacoesSolicitacaoCompra = facade.criarTrasnferenciaDotacaoAlteracaoContratualVO(contrato, tiposObjetosCompra);
        }
    }

    public void listenerFornecedorProcesso() {
        List<Pessoa> fornecedoresProcesso = facade.getContratoFacade().buscarFornecedoresProcesso(contrato);
        if (fornecedoresProcesso.contains(movimentoAlteracaoContratual.getFornecedor())) {
            FacesUtil.addOperacaoNaoPermitida("Não é permitido selecionar um fornecedor que participou do processo licitatório.");
            movimentoAlteracaoContratual.setFornecedor(null);
        }
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, facade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public void aprovar() {
        try {
            validarSalvar();
            selecionado.setDataAprovacao(new Date());
            voReservasPorLicitacao = Lists.newArrayList();
            atribuirHierarquiaNoMovimentoTransferenciaUnidade();
            FacesUtil.executaJavaScript("dlgAprovar.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void atribuirHierarquiaNoMovimentoTransferenciaUnidade() {
        if (isTransferenciaUnidade()) {
            MovimentoAlteracaoContratual mov = selecionado.getMovimentos().get(0);
            if (mov != null) {
                setHierarquiaTransferenciaUnidade(facade.getHierarquiaOrganizacionalPorUnidade(mov.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
            }
        }
    }

    public boolean isMovimentoValorVariacaoSupressao() {
        for (MovimentoAlteracaoContratual movimento : selecionado.getMovimentos()) {
            if (movimento.getFinalidade().isSupressao() && movimento.getValorVariacaoContrato()) {
                return true;
            }
        }
        return false;
    }

    public void atribuirMovimentoParaGerarSaldoOrcamentarioSupressao() {
        voReservasPorLicitacao = Lists.newArrayList();
        for (MovimentoAlteracaoContratual movimento : selecionado.getMovimentos()) {
            if (movimento.getFinalidade().isSupressao() && movimento.getValorVariacaoContrato()) {
                voReservasPorLicitacao.addAll(facade.buscarValoresExecutadoReservaoPorLicitacao(movimento));

                for (VOExecucaoReservadoPorLicitacao voReservado : voReservasPorLicitacao) {
                    if (voReservado.getFonteDespesaORC().equals(movimento.getFonteDespesaORC())
                        && voReservado.getReservadoPorLicitacao().compareTo(movimento.getValor()) > 0) {
                        movimento.setGerarReserva(true);
                    }
                }
            }
        }
    }

    public void cancelarAprovacao() {
        selecionado.setDataAprovacao(null);
        voReservasPorLicitacao = Lists.newArrayList();
        selecionado.setMovimentarSaldoOrcamentario(false);
    }

    public void confirmarAprovacao() {
        try {
            validarDataAprovacao();
            removerItemComValorZerado();
            selecionado = facade.aprovar(selecionado);
            FacesUtil.addOperacaoRealizada("Alteração Contratual nº " + selecionado.getNumeroAnoTermo() + " foi salvo com sucesso.");
            redirecionarParaEditar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void removerItemComValorZerado() {
        selecionado.getMovimentos().forEach(mov -> mov.getItens().removeIf(item -> !item.hasValorTotalMaiorQueZero()));
    }

    private void validarDataAprovacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataAprovacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de aprovação deve ser informado.");
        }
        ve.lancarException();
        facade.validarDataAprovacao(selecionado);
    }

    public void deferirAlteracao() {
        selecionado.setDataDeferimento(new Date());
        FacesUtil.executaJavaScript("dlgDeferir.show()");
    }

    public void cancelarDeferimento() {
        selecionado.setDataDeferimento(null);
    }

    public void confirmarDeferimento() {
        try {
            validarDeferimento();
            validarDataAprovacao();
            selecionado = facade.deferir(selecionado);
            FacesUtil.addOperacaoRealizada("O alteração contratual " + selecionado + " foi deferida com sucesso.");
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarDeferimento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataAssinatura() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de assinatura deve ser informado.");
        }
        ve.lancarException();
        facade.validarDataAssinatura(selecionado);
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    private void redirecionarParaEditar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId());
    }

    public void salvarAlteracao() {
        try {
            validarSalvar();
            selecionado = facade.salvarAlteracao(selecionado);
            FacesUtil.addOperacaoRealizada("Alteração contratual nº " + selecionado.getNumeroAnoTermo() + " foi salva com sucesso.");
            redirecionarParaEditar();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            super.recuperarObjeto();
            AlteracaoContratual alteracaoContratual = selecionado;
            if (selecionado.getTipoCadastro().isContrato()) {
                contrato = selecionado.getContrato();
            }
            facade.remover(selecionado);
            if (isAprovado() && selecionado.getTipoCadastro().isContrato()) {
                facade.getReprocessamentoSaldoContratoFacade().reprocessarUnicoContratoAlteracaoContratual(contrato, alteracaoContratual);
            }
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarSalvar() {
        validarContratoNulo();
        validarAlteracaoContratual();
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoCadastro().isContrato()) {
            facade.validarDataLancamento(selecionado, contrato);
        }
        if (!selecionado.hasMovimentos()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum movimento adicionado para este processo.");

        } else if (selecionado.isAlteracaoContratualValor() && !hasItemMovimentosSelecionado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar ao menos um item para continuar.");
        }
        ve.lancarException();
    }

    public void validarSaldoDisponivelTransferenciaDotacao() {
        if (movimentoAlteracaoContratual.getValor().compareTo(dotacaoSolicitacaoCompraSelecionada.getSaldo()) > 0) {
            FacesUtil.addOperacaoNaoPermitida("O valor à transferir deve ser menor ou igual ao saldo disponível");
            movimentoAlteracaoContratual.setValor(dotacaoSolicitacaoCompraSelecionada.getSaldo());
        }
    }

    private void validarContratoNulo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoCadastro().isContrato() && contrato == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contrato deve ser informado.");
        }
        if (selecionado.getTipoCadastro().isAta() && ataRegistroPreco == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ata Registro de Preço deve ser informado.");
        }
        ve.lancarException();
    }

    private void popularAlteracaoContratual() {
        try {
            selecionado.setDataLancamento(new Date());
            selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
            selecionado.setNumeroTermo(Util.zerosAEsquerda(facade.getProximoNumero(getIdProcesso(), "numeroTermo", selecionado.getTipoAlteracaoContratual()).toString(), 4));
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public List<HierarquiaOrganizacional> completarHierarquiasAdministrativa(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(
            parte.trim(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            facade.getSistemaFacade().getDataOperacao());
    }

    private void validarAlteracaoContratual() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoTermo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo do Termo deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getNumeroTermo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Termo deve ser informado.");
        }
        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano do Termo deve ser informado.");
        }
        if (Strings.isNullOrEmpty(selecionado.getJustificativa())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Justificativa deve ser informado.");
        }
        ve.lancarException();
    }

    public void novoMovimento() {
        try {
            validarNovoMovimento();
            movimentoAlteracaoContratual = new MovimentoAlteracaoContratual();
            movimentoAlteracaoContratual.setAlteracaoContratual(selecionado);
            movimentoAlteracaoContratual.setFinalidade(FinalidadeMovimentoAlteracaoContratual.ACRESCIMO);
            operacoesMovimento = Lists.newArrayList();
            definirOperacoesMovimento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarNovoMovimento() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.getMovimentos().stream()
            .filter(mov -> mov.getOperacao().isTransferenciaFornecedor() || mov.getOperacao().isTransferenciaUnidade())
            .map(mov -> "Só é permitido um movimento de " + mov.getOperacao().getDescricao() + " por termo.")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        ve.lancarException();
    }

    private void criarItemMovimento() {
        try {
            if (movimentoAlteracaoContratual.isOperacoesValor()) {
                movimentoAlteracaoContratual.setItens(Lists.newArrayList());
                if (selecionado.getTipoCadastro().isContrato()) {
                    itensContrato.stream().filter(ItemContrato::hasValorTotalMaiorQueZero).forEach(itemContrato -> {
                        MovimentoAlteracaoContratualItem novoMovItem = new MovimentoAlteracaoContratualItem();
                        novoMovItem.setItemContrato(itemContrato);
                        novoMovItem.setItemProcessoCompra(itemContrato.getItemAdequado().getItemProcessoCompra());
                        novoMovItem.setFornecedor(contrato.getContratado());
                        novoMovItem.setMovimentoAlteracaoCont(movimentoAlteracaoContratual);
                        novoMovItem.setValorUnitarioProcesso(itemContrato.getValorUnitario());
                        novoMovItem.setValorTotalProcesso(itemContrato.getValorUnitario());
                        novoMovItem.setQuantidade(itemContrato.getQuantidadeTotalContrato());
                        novoMovItem.setValorUnitario(itemContrato.getValorUnitario());
                        novoMovItem.setValorTotal(novoMovItem.calcularValorTotal());
                        Util.adicionarObjetoEmLista(movimentoAlteracaoContratual.getItens(), novoMovItem);
                    });
                } else {
                    saldoAta.getSaldosAgrupadosPorOrigem().stream()
                        .flatMap(saldo -> saldo.getItens().stream())
                        .filter(item -> item.getValorAta().compareTo(BigDecimal.ZERO) > 0)
                        .forEach(item -> {
                            MovimentoAlteracaoContratualItem novoMovItem = new MovimentoAlteracaoContratualItem();
                            novoMovItem.setItemProcessoCompra(item.getItemProcessoCompra());
                            novoMovItem.setFornecedor(facade.getPessoaFacade().recuperarSemDependencias(item.getIdFornecedor()));
                            novoMovItem.setMovimentoAlteracaoCont(movimentoAlteracaoContratual);
                            novoMovItem.setValorUnitarioProcesso(item.getValorUnitarioHomologado());
                            novoMovItem.setValorTotalProcesso(item.getValorlHomologado());
                            novoMovItem.setQuantidade(item.getQuantidadeAta());
                            novoMovItem.setValorUnitario(item.getValorUnitarioHomologado());
                            novoMovItem.setValorTotal(novoMovItem.calcularValorTotal());
                            Util.adicionarObjetoEmLista(movimentoAlteracaoContratual.getItens(), novoMovItem);
                        });
                }
                movimentoAlteracaoContratual.setValor(movimentoAlteracaoContratual.getValorTotalItens());
                BigDecimal valorTotalProc = selecionado.getTipoCadastro().isContrato() ? contrato.getValorTotal() : saldoAta.getValorOriginalAta();
                movimentoAlteracaoContratual.setPercentual(movimentoAlteracaoContratual.getPercentualTotalItens(valorTotalProc));
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void adicionarMovimento(String idComponente) {
        try {
            validarMovimento();
            criarMovimentoTransferenciaDotacaoSupressao();
            atribuirIdMovimentoOrigemSupressao();
            Util.adicionarObjetoEmLista(selecionado.getMovimentos(), movimentoAlteracaoContratual);
            definirTipoTermo();
            cancelarMovimento();
            Util.ordenarListas(selecionado.getMovimentos());
            FacesUtil.atualizarComponente(idComponente + ":Formulario:tab-view-geral:panel-movimentos");
            FacesUtil.atualizarComponente(idComponente + ":Formulario:tab-view-geral:panel-alteracao");
            FacesUtil.atualizarComponente(idComponente + ":Formulario:gridGeral");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void criarMovimentoTransferenciaDotacaoSupressao() {
        if (movimentoAlteracaoContratual.isTransferenciaDotacao()) {
            if (selecionado.hasMovimentos()) {
                selecionado.getMovimentos().removeIf(mov -> mov.getFinalidade().isSupressao());
            }
            MovimentoAlteracaoContratual novoMovTransf = new MovimentoAlteracaoContratual();
            novoMovTransf.setAlteracaoContratual(selecionado);
            novoMovTransf.setFonteDespesaORC(dotacaoSolicitacaoCompraSelecionada.getFonteDespesaORC());
            novoMovTransf.setOperacao(OperacaoMovimentoAlteracaoContratual.TRANSFERENCIA_DOTACAO);
            novoMovTransf.setFinalidade(FinalidadeMovimentoAlteracaoContratual.SUPRESSAO);
            novoMovTransf.setValor(movimentoAlteracaoContratual.getValor());
            Util.adicionarObjetoEmLista(selecionado.getMovimentos(), novoMovTransf);
        }
    }

    public void atribuirValorUnitarioAndCalcularValorTotalItem(MovimentoAlteracaoContratualItem item, MovimentoAlteracaoContratual mov) {
        try {
            if (item.getQuantidade() != null) {
                item.setValorUnitario(item.getValorUnitarioProcesso());
                calcularValorTotalMovimento(item, mov);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void calcularValorTotalMovimento(MovimentoAlteracaoContratualItem item, MovimentoAlteracaoContratual mov) {
        try {
            if (item.getQuantidade() != null && item.getValorUnitario() != null) {
                validarValorOrQuantidadeAjusteItemInformado(item);
                item.setValorTotal(item.calcularValorTotal());
                mov.setValor(mov.getValorTotalItens());

                BigDecimal valorTotalProc = selecionado.getTipoCadastro().isContrato() ? contrato.getValorTotal() : saldoAta.getValorOriginalAta();
                mov.setPercentual(mov.getPercentualTotalItens(valorTotalProc));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarValorOrQuantidadeAjusteItemInformado(MovimentoAlteracaoContratualItem item) {
        ValidacaoException ve = new ValidacaoException();
        if (item.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo quantidade deve ser maior que zero(0).");
            item.setQuantidade(BigDecimal.ZERO);
        } else if (item.getValorUnitario().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero(0).");
            item.setValorUnitario(BigDecimal.ZERO);
        }
        ve.lancarException();
        if (movimentoAlteracaoContratual.getFinalidade().isSupressao() && movimentoAlteracaoContratual.isOperacoesValor() && item.getValorTotal().compareTo(item.getItemContrato().getValorTotal()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Valor atual do contrato indisponível para realizar a supressão do item " + item.getItemContrato() + ".");
            item.setValorTotal(BigDecimal.ZERO);
        }
        ve.lancarException();
    }

    public void limparCamposDespesa() {
        setSaldoFonteDespesaORC(BigDecimal.ZERO);
        movimentoAlteracaoContratual.setFonteDespesaORC(null);
    }

    public void confirmarDotacao() {
        try {
            validarDotacao();
            FacesUtil.executaJavaScript("dlgDotacao.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarDotacao() {
        ValidacaoException ve = new ValidacaoException();
        if (despesaORC == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Despesa Orçamentária deve ser informada.");
        }
        if (movimentoAlteracaoContratual.getFonteDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fonte de Recursos deve ser informado.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getFontesDespesaOrcamentaria() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (this.movimentoAlteracaoContratual != null && despesaORC != null) {
            List<FonteDespesaORC> list = facade.getFonteDespesaORCFacade().completaFonteDespesaORC("", despesaORC);
            if (list == null || list.isEmpty()) {
                FacesUtil.addAtencao("Nenhum fonte encontrada para a despesa: " + despesaORC + ".");
                return Lists.newArrayList();
            }
            for (FonteDespesaORC fonte : list) {
                toReturn.add(new SelectItem(fonte, fonte.getDescricaoFonteDeRecurso()));
            }
        }
        return toReturn;
    }

    public void novaDespesaOrcamentaria() {
        try {
            if (movimentoAlteracaoContratual.getFonteDespesaORC() != null) {
                setDespesaORC(movimentoAlteracaoContratual.getFonteDespesaORC().getDespesaORC());
                buscarSaldoFonteDespesaORC();
            }
            FacesUtil.executaJavaScript("dlgDotacao.show()");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarMovimento() {
        Util.validarCampos(movimentoAlteracaoContratual);
        ValidacaoException ve = new ValidacaoException();
        if (movimentoAlteracaoContratual.isOperacoesValor() && movimentoAlteracaoContratual.getFinalidade().isSupressao()) {
            if (movimentoAlteracaoContratual.getValorTotalItens().compareTo(contrato.getValorTotal()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Valor do movimento deve ser menor ou igual ao valor original do contrato " + Util.formataValor(contrato.getValorTotal()) + ".");
            }
            if (movimentoAlteracaoContratual.getValorTotalItens().compareTo(contrato.getSaldoAtualContrato()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Valor do movimento deve ser menor ou igual ao saldo atual do contrato " + Util.formataValor(contrato.getSaldoAtualContrato()) + ".");
            }
        }
        if (movimentoAlteracaoContratual.isOperacoesValor() && movimentoAlteracaoContratual.getItens().stream().noneMatch(MovimentoAlteracaoContratualItem::hasValorTotalMaiorQueZero)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar ao menos um item para continuar.");
        }
        ve.lancarException();
        validarMesmoMovimentoOperacao(ve);
        validarMovimentoPrazo(ve);
        validarMovimentoValor(ve);
        validarMovimentoTransferenciaFornecedor(ve);
        validarMovimentoTransferenciaUnidade(ve);
        validarMovimentoTransferenciaDotacao(ve);
    }

    private void validarMesmoMovimentoOperacao(ValidacaoException ve) {
        selecionado.getMovimentos().stream()
            .filter(mov -> mov.getOperacao().equals(movimentoAlteracaoContratual.getOperacao())
                && !mov.equals(movimentoAlteracaoContratual)
                && mov.getFinalidade().equals(movimentoAlteracaoContratual.getFinalidade()))
            .map(mov -> "O movimento com a operação " + movimentoAlteracaoContratual.getOperacao().getDescricao() + " já foi adicionado na lista.")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
    }

    private void validarMovimentoValor(ValidacaoException ve) {
        if (movimentoAlteracaoContratual.isOperacoesValor()
            && movimentoAlteracaoContratual.getFinalidade().isSupressao()
            && movimentoAlteracaoContratual.getMovimentoOrigemExecucaoContratoVO() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo origem da supressão deve ser informado.");
        }
        if (movimentoAlteracaoContratual.getOperacao().isRedimensionamentoObjeto()) {
            if (despesaORC == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Despesa Orçamentária deve ser informado.");
            }
            if (Strings.isNullOrEmpty(movimentoAlteracaoContratual.getObjeto())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Objeto deve ser informado.");
            }
        }
        ve.lancarException();
    }

    private void validarMovimentoTransferenciaFornecedor(ValidacaoException ve) {
        if (movimentoAlteracaoContratual.isTransferenciaFornecedor()) {
            if (movimentoAlteracaoContratual.getFornecedor() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor deve ser informado.");

            } else if (movimentoAlteracaoContratual.getFornecedor().equals(contrato.getContratado())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo fornecedor da trasnferência deve ser diferente do fornecedor do contrato.");
            }
            if (movimentoAlteracaoContratual.getResponsavelFornecedor() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo responsável pelo fornecedor deve ser informado.");

            } else if (movimentoAlteracaoContratual.getFornecedor().equals(contrato.getContratado())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo responsável pelo fornecedor da trasnferência deve ser diferente do responsável pelo fornecedor do contrato.");
            }
            ve.lancarException();
        }
    }

    private void validarMovimentoTransferenciaUnidade(ValidacaoException ve) {
        if (movimentoAlteracaoContratual.isTransferenciaUnidade()) {
            if (movimentoAlteracaoContratual.getUnidadeOrganizacional() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo unidade a transferir deve ser informado.");

            } else if (contrato.getUnidadeAdministrativa().getSubordinada().equals(movimentoAlteracaoContratual.getUnidadeOrganizacional())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido transferência para unidades iguais.");
            }
            ve.lancarException();
        }
    }

    private void validarMovimentoTransferenciaDotacao(ValidacaoException ve) {
        if (movimentoAlteracaoContratual.isTransferenciaDotacao()) {
            if (dotacaoSolicitacaoCompraSelecionada == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione uma dotação de origem para iniciar o processo de transferência.");
            }
            if (movimentoAlteracaoContratual.getFonteDespesaORC() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo dotação de destino deve ser informado.");

            } else if (dotacaoSolicitacaoCompraSelecionada.getFonteDespesaORC().equals(movimentoAlteracaoContratual.getFonteDespesaORC())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido transferência para fontes iguais.");

            } else if (movimentoAlteracaoContratual.getValor().compareTo(dotacaoSolicitacaoCompraSelecionada.getSaldo()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor à transferir deve ser menor ou igual ao saldo disponível");
            }
            for (MovimentoAlteracaoContratual mov : selecionado.getMovimentos()) {
                if (!mov.equals(movimentoAlteracaoContratual) && mov.getFonteDespesaORC().equals(movimentoAlteracaoContratual.getFonteDespesaORC())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("já existe uma operação de transferência para a fonte " + movimentoAlteracaoContratual.getFonteDespesaORC() + ".");
                }
            }
            ve.lancarException();
        }
    }

    private void validarMovimentoPrazo(ValidacaoException ve) {
        if (movimentoAlteracaoContratual.isOperacoesPrazo()) {
            if (Util.isNull(movimentoAlteracaoContratual.getInicioVigencia())
                && Util.isNull(movimentoAlteracaoContratual.getTerminoVigencia())
                && Util.isNull(movimentoAlteracaoContratual.getInicioExecucao())
                && Util.isNull(movimentoAlteracaoContratual.getTerminoExecucao())) {
                ve.adicionarMensagemDeCampoObrigatorio("Para movimento de prazo é necessário informar início/término de vigência ou execução.");
            }
            ve.lancarException();
            if (movimentoAlteracaoContratual.getFinalidade().isAcrescimo()
                && movimentoAlteracaoContratual.getTerminoVigencia() != null
                && DataUtil.dataSemHorario(movimentoAlteracaoContratual.getTerminoVigencia()).before(DataUtil.dataSemHorario(selecionado.getTerminoVigenciaAtual()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo término de vigência deve ser superior ao término de vigência atual do contrato.");

            } else if (movimentoAlteracaoContratual.getFinalidade().isAcrescimo()
                && movimentoAlteracaoContratual.getTerminoVigencia() != null
                && DataUtil.dataSemHorario(movimentoAlteracaoContratual.getTerminoVigencia()).before(DataUtil.dataSemHorario(movimentoAlteracaoContratual.getInicioVigencia()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O término de vigência deve ser posterior ao início de vigência.");

            } else if (movimentoAlteracaoContratual.getFinalidade().isSupressao()
                && movimentoAlteracaoContratual.getTerminoVigencia() != null
                && DataUtil.dataSemHorario(movimentoAlteracaoContratual.getTerminoVigencia()).after(DataUtil.dataSemHorario(contrato.getTerminoVigenciaAtual()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Término de Vigência deve ser anterior ao término de vigência atual do contrato.");

            } else if (movimentoAlteracaoContratual.getFinalidade().isAcrescimo()
                && movimentoAlteracaoContratual.getTerminoExecucao() != null
                && DataUtil.dataSemHorario(movimentoAlteracaoContratual.getTerminoExecucao()).before(DataUtil.dataSemHorario(contrato.getTerminoExecucaoAtual()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo término de execução deve ser superior ao término de execução atual do contrato.");

            } else if (movimentoAlteracaoContratual.getFinalidade().isAcrescimo()
                && movimentoAlteracaoContratual.getTerminoExecucao() != null
                && DataUtil.dataSemHorario(movimentoAlteracaoContratual.getTerminoExecucao()).before(DataUtil.dataSemHorario(movimentoAlteracaoContratual.getInicioExecucao()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O término de execução deve ser posterior ao início de execução.");

            } else if (movimentoAlteracaoContratual.getFinalidade().isSupressao()
                && movimentoAlteracaoContratual.getTerminoExecucao() != null
                && DataUtil.dataSemHorario(movimentoAlteracaoContratual.getTerminoExecucao()).after(DataUtil.dataSemHorario(contrato.getTerminoExecucaoAtual()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Término de execução deve ser anterior ao término de execução atual do contrato.");
            }
        }
        ve.lancarException();
    }

    private void validarPercentualParaObra(ValidacaoException ve) {

        if (contrato.isDeLicitacao() || contrato.isDeDispensaDeLicitacao()) {
            BigDecimal totalMov = selecionado.getValorTotal();

            BigDecimal valorPermitidoConstrucaoAmpliacao = contrato.getValorTotal().multiply(UM_QUARTO);
            BigDecimal valorPermitidoReforma = contrato.getValorTotal().multiply(METADE);

            SubTipoObjetoCompra subTipoObjetoCompra = contrato.getObjetoAdequado().getSolicitacaoMaterial().getSubTipoObjetoCompra();
            TipoObjetoCompra tipoObjetoCompra = contrato.getObjetoAdequado().getSolicitacaoMaterial().getTipoObjetoCompra();

            if (subTipoObjetoCompra != null && tipoObjetoCompra != null) {
                String msgVinteCincoPorcento = "O valor total dos movimentos ultrapassa o valor permitido para esse contrato que é de " + Util.formataValor(valorPermitidoConstrucaoAmpliacao)
                    + ", permitido para " + tipoObjetoCompra.getDescricao() + " (" + subTipoObjetoCompra.getDescricao() + ").";
                String msgCinquentaPorcento = "O valor total dos movimentos ultrapassa o valor permitido para esse contrato que é de " + Util.formataValor(valorPermitidoReforma)
                    + ", permitido para " + tipoObjetoCompra.getDescricao() + " (" + subTipoObjetoCompra.getDescricao() + ").";

                boolean isConstrucaoAmpliacao = SubTipoObjetoCompra.CONSTRUCAO.equals(subTipoObjetoCompra) || SubTipoObjetoCompra.AMPLIACAO.equals(subTipoObjetoCompra);
                boolean isReforma = SubTipoObjetoCompra.REFORMA.equals(subTipoObjetoCompra);
                boolean hasValorVinteCincoPorcentoMaiorZero = totalMov.compareTo(valorPermitidoReforma) > 0;

                if (tipoObjetoCompra.isObraInstalacoes() || tipoObjetoCompra.isServico()) {
                    if (isConstrucaoAmpliacao
                        && totalMov.compareTo(valorPermitidoConstrucaoAmpliacao) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(msgVinteCincoPorcento);
                    }
                    if (isReforma
                        && hasValorVinteCincoPorcentoMaiorZero) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(msgCinquentaPorcento);
                    }
                }
                if (tipoObjetoCompra.isMaterialPermanente() || tipoObjetoCompra.isMaterialConsumo()) {
                    if (hasValorVinteCincoPorcentoMaiorZero) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida(msgVinteCincoPorcento);
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void buscarSaldoFonteDespesaORC() {
        setSaldoFonteDespesaORC(BigDecimal.ZERO);
        try {
            SaldoFonteDespesaORC saldoFonteDespesaORC = facade.getSaldoFonteDespesaORCFacade().recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(
                movimentoAlteracaoContratual.getFonteDespesaORC(),
                selecionado.getDataLancamento(),
                movimentoAlteracaoContratual.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());

            if (saldoFonteDespesaORC != null) {
                setSaldoFonteDespesaORC(saldoFonteDespesaORC.getSaldoAtual());
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            logger.error("buscarSaldoFonteDespesaORC() ", e);
        }
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public MovimentoAlteracaoContratual getMovimentoAlteracaoContratual() {
        return movimentoAlteracaoContratual;
    }

    public void setMovimentoAlteracaoContratual(MovimentoAlteracaoContratual movimentoAlteracaoContratual) {
        this.movimentoAlteracaoContratual = movimentoAlteracaoContratual;
    }

    public List<AlteracaoContratual> getAlteracoesContratuais() {
        return alteracoesContratuais;
    }

    public void setAlteracoesContratuais(List<AlteracaoContratual> alteracoesContratuais) {
        this.alteracoesContratuais = alteracoesContratuais;
    }

    public List<VOExecucaoReservadoPorLicitacao> getVoReservasPorLicitacao() {
        return voReservasPorLicitacao;
    }

    public void setVoReservasPorLicitacao(List<VOExecucaoReservadoPorLicitacao> voReservasPorLicitacao) {
        this.voReservasPorLicitacao = voReservasPorLicitacao;
    }

    public List<SaldoItemContratoOrigemVO> getMovimentosItemOrigemSupressao() {
        return movimentosItemOrigemSupressao;
    }

    public void setMovimentosItemOrigemSupressao
        (List<SaldoItemContratoOrigemVO> movimentosItemOrigemSupressao) {
        this.movimentosItemOrigemSupressao = movimentosItemOrigemSupressao;
    }

    public List<Pessoa> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<Pessoa> fornecedores) {
        this.fornecedores = fornecedores;
    }

    public String geTextoInformativo() {
        if (movimentoAlteracaoContratual.getOperacao().isOutro()) {
            if (selecionado.isAditivo()) {
                return "Aditivo - Quando há o tombo de ata/contrato, a dobra.";
            }
            return "Apostilamento - Quando há necessidade de lançar uma informação adicional a ata/contrato.";
        }
        return movimentoAlteracaoContratual.getOperacao().getTextoInformativo();
    }

    public String getTextoInformativoTransferenciaUnidade() {
        return "A unidade atual do contrato terá sua vigência encerrada, dando início a nova unidade com vigência em: " + DataUtil.getDataFormatada(selecionado.getDataLancamento()) + ".";
    }

    public String getTextoAjudaOperacao() {
        return "Operações: " +
            "<ul>" +
            "<li><b>Dilação de Prazo</b>: é a prorrogação de um prazo processual.</li>" +

            "<li><b>Redimensionamento do Objeto</b>: Alteração legal na quantidade adquirida (ex: o limite de 25%).</li>" +

            "<li><b>Reajuste de Valores</b>: Reajuste legal através de índice, geralmente a correção da inflação pelo IPCA/IGPM já predefino na ata/contrato.</li>" +

            "<li><b>Reequilíbrio Financeiro</b>: Ajuste segundo um evento que não é a inflação, ou seja, que levou\n" +
            "a um desequilíbrio do valor ofertado (falta de matéria prima, fechamento de uma industria, nova\n" +
            "legislação sobre a produção e valores fiscais do item, etc. </li>" +

            "<li><b>Pré-execução</b>: É a rotina de praxe, a que o sistema sempre trabalhou, onde o aditivo/apostilamento vai\n" +
            "alterar um valor de um serviço/material que ainda vai ser prestado/fornecido.</li>" +

            "<li><b>Pós-execução</b>: É a nova funcionalidade para registrar um dilação do valor do item a\n" +
            "reembolsar o fornecedor/prestador quando esse item já foi entregue, executado, com\n" +
            "liquidação (entrada de material por compra) registrado anteriormente no sistema.</li>" +

            "<li><b>Não se aplica</b>: quando há o tombo de contrato, a dobra, por exemplo quando eu tenho um\n" +
            "serviço pago mensalmente por 12 meses, ai eu prorrogo o prazo por mais 12 meses e aumento\n" +
            "a quantidade de serviços por mais 12 mensalidades.</li>" +

            "<br><b>*</b> Lembrando que reajuste e reequilíbrio PÓS execução, é quando um produto/serviço já foi\n" +
            "executado (liquidado e pago) e nesse caso não haverá nova Requisição de compra e assim\n" +
            "não haverá nova entrada do material, pois o mesmo já entrou e até já pode ter sido utilizado\n" +
            "(saída de material)." +
            "</ul>";
    }

    public List<DotacaoAlteracaoContratualVO> getDotacoesSolicitacaoCompra() {
        return dotacoesSolicitacaoCompra;
    }

    public void setDotacoesSolicitacaoCompra(List<DotacaoAlteracaoContratualVO> dotacoesSolicitacaoCompra) {
        this.dotacoesSolicitacaoCompra = dotacoesSolicitacaoCompra;
    }

    public BigDecimal getSaldoFonteDespesaORC() {
        return saldoFonteDespesaORC;
    }

    public void setSaldoFonteDespesaORC(BigDecimal saldoFonteDespesaORC) {
        this.saldoFonteDespesaORC = saldoFonteDespesaORC;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public HierarquiaOrganizacional getHierarquiaTransferenciaUnidade() {
        return hierarquiaTransferenciaUnidade;
    }

    public void setHierarquiaTransferenciaUnidade(HierarquiaOrganizacional hierarquiaTransferenciaUnidade) {
        if (movimentoAlteracaoContratual != null && hierarquiaTransferenciaUnidade != null) {
            movimentoAlteracaoContratual.setUnidadeOrganizacional(hierarquiaTransferenciaUnidade.getSubordinada());
        }
        this.hierarquiaTransferenciaUnidade = hierarquiaTransferenciaUnidade;
    }

    public DotacaoAlteracaoContratualVO getDotacaoSolicitacaoCompraSelecionada() {
        return dotacaoSolicitacaoCompraSelecionada;
    }

    public void setDotacaoSolicitacaoCompraSelecionada(DotacaoAlteracaoContratualVO dotacaoSolicitacaoCompraSelecionada) {
        this.dotacaoSolicitacaoCompraSelecionada = dotacaoSolicitacaoCompraSelecionada;
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public HierarquiaOrganizacional getHierarquiaProcesso() {
        return hierarquiaProcesso;
    }

    public void setHierarquiaProcesso(HierarquiaOrganizacional hierarquiaProcesso) {
        this.hierarquiaProcesso = hierarquiaProcesso;
    }
}
