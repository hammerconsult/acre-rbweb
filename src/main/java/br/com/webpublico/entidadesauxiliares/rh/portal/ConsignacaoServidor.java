package br.com.webpublico.entidadesauxiliares.rh.portal;

import br.com.webpublico.entidadesauxiliares.ItemConsignacao;
import br.com.webpublico.enums.TipoDeConsignacao;
import br.com.webpublico.enums.TipoLancamentoFP;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by peixe on 26/10/17.
 */
public class ConsignacaoServidor implements Serializable {

    private BigDecimal totalBaseConsignacao;
    private BigDecimal totalBase5;
    private BigDecimal totalBase10;
    private BigDecimal totalBase20;
    private BigDecimal restante5;
    private BigDecimal restante10;
    private BigDecimal restante20;
    private BigDecimal totalCartao;
    private BigDecimal totalConvenio;
    private BigDecimal totalEmprestimo;
    private BigDecimal cambioEntreConvenioEmprestimo;
    private List<ItemConsignacao> itemConsignacoes;
    private BigDecimal totalBaseEuConsigoMais;
    private BigDecimal valorMargemEuConsigoMais;
    private BigDecimal percentualMargemEuConsigoMais;
    private Integer minimoDiasDireitoEuConsigoMais;

    public ConsignacaoServidor() {
        itemConsignacoes = Lists.newLinkedList();
        totalBase5 = BigDecimal.ZERO;
        totalBase10 = BigDecimal.ZERO;
        totalBase20 = BigDecimal.ZERO;
        restante5 = BigDecimal.ZERO;
        restante10 = BigDecimal.ZERO;
        restante20 = BigDecimal.ZERO;
        totalCartao = BigDecimal.ZERO;
        totalConvenio = BigDecimal.ZERO;
        totalEmprestimo = BigDecimal.ZERO;
        cambioEntreConvenioEmprestimo = BigDecimal.ZERO;
    }

    public BigDecimal getTotalBaseConsignacao() {
        return totalBaseConsignacao;
    }

    public void setTotalBaseConsignacao(BigDecimal totalBaseConsignacao) {
        this.totalBaseConsignacao = totalBaseConsignacao;
    }

    public BigDecimal getTotalBase5() {
        return totalBase5;
    }

    public void setTotalBase5(BigDecimal totalBase5) {
        this.totalBase5 = totalBase5;
    }

    public BigDecimal getTotalBase10() {
        return totalBase10;
    }

    public void setTotalBase10(BigDecimal totalBase10) {
        this.totalBase10 = totalBase10;
    }

    public BigDecimal getTotalBase20() {
        return totalBase20;
    }

    public void setTotalBase20(BigDecimal totalBase20) {
        this.totalBase20 = totalBase20;
    }

    public BigDecimal getRestante5() {
        return restante5;
    }

    public void setRestante5(BigDecimal restante5) {
        this.restante5 = restante5;
    }

    public BigDecimal getRestante10() {
        return restante10;
    }

    public void setRestante10(BigDecimal restante10) {
        this.restante10 = restante10;
    }

    public BigDecimal getRestante20() {
        return restante20;
    }

    public void setRestante20(BigDecimal restante20) {
        this.restante20 = restante20;
    }

    public BigDecimal getTotalCartao() {
        return totalCartao;
    }

    public void setTotalCartao(BigDecimal totalCartao) {
        this.totalCartao = totalCartao;
    }

    public BigDecimal getTotalConvenio() {
        return totalConvenio;
    }

    public void setTotalConvenio(BigDecimal totalConvenio) {
        this.totalConvenio = totalConvenio;
    }

    public BigDecimal getTotalEmprestimo() {
        return totalEmprestimo;
    }

    public void setTotalEmprestimo(BigDecimal totalEmprestimo) {
        this.totalEmprestimo = totalEmprestimo;
    }

    public BigDecimal getCambioEntreConvenioEmprestimo() {
        return cambioEntreConvenioEmprestimo;
    }

    public void setCambioEntreConvenioEmprestimo(BigDecimal cambioEntreConvenioEmprestimo) {
        this.cambioEntreConvenioEmprestimo = cambioEntreConvenioEmprestimo;
    }

    public List<ItemConsignacao> getItemConsignacoes() {
        return itemConsignacoes;
    }

    public void setItemConsignacoes(List<ItemConsignacao> itemConsignacoes) {
        this.itemConsignacoes = itemConsignacoes;
    }

    public BigDecimal getTotalBaseEuConsigoMais() {
        return totalBaseEuConsigoMais;
    }

    public void setTotalBaseEuConsigoMais(BigDecimal totalBaseEuConsigoMais) {
        this.totalBaseEuConsigoMais = totalBaseEuConsigoMais;
    }

    public BigDecimal getValorMargemEuConsigoMais() {
        return valorMargemEuConsigoMais;
    }

    public void setValorMargemEuConsigoMais(BigDecimal valorMargemEuConsigoMais) {
        this.valorMargemEuConsigoMais = valorMargemEuConsigoMais;
    }

    public BigDecimal getPercentualMargemEuConsigoMais() {
        return percentualMargemEuConsigoMais;
    }

    public void setPercentualMargemEuConsigoMais(BigDecimal percentualMargemEuConsigoMais) {
        this.percentualMargemEuConsigoMais = percentualMargemEuConsigoMais;
    }

