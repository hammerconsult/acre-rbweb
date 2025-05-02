package br.com.webpublico.entidadesauxiliares;

import java.util.List;

public class AssistenteRelatorioMaioresDevedores {

    private List<MaioresDevedores> resultado;

    public AssistenteRelatorioMaioresDevedores() {
    }

    public AssistenteRelatorioMaioresDevedores(List<MaioresDevedores> resultado) {
        this.resultado = resultado;
    }

    public List<MaioresDevedores> getResultado() {
        return resultado;
    }

    public void setResultado(List<MaioresDevedores> resultado) {
        this.resultado = resultado;
    }
}
