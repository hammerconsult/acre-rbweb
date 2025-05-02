/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author terminal3
 */
@GrupoDiagrama(nome = "CadastroUnico")
public enum TipoEndereco {
    RESIDENCIAL("Residêncial", br.com.webpublico.tributario.enumeration.TipoEndereco.RESIDENCIAL, "Secundário"),
    COMERCIAL("Comercial", br.com.webpublico.tributario.enumeration.TipoEndereco.COMERCIAL, "Principal"),
    COBRANCA("Cobrança", br.com.webpublico.tributario.enumeration.TipoEndereco.COBRANCA, "Secundário"),
    CORRESPONDENCIA("Correspondência", br.com.webpublico.tributario.enumeration.TipoEndereco.CORRESPONDENCIA, "Secundário"),
    RURAL("Rural", br.com.webpublico.tributario.enumeration.TipoEndereco.RURAL, "Secundário"),
    DOMICILIO_FISCAL("Domicílio Fiscal", br.com.webpublico.tributario.enumeration.TipoEndereco.DOMICILIO_FISCAL, "Secundário"),
    OUTROS("Outros", br.com.webpublico.tributario.enumeration.TipoEndereco.OUTROS, "Secundário");
    private String descricao;
    private br.com.webpublico.tributario.enumeration.TipoEndereco tipoEnderecoDTO;
    private String principalOuSecundario;

    TipoEndereco(String descricao, br.com.webpublico.tributario.enumeration.TipoEndereco tipoEnderecoDTO,
                 String principalOuSecundario) {
        this.descricao = descricao;
        this.tipoEnderecoDTO = tipoEnderecoDTO;
        this.principalOuSecundario = principalOuSecundario;
    }

    public String getDescricao() {
        return descricao;
    }

    public br.com.webpublico.tributario.enumeration.TipoEndereco getTipoEnderecoDTO() {
        return tipoEnderecoDTO;
    }

    public String getPrincipalOuSecundario() {
        return principalOuSecundario;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static TipoEndereco getTipoEnderecoPorTipoEnderecoDTO(br.com.webpublico.tributario.enumeration.TipoEndereco tipoEnderecoDTO) {
        for (TipoEndereco tipoEndereco : TipoEndereco.values()) {
            if (tipoEndereco.getTipoEnderecoDTO().equals(tipoEnderecoDTO)) {
                return tipoEndereco;
            }
        }
        return null;
    }
}
