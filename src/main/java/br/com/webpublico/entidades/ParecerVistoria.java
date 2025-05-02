package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusParecerVistoria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 17/01/14
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Alvara")
@Entity
@Audited
@Etiqueta("Parecer da Vistoria")
public class ParecerVistoria extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Vistoria")
    private Vistoria vistoria;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data")
    private Date data;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Status Parecer")
    private StatusParecerVistoria status;
    @Etiqueta("Motivo")
    @Obrigatorio
    private String motivo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vistoria getVistoria() {
        return vistoria;
    }

    public void setVistoria(Vistoria vistoria) {
        this.vistoria = vistoria;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public StatusParecerVistoria getStatus() {
        return status;
    }

    public void setStatus(StatusParecerVistoria status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
