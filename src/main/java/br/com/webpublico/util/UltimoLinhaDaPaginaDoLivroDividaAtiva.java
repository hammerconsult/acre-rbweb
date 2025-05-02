/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

/**
 * @author gustavo
 */
public class UltimoLinhaDaPaginaDoLivroDividaAtiva {

    private Long linha;
    private Long sequencia;
    private Long pagina;
    private Long numeroLivro;

    public Long getLinha() {
        return linha;
    }

    public void setLinha(Long linha) {
        this.linha = linha;
    }

    public Long getPagina() {
        return pagina;
    }

    public void setPagina(Long pagina) {
        this.pagina = pagina;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public UltimoLinhaDaPaginaDoLivroDividaAtiva() {
    }

    public UltimoLinhaDaPaginaDoLivroDividaAtiva(Long linha, Long sequencia, Long pagina) {
        this.linha = linha;
        this.sequencia = sequencia;
        this.pagina = pagina;
    }

    public UltimoLinhaDaPaginaDoLivroDividaAtiva(Long linha, Long sequencia, Long pagina, Long numeroLivro) {
        this.linha = linha;
        this.sequencia = sequencia;
        this.pagina = pagina;
        this.numeroLivro = numeroLivro;
    }

    public Long getNumeroLivro() {
        return numeroLivro;
    }

    public void setNumeroLivro(Long numeroLivro) {
        this.numeroLivro = numeroLivro;
    }
}
