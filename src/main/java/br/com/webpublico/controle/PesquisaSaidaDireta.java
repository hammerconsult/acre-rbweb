package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.FacesUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 28/05/15
 * Time: 12:05
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
public class PesquisaSaidaDireta extends AbstractPesquisaMateriais {

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("tb.descricao", "Tipo de Baixa", String.class));
        itens.add(new ItemPesquisaGenerica("saida.numero", "N° da Saída", Long.class));
        itens.add(new ItemPesquisaGenerica("saida.dataSaida", "Data da Saída", Date.class));
        itens.add(new ItemPesquisaGenerica("saida.retiradoPor", "Retirado por", String.class));
        itens.add(new ItemPesquisaGenerica("obj.crm", "CRM", String.class));
        itens.add(new ItemPesquisaGenerica("obj.cns", "CNS", String.class));
        itens.add(new ItemPesquisaGenerica("obj.observacao", "Observação", String.class));

        itensOrdenacao.addAll(itens);
    }

    @Override
    public String getHqlConsulta() {
        return " select distinct " +
            "  obj.id as idObjeto, " +
            "  saida.numero as numeroSaida, " +
            "  saida.datasaida, " +
            "  pf.id as idPessoa, " +
            "  saida.retroativo, " +
            "  saida.processado, " +
            "  saida.retiradopor " + getSelect();
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id) " + getSelect();
    }

    public String getSelect() {
        return "  from saidadireta obj " +
            "  inner join saidamaterial saida on saida.id = obj.id " +
            "  inner join itemsaidamaterial item on item.saidamaterial_id = saida.id " +
            "  inner join localestoqueorcamentario le on le.id = item.localestoqueorcamentario_id " +
            "  inner join localestoque local on local.id = le.localestoque_id " +
            "  inner join usuariosistema usu on usu.id = saida.usuario_id " +
            "  inner join pessoafisica pf on pf.id = usu.pessoafisica_id " +
            "  inner join tipobaixabens tb on tb.id = saida.tipobaixabens_id ";
    }

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        return " where " + montarCondicaoUnidade("local.unidadeorganizacional_id") +
            "     and " + condicao;
    }

    @Override
    public String getComplementoQuery() {
        return montaCondicao() + montaOrdenacao();
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        try {
            validarUsuarioGestorMateriais();
            Object[] retorno = getSaidaMaterialFacade().filtarComContadorDeRegistrosSaidaDireta(sql, sqlCount, inicio, maximoRegistrosTabela);
            processarRetorno(retorno);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao tentar Pesquisar!" + ex.getMessage());
            logger.debug(ex.getMessage());
        }
    }

    @Override
    public TipoMovimento getTipoMovimento() {
        return TipoMovimento.SAIDA;
    }

    @Override
    public String getNivelHierarquia() {
        return null;
    }
}
