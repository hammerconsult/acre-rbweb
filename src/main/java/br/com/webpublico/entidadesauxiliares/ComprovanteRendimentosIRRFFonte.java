package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.rh.dirf.DirfInfoComplementares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by peixe on 17/08/2015.
 */
public class ComprovanteRendimentosIRRFFonte implements Serializable {
    //Cabeçalho
    private Integer anoCalendario;
    private Integer anoExercicio;

    //1. Fonte Pagadora Pessoa Jurídica
    private String fontePagadora;
    private String cnpjFontePagadora;

    //2. Pessoa Física Beneficiária dos Rendimentos
    private String cpfBeneficiaria;
    private String nome;
    //TODO verificar necessidade de enum ou cadastro;
    private String naturezaRendimento;

    //3. Rendimentos Tributáveis, Deduções e Imposto Retido na Fonte
    private BigDecimal totalRendimentos;
    private BigDecimal contribuicaoPrevidenciaria;
    private BigDecimal contribuicaoPreviFundoAposentadoria;
    private BigDecimal pensaoAlimenticia;
    private BigDecimal impostoRetido;

    //4. Rendimentos Isentos e não Tributáveis
    private BigDecimal parcelIsentaProvetosAposentadoria;
    private BigDecimal diariasEAjudaCusto;
    private BigDecimal pensoesProventosApoMolestiaGrave;
    private BigDecimal lucroEDividendo;
    private BigDecimal valoresPagosAoTitular;
    private BigDecimal indenizacaoPorRescisaoContrato;
    private BigDecimal outrosIsentos;
    private List<DirfInfoComplementares> informacoesComplementares;

    private List<PensaoJudicialRetidoFonte> pensoesJudiciais;

    //5. Rendimentos sujeitos à tributação exclusiva (rendimento líquido)
    private BigDecimal decimoTerceiro;
    private BigDecimal irRetidoNaFonte13Salario;
    private BigDecimal outrosSujeitosTributacao;

    //6. Rendimentos recebidos acumuladamente - Art. 12-A da Lei nº 7.713, de 1988 (sujeito à tributação exclusiva)
    private String numeroProcesso;
    private BigDecimal quantidadeDeMeses;
    private String naturezaDoRendimentoRecebido;
    private BigDecimal totalRendimentosTributaveis;
    private BigDecimal exclusaoDespesasAcaoJudicial;
    private BigDecimal deducaoContribuicaoPrev;
    private BigDecimal deducaoPensaoAlimenticia;
    private BigDecimal impostoSobreARendaRetido;
    private BigDecimal rendimentosIsentosDePensao;

    //7. Informações Complementares
    private String cpfBeneficiarioPensao;
    private Date nascimentoBeneficiarioPensao;
    private String nomeBeneficiarioPensao;
    private BigDecimal valor;

    private boolean pensao;


    //8. Responsável pelas informações
    private String nomeResponsavel;
    private Date data;

    private String brasao;


    public ComprovanteRendimentosIRRFFonte() {

        totalRendimentos = BigDecimal.ZERO;
        contribuicaoPrevidenciaria = BigDecimal.ZERO;
        contribuicaoPreviFundoAposentadoria = BigDecimal.ZERO;
        pensaoAlimenticia = BigDecimal.ZERO;
        impostoRetido = BigDecimal.ZERO;


        parcelIsentaProvetosAposentadoria = BigDecimal.ZERO;
        diariasEAjudaCusto = BigDecimal.ZERO;
        pensoesProventosApoMolestiaGrave = BigDecimal.ZERO;
        lucroEDividendo = BigDecimal.ZERO;
        valoresPagosAoTitular = BigDecimal.ZERO;
        indenizacaoPorRescisaoContrato = BigDecimal.ZERO;
        outrosIsentos = BigDecimal.ZERO;


        decimoTerceiro = BigDecimal.ZERO;
        irRetidoNaFonte13Salario = BigDecimal.ZERO;
        outrosSujeitosTributacao = BigDecimal.ZERO;

        quantidadeDeMeses = BigDecimal.ZERO;
        totalRendimentosTributaveis = BigDecimal.ZERO;
        exclusaoDespesasAcaoJudicial = BigDecimal.ZERO;
        deducaoContribuicaoPrev = BigDecimal.ZERO;
        deducaoPensaoAlimenticia = BigDecimal.ZERO;
        impostoSobreARendaRetido = BigDecimal.ZERO;
        rendimentosIsentosDePensao = BigDecimal.ZERO;

        valor = BigDecimal.ZERO;

        informacoesComplementares = new LinkedList<>();
    }

    public List<DirfInfoComplementares> getInformacoesComplementares() {
        return informacoesComplementares;
    }

    public void setInformacoesComplementares(List<DirfInfoComplementares> informacoesComplementares) {
        this.informacoesComplementares = informacoesComplementares;
    }

    public String getBrasao() {
        return brasao;
    }

    public void setBrasao(String brasao) {
        this.brasao = brasao;
    }

