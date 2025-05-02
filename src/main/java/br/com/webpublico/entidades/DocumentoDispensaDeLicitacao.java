package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 09/07/14
 * Time: 18:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "DOCUMENTODISPLIC")
@Etiqueta("Documento Dispensa de Licitação")
public class DocumentoDispensaDeLicitacao implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Dispensa de Licitação")
    @ManyToOne
    private DispensaDeLicitacao dispensaDeLicitacao;

    @Obrigatorio
    @Etiqueta("Documento")
    @ManyToOne
    private DoctoHabilitacao documentoHabilitacao;

    @Invisivel
    @Transient
    private Long criadoEm;

    public DocumentoDispensaDeLicitacao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        return dispensaDeLicitacao;
    }

    public void setDispensaDeLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        this.dispensaDeLicitacao = dispensaDeLicitacao;
    }

    public DoctoHabilitacao getDocumentoHabilitacao() {
        return documentoHabilitacao;
    }

    public void setDocumentoHabilitacao(DoctoHabilitacao documentoHabilitacao) {
        this.documentoHabilitacao = documentoHabilitacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }
}
