package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class CadastroRuralMarcaFogo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private MarcaFogo marcaFogo;

    @ManyToOne
    private CadastroRural cadastroRural;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public MarcaFogo getMarcaFogo() {
        return marcaFogo;
    }

    public void setMarcaFogo(MarcaFogo marcaFogo) {
        this.marcaFogo = marcaFogo;
    }

    public CadastroRural getCadastroRural() {
        return cadastroRural;
    }

    public void setCadastroRural(CadastroRural cadastroRural) {
        this.cadastroRural = cadastroRural;
    }
}
