package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Criado por Mateus
 * Data: 15/03/2017.
 */
@Entity
@Audited
@Etiqueta("Memorando")
public class Memorando extends SuperEntidade implements Serializable, PossuidorArquivo {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Usuário de Origem")
    private UsuarioSistema usuarioOrigem;
    @Etiqueta("Para")
    @Enumerated(EnumType.STRING)
    private Para para;
    @ManyToOne
    @Etiqueta("Usuário de Destino")
    private UsuarioSistema usuarioDestino;
    @ManyToOne
    @Etiqueta("Pessoa de Destino")
    private Pessoa pessoaDestino;
    @ManyToOne
    @Etiqueta("Pessoa de Origem")
    private Pessoa pessoaOrigem;
    @Length(maximo = 70)
    @Etiqueta("Título")
    private String titulo;
    @Etiqueta("Conteúdo")
    private String conteudo;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data de Envio")
    private Date dataEnvio;
    @OneToOne(cascade = CascadeType.MERGE)
    private Memorando memorandoOrigem;
    private Boolean visualizado;
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    private Boolean removido;

    public Memorando() {
        super();
        visualizado = Boolean.FALSE;
        removido = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioOrigem() {
        return usuarioOrigem;
    }

    public void setUsuarioOrigem(UsuarioSistema usuarioOrigem) {
        this.usuarioOrigem = usuarioOrigem;
    }

    public UsuarioSistema getUsuarioDestino() {
        return usuarioDestino;
    }

    public void setUsuarioDestino(UsuarioSistema usuarioDestino) {
        this.usuarioDestino = usuarioDestino;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo.length() > 70) {
            this.titulo = titulo.substring(0, 70);
        } else {
            this.titulo = titulo;
        }
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getConteudoResumido() {
        String conteudo = this.conteudo != null ? this.conteudo : "";
        if (conteudo.length() > 100) {
            conteudo = conteudo.substring(0, 99) + "...";
        }
        return conteudo.replaceAll("\\<.*?>", "");
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Memorando getMemorandoOrigem() {
        return memorandoOrigem;
    }

    public void setMemorandoOrigem(Memorando memorandoOrigem) {
        this.memorandoOrigem = memorandoOrigem;
    }

    public Boolean getVisualizado() {
        return visualizado;
    }

    public void setVisualizado(Boolean visualizado) {
        this.visualizado = visualizado;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Boolean getRemovido() {
        return removido;
    }

    public void setRemovido(Boolean removido) {
        this.removido = removido;
    }

    public Para getPara() {
        return para;
    }

    public void setPara(Para para) {
        this.para = para;
    }

    public Pessoa getPessoaDestino() {
        return pessoaDestino;
    }

    public void setPessoaDestino(Pessoa pessoaDestino) {
        this.pessoaDestino = pessoaDestino;
    }

    public Pessoa getPessoaOrigem() {
        return pessoaOrigem;
    }

    public void setPessoaOrigem(Pessoa pessoaOrigem) {
        this.pessoaOrigem = pessoaOrigem;
    }

    public enum Para {
        USUARIO("Usuário do Sistema"),
        UNIDADE("Unidade Administrativa"),
        PESSOA("Contribuinte");

        private String descricao;

        Para(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
