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
 * Date: 25/09/13
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PesquisaEfetivacaoIssFixoGeral extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getHqlConsulta() {
        return "select new EfetivacaoProcessoIssFixoGeral(obj.id, obj.processo, obj.efetivacao) " +
                "       from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public void getCampos() {
         adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("", "", null));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.efetivacao.usuarioSistema.pessoaFisica.nome", "Usuário da Efetivação.Nome", String.class, false , true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.efetivacao.usuarioSistema.login", "Usuário da Efetivação.Login", String.class, false , true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.processo.dataDoLancamento", "Data do Lançamento", Date.class, false ,true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.processo.descricao", "Descrição do Lançamento", String.class, false , true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.processo.exercicio.ano", "Exercício.Ano", Integer.class, false , true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.processo.tipoAutonomo.descricao", "Tipo de Autônomo.Descrição", String.class, false, true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.processo.cmcInicial", "C.M.C Inicial", String.class, false ,true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.processo.cmcFinal", "C.M.C Final", String.class,false,true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.processo.situacaoProcesso", "Situação do Processo", SituacaoProcessoCalculoGeralIssFixo.class, true,true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.processo.usuarioSistema.pessoaFisica.nome", "Usuário do Lançamento.Nome", String.class,false , true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.processo.usuarioSistema.login", "Usuário do Lançamento.Login", String.class, false , true));
        adicionarItemNaPesquisaENaOrdenacao(new ItemPesquisaGenerica("obj.processo.dataDoLancamento", "Data do Lançamento", Date.class, false , true));
    }

    private void adicionarItemNaPesquisaENaOrdenacao(ItemPesquisaGenerica itemPesquisaGenerica) {
        itens.add(itemPesquisaGenerica);
        itensOrdenacao.add(itemPesquisaGenerica);
    }
}
