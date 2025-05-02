package br.com.webpublico.entidades.rh.portal;

import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProgramacaoFeriasPortal extends SuperEntidade{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data Inicial")
    @Tabelavel
    @Pesquisavel
    private Date dataInicio;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data Final")
    @Tabelavel
    @Pesquisavel
    private Date dataFim;
    @Etiqueta("Abono Pecúnia")
    @Tabelavel
    private Boolean abonoPecunia;
    @Tabelavel
    @Etiqueta("Dias de Abono")
    private Integer diasAbono;
    @OneToOne
    @Obrigatorio
    @Etiqueta("Período Aquisitivo FL")
    @Tabelavel
    @Pesquisavel
    @JsonIgnore
    private PeriodoAquisitivoFL periodoAquisitivoFL;
    @Invisivel
    @Transient
    private Long periodoAquisitivoFLId;

    public ProgramacaoFeriasPortal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Boolean getAbonoPecunia() {
        return abonoPecunia;
    }

    public void setAbonoPecunia(Boolean abonoPecunia) {
        this.abonoPecunia = abonoPecunia;
    }

    public Integer getDiasAbono() {
        return diasAbono;
    }

    public void setDiasAbono(Integer diasAbono) {
        this.diasAbono = diasAbono;
    }

    public PeriodoAquisitivoFL getPeriodoAquisitivoFL() {
        return periodoAquisitivoFL;
    }

    public void setPeriodoAquisitivoFL(PeriodoAquisitivoFL periodoAquisitivoFL) {
        this.periodoAquisitivoFL = periodoAquisitivoFL;
    }

    public Long getPeriodoAquisitivoFLId() {
        return periodoAquisitivoFLId;
    }

    public void setPeriodoAquisitivoFLId(Long periodoAquisitivoFLId) {
        this.periodoAquisitivoFLId = periodoAquisitivoFLId;
    }

    @Override
    public String toString() {
        return (getPeriodoAquisitivoFL() != null ? (getPeriodoAquisitivoFL().getContratoFP() + " Período Aquisitivo: " +DataUtil.getDataFormatada(getPeriodoAquisitivoFL().getInicioVigencia()) +" à "+ DataUtil.getDataFormatada(getPeriodoAquisitivoFL().getFinalVigencia())) : " ") + " Sugestão: "+ (getDataInicio() != null ? DataUtil.getDataFormatada(getDataInicio()) : "") + " à "+ (getDataFim() != null ? DataUtil.getDataFormatada(getDataFim()) : "");
    }
}
