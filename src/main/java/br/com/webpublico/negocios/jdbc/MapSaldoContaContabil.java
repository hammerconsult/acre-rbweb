package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaContabil;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoBalancete;

/**
 * Created by venom on 26/02/15.
 */
public class MapSaldoContaContabil {

    private Conta contaContabil;
    private UnidadeOrganizacional unidadeOrganizacional;
    private TipoBalancete tipoBalancete;

    public MapSaldoContaContabil(Conta contaContabil, UnidadeOrganizacional unidadeOrganizacional, TipoBalancete tipoBalancete) {
        this.contaContabil = contaContabil;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.tipoBalancete = tipoBalancete;
    }

    public Conta getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(Conta contaContabil) {
        this.contaContabil = contaContabil;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoBalancete getTipoBalancete() {
        return tipoBalancete;
    }

    public void setTipoBalancete(TipoBalancete tipoBalancete) {
        this.tipoBalancete = tipoBalancete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapSaldoContaContabil that = (MapSaldoContaContabil) o;

        if (!contaContabil.equals(that.contaContabil)) return false;
        if (tipoBalancete != that.tipoBalancete) return false;
        if (!unidadeOrganizacional.equals(that.unidadeOrganizacional)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = contaContabil.hashCode();
        result = 31 * result + unidadeOrganizacional.hashCode();
        result = 31 * result + tipoBalancete.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MapSaldoContaContabil{" +
                "conta=" + contaContabil +
                ", unidadeOrganizacional=" + unidadeOrganizacional +
                ", tipoBalancete=" + tipoBalancete +
                '}';
    }
}
