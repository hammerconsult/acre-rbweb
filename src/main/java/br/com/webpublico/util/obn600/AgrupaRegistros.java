/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn600;


import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author reidocrime
 */
public class AgrupaRegistros {

    private RegistroObn600TipoUm obn600TipoUm;
    private List<RegistroObn600TipoDois> listaRegistroObnTipoDois;
    private RegistroObn600TipoDois registroObn600TipoDois;
    private Bordero bordero;
    private StringBuilder texto;

    public AgrupaRegistros() {
        throw new ExcecaoNegocioGenerica("Construtor não suportado para a classe: LayoutObn600!");
    }

    public AgrupaRegistros(Bordero bordero) {
        texto = new StringBuilder();
        this.bordero = bordero;
        listaRegistroObnTipoDois = new ArrayList<>();
        obn600TipoUm = new RegistroObn600TipoUm("1", bordero.getUnidadeOrganizacional().getAgenciaBancaria(), " ", bordero.getUnidadeOrganizacional().getContaBancaria(), " ", bordero.getUnidadeOrganizacional().getDescricao(), " ");
        texto.append(obn600TipoUm.getLinha());
        triagemTipoMovimentacaoFinanceiraEmBordero(this.bordero);
    }

    public RegistroObn600TipoUm getObn600TipoUm() {
        return obn600TipoUm;
    }

    public void setObn600TipoUm(RegistroObn600TipoUm obn600TipoUm) {
        this.obn600TipoUm = obn600TipoUm;
    }

    public List<RegistroObn600TipoDois> getListaRegistroObnTipoDois() {
        return listaRegistroObnTipoDois;
    }

    public void setListaRegistroObnTipoDois(List<RegistroObn600TipoDois> listaRegistroObnTipoDois) {
        this.listaRegistroObnTipoDois = listaRegistroObnTipoDois;
    }

    private void triagemTipoMovimentacaoFinanceiraEmBordero(Bordero bordero) {
        percorreLiberacaoCotasFinanceiras(bordero.getListaLiberacaoCotaFinanceira());
        percorrePagamentos(bordero.getListaPagamentos());
        percorrePagamentosExtras(bordero.getListaPagamentosExtra());
        percorreTransferenciasFinanceiras(bordero.getListaTransferenciaFinanceira());
        percorreTransferenciasMesmaUnidade(bordero.getListaTransferenciaMesmaUnidade());
    }

    private void percorrePagamentosExtras(List<BorderoPagamentoExtra> listaPagamentoExtras) {
        for (BorderoPagamentoExtra borderoPagamentoExtra : listaPagamentoExtras) {
            PagamentoExtra l = borderoPagamentoExtra.getPagamentoExtra();
            registroObn600TipoDois =
                    new RegistroObn600TipoDois("2", l.getUnidadeOrganizacional().getAgenciaBancaria(),
                    "semsolu", bordero.getSequenciaArquivo() + "", l.getNumero(),
                    DataUtil.getDataFormatada(bordero.getDataDebito()), " ", l.getTipoOperacaoPagto().getNumero(),
                    "0", "0", l.getValor() + "",
                    l.getFornecedor().getContaCorrenteBancPessoas().get(0).getContaCorrenteBancaria().getAgencia().getBanco().getNumeroBanco(),
                    l.getFornecedor().getContaCorrenteBancPessoas().get(0).getContaCorrenteBancaria().getAgencia().getNumeroAgencia(),
                    l.getFornecedor().getContaCorrenteBancPessoas().get(0).getContaCorrenteBancaria().getAgencia().getDigitoVerificador(),
                    l.getFornecedor().getContaCorrenteBancPessoas().get(0).getContaCorrenteBancaria().getNumeroConta(),
                    l.getFornecedor().getNome(), l.getFornecedor().getEnderecoPrincipal().getLogradouro(),
                    l.getFornecedor().getEnderecoPrincipal().getLocalidade(), "0", l.getFornecedor().getEnderecoPrincipal().getCep(),
                    l.getFornecedor().getEnderecoPrincipal().getUf(), l.getComplementoHistorico(), "1",
                    l.getFornecedor() instanceof PessoaFisica ? "2" : "1", l.getFornecedor().getCpf_Cnpj(), "",
                    "", "", "",
                    "10", "Seq");
            listaRegistroObnTipoDois.add(registroObn600TipoDois);
        }
    }

