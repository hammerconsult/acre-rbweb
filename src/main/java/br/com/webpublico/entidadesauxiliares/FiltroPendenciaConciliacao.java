package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidades.TipoConciliacao;
import br.com.webpublico.enums.TipoOperacaoConcilicaoBancaria;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by HardRock on 23/03/2017.
 */
public class FiltroPendenciaConciliacao {

    private Date dataInicial;
    private Date dataFinal;
    private Date dataConciliacao;
    private Date dataConciliacaoPorIdentificador;
    private List<Date> datasConciliacoesPorIdentificadores;
    private Date dataConciliacaoInicial;
    private Date dataConciliacaoFinal;
    private SubConta contaFinanceira;
    private ContaBancariaEntidade contaBancariaEntidade;
    private TipoOperacaoConcilicaoBancaria operacaoConciliacao;
    private TipoConciliacao tipoConciliacao;
    private String numero;
    private List<String> numeros;
    private Boolean visualizarPendenciasBaixadas;
    private BigDecimal valor;
    private List<BigDecimal> valores;
    private Conciliado conciliado;

    public FiltroPendenciaConciliacao() {
        conciliado = Conciliado.NAO;
        datasConciliacoesPorIdentificadores = Lists.newArrayList();
        numeros = Lists.newArrayList();
        valores = Lists.newArrayList();
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

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public TipoOperacaoConcilicaoBancaria getOperacaoConciliacao() {
        return operacaoConciliacao;
    }

    public void setOperacaoConciliacao(TipoOperacaoConcilicaoBancaria operacaoConciliacao) {
        this.operacaoConciliacao = operacaoConciliacao;
    }

    public TipoConciliacao getTipoConciliacao() {
        return tipoConciliacao;
    }

    public void setTipoConciliacao(TipoConciliacao tipoConciliacao) {
        this.tipoConciliacao = tipoConciliacao;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public Date getDataConciliacaoInicial() {
        return dataConciliacaoInicial;
    }

    public void setDataConciliacaoInicial(Date dataConciliacaoInicial) {
        this.dataConciliacaoInicial = dataConciliacaoInicial;
    }

    public Date getDataConciliacaoFinal() {
        return dataConciliacaoFinal;
    }

    public void setDataConciliacaoFinal(Date dataConciliacaoFinal) {
        this.dataConciliacaoFinal = dataConciliacaoFinal;
    }

    public Conciliado getConciliado() {
        return conciliado;
    }

    public void setConciliado(Conciliado conciliado) {
        this.conciliado = conciliado;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Boolean getVisualizarPendenciasBaixadas() {
        return visualizarPendenciasBaixadas;
    }

    public void setVisualizarPendenciasBaixadas(Boolean visualizarPendenciasBaixadas) {
        this.visualizarPendenciasBaixadas = visualizarPendenciasBaixadas;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataConciliacaoPorIdentificador() {
        return dataConciliacaoPorIdentificador;
    }

    public void setDataConciliacaoPorIdentificador(Date dataConciliacaoPorIdentificador) {
        this.dataConciliacaoPorIdentificador = dataConciliacaoPorIdentificador;
    }

    public List<Date> getDatasConciliacoesPorIdentificadores() {
        return datasConciliacoesPorIdentificadores;
    }

    public void setDatasConciliacoesPorIdentificadores(List<Date> datasConciliacoesPorIdentificadores) {
        this.datasConciliacoesPorIdentificadores = datasConciliacoesPorIdentificadores;
    }

    public List<String> getNumeros() {
        return numeros;
    }

    public void setNumeros(List<String> numeros) {
        this.numeros = numeros;
    }

    public List<BigDecimal> getValores() {
        return valores;
    }

    public void setValores(List<BigDecimal> valores) {
        this.valores = valores;
    }

    public enum Conciliado {
        SIM("Sim"),
        NAO("Não"),
        TODOS("Todos");

        private String descricao;

        Conciliado(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

        public boolean isTodos() {
            return TODOS.equals(this);
        }

        public boolean isSim() {
            return SIM.equals(this);
        }

        public boolean isNao() {
            return NAO.equals(this);
        }
    }
}
