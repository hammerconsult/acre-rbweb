package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.TermoUso;

import javax.persistence.*;
import java.util.Date;

@Entity
public class TermoUsoNotaPremiada extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAceite;

    @ManyToOne
    private TermoUso termoUso;

    @ManyToOne
    private UsuarioNotaPremiada usuario;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAceite() {
        return dataAceite;
    }

    public void setDataAceite(Date dataAceite) {
        this.dataAceite = dataAceite;
    }

    public TermoUso getTermoUso() {
        return termoUso;
    }

    public void setTermoUso(TermoUso termoUso) {
        this.termoUso = termoUso;
    }

    public UsuarioNotaPremiada getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioNotaPremiada usuario) {
        this.usuario = usuario;
    }
}
