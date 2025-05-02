package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.ServicosAlvaraConstrucao;

import java.math.BigDecimal;

public class WsServicosAlvaraConstrucao {

    private Long id;
    private String servicoConstrucao;
    private BigDecimal area;
    private String habiteseClassesConstrucao;
    private String itemServico;

    public WsServicosAlvaraConstrucao() {

    }

    public WsServicosAlvaraConstrucao(ServicosAlvaraConstrucao servicosAlvaraConstrucao) {
        this.id = servicosAlvaraConstrucao.getId();
        this.servicoConstrucao = servicosAlvaraConstrucao.getServicoConstrucao().toString();
        this.area = servicosAlvaraConstrucao.getArea();
        this.habiteseClassesConstrucao = servicosAlvaraConstrucao.getHabiteseClassesConstrucao() != null ? servicosAlvaraConstrucao.getHabiteseClassesConstrucao().toString() : "";
        this.itemServico = servicosAlvaraConstrucao.getItemServicoConstrucao() != null ? servicosAlvaraConstrucao.getItemServicoConstrucao().toString() : "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServicoConstrucao() {
        return servicoConstrucao;
    }

    public void setServicoConstrucao(String servicoConstrucao) {
        this.servicoConstrucao = servicoConstrucao;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getHabiteseClassesConstrucao() {
        return habiteseClassesConstrucao;
    }

    public void setHabiteseClassesConstrucao(String habiteseClassesConstrucao) {
        this.habiteseClassesConstrucao = habiteseClassesConstrucao;
    }

    public String getItemServico() {
        return itemServico;
    }

    public void setItemServico(String itemServico) {
        this.itemServico = itemServico;
    }
}
