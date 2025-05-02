/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Entity
@Table(name = "CONFPREVIDENCIAVINCULOFP")
public class ConfiguracaoPrevidenciaVinculoFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoModalidadeContratoFP confModalidadeContratoFP;
    @Pesquisavel
    @ManyToOne
    private TipoPrevidenciaFP tipoPrevidenciaFP;
    @Transient
    private Long criadoEm;

    public ConfiguracaoPrevidenciaVinculoFP() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoModalidadeContratoFP getConfModalidadeContratoFP() {
        return confModalidadeContratoFP;
    }

    public void setConfModalidadeContratoFP(ConfiguracaoModalidadeContratoFP confModalidadeContratoFP) {
        this.confModalidadeContratoFP = confModalidadeContratoFP;
    }

    public TipoPrevidenciaFP getTipoPrevidenciaFP() {
        return tipoPrevidenciaFP;
    }

    public void setTipoPrevidenciaFP(TipoPrevidenciaFP tipoPrevidenciaFP) {
        this.tipoPrevidenciaFP = tipoPrevidenciaFP;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        if (tipoPrevidenciaFP != null) {
            return tipoPrevidenciaFP + "";
        }
        return "instanciado";
    }
}
