/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Associação entre o programa PPA e os diversos públicos-alvo favorecidos pelo
 * programa.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("PPA")
public class PublicoAlvoProgramaPPA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    private PublicoAlvo publicoAlvo;
    @ManyToOne
    private ProgramaPPA programaPPA;
    @Invisivel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToOne
    private PublicoAlvoProgramaPPA origem;
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;
    @Transient
    private Long criadoEm;

    public PublicoAlvoProgramaPPA(PublicoAlvo publicoAlvo, Date dataRegistro) {
        this.publicoAlvo = publicoAlvo;
        this.dataRegistro = dataRegistro;
        somenteLeitura = false;
        criadoEm = System.nanoTime();
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public PublicoAlvoProgramaPPA() {
        dataRegistro = new Date();
        criadoEm = System.nanoTime();
    }

    public PublicoAlvoProgramaPPA getOrigem() {
        return origem;
    }

    public void setOrigem(PublicoAlvoProgramaPPA origem) {
        this.origem = origem;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public PublicoAlvo getPublicoAlvo() {
        return publicoAlvo;
    }

    public void setPublicoAlvo(PublicoAlvo publicoAlvo) {
        this.publicoAlvo = publicoAlvo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PublicoAlvoProgramaPPA other = (PublicoAlvoProgramaPPA) obj;
        if (!Objects.equals(this.publicoAlvo, other.publicoAlvo)) {
            return false;
        }
        if (!Objects.equals(this.programaPPA, other.programaPPA)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + publicoAlvo;
    }
}
