package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SituacaoAcaoFiscal;
import br.com.webpublico.enums.SituacaoAutoInfracao;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

@ManagedBean
@ViewScoped
public class PesquisaAutoInfracao extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("numero", "N. do Auto de Infração", Integer.class));
        itens.add(new ItemPesquisaGenerica("doctoAcaoFiscal.acaoFiscal.programacaoFiscal.numero", "N. da Programação", Long.class));
        itens.add(new ItemPesquisaGenerica("doctoAcaoFiscal.acaoFiscal.ordemServico", "N. Orderm Serv.", Long.class));
        itens.add(new ItemPesquisaGenerica("doctoAcaoFiscal.acaoFiscal.cadastroEconomico.inscricaoCadastral", "C.M.C", String.class));
        itens.add(new ItemPesquisaGenerica("doctoAcaoFiscal.acaoFiscal.cadastroEconomico.pessoa", "Razão Social / CNPJ", Pessoa.class));
        itens.add(new ItemPesquisaGenerica("situacaoAutoInfracao", "Situação", SituacaoAutoInfracao.class, true));
        itens.add(new ItemPesquisaGenerica("vencimento", "Data de Vencimento", Date.class));
        itens.add(new ItemPesquisaGenerica("dataCienciaRevelia", "Data de Ciencia/Revelia", Date.class));
        itens.add(new ItemPesquisaGenerica("dataEstorno", "Data de Estorno", Date.class));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("numero", "N. do Auto de Infração", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("doctoAcaoFiscal.acaoFiscal.programacaoFiscal.numero", "N. da Programação", Long.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("doctoAcaoFiscal.acaoFiscal.ordemServico", "N. Orderm Serv.", Long.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("doctoAcaoFiscal.acaoFiscal.cadastroEconomico.inscricaoCadastral", "C.M.C", SituacaoAcaoFiscal.class, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("doctoAcaoFiscal.acaoFiscal.cadastroEconomico.pessoa", "Razão Social / CNPJ", Pessoa.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("situacaoAutoInfracao", "Situação", SituacaoAutoInfracao.class, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("vencimento", "Data de Vencimento", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("dataCienciaRevelia", "Data de Ciencia/Revelia", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("dataEstorno", "Data de Estorno", Date.class));
    }

    @Override
    public String getHqlConsulta() {
        return "select new AutoInfracaoFiscal(obj.id, "
                + "obj.numero, "
                + "obj.ano, "
                + "obj.situacaoAutoInfracao, "
                + "obj.valorAutoInfracao, "
                + "obj.vencimento, "
                + "obj.doctoAcaoFiscal.acaoFiscal.cadastroEconomico, "
                + "obj.doctoAcaoFiscal.acaoFiscal.programacaoFiscal.numero, "
                + "obj.doctoAcaoFiscal.acaoFiscal.ordemServico) from " + classe.getSimpleName() + " obj ";
    }
}
