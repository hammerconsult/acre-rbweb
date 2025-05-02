package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.enums.Mes;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DashboardPrestadorDTO {


    //Filtros
    private Date dataInicial;
    private Date dataFinal;
    private CadastroEconomico cadastroEconomico;


    //resultado
    private List<EstatisticaMensalNfseDTO> estatisticas;
    private List<EstatisticaMensalPorServicoNfseDTO> estatisticasPorServico;
    private List<EstatisticaMensalPorTomadorNfseDTO> estatisticasPorTomador;

    public DashboardPrestadorDTO() {

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

    public List<EstatisticaMensalNfseDTO> getEstatisticas() {
        return estatisticas;
    }

    public void setEstatisticas(List<EstatisticaMensalNfseDTO> estatisticas) {
        this.estatisticas = estatisticas;
        Collections.sort(this.estatisticas, new Comparator<EstatisticaMensalNfseDTO>() {
            @Override
            public int compare(EstatisticaMensalNfseDTO o1, EstatisticaMensalNfseDTO o2) {
                int i = o1.getAno().compareTo(o2.getAno());
                return i == 0 ? o1.getMes().compareTo(o2.getMes()): i;
            }
        });
    }

    public List<EstatisticaMensalPorServicoNfseDTO> getEstatisticasPorServico() {
        return estatisticasPorServico;
    }

    public void setEstatisticasPorServico(List<EstatisticaMensalPorServicoNfseDTO> estatisticasPorServico) {
        this.estatisticasPorServico = estatisticasPorServico;
    }

    public List<EstatisticaMensalPorTomadorNfseDTO> getEstatisticasPorTomador() {
        return estatisticasPorTomador;
    }

    public void setEstatisticasPorTomador(List<EstatisticaMensalPorTomadorNfseDTO> estatisticasPorTomador) {
        this.estatisticasPorTomador = estatisticasPorTomador;
    }

    public String getMes(Integer mes) {
        return Mes.getMesToInt(mes).getDescricao();
    }
}
