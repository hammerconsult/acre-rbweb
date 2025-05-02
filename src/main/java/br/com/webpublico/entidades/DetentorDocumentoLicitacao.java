package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
public class DetentorDocumentoLicitacao extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "detentorDocumentoLicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoLicitacao> documentoLicitacaoList;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DocumentoLicitacao> getDocumentoLicitacaoList() {
        return documentoLicitacaoList;
    }

    public void setDocumentoLicitacaoList(List<DocumentoLicitacao> documentoLicitacaoList) {
        this.documentoLicitacaoList = documentoLicitacaoList;
    }

}
