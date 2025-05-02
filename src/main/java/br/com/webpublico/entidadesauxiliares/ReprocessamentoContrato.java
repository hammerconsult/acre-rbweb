package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AlteracaoContratual;
import br.com.webpublico.entidades.ExecucaoContrato;
import br.com.webpublico.entidades.MovimentoAlteracaoContratual;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 23/05/2018.
 */
public class ReprocessamentoContrato {

    private AlteracaoContratual alteracaoContratual;
    private ExecucaoContrato execucaoContrato;
    private TipoMovimentoContrato tipoMovimentoContrato;
    private Date dataMovimento;
    private Long idMovimento;
    private BigDecimal valor;
    public List<MovimentoAlteracaoContratual> movimentosAlteracaoContratual;

    public AlteracaoContratual getAlteracaoContratual() {
        return alteracaoContratual;
    }

    public void setAlteracaoContratual(AlteracaoContratual alteracaoContratual) {
        this.alteracaoContratual = alteracaoContratual;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public TipoMovimentoContrato getTipoMovimentoContrato() {
        return tipoMovimentoContrato;
    }

    public void setTipoMovimentoContrato(TipoMovimentoContrato tipoMovimentoContrato) {
        this.tipoMovimentoContrato = tipoMovimentoContrato;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public Long getIdMovimento() {
        return idMovimento;
    }

    public void setIdMovimento(Long idMovimento) {
        this.idMovimento = idMovimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<MovimentoAlteracaoContratual> getMovimentosAlteracaoContratual() {
        return movimentosAlteracaoContratual;
    }

    public void setMovimentosAlteracaoContratual(List<MovimentoAlteracaoContratual> movimentosAlteracaoContratual) {
        this.movimentosAlteracaoContratual = movimentosAlteracaoContratual;
    }

    public enum TipoMovimentoContrato{
        CONTRATO("Contrato"),
        ADITIVO("Aditivo"),
        APOSTILAMENTO("Apostilamento"),
        EXECUCAO("Execução"),
        ESTORNO_EXECUCAO("Estorno de Execução");
        private String descricao;

        TipoMovimentoContrato(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isVariacaoContratual(){
            return ADITIVO.equals(this) || APOSTILAMENTO.equals(this);
        }
    }
}
