package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.MovimentoContabil;

import java.util.List;

public class MovimentoContabilPorClasse {

    private String classe;
    private List<MovimentoContabil> movimentos;

    public MovimentoContabilPorClasse(String classe, List<MovimentoContabil> movimentos) {
        this.classe = classe;
        this.movimentos = movimentos;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public List<MovimentoContabil> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<MovimentoContabil> movimentos) {
        this.movimentos = movimentos;
    }
}
