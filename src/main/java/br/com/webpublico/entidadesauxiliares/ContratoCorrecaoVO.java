package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ContratoCorrecaoVO {

    private Contrato contrato;
    private ExecucaoContratoCorrecaoOrigemVO origemVOSelecionada;
    private List<ExecucaoContratoCorrecaoVO> execucoesCorrigidas;
    private List<ExecucaoContratoCorrecaoVO> execucoesCorrecao;
    private List<ExecucaoContratoCorrecaoOrigemVO> origensExecucao;

    public ContratoCorrecaoVO() {
        origensExecucao = Lists.newArrayList();
        execucoesCorrecao = Lists.newArrayList();
        execucoesCorrigidas = Lists.newArrayList();
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }


    public List<ExecucaoContratoCorrecaoOrigemVO> getOrigensExecucao() {
        return origensExecucao;
    }

    public void setOrigensExecucao(List<ExecucaoContratoCorrecaoOrigemVO> origensExecucao) {
        this.origensExecucao = origensExecucao;
    }

    public List<ExecucaoContratoCorrecaoVO> getExecucoesCorrecao() {
        return execucoesCorrecao;
    }

    public void setExecucoesCorrecao(List<ExecucaoContratoCorrecaoVO> execucoesCorrecao) {
        this.execucoesCorrecao = execucoesCorrecao;
    }

    public List<ExecucaoContratoCorrecaoVO> getExecucoesCorrigidas() {
        return execucoesCorrigidas;
    }

    public void setExecucoesCorrigidas(List<ExecucaoContratoCorrecaoVO> execucoesCorrigidas) {
        this.execucoesCorrigidas = execucoesCorrigidas;
    }

    public ExecucaoContratoCorrecaoOrigemVO getOrigemVOSelecionada() {
        return origemVOSelecionada;
    }

    public void setOrigemVOSelecionada(ExecucaoContratoCorrecaoOrigemVO origemVOSelecionada) {
        this.origemVOSelecionada = origemVOSelecionada;
    }

    public BigDecimal getValorTotalExecucoes(ContratoCorrecaoVO contVO) {
        BigDecimal valorTotalExecucao = BigDecimal.ZERO;
        for (ExecucaoContratoCorrecaoVO execVO : contVO.getExecucoesCorrecao()) {
            valorTotalExecucao = valorTotalExecucao.add(execVO.getExecucaoContrato().getValor());
        }
        return valorTotalExecucao;
    }

    public BigDecimal getValorTotalExecucoesSelecionadas() {
        BigDecimal valorTotalExecucao = BigDecimal.ZERO;
        for (ExecucaoContratoCorrecaoVO execVO : getExecucoesCorrecao()) {
            if (execVO.getSelecionada()) {
                valorTotalExecucao = valorTotalExecucao.add(execVO.getExecucaoContrato().getValor());
            }
        }
        return valorTotalExecucao;
    }

    public BigDecimal getValorTotalLiquidadoExecucoesSelecionadas() {
        BigDecimal valorTotalExecucao = BigDecimal.ZERO;
        for (ExecucaoContratoCorrecaoVO execVO : getExecucoesCorrecao()) {
            if (execVO.getSelecionada()) {
                valorTotalExecucao = valorTotalExecucao.add(execVO.getValorLiquidoExecucao());
            }
        }
        return valorTotalExecucao;
    }

    public BigDecimal getValorTotalEstornadoExecucoesSelecionados() {
        BigDecimal valorTotalExecucao = BigDecimal.ZERO;
        for (ExecucaoContratoCorrecaoVO execVO : getExecucoesCorrecao()) {
            if (execVO.getSelecionada()) {
                valorTotalExecucao = valorTotalExecucao.add(execVO.getValorEstornado());
            }
        }
        return valorTotalExecucao;
    }

    public void selecionarOrigem(ExecucaoContratoCorrecaoOrigemVO origemVO) {
        origensExecucao.forEach(origemList -> {
            origemList.setSelecionada(origemList.getIdOrigem().equals(origemVO.getIdOrigem()) && origemVO.getSelecionada());
        });
        execucoesCorrecao.forEach(execVO -> execVO.setSelecionada(false));
        if (origemVO.getSelecionada()) {
            execucoesCorrecao.stream()
                .filter(execVO -> execVO.getExecucaoContrato().getIdOrigem().equals(origemVO.getIdOrigem()))
                .forEach(execVO -> execVO.setSelecionada(true));
        }
    }

    public boolean hasExecucoesCorrecoes() {
        return !Util.isListNullOrEmpty(execucoesCorrecao);
    }

    public boolean hasExecucoes() {
        return !Util.isListNullOrEmpty(execucoesCorrigidas);
    }

    public boolean hasOrigensExecucoes() {
        return !Util.isListNullOrEmpty(origensExecucao);
    }
}
