package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 14/08/14
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Chamado")
@Audited
@Entity
public class ConfiguracaoChamado extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(mappedBy = "configucao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoChamadoUsuario> usuarios;

    public ConfiguracaoChamado() {
        usuarios = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ConfiguracaoChamadoUsuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<ConfiguracaoChamadoUsuario> usuarios) {
        this.usuarios = usuarios;
    }


}
