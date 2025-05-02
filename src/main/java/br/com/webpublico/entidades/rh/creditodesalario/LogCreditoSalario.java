package br.com.webpublico.entidades.rh.creditodesalario;

import br.com.webpublico.entidades.ContaCorrenteBancaria;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.VinculoFP;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by William on 07/03/2019.
 */
@Entity
@Audited
public class LogCreditoSalario extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CreditoSalario creditoSalario;
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    private ContaCorrenteBancaria contaCorrenteBancaria;
    private String log;

    private String orgao;
    @Transient
    private String banco;
    @Transient
    private String agencia;
    @Transient
    private String conta;
    @Transient
    private String matricula;
    @Transient
    private String nome;

    private BigDecimal valorAReceber;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public CreditoSalario getCreditoSalario() {
        return creditoSalario;
    }

    public void setCreditoSalario(CreditoSalario creditoSalario) {
        this.creditoSalario = creditoSalario;
    }

    public ContaCorrenteBancaria getContaCorrenteBancaria() {
        return contaCorrenteBancaria;
    }

    public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValorAReceber() {
        return valorAReceber;
    }

    public void setValorAReceber(BigDecimal valorAReceber) {
        this.valorAReceber = valorAReceber;
    }
}
