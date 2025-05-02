package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoAjusteBemMovelFacade;
import br.com.webpublico.negocios.CabecalhoRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by mga on 06/03/2018.
 */

public class SolicitacaoAjusteBemMovelSuperControlador extends PrettyControlador<SolicitacaoAjusteBemMovel> implements Serializable, CRUD {

    @EJB
    private SolicitacaoAjusteBemMovelFacade facade;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private FiltroPesquisaBem filtro;
    private LoteReducaoValorBem loteReducaoValorBem;
    private Integer indexAba;
    private BigDecimal quantidadeMeses;

    public SolicitacaoAjusteBemMovelSuperControlador() {
        super(SolicitacaoAjusteBemMovel.class);
    }

    @Override
    public String getCaminhoPadrao() {
        if (selecionado.getTipoAjusteBemMovel().isOriginal()) {
            return "/solicitacao-ajuste-bem-movel-original/";
        } else if (selecionado.getTipoAjusteBemMovel().isDepreciacao()) {
            return "/solicitacao-ajuste-bem-movel-depreciacao/";
        } else {
            return "/solicitacao-ajuste-bem-movel-amortizacao/";
        }
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
    public void ver() {
        selecionado = facade.recuperarArquivo(getId());
        operacao = Operacoes.VER;
    }

    @Override
    public void editar() {
        try {
            selecionado = facade.recuperarArquivo(getId());
            operacao = Operacoes.EDITAR;
            if (!selecionado.isSolicitacaoEmElaboracao()) {
                FacesUtil.addOperacaoNaoPermitida("A solicitação encontra-se " + selecionado.getSituacao().getDescricao() + ", não é permitido realizar alterações.");
                redirecionarParaVer();
            }
            novoAssistenteMovimentacao();
            recuperarConfiguracaoMovimentacaoBem();
            novoFiltroPesquisa();
            definirUnidadePesquisaBem();
            inicializarListas();
            indexAba = 1;
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataSolicitacao(getDataOperacao());
        selecionado.setSituacao(SituacaoMovimentoAdministrativo.EM_ELABORACAO);
        indexAba = 0;
        novoAssistenteMovimentacao();
        inicializarListas();
        novoFiltroPesquisa();
    }

    public void inicializarListas() {
        assistenteMovimentacao.setBensSelecionadosVo(new ArrayList<BemVo>());
        assistenteMovimentacao.setBensDisponiveisVo(new ArrayList<BemVo>());
    }


    public void buscarUnidadePesquisaBem() {
        definirUnidadePesquisaBem();
        inicializarListas();
    }

    public void recuperarConfiguracaoMovimentacaoBem() {
        try {
            if (selecionado.getOperacaoAjusteBemMovel() != null) {
                ConfigMovimentacaoBem configMovimentacaoBem = facade.recuperarConfiguracaoMovimentacaoBem(selecionado);
                if (configMovimentacaoBem != null) {
                    assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
                }
            }
        } catch (ExcecaoNegocioGenerica ex) {
            selecionado.setOperacaoAjusteBemMovel(null);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (assistenteMovimentacao.getConfigMovimentacaoBem() != null) {
            assistenteMovimentacao.getConfigMovimentacaoBem().validarDatasMovimentacao(selecionado.getDataSolicitacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public void definirUnidadePesquisaBem() {
        if (filtro.getHierarquiaAdministrativa() != null) {
            filtro.setUnidadePesquisaBem(filtro.getHierarquiaAdministrativa().getSubordinada());
            if (filtro.getHierarquiaAdministrativaFilha() != null) {
                filtro.setUnidadePesquisaBem(filtro.getHierarquiaAdministrativaFilha().getSubordinada());
            }
        }
    }

    public void definirUnidadeFilhaComoNull() {
        if (filtro.getHierarquiaAdministrativa() != null) {
            filtro.setHierarquiaAdministrativaFilha(null);
            filtro.setUnidadePesquisaBem(filtro.getHierarquiaAdministrativa().getSubordinada());
        }
    }

    public void validarValorAjuste(BemVo bem) {
        ValidacaoException ve = new ValidacaoException();
        if (bem.getValorAjuste() == null || bem.getValorAjuste().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do ajuste deve informado com um valor maior que zero.");
            bem.setValorAjuste(BigDecimal.ZERO);
        }
        ve.lancarException();
    }

    public void consultarFutureSalvar() {
        if (assistenteMovimentacao.getFutureSalvar() != null && assistenteMovimentacao.getFutureSalvar().isDone()) {
            try {
                AssistenteMovimentacaoBens assistente = assistenteMovimentacao.getFutureSalvar().get();
                if (assistente.getSelecionado() != null) {
                    selecionado = (SolicitacaoAjusteBemMovel) assistente.getSelecionado();
                    desbloquearMovimentacaoSingleton();
                    FacesUtil.executaJavaScript("finalizaFuture()");
                }
            } catch (Exception ex) {
                assistenteMovimentacao.setBloquearAcoesTela(true);
                assistenteMovimentacao.descobrirETratarException(ex);
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                assistenteMovimentacao.setFutureSalvar(null);
                desbloquearMovimentacaoSingleton();
            }
        }
    }

    public void finalizarFutureSalvar() {
        assistenteMovimentacao.setFutureSalvar(null);
        redirecionarParaVer();
        FacesUtil.addOperacaoRealizada(isOperacaoVer() ? "Registro concluído com sucesso." : getMensagemSucessoAoSalvar());
    }

    public List<HierarquiaOrganizacional> completarHierarquiaFilhasAdministrativa(String parte) {
        if (filtro.getHierarquiaAdministrativa() != null) {
            return facade.getHierarquiaOrganizacionalFacade().listaHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativa(
                filtro.getHierarquiaAdministrativa(),
                selecionado.getUsuarioSistema(),
                selecionado.getDataSolicitacao(),
                parte.trim());
        }
        return new ArrayList<>();
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (assistenteMovimentacao.getFuturePesquisaBemVo() != null && assistenteMovimentacao.getFuturePesquisaBemVo().isDone()) {
            List<BemVo> bens = assistenteMovimentacao.getFuturePesquisaBemVo().get();
            assistenteMovimentacao.setBensDisponiveisVo(bens);
            filtrarBensDepreciacao(bens);
            if (isOperacaoVer()) {
                assistenteMovimentacao.setBensMovimentadosVo(bens);
            }
            assistenteMovimentacao.setFuturePesquisaBemVo(null);
            FacesUtil.executaJavaScript("finalizarPesquisa()");
        }
    }

    private void filtrarBensDepreciacao(List<BemVo> bens) {
        if (loteReducaoValorBem != null) {
            assistenteMovimentacao.setBensDisponiveisVo(Lists.newArrayList());
            List<Bem> bensDepreciacao = facade.getReducaoValorBemFacade().buscarBensReducaoValorBem(loteReducaoValorBem, filtro.getGrupoBem());
            bensDepreciacao.forEach(bemDep -> bens.stream()
                .filter(bemPesq -> bemPesq.getBem().equals(bemDep))
                .forEachOrdered(bemPesq -> assistenteMovimentacao.getBensDisponiveisVo().add(bemPesq)));
            }
        }

    public boolean hasBensSelecinado() {
        return assistenteMovimentacao.hasBensSelecionadoVo();
    }

    private void selecionarMesmoBemAoPesquisar() {
        if (isOperacaoEditar()
            && selecionado.getUnidadeAdministrativa() != null
            && selecionado.getUnidadeAdministrativa().equals(filtro.getHierarquiaAdministrativa().getSubordinada())) {

            assistenteMovimentacao.setBensSelecionadosVo(new ArrayList<BemVo>());
            if (assistenteMovimentacao.hasBensMovimentadosVo()) {
                assistenteMovimentacao.getBensSelecionadosVo().addAll(assistenteMovimentacao.getBensMovimentadosVo());
                assistenteMovimentacao.getBensDisponiveisVo().addAll(assistenteMovimentacao.getBensMovimentadosVo());
            }
        }
        Collections.sort(assistenteMovimentacao.getBensDisponiveisVo());
        Collections.sort(assistenteMovimentacao.getBensSelecionadosVo());
    }

    public void finalizarPesquisa() {
        try {
            selecionarMesmoBemAoPesquisar();
            FacesUtil.executaJavaScript("atualizaFormulario()");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public List<SelectItem> getHierarquiasOrcamentariaPorAdministrativa() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (filtro != null && filtro.getHierarquiaAdministrativa() != null && filtro.getHierarquiaAdministrativa().getSubordinada() != null) {
            UnidadeOrganizacional unidadeAdm = filtro.getHierarquiaAdministrativa().getSubordinada();
            if (filtro.getHierarquiaAdministrativaFilha() != null) {
                unidadeAdm = filtro.getHierarquiaAdministrativaFilha().getSubordinada();
            }
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(
                unidadeAdm,
                getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    @Override
    public void excluir() {
        try {
            recuperarConfiguracaoMovimentacaoBem();
            facade.remover(selecionado, assistenteMovimentacao);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void concluirSolicitacao() {
        try {
            validarDataLancamentoAndDataOperacaoBem();
            recuperarConfiguracaoMovimentacaoBem();
            bloquearMovimentacaoBens();
            assistenteMovimentacao.setFutureSalvar(facade.concluirSolicitacao(selecionado, assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciaFuture()");
            desbloquearMovimentacaoSingleton();
        } catch (MovimentacaoBemException ex) {
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            assistenteMovimentacao.descobrirETratarException(e);
            desbloquearMovimentacaoSingleton();
        }
    }

    @Override
    public void salvar() {
        try {
            realizarValidacoes();
            adicionarUnidadeAdministrativa();
            bloquearMovimentacaoBens();
            assistenteMovimentacao.setFutureSalvar(facade.salvarRetornando(selecionado, assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciaFuture()");
        } catch (MovimentacaoBemException ex) {
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    public void pesquisarBens() {
        try {
            if (isOperacaoVer()) {
                novoAssistenteMovimentacao();
                assistenteMovimentacao.setFuturePesquisaBemVo(facade.pesquisarAssincronoBensSolicitacao(selecionado, assistenteMovimentacao));
            } else {
                validarDataLancamentoAndDataOperacaoBem();
                validarHierarquiaAdministrativa();
                if (verificarBensBloqueadoSingletonAoPesquisar()) return;
                inicializarListas();
                assistenteMovimentacao.setFuturePesquisaBemVo(facade.pesquisarBens(filtro, assistenteMovimentacao));
            }
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (MovimentacaoBemException me) {
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void calcularValorFinal(BemVo bemVo) {
        try {
            validarValorAjuste(bemVo);
            BigDecimal valorFinal = bemVo.calcularValorAjusteFinal(selecionado.getOperacaoAjusteBemMovel());

            ValidacaoException ve = new ValidacaoException();
            validarValorAjuste(bemVo, valorFinal, ve);
            ve.lancarException();

            bemVo.setValorAjusteFinal(valorFinal);
            atribuirValorAjustadoParaBemSelecionado(bemVo);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void aplicarCalculoDepreciacao() {
        try {
            validarAplicacaoCalculo();
            ValidacaoException ve = new ValidacaoException();
            assistenteMovimentacao.setBensSelecionadosVo(new ArrayList<BemVo>());
            for (BemVo bemVo : assistenteMovimentacao.getBensDisponiveisVo()) {

                if (verificarBemResidual(bemVo)) continue;

                BigDecimal valorReducao = bemVo.getBem().calcularReducaoValorBem(bemVo.getEstadoResultante(), bemVo.getTipoReducao());
                if (valorReducao.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal valorReducaoCalculado = quantidadeMeses.multiply(valorReducao);
                    bemVo.setValorAjuste(valorReducaoCalculado);

                    BigDecimal valorReducaoFinal = bemVo.calcularValorAjusteFinal(selecionado.getOperacaoAjusteBemMovel());
                    validarValorAjuste(bemVo, valorReducaoFinal, ve);

                    if (!ve.temMensagens()) {
                        bemVo.setValorAjusteFinal(valorReducaoFinal);
                        assistenteMovimentacao.getBensSelecionadosVo().add(bemVo);
                        atribuirValorAjustadoParaBemSelecionado(bemVo);
                    }
                }
            }
            ve.lancarException();
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("atualizaFormulario()");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private boolean verificarBemResidual(BemVo bemVo) {
        BigDecimal valorMinimoRedutivel = bemVo.getBem().getValorOriginal().multiply(bemVo.getTipoReducao().getValorResidual().divide(new BigDecimal("100"))).setScale(2, RoundingMode.HALF_UP);
        if (bemVo.getBem().getValorLiquido().compareTo(valorMinimoRedutivel) <= 0) {
            bemVo.setResidual(true);
            return true;
        }
        return false;
    }

    private void validarAplicacaoCalculo() {
        ValidacaoException ve = new ValidacaoException();
        if (!assistenteMovimentacao.hasBensDisponivel()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Pesquise os bens para aplicar o cálculo.");
        }
        if (quantidadeMeses == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo quantidade de meses deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarValorAjuste(BemVo bemVo, BigDecimal valorAcumuladoFinal, ValidacaoException ve) {
        if (selecionado.getTipoAjusteBemMovel().isDepreciacao()) {
            validarAjsuteValorDepreciacao(bemVo, valorAcumuladoFinal, ve);
        } else if (selecionado.getTipoAjusteBemMovel().isAmortizacao()) {
            validarAjusteValorAmortizacao(bemVo, valorAcumuladoFinal, ve);
        } else {
            validarAjusteValorOriginal(bemVo, valorAcumuladoFinal, ve);
        }
    }

    public void validarAjsuteValorDepreciacao(BemVo bemVo, BigDecimal valorDepreciacaoFinal, ValidacaoException ve) {
        if (selecionado.getOperacaoAjusteBemMovel().isAumentativoDepreciacao()) {

            if (valorDepreciacaoFinal.compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor depreciação final para bem: " + bemVo.getIdentificacao()
                    + " ficará negativo em <b>" + Util.formataValor(valorDepreciacaoFinal) + "</b>" +
                    "  se reajustado para: <b>" + Util.formataValor(bemVo.getValorAjuste()) + "</b>.");
            }

            BigDecimal novoValorLiquido = bemVo.getBem().getValorOriginal().subtract(valorDepreciacaoFinal);
            if (novoValorLiquido.compareTo(bemVo.getBem().getValorOriginal()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor líquido do bem: " + bemVo.getIdentificacao()
                    + ", deve ser menor ou igual ao valor original do bem. "
                    + "<b>" + Util.formataValor(bemVo.getBem().getValorOriginal()) + "</b>.");
            }

            if (novoValorLiquido.compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O novo valor líquido de: <b>" + Util.formataValor(novoValorLiquido) + "</b>"
                    + " para o bem " + bemVo.getIdentificacao() + " deve ser maior que zero. ");
            }
        } else {
            if (valorDepreciacaoFinal.compareTo(bemVo.getBem().getValorOriginal()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor depreciação final para bem: " + bemVo.getIdentificacao()
                    + " não pode ser maior que valor original de <b>" + Util.formataValor(bemVo.getBem().getValorOriginal()) + "</b>");
            }
        }
        if (ve.temMensagens()) {
            bemVo.setValorAjuste(BigDecimal.ZERO);
            bemVo.setValorAjusteFinal(bemVo.getBem().getValorAcumuladoDaDepreciacao());
        }
    }

    public void validarAjusteValorAmortizacao(BemVo bemVo, BigDecimal valorAmortizacaoFinal, ValidacaoException ve) {
        if (selecionado.getOperacaoAjusteBemMovel().isAumentativoAmortizacao()) {
            if (valorAmortizacaoFinal.compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor amortização final para bem: " + bemVo.getIdentificacao()
                    + " ficará negativo em <b>" + Util.formataValor(valorAmortizacaoFinal) + "</b>" +
                    "  se reajustado para: <b>" + Util.formataValor(bemVo.getValorAjuste()) + "</b>.");
            }

            BigDecimal novoValorLiquido = bemVo.getBem().getValorLiquido().add(bemVo.getValorAjuste());
            if (novoValorLiquido.compareTo(bemVo.getBem().getValorOriginal()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do ajuste para o bem: " + bemVo.getIdentificacao() +
                    " deve ser maior que zero, menor ou igual ao valor da amortização: <b>" + Util.formataValor(bemVo.getBem().getValorAcumuladoDaAmortizacao()) + "</b>" +
                    " e resultando em um valor líquido <b>" + Util.formataValor(novoValorLiquido) + "</b> maior ou igual a zero.");
            }

            if (novoValorLiquido.compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O novo valor líquido de: <b>" + Util.formataValor(novoValorLiquido) + "</b>"
                    + " para o bem " + bemVo.getIdentificacao() + " deve ser maior que zero. ");
            }
        } else {
            if (valorAmortizacaoFinal.compareTo(bemVo.getBem().getValorOriginal()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor amortização final para bem: " + bemVo.getIdentificacao()
                    + " não pode ser maior que valor original de <b>" + Util.formataValor(bemVo.getBem().getValorOriginal()) + "</b>");
            }

            BigDecimal novoValorLiquido = bemVo.getBem().getValorLiquido().subtract(bemVo.getValorAjuste());
            if (novoValorLiquido.compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O novo valor líquido de: <b>" + Util.formataValor(novoValorLiquido) + "</b>"
                    + " para o bem " + bemVo.getIdentificacao() + " deve ser maior que zero. ");
            }
        }
        if (ve.temMensagens()) {
            bemVo.setValorAjuste(BigDecimal.ZERO);
            bemVo.setValorAjusteFinal(bemVo.getBem().getValorAcumuladoDaAmortizacao());
        }
    }

    public void validarAjusteValorOriginal(BemVo bemVo, BigDecimal valorOriginalFinal, ValidacaoException ve) {

        if (selecionado.getOperacaoAjusteBemMovel().isDiminutivoOriginal()) {
            if (valorOriginalFinal.compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor original final para bem: " + bemVo.getIdentificacao()
                    + " ficará negativo em <b>" + Util.formataValor(valorOriginalFinal) + "</b>" +
                    "  se reajustado para: <b>" + Util.formataValor(bemVo.getValorAjuste()) + "</b>.");
            }

            BigDecimal novoValorLiquido = bemVo.getBem().getValorLiquido().subtract(bemVo.getValorAjuste());
            if (novoValorLiquido.compareTo(BigDecimal.ZERO) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do ajuste para o bem: " + bemVo.getIdentificacao() +
                    " deve ser menor ou igual ao valor original: <b>" + Util.formataValor(bemVo.getBem().getValorOriginal()) + "</b>" +
                    " e resultando em um valor líquido <b>" + Util.formataValor(novoValorLiquido) + "</b> maior ou igual a zero.");

            }
        }
        if (ve.temMensagens()) {
            bemVo.setValorAjuste(BigDecimal.ZERO);
            bemVo.setValorAjusteFinal(bemVo.getBem().getValorOriginal());
        }
    }

    private boolean verificarBensBloqueadoSingletonAoPesquisar() {
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistenteMovimentacao)) {
            if (isOperacaoNovo()) {
                FacesUtil.executaJavaScript("aguarde.hide()");
                FacesUtil.atualizarComponente("Formulario");
                assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
            } else {
                FacesUtil.addOperacaoNaoPermitida(assistenteMovimentacao.getMensagens().get(0));
                redireciona();
            }
        }
        return false;
    }

    private void bloquearMovimentacaoBens() {
        if (!isOperacaoNovo()) {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(SolicitacaoAjusteBemMovel.class, selecionado.getUnidadeAdministrativa(), assistenteMovimentacao);
        }
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistenteMovimentacao)) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
        }
        if (isOperacaoNovo() && !assistenteMovimentacao.hasInconsistencias()) {
            facade.getConfigMovimentacaoBemFacade().verificarBensSelecionadosSeDisponivelConfiguracaoBloqueio(assistenteMovimentacao);
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistenteMovimentacao.lancarMensagemBensBloqueioSingleton();
        }
        if (!isOperacaoVer()) {
            for (BemVo bemVo : assistenteMovimentacao.getBensSelecionadosVo()) {
                facade.getSingletonBloqueioPatrimonio().bloquearBem(bemVo.getBem(), assistenteMovimentacao);
            }
        }
    }

    private void desbloquearMovimentacaoSingleton() {
        if (selecionado.getUnidadeAdministrativa() != null) {
            facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistenteMovimentacao, SolicitacaoAjusteBemMovel.class);
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    private void realizarValidacoes() {
        selecionado.realizarValidacoes();
        validarHierarquiaAdministrativa();
        validarItensSelecionados();
        validarDataLancamentoAndDataOperacaoBem();
    }

    private void adicionarUnidadeAdministrativa() {
        if (filtro.getHierarquiaAdministrativa() != null) {
            selecionado.setUnidadeAdministrativa(filtro.getHierarquiaAdministrativa().getSubordinada());
        }
    }

    private void novoAssistenteMovimentacao() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataSolicitacao(), operacao);
        assistenteMovimentacao.setSelecionado(selecionado);
        assistenteMovimentacao.setTipoEventoBem(TipoEventoBem.SOLICITACAO_ALTERACAO_CONSERVACAO_BEM);
        if (selecionado.getUnidadeAdministrativa() != null) {
            assistenteMovimentacao.setUnidadeOrganizacional(selecionado.getUnidadeAdministrativa());
        }
    }

    private void validarHierarquiaAdministrativa() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getOperacaoAjusteBemMovel() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Operação deve ser informado.");
        }
        if (filtro.getHierarquiaAdministrativa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarItensSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        if (!hasBensSelecinado()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos um bem para continuar a operação.");
        } else {
            for (BemVo bemVo : assistenteMovimentacao.getBensSelecionadosVo()) {
                if (assistenteMovimentacao.bemSelecionado(bemVo) && bemVo.getValorAjuste().compareTo(BigDecimal.ZERO) <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi ajustado o valor para o bemVo: " + bemVo.getBem() + ". O valor deve ser maior que zero.");
                }
            }
        }
        ve.lancarException();
    }

    public Boolean mostrarBotaoSelecionarTodos() {
        try {
            return assistenteMovimentacao.getBensDisponiveisVo().size() != assistenteMovimentacao.getBensSelecionadosVo().size();
        } catch (NullPointerException ex) {
            return Boolean.FALSE;
        }
    }

    public void desmarcarTodos() {
        for (BemVo bemVo : assistenteMovimentacao.getBensSelecionadosVo()) {
            bemVo.setValorAjuste(BigDecimal.ZERO);
            bemVo.setValorAjusteFinal(bemVo.getValorAjusteInicial());
        }
        assistenteMovimentacao.getBensSelecionadosVo().clear();
    }

    public void desmarcarItem(BemVo bemVo) {
        bemVo.setValorAjusteFinal(bemVo.getValorAjusteInicial());
        bemVo.setValorAjuste(BigDecimal.ZERO);
        assistenteMovimentacao.getBensSelecionadosVo().remove(bemVo);
    }

    public void selecionarItem(BemVo bem) {
        assistenteMovimentacao.getBensSelecionadosVo().add(bem);
    }

    public void selecionarTodos() {
        try {
            desmarcarTodos();
            assistenteMovimentacao.getBensSelecionadosVo().addAll(assistenteMovimentacao.getBensDisponiveisVo());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public List<SelectItem> getOperacoesAjuste() {
        if (selecionado.getTipoAjusteBemMovel() == null) {
            return Lists.newArrayList();
        }
        return Util.getListSelectItem(OperacaoAjusteBemMovel.retornaOperacoesPorTipoAjuste(selecionado.getTipoAjusteBemMovel()));
    }

    public void atribuirValorAjustadoParaBemSelecionado(BemVo bemVo) {
        for (BemVo bemSelecionado : assistenteMovimentacao.getBensSelecionadosVo()) {
            if (bemVo.equals(bemSelecionado)) {
                bemSelecionado.setValorAjuste(bemVo.getValorAjuste());
                bemSelecionado.setValorAjusteFinal(bemVo.getValorAjusteFinal());
            }
        }
    }

    public BigDecimal getValorlTotalInicial() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteMovimentacao != null) {
            List<BemVo> bens = isOperacaoVer() ? assistenteMovimentacao.getBensMovimentadosVo() : assistenteMovimentacao.getBensSelecionadosVo();
            for (BemVo bemVo : bens) {
                total = total.add(bemVo.getValorAjusteInicial());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAjuste() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteMovimentacao != null) {
            List<BemVo> bens = isOperacaoVer() ? assistenteMovimentacao.getBensMovimentadosVo() : assistenteMovimentacao.getBensSelecionadosVo();
            for (BemVo bemVo : bens) {
                total = total.add(bemVo.getValorAjuste());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalFinal() {
        BigDecimal total = BigDecimal.ZERO;
        if (assistenteMovimentacao != null) {
            List<BemVo> bens = isOperacaoVer() ? assistenteMovimentacao.getBensMovimentadosVo() : assistenteMovimentacao.getBensSelecionadosVo();
            for (BemVo bemVo : bens) {
                total = total.add(bemVo.getValorAjusteFinal());
            }
        }
        return total;
    }

    private void novoFiltroPesquisa() {
        filtro = new FiltroPesquisaBem(TipoBem.MOVEIS, selecionado);
        if (selecionado.getUnidadeAdministrativa() != null) {
            filtro.setHierarquiaAdministrativa(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                selecionado.getUnidadeAdministrativa(),
                selecionado.getDataSolicitacao()));
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            String nomeRelatorio = "RELATÓRIO DE SOLICITAÇÃO DE AJUSTE DE BENS MÓVEIS - " + selecionado.getTipoAjusteBemMovel().getDescricao().toUpperCase();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + facade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("SOLICITACAO_REJEITADA", selecionado.isSolicitacaoRejeitada());
            dto.adicionarParametro("TIPO_AJUSTE", selecionado.getTipoAjusteBemMovel().getDescricao());
            dto.adicionarParametro("operacaoAjusteBemMovelDTO", selecionado.getOperacaoAjusteBemMovel().getToDto());
            dto.adicionarParametro("idSolicitacaoAjusteBemMovel", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/solicitacao-ajuste-bem-movel/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<LoteReducaoValorBem> completarReducaoValorBem(String parte) {
        return facade.getReducaoValorBemFacade().buscarLoteReducaoValorBem(parte.trim());
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteMovimentacao) {
        this.assistenteMovimentacao = assistenteMovimentacao;
    }

    public FiltroPesquisaBem getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroPesquisaBem filtro) {
        this.filtro = filtro;
    }

    public Integer getIndexAba() {
        return indexAba;
    }

    public void setIndexAba(Integer indexAba) {
        this.indexAba = indexAba;
    }

    public BigDecimal getQuantidadeMeses() {
        return quantidadeMeses;
    }

    public void setQuantidadeMeses(BigDecimal quantidadeMeses) {
        this.quantidadeMeses = quantidadeMeses;
    }

    public LoteReducaoValorBem getLoteReducaoValorBem() {
        return loteReducaoValorBem;
    }

    public void setLoteReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem) {
        this.loteReducaoValorBem = loteReducaoValorBem;
    }
}
