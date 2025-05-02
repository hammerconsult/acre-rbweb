package br.com.webpublico.entidades.rh.creditodesalario;

import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.RecursoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;


@Entity
@Audited
@Etiqueta(value = "Crédito de Salário Grupo Recurso")
public class FiltroCreditoSalario extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CreditoSalario creditoSalario;
    @ManyToOne
    private GrupoRecursoFP grupoRecursoFP;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private RecursoFP recursoFP;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public FiltroCreditoSalario() {
    }

    public FiltroCreditoSalario(CreditoSalario creditoSalario, GrupoRecursoFP grupoRecursoFP, UnidadeOrganizacional unidadeOrganizacional, RecursoFP recursoFP) {
        this.creditoSalario = creditoSalario;
        this.grupoRecursoFP = grupoRecursoFP;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.recursoFP = recursoFP;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CreditoSalario getCreditoSalario() {
        return creditoSalario;
    }

    public void setCreditoSalario(CreditoSalario creditoSalario) {
        this.creditoSalario = creditoSalario;
    }

    public GrupoRecursoFP getGrupoRecursoFP() {
        return grupoRecursoFP;
    }

    public void setGrupoRecursoFP(GrupoRecursoFP grupoRecursoFP) {
        this.grupoRecursoFP = grupoRecursoFP;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }
}
