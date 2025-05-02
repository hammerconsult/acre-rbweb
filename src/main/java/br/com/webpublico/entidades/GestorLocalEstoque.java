package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 21/03/14
 * Time: 11:01
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@Etiqueta("Gestor de Local de Estoque")
public class GestorLocalEstoque extends SuperEntidade implements ValidadorVigencia {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Etiqueta("Local de Estoque")
    @ManyToOne
    private LocalEstoque localEstoque;

    @Tabelavel
    @Etiqueta("Gestor")
    @ManyToOne
    @Obrigatorio
    private UsuarioSistema usuarioSistema;

    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início da Vigência")
    @Obrigatorio
    private Date inicioVigencia;

    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim da Vigência")
    private Date fimVigencia;

    public GestorLocalEstoque() {
        super();
    }

    public GestorLocalEstoque(UsuarioSistema us, LocalEstoque localEstoque) {
        super();
        this.usuarioSistema = us;
        this.localEstoque = localEstoque;
    }

    public GestorLocalEstoque(GestorLocalEstoque gestorLocalEstoque, LocalEstoque localEstoque) {
        super();
        this.usuarioSistema = gestorLocalEstoque.getUsuarioSistema();
        this.localEstoque = localEstoque;
        this.setInicioVigencia(gestorLocalEstoque.getInicioVigencia());
        this.setFimVigencia(gestorLocalEstoque.getFimVigencia());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    @Override
    public String toString() {
        return usuarioSistema.toString();
    }
}
