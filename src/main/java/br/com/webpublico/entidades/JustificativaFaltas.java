package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 28/08/13
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
@Etiqueta("Justificativa de Faltas Injustificadas")
@GrupoDiagrama(nome = "RecursosHumanos")
public class JustificativaFaltas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private Faltas faltas;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Início")
    private Date inicioVigencia;
    @Obrigatorio
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Fim")
    private Date finalVigencia;
    @ManyToOne
    @Etiqueta("CID")
    private CID cid;
    @Etiqueta("Médico")
    @ManyToOne
    private Medico medico;
    @Etiqueta("OBS")
    private String obs;
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Mes/Ano para Calculo")
    @Temporal(TemporalType.DATE)
    private Date dataParaCalculo;
    @Etiqueta("Total de Dias")
    @Tabelavel
    @Pesquisavel
    private Integer dias;

    public JustificativaFaltas() {
    }

    public Date getDataParaCalculo() {
        return dataParaCalculo;
    }

    public void setDataParaCalculo(Date dataParaCalculo) {
        this.dataParaCalculo = dataParaCalculo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Faltas getFaltas() {
        return faltas;
    }

    public void setFaltas(Faltas faltas) {
        this.faltas = faltas;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public CID getCid() {
        return cid;
    }

    public void setCid(CID cid) {
        this.cid = cid;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public int getDias() {
        if (inicioVigencia != null && finalVigencia!= null) {
            DateTime ini = new DateTime(inicioVigencia);
            DateTime termino = new DateTime(finalVigencia);
            dias = Days.daysBetween(ini, termino).getDays() + 1;
            return dias;
        } else {
            return 0;
        }
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JustificativaFaltas that = (JustificativaFaltas) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return faltas+ "";
    }
}
