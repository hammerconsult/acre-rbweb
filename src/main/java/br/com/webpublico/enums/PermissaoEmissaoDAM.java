package br.com.webpublico.enums;

import br.com.webpublico.tributario.consultadebitos.dtos.DTOConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.enums.EnumConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.enums.PermissaoEmissaoDAMDTO;

/**
 * Created by Fabio on 30/01/2017.
 */
public enum PermissaoEmissaoDAM implements EnumConsultaDebitos {

    HABILITA("Habilita Impressão de DAM", PermissaoEmissaoDAMDTO.HABILITA),
    HABILITA_ATE_VENCIMENTO_PARCELA("Habilita Impressão de DAM - Até o vencimento da parcela", PermissaoEmissaoDAMDTO.HABILITA_ATE_VENCIMENTO_PARCELA),
    BLOQUEIA("Bloqueia Impressão de DAM", PermissaoEmissaoDAMDTO.BLOQUEIA),
    HABILITA_BLOQUEIA_EXERCICIOS_POSTERIORES("Bloqueia Impressão de DAM para exercícios posteriores ao atual", PermissaoEmissaoDAMDTO.HABILITA_BLOQUEIA_EXERCICIOS_POSTERIORES),
    HABILITA_EXERCICIOS_POSTERIORES_VENCIMENTO_EXERCICIO_ATUAL("Habilita a Impressão de DAM para exercícios posteriores ao atual com vencimento no exercício atual", PermissaoEmissaoDAMDTO.HABILITA_EXERCICIOS_POSTERIORES_VENCIMENTO_EXERCICIO_ATUAL);

    private String descricao;
    private PermissaoEmissaoDAMDTO dto;

    PermissaoEmissaoDAM(String descricao, PermissaoEmissaoDAMDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public DTOConsultaDebitos toDto() {
        return dto;
    }
}
