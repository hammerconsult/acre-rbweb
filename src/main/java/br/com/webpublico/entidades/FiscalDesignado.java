/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
@Entity
@Audited
public class FiscalDesignado extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AcaoFiscal acaoFiscal;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

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

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    @Override
    public String toString() {
        if (usuarioSistema.getPessoaFisica() != null) {
            if (usuarioSistema.getPessoaFisica().getNome() != null) {
                return usuarioSistema.getPessoaFisica().getNome();
            } else {
                return usuarioSistema.getLogin();
            }

        }
        return usuarioSistema.getLogin();
    }
}
