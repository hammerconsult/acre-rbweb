/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.dirf.DirfVinculoFP;
import br.com.webpublico.enums.TipoDirf;
import br.com.webpublico.enums.TipoEmissaoDirf;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Recursos Humanos")
@Etiqueta("DIRF")
@Entity
public class Dirf implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Dirf.class);

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Obrigatorio
    @Etiqueta("Ano Calendário")
    @Pesquisavel
    @Tabelavel
    private Exercicio exercicio;
    @OneToOne
    private Arquivo arquivo;
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Processada Em")
    private Date processadaEm;
    @ManyToOne
    @Etiqueta("Entidade")
    @Tabelavel
    private Entidade entidade;
    @ManyToOne
    @Etiqueta("Responsável")
    private ContratoFP responsavel;
    @Transient
    @Invisivel
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;
    @Transient
    @Invisivel
    private List<RecursoFP> recursos;
    @Transient
    @Invisivel
    private Date dataOperacao;
    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoDirf tipoDirf;
    @Etiqueta("Tipo Emissão Dirf")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoEmissaoDirf tipoEmissaoDirf;
    @Transient
    @Invisivel
    private Long criadoEm;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "dirf")
    @Invisivel
    private List<DirfVinculoFP> itemDirfVinculoFP;

    @Transient
    private List<DirfVinculoFP> itemVinculoFP;

    public Dirf() {
        criadoEm = System.nanoTime();
        itemDirfVinculoFP = Lists.newArrayList();
        itemVinculoFP = Lists.newArrayList();
    }

    public List<DirfVinculoFP> getItemVinculoFP() {
        return itemVinculoFP;
    }

    public void setItemVinculoFP(List<DirfVinculoFP> itemVinculoFP) {
        this.itemVinculoFP = itemVinculoFP;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public List<DirfVinculoFP> getItemDirfVinculoFP() {
        return itemDirfVinculoFP;
    }

    public void setItemDirfVinculoFP(List<DirfVinculoFP> itemDirfVinculoFP) {
        this.itemDirfVinculoFP = itemDirfVinculoFP;
    }

    public ContratoFP getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(ContratoFP responsavel) {
        this.responsavel = responsavel;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Date getProcessadaEm() {
        return processadaEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<RecursoFP> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoFP> recursos) {
        this.recursos = recursos;
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
            return "DIRF - " + exercicio.getAno();
        } catch (NullPointerException npe) {
            return "DIRF - " + "(Sem exercício associado)";
        }
    }

    public void atualizarDataProcessamento() {
        processadaEm = new Date();
    }

    public StreamedContent recuperarArquivoParaDownload() {
        StreamedContent s = null;
        if (getArquivo() != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (ArquivoParte a : getArquivo().getPartes()) {
                try {
                    buffer.write(a.getDados());
                } catch (IOException ex) {
                    logger.error("Erro: ", ex);
                }
            }
            InputStream is = new ByteArrayInputStream(buffer.toByteArray());
            s = new DefaultStreamedContent(is, getArquivo().getMimeType(), getArquivo().getNome());
        }
        return s;
    }

    public Date getPrimeiroDia() {
        Calendar pDia = Calendar.getInstance();
        pDia.set(Calendar.DAY_OF_MONTH, 1);
        pDia.set(Calendar.MONTH, 0);
        try {
            pDia.set(Calendar.YEAR, getExercicio().getAno());
        } catch (NullPointerException npe) {
            return null;
        }

        pDia.setTime(Util.getDataHoraMinutoSegundoZerado(pDia.getTime()));
        return pDia.getTime();
    }

    public Date getUltimoDia() {
        Calendar uDia = Calendar.getInstance();
        uDia.set(Calendar.DAY_OF_MONTH, 31);
        uDia.set(Calendar.MONTH, 11);
        try {
            uDia.set(Calendar.YEAR, getExercicio().getAno());
        } catch (NullPointerException npe) {
            return null;
        }

        uDia.setTime(Util.getDataHoraMinutoSegundoZerado(uDia.getTime()));
        return uDia.getTime();
    }

    public TipoDirf getTipoDirf() {
        return tipoDirf;
    }

    public void setTipoDirf(TipoDirf tipoDirf) {
        this.tipoDirf = tipoDirf;
    }

    public TipoEmissaoDirf getTipoEmissaoDirf() {
        return tipoEmissaoDirf;
    }

    public void setTipoEmissaoDirf(TipoEmissaoDirf tipoEmissaoDirf) {
        this.tipoEmissaoDirf = tipoEmissaoDirf;
    }


}
