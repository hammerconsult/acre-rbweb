package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Plano de Contratações Anual - PCA")
public class PlanoContratacaoAnual extends SuperEntidade {

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

    @Etiqueta("Sequencial Criado pelo PNCP ao Realizar o Envio do PCA")
    @Tabelavel(campoSelecionado = false)
    private String sequencialIdPncp;

    @Etiqueta("Id Criado pelo PNCP ao Realizar o Envio do PCA")
    @Tabelavel(campoSelecionado = false)
    private String idPncp;

    @OneToMany(mappedBy = "planoContratacaoAnual", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanoContratacaoAnualGrupoObjetoCompra> gruposObjetoCompra;

    public PlanoContratacaoAnual() {
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

    public String getSequencialIdPncp() {
        return sequencialIdPncp;
    }

    public void setSequencialIdPncp(String sequencialIdPncp) {
        this.sequencialIdPncp = sequencialIdPncp;
    }

    public String getIdPncp() {
        return idPncp;
    }

    public void setIdPncp(String idPncp) {
        this.idPncp = idPncp;
    }

    public List<PlanoContratacaoAnualGrupoObjetoCompra> getGruposObjetoCompra() {
        return gruposObjetoCompra;
    }

    public void setGruposObjetoCompra(List<PlanoContratacaoAnualGrupoObjetoCompra> gruposOjbetoCompra) {
        this.gruposObjetoCompra = gruposOjbetoCompra;
    }
}
