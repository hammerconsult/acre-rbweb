package br.com.webpublico.negocios.tributario.arrecadacao;

import br.com.webpublico.entidades.ParcelaValorDivida;
import com.google.common.collect.Lists;

import java.util.List;

public class DepoisDePagarResquest {
    private String nome;
    private List<ParcelaValorDivida> parcelas = Lists.newArrayList();

    public String getNome() {
        return nome;
    }

    public List<ParcelaValorDivida> getParcelas() {
        return parcelas;
    }

    public DepoisDePagarResquest nome(String nome) {
        this.nome = nome;
        return this;
    }

    public DepoisDePagarResquest parcelas(List<ParcelaValorDivida> parcelas) {
        this.parcelas = parcelas;
        return this;
    }

    public void addParcelas(ParcelaValorDivida parcela) {
        this.parcelas.add(parcela);
    }

    public static DepoisDePagarResquest build() {
        return new DepoisDePagarResquest();
    }
}
