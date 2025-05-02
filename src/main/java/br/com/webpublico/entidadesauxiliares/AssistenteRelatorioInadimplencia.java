package br.com.webpublico.entidadesauxiliares;

import java.util.List;

/**
 * Created by HardRock on 12/05/2017.
 */
public class AssistenteRelatorioInadimplencia {

    private List<Inadimplencia> resultado;

    public AssistenteRelatorioInadimplencia() {
    }

    public AssistenteRelatorioInadimplencia(List<Inadimplencia> resultado) {
        this.resultado = resultado;
    }

    public List<Inadimplencia> getResultado() {
        return resultado;
    }

    public void setResultado(List<Inadimplencia> resultado) {
        this.resultado = resultado;
    }
}
