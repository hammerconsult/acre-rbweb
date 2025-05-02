package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Table(name="CADASTROECONOMICOHIST")
@Etiqueta("Histórico de Impressão de Cadastro Econômico")
public class CadastroEconomicoImpressaoHist extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;
    private String autenticidade;

    public CadastroEconomicoImpressaoHist(){

    }

    public CadastroEconomicoImpressaoHist(UsuarioSistema usuarioSistema, Date dataOperacao, CadastroEconomico cadastroEconomico){
        this.usuarioSistema = usuarioSistema;
        this.dataOperacao = dataOperacao;
        this.cadastroEconomico = cadastroEconomico;
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

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public String getAutenticidade() {
        return autenticidade;
    }

    public void setAutenticidade(String autenticidade) {
        this.autenticidade = autenticidade;
    }

}