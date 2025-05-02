package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Estorno Execução de Contrato")
public class ExecucaoContratoEstorno extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Execução de Contrato")
    private ExecucaoContrato execucaoContrato;

    @Monetario
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "execucaoContratoEstorno", orphanRemoval = true)
    private List<ExecucaoContratoEmpenhoEstorno> estornosEmpenho;

    public ExecucaoContratoEstorno() {
        estornosEmpenho = Lists.newArrayList();
        valorTotal = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public List<ExecucaoContratoEmpenhoEstorno> getEstornosEmpenho() {
        return estornosEmpenho;
    }

    public void setEstornosEmpenho(List<ExecucaoContratoEmpenhoEstorno> estornosEmpenho) {
        this.estornosEmpenho = estornosEmpenho;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public String toString() {
        try {
            return numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + execucaoContrato.toString();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getDescricaoEstorno() {
        String s = "";
        if (numero != null) {
            s = numero + " - ";
        }
        return s + DataUtil.getDataFormatada(dataLancamento) + " - " + Util.formataValor(valorTotal);
    }

    public void calcularValorTotal() {
        valorTotal = BigDecimal.ZERO;
        if (getEstornosEmpenho() != null) {
            for (ExecucaoContratoEmpenhoEstorno estorno : getEstornosEmpenho()) {
                valorTotal = valorTotal.add(estorno.getValorTotalItens());
            }
        }
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal total = BigDecimal.ZERO;
        for (ExecucaoContratoEmpenhoEstorno estorno : getEstornosEmpenho()) {
            total = total.add(estorno.getValorTotalItens());
        }
        return total;
    }

    public BigDecimal getValorTotalEmpenhoEstono() {
        BigDecimal total = BigDecimal.ZERO;
        for (ExecucaoContratoEmpenhoEstorno estorno : getEstornosEmpenho()) {
            total = total.add(estorno.getValor());
        }
        return total;
    }

    public boolean hasSolicitacaoEstornoEmpenhoEstornada() {
        if (estornosEmpenho != null) {
            for (ExecucaoContratoEmpenhoEstorno estorno : estornosEmpenho) {
                if (estorno.getSolicitacaoEmpenhoEstorno().hasSolicitacaoEstornoEmpenhoEstornada()) {
                    return true;
                }
            }
        }
        return false;
    }
}
