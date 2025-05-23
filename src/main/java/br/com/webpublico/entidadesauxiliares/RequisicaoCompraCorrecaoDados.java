package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class RequisicaoCompraCorrecaoDados {

    Long idRequisicao;
    Long numeroRequisicao;
    TipoObjetoCompra tipoObjetoCompra;
    List<RequisicaoCompraExecucaoCorrecaoDados> execucoes;

    public RequisicaoCompraCorrecaoDados() {
        execucoes = Lists.newArrayList();
    }

    public Long getIdRequisicao() {
        return idRequisicao;
    }

    public void setIdRequisicao(Long idRequisicao) {
        this.idRequisicao = idRequisicao;
    }

    public Long getNumeroRequisicao() {
        return numeroRequisicao;
    }

    public void setNumeroRequisicao(Long numeroRequisicao) {
        this.numeroRequisicao = numeroRequisicao;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public List<RequisicaoCompraExecucaoCorrecaoDados> getExecucoes() {
        return execucoes;
    }

    public void setExecucoes(List<RequisicaoCompraExecucaoCorrecaoDados> execucoes) {
        this.execucoes = execucoes;
    }

    public List<RequisicaoCompraExecucaoCorrecaoDados> getExecucoesInsert(){
        if (!Util.isListNullOrEmpty(execucoes)){
            return execucoes.stream()
                .filter(ex -> CorrecaoDadosAdministrativoVO.TipoAlteracaoDados.INSERT.equals(ex.getTipoAlteracaoDados()))
                .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (!Util.isListNullOrEmpty(execucoes)) {
            for (RequisicaoCompraExecucaoCorrecaoDados exec : execucoes) {
                total = total.add(exec.getValorTotalItens());
            }
        }
        return total;
    }
}
