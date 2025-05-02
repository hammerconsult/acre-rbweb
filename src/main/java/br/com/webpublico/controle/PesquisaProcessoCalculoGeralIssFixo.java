package br.com.webpublico.controle;

import br.com.webpublico.enums.SituacaoProcessoCalculoGeralIssFixo;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 20/09/13
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PesquisaProcessoCalculoGeralIssFixo extends ComponentePesquisaGenerico implements Serializable {
    @Override
    public String getHqlConsulta() {
        return "select new ProcessoCalculoGeralIssFixo(obj.id, obj.descricao, ex, ta, obj.cmcInicial, obj.cmcFinal, obj.situacaoProcesso, usuarioSistema, obj.dataDoLancamento) " +
                "       from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getComplementoQuery() {
        return "  left join obj.exercicio ex " +
                "  left join obj.tipoAutonomo ta " +
                "  inner join obj.usuarioSistema usuarioSistema" +
                "      where " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public void getCampos() {
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("", "", null));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.descricao", "Descrição", String.class, false, true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.exercicio.ano", "Exercício.Ano", Integer.class,false ,true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.tipoAutonomo.descricao", "Tipo de Autônomo.Descrição", String.class, false, true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("cmcInicial", "C.M.C Inicial", String.class));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("cmcFinal", "C.M.C Final", String.class));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("situacaoProcesso", "Situação do Processo", SituacaoProcessoCalculoGeralIssFixo.class, true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("usuarioSistema.pessoaFisica.nome", "Usuário do Lançamento.Nome", String.class));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("usuarioSistema.login", "Usuário do Lançamento.Login", String.class));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("dataDoLancamento", "Data do Lançamento", Date.class));
    }

    private void adicionarItemNaPesquisaENaOrdenacao(ItemPesquisaGenerica itemPesquisaGenerica) {
        itens.add(itemPesquisaGenerica);
        itensOrdenacao.add(itemPesquisaGenerica);
    }
}
