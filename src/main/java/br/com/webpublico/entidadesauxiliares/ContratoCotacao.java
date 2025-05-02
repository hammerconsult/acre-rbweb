package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Contrato;

import java.util.List;
import java.util.Objects;

public class ContratoCotacao {
    Contrato contrato;
    List<ContratoCotacaoObjetoCompra> objetosCompra;

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public List<ContratoCotacaoObjetoCompra> getObjetosCompra() {
        return objetosCompra;
    }

    public void setObjetosCompra(List<ContratoCotacaoObjetoCompra> objetosCompra) {
        this.objetosCompra = objetosCompra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContratoCotacao that = (ContratoCotacao) o;
        return Objects.equals(contrato, that.contrato) && Objects.equals(objetosCompra, that.objetosCompra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contrato);
    }
}
