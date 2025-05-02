package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoCadastroTributario;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mga on 30/06/2017.
 */
public class NotificacaoCobrancaMalaDireta implements Comparable<NotificacaoCobrancaMalaDireta> {

    private Long idCadastro;
    private String inscricaoCadastral;
    private String cpfCnpj;
    private String nomeContribuinte;
    private TipoCadastroTributario tipoCadastroTributario;

    private String referenciaDebito;
    private Date dataLancamento;
    private Date dataVencimento;
    private String situacaoPagamento;
    private String divida;
    private String parcela;
    private Integer atraso;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal correcao;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal honorarios;
    private BigDecimal totalLancado;

    public NotificacaoCobrancaMalaDireta() {
        zerarValores();
    }

    public void zerarValores() {
        imposto = BigDecimal.ZERO;
        taxa = BigDecimal.ZERO;
        correcao = BigDecimal.ZERO;
        juros = BigDecimal.ZERO;
        multa = BigDecimal.ZERO;
        totalLancado = BigDecimal.ZERO;
        honorarios = BigDecimal.ZERO;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getParcela() {
        return parcela;
    }

    public void setParcela(String parcela) {
        this.parcela = parcela;
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }


    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomeContribuinte() {
        return nomeContribuinte;
    }

    public void setNomeContribuinte(String nomeContribuinte) {
        this.nomeContribuinte = nomeContribuinte;
    }

    public String getReferenciaDebito() {
        return referenciaDebito;
    }

    public void setReferenciaDebito(String referenciaDebito) {
        this.referenciaDebito = referenciaDebito;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getSituacaoPagamento() {
        return situacaoPagamento;
    }

    public void setSituacaoPagamento(String situacaoPagamento) {
        this.situacaoPagamento = situacaoPagamento;
    }

    public Integer getAtraso() {
        return atraso;
    }

    public void setAtraso(Integer atraso) {
        this.atraso = atraso;
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

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
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

    public BigDecimal getTotalLancado() {
        return totalLancado;

    }

    public void setTotalLancado(BigDecimal totalLancado) {
        this.totalLancado = totalLancado;
    }

    @Override
    public int compareTo(NotificacaoCobrancaMalaDireta o) {
        int i = 0;
        if (!this.tipoCadastroTributario.isPessoa()) {
            if (this.inscricaoCadastral != null && o.getInscricaoCadastral() != null) {
                i = this.inscricaoCadastral.compareTo(o.getInscricaoCadastral());
            }
        } else {
            i = this.nomeContribuinte.compareTo(o.getNomeContribuinte());
        }
        if (i == 0 && this.dataVencimento != null) {
            i = this.dataVencimento.compareTo(o.getDataVencimento());
        }
        if (i == 0 && this.getParcela() != null) {
            i = this.getParcela().compareTo(o.getParcela());
        }
        return i;
    }
}
