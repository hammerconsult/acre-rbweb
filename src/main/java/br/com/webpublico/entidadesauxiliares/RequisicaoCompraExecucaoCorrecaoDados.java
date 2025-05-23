package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class RequisicaoCompraExecucaoCorrecaoDados {

    Long idRequisicao;
    TipoObjetoCompra tipoObjetoCompra;
    Long numeroExecucao;
    Long idExecucao;
    Long idExecucaoEmpenho;
    Long idRequisicaoCompraExecucao;
    CorrecaoDadosAdministrativoVO.TipoAlteracaoDados tipoAlteracaoDados;
    List<RequisicaoCompraEmpenhoCorrecaoDados> empenhos;
    List<RequisicaoCompraExecucaoItemCorrecaoDados> itens;
    List<RequisicaoCompraExecucaoItemGrupoCorrecaoDados> itensGrupo;

    public RequisicaoCompraExecucaoCorrecaoDados() {
        empenhos = Lists.newArrayList();
        itens = Lists.newArrayList();
        itensGrupo = Lists.newArrayList();
    }

    public Long getIdExecucaoEmpenho() {
        return idExecucaoEmpenho;
    }

    public void setIdExecucaoEmpenho(Long idExecucaoEmpenho) {
        this.idExecucaoEmpenho = idExecucaoEmpenho;
    }

    public CorrecaoDadosAdministrativoVO.TipoAlteracaoDados getTipoAlteracaoDados() {
        return tipoAlteracaoDados;
    }

    public void setTipoAlteracaoDados(CorrecaoDadosAdministrativoVO.TipoAlteracaoDados tipoAlteracaoDados) {
        this.tipoAlteracaoDados = tipoAlteracaoDados;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public Long getIdRequisicao() {
        return idRequisicao;
    }

    public void setIdRequisicao(Long idRequisicao) {
        this.idRequisicao = idRequisicao;
    }

    public Long getIdRequisicaoCompraExecucao() {
        return idRequisicaoCompraExecucao;
    }

    public void setIdRequisicaoCompraExecucao(Long idRequisicaoCompraExecucao) {
        this.idRequisicaoCompraExecucao = idRequisicaoCompraExecucao;
    }

    public Long getIdExecucao() {
        return idExecucao;
    }

    public void setIdExecucao(Long idExecucao) {
        this.idExecucao = idExecucao;
    }

    public Long getNumeroExecucao() {
        return numeroExecucao;
    }

    public void setNumeroExecucao(Long numeroExecucao) {
        this.numeroExecucao = numeroExecucao;
    }

    public List<RequisicaoCompraEmpenhoCorrecaoDados> getEmpenhos() {
        return empenhos;
    }

    public void setEmpenhos(List<RequisicaoCompraEmpenhoCorrecaoDados> empenhos) {
        this.empenhos = empenhos;
    }

    public List<RequisicaoCompraExecucaoItemCorrecaoDados> getItens() {
        return itens;
    }

    public void setItens(List<RequisicaoCompraExecucaoItemCorrecaoDados> itens) {
        this.itens = itens;
    }

    public List<RequisicaoCompraExecucaoItemGrupoCorrecaoDados> getItensGrupo() {
        return itensGrupo;
    }

    public void setItensGrupo(List<RequisicaoCompraExecucaoItemGrupoCorrecaoDados> itensGrupo) {
        this.itensGrupo = itensGrupo;
    }

    public BigDecimal getValorTotalItens(){
        BigDecimal total = BigDecimal.ZERO;
        if (!Util.isListNullOrEmpty(itens)) {
            for (RequisicaoCompraExecucaoItemCorrecaoDados item : itens) {
                total = total.add(item.getValorTotal());
            }
        }
        return total;
    }
}
