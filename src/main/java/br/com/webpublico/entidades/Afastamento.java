/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.SolicitacaoAfastamentoPortal;
import br.com.webpublico.enums.rh.TipoEnvioDadosRBPonto;
import br.com.webpublico.enums.rh.esocial.TipoAfastamentoESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Entity

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Etiqueta("Afastamento")
public class Afastamento extends SuperEntidade implements PossuidorArquivo, IHistoricoEsocial {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Início")
    @Temporal(javax.persistence.TemporalType.DATE)
    @ColunaAuditoriaRH(posicao = 3)
    private Date inicio;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Data de Término")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ColunaAuditoriaRH(posicao = 4)
    private Date termino;
    @Tabelavel
    @Etiqueta("Carência")
    private Integer carencia;
    @Etiqueta("Observação")
    @Length(maximo = 3000)
    private String observacao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Contrato do Servidor")
    @ManyToOne
    @ColunaAuditoriaRH(posicao = 1)
    private ContratoFP contratoFP;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo de Afastamento")
    @ManyToOne
    @Pesquisavel
    @ColunaAuditoriaRH(posicao = 2)
    private TipoAfastamento tipoAfastamento;
    @ManyToOne
    @Etiqueta("CID")
    private CID cid;
    @Etiqueta("Data do Cadastro")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCadastro;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCadastroFinalVigencia;
    @ManyToOne
    @Etiqueta("Médico")
    private Medico medico;
    @Etiqueta("Número Máximo de Dias Permitido")
    @Transient
    private Integer diasMaximoPermitido;
    private Boolean retornoInformado;
    @Transient
    private TipoAfastamentoESocial tipoAfastamentoESocial;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Temporal(TemporalType.DATE)
    private Date dataRetornoInformado;


    @ManyToOne
    private Sindicato sindicato;
    @Etiqueta("Quantidade de Dias")
    @Tabelavel
    private Integer quantidadeDias;

    @Transient
    private List<EventoESocialDTO> eventosEsocial;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    @Etiqueta("Arquivo")
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @OneToOne
    private SolicitacaoAfastamentoPortal solicitacaoAfastamento;
    @Enumerated(EnumType.STRING)
    private TipoEnvioDadosRBPonto tipoEnvioDadosRBPonto;

    public Afastamento() {
        super();
        retornoInformado = false;
    }

    public Afastamento(Date inicio, Date termino) {
        this.inicio = inicio;
        this.termino = termino;
    }

    public int getTotalDias() {
        if (inicio != null && termino != null) {
            DateTime ini = new DateTime(inicio);
            DateTime fim = new DateTime(termino);
            return Days.daysBetween(ini, fim).getDays() + 1;
        } else return 0;
    }

    public void setTotalDias(int totalDias) {

    }

    public Sindicato getSindicato() {
        return sindicato;
    }

    public void setSindicato(Sindicato sindicato) {
        this.sindicato = sindicato;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Boolean getRetornoInformado() {
        return retornoInformado == null ? false : retornoInformado;
    }

    public void setRetornoInformado(Boolean retornoInformado) {
        this.retornoInformado = retornoInformado;
    }

    public Boolean isRetornoInformado() {
        return retornoInformado == null ? false : retornoInformado;
    }

    public TipoAfastamentoESocial getTipoAfastamentoESocial() {
        return tipoAfastamentoESocial;
    }

    public void setTipoAfastamentoESocial(TipoAfastamentoESocial tipoAfastamentoESocial) {
        this.tipoAfastamentoESocial = tipoAfastamentoESocial;
    }

    public Date getDataCadastroFinalVigencia() {
        return dataCadastroFinalVigencia;
    }

    public void setDataCadastroFinalVigencia(Date dataCadastroFinalVigencia) {
        this.dataCadastroFinalVigencia = dataCadastroFinalVigencia;
    }

    public Date getDataRetornoInformado() {
        return dataRetornoInformado;
    }

    public void setDataRetornoInformado(Date dataRetornoInformado) {
        this.dataRetornoInformado = dataRetornoInformado;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public CID getCid() {
        return cid;
    }

    public void setCid(CID cid) {
        this.cid = cid;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCarencia() {
        return carencia;
    }

    public void setCarencia(Integer carencia) {
        this.carencia = carencia;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getTermino() {
        return termino;
    }

    public void setTermino(Date termino) {
        this.termino = termino;
    }

    public TipoAfastamento getTipoAfastamento() {
        return tipoAfastamento;
    }

    public void setTipoAfastamento(TipoAfastamento tipoAfastamento) {
        this.tipoAfastamento = tipoAfastamento;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Integer getDiasMaximoPermitido() {
        return diasMaximoPermitido;
    }

    public void setDiasMaximoPermitido(Integer diasMaximoPermitido) {
        this.diasMaximoPermitido = diasMaximoPermitido;
    }

    public Integer getQuantidadeDias() {
        return quantidadeDias;
    }

    public void setQuantidadeDias(Integer quantidadeDias) {
        this.quantidadeDias = quantidadeDias;
    }

    public List<EventoESocialDTO> getEventosEsocial() {
        return eventosEsocial;
    }

    public void setEventosEsocial(List<EventoESocialDTO> eventosEsocial) {
        this.eventosEsocial = eventosEsocial;
    }

    @Override
    public String toString() {
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

        return formatoData.format(inicio) + " - " + formatoData.format(termino) + " - " + contratoFP + " - " + tipoAfastamento;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public SolicitacaoAfastamentoPortal getSolicitacaoAfastamento() {
        return solicitacaoAfastamento;
    }

    public void setSolicitacaoAfastamento(SolicitacaoAfastamentoPortal solicitacaoAfastamento) {
        this.solicitacaoAfastamento = solicitacaoAfastamento;
    }

    public TipoEnvioDadosRBPonto getTipoEnvioDadosRBPonto() {
        return tipoEnvioDadosRBPonto;
    }

    public void setTipoEnvioDadosRBPonto(TipoEnvioDadosRBPonto tipoEnvioDadosRBPonto) {
        this.tipoEnvioDadosRBPonto = tipoEnvioDadosRBPonto;
    }

    @Override
    public String getDescricaoCompleta() {
        return toString();
    }

    @Override
    public String getIdentificador() {
        return getId().toString();
    }
}
