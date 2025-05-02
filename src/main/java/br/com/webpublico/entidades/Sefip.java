/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.rh.ParametrosRelatorioConferenciaSefip;
import br.com.webpublico.entidadesauxiliares.rh.VOParametrosRelatorioConferenciaSefip;
import br.com.webpublico.enums.SefipFGTS;
import br.com.webpublico.enums.SefipModalidadeArquivo;
import br.com.webpublico.enums.SefipPrevidenciaSocial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Recursos Humanos")
@Etiqueta("SEFIP")
@Entity
@Audited
public class Sefip implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Modalidade do Arquivo")
    @Enumerated(EnumType.STRING)
    private SefipModalidadeArquivo sefipModalidadeArquivo;
    @Obrigatorio
    @Etiqueta("Mês Calendário")
    @Pesquisavel
    @Tabelavel
    private Integer mes;
    @Obrigatorio
    @Etiqueta("Ano")
    @Pesquisavel
    @Tabelavel
    private Integer ano;
    @Etiqueta("Recolhimento SEFIP")
    @Enumerated(EnumType.STRING)
    @ManyToOne
    private RecolhimentoSEFIP recolhimentoSEFIP;
    @Etiqueta("Estabelecimento")
    @ManyToOne
    private Entidade entidade;
    @ManyToOne
    @Etiqueta("Responsável")
    private PessoaFisica responsavel;
    @Etiqueta("Atualizar Endereços")
    private Boolean atualizaEnderecos;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Processado Em")
    private Date processadoEm;
    @Etiqueta("Valor Compensação")
    private BigDecimal valorCompensacao;
    private Integer mesInicioCompensacao;
    private Integer anoInicioCompensacao;
    private Integer mesFimCompensacao;
    private Integer anoFimCompensacao;
    @Enumerated(EnumType.STRING)
    private SefipFGTS sefipFGTS;
    @Temporal(TemporalType.DATE)
    private Date dataRecolhimentoFGTS;
    @Enumerated(EnumType.STRING)
    private SefipPrevidenciaSocial sefipPrevidenciaSocial;
    @Temporal(TemporalType.DATE)
    private Date dataRecolhimentoPrevidencia;
    @Etiqueta("Índice Atraso")
    private BigDecimal indiceAtrasoPrevidencia;
    @OneToOne
    @Etiqueta("Arquivo")
    private Arquivo arquivo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sefip", orphanRemoval = true)
    @Tabelavel
    @Etiqueta("Folhas de Pagamento")
    private List<SefipFolhaDePagamento> sefipFolhasDePagamento;
    @Transient
    @Invisivel
    private Long criadoEm;
    @Transient
    @Invisivel
    private Date dataOperacao;
    @Invisivel
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Arquivo relatorioDeConferencia;

    public Sefip() {
        this.criadoEm = System.nanoTime();
    }

    public BigDecimal getIndiceAtrasoPrevidencia() {
        return indiceAtrasoPrevidencia;
    }

    public void setIndiceAtrasoPrevidencia(BigDecimal indiceAtrasoPrevidencia) {
        this.indiceAtrasoPrevidencia = indiceAtrasoPrevidencia;
    }

    public Arquivo getRelatorioDeConferencia() {
        return relatorioDeConferencia;
    }

    public void setRelatorioDeConferencia(Arquivo relatorioDeConferencia) {
        this.relatorioDeConferencia = relatorioDeConferencia;
    }

    public List<SefipFolhaDePagamento> getSefipFolhasDePagamento() {
        return sefipFolhasDePagamento;
    }

    public void setSefipFolhasDePagamento(List<SefipFolhaDePagamento> sefipFolhasDePagamento) {
        this.sefipFolhasDePagamento = sefipFolhasDePagamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SefipModalidadeArquivo getSefipModalidadeArquivo() {
        return sefipModalidadeArquivo;
    }

    public void setSefipModalidadeArquivo(SefipModalidadeArquivo sefipModalidadeArquivo) {
        this.sefipModalidadeArquivo = sefipModalidadeArquivo;
    }

    public Integer getMesCompetencia() {
        return mes;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        if (mes > 12) {
            setSefipFGTS(SefipFGTS.NAO_INFORMADO);
            setSefipModalidadeArquivo(SefipModalidadeArquivo.DECLARACAO_FGTS);
            this.mes = mes;
            return;
        }
        setSefipFGTS(SefipFGTS.NO_PRAZO);
        setSefipModalidadeArquivo(SefipModalidadeArquivo.RECOLHIMENTO_FGTS);
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }


    public RecolhimentoSEFIP getRecolhimentoSEFIP() {
        return recolhimentoSEFIP;
    }

    public void setRecolhimentoSEFIP(RecolhimentoSEFIP recolhimentoSEFIP) {
        this.recolhimentoSEFIP = recolhimentoSEFIP;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public Boolean getAtualizaEnderecos() {
        return atualizaEnderecos;
    }

    public void setAtualizaEnderecos(Boolean atualizaEnderecos) {
        this.atualizaEnderecos = atualizaEnderecos;
    }

    public Date getProcessadoEm() {
        return processadoEm;
    }

    public void setProcessadoEm(Date processadoEm) {
        this.processadoEm = processadoEm;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public BigDecimal getValorCompensacao() {
        return valorCompensacao;
    }

    public void setValorCompensacao(BigDecimal valorCompensacao) {
        this.valorCompensacao = valorCompensacao;
    }

    public Integer getMesInicioCompensacao() {
        return mesInicioCompensacao;
    }

    public void setMesInicioCompensacao(Integer mesInicioCompensacao) {
        this.mesInicioCompensacao = mesInicioCompensacao;
    }

    public Integer getAnoInicioCompensacao() {
        return anoInicioCompensacao;
    }

    public void setAnoInicioCompensacao(Integer anoInicioCompensacao) {
        this.anoInicioCompensacao = anoInicioCompensacao;
    }

    public Integer getMesFimCompensacao() {
        return mesFimCompensacao;
    }

    public void setMesFimCompensacao(Integer mesFimCompensacao) {
        this.mesFimCompensacao = mesFimCompensacao;
    }

    public Integer getAnoFimCompensacao() {
        return anoFimCompensacao;
    }

    public void setAnoFimCompensacao(Integer anoFimCompensacao) {
        this.anoFimCompensacao = anoFimCompensacao;
    }

    public SefipFGTS getSefipFGTS() {
        return sefipFGTS;
    }

    public void setSefipFGTS(SefipFGTS sefipFGTS) {
        this.sefipFGTS = sefipFGTS;
    }

    public Date getDataRecolhimentoFGTS() {
        return dataRecolhimentoFGTS;
    }

    public void setDataRecolhimentoFGTS(Date dataRecolhimentoFGTS) {
        this.dataRecolhimentoFGTS = dataRecolhimentoFGTS;
    }

    public SefipPrevidenciaSocial getSefipPrevidenciaSocial() {
        return sefipPrevidenciaSocial;
    }

    public void setSefipPrevidenciaSocial(SefipPrevidenciaSocial sefipPrevidenciaSocial) {
        this.sefipPrevidenciaSocial = sefipPrevidenciaSocial;
    }

    public Date getDataRecolhimentoPrevidencia() {
        return dataRecolhimentoPrevidencia;
    }

    public void setDataRecolhimentoPrevidencia(Date dataRecolhimentoPrevidencia) {
        this.dataRecolhimentoPrevidencia = dataRecolhimentoPrevidencia;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        try {
            return "SEFIP - " + getMes() + "/" + getAno() + " - " + getEntidade();
        } catch (NullPointerException npe) {
            return "SEFIP - (Sem período associado)";
        }
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Date getPrimeiroDiaSeisMesesAntes() {
        Calendar pd = Calendar.getInstance();
        pd.set(Calendar.DAY_OF_MONTH, 1);
        pd.set(Calendar.MONTH, this.getMes() - 7);
        pd.set(Calendar.YEAR, this.getAno());
        pd.setTime(Util.getDataHoraMinutoSegundoZerado(pd.getTime()));
        return pd.getTime();
    }

    public Date getPrimeiroDiaDoMes() {
        Calendar pd = Calendar.getInstance();
        pd.set(Calendar.DAY_OF_MONTH, 1);
        pd.set(Calendar.MONTH, getMesCorreto() - 1);
        pd.set(Calendar.YEAR, this.getAno());
        pd.setTime(Util.getDataHoraMinutoSegundoZerado(pd.getTime()));
        return pd.getTime();
    }

    public Date getUltimoDiaDoMes() {
        Calendar ud = Calendar.getInstance();
        ud.set(Calendar.MONTH, getMesCorreto() - 1);
        ud.set(Calendar.DAY_OF_MONTH, ud.getActualMaximum(Calendar.DAY_OF_MONTH));
        ud.set(Calendar.YEAR, this.getAno());
        ud.setTime(Util.getDataHoraMinutoSegundoZerado(ud.getTime()));
        return ud.getTime();
    }

    public Integer getMesCorreto() {
        if (getMes() == 13) {
            return 12;
        }
        return getMes();
    }
}