    public Integer getMinimoDiasDireitoEuConsigoMais() {
        return minimoDiasDireitoEuConsigoMais;
    }

    public void setMinimoDiasDireitoEuConsigoMais(Integer minimoDiasDireitoEuConsigoMais) {
        this.minimoDiasDireitoEuConsigoMais = minimoDiasDireitoEuConsigoMais;
    }

    public void preencherValoresDoPercentualDaConsignacao() {
        if (totalBaseConsignacao != null && totalBaseConsignacao.compareTo(BigDecimal.ZERO) > 0) {
            totalBase5 = totalBaseConsignacao.multiply(TipoDeConsignacao.CARTAO.getPercentualDecimal().divide(BigDecimal.valueOf(100)));
            totalBase10 = totalBaseConsignacao.multiply(TipoDeConsignacao.CONVENIO.getPercentualDecimal().divide(BigDecimal.valueOf(100)));
            totalBase20 = totalBaseConsignacao.multiply(TipoDeConsignacao.EMPRESTIMO.getPercentualDecimal().divide(BigDecimal.valueOf(100)));
        }
    }

    public void preencherValoresParaDescontados() {
        for (ItemConsignacao itemConsignacao : itemConsignacoes) {
            if (TipoLancamentoFP.VALOR.equals(itemConsignacao.getLancamentoFP().getTipoLancamentoFP())) {
                if (TipoDeConsignacao.CARTAO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                    totalCartao = totalCartao.add(itemConsignacao.getLancamentoFP().getQuantificacao());
                }
                if (TipoDeConsignacao.CONVENIO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                    totalConvenio = totalConvenio.add(itemConsignacao.getLancamentoFP().getQuantificacao());
                }
                if (TipoDeConsignacao.EMPRESTIMO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                    totalEmprestimo = totalEmprestimo.add(itemConsignacao.getLancamentoFP().getQuantificacao());
                }
            } else {
                if (itemConsignacao.getItemFichaFinanceiraFP() != null) {
                    if (TipoDeConsignacao.CARTAO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                        totalCartao = totalCartao.add(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                    }
                    if (TipoDeConsignacao.CONVENIO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                        totalConvenio = totalConvenio.add(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                    }
                    if (TipoDeConsignacao.EMPRESTIMO.equals(itemConsignacao.getLancamentoFP().getEventoFP().getTipoDeConsignacao())) {
                        totalEmprestimo = totalEmprestimo.add(itemConsignacao.getItemFichaFinanceiraFP().getValor());
                    }
                }
            }
        }
    }

    public ConsignacaoServidorDTO toConsignacaoServidorDTO() {
        ConsignacaoServidorDTO dto = new ConsignacaoServidorDTO();
        dto.setTotalBaseConsignacao(totalBaseConsignacao);
        dto.setTotalBase5(totalBase5);
        dto.setTotalBase10(totalBase10);
        dto.setTotalBase20(totalBase20);
        dto.setRestante5(restante5);
        dto.setRestante10(restante10);
        dto.setRestante20(restante20);
        dto.setTotalCartao(totalCartao);
        dto.setTotalConvenio(totalConvenio);
        dto.setTotalEmprestimo(totalEmprestimo);
        dto.setCambioEntreConvenioEmprestimo(cambioEntreConvenioEmprestimo);
        dto.setItemConsignacoes(transformToItensDTO());
        dto.setTotalBaseEuConsigoMais(totalBaseEuConsigoMais);
        dto.setValorMargemEuConsigoMais(valorMargemEuConsigoMais);
        dto.setPercentualMargemEuConsigoMais(percentualMargemEuConsigoMais);
        dto.setMinimoDiasDireitoEuConsigoMais(minimoDiasDireitoEuConsigoMais);
        return dto;
    }

    public List<ItemConsignacaoDTO> transformToItensDTO() {
        List<ItemConsignacaoDTO> dtos = Lists.newLinkedList();
        for (ItemConsignacao itemConsignacoe : itemConsignacoes) {
            ItemConsignacaoDTO item = new ItemConsignacaoDTO();
            item.setDescricao(itemConsignacoe.getLancamentoFP().getEventoFP().toString());
            item.setInicioVigencia(itemConsignacoe.getLancamentoFP().getInicioVigencia());
            item.setFinalVigencia(itemConsignacoe.getLancamentoFP().getFinalVigencia());
            item.setQuantificacao(itemConsignacoe.getLancamentoFP().getQuantificacao());
            item.setTipoDeConsignacao(itemConsignacoe.getLancamentoFP().getEventoFP().getTipoDeConsignacao());
            item.setDataCadastroEconsig(itemConsignacoe.getLancamentoFP().getDataCadastroEconsig());
            item.setValorDescontado(itemConsignacoe.getItemFichaFinanceiraFP() != null ? itemConsignacoe.getItemFichaFinanceiraFP().getValor() : BigDecimal.ZERO);
            item.setMotivoRejeicao(itemConsignacoe.getLancamentoFP().getMotivoRejeicao() != null ? itemConsignacoe.getLancamentoFP().getMotivoRejeicao().toString() : null);
            dtos.add(item);
        }
        return dtos;
    }

}
