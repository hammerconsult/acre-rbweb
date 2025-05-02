/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@GrupoDiagrama(nome = "Tributário")
@Entity
@Audited
public class UsuarioAlvaraSeker implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    Long idUsuario;
    String nomeUsuario;
    String cargoUsuario;
    String loginUsuario;
    String senhaUsuario;
    String cpfUsuario;
    String matUsuario;
    String telUsuario;
    String celUsuario;
    String emailUsuario;
    String dataCadastro;
    Long idUniOrg;
    String emailAlternativo;
    Long field14;

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getCargoUsuario() {
        return cargoUsuario;
    }

    public void setCargoUsuario(String cargoUsuario) {
        this.cargoUsuario = cargoUsuario;
    }

    public String getLoginUsuario() {
        return loginUsuario;
    }

    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }

    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    public void setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpfUsuario = cpfUsuario;
    }

    public String getMatUsuario() {
        return matUsuario;
    }

    public void setMatUsuario(String matUsuario) {
        this.matUsuario = matUsuario;
    }

    public String getTelUsuario() {
        return telUsuario;
    }

    public void setTelUsuario(String telUsuario) {
        this.telUsuario = telUsuario;
    }

    public String getCelUsuario() {
        return celUsuario;
    }

    public void setCelUsuario(String celUsuario) {
        this.celUsuario = celUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Long getIdUniOrg() {
        return idUniOrg;
    }

    public void setIdUniOrg(Long idUniOrg) {
        this.idUniOrg = idUniOrg;
    }

    public String getEmailAlternativo() {
        return emailAlternativo;
    }

    public void setEmailAlternativo(String emailAlternativo) {
        this.emailAlternativo = emailAlternativo;
    }

    public Long getField14() {
        return field14;
    }

    public void setField14(Long field14) {
        this.field14 = field14;
    }
}
