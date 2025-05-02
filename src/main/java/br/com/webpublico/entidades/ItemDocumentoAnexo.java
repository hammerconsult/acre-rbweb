package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoFuncionalidadeParaAnexo;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 22/02/2016.
 */
@Entity
@Audited
public class ItemDocumentoAnexo {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DocumentosNecessariosAnexo documentosNecessariosAnexo;
    @Enumerated(EnumType.STRING)
    private TipoFuncionalidadeParaAnexo tipoFuncionalidadeParaAnexo;
    @ManyToOne
    private TipoDocumentoAnexo tipoDocumentoAnexo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoFuncionalidadeParaAnexo getTipoFuncionalidadeParaAnexo() {
        return tipoFuncionalidadeParaAnexo;
    }

    public void setTipoFuncionalidadeParaAnexo(TipoFuncionalidadeParaAnexo tipoFuncionalidadeParaAnexo) {
        this.tipoFuncionalidadeParaAnexo = tipoFuncionalidadeParaAnexo;
    }

    public TipoDocumentoAnexo getTipoDocumentoAnexo() {
        return tipoDocumentoAnexo;
    }

    public void setTipoDocumentoAnexo(TipoDocumentoAnexo tipoDocumentoAnexo) {
        this.tipoDocumentoAnexo = tipoDocumentoAnexo;
    }

    public DocumentosNecessariosAnexo getDocumentosNecessariosAnexo() {
        return documentosNecessariosAnexo;
    }

    public void setDocumentosNecessariosAnexo(DocumentosNecessariosAnexo documentosNecessariosAnexo) {
        this.documentosNecessariosAnexo = documentosNecessariosAnexo;
    }
}
