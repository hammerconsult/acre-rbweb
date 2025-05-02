/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.AlgorismoRomano;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author reidocrime
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

public class ClasseDiaria extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Código")
    private String codigo;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    @Etiqueta("Cargo Função")
    private String cargo;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor Estadual")
    private BigDecimal valorEstadual;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor Nacional")
    private BigDecimal valorNacional;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor Internacional ($)")
    private BigDecimal valorInternacional;
    @ManyToOne
    private ConfiguracaoDiaria configuracaoDiaria;

    public ClasseDiaria() {
        this.valorNacional = BigDecimal.ZERO;
        this.valorEstadual = BigDecimal.ZERO;
        this.valorInternacional = BigDecimal.ZERO;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }


    public String getCodigo() {
        return codigo;
    }

    public BigDecimal getValorInternacional() {
        return valorInternacional;
    }

    public void setValorInternacional(BigDecimal valorInternacional) {
        this.valorInternacional = valorInternacional;
    }

    public void setCodigo(String codigo) {
        if (codigo != null) {
            if ((codigo.length() > 0) && (this.id == null)) {
                this.codigo = converteParaAlgorismo(codigo);
            }

        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorEstadual() {
        return valorEstadual;
    }

    public void setValorEstadual(BigDecimal valorEstadual) {
        this.valorEstadual = valorEstadual;
    }

    public BigDecimal getValorNacional() {
        return valorNacional;
    }

    public void setValorNacional(BigDecimal valorNacional) {
        this.valorNacional = valorNacional;
    }

    public ConfiguracaoDiaria getConfiguracaoDiaria() {
        return configuracaoDiaria;
    }

    public void setConfiguracaoDiaria(ConfiguracaoDiaria configuracaoDiaria) {
        this.configuracaoDiaria = configuracaoDiaria;
    }

    private String converteParaAlgorismo(String numero) {
        int n = Integer.parseInt(numero);
        return AlgorismoRomano.converteNumero(n);
    }

    @Override
    public String toString() {
        return "Classe " + codigo + " - " + cargo;
    }

    public String toStringClasseDiaria(){
        int tamanho = 57;
        return "Classe " + codigo + " - " + (cargo.length() >= tamanho ? cargo.substring(0, tamanho) + "..." : cargo);
    }
}
