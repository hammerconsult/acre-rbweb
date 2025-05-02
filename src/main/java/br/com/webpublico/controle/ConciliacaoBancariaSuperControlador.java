package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPendenciaConciliacao;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoOperacaoConcilicaoBancaria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.LancamentoConciliacaoBancariaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 01/08/17.
 */
public abstract class ConciliacaoBancariaSuperControlador extends AbstractReport implements Serializable {

    @EJB
    private LancamentoConciliacaoBancariaFacade facade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    protected SistemaControlador sistemaControlador;
    protected Conciliacao conciliacao;
    protected LancamentoConciliacaoBancaria lancamentoConciliacaoBancaria;
    protected SaldoConstContaBancaria saldoConstContaBancaria;
    protected List<LancamentoConciliacaoBancaria> pendencias;
    protected List<SaldoConstContaBancaria> historicosSaldoConstante;
    protected Date dataLancamento;
    protected FiltroPendenciaConciliacao filtroPendencia;

    public void novo() {
        filtroPendencia = new FiltroPendenciaConciliacao();
        lancamentoConciliacaoBancaria = new LancamentoConciliacaoBancaria();
        saldoConstContaBancaria = new SaldoConstContaBancaria();
        if (!recuperouDependenciasDaSessao()) {
            conciliacao = new Conciliacao();
            conciliacao.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
            conciliacao.setData(sistemaControlador.getDataOperacao());
        }
        dataLancamento = sistemaControlador.getDataOperacao();
    }

    public List<LancamentoConciliacaoBancaria> getPendencias() {
        return pendencias;
    }

    public void setPendencias(List<LancamentoConciliacaoBancaria> pendencias) {
        this.pendencias = pendencias;
    }

