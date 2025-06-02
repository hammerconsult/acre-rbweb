package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 20/05/14
 * Time: 11:30
 * To change this template use File | Settings | File Templates.
 */

@Etiqueta(value = "Unidades Orçamentárias do Usuario")
@GrupoDiagrama(nome = "Segurança")
@Audited
@Entity
@Table(name = "USUARIOUNIDADEORGANIZACORC")
public class UsuarioUnidadeOrganizacionalOrcamentaria extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    public UsuarioUnidadeOrganizacionalOrcamentaria() {
    }

    public UsuarioUnidadeOrganizacionalOrcamentaria(UsuarioSistema usuarioSistema, UnidadeOrganizacional unidadeOrganizacional) {
        this.usuarioSistema = usuarioSistema;
        this.unidadeOrganizacional = unidadeOrganizacional;
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

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }
}
