package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.nfse.enums.TipoMensagemContribuinte;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Etiqueta("Mensagem ao Contribuinte")
@Entity
@Audited
public class MensagemContribuinte extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;

    @Obrigatorio
    @Etiqueta("Emitida em")
    @Enumerated(EnumType.STRING)
    private TipoMensagemContribuinte tipoMensagemContribuinte;

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

    @Obrigatorio
    @Etiqueta("Conteúdo")
    private String conteudo;

    private Boolean enviarTodosUsuarios;

    @OneToMany(mappedBy = "mensagemContribuinte")
    private List<MensagemContribuinteUsuario> usuarios;

    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "mensagemContribuinte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MensagemContribuinteDocumento> documentos;

    public MensagemContribuinte() {
        super();
        this.enviarTodosUsuarios = Boolean.FALSE;
        this.tipoMensagemContribuinte = TipoMensagemContribuinte.INFORMACAO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoMensagemContribuinte getTipoMensagemContribuinte() {
        return tipoMensagemContribuinte;
    }

    public void setTipoMensagemContribuinte(TipoMensagemContribuinte tipoMensagemContribuinte) {
        this.tipoMensagemContribuinte = tipoMensagemContribuinte;
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

    public Boolean getEnviarTodosUsuarios() {
        return enviarTodosUsuarios != null ? enviarTodosUsuarios : Boolean.FALSE;
    }

    public void setEnviarTodosUsuarios(Boolean enviarTodosUsuarios) {
        this.enviarTodosUsuarios = enviarTodosUsuarios;
    }

    public List<MensagemContribuinteUsuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<MensagemContribuinteUsuario> usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<MensagemContribuinteDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<MensagemContribuinteDocumento> documentos) {
        this.documentos = documentos;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
    }
}
