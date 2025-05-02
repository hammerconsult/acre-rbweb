package br.com.webpublico.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Fabio
 */
public enum TipoUsuarioTributario {

    FISCAL_OBRAS("1 - Fiscal de Obras"),
    FISCAL_TRANSITO("2 - Fiscal de Trânsito"),
    FISCAL_TRANSITO_TRANSPORTE("3 - Fiscal de Trânsito e Transporte"),
    FISCAL_TRANSPORTE("4 - Fiscal de Transporte"),
    FISCAL_TRIBUTARIO("5 - Fiscal Tributário"),
    JULGADOR_CONTENCIOSO_ALVARA("6 - Julgador Contencioso de Alvará"),
    JULGADOR_CONTENCIOSO_CMC("7 - Julgador Contencioso de C.M.C."),
    JULGADOR_CONTENCIOSO_IPTU("8 - Julgador Contencioso de IPTU"),
    JULGADOR_CONTECIOSO_ISSQN("9 - Julgador Contencioso de ISSQN"),
    JULGADOR_CONTENCIOSO("10 - Julgador Contencioso"),
    PROCURADOR_MUNICIPAL("11 - Procurador Municipal"),
    RESPONSAVEL_JURIDICO("12 - Responsável Jurídico"),
    SERVIDOR_CADASTRO("13 - Servidor de Cadastro"),
    SERVIDOR_TRIBUTARIO("14 - Servidor Tributário"),
    TECNICO_ANALISE_PROJETO("15 - Técnico de Análise de Projeto Fiscal Sanitário"),
    FISCAL_MEIO_AMBIENTE("16 - Fiscal de Meio Ambiente"),
    AVALIADOR_ITBI("17 - Avaliador de ITBI"),
    FISCAL_SANITARIO("18 - Fiscal Sanitário"),
    GESTOR_RBTRANS("19 - Gestão RBTrans"),
    DIRETOR_DPTO_ADM_TRIBUTARIA("20 - Diretor do Departamento de Admnistração Tributária"),
    CHEFE_DIVISAO_IPTU("21 - Chefe da Divisão de IPTU"),
    USUARIO_DIVISAO_IPTU("22 - Usuário da Divisão de IPTU");

    private String descricao;

    private TipoUsuarioTributario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static List<String> buscarTipoUsuarioIsencaoIPTU() {
        return Lists.newArrayList(DIRETOR_DPTO_ADM_TRIBUTARIA.name(), CHEFE_DIVISAO_IPTU.name(), USUARIO_DIVISAO_IPTU.name());
    }

}
