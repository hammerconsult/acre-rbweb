package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.CalculoITBI;
import br.com.webpublico.entidades.ProcessoCalculoITBI;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class WSItbi implements Serializable {

    private Long id;
    private Integer ano;
    private Long numero;
    private WSImovel imovel;
    private Boolean isImobiliario = true;
    private List<WSCalculoItbi> calculosItbi;

    public WSItbi() {
        this.calculosItbi = Lists.newArrayList();
    }

    public WSItbi(ProcessoCalculoITBI processo) {
        this();
        this.id = processo.getId();
        isImobiliario = processo.isImobiliario();
        if (processo.isImobiliario()) {
            this.imovel = new WSImovel(processo.getCadastroImobiliario());
        } else {
            this.imovel = new WSImovel(processo.getCadastroRural());
        }
        processo.getCalculos().sort(new Comparator<CalculoITBI>() {
            @Override
            public int compare(CalculoITBI c1, CalculoITBI c2) {
                return c1.getOrdemCalculo().compareTo(c2.getOrdemCalculo());
            }
        });
        for (CalculoITBI calculo : processo.getCalculos()) {
            this.calculosItbi.add(new WSCalculoItbi(calculo));
        }
        this.numero = processo.getCodigo();
        this.ano = processo.getExercicio().getAno();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public WSImovel getImovel() {
        return imovel;
    }

    public void setImovel(WSImovel imovel) {
        this.imovel = imovel;
    }

    public List<WSCalculoItbi> getCalculosItbi() {
        return calculosItbi;
    }

    public void setCalculosItbi(List<WSCalculoItbi> calculosItbi) {
        this.calculosItbi = calculosItbi;
    }

    public Boolean getImobiliario() {
        return isImobiliario;
    }

    public void setImobiliario(Boolean imobiliario) {
        isImobiliario = imobiliario;
    }
}
