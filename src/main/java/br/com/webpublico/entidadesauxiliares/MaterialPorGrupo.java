package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mateus on 13/03/18.
 */
public class MaterialPorGrupo {

    private BigDecimal codigoMaterial;
    private BigDecimal controleDeLote;
    private String descricaoMaterial;
    private String descricaoMarca;
    private BigDecimal marcaId;
    private String descricaoModelo;
    private String descricaoMarcaModelo;
    private String codigoGrupo;
    private String descricaoGrupo;
    private BigDecimal fisico;
    private BigDecimal financeiro;
    private BigDecimal unitario;

    public MaterialPorGrupo() {
    }

    public static List<MaterialPorGrupo> preencherDados(List<Object[]> objetos) {
        List<MaterialPorGrupo> retorno = new ArrayList<>();
        for (Object[] obj : objetos) {
            MaterialPorGrupo materialPorGrupo = new MaterialPorGrupo();
            materialPorGrupo.setCodigoMaterial((BigDecimal) obj[0]);
            materialPorGrupo.setControleDeLote((BigDecimal) obj[1]);
            materialPorGrupo.setDescricaoMaterial((String) obj[2]);
            materialPorGrupo.setMarcaId((BigDecimal) obj[3]);
            materialPorGrupo.setDescricaoMarca((String) obj[4]);
            materialPorGrupo.setDescricaoModelo((String) obj[5]);
            materialPorGrupo.setDescricaoMarcaModelo((String) obj[6]);
            materialPorGrupo.setCodigoGrupo((String) obj[7]);
            materialPorGrupo.setDescricaoGrupo((String) obj[8]);
            materialPorGrupo.setFisico((BigDecimal) obj[9]);
            materialPorGrupo.setFinanceiro((BigDecimal) obj[10]);
            materialPorGrupo.setUnitario(calcularValorUnitario(materialPorGrupo.getFinanceiro(), materialPorGrupo.getFisico()));
            retorno.add(materialPorGrupo);
        }
        return retorno;
    }

    private static BigDecimal calcularValorUnitario(BigDecimal valorFinanceiro, BigDecimal valorFisico) {
        return (valorFinanceiro.divide(valorFisico, 8, BigDecimal.ROUND_HALF_UP)).setScale(4, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getCodigoMaterial() {
        return codigoMaterial;
    }

    public void setCodigoMaterial(BigDecimal codigoMaterial) {
        this.codigoMaterial = codigoMaterial;
    }

    public BigDecimal getControleDeLote() {
        return controleDeLote;
    }

    public void setControleDeLote(BigDecimal controleDeLote) {
        this.controleDeLote = controleDeLote;
    }

    public String getDescricaoMaterial() {
        return descricaoMaterial;
    }

    public void setDescricaoMaterial(String descricaoMaterial) {
        this.descricaoMaterial = descricaoMaterial;
    }

    public String getDescricaoMarca() {
        return descricaoMarca;
    }

    public void setDescricaoMarca(String descricaoMarca) {
        this.descricaoMarca = descricaoMarca;
    }

    public String getDescricaoModelo() {
        return descricaoModelo;
    }

    public void setDescricaoModelo(String descricaoModelo) {
        this.descricaoModelo = descricaoModelo;
    }

    public String getDescricaoMarcaModelo() {
        return descricaoMarcaModelo;
    }

    public void setDescricaoMarcaModelo(String descricaoMarcaModelo) {
        this.descricaoMarcaModelo = descricaoMarcaModelo;
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public String getDescricaoGrupo() {
        return descricaoGrupo;
    }

    public void setDescricaoGrupo(String descricaoGrupo) {
        this.descricaoGrupo = descricaoGrupo;
    }

    public BigDecimal getFisico() {
        return fisico;
    }

    public void setFisico(BigDecimal fisico) {
        this.fisico = fisico;
    }

    public BigDecimal getFinanceiro() {
        return financeiro;
    }

    public void setFinanceiro(BigDecimal financeiro) {
        this.financeiro = financeiro;
    }

    public BigDecimal getUnitario() {
        return unitario;
    }

    public void setUnitario(BigDecimal unitario) {
        this.unitario = unitario;
    }

    public BigDecimal getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(BigDecimal marcaId) {
        this.marcaId = marcaId;
    }
}
