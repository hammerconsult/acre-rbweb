package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class EventoFPEmpregador extends SuperEntidade implements Comparable<EventoFPEmpregador> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private EventoFP eventoFP;

    @ManyToOne
    private Entidade entidade;

    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;
    private String identificacaoTabela;

    @ManyToOne
    @Etiqueta("Natureza das Rubricas")
    @Obrigatorio
    private NaturezaRubrica naturezaRubrica;

    @ManyToOne
    @Etiqueta("Incidência Tributária para a Previdência Social")
    @Obrigatorio
    private IncidenciaTributariaPrevidencia incidenciaPrevidencia;

    @ManyToOne
    @Etiqueta("Incidência Tributária para IRRF")
    @Obrigatorio
    private IncidenciaTributariaIRRF incidenciaTributariaIRRF;

    @ManyToOne
    @Etiqueta("Incidência Tributária para FGTS")
    @Obrigatorio
    private IncidenciaTributariaFGTS incidenciaTributariaFGTS;

    @ManyToOne
    @Etiqueta("Contribuição Sindical Laboral")
    @Obrigatorio
    private IncidenciaTributariaRPPS incidenciaTributariaRPPS;


    @Etiqueta("Verba de Férias")
    private Boolean verbaDeFerias;

    private Boolean tetoRemuneratorio;

    public EventoFPEmpregador() {
        tetoRemuneratorio = Boolean.FALSE;
    }


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
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

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public String getIdentificacaoTabela() {
        return identificacaoTabela;
    }

    public void setIdentificacaoTabela(String identificacaoTabela) {
        this.identificacaoTabela = identificacaoTabela;
    }

    public NaturezaRubrica getNaturezaRubrica() {
        return naturezaRubrica;
    }

    public void setNaturezaRubrica(NaturezaRubrica naturezaRubrica) {
        this.naturezaRubrica = naturezaRubrica;
    }

    public IncidenciaTributariaPrevidencia getIncidenciaPrevidencia() {
        return incidenciaPrevidencia;
    }

    public void setIncidenciaPrevidencia(IncidenciaTributariaPrevidencia incidenciaPrevidencia) {
        this.incidenciaPrevidencia = incidenciaPrevidencia;
    }

    public IncidenciaTributariaIRRF getIncidenciaTributariaIRRF() {
        return incidenciaTributariaIRRF;
    }

    public void setIncidenciaTributariaIRRF(IncidenciaTributariaIRRF incidenciaTributariaIRRF) {
        this.incidenciaTributariaIRRF = incidenciaTributariaIRRF;
    }

    public IncidenciaTributariaFGTS getIncidenciaTributariaFGTS() {
        return incidenciaTributariaFGTS;
    }

    public void setIncidenciaTributariaFGTS(IncidenciaTributariaFGTS incidenciaTributariaFGTS) {
        this.incidenciaTributariaFGTS = incidenciaTributariaFGTS;
    }

    public IncidenciaTributariaRPPS getIncidenciaTributariaRPPS() {
        return incidenciaTributariaRPPS;
    }

    public void setIncidenciaTributariaRPPS(IncidenciaTributariaRPPS incidenciaTributariaRPPS) {
        this.incidenciaTributariaRPPS = incidenciaTributariaRPPS;
    }

    public Boolean getTetoRemuneratorio() {
        return tetoRemuneratorio ? tetoRemuneratorio : false;
    }

    public void setTetoRemuneratorio(Boolean tetoRemuneratorio) {
        this.tetoRemuneratorio = tetoRemuneratorio;
    }

    @Override
    public int compareTo(EventoFPEmpregador o) {
        return ComparisonChain.start()
            .compare(entidade.getPessoaJuridica().getNome(), o.getEntidade().getPessoaJuridica().getNome())
            .compare(o.inicioVigencia, inicioVigencia)
            .result();
    }
}
