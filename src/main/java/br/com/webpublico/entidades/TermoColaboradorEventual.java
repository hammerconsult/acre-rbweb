package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 24/08/14
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta("Termo de Diária Colaborador Eventual")
@GrupoDiagrama(nome = "Diaria")
@Audited
@Entity
public class TermoColaboradorEventual extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Pessoa")
    @ManyToOne
    private Pessoa pessoa;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Unidade")
    @ManyToOne
    private UnidadeOrganizacional unidade;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Ato Legal")
    @ManyToOne
    private AtoLegal atoLegal;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Gerado Em")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date geradoEm;

    public TermoColaboradorEventual() {
    }

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

    public UnidadeOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeOrganizacional unidade) {
        this.unidade = unidade;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(Date geradoEm) {
        this.geradoEm = geradoEm;
    }
}
