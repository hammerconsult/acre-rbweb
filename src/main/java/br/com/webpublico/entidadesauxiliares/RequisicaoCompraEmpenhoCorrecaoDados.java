package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class RequisicaoCompraEmpenhoCorrecaoDados {

    Long idExecucaoEmpenho;
    Long idEmpenho;
    Long idGrupoEmpenho;
    String numeroEmpenho;
    String codigoGrupo;
    String codigoConta;
    BigDecimal valorEmpenho;
    private List<RequisicaoCompraEmpenhoGrupoCorrecaoDados> gruposEmpenho;

    public RequisicaoCompraEmpenhoCorrecaoDados() {
        gruposEmpenho = Lists.newArrayList();
    }

    public Long getIdExecucaoEmpenho() {
        return idExecucaoEmpenho;
    }

    public void setIdExecucaoEmpenho(Long idExecucaoEmpenho) {
        this.idExecucaoEmpenho = idExecucaoEmpenho;
    }

    public Long getIdEmpenho() {
        return idEmpenho;
    }

    public void setIdEmpenho(Long idEmpenho) {
        this.idEmpenho = idEmpenho;
    }

    public Long getIdGrupoEmpenho() {
        return idGrupoEmpenho;
    }

    public void setIdGrupoEmpenho(Long idGrupoEmpenho) {
        this.idGrupoEmpenho = idGrupoEmpenho;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public String getCodigoConta() {
        return codigoConta;
    }

    public void setCodigoConta(String codigoConta) {
        this.codigoConta = codigoConta;
    }

    public BigDecimal getValorEmpenho() {
        return valorEmpenho;
    }

    public void setValorEmpenho(BigDecimal valorEmpenho) {
        this.valorEmpenho = valorEmpenho;
    }

    public List<RequisicaoCompraEmpenhoGrupoCorrecaoDados> getGruposEmpenho() {
        return gruposEmpenho;
    }

    public void setGruposEmpenho(List<RequisicaoCompraEmpenhoGrupoCorrecaoDados> gruposEmpenho) {
        this.gruposEmpenho = gruposEmpenho;
    }
}
