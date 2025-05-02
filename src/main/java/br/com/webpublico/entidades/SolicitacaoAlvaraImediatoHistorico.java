package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAlvaraImediato;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class SolicitacaoAlvaraImediatoHistorico extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SolicitacaoAlvaraImediato solicitacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Enumerated(EnumType.STRING)
    private SituacaoAlvaraImediato situacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoAlvaraImediato getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoAlvaraImediato solicitacao) {
        this.solicitacao = solicitacao;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public SituacaoAlvaraImediato getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAlvaraImediato situacao) {
        this.situacao = situacao;
    }
}
