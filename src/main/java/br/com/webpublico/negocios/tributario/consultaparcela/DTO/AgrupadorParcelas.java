package br.com.webpublico.negocios.tributario.consultaparcela.DTO;


import br.com.webpublico.tributario.consultadebitos.dtos.ParcelaDTO;

import java.util.List;

public class AgrupadorParcelas {

    private String emails;
    private List<ParcelaDTO> parcelas;

    public AgrupadorParcelas() {
    }

    public List<ParcelaDTO> getParcelas() {
        return this.parcelas;
    }

    public void setParcelas(List<ParcelaDTO> parcelas) {
        this.parcelas = parcelas;
    }

    public String getEmails() {
        return this.emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }
}
