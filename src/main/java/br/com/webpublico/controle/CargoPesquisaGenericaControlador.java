/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@ManagedBean
@ViewScoped
public class CargoPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void novo(ComponentSystemEvent evento) {
        super.novo(evento);
    }

    @Override
    public void getCampos() {
        super.getCampos();

        itens.add(new ItemPesquisaGenerica("obj.atoLegal.numero", "Número do Ato Legal", String.class));
        itens.add(new ItemPesquisaGenerica("obj.atoLegal.nome", "Nome do Ato Legal", String.class));
        itens.add(new ItemPesquisaGenerica("obj.atoLegal.exercicio.ano", "Exercício do Ato Legal", Integer.class));
        itens.add(new ItemPesquisaGenerica("entidade.nome", "Empregador", String.class, false, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("obj.atoLegal.numero", "Número do Ato Legal", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.atoLegal.nome", "Nome do Ato Legal", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.atoLegal.exercicio.ano", "Exercício do Ato Legal", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("entidade.nome", "Empregador", String.class, false, true));
    }

    @Override
    public String getComplementoQuery() {
        return " left join obj.empregadores empregadores " +
            " left join empregadores.empregador empregador " +
            " left join empregador.entidade entidade " + super.getComplementoQuery();
    }
}
