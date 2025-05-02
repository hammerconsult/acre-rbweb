package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class LoteNotificacaoDocDte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LoteNotificacaoDte lote;

    @ManyToOne
    private ModeloDocumentoDte modeloDocumento;

    private String conteudo;

    public LoteNotificacaoDocDte() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteNotificacaoDte getLote() {
        return lote;
    }

    public void setLote(LoteNotificacaoDte lote) {
        this.lote = lote;
    }

    public ModeloDocumentoDte getModeloDocumento() {
        return modeloDocumento;
    }

    public void setModeloDocumento(ModeloDocumentoDte modeloDocumento) {
        this.modeloDocumento = modeloDocumento;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
