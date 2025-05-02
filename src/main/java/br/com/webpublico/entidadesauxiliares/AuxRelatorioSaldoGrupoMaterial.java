package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Criado por Alex
 * Data: 20/06/2017.
 */
public class AuxRelatorioSaldoGrupoMaterial {

    private String codigo;
    private String descricaoGrupo;
    private BigDecimal saldo;
    private Date dataSaldo;
    private String unidadeOrganizacional;
    private String tipoEstoque;

    public AuxRelatorioSaldoGrupoMaterial() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricaoGrupo() {
        return descricaoGrupo;
    }

    public void setDescricaoGrupo(String descricaoGrupo) {
        this.descricaoGrupo = descricaoGrupo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public String getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(String unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(String tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }
}