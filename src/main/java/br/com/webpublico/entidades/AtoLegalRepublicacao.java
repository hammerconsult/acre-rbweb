package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity

@Audited
@Etiqueta("Ato Legal - Republicações")
public class AtoLegalRepublicacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AtoLegal atoLegal;
    @Obrigatorio
    @Etiqueta("Número da Republicação")
    private Integer numeroRepublicacao;
    @Obrigatorio
    @Etiqueta("Data da Republicação")
    @Temporal(TemporalType.DATE)
    private Date dataRepublicacao;

    public AtoLegalRepublicacao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Integer getNumeroRepublicacao() {
        return numeroRepublicacao;
    }

    public void setNumeroRepublicacao(Integer numeroRepublicacao) {
        this.numeroRepublicacao = numeroRepublicacao;
    }

    public Date getDataRepublicacao() {
        return dataRepublicacao;
    }

    public void setDataRepublicacao(Date dataRepublicacao) {
        this.dataRepublicacao = dataRepublicacao;
    }
}
