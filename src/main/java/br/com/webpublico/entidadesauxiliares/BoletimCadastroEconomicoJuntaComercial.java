package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class BoletimCadastroEconomicoJuntaComercial implements Serializable {
    private String numProtocoloJuntaComercial;
    private String numProtocoloViabilidade;

    public BoletimCadastroEconomicoJuntaComercial() {
    }

    public String getNumProtocoloJuntaComercial() {
        return numProtocoloJuntaComercial;
    }

    public void setNumProtocoloJuntaComercial(String numProtocoloJuntaComercial) {
        this.numProtocoloJuntaComercial = numProtocoloJuntaComercial;
    }

    public String getNumProtocoloViabilidade() {
        return numProtocoloViabilidade;
    }

    public void setNumProtocoloViabilidade(String numProtocoloViabilidade) {
        this.numProtocoloViabilidade = numProtocoloViabilidade;
    }
}
