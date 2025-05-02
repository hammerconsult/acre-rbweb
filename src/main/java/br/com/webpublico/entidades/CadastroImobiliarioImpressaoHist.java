package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Table(name="CADASTROIMOBILIARIOHIST")
@Etiqueta("Histórico de Impressão de Cadastro Imobiliário")
public class CadastroImobiliarioImpressaoHist extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;
    private String autenticidade;
    private boolean auditoria;

    public CadastroImobiliarioImpressaoHist(){
        this.auditoria = false;
    }

    public CadastroImobiliarioImpressaoHist(UsuarioSistema usuarioSistema, Date dataOperacao, CadastroImobiliario cadastroImobiliario, boolean auditoria){
        this.usuarioSistema = usuarioSistema;
        this.dataOperacao = dataOperacao;
        this.cadastroImobiliario = cadastroImobiliario;
        this.auditoria = auditoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public String getAutenticidade() {
        return autenticidade;
    }

    public void setAutenticidade(String autenticidade) {
        this.autenticidade = autenticidade;
    }

    public boolean isAuditoria() {
        return auditoria;
    }

    public void setAuditoria(boolean auditoria) {
        this.auditoria = auditoria;
    }
}
