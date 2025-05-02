package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.enums.PortalTransparenciaSituacao;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by renat on 13/05/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class EntidadePortalTransparencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private PortalTransparenciaSituacao situacao;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo")
    private TipoObjetoPortalTransparencia tipo;

    public EntidadePortalTransparencia() {
        situacao = PortalTransparenciaSituacao.NAO_PUBLICADO;
    }

    public PortalTransparenciaSituacao getSituacao() {
        return situacao;
    }

    public void setSituacao(PortalTransparenciaSituacao situacao) {
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoObjetoPortalTransparencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoObjetoPortalTransparencia tipo) {
        this.tipo = tipo;
    }
}
