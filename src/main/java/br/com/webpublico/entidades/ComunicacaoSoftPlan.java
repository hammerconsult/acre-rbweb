package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.ComunicaSofPlanFacade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Dívida Ativa")
@Entity
@Audited
@Etiqueta("Comunicação com a SoftPlan")
public class ComunicacaoSoftPlan implements Serializable, Comparable<ComunicacaoSoftPlan> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CertidaoDividaAtiva cda;
    private String serviceId;
    private String version;
    private String code;
    private String msgDesc;
    private String fromAddress;
    private String toAddress;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataComunicacao;
    private String codigoResposta;
    private String descricaoResposta;
    @ManyToOne(cascade = CascadeType.ALL)
    @NotAudited
    private Notificacao notificacao;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private ProcessoParcelamento processoParcelamento;
    private String xml;
    @Enumerated(EnumType.STRING)
    private Status status;

    public ComunicacaoSoftPlan() {
    }

    public ProcessoParcelamento getProcessoParcelamento() {
        return processoParcelamento;
    }

    public void setProcessoParcelamento(ProcessoParcelamento processoParcelamento) {
        this.processoParcelamento = processoParcelamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsgDesc() {
        return msgDesc;
    }

    public void setMsgDesc(String msgDesc) {
        this.msgDesc = msgDesc;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public Date getDataComunicacao() {
        return dataComunicacao;
    }

    public void setDataComunicacao(Date dataComunicacao) {
        this.dataComunicacao = dataComunicacao;
    }

    public String getCodigoResposta() {
        return codigoResposta;
    }

    public void setCodigoResposta(String codigoResposta) {
        this.codigoResposta = codigoResposta;
    }

    public String getDescricaoResposta() {
        if (processoParcelamento != null) {
            return descricaoResposta + " (Parcelamento " + processoParcelamento.getNumeroCompostoComAno() + " )";
        }
        return descricaoResposta;
    }

    public void setDescricaoResposta(String descricaoResposta) {
        this.descricaoResposta = descricaoResposta;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CertidaoDividaAtiva getCda() {
        return cda;
    }

    public void setCda(CertidaoDividaAtiva cda) {
        this.cda = cda;
    }

    public Notificacao getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(Notificacao notificacao) {
        this.notificacao = notificacao;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        return serviceId + " - " + Util.formatterDataSemBarra.format(dataComunicacao).toString();
    }

    public String getDataComunicacaoToString() {
        return Util.dateToString(dataComunicacao);
    }

    public ComunicaSofPlanFacade.ServiceId getTipoComunicacao() {
        return ComunicaSofPlanFacade.ServiceId.getPorValor(serviceId);
    }

    @Override
    public int compareTo(ComunicacaoSoftPlan o) {
        int i = o.getDataComunicacao().compareTo(this.dataComunicacao);
        if (i == 0) {
            i = o.code.compareTo(this.getCode());
        }
        if (i == 0) {
            i = this.id.compareTo(o.getId());
        }
        return i;
    }

    public enum Status {
        EM_ANDAMENTO("Em andamento"), FINALIZADO("Finalizado"), ERRO("Erro");

        private final String descricao;

        Status(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
