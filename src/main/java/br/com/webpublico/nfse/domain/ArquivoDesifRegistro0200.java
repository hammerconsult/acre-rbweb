package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class ArquivoDesifRegistro0200 extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Long linha;
    @ManyToOne
    private ArquivoDesif arquivoDesif;
    @ManyToOne
    private TarifaBancaria tarifaBancaria;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    private BigDecimal valorUnitario;
    private BigDecimal valorPercentual;
    private String conta;
    private String desdobramento;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLinha() {
        return linha;
    }

    public void setLinha(Long linha) {
        this.linha = linha;
    }

    public ArquivoDesif getArquivoDesif() {
        return arquivoDesif;
    }

    public void setArquivoDesif(ArquivoDesif arquivoDesif) {
        this.arquivoDesif = arquivoDesif;
    }

    public TarifaBancaria getTarifaBancaria() {
        return tarifaBancaria;
    }

    public void setTarifaBancaria(TarifaBancaria tarifaBancaria) {
        this.tarifaBancaria = tarifaBancaria;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorPercentual() {
        return valorPercentual;
    }

    public void setValorPercentual(BigDecimal valorPercentual) {
        this.valorPercentual = valorPercentual;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(String desdobramento) {
        this.desdobramento = desdobramento;
    }
}
