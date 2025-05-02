package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ITipoDocumentoAnexo;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author venom
 */
@Entity
@Audited
public class ObraAnexo implements Serializable, ValidadorEntidade, ITipoDocumentoAnexo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    private Obra obra;
    @Obrigatorio
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    @ManyToOne
    private TipoDocumentoAnexo tipoDocumentoAnexo;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ObraAnexo() {
        criadoEm = System.nanoTime();
    }

    public ObraAnexo(Obra obra, Arquivo arquivo) {
        this.obra = obra;
        this.arquivo = arquivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public TipoDocumentoAnexo getTipoDocumentoAnexo() {
        return tipoDocumentoAnexo;
    }

    public void setTipoDocumentoAnexo(TipoDocumentoAnexo tipoDocumentoAnexo) {
        this.tipoDocumentoAnexo = tipoDocumentoAnexo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ObraAnexo[ id=" + id + " ]";
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {

    }
}
