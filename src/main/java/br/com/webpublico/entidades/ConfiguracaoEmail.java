/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Configuração de e-mail")
public class ConfiguracaoEmail extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String host;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String port;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String username;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String email;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String password;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String protocol;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String tls;
    private String emailsPermitidos;
    @Transient
    private String sistema;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getTls() {
        return tls;
    }

    public void setTls(String tls) {
        this.tls = tls;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailsPermitidos() {
        return emailsPermitidos;
    }

    public void setEmailsPermitidos(String emailsPermitidos) {
        this.emailsPermitidos = emailsPermitidos;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }
}
