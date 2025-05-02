package br.com.webpublico.entidadesauxiliares;


import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ArquivoRCB001 extends ArquivoBancarioTributario {
    private HeaderRCB001 header;
    private List<RegistroRCB001> registros;
    private TraillerRCB001 trailler;
    private BigDecimal valorTotal;
    private Integer totalLinhas;

    public ArquivoRCB001() {
        registros = Lists.newArrayList();
        valorTotal = BigDecimal.ZERO;
        totalLinhas = 0;
    }

    @Override
    public Long getId() {
        return null;
    }

    public HeaderRCB001 getHeader() {
        return header;
    }

    public void setHeader(HeaderRCB001 header) {
        this.header = header;
    }

    public List<RegistroRCB001> getRegistros() {
        return registros;
    }

    public void setRegistros(List<RegistroRCB001> registros) {
        this.registros = registros;
    }

    public TraillerRCB001 getTrailler() {
        return trailler;
    }

    public void setTrailler(TraillerRCB001 trailler) {
        this.trailler = trailler;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void somarValor(BigDecimal valor) {
        valorTotal = valorTotal.add(valor);
    }

    public void adicionarRegisto(RegistroRCB001 registroRCB001) {
        somarValor(registroRCB001.getValorRecebido());
        registros.add(registroRCB001);
    }

    public void contarLinha() {
        totalLinhas++;
    }

    @Override
    public int getQuantidadeRegistros() {
        return this.getRegistros() != null ? this.getRegistros().size() : 0;
    }

    public Integer getTotalLinhas() {
        return totalLinhas;
    }

    public void setTotalLinhas(Integer totalLinhas) {
        this.totalLinhas = totalLinhas;
    }

    public boolean isValorTotalValido() {
        try {
            return (getValorTotal().compareTo(getTrailler().getValorTotal()) == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTotalLinhasValido() {
        try {
            return (getTotalLinhas().equals(getTrailler().getTotalRegistros()));
        } catch (Exception e) {
            return false;
        }
    }

}
