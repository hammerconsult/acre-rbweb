package br.com.webpublico.nfse.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.nfse.domain.dtos.enums.ExigibilidadeNfseDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * The Exigibilidade enumeration.
 */
public enum Exigibilidade implements EnumComDescricao {
    TRIBUTACAO_MUNICIPAL("1", "ISSQN a Recolher", ExigibilidadeNfseDTO.TRIBUTACAO_MUNICIPAL),
    NAO_INCIDENCIA("2", "Não incidência", ExigibilidadeNfseDTO.NAO_INCIDENCIA),
    ISENCAO("3", "Isenção", ExigibilidadeNfseDTO.ISENCAO),
    EXPORTACAO("4", "Exportação", ExigibilidadeNfseDTO.EXPORTACAO),
    IMUNIDADE("5", "Imunidade", ExigibilidadeNfseDTO.IMUNIDADE),
    EXIGIBILIDADE_SUSPENSA_DECISAO_JUDICIAL("6", "Exigibilidade Suspensa por Decisão Judicial", ExigibilidadeNfseDTO.EXIGIBILIDADE_SUSPENSA_DECISAO_JUDICIAL),
    EXIGIBILIDADE_SUSPENSA_PROCESSO_ADMINISTRATIVO("7", "Exigibilidade Suspensa por Processo Administrativo", ExigibilidadeNfseDTO.EXIGIBILIDADE_SUSPENSA_PROCESSO_ADMINISTRATIVO),
    RETENCAO("8", "Retenção do ISSQN", ExigibilidadeNfseDTO.RETENCAO),
    SIMPLES_NACIONAL("9", "Simples Nacional", ExigibilidadeNfseDTO.SIMPLES_NACIONAL),
    RETENCAO_SIMPLES_NACIONAL("10", "Retenção Simples", ExigibilidadeNfseDTO.RETENCAO_SIMPLES_NACIONAL),
    TRIBUTACAO_FORA_MUNICIPIO("11", "Tributação fora do município", ExigibilidadeNfseDTO.TRIBUTACAO_FORA_MUNICIPIO);

    private String codigo;
    private String descricao;
    private ExigibilidadeNfseDTO dto;

    Exigibilidade(String codigo, String descricao, ExigibilidadeNfseDTO dto) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.dto = dto;
    }

    public static Exigibilidade fromDto(ExigibilidadeNfseDTO dto) {
        for (int i = 0; i < values().length; i++) {
            Exigibilidade value = values()[i];
            if (value.toDto().equals(dto)) {
                return value;
            }
        }
        return null;
    }

    public static Exigibilidade getByCodigo(String codigo) {
        for (Exigibilidade exigibilidade : values()) {
            if (exigibilidade.codigo.equals(codigo)) {
                return exigibilidade;
            }
        }

        return null;
    }

    public static List<String> getDescricoes() {
        List<String> descricoes = Lists.newArrayList();
        for (Exigibilidade e : values()) {
            descricoes.add(e.getDescricao());
        }
        return descricoes;
    }

    public static List<String> getNames() {
        List<String> names = Lists.newArrayList();
        for (Exigibilidade e : values()) {
            names.add(e.name());
        }
        return names;
    }


    public String getDescricao() {
        return descricao;
    }

    public ExigibilidadeNfseDTO toDto() {
        return dto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
