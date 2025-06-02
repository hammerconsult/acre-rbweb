package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAprovacaoEmendaUsuario;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Aprovação de Emenda")
public class AprovacaoEmenda extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Inicio de Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;
    @OneToMany(orphanRemoval = true, mappedBy = "aprovacaoEmenda", cascade = CascadeType.ALL)
    private List<AprovacaoEmendaUsuario> usuarios;

    public AprovacaoEmenda() {
        super();
        usuarios = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<AprovacaoEmendaUsuario> getUsuarios() {
        return usuarios;
    }

    public List<AprovacaoEmendaUsuario> getUsuariosPrefeitura() {
        List<AprovacaoEmendaUsuario> retorno = Lists.newArrayList();
        for (AprovacaoEmendaUsuario usuario : usuarios) {
            if (TipoAprovacaoEmendaUsuario.PREFEITURA.equals(usuario.getTipoAprovacaoEmendaUsuario())) {
                retorno.add(usuario);
            }
        }
        return retorno;
    }

    public List<AprovacaoEmendaUsuario> getUsuariosCamara() {
        List<AprovacaoEmendaUsuario> retorno = Lists.newArrayList();
        for (AprovacaoEmendaUsuario usuario : usuarios) {
            if (TipoAprovacaoEmendaUsuario.CAMARA.equals(usuario.getTipoAprovacaoEmendaUsuario())) {
                retorno.add(usuario);
            }
        }
        return retorno;
    }

    public void setUsuarios(List<AprovacaoEmendaUsuario> usuarios) {
        this.usuarios = usuarios;
    }
}
