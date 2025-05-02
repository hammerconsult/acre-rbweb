package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Tributo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Fabio on 16/04/2018.
 */
public class IntegraTributoDam {
    public Long idTributo;
    public Tributo.TipoTributo tipoTributo;
    public Boolean dividaAtiva;
    public BigDecimal valor;
    public Long idLoteBaixa;
    public Date dataPagamento;
    public Long idEntidade;
    public Integer exercicio;

    public IntegraTributoDam(Long idTributo, Boolean dividaAtiva, BigDecimal valor, Long idLoteBaixa, Date dataPagamento, Long idEntidade, Integer exercicio, Tributo.TipoTributo tipoTributo) {
        this.idTributo = idTributo;
        this.dividaAtiva = dividaAtiva;
        this.valor = valor;
        this.idLoteBaixa = idLoteBaixa;
        this.dataPagamento = dataPagamento;
        this.idEntidade = idEntidade;
        this.exercicio = exercicio;
        this.tipoTributo = tipoTributo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntegraTributoDam that = (IntegraTributoDam) o;

        if (!dividaAtiva.equals(that.dividaAtiva)) return false;
        if (!idLoteBaixa.equals(that.idLoteBaixa)) return false;
        if (!idTributo.equals(that.idTributo)) return false;
        if (!idEntidade.equals(that.idEntidade)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idTributo.hashCode();
        result = 31 * result + dividaAtiva.hashCode();
        result = 31 * result + idLoteBaixa.hashCode();
        result = 31 * result + idEntidade.hashCode();
        return result;
    }

    public void addValor(BigDecimal valor) {
        this.valor = this.valor.add(valor);
    }

    @Override
    public String toString() {
        return "idTributo: " + idTributo +
            " tipoTributo: " + tipoTributo +
            " dividaAtiva: " + dividaAtiva +
            " valor: " + valor +
            " idLoteBaixa: " + idLoteBaixa +
            " dataPagamento: " + dataPagamento +
            " idEntidade: " + idEntidade +
            " exercicio: " + exercicio;
    }
}
