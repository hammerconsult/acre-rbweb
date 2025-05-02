package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitações")
public class ExecucaoProcessoEstorno extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Execução Processo")
    private ExecucaoProcesso execucaoProcesso;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "execucaoProcessoEstorno", orphanRemoval = true)
    private List<ExecucaoProcessoEmpenhoEstorno> estornosEmpenho;

    public ExecucaoProcessoEstorno() {
        estornosEmpenho = Lists.newArrayList();
        valor = BigDecimal.ZERO;
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

    public ExecucaoProcesso getExecucaoProcesso() {
        return execucaoProcesso;
    }

    public void setExecucaoProcesso(ExecucaoProcesso execucaoAta) {
        this.execucaoProcesso = execucaoAta;
    }

    public List<ExecucaoProcessoEmpenhoEstorno> getEstornosEmpenho() {
        return estornosEmpenho;
    }

    public void setEstornosEmpenho(List<ExecucaoProcessoEmpenhoEstorno> estornosEmpenho) {
        this.estornosEmpenho = estornosEmpenho;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valorTotal) {
        this.valor = valorTotal;
    }

    @Override
    public String toString() {
        try {
            return numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + execucaoProcesso.toString();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public void calcularValorTotal() {
        valor = BigDecimal.ZERO;
        if (getEstornosEmpenho() != null) {
            for (ExecucaoProcessoEmpenhoEstorno estorno : getEstornosEmpenho()) {
                valor = valor.add(estorno.getValorTotalItens());
            }
        }
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal total = BigDecimal.ZERO;
        for (ExecucaoProcessoEmpenhoEstorno estorno : getEstornosEmpenho()) {
            total = total.add(estorno.getValorTotalItens());
        }
        return total;
    }

    public boolean hasSolicitacaoEstornoEmpenhoEstornada() {
        if (estornosEmpenho != null) {
            for (ExecucaoProcessoEmpenhoEstorno estorno : estornosEmpenho) {
                if (estorno.getSolicitacaoEmpenhoEstorno().getEmpenho() != null
                    && estorno.getSolicitacaoEmpenhoEstorno().hasSolicitacaoEstornoEmpenhoEstornada()) {
                    return true;
                }
            }
        }
        return false;
    }
}
