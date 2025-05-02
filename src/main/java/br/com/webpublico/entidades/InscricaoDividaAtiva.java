/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoInscricaoDividaAtiva;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoVencimentoParcela;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Ativa")
@Etiqueta("Processo de Inscrição em Dívida Ativa ")
public class InscricaoDividaAtiva extends ProcessoCalculo implements Serializable {

    @Tabelavel(ordemApresentacao = 2)
    @Etiqueta(value = "Número")
    @Pesquisavel
    private Long numero;
    @ManyToOne
    @Pesquisavel
    @Etiqueta(value = "Exercício Inicial")
    private Exercicio exercicio;
    @ManyToOne
    @Pesquisavel
    @Etiqueta(value = "Exercício Final")
    private Exercicio exercicioFinal;
    @Tabelavel(ordemApresentacao = 3)
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta(value = "Data da Inscrição")
    @Pesquisavel
    private Date dataInscricao;
    @Tabelavel(ordemApresentacao = 4)
    @Pesquisavel
    @Etiqueta(value = "Tipo de Cadastro")
    @Enumerated(EnumType.STRING)
    private TipoCadastroTributario tipoCadastroTributario;
    @Etiqueta(value = "Cadastro Inicial")
    @Tabelavel(ordemApresentacao = 5)
    private String cadastroInicial;
    @Etiqueta(value = "Cadastro Final")
    @Tabelavel(ordemApresentacao = 6)
    private String cadastroFinal;
    @ManyToOne
    private Pessoa contribuinte;
    @Etiqueta(value = "Contribuinte")
    @Tabelavel(ordemApresentacao = 7)
    @Transient
    private String cpfCnpjComNomeContribuinte;
    @Tabelavel(ordemApresentacao = 8)
    @Etiqueta(value = "Vencimento Inicial")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimentoInicial;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel(ordemApresentacao = 9)
    @Etiqueta(value = "Vencimento Final")
    private Date vencimentoFinal;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 10)
    @Etiqueta(value = "Situação")
    private SituacaoInscricaoDividaAtiva situacaoInscricaoDividaAtiva;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "inscricaoDividaAtiva")
    private List<DividaAtivaDivida> dividaAtivaDividas;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "inscricaoDividaAtiva")
    private List<ItemInscricaoDividaAtiva> itensInscricaoDividaAtivas;
    @Enumerated(EnumType.STRING)
    private TipoVencimentoParcela tipoVencimentoParcela;
    private Boolean agruparParcelas;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private CancelamentoInscricaoDA cancelamentoInscricaoDA;
    @Transient
    @Invisivel
    private Long criadoEm;
    private String migracaochave;

    public InscricaoDividaAtiva() {
        this.criadoEm = System.nanoTime();
        this.itensInscricaoDividaAtivas = new ArrayList<>();
        agruparParcelas = true;
    }

    public InscricaoDividaAtiva(Long id, Long numero, Exercicio exercicio, Exercicio exercicioFinal, Date dataInscricao, TipoCadastroTributario tipoCadastroTributario, String cadastroInicial, String cadastroFinal, Pessoa contribuinte, Date vencimentoInicial, Date vencimentoFinal, SituacaoInscricaoDividaAtiva situacaoInscricaoDividaAtiva) {
        this.criadoEm = System.nanoTime();
        this.itensInscricaoDividaAtivas = new ArrayList<>();

        setId(id);
        this.numero = numero;
        this.exercicio = exercicio;
        this.exercicioFinal = exercicioFinal;
        this.dataInscricao = dataInscricao;
        this.tipoCadastroTributario = tipoCadastroTributario;
        this.cadastroInicial = cadastroInicial;
        this.cadastroFinal = cadastroFinal;
        this.contribuinte = contribuinte;
        this.vencimentoInicial = vencimentoInicial;
        this.vencimentoFinal = vencimentoFinal;
        this.situacaoInscricaoDividaAtiva = situacaoInscricaoDividaAtiva;

        if (contribuinte != null) {
            this.cpfCnpjComNomeContribuinte = this.contribuinte.getNomeCpfCnpj();
        }
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public String getCpfCnpjComNomeContribuinte() {
        return cpfCnpjComNomeContribuinte;
    }

    public void setCpfCnpjComNomeContribuinte(String cpfCnpjComNomeContribuinte) {
        this.cpfCnpjComNomeContribuinte = cpfCnpjComNomeContribuinte;
    }

    public CancelamentoInscricaoDA getCancelamentoInscricaoDA() {
        return cancelamentoInscricaoDA;
    }

    public void setCancelamentoInscricaoDA(CancelamentoInscricaoDA cancelamentoInscricaoDA) {
        this.cancelamentoInscricaoDA = cancelamentoInscricaoDA;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(Date dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public List<DividaAtivaDivida> getDividaAtivaDividas() {
        return dividaAtivaDividas;
    }

    public void setDividaAtivaDividas(List<DividaAtivaDivida> dividaAtivaDividas) {
        this.dividaAtivaDividas = dividaAtivaDividas;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public List<ItemInscricaoDividaAtiva> getItensInscricaoDividaAtivas() {
        return itensInscricaoDividaAtivas;
    }

    public void setItensInscricaoDividaAtivas(List<ItemInscricaoDividaAtiva> itensInscricaoDividaAtivas) {
        this.itensInscricaoDividaAtivas = itensInscricaoDividaAtivas;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public SituacaoInscricaoDividaAtiva getSituacaoInscricaoDividaAtiva() {
        return situacaoInscricaoDividaAtiva;
    }

    public void setSituacaoInscricaoDividaAtiva(SituacaoInscricaoDividaAtiva situacaoInscricaoDividaAtiva) {
        this.situacaoInscricaoDividaAtiva = situacaoInscricaoDividaAtiva;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public Boolean getAgruparParcelas() {
        return agruparParcelas;
    }

    public void setAgruparParcelas(Boolean agruparParcelas) {
        this.agruparParcelas = agruparParcelas;
    }

    public TipoVencimentoParcela getTipoVencimentoParcela() {
        return tipoVencimentoParcela;
    }

    public void setTipoVencimentoParcela(TipoVencimentoParcela tipoVencimentoParcela) {
        this.tipoVencimentoParcela = tipoVencimentoParcela;
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
        return "Nr.: " + this.numero + " - " + this.exercicio.getAno() +
            (this.exercicioFinal != null ? "/" + this.exercicioFinal.getAno() : "");
    }

    public String getMigracaochave() {
        return migracaochave;
    }

    public void setMigracaochave(String migracaochave) {
        this.migracaochave = migracaochave;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return itensInscricaoDividaAtivas;
    }

    public boolean ehContribuinteGeral() {
        return tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA);
    }

    public List<Divida> getDividas() {
        List<Divida> retorno = new ArrayList<>();
        for (DividaAtivaDivida dividaAtivaDivida : dividaAtivaDividas) {
            retorno.add(dividaAtivaDivida.getDivida());
        }
        return retorno;
    }

    public String getFiltosUtilizados() {
        StringBuilder filtros = new StringBuilder("");
        if (tipoCadastroTributario != null) {
            filtros.append(" Tipo de Cadastro: ").append(tipoCadastroTributario.getDescricao());
        }
        if (cadastroInicial != null && cadastroFinal != null) {
            filtros.append(" Cadastro: ").append(cadastroInicial).append(" - ").append(cadastroFinal);
        }
        if (exercicio != null && exercicioFinal != null) {
            filtros.append(" Exercício: ").append(exercicio.getAno()).append(" - ").append(exercicioFinal.getAno());
        }
        if (vencimentoFinal != null) {
            filtros.append(" Vencimento: ").append(vencimentoInicial != null ? DataUtil.getDataFormatada(vencimentoInicial) + " - " : "até ").append((DataUtil.getDataFormatada(vencimentoFinal)));
        }
        return filtros.toString();
    }
}
