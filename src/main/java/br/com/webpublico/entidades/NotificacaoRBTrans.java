package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCiencia;
import br.com.webpublico.enums.TipoProcessoRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author AndreGustavo
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Notificação RBTrans")
public class NotificacaoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataCiencia;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataEmissaoNotificacao;
    @Enumerated(EnumType.STRING)
    private TipoCiencia tipoCiencia;
    @Enumerated(EnumType.STRING)
    private TipoProcessoRBTrans tipoProcessoRBTrans;
    @ManyToOne
    private AutuacaoFiscalizacaoRBTrans autuacaoFiscalizacao;
    private Long numero;
    private Integer ano;
    private String nomeResponsavelCiencia;
    private String cpfResponsavelCiencia;
    private String rgResponsavelCiencia;
    private String orgaoComunicacao;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataPublicacaoDiarioOficial;
    private String textoPublicacaoDiarioOficial;
    private String numeroDiarioOficial;

    public String getNomeResponsavelCiencia() {
        return nomeResponsavelCiencia;
    }

    public void setNomeResponsavelCiencia(String nomeResponsavelCiencia) {
        this.nomeResponsavelCiencia = nomeResponsavelCiencia;
    }

    public String getCpfResponsavelCiencia() {
        return cpfResponsavelCiencia;
    }

    public void setCpfResponsavelCiencia(String cpfResponsavelCiencia) {
        this.cpfResponsavelCiencia = cpfResponsavelCiencia;
    }

    public String getRgResponsavelCiencia() {
        return rgResponsavelCiencia;
    }

    public void setRgResponsavelCiencia(String rgResponsavelCiencia) {
        this.rgResponsavelCiencia = rgResponsavelCiencia;
    }

    public String getOrgaoComunicacao() {
        return orgaoComunicacao;
    }

    public void setOrgaoComunicacao(String orgaoComunicacao) {
        this.orgaoComunicacao = orgaoComunicacao;
    }

    public Date getDataPublicacaoDiarioOficial() {
        return dataPublicacaoDiarioOficial;
    }

    public void setDataPublicacaoDiarioOficial(Date dataPublicacaoDiarioOficial) {
        this.dataPublicacaoDiarioOficial = dataPublicacaoDiarioOficial;
    }

    public String getTextoPublicacaoDiarioOficial() {
        return textoPublicacaoDiarioOficial;
    }

    public void setTextoPublicacaoDiarioOficial(String textoPublicacaoDiarioOficial) {
        this.textoPublicacaoDiarioOficial = textoPublicacaoDiarioOficial;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Date getDataNitificacao() {
        return dataEmissaoNotificacao;
    }

    public void setDataNitificacao(Date dataNitificacao) {
        this.dataEmissaoNotificacao = dataNitificacao;
    }

    public TipoCiencia getTipoCiencia() {
        return tipoCiencia;
    }

    public void setTipoCiencia(TipoCiencia tipoCiencia) {
        this.tipoCiencia = tipoCiencia;
    }

    public TipoProcessoRBTrans getTipoProcessoRBTrans() {
        return tipoProcessoRBTrans;
    }

    public AutuacaoFiscalizacaoRBTrans getAutuacaoFiscalizacao() {
        return autuacaoFiscalizacao;
    }

    public Date getDataEmissaoNotificacao() {
        return dataEmissaoNotificacao;
    }

    public void setDataEmissaoNotificacao(Date dataEmissaoNotificacao) {
        this.dataEmissaoNotificacao = dataEmissaoNotificacao;
    }

    public String getNumeroDiarioOficial() {
        return numeroDiarioOficial;
    }

    public void setNumeroDiarioOficial(String numeroDiarioOficial) {
        this.numeroDiarioOficial = numeroDiarioOficial;
    }


    public void setAutuacaoFiscalizacao(AutuacaoFiscalizacaoRBTrans autuacaoFiscalizacao) {
        this.autuacaoFiscalizacao = autuacaoFiscalizacao;
    }

    public void setTipoProcessoRBTrans(TipoProcessoRBTrans tipoProcessoRBTrans) {
        this.tipoProcessoRBTrans = tipoProcessoRBTrans;
    }

    public Date getDataCiencia() {
        return dataCiencia;
    }

    public void setDataCiencia(Date dataCiencia) {
        this.dataCiencia = dataCiencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NotificacaoRBTrans)) {
            return false;
        }
        NotificacaoRBTrans other = (NotificacaoRBTrans) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.NotificacaoRBTrans[ id=" + id + " ]";
    }
}
