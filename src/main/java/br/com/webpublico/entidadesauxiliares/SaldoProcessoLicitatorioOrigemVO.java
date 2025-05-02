package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.OrigemSaldoExecucaoProcesso;
import br.com.webpublico.util.Util;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class SaldoProcessoLicitatorioOrigemVO implements Comparable<SaldoProcessoLicitatorioOrigemVO>{

    private Boolean processoIrp;
    private Long idOrigem;
    private Long idFornecedor;
    private String descricaoMovimento;
    private OrigemSaldoExecucaoProcesso origem;
    private NaturezaSaldo natureza;
    private Long criadoEm;
    private List<SaldoProcessoLicitatorioItemVO> itens;

    public SaldoProcessoLicitatorioOrigemVO() {
        itens = Lists.newArrayList();
        processoIrp = false;
        criadoEm = System.nanoTime();
        natureza = NaturezaSaldo.ORIGINAL;
    }

    public NaturezaSaldo getNatureza() {
        return natureza;
    }

    public void setNatureza(NaturezaSaldo natureza) {
        this.natureza = natureza;
    }

    public String getDescricaoMovimento() {
        return descricaoMovimento;
    }

    public void setDescricaoMovimento(String descricaoMovimento) {
        this.descricaoMovimento = descricaoMovimento;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public OrigemSaldoExecucaoProcesso getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemSaldoExecucaoProcesso origem) {
        this.origem = origem;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }


    public boolean hasItens() {
        return !Util.isListNullOrEmpty(itens);
    }

    public List<SaldoProcessoLicitatorioItemVO> getItens() {
        return itens;
    }

    public void setItens(List<SaldoProcessoLicitatorioItemVO> itens) {
        this.itens = itens;
    }

    public Boolean getProcessoIrp() {
        return processoIrp;
    }

    public void setProcessoIrp(Boolean processoIrp) {
        this.processoIrp = processoIrp;
    }

    public BigDecimal getValorOriginalAtaOrDispensa() {
       if (origem.isAta()){
           return getValorOriginalAta();
       }
       return getValorOriginalDispensa();
    }

    public BigDecimal getValorOriginalDispensa() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioItemVO item : itens) {
            if (item.getOrigemSaldo().isDispensa()){
            valorTotal = valorTotal.add(item.getValorlHomologado());
            }
        }
        return valorTotal;
    }

    public BigDecimal getValorOriginalAta() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioItemVO item : itens) {
            if (item.getOrigemSaldo().isAta()) {
                valorTotal = valorTotal.add(item.getValorAta());
            }
        }
        return valorTotal;
    }

    public BigDecimal getValorAtualAta() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioItemVO item : itens) {
            valorTotal = valorTotal.add(item.getValorAta().add(item.getValorAcrescimo()));
        }
        return valorTotal;
    }

    public BigDecimal getValorAcrescimo() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioItemVO item : itens) {
            valorTotal = valorTotal.add(item.getValorAcrescimo());
        }
        return valorTotal;
    }

    public BigDecimal getValorExecutadoSemContrato() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioItemVO item : itens) {
            valorTotal = valorTotal.add(item.getValorExecutado());
        }
        return valorTotal;
    }

    public BigDecimal getValorEstornadoSemContrato() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioItemVO item : itens) {
            valorTotal = valorTotal.add(item.getValorEstornado());
        }
        return valorTotal;
    }

    public BigDecimal getValorContratado() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (SaldoProcessoLicitatorioItemVO item : itens) {
            valorTotal = valorTotal.add(item.getValorContratado());
        }
        return valorTotal;
    }

    public BigDecimal getSaldoDisponivel() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (hasItens()) {
            for (SaldoProcessoLicitatorioItemVO item : itens) {
                saldo = saldo.add(item.getSaldoDisponivel());
            }
        }
        return saldo;
    }

    public BigDecimal getSaldoDisponivelComAcrescimo() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (hasItens()) {
            for (SaldoProcessoLicitatorioItemVO item : itens) {
                saldo = saldo.add(item.getSaldoDisponivelComAcrescimo());
            }
        }
        return saldo;
    }

    public boolean hasSaldoDisponivel(){
        return getSaldoDisponivel().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isAtaIrp() {
        return origem != null && origem.isAta() && processoIrp;
    }

    public enum NaturezaSaldo {
        ORIGINAL("Original"),
        ACRESCIMO("Acréscimo");
        private String descricao;

        NaturezaSaldo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isOriginal() {
            return this.equals(ORIGINAL);
        }

        public boolean isAcrescimo() {
            return this.equals(ACRESCIMO);
        }
    }

    @Override
    public String toString() {
        try {
            return origem.getDescricao() + " - " + descricaoMovimento;
        } catch (Exception e) {
            return origem.getDescricao();
        }
    }

    @Override
    public int compareTo(SaldoProcessoLicitatorioOrigemVO o) {
        return ComparisonChain.start()
            .compare(this.origem.getOrdem(), o.getOrigem().getOrdem())
            .compare(this.idOrigem, o.getIdOrigem())
            .result();
    }
}
