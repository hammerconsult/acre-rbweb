package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity
@Audited
@Etiqueta("Integração do Cadastro Imobiliário com o Sit")
public class IntegracaoSit implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    private Date dataIntegracao;
    private Boolean sucesso;
    private String mensagem;
    @Transient
    private String usuarioDaAuditoria;

    public IntegracaoSit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Date getDataIntegracao() {
        return dataIntegracao;
    }

    public void setDataIntegracao(Date dataIntegracao) {
        this.dataIntegracao = dataIntegracao;
    }

    public Boolean getSucesso() {
        return sucesso;
    }

    public void setSucesso(Boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem != null ? mensagem : "Não foi possível sincronizar!";
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getUsuarioDaAuditoria() {
        return usuarioDaAuditoria;
    }

    public void setUsuarioDaAuditoria(String usuarioDaAuditoria) {
        this.usuarioDaAuditoria = usuarioDaAuditoria;
    }
}
