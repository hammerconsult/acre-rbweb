package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@GrupoDiagrama(nome = "Tributário")
@Entity

@Audited
public class DenunciaFiscaisDesignados implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Denuncia denuncia;
    @ManyToOne
    private UsuarioSistema fiscalUsuarioSistema;
    @Transient
    @Invisivel
    private Long criadoEm;

    public DenunciaFiscaisDesignados() {
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Denuncia getDenuncia() {
        return denuncia;
    }

    public void setDenuncia(Denuncia denuncia) {
        this.denuncia = denuncia;
    }

    public UsuarioSistema getFiscalUsuarioSistema() {
        return fiscalUsuarioSistema;
    }

    public void setFiscalUsuarioSistema(UsuarioSistema fiscalUsuarioSistema) {
        this.fiscalUsuarioSistema = fiscalUsuarioSistema;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
