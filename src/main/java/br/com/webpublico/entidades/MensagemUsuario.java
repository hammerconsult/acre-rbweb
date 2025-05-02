package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Etiqueta("Mensagem ao Usuario")
@Entity
@Audited
public class MensagemUsuario extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @Obrigatorio
    @Etiqueta("Emitida em")
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.TIMESTAMP)
    private Date emitidaEm;

    @ManyToOne
    private UsuarioSistema enviadaPor;

    @Obrigatorio
    @Etiqueta("Título")
    @Tabelavel
    @Pesquisavel
    private String titulo;
    private String cronPublicar;
    private String cronRemover;
    @Obrigatorio
    @Etiqueta("Conteúdo")
    private String conteudo;
    private Boolean bloqueiaAcesso;
    private Boolean publicada;
    private Boolean ativo;


    public MensagemUsuario() {
        super();
        this.bloqueiaAcesso = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEmitidaEm() {
        return emitidaEm;
    }

    public void setEmitidaEm(Date emitidaEm) {
        this.emitidaEm = emitidaEm;
    }

    public UsuarioSistema getEnviadaPor() {
        return enviadaPor;
    }

    public void setEnviadaPor(UsuarioSistema enviadaPor) {
        this.enviadaPor = enviadaPor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Boolean getBloqueiaAcesso() {
        return bloqueiaAcesso != null && bloqueiaAcesso;
    }

    public void setBloqueiaAcesso(Boolean bloqueiaAcesso) {
        this.bloqueiaAcesso = bloqueiaAcesso;
    }

    public String getCronPublicar() {
        return cronPublicar;
    }

    public void setCronPublicar(String cronPublicar) {
        this.cronPublicar = cronPublicar;
    }

    public String getCronRemover() {
        return cronRemover;
    }

    public void setCronRemover(String cronRemover) {
        this.cronRemover = cronRemover;
    }

    public Boolean getPublicada() {
        return publicada;
    }

    public void setPublicada(Boolean publicada) {
        this.publicada = publicada;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
