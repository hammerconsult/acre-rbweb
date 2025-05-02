package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by william on 18/09/17.
 */
@Entity
@Audited
public class ConfiguracaoEmailNfse extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String servidorSaidaSmtp;
    private String email;
    private String senha;
    private String porta;
    private Boolean autenticacaoSegura;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServidorSaidaSmtp() {
        return servidorSaidaSmtp;
    }

    public void setServidorSaidaSmtp(String servidorSaidaSmtp) {
        this.servidorSaidaSmtp = servidorSaidaSmtp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPorta() {
        return porta;
    }

    public void setPorta(String porta) {
        this.porta = porta;
    }

    public Boolean getAutenticacaoSegura() {
        return autenticacaoSegura;
    }

    public void setAutenticacaoSegura(Boolean autenticacaoSegura) {
        this.autenticacaoSegura = autenticacaoSegura;
    }

}
