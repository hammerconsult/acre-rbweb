package br.com.webpublico.entidadesauxiliares;

import java.util.List;

/**
 * Created by andre on 17/11/15.
 */
public class ObjetoCampoValor implements Comparable<ObjetoCampoValor>{
    private String campo;
    private String valor;
    private List<String> valores;
    private Long criadoEm;

    public ObjetoCampoValor() {
        criadoEm = System.nanoTime();
    }

    public ObjetoCampoValor(String campo, String valor) {
        this.campo = campo;
        this.valor = valor;
        criadoEm = System.nanoTime();
    }
    public ObjetoCampoValor(String campo, List<String> valores) {
        this.campo = campo;
        this.valores = valores;
        criadoEm = System.nanoTime();
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public List<String> getValores() {
        return valores;
    }

    public void setValores(List<String> valores) {
        this.valores = valores;
    }

    @Override
    public int compareTo(ObjetoCampoValor o) {
        return this.criadoEm.compareTo(o.criadoEm);
    }
}
