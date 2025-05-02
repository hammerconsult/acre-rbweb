package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAprovacaoEmendaUsuario;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Aprovação de Emenda - Usuário")
public class AprovacaoEmendaUsuario extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private AprovacaoEmenda aprovacaoEmenda;
    @Enumerated(EnumType.STRING)
    private TipoAprovacaoEmendaUsuario tipoAprovacaoEmendaUsuario;

    public AprovacaoEmendaUsuario() {
        super();
    }

    @Override
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

    public AprovacaoEmenda getAprovacaoEmenda() {
        return aprovacaoEmenda;
    }

    public void setAprovacaoEmenda(AprovacaoEmenda aprovacaoEmenda) {
        this.aprovacaoEmenda = aprovacaoEmenda;
    }

    public TipoAprovacaoEmendaUsuario getTipoAprovacaoEmendaUsuario() {
        return tipoAprovacaoEmendaUsuario;
    }

    public void setTipoAprovacaoEmendaUsuario(TipoAprovacaoEmendaUsuario tipoAprovacaoEmendaUsuario) {
        this.tipoAprovacaoEmendaUsuario = tipoAprovacaoEmendaUsuario;
    }
}
