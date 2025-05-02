package br.com.webpublico.pncp.dto;

import java.util.List;

public class ContratacaoErroDTO {

    private List<ErroDTO> erros;

    public List<ErroDTO> getErros() {
        return erros;
    }

    public void setErros(List<ErroDTO> erros) {
        this.erros = erros;
    }
}
