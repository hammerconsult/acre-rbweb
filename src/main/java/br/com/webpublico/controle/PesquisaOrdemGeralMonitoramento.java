package br.com.webpublico.controle;

import br.com.webpublico.entidades.MonitoramentoFiscal;
import br.com.webpublico.entidades.OrdemGeralMonitoramento;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.SituacaoOrdemGeralMonitoramento;
import br.com.webpublico.negocios.OrdemGeralMonitoramentoFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author octavio
 */
@ManagedBean
@ViewScoped
public class PesquisaOrdemGeralMonitoramento extends ComponentePesquisaGenerico implements Serializable {

    @EJB
    private OrdemGeralMonitoramentoFacade ordemGeralMonitoramentoFacade;

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.situacaoOGM", "Situação Ordem Geral de Monitoramento", SituacaoOrdemGeralMonitoramento.class));
        itens.add(new ItemPesquisaGenerica("obj.numeroProtocolo", "Número do Protocolo", String.class));
        itens.add(new ItemPesquisaGenerica("obj.anoProtocolo", "Ano do Protocolo", String.class));
        itens.add(new ItemPesquisaGenerica("obj.dataInicial", "Data Inicial OGM", Date.class));
        itens.add(new ItemPesquisaGenerica("obj.dataFinal", "Data Final OGM", Date.class));
        itens.add(new ItemPesquisaGenerica("obj.dataCriacao", "Data de Criação", Date.class));
        itens.add(new ItemPesquisaGenerica("obj.auditorFiscal", "Auditor Fiscal", Pessoa.class));
        itens.add(new ItemPesquisaGenerica("obj.descricao", "Descrição", String.class));
        itens.add(new ItemPesquisaGenerica("monitoramento.cadastroEconomico.inscricaoCadastral", "C.M.C", String.class));
        itens.add(new ItemPesquisaGenerica("monitoramento.cadastroEconomico.pessoa", "Razão Social / CNPJ", Pessoa.class));
        itens.add(new ItemPesquisaGenerica("fm.auditorFiscal.pessoaFisica", "Fiscal Designado", Pessoa.class));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.situacaoOGM", "Situação Ordem Geral de Monitoramento", SituacaoOrdemGeralMonitoramento.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.numeroProtocolo", "Número do Protocolo", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.anoProtocolo", "Ano do Protocolo", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.dataInicial", "Data Inicial OGM", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.dataFinal", "Data Final OGM", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.dataCriacao", "Data de Criação", Date.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.auditorFiscal", "Auditor Fiscal", Pessoa.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.descricao", "Descrição", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("monitoramento.cadastroEconomico.inscricaoCadastral", "C.M.C", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("monitoramento.cadastroEconomico.pessoa", "Razão Social / CNPJ", Pessoa.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("fm.auditorFiscal.pessoaFisica", "Fiscal Designado", Pessoa.class));
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id) " +
            " from OrdemGeralMonitoramento obj ";
    }

    @Override
    public String getHqlConsulta() {
        return "select distinct new OrdemGeralMonitoramento(obj.id, obj)" +
            " from OrdemGeralMonitoramento obj ";
    }

    @Override
    public String getComplementoQuery() {
        return " join obj.monitoramentosFiscais monitoramento " +
            " left join monitoramento.fiscaisMonitoramento fm " +
            " where " + montaCondicao() + montaOrdenacao();

    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
        for (OrdemGeralMonitoramento ordem : (List<OrdemGeralMonitoramento>) lista) {
            for (MonitoramentoFiscal monitoramentoFiscal : ordemGeralMonitoramentoFacade.monitoramentosDaOrdem(ordem)) {
                ordem.getFiscaisMonitoramento().addAll(ordemGeralMonitoramentoFacade.fiscaisDoMonitoramento(monitoramentoFiscal));
            }
        }
    }

}
