package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.List;

public enum AtributoParamAmbiental implements EnumComDescricao {
    AREA_CONSTRUIDA("Área Construída (m²)", 1, "areaConstruida"),
    CAPACIDADE_INSTALADA("Capacidade Instalada (l)", 2, "capacidadeInstalada"),
    MATERIA_PRIMA("Matéria Prima", 3, "tipoMateriaPrima"),
    LOCALIZACAO("Localização", 4, "tipoLocalizacao"),
    GERACAO_RUIDOS_VIBRACAO("Geração de Ruídos e Vibração", 5, "geracaoRuidosEVibracao"),
    AREA_DESTINADA_CACAMBAS("Área Destinada Para Guardar as Caçambas, é Coberta e Impermeável", 6, "cobertaImpermeavel"),
    ESPECIES_PROTEGIDAS_POR_LEI("Espécies Protegidas Por Lei", 7, "especiesProtegidaPorLei"),
    QUANTIDADE_ESPECIES_SUPRIMIDAS("Quantidade de Espécies Suprimidas", 8, "quantidadeEspeciesSuprimidas"),
    AREA_TERRENO_PARTICULAR("Área Terreno Particular", 9, "areaTerreno");

    private final String descricao;
    private final Integer ordenacao;
    private final String nomeCampoEnquadramento;

    AtributoParamAmbiental(String descricao, Integer ordenacao, String nomeCampoEnquadramento) {
        this.descricao = descricao;
        this.ordenacao = ordenacao;
        this.nomeCampoEnquadramento = nomeCampoEnquadramento;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public Integer getOrdenacao() {
        return ordenacao;
    }

    public String getNomeCampoEnquadramento() {
        return nomeCampoEnquadramento;
    }

    public static List<AtributoParamAmbiental> buscarAtributosValor() {
        return Lists.newArrayList(AREA_CONSTRUIDA, CAPACIDADE_INSTALADA,
            QUANTIDADE_ESPECIES_SUPRIMIDAS, AREA_TERRENO_PARTICULAR);
    }

    public static List<AtributoParamAmbiental> buscarCamposSimNao() {
        return Lists.newArrayList(GERACAO_RUIDOS_VIBRACAO, AREA_DESTINADA_CACAMBAS,
            ESPECIES_PROTEGIDAS_POR_LEI);
    }
}
