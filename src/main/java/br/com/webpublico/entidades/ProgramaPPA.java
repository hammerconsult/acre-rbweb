/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.usertype.ParticipanteProgramaPPA;
import br.com.webpublico.enums.BaseGeografica;
import br.com.webpublico.enums.HorizonteTemporal;
import br.com.webpublico.enums.TipoProgramaPPA;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Etiqueta("Programa do PPA")
@Audited
public class ProgramaPPA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @ManyToOne
    @Etiqueta("PPA")
    @Tabelavel(campoSelecionado = false)
    private PPA ppa;
    @Pesquisavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Cadastro")
    private Date dataCadastro;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Denominação")
    @Obrigatorio
    private String denominacao;
    @Tabelavel
    @Obrigatorio
    @Column(length = 1000)
    private String objetivo;
    /**
     * Código usado para identificação do Programa PPA nos relatórios e
     * formulários internos.
     */
    @Pesquisavel
    @Etiqueta("Código")
    @Tabelavel
    @Obrigatorio
    private String codigo;
    /*
     * Unidade organizacional responsável por este programa do PPA.
     */
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade Responsável")
    private UnidadeOrganizacional responsavel;

    /*
     * Item do planejamento estratégico no qual se encaixa este programa.
     * Utilizado para representar o mapa estratégico com seus diversos níveis
     * (eixos, subdivisões, etc.)
     * Item do planejamento estratégico agora chama Objetivo do Eixo
     */
    @Pesquisavel
    @Etiqueta("Objetivo do Eixo")
    @ManyToOne
    private ItemPlanejamentoEstrategico itemPlanejamentoEstrategico;

    /*
     * Macro objetivo do planejamento estratégico que será atingido a partir da
     * execução das ações deste programa.
     * Macro Objetivo Estratégico passou a chamar Eixo Estratégico
     */
    @Etiqueta("Eixo Estratégico")
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    private MacroObjetivoEstrategico macroObjetivoEstrategico;
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Programa")
    private TipoProgramaPPA tipoPrograma;
    /**
     * Indica se este programa terá início e fim definidos. Se for Temporario,
     * tem inicio e fim
     */
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Horizonte Temporal")
    private HorizonteTemporal horizonteTemporal;
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data de Início")
    private Date inicio;
    @Pesquisavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data de Fim")
    private Date fim;
    @Pesquisavel
    private Boolean multisetorial;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Indicadores")
    @OneToMany(mappedBy = "programa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IndicadorProgramaPPA> indicadores;
    @Etiqueta("Ações")
    @Tabelavel(campoSelecionado = false)
    @OneToMany(mappedBy = "programa", cascade = CascadeType.MERGE)
    private List<AcaoPrincipal> acoes;

    @Etiqueta("Projetos/Atividades")
    @Tabelavel(campoSelecionado = false)
    @OneToMany(mappedBy = "programa", cascade = CascadeType.MERGE)
    private List<AcaoPPA> projetosAtividades;


    @Etiqueta("Público Alvo")
    @Tabelavel(campoSelecionado = false)
    @OneToMany(mappedBy = "programaPPA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PublicoAlvoProgramaPPA> publicoAlvo;
    @OneToOne
    private ProgramaPPA origem;
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;
    //    criado para migração
    @Invisivel
    @ManyToOne
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta(value = "Base Geográfica")
    private BaseGeografica baseGeografica;
    @Transient
    private Long criadoEm;

    @Etiqueta("Participantes do Programa PPA")
    @OneToMany(mappedBy = "programaPPA", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<ParticipanteProgramaPPA> participantesProgramaPPA;

    public ProgramaPPA(PPA ppa, String denominacao, String objetivo, String codigo, UnidadeOrganizacional responsavel, ItemPlanejamentoEstrategico itemPlanejamentoEstrategico, MacroObjetivoEstrategico macroObjetivoEstrategico, TipoProgramaPPA tipoPrograma, HorizonteTemporal horizonteTemporal, Date inicio, Date fim, Boolean multisetorial, List<IndicadorProgramaPPA> indicadores, List<AcaoPrincipal> acoes, List<PublicoAlvoProgramaPPA> publicoAlvo, ProgramaPPA origem, Boolean somenteLeitura, Exercicio exercicio) {
        this.ppa = ppa;
        this.denominacao = denominacao;
        this.objetivo = objetivo;
        this.codigo = codigo;
        this.responsavel = responsavel;
        this.itemPlanejamentoEstrategico = itemPlanejamentoEstrategico;
        this.macroObjetivoEstrategico = macroObjetivoEstrategico;
        this.tipoPrograma = tipoPrograma;
        this.horizonteTemporal = horizonteTemporal;
        this.inicio = inicio;
        this.fim = fim;
        this.multisetorial = multisetorial;
        this.indicadores = indicadores;
        this.acoes = acoes;
        this.publicoAlvo = publicoAlvo;
        this.origem = origem;
        this.somenteLeitura = somenteLeitura;
        this.exercicio = exercicio;
        criadoEm = System.nanoTime();
    }

    public ProgramaPPA() {
        somenteLeitura = false;
        this.indicadores = new ArrayList<IndicadorProgramaPPA>();
        this.publicoAlvo = new ArrayList<PublicoAlvoProgramaPPA>();
        this.acoes = new ArrayList<AcaoPrincipal>();
        this.projetosAtividades = new ArrayList<AcaoPPA>();
        criadoEm = System.nanoTime();
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public ProgramaPPA getOrigem() {
        return origem;
    }

    public void setOrigem(ProgramaPPA origem) {
        this.origem = origem;
    }


    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public UnidadeOrganizacional getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UnidadeOrganizacional responsavel) {
        this.responsavel = responsavel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominacao() {
        return denominacao;
    }

    public void setDenominacao(String denominacao) {
        this.denominacao = denominacao;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public HorizonteTemporal getHorizonteTemporal() {
        return horizonteTemporal;
    }

    public void setHorizonteTemporal(HorizonteTemporal horizonteTemporal) {
        this.horizonteTemporal = horizonteTemporal;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Boolean getMultisetorial() {
        return multisetorial;
    }

    public void setMultisetorial(Boolean multisetorial) {
        this.multisetorial = multisetorial;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public BaseGeografica getBaseGeografica() {
        return baseGeografica;
    }

    public void setBaseGeografica(BaseGeografica baseGeografica) {
        this.baseGeografica = baseGeografica;
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
        if (codigo != null) {
            return codigo + " - " + denominacao;
        } else {
            return denominacao;
        }

    }

    public String toStringAutoComplete() {
        String retorno = toString();
        return retorno.length() > 68 ? retorno.substring(0, 65) + "..." : retorno;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoProgramaPPA getTipoPrograma() {
        return tipoPrograma;
    }

    public void setTipoPrograma(TipoProgramaPPA tipoPrograma) {
        this.tipoPrograma = tipoPrograma;
    }

    public ItemPlanejamentoEstrategico getItemPlanejamentoEstrategico() {
        return itemPlanejamentoEstrategico;
    }

    public void setItemPlanejamentoEstrategico(ItemPlanejamentoEstrategico itemPlanejamentoEstrategico) {
        this.itemPlanejamentoEstrategico = itemPlanejamentoEstrategico;
    }

    public MacroObjetivoEstrategico getMacroObjetivoEstrategico() {
        return macroObjetivoEstrategico;
    }

    public void setMacroObjetivoEstrategico(MacroObjetivoEstrategico macroObjetivoEstrategico) {
        this.macroObjetivoEstrategico = macroObjetivoEstrategico;
    }

    public List<IndicadorProgramaPPA> getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(List<IndicadorProgramaPPA> indicadores) {
        this.indicadores = indicadores;
    }

    public List<AcaoPrincipal> getAcoes() {
        return acoes;
    }

    public void setAcoes(List<AcaoPrincipal> acoes) {
        this.acoes = acoes;
    }

    public List<AcaoPPA> getProjetosAtividades() {
        return projetosAtividades;
    }

    public void setProjetosAtividades(List<AcaoPPA> projetosAtividades) {
        this.projetosAtividades = projetosAtividades;
    }

    public List<PublicoAlvoProgramaPPA> getPublicoAlvo() {
        return publicoAlvo;
    }

    public void setPublicoAlvo(List<PublicoAlvoProgramaPPA> publicoAlvo) {
        this.publicoAlvo = publicoAlvo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void ordernarAcaoPrincipal() {
        Collections.sort(this.getAcoes(), new Comparator<AcaoPrincipal>() {
            @Override
            public int compare(AcaoPrincipal o1, AcaoPrincipal o2) {
                String i1 = o1.getCodigo();
                String i2 = o2.getCodigo();
                return i1.compareTo(i2);
            }
        });
    }

    public void ordernarProjetoAtividade() {
        Collections.sort(this.getProjetosAtividades(), new Comparator<AcaoPPA>() {
            @Override
            public int compare(AcaoPPA o1, AcaoPPA o2) {
                String i1 = o1.getCodigo();
                String i2 = o2.getCodigo();
                return i1.compareTo(i2);
            }
        });
    }

    public List<ParticipanteProgramaPPA> getParticipantesProgramaPPA() {
        return participantesProgramaPPA;
    }

    public void setParticipantesProgramaPPA(List<ParticipanteProgramaPPA> participantesProgramaPPA) {
        this.participantesProgramaPPA = participantesProgramaPPA;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
