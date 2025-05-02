package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.CadastroEconomico;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 15/01/14
 * Time: 09:32
 * To change this template use File | Settings | File Templates.
 */
public class WSDadosConsultaCmcSaida {
    private List<ContribuinteConsulta> CMCXML;

    public WSDadosConsultaCmcSaida() {
        this.CMCXML = new ArrayList<>();
    }

    public List<ContribuinteConsulta> getCMCXML() {
        return CMCXML;
    }

    public void setCMCXML(List<ContribuinteConsulta> contribuintes) {
        this.CMCXML = contribuintes;
    }

    public void adicionarContribuinte(CadastroEconomico cmc) {
        this.CMCXML.add(new ContribuinteConsulta(cmc.getInscricaoCadastral(), cmc.getPessoa().getNome()));
    }
}
