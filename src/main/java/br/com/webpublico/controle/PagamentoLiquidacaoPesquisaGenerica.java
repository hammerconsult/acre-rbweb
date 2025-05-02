/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

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
public class PagamentoLiquidacaoPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "pagamentoControlador", value = "#{pagamentoControlador}")
    private PagamentoControlador pagamentoControlador;

    @Override
    public String getComplementoQuery() {
        if (pagamentoControlador.getCategoriaOrcamentariaPagamento()) {
            return " inner join obj.empenho e "
                    + " where obj.saldo > 0 "
                    + " and obj.categoriaOrcamentaria = 'NORMAL'"
                    + " and to_char(e.dataEmpenho,'yyyy') = " + sistemaControlador.getExercicioCorrente().getAno()
                    + " and obj.unidadeOrganizacional.id = " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId()
                    + " and " + montaCondicao() + montaOrdenacao();
        } else {
            return " inner join obj.empenho e "
                    + " where obj.saldo > 0 "
                    + " and obj.categoriaOrcamentaria = 'RESTO'"
                    + " and to_char(e.dataEmpenho,'yyyy') = " + sistemaControlador.getExercicioCorrente().getAno()
                    + " and obj.unidadeOrganizacional.id = " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId()
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

    public PagamentoControlador getPagamentoControlador() {
        return pagamentoControlador;
    }

    public void setPagamentoControlador(PagamentoControlador pagamentoControlador) {
        this.pagamentoControlador = pagamentoControlador;
    }
}
