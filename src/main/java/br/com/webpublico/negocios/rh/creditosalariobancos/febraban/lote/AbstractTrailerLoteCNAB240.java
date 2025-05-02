package br.com.webpublico.negocios.rh.creditosalariobancos.febraban.lote;

import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation.Campo;
import br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo.CNAB240;

import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.CARACTERE;
import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.INTEIRO;


public abstract class AbstractTrailerLoteCNAB240 extends CNAB240 {

    //Código do Banco - 1 até 3
    @Campo(posicao = 1, tipo = CARACTERE, tamanho = 3)
    private String banco;
    //Código do Lote de Serviço - 4 a 7
    @Campo(posicao = 2, tipo = CARACTERE, tamanho = 4)
    private String lote;
    //tipo de registro - 8 a 8
    @Campo(posicao = 3, tipo = CARACTERE, tamanho = 1)
    private String registro;
    //uso exclusivo FEBRABAN/CNAB - 9 a 17
    @Campo(posicao = 4, tipo = CARACTERE, tamanho = 9)
    private String usoExclusivo;
    //quantidade de registros do lote - 18 a 23
    @Campo(posicao = 5, tipo = CARACTERE, tamanho = 6)
    private String quantidadeLote;
    //somatória dos valores - 24 a 41
    @Campo(posicao = 6, tipo = CARACTERE, tamanho = 18)
    private String somatoriaValores;
    //somatória de quantidade de moedas - 42 a 59
    @Campo(posicao = 7, tipo = CARACTERE, tamanho = 18)
    private String somatoriaQuantidade;
    //número aviso de débito - 60 a 65
    @Campo(posicao = 8, tipo = CARACTERE, tamanho = 6)
    private String numeroAvisoDebito;
    //uso exclusivo FEBRABAN/CNAB - 66 a 230
    @Campo(posicao = 9, tipo = CARACTERE, tamanho = 165)
    private String usoExclusivo2;
    //códigos das ocorrências para retorno - 231 a 240
    @Campo(posicao = 10, tipo = CARACTERE, tamanho = 10)
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

    public String getUsoExclusivo() {
        return usoExclusivo;
    }

    public void setUsoExclusivo(String usoExclusivo) {
        this.usoExclusivo = usoExclusivo;
    }

    public String getQuantidadeLote() {
        return quantidadeLote;
    }

    public void setQuantidadeLote(String quantidadeLote) {
        this.quantidadeLote = quantidadeLote;
    }

    public String getSomatoriaValores() {
        return somatoriaValores;
    }

    public void setSomatoriaValores(String somatoriaValores) {
        this.somatoriaValores = somatoriaValores;
    }

    public String getSomatoriaQuantidade() {
        return somatoriaQuantidade;
    }

    public void setSomatoriaQuantidade(String somatoriaQuantidade) {
        this.somatoriaQuantidade = somatoriaQuantidade;
    }

    public String getNumeroAvisoDebito() {
        return numeroAvisoDebito;
    }

    public void setNumeroAvisoDebito(String numeroAvisoDebito) {
        this.numeroAvisoDebito = numeroAvisoDebito;
    }

    public String getUsoExclusivo2() {
        return usoExclusivo2;
    }

    public void setUsoExclusivo2(String usoExclusivo2) {
        this.usoExclusivo2 = usoExclusivo2;
    }

    public String getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(String ocorrencias) {
        this.ocorrencias = ocorrencias;
    }
}
