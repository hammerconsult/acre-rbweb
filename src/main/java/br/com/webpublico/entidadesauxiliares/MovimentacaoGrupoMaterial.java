package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.webreportdto.dto.administrativo.MovimentacaoGrupoMaterialDTO;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class MovimentacaoGrupoMaterial {

    private GrupoMaterial grupoMaterial;
    private BigDecimal saldoEstoqueAtual;
    private BigDecimal saldoEstoqueAnterior;
    private BigDecimal saldoAtualGrupoContabil;
    private BigDecimal saldoAnteriorGrupoContabil;
    private List<MovimentoEstoqueContabil> entradasEstoque;
    private List<MovimentoEstoqueContabil> saidasEstoque;
    private List<MovimentoEstoqueContabil> movimentosEstoque;

    private List<MovimentoEstoqueContabil> entradasContabil;
    private List<MovimentoEstoqueContabil> saidasContabil;
    private List<MovimentoEstoqueContabil> movimentosEntradaContabil;
    private List<MovimentoEstoqueContabil> movimentosSaidaContabil;
    private List<MovimentoEstoqueContabil> movimentosGrupoMaterialContabil;

    public MovimentacaoGrupoMaterial() {
        entradasEstoque = Lists.newArrayList();
        saidasEstoque = Lists.newArrayList();
        entradasContabil = Lists.newArrayList();
        saidasContabil = Lists.newArrayList();
        movimentosEstoque = Lists.newArrayList();
        movimentosGrupoMaterialContabil = Lists.newArrayList();
        movimentosEntradaContabil = Lists.newArrayList();
        movimentosSaidaContabil = Lists.newArrayList();
        saldoEstoqueAtual = BigDecimal.ZERO;
        saldoAtualGrupoContabil = BigDecimal.ZERO;
        saldoAnteriorGrupoContabil = BigDecimal.ZERO;
    }

    public static List<MovimentacaoGrupoMaterialDTO> getMovimentosToDto(List<MovimentacaoGrupoMaterial> movimentos) {
        List<MovimentacaoGrupoMaterialDTO> retorno = Lists.newArrayList();
        if (!movimentos.isEmpty()) {
            for (MovimentacaoGrupoMaterial movimento : movimentos) {
                MovimentacaoGrupoMaterialDTO dto = new MovimentacaoGrupoMaterialDTO();
                dto.setCodigoGrupoMaterial(movimento.getGrupoMaterial().getCodigo());
                dto.setDescricaoGrupoMaterial(movimento.getGrupoMaterial().getDescricao());
                dto.setSaldoEstoqueAtual(movimento.getSaldoEstoqueAtual());
                dto.setSaldoAtualGrupoContabil(movimento.getSaldoAtualGrupoContabil());
                dto.setTotalEntradaEstoque(movimento.getTotalEntradaEstoque());
                dto.setTotalSaidaEstoque(movimento.getTotalSaidaEstoque());
                dto.setTotalEntradasContabil(movimento.getTotalEntradaContabil());
                dto.setTotalSaidaContabil(movimento.getTotalSaidaContabil());
                dto.setTotalDiferencaEntrada(movimento.getTotalDiferencaEntrada());
                dto.setTotalDiferencaSaida(movimento.getTotalDiferencaSaida());
                dto.setTotalDiferencaConciliacao(movimento.getTotalDiferencaConciliacao());
                retorno.add(dto);
            }
        }
        return retorno;
    }

    public List<MovimentoEstoqueContabil> getMovimentosSaidaContabil() {
        return movimentosSaidaContabil;
    }

    public void setMovimentosSaidaContabil(List<MovimentoEstoqueContabil> movimentosSaidaContabil) {
        this.movimentosSaidaContabil = movimentosSaidaContabil;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public BigDecimal getSaldoEstoqueAtual() {
        return saldoEstoqueAtual;
    }

    public void setSaldoEstoqueAtual(BigDecimal saldoEstoqueAtual) {
        this.saldoEstoqueAtual = saldoEstoqueAtual;
    }

    public BigDecimal getSaldoEstoqueAnterior() {
        return saldoEstoqueAnterior;
    }

    public void setSaldoEstoqueAnterior(BigDecimal saldoEstoqueAnterior) {
        this.saldoEstoqueAnterior = saldoEstoqueAnterior;
    }

    public BigDecimal getSaldoAtualGrupoContabil() {
        return saldoAtualGrupoContabil;
    }

    public void setSaldoAtualGrupoContabil(BigDecimal saldoAtualGrupoContabil) {
        this.saldoAtualGrupoContabil = saldoAtualGrupoContabil;
    }

    public BigDecimal getSaldoAnteriorGrupoContabil() {
        return saldoAnteriorGrupoContabil;
    }

    public void setSaldoAnteriorGrupoContabil(BigDecimal saldoAnteriorGrupoContabil) {
        this.saldoAnteriorGrupoContabil = saldoAnteriorGrupoContabil;
    }

    public List<MovimentoEstoqueContabil> getEntradasEstoque() {
        return entradasEstoque;
    }

    public void setEntradasEstoque(List<MovimentoEstoqueContabil> entradasEstoque) {
        this.entradasEstoque = entradasEstoque;
    }

    public List<MovimentoEstoqueContabil> getSaidasEstoque() {
        return saidasEstoque;
    }

    public void setSaidasEstoque(List<MovimentoEstoqueContabil> saidasEstoque) {
        this.saidasEstoque = saidasEstoque;
    }

    public List<MovimentoEstoqueContabil> getMovimentosEstoque() {
        return movimentosEstoque;
    }

    public void setMovimentosEstoque(List<MovimentoEstoqueContabil> movimentosEstoque) {
        this.movimentosEstoque = movimentosEstoque;
    }

    public List<MovimentoEstoqueContabil> getEntradasContabil() {
        return entradasContabil;
    }

    public void setEntradasContabil(List<MovimentoEstoqueContabil> entradasContabil) {
        this.entradasContabil = entradasContabil;
    }

    public List<MovimentoEstoqueContabil> getSaidasContabil() {
        return saidasContabil;
    }

    public void setSaidasContabil(List<MovimentoEstoqueContabil> saidasContabil) {
        this.saidasContabil = saidasContabil;
    }

    public List<MovimentoEstoqueContabil> getMovimentosGrupoMaterialContabil() {
        return movimentosGrupoMaterialContabil;
    }

    public void setMovimentosGrupoMaterialContabil(List<MovimentoEstoqueContabil> movimentosGrupoMaterialContabil) {
        this.movimentosGrupoMaterialContabil = movimentosGrupoMaterialContabil;
    }

    public List<MovimentoEstoqueContabil> getMovimentosEntradaContabil() {
        return movimentosEntradaContabil;
    }

    public void setMovimentosEntradaContabil(List<MovimentoEstoqueContabil> movimentosEntradaContabil) {
        this.movimentosEntradaContabil = movimentosEntradaContabil;
    }

    public boolean hasEntradasContabil() {
        return entradasContabil != null && !entradasContabil.isEmpty();
    }

    public boolean hasSaidasContabil() {
        return saidasContabil != null && !saidasContabil.isEmpty();
    }

    public boolean hasMovimentosEstoque() {
        return movimentosEstoque != null && !movimentosEstoque.isEmpty();
    }

    public boolean hasMovimentosGrupoMaterialContabil() {
        return movimentosGrupoMaterialContabil != null && !movimentosGrupoMaterialContabil.isEmpty();
    }

    public String getSiglaCreditoDebito(BigDecimal valor) {
        return valor.compareTo(BigDecimal.ZERO) >= 0 ? "C" : "D";
    }

    public BigDecimal getTotalEntradaEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentosEstoque()) {
            for (MovimentoEstoqueContabil entrada : entradasEstoque) {
                total = total.add(entrada.getValor());
            }
        }
        return total;
    }

    public BigDecimal getTotalSaidaEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentosEstoque()) {
            for (MovimentoEstoqueContabil saida : saidasEstoque) {
                total = total.add(saida.getValor());
            }
        }
        return total;
    }

    public BigDecimal getTotalEstoqueCalculado() {
        try {
            return getTotalEntradaEstoque().subtract(getTotalSaidaEstoque()).add(getSaldoEstoqueAnterior());
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getDiferencaEntradaSaidaComEstoque() {
        try {
            return getTotalEstoqueCalculado().subtract(getSaldoEstoqueAtual());
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSaldoAtualMovimentoEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentosEstoque()) {
            for (MovimentoEstoqueContabil movEstoque : movimentosEstoque) {
                total = total.add(movEstoque.getCredito().subtract(movEstoque.getDebito()));
            }
        }
        return total.add(getSaldoEstoqueAnterior());
    }

    public BigDecimal getTotalCreditoMovimentosEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentosEstoque()) {
            for (MovimentoEstoqueContabil movEstoque : movimentosEstoque) {
                total = total.add(movEstoque.getCredito());
            }
        }
        return total;
    }

    public BigDecimal getTotalDebitoMovimentosEstoque() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentosEstoque()) {
            for (MovimentoEstoqueContabil movEstoque : movimentosEstoque) {
                total = total.add(movEstoque.getDebito());
            }
        }
        return total;
    }

    public BigDecimal getTotalMovimentosEntradaContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasEntradasContabil()) {
            for (MovimentoEstoqueContabil movEntrada : getMovimentosEntradaContabil()) {
                total = total.add(movEntrada.getDebito());
            }
        }
        return total;
    }

    public BigDecimal getTotalMovimentosSaidaContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasEntradasContabil()) {
            for (MovimentoEstoqueContabil movEntrada : getMovimentosSaidaContabil()) {
                total = total.add(movEntrada.getCredito());
            }
        }
        return total;
    }

    public BigDecimal getTotalSaidaContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasSaidasContabil()) {
            for (MovimentoEstoqueContabil movSaida : saidasContabil) {
                total = total.add(movSaida.getValor());
            }
        }
        return total;
    }

    public BigDecimal getSaldoMovimentosGrupoMaterialContabil() {
        boolean isDebito = getSaldoAnteriorGrupoContabil().compareTo(BigDecimal.ZERO) < 0;
        if (isDebito) {
            return getSaldoAnteriorGrupoContabil().abs().add(getTotalDebitoMovimentosGrupoMaterialContabil()).subtract(getTotalCreditoMovimentosGrupoMaterialContabil());
        }
        return getSaldoAnteriorGrupoContabil().add(getTotalCreditoMovimentosGrupoMaterialContabil()).subtract(getTotalDebitoMovimentosGrupoMaterialContabil());
    }

    public BigDecimal getTotalCreditoMovimentosGrupoMaterialContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentosGrupoMaterialContabil()) {
            for (MovimentoEstoqueContabil movGrupo : movimentosGrupoMaterialContabil) {
                total = total.add(movGrupo.getCredito());
            }
        }
        return total;
    }

    public BigDecimal getTotalDebitoMovimentosGrupoMaterialContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentosGrupoMaterialContabil()) {
            for (MovimentoEstoqueContabil movGrupo : movimentosGrupoMaterialContabil) {
                total = total.add(movGrupo.getDebito());
            }
        }
        return total;
    }

    public BigDecimal getTotalEntradaContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentosGrupoMaterialContabil()) {
            for (MovimentoEstoqueContabil movGrupo : entradasContabil) {
                total = total.add(movGrupo.getTipoLancamento().isEstorno()
                    ? movGrupo.getValor().multiply(new BigDecimal(-1))
                    : movGrupo.getValor());
            }
        }
        return total;
    }

    public BigDecimal getTotalDiferencaEntrada() {
        try {
            return getTotalEntradaContabil().subtract(getTotalEntradaEstoque());
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalDiferencaSaida() {
        try {
            return getTotalSaidaContabil().subtract(getTotalSaidaEstoque());
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalDiferencaConciliacao() {
        return getSaldoAtualGrupoContabil().subtract(getSaldoEstoqueAtual().multiply(new BigDecimal("-1")));
    }

    public boolean isGrupoConciliado() {//renomear
        return getTotalDiferencaConciliacao().compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isGrupoSemDiferencaEstoqueCalculado() {
        return getDiferencaEntradaSaidaComEstoque().compareTo(BigDecimal.ZERO) == 0;
    }

    public String getStyleRow() {
        if (isGrupoConciliado()) {
            return "verdenegrito";
        }
        return "vermelhonegrito";
    }
}
