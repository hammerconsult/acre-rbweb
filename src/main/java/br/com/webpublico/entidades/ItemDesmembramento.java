package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Item do Desmembramento")
public class ItemDesmembramento implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Desmembramento desmembramento;
    @ManyToOne(cascade = CascadeType.ALL)
    private CadastroImobiliario originario;
    @Transient
    private Long criadoEm;

    public ItemDesmembramento() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Desmembramento getDesmembramento() {
        return desmembramento;
    }

    public void setDesmembramento(Desmembramento desmembramento) {
        this.desmembramento = desmembramento;
    }

    public CadastroImobiliario getOriginario() {
        return originario;
    }

    public void setOriginario(CadastroImobiliario originario) {
        this.originario = originario;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return originario.getInscricaoCadastral();
    }
}
