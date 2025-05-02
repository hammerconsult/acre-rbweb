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
@GrupoDiagrama(nome = "ITBI")
@Etiqueta("Adquirentes da solicitação do ITBI Online")
public class AdquirentesSolicitacaoITBIOnline implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal percentual;
    @ManyToOne
    private CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private Pessoa adquirente;

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public AdquirentesSolicitacaoITBIOnline() {
        criadoEm = System.nanoTime();
    }

    public CalculoSolicitacaoItbiOnline getCalculoITBIOnline() {
        return calculoSolicitacaoItbiOnline;
    }

    public void setCalculoITBIOnline(CalculoSolicitacaoItbiOnline calculoSolicitacaoItbiOnline) {
        this.calculoSolicitacaoItbiOnline = calculoSolicitacaoItbiOnline;
    }

    public Pessoa getAdquirente() {
        return adquirente;
    }

    public void setAdquirente(Pessoa adquirente) {
        this.adquirente = adquirente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return adquirente.getNome() + " " + adquirente.getCpf_Cnpj();
    }
}
