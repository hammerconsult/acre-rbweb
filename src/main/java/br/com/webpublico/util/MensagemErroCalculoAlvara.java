package br.com.webpublico.util;

import br.com.webpublico.entidades.Calculo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanderleydc
 */
public class MensagemErroCalculoAlvara {

    private Calculo calculo;
    private List<String> mensagensErro = new ArrayList<String>();
    private List<String> mensagensAlerta = new ArrayList<String>();

    public List<String> getMensagensAlerta() {
        return mensagensAlerta;
    }

    public void setMensagensAlerta(List<String> mensagensAlerta) {
        this.mensagensAlerta = mensagensAlerta;
    }

    public List<String> getMensagensErro() {
        return mensagensErro;
    }

    public void setMensagensErro(List<String> mensagensErro) {
        this.mensagensErro = mensagensErro;
    }

    public void addMensagemErro(String mensagem) {
        if (this.mensagensErro == null) {
            this.mensagensErro = new ArrayList<String>();
        }
        this.mensagensErro.add(mensagem);
    }

    public void addMensagemAlerta(String mensagem) {
        if (this.mensagensAlerta == null) {
            this.mensagensAlerta = new ArrayList<String>();
        }
        this.mensagensAlerta.add(mensagem);
    }

    public Calculo getCalculo() {
        return calculo;
    }

    public void setCalculo(Calculo calculo) {
        this.calculo = calculo;
    }
}
