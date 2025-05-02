package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoProvimento;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by Buzatto on 31/07/2015.
 */
@ManagedBean
@ViewScoped
public class AlteracaoCargoComissaoPesquisaGenericaControlador extends PesquisaGenericaRHControlador implements Serializable {

    public AlteracaoCargoComissaoPesquisaGenericaControlador() {
        setNomeVinculo("obj.provimentoFP.vinculoFP");
    }

    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("uoAdm.descricao", "Descrição Unidade Organizacional", String.class, false, true, false));
        itens.add(new ItemPesquisaGenerica("cargo.codigoDoCargo", "Código do Cargo", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("cargo.descricao", "Descrição do Cargo", String.class, false, true));
        itensOrdenacao.clear();
        itensOrdenacao.addAll(itens);
    }

    @Override
    public String getHqlConsulta() {
        return " select new AlteracaoCargo(obj, unid) " +
            "   from AlteracaoCargo obj, UnidadeOrganizacional uoAdm " +
            "   join obj.provimentoFP prov " +
            "   join obj.contratoFPCargo.cargo cargo " +
            "   join prov.vinculoFP vin " +
            "   join vin.lotacaoFuncionals lot " +
            "   join lot.unidadeOrganizacional unid ";
    }

    @Override
    public String getComplementoQuery() {
        return " where uoAdm = unid " +
            "     and obj.provimentoFP.tipoProvimento.codigo = " + TipoProvimento.ALTERACAO_CARGO_COMISSAO +
            "     and " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public String getHqlContador() {
        return " select count(distinct obj.id) from AlteracaoCargo obj, UnidadeOrganizacional uoAdm " +
            "   join obj.provimentoFP prov " +
            "   join obj.contratoFPCargo.cargo cargo " +
            "   join prov.vinculoFP vin " +
            "   join vin.lotacaoFuncionals lot" +
            "   join lot.unidadeOrganizacional unid";
    }
}
