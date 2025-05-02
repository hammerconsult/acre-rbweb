package br.com.webpublico.negocios.rh.creditosalariobancos.febraban.lote;

import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation.Campo;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.CNAB240;

import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.CARACTERE;
import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.INTEIRO;

public abstract class AbstractHeaderLoteCNAB240 extends CNAB240 {

    //Código do Banco - 1 até 3
    @Campo(posicao = 1, tipo = INTEIRO, tamanho = 3)
    private String banco;
    //Código do Lote de Serviço - 4 a 7
    @Campo(posicao = 2, tipo = INTEIRO, tamanho = 4)
    private String lote;
    //tipo de registro - 8 a 8
    @Campo(posicao = 3, tipo = INTEIRO, tamanho = 1)
    private String registro;
    //tipo de operação - 9 a 9
    @Campo(posicao = 4, tipo = CARACTERE, tamanho = 1)
    private String operacao;
    //tipo do serviço - 10 a 11
    @Campo(posicao = 5, tipo = INTEIRO, tamanho = 2)
    private String servico;
    //forma de lançamento - 12 a 13
    @Campo(posicao = 6, tipo = INTEIRO, tamanho = 2)
    private String lancamento;
    //nº da versão do layout do lote - 14 a 16
    @Campo(posicao = 7, tipo = INTEIRO, tamanho = 3)
    private String versaoLayout;
    //uso exclusivo da FABRABAN/CNAB - 17 a 17
    @Campo(posicao = 8, tipo = CARACTERE, tamanho = 1)
    private String usoExclusivo;
    //tipo de inscrição da empresa - 18 a 18
    @Campo(posicao = 9, tipo = INTEIRO, tamanho = 1)
    private String tipoInscricao;
    //número de inscrição da empresa - 19 a 32
    @Campo(posicao = 10, tipo = INTEIRO, tamanho = 14)
    private String numeroEmpresa;
    //código do convênio no banco - 33 a 52
    @Campo(posicao = 11, tipo = CARACTERE, tamanho = 20)
    private String codigoBanco;
    //agência mantenedora da conta - 53 a 57
    @Campo(posicao = 12, tipo = INTEIRO, tamanho = 5)
    private String agenciaConta;
    //dígito verificador da agência - 58 a 58
    @Campo(posicao = 13, tipo = CARACTERE, tamanho = 1)
    private String digitoAgencia;
    //número da conta correntE - 59 a 70
    @Campo(posicao = 14, tipo = INTEIRO, tamanho = 12)
    private String numeroConta;
    //dígito verificador da conta - 71 a 71
    @Campo(posicao = 15, tipo = CARACTERE, tamanho = 1)
    private String digitoConta;
    //dígito verificador da Ag/Conta - 72 a 72
    @Campo(posicao = 16,tipo = CARACTERE,tamanho = 1)
    private String digitoAgConta;
    //mome da empresa - 73 a 102
    @Campo(posicao = 17,tipo = CARACTERE,tamanho = 30)
    private String nomeEmpresa;
    //mensagem - 103 a 142
    @Campo(posicao = 18,tipo = CARACTERE,tamanho = 40)
    private String mensagem;
    //nome da rua, av, pça, etc - 143 a 172
    @Campo(posicao = 19,tipo = CARACTERE,tamanho = 30)
    private String logradoudo;
    //número do local - 173 a 177
    @Campo(posicao = 20,tipo = INTEIRO,tamanho = 5)
    private String numeroLocal;
    //casa, apto, sala, etc - 178 a 192
    @Campo(posicao = 21,tipo = CARACTERE,tamanho = 15)
    private String complemento;
    //nome da cidade - 193 a 212
    @Campo(posicao = 22,tipo = CARACTERE,tamanho = 20)
    private String cidade;
    //cep - 213 a 217
    @Campo(posicao = 23,tipo = INTEIRO,tamanho = 5)
    private String cep;
    //complemento do cep - 218 a 220
    @Campo(posicao = 24,tipo = CARACTERE,tamanho = 3)
    private String complementoCep;
    //sigla do estado - 221 a 222
    @Campo(posicao = 25,tipo = CARACTERE,tamanho = 2)
    private String estado;
    //indicativo da forma de pagamento do serviço - 223 a 224
    @Campo(posicao = 26,tipo = INTEIRO,tamanho = 2)
    private String formaPagamento;
    //uso exclusido FABRABAN/CNAB - 225 a 230
    @Campo(posicao = 27,tipo = CARACTERE,tamanho = 6)
    private String usoExclusivo2;
    //códigos das ocorrências p/ retorno - 231 a 240
    @Campo(posicao = 28,tipo = CARACTERE,tamanho = 10)
    private String codigoOcorrencia;

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public String getLancamento() {
        return lancamento;
    }

    public void setLancamento(String lancamento) {
        this.lancamento = lancamento;
    }

    public String getVersaoLayout() {
        return versaoLayout;
    }

    public void setVersaoLayout(String versaoLayout) {
        this.versaoLayout = versaoLayout;
    }

    public String getUsoExclusivo() {
        return usoExclusivo;
    }

    public void setUsoExclusivo(String usoExclusivo) {
        this.usoExclusivo = usoExclusivo;
    }

    public String getTipoInscricao() {
        return tipoInscricao;
    }

    public void setTipoInscricao(String tipoInscricao) {
        this.tipoInscricao = tipoInscricao;
    }

    public String getNumeroEmpresa() {
        return numeroEmpresa;
    }

    public void setNumeroEmpresa(String numeroEmpresa) {
        this.numeroEmpresa = numeroEmpresa;
    }

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public String getAgenciaConta() {
        return agenciaConta;
    }

    public void setAgenciaConta(String agenciaConta) {
        this.agenciaConta = agenciaConta;
    }

    public String getDigitoAgencia() {
        return digitoAgencia;
    }

    public void setDigitoAgencia(String digitoAgencia) {
        this.digitoAgencia = digitoAgencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getDigitoConta() {
        return digitoConta;
    }

    public void setDigitoConta(String digitoConta) {
        this.digitoConta = digitoConta;
    }

    public String getDigitoAgConta() {
        return digitoAgConta;
    }

    public void setDigitoAgConta(String digitoAgConta) {
        this.digitoAgConta = digitoAgConta;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getLogradoudo() {
        return logradoudo;
    }

    public void setLogradoudo(String logradoudo) {
        this.logradoudo = logradoudo;
    }

    public String getNumeroLocal() {
        return numeroLocal;
    }

    public void setNumeroLocal(String numeroLocal) {
        this.numeroLocal = numeroLocal;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
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

    public String getComplementoCep() {
        return complementoCep;
    }

    public void setComplementoCep(String complementoCep) {
        this.complementoCep = complementoCep;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getUsoExclusivo2() {
        return usoExclusivo2;
    }

    public void setUsoExclusivo2(String usoExclusivo2) {
        this.usoExclusivo2 = usoExclusivo2;
    }

    public String getCodigoOcorrencia() {
        return codigoOcorrencia;
    }

    public void setCodigoOcorrencia(String codigoOcorrencia) {
        this.codigoOcorrencia = codigoOcorrencia;
    }
}
