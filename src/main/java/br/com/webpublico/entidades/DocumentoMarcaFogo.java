package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaDocumentoMarcaFogo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class DocumentoMarcaFogo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ParametroMarcaFogo parametroMarcaFogo;

    @Obrigatorio
    @Etiqueta("Natureza do Documento")
    @Enumerated(EnumType.STRING)
    private NaturezaDocumentoMarcaFogo naturezaDocumento;

    @Obrigatorio
    @Etiqueta("Descrição do Documento")
    private String descricao;

    @Obrigatorio
    @Etiqueta("Extensões Permitidas")
    private String extensoesPermitidas;

    private Boolean obrigatorio;

    private Boolean ativo;

    public DocumentoMarcaFogo() {
        super();
        this.obrigatorio = Boolean.FALSE;
        this.ativo = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ParametroMarcaFogo getParametroMarcaFogo() {
        return parametroMarcaFogo;
    }

    public void setParametroMarcaFogo(ParametroMarcaFogo parametroMarcaFogo) {
        this.parametroMarcaFogo = parametroMarcaFogo;
    }

    public NaturezaDocumentoMarcaFogo getNaturezaDocumento() {
        return naturezaDocumento;
    }

    public void setNaturezaDocumento(NaturezaDocumentoMarcaFogo naturezaDocumento) {
        this.naturezaDocumento = naturezaDocumento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getExtensoesPermitidas() {
        return extensoesPermitidas;
    }

    public void setExtensoesPermitidas(String extensoesPermitidas) {
        this.extensoesPermitidas = extensoesPermitidas;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
