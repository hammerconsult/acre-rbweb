package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author fabio
 */
@Entity

@Audited
@GrupoDiagrama(nome = "ITBI")
@Etiqueta("Transmitentes do Cálculo do ITBI")
public class TransmitentesCalculoITBI implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoITBI calculoITBI;
    private BigDecimal percentual;
    @ManyToOne
    private Pessoa pessoa;
    @Transient
    private Long criadoEm;

    public TransmitentesCalculoITBI() {
        this.criadoEm = System.nanoTime();
    }

    public TransmitentesCalculoITBI(CalculoITBI calculo, Pessoa pessoa, BigDecimal percentual) {
        this.criadoEm = System.nanoTime();
        this.calculoITBI = calculo;
        this.percentual = percentual;
        this.pessoa = pessoa;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CalculoITBI getCalculoITBI() {
        return calculoITBI;
    }

    public void setCalculoITBI(CalculoITBI calculoITBI) {
        this.calculoITBI = calculoITBI;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(object, this);
    }

    @Override
    public String toString() {
        String nomeRazao = "";
        String cpfCnpj = "";

        try {
            nomeRazao = getPessoa().getNome();
            if (getPessoa().getCpf_Cnpj() != null) {
                cpfCnpj = getPessoa().getCpf_Cnpj();
            }
        } catch (Exception ex) {
        }

        return nomeRazao + " " + cpfCnpj;
    }
}
