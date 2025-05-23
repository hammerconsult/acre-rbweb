/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ClasseAfastamento;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.enums.rh.esocial.TipoAcidenteTransito;
import br.com.webpublico.enums.rh.esocial.TipoAfastamentoESocial;
import br.com.webpublico.enums.rh.previdencia.TipoSituacaoBBPrev;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Etiqueta("Tipo de Afastamento")
@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
public class TipoAfastamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Código")
    @Tabelavel
    private Integer codigo;
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    @Tabelavel
    private String descricao;
    /**
     * Quantos dias serão desconsiderados do cálculo da folha para efeito de
     * desconto.
     */
    @Etiqueta("Carência")
    @Tabelavel
    private Long carencia;
    @Obrigatorio
    @Etiqueta("Classe do Afastamento")
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private ClasseAfastamento classeAfastamento;
    @Etiqueta("Descontar Dias Trabalhados")
    private Boolean descontarDiasTrabalhados;
    @Etiqueta("Descontar Tempo de Serviço Efetivo")
    private Boolean descontarTempoServicoEfetivo;
    @Etiqueta("Descontar Descanço Semanal Remunerado")
    private Boolean descontarDSR;
    @Etiqueta("Descontar Tempo de Servico para Aposentadoria")
    private Boolean descTempoServicoAposentadoria;
    @Etiqueta("Pago Pela Entidade Previdenciária")
    private Boolean pagoEntidadePrevidenciaria;
    @Etiqueta("Motivo de Afastamento da RAIS")
    @ManyToOne
    private MotivoAfastamentoRais motivoAfastamentoRais;
    @ManyToOne
    @Etiqueta("Movimento SEFIP para Afastamento")
    private MovimentoSEFIP movimentoSEFIPAfastamento;
    @ManyToOne
    @Etiqueta("Movimento SEFIP para Retorno")
    private MovimentoSEFIP movimentoSEFIPRetorno;
    @ManyToOne
    @Etiqueta("Referência Folha de Pagamento")
    private ReferenciaFP referenciaFP;
    @Obrigatorio
    @Etiqueta("Número Máximo de Dias Permitido")
    @Tabelavel
    private Integer diasMaximoPermitido;
    @Tabelavel
    @Etiqueta("Calcular Vale Transporte")
    private Boolean calcularValeTransporte;
    private Boolean processaNormal;
    private Integer periodoNovoAfastamento;
    private Boolean permiteAfastamentoCc;
    private Boolean exigeSindicato;
    @Etiqueta("Não Permitir Progressão")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Boolean naoPermitirProgressao;
    private Integer maximoDiasAfastamentoAno;
    @Enumerated(EnumType.STRING)
    private Sexo sexo;
    private String fundamentacaoLegal;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Afastamento E-Social")
    private TipoAfastamentoESocial tipoAfastamentoESocial;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Acidente de Trânsito")
    private TipoAcidenteTransito tipoAcidenteTransito;
    /**
     * alongamento do periodo aquisitivo, caso o servidor
     * tenha afastamento durante o período aquisitivo.
     **/
    @Pesquisavel
    private Boolean alongarPeriodoAquisitivo;
    /**
     * carência que armazenará a quantidade de dias afastados
     * em que o período aquisitivo irá começar a ser alongado.
     **/
    @Pesquisavel
    private Integer carenciaAlongamento;
    /**
     * quantidade máxima de dias afastados que o servidor
     * irá perder o período aquisitivo.
     **/
    @Pesquisavel
    private Integer maximoPerdaPeriodoAquisitivo;

    /**
     * Perder o periodo aquisitivo por afastamento do cargo
     **/
    private Boolean reiniciarContagem;

    private Boolean naoGerarPeriodoAquisitivo;
    @Invisivel
    @Transient
    private Long criadoEm;

    @Etiqueta("Permitir Frequência no Período")
    private Boolean permitirFrequenciaPeriodo;

    @Etiqueta("Perder Período Aquisitivo de Férias")
    private Boolean perderPAFerias;

    @Etiqueta("Permitida para Função Gratificada")
    private Boolean permitidaParaFG;

    @Etiqueta("Processa 13º Salário")
    private Boolean processar13Salario;

    @Etiqueta("Não permitir promoção")
    private Boolean naoPermitirPromocao;

    private Boolean naoCalcularFichaSemRetorno;

    private Boolean ativo;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação BBPrev")
    private TipoSituacaoBBPrev tipoSituacaoBBPrev;

    @OneToMany(mappedBy = "tipoAfastamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoAfastamentoTipoPrevidenciaFP> tipoAfastamentoTipoPrevidenciaFPList;
    @Etiqueta("Intervalo a Considerar como Prorrogação (em dias)")
    private Integer intervaloProrrogacaoDias;

    public TipoAfastamento() {
        criadoEm = System.nanoTime();
        calcularValeTransporte = false;
    }

    public TipoAcidenteTransito getTipoAcidenteTransito() {
        return tipoAcidenteTransito;
    }

    public void setTipoAcidenteTransito(TipoAcidenteTransito tipoAcidenteTransito) {
        this.tipoAcidenteTransito = tipoAcidenteTransito;
    }

    public Boolean getProcessaNormal() {
        return processaNormal == null ? false : processaNormal;
    }

    public void setProcessaNormal(Boolean processaNormal) {
        this.processaNormal = processaNormal;
    }

    public Integer getPeriodoNovoAfastamento() {
        return periodoNovoAfastamento;
    }

    public void setPeriodoNovoAfastamento(Integer periodoNovoAfastamento) {
        this.periodoNovoAfastamento = periodoNovoAfastamento;
    }

    public Boolean getPermiteAfastamentoCc() {
        return permiteAfastamentoCc;
    }

    public void setPermiteAfastamentoCc(Boolean permiteAfastamentoCc) {
        this.permiteAfastamentoCc = permiteAfastamentoCc;
    }

    public Boolean getNaoPermitirProgressao() {
        return naoPermitirProgressao;
    }

    public void setNaoPermitirProgressao(Boolean naoPermitirProgressao) {
        this.naoPermitirProgressao = naoPermitirProgressao;
    }

    public Integer getMaximoDiasAfastamentoAno() {
        return maximoDiasAfastamentoAno;
    }

    public void setMaximoDiasAfastamentoAno(Integer maximoDiasAfastamentoAno) {
        this.maximoDiasAfastamentoAno = maximoDiasAfastamentoAno;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getFundamentacaoLegal() {
        return fundamentacaoLegal;
    }

    public void setFundamentacaoLegal(String fundamentacaoLegal) {
        this.fundamentacaoLegal = fundamentacaoLegal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarencia() {
        return carencia;
    }

    public void setCarencia(Long carencia) {
        this.carencia = carencia;
    }

    public Boolean getReiniciarContagem() {
        return reiniciarContagem == null ? false : reiniciarContagem;
    }

    public void setReiniciarContagem(Boolean reiniciarContagem) {
        this.reiniciarContagem = reiniciarContagem;
    }

    public Boolean getCalcularValeTransporte() {
        return calcularValeTransporte;
    }

    public void setCalcularValeTransporte(Boolean calcularValeTransporte) {
        this.calcularValeTransporte = calcularValeTransporte;
    }

    public MotivoAfastamentoRais getMotivoAfastamentoRais() {
        return motivoAfastamentoRais;
    }

    public void setMotivoAfastamentoRais(MotivoAfastamentoRais motivoAfastamentoRais) {
        this.motivoAfastamentoRais = motivoAfastamentoRais;
    }

    public MovimentoSEFIP getMovimentoSEFIPAfastamento() {
        return movimentoSEFIPAfastamento;
    }

    public void setMovimentoSEFIPAfastamento(MovimentoSEFIP movimentoSEFIPAfastamento) {
        this.movimentoSEFIPAfastamento = movimentoSEFIPAfastamento;
    }

    public MovimentoSEFIP getMovimentoSEFIPRetorno() {
        return movimentoSEFIPRetorno;
    }

    public void setMovimentoSEFIPRetorno(MovimentoSEFIP movimentoSEFIPRetorno) {
        this.movimentoSEFIPRetorno = movimentoSEFIPRetorno;
    }

    public ReferenciaFP getReferenciaFP() {
        return referenciaFP;
    }

    public void setReferenciaFP(ReferenciaFP referenciaFP) {
        this.referenciaFP = referenciaFP;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getDescTempoServicoAposentadoria() {
        return descTempoServicoAposentadoria;
    }

    public void setDescTempoServicoAposentadoria(Boolean descTempoServicoAposentadoria) {
        this.descTempoServicoAposentadoria = descTempoServicoAposentadoria;
    }

    public Boolean getDescontarDSR() {
        return descontarDSR;
    }

    public void setDescontarDSR(Boolean descontarDSR) {
        this.descontarDSR = descontarDSR;
    }

    public Boolean getNaoGerarPeriodoAquisitivo() {
        return naoGerarPeriodoAquisitivo == null ? false : naoGerarPeriodoAquisitivo;
    }

    public void setNaoGerarPeriodoAquisitivo(Boolean naoGerarPeriodoAquisitivo) {
        this.naoGerarPeriodoAquisitivo = naoGerarPeriodoAquisitivo;
    }

    public Boolean getDescontarDiasTrabalhados() {
        return descontarDiasTrabalhados == null ? false : descontarDiasTrabalhados;
    }

    public void setDescontarDiasTrabalhados(Boolean descontarDiasTrabalhados) {
        this.descontarDiasTrabalhados = descontarDiasTrabalhados;
    }

    public Boolean getDescontarTempoServicoEfetivo() {
        return descontarTempoServicoEfetivo;
    }

    public void setDescontarTempoServicoEfetivo(Boolean descontarTempoServicoEfetivo) {
        this.descontarTempoServicoEfetivo = descontarTempoServicoEfetivo;
    }

    public Boolean getPagoEntidadePrevidenciaria() {
        return pagoEntidadePrevidenciaria;
    }

    public void setPagoEntidadePrevidenciaria(Boolean pagoEntidadePrevidenciaria) {
        this.pagoEntidadePrevidenciaria = pagoEntidadePrevidenciaria;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public ClasseAfastamento getClasseAfastamento() {
        return classeAfastamento;
    }

    public void setClasseAfastamento(ClasseAfastamento classeAfastamento) {
        this.classeAfastamento = classeAfastamento;
    }

    public Boolean getAlongarPeriodoAquisitivo() {
        return alongarPeriodoAquisitivo == null ? false : alongarPeriodoAquisitivo;
    }

    public void setAlongarPeriodoAquisitivo(Boolean alongarPeriodoAquisitivo) {
        this.alongarPeriodoAquisitivo = alongarPeriodoAquisitivo;
    }

    public Integer getCarenciaAlongamento() {
        return carenciaAlongamento;
    }

    public void setCarenciaAlongamento(Integer carenciaAlongamento) {
        this.carenciaAlongamento = carenciaAlongamento;
    }

    public Integer getMaximoPerdaPeriodoAquisitivo() {
        return maximoPerdaPeriodoAquisitivo;
    }

    public void setMaximoPerdaPeriodoAquisitivo(Integer maximoPerdaPeriodoAquisitivo) {
        this.maximoPerdaPeriodoAquisitivo = maximoPerdaPeriodoAquisitivo;
    }

    public Boolean isPeriodoAquisitivoAlongado() {
        return alongarPeriodoAquisitivo;
    }

    public Integer getDiasMaximoPermitido() {
        return diasMaximoPermitido;
    }

    public void setDiasMaximoPermitido(Integer diasMaximoPermitido) {
        this.diasMaximoPermitido = diasMaximoPermitido;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getPermitirFrequenciaPeriodo() {
        return permitirFrequenciaPeriodo;
    }

    public void setPermitirFrequenciaPeriodo(Boolean permitirFrequenciaPeriodo) {
        this.permitirFrequenciaPeriodo = permitirFrequenciaPeriodo;
    }

    public Boolean getExigeSindicato() {
        return exigeSindicato != null ? exigeSindicato : false;
    }

    public void setExigeSindicato(Boolean exigeSindicato) {
        this.exigeSindicato = exigeSindicato;
    }

    public Boolean getPerderPAFerias() {
        return perderPAFerias == null ? false : perderPAFerias;
    }

    public void setPerderPAFerias(Boolean perderPAFerias) {
        this.perderPAFerias = perderPAFerias;
    }

    public Boolean getPermitidaParaFG() {
        return permitidaParaFG;
    }

    public void setPermitidaParaFG(Boolean permitidaParaFG) {
        this.permitidaParaFG = permitidaParaFG;
    }

    public Boolean getProcessar13Salario() {
        return processar13Salario;
    }

    public void setProcessar13Salario(Boolean processar13Salario) {
        this.processar13Salario = processar13Salario;
    }

    public Boolean getNaoPermitirPromocao() {
        return naoPermitirPromocao;
    }

    public void setNaoPermitirPromocao(Boolean naoPermitirPromocao) {
        this.naoPermitirPromocao = naoPermitirPromocao;
    }

    public TipoAfastamentoESocial getTipoAfastamentoESocial() {
        return tipoAfastamentoESocial;
    }

    public void setTipoAfastamentoESocial(TipoAfastamentoESocial tipoAfastamentoESocial) {
        this.tipoAfastamentoESocial = tipoAfastamentoESocial;
    }

    public Boolean getNaoCalcularFichaSemRetorno() {
        return naoCalcularFichaSemRetorno != null ? naoCalcularFichaSemRetorno : false;
    }

    public void setNaoCalcularFichaSemRetorno(Boolean naoCalcularFichaSemRetorno) {
        this.naoCalcularFichaSemRetorno = naoCalcularFichaSemRetorno;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public TipoSituacaoBBPrev getTipoSituacaoBBPrev() {
        return tipoSituacaoBBPrev;
    }

    public void setTipoSituacaoBBPrev(TipoSituacaoBBPrev tipoSituacaoBBPrev) {
        this.tipoSituacaoBBPrev = tipoSituacaoBBPrev;
    }

    public List<TipoAfastamentoTipoPrevidenciaFP> getTipoAfastamentoTipoPrevidenciaFPList() {
        return tipoAfastamentoTipoPrevidenciaFPList;
    }

    public void setTipoAfastamentoTipoPrevidenciaFPList(List<TipoAfastamentoTipoPrevidenciaFP> tipoAfastamentoTipoPrevidenciaFPList) {
        this.tipoAfastamentoTipoPrevidenciaFPList = tipoAfastamentoTipoPrevidenciaFPList;
    }

    public Integer getIntervaloProrrogacaoDias() {
        return intervaloProrrogacaoDias != null ? intervaloProrrogacaoDias : 1;
    }

    public void setIntervaloProrrogacaoDias(Integer intervaloProrrogacaoDias) {
        this.intervaloProrrogacaoDias = intervaloProrrogacaoDias;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        return (codigo != null ? codigo + " - " : "") + descricao + " (Max. dias " + diasMaximoPermitido + ")";
    }
}
