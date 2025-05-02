package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EventoCalculo;
import br.com.webpublico.entidades.Pontuacao;

import java.io.Serializable;
import java.util.List;

public class GrupoPontuacao implements Serializable {

    List<Pontuacao> pontos;

    public List<Pontuacao> getPontos() {
        return pontos;
    }

    public void setPontos(List<Pontuacao> pontos) {
        this.pontos = pontos;
    }
}
