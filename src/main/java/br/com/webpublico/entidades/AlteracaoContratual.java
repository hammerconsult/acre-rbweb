/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Alteração Contratual")
public class AlteracaoContratual extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao, Comparable<AlteracaoContratual> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private String numero;

    @Etiqueta("Número do Termo")
    private String numeroTermo;

    @ManyToOne
    @Etiqueta("Ano do Termo")
    private Exercicio exercicio;

    @Etiqueta("Tipo de Cadastro")
    @Enumerated(EnumType.STRING)
    private TipoCadastroAlteracaoContratual tipoCadastro;

    @Etiqueta("Tipo de Alteração Contratual")
    @Enumerated(EnumType.STRING)
    private TipoAlteracaoContratual tipoAlteracaoContratual;

    @Etiqueta("Tipo de Termo")
    @Enumerated(EnumType.STRING)
    private TipoTermoAlteracaoContratual tipoTermo;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Aprovação")
    private Date dataAprovacao;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Deferimento")
    private Date dataDeferimento;

    @Etiqueta("Data de Assinatura")
    @Temporal(TemporalType.DATE)
    private Date dataAssinatura;

    @Length(maximo = 3000)
    @Etiqueta("Justificativa")
    private String justificativa;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoContrato situacao;

    @OneToOne(cascade = CascadeType.ALL)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "alteracaoContratual")
    private List<MovimentoAlteracaoContratual> movimentos;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "alteracaoContratual")
    private List<PublicacaoAlteracaoContratual> publicacoes;

    @Etiqueta("Contrato")
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "alteracaoContratual", orphanRemoval = true)
    private AlteracaoContratualContrato alteracaoContratualContrato;

    @Etiqueta("Ata Registro de Preço")
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "alteracaoContratual", orphanRemoval = true)
    private AlteracaoContratualAta alteracaoContratualAta;

    private Boolean movimentarSaldoOrcamentario;

    public AlteracaoContratual() {
        situacao = SituacaoContrato.EM_ELABORACAO;
        movimentos = Lists.newArrayList();
        movimentarSaldoOrcamentario = Boolean.FALSE;
        dataLancamento = new Date();
        publicacoes = Lists.newArrayList();
    }

    public Boolean getMovimentarSaldoOrcamentario() {
        return movimentarSaldoOrcamentario;
    }

    public void setMovimentarSaldoOrcamentario(Boolean movimentarSaldoOrcamentario) {
        this.movimentarSaldoOrcamentario = movimentarSaldoOrcamentario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public Contrato getContrato() {
        return alteracaoContratualContrato.getContrato();
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return alteracaoContratualAta.getAtaRegistroPreco();
    }

    public String getNumeroTermo() {
        return numeroTermo;
    }

    public void setNumeroTermo(String numeroTermo) {
        this.numeroTermo = numeroTermo;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public Date getDataDeferimento() {
        return dataDeferimento;
    }

    public void setDataDeferimento(Date dataDeferimento) {
        this.dataDeferimento = dataDeferimento;
    }

    public SituacaoContrato getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoContrato situacao) {
        this.situacao = situacao;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public List<MovimentoAlteracaoContratual> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<MovimentoAlteracaoContratual> movimentos) {
        this.movimentos = movimentos;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }


    public TipoTermoAlteracaoContratual getTipoTermo() {
        return tipoTermo;
    }

    public void setTipoTermo(TipoTermoAlteracaoContratual tipoTermo) {
        this.tipoTermo = tipoTermo;
    }

    public List<PublicacaoAlteracaoContratual> getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(List<PublicacaoAlteracaoContratual> publicacoes) {
        this.publicacoes = publicacoes;
    }

    public TipoAlteracaoContratual getTipoAlteracaoContratual() {
        return tipoAlteracaoContratual;
    }

    public void setTipoAlteracaoContratual(TipoAlteracaoContratual tipoAlteracaoContratual) {
        this.tipoAlteracaoContratual = tipoAlteracaoContratual;
    }

    public TipoCadastroAlteracaoContratual getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroAlteracaoContratual tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public Date getTerminoVigenciaAtual(){
        if (tipoCadastro.isContrato()){
            return getContrato().getTerminoVigenciaAtual();
        }
        return getAtaRegistroPreco().getDataVencimentoAtual();
    }

    public BigDecimal getValorTotal() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasMovimentos()) {
            for (MovimentoAlteracaoContratual mov : movimentos) {
                valor = valor.add(mov.getValorTotalItens());
            }
        }
        return valor;
    }

    @Override
    public String toString() {
        try {
            String numeroAnoTermo = !Strings.isNullOrEmpty(getNumeroTermo()) ? "-" + getNumeroAnoTermo() : " ";
            if (tipoCadastro.isContrato()) {
                return getNumero() + numeroAnoTermo + " - " + getContrato().getContratado();
            }
            return getNumero() + numeroAnoTermo + " - " + getAtaRegistroPreco().getLicitacao().getResumoObjetoSubstrig();
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public String getDescricaoTipoTermo() {
        return tipoAlteracaoContratual.getDescricao();
    }

    public String getTituloTela() {
        return tipoAlteracaoContratual.getDescricao() + " " + tipoCadastro.getDescricao();
    }

    public boolean isAditivo() {
        return tipoAlteracaoContratual != null && tipoAlteracaoContratual.isAditivo();
    }

    public boolean isApostilamento() {
        return tipoAlteracaoContratual != null && tipoAlteracaoContratual.isApostilamento();
    }

    public boolean isAlteracaoContratualValor() {
        return movimentos.stream().anyMatch(MovimentoAlteracaoContratual::isOperacoesValor);
    }

    public boolean isTransferenciaUnidade() {
        return movimentos.stream().anyMatch(MovimentoAlteracaoContratual::isTransferenciaUnidade);
    }

    public boolean isTransferenciaDotacao() {
        return movimentos.stream().anyMatch(MovimentoAlteracaoContratual::isTransferenciaDotacao);
    }

    public boolean hasMovimentos() {
        return !Util.isListNullOrEmpty(movimentos);
    }

    public String getNumeroAnoTermo() {
        return numeroTermo + "/" + exercicio.getAno();
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    public AlteracaoContratualContrato getAlteracaoContratualContrato() {
        return alteracaoContratualContrato;
    }

    public void setAlteracaoContratualContrato(AlteracaoContratualContrato alteracaoContratualContrato) {
        this.alteracaoContratualContrato = alteracaoContratualContrato;
    }

    public AlteracaoContratualAta getAlteracaoContratualAta() {
        return alteracaoContratualAta;
    }

    public void setAlteracaoContratualAta(AlteracaoContratualAta alteracaoContratualAta) {
        this.alteracaoContratualAta = alteracaoContratualAta;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        if (tipoAlteracaoContratual.isAditivo()) {
            return TipoMovimentoProcessoLicitatorio.ADITIVO_CONTRATO;
        }
        return TipoMovimentoProcessoLicitatorio.APOSTILAMENTO_CONTRATO;
    }

    public Long getIdProcesso() {
        if (tipoCadastro.isContrato()) {
            return getContrato().getId();
        }
        return getAtaRegistroPreco().getId();
    }

    @Override
    public int compareTo(AlteracaoContratual o) {
        if (getDataLancamento() != null && o.getDataLancamento() != null
            && getNumeroTermo() != null && o.getNumeroTermo() != null) {
            return ComparisonChain.start()
                .compare(getNumeroTermo(), o.getNumeroTermo())
                .compare(getDataLancamento(), o.getDataLancamento())
                .result();
        }
        return 0;
    }
}
