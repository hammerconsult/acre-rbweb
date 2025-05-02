package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 14/08/14
 * Time: 10:18
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Chamado")
@Audited
@Entity
public class RespostaChamado extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Mensagem")
    private String mensagem;
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuario;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Enviada Em")
    private Date enviadaEm;
    @Etiqueta("Chamado")
    @ManyToOne
    private Chamado chamado;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Visualizada Em")
    private Date visualizadaEm;

    public RespostaChamado() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public Date getEnviadaEm() {
        return enviadaEm;
    }

    public void setEnviadaEm(Date enviadaEm) {
        this.enviadaEm = enviadaEm;
    }

    public Chamado getChamado() {
        return chamado;
    }

    public void setChamado(Chamado chamado) {
        this.chamado = chamado;
    }

    public Date getVisualizadaEm() {
        return visualizadaEm;
    }

    public void setVisualizadaEm(Date visualizadaEm) {
        this.visualizadaEm = visualizadaEm;
    }

    @Override
    public String toString() {
        return usuario.getNome() + " - " + DataUtil.getDataFormatada(enviadaEm);
    }
}
