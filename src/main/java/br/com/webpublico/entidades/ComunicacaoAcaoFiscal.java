/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Alex
 * @since 09/03/2017 14:40
 */
@GrupoDiagrama(nome = "Fiscalizacao")
@Entity

@Audited
public class ComunicacaoAcaoFiscal extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AcaoFiscal acaoFiscal;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvioEmail;
    private String textoEmail;
    private String nomeArquivo;
    private String emailsEnvio;
    @ManyToOne
    private UsuarioSistema usuario;

    public ComunicacaoAcaoFiscal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AcaoFiscal getAcaoFiscal() {
        return acaoFiscal;
    }

    public void setAcaoFiscal(AcaoFiscal acaoFiscal) {
        this.acaoFiscal = acaoFiscal;
    }

    public Date getDataEnvioEmail() {
        return dataEnvioEmail;
    }

    public void setDataEnvioEmail(Date dataEnvioEmail) {
        this.dataEnvioEmail = dataEnvioEmail;
    }

    public String getTextoEmail() {
        return textoEmail;
    }

    public void setTextoEmail(String textoEmail) {
        this.textoEmail = textoEmail;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getEmailsEnvio() {
        return emailsEnvio;
    }

    public void setEmailsEnvio(String emailsEnvio) {
        this.emailsEnvio = emailsEnvio;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }
}
