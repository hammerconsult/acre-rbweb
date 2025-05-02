package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.Ordenacao;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HardRock on 12/05/2017.
 */
public class Inadimplencia implements Comparable<Inadimplencia> {

    private Long idCadastro;
    private BigDecimal valor;
    private BigDecimal valorTotal;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal honorarios;
    private BigDecimal desconto;
    private BigDecimal percentual;
    private String nome;
    private String tipoPessoa;
    private Ordenacao ordenacao;
    private FiltroRelatorioInadimplencia.OrdenacaoInadimplencia ordenacaoInadimplencia;
    private String inscricaoCadastral;
    private List<InadimplenciaParcelas> parcelas;

    public Inadimplencia() {
        zeraValores();
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public Ordenacao getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public FiltroRelatorioInadimplencia.OrdenacaoInadimplencia getOrdenacaoInadimplencia() {
        return ordenacaoInadimplencia;
    }

    public void setOrdenacaoInadimplencia(FiltroRelatorioInadimplencia.OrdenacaoInadimplencia ordenacaoInadimplencia) {
        this.ordenacaoInadimplencia = ordenacaoInadimplencia;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<InadimplenciaParcelas> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<InadimplenciaParcelas> parcelas) {
        this.parcelas = parcelas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inadimplencia that = (Inadimplencia) o;

        return idCadastro.equals(that.idCadastro);

    }

    @Override
    public int hashCode() {
        return idCadastro.hashCode();
    }

    @Override
    public int compareTo(Inadimplencia o) {
        if (FiltroRelatorioInadimplencia.OrdenacaoInadimplencia.VALOR.equals(this.getOrdenacaoInadimplencia())) {
            if (Ordenacao.CRESCENTE.equals(this.getOrdenacao())) {
                return this.getValor().compareTo(o.getValor());
            } else {
                return o.getValor().compareTo(this.getValor());
            }
        }
        if (FiltroRelatorioInadimplencia.OrdenacaoInadimplencia.TIPO_CADASTRO.equals(this.getOrdenacaoInadimplencia())) {
            if (Ordenacao.CRESCENTE.equals(this.getOrdenacao())) {
                return this.getInscricaoCadastral().compareTo(o.getInscricaoCadastral());
            } else {
                return o.getInscricaoCadastral().compareTo(this.getInscricaoCadastral());
            }
        }
        return 0;
    }

    public void zeraValores() {
        valor = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        imposto = BigDecimal.ZERO;
        taxa = BigDecimal.ZERO;
        juros = BigDecimal.ZERO;
        multa = BigDecimal.ZERO;
        correcao = BigDecimal.ZERO;
        honorarios = BigDecimal.ZERO;
        desconto = BigDecimal.ZERO;
        percentual = BigDecimal.ZERO;
        parcelas = Lists.newArrayList();
    }

    public void somarValoresParcelas() {
        BigDecimal totalImposto = BigDecimal.ZERO;
        BigDecimal totalTaxa = BigDecimal.ZERO;
        BigDecimal totalJuros = BigDecimal.ZERO;
        BigDecimal totalMulta = BigDecimal.ZERO;
        BigDecimal totalCorrecao = BigDecimal.ZERO;
        BigDecimal totalHonorarios = BigDecimal.ZERO;
        BigDecimal totalDesconto = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (InadimplenciaParcelas parcela : parcelas) {
            totalImposto = totalImposto.add(parcela.getImposto());
            totalTaxa = totalTaxa.add(parcela.getTaxa());
            totalJuros = totalJuros.add(parcela.getJuros());
            totalMulta = totalMulta.add(parcela.getMulta());
            totalCorrecao = totalCorrecao.add(parcela.getCorrecao());
            totalHonorarios = totalHonorarios.add(parcela.getHonorarios());
            totalDesconto = totalDesconto.add(parcela.getDesconto());
            total = total.add(parcela.getValor());
        }
        setImposto(totalImposto);
        setTaxa(totalTaxa);
        setJuros(totalJuros);
        setMulta(totalMulta);
        setCorrecao(totalCorrecao);
        setHonorarios(totalHonorarios);
        setDesconto(totalDesconto);
        setValor(total);
    }
}
