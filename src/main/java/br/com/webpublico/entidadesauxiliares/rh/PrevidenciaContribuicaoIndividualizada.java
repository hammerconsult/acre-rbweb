package br.com.webpublico.entidadesauxiliares.rh;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * @Author peixe on 03/12/2015  11:34.
 */
public class PrevidenciaContribuicaoIndividualizada implements Serializable {

    private String matricula;
    private Integer contrato;
    private String cpf;
    private String nome;
    private String orgao;
    private LinkedList<ItemContribuicaoIndividualizada> itemContribuicaoIndividualizadas;

    private BigDecimal totalSalarioBruto = BigDecimal.ZERO;
    private BigDecimal totalBasePrevidencia = BigDecimal.ZERO;
    private BigDecimal totalContribuicaoServidor = BigDecimal.ZERO;
    private BigDecimal totalContribuicaoPatronal = BigDecimal.ZERO;
    private BigDecimal totalAliquotaSuplementar = BigDecimal.ZERO;
    private BigDecimal totalDeContribuicaoGeral = BigDecimal.ZERO;

    public PrevidenciaContribuicaoIndividualizada() {
        itemContribuicaoIndividualizadas = new LinkedList<>();
    }

    public BigDecimal getTotalSalarioBruto() {
        return totalSalarioBruto;
    }

    public void setTotalSalarioBruto(BigDecimal totalSalarioBruto) {
        this.totalSalarioBruto = totalSalarioBruto;
    }

    public BigDecimal getTotalBasePrevidencia() {
        return totalBasePrevidencia;
    }

    public void setTotalBasePrevidencia(BigDecimal totalBasePrevidencia) {
        this.totalBasePrevidencia = totalBasePrevidencia;
    }

    public BigDecimal getTotalContribuicaoServidor() {
        return totalContribuicaoServidor;
    }

    public void setTotalContribuicaoServidor(BigDecimal totalContribuicaoServidor) {
        this.totalContribuicaoServidor = totalContribuicaoServidor;
    }

    public BigDecimal getTotalContribuicaoPatronal() {
        return totalContribuicaoPatronal;
    }

    public void setTotalContribuicaoPatronal(BigDecimal totalContribuicaoPatronal) {
        this.totalContribuicaoPatronal = totalContribuicaoPatronal;
    }

    public BigDecimal getTotalAliquotaSuplementar() {
        return totalAliquotaSuplementar;
    }

    public void setTotalAliquotaSuplementar(BigDecimal totalAliquotaSuplementar) {
        this.totalAliquotaSuplementar = totalAliquotaSuplementar;
    }

    public BigDecimal getTotalDeContribuicaoGeral() {
        return totalDeContribuicaoGeral;
    }

    public void setTotalDeContribuicaoGeral(BigDecimal totalDeContribuicaoGeral) {
        this.totalDeContribuicaoGeral = totalDeContribuicaoGeral;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Integer getContrato() {
        return contrato;
    }

    public void setContrato(Integer contrato) {
        this.contrato = contrato;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public LinkedList<ItemContribuicaoIndividualizada> getItemContribuicaoIndividualizadas() {
        return itemContribuicaoIndividualizadas;
    }

    public void setItemContribuicaoIndividualizadas(LinkedList<ItemContribuicaoIndividualizada> itemContribuicaoIndividualizadas) {
        this.itemContribuicaoIndividualizadas = itemContribuicaoIndividualizadas;
    }
}
