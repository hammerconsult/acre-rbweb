/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity
@GrupoDiagrama(nome = "RBTrans")
@Audited
@Etiqueta("Parâmetros de Trânsito do RBTRANS")
@Inheritance(strategy = InheritanceType.JOINED)
public class ParametrosTransitoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;
    @ManyToOne
    @Etiqueta("Responsável")
    @Tabelavel
    @Obrigatorio
    private UsuarioSistema alteradoPor;
    private String Tipo;
    @Transient
    @Invisivel
    private Long criadoEm;

    public ParametrosTransitoRBTrans() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public UsuarioSistema getAlteradoPor() {
        return alteradoPor;
    }

    public void setAlteradoPor(UsuarioSistema alteradoPor) {
        this.alteradoPor = alteradoPor;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String Tipo) {
        this.Tipo = Tipo;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + this.getId();
    }

    public boolean isParametroTransitoTransporte() {
        return this instanceof ParametrosTransitoTransporte;
    }


    public boolean isParametroFiscalizacaoRBTrans() {
        return this instanceof ParametrosFiscalizacaoRBTrans;
    }
}
