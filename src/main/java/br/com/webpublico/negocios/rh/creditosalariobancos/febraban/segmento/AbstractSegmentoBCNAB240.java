package br.com.webpublico.negocios.rh.creditosalariobancos.febraban.segmento;

import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation.Campo;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.CNAB240;

import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.CARACTERE;
import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.INTEIRO;

public abstract class AbstractSegmentoBCNAB240 extends CNAB240 {

    //Código do Banco - 1 até 3
    @Campo(posicao = 1, tipo = CARACTERE, tamanho = 3)
    private String banco;
    //Código do Lote de Serviço - 4 a 7
    @Campo(posicao = 2, tipo = CARACTERE, tamanho = 4)
    private String lote;
    //tipo de registro - 8 a 8
    @Campo(posicao = 3, tipo = CARACTERE, tamanho = 1)
    private String registro;
    //nº sequencial do registro do lote - 9 a 13
    @Campo(posicao = 4, tipo = CARACTERE, tamanho = 5)
    private String numeroRegistro;
    //código de segmento do reg. detalhe - 14 a 14
    @Campo(posicao = 5, tipo = CARACTERE, tamanho = 1)
    private String segmento;
    //uso exclusivo febraban/cnab - 15 a 17
    @Campo(posicao = 6, tipo = CARACTERE, tamanho = 3)
    private String usoExclusivo;
    //tipo de inscrição do favorecido - 18 a 18
    @Campo(posicao = 7, tipo = CARACTERE, tamanho = 1)
    private String tipoInscricao;
    //nº de inscrição do favorecido - 19 a 32
    @Campo(posicao = 8, tipo = CARACTERE, tamanho = 14)
    private String numeroInscricao;
    //nome da rua, av, pça, etc - 33 a 62
    @Campo(posicao = 9, tipo = CARACTERE, tamanho = 30)
    private String logradourro;
    //nº do local - 63 a 67
    @Campo(posicao = 10, tipo = CARACTERE, tamanho = 5)
    private String numeroFavorecido;
    //casa, apto, etc - 68 a 82
    @Campo(posicao = 11, tipo = CARACTERE, tamanho = 15)
    private String complemento;
    //bairro do favorecido - 83 a 97
    @Campo(posicao = 12, tipo = CARACTERE, tamanho = 15)
    private String bairro;
    //nome da cidade - 98 a 117
    @Campo(posicao = 13, tipo = CARACTERE, tamanho = 20)
    private String cidade;
    //cep - 118 a 122
    @Campo(posicao = 14, tipo = CARACTERE, tamanho = 5)
    private String cep;
    //complemento cep - 123 a 125
    @Campo(posicao = 15, tipo = CARACTERE, tamanho = 3)
    private String complementoCep;
    //sigla do estado - 126 a 127
    @Campo(posicao = 16, tipo = CARACTERE, tamanho = 2)
    private String siglaEstado;
    //data de vencimento (nominal) - 128 a 135
    @Campo(posicao = 17, tipo = CARACTERE, tamanho = 8)
    private String dataVencimento;
    //valor do documento(nominal) - 136 a 150
    @Campo(posicao = 18, tipo = CARACTERE, tamanho = 15)
    private String valorDocumento;
    //valor do atributo - 151 a 165
    @Campo(posicao = 19, tipo = CARACTERE, tamanho = 15)
    private String valorAbatimento;
    //valor do desconto - 166 a 180
    @Campo(posicao = 20, tipo = CARACTERE, tamanho = 15)
    private String valorDesconto;
    //valor da mora - 181 a 195
    @Campo(posicao = 21, tipo = CARACTERE, tamanho = 15)
    private String valorMora;
    //valor da multa - 196 a 210
    @Campo(posicao = 22, tipo = CARACTERE, tamanho = 15)
    private String valorMulta;
    //código/documento do favorecido - 221 a 225
    @Campo(posicao = 23, tipo = CARACTERE, tamanho = 15)
    private String codigoDocumentoFavorecido;
    //aviso ao favorecido - 196 a 210
    @Campo(posicao = 24, tipo = CARACTERE, tamanho = 1)
    private String avisoFavorecido;
    //uso exclusivo para siape - 227 a 232
    @Campo(posicao = 25, tipo = CARACTERE, tamanho = 6)
    private String codigoUnidadeCentralizado;
    //código ISPB - 133 a 140
    @Campo(posicao = 26, tipo = CARACTERE, tamanho = 8)
    private String codigoISPB;

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

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
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

    public String getNumeroInscricao() {
        return numeroInscricao;
    }

    public void setNumeroInscricao(String numeroInscricao) {
        this.numeroInscricao = numeroInscricao;
    }

    public String getLogradourro() {
        return logradourro;
    }

    public void setLogradourro(String logradourro) {
        this.logradourro = logradourro;
    }

    public String getNumeroFavorecido() {
        return numeroFavorecido;
    }

    public void setNumeroFavorecido(String numeroFavorecido) {
        this.numeroFavorecido = numeroFavorecido;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
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

    public String getComplementoCep() {
        return complementoCep;
    }

    public void setComplementoCep(String complementoCep) {
        this.complementoCep = complementoCep;
    }

    public String getSiglaEstado() {
        return siglaEstado;
    }

    public void setSiglaEstado(String siglaEstado) {
        this.siglaEstado = siglaEstado;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getValorDocumento() {
        return valorDocumento;
    }

    public void setValorDocumento(String valorDocumento) {
        this.valorDocumento = valorDocumento;
    }

    public String getValorAbatimento() {
        return valorAbatimento;
    }

    public void setValorAbatimento(String valorAbatimento) {
        this.valorAbatimento = valorAbatimento;
    }

    public String getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(String valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public String getValorMora() {
        return valorMora;
    }

    public void setValorMora(String valorMora) {
        this.valorMora = valorMora;
    }

    public String getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(String valorMulta) {
        this.valorMulta = valorMulta;
    }

    public String getCodigoDocumentoFavorecido() {
        return codigoDocumentoFavorecido;
    }

    public void setCodigoDocumentoFavorecido(String codigoDocumentoFavorecido) {
        this.codigoDocumentoFavorecido = codigoDocumentoFavorecido;
    }

    public String getAvisoFavorecido() {
        return avisoFavorecido;
    }

    public void setAvisoFavorecido(String avisoFavorecido) {
        this.avisoFavorecido = avisoFavorecido;
    }

    public String getCodigoUnidadeCentralizado() {
        return codigoUnidadeCentralizado;
    }

    public void setCodigoUnidadeCentralizado(String codigoUnidadeCentralizado) {
        this.codigoUnidadeCentralizado = codigoUnidadeCentralizado;
    }

    public String getCodigoISPB() {
        return codigoISPB;
    }

    public void setCodigoISPB(String codigoISPB) {
        this.codigoISPB = codigoISPB;
    }
}
