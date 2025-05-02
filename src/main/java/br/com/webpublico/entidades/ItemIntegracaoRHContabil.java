package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.rh.esocial.TipoRegimePrevidenciario;
import br.com.webpublico.enums.rh.integracao.TipoContabilizacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Integração RH/Contábil - Item")
public class ItemIntegracaoRHContabil extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Fornecedor")
    private Pessoa fornecedor;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe credor")
    private ClasseCredor classeCredor;
    @ManyToOne
    private IntegracaoRHContabil integracaoRHContabil;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Fornecedor")
    private DespesaORC despesaORC;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Fonte de Recurso")
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Conta Financeira")
    private SubConta subConta;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Desdobramento")
    private Conta desdobramento;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Valor")
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Conta de Despesa")
    private TipoContaDespesa tipoContaDespesa;
    @Enumerated(EnumType.STRING)
    private TipoRegimePrevidenciario tipoPrevidencia;
    @Enumerated(EnumType.STRING)
    private TipoContabilizacao tipoContabilizacao;
    @ManyToOne
    private FonteEventoFP fonteEventoFP;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "itemIntegracaoRHContabil")
    private List<ServidorIntegracaoRHContabil> servidores;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "itemIntegracaoRHContabil")
    private List<RetencaoIntegracaoRHContabil> retencoes;

    public ItemIntegracaoRHContabil() {
        this.valor = BigDecimal.ZERO;
        this.servidores = Lists.newArrayList();
        this.retencoes = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public IntegracaoRHContabil getIntegracaoRHContabil() {
        return integracaoRHContabil;
    }

    public void setIntegracaoRHContabil(IntegracaoRHContabil integracaoRHContabil) {
        this.integracaoRHContabil = integracaoRHContabil;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public FonteEventoFP getFonteEventoFP() {
        return fonteEventoFP;
    }

    public void setFonteEventoFP(FonteEventoFP fonteEventoFP) {
        this.fonteEventoFP = fonteEventoFP;
    }

    public List<ServidorIntegracaoRHContabil> getServidores() {
        return servidores;
    }

    public void setServidores(List<ServidorIntegracaoRHContabil> servidores) {
        this.servidores = servidores;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Conta getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(Conta desdobramento) {
        this.desdobramento = desdobramento;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public List<RetencaoIntegracaoRHContabil> getRetencoes() {
        return retencoes;
    }

    public void setRetencoes(List<RetencaoIntegracaoRHContabil> retencoes) {
        this.retencoes = retencoes;
    }

    public BigDecimal getValorLiquido() {
        return this.getValor().subtract(this.getValorTotalRetencoes());
    }

    public Boolean isSaldoDisponivel() {
        return this.getValor().compareTo(this.getValorTotalRetencoes()) > 0;
    }

    public TipoRegimePrevidenciario getTipoPrevidencia() {
        return tipoPrevidencia;
    }

    public void setTipoPrevidencia(TipoRegimePrevidenciario tipoPrevidencia) {
        this.tipoPrevidencia = tipoPrevidencia;
    }

    public TipoContabilizacao getTipoContabilizacao() {
        return tipoContabilizacao;
    }

    public void setTipoContabilizacao(TipoContabilizacao tipoContabilizacao) {
        this.tipoContabilizacao = tipoContabilizacao;
    }

    public BigDecimal getValorTotalRetencoes() {
        BigDecimal valor = BigDecimal.ZERO;
        if (retencoes != null) {
            for (RetencaoIntegracaoRHContabil retencao : retencoes) {
                valor = valor.add(retencao.getValor());
            }
        }
        return valor;
    }
}

