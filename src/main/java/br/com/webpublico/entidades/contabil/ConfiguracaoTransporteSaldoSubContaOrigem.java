package br.com.webpublico.entidades.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Audited
@Entity
@Table(name = "CONFIGTRANSPSALDOSUBCORIG")
public class ConfiguracaoTransporteSaldoSubContaOrigem extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoTransporteSaldoSubConta configuracao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Financeira Origem")
    private SubConta subConta;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Origem")
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Fonte de Recursos Origem")
    private ContaDeDestinacao contaDeDestinacao;
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valor;
    @OneToMany(mappedBy = "configuracaoOrigem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoTransporteSaldoSubContaDestino> destinos;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Transient
    private Boolean selecionado;
    @Transient
    private String styleText;

    public ConfiguracaoTransporteSaldoSubContaOrigem() {
        destinos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoTransporteSaldoSubConta getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(ConfiguracaoTransporteSaldoSubConta configuracao) {
        this.configuracao = configuracao;
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

    public List<ConfiguracaoTransporteSaldoSubContaDestino> getDestinos() {
        return destinos;
    }

    public void setDestinos(List<ConfiguracaoTransporteSaldoSubContaDestino> destinos) {
        this.destinos = destinos;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getSelecionado() {
        return selecionado == null ? Boolean.FALSE : selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public String getStyleText() {
        if (styleText == null) {
            atualizarStyleText();
        }
        return styleText;
    }

    public void setStyleText(String styleText) {
        this.styleText = styleText;
    }

    public String getValorFormatado() {
        return Util.formatarValor(this.valor, null, false);
    }

    public BigDecimal getValorTotalDestinos() {
        BigDecimal retorno = BigDecimal.ZERO;
        if (this.destinos != null && !this.destinos.isEmpty()) {
            for (ConfiguracaoTransporteSaldoSubContaDestino destino : this.destinos) {
                retorno = retorno.add(destino.getValor());
            }
        }
        return retorno;
    }

    public BigDecimal getValorTotalDestinos(ConfiguracaoTransporteSaldoSubContaDestino dest) {
        BigDecimal retorno = BigDecimal.ZERO;
        if (this.destinos != null && !this.destinos.isEmpty()) {
            for (ConfiguracaoTransporteSaldoSubContaDestino destino : this.destinos) {
                if (!destino.equals(dest)) {
                    retorno = retorno.add(destino.getValor());
                }
            }
        }
        return retorno;
    }

    public BigDecimal getValorDisponivel() {
        return valor.subtract(getValorTotalDestinos());
    }

    public void atualizarStyleText() {
        if (destinos != null && !destinos.isEmpty()) {
            if (valor.compareTo(getValorTotalDestinos()) == 0) {
                styleText = "verdenegrito";
            } else {
                styleText = "amarelonegrito";
            }
        } else {
            styleText = "vermelhonegrito";
        }
    }

    @Override
    public String toString() {
        return subConta.getCodigo() + " (" + contaDeDestinacao.getFonteDeRecursos().getCodigo() + ") - " + Util.formataValor(valor);
    }
}
