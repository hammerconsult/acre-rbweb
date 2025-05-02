package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoLicenciamentoETR;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class RequerimentoLicenciamentoETRHistorico extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RequerimentoLicenciamentoETR requerimento;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Enumerated(EnumType.STRING)
    private SituacaoLicenciamentoETR situacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequerimentoLicenciamentoETR getRequerimento() {
        return requerimento;
    }

    public void setRequerimento(RequerimentoLicenciamentoETR requerimento) {
        this.requerimento = requerimento;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public SituacaoLicenciamentoETR getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLicenciamentoETR situacao) {
        this.situacao = situacao;
    }
}
