package br.com.webpublico.entidades.rh.dirf;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
public class DirfPessoa extends SuperEntidade implements Serializable, DirfVinculoFPPessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Fonte Pagadora Pessoa Jurídica")
    private Pessoa fontePagadora;
    @ManyToOne
    @Etiqueta("Pessoa Física Beneficiária dos Rendimentos")
    private PessoaFisica pessoaFisica;
    @Etiqueta("Natureza do Rendimento")
    private String naturezaDoRendimento;
    //------------- 3.
    @Etiqueta("01. Total dos Rendimentos (Inclusive férias)")
    private BigDecimal totalRendimentos;
    @Etiqueta("02. Contribuição Previdenciária Oficial")
    private BigDecimal contribuicaoPrevidenciaria;
    @Etiqueta("03. Pensão, Proventos de Aposentadoria ou Reforma, Reforma e Pensão (65 anos ou mais)")
    private BigDecimal pensaoProventos;
    @Etiqueta("04. Pensão Alimentícia (Informar o beneficiário no Quadro 7)")
    private BigDecimal pensaoAlimenticia;
    @Etiqueta("05. Imposto de Renda Retido")
    private BigDecimal impostoDeRendaRetido;
    //------------- 4.
    @Etiqueta("01. Parcela Isenta dos Proventos de Aposentadoria, Reserva e Pensão (65 anos ou mais)")
    private BigDecimal parcelaIsenta;
    @Etiqueta("02. Diárias e Ajudas de Custo")
    private BigDecimal diariasAjudasDeCusto;
    @Etiqueta("03. Pensão, Proventos de Aposentadoria ou Reforma por Moléstia Grave Aposentadoria ou Reforma por Acidente em Serviço")
    private BigDecimal pensaoProventosPorAcidente;
    @Etiqueta("04. Lucro e Dividendo Apurado a partir de 1996 pago por PJ (Lucro Real, Presumido ou Arbitrado)")
    private BigDecimal lucroEDividendoApurado;
    @Etiqueta("05. Valores Pagos ao Titular ou Sócio da microempresa ou Empresa de PequenoPorte exceto Pro labore, Aluguéis ou Serviços Prestados")
    private BigDecimal valoresPagosAoTitular;
    @Etiqueta("06. Indenizações por rescisão de contrato de trabalho, inclusive a título de PDV e acidente de trabalho")
    private BigDecimal indenizacoesPorRescisao;
    @Etiqueta("07. Outros (especificar)")
    private BigDecimal outrosRendimentosIsentos;
    //------------- 5.
    @Etiqueta("01. Décimo Terceiro Salário")
    private BigDecimal decimoTerceiroSalario;
    @Etiqueta("02. Imposto sobre a renda retido na fonte sobre 13o salário")
    private BigDecimal impostoSobreARendaRetidoNo13;
    @Etiqueta("03. Outros")
    private BigDecimal outrosSujeitosATributacao;
    //------------- 6.
    @Etiqueta("6.1 Número do processo")
    private String numeroProcesso;
    @Etiqueta("Quantidade de Meses")
    private BigDecimal quantidadeDeMeses;
    @Etiqueta("Natureza do rendimento Recebido Acumuladamente")
    private String naturezaDoRendimentoRecebido;
    @Etiqueta("1. Total dos rendimentos tributáveis (inclusive férias e décimo terceiro salário)")
    private BigDecimal totalRendimentosTributaveis;
    @Etiqueta("2. Exclusão: Despesas com a ação judicial")
    private BigDecimal exclusaoDespesasAcaoJudicial;
    @Etiqueta("3. Dedução: Contribuição previdenciária oficial")
    private BigDecimal deducaoContribuicaoPrev;
    @Etiqueta("4. Dedução: Pensão alimentícia (preencher também o quadro 7)")
    private BigDecimal deducaoPensaoAlimenticia;
    @Etiqueta("5. Imposto sobre a renda retido na fonte")
    private BigDecimal impostoSobreARendaRetido;
    @Etiqueta("6. Rendimentos isentos de pensão, proventos de aposentadoria ou reforma por moléstia grave ou aposentadoria ou reforma por acidente em serviço")
    private BigDecimal rendimentosIsentosDePensao;
    @ManyToOne
    private DirfVinculoFP dirfVinculoFP;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "dirfPessoa")
    private List<DirfValor> valores;

    public DirfPessoa() {
        valores = Lists.newArrayList();
        totalRendimentos = BigDecimal.ZERO;
        contribuicaoPrevidenciaria = BigDecimal.ZERO;
        pensaoProventos = BigDecimal.ZERO;
        pensaoAlimenticia = BigDecimal.ZERO;
        impostoDeRendaRetido = BigDecimal.ZERO;
        parcelaIsenta = BigDecimal.ZERO;
        diariasAjudasDeCusto = BigDecimal.ZERO;
        pensaoProventosPorAcidente = BigDecimal.ZERO;
        lucroEDividendoApurado = BigDecimal.ZERO;
        valoresPagosAoTitular = BigDecimal.ZERO;
        indenizacoesPorRescisao = BigDecimal.ZERO;
        outrosRendimentosIsentos = BigDecimal.ZERO;
        decimoTerceiroSalario = BigDecimal.ZERO;
        impostoSobreARendaRetidoNo13 = BigDecimal.ZERO;
        outrosSujeitosATributacao = BigDecimal.ZERO;
        quantidadeDeMeses = BigDecimal.ZERO;
        totalRendimentosTributaveis = BigDecimal.ZERO;
        exclusaoDespesasAcaoJudicial = BigDecimal.ZERO;
        deducaoContribuicaoPrev = BigDecimal.ZERO;
        deducaoPensaoAlimenticia = BigDecimal.ZERO;
        impostoSobreARendaRetido = BigDecimal.ZERO;
        rendimentosIsentosDePensao = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Dirf getDirf() {
        return null;
    }

    @Override
    public PessoaJuridica getFontePagadora() {
        return (PessoaJuridica) this.fontePagadora;
    }

    public void setFontePagadora(Pessoa fontePagadora) {
        this.fontePagadora = fontePagadora;
    }

    @Override
    public VinculoFP getVinculoFP() {
        return dirfVinculoFP.getVinculoFP();
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    @Override
    public String getNaturezaDoRendimento() {
        return naturezaDoRendimento;
    }

    public void setNaturezaDoRendimento(String naturezaDoRendimento) {
        this.naturezaDoRendimento = naturezaDoRendimento;
    }

    @Override
    public BigDecimal getTotalRendimentos() {
        return totalRendimentos;
    }

    public void setTotalRendimentos(BigDecimal totalRendimentos) {
        this.totalRendimentos = totalRendimentos;
    }

    public String getTotalRendimentosAsString() {
        return Util.formataValor(totalRendimentos);
    }

    @Override
    public BigDecimal getContribuicaoPrevidenciaria() {
        return contribuicaoPrevidenciaria;
    }

    public void setContribuicaoPrevidenciaria(BigDecimal contribuicaoPrevidenciaria) {
        this.contribuicaoPrevidenciaria = contribuicaoPrevidenciaria;
    }

    @Override
    public BigDecimal getPensaoProventos() {
        return pensaoProventos;
    }

    public void setPensaoProventos(BigDecimal pensaoProventos) {
        this.pensaoProventos = pensaoProventos;
    }

    @Override
    public BigDecimal getPensaoAlimenticia() {
        return pensaoAlimenticia;
    }

    public void setPensaoAlimenticia(BigDecimal pensaoAlimenticia) {
        this.pensaoAlimenticia = pensaoAlimenticia;
    }

    @Override
    public BigDecimal getImpostoDeRendaRetido() {
        return impostoDeRendaRetido;
    }

    public void setImpostoDeRendaRetido(BigDecimal impostoDeRendaRetido) {
        this.impostoDeRendaRetido = impostoDeRendaRetido;
    }

    @Override
    public BigDecimal getParcelaIsenta() {
        return parcelaIsenta;
    }

    public void setParcelaIsenta(BigDecimal parcelaIsenta) {
        this.parcelaIsenta = parcelaIsenta;
    }

    @Override
    public BigDecimal getDiariasAjudasDeCusto() {
        return diariasAjudasDeCusto;
    }

    public void setDiariasAjudasDeCusto(BigDecimal diariasAjudasDeCusto) {
        this.diariasAjudasDeCusto = diariasAjudasDeCusto;
    }

    @Override
    public BigDecimal getPensaoProventosPorAcidente() {
        return pensaoProventosPorAcidente;
    }

    public void setPensaoProventosPorAcidente(BigDecimal pensaoProventosPorAcidente) {
        this.pensaoProventosPorAcidente = pensaoProventosPorAcidente;
    }

    @Override
    public BigDecimal getLucroEDividendoApurado() {
        return lucroEDividendoApurado;
    }

    public void setLucroEDividendoApurado(BigDecimal lucroEDividendoApurado) {
        this.lucroEDividendoApurado = lucroEDividendoApurado;
    }

    @Override
    public BigDecimal getValoresPagosAoTitular() {
        return valoresPagosAoTitular;
    }

    public void setValoresPagosAoTitular(BigDecimal valoresPagosAoTitular) {
        this.valoresPagosAoTitular = valoresPagosAoTitular;
    }

    @Override
    public BigDecimal getIndenizacoesPorRescisao() {
        return indenizacoesPorRescisao;
    }

    public void setIndenizacoesPorRescisao(BigDecimal indenizacoesPorRescisao) {
        this.indenizacoesPorRescisao = indenizacoesPorRescisao;
    }

    @Override
    public BigDecimal getOutrosRendimentosIsentos() {
        return outrosRendimentosIsentos;
    }

    public void setOutrosRendimentosIsentos(BigDecimal outrosRendimentosIsentos) {
        this.outrosRendimentosIsentos = outrosRendimentosIsentos;
    }

    @Override
    public BigDecimal getDecimoTerceiroSalario() {
        return decimoTerceiroSalario;
    }

    public void setDecimoTerceiroSalario(BigDecimal decimoTerceiroSalario) {
        this.decimoTerceiroSalario = decimoTerceiroSalario;
    }

    @Override
    public BigDecimal getDecimoTerceiroSalarioPensao() {
        return null;
    }

    @Override
    public BigDecimal getImpostoSobreARendaRetidoNo13() {
        return impostoSobreARendaRetidoNo13;
    }

    public void setImpostoSobreARendaRetidoNo13(BigDecimal impostoSobreARendaRetidoNo13) {
        this.impostoSobreARendaRetidoNo13 = impostoSobreARendaRetidoNo13;
    }

    @Override
    public BigDecimal getOutrosSujeitosATributacao() {
        return outrosSujeitosATributacao;
    }

    public void setOutrosSujeitosATributacao(BigDecimal outrosSujeitosATributacao) {
        this.outrosSujeitosATributacao = outrosSujeitosATributacao;
    }

    @Override
    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    @Override
    public BigDecimal getQuantidadeDeMeses() {
        return quantidadeDeMeses;
    }

    public void setQuantidadeDeMeses(BigDecimal quantidadeDeMeses) {
        this.quantidadeDeMeses = quantidadeDeMeses;
    }

    @Override
    public String getNaturezaDoRendimentoRecebido() {
        return naturezaDoRendimentoRecebido;
    }

    public void setNaturezaDoRendimentoRecebido(String naturezaDoRendimentoRecebido) {
        this.naturezaDoRendimentoRecebido = naturezaDoRendimentoRecebido;
    }

    @Override
    public BigDecimal getTotalRendimentosTributaveis() {
        return totalRendimentosTributaveis;
    }

    public void setTotalRendimentosTributaveis(BigDecimal totalRendimentosTributaveis) {
        this.totalRendimentosTributaveis = totalRendimentosTributaveis;
    }

    @Override
    public BigDecimal getExclusaoDespesasAcaoJudicial() {
        return exclusaoDespesasAcaoJudicial;
    }

    public void setExclusaoDespesasAcaoJudicial(BigDecimal exclusaoDespesasAcaoJudicial) {
        this.exclusaoDespesasAcaoJudicial = exclusaoDespesasAcaoJudicial;
    }

    @Override
    public BigDecimal getDeducaoContribuicaoPrev() {
        return deducaoContribuicaoPrev;
    }

    public void setDeducaoContribuicaoPrev(BigDecimal deducaoContribuicaoPrev) {
        this.deducaoContribuicaoPrev = deducaoContribuicaoPrev;
    }

    @Override
    public BigDecimal getDeducaoPensaoAlimenticia() {
        return deducaoPensaoAlimenticia;
    }

    public void setDeducaoPensaoAlimenticia(BigDecimal deducaoPensaoAlimenticia) {
        this.deducaoPensaoAlimenticia = deducaoPensaoAlimenticia;
    }

    @Override
    public BigDecimal ImpostoSobreARendaRetido() {
        return null;
    }

    @Override
    public BigDecimal getImpostoSobreARendaRetido() {
        return impostoSobreARendaRetido;
    }

    public void setImpostoSobreARendaRetido(BigDecimal impostoSobreARendaRetido) {
        this.impostoSobreARendaRetido = impostoSobreARendaRetido;
    }

    @Override
    public BigDecimal getRendimentosIsentosDePensao() {
        return rendimentosIsentosDePensao;
    }

    public void setRendimentosIsentosDePensao(BigDecimal rendimentosIsentosDePensao) {
        this.rendimentosIsentosDePensao = rendimentosIsentosDePensao;
    }

    @Override
    public Boolean isDirfVinculo() {
        return false;
    }

    @Override
    public List<DirfInfoComplementares> getInformacoesComplementares() {
        return null;
    }

    @Override
    public String getNome() {
        return pessoaFisica.getNome();
    }

    @Override
    public String getCpf() {
        return pessoaFisica.getCpf();
    }

    public DirfVinculoFP getDirfVinculoFP() {
        return dirfVinculoFP;
    }

    public void setDirfVinculoFP(DirfVinculoFP dirfVinculoFP) {
        this.dirfVinculoFP = dirfVinculoFP;
    }

    public List<DirfValor> getValores() {
        return valores;
    }

    public void setValores(List<DirfValor> valores) {
        this.valores = valores;
    }

    public List<DirfValor> getValoresRendimentosTributaveis() {
        List<DirfValor> retorno = Lists.newArrayList();
        for (DirfValor dirfValor : valores) {
            if (dirfValor.getDirfTipoValor().isRendimentosTributaveis()) {
                retorno.add(dirfValor);
            }
        }
        return retorno;
    }

    public BigDecimal getTotalValoresRendimentosTributaveis() {
        BigDecimal total = BigDecimal.ZERO;
        for (DirfValor dirfValor : getValoresRendimentosTributaveis()) {
            total = total.add(dirfValor.getValor());
        }
        return total;
    }

    public List<DirfValor> getValoresSalarioFamilia() {
        List<DirfValor> retorno = Lists.newArrayList();
        for (DirfValor dirfValor : valores) {
            if (dirfValor.getDirfTipoValor().isSalarioFamilia()) {
                retorno.add(dirfValor);
            }
        }
        return retorno;
    }

    public BigDecimal getTotalValoresSalarioFamilia() {
        BigDecimal total = BigDecimal.ZERO;
        for (DirfValor dirfValor : getValoresSalarioFamilia()) {
            total = total.add(dirfValor.getValor());
        }
        return total;
    }
}
