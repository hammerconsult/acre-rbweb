package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ContaReceita;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.ItemIntegracaoTributarioContabil;
import br.com.webpublico.enums.OperacaoIntegracaoTribCont;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.TipoIntegracao;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * Created by Fabio on 26/10/2017.
 */
public class ItemInconsistenciaIntegracaoTribCont implements Comparable<ItemInconsistenciaIntegracaoTribCont> {

    private Date dataPagamento;
    private Date dataCredito;
    private ContaReceita contaReceita;
    private OperacaoReceita operacaoReceitaRealizada;
    private FonteDeRecursos fonteDeRecursos;
    private OperacaoIntegracaoTribCont operacaoIntegracao;
    private Entidade entidade;
    private TipoIntegracao tipo;
    private BigDecimal valorTributario;
    private BigDecimal valorContabil;
    private List<ItemIntegracaoTributarioContabil> integracoes;

    public ItemInconsistenciaIntegracaoTribCont() {
        this.integracoes = Lists.newArrayList();
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento != null ? DataUtil.dataSemHorario(dataPagamento) : dataPagamento;
    }

    public Date getDataCredito() {
        return dataCredito;
    }

    public void setDataCredito(Date dataCredito) {
        this.dataCredito = dataCredito != null ? DataUtil.dataSemHorario(dataCredito) : dataCredito;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public OperacaoReceita getOperacaoReceitaRealizada() {
        return operacaoReceitaRealizada;
    }

    public void setOperacaoReceitaRealizada(OperacaoReceita operacaoReceitaRealizada) {
        this.operacaoReceitaRealizada = operacaoReceitaRealizada;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public BigDecimal getValorTributario() {
        return valorTributario != null ? valorTributario.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorTributario(BigDecimal valorTributario) {
        this.valorTributario = valorTributario;
    }

    public BigDecimal getValorContabil() {
        return valorContabil != null ? valorContabil.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorContabil(BigDecimal valorContabil) {
        this.valorContabil = valorContabil;
    }

    public TipoIntegracao getTipo() {
        return tipo;
    }

    public void setTipo(TipoIntegracao tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getDiferenca() {
        return getValorTributario().subtract(getValorContabil());
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public OperacaoIntegracaoTribCont getOperacaoIntegracao() {
        return operacaoIntegracao;
    }

    public void setOperacaoIntegracao(OperacaoIntegracaoTribCont operacaoIntegracao) {
        this.operacaoIntegracao = operacaoIntegracao;
    }

    public List<ItemIntegracaoTributarioContabil> getIntegracoes() {
        return integracoes;
    }

    public void setIntegracoes(List<ItemIntegracaoTributarioContabil> integracoes) {
        this.integracoes = integracoes;
    }

    public int compareTo(ItemInconsistenciaIntegracaoTribCont o) {
        int i = o.getDiferenca().compareTo(this.getDiferenca());
        if (i == 0) {
            i = o.getContaReceita().getCodigo().compareTo(this.getContaReceita().getCodigo());
        }
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemInconsistenciaIntegracaoTribCont that = (ItemInconsistenciaIntegracaoTribCont) o;

        if (contaReceita != null ? !contaReceita.equals(that.contaReceita) : that.contaReceita != null) return false;
        if (operacaoReceitaRealizada != null && !operacaoReceitaRealizada.equals(that.operacaoReceitaRealizada)) return false;
        if (operacaoIntegracao != null && !operacaoIntegracao.equals(that.operacaoIntegracao)) return false;
        return tipo.equals(that.tipo);
    }

    @Override
    public int hashCode() {
        int result = (contaReceita != null ? contaReceita.hashCode() : 0);
        result = 31 * result + (operacaoReceitaRealizada != null ? operacaoReceitaRealizada.hashCode() : 0);
        result = 31 * result + (operacaoIntegracao != null ? operacaoIntegracao.hashCode() : 0);
        result = 31 * result + (tipo != null ? tipo.hashCode() : 0);
        return result;
    }
}
