package br.com.webpublico.entidades.rh.ponto;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.Faltas;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoFalta;
import br.com.webpublico.enums.rh.ponto.StatusSolicitacaoFaltas;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class SolicitacaoFaltas extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Temporal(TemporalType.TIMESTAMP)
    private Date criadaEm;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data Inicial")
    private Date dataInicio;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data Final")
    private Date dataFim;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Afastamento")
    private TipoFalta tipoFalta;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Contrato do Servidor")
    private ContratoFP contratoFP;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacaoFaltas status;

    private String motivoRejeicao;

    @ManyToOne
    private Faltas faltas;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(Date criadaEm) {
        this.criadaEm = criadaEm;
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

    public TipoFalta getTipoFalta() {
        return tipoFalta;
    }

    public void setTipoFalta(TipoFalta tipoFalta) {
        this.tipoFalta = tipoFalta;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public StatusSolicitacaoFaltas getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacaoFaltas status) {
        this.status = status;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public Faltas getFaltas() {
        return faltas;
    }

    public void setFaltas(Faltas faltas) {
        this.faltas = faltas;
    }
}
