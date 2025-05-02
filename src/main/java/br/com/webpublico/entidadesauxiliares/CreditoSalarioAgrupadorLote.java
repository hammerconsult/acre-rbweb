package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.enums.ModalidadeConta;

/**
 * Created by Edi
 */
public class CreditoSalarioAgrupadorLote {

    private Banco banco;
    private ModalidadeConta modalidadeConta;
    private String descricaoLote;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreditoSalarioAgrupadorLote that = (CreditoSalarioAgrupadorLote) o;

        if (banco != null ? !banco.equals(that.banco) : that.banco != null) return false;
        return modalidadeConta == that.modalidadeConta;

    }

    @Override
    public int hashCode() {
        int result = banco != null ? banco.hashCode() : 0;
        result = 31 * result + (modalidadeConta != null ? modalidadeConta.hashCode() : 0);
        return result;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public ModalidadeConta getModalidadeConta() {
        return modalidadeConta;
    }

    public void setModalidadeConta(ModalidadeConta modalidadeConta) {
        this.modalidadeConta = modalidadeConta;
    }

    public String getDescricaoLote() {
        return descricaoLote;
    }

    public void setDescricaoLote(String descricaoLote) {
        this.descricaoLote = descricaoLote;
    }
}
