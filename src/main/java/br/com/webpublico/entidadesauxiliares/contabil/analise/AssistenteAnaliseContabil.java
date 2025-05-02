package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.AnaliseMovimentoContabil;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.MovimentoHashContabil;
import br.com.webpublico.entidadesauxiliares.contabil.ExtratoMovimentoDespesaORC;
import br.com.webpublico.util.AssistenteBarraProgresso;

import java.util.Date;
import java.util.List;

public class AssistenteAnaliseContabil  {

    private Date dataInicial;
    private Date dataFinal;
    private List<TipoAssistenteAnaliseContabil> tipos;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Boolean mostrarSomenteInsconsistencia;
    private Boolean buscarSomenteContasComMovimentacao;

    //validacao
    private List<ExtratoMovimentoDespesaORC> orcamentario;
    private List<AnaliseContaFinanceira> financeiro;
    private List<AnaliseBalanceteContabil> contabil;
    private List<AnaliseBalanceteContabilSiconfi> siconfi;
    private List<AnaliseContaExtra> extraOrcamentario;
    private List<AnaliseDividaPublica> dividaPublica;
    private List<AnaliseGrupoBemMovel> grupoBemMovel;
    private List<AnaliseGrupoBemImovel> grupoBemImovel;
    private List<AnaliseGrupoBemEstoque> grupoBemEstoque;

    //geracao de saldo
    private Boolean gerarSaldo;
    private Integer quantidadeConcorrencia;
    private Integer quantidadeRegistros;
    private List<AnaliseGerarSaldoOrcamentario> gerarSaldoOrcamentarios;

    private List<MovimentoHashContabil> movimentos;
    private AnaliseMovimentoContabil analise;

    public AssistenteAnaliseContabil() {
        mostrarSomenteInsconsistencia = true;
        gerarSaldo = false;
        quantidadeConcorrencia = 5;
        quantidadeRegistros = 5;
    }


    public List<ExtratoMovimentoDespesaORC> getOrcamentario() {
        return orcamentario;
    }

    public void setOrcamentario(List<ExtratoMovimentoDespesaORC> orcamentario) {
        this.orcamentario = orcamentario;
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

    public Boolean getMostrarSomenteInsconsistencia() {
        return mostrarSomenteInsconsistencia;
    }

    public void setMostrarSomenteInsconsistencia(Boolean mostrarSomenteInsconsistencia) {
        this.mostrarSomenteInsconsistencia = mostrarSomenteInsconsistencia;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<AnaliseContaFinanceira> getFinanceiro() {
        return financeiro;
    }

    public void setFinanceiro(List<AnaliseContaFinanceira> financeiro) {
        this.financeiro = financeiro;
    }

    public List<TipoAssistenteAnaliseContabil> getTipos() {
        return tipos;
    }

    public void setTipos(List<TipoAssistenteAnaliseContabil> tipos) {
        this.tipos = tipos;
    }

    public List<AnaliseBalanceteContabil> getContabil() {
        return contabil;
    }

    public void setContabil(List<AnaliseBalanceteContabil> contabil) {
        this.contabil = contabil;
    }

    public List<AnaliseBalanceteContabilSiconfi> getSiconfi() {
        return siconfi;
    }

    public void setSiconfi(List<AnaliseBalanceteContabilSiconfi> siconfi) {
        this.siconfi = siconfi;
    }

    public List<AnaliseContaExtra> getExtraOrcamentario() {
        return extraOrcamentario;
    }

    public void setExtraOrcamentario(List<AnaliseContaExtra> extraOrcamentario) {
        this.extraOrcamentario = extraOrcamentario;
    }

    public List<AnaliseDividaPublica> getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(List<AnaliseDividaPublica> dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public Integer getQuantidadeLancamentos() {
        Integer quantidade = 0;
        if (orcamentario != null) quantidade += orcamentario.size();
        if (financeiro != null) quantidade += financeiro.size();
        if (contabil != null) quantidade += contabil.size();
        if (siconfi != null) quantidade += siconfi.size();
        if (extraOrcamentario != null) quantidade += extraOrcamentario.size();
        if (dividaPublica != null) quantidade += dividaPublica.size();
        return quantidade;
    }

    public List<AnaliseGerarSaldoOrcamentario> getGerarSaldoOrcamentarios() {
        return gerarSaldoOrcamentarios;
    }

    public void setGerarSaldoOrcamentarios(List<AnaliseGerarSaldoOrcamentario> gerarSaldoOrcamentarios) {
        this.gerarSaldoOrcamentarios = gerarSaldoOrcamentarios;
    }

    public Integer getQuantidadeConcorrencia() {
        return quantidadeConcorrencia;
    }

    public void setQuantidadeConcorrencia(Integer quantidadeConcorrencia) {
        this.quantidadeConcorrencia = quantidadeConcorrencia;
    }

    public Integer getQuantidadeRegistros() {
        return quantidadeRegistros;
    }

    public void setQuantidadeRegistros(Integer quantidadeRegistros) {
        this.quantidadeRegistros = quantidadeRegistros;
    }

    public Boolean getGerarSaldo() {
        return gerarSaldo;
    }

    public void setGerarSaldo(Boolean gerarSaldo) {
        this.gerarSaldo = gerarSaldo;
    }

    public List<MovimentoHashContabil> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<MovimentoHashContabil> movimentos) {
        this.movimentos = movimentos;
    }

    public AnaliseMovimentoContabil getAnalise() {
        return analise;
    }

    public void setAnalise(AnaliseMovimentoContabil analise) {
        this.analise = analise;
    }

    public Boolean getBuscarSomenteContasComMovimentacao() {
        return buscarSomenteContasComMovimentacao;
    }

    public void setBuscarSomenteContasComMovimentacao(Boolean buscarSomenteContasComMovimentacao) {
        this.buscarSomenteContasComMovimentacao = buscarSomenteContasComMovimentacao;
    }

    public List<AnaliseGrupoBemMovel> getGrupoBemMovel() {
        return grupoBemMovel;
    }

    public void setGrupoBemMovel(List<AnaliseGrupoBemMovel> grupoBemMovel) {
        this.grupoBemMovel = grupoBemMovel;
    }

    public List<AnaliseGrupoBemImovel> getGrupoBemImovel() {
        return grupoBemImovel;
    }

    public void setGrupoBemImovel(List<AnaliseGrupoBemImovel> grupoBemImovel) {
        this.grupoBemImovel = grupoBemImovel;
    }

    public List<AnaliseGrupoBemEstoque> getGrupoBemEstoque() {
        return grupoBemEstoque;
    }

    public void setGrupoBemEstoque(List<AnaliseGrupoBemEstoque> grupoBemEstoque) {
        this.grupoBemEstoque = grupoBemEstoque;
    }
}
