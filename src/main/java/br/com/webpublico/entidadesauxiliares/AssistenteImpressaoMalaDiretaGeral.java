package br.com.webpublico.entidadesauxiliares;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AssistenteImpressaoMalaDiretaGeral {

    private Integer total;
    private Integer gerados;
    private List<ImpressaoMalaDiretaGeral> itens;
    private List jaspers;

    public AssistenteImpressaoMalaDiretaGeral(List<ImpressaoMalaDiretaGeral> itens) {
        this.itens = itens;
        total = itens.size();
        gerados = 0;
        jaspers = new ArrayList();
    }

    public AssistenteImpressaoMalaDiretaGeral() {
        total = 0;
        gerados = 0;
        jaspers = new ArrayList();
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<ImpressaoMalaDiretaGeral> getItens() {
        return itens;
    }

    public void setItens(List<ImpressaoMalaDiretaGeral> itens) {
        this.itens = itens;
        this.total = itens.size();
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
        return new BigDecimal(((gerados.doubleValue() / total.doubleValue()) * 100)).setScale(0, RoundingMode.UP).intValue();
    }
}
