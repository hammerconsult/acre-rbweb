package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.ContaReceita;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Tributo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 30/03/15
 * Time: 10:45
 * To change this template use File | Settings | File Templates.
 */
public class RelatorioArrecadacaoConsolidadoTributoContaAtributos implements Comparable<RelatorioArrecadacaoConsolidadoTributoContaAtributos> {

    private Banco banco;
    private ContaReceita contaReceita;
    private Tributo tributo;
    private Date dataArrecadacao;
    private BigDecimal valor;
    private Exercicio exercicio;

    public RelatorioArrecadacaoConsolidadoTributoContaAtributos() {
    }

    public RelatorioArrecadacaoConsolidadoTributoContaAtributos(
        Banco banco,
        ContaReceita contaReceita,
        Tributo tributo,
        Date dataArrecadacao,
        Exercicio exercicio,
        BigDecimal valor) {
        this.banco = banco;
        this.contaReceita = contaReceita;
        this.tributo = tributo;
        this.dataArrecadacao = dataArrecadacao;
        this.valor = valor;
        this.exercicio = exercicio;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public Date getDataArrecadacao() {
        return dataArrecadacao;
    }

    public void setDataArrecadacao(Date dataArrecadacao) {
        this.dataArrecadacao = dataArrecadacao;
    }

    public BigDecimal getValor() {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelatorioArrecadacaoConsolidadoTributoContaAtributos that = (RelatorioArrecadacaoConsolidadoTributoContaAtributos) o;

        if (banco != null ? !banco.equals(that.banco) : that.banco != null) return false;
        if (contaReceita != null ? !contaReceita.equals(that.contaReceita) : that.contaReceita != null) return false;
        if (tributo != null ? !tributo.equals(that.tributo) : that.tributo != null) return false;
        if (dataArrecadacao != null ? !dataArrecadacao.equals(that.dataArrecadacao) : that.dataArrecadacao != null)
            return false;
        if (getValor() != null ? !getValor().equals(that.getValor()) : that.getValor() != null) return false;
        return !(exercicio != null ? !exercicio.equals(that.exercicio) : that.exercicio != null);

    }

    @Override
    public int hashCode() {
        int result = banco != null ? banco.hashCode() : 0;
        result = 31 * result + (contaReceita != null ? contaReceita.hashCode() : 0);
        result = 31 * result + (tributo != null ? tributo.hashCode() : 0);
        result = 31 * result + (dataArrecadacao != null ? dataArrecadacao.hashCode() : 0);
        result = 31 * result + (getValor() != null ? getValor().hashCode() : 0);
        result = 31 * result + (exercicio != null ? exercicio.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(RelatorioArrecadacaoConsolidadoTributoContaAtributos o) {
        int i = o.getExercicio() == null || this.getExercicio() == null ? 0 : this.getExercicio().getAno().compareTo(o.getExercicio().getAno());
        if (i != 0) {
            return i;
        }
        i = o.getBanco() == null || this.getBanco() == null ? 0 : o.getBanco().getDescricao().compareTo(this.getBanco().getDescricao());
        if (i != 0) {
            return i;
        }
        i = o.getContaReceita() == null || this.getContaReceita() == null ? 0 : o.getContaReceita().getDescricao().compareTo(this.getContaReceita().getDescricao());
        if (i != 0) {
            return i;
        }
        i = o.getTributo() == null || this.getTributo() == null ? 0 : o.getTributo().getDescricao().compareTo(this.getTributo().getDescricao());
        if (i != 0) {
            return i;
        }
        i = o.getDataArrecadacao() == null || this.getDataArrecadacao() == null ? 0 : o.getDataArrecadacao().compareTo(this.getDataArrecadacao());
        if (i != 0) {
            return i;
        }
        return 0;
    }
}
