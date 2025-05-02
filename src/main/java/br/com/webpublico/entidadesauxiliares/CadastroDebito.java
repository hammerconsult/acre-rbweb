package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class CadastroDebito {

    private String cadastro;
    private List<CDAResultadoParcela> debitos;

    public CadastroDebito() {
        debitos = Lists.newArrayList();
    }

    public String getCadastro() {
        return cadastro;
    }

    public void setCadastro(String cadastro) {
        this.cadastro = cadastro;
    }

    public List<CDAResultadoParcela> getDebitos() {
        return debitos;
    }

    public void setDebitos(List<CDAResultadoParcela> debitos) {
        this.debitos = debitos;
    }

    public BigDecimal getValorImpostoSelecionado() {
        return this.debitos.stream().filter(CDAResultadoParcela::isSelecionado)
            .map(CDAResultadoParcela::getResultadoParcela)
            .map(ResultadoParcela::getValorImposto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorTaxaSelecionado() {
        return this.debitos.stream().filter(CDAResultadoParcela::isSelecionado)
            .map(CDAResultadoParcela::getResultadoParcela)
            .map(ResultadoParcela::getValorTaxa)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorDescontoSelecionado() {
        return this.debitos.stream().filter(CDAResultadoParcela::isSelecionado)
            .map(CDAResultadoParcela::getResultadoParcela)
            .map(ResultadoParcela::getValorDesconto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorJurosSelecionado() {
        return this.debitos.stream().filter(CDAResultadoParcela::isSelecionado)
            .map(CDAResultadoParcela::getResultadoParcela)
            .map(ResultadoParcela::getValorJuros)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorMultaSelecionado() {
        return this.debitos.stream().filter(CDAResultadoParcela::isSelecionado)
            .map(CDAResultadoParcela::getResultadoParcela)
            .map(ResultadoParcela::getValorMulta)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorHonorariosSelecionado() {
        return this.debitos.stream().filter(CDAResultadoParcela::isSelecionado)
            .map(CDAResultadoParcela::getResultadoParcela)
            .map(ResultadoParcela::getValorHonorarios)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorCorrecaoSelecionado() {
        return this.debitos.stream().filter(CDAResultadoParcela::isSelecionado)
            .map(CDAResultadoParcela::getResultadoParcela)
            .map(ResultadoParcela::getValorCorrecao)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorTotalSelecionado() {
        return this.debitos.stream().filter(CDAResultadoParcela::isSelecionado)
            .map(CDAResultadoParcela::getResultadoParcela)
            .map(ResultadoParcela::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
