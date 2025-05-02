package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity

@Audited
@GrupoDiagrama(nome = "ITBI Online")
@Etiqueta("Transmitentes do Cálculo do ITBI Online")
public class TransmitentesSolicitacaoItbiOnline implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline;
    private BigDecimal percentual;
    @ManyToOne
    private Pessoa transmitente;
    @Transient
    private Long criadoEm;

    public TransmitentesSolicitacaoItbiOnline() {
        this.criadoEm = System.nanoTime();
    }

    public TransmitentesSolicitacaoItbiOnline(CalculoSolicitacaoItbiOnline calculo, Pessoa transmitente, BigDecimal percentual) {
        this.criadoEm = System.nanoTime();
        this.calculoSolicitacaoItbiOnline = calculo;
        this.percentual = percentual;
        this.transmitente = transmitente;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CalculoSolicitacaoItbiOnline getCalculoITBIOnline() {
        return calculoSolicitacaoItbiOnline;
    }

    public void setCalculoITBIOnline(CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline) {
        this.calculoSolicitacaoItbiOnline = calculoSolicitacaoItbiOnline;
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

    public Pessoa getTransmitente() {
        return transmitente;
    }

    public void setTransmitente(Pessoa transmitente) {
        this.transmitente = transmitente;
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
            nomeRazao = getTransmitente().getNome();
            if (getTransmitente().getCpf_Cnpj() != null) {
                cpfCnpj = getTransmitente().getCpf_Cnpj();
            }
        } catch (Exception ex) {
        }

        return nomeRazao + " " + cpfCnpj;
    }
}
