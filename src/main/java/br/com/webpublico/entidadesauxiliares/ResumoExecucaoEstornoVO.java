package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SolicitacaoEmpenhoEstorno;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ResumoExecucaoEstornoVO {

    private Long id;
    private String numero;
    private Date data;
    private BigDecimal valor;
    private ResumoExecucaoVO resumoExecucaoVO;
    private List<SolicitacaoEmpenhoEstorno> solicitacoesEstorno;

    public ResumoExecucaoEstornoVO() {
        solicitacoesEstorno = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<SolicitacaoEmpenhoEstorno> getSolicitacoesEstorno() {
        return solicitacoesEstorno;
    }

    public void setSolicitacoesEstorno(List<SolicitacaoEmpenhoEstorno> solicitacoesEstorno) {
        this.solicitacoesEstorno = solicitacoesEstorno;
    }

    public ResumoExecucaoVO getResumoExecucaoVO() {
        return resumoExecucaoVO;
    }

    public void setResumoExecucaoVO(ResumoExecucaoVO resumoExecucaoVO) {
        this.resumoExecucaoVO = resumoExecucaoVO;
    }

    public String getUrlEstornoExecucao() {
        if (resumoExecucaoVO.getTipoExecucao().isExecucaoContrato()) {
            return "/execucao-contrato-estorno/ver/" + id + "/";
        }
        return "/execucao-sem-contrato-estorno/ver/" + id + "/";
    }

    public String getUrlSolicitacaoEmpenho(SolicitacaoEmpenhoEstorno sol) {
        if (sol.getEmpenhoEstorno() != null) {
            if (sol.getEmpenhoEstorno().isEmpenhoEstornoNormal()) {
                return "/estorno-empenho/ver/" + sol.getEmpenhoEstorno().getId() + "/";
            }
            return "/cancelamento-empenho-restos-a-pagar/ver/" + sol.getEmpenhoEstorno().getId() + "/";
        }
        if (sol.getSolicitacaoEmpenho() != null) {
            return "/solicitacao-empenho/ver/" + sol.getSolicitacaoEmpenho().getId() + "/";
        }
        return "";
    }
}
