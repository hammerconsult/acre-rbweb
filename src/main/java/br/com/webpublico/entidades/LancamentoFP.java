/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoImportacao;
import br.com.webpublico.enums.TipoLancamentoFP;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorHistorico;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

/**
 * @author magneto
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Lançamento Folha de Pagamento")
public class LancamentoFP extends SuperEntidade implements Serializable, Comparable, PossuidorHistorico {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ColunaAuditoriaRH
    @Tabelavel
    @Etiqueta("Servidor")
    @ManyToOne
    private VinculoFP vinculoFP;
    @ColunaAuditoriaRH(posicao = 2)
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Evento FP")
    @Tabelavel
    @Pesquisavel
    private EventoFP eventoFP;
    @ColunaAuditoriaRH(posicao = 3)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo de Lançamento")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoLancamentoFP tipoLancamentoFP;
    @Tabelavel
    @Etiqueta("Quantificação/Percentual")
    @Obrigatorio
    @Pesquisavel
    @ColunaAuditoriaRH(monetario = true, posicao = 4)
    private BigDecimal quantificacao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Mês/Ano Inicial")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    private Date mesAnoInicial;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Mês/Ano Final")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date mesAnoFinal;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Cadastro")
    @Pesquisavel
    private Date dataCadastro;
    @Etiqueta("Ato Legal")
    @ManyToOne
    @Pesquisavel
    @Obrigatorio
    private AtoLegal atoLegal;
    @ManyToOne
    @Etiqueta("Motivo da Rejeição")
    private MotivoRejeicao motivoRejeicao;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data do Cadastro E-Consig")
    private Date dataCadastroEconsig;
    @Obrigatorio
    @Etiqueta("Mês/Ano Inicio Cálculo")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date mesAnoInicioCalculo;
    @Etiqueta("Observação")
    private String observacao;
    @Etiqueta("Proporcionalizar")
    private Boolean proporcionalizar;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoImportacao tipoImportacao;
    @ManyToOne(cascade = CascadeType.ALL)
    private BaseFP baseFP;
    @Etiqueta("Número Parcelas")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Integer numeroParcelas;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private LancamentoFPHist lancamentoFPHist;
    @Etiqueta("Valor da Base")
    private BigDecimal valorDaBase;

    @Transient
    private String linhaArquivo;
    @Transient
    @Invisivel
    private Boolean selecionado;

    public LancamentoFP() {
        proporcionalizar = true;
        tipoFolhaDePagamento = TipoFolhaDePagamento.NORMAL;
        numeroParcelas = 1;
    }

    public LancamentoFP(VinculoFP vinculoFP, EventoFP eventoFP, TipoLancamentoFP tipoLancamentoFP, BigDecimal quantificacao) {
        this.vinculoFP = vinculoFP;
        this.eventoFP = eventoFP;
        this.tipoLancamentoFP = tipoLancamentoFP;
        this.quantificacao = quantificacao;
        proporcionalizar = true;
    }

    public LancamentoFP(Long id, VinculoFP vinculoFP, EventoFP eventoFP, TipoLancamentoFP tipoLancamentoFP, BigDecimal quantificacao, Date mesAnoInicial, Date mesAnoFinal, MotivoRejeicao motivoRejeicao) {
        this.id = id;
        this.vinculoFP = vinculoFP;
        this.eventoFP = eventoFP;
        this.tipoLancamentoFP = tipoLancamentoFP;
        this.quantificacao = quantificacao;
        this.mesAnoInicial = mesAnoInicial;
        this.mesAnoFinal = mesAnoFinal;
        this.motivoRejeicao = motivoRejeicao;
    }

    public LancamentoFP(Long id, VinculoFP vinculoFP, EventoFP eventoFP, TipoLancamentoFP tipoLancamentoFP, BigDecimal quantificacao, Date mesAnoInicial, Date mesAnoFinal) {
        this.id = id;
        this.vinculoFP = vinculoFP;
        this.eventoFP = eventoFP;
        this.tipoLancamentoFP = tipoLancamentoFP;
        this.quantificacao = quantificacao;
        this.mesAnoInicial = mesAnoInicial;
        this.mesAnoFinal = mesAnoFinal;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public TipoImportacao getTipoImportacao() {
        return tipoImportacao;
    }

    public void setTipoImportacao(TipoImportacao tipoImportacao) {
        this.tipoImportacao = tipoImportacao;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getMesAnoFinal() {
        return mesAnoFinal;
    }

    public void setMesAnoFinal(Date mesAnoFinal) {
        this.mesAnoFinal = mesAnoFinal;
    }

    public Date getMesAnoInicial() {
        return mesAnoInicial;
    }

    public void setMesAnoInicial(Date mesAnoInicial) {
        this.mesAnoInicial = mesAnoInicial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public TipoLancamentoFP getTipoLancamentoFP() {
        return tipoLancamentoFP;
    }

    public void setTipoLancamentoFP(TipoLancamentoFP tipoLancamentoFP) {
        this.tipoLancamentoFP = tipoLancamentoFP;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public BigDecimal getQuantificacao() {
        return quantificacao;
    }

    public void setQuantificacao(BigDecimal quantificacao) {
        this.quantificacao = quantificacao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }


    public MotivoRejeicao getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(MotivoRejeicao motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public Date getDataCadastroEconsig() {
        return dataCadastroEconsig;
    }

    public void setDataCadastroEconsig(Date dataCadastroEconsig) {
        this.dataCadastroEconsig = dataCadastroEconsig;
    }

    public String getLinhaArquivo() {
        return linhaArquivo;
    }

    public void setLinhaArquivo(String linhaArquivo) {
        this.linhaArquivo = linhaArquivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Boolean getProporcionalizar() {
        return proporcionalizar == null ? false : proporcionalizar;
    }

    public void setProporcionalizar(Boolean proporcionalizar) {
        this.proporcionalizar = proporcionalizar;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public LancamentoFPHist getLancamentoFPHist() {
        return lancamentoFPHist;
    }

    public void setLancamentoFPHist(LancamentoFPHist lancamentoFPHist) {
        this.lancamentoFPHist = lancamentoFPHist;
    }

    @Override
    public String toString() {
        return "Vinculo: " + eventoFP + " Evento: " + vinculoFP + " Tipo Lançamento: " + tipoLancamentoFP;
    }

    public Date getMesAnoInicioCalculo() {
        return mesAnoInicioCalculo;
    }

    public void setMesAnoInicioCalculo(Date mesAnoInicioCalculo) {
        this.mesAnoInicioCalculo = mesAnoInicioCalculo;
    }

    @Override
    public int compareTo(Object o) {
        LancamentoFP l = (LancamentoFP) o;
        if (l.getEventoFP().getOrdemProcessamento() != null && eventoFP.getOrdemProcessamento() != null)
            return eventoFP.getOrdemProcessamento().compareTo(l.getEventoFP().getOrdemProcessamento());
        else return 1;
    }

    public boolean isValor() {
        return TipoLancamentoFP.VALOR.equals(this.getTipoLancamentoFP());
    }

    public boolean isPercentual() {
        return TipoLancamentoFP.REFERENCIA.equals(this.getTipoLancamentoFP());
    }

    public boolean isValorNormal() {
        return TipoLancamentoFP.QUANTIDADE.equals(this.getTipoLancamentoFP()) || TipoLancamentoFP.DIAS.equals(this.getTipoLancamentoFP()) || TipoLancamentoFP.HORAS.equals(this.getTipoLancamentoFP());
    }

    public boolean isValidoParaImportacao() {
        if (this.getVinculoFP() == null) {
            return false;
        }
        if (this.getEventoFP() == null) {
            return false;
        }
        if (this.getQuantificacao() == null || this.getQuantificacao().compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean temHistorico() {
        return lancamentoFPHist != null;
    }

    @Override
    public Date getInicioVigencia() {
        return mesAnoInicial;
    }

    @Override
    public Date getFinalVigencia() {
        return mesAnoFinal;
    }

    @Override
    public void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original) {
        LancamentoFPHist historico = this.temHistorico() ? this.lancamentoFPHist : new LancamentoFPHist();
        historico.setDataCadastro(new Date());
        historico.setMesAnoInicial(original.getInicioVigencia());
        if (atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(original, historico)
            || atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(original)) {
            historico.setMesAnoFinal(original.getFinalVigencia());
        }
        this.lancamentoFPHist = historico;
    }

    @Override
    public void voltarHistorico() {
        if (temHistorico()) {
            setMesAnoFinal(lancamentoFPHist.getMesAnoFinal());
        } else {
            setMesAnoFinal(null);
        }
    }

    public boolean atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(PossuidorHistorico original) {
        return !this.temFinalVigencia() && original.temFinalVigencia();
    }

    public boolean atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(PossuidorHistorico original, LancamentoFPHist historico) {
        return this.temFinalVigencia() && finalVigenciaAtualDiferenteFinalVigenciaOriginal(original) || !historico.temId();
    }

    public boolean finalVigenciaAtualDiferenteFinalVigenciaOriginal(PossuidorHistorico original) {
        return !this.getFinalVigencia().equals(original.getFinalVigencia());
    }

    public BigDecimal getValorDaBase() {
        return valorDaBase;
    }

    public void setValorDaBase(BigDecimal valorDaBase) {
        this.valorDaBase = valorDaBase;
    }

    @Override
    public boolean temFinalVigencia() {
        return mesAnoFinal != null;
    }


    public static class Comparators {

        public static Comparator<LancamentoFP> MATRICULA_ASC = new Comparator<LancamentoFP>() {
            @Override
            public int compare(LancamentoFP o1, LancamentoFP o2) {
                Integer mat1 = Integer.valueOf(o1.getVinculoFP().getMatriculaFP().getMatricula());
                Integer mat2 = Integer.valueOf(o2.getVinculoFP().getMatriculaFP().getMatricula());
                return mat1.compareTo(mat2);
            }
        };

        public static Comparator<LancamentoFP> MATRICULA_INICIO_LANCAMENTO = new Comparator<LancamentoFP>() {
            @Override
            public int compare(LancamentoFP o1, LancamentoFP o2) {
                int valor = 0;
                Integer mat1 = Integer.valueOf(o1.getVinculoFP().getMatriculaFP().getMatricula());
                Integer mat2 = Integer.valueOf(o2.getVinculoFP().getMatriculaFP().getMatricula());
                valor = mat1.compareTo(mat2);
                if (valor == 0) {
                    return o1.getMesAnoInicial().compareTo(o2.getMesAnoInicial());
                }
                return valor;
            }
        };
    }
}



