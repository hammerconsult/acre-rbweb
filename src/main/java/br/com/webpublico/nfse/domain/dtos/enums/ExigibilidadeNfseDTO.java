package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum ExigibilidadeNfseDTO implements NfseEnumDTO {
    TRIBUTACAO_MUNICIPAL(1, 1, "ISSQN a Recolher"),
    NAO_INCIDENCIA(2, 2, "Não Incidência"),
    ISENCAO(3, 3, "Isenção"),
    EXPORTACAO(4, 2, "Exportação"),
    IMUNIDADE(5, 4, "Imunidade"),
    EXIGIBILIDADE_SUSPENSA_DECISAO_JUDICIAL(6, 5, "Exigibilidade Suspensa por Decisão Judicial"),
    EXIGIBILIDADE_SUSPENSA_PROCESSO_ADMINISTRATIVO(7, 6, "Exigibilidade Suspensa por Processo Administrativo"),
    RETENCAO(8, 1, "Retenção do ISSQN"),
    SIMPLES_NACIONAL(9, 1, "Simples Nacional"),
    RETENCAO_SIMPLES_NACIONAL(10, 1, "Retenção Simples"),
    TRIBUTACAO_FORA_MUNICIPIO(11, 2, "Tributação fora do município");

    private Integer codigo;
    private Integer codigoV1;
    private String descricao;

    ExigibilidadeNfseDTO(Integer codigo, Integer codigoV1, String descricao) {
        this.codigo = codigo;
        this.codigoV1 = codigoV1;
        this.descricao = descricao;
    }

    public static ExigibilidadeNfseDTO getByCodigo(Integer codigo) {
        for (ExigibilidadeNfseDTO exigibilidade : values()) {
            if (exigibilidade.codigo.equals(codigo)) {
                return exigibilidade;
            }
        }
        return null;
    }

    public static ExigibilidadeNfseDTO getByCodigoV1(Integer codigo) {
        for (ExigibilidadeNfseDTO exigibilidade : values()) {
            if (exigibilidade.codigoV1.equals(codigo)) {
                return exigibilidade;
            }
        }
        return null;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public Integer getCodigoV1() {
        return codigoV1;
    }

    public String getDescricao() {
        return descricao;
    }
}
