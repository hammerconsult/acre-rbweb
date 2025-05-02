package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
public class OcorrenciaCDA implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataOcorrencia;
    private String ocorrencia;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private CertidaoDividaAtiva certidaoDividaAtiva;
    @ManyToOne(cascade = CascadeType.ALL)
    @NotAudited
    private Notificacao notificacao;

    public OcorrenciaCDA() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public String getDataOcorrenciaToString() {
        return Util.dateToString(dataOcorrencia);
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public String getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(String ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CertidaoDividaAtiva getCertidaoDividaAtiva() {
        return certidaoDividaAtiva;
    }

    public void setCertidaoDividaAtiva(CertidaoDividaAtiva certidaoDividaAtiva) {
        this.certidaoDividaAtiva = certidaoDividaAtiva;
    }

    public Notificacao getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(Notificacao notificacao) {
        this.notificacao = notificacao;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
