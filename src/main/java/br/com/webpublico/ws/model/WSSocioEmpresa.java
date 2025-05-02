package br.com.webpublico.ws.model;


import br.com.webpublico.entidades.SociedadeCadastroEconomico;

import java.math.BigDecimal;

public class WSSocioEmpresa {
    private String socio;
    private BigDecimal proporcao;

    public WSSocioEmpresa() {
    }


    public WSSocioEmpresa(SociedadeCadastroEconomico sociedade) {
        this.socio = sociedade.getSocio().getNome();
        this.proporcao = BigDecimal.valueOf(sociedade.getProporcao());
    }

    public String getSocio() {
        return socio;
    }

    public void setSocio(String socio) {
        this.socio = socio;
    }

    public BigDecimal getProporcao() {
        return proporcao;
    }

    public void setProporcao(BigDecimal proporcao) {
        this.proporcao = proporcao;
    }
}
