package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
public class NotificacaoEmailEsocial extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ConfiguracaoEmpregadorESocial empregador;

    private Boolean eventoS1010;
    private Boolean eventoS2200;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "notificacaoEmailEsocial")
    private List<UsuarioEmailEsocial> usuarios;

    public NotificacaoEmailEsocial() {
        usuarios = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoEmpregadorESocial getEmpregador() {
        return empregador;
    }

    public void setEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        this.empregador = empregador;
    }

    public Boolean getEventoS1010() {
        return eventoS1010 != null ? eventoS1010 : false;
    }

    public void setEventoS1010(Boolean eventoS1010) {
        this.eventoS1010 = eventoS1010;
    }

    public List<UsuarioEmailEsocial> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioEmailEsocial> usuarios) {
        this.usuarios = usuarios;
    }

    public Boolean getEventoS2200() {
        return eventoS2200 != null ? eventoS2200 : false;
    }

    public void setEventoS2200(Boolean eventoS2200) {
        this.eventoS2200 = eventoS2200;
    }
}
