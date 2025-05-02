/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.DependenteVinculoPortal;
import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@CorEntidade(value = "#00FFFF")
@Etiqueta("Dependente do Vínculo da FP")
public class DependenteVinculoFP extends SuperEntidade implements Comparable<DependenteVinculoFP>, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início da Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Fim da Vigência")
    private Date finalVigencia;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    private Dependente dependente;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Etiqueta("Tipo de Dependente")
    @ManyToOne
    private TipoDependente tipoDependente;

    public TipoDependente getTipoDependente() {
        return tipoDependente;
    }

    public void setTipoDependente(TipoDependente tipoDependente) {
        this.tipoDependente = tipoDependente;
    }

    public void DependenteVinculoFP() {
        dataRegistro = new Date();
    }

    public Dependente getDependente() {
        return dependente;
    }

    public void setDependente(Dependente dependente) {
        this.dependente = dependente;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return finalVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    @Override
    public String toString() {
        return inicioVigencia + " - " + finalVigencia + " - " + dependente;
    }

    public String descricaoCompleta() {
        return (inicioVigencia != null ? Util.dateToString(inicioVigencia) : " ") + " à " + (finalVigencia != null ? Util.dateToString(finalVigencia) : " Atualmente ") + (tipoDependente != null ? " - " + tipoDependente + " - Idade Max.: " + tipoDependente.getIdadeMaxima() : "");
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public int compareTo(DependenteVinculoFP dependenteVinculoFP) {
        if (dependente != null && dependenteVinculoFP.getDependente() != null) {
            return this.dependente.getDependente().compareTo(dependenteVinculoFP.dependente.getDependente());
        }
        return 0;
    }

    public static List<DependenteVinculoFP> toDependenteVinculoFPs(Dependente dependente, List<DependenteVinculoPortal> teles) {
        if (teles != null && !teles.isEmpty()) {
            List<DependenteVinculoFP> dependenteVinculoFPS = Lists.newLinkedList();
            for (DependenteVinculoPortal dependenteVinculoPortal : teles) {
                DependenteVinculoFP dto = toDependenteVinculoFP(dependente, dependenteVinculoPortal);
                if (dto != null) {
                    dependenteVinculoFPS.add(dto);
                }
            }
            return dependenteVinculoFPS;
        }
        return null;
    }

    public static DependenteVinculoFP toDependenteVinculoFP(Dependente dependente, DependenteVinculoPortal dependentePortal) {
        if (dependentePortal != null) {
            DependenteVinculoFP dependenteVinculoFP = new DependenteVinculoFP();
            dependenteVinculoFP.setDataRegistro(new Date());
            dependenteVinculoFP.setInicioVigencia(dependentePortal.getInicioVigencia());
            dependenteVinculoFP.setFinalVigencia(dependentePortal.getFinalVigencia());
            dependenteVinculoFP.setDependente(dependente);
            dependenteVinculoFP.setTipoDependente(dependentePortal.getTipoDependente());
            return dependenteVinculoFP;
        }
        return null;
    }
}
