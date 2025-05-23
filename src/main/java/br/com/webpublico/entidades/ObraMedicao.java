package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author venom
 */
@Entity
@Audited
@Etiqueta("Medição da Obra")
public class ObraMedicao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Obra")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private Obra obra;

    @Etiqueta("Número da Medição")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private Long numero;

    @Etiqueta("Data Inicial")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date dataInicial;

    @Etiqueta("Data Final")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date dataFinal;

    @Tabelavel
    @Monetario
    @Obrigatorio
    @Etiqueta("Valor")
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "obraMedicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObraMedicaoAnexo> anexos;

    @OneToMany(mappedBy = "obraMedicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObraMedicaoExecucaoContrato> execucoesMedicao;

    @Invisivel
    @OneToMany(mappedBy = "obraMedicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObraMedicaoSolicitacaoEmpenho> obraMedicaoSolicitacaoEmpenhos;

    public ObraMedicao() {
        this.valorTotal = BigDecimal.ZERO;
        this.anexos = new ArrayList<>();
        this.execucoesMedicao = new ArrayList<>();
        this.obraMedicaoSolicitacaoEmpenhos = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public List<ObraMedicaoAnexo> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<ObraMedicaoAnexo> anexos) {
        this.anexos = anexos;
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void addAnexo(ObraMedicaoAnexo anexo) {
        if (this.anexos == null) {
            this.anexos = new ArrayList<>();
        }
        if (this.anexos.contains(anexo)) {
            this.anexos.set(this.anexos.indexOf(anexo), anexo);
        } else {
            this.anexos.add(anexo);
        }
    }

    public void delAnexo(ObraMedicaoAnexo anexo) {
        if (this.anexos == null) {
            this.anexos = new ArrayList<>();
        }
        if (this.anexos.contains(anexo)) {
            this.anexos.remove(anexo);
        }
    }

    @Override
    public String toString() {
        return numero + "";
    }

    public Date getInicioVigencia() {
        return dataInicial;
    }

    public void setInicioVigencia(Date data) {
        this.dataInicial = data;
    }

    public Date getFimVigencia() {
        return dataFinal;
    }

    public void setFimVigencia(Date data) {
        this.dataFinal = data;
    }

    public List<ObraMedicaoExecucaoContrato> getExecucoesMedicao() {
        return execucoesMedicao;
    }

    public void setExecucoesMedicao(List<ObraMedicaoExecucaoContrato> execucoesMedicao) {
        this.execucoesMedicao = execucoesMedicao;
    }

    public List<ExecucaoContratoEmpenho> getExecucoesContratoEmpenho() {
        List<ExecucaoContratoEmpenho> toReturn = Lists.newArrayList();
        if (getExecucoesMedicao() != null && !getExecucoesMedicao().isEmpty()) {
            for (ObraMedicaoExecucaoContrato execucaoMedicao : getExecucoesMedicao()) {
                for (ExecucaoContratoEmpenho empenhoEmpenho : execucaoMedicao.getExecucaoContrato().getEmpenhos()) {
                    toReturn.add(empenhoEmpenho);
                }
            }
        }
        return toReturn;
    }

    public BigDecimal getValorTotalEmpenhos() {
        BigDecimal total = BigDecimal.ZERO;
        if (getExecucoesContratoEmpenho() != null && !getExecucoesContratoEmpenho().isEmpty()) {
            for (ExecucaoContratoEmpenho execucaoEmpenho : getExecucoesContratoEmpenho()) {
                if (execucaoEmpenho != null) {
                    if (execucaoEmpenho.getEmpenho() != null) {
                        total = total.add(execucaoEmpenho.getEmpenho().getValor());
                    } else {
                        total = total.add(execucaoEmpenho.getSolicitacaoEmpenho().getValor());
                    }
                }
            }
        }
        return total;
    }

    public List<Empenho> getEmpenhosOld() {
        List<Empenho> toReturn = Lists.newArrayList();
        if (obraMedicaoSolicitacaoEmpenhos != null) {
            for (ObraMedicaoSolicitacaoEmpenho obraMedicaoSolicitacaoEmpenho : obraMedicaoSolicitacaoEmpenhos) {
                if (obraMedicaoSolicitacaoEmpenho.getEmpenho() != null) {
                    toReturn.add(obraMedicaoSolicitacaoEmpenho.getEmpenho());
                }
            }
        }
        return toReturn;
    }

    public void validarMesmaExecucaoEmLista(ObraMedicaoExecucaoContrato execucaoMedicao) {
        ValidacaoException ve = new ValidacaoException();
        if (getExecucoesMedicao() != null && !getExecucoesMedicao().isEmpty()) {
            for (ObraMedicaoExecucaoContrato execucao : getExecucoesMedicao()) {
                if (execucao.getExecucaoContrato().equals(execucaoMedicao.getExecucaoContrato())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A execução " + execucaoMedicao.getExecucaoContrato() + " já foi adicionada na lista.");
                }
            }
        }
        ve.lancarException();
    }

    public List<ObraMedicaoSolicitacaoEmpenho> getSolicitacoes() {
        List<ObraMedicaoSolicitacaoEmpenho> toReturn = Lists.newArrayList();
        if (obraMedicaoSolicitacaoEmpenhos != null) {
            for (ObraMedicaoSolicitacaoEmpenho obraMedicaoSolicitacaoEmpenho : obraMedicaoSolicitacaoEmpenhos) {
                if (obraMedicaoSolicitacaoEmpenho.getSolicitacaoEmpenho() != null) {
                    toReturn.add(obraMedicaoSolicitacaoEmpenho);
                }
            }
        }
        return toReturn;
    }

    public BigDecimal getValorTotalExecucoes() {
        BigDecimal total = BigDecimal.ZERO;
        for (ObraMedicaoExecucaoContrato medicaoExecucao : getExecucoesMedicao()) {
            if (medicaoExecucao.getExecucaoContrato() != null) {
                total = total.add(medicaoExecucao.getExecucaoContrato().getValor());
            }
        }
        return total;
    }
}
