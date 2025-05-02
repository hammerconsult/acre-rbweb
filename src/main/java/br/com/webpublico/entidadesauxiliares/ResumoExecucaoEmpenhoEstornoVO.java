package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.CategoriaOrcamentaria;

import java.math.BigDecimal;
import java.util.Date;

public class ResumoExecucaoEmpenhoEstornoVO {

    public Long id;
    public String numero;
    public Date data;
    public CategoriaOrcamentaria categoria;
    public String historico;
    public BigDecimal valor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public CategoriaOrcamentaria getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaOrcamentaria categoria) {
        this.categoria = categoria;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getUrlEstorno() {
        String caminho = categoria.isNormal()
            ? "/estorno-empenho/ver/"
            : "/cancelamento-empenho-restos-a-pagar/ver/";
        return caminho + id + "/";
    }
}
