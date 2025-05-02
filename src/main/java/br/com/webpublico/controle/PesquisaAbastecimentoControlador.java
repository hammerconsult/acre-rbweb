package br.com.webpublico.controle;

import br.com.webpublico.entidades.AbastecimentoObjetoFrota;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 29/09/14
 * Time: 10:45
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PesquisaAbastecimentoControlador extends ComponentePesquisaGenerico {

    private static Logger logger = LoggerFactory.getLogger(PesquisaAbastecimentoControlador.class);
    private static String PLACA = "placa";
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.ano", "Ano", Integer.class));
        itens.add(new ItemPesquisaGenerica("obj.numero", "Número", Integer.class));
        itens.add(new ItemPesquisaGenerica("obj.numeroAbastecimentoManual", "Número Manual", String.class));
        itens.add(new ItemPesquisaGenerica("obj.cotaAbastecimento.anoCota", "Ano da Cota", Integer.class));
        itens.add(new ItemPesquisaGenerica("obj.cotaAbastecimento.numeroCota", "Número da Cota", Integer.class));
        itens.add(new ItemPesquisaGenerica("obj.tipoObjetoFrota", "Tipo", TipoObjetoFrota.class, true));
        itens.add(new ItemPesquisaGenerica("obj.objetoFrota.identificacao", "Identificação", String.class, false, true));
        itens.add(new ItemPesquisaGenerica(PLACA, "Placa do Veículo", String.class, false, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.ano", "Ano", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.numero", "Número", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.numeroAbastecimentoManual", "Número Manual", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.cotaAbastecimento.anoCota", "Ano da Cota", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.cotaAbastecimento.numeroCota", "Número da Cota", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.tipoObjetoFrota", "Tipo", TipoObjetoFrota.class, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.objetoFrota.identificacao", "Identificação", String.class, false, true));
    }

    @Override
    public String getHqlContador() {
        return "select count( distinct  obj.id) from AbastecimentoObjetoFrota obj " +
            "   join obj.objetoFrota.unidades unidObjFrota " +
            "   join unidObjFrota.unidadeOrganizacional unidade ";
    }

    @Override
    public String getHqlConsulta() {
        return " select distinct new AbastecimentoObjetoFrota(obj, (select ho from HierarquiaOrganizacional ho " +
            "               where ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' " +
            "                and '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between ho.inicioVigencia " +
            "                and coalesce(ho.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "')" +
            "                and ho.subordinada = unidade)) " +
            "   from AbastecimentoObjetoFrota obj " +
            "   join obj.objetoFrota.unidades unidObjFrota " +
            "   join unidObjFrota.unidadeOrganizacional unidade ";
    }

    @Override
    public String getComplementoQuery() {
        return " where '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between unidObjFrota.inicioVigencia " +
            "    and coalesce(unidObjFrota.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "')" +
            "    and " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        logger.debug("SQL: " + sql);
        super.executarConsulta(sql, sqlCount);
        HierarquiaOrganizacional hierarquiaOrganizacionalCorrente =
            hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                sistemaFacade.getDataOperacao(),
                sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        List<AbastecimentoObjetoFrota> abastecimentosDoUsuario = new ArrayList();
        if (lista.size() > 0) {
            for (AbastecimentoObjetoFrota abastecimento : (List<AbastecimentoObjetoFrota>) lista) {
                if (abastecimento.getHierarquiaOrganizacional().getCodigo().substring(0, 5).equals(hierarquiaOrganizacionalCorrente.getCodigo().substring(0, 5))) {
                    abastecimentosDoUsuario.add(abastecimento);
                }
            }
        }
        lista = abastecimentosDoUsuario;
    }

    @Override
    public String montaCondicao() {
        String condicao = "";
        List<DataTablePesquisaGenerico> paraRemover = Lists.newArrayList();
        for (DataTablePesquisaGenerico dataTablePesquisaGenerico : camposPesquisa) {
            if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals(PLACA)) {
                paraRemover.add(dataTablePesquisaGenerico);
                condicao += " and obj.objetoFrota.id in (select id from Veiculo where lower(placa) like '%" + dataTablePesquisaGenerico.getValuePesquisa().trim().toLowerCase() + "%') ";
            }
        }
        camposPesquisa.removeAll(paraRemover);
        condicao = super.montaCondicao() + condicao;
        camposPesquisa.addAll(paraRemover);
        return condicao;
    }
}
