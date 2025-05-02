package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Audited
@Entity
@Table(name = "CONFIGTRANSPSALDOSUBCDEST")
public class ConfiguracaoTransporteSaldoSubContaDestino extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoTransporteSaldoSubContaOrigem configuracaoOrigem;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Financeira Destino")
    private SubConta subConta;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Destino")
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Fonte de Recursos Destino")
    private ContaDeDestinacao contaDeDestinacao;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valor;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ConfiguracaoTransporteSaldoSubContaDestino() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoTransporteSaldoSubContaOrigem getConfiguracaoOrigem() {
        return configuracaoOrigem;
    }

    public void setConfiguracaoOrigem(ConfiguracaoTransporteSaldoSubContaOrigem configuracaoOrigem) {
        this.configuracaoOrigem = configuracaoOrigem;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public String toString() {
        return (subConta != null ? subConta.getCodigo() : "") +
            (contaDeDestinacao != null ? " (" + contaDeDestinacao.getFonteDeRecursos().getCodigo() + ") - " : "") +
            Util.formataValor(valor);
    }
}
