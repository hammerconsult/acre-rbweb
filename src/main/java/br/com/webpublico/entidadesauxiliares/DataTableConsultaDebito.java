package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.consultaparcela.CalculadorAcrescimos;

import java.math.BigDecimal;
import java.util.Date;

public class DataTableConsultaDebito implements Comparable<DataTableConsultaDebito> {

    private Cadastro cadastro;
    private Divida divida;
    private Exercicio exercicio;
    private String sequenciaParcela;
    private Long sd;
    private String parcelamento;
    private Date vencimento;
    private ParcelaValorDivida parcela;
    private SituacaoParcelaValorDivida situacao;
    private BigDecimal valorParcela;
    private BigDecimal valorUFM;
    private BigDecimal valorDesconto;
    private BigDecimal valorImposto;
    private BigDecimal valorTaxa;

    public DataTableConsultaDebito() {
    }

    public DataTableConsultaDebito(ParcelaValorDivida parcela,
                                   Cadastro cadastro, Divida divida, Exercicio exercicio,
                                   String sequenciaParcela, Long sd, String parcelamento,
                                   Date vencimento, SituacaoParcelaValorDivida situacao,
                                   BigDecimal valorUFM,
                                   BigDecimal valor,
                                   ConfiguracaoAcrescimos acrescimo,
                                   BigDecimal valorDesconto,
                                   BigDecimal valorImposto,
                                   BigDecimal valorTaxa
            ) {
        this.valorUFM = valorUFM;
        this.cadastro = cadastro;
        this.divida = divida;
        this.exercicio = exercicio;
        this.sd = sd;
        this.parcelamento = parcelamento;
        this.vencimento = vencimento;
        this.parcela = parcela;
        this.sequenciaParcela = sequenciaParcela;
        this.situacao = situacao;
        this.valorParcela = valor;
        this.valorImposto = valorImposto;
        this.valorTaxa = valorTaxa;
        this.valorDesconto = valorDesconto;
        CalculadorAcrescimos.calculaAcrescimo(parcela, new Date(), getValorTotal(), acrescimo);

    }

    public BigDecimal getValorUFM() {
        return valorUFM;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getSequenciaParcela() {
        return sequenciaParcela;
    }

    public void setSequenciaParcela(String sequenciaParcela) {
        this.sequenciaParcela = sequenciaParcela;
    }

    public Long getSd() {
        return sd;
    }

    public void setSd(Long sd) {
        this.sd = sd;
    }

    public String getParcelamento() {
        return parcelamento;
    }

    public void setParcelamento(String parcelamento) {
        this.parcelamento = parcelamento;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public SituacaoParcelaValorDivida getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoParcelaValorDivida situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getValorParcela() {
        return valorParcela;
    }

    public BigDecimal getValorJuros() {
        return parcela.getValorJuros();
    }

    public BigDecimal getValorMulta() {
        return parcela.getValorMulta();
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public BigDecimal getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa;
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
    }

    public BigDecimal getValorTotal() {
        BigDecimal valor = BigDecimal.ZERO;
        if (getValorParcela() != null) {
            valor = valor.add(getValorParcela());
        }
        if (getValorJuros() != null) {
            valor = valor.add(getValorJuros());
        }
        if (getValorMulta() != null) {
            valor = valor.add(getValorMulta());
        }
        if (getValorDesconto() != null) {
            valor = valor.subtract(getValorDesconto());
        }
        return valor;
    }

    @Override
    public int compareTo(DataTableConsultaDebito o) {
        try {
            int retorno = 0;
            retorno = this.getCadastro().getNumeroCadastro().compareTo(o.getCadastro().getNumeroCadastro());
            if (retorno == 0) {
                retorno = this.getDivida().getDescricao().compareTo(o.getDivida().getDescricao());
            }
            if (retorno == 0) {
                retorno = this.getExercicio().getAno().compareTo(o.getExercicio().getAno());
            }
            if (retorno == 0) {
                Integer primeiro = Integer.parseInt(this.getParcela().getSequenciaParcela());
                Integer segundo = Integer.parseInt(o.getParcela().getSequenciaParcela());
                retorno = primeiro.compareTo(segundo);
            }
            if (retorno == 0) {
                retorno = this.getSd().compareTo(o.getSd());
            }
            return retorno;
        } catch (Exception e) {
            return 0;
        }
    }
}
