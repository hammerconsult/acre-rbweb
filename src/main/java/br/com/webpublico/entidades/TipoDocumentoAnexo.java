package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDocumentoAnexoPNCP;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by hudson on 23/11/15.
 */

@Entity
@Audited
@Etiqueta("Tipo de Documento Anexo")
public class TipoDocumentoAnexo extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Tipo Anexo PNCP")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoDocumentoAnexoPNCP tipoAnexoPNCP;

    @Etiqueta("Descrição")
    @Obrigatorio
    @Length(maximo = 255)
    private String descricao;

    private Boolean ativo;


    public TipoDocumentoAnexo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoDocumentoAnexoPNCP getTipoAnexoPNCP() {
        return tipoAnexoPNCP;
    }

    public void setTipoAnexoPNCP(TipoDocumentoAnexoPNCP tipo) {
        this.tipoAnexoPNCP = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
