package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 23/09/14
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Lançamentos de Taxas dos Veículos")
public class LancamentoTaxaVeiculo extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano")
    private Integer ano;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Veículo")
    @ManyToOne
    private Veiculo veiculo;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lancamentoTaxaVeiculo")
    private List<ItemLancamentoTaxaVeiculo> itensLancamentoTaxaVeiculo;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public LancamentoTaxaVeiculo() {
    }

    public LancamentoTaxaVeiculo(LancamentoTaxaVeiculo lancamentoTaxaVeiculo, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(lancamentoTaxaVeiculo.getId());
        this.setAno(lancamentoTaxaVeiculo.getAno());
        this.setVeiculo(lancamentoTaxaVeiculo.getVeiculo());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public List<ItemLancamentoTaxaVeiculo> getItensLancamentoTaxaVeiculo() {
        return itensLancamentoTaxaVeiculo;
    }

    public void setItensLancamentoTaxaVeiculo(List<ItemLancamentoTaxaVeiculo> itensLancamentoTaxaVeiculo) {
        this.itensLancamentoTaxaVeiculo = itensLancamentoTaxaVeiculo;
    }


    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