    private void percorreLiberacaoCotasFinanceiras(List<BorderoLiberacaoFinanceira> listaLiberacaoCotaFinanceiras) {
        for (BorderoLiberacaoFinanceira l : listaLiberacaoCotaFinanceiras) {
            registroObn600TipoDois = new RegistroObn600TipoDois(null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null);
            listaRegistroObnTipoDois.add(registroObn600TipoDois);
        }
    }

    private void percorrePagamentos(List<BorderoPagamento> listaPagamento) {
        for (BorderoPagamento borderoPagamento : listaPagamento) {
            Pagamento p = borderoPagamento.getPagamento();
            registroObn600TipoDois = new RegistroObn600TipoDois("2", p.getUnidadeOrganizacional().getAgenciaBancaria(),
                    "semsolu", bordero.getSequenciaArquivo() + "", p.getNumeroPagamento(),
                    DataUtil.getDataFormatada(bordero.getDataDebito()), " ", p.getTipoOperacaoPagto().getNumero(),
                    "0", "0", p.getValorFinal() + "",
                    p.getContaPgto().getContaCorrenteBancPessoas().get(0).getContaCorrenteBancaria().getAgencia().getBanco().getNumeroBanco(),
                    p.getContaPgto().getContaCorrenteBancPessoas().get(0).getContaCorrenteBancaria().getAgencia().getNumeroAgencia(),
                    p.getContaPgto().getContaCorrenteBancPessoas().get(0).getContaCorrenteBancaria().getAgencia().getDigitoVerificador(),
                    p.getContaPgto().getContaCorrenteBancPessoas().get(0).getContaCorrenteBancaria().getNumeroConta(),
                    p.getLiquidacao().getEmpenho().getFornecedor().getNome(), p.getLiquidacao().getEmpenho().getFornecedor().getEnderecoPrincipal().getLogradouro(),
                    p.getLiquidacao().getEmpenho().getFornecedor().getEnderecoPrincipal().getLocalidade(), "0", p.getLiquidacao().getEmpenho().getFornecedor().getEnderecoPrincipal().getCep(),
                    p.getLiquidacao().getEmpenho().getFornecedor().getEnderecoPrincipal().getUf(), p.getComplementoHistorico(), "1",
                    p.getLiquidacao().getEmpenho().getFornecedor() instanceof PessoaFisica ? "2" : "1", p.getLiquidacao().getEmpenho().getFornecedor().getCpf_Cnpj(), "",
                    "", "", "",
                    "10", "Seq");
            listaRegistroObnTipoDois.add(registroObn600TipoDois);
        }
    }

    private void percorreTransferenciasFinanceiras(List<BorderoTransferenciaFinanceira> listaTransferenciaContaFinanceira) {
        for (BorderoTransferenciaFinanceira t : listaTransferenciaContaFinanceira) {
            registroObn600TipoDois = new RegistroObn600TipoDois(null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null);
            listaRegistroObnTipoDois.add(registroObn600TipoDois);
        }
    }

    private void percorreTransferenciasMesmaUnidade(List<BorderoTransferenciaMesmaUnidade> listaTransferenciaMesmaUnidades) {
        for (BorderoTransferenciaMesmaUnidade t : listaTransferenciaMesmaUnidades) {
            registroObn600TipoDois = new RegistroObn600TipoDois(null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null, null,
                    null, null);
            listaRegistroObnTipoDois.add(registroObn600TipoDois);
        }
    }
}
