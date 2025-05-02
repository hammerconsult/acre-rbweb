package br.com.webpublico.entidades;

import br.com.webpublico.enums.tributario.StatusSolicitacaoPIX;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class StatusPIX extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private PIX pix;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacaoPIX statusSolicitacao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataSituacao;

    public StatusPIX() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PIX getPix() {
        return pix;
    }

    public void setPix(PIX pix) {
        this.pix = pix;
    }

    public StatusSolicitacaoPIX getStatusSolicitacao() {
        return statusSolicitacao;
    }

    public void setStatusSolicitacao(StatusSolicitacaoPIX statusSolicitacao) {
        this.statusSolicitacao = statusSolicitacao;
    }

    public Date getDataSituacao() {
        return dataSituacao;
    }

    public void setDataSituacao(Date dataSituacao) {
        this.dataSituacao = dataSituacao;
    }
}
