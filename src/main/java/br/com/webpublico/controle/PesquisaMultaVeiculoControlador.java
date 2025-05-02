package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.MultaVeiculo;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
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
 * Time: 13:24
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PesquisaMultaVeiculoControlador extends ComponentePesquisaGenerico {
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.numeroAutoInfracao", "Número do Auto de Infração", String.class));
        itens.add(new ItemPesquisaGenerica("obj.veiculo.identificacao", "Identificação do Veículo", String.class));
        itens.add(new ItemPesquisaGenerica("obj.veiculo.placa", "Placa do Veículo", String.class));
        itens.add(new ItemPesquisaGenerica("obj.emitidaEm", "Data Emissão", Date.class));
        itens.add(new ItemPesquisaGenerica("obj.localMulta", "Local da Multa", String.class));
        itens.add(new ItemPesquisaGenerica("obj.motorista.pessoaFisica.cpf", "CPF do Motorista", String.class));
        itens.add(new ItemPesquisaGenerica("obj.motorista.pessoaFisica.nome", "Nome do Motorista", String.class));
        itens.add(new ItemPesquisaGenerica("obj.cidade.nome", "Cidade", String.class));
        itens.add(new ItemPesquisaGenerica("obj.tipoMulta.descricao", "Tipo de Multa", String.class));

        itensOrdenacao.addAll(itens);
    }

    @Override
    public String getHqlConsulta() {
        return " select new MultaVeiculo(obj, (select ho from HierarquiaOrganizacional ho " +
            "               where ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' " +
            "                and '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "' between ho.inicioVigencia " +
            "                and coalesce(ho.fimVigencia, '" + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + "')" +
            "                and ho.subordinada = unidade)) " +
            "   from MultaVeiculo obj " +
            "   join obj.veiculo veiculo " +
            "   join veiculo.unidades unidObjFrota " +
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
            "   join obj.veiculo veiculo " +
            "   join veiculo.unidades unidObjFrota " +
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
        List<MultaVeiculo> multasDoUsuario = new ArrayList();
        if (lista.size() > 0) {
            for (MultaVeiculo multaVeiculo : (List<MultaVeiculo>) lista) {
                if (multaVeiculo.getHierarquiaOrganizacional().getCodigo().substring(0, 5).equals(hierarquiaOrganizacionalCorrente.getCodigo().substring(0, 5))) {
                    multasDoUsuario.add(multaVeiculo);
                }
            }
        }
        lista = multasDoUsuario;
    }
}
