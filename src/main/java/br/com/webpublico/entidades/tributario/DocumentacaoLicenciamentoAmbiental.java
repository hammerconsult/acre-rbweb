package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class DocumentacaoLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private UsuarioSistema usuarioRegistro;
    @OneToMany(mappedBy = "documentacaoLicenciamentoAmbiental", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoLicenciamentoAmbiental> documentos;

    public DocumentacaoLicenciamentoAmbiental() {
        super();
        this.dataRegistro = new Date();
        this.documentos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UsuarioSistema getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(UsuarioSistema usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public List<DocumentoLicenciamentoAmbiental> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoLicenciamentoAmbiental> documentos) {
        this.documentos = documentos;
    }

    public boolean hasDocumento(DocumentoLicenciamentoAmbiental documento) {
        if (documentos != null && !documentos.isEmpty()) {
            return documentos.stream().anyMatch(doc ->
                !Util.isEntidadesIguais(doc, documento)
                    && Util.isEntidadesIguais(doc.getAssuntoLicenciamentoAmbiental(), documento.getAssuntoLicenciamentoAmbiental())
                    && Util.isStringIgual(doc.getDescricaoReduzida(), documento.getDescricaoReduzida()));
        }
        return false;
    }
}
