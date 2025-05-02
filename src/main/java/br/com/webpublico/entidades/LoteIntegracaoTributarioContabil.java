package br.com.webpublico.entidades;


import br.com.webpublico.enums.OperacaoReceita;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Table(name = "LOTEINTEGRACAOTRIBCONT")
public class LoteIntegracaoTributarioContabil extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UnidadeOrganizacional orcamentaria;
    @ManyToOne
    private UnidadeOrganizacional administrativa;
    @ManyToOne
    private ContaReceita contareceita;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lote")
    private List<IntegracaoTributarioContabil> itens;
    @Enumerated(EnumType.STRING)
    private OperacaoReceita operacaoReceitaRealizada;
    private Date dataIntegracao;
    private BigDecimal valor;
    @ManyToOne
    private SubConta subConta;
    @Temporal(TemporalType.DATE)
    private Date dataConciliacao;
    @ManyToOne
    private LoteBaixa loteBaixa;

    public LoteIntegracaoTributarioContabil() {
        dataIntegracao = new Date();
        dataConciliacao = new Date();
        valor = BigDecimal.ZERO;
        itens = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getOrcamentaria() {
        return orcamentaria;
    }

    public void setOrcamentaria(UnidadeOrganizacional orcamentaria) {
        this.orcamentaria = orcamentaria;
    }

    public UnidadeOrganizacional getAdministrativa() {
        return administrativa;
    }

    public void setAdministrativa(UnidadeOrganizacional administrativa) {
        this.administrativa = administrativa;
    }

    public ContaReceita getContaReceita() {
        return contareceita;
    }

    public void setContaReceita(ContaReceita conta) {
        this.contareceita = conta;
    }

    public Date getDataIntegracao() {
        return dataIntegracao;
    }

    public void setDataIntegracao(Date dataIntegracao) {
        this.dataIntegracao = dataIntegracao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public List<IntegracaoTributarioContabil> getItens() {
        return itens;
    }

    public void setItens(List<IntegracaoTributarioContabil> itens) {
        this.itens = itens;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public OperacaoReceita getOperacaoReceitaRealizada() {
        return operacaoReceitaRealizada;
    }

    public void setOperacaoReceitaRealizada(OperacaoReceita operacaoReceitaRealizada) {
        this.operacaoReceitaRealizada = operacaoReceitaRealizada;
    }

    @Override
    public String toString() {
        return "LoteIntegracao{" +
                "orcamentaria=" + orcamentaria +
                ", administrativa=" + administrativa +
                ", conta=" + contareceita +
                ", data=" + dataIntegracao +
                '}';
    }
}


