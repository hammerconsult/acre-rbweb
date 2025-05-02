package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "INTEGRACAOTRIBCONT")
public class IntegracaoTributarioContabil {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LoteIntegracaoTributarioContabil lote;
    @ManyToOne
    private ItemIntegracaoTributarioContabil item;
    @Transient
    private Long criadoEm;

    public IntegracaoTributarioContabil() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteIntegracaoTributarioContabil getLote() {
        return lote;
    }

    public void setLote(LoteIntegracaoTributarioContabil lote) {
        this.lote = lote;
    }

    public ItemIntegracaoTributarioContabil getItem() {
        return item;
    }

    public void setItem(ItemIntegracaoTributarioContabil item) {
        this.item = item;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {


        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
