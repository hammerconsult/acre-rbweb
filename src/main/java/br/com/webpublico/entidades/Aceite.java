package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wanderley
 * Date: 29/11/13
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Cacheable
@Audited
public class Aceite extends SuperEntidade{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @OneToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;
    private String observacoes;

    public Aceite() {
    }

    public Aceite(UsuarioSistema usuarioSistema, Date dataOperacao) {
        this.usuarioSistema = usuarioSistema;
        this.dataOperacao = dataOperacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
