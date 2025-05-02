package br.com.webpublico.entidadesauxiliares;

import com.beust.jcommander.internal.Lists;

import java.math.BigDecimal;
import java.util.List;

public class NotaExecucaoOrcamentaria {
    private Long id;
    private String codigoOrgao;
    private String codigoUnidade;
    private String descricaoOrgao;
    private String descricaoUnidade;
    private String especificacaoAcao;
    private String funcionalProgramatica;
    private String especificacaoDespesa;
    private String codigoDestinacao;
    private String descricaoDestinacao;
    private String codigoProjetoAtividade;
    private String descricaoProjetoAtividade;
    private String naturezaDespesa;
    private String nomePessoa;
    private String classePessoa;
    private String cpfCnpj;
    private String logradouro;
    private String localidade;
    private String bairro;
    private String cidade;
    private String cep;
    private String uf;
    private String valorPorExtenso;
    private String historico;
    private String licitacao;
    private String numeroLicitacao;
    private String modalidadeLicitacao;
    private String codigoFuncao;
    private String descricaoFuncao;
    private String codigoSubFuncao;
    private String descricaoSubFuncao;
    private String codigoProgramaTrabalho;
    private String codigoPrograma;
    private String descricaoPrograma;
    private BigDecimal idUnidadeOrcamentaria;
    private String numeroDocumento;
    private String dataEstorno;
    private String contaExtraorcamentaria;
    private Boolean hasIdRetencaoPagamento;
    private String nomeDaNota;

    private BigDecimal saldoAnterior;
    private BigDecimal valor;
    private BigDecimal saldoAtual;
    private BigDecimal valorEstorno;

    // Atributos Empenho
    private String tipoEmpenho;
    private String numeroEmpenho;
    private String data;
    private String exercicioOriginalEmpenho;

    // Atributos Liquidação
    private String numeroLiquidacao;
    private String dataLiquidacao;
    private BigDecimal valorEmpenhado;

    // Atributos Pagamento
    private String numeroPagamento;
    private String dataPagamento;
    private BigDecimal valorLiquidado;
    private String descricaoAgencia;
    private String descricaoBanco;
    private String numeroAgencia;
    private String numeroBanco;
    private String digitoVerificador;
    private String numeroContaCorrente;
    private String digitoVerificadorContaCorrente;
    private String tipoContaBancaria;
    private String situacaoContaBancaria;
    private String bancoAgenciaConta;
    private String bancoContaBancariaEntidade;
    private String contaFinanceira;

    //Atributos Contrato
    private String numeroContrato;
    private String inicioContrato;
    private String fimContrato;
    private String fimContratoAtual;
    private String execucaoContrato;

    private List<NotaExecucaoOrcamentariaDocumentoComprobatorio> documentosComprobatorios;
    private List<NotaExecucaoOrcamentariaDetalhamento> detalhamentos;
    private List<NotaExecucaoOrcamentariaRetencao> retencoes;
    private List<NotaExecucaoOrcamentariaPagamento> pagamentos;
    private List<NotaExecucaoOrcamentariaFatura> faturas;
    private List<NotaExecucaoOrcamentariaConvenio> convenios;
    private List<NotaExecucaoOrcamentariaGps> gps;
    private List<NotaExecucaoOrcamentariaDarf> darfs;
    private List<NotaExecucaoOrcamentariaDarf> darfsSimples;
    private List<NotaExecucaoOrcamentariaReceitaExtra> receitasExtras;

