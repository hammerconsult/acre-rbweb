package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;
public enum MotivoCancelamentoNfseDTO implements NfseEnumDTO {
    ERRO_EMISSAO("Erro na Emissão", 1),
    SERVICO_NAO_PRESTADO("Serviço não prestado", 2),
    ERRO_ASSINATURA("Erro na assinatura", 3),
    DUPLICIDADE("Duplicidade", 4),
    ERRO_PROCESSAMENTO("Erro de processamento", 5),
    ERRO_DADOS("Erro de dados na Emissão", 6);

    private String descricao;
    private Integer codigo;

    MotivoCancelamentoNfseDTO(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public static MotivoCancelamentoNfseDTO findByCodigo(Integer codigo) {
        for (MotivoCancelamentoNfseDTO motivo : MotivoCancelamentoNfseDTO.values()) {
            if (motivo.codigo.equals(codigo)) {
                return motivo;
            }
        }
        return null;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }
}