    public Integer getAnoCalendario() {
        return anoCalendario;
    }

    public void setAnoCalendario(Integer anoCalendario) {
        this.anoCalendario = anoCalendario;
    }

    public Integer getAnoExercicio() {
        return anoExercicio;
    }

    public void setAnoExercicio(Integer anoExercicio) {
        this.anoExercicio = anoExercicio;
    }

    public String getFontePagadora() {
        return fontePagadora;
    }

    public void setFontePagadora(String fontePagadora) {
        this.fontePagadora = fontePagadora;
    }

    public String getCnpjFontePagadora() {
        return cnpjFontePagadora;
    }

    public void setCnpjFontePagadora(String cnpjFontePagadora) {
        this.cnpjFontePagadora = cnpjFontePagadora;
    }

    public String getCpfBeneficiaria() {
        return cpfBeneficiaria;
    }

    public void setCpfBeneficiaria(String cpfBeneficiaria) {
        this.cpfBeneficiaria = cpfBeneficiaria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNaturezaRendimento() {
        return naturezaRendimento;
    }

    public void setNaturezaRendimento(String naturezaRendimento) {
        this.naturezaRendimento = naturezaRendimento;
    }

    public BigDecimal getTotalRendimentos() {
        return totalRendimentos;
    }

    public void setTotalRendimentos(BigDecimal totalRendimentos) {
        this.totalRendimentos = totalRendimentos;
    }

    public void adicionarTotalRendimentos(BigDecimal totalRendimentos) {
        this.totalRendimentos = this.totalRendimentos.add(totalRendimentos);
    }

    public List<PensaoJudicialRetidoFonte> getPensoesJudiciais() {
        return pensoesJudiciais;
    }

    public void setPensoesJudiciais(List<PensaoJudicialRetidoFonte> pensoesJudiciais) {
        this.pensoesJudiciais = pensoesJudiciais;
    }

    public BigDecimal getContribuicaoPrevidenciaria() {
        return contribuicaoPrevidenciaria;
    }

    public void setContribuicaoPrevidenciaria(BigDecimal contribuicaoPrevidenciaria) {
        this.contribuicaoPrevidenciaria = this.contribuicaoPrevidenciaria.add(contribuicaoPrevidenciaria);
    }

    public BigDecimal getContribuicaoPreviFundoAposentadoria() {
        return contribuicaoPreviFundoAposentadoria;
    }

    public void setContribuicaoPreviFundoAposentadoria(BigDecimal contribuicaoPreviFundoAposentadoria) {
        this.contribuicaoPreviFundoAposentadoria = this.contribuicaoPreviFundoAposentadoria.add(contribuicaoPreviFundoAposentadoria);
    }

    public BigDecimal getPensaoAlimenticia() {
        return pensaoAlimenticia;
    }

    public void setPensaoAlimenticia(BigDecimal pensaoAlimenticia) {
        this.pensaoAlimenticia = this.pensaoAlimenticia.add(pensaoAlimenticia);
    }

    public BigDecimal getImpostoRetido() {
        return impostoRetido;
    }

    public void setImpostoRetido(BigDecimal impostoRetido) {
        this.impostoRetido = this.impostoRetido.add(impostoRetido);
    }

    public BigDecimal getParcelIsentaProvetosAposentadoria() {
        return parcelIsentaProvetosAposentadoria;
    }

    public void setParcelIsentaProvetosAposentadoria(BigDecimal parcelIsentaProvetosAposentadoria) {
        this.parcelIsentaProvetosAposentadoria = this.parcelIsentaProvetosAposentadoria.add(parcelIsentaProvetosAposentadoria);
    }

    public BigDecimal getDiariasEAjudaCusto() {
        return diariasEAjudaCusto;
    }

    public void setDiariasEAjudaCusto(BigDecimal diariasEAjudaCusto) {
        this.diariasEAjudaCusto = this.diariasEAjudaCusto.add(diariasEAjudaCusto);
    }

    public BigDecimal getPensoesProventosApoMolestiaGrave() {
        return pensoesProventosApoMolestiaGrave;
    }

    public void setPensoesProventosApoMolestiaGrave(BigDecimal pensoesProventosApoMolestiaGrave) {
        this.pensoesProventosApoMolestiaGrave = this.pensoesProventosApoMolestiaGrave.add(pensoesProventosApoMolestiaGrave);
    }

    public BigDecimal getLucroEDividendo() {
        return lucroEDividendo;
    }

    public void setLucroEDividendo(BigDecimal lucroEDividendo) {
        this.lucroEDividendo = this.lucroEDividendo.add(lucroEDividendo);
    }

    public BigDecimal getValoresPagosAoTitular() {
        return valoresPagosAoTitular;
    }

    public void setValoresPagosAoTitular(BigDecimal valoresPagosAoTitular) {
        this.valoresPagosAoTitular = this.valoresPagosAoTitular.add(valoresPagosAoTitular);
    }

    public BigDecimal getIndenizacaoPorRescisaoContrato() {
        return indenizacaoPorRescisaoContrato;
    }

    public void setIndenizacaoPorRescisaoContrato(BigDecimal indenizacaoPorRescisaoContrato) {
        this.indenizacaoPorRescisaoContrato = this.indenizacaoPorRescisaoContrato.add(indenizacaoPorRescisaoContrato);
    }

    public BigDecimal getOutrosIsentos() {
        return outrosIsentos;
    }

    public void setOutrosIsentos(BigDecimal outrosIsentos) {
        this.outrosIsentos = this.outrosIsentos.add(outrosIsentos);
    }

    public BigDecimal getDecimoTerceiro() {
        return decimoTerceiro;
    }

    public void setDecimoTerceiro(BigDecimal decimoTerceiro) {
        this.decimoTerceiro = decimoTerceiro;
    }

    public BigDecimal getIrRetidoNaFonte13Salario() {
        return irRetidoNaFonte13Salario;
    }

    public void setIrRetidoNaFonte13Salario(BigDecimal irRetidoNaFonte13Salario) {
        this.irRetidoNaFonte13Salario = this.irRetidoNaFonte13Salario.add(irRetidoNaFonte13Salario);
    }

    public BigDecimal getOutrosSujeitosTributacao() {
        return outrosSujeitosTributacao;
    }

    public void setOutrosSujeitosTributacao(BigDecimal outrosSujeitosTributacao) {
        this.outrosSujeitosTributacao = this.outrosSujeitosTributacao.add(outrosSujeitosTributacao);
    }

    public String getCpfBeneficiarioPensao() {
        return cpfBeneficiarioPensao;
    }

    public void setCpfBeneficiarioPensao(String cpfBeneficiarioPensao) {
        this.cpfBeneficiarioPensao = cpfBeneficiarioPensao;
    }

    public String getNomeBeneficiarioPensao() {
        return nomeBeneficiarioPensao;
    }

    public void setNomeBeneficiarioPensao(String nomeBeneficiarioPensao) {
        this.nomeBeneficiarioPensao = nomeBeneficiarioPensao;
    }

    public Date getNascimentoBeneficiarioPensao() {
        return nascimentoBeneficiarioPensao;
    }

    public void setNascimentoBeneficiarioPensao(Date nascimentoBeneficiarioPensao) {
        this.nascimentoBeneficiarioPensao = nascimentoBeneficiarioPensao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public BigDecimal getQuantidadeDeMeses() {
        return quantidadeDeMeses;
    }

    public void setQuantidadeDeMeses(BigDecimal quantidadeDeMeses) {
        this.quantidadeDeMeses = quantidadeDeMeses;
    }

    public String getNaturezaDoRendimentoRecebido() {
        return naturezaDoRendimentoRecebido;
    }

    public void setNaturezaDoRendimentoRecebido(String naturezaDoRendimentoRecebido) {
        this.naturezaDoRendimentoRecebido = naturezaDoRendimentoRecebido;
    }

    public BigDecimal getTotalRendimentosTributaveis() {
        return totalRendimentosTributaveis;
    }

    public void setTotalRendimentosTributaveis(BigDecimal totalRendimentosTributaveis) {
        this.totalRendimentosTributaveis = totalRendimentosTributaveis;
    }

    public BigDecimal getExclusaoDespesasAcaoJudicial() {
        return exclusaoDespesasAcaoJudicial;
    }

    public void setExclusaoDespesasAcaoJudicial(BigDecimal exclusaoDespesasAcaoJudicial) {
        this.exclusaoDespesasAcaoJudicial = exclusaoDespesasAcaoJudicial;
    }

    public BigDecimal getDeducaoContribuicaoPrev() {
        return deducaoContribuicaoPrev;
    }

    public void setDeducaoContribuicaoPrev(BigDecimal deducaoContribuicaoPrev) {
        this.deducaoContribuicaoPrev = deducaoContribuicaoPrev;
    }

    public BigDecimal getDeducaoPensaoAlimenticia() {
        return deducaoPensaoAlimenticia;
    }

    public void setDeducaoPensaoAlimenticia(BigDecimal deducaoPensaoAlimenticia) {
        this.deducaoPensaoAlimenticia = deducaoPensaoAlimenticia;
    }

    public BigDecimal getImpostoSobreARendaRetido() {
        return impostoSobreARendaRetido;
    }

    public void setImpostoSobreARendaRetido(BigDecimal impostoSobreARendaRetido) {
        this.impostoSobreARendaRetido = impostoSobreARendaRetido;
    }

    public BigDecimal getRendimentosIsentosDePensao() {
        return rendimentosIsentosDePensao;
    }

    public void setRendimentosIsentosDePensao(BigDecimal rendimentosIsentosDePensao) {
        this.rendimentosIsentosDePensao = rendimentosIsentosDePensao;
    }

    public boolean isPensao() {
        return pensao;
    }

    public void setPensao(boolean pensao) {
        this.pensao = pensao;
    }
}
