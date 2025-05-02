package br.com.webpublico.entidades;

import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 16/10/14
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Detentor Documento")
public class DetentorDocto extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private DetentorDoctoFiscalLiquidacao detDoctoFiscalLiquidacao;

    @ManyToOne(cascade = CascadeType.ALL)
    private DoctoFiscalLiquidacao doctoFiscalLiquidacao;

    @Enumerated(EnumType.STRING)
    private SituacaoDocumentoFiscalEntradaMaterial situacao;

    public DetentorDocto() {
        super();
        this.doctoFiscalLiquidacao = new DoctoFiscalLiquidacao();
    }

    public DetentorDocto(DetentorDoctoFiscalLiquidacao detDoctoFiscalLiquidacao) {
        super();
        this.detDoctoFiscalLiquidacao = detDoctoFiscalLiquidacao;
        this.doctoFiscalLiquidacao = new DoctoFiscalLiquidacao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DetentorDoctoFiscalLiquidacao getDetDoctoFiscalLiquidacao() {
        return detDoctoFiscalLiquidacao;
    }

    public void setDetDoctoFiscalLiquidacao(DetentorDoctoFiscalLiquidacao detDoctoFiscalLiquidacao) {
        this.detDoctoFiscalLiquidacao = detDoctoFiscalLiquidacao;
    }

    public DoctoFiscalLiquidacao getDoctoFiscalLiquidacao() {
        return doctoFiscalLiquidacao;
    }

    public void setDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        this.doctoFiscalLiquidacao = doctoFiscalLiquidacao;
    }

    public SituacaoDocumentoFiscalEntradaMaterial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDocumentoFiscalEntradaMaterial situacao) {
        this.situacao = situacao;
    }
}
