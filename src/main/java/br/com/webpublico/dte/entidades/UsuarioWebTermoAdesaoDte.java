package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class UsuarioWebTermoAdesaoDte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date registradoEm;

    @ManyToOne
    private UsuarioWeb usuarioWeb;

    @ManyToOne
    private TermoAdesaoDte termoAdesao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRegistradoEm() {
        return registradoEm;
    }

    public void setRegistradoEm(Date registradoEm) {
        this.registradoEm = registradoEm;
    }

    public UsuarioWeb getUsuarioWeb() {
        return usuarioWeb;
    }

    public void setUsuarioWeb(UsuarioWeb usuarioWeb) {
        this.usuarioWeb = usuarioWeb;
    }

    public TermoAdesaoDte getTermoAdesao() {
        return termoAdesao;
    }

    public void setTermoAdesao(TermoAdesaoDte termoAdesao) {
        this.termoAdesao = termoAdesao;
    }
}
