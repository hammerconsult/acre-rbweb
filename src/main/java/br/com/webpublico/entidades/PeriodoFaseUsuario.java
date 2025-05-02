package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 17/10/14
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Segurança")
@Entity
@Audited
public class PeriodoFaseUsuario extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private PeriodoFaseUnidade periodoFaseUnidade;

    public PeriodoFaseUsuario() {

    }

    public PeriodoFaseUsuario(UsuarioSistema usuarioSistema, PeriodoFaseUnidade periodoFaseUnidade) {
        this.usuarioSistema = usuarioSistema;
        this.periodoFaseUnidade = periodoFaseUnidade;
    }

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

    public PeriodoFaseUnidade getPeriodoFaseUnidade() {
        return periodoFaseUnidade;
    }

    public void setPeriodoFaseUnidade(PeriodoFaseUnidade periodoFaseUnidade) {
        this.periodoFaseUnidade = periodoFaseUnidade;
    }

    @Override
    public String toString() {
        return usuarioSistema.toString();
    }
}
