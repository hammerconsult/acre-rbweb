package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidades.UnidadeOrganizacional;

/**
 * Created by venom on 09/03/15.
 */
public class MapSaldoSubconta {
    private SubConta subConta;
    private UnidadeOrganizacional unidadeOrganizacional;
    private ContaDeDestinacao contaDeDestinacao;

    public MapSaldoSubconta(SubConta subConta, UnidadeOrganizacional unidadeOrganizacional, ContaDeDestinacao contaDeDestinacao) {
        this.subConta = subConta;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapSaldoSubconta that = (MapSaldoSubconta) o;

        if (!contaDeDestinacao.equals(that.contaDeDestinacao)) return false;
        if (!subConta.equals(that.subConta)) return false;
        if (!unidadeOrganizacional.equals(that.unidadeOrganizacional)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subConta.hashCode();
        result = 31 * result + unidadeOrganizacional.hashCode();
        result = 31 * result + contaDeDestinacao.hashCode();
        return result;
    }
}
