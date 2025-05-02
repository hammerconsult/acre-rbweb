package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.entidadesauxiliares.contabil.ReprocessamentoSaldoExtraOrcamentario;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Persistencia;
import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 13/06/2017.
 */
public class AssistenteReprocessamento {
    private Date dataInicial;
    private Date dataFinal;
    private Boolean excluirSaldos;
    private SubConta subConta;
    private ContaBancariaEntidade contaBancariaEntidade;
    private ContaDeDestinacao contaDeDestinacao;
    private Exercicio exercicio;
    private BarraProgressoAssistente assistenteBarraProgresso;
    private List<Long> idsContaFinanceira;
    private String filtro;
    private String queryReprocessamento;
    private String queryReprocessamentoCreditoReceber;
    private String queryReprocessamentoCreditoReceberReceita;
    private String queryReprocessamentoCreditoReceberReceitaEstorno;
    private ReprocessamentoHistorico historico;
    private ReprocessamentoSaldoExtraOrcamentario reprocessamentoSaldoExtraOrcamentario;
    private GrupoBem grupoBem;
    private GrupoMaterial grupoMaterial;
    private String queryDividaAtiva;
    private String queryReceita;
    private String queryEstornoReceita;
    private ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc;

    public AssistenteReprocessamento() {
        novo();
    }

    public void novo() {
        filtro = "";
        excluirSaldos = Boolean.TRUE;
        dataInicial = new Date();
        dataFinal = new Date();
        idsContaFinanceira = Lists.newArrayList();
        assistenteBarraProgresso = new BarraProgressoAssistente();
        queryReprocessamento = "";
        queryReprocessamentoCreditoReceber = "";
        queryReprocessamentoCreditoReceberReceita = "";
        queryReprocessamentoCreditoReceberReceitaEstorno = "";
        queryDividaAtiva = "";
        queryReceita = "";
        queryEstornoReceita = "";
    }

    public void inicializarHistorico(TipoReprocessamentoHistorico tipo, UsuarioSistema usuarioSistema) {
        historico = new ReprocessamentoHistorico();
        historico.setTipoReprocessamentoHistorico(tipo);
        historico.setDataHoraInicio(new Date());
        historico.setProcessados(0);
        historico.setProcessadosComErro(0);
        historico.setProcessadosSemErro(0);
        historico.setTotal(0);
        historico.setUsuarioSistema(usuarioSistema);
        historico.setDataInicial(this.dataInicial);
        historico.setDataFinal(this.dataFinal);
    }

    public void setTotal(Integer total) {
        getAssistenteBarraProgresso().setTotal(total);
        if (getHistorico() != null) {
            getHistorico().setTotal(total);
        }
    }

