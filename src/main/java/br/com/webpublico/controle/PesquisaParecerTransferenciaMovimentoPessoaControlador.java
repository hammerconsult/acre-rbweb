package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.SituacaoTransfMovimentoPessoa;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Wellington on 16/12/2015.
 */
@ManagedBean
@ViewScoped
public class PesquisaParecerTransferenciaMovimentoPessoaControlador extends ComponentePesquisaGenerico implements Serializable {
    @Override
    public void getCampos() {
        adicionarItemNaPesquisaAndOrdenacao(new ItemPesquisaGenerica("", "", null));
        adicionarItemNaPesquisaAndOrdenacao(new ItemPesquisaGenerica("obj.realizadoEm", "Data/Hora do Parecer", Date.class, false));
        adicionarItemNaPesquisaAndOrdenacao(new ItemPesquisaGenerica("coalesce(obj.usuarioSistema.pessoaFisica.nome, obj.usuarioSistema.login)", "Responsável do Parecer", String.class, false, true));
        adicionarItemNaPesquisaAndOrdenacao(new ItemPesquisaGenerica("obj.transferenciaMovPessoa.numeroTransferencia", "Número da Solicitação", Long.class, false));
        adicionarItemNaPesquisaAndOrdenacao(new ItemPesquisaGenerica("obj.transferenciaMovPessoa.dataTransferencia", "Data/Hora da Solicitação", Date.class, false));
        adicionarItemNaPesquisaAndOrdenacao(new ItemPesquisaGenerica("coalesce(obj.transferenciaMovPessoa.usuarioSistema.pessoaFisica.nome, obj.transferenciaMovPessoa.usuarioSistema.login)", "Responsável da Solicitação", String.class, false, true));
        adicionarItemNaPesquisaAndOrdenacao(new ItemPesquisaGenerica("obj.transferenciaMovPessoa.situacao", "Situação da Solicitação", SituacaoTransfMovimentoPessoa.class, true));
        adicionarItemNaPesquisaAndOrdenacao(new ItemPesquisaGenerica("obj.transferenciaMovPessoa.pessoaOrigem", "Pessoa de Origem da Solicitação", Pessoa.class, false));
        adicionarItemNaPesquisaAndOrdenacao(new ItemPesquisaGenerica("obj.transferenciaMovPessoa.pessoaDestino", "Pessoa de Destino da Solicitação", Pessoa.class, false));
    }

    private void adicionarItemNaPesquisaAndOrdenacao(ItemPesquisaGenerica itemPesquisaGenerica) {
        itens.add(itemPesquisaGenerica);
        itensOrdenacao.add(itemPesquisaGenerica);
    }
}
