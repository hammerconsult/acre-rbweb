package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ServicosAlvaraConstrucao;

import java.math.BigDecimal;

public class VOServicosAlvaraConstrucao {

    private String servico;
    private BigDecimal area;
    private String classe;
    private String itemServico;
    private String tipo;

    public VOServicosAlvaraConstrucao(ServicosAlvaraConstrucao servico) {
        this.servico = servico.getServicoConstrucao().toString();
        this.area = servico.getArea();
        this.classe = servico.getHabiteseClassesConstrucao() != null ? servico.getHabiteseClassesConstrucao().toString() : "";
        this.itemServico = servico.getItemServicoConstrucao() != null ? servico.getItemServicoConstrucao().toString() : "";
        if (servico.getServicoConstrucao().getTipoConstrucao() != null) {
            this.tipo = servico.getServicoConstrucao().getTipoConstrucao().getDescricao();
        } else {
            this.tipo = "";
        }
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getItemServico() {
        return itemServico;
    }

    public void setItemServico(String itemServico) {
        this.itemServico = itemServico;
    }
}
