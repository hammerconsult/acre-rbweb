package br.com.webpublico.negocios.rh.creditosalariobancos.febraban.segmento;

import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation.Campo;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.CNAB240;

import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.CARACTERE;
import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.INTEIRO;


public abstract class AbstractSegmentoACNAB240 extends CNAB240 {


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
    //tipo de movimento - 15 a 15
    @Campo(posicao = 6, tipo = CARACTERE, tamanho = 1)
    private String tipoMovimento;
    //Código da instrução p/movimento - 16 a 17
    @Campo(posicao = 7, tipo = CARACTERE, tamanho = 2)
    private String codigoMovimento;
    //Código da câmara centralizadora - 18 a 20
    @Campo(posicao = 8, tipo = CARACTERE, tamanho = 3)
    private String camara;
    //código do banco do favorecido - 21 a 23
    @Campo(posicao = 9, tipo = CARACTERE, tamanho = 3)
    private String bancoFavorecido;
    //ag. mantenedora da cta do favor. - 24 a 28
    @Campo(posicao = 10, tipo = CARACTERE, tamanho = 5)
    private String codigoAgenciaFavorecido;
    //dígito verificador da agência - 29 a 29
    @Campo(posicao = 11, tipo = CARACTERE, tamanho = 1)
    private String digitoAgenciaFavorecido;
    //número da conta corrente - 30 a 41
    @Campo(posicao = 12, tipo = CARACTERE, tamanho = 12)
    private String numeroContaFavorecido;
    //dígito verificador da conta
    @Campo(posicao = 13, tipo = CARACTERE, tamanho = 1)
    private String digitoContaFavorecido;
    //dígito verificador da ag/conta - 43 a 43
    @Campo(posicao = 14, tipo = CARACTERE, tamanho = 1)
    private String digitoContaCorrente;
    //nome do favorecido - 44 a 73
    @Campo(posicao = 15, tipo = CARACTERE, tamanho = 30)
    private String nomeFavorecido;
    //nº do docum. atribuído p/ empresa - 74 a 93
    @Campo(posicao = 16, tipo = CARACTERE, tamanho = 20)
    private String numeroCredito;
    //data do pagamento - 94 a 101
    @Campo(posicao = 17, tipo = CARACTERE, tamanho = 8)
    private String dataPagamento;
    //tipo da moeda - 102 a 104
    @Campo(posicao = 18, tipo = CARACTERE, tamanho = 3)
    private String tipoMoeda;
    //quantidade da moeda - 105 a 119
    @Campo(posicao = 19, tipo = CARACTERE, tamanho = 15)
    private String quantidadeMoeda;
    //valor do pagamento - 120 a 134
    @Campo(posicao = 20, tipo = CARACTERE, tamanho = 15)
    private String valorPagamento;
    //n° do docum. atribuído pelo banco - 135 a 154
    @Campo(posicao = 21, tipo = CARACTERE, tamanho = 20)
    private String nossoNumeroCredito;
    //data real da efetivação pagto - 155 a 162
    @Campo(posicao = 22, tipo = CARACTERE, tamanho = 8)
    private String dataRealCredito;
    //valor real da efativação do pagto - 163 a 177
    @Campo(posicao = 23, tipo = CARACTERE, tamanho = 15)
    private String valorRealCredito;
    //outras informações – vide formatação em g031 para identificação de deposito judicial e pgto.salários de servidores pelo SIAPE - 178 a 217
    @Campo(posicao = 24, tipo = CARACTERE, tamanho = 40)
    private String informacao;
    //compl. tipo serviço - 218 a 219
    @Campo(posicao = 25, tipo = CARACTERE, tamanho = 2)
    private String codigoFinalidadeDoc;
    //codigo finalidade da TED - 220 a 224
    @Campo(posicao = 26, tipo = CARACTERE, tamanho = 5)
    private String codigoFinalidadeTED;
    //complemento de finalidade pagto. - 225 a 226
    @Campo(posicao = 27, tipo = CARACTERE, tamanho = 2)
    private String codigoFinalidadeComplementar;
    //uso exclusivo FABRABAN/CNAB - 227 a 229
    @Campo(posicao = 28, tipo = CARACTERE, tamanho = 3)
    private String usoExclusivo;
    //aviso do favorecido - 230 a 230
    @Campo(posicao = 29, tipo = CARACTERE, tamanho = 1)
    private String aviso;
    //código das ocorrências p/ retorno - 231 a 240
    @Campo(posicao = 30, tipo = CARACTERE, tamanho = 10)
    private String ocorrencias;

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

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getCodigoMovimento() {
        return codigoMovimento;
    }

