package br.com.webpublico.entidadesauxiliares;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AssistenteImpressaoMalaDireta {

    private String usuario;
    private Long idMalaDireta;
    private Integer total;
    private Integer gerados;
    private List jaspers;

    public AssistenteImpressaoMalaDireta() {
        total = 0;
        gerados = 0;
        jaspers = new ArrayList();
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Long getIdMalaDireta() {
        return idMalaDireta;
    }

    public void setIdMalaDireta(Long idMalaDireta) {
        this.idMalaDireta = idMalaDireta;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setGerados(Integer gerados) {
        this.gerados = gerados;
    }

    public List getJaspers() {
        return jaspers;
    }

    public void setJaspers(List jaspers) {
        this.jaspers = jaspers;
    }

    public void contar() {
        gerados++;
    }

    public void contar(int quantidade) {
        gerados = gerados + quantidade;
    }

    public int getPorcentagemGerados() {
        if (gerados == null || total == null) {
            return 0;
        }
        return BigDecimal.valueOf((gerados.doubleValue() / total.doubleValue()) * 100).setScale(0, RoundingMode.UP).intValue();
    }
}
