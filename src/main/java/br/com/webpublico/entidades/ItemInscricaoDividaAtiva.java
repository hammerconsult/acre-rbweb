/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
@Entity

@Audited
@Etiqueta("Item Inscrição Dívida Ativa")
@GrupoDiagrama(nome = "Divida Ativa")
public class ItemInscricaoDividaAtiva extends Calculo implements Serializable {

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Inscrição")
    private InscricaoDividaAtiva inscricaoDividaAtiva;
    @Transient
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Tipo de Cadastro")
    private TipoCadastroTributario tipoCadastroTabelavel;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Contribuinte")
    private Pessoa pessoa;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Dívida")
    private Divida divida;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "itemInscricaoDividaAtiva")
    private List<InscricaoDividaParcela> itensInscricaoDividaParcelas;
    @Transient
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data da Inscrição")
    private Date dateInscricaoTabelavel;
    @Transient
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Venc Inicial da Insc")
    private Date vencimentoInicialInscricaoTabelavel;
    @Transient
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Venc Final da Insc")
    private Date vencimentoFinalInscricaoTabelavel;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    private Situacao situacao;
    @ManyToOne
    private Exercicio exercicio;
    @Transient
    private long criadoEm;
    private String migracaoSituacao;
    private String migracaoChave;
    @Transient
    private Boolean marcado;
    @Transient
    private ResultadoParcela resultadoParcela;
    @Transient
    private ValorDivida valorDivida;
    @OneToMany(mappedBy = "itemInscricaoDividaAtiva")
    private List<ItemCertidaoDividaAtiva> itensCertidao;
    @Transient
    @Tabelavel
    @Etiqueta("Certidão")
    private String certidaoDividaAtivaTabelavel;
    @Transient
    @Tabelavel
    @Etiqueta("Ajuizamento")
    private String ajuizamentoTabelavel;
    @Transient
    @Tabelavel
    @Etiqueta("Referência")
    private String referenciaParcelaTabelavel;
    @Transient
    @Tabelavel
    @Etiqueta("Exercício")
    private Integer exercicioParcelaTabelavel;
    @Transient
    @Tabelavel
    @Etiqueta("Parcela")
    private String parcelaTabelavel;
    @Transient
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Vencimento")
    private Date vencimentoParcelaTabelavel;
    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor da Parcela")
    private BigDecimal valorTotalParcelaTabelavel;

    public ItemInscricaoDividaAtiva(Long id, InscricaoDividaAtiva inscricaoDividaAtiva, Cadastro cadastro, Pessoa pessoa, Divida divida, Situacao situacao, InscricaoDividaParcela itemInscricaoDividaParcela, String certidaoDividaAtivaTabelavel, String ajuizamentoTabelavel) {
        this.setId(id);
        this.inscricaoDividaAtiva = inscricaoDividaAtiva;
        setCadastro(cadastro);
        this.pessoa = pessoa;
        this.divida = divida;
        this.situacao = situacao;
        if (this.itensInscricaoDividaParcelas == null) {
            this.itensInscricaoDividaParcelas = Lists.newArrayList();
        }
        this.itensInscricaoDividaParcelas.add(itemInscricaoDividaParcela);
        this.certidaoDividaAtivaTabelavel = certidaoDividaAtivaTabelavel;
        this.ajuizamentoTabelavel = ajuizamentoTabelavel;
    }

    public ItemInscricaoDividaAtiva() {
        this.criadoEm = System.nanoTime();
        super.setTipoCalculo(TipoCalculo.INSCRICAO_DA);
        itensInscricaoDividaParcelas = new ArrayList<InscricaoDividaParcela>();
        this.marcado = Boolean.FALSE;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public InscricaoDividaAtiva getInscricaoDividaAtiva() {
        return inscricaoDividaAtiva;
    }

    public List<ItemCertidaoDividaAtiva> getItensCertidao() {
        return itensCertidao;
    }

    public void setItensCertidao(List<ItemCertidaoDividaAtiva> itensCertidao) {
        this.itensCertidao = itensCertidao;
    }

    public void setInscricaoDividaAtiva(InscricaoDividaAtiva inscricaoDividaAtiva) {
        super.setProcessoCalculo(inscricaoDividaAtiva);
        this.inscricaoDividaAtiva = inscricaoDividaAtiva;
    }

    public List<InscricaoDividaParcela> getItensInscricaoDividaParcelas() {
        return itensInscricaoDividaParcelas;
    }

    public void setItensInscricaoDividaParcelas(List<InscricaoDividaParcela> itensInscricaoDividaParcelas) {
        this.itensInscricaoDividaParcelas = itensInscricaoDividaParcelas;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return inscricaoDividaAtiva;
    }

    public String getMigracaoSituacao() {
        return migracaoSituacao;
    }

    public void setMigracaoSituacao(String migracaoSituacao) {
        this.migracaoSituacao = migracaoSituacao;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Boolean getMarcado() {
        return marcado;
    }

    public void setMarcado(Boolean marcado) {
        this.marcado = marcado;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }

    public ValorDivida getValorDivida() {
        return valorDivida;
    }

    public void setValorDivida(ValorDivida valorDivida) {
        this.valorDivida = valorDivida;
    }

    public Date getDateInscricaoTabelavel() {
        return dateInscricaoTabelavel;
    }

    public void setDateInscricaoTabelavel(Date dateInscricaoTabelavel) {
        this.dateInscricaoTabelavel = dateInscricaoTabelavel;
    }

    public TipoCadastroTributario getTipoCadastroTabelavel() {
        return tipoCadastroTabelavel;
    }

    public void setTipoCadastroTabelavel(TipoCadastroTributario tipoCadastroTabelavel) {
        this.tipoCadastroTabelavel = tipoCadastroTabelavel;
    }

    public String getCertidaoDividaAtivaTabelavel() {
        return certidaoDividaAtivaTabelavel;
    }

    public void setCertidaoDividaAtivaTabelavel(String certidaoDividaAtivaTabelavel) {
        this.certidaoDividaAtivaTabelavel = certidaoDividaAtivaTabelavel;
    }

    public String getAjuizamentoTabelavel() {
        return ajuizamentoTabelavel;
    }

    public void setAjuizamentoTabelavel(String ajuizamentoTabelavel) {
        this.ajuizamentoTabelavel = ajuizamentoTabelavel;
    }

    public String getReferenciaParcelaTabelavel() {
        return referenciaParcelaTabelavel;
    }

    public void setReferenciaParcelaTabelavel(String referenciaParcelaTabelavel) {
        this.referenciaParcelaTabelavel = referenciaParcelaTabelavel;
    }

    public String getParcelaTabelavel() {
        return parcelaTabelavel;
    }

    public void setParcelaTabelavel(String parcelaTabelavel) {
        this.parcelaTabelavel = parcelaTabelavel;
    }

    public Date getVencimentoInicialInscricaoTabelavel() {
        return vencimentoInicialInscricaoTabelavel;
    }

    public void setVencimentoInicialInscricaoTabelavel(Date vencimentoInicialInscricaoTabelavel) {
        this.vencimentoInicialInscricaoTabelavel = vencimentoInicialInscricaoTabelavel;
    }

    public Date getVencimentoFinalInscricaoTabelavel() {
        return vencimentoFinalInscricaoTabelavel;
    }

    public void setVencimentoFinalInscricaoTabelavel(Date vencimentoFinalInscricaoTabelavel) {
        this.vencimentoFinalInscricaoTabelavel = vencimentoFinalInscricaoTabelavel;
    }

    public Integer getExercicioParcelaTabelavel() {
        return exercicioParcelaTabelavel;
    }

    public void setExercicioParcelaTabelavel(Integer exercicioParcelaTabelavel) {
        this.exercicioParcelaTabelavel = exercicioParcelaTabelavel;
    }

    public Date getVencimentoParcelaTabelavel() {
        return vencimentoParcelaTabelavel;
    }

    public void setVencimentoParcelaTabelavel(Date vencimentoParcelaTabelavel) {
        this.vencimentoParcelaTabelavel = vencimentoParcelaTabelavel;
    }

    public BigDecimal getValorTotalParcelaTabelavel() {
        return valorTotalParcelaTabelavel;
    }

    public void setValorTotalParcelaTabelavel(BigDecimal valorTotalParcelaTabelavel) {
        this.valorTotalParcelaTabelavel = valorTotalParcelaTabelavel;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public static enum Situacao implements EnumComDescricao {
        ATIVO("Ativo"),
        CANCELADO("Cancelado");

        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