    public void setCodigoMovimento(String codigoMovimento) {
        this.codigoMovimento = codigoMovimento;
    }

    public String getCamara() {
        return camara;
    }

    public void setCamara(String camara) {
        this.camara = camara;
    }

    public String getBancoFavorecido() {
        return bancoFavorecido;
    }

    public void setBancoFavorecido(String bancoFavorecido) {
        this.bancoFavorecido = bancoFavorecido;
    }

    public String getCodigoAgenciaFavorecido() {
        return codigoAgenciaFavorecido;
    }

    public void setCodigoAgenciaFavorecido(String codigoAgenciaFavorecido) {
        this.codigoAgenciaFavorecido = codigoAgenciaFavorecido;
    }

    public String getDigitoAgenciaFavorecido() {
        return digitoAgenciaFavorecido;
    }

    public void setDigitoAgenciaFavorecido(String digitoAgenciaFavorecido) {
        this.digitoAgenciaFavorecido = digitoAgenciaFavorecido;
    }

    public String getNumeroContaFavorecido() {
        return numeroContaFavorecido;
    }

    public void setNumeroContaFavorecido(String numeroContaFavorecido) {
        this.numeroContaFavorecido = numeroContaFavorecido;
    }

    public String getDigitoContaFavorecido() {
        return digitoContaFavorecido;
    }

    public void setDigitoContaFavorecido(String digitoContaFavorecido) {
        this.digitoContaFavorecido = digitoContaFavorecido;
    }

    public String getDigitoContaCorrente() {
        return digitoContaCorrente;
    }

    public void setDigitoContaCorrente(String digitoContaCorrente) {
        this.digitoContaCorrente = digitoContaCorrente;
    }

    public String getNomeFavorecido() {
        return nomeFavorecido;
    }

    public void setNomeFavorecido(String nomeFavorecido) {
        this.nomeFavorecido = nomeFavorecido;
    }

    public String getNumeroCredito() {
        return numeroCredito;
    }

    public void setNumeroCredito(String numeroCredito) {
        this.numeroCredito = numeroCredito;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getTipoMoeda() {
        return tipoMoeda;
    }

    public void setTipoMoeda(String tipoMoeda) {
        this.tipoMoeda = tipoMoeda;
    }

    public String getQuantidadeMoeda() {
        return quantidadeMoeda;
    }

    public void setQuantidadeMoeda(String quantidadeMoeda) {
        this.quantidadeMoeda = quantidadeMoeda;
    }

    public String getValorPagamento() {
        return valorPagamento;
    }

    public void setValorPagamento(String valorPagamento) {
        this.valorPagamento = valorPagamento;
    }

    public String getNossoNumeroCredito() {
        return nossoNumeroCredito;
    }

    public void setNossoNumeroCredito(String nossoNumeroCredito) {
        this.nossoNumeroCredito = nossoNumeroCredito;
    }

    public String getDataRealCredito() {
        return dataRealCredito;
    }

    public void setDataRealCredito(String dataRealCredito) {
        this.dataRealCredito = dataRealCredito;
    }

    public String getValorRealCredito() {
        return valorRealCredito;
    }

    public void setValorRealCredito(String valorRealCredito) {
        this.valorRealCredito = valorRealCredito;
    }

    public String getInformacao() {
        return informacao;
    }

    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    public String getCodigoFinalidadeDoc() {
        return codigoFinalidadeDoc;
    }

    public void setCodigoFinalidadeDoc(String codigoFinalidadeDoc) {
        this.codigoFinalidadeDoc = codigoFinalidadeDoc;
    }

    public String getCodigoFinalidadeTED() {
        return codigoFinalidadeTED;
    }

    public void setCodigoFinalidadeTED(String codigoFinalidadeTED) {
        this.codigoFinalidadeTED = codigoFinalidadeTED;
    }

    public String getCodigoFinalidadeComplementar() {
        return codigoFinalidadeComplementar;
    }

    public void setCodigoFinalidadeComplementar(String codigoFinalidadeComplementar) {
        this.codigoFinalidadeComplementar = codigoFinalidadeComplementar;
    }

    public String getUsoExclusivo() {
        return usoExclusivo;
    }

    public void setUsoExclusivo(String usoExclusivo) {
        this.usoExclusivo = usoExclusivo;
    }

    public String getAviso() {
        return aviso;
    }

    public void setAviso(String aviso) {
        this.aviso = aviso;
    }

    public String getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(String ocorrencias) {
        this.ocorrencias = ocorrencias;
    }
}
