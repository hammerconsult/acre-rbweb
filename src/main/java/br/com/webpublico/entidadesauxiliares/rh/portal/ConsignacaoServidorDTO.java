package br.com.webpublico.entidadesauxiliares.rh.portal;

import br.com.webpublico.enums.TipoDeConsignacao;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by peixe on 26/10/17.
 */
public class ConsignacaoServidorDTO implements Serializable {

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
    private List<ItemConsignacaoDTO> itemConsignacoes;
    private BigDecimal totalBaseEuConsigoMais;
    private BigDecimal valorMargemEuConsigoMais;
    private BigDecimal percentualMargemEuConsigoMais;
    private Integer minimoDiasDireitoEuConsigoMais;

    public ConsignacaoServidorDTO() {
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

    public List<ItemConsignacaoDTO> getItemConsignacoes() {
        return itemConsignacoes;
    }

    public void setItemConsignacoes(List<ItemConsignacaoDTO> itemConsignacoes) {
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
        if (totalBaseConsignacao != null && totalBaseConsignacao.compareTo(BigDecimal.ZERO) != 0) {
            totalBase5 = totalBaseConsignacao.multiply(TipoDeConsignacao.CARTAO.getPercentualDecimal());
            totalBase10 = totalBaseConsignacao.multiply(TipoDeConsignacao.CONVENIO.getPercentualDecimal());
            totalBase20 = totalBaseConsignacao.multiply(TipoDeConsignacao.EMPRESTIMO.getPercentualDecimal());
        }
    }
}
