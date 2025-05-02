package br.com.webpublico.entidades.rh.portal;

import br.com.webpublico.entidades.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitacaoAfastamentoPortal extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    private Date criadaEm;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data Inicial")
    @Tabelavel
    @Pesquisavel
    private Date dataInicio;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data Final")
    @Tabelavel
    @Pesquisavel
    private Date dataFim;

    @ManyToOne
    @Etiqueta("Tipo de Afastamento")
    private TipoAfastamento tipoAfastamento;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    @Etiqueta("Arquivo")
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Contrato do Servidor")
    private ContratoFP contratoFP;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Status")
    private RHStatusSolicitacaoAfastamentoPortal status;

    @Etiqueta("Motivo da Rejeição")
    private String motivoRejeicao;

    @OneToOne
    private Afastamento afastamento;

    public SolicitacaoAfastamentoPortal() {
    }

    @Override
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

    public TipoAfastamento getTipoAfastamento() {
        return tipoAfastamento;
    }

    public void setTipoAfastamento(TipoAfastamento tipoAfastamento) {
        this.tipoAfastamento = tipoAfastamento;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public RHStatusSolicitacaoAfastamentoPortal getStatus() {
        return status;
    }

    public void setStatus(RHStatusSolicitacaoAfastamentoPortal status) {
        this.status = status;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public Afastamento getAfastamento() {
        return afastamento;
    }

    public void setAfastamento(Afastamento afastamento) {
        this.afastamento = afastamento;
    }

    public Date getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(Date criadaEm) {
        this.criadaEm = criadaEm;
    }

    @Override
    public String toString() {
        return tipoAfastamento.toString() + " - Período: " + DataUtil.getDataFormatada(dataInicio) + " à " + DataUtil.getDataFormatada(dataFim);
    }
}
