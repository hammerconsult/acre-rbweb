/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidadesauxiliares.contabil.MovimentoDespesaORCVO;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.enums.TipoReservaSolicitacaoCompra;
import br.com.webpublico.util.Util;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DotacaoSolicitacaoMaterialItemFonteVO implements Serializable, Comparable<DotacaoSolicitacaoMaterialItemFonteVO> {

    private Long id;
    private Long idOrigem;
    private Date dataLancamento;
    private FonteDespesaORC fonteDespesaORC;
    private TipoOperacaoORC tipoOperacao;
    private TipoReservaSolicitacaoCompra tipoReserva;
    private DotacaoProcessoCompraVO dotacaoProcessoCompraVO;
    private BigDecimal valorReservado;
    private BigDecimal saldoEstornoReservado;
    private BigDecimal saldoNormalReservado;
    private Exercicio exercicio;
    private Boolean fonteAgrupadora;
    private Boolean fonteJaExecutada;
    private Long criadoEm;
    private List<MovimentoDespesaORCVO> movimentosDespesarOrc;

    public DotacaoSolicitacaoMaterialItemFonteVO() {
        this.dataLancamento = LocalDateTime.now().toDate();
        this.valorReservado = BigDecimal.ZERO;
        this.saldoEstornoReservado = BigDecimal.ZERO;
        this.saldoNormalReservado = BigDecimal.ZERO;
        this.tipoOperacao = TipoOperacaoORC.NORMAL;
        this.tipoReserva = TipoReservaSolicitacaoCompra.SOLICITACAO_COMPRA;
        this.movimentosDespesarOrc = Lists.newArrayList();
        this.fonteAgrupadora = true;
        this.fonteJaExecutada = false;
        this.criadoEm = System.nanoTime();
    }

    public DotacaoProcessoCompraVO getDotacaoProcessoCompraVO() {
        return dotacaoProcessoCompraVO;
    }

    public void setDotacaoProcessoCompraVO(DotacaoProcessoCompraVO dotacaoProcessoCompraVO) {
        this.dotacaoProcessoCompraVO = dotacaoProcessoCompraVO;
    }

    public Boolean getFonteJaExecutada() {
        return fonteJaExecutada;
    }

    public void setFonteJaExecutada(Boolean fonteJaExecutada) {
        this.fonteJaExecutada = fonteJaExecutada;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getFonteAgrupadora() {
        return fonteAgrupadora;
    }

    public void setFonteAgrupadora(Boolean fonteAgrupadora) {
        this.fonteAgrupadora = fonteAgrupadora;
    }

    public List<MovimentoDespesaORCVO> getMovimentosDespesarOrc() {
        return movimentosDespesarOrc;
    }

    public void setMovimentosDespesarOrc(List<MovimentoDespesaORCVO> movimentosDespesarOrc) {
        this.movimentosDespesarOrc = movimentosDespesarOrc;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public BigDecimal getValorReservado() {
        return valorReservado;
    }

    public void setValorReservado(BigDecimal valorReservado) {
        this.valorReservado = valorReservado;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoOperacaoORC getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoORC tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public TipoReservaSolicitacaoCompra getTipoReserva() {
        return tipoReserva;
    }

    public void setTipoReserva(TipoReservaSolicitacaoCompra tipoReserva) {
        this.tipoReserva = tipoReserva;
    }

    public BigDecimal getValorExecutado() {
        if (dotacaoProcessoCompraVO != null && fonteAgrupadora) {
            return dotacaoProcessoCompraVO.getValorExecutadoReservaInicial().add(dotacaoProcessoCompraVO.getValorExecutadoReservaExecucao());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorEstornoReservado() {
        if (dotacaoProcessoCompraVO != null) {
            return dotacaoProcessoCompraVO.getValorEstornoReservado();
        }
        return BigDecimal.ZERO;
    }


    public BigDecimal getValorEstornoExecutado() {
        if (dotacaoProcessoCompraVO != null) {
            return dotacaoProcessoCompraVO.getValorEstornoExecutadoReservaExecucao().add(dotacaoProcessoCompraVO.getValorEstornoExecutadoReservaInicial());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSaldoEstornoReservado() {
        return saldoEstornoReservado;
    }

    public void setSaldoEstornoReservado(BigDecimal saldoEstornoReservado) {
        this.saldoEstornoReservado = saldoEstornoReservado;
    }

    public BigDecimal getSaldoNormalReservado() {
        return saldoNormalReservado;
    }

    public void setSaldoNormalReservado(BigDecimal saldoNormalReservado) {
        this.saldoNormalReservado = saldoNormalReservado;
    }

    public BigDecimal getSaldoEstornoReservadoCalculado() {
        return valorReservado.subtract(dotacaoProcessoCompraVO.getValorEstornoReservado())
            .subtract(dotacaoProcessoCompraVO.getValorExecutadoReservaInicial())
            .add(dotacaoProcessoCompraVO.getValorEstornoExecutadoReservaInicial());
    }

    public BigDecimal getSaldoAEmpenhar() {
        return getValorTotalReservado().subtract(getValorTotalEmpenhadoLiquido()).subtract(getValorTotalEstornoReservado());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DotacaoSolicitacaoMaterialItemFonteVO that = (DotacaoSolicitacaoMaterialItemFonteVO) object;
        return Objects.equals(criadoEm, that.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(criadoEm);
    }

    @Override
    public int compareTo(DotacaoSolicitacaoMaterialItemFonteVO dotFonte) {
        if (dotFonte.getFonteDespesaORC() != null && getFonteDespesaORC() != null) {
            return ComparisonChain.start()
                .compare(getFonteDespesaORC().getDespesaORC().getCodigo(), dotFonte.getFonteDespesaORC().getDespesaORC().getCodigo())
                .compare(getDataLancamento(), dotFonte.getDataLancamento())
                .result();
        }
        return 0;
    }

    public boolean hasMovimentosDespOrc() {
        return !Util.isListNullOrEmpty(movimentosDespesarOrc);
    }

    public BigDecimal getValorTotalReservado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasMovimentosDespOrc()) {
            for (MovimentoDespesaORCVO mov : movimentosDespesarOrc) {
                valor = valor.add(mov.getValorReservadoPorLicitacao());
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalEstornoReservado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasMovimentosDespOrc()) {
            for (MovimentoDespesaORCVO mov : movimentosDespesarOrc) {
                valor = valor.add(mov.getValorReservadoPorLicitacaoEstorno());
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalEmpenhado() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasMovimentosDespOrc()) {
            for (MovimentoDespesaORCVO mov : movimentosDespesarOrc) {
                valor = valor.add(mov.getValorEmpenhado());
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalEmpenhadoEstorno() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasMovimentosDespOrc()) {
            for (MovimentoDespesaORCVO mov : movimentosDespesarOrc) {
                valor = valor.add(mov.getValorEmpenhadoEstorno());
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalReservadoLiquido() {
        return getValorTotalReservado().subtract(getValorTotalEstornoReservado());
    }

    public BigDecimal getValorTotalEmpenhadoLiquido() {
        return getValorTotalEmpenhado().subtract(getValorTotalEmpenhadoEstorno());
    }

    public String getCorLinhaMovDespOrcReservado() {
        return "verdeescuro";
    }

    public String getCorLinhaMovDespOrcEstorno() {
        return "vermelhoescuro";
    }

    public String getCorLinhaMovDespOrcEmpenhado() {
        return "azulnegrito";
    }

    public String getCorLinhaMovDespOrc(MovimentoDespesaORCVO movVO) {
        String cor = "";
        if (movVO.isReservadoPorLicitacao()) {
            cor = getCorLinhaMovDespOrcReservado();
        }
        if (movVO.isEmpenhado()) {
            cor = getCorLinhaMovDespOrcEmpenhado();
        }
        if (movVO.isReservadoPorLicitacaoEstorno() || movVO.isEmpenhadoEstorno()) {
            cor = getCorLinhaMovDespOrcEstorno();
        }
        return cor;
    }
}
