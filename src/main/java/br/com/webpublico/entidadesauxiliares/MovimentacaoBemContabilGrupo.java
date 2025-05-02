package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class MovimentacaoBemContabilGrupo {

    private GrupoBem grupoBem;
    private BigDecimal valorOriginalContabil;
    private BigDecimal valorAjusteContabil;
    private BigDecimal valorOriginalAdm;
    private BigDecimal valorAjusteAdm;
    private BigDecimal saldoGrupo;
    private TipoOperacao tipoOperacao;
    private List<MovimentacaoBemContabilMovimento> movimentos;

    public MovimentacaoBemContabilGrupo() {
        valorOriginalContabil = BigDecimal.ZERO;
        valorAjusteContabil = BigDecimal.ZERO;
        saldoGrupo = BigDecimal.ZERO;
        valorOriginalAdm = BigDecimal.ZERO;
        valorAjusteAdm = BigDecimal.ZERO;
        movimentos = Lists.newArrayList();
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public BigDecimal getValorOriginalContabil() {
        return valorOriginalContabil;
    }

    public void setValorOriginalContabil(BigDecimal valorOriginalContabil) {
        this.valorOriginalContabil = valorOriginalContabil;
    }

    public BigDecimal getValorAjusteContabil() {
        return valorAjusteContabil;
    }

    public void setValorAjusteContabil(BigDecimal valorAjusteContabil) {
        this.valorAjusteContabil = valorAjusteContabil;
    }

    public BigDecimal getValorAtualContabil() {
        return getValorOriginalContabil().subtract(getValorAjusteContabil());
    }


    public BigDecimal getValorOriginalAdm() {
        return valorOriginalAdm;
    }

    public void setValorOriginalAdm(BigDecimal valorOriginalAdm) {
        this.valorOriginalAdm = valorOriginalAdm;
    }

    public BigDecimal getValorAjusteAdm() {
        return valorAjusteAdm;
    }

    public void setValorAjusteAdm(BigDecimal valorAjusteAdm) {
        this.valorAjusteAdm = valorAjusteAdm;
    }

    public BigDecimal getValorAtualAdm() {
        return getValorOriginalAdm().subtract(getValorAjusteAdm());
    }

    public BigDecimal getValorOriginalConciliacao() {
        return getValorOriginalContabil().subtract(getValorOriginalAdm());
    }

    public BigDecimal getValorAjusteConciliacao() {
        return getValorAjusteContabil().subtract(getValorAjusteAdm());
    }

    public BigDecimal getValorAtualConciliacao() {
        return getValorAtualContabil().subtract(getValorAtualAdm());
    }

    public List<MovimentacaoBemContabilMovimento> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<MovimentacaoBemContabilMovimento> movimentos) {
        this.movimentos = movimentos;
    }

    public BigDecimal getSaldoGrupo() {
        return saldoGrupo;
    }

    public void setSaldoGrupo(BigDecimal saldoGrupo) {
        this.saldoGrupo = saldoGrupo;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacao tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public boolean isGrupoConciliado() {
        return getValorAtualConciliacao().compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean hasMovimentos() {
        return !Util.isListNullOrEmpty(movimentos);
    }

    public BigDecimal getValorTotalCredito() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentos()) {
            for (MovimentacaoBemContabilMovimento mov : movimentos) {
                total = total.add(mov.getCredito());
            }
        }
        return total.abs();
    }

    public BigDecimal getValorTotalDebito() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentos()) {
            for (MovimentacaoBemContabilMovimento mov : movimentos) {
                total = total.add(mov.getDebito());
            }
        }
        return total.abs();
    }

    public BigDecimal getValorTotalAtual() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentos()) {
            for (MovimentacaoBemContabilMovimento mov : movimentos) {
                total = total.add(mov.getValorAtual());
            }
        }
        return total.abs();
    }

    public String getStyleRowConciliado() {
        if (isGrupoConciliado()) {
            return "verdenegrito";
        }
        return "vermelhonegrito";
    }

    public String getCorDiv() {
        if (tipoOperacao.isContabil()) {
            return "alert alert-info";
        }
        return "alert alert-success";
    }

    public String getCorTitulo() {
        if (tipoOperacao.isContabil()) {
            return "label label-info";
        }
        return "label label-success";
    }

    public enum TipoOperacao {
        CONTABIL_ORIGINAL("Contábil - Original"),
        CONTABIL_AJUSTE("Contábil - Ajuste"),
        CONTABIL_CONCILIACAO("Contábil - Conciliação"),
        ADMINISTRATIVO_ORIGINAL("Administrativo - Original"),
        ADMINISTRATIVO_AJUSTE("Administrativo - Ajuste"),
        ADMINISTRATIVO_CONCILIACAO("Administrativo - Conciliação");
        private String descricao;

        TipoOperacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isContabilOriginal() {
            return TipoOperacao.CONTABIL_ORIGINAL.equals(this);
        }

        public boolean isContabilAjuste() {
            return TipoOperacao.CONTABIL_AJUSTE.equals(this);
        }

        public boolean isContabilConciliacao() {
            return TipoOperacao.CONTABIL_CONCILIACAO.equals(this);
        }

        public boolean isContabil() {
            return isContabilOriginal() || isContabilAjuste() || isContabilConciliacao();
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    @Override
    public String toString() {
        return grupoBem.toString();
    }
}
