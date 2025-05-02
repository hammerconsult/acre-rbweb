package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSituacaoObra;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Situação da Obra")
public class ObraSituacao extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Obra")
    @ManyToOne
    private Obra obra;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data")
    private Date dataSituacao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Responsável")
    private PessoaFisica pessoaFisica;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Situação")
    private TipoSituacaoObra situacao;
    @Obrigatorio
    @Etiqueta("Motivo")
    private String motivo;

    public ObraSituacao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

    public Date getDataSituacao() {
        return dataSituacao;
    }

    public void setDataSituacao(Date dataSituacao) {
        this.dataSituacao = dataSituacao;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public TipoSituacaoObra getSituacao() {
        return situacao;
    }

    public void setSituacao(TipoSituacaoObra situacao) {
        this.situacao = situacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public String toString() {
        return situacao.getDescricao();
    }
}
