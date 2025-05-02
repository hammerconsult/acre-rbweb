/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn350;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FormataValoresUtil;
import java.io.Serializable;


/**
 *
 * @author reidocrime
 */
public class TrailerObn350 implements Serializable{

    private String linha;
    private String noves;
    private String brancos;
    private String somatorioTodasOBs;
    private String somatoriaSequenciaTodasOBs;

    public TrailerObn350() {
        throw new ExcecaoNegocioGenerica("Construtor não suportado, utilize o construtor informando uma linha \"Texto\" como paramentro!");
    }

    public TrailerObn350(String linha) {
        this.linha = linha;
        validaLinha(this.linha);
    }

    private void validaLinha(String linha) {
        if (linha == null) {
            throw new ExcecaoNegocioGenerica("O valor da linha para o trailer esta nula!");
        }
        if (linha.equals("")) {
            throw new ExcecaoNegocioGenerica("A linha para o trailer esta em Branco!");
        }
        if (linha.length() != 350) {
            throw new ExcecaoNegocioGenerica(" A linhas não atende o formato exigido pelo trailer, deveriater 350 char e contem " + linha.length() + "!");
        }


    }

    public String getLinha() {
        return linha;
    }

    public String getNoves() {
        this.noves = linha.substring(0, 35);
        return noves;
    }

    public String getBrancos() {
        this.brancos = linha.substring(35, 320);
        return brancos;
    }

    public String getSomatorioTodasOBs() {
        this.somatorioTodasOBs = linha.substring(320, 337);
        return somatorioTodasOBs;
    }

    public String getSomatoriaSequenciaTodasOBs() {
        this.somatoriaSequenciaTodasOBs = linha.substring(337, 350);
        return somatoriaSequenciaTodasOBs;
    }

    public String getSomatorioTodasOBsConvertida() {

        this.somatorioTodasOBs = linha.substring(320, 337);
        return FormataValoresUtil.valorConvertidoR$(somatorioTodasOBs);
    }

    public int getSomatoriaSequenciaTodasOBsConvertido() {
        this.somatoriaSequenciaTodasOBs = linha.substring(337, 350);
        return new Integer(somatoriaSequenciaTodasOBs);
    }
}
