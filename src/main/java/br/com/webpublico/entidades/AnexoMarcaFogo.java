package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaDocumentoMarcaFogo;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class AnexoMarcaFogo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private MarcaFogo marcaFogo;

    @ManyToOne
    private DocumentoMarcaFogo documentoMarcaFogo;

    @ManyToOne(cascade = CascadeType.ALL)
    private Arquivo arquivo;

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

    public DocumentoMarcaFogo getDocumentoMarcaFogo() {
        return documentoMarcaFogo;
    }

    public void setDocumentoMarcaFogo(DocumentoMarcaFogo documentoMarcaFogo) {
        this.documentoMarcaFogo = documentoMarcaFogo;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }
}
