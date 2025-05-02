package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Fale Conosco")
public class FaleConosco extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Enviado em")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvio;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Assunto")
    private String assunto;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Contribuinte")
    private String nome;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("E-mail")
    @Obrigatorio
    private String email;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Telefone")
    private String telefone;
    @Etiqueta("Mensagem")
    private String mensagem;
    @Etiqueta("Visualizado")
    @Tabelavel
    @Pesquisavel
    private Boolean visualizado;
    @Etiqueta("Resposta")
    private String resposta;
    @Etiqueta("Respondido em")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataResposta;
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    public FaleConosco() {
        dataResposta = new Date();
    }

    public FaleConosco(String assunto, String nome, String email, String telefone, String mensagem, Boolean visualizado) {
        this.assunto = assunto;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.mensagem = mensagem;
        this.visualizado = visualizado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Boolean getVisualizado() {
        return visualizado;
    }

    public void setVisualizado(Boolean visualizado) {
        this.visualizado = visualizado;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public Date getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(Date dataResposta) {
        this.dataResposta = dataResposta;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
