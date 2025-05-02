package br.com.webpublico.entidades;


import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Audited
public class PrestadorServicoAlteracao extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private PrestadorServicos prestadorServico;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAlteracao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public PrestadorServicos getPrestadorServico() {
        return prestadorServico;
    }

    public void setPrestadorServico(PrestadorServicos prestadorServico) {
        this.prestadorServico = prestadorServico;
    }
}
