/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Adriano Jandre
 */
@ManagedBean
@ViewScoped
public class EstornoLiquidacaoPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "liquidacaoEstornoControlador", value = "#{liquidacaoEstornoControlador}")
    private LiquidacaoEstornoControlador liquidacaoEstornoControlador;

    @Override
    public String getComplementoQuery() {
        if (liquidacaoEstornoControlador.isLiquidacaoEstornoCategoriaNormal()) {
            return " INNER JOIN obj.empenho e "
                    + " where obj.saldo > 0 "
                    + " and obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.NORMAL + "' "
                    + " and to_char(e.dataEmpenho, 'yyyy') = " + sistemaControlador.getExercicioCorrenteAsInteger() + " "
                    + " and obj.unidadeOrganizacional.id = " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId() + " "
                    + " and " + montaCondicao() + montaOrdenacao();
        } else {
            return " INNER JOIN obj.empenho e "
                    + " where obj.saldo > 0 "
                    + " and obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.RESTO + "' "
                    + " and to_char(e.dataEmpenho, 'yyyy') = " + sistemaControlador.getExercicioCorrenteAsInteger() + " "
                    + " and obj.unidadeOrganizacional.id = " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId() + " "
                    + " and " + montaCondicao() + montaOrdenacao();
        }
    }

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("obj.status", "status", StatusPagamento.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.status", "status", StatusPagamento.class));
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public LiquidacaoEstornoControlador getLiquidacaoEstornoControlador() {
        return liquidacaoEstornoControlador;
    }

    public void setLiquidacaoEstornoControlador(LiquidacaoEstornoControlador liquidacaoEstornoControlador) {
        this.liquidacaoEstornoControlador = liquidacaoEstornoControlador;
    }
}
