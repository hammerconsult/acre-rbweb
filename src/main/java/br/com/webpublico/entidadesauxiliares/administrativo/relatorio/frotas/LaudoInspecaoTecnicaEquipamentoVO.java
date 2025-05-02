package br.com.webpublico.entidadesauxiliares.administrativo.relatorio.frotas;

import java.math.BigDecimal;

/**
 * Created by carlos on 25/05/17.
 */
public class LaudoInspecaoTecnicaEquipamentoVO {
    private BigDecimal equipamentoId;
    private String descricaoEquipamento;
    private String identificacaoEquipamento;
    private String itemInspecaoEquipamento;
    private String tipoInspecaoEquipamento;
    private String unidade;

    public LaudoInspecaoTecnicaEquipamentoVO() {
    }

    public BigDecimal getEquipamentoId() {
        return equipamentoId;
    }

    public void setEquipamentoId(BigDecimal equipamentoId) {
        this.equipamentoId = equipamentoId;
    }

    public String getDescricaoEquipamento() {
        return descricaoEquipamento;
    }

    public void setDescricaoEquipamento(String descricaoEquipamento) {
        this.descricaoEquipamento = descricaoEquipamento;
    }

    public String getIdentificacaoEquipamento() {
        return identificacaoEquipamento;
    }

    public void setIdentificacaoEquipamento(String identificacaoEquipamento) {
        this.identificacaoEquipamento = identificacaoEquipamento;
    }

    public String getItemInspecaoEquipamento() {
        return itemInspecaoEquipamento;
    }

    public void setItemInspecaoEquipamento(String itemInspecaoEquipamento) {
        this.itemInspecaoEquipamento = itemInspecaoEquipamento;
    }

    public String getTipoInspecaoEquipamento() {
        return tipoInspecaoEquipamento;
    }

    public void setTipoInspecaoEquipamento(String tipoInspecaoEquipamento) {
        this.tipoInspecaoEquipamento = tipoInspecaoEquipamento;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}
