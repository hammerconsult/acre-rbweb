package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.TipoContrato;
import br.com.webpublico.util.Util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 21/02/2018.
 */
public class RelatorioItensPorAlmoxarifado implements Serializable {

    private String numeroLicitacao;
    private String exercicioLicitacao;
    private String modalidadeLicitacao;
    private String numeroContrato;
    private String tipoContrato;
    private String execucaoContrato;
    private String localEstoque;
    private String unidadeOrcamentaria;
    private String material;
    private String grupoMaterial;
    private BigDecimal quantidadeRequisitada;
    private BigDecimal quantidadeLicitada;
    private BigDecimal quantidadeEntrada;
    private BigDecimal quantidadeRestante;
    private BigDecimal quantidadeFaseAquisicao;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal valorExecucao;
    private Date dataEntrada;

    public RelatorioItensPorAlmoxarifado() {
        quantidadeRequisitada = BigDecimal.ZERO;
        quantidadeLicitada = BigDecimal.ZERO;
        quantidadeEntrada = BigDecimal.ZERO;
        quantidadeRestante = BigDecimal.ZERO;
        quantidadeFaseAquisicao = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        valorExecucao = BigDecimal.ZERO;
    }

    public static List<RelatorioItensPorAlmoxarifado> preencherDados(List<Object[]> objetos) {
        List<RelatorioItensPorAlmoxarifado> toReturn = new ArrayList<>();
        for (Object[] obj : objetos) {
            RelatorioItensPorAlmoxarifado item = new RelatorioItensPorAlmoxarifado();
            item.setNumeroLicitacao(((BigDecimal) obj[0]).toString());
            item.setExercicioLicitacao(((BigDecimal) obj[1]).toString());
            item.setModalidadeLicitacao(ModalidadeLicitacao.valueOf((String) obj[2]).getDescricao());
            item.setNumeroContrato((String) obj[3]);
            item.setTipoContrato(TipoContrato.valueOf((String) obj[4]).getDescricao());
            item.setExecucaoContrato(((BigDecimal) obj[5]).toString() + " / " + Util.formataValor((BigDecimal) obj[6]));
            item.setLocalEstoque((String) obj[7]);
            item.setUnidadeOrcamentaria((String) obj[8]);
            item.setMaterial((String) obj[9]);
            item.setGrupoMaterial((String) obj[10]);
            item.setQuantidadeRequisitada((BigDecimal) obj[11]);
            item.setQuantidadeLicitada((BigDecimal) obj[12]);
            item.setQuantidadeEntrada((BigDecimal) obj[13]);
            item.setValorUnitario((BigDecimal) obj[14]);
            item.setDataEntrada((Date) obj[15]);
            item.setValorTotal((BigDecimal) obj[16]);
            item.setQuantidadeRestante((BigDecimal) obj[17]);
            item.setQuantidadeFaseAquisicao((BigDecimal) obj[18]);
            toReturn.add(item);
        }
        return toReturn;
    }

    public String getNumeroLicitacao() {
        return numeroLicitacao;
    }

    public void setNumeroLicitacao(String numeroLicitacao) {
        this.numeroLicitacao = numeroLicitacao;
    }

    public String getExercicioLicitacao() {
        return exercicioLicitacao;
    }

    public void setExercicioLicitacao(String exercicioLicitacao) {
        this.exercicioLicitacao = exercicioLicitacao;
    }

    public String getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(String modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(String execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public String getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(String localEstoque) {
        this.localEstoque = localEstoque;
    }

    public String getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(String unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(String grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public BigDecimal getQuantidadeRequisitada() {
        return quantidadeRequisitada;
    }

    public void setQuantidadeRequisitada(BigDecimal quantidadeRequisitada) {
        this.quantidadeRequisitada = quantidadeRequisitada;
    }

    public BigDecimal getQuantidadeLicitada() {
        return quantidadeLicitada;
    }

    public void setQuantidadeLicitada(BigDecimal quantidadeLicitada) {
        this.quantidadeLicitada = quantidadeLicitada;
    }

    public BigDecimal getQuantidadeEntrada() {
        return quantidadeEntrada;
    }

    public void setQuantidadeEntrada(BigDecimal quantidadeEntrada) {
        this.quantidadeEntrada = quantidadeEntrada;
    }

    public BigDecimal getQuantidadeRestante() {
        return quantidadeRestante;
    }

    public void setQuantidadeRestante(BigDecimal quantidadeRestante) {
        this.quantidadeRestante = quantidadeRestante;
    }

    public BigDecimal getQuantidadeFaseAquisicao() {
        return quantidadeFaseAquisicao;
    }

    public void setQuantidadeFaseAquisicao(BigDecimal quantidadeFaseAquisicao) {
        this.quantidadeFaseAquisicao = quantidadeFaseAquisicao;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorExecucao() {
        return valorExecucao;
    }

    public void setValorExecucao(BigDecimal valorExecucao) {
        this.valorExecucao = valorExecucao;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }
}
