package br.com.webpublico.entidades.contabil.financeiro;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoLiberacaoFinanceira;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by renatoromanini on 17/04/17.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Unidade de Acompanhamento Financeiro")
@Table(name = "UNIDADEACOMPAFINANCEIRO")
public class UnidadeAcompanhamentoFinanceiro extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @OneToMany(mappedBy = "unidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FiltroAcompanhamentoFinanceiro> filtros;
    @OneToMany(mappedBy = "unidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContaAcompanhamentoFinanceiro> intervalosConta;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Transient
    private TipoLiberacaoFinanceira[] tipos;
    @Transient
    private ContaAcompanhamentoFinanceiro conta;


    public UnidadeAcompanhamentoFinanceiro() {
        filtros = Lists.newArrayList();
        intervalosConta = Lists.newArrayList();
        conta = new ContaAcompanhamentoFinanceiro();
        conta.setUnidade(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<FiltroAcompanhamentoFinanceiro> getFiltros() {
        return filtros;
    }

    public void setFiltros(List<FiltroAcompanhamentoFinanceiro> filtros) {
        this.filtros = filtros;
    }

    public List<ContaAcompanhamentoFinanceiro> getIntervalosConta() {
        return intervalosConta;
    }

    public void setIntervalosConta(List<ContaAcompanhamentoFinanceiro> intervalosConta) {
        this.intervalosConta = intervalosConta;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public TipoLiberacaoFinanceira[] getTipos() {
        return tipos;
    }

    public ContaAcompanhamentoFinanceiro getConta() {
        return conta;
    }

    public void setConta(ContaAcompanhamentoFinanceiro conta) {
        this.conta = conta;
    }

    public void setTipos(TipoLiberacaoFinanceira[] tipos) {
        this.tipos = tipos;
    }

    @Override
    public String toString() {
        return " unidade = " + unidadeOrganizacional;
    }
}
