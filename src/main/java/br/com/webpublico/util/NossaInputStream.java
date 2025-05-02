/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoParte;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author terminal4
 */
public class NossaInputStream extends InputStream {

    private Arquivo arquivo;

    private int parteCorrente;
    private int byteCorrente;
    private long posicao;


    public NossaInputStream(Arquivo arquivo) {
        this.arquivo = arquivo;
        parteCorrente = 0;
        byteCorrente = 0;
        posicao = 0;
    }

    @Override
    public int read() throws IOException {
        ArquivoParte arquivoParte = arquivo.getPartes().get(parteCorrente);
        if (byteCorrente < arquivoParte.getDados().length) {
            posicao++;
            return arquivoParte.getDados()[byteCorrente++];
        } else {
            parteCorrente++; //Vai para próxima parte
            byteCorrente = 0;
            if (parteCorrente < arquivo.getPartes().size()) {
                return read();
            } else {
                return -1;
            }
        }
    }

    @Override
    public int available() throws IOException {
        return (int) (arquivo.getTamanho() - posicao);
    }


}
