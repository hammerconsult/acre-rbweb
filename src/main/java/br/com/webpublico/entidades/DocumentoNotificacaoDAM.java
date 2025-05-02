package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 31/07/13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "DocumentoAcaoCobrancaDAM")
@Entity
@Cacheable
@Audited
public class DocumentoNotificacaoDAM implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.MERGE)
    private DAM dam;
    @ManyToOne(cascade = CascadeType.MERGE)
    private DocumentoNotificacao documentoNotificacao;
    @Invisivel
    @Transient
    private Long criadoEm;

    public DocumentoNotificacaoDAM() {
        this.criadoEm = System.currentTimeMillis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }

    public DocumentoNotificacao getDocumentoNotificacao() {
        return documentoNotificacao;
    }

    public void setDocumentoNotificacao(DocumentoNotificacao documentoNotificacao) {
        this.documentoNotificacao = documentoNotificacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
