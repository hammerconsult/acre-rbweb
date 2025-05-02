package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 06/03/15
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
public class PesquisaEfetivacaoEstornoTransferencia extends AbstractPesquisaPatrimonial {

    @Override
    public String getNivelHierarquia() {
        return null;
    }

    @Override
    public TipoDaConsultaDaHirarquia getTipoHierarquia() {
        return TipoDaConsultaDaHirarquia.PAI_E_FILHO;
    }

    @Override
    protected String[] getAtributoUnidadeOrganizacional() {
        return new String[]{"unidade.id"};
    }

    @Override
    public String getComplementoQuery() {
        return "  inner join obj.solicitacaoEstorno.loteEfetivacaoTransferencia.unidadeOrganizacional  unidade " +
            "  inner join obj.listaItemEfetivacaoEstornoTransferencia est " +
            "  inner join obj.efetivador.pessoaFisica pf " +
            "  where " + montaCondicao() + montaOrdenacao();
    }

    @Override
    protected String ordenacaoPadrao() {
        return " order by obj.codigo desc";
    }

    @Override
    public String getNomeCampoTipoBem() {
        return "obj.solicitacaoEstorno.loteEfetivacaoTransferencia.tipoBem";
    }

    public String getHqlConsulta() {
        return "select distinct obj  from " + classe.getSimpleName() + " obj ";
    }

    public String getHqlContador() {
        return "select count(distinct obj.id)  from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("pf.nome", "Efetivador", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("est.bem.identificacao", "Registro Patrimonial", String.class, false, true, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("pf.nome", "Efetivador", String.class, false, true));
    }
}
