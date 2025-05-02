package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "SOLICITACAOMATDOCOFIC")
public class SolicitacaoMaterialDocumentoOficial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    @ManyToOne
    private SolicitacaoMaterial solicitacaoMaterial;
    private Integer versao;
    @Transient
    private Boolean ultimaVersao;

    public SolicitacaoMaterialDocumentoOficial() {
    }

    public SolicitacaoMaterialDocumentoOficial(DocumentoOficial documentoOficial, SolicitacaoMaterial solicitacaoMaterial, Integer versao) {
        this.documentoOficial = documentoOficial;
        this.solicitacaoMaterial = solicitacaoMaterial;
        this.versao = versao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return solicitacaoMaterial;
    }

    public void setSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        this.solicitacaoMaterial = solicitacaoMaterial;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public Boolean getUltimaVersao() {
        return ultimaVersao == null ? Boolean.FALSE : ultimaVersao;
    }

    public void setUltimaVersao(Boolean ultimaVersao) {
        this.ultimaVersao = ultimaVersao;
    }
}
