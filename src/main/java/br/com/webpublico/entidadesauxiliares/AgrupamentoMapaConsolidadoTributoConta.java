package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.ContaReceita;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Tributo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 23/07/15
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
public class AgrupamentoMapaConsolidadoTributoConta implements Serializable {

    private Banco banco;
    private ContaReceita contaReceita;
    private Tributo tributo;
    private Date dataArrecadacao;
    private Exercicio exercicio;

    public AgrupamentoMapaConsolidadoTributoConta(Banco banco, ContaReceita contaReceita, Tributo tributo, Date dataArrecadacao, Exercicio exercicio) {
        this.banco = banco;
        this.contaReceita = contaReceita;
        this.tributo = tributo;
        this.dataArrecadacao = dataArrecadacao;
        this.exercicio = exercicio;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public Date getDataArrecadacao() {
        return dataArrecadacao;
    }

    public void setDataArrecadacao(Date dataArrecadacao) {
        this.dataArrecadacao = dataArrecadacao;
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

        AgrupamentoMapaConsolidadoTributoConta that = (AgrupamentoMapaConsolidadoTributoConta) o;

        if (banco != null ? !banco.equals(that.banco) : that.banco != null) return false;
        if (contaReceita != null ? !contaReceita.equals(that.contaReceita) : that.contaReceita != null) return false;
        if (tributo != null ? !tributo.equals(that.tributo) : that.tributo != null) return false;
        if (dataArrecadacao != null ? !dataArrecadacao.equals(that.dataArrecadacao) : that.dataArrecadacao != null)
            return false;
        return !(exercicio != null ? !exercicio.equals(that.exercicio) : that.exercicio != null);

    }

    @Override
    public int hashCode() {
        int result = banco != null ? banco.hashCode() : 0;
        result = 31 * result + (contaReceita != null ? contaReceita.hashCode() : 0);
        result = 31 * result + (tributo != null ? tributo.hashCode() : 0);
        result = 31 * result + (dataArrecadacao != null ? dataArrecadacao.hashCode() : 0);
        result = 31 * result + (exercicio != null ? exercicio.hashCode() : 0);
        return result;
    }
}
