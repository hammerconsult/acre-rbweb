package br.com.webpublico.nfse.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.nfse.domain.dtos.enums.TipoDeclaracaoMensalServicoNfseDTO;

public enum TipoDeclaracaoMensalServico implements EnumComDescricao {

    PRINCIPAL("Principal", TipoDeclaracaoMensalServicoNfseDTO.PRINCIPAL),
    COMPLEMENTAR("Complementar", TipoDeclaracaoMensalServicoNfseDTO.COMPLEMENTAR);

    private String descricao;
    private TipoDeclaracaoMensalServicoNfseDTO dto;

    TipoDeclaracaoMensalServico(String descricao, TipoDeclaracaoMensalServicoNfseDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoDeclaracaoMensalServicoNfseDTO toDto() {
        return dto;
    }
}
