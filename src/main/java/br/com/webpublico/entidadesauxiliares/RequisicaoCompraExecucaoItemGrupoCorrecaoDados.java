package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.util.List;

public class RequisicaoCompraExecucaoItemGrupoCorrecaoDados {

    String codigoGrupo;
    List<RequisicaoCompraExecucaoItemCorrecaoDados> itens;

    public RequisicaoCompraExecucaoItemGrupoCorrecaoDados() {
        itens = Lists.newArrayList();
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public List<RequisicaoCompraExecucaoItemCorrecaoDados> getItens() {
        return itens;
    }

    public void setItens(List<RequisicaoCompraExecucaoItemCorrecaoDados> itens) {
        this.itens = itens;
    }
}
