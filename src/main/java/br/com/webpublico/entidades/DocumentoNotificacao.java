package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 31/07/13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "DocumentoAcaoCobranca")
@Entity
@Cacheable
@Audited
@Etiqueta("Documento Ação Cobrança")
public class DocumentoNotificacao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.MERGE)
    private DocumentoOficial documentoOficial;
    @OneToMany(mappedBy = "documentoNotificacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoNotificacaoDAM> listaDocumentoNotificacaoDAM;
    @OneToOne
    private ItemNotificacao itemNotificacao;
    @Invisivel
    @Transient
    private Long criadoEm;

    public DocumentoNotificacao() {
        this.criadoEm = System.currentTimeMillis();
        this.documentoOficial = new DocumentoOficial();
        this.listaDocumentoNotificacaoDAM = Lists.newArrayList();
    }

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

    public List<DocumentoNotificacaoDAM> getListaDocumentoNotificacaoDAM() {
        return listaDocumentoNotificacaoDAM;
    }

    public void setListaDocumentoNotificacaoDAM(List<DocumentoNotificacaoDAM> listaDocumentoNotificacaoDAM) {
        this.listaDocumentoNotificacaoDAM = listaDocumentoNotificacaoDAM;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ItemNotificacao getItemNotificacao() {
        return itemNotificacao;
    }

    public void setItemNotificacao(ItemNotificacao itemNotificacao) {
        this.itemNotificacao = itemNotificacao;
    }

    public List<DAM> listaDAM() {
        List<DAM> retorno = new ArrayList<>();
        for (DocumentoNotificacaoDAM doc : listaDocumentoNotificacaoDAM) {
            retorno.add(doc.getDam());
        }
        return retorno;
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
