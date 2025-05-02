package br.com.webpublico.entidades.rh.cadastrofuncional;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author peixe on 21/10/2016  11:09.
 */
@GrupoDiagrama(nome = "Cadastro Funcional")
@Audited
@CorEntidade(value = "#FFFF00")
@Entity
public class PeriodoAquisitivoExcluido extends SuperEntidade implements ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @Enumerated(EnumType.STRING)
    private TipoPeriodoAquisitivo tipoPeriodoAquisitivo;

    public PeriodoAquisitivoExcluido() {
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

    public DateTime getInicioVigenciaJoda() {
        if(inicioVigencia != null){
            return new DateTime(inicioVigencia);
        }
        return null;
    }

    public DateTime getFinalVigenciaJoda() {
        if(finalVigencia != null){
            return new DateTime(finalVigencia);
        }
        return null;
    }


    @Override
    public Date getFimVigencia() {
        return finalVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public TipoPeriodoAquisitivo getTipoPeriodoAquisitivo() {
        return tipoPeriodoAquisitivo;
    }

    public void setTipoPeriodoAquisitivo(TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        this.tipoPeriodoAquisitivo = tipoPeriodoAquisitivo;
    }


    @Override
    public String toString() {
        return (inicioVigencia != null ? DataUtil.getDataFormatada(inicioVigencia) : "") + " - " + (finalVigencia != null ? DataUtil.getDataFormatada(finalVigencia) : "") + " - " + tipoPeriodoAquisitivo;
    }
}
