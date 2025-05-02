package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class DocumentoLicenciamentoAmbiental extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DocumentacaoLicenciamentoAmbiental documentacaoLicenciamentoAmbiental;
    @ManyToOne
    private AssuntoLicenciamentoAmbiental assuntoLicenciamentoAmbiental;
    @Obrigatorio
    @Etiqueta("Descrição Reduzida")
    private String descricaoReduzida;
    @Obrigatorio
    @Etiqueta("Descrição Completa")
    private String descricaoCompleta;
    @Obrigatorio
    @Etiqueta("Extensões Permitidas")
    private String extensoesPermitidas;
    private Boolean ativo;

    public DocumentoLicenciamentoAmbiental() {
        super();
        ativo = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentacaoLicenciamentoAmbiental getDocumentacaoLicenciamentoAmbiental() {
        return documentacaoLicenciamentoAmbiental;
    }

    public void setDocumentacaoLicenciamentoAmbiental(DocumentacaoLicenciamentoAmbiental documentacaoLicenciamentoAmbiental) {
        this.documentacaoLicenciamentoAmbiental = documentacaoLicenciamentoAmbiental;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public String getDescricaoCompleta() {
        return descricaoCompleta;
    }

    public void setDescricaoCompleta(String descricaoCompleta) {
        this.descricaoCompleta = descricaoCompleta;
    }

    public String getExtensoesPermitidas() {
        return extensoesPermitidas;
    }

    public void setExtensoesPermitidas(String extensoesPermitidas) {
        this.extensoesPermitidas = extensoesPermitidas;
    }

    public AssuntoLicenciamentoAmbiental getAssuntoLicenciamentoAmbiental() {
        return assuntoLicenciamentoAmbiental;
    }

    public void setAssuntoLicenciamentoAmbiental(AssuntoLicenciamentoAmbiental assuntoLicenciamentoAmbiental) {
        this.assuntoLicenciamentoAmbiental = assuntoLicenciamentoAmbiental;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return descricaoReduzida;
    }
}
