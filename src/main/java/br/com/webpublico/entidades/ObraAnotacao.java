package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author venom
 */
@Entity
@Audited
@Etiqueta("Obra Anotação")
public class ObraAnotacao extends SuperEntidade implements ValidadorEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data da Anotação")
    private Date dataAnotacao;

    @Obrigatorio
    @Etiqueta("Responsável")
    private String responsavel;

    @Obrigatorio
    @Etiqueta("Anotação")
    private String anotacao;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Obra")
    private Obra obra;

    public ObraAnotacao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnotacao() {
        return anotacao;
    }

    public void setAnotacao(String anotacao) {
        this.anotacao = anotacao;
    }

    public Date getDataAnotacao() {
        return dataAnotacao;
    }

    public void setDataAnotacao(Date dataAnotacao) {
        this.dataAnotacao = dataAnotacao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ObraAnotacao[ id=" + id + " ]";
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {

    }
}
