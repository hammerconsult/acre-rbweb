/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import java.io.Serializable;
import java.util.List;


@ManagedBean
@ViewScoped
public class DespesaExtraPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private List<UnidadeOrganizacional> unidades;

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @Override
    public void novo(ComponentSystemEvent evento) {
        super.novo(evento);

        unidades = unidadeOrganizacionalFacade.listaUnidadesUsuarioCorrenteNivel3(sistemaControlador.getUsuarioCorrente(), sistemaControlador.getDataOperacao());
    }

    public String recuperaUnidade() {
        String retorno = "";
        for (UnidadeOrganizacional unidade : unidades) {
            retorno += unidade.getId() + ",";
        }
        return retorno.substring(0, retorno.length() - 1);
    }

    @Override
    public String getComplementoQuery() {
        return " where obj.unidadeOrganizacional.id in (" + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente().getId() + ")" +
               " and (obj.status = 'DEFERIDO' or obj.status = 'INDEFERIDO')" +
               " and to_char(obj.previstoPara, 'yyyy') = " + "'" + sistemaControlador.getExercicioCorrente() + "'" +
               " and obj.saldo > 0 " +
               " and " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("obj.status", "status", StatusPagamento.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.status", "status", StatusPagamento.class));
    }
}
