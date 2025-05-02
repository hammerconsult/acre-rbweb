/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn350;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;


/**
 * @author reidocrime
 */
public class LayoutObn350 implements Serializable {

    private HeaderObn350 headerObn350;
    private TrailerObn350 trailerObn350;
    private List<RegistroObn350TipoDois> registroObn350TipoDois;
    private List<RegistroObn350TipoTres> registrosObn350TipoTres;
    private List<RegistroObn350TipoQuatro> registroObn350TipoQuatro;
    private List<RegistroObn350TipoCinco> registroObn350TipoCinco;
    private boolean executaLeitura;
    private BufferedReader bufferedReader;
    private int ano;

    public LayoutObn350() {
        throw new ExcecaoNegocioGenerica("Construtor não suportado, utilize o construtor informando uma linha \"Texto\" como paramentro!");
    }

    public LayoutObn350(BufferedReader bufferedReader, int ano) {
        validaBufferReader(bufferedReader);
        this.bufferedReader = bufferedReader;
        executaLeitura = true;
        registroObn350TipoDois = Lists.newArrayList();
        registrosObn350TipoTres = Lists.newArrayList();
        registroObn350TipoQuatro = Lists.newArrayList();
        registroObn350TipoCinco = Lists.newArrayList();
        this.ano = ano;
    }

    private void validaBufferReader(BufferedReader bufferedReader) {
        if (bufferedReader == null) {
            throw new ExcecaoNegocioGenerica("O arquivo não gerou o BufferedReader!");
        }
    }

    private void validaLinha(String linha) {
        if (linha == null) {
            throw new ExcecaoNegocioGenerica("O valor da linha para o header esta nula!");
        }
        if (linha.equals("")) {
            throw new ExcecaoNegocioGenerica("A linha para o header esta em Branco!");
        }
        if (linha.length() != 350) {
            throw new ExcecaoNegocioGenerica(" A linhas não atende o formato exigido pelo header, deveria ter 350 char e contém " + linha.length() + "!");
        }
    }

    private void executaLeituraDoBuffer() {
        if (executaLeitura) {
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    validaLinha(line);
                    triagemPorTipoDeRegistro(line);
                }
                atualizarTiposTresQuatroECincoDoTipoDois();
            } catch (IOException ex) {
                throw new ExcecaoNegocioGenerica("A Leitura do Buffer apresentou um problema! " + ex.getMessage());
            }
            executaLeitura = false;
        }
    }

    private void atualizarTiposTresQuatroECincoDoTipoDois() {
        for (RegistroObn350TipoDois obn350TipoDois : registroObn350TipoDois) {
            String numeroMovimento = obn350TipoDois.getNumeroMovimento();

            for (RegistroObn350TipoTres tipotres : registrosObn350TipoTres) {
                if (numeroMovimento.equals(tipotres.getNumeroMovimento())) {
                    obn350TipoDois.getRegistrosObn350TipoTres().add(tipotres);
                }
            }

            for (RegistroObn350TipoQuatro tipoQuatro : registroObn350TipoQuatro) {
                if (numeroMovimento.equals(tipoQuatro.getNumeroMovimento())) {
                    obn350TipoDois.getRegistroObn350TipoQuatros().add(tipoQuatro);
                }
            }

            for (RegistroObn350TipoCinco tipoCinco : registroObn350TipoCinco) {
                if (numeroMovimento.equals(tipoCinco.getNumeroMovimento())) {
                    obn350TipoDois.getRegistroObn350TipoCincos().add(tipoCinco);
                }
            }
        }
    }

    private void triagemPorTipoDeRegistro(String linha) {
        if (linha.startsWith("0")) {
            headerObn350 = new HeaderObn350(linha);
        } else if (linha.startsWith("2")) {
            this.registroObn350TipoDois.add(new RegistroObn350TipoDois(linha, headerObn350));
        } else if (linha.startsWith("3")) {
            registrosObn350TipoTres.add(new RegistroObn350TipoTres(linha));
        } else if (linha.startsWith("4")) {
            this.registroObn350TipoQuatro.add(new RegistroObn350TipoQuatro(linha, headerObn350));
        } else if (linha.startsWith("5")) {
            this.registroObn350TipoCinco.add(new RegistroObn350TipoCinco(linha, headerObn350));
        } else if (linha.startsWith("9")) {
            trailerObn350 = new TrailerObn350(linha);
        } else {
            throw new ExcecaoNegocioGenerica("O tipo da linha não pertence a nenhum dos tipos previsto para o Layout OBN350");
        }
    }

    public HeaderObn350 getHeaderObn350() {
        try {
            executaLeituraDoBuffer();
            return headerObn350;
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível ler o Header do arquivo. Detalhes do erro: ", e.getMessage());
        }
        return null;
    }

    public TrailerObn350 getTrailerObn350() {
        try {
            executaLeituraDoBuffer();
            return trailerObn350;
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível ler o Trailer do arquivo. Detalhes do erro: ", e.getMessage());
        }
        return null;
    }

    public List<RegistroObn350TipoDois> getRegistroObn350TipoDois() {
        try {
            executaLeituraDoBuffer();
            return registroObn350TipoDois;
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível ler o arquivo tipo 2. Detalhes do erro: ", e.getMessage());
        }
        return null;
    }

    public List<RegistroObn350TipoTres> getRegistrosObn350TipoTres() {
        try {
            executaLeituraDoBuffer();
            return registrosObn350TipoTres;
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível ler o arquivo tipo 3. Detalhes do erro: ", e.getMessage());
        }
        return null;
    }

    public List<RegistroObn350TipoQuatro> getRegistroObn350TipoQuatro() {
        try {
            executaLeituraDoBuffer();
            return registroObn350TipoQuatro;
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível ler o arquivo tipo 4. Detalhes do erro: ", e.getMessage());
        }
        return null;
    }

    public List<RegistroObn350TipoCinco> getRegistroObn350TipoCinco() {
        try {
            executaLeituraDoBuffer();
            return registroObn350TipoCinco;
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível ler o arquivo tipo 5. Detalhes do erro: ", e.getMessage());
        }
        return null;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public void setRegistrosObn350TipoTres(List<RegistroObn350TipoTres> registrosObn350TipoTres) {
        this.registrosObn350TipoTres = registrosObn350TipoTres;
    }

    public void setRegistroObn350TipoQuatro(List<RegistroObn350TipoQuatro> registroObn350TipoQuatro) {
        this.registroObn350TipoQuatro = registroObn350TipoQuatro;
    }

    public void setRegistroObn350TipoDois(List<RegistroObn350TipoDois> registroObn350TipoDois) {
        this.registroObn350TipoDois = registroObn350TipoDois;
    }

    public void setRegistroObn350TipoCinco(List<RegistroObn350TipoCinco> registroObn350TipoCinco) {
        this.registroObn350TipoCinco = registroObn350TipoCinco;
    }
}
