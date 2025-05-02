package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.ITipoDocumentoAnexo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author venom
 */
@Entity
@Audited
public class ObraMedicaoAnexo implements Serializable, ITipoDocumentoAnexo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    private ObraMedicao obraMedicao;

    @Obrigatorio
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;

    @Etiqueta(" Tipo Documento ")
    @Obrigatorio
    @ManyToOne
    private TipoDocumentoAnexo tipoDocumentoAnexo;
    @Transient
    private Long criadoEm;

    public ObraMedicaoAnexo() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObraMedicao getObraMedicao() {
        return obraMedicao;
    }

    public void setObraMedicao(ObraMedicao obraMedicao) {
        if (this.obraMedicao == null) {
            this.obraMedicao = obraMedicao;
        } else {
            this.obraMedicao.delAnexo(this);
            this.obraMedicao = obraMedicao;
        }
        obraMedicao.addAnexo(this);
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
        return "br.com.webpublico.entidades.ObraMedicaoAnexo[ id=" + id + " ]";
    }

}
