package br.com.webpublico.nfse.domain;

import br.com.webpublico.nfse.domain.dtos.enums.ConstrucaoCivilStatusNfseDTO;

public enum ConstrucaoCivilStatus {

    APROVADA("Aprovada", ConstrucaoCivilStatusNfseDTO.APROVADA),
    NAO_INICIADA("Não iniciada", ConstrucaoCivilStatusNfseDTO.NAO_INICIADA),
    EM_ANDAMENTO("Em andamento", ConstrucaoCivilStatusNfseDTO.EM_ANDAMENTO),
    CONCLUIDA("Concluída", ConstrucaoCivilStatusNfseDTO.CONCLUIDA),
    PARALIZADA("Paralizada", ConstrucaoCivilStatusNfseDTO.PARALIZADA);

    private String descricao;
    private ConstrucaoCivilStatusNfseDTO dto;

    ConstrucaoCivilStatus(String descricao, ConstrucaoCivilStatusNfseDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public static ConstrucaoCivilStatus fromDTO(ConstrucaoCivilStatusNfseDTO status) {
        for (int i = 0; i < values().length; i++) {
            ConstrucaoCivilStatus value = values()[i];
            if (value.getDto().equals(status)) {
                return value;
            }
        }
        return null;
    }

    public String getDescricao() {
        return descricao;
    }

    public ConstrucaoCivilStatusNfseDTO getDto() {
        return dto;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public ConstrucaoCivilStatusNfseDTO toDTO() {
        return dto;
    }
}