    public void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final deve ser informado.");
        }
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data final deve ser superior ou igual a data inicial.");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes");
        }
        ve.lancarException();
    }

    public void adicionarHistoricoLog(String mensagem) {
        if (historico != null && historico.getMensagens() != null) {
            historico.getMensagens().add(new ReprocessamentoLog(new Date(), mensagem, "", null, false, historico, null, null));
        }
    }

    public void adicionarHistoricoLogSemErro(Object objeto, String objetosUtilizados) {
        if (historico != null && historico.getMensagens() != null) {
            historico.getMensagens().add(new ReprocessamentoLog(new Date(), " Reprocessou o(a) " + Persistencia.getNomeDaClasse(objeto.getClass()) + ": <b>" + objeto.toString() + "</b>", "", objetosUtilizados, false, historico, ((Long) Persistencia.getId(objeto)), objeto.getClass().getSimpleName()));
        }
    }

    public void finalizar() {
        assistenteBarraProgresso.setMensagem("FINALIZANDO PROCESSO");
        if (historico != null && historico.getMensagens() != null) {
            historico.getMensagens().add(new ReprocessamentoLog(historico, "<b> <font color='blue'>... FIM ...</font> </b>", new Date()));
            historico.getMensagens().add(new ReprocessamentoLog(historico, "<b> <font color='blue'>... Salvando histórico...</font> </b>", new Date()));
            historico.setDataHoraTermino(new Date());
            historico.getMensagens().add(new ReprocessamentoLog(historico, "<b> <font color='blue'>... Finalizado...</font> </b>", new Date()));
        }
        assistenteBarraProgresso.finaliza();
    }

    public void adicionarReprocessandoErroLog(String erro, Object objeto, String objetosUtilizados) {
        if (historico != null && historico.getMensagens() != null) {
            historico.getMensagens().add(new ReprocessamentoLog(new Date(), " Erro ao Reprocessar o(a) " + Persistencia.getNomeDaClasse(objeto.getClass()) + " : " + objeto.toString(), " <b> <font color='red'>" + erro + " </font> </b>", objetosUtilizados, true, historico, ((Long) Persistencia.getId(objeto)), objeto.getClass().getSimpleName()));
        }
    }

    public synchronized void historicoConta() {
        if (historico !=null) {
            historico.setProcessados(historico.getProcessados() + 1);
            assistenteBarraProgresso.setProcessados(assistenteBarraProgresso.getProcessados() + 1);
        }
    }

    public synchronized void historicoContaComErro() {
        if (historico !=null) {
            historico.setProcessadosComErro(historico.getProcessadosComErro() + 1);
        }
    }

    public synchronized void historicoRemoveSemErro() {
        if (historico !=null) {
            historico.setProcessadosComErro(historico.getProcessadosSemErro() - 1);
        }
    }

    public synchronized void historicoContaSemErro() {
        historico.setProcessadosSemErro(historico.getProcessadosSemErro() + 1);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<Long> getIdsContaFinanceira() {
        return idsContaFinanceira;
    }

    public void setIdsContaFinanceira(List<Long> idsContaFinanceira) {
        this.idsContaFinanceira = idsContaFinanceira;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Boolean getExcluirSaldos() {
        return excluirSaldos;
    }

    public void setExcluirSaldos(Boolean excluirSaldos) {
        this.excluirSaldos = excluirSaldos;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public BarraProgressoAssistente getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(BarraProgressoAssistente assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public String getQueryReprocessamento() {
        return queryReprocessamento;
    }

    public void setQueryReprocessamento(String queryReprocessamento) {
        this.queryReprocessamento = queryReprocessamento;
    }

    public String getQueryReprocessamentoCreditoReceber() {
        return queryReprocessamentoCreditoReceber;
    }

    public void setQueryReprocessamentoCreditoReceber(String queryReprocessamentoCreditoReceber) {
        this.queryReprocessamentoCreditoReceber = queryReprocessamentoCreditoReceber;
    }

    public String getQueryReprocessamentoCreditoReceberReceita() {
        return queryReprocessamentoCreditoReceberReceita;
    }

    public void setQueryReprocessamentoCreditoReceberReceita(String queryReprocessamentoCreditoReceberReceita) {
        this.queryReprocessamentoCreditoReceberReceita = queryReprocessamentoCreditoReceberReceita;
    }

    public String getQueryReprocessamentoCreditoReceberReceitaEstorno() {
        return queryReprocessamentoCreditoReceberReceitaEstorno;
    }

    public void setQueryReprocessamentoCreditoReceberReceitaEstorno(String queryReprocessamentoCreditoReceberReceitaEstorno) {
        this.queryReprocessamentoCreditoReceberReceitaEstorno = queryReprocessamentoCreditoReceberReceitaEstorno;
    }

    public ReprocessamentoHistorico getHistorico() {
        return historico;
    }

    public void setHistorico(ReprocessamentoHistorico historico) {
        this.historico = historico;
    }

    public ReprocessamentoSaldoExtraOrcamentario getReprocessamentoSaldoExtraOrcamentario() {
        return reprocessamentoSaldoExtraOrcamentario;
    }

    public void setReprocessamentoSaldoExtraOrcamentario(ReprocessamentoSaldoExtraOrcamentario reprocessamentoSaldoExtraOrcamentario) {
        this.reprocessamentoSaldoExtraOrcamentario = reprocessamentoSaldoExtraOrcamentario;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public String getQueryDividaAtiva() {
        return queryDividaAtiva;
    }

    public void setQueryDividaAtiva(String queryDividaAtiva) {
        this.queryDividaAtiva = queryDividaAtiva;
    }

    public String getQueryReceita() {
        return queryReceita;
    }

    public void setQueryReceita(String queryReceita) {
        this.queryReceita = queryReceita;
    }

    public String getQueryEstornoReceita() {
        return queryEstornoReceita;
    }

    public void setQueryEstornoReceita(String queryEstornoReceita) {
        this.queryEstornoReceita = queryEstornoReceita;
    }

    public ReprocessamentoSaldoFonteDespesaOrc getReprocessamentoSaldoFonteDespesaOrc() {
        return reprocessamentoSaldoFonteDespesaOrc;
    }

    public void setReprocessamentoSaldoFonteDespesaOrc(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        this.reprocessamentoSaldoFonteDespesaOrc = reprocessamentoSaldoFonteDespesaOrc;
    }
}
