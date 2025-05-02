package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.esocial.ProcedimentoDiagnostico;
import br.com.webpublico.enums.rh.esocial.TipoIndicacaoResultado;
import br.com.webpublico.enums.rh.esocial.TipoOrdemExame;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by carlos on 23/06/15.
 */
@Entity
@Audited
@Etiqueta("EXAME COMPLEMENTAR")
public class ExameComplementar extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do exame")
    @Obrigatorio
    private Date dataExame;
    @Etiqueta("Resultado")
    @Obrigatorio
    private String resultado;
    @ManyToOne
    @Obrigatorio
    private Exame exame;
    @ManyToOne
    private ASO aso;

    @ManyToOne
    private ProcedimentoDiagnostico procedimentoDiagnostico;

    private String observacaoProcesso;

    @Enumerated(EnumType.STRING)
    private TipoIndicacaoResultado tipoIndicacaoResultado;

    @Enumerated(EnumType.STRING)
    private TipoOrdemExame tipoOrdemExame;



    public ExameComplementar() {
    }

    public Date getDataExame() {
        return dataExame;
    }

    public void setDataExame(Date dataExame) {
        this.dataExame = dataExame;
    }

    public Exame getExame() {
        return exame;
    }

    public void setExame(Exame exame) {
        this.exame = exame;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public ASO getAso() {
        return aso;
    }

    public void setAso(ASO aso) {
        this.aso = aso;
    }

    public ProcedimentoDiagnostico getProcedimentoDiagnostico() {
        return procedimentoDiagnostico;
    }

    public void setProcedimentoDiagnostico(ProcedimentoDiagnostico procedimentoDiagnostico) {
        this.procedimentoDiagnostico = procedimentoDiagnostico;
    }

    public String getObservacaoProcesso() {
        return observacaoProcesso;
    }

    public void setObservacaoProcesso(String observacaoProcesso) {
        this.observacaoProcesso = observacaoProcesso;
    }

    public TipoIndicacaoResultado getTipoIndicacaoResultado() {
        return tipoIndicacaoResultado;
    }

    public void setTipoIndicacaoResultado(TipoIndicacaoResultado tipoIndicacaoResultado) {
        this.tipoIndicacaoResultado = tipoIndicacaoResultado;
    }

    public TipoOrdemExame getTipoOrdemExame() {
        return tipoOrdemExame;
    }

    public void setTipoOrdemExame(TipoOrdemExame tipoOrdemExame) {
        this.tipoOrdemExame = tipoOrdemExame;
    }

    @Override
    public String toString() {
        return exame.getDescricao();
    }
}
