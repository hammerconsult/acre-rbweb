package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.util.List;

public class AtaRegistroPrecoContrato {

    private Long idContrato;
    private String contrato;
    private List<AtaRegistroPrecoItensContrato> itensContratoQuantidade;
    private List<AtaRegistroPrecoItensContrato> itensContratoValor;

    public AtaRegistroPrecoContrato(){
        itensContratoQuantidade = Lists.newArrayList();
        itensContratoValor = Lists.newArrayList();
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public List<AtaRegistroPrecoItensContrato> getItensContratoQuantidade() {
        return itensContratoQuantidade;
    }

    public void setItensContratoQuantidade(List<AtaRegistroPrecoItensContrato> itensContratoQuantidade) {
        this.itensContratoQuantidade = itensContratoQuantidade;
    }

    public List<AtaRegistroPrecoItensContrato> getItensContratoValor() {
        return itensContratoValor;
    }

    public void setItensContratoValor(List<AtaRegistroPrecoItensContrato> itensContratoValor) {
        this.itensContratoValor = itensContratoValor;
    }
}
