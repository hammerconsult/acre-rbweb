/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;

/**
 * @author Adriano Jandre
 */
@ManagedBean
@ViewScoped
public class EstornoEmpenhoPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "empenhoEstornoControlador", value = "#{empenhoEstornoControlador}")
    private EmpenhoEstornoControlador empenhoEstornoControlador;

    @Override
    public String getComplementoQuery() {
        if (empenhoEstornoControlador.isEmpenhoEstornoNormal()) {
            return " where obj.saldo > 0 and to_char(obj.dataEmpenho, 'yyyy') = " + "'" + sistemaControlador.getExercicioCorrente() + "'"
                    + " and obj.categoriaOrcamentaria = 'NORMAL'"
                    + " and obj.unidadeOrganizacional.id = " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId()
                    + " and " + montaCondicao() + montaOrdenacao();
        } else {
            return " where obj.saldo > 0 and to_char(obj.dataEmpenho, 'yyyy') = " + "'" + sistemaControlador.getExercicioCorrente() + "'"
                    + " and obj.categoriaOrcamentaria = 'RESTO'"
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


    public EmpenhoEstornoControlador getEmpenhoEstornoControlador() {
        return empenhoEstornoControlador;
    }

    public void setEmpenhoEstornoControlador(EmpenhoEstornoControlador empenhoEstornoControlador) {
        this.empenhoEstornoControlador = empenhoEstornoControlador;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
