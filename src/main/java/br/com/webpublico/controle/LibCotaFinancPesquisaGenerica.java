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
public class LibCotaFinancPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @Override
    public String getComplementoQuery() {
        return  " where obj.unidadeOrganizacional.id = " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId() + " " +
                " and obj.saldo > 0 "
                + " and (obj.statusPagamento = '" + StatusPagamento.DEFERIDO.name() + "'"
                + " or obj.statusPagamento = '" + StatusPagamento.INDEFERIDO + "')"
                + " and " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("obj.status", "status", StatusPagamento.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.status", "status", StatusPagamento.class));
    }
}
