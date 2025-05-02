package br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo;

import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation.Campo;
import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo;
import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.CARACTERE;
import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.INTEIRO;


public abstract class AbstractTrailerCNAB240 extends CNAB240 {
    //Código do Banco - 1 até 3
    @Campo(posicao = 1, tipo = INTEIRO, tamanho = 3)
    private String banco;
    //Código do Lote de Serviço - 4 a 7
    @Campo(posicao = 2, tipo = INTEIRO, tamanho = 4)
    private String lote;
    //tipo de registro - 8 a 8
    @Campo(posicao = 3, tipo = INTEIRO, tamanho = 1)
    private String registro;

    // Cnab febraban - uso esclusivo - 9 a 17
    @Campo(posicao = 4, tipo = CARACTERE, tamanho = 9)
    private String usoExlcusivo;
    @Campo(posicao = 5,tipo = TipoCampo.CARACTERE,tamanho = 6)
    private String quantidadeLotes;
    @Campo(posicao = 6,tipo = TipoCampo.CARACTERE,tamanho = 6)
    private String quantidadeRegistros;
    @Campo(posicao = 7,tipo = TipoCampo.CARACTERE,tamanho = 6)
    private String quantidadeContas;

    @Campo(posicao = 8,tipo = TipoCampo.CARACTERE,tamanho = 205)
    private String usoExlcusivo2;

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

    public String getUsoExlcusivo() {
        return usoExlcusivo;
    }

    public void setUsoExlcusivo(String usoExlcusivo) {
        this.usoExlcusivo = usoExlcusivo;
    }

    public String getQuantidadeLotes() {
        return quantidadeLotes;
    }

    public void setQuantidadeLotes(String quantidadeLotes) {
        this.quantidadeLotes = quantidadeLotes;
    }

    public String getQuantidadeRegistros() {
        return quantidadeRegistros;
    }

    public void setQuantidadeRegistros(String quantidadeRegistros) {
        this.quantidadeRegistros = quantidadeRegistros;
    }

    public String getQuantidadeContas() {
        return quantidadeContas;
    }

    public void setQuantidadeContas(String quantidadeContas) {
        this.quantidadeContas = quantidadeContas;
    }

    public String getUsoExlcusivo2() {
        return usoExlcusivo2;
    }

    public void setUsoExlcusivo2(String usoExlcusivo2) {
        this.usoExlcusivo2 = usoExlcusivo2;
    }
}
