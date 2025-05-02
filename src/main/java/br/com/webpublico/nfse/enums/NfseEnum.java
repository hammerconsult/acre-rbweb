package br.com.webpublico.nfse.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public interface NfseEnum<T> {

    NfseEnumDTO toDto();
}
