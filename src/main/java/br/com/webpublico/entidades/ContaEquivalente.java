package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaSaldoContaEquivalente;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

/**
 *
 * Determina a equivalência entre contas de diferentes planos de contas. O
 * vínculo deve respeitar as seguintes regras:
 *
 * 1. As contas devem ser de planos de conta do mesmo tipo de conta. 2. Só deve
 * haver uma equivalência vigente (deve ser considerada nas consultas).
 *
 * É usado para permitir que a Prefeitura utilize seu plano de contas
 * independentemente do plano de contas de outros órgãos de controle, como o
 * TCU, porém mantendo a capacidade de gerar demonstrativos a partir dos
 * vínculos.
 *
 */
@Etiqueta("Conta Equivalente")
public class ContaEquivalente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Conta origem a partir da qual os lançamentos serão consolidados na
     * contaDestino. Uma conta origem não deve estar vinculada a mais de uma
     * conta destino de um mesmo plano de contas, pois várias origens podem ser
     * consolidadas em um destino, mas o contrário não é possível.
     */
    @ManyToOne
    private Conta contaOrigem;
    /**
     * Conta na qual serão consolidados os lançamentos da(s) conta(s) origem
     * vinculadas.
     */
    @ManyToOne
    private Conta contaDestino;
    /**
     * Data a partir da qual este vínculo deve ser considerado. Utilizado para
     * tratar diversos vínculos dentro de um mesmo exercício.
     */
    @Temporal(TemporalType.DATE)
    private Date vigencia;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Enumerated(EnumType.STRING)
    private NaturezaSaldoContaEquivalente naturezaSaldo;

    public ContaEquivalente() {
        dataRegistro = new Date();
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Conta getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(Conta contaDestino) {
        this.contaDestino = contaDestino;
    }

    public Conta getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(Conta contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getVigencia() {
        return vigencia;
    }

    public void setVigencia(Date vigencia) {
        this.vigencia = vigencia;
    }

    public NaturezaSaldoContaEquivalente getNaturezaSaldo() {
        return naturezaSaldo;
    }

    public void setNaturezaSaldo(NaturezaSaldoContaEquivalente naturezaSaldo) {
        this.naturezaSaldo = naturezaSaldo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContaEquivalente other = (ContaEquivalente) obj;
        if (this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Origem: " + contaOrigem + " Destino: " + contaDestino + " Vigência: " + vigencia;
    }
}
