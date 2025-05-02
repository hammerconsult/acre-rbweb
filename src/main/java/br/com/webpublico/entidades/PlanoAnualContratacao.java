package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Plano Anual de Contratações - PAC")
public class PlanoAnualContratacao extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Integer numero;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Ano de Aplicação")
    private Exercicio exercicio;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeOrganizacional;

    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;

    @Obrigatorio
    @Temporal(value = TemporalType.DATE)
    @Etiqueta("Início da Elaboração")
    private Date inicioElaboracao;

    @Obrigatorio
    @Temporal(value = TemporalType.DATE)
    @Etiqueta("Fim da Elaboração")
    private Date fimElaboracao;

    @OneToMany(mappedBy = "planoAnualContratacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanoAnualContratacaoGrupoObjetoCompra> gruposObjetoCompra;

    public PlanoAnualContratacao() {
        gruposObjetoCompra = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getInicioElaboracao() {
        return inicioElaboracao;
    }

    public void setInicioElaboracao(Date inicioElaboracao) {
        this.inicioElaboracao = inicioElaboracao;
    }

    public Date getFimElaboracao() {
        return fimElaboracao;
    }

    public void setFimElaboracao(Date fimElaboracao) {
        this.fimElaboracao = fimElaboracao;
    }

    public List<PlanoAnualContratacaoGrupoObjetoCompra> getGruposObjetoCompra() {
        return gruposObjetoCompra;
    }

    public void setGruposObjetoCompra(List<PlanoAnualContratacaoGrupoObjetoCompra> gruposOjbetoCompra) {
        this.gruposObjetoCompra = gruposOjbetoCompra;
    }
}
