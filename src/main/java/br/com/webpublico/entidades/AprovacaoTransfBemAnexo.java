package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.ITipoDocumentoAnexo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class AprovacaoTransfBemAnexo extends SuperEntidade implements ITipoDocumentoAnexo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Aprovação")
    private AprovacaoTransferenciaBem aprovacao;

    @Obrigatorio
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Etiqueta("Arquivo")
    private Arquivo arquivo;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Tipo de Documento ")
    private TipoDocumentoAnexo tipoDocumentoAnexo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AprovacaoTransferenciaBem getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(AprovacaoTransferenciaBem aprovacao) {
        this.aprovacao = aprovacao;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public TipoDocumentoAnexo getTipoDocumentoAnexo() {
        return tipoDocumentoAnexo;
    }

    public void setTipoDocumentoAnexo(TipoDocumentoAnexo tipoDocumentoAnexo) {
        this.tipoDocumentoAnexo = tipoDocumentoAnexo;
    }
}
