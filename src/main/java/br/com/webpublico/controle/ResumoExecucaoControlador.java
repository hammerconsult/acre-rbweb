package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ResumoExecucaoVO;
import br.com.webpublico.entidadesauxiliares.FiltroResumoExecucaoVO;
import br.com.webpublico.negocios.ResumoExecucaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
public class ResumoExecucaoControlador implements Serializable {

    @EJB
    private ResumoExecucaoFacade facade;
    public List<ResumoExecucaoVO> execucoes;
    public ResumoExecucaoVO execucaoSelecionada;
    public FiltroResumoExecucaoVO filtro;

    public void novo(FiltroResumoExecucaoVO filtro) {
        if (filtro != null) {
            this.filtro = filtro;
            if (Util.isListNullOrEmpty(execucoes)) {
                buscarExecucoes();
            }
        }
    }

    private void buscarExecucoes() {
        execucoes = facade.buscarExecucoes(filtro);
        if (!Util.isListNullOrEmpty(execucoes)) {
            execucoes.stream().map(ResumoExecucaoVO::getItens).forEach(Collections::sort);
            if (filtro.getExecucaoContrato() == null && filtro.getExecucaoProcesso() == null) {
                FacesUtil.executaJavaScript("atualizaTabelaExecucao()");
            }
            if (filtro.getExecucaoContrato() != null || filtro.getExecucaoProcesso() != null) {
                execucaoSelecionada = execucoes.get(0);
            }
        }
        this.filtro = null;
    }

    public boolean hasExecucoes() {
        return !Util.isListNullOrEmpty(execucoes);
    }

    public void selecionarExecucao(ResumoExecucaoVO exec) {
        execucaoSelecionada = exec;
    }

    public BigDecimal getValorTotalExecucoes() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasExecucoes()) {
            for (ResumoExecucaoVO exec : execucoes) {
                total = total.add(exec.getValorExecucao());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalEstornosExecucao() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasExecucoes()) {
            for (ResumoExecucaoVO exec : execucoes) {
                total = total.add(exec.getValorEstornoExecucao());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalEmpenhado() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasExecucoes()) {
            for (ResumoExecucaoVO exec : execucoes) {
                total = total.add(exec.getValorEmpenhado());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalEstornosEmpenho() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasExecucoes()) {
            for (ResumoExecucaoVO exec : execucoes) {
                total = total.add(exec.getValorEstornoEmpenho());
            }
        }
        return total;
    }

    public BigDecimal getSaldoTotalEmpenhar() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasExecucoes()) {
            for (ResumoExecucaoVO exec : execucoes) {
                total = total.add(exec.getSaldoEmpenhar());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalExecutado() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasExecucoes()) {
            for (ResumoExecucaoVO exec : execucoes) {
                total = total.add(exec.getTotalExecutado());
            }
        }
        return total;
    }

    public ResumoExecucaoVO getExecucaoSelecionada() {
        return execucaoSelecionada;
    }

    public void setExecucaoSelecionada(ResumoExecucaoVO execucaoSelecionada) {
        this.execucaoSelecionada = execucaoSelecionada;
    }

    public List<ResumoExecucaoVO> getExecucoes() {
        return execucoes;
    }

    public void setExecucoes(List<ResumoExecucaoVO> execucoes) {
        this.execucoes = execucoes;
    }
}
