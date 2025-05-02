package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class FaleConoscoWebPesquisaControlador extends ComponentePesquisaGenerico {

    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("hoAdm.codigo", "Código Unidade Administrativa", String.class, false, true, false));
        itens.add(new ItemPesquisaGenerica("hoAdm.descricao", "Descrição Unidade Administrativa", String.class, false, true, false));
        itens.add(new ItemPesquisaGenerica("hoOrc.codigo", "Código Unidade Orçamentária", String.class, false, true, false));
        itens.add(new ItemPesquisaGenerica("hoOrc.descricao", "Descrição Unidade Orçamentária", String.class, false, true, false));
        itensOrdenacao.clear();
        itensOrdenacao.addAll(itens);
    }

    @Override
    public String getHqlConsulta() {
        return " select distinct new FaleConoscoWeb(obj, " +
            "          (select hoAdmConst from HierarquiaOrganizacional hoAdmConst " +
            "               where hoAdmConst.tipoHierarquiaOrganizacional = '" + TipoHierarquiaOrganizacional.ADMINISTRATIVA.name() + "' " +
            "                and '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between hoAdmConst.inicioVigencia " +
            "                and coalesce(hoAdmConst.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "')" +
            "                and hoAdmConst.subordinada = obj.unidadeAdministrativa), " +
            "           (select hoOrcConst from HierarquiaOrganizacional hoOrcConst " +
            "                   where hoOrcConst.tipoHierarquiaOrganizacional = '" + TipoHierarquiaOrganizacional.ORCAMENTARIA.name() + "' " +
            "                and '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between hoOrcConst.inicioVigencia " +
            "                and coalesce(hoOrcConst.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "')" +
            "                and hoOrcConst.subordinada = obj.unidadeOrcamentaria) " +
            "                ) " +
            "   from FaleConoscoWeb obj, HierarquiaOrganizacional hoAdm, HierarquiaOrganizacional hoOrc " +
            "   inner join obj.unidadeAdministrativa unidAdm  " +
            "   inner join obj.unidadeOrcamentaria unidOrc  ";
    }

    @Override
    public String getComplementoQuery() {
        return " where hoAdm.subordinada = unidAdm " +
            "     and hoAdm.tipoHierarquiaOrganizacional = '" + TipoHierarquiaOrganizacional.ADMINISTRATIVA.name() + "'" +
            "     and '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between hoAdm.inicioVigencia and coalesce(hoAdm.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "')" +
            "     and hoOrc.subordinada = unidOrc " +
            "     and hoOrc.tipoHierarquiaOrganizacional = '" + TipoHierarquiaOrganizacional.ORCAMENTARIA.name() + "'" +
            "     and '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between hoOrc.inicioVigencia and coalesce(hoOrc.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "')" +
            "     and " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public String getHqlContador() {
        return " select count(distinct obj.id) from FaleConoscoWeb obj, HierarquiaOrganizacional hoAdm, HierarquiaOrganizacional hoOrc "
            + "  inner join obj.unidadeAdministrativa unidAdm "
            + "  inner join obj.unidadeOrcamentaria unidOrc ";
    }
}
