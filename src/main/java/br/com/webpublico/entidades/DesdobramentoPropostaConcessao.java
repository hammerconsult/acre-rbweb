package br.com.webpublico.entidades;

import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
@Table(name = "DESDOBRAMENTOPC")
@Etiqueta("Desdobramento Proposta Concessão")
public class DesdobramentoPropostaConcessao extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Concessão")
    private PropostaConcessaoDiaria propostaConcessaoDiaria;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Despesa")
    private Conta conta;

    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valor;

    @Monetario
    @Etiqueta("Saldo")
    private BigDecimal saldo;

    public DesdobramentoPropostaConcessao() {
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public PropostaConcessaoDiaria getPropostaConcessaoDiaria() {
        return propostaConcessaoDiaria;
    }

    public void setPropostaConcessaoDiaria(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        try {
            return "Concessão: " + propostaConcessaoDiaria.toString() + " - desdobramento.: " + conta + " - " + Util.formataValor(valor);
        } catch (Exception ex) {
            return "";
        }
    }
}
