package br.com.webpublico.entidadesauxiliares.rh.relatorio;

import java.math.BigDecimal;

/**
 * Created by William on 20/08/2018.
 */
public class RelatorioDuplicidadeFolhaPagamento {
    private String matricula;
    private String nome;
    private String secretaria;
    private BigDecimal valor;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
