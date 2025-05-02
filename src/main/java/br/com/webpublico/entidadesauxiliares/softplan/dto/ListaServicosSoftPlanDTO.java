package br.com.webpublico.entidadesauxiliares.softplan.dto;

import com.google.common.collect.Lists;

import java.util.List;

public class ListaServicosSoftPlanDTO<D extends DadosServicoSoftPlanDTO, S extends ServicoSoftplanDTO<D>> {

    private List<S> servicos;

    public ListaServicosSoftPlanDTO() {
        this.servicos = Lists.newArrayList();
    }

    public List<S> getServicos() {
        return servicos;
    }

    public void setServicos(List<S> servicos) {
        this.servicos = servicos;
    }
}