    public NotaExecucaoOrcamentaria() {
        valor = BigDecimal.ZERO;
        saldoAnterior = BigDecimal.ZERO;
        saldoAtual = BigDecimal.ZERO;
        valorEstorno = BigDecimal.ZERO;
        valorEmpenhado = BigDecimal.ZERO;
        valorLiquidado = BigDecimal.ZERO;
        documentosComprobatorios = Lists.newArrayList();
        detalhamentos = Lists.newArrayList();
        retencoes = Lists.newArrayList();
        pagamentos = Lists.newArrayList();
        faturas = Lists.newArrayList();
        convenios = Lists.newArrayList();
        gps = Lists.newArrayList();
        darfs = Lists.newArrayList();
        darfsSimples = Lists.newArrayList();
        receitasExtras = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public String getEspecificacaoAcao() {
        return especificacaoAcao;
    }

    public void setEspecificacaoAcao(String especificacaoAcao) {
        this.especificacaoAcao = especificacaoAcao;
    }

    public String getFuncionalProgramatica() {
        return funcionalProgramatica;
    }

    public void setFuncionalProgramatica(String funcionalProgramatica) {
        this.funcionalProgramatica = funcionalProgramatica;
    }

    public String getEspecificacaoDespesa() {
        return especificacaoDespesa;
    }

    public void setEspecificacaoDespesa(String especificacaoDespesa) {
        this.especificacaoDespesa = especificacaoDespesa;
    }

    public String getCodigoDestinacao() {
        return codigoDestinacao;
    }

    public void setCodigoDestinacao(String codigoDestinacao) {
        this.codigoDestinacao = codigoDestinacao;
    }

    public String getDescricaoDestinacao() {
        return descricaoDestinacao;
    }

    public void setDescricaoDestinacao(String descricaoDestinacao) {
        this.descricaoDestinacao = descricaoDestinacao;
    }

    public String getCodigoProjetoAtividade() {
        return codigoProjetoAtividade;
    }

    public void setCodigoProjetoAtividade(String codigoProjetoAtividade) {
        this.codigoProjetoAtividade = codigoProjetoAtividade;
    }

    public String getDescricaoProjetoAtividade() {
        return descricaoProjetoAtividade;
    }

    public void setDescricaoProjetoAtividade(String descricaoProjetoAtividade) {
        this.descricaoProjetoAtividade = descricaoProjetoAtividade;
    }

    public String getNaturezaDespesa() {
        return naturezaDespesa;
    }

    public void setNaturezaDespesa(String naturezaDespesa) {
        this.naturezaDespesa = naturezaDespesa;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String getClassePessoa() {
        return classePessoa;
    }

    public void setClassePessoa(String classePessoa) {
        this.classePessoa = classePessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getValorPorExtenso() {
        return valorPorExtenso;
    }

    public void setValorPorExtenso(String valorPorExtenso) {
        this.valorPorExtenso = valorPorExtenso;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(String licitacao) {
        this.licitacao = licitacao;
    }

    public String getNumeroLicitacao() {
        return numeroLicitacao;
    }

    public void setNumeroLicitacao(String numeroLicitacao) {
        this.numeroLicitacao = numeroLicitacao;
    }

    public String getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(String modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public String getCodigoFuncao() {
        return codigoFuncao;
    }

    public void setCodigoFuncao(String codigoFuncao) {
        this.codigoFuncao = codigoFuncao;
    }

    public String getDescricaoFuncao() {
        return descricaoFuncao;
    }

    public void setDescricaoFuncao(String descricaoFuncao) {
        this.descricaoFuncao = descricaoFuncao;
    }

    public String getCodigoSubFuncao() {
        return codigoSubFuncao;
    }

    public void setCodigoSubFuncao(String codigoSubFuncao) {
        this.codigoSubFuncao = codigoSubFuncao;
    }

    public String getDescricaoSubFuncao() {
        return descricaoSubFuncao;
    }

    public void setDescricaoSubFuncao(String descricaoSubFuncao) {
        this.descricaoSubFuncao = descricaoSubFuncao;
    }

    public String getCodigoPrograma() {
        return codigoPrograma;
    }

    public void setCodigoPrograma(String codigoPrograma) {
        this.codigoPrograma = codigoPrograma;
    }

    public String getDescricaoPrograma() {
        return descricaoPrograma;
    }

    public void setDescricaoPrograma(String descricaoPrograma) {
        this.descricaoPrograma = descricaoPrograma;
    }

    public BigDecimal getIdUnidadeOrcamentaria() {
        return idUnidadeOrcamentaria;
    }

    public void setIdUnidadeOrcamentaria(BigDecimal idUnidadeOrcamentaria) {
        this.idUnidadeOrcamentaria = idUnidadeOrcamentaria;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public String getTipoEmpenho() {
        return tipoEmpenho;
    }

    public void setTipoEmpenho(String tipoEmpenho) {
        this.tipoEmpenho = tipoEmpenho;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExercicioOriginalEmpenho() {
        return exercicioOriginalEmpenho;
    }

    public void setExercicioOriginalEmpenho(String exercicioOriginalEmpenho) {
        this.exercicioOriginalEmpenho = exercicioOriginalEmpenho;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getInicioContrato() {
        return inicioContrato;
    }

    public void setInicioContrato(String inicioContrato) {
        this.inicioContrato = inicioContrato;
    }

    public String getFimContrato() {
        return fimContrato;
    }

    public void setFimContrato(String fimContrato) {
        this.fimContrato = fimContrato;
    }

    public String getFimContratoAtual() {
        return fimContratoAtual;
    }

    public void setFimContratoAtual(String fimContratoAtual) {
        this.fimContratoAtual = fimContratoAtual;
    }

    public String getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(String execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public String getCodigoProgramaTrabalho() {
        return codigoProgramaTrabalho;
    }

    public void setCodigoProgramaTrabalho(String codigoProgramaTrabalho) {
        this.codigoProgramaTrabalho = codigoProgramaTrabalho;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public BigDecimal getValorEstorno() {
        return valorEstorno;
    }

    public void setValorEstorno(BigDecimal valorEstorno) {
        this.valorEstorno = valorEstorno;
    }

    public String getNumeroLiquidacao() {
        return numeroLiquidacao;
    }

    public void setNumeroLiquidacao(String numeroLiquidacao) {
        this.numeroLiquidacao = numeroLiquidacao;
    }

    public String getDataLiquidacao() {
        return dataLiquidacao;
    }

    public void setDataLiquidacao(String dataLiquidacao) {
        this.dataLiquidacao = dataLiquidacao;
    }

    public BigDecimal getValorEmpenhado() {
        return valorEmpenhado;
    }

    public void setValorEmpenhado(BigDecimal valorEmpenhado) {
        this.valorEmpenhado = valorEmpenhado;
    }

    public String getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(String dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public List<NotaExecucaoOrcamentariaDocumentoComprobatorio> getDocumentosComprobatorios() {
        return documentosComprobatorios;
    }

    public void setDocumentosComprobatorios(List<NotaExecucaoOrcamentariaDocumentoComprobatorio> documentosComprobatorios) {
        this.documentosComprobatorios = documentosComprobatorios;
    }

    public List<NotaExecucaoOrcamentariaDetalhamento> getDetalhamentos() {
        return detalhamentos;
    }

    public void setDetalhamentos(List<NotaExecucaoOrcamentariaDetalhamento> detalhamentos) {
        this.detalhamentos = detalhamentos;
    }

    public String getNumeroPagamento() {
        return numeroPagamento;
    }

    public void setNumeroPagamento(String numeroPagamento) {
        this.numeroPagamento = numeroPagamento;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getValorLiquidado() {
        return valorLiquidado;
    }

    public void setValorLiquidado(BigDecimal valorLiquidado) {
        this.valorLiquidado = valorLiquidado;
    }

    public String getDescricaoAgencia() {
        return descricaoAgencia;
    }

    public void setDescricaoAgencia(String descricaoAgencia) {
        this.descricaoAgencia = descricaoAgencia;
    }

    public String getDescricaoBanco() {
        return descricaoBanco;
    }

    public void setDescricaoBanco(String descricaoBanco) {
        this.descricaoBanco = descricaoBanco;
    }

    public String getNumeroAgencia() {
        return numeroAgencia;
    }

    public void setNumeroAgencia(String numeroAgencia) {
        this.numeroAgencia = numeroAgencia;
    }

    public String getNumeroBanco() {
        return numeroBanco;
    }

    public void setNumeroBanco(String numeroBanco) {
        this.numeroBanco = numeroBanco;
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(String digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
    }

    public String getNumeroContaCorrente() {
        return numeroContaCorrente;
    }

    public void setNumeroContaCorrente(String numeroContaCorrente) {
        this.numeroContaCorrente = numeroContaCorrente;
    }

    public String getDigitoVerificadorContaCorrente() {
        return digitoVerificadorContaCorrente;
    }

    public void setDigitoVerificadorContaCorrente(String digitoVerificadorContaCorrente) {
        this.digitoVerificadorContaCorrente = digitoVerificadorContaCorrente;
    }

    public String getTipoContaBancaria() {
        return tipoContaBancaria;
    }

    public void setTipoContaBancaria(String tipoContaBancaria) {
        this.tipoContaBancaria = tipoContaBancaria;
    }

    public String getSituacaoContaBancaria() {
        return situacaoContaBancaria;
    }

    public void setSituacaoContaBancaria(String situacaoContaBancaria) {
        this.situacaoContaBancaria = situacaoContaBancaria;
    }

    public String getBancoAgenciaConta() {
        return bancoAgenciaConta;
    }

    public void setBancoAgenciaConta(String bancoAgenciaConta) {
        this.bancoAgenciaConta = bancoAgenciaConta;
    }

    public String getBancoContaBancariaEntidade() {
        return bancoContaBancariaEntidade;
    }

    public void setBancoContaBancariaEntidade(String bancoContaBancariaEntidade) {
        this.bancoContaBancariaEntidade = bancoContaBancariaEntidade;
    }

    public List<NotaExecucaoOrcamentariaRetencao> getRetencoes() {
        return retencoes;
    }

    public void setRetencoes(List<NotaExecucaoOrcamentariaRetencao> retencoes) {
        this.retencoes = retencoes;
    }

    public List<NotaExecucaoOrcamentariaPagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<NotaExecucaoOrcamentariaPagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public List<NotaExecucaoOrcamentariaFatura> getFaturas() {
        return faturas;
    }

    public void setFaturas(List<NotaExecucaoOrcamentariaFatura> faturas) {
        this.faturas = faturas;
    }

    public List<NotaExecucaoOrcamentariaConvenio> getConvenios() {
        return convenios;
    }

    public void setConvenios(List<NotaExecucaoOrcamentariaConvenio> convenios) {
        this.convenios = convenios;
    }

    public List<NotaExecucaoOrcamentariaGps> getGps() {
        return gps;
    }

    public void setGps(List<NotaExecucaoOrcamentariaGps> gps) {
        this.gps = gps;
    }

    public List<NotaExecucaoOrcamentariaDarf> getDarfs() {
        return darfs;
    }

    public void setDarfs(List<NotaExecucaoOrcamentariaDarf> darfs) {
        this.darfs = darfs;
    }

    public List<NotaExecucaoOrcamentariaDarf> getDarfsSimples() {
        return darfsSimples;
    }

    public void setDarfsSimples(List<NotaExecucaoOrcamentariaDarf> darfsSimples) {
        this.darfsSimples = darfsSimples;
    }

    public String getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(String contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public String getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(String contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public Boolean getHasIdRetencaoPagamento() {
        return hasIdRetencaoPagamento;
    }

    public void setHasIdRetencaoPagamento(Boolean hasIdRetencaoPagamento) {
        this.hasIdRetencaoPagamento = hasIdRetencaoPagamento;
    }

    public List<NotaExecucaoOrcamentariaReceitaExtra> getReceitasExtras() {
        return receitasExtras;
    }

    public void setReceitasExtras(List<NotaExecucaoOrcamentariaReceitaExtra> receitasExtras) {
        this.receitasExtras = receitasExtras;
    }

    public String getNomeDaNota() {
        return nomeDaNota;
    }

    public void setNomeDaNota(String nomeDaNota) {
        this.nomeDaNota = nomeDaNota;
    }
}
