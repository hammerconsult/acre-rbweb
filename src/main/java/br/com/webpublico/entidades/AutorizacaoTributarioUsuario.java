/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 *
 * @author Fabio
 */
@Etiqueta(value = "Autorização do Usuário do Tributário")
@GrupoDiagrama(nome = "Segurança")
@Audited
@Entity
@Table(name = "AUTORIZACAOTRIBUSUARIO")
public class AutorizacaoTributarioUsuario extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VigenciaTribUsuario vigenciaTribUsuario;
    @Enumerated(EnumType.STRING)
    private AutorizacaoTributario autorizacao;

    public AutorizacaoTributarioUsuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AutorizacaoTributario getAutorizacao() {
        return autorizacao;
    }

    public void setAutorizacao(AutorizacaoTributario autorizacao) {
        this.autorizacao = autorizacao;
    }

    public VigenciaTribUsuario getVigenciaTribUsuario() {
        return vigenciaTribUsuario;
    }

    public void setVigenciaTribUsuario(VigenciaTribUsuario vigenciaTribUsuario) {
        this.vigenciaTribUsuario = vigenciaTribUsuario;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.AutorizacaoTributarioUsuario[ id=" + id + " ]";
    }
}
