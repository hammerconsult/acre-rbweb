/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.cadastrofuncional.PeriodoAquisitivoExcluido;
import br.com.webpublico.entidades.rh.esocial.CargoEmpregadorESocial;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.estudoatuarial.TipoEspecificacaoCargo;
import br.com.webpublico.enums.rh.esocial.TipoContagemEspecialEsocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static br.com.webpublico.util.DataUtil.getAnosAndDiasFormatadosPorPeriodo;

/**
 * @author terminal3
 */
@GrupoDiagrama(nome = "Protocolo")
@Audited
@CorEntidade(value = "#FFFF00")
@Entity

public class Cargo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Column(length = 70)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BaseCargo> baseCargos;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Código do Cargo")
    private String codigoDoCargo;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final da Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vaga> vagas;
    @Obrigatorio
    @Etiqueta("Tipo PCS")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private TipoPCS tipoPCS;
    @ManyToOne
    @Etiqueta("Nível de Escolaridade")
    private NivelEscolaridade nivelEscolaridade;
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "CARGOCBO",
        joinColumns = {
            @JoinColumn(name = "CARGO_ID", referencedColumnName = "ID")}, inverseJoinColumns = {
        @JoinColumn(name = "CBO_ID", referencedColumnName = "ID")})
    private List<CBO> cbos;
    @Etiqueta("Permite Acúmulo")
    private Boolean permiteAcumulo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ativo")
    private Boolean ativo;
    @Tabelavel
    @Etiqueta("Tempo de estágio probatório")
    private Integer tempoEstagioProbatorio; // Dias
    @Transient
    @Invisivel
    private Long criadoEm;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código da Carreira")
    @Obrigatorio
    private String codigoCarreira;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição da Carreira")
    @Obrigatorio
    private String descricaoCarreira;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private SituacaoAtoLegal situacaoCarreira;
    @Invisivel
    private String migracaoChave;
    @Etiqueta("Habilidades e Competências")
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CargoHabilidade> habilidades;
    @Etiqueta("Áreas de Formação")
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CargoAreaFormacao> areasFormacao;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Carga Horária")
    private Integer cargaHoraria;
    @Etiqueta("Contagem especial")
    @Enumerated(EnumType.STRING)
    private TipoContagemEspecial tipoContagemSIPREV;
    @Etiqueta("Contagem de Tempo especial E-Social")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoContagemEspecialEsocial tipoContagemEspecialEsocial;
    @Etiqueta("Cargo acumulável")
    @Enumerated(EnumType.STRING)
    private TipoCargoAcumulavel tipoCargoAcumulavelSIPREV;
    @Etiqueta("Tecnico Científico")
    @Tabelavel
    private Boolean tecnicoCientificoSIPREV;
    @Etiqueta("Dedicação Exclusiva")
    @Tabelavel
    private Boolean dedicacaoExclusivaSIPREV;
    @Etiqueta("Aposentadoria Especial")
    @Tabelavel
    private Boolean aposentadoriaEspecialSIPREV;
    @Etiqueta("Magistério exclusivo em sala de aula")
    @Enumerated(EnumType.STRING)
    private TipoMagisterio tipoMagisterio;
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CargoEventoFP> cargoEventoFP;
    @ManyToOne(cascade = CascadeType.ALL)
    private PeriodoAquisitivoExcluido periodoAquisitivoExcluido;
    @Etiqueta("Tipo de Cargo")
    @Enumerated(EnumType.STRING)
    private TipoDeCargo tipoDeCargo;
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCargoSindicato> itensCargoSindicato;

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel
    @Etiqueta("Empregadores")
    private List<CargoEmpregadorESocial> empregadores;
    @Invisivel
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoCargoEventoFP> itemConfiguracaoCargoEventoFP;

    @Enumerated(EnumType.STRING)
    private TipoEspecificacaoCargo tipoEspecificacaoCargo;

    @ManyToOne
    private ReferenciaFP referenciaFP;
    @ManyToOne
    private ClassificacaoCargo classificacaoCargo;

    @Etiqueta("Cargo acumulável")
    @Enumerated(EnumType.STRING)
    private TipoAcumulavelSIG tipoAcumulavelSIG;

    @Invisivel
    @Transient
    private Integer vacancia;
    @Invisivel
    @Transient
    private Integer vagasCadastradas;
    @Invisivel
    @Transient
    private Integer vagasOcupadas;
    @Invisivel
    @Transient
    private boolean selecionado;
    @Transient
    private List<EventoESocialDTO> eventosEsocial;
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnidadeCargo> unidadesCargo;

    public Cargo() {
        this.criadoEm = System.nanoTime();
        vagas = new ArrayList<>();
        habilidades = new ArrayList<>();
        areasFormacao = new ArrayList<>();
        cargoEventoFP = new ArrayList<>();
        itensCargoSindicato = new ArrayList<>();
        ativo = true;
        tecnicoCientificoSIPREV = false;
        aposentadoriaEspecialSIPREV = false;
        dedicacaoExclusivaSIPREV = false;
        empregadores = new ArrayList<>();
        itemConfiguracaoCargoEventoFP = Lists.newArrayList();
        eventosEsocial = Lists.newArrayList();
        this.unidadesCargo = Lists.newArrayList();
    }

    public Cargo(String descricao) {
        this.descricao = descricao;
    }

    public List<EventoESocialDTO> getEventosEsocial() {
        return eventosEsocial;
    }

    public void setEventosEsocial(List<EventoESocialDTO> eventosEsocial) {
        this.eventosEsocial = eventosEsocial;
    }

    public Integer getVacancia() {
        if (vacancia != null) {
            return vacancia;
        }

        try {
            vacancia = vagasCadastradas - vagasOcupadas;
        } catch (Exception e) {
            vacancia = 0;
        }
        return vacancia;
    }

    public void setVacancia(Integer vacancia) {
        this.vacancia = vacancia;
    }

    public PeriodoAquisitivoExcluido getPeriodoAquisitivoExcluido() {
        return periodoAquisitivoExcluido;
    }

    public void setPeriodoAquisitivoExcluido(PeriodoAquisitivoExcluido periodoAquisitivoExcluido) {
        this.periodoAquisitivoExcluido = periodoAquisitivoExcluido;
    }

    public TipoMagisterio getTipoMagisterio() {
        return tipoMagisterio;
    }

    public void setTipoMagisterio(TipoMagisterio tipoMagisterio) {
        this.tipoMagisterio = tipoMagisterio;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getPermiteAcumulo() {
        return permiteAcumulo;
    }

    public void setPermiteAcumulo(Boolean permiteAcumulo) {
        this.permiteAcumulo = permiteAcumulo;
    }

    public List<Vaga> getVagas() {
        return vagas;
    }

    public void setVagas(List<Vaga> vagas) {
        this.vagas = vagas;
    }

    public List<CBO> getCbos() {
        return cbos;
    }

    public void setCbos(List<CBO> cbos) {
        this.cbos = cbos;
    }

    public List<CargoEventoFP> getCargoEventoFP() {
        return cargoEventoFP;
    }

    public void setCargoEventoFP(List<CargoEventoFP> cargoEventoFP) {
        this.cargoEventoFP = cargoEventoFP;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public NivelEscolaridade getNivelEscolaridade() {
        return nivelEscolaridade;
    }

    public void setNivelEscolaridade(NivelEscolaridade nivelEscolaridade) {
        this.nivelEscolaridade = nivelEscolaridade;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<BaseCargo> getBaseCargos() {
        return baseCargos;
    }

    public void setBaseCargos(List<BaseCargo> baseCargos) {
        this.baseCargos = baseCargos;
    }

    public String getCodigoDoCargo() {
        return codigoDoCargo;
    }

    public void setCodigoDoCargo(String codigoDoCargo) {
        this.codigoDoCargo = codigoDoCargo;
    }

    public TipoPCS getTipoPCS() {
        return tipoPCS;
    }

    public void setTipoPCS(TipoPCS tipoPCS) {
        this.tipoPCS = tipoPCS;
    }

    public Integer getTempoEstagioProbatorio() {
        return tempoEstagioProbatorio;
    }

    public void setTempoEstagioProbatorio(Integer tempoEstagioProbatorio) {
        this.tempoEstagioProbatorio = tempoEstagioProbatorio;
    }

    public String getCodigoCarreira() {
        return codigoCarreira;
    }

    public void setCodigoCarreira(String codigoCarreira) {
        this.codigoCarreira = codigoCarreira;
    }

    public List<CargoHabilidade> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<CargoHabilidade> habilidades) {
        this.habilidades = habilidades;
    }

    public List<CargoAreaFormacao> getAreasFormacao() {
        return areasFormacao;
    }

    public void setAreasFormacao(List<CargoAreaFormacao> cursos) {
        this.areasFormacao = cursos;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public TipoContagemEspecial getTipoContagemSIPREV() {
        return tipoContagemSIPREV;
    }

    public void setTipoContagemSIPREV(TipoContagemEspecial tipoContagemSIPREV) {
        this.tipoContagemSIPREV = tipoContagemSIPREV;
    }

    public TipoCargoAcumulavel getTipoCargoAcumulavelSIPREV() {
        return tipoCargoAcumulavelSIPREV;
    }

    public void setTipoCargoAcumulavelSIPREV(TipoCargoAcumulavel tipoCargoAcumulavelSIPREV) {
        this.tipoCargoAcumulavelSIPREV = tipoCargoAcumulavelSIPREV;
    }

    public Boolean getTecnicoCientificoSIPREV() {
        return tecnicoCientificoSIPREV;
    }

    public void setTecnicoCientificoSIPREV(Boolean tecnicoCientificoSIPREV) {
        this.tecnicoCientificoSIPREV = tecnicoCientificoSIPREV;
    }

    public Boolean getDedicacaoExclusivaSIPREV() {
        return dedicacaoExclusivaSIPREV;
    }

    public void setDedicacaoExclusivaSIPREV(Boolean dedicacaoExclusivaSIPREV) {
        this.dedicacaoExclusivaSIPREV = dedicacaoExclusivaSIPREV;
    }

    public Boolean getAposentadoriaEspecialSIPREV() {
        return aposentadoriaEspecialSIPREV;
    }

    public void setAposentadoriaEspecialSIPREV(Boolean aposentadoriaEspecialSIPREV) {
        this.aposentadoriaEspecialSIPREV = aposentadoriaEspecialSIPREV;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public TipoDeCargo getTipoDeCargo() {
        return tipoDeCargo;
    }

    public void setTipoDeCargo(TipoDeCargo tipoDeCargo) {
        this.tipoDeCargo = tipoDeCargo;
    }

    public String getDescricaoCarreira() {
        return descricaoCarreira;
    }

    public void setDescricaoCarreira(String descricaoCarreira) {
        this.descricaoCarreira = descricaoCarreira;
    }

    public List<ItemCargoSindicato> getItensCargoSindicato() {
        return itensCargoSindicato;
    }

    public void setItensCargoSindicato(List<ItemCargoSindicato> itensCargoSindicato) {
        this.itensCargoSindicato = itensCargoSindicato;
    }

    public List<ConfiguracaoCargoEventoFP> getItemConfiguracaoCargoEventoFP() {
        return itemConfiguracaoCargoEventoFP;
    }

    public void setItemConfiguracaoCargoEventoFP(List<ConfiguracaoCargoEventoFP> itemConfiguracaoCargoEventoFP) {
        this.itemConfiguracaoCargoEventoFP = itemConfiguracaoCargoEventoFP;
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
        return codigoDoCargo + " - " + descricao;
    }

    public String periodoNoCargo(ContratoFP contratoFP) {
        String anoDia = "0";
        DateTime inicio = new DateTime(contratoFP.getInicioVigencia());
        DateTime fim = new DateTime(contratoFP.getFinalVigencia() != null ? contratoFP.getFinalVigencia() : new Date());
        anoDia = getAnosAndDiasFormatadosPorPeriodo(inicio, fim, 0);

        return anoDia;
    }

    public String getCargaHorariaMensagem() {
        return cargaHoraria != null ? "" + cargaHoraria : "Não informada";
    }

    public String getNivelEscolaridadeMensagem() {
        return nivelEscolaridade != null ? "" + nivelEscolaridade : "Não informada";
    }

    public void calcularVagasCadastradas() {
        vagasCadastradas = 0;
        for (Vaga vaga : vagas) {
            vagasCadastradas += vaga.getNumeroVagas();
        }
    }

    public Integer getVagasCadastradas() {
        return vagasCadastradas;
    }

    public void setVagasCadastradas(Integer vagasCadastradas) {
        this.vagasCadastradas = vagasCadastradas;
    }

    public Integer getVagasOcupadas() {
        return vagasOcupadas;
    }

    public void setVagasOcupadas(Integer vagasOcupadas) {
        this.vagasOcupadas = vagasOcupadas;
    }

    public boolean temNivelEscolaridade() {
        return nivelEscolaridade != null;
    }

    public TipoContagemEspecialEsocial getTipoContagemEspecialEsocial() {
        return tipoContagemEspecialEsocial;
    }

    public void setTipoContagemEspecialEsocial(TipoContagemEspecialEsocial tipoContagemEspecialEsocial) {
        this.tipoContagemEspecialEsocial = tipoContagemEspecialEsocial;
    }

    public List<CargoEmpregadorESocial> getEmpregadores() {
        return empregadores;
    }

    public void setEmpregadores(List<CargoEmpregadorESocial> empregadores) {
        this.empregadores = empregadores;
    }

    public SituacaoAtoLegal getSituacaoCarreira() {
        return situacaoCarreira;
    }

    public void setSituacaoCarreira(SituacaoAtoLegal situacaoCarreira) {
        this.situacaoCarreira = situacaoCarreira;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public TipoEspecificacaoCargo getTipoEspecificacaoCargo() {
        return tipoEspecificacaoCargo;
    }

    public void setTipoEspecificacaoCargo(TipoEspecificacaoCargo tipoEspecificacaoCargo) {
        this.tipoEspecificacaoCargo = tipoEspecificacaoCargo;
    }

    public ReferenciaFP getReferenciaFP() {
        return referenciaFP;
    }

    public void setReferenciaFP(ReferenciaFP referenciaFP) {
        this.referenciaFP = referenciaFP;
    }

    public ClassificacaoCargo getClassificacaoCargo() {
        return classificacaoCargo;
    }

    public void setClassificacaoCargo(ClassificacaoCargo classificacaoCargo) {
        this.classificacaoCargo = classificacaoCargo;
    }

    public TipoAcumulavelSIG getTipoAcumulavelSIG() {
        return tipoAcumulavelSIG;
    }

    public void setTipoAcumulavelSIG(TipoAcumulavelSIG tipoAcumulavelSIG) {
        this.tipoAcumulavelSIG = tipoAcumulavelSIG;
    }

    public List<UnidadeCargo> getUnidadesCargo() {
        return unidadesCargo;
    }

    public void setUnidadesCargo(List<UnidadeCargo> unidadesCargo) {
        this.unidadesCargo = unidadesCargo;
    }
}
