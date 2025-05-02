package br.com.webpublico.negocios.contabil.reprocessamento.consulta;

public class FiltroConsultaMovimentoContabil {
    private transient Object valor;
    private ConsultaMovimentoContabil.Operador operador;
    private ConsultaMovimentoContabil.Campo campo;
    private String condicao;

    public FiltroConsultaMovimentoContabil() {
    }

    public FiltroConsultaMovimentoContabil(Object valor, ConsultaMovimentoContabil.Operador operador, ConsultaMovimentoContabil.Campo campo) {
        this.valor = valor;
        this.operador = operador;

        this.campo = campo;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public void setOperador(ConsultaMovimentoContabil.Operador operador) {
        this.operador = operador;
    }

    public void setCampo(ConsultaMovimentoContabil.Campo campo) {
        this.campo = campo;
    }

    public ConsultaMovimentoContabil.Operador getOperador() {
        return operador;
    }

    public ConsultaMovimentoContabil.Campo getCampo() {
        return campo;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FiltroConsultaMovimentoContabil that = (FiltroConsultaMovimentoContabil) o;

        if (valor != null ? !valor.equals(that.valor) : that.valor != null) return false;
        if (operador != that.operador) return false;
        return campo == that.campo;
    }

    @Override
    public int hashCode() {
        int result = valor != null ? valor.hashCode() : 0;
        result = 31 * result + (operador != null ? operador.hashCode() : 0);
        result = 31 * result + (campo != null ? campo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FiltroConsultaMovimentoContabil{" +
            "valor=" + valor +
            ", operador=" + operador +
            ", campo=" + campo +
            '}';
    }
}
