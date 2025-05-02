package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Tributario")
@Entity
@Audited
public class SituacaoParcelaIntegracao implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SituacaoParcelaValorDivida situacao;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private ItemIntegracaoTributarioContabil itemItengracao;
    @Transient
    private Long criadoEm;

    public SituacaoParcelaIntegracao(){
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SituacaoParcelaValorDivida getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoParcelaValorDivida situacao) {
        this.situacao = situacao;
    }

    public ItemIntegracaoTributarioContabil getItemItengracao() {
        return itemItengracao;
    }

    public void setItemItengracao(ItemIntegracaoTributarioContabil itemItengracao) {
        this.itemItengracao = itemItengracao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
      return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
