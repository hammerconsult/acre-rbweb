package br.com.webpublico.controle;

import br.com.webpublico.entidades.DiarioTrafego;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 17/10/14
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PesquisaDiarioTrafego extends ComponentePesquisaGenerico {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.ano", "Ano", Integer.class));
        itens.add(new ItemPesquisaGenerica("obj.mes", "Mês", Integer.class));
        itens.add(new ItemPesquisaGenerica("obj.veiculo.identificacao", "Identificação do Veículo", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.veiculo.placa", "Placa do Veículo", String.class, false, true));


        itensOrdenacao.addAll(itens);
    }

    @Override
    public String getHqlConsulta() {
        return " select new DiarioTrafego(obj, (select ho from HierarquiaOrganizacional ho " +
            "               where ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' " +
            "                 and '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between ho.inicioVigencia " +
            "                 and coalesce(ho.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "')" +
            "                 and ho.subordinada = unidade)) " +
            "    from DiarioTrafego obj " +
            "   join obj.veiculo veic " +
            "   join veic.unidades unidObjFrota " +
            "   join unidObjFrota.unidadeOrganizacional unidade ";
    }

    @Override
    public String getComplementoQuery() {
        return "where '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between unidObjFrota.inicioVigencia " +
            "   and coalesce(unidObjFrota.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "')" +
            "   and " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public String getHqlContador() {
        return " select count(distinct obj.id) from " + classe.getSimpleName() + " obj " +
            "   join obj.veiculo veic " +
            "   join veic.unidades unidObjFrota " +
            "   join unidObjFrota.unidadeOrganizacional unidade ";
    }


    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
        HierarquiaOrganizacional hierarquiaOrganizacionalCorrente =
            hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                sistemaFacade.getDataOperacao(),
                sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        List<DiarioTrafego> diarioTrafegos = new ArrayList();
        if (lista.size() > 0) {
            for (DiarioTrafego diarioTrafego : (List<DiarioTrafego>) lista) {
                if (diarioTrafego.getHierarquiaOrganizacional().getCodigo().substring(0, 5).equals(hierarquiaOrganizacionalCorrente.getCodigo().substring(0, 5))) {
                    diarioTrafegos.add(diarioTrafego);
                }
            }
        }
        lista = diarioTrafegos;
    }
}
