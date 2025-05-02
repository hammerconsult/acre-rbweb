package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.util.List;

public class AssistenteAgrupadorContratoEmVigencia {

    private List<AgrupadorContratoEmVigencia> agrupadores;
    private List<ContratoEmVigencia> contratos;

    public AssistenteAgrupadorContratoEmVigencia() {
        agrupadores = Lists.newArrayList();
        contratos = Lists.newArrayList();
    }

    public List<AgrupadorContratoEmVigencia> getAgrupadores() {
        return agrupadores;
    }

    public void setAgrupadores(List<AgrupadorContratoEmVigencia> agrupadores) {
        this.agrupadores = agrupadores;
    }

    public List<ContratoEmVigencia> getContratos() {
        return contratos;
    }

    public void setContratos(List<ContratoEmVigencia> contratos) {
        this.contratos = contratos;
    }
}
