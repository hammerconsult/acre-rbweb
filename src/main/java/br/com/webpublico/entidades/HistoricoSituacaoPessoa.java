package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCadastralPessoa;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Fabio on 27/11/2018.
 */
@Entity
@Audited
public class HistoricoSituacaoPessoa extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pessoa pessoa;
    @Temporal(TemporalType.DATE)
    private Date dataSituacao;
    @Enumerated(EnumType.STRING)
    private SituacaoCadastralPessoa situacaoCadastralPessoa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDataSituacao() {
        return dataSituacao;
    }

    public void setDataSituacao(Date dataSituacao) {
        this.dataSituacao = dataSituacao;
    }

    public SituacaoCadastralPessoa getSituacaoCadastralPessoa() {
        return situacaoCadastralPessoa;
    }

    public void setSituacaoCadastralPessoa(SituacaoCadastralPessoa situacaoCadastralPessoa) {
        this.situacaoCadastralPessoa = situacaoCadastralPessoa;
    }
}
