/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Renato
 */
@Entity
@Audited
@Etiqueta("Termo Geral Fiscalizacao")
@GrupoDiagrama(nome = "fiscalizacaogeral")

public class TermoGeralFiscalizacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "termoGeralFiscalizacao")
    private List<ValorAtributoFiscalizacao> valoresAtributosFiscalizacao;
    @OneToOne(cascade = CascadeType.ALL)
    private DocumentoOficial documentoOficial;
    @ManyToOne
    private ProcessoFiscalizacao processoFiscalizacao;
    @Transient
    private Long criadoEm;

    public TermoGeralFiscalizacao() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ValorAtributoFiscalizacao> getValoresAtributosFiscalizacao() {
        return valoresAtributosFiscalizacao;
    }

    public void setValoresAtributosFiscalizacao(List<ValorAtributoFiscalizacao> valoresAtributosFiscalizacao) {
        this.valoresAtributosFiscalizacao = valoresAtributosFiscalizacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ProcessoFiscalizacao getProcessoFiscalizacao() {
        return processoFiscalizacao;
    }

    public void setProcessoFiscalizacao(ProcessoFiscalizacao processoFiscalizacao) {
        this.processoFiscalizacao = processoFiscalizacao;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }


    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
