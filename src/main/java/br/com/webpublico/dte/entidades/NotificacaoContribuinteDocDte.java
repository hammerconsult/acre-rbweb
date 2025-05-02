package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Table(name = "NOTIFICACAOCONTRIBDOCDTE")
public class NotificacaoContribuinteDocDte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private NotificacaoContribuinteDte notificacaoContribuinte;

    @ManyToOne
    private ModeloDocumentoDte modeloDocumento;

    private String conteudo;

    private String documento;

    @Temporal(TemporalType.TIMESTAMP)
    private Date visualizadoEm;

    @ManyToOne
    private UsuarioWeb visualizadoPor;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificacaoContribuinteDte getNotificacaoContribuinte() {
        return notificacaoContribuinte;
    }

    public void setNotificacaoContribuinte(NotificacaoContribuinteDte notificacaoContribuinte) {
        this.notificacaoContribuinte = notificacaoContribuinte;
    }

    public ModeloDocumentoDte getModeloDocumento() {
        return modeloDocumento;
    }

    public void setModeloDocumento(ModeloDocumentoDte modeloDocumento) {
        this.modeloDocumento = modeloDocumento;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Date getVisualizadoEm() {
        return visualizadoEm;
    }

    public void setVisualizadoEm(Date visualizadoEm) {
        this.visualizadoEm = visualizadoEm;
    }

    public UsuarioWeb getVisualizadoPor() {
        return visualizadoPor;
    }

    public void setVisualizadoPor(UsuarioWeb visualizadoPor) {
        this.visualizadoPor = visualizadoPor;
    }
}


