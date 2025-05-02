package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SolicitacaoEmpenhoEstorno;
import br.com.webpublico.enums.TipoResumoExecucao;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResumoExecucaoVO {

    private Long id;
    private Long numero;
    private Date data;
    private TipoResumoExecucao tipoExecucao;
    private ResumoExecucaoOrigemVO origemExecucao;
    private BigDecimal valorExecucao;
    private List<ResumoExecucaoItemVO> itens;
    private List<ResumoExecucaoFonteVO> fontes;
    private List<ResumoExecucaoEstornoVO> estornosExecucao;
    private List<ResumoExecucaoEmpenhoVO> empenhos;
    private List<ResumoExecucaoEmpenhoEstornoVO> estornosEmpenho;

    public ResumoExecucaoVO() {
        itens = Lists.newArrayList();
        fontes = Lists.newArrayList();
        estornosExecucao = Lists.newArrayList();
        empenhos = Lists.newArrayList();
        estornosEmpenho = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResumoExecucaoOrigemVO getOrigemExecucao() {
        return origemExecucao;
    }

    public void setOrigemExecucao(ResumoExecucaoOrigemVO origemExecucao) {
        this.origemExecucao = origemExecucao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public BigDecimal getValorExecucao() {
        return valorExecucao;
    }

    public void setValorExecucao(BigDecimal valorExecucao) {
        this.valorExecucao = valorExecucao;
    }


    public BigDecimal getSaldoEmpenhar() {
        return valorExecucao.subtract(getValorEmpenhado()).subtract(getValorEstornoExecucaoSolicitacaoEstornada());
    }

    public BigDecimal getTotalExecutado() {
        return valorExecucao.subtract(getValorEstornoExecucao());
    }

    public List<ResumoExecucaoItemVO> getItens() {
        return itens;
    }

    public void setItens(List<ResumoExecucaoItemVO> itens) {
        this.itens = itens;
    }

    public List<ResumoExecucaoFonteVO> getFontes() {
        return fontes;
    }

    public void setFontes(List<ResumoExecucaoFonteVO> fontes) {
        this.fontes = fontes;
    }

    public List<ResumoExecucaoEstornoVO> getEstornosExecucao() {
        return estornosExecucao;
    }

    public void setEstornosExecucao(List<ResumoExecucaoEstornoVO> estornosExecucao) {
        this.estornosExecucao = estornosExecucao;
    }

    public List<ResumoExecucaoEmpenhoVO> getEmpenhos() {
        return empenhos;
    }

    public void setEmpenhos(List<ResumoExecucaoEmpenhoVO> empenhos) {
        this.empenhos = empenhos;
    }

    public List<ResumoExecucaoEmpenhoEstornoVO> getEstornosEmpenho() {
        return estornosEmpenho;
    }

    public void setEstornosEmpenho(List<ResumoExecucaoEmpenhoEstornoVO> estornosEmpenho) {
        this.estornosEmpenho = estornosEmpenho;
    }

    public TipoResumoExecucao getTipoExecucao() {
        return tipoExecucao;
    }

    public void setTipoExecucao(TipoResumoExecucao tipoExecucao) {
        this.tipoExecucao = tipoExecucao;
    }

    public Boolean getSolicitacaoEmpenhoEstornada() {
        return estornosExecucao.stream()
            .flatMap(exEst -> exEst.getSolicitacoesEstorno().stream())
            .filter(exSol -> exSol.getSolicitacaoEmpenho() != null)
            .anyMatch(exSol -> exSol.getSolicitacaoEmpenho().getEstornada());
    }

    public List<Long> getIdsEmpenhos() {
        List<Long> ids = empenhos.stream()
            .filter(ResumoExecucaoEmpenhoVO::hasEmpenhoResto)
            .map(emp -> emp.getEmpenhoResto().getId()).collect(Collectors.toList());
        ids.addAll(empenhos.stream()
            .map(ResumoExecucaoEmpenhoVO::getId)
            .filter(Objects::nonNull).collect(Collectors.toList()));
        return ids;

    }

    public String getUrlExecucao() {
        if (tipoExecucao != null) {
            if (tipoExecucao.isExecucaoContrato()) {
                return "/execucao-contrato-adm/ver/" + id + "/";
            }
            return "/execucao-sem-contrato/ver/" + id + "/";
        }
        return "";
    }

    public String getLabelFormulaSaldoEmpenhar() {
        if (getSolicitacaoEmpenhoEstornada()) {
            return "(e) = (a - b - c): ";
        }
        return "(e) = (a - c): ";
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal total = BigDecimal.ZERO;
        for (ResumoExecucaoItemVO item : itens) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalFontes() {
        BigDecimal total = BigDecimal.ZERO;
        for (ResumoExecucaoFonteVO fonte : fontes) {
            total = total.add(fonte.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorEstornoExecucao() {
        BigDecimal total = BigDecimal.ZERO;
        if (!Util.isListNullOrEmpty(estornosExecucao)) {
            for (ResumoExecucaoEstornoVO exec : estornosExecucao) {
                total = total.add(exec.getValor());
            }
        }
        return total;
    }

    public BigDecimal getValorEstornoExecucaoSolicitacaoEstornada() {
        BigDecimal total = BigDecimal.ZERO;
        if (!Util.isListNullOrEmpty(estornosExecucao)) {
            for (ResumoExecucaoEstornoVO exec : estornosExecucao) {
                for (SolicitacaoEmpenhoEstorno sol : exec.getSolicitacoesEstorno()) {
                    if (sol.getSolicitacaoEmpenho() != null && sol.getSolicitacaoEmpenho().getEstornada()) {
                        total = total.add(sol.getValor());
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal getValorEmpenhado() {
        BigDecimal total = BigDecimal.ZERO;
        if (!Util.isListNullOrEmpty(empenhos)) {
            for (ResumoExecucaoEmpenhoVO exec : empenhos) {
                if (exec.isEmpenhoExecutado()) {
                    total = total.add(exec.getValor());
                }
            }
        }
        return total;
    }

    public BigDecimal getValorEstornoEmpenho() {
        BigDecimal total = BigDecimal.ZERO;
        if (!Util.isListNullOrEmpty(estornosEmpenho)) {
            for (ResumoExecucaoEmpenhoEstornoVO exec : estornosEmpenho) {
                total = total.add(exec.getValor());
            }
        }
        return total;
    }
}
