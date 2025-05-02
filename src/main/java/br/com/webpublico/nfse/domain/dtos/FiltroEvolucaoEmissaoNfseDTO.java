package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.enums.Mes;

import java.util.Date;

public class FiltroEvolucaoEmissaoNfseDTO {

    //Filtros
    private Date dataInicial;
    private Date dataFinal;
    private CadastroEconomico cadastroEconomico;

    public FiltroEvolucaoEmissaoNfseDTO() {
    }


    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public String getMes(Integer mes) {
        return Mes.getMesToInt(mes).getDescricao();
    }
}
