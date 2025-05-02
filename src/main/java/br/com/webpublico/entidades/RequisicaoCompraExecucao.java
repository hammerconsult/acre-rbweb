package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Requisição de Compra Execução")
public class RequisicaoCompraExecucao extends SuperEntidade implements Comparable<RequisicaoCompraExecucao> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Requisição de Compra")
    private RequisicaoDeCompra requisicaoCompra;

    @ManyToOne
    private ExecucaoContrato execucaoContrato;

    @ManyToOne
    private ExecucaoProcesso execucaoProcesso;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequisicaoDeCompra getRequisicaoCompra() {
        return requisicaoCompra;
    }

    public void setRequisicaoCompra(RequisicaoDeCompra requisicaoCompra) {
        this.requisicaoCompra = requisicaoCompra;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        return execucaoProcesso;
    }

    public void setExecucaoProcesso(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
    }

    @Override
    public String toString() {
        try {
            if (requisicaoCompra.isTipoContrato()) {
                return "Nº " + execucaoContrato.getNumero() + " - " + DataUtil.getDataFormatada(execucaoContrato.getDataLancamento()) + " - " + Util.formataValor(execucaoContrato.getValor());
            }
            return "Nº " + execucaoProcesso.getNumero() + " - " + DataUtil.getDataFormatada(execucaoProcesso.getDataLancamento()) + " - " + Util.formataValor(execucaoProcesso.getValor());
        } catch (NullPointerException e) {
            return "";
        }
    }

    @Override
    public int compareTo(RequisicaoCompraExecucao o) {
        try {
            return ComparisonChain.start().compare(getExecucaoContrato().getNumero(), o.getExecucaoContrato().getNumero()).result();
        } catch (Exception e) {
            return 0;
        }
    }
}