    public List<SelectItem> getTipoOperacaoConcilicaoBancaria() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (TipoOperacaoConcilicaoBancaria tipo : TipoOperacaoConcilicaoBancaria.values()) {
            retorno.add(new SelectItem(tipo, tipo.getNumero() + " - " + tipo.getDescricao()));
        }
        return Util.ordenaSelectItem(retorno);
    }

    public List<SelectItem> getTiposConciliados() {
        return Util.getListSelectItemSemCampoVazio(FiltroPendenciaConciliacao.Conciliado.values(), false);
    }

    public List<TipoConciliacao> completarTipoConciliacao(String parte) {
        return facade.getTipoConciliacaoFacade().listaFiltrando(parte.trim(), "numero", "descricao");
    }

    public List<ContaBancariaEntidade> completarContaBancariaEntidade(String parte) {
        try {
            return facade.getContaBancariaEntidadeFacade().buscarContasBancariasPorUnidade(
                parte.trim(),
                sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(),
                sistemaControlador.getExercicioCorrente());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Conta bancária não encontrada.");
        }
        return new ArrayList<>();
    }

    public List<SubConta> completarContaFinanceira(String parte) {
        if (conciliacao.getContaBancaria() != null && conciliacao.getContaBancaria().getId() != null) {
            return facade.getSubContaFacade().listaPorContaBancariaEntidadeDoUsuarioLogadoOuTodas(
                parte.trim(),
                conciliacao.getContaBancaria(),
                sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(),
                sistemaControlador.getExercicioCorrente(),
                sistemaControlador.getUsuarioCorrente(),
                conciliacao.getData());
        }
        return facade.getSubContaFacade().listaTodas(parte.trim());
    }

    public List<SubConta> completarContaFinanceiraFiltro(String parte) {
        if (conciliacao.getContaBancaria() != null) {
            return facade.getSubContaFacade().listaPorContaBancariaEntidade(
                parte.trim(),
                conciliacao.getContaBancaria());
        }
        return new ArrayList<>();
    }

    public void recuperarPorContaBancaria() {
        if (conciliacao.getContaBancaria() != null && conciliacao.getData() != null) {
            buscarUltimoSaldo();
            iniciarLancamentoPendenciaAndSaldoConstante();
            preencherFiltrosPendencia();
            buscarPendencias();
        }
    }

    public void preencherFiltrosPendencia() {
        filtroPendencia.setDataInicial(null);
        filtroPendencia.setDataFinal(null);
        filtroPendencia.setContaBancariaEntidade(conciliacao.getContaBancaria());
        filtroPendencia.setContaFinanceira(null);
        filtroPendencia.setOperacaoConciliacao(null);
        filtroPendencia.setTipoConciliacao(null);
        filtroPendencia.setNumero(null);
        filtroPendencia.setDataConciliacaoInicial(null);
        filtroPendencia.setDataConciliacaoFinal(null);
        filtroPendencia.setValor(null);
        filtroPendencia.setVisualizarPendenciasBaixadas(Boolean.FALSE);
        filtroPendencia.setConciliado(FiltroPendenciaConciliacao.Conciliado.NAO);
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        validarDataInicialEFinal(ve, filtroPendencia.getDataInicial(),
            filtroPendencia.getDataFinal(),
            "A data inicial deve ser anterior à data de conciliação (" + DataUtil.getDataFormatada(conciliacao.getData()) + ")",
            "A data final deve ser anterior à data de conciliação (" + DataUtil.getDataFormatada(conciliacao.getData()) + ")",
            "O campo data final deve ser superior ou igual ao campo data inicial.");
        validarDataInicialEFinal(ve, filtroPendencia.getDataConciliacaoInicial(),
            filtroPendencia.getDataConciliacaoFinal(),
            "A data de conciliação inicial deve ser anterior à data de conciliação (" + DataUtil.getDataFormatada(conciliacao.getData()) + ")",
            "A data de conciliação final deve ser anterior à data de conciliação (" + DataUtil.getDataFormatada(conciliacao.getData()) + ")",
            "O campo data de conciliação final deve ser superior ou igual ao campo data de conciliação inicial.");
        ve.lancarException();
    }

    private void validarDataInicialEFinal(ValidacaoException ve, Date dataInicial, Date dataFinal, String mensagemDataInicial, String mensagemDataFinal, String validacaoDatas) {
        if (dataInicial != null &&
            dataInicial.after(conciliacao.getData())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagemDataInicial);
        }
        if (dataFinal != null &&
            dataFinal.after(conciliacao.getData())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagemDataFinal);
        }
        if (dataInicial != null &&
            dataFinal != null &&
            dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(validacaoDatas);
        }
    }

    public void buscarPendenciasConciliada() {
        pesquisarPendencias();
    }

    public void pesquisarPendencias() {
        try {
            validarFiltros();
            buscarPendencias();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void recuperarMovimentosPorData() {
        try {
            Exercicio exercicio = getExercicioFacade().getExercicioPorAno(Integer.valueOf(Util.getAnoDaData(conciliacao.getData())));
            if (!facade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/conciliacao/movimentobancario/edita.xhtml", conciliacao.getData(), conciliacao.getUnidadeOrganizacional(), exercicio)) {
                recuperarPorContaBancaria();
                dataLancamento = conciliacao.getData();
            } else {
                conciliacao.setData(dataLancamento);
                FacesUtil.addOperacaoNaoRealizada("A data selecionada está fora do período fase.");
            }
        } catch (Exception ex) {
            conciliacao.setData(dataLancamento);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public void iniciarLancamentoPendenciaAndSaldoConstante() {
        lancamentoConciliacaoBancaria.setData(conciliacao.getData());
        lancamentoConciliacaoBancaria.setUnidadeOrganizacional(conciliacao.getUnidadeOrganizacional());
    }

    public void buscarHistoricoSaldo() {
        if (conciliacao.getContaBancaria() != null && conciliacao.getData() != null) {
            historicosSaldoConstante = facade.getSaldoConstContaBancariaFacade().buscarHistoricoSaldoConstante(conciliacao.getData(), conciliacao.getContaBancaria());
        }
    }

    private void buscarUltimoSaldo() {
        if (conciliacao.getContaBancaria() != null && conciliacao.getData() != null) {
            SaldoConstContaBancaria saldoConstante = facade.getSaldoConstContaBancariaFacade().recuperarSaldoDoDia(conciliacao.getData(), conciliacao.getContaBancaria());
            if (saldoConstante == null) {
                saldoConstContaBancaria = new SaldoConstContaBancaria();
                saldoConstContaBancaria.setDataSaldo(conciliacao.getData());
                saldoConstContaBancaria.setContaBancariaEntidade(conciliacao.getContaBancaria());
            } else {
                saldoConstContaBancaria = saldoConstante;
            }
        }
    }

    public abstract void buscarPendencias();

    public void validarRelatorioConciliacao(Boolean diferenteZero) {
        ValidacaoException ve = new ValidacaoException();
        if (conciliacao.getContaBancaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo conta bancária deve ser informado.");
        }
        if (conciliacao.getData() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de conciliação deve ser informado.");
        }
        ve.lancarException();
        if (!diferenteZero) {
            ContaBancariaEntidade cbe = facade.getContaBancariaEntidadeFacade().recuperar(conciliacao.getContaBancaria().getId());
            if (cbe.getSubContas().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("A conta bancária não possui conta financeira.");
            }
        }
        ve.lancarException();
    }

    public void validarCamposPendencia() {
        ValidacaoException ve = new ValidacaoException();
        if (lancamentoConciliacaoBancaria.getData() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Lançamento deve ser informado.");
        }
        if (lancamentoConciliacaoBancaria.getSubConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Financeira deve ser informado.");
        }
        if (lancamentoConciliacaoBancaria.getTipoOperacaoConciliacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Operação da Conciliação deve ser informado.");
        }
        if (lancamentoConciliacaoBancaria.getTipoConciliacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Conciliação deve ser informado.");
        }
        if (lancamentoConciliacaoBancaria.getHistorico().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Histórico deve ser informado.");
        }
        if (lancamentoConciliacaoBancaria.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero(0).");
        }
        if (lancamentoConciliacaoBancaria.getDataConciliacao() != null) {
            if (lancamentoConciliacaoBancaria.getDataConciliacao().before(lancamentoConciliacaoBancaria.getData())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A data de conciliação deve ser superior a data do lançamento.");
            }
        }
        ve.lancarException();
    }

    public void validarSaldoConstante() {
        ValidacaoException ve = new ValidacaoException();
        if (saldoConstContaBancaria.getDataSaldo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Data deve ser informado.");
        }
        ve.lancarException();
    }

    public void limparCampoPendencia() {
        lancamentoConciliacaoBancaria = new LancamentoConciliacaoBancaria();
        lancamentoConciliacaoBancaria.setData(conciliacao.getData());
        lancamentoConciliacaoBancaria.setValor(BigDecimal.ZERO);
    }

    public void definirUnidadeDaContaFinancieraNaPendencia() {
        if (lancamentoConciliacaoBancaria.getSubConta() != null) {
            lancamentoConciliacaoBancaria.setUnidadeOrganizacional(facade.getSubContaFacade().recuperarUnidadeDaSubConta(lancamentoConciliacaoBancaria.getSubConta(), sistemaControlador.getExercicioCorrente()));
        }
    }

    public void validarDatasPeriodoFase() {
        ValidacaoException ve = new ValidacaoException();
        Exercicio exercicio = getExercicioFacade().getExercicioPorAno(Integer.valueOf(Util.getAnoDaData(lancamentoConciliacaoBancaria.getData())));
        if (facade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/conciliacao/movimentobancario/edita.xhtml", lancamentoConciliacaoBancaria.getData(), lancamentoConciliacaoBancaria.getUnidadeOrganizacional(), exercicio)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de lançamento está fora do período fase!");
        }
        if (lancamentoConciliacaoBancaria.getDataConciliacao() != null) {
            exercicio = getExercicioFacade().getExercicioPorAno(Integer.valueOf(Util.getAnoDaData(lancamentoConciliacaoBancaria.getDataConciliacao())));
            if (facade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/conciliacao/movimentobancario/edita.xhtml", lancamentoConciliacaoBancaria.getDataConciliacao(), lancamentoConciliacaoBancaria.getUnidadeOrganizacional(), exercicio)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está fora do período fase!");
            }
        }
        ve.lancarException();
    }

    public void validarEstornoDeConciliacao(Date data, UnidadeOrganizacional unidadeOrganizacional) {
        ValidacaoException ve = new ValidacaoException();
        Exercicio exercicio = getExercicioFacade().getExercicioPorAno(Integer.valueOf(Util.getAnoDaData(data)));
        if (facade.getFaseFacade().temBloqueioFaseParaRecurso("/financeiro/conciliacao/movimentobancario/edita.xhtml", data, unidadeOrganizacional, exercicio)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está fora do período fase!");
        }
        ve.lancarException();
    }

    public void salvarPendencia() {
        try {
            validarCamposPendencia();
            validarDatasPeriodoFase();
            facade.salvarNovo(lancamentoConciliacaoBancaria);
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Pendência salva com sucesso.");
            buscarPendencias();
            limparCampoPendencia();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    public void cancelarPendencia() {
        limparCampoPendencia();
    }

    public String redirecionarConciliacaoManual() {
        Web.poeNaSessao(this.conciliacao);
        return "/conciliacao-bancaria-manual/";
    }

    public void salvarSaldo() {
        try {
            validarSaldoConstante();
            if (saldoConstContaBancaria.getId() == null) {
                saldoConstContaBancaria.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
                facade.getSaldoConstContaBancariaFacade().salvarNovo(saldoConstContaBancaria);
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Saldo foi salvo com sucesso.");
            } else {
                facade.getSaldoConstContaBancariaFacade().salvar(saldoConstContaBancaria);
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Saldo foi atualizado com sucesso.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void novoSaldo() {
        saldoConstContaBancaria = (SaldoConstContaBancaria) Util.clonarObjeto(saldoConstContaBancaria);
        saldoConstContaBancaria.setId(null);
        saldoConstContaBancaria.setDataSaldo(conciliacao.getData());
        saldoConstContaBancaria.setValor(BigDecimal.ZERO);
        saldoConstContaBancaria.setHistorico("");
    }

    public void removerSaldoConstante(SaldoConstContaBancaria saldoConstContaBancaria) {
        facade.getSaldoConstContaBancariaFacade().remover(saldoConstContaBancaria);
        buscarHistoricoSaldo();
        buscarUltimoSaldo();
    }

    public void alterarSaldoConstante(SaldoConstContaBancaria saldoConstContaBancaria) {
        this.saldoConstContaBancaria = saldoConstContaBancaria;
    }

    public void atualizarSaldo() {
        buscarUltimoSaldo();
    }

    public Conciliacao getConciliacao() {
        return conciliacao;
    }

    public void setConciliacao(Conciliacao conciliacao) {
        this.conciliacao = conciliacao;
    }

    public List<SaldoConstContaBancaria> getHistoricosSaldoConstante() {
        return historicosSaldoConstante;
    }

    public void setHistoricosSaldoConstante(List<SaldoConstContaBancaria> historicosSaldoConstante) {
        this.historicosSaldoConstante = historicosSaldoConstante;
    }

    public String getCaminhoOrigem() {
        return "/conciliar/";
    }

    private Boolean recuperouDependenciasDaSessao() {
        conciliacao = (Conciliacao) Web.pegaDaSessao(Conciliacao.class);
        LancamentoReceitaOrc receitaRealizada = (LancamentoReceitaOrc) Web.pegaDaSessao(LancamentoReceitaOrc.class);
        if (conciliacao == null) {
            return false;
        }
        recuperarPorContaBancaria();
        return true;
    }

    public LancamentoConciliacaoBancaria getLancamentoConciliacaoBancaria() {
        return lancamentoConciliacaoBancaria;
    }

    public void setLancamentoConciliacaoBancaria(LancamentoConciliacaoBancaria lancamentoConciliacaoBancaria) {
        this.lancamentoConciliacaoBancaria = lancamentoConciliacaoBancaria;
    }

    public SaldoConstContaBancaria getSaldoConstContaBancaria() {
        return saldoConstContaBancaria;
    }

    public void setSaldoConstContaBancaria(SaldoConstContaBancaria saldoConstContaBancaria) {
        this.saldoConstContaBancaria = saldoConstContaBancaria;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public FiltroPendenciaConciliacao getFiltroPendencia() {
        return filtroPendencia;
    }

    public void setFiltroPendencia(FiltroPendenciaConciliacao filtroPendencia) {
        this.filtroPendencia = filtroPendencia;
    }

    public void definirNullParaElementoPagina() {
        conciliacao.setContaBancaria(null);
    }

    public LancamentoConciliacaoBancariaFacade getFacade() {
        return facade;
    }

}
