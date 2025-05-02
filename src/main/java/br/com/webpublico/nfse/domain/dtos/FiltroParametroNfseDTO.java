package br.com.webpublico.nfse.domain.dtos;

import java.util.List;

public class FiltroParametroNfseDTO {

    private Integer page;
    private Integer per_page;
    private List<ParametrosFiltroNfseDTO> filtros;


    public FiltroParametroNfseDTO() {

    }


    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPer_page() {
        return per_page;
    }

    public void setPer_page(Integer per_page) {
        this.per_page = per_page;
    }

    public List<ParametrosFiltroNfseDTO> getFiltros() {
        return filtros;
    }

    public void setFiltros(List<ParametrosFiltroNfseDTO> filtros) {
        this.filtros = filtros;
    }
}
