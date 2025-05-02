/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CarteiraTrabalho;
import br.com.webpublico.entidades.RG;
import br.com.webpublico.entidades.TituloEleitor;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.PerfilEnum;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Felipe Marinzeck
 */
@ManagedBean
@ViewScoped
public class PessoaFisicaPesquisaGenericaRHControlador extends ComponentePesquisaGenerico implements Serializable {

    private final static String ALIAS_RG = "rg";
    private final static String ALIAS_TITULOELEITOR = "titulo";
    private final static String ALIAS_CARTEIRADETRABALHO = "ct";

    @Override
    public void novo(ComponentSystemEvent evento) {
        super.novo(evento);
    }

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica(ALIAS_RG + ".numero", "RG", RG.class, false, true));
        itens.add(new ItemPesquisaGenerica(ALIAS_TITULOELEITOR + ".numero", "Titulo Eleitor", TituloEleitor.class, false, true));
        itens.add(new ItemPesquisaGenerica(ALIAS_CARTEIRADETRABALHO + ".numero", "Carteira de Trabalho", CarteiraTrabalho.class, false, true));
        ItemPesquisaGenerica ipg = new ItemPesquisaGenerica("p", "Perfil", PerfilEnum.class, true, false);
        itens.add(ipg);
        DataTablePesquisaGenerico item = new DataTablePesquisaGenerico();
        item.setItemPesquisaGenerica(ipg);
        item.setValuePesquisa(PerfilEnum.PERFIL_RH.name());
        camposPesquisa.add(item);

    }

    @Override
    public String getHqlConsulta() {
        String tabelas = montarTabelasToSelect();
        return "select obj from " + classe.getSimpleName() + " obj" + tabelas;
    }

    @Override
    public String getComplementoQuery() {
        String innerJoin = complementarInnerJoinQuery();

        String where = complementarWhereQuery();
        String restanteHQL = innerJoin + " where " + where + montaCondicao() + montaOrdenacao();

        if (verificarSePerfilFoiSelecionado()) {
            return " inner join obj.perfis as p " + restanteHQL;
        }
        return restanteHQL;
    }


    private boolean verificarSePerfilFoiSelecionado() {
        for (DataTablePesquisaGenerico pesquisaGenerico : camposPesquisa) {
            if (pesquisaGenerico != null) {
                if (pesquisaGenerico.getItemPesquisaGenerica().getTipo() != null) {
                    if (pesquisaGenerico.getItemPesquisaGenerica().getTipo().equals(PerfilEnum.class)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public String complementarInnerJoinQuery() {
        String innerJoin = "";
        for (DataTablePesquisaGenerico ipg : camposPesquisa) {
            try {
                if (ipg.getItemPesquisaGenerica().getTipo().equals(RG.class)) {
                    innerJoin += " inner join obj.documentosPessoais as dpsrg ";
                }
                if (ipg.getItemPesquisaGenerica().getTipo().equals(CarteiraTrabalho.class)) {
                    innerJoin += " inner join obj.documentosPessoais as dpscarteira ";
                }
                if (ipg.getItemPesquisaGenerica().getTipo().equals(TituloEleitor.class)) {
                    innerJoin += " inner join obj.documentosPessoais as dpstitulo ";
                }
            } catch (NullPointerException e) {
            }
        }
        return innerJoin;
    }

    public String complementarWhereQuery() {
        String complemento = "";
        for (DataTablePesquisaGenerico ipg : camposPesquisa) {
            try {
                if (ipg.getItemPesquisaGenerica().getTipo().equals(RG.class)) {
                    complemento += " dpsrg in (" + ALIAS_RG + ") and ";
                }
                if (ipg.getItemPesquisaGenerica().getTipo().equals(CarteiraTrabalho.class)) {
                    complemento += " dpscarteira in (" + ALIAS_CARTEIRADETRABALHO + ") and ";
                }
                if (ipg.getItemPesquisaGenerica().getTipo().equals(TituloEleitor.class)) {
                    complemento += " dpstitulo in (" + ALIAS_TITULOELEITOR + ") and ";
                }
            } catch (NullPointerException e) {
            }
        }
        return complemento;
    }

    public String montarTabelasToSelect() {
        String tabelas = "";
        for (DataTablePesquisaGenerico ipg : camposPesquisa) {
            try {
                if (ipg.getItemPesquisaGenerica().getTipo().equals(RG.class)) {
                    tabelas += ", RG rg";
                }
                if (ipg.getItemPesquisaGenerica().getTipo().equals(CarteiraTrabalho.class)) {
                    tabelas += ", CarteiraTrabalho ct";
                }
                if (ipg.getItemPesquisaGenerica().getTipo().equals(TituloEleitor.class)) {
                    tabelas += ", TituloEleitor titulo";
                }
            } catch (NullPointerException e) {
            }
        }
        return tabelas;
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
        lista = new ArrayList(new HashSet(lista));
    }
}
