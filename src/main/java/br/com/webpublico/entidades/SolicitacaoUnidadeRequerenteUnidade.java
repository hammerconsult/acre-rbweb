package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;


@Entity
@Audited
@Table(name = "SOLICITACAOUNDREQUNIDADE")
public class SolicitacaoUnidadeRequerenteUnidade extends SuperEntidade implements Comparable<SolicitacaoUnidadeRequerenteUnidade> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    private Long id;

    @Etiqueta("Solicitação")
    @Obrigatorio
    @ManyToOne
    private SolicitacaoUnidadeRequerente solicitacao;

    @Etiqueta("Unidade Requerente")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public SolicitacaoUnidadeRequerenteUnidade() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoUnidadeRequerente getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoUnidadeRequerente solicitacao) {
        this.solicitacao = solicitacao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public int compareTo(SolicitacaoUnidadeRequerenteUnidade o) {
        if (getHierarquiaOrganizacional() != null && o.getHierarquiaOrganizacional() != null){
            return getHierarquiaOrganizacional().getCodigo().compareTo(o.getHierarquiaOrganizacional().getCodigo());
        }
        return 0;
    }
}
