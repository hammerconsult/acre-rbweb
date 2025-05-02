/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Peixe
 */
public enum CamposPesquisa{

    MATRICULA("Matrícula", 1),
    CARGO("Cargo", 2),
    SITUACAO_FUNCIONAL("Situação Funcional", 3),
    ORGAO("Órgão - Local de Trabalho", 4),
    ORGAO_RECURSOFP("Órgão - Recurso do Vínculo FP", 7),
    CATEGORIA("Categoria", 5),
    PERIODO("Período da Nomeação", 6),
    VERBA("Verba", 7);
    private String descricao;
    private Integer codigo;

    private CamposPesquisa(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    private CamposPesquisa() {
    }

    public static List<CamposPesquisa> findAll(String nome) {
        List<CamposPesquisa> aReturn = new ArrayList<>();
        for (CamposPesquisa campo : CamposPesquisa.values()) {
            if (nome.contains(campo.getDescricao())) {
                aReturn.add(campo);
            }
        }
        return aReturn;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
