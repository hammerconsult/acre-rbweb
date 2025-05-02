package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by renato on 30/08/2016.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class UsuarioPagina extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Página")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private PaginaPrefeituraPortal pagina;

    @Etiqueta("Página")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private UsuarioPaginaPortal usuario;

    public UsuarioPagina() {

    }

    public UsuarioPagina(UsuarioPaginaPortal usuario, PaginaPrefeituraPortal selecionado) {
        this.pagina = selecionado;
        this.usuario = usuario;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaginaPrefeituraPortal getPagina() {
        return pagina;
    }

    public void setPagina(PaginaPrefeituraPortal pagina) {
        this.pagina = pagina;
    }

    public UsuarioPaginaPortal getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioPaginaPortal usuario) {
        this.usuario = usuario;
    }
}
