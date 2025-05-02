package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 06/03/15
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
public class PesquisaSolicitacaoEstornoTransferencia extends AbstractPesquisaPatrimonial {

    @Override
    public String getNivelHierarquia() {
        return null;
    }

    @Override
    protected String[] getAtributoUnidadeOrganizacional() {
        return new String[]{"unidade.id"};
    }

    @Override
    public String getComplementoQuery() {
        return "  inner join obj.loteEfetivacaoTransferencia.unidadeOrganizacional unidade " +
            "  inner join obj.solicitante.pessoaFisica pf " +
            " where " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("pf.nome", "Solicitante", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("pf.nome", "Solicitante", String.class, false, true));
    }

    @Override
    protected String ordenacaoPadrao() {
        return " order by obj.codigo desc";
    }

    @Override
    public String getNomeCampoTipoBem() {
        return "obj.loteEfetivacaoTransferencia.tipoBem";
    }

    @Override
    public String montaCondicao() {
        return super.montaCondicao() + " and obj.removido = false ";
    }

    public String getHqlConsulta() {
        return "select distinct obj  from " + classe.getSimpleName() + " obj ";
    }

    public String getHqlContador() {
        return "select count(distinct obj.id)  from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public TipoDaConsultaDaHirarquia getTipoHierarquia() {
        return TipoDaConsultaDaHirarquia.PAI_E_FILHO;
    }
}
