package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class RecolhimentoFundoPrev {

    private Long recId;
    private String matricula;
    private BigDecimal base;
    private BigDecimal valor;
    private String nomeGrupo;
    private String orgao;
    private List<RecolhimentoFundoPrevMatricula> matriculas;

    public RecolhimentoFundoPrev() {
        matriculas = Lists.newArrayList();
    }

    public Long getRecId() {
        return recId;
    }

    public void setRecId(Long recId) {
        this.recId = recId;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public List<RecolhimentoFundoPrevMatricula> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(List<RecolhimentoFundoPrevMatricula> matriculas) {
        this.matriculas = matriculas;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }
}
