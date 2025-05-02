/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author leonardo
 */
@GrupoDiagrama(nome = "Alvara")
@Etiqueta("Alvará")
@Audited
@Entity
public class Alvara extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    private Exercicio exercicio;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private TipoAlvara tipoAlvara;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "alvara")
    private List<CNAEAlvara> cnaeAlvaras;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date emissao;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    private String assinaturadigital;
    @ManyToOne
    @Tabelavel
    private CadastroEconomico cadastroEconomico;
    private BigDecimal areaOcupada;
    private Boolean licencaEspecial;
    private String observacao;
    private Boolean provisorio;
    @Transient
    private Long criadoEm;
    private String migracaoChave;
    private String nomeRazaoSocial;
    @ManyToOne
    private Processo processo;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "alvara")
    private List<ReciboImpressaoAlvara> recibosImpressaoAlvara;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "alvara")
    private List<AlvaraHorarioFuncionamento> horariosDeFuncionamento;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "alvara")
    private List<EnderecoAlvara> enderecosAlvara;
    private Boolean emitirModeloNovo;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAlteracao;

    public Alvara() {
        super();
        cnaeAlvaras = Lists.newArrayList();
        recibosImpressaoAlvara = Lists.newArrayList();
        horariosDeFuncionamento = Lists.newArrayList();
        enderecosAlvara = Lists.newArrayList();
        emitirModeloNovo = true;
    }

    public Alvara(Long id, String migracaoChave) {
        this();
        this.id = id;
        this.migracaoChave = migracaoChave;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public List<CNAEAlvara> getCnaeAlvaras() {
        return cnaeAlvaras;
    }

    public void setCnaeAlvaras(List<CNAEAlvara> cnaeAlvaras) {
        this.cnaeAlvaras = cnaeAlvaras;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getAssinaturadigital() {
        return assinaturadigital;
    }

    public void setAssinaturadigital(String assinaturadigital) {
        this.assinaturadigital = assinaturadigital;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public BigDecimal getAreaOcupada() {
        return areaOcupada;
    }

    public void setAreaOcupada(BigDecimal areaOcupada) {
        this.areaOcupada = areaOcupada;
    }

    public Boolean getLicencaEspecial() {
        return licencaEspecial;
    }

    public void setLicencaEspecial(Boolean licencaEspecial) {
        this.licencaEspecial = licencaEspecial;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Boolean getProvisorio() {
        return provisorio != null ? provisorio : false;
    }

    public void setProvisorio(Boolean provisorio) {
        this.provisorio = provisorio;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public List<ReciboImpressaoAlvara> getRecibosImpressaoAlvara() {
        return recibosImpressaoAlvara;
    }

    public void setRecibosImpressaoAlvara(List<ReciboImpressaoAlvara> impressoesAlvara) {
        this.recibosImpressaoAlvara = impressoesAlvara;
    }

    public List<EnderecoAlvara> getEnderecosAlvara() {
        enderecosAlvara.sort(Comparator.comparing(EnderecoAlvara::getSequencial));
        return enderecosAlvara;
    }

    public void setEnderecosAlvara(List<EnderecoAlvara> enderecosAlvara) {
        this.enderecosAlvara = enderecosAlvara;
    }

    public List<AlvaraHorarioFuncionamento> getHorariosDeFuncionamento() {
        return horariosDeFuncionamento;
    }

    public void setHorariosDeFuncionamento(List<AlvaraHorarioFuncionamento> horariosDeFuncionamento) {
        this.horariosDeFuncionamento = horariosDeFuncionamento;
    }

    public Boolean getEmitirModeloNovo() {
        return emitirModeloNovo;
    }

    public void setEmitirModeloNovo(Boolean emitirModeloNovo) {
        this.emitirModeloNovo = emitirModeloNovo;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
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
        return exercicio.getAno() + " / " + tipoAlvara.getDescricaoSimples();
    }

    public void copiarHorariosDeFuncionamentoDoCadastroEconomico(List<PessoaHorarioFuncionamento> horarios) {
        if (horarios != null && horariosDeFuncionamento.isEmpty()) {
            for (PessoaHorarioFuncionamento horario : horarios) {
                AlvaraHorarioFuncionamento horarioFuncionamento = new AlvaraHorarioFuncionamento();
                horarioFuncionamento.setHorarioFuncionamento(horario.getHorarioFuncionamento());
                horarioFuncionamento.setAlvara(this);
                Util.adicionarObjetoEmLista(horariosDeFuncionamento, horarioFuncionamento);
            }
        }
    }

    public boolean isExercicioAtual() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == getExercicio().getAno();
    }

    public EnderecoAlvara getEnderecoPrincipal() {
        return enderecosAlvara.stream()
            .filter(ea -> ea.getTipoEndereco().equals(TipoEndereco.COMERCIAL))
            .findFirst()
            .orElse(null);
    }
}
