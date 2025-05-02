package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 25/06/2018.
 */
@Entity
@Audited
public class AutorizacaoUsuarioRH extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    private TipoAutorizacaoRH tipoAutorizacaoRH;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoAutorizacaoRH getTipoAutorizacaoRH() {
        return tipoAutorizacaoRH;
    }

    public void setTipoAutorizacaoRH(TipoAutorizacaoRH tipoAutorizacaoRH) {
        this.tipoAutorizacaoRH = tipoAutorizacaoRH;
    }
}
