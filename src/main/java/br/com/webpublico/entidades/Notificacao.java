package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Etiqueta("Notificação ao Usuário")
public class Notificacao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    private String titulo;
    private String descricao;
    private String link;
    @Enumerated(EnumType.STRING)
    private Gravidade gravidade;
    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipoNotificacao;
    private Boolean visualizado;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    private Date criadoEm;

    public Notificacao() {
        visualizado = Boolean.FALSE;
        criadoEm = new Date();
    }

    public Notificacao(Long id) {
        this.id = id;
    }

    public Notificacao(String titulo, String descricao, String link, Gravidade gravidade, TipoNotificacao tipoNotificacao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.link = link;
        this.gravidade = gravidade;
        this.tipoNotificacao = tipoNotificacao;
        this.visualizado = false;
        this.criadoEm = new Date();
    }

    public Notificacao(String titulo, String descricao, String link, Gravidade gravidade, UnidadeOrganizacional unidadeOrganizacional, TipoNotificacao tipoNotificacao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.link = link;
        this.gravidade = gravidade;
        this.tipoNotificacao = tipoNotificacao;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.visualizado = false;
        this.criadoEm = new Date();
    }

    public Notificacao(String titulo, String descricao, String link, Gravidade gravidade, TipoNotificacao tipoNotificacao, UsuarioSistema usuarioSistema) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.link = link;
        this.gravidade = gravidade;
        this.tipoNotificacao = tipoNotificacao;
        this.usuarioSistema = usuarioSistema;
        this.visualizado = false;
        this.criadoEm = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Gravidade getGravidade() {
        return gravidade;
    }

    public void setGravidade(Gravidade gravidade) {
        this.gravidade = gravidade;
    }

    public TipoNotificacao getTipoNotificacao() {
        return tipoNotificacao;
    }

    public void setTipoNotificacao(TipoNotificacao tipoNotificacao) {
        this.tipoNotificacao = tipoNotificacao;
    }

    public Boolean getVisualizado() {
        return visualizado;
    }

    public void setVisualizado(Boolean visualizado) {
        this.visualizado = visualizado;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    @Override
    public boolean equals(Object o) {

        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public enum Gravidade {
        ERRO("error"),
        ATENCAO("war"),
        INFORMACAO("info");
        private String descricao;

        private Gravidade(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {

            return descricao;
        }
    }

    @Override
    public String toString() {
        return descricao;
    }
}
