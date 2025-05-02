package br.com.webpublico.entidadesauxiliares.administrativo.relatorio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desenvolvimento on 09/02/2017.
 */
public class InventarioEstoque implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(InventarioEstoque.class);

    private String codigoOrgao;
    private String descricaoOrgao;
    private String codigoUnidade;
    private String descricaoUnidade;
    private String codigoAdministrativa;
    private String descricaoAdministrativa;
    private String codigoAdministrativaOrgao;
    private String descricaoAdministrativaOrgao;
    private String codigoMaterial;
    private String material;
    private String unidade;
    private String codigoGrupo;
    private String grupo;
    private BigDecimal quantidade;
    private BigDecimal valorTotal;
    private BigDecimal valorUnitario;
    private BigDecimal valorAjuste;

    public InventarioEstoque() {
    }

    public static List<InventarioEstoque> preencherDados(List<Object[]> objetos) {
        List<InventarioEstoque> retorno = new ArrayList<>();
        for (Object[] objeto : objetos) {
            InventarioEstoque inventario = new InventarioEstoque();
            inventario.setCodigoOrgao((String) objeto[0]);
            inventario.setDescricaoOrgao((String) objeto[1]);
            inventario.setCodigoUnidade((String) objeto[2]);
            inventario.setDescricaoUnidade((String) objeto[3]);
            inventario.setCodigoAdministrativa((String) objeto[4]);
            inventario.setDescricaoAdministrativa((String) objeto[5]);
            inventario.setCodigoAdministrativaOrgao((String) objeto[6]);
            inventario.setDescricaoAdministrativaOrgao((String) objeto[7]);
            inventario.setCodigoMaterial((String) objeto[8]);
            inventario.setMaterial((String) objeto[9]);
            inventario.setUnidade((String) objeto[10]);
            inventario.setCodigoGrupo((String) objeto[11]);
            inventario.setGrupo((String) objeto[12]);
            inventario.setQuantidade((BigDecimal) objeto[13]);
            inventario.setValorTotal((BigDecimal) objeto[14]);
            inventario.setValorUnitario((BigDecimal) objeto[15]);
            inventario.setValorAjuste((BigDecimal) objeto[16]);
            retorno.add(inventario);
        }
        return retorno;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public String getCodigoAdministrativa() {
        return codigoAdministrativa;
    }

    public void setCodigoAdministrativa(String codigoAdministrativa) {
        this.codigoAdministrativa = codigoAdministrativa;
    }

    public String getDescricaoAdministrativa() {
        return descricaoAdministrativa;
    }

    public void setDescricaoAdministrativa(String descricaoAdministrativa) {
        this.descricaoAdministrativa = descricaoAdministrativa;
    }

    public String getCodigoMaterial() {
        return codigoMaterial;
    }

    public void setCodigoMaterial(String codigoMaterial) {
        this.codigoMaterial = codigoMaterial;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }

    public String getCodigoAdministrativaOrgao() {
        return codigoAdministrativaOrgao;
    }

    public void setCodigoAdministrativaOrgao(String codigoAdministrativaOrgao) {
        this.codigoAdministrativaOrgao = codigoAdministrativaOrgao;
    }

    public String getDescricaoAdministrativaOrgao() {
        return descricaoAdministrativaOrgao;
    }

    public void setDescricaoAdministrativaOrgao(String descricaoAdministrativaOrgao) {
        this.descricaoAdministrativaOrgao = descricaoAdministrativaOrgao;
    }
}
