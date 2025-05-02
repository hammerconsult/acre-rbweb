package br.com.webpublico.enums.rh.esocial;

import com.google.common.collect.Lists;

import java.util.List;

public enum TipoLotacaoTributariaESocial {
    CLASSIFICACAO_ATIVIDADE_PESSOA_JURIDICA("Classificação da atividade econômica exercida pela Pessoa Jurídica para fins de atribuição de código FPAS, inclusive obras de construção civil própria, exceto: a) empreitada parcial ou sub-empreitada de obra de construção civil (utilizar opção 02); b) prestação de serviços em instalações de terceiros (utilizar opções 03 a 09); c) Embarcação inscrita no Registro Especial Brasileiro - REB(utilizar opção 10).", "01"),
    OBRA_CONSTRUCAO_CIVIL("Obra de Construção Civil - Empreitada Parcial ou Sub-empreitada", "02"),
    PESSOA_FISICA_TOMADORA_SERVICOS("Pessoa Física Tomadora de Serviços prestados mediante cessão de mão de obra, exceto contratante de cooperativa", "03"),
    PESSOA_JURIDICA_TOMADORA_SERVICOS_8_LEI_212_1991("Pessoa Jurídica Tomadora de Serviços prestados mediante cessão de mão de obra, exceto contratante de cooperativa, nos\n" +
        "termos da lei 8.212/1991", "04"),
    PESSOA_JURIDICA_TOMADORA_SERVICOS_COOPERATIVA("Pessoa Jurídica Tomadora de Serviços prestados por cooperados por intermédio de cooperativa de trabalho, exceto aqueles\n" +
        "prestados a entidade beneficente/isenta", "05"),
    ENTIDADE_ISENTA_TOMRADORA_SERVICOS("Entidade beneficente/isenta Tomadora de Serviços prestados por cooperados por intermédio de cooperativa de trabalho", "06"),
    PESSOA_FISICA_TOMADORA_SERVICOS_POR_COOPERATIVAS("Pessoa Física tomadora de Serviços prestados por Cooperados por intermédio de Cooperativa de Trabalho", "07"),

    OPERADOR_PORTUARIO_TOMADOR("Operador Portuário tomador de serviços de trabalhadores avulsos", "08"),

    CONTRATANTE_TRABALHADORES_NAO_PORTUARIOS_POR_SINDICATO("Contratante de trabalhadores avulsos não portuários por intermédio de Sindicato", "09"),
    EMBARCACAO_INSCRITA_REB("Embarcação inscrita no Registro Especial Brasileiro - REB", "10"),
    CLASSIFICACAO_OBRA_PROPRIA_PESSOA_FISICA("Classificação da atividade econômica ou obra própria de construção civil da Pessoa Física", "21"),
    EMPREGADOR_DOMESTICO("Empregador Doméstico", "24"),
    RGPS_EXTERIOR("Atividades desenvolvidas no exterior por trabalhador vinculado ao Regime Geral de Previdência Social (expatriados)", "90"),
    RGPS_ESTRANGEIRO("Atividades desenvolvidas por trabalhador estrangeiro vinculado a Regime de Previdência Social Estrangeiro", "91");


    private String descricao;
    private String codigo;

    TipoLotacaoTributariaESocial(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        String s = codigo + " - " + descricao;
        return s.length() > 150 ? s.substring(0, 149) + "..." : s;
    }

    public static boolean preencherTpIns(TipoLotacaoTributariaESocial tipo) {
        List<TipoLotacaoTributariaESocial> lista = Lists.newArrayList();
        lista.add(CLASSIFICACAO_ATIVIDADE_PESSOA_JURIDICA);
        lista.add(EMBARCACAO_INSCRITA_REB);
        lista.add(CLASSIFICACAO_OBRA_PROPRIA_PESSOA_FISICA);
        lista.add(EMPREGADOR_DOMESTICO);
        lista.add(RGPS_EXTERIOR);
        lista.add(RGPS_ESTRANGEIRO);

        return lista.contains(tipo);
    }
}
