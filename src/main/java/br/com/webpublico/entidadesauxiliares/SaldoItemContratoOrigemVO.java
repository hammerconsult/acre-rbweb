package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SaldoItemContrato;
import br.com.webpublico.enums.OperacaoSaldoItemContrato;
import br.com.webpublico.enums.OrigemSaldoItemContrato;
import br.com.webpublico.enums.SubTipoSaldoItemContrato;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class SaldoItemContratoOrigemVO implements Serializable, Comparable<SaldoItemContratoOrigemVO> {

    private String descricaoMovimento;
    private String numeroAnoTermo;
    private Long idMovimentoOrigem;
    private OperacaoSaldoItemContrato operacao;
    private OrigemSaldoItemContrato origem;
    private SubTipoSaldoItemContrato subTipo;
    private BigDecimal saldo;
    private List<SaldoItemContrato> itensSaldo;
    private Long criadoEm;

    public SaldoItemContratoOrigemVO() {
        criadoEm = System.nanoTime();
        itensSaldo = Lists.newArrayList();
    }

    public List<SaldoItemContrato> getItensSaldo() {
        return itensSaldo;
    }

    public void setItensSaldo(List<SaldoItemContrato> itensSaldo) {
        this.itensSaldo = itensSaldo;
    }

    public Long getIdMovimentoOrigem() {
        return idMovimentoOrigem;
    }

    public void setIdMovimentoOrigem(Long idMovimentoOrigem) {
        this.idMovimentoOrigem = idMovimentoOrigem;
    }

    public String getDescricaoMovimento() {
        return descricaoMovimento;
    }

    public void setDescricaoMovimento(String descricaoMovimento) {
        this.descricaoMovimento = descricaoMovimento;
    }

    public OrigemSaldoItemContrato getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemSaldoItemContrato origem) {
        this.origem = origem;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getNumeroAnoTermo() {
        return numeroAnoTermo;
    }

    public void setNumeroAnoTermo(String numeroAnoTermo) {
        this.numeroAnoTermo = numeroAnoTermo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public OperacaoSaldoItemContrato getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoSaldoItemContrato operacao) {
        this.operacao = operacao;
    }

    public SubTipoSaldoItemContrato getSubTipo() {
        return subTipo;
    }

    public void setSubTipo(SubTipoSaldoItemContrato subTipo) {
        this.subTipo = subTipo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaldoItemContratoOrigemVO that = (SaldoItemContratoOrigemVO) o;
        return Objects.equals(criadoEm, that.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(criadoEm);
    }

    @Override
    public int compareTo(SaldoItemContratoOrigemVO o) {
        return ComparisonChain.start().compare(this.origem.getOrdem(), o.getOrigem().getOrdem()).result();
    }

    @Override
    public String toString() {
        try {
            return origem.getDescricao() + " - " + numeroAnoTermo;
        } catch (Exception e) {
            return origem.getDescricao();
        }
    }
}
