package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class FornecedorAtaRegistroPreco {

    private Pessoa fornecedor;
    private List<SaldoProcessoLicitatorioItemVO> itens;

    public FornecedorAtaRegistroPreco() {
        itens = Lists.newArrayList();
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public List<SaldoProcessoLicitatorioItemVO> getItens() {
        return itens;
    }

    public void setItens(List<SaldoProcessoLicitatorioItemVO> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorTotalAta() {
        BigDecimal total = BigDecimal.ZERO;
        if (!itens.isEmpty()) {
            for (SaldoProcessoLicitatorioItemVO item : itens) {
                total = total.add(item.getValorAta());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalContratado() {
        BigDecimal total = BigDecimal.ZERO;
        if (!itens.isEmpty()) {
            for (SaldoProcessoLicitatorioItemVO item : itens) {
                total = total.add(item.getValorContratado());
            }
        }
        return total;
    }
}
