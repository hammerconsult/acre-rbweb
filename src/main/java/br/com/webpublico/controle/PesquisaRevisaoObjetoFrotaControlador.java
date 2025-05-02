package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.RevisaoObjetoFrota;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 29/09/14
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PesquisaRevisaoObjetoFrotaControlador extends ComponentePesquisaGenerico {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.descricao", "Descrição", null));
        itens.add(new ItemPesquisaGenerica("tipoObjetoFrota", "Tipo", TipoObjetoFrota.class, true));
        itens.add(new ItemPesquisaGenerica("veic.identificacao", "Identificação do Veículo", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("veic.placa", "Placa do Veículo", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("equip.identificacao", "Identificação do Equipamento/Máquina", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.revisadoPor", "Revisado Por", Pessoa.class));
        itens.add(new ItemPesquisaGenerica("obj.realizadoEm", "Realiza Em", Date.class));
        itens.add(new ItemPesquisaGenerica("obj.programadaPara", "Programada Para", Date.class));
        itens.add(new ItemPesquisaGenerica("obj.kmRealizada", "Km Realizada", Integer.class));
        itens.add(new ItemPesquisaGenerica("obj.kmProgramada", "Km Programada", Integer.class));

        itensOrdenacao.addAll(itens);
    }

    @Override
    public String getHqlConsulta() {
        return " select distinct new RevisaoObjetoFrota(obj, (select ho from HierarquiaOrganizacional ho " +
            "               where ho.tipoHierarquiaOrganizacional = '" + TipoHierarquiaOrganizacional.ADMINISTRATIVA.name() + "'" +
            "                 and '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between ho.inicioVigencia " +
            "                 and coalesce(ho.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "')" +
            "                 and ho.subordinada = unidade)) " +
            "    from RevisaoObjetoFrota obj, Veiculo veic, Equipamento equip " +
            "   join obj.objetoFrota objFrota " +
            "   join objFrota.unidades unidObjFrota " +
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
        return " select count(distinct obj.id) from " + classe.getSimpleName() + " obj, Veiculo veic, Equipamento equip "
            + "  join obj.objetoFrota objFrota "
            + "  join objFrota.unidades unidObjFrota "
            + "  join unidObjFrota.unidadeOrganizacional unidade ";
    }

    @Override
    public String montaCondicao() {
        String where = super.montaCondicao();
        if (where.contains("veic.")) {
            where += " and obj.objetoFrota.id = veic.id ";
        } else if (where.contains("equip.")) {
            where += " and obj.objetoFrota.id = equip.id ";
        }
        return where;
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
        HierarquiaOrganizacional hierarquiaOrganizacionalCorrente =
            hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                sistemaFacade.getDataOperacao(),
                sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        List<RevisaoObjetoFrota> revisoesDoUsuario = new ArrayList();
        if (lista.size() > 0) {
            for (RevisaoObjetoFrota revisaoObjetoFrota : (List<RevisaoObjetoFrota>) lista) {
                if (revisaoObjetoFrota.getHierarquiaOrganizacional().getCodigo().substring(0, 5).equals(hierarquiaOrganizacionalCorrente.getCodigo().substring(0, 5))) {
                    revisoesDoUsuario.add(revisaoObjetoFrota);
                }
            }
        }
        lista = revisoesDoUsuario;
    }
}
