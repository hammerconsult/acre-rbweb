package br.com.webpublico.entidadesauxiliares;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 02/09/13
 * Time: 09:25
 * To change this template use File | Settings | File Templates.
 */
public class RelatorioAnexo3ReceitaItem {
    private String descricao;
    private List<RelatorioAnexo3ReceitaItemExercicio> itens;
    private Integer nivel;

    public RelatorioAnexo3ReceitaItem() {
        itens = new ArrayList<RelatorioAnexo3ReceitaItemExercicio>();
    }

    public RelatorioAnexo3ReceitaItem(String descricao, List<RelatorioAnexo3ReceitaItemExercicio> itens) {
        this.descricao = descricao;
        this.itens = itens;
    }

    public List<RelatorioAnexo3ReceitaItemExercicio> getItens() {
        return itens;
    }

    public void setItens(List<RelatorioAnexo3ReceitaItemExercicio> itens) {
        this.itens = itens;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }
}
