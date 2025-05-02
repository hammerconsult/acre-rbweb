package br.com.webpublico.entidadesauxiliares.rh.relatorio;

import br.com.webpublico.entidades.Faltas;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by William on 17/10/2018.
 */
public class RelatorioConferenciaFaltas implements Serializable {
    private String nome;
    private String matricula;
    private Integer totalFaltas;
    private String orgao;
    private List<Faltas> faltas;

    public RelatorioConferenciaFaltas() {
        this.faltas = Lists.newArrayList();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Integer getTotalFaltas() {
        return totalFaltas;
    }

    public void setTotalFaltas(Integer totalFaltas) {
        this.totalFaltas = totalFaltas;
    }

    public List<Faltas> getFaltas() {
        return faltas;
    }

    public void setFaltas(List<Faltas> faltas) {
        this.faltas = faltas;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }
}
