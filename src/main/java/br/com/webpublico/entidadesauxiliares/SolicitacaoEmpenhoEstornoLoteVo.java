package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class SolicitacaoEmpenhoEstornoLoteVo {
    private SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno;
    private ExecucaoContratoEstorno execucaoContratoEstorno;
    private ExecucaoProcessoEstorno execucaoProcessoEstorno;

    public SolicitacaoEmpenhoEstornoLoteVo() {
    }

    public SolicitacaoEmpenhoEstornoLoteVo(SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno, ExecucaoContratoEstorno execucaoContratoEstorno, ExecucaoProcessoEstorno execucaoProcessoEstorno) {
        this.solicitacaoEmpenhoEstorno = solicitacaoEmpenhoEstorno;
        this.execucaoContratoEstorno = execucaoContratoEstorno;
        this.execucaoProcessoEstorno = execucaoProcessoEstorno;
    }

    public SolicitacaoEmpenhoEstorno getSolicitacaoEmpenhoEstorno() {
        return solicitacaoEmpenhoEstorno;
    }

    public void setSolicitacaoEmpenhoEstorno(SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno) {
        this.solicitacaoEmpenhoEstorno = solicitacaoEmpenhoEstorno;
    }

    public ExecucaoContratoEstorno getExecucaoContratoEstorno() {
        return execucaoContratoEstorno;
    }

    public void setExecucaoContratoEstorno(ExecucaoContratoEstorno execucaoContratoEstorno) {
        this.execucaoContratoEstorno = execucaoContratoEstorno;
    }

    public ExecucaoProcessoEstorno getExecucaoProcessoEstorno() {
        return execucaoProcessoEstorno;
    }

    public void setExecucaoProcessoEstorno(ExecucaoProcessoEstorno execucaoProcessoEstorno) {
        this.execucaoProcessoEstorno = execucaoProcessoEstorno;
    }
}
