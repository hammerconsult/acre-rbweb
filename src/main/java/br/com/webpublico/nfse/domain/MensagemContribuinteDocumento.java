package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Etiqueta("Mensagem ao Contribuinte - Documento(s)")
@Table(name = "MENSAGEMCONTRIBUINTEDOC")
@Entity
@Audited
public class MensagemContribuinteDocumento extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private MensagemContribuinte mensagemContribuinte;

    private String descricaoDocumento;

    private Boolean obrigatorio;

    public MensagemContribuinteDocumento() {
        super();
        obrigatorio = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MensagemContribuinte getMensagemContribuinte() {
        return mensagemContribuinte;
    }

    public void setMensagemContribuinte(MensagemContribuinte mensagemContribuinte) {
        this.mensagemContribuinte = mensagemContribuinte;
    }

    public String getDescricaoDocumento() {
        return descricaoDocumento;
    }

    public void setDescricaoDocumento(String descricaoDocumento) {
        this.descricaoDocumento = descricaoDocumento;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }
}
