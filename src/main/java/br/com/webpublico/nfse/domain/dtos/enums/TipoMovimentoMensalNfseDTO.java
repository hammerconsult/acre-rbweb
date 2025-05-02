package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum TipoMovimentoMensalNfseDTO implements NfseEnumDTO {
    NORMAL, RETENCAO, INSTITUICAO_FINANCEIRA, ISS_RETIDO;
}
