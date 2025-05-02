package br.com.webpublico.dte.entidades;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Etiqueta("Notificação do Contribuinte - DTE")
@Entity
@Audited
public class NotificacaoContribuinteDte extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Cadastro Econômico")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private CadastroEconomico cadastroEconomico;

    @Etiqueta("Registrada em")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.TIMESTAMP)
    private Date registradaEm;

    @Etiqueta("Título")
    @Pesquisavel
    @Tabelavel
    private String titulo;

    private String conteudo;

    @Etiqueta("Visualizada?")
    @Pesquisavel
    private Boolean visualizada;

    @Etiqueta("Visualizada em")
    @Pesquisavel
    @Temporal(TemporalType.TIMESTAMP)
    private Date visualizadaEm;

    @Etiqueta("Visualizada por")
    @Pesquisavel
    @ManyToOne
    private UsuarioWeb visualizadaPor;

    @Etiqueta("Ciência?")
    @Pesquisavel
    private Boolean ciencia;

    @Temporal(TemporalType.TIMESTAMP)
    private Date cienciaAutomaticaEm;

    @Etiqueta("Ciência em")
    @Pesquisavel
    @Temporal(TemporalType.TIMESTAMP)
    private Date cienciaEm;

    @Etiqueta("Ciência por")
    @Pesquisavel
    @ManyToOne
    private UsuarioWeb cienciaPor;

    @OneToMany(mappedBy = "notificacaoContribuinte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificacaoContribuinteDocDte> documentos;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Date getRegistradaEm() {
        return registradaEm;
    }

    public void setRegistradaEm(Date registradaEm) {
        this.registradaEm = registradaEm;
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

    public Boolean getVisualizada() {
        return visualizada;
    }

    public void setVisualizada(Boolean visualizada) {
        this.visualizada = visualizada;
    }

    public Date getVisualizadaEm() {
        return visualizadaEm;
    }

    public void setVisualizadaEm(Date visualizadaEm) {
        this.visualizadaEm = visualizadaEm;
    }

    public UsuarioWeb getVisualizadaPor() {
        return visualizadaPor;
    }

    public void setVisualizadaPor(UsuarioWeb visualizadaPor) {
        this.visualizadaPor = visualizadaPor;
    }

    public Boolean getCiencia() {
        return ciencia;
    }

    public void setCiencia(Boolean ciencia) {
        this.ciencia = ciencia;
    }

    public Date getCienciaAutomaticaEm() {
        return cienciaAutomaticaEm;
    }

    public void setCienciaAutomaticaEm(Date cienciaAutomaticaEm) {
        this.cienciaAutomaticaEm = cienciaAutomaticaEm;
    }

    public Date getCienciaEm() {
        return cienciaEm;
    }

    public void setCienciaEm(Date cienciaEm) {
        this.cienciaEm = cienciaEm;
    }

    public UsuarioWeb getCienciaPor() {
        return cienciaPor;
    }

    public void setCienciaPor(UsuarioWeb cienciaPor) {
        this.cienciaPor = cienciaPor;
    }

    public List<NotificacaoContribuinteDocDte> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<NotificacaoContribuinteDocDte> documentos) {
        this.documentos = documentos;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
