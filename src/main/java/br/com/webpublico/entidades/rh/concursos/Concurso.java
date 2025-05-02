package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.CriterioDesempate;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by venom on 21/10/14.
 */
@Entity
@Audited
@Etiqueta(value = "Concurso Público")
@GrupoDiagrama(nome = "Concursos")
public class Concurso extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta(value = "Código")
    @Pesquisavel
    @Tabelavel
    private Integer codigo;

    @Etiqueta(value = "Ano")
    @Pesquisavel
    @Tabelavel
    private Integer ano;

    @Etiqueta(value = "Nome")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String nome;

    @Etiqueta(value = "Descrição")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    private String descricao;

    @Etiqueta("Órgão")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta(value = "Data Homologação")
    @Temporal(TemporalType.DATE)
    @Tabelavel
    private Date homologacao;

    @Etiqueta(value = "Abertura")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Etiqueta(value = "Encerramento")
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Date finalVigencia;

    @Etiqueta(value = "Início da Inscrição")
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Date inicioInscricao;

    @Etiqueta(value = "Final da Inscrição")
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Date finalInscricao;

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocalConcurso> locais;

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CargoConcurso> cargos;

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InscricaoConcurso> inscricoes;

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecursoConcurso> recursos;

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResponsavelConcurso> responsaveis;

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EtapaConcurso> etapas;

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesempateConcurso> desempates;

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnexoConcurso> anexos;

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PublicacaoConcurso> publicacoes;

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FaseConcurso> fases;

    public Concurso() {
        super();
        this.responsaveis = new ArrayList<>();
        this.cargos = new ArrayList<>();
        this.etapas = new ArrayList<>();
        this.desempates = new ArrayList<>();
        this.anexos = new ArrayList<>();
        this.publicacoes = Lists.newArrayList();
    }

    public List<FaseConcurso> getFases() {
        return fases;
    }

    public void setFases(List<FaseConcurso> fases) {
        this.fases = fases;
    }

    public List<PublicacaoConcurso> getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(List<PublicacaoConcurso> publicacoes) {
        this.publicacoes = publicacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Date getHomologacao() {
        return homologacao;
    }

    public void setHomologacao(Date homologacao) {
        this.homologacao = homologacao;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioInscricao() {
        return inicioInscricao;
    }

    public void setInicioInscricao(Date inicioInscricao) {
        this.inicioInscricao = inicioInscricao;
    }

    public Date getFinalInscricao() {
        return finalInscricao;
    }

    public void setFinalInscricao(Date finalInscricao) {
        this.finalInscricao = finalInscricao;
    }

    public List<LocalConcurso> getLocais() {
        return locais;
    }

    public void setLocais(List<LocalConcurso> locais) {
        this.locais = locais;
    }

    public List<ResponsavelConcurso> getResponsaveis() {
        return responsaveis;
    }

    public void setResponsaveis(List<ResponsavelConcurso> responsaveis) {
        this.responsaveis = responsaveis;
    }

    public void adicionaResponsavel(ResponsavelConcurso rc) {
        addObject(this.responsaveis, rc);
    }

    public void removeResponsavel(ResponsavelConcurso rc) {
        delObject(this.responsaveis, rc);
    }

    public List<CargoConcurso> getCargos() {
        return cargos;
    }

    public void setCargos(List<CargoConcurso> cargos) {
        this.cargos = cargos;
    }

    public void adicionaCargoConcurso(CargoConcurso cc) {
        addObject(this.cargos, cc);
    }

    public void removeCargoConcurso(CargoConcurso cc) {
        delObject(this.cargos, cc);
    }

    public List<InscricaoConcurso> getInscricoes() {
        return inscricoes;
    }

    public void setInscricoes(List<InscricaoConcurso> inscricoes) {
        this.inscricoes = inscricoes;
    }

    public List<RecursoConcurso> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoConcurso> recursos) {
        this.recursos = recursos;
    }

    public List<EtapaConcurso> getEtapas() {
        return etapas;
    }

    public void setEtapas(List<EtapaConcurso> etapas) {
        this.etapas = etapas;
    }

    public List<DesempateConcurso> getDesempates() {
        return desempates;
    }

    public void setDesempates(List<DesempateConcurso> desempates) {
        this.desempates = desempates;
    }

    public void removeCriterioDesempate(DesempateConcurso dc) {
        delObject(this.desempates, dc);
    }

    public List<AnexoConcurso> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<AnexoConcurso> anexos) {
        this.anexos = anexos;
    }

    public void adicionaAnexo(AnexoConcurso anexo) {
        addObject(this.anexos, anexo);
    }

    public void removeAnexo(AnexoConcurso anexo) {
        delObject(this.anexos, anexo);
    }

    private void addObject(List lista, Object obj) {
        if (lista.contains(obj)) {
            lista.set(lista.indexOf(obj), obj);
        } else {
            lista.add(obj);
        }
    }

    private void delObject(List lista, Object obj) {
        if (lista.contains(obj)) {
            lista.remove(obj);
        }
    }

    public String getCodigoComZeros() {
        return StringUtil.cortaOuCompletaEsquerda("" + codigo, 3, "0");
    }

    @Override
    public String toString() {
        return getCodigoComZeros() + "/" + ano + " - " + nome;
    }

    public boolean temInscricao() {
        return this.getInscricoes() != null && !this.getInscricoes().isEmpty();
    }

    public boolean temRecurso() {
        return this.getRecursos() != null && !this.getRecursos().isEmpty();
    }

    public String toStringParaAprovacao() {
        return getCodigoComZeros() + "/" + ano;
    }

    public Integer getProximaOrdemDasFases() {
        try {
            return getFases().size() + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public List<FaseConcurso> getFasesOrdenadas() {
        if (fases == null) {
            return null;
        }
        Collections.sort(fases);
        return fases;
    }

    public List<DesempateConcurso> getDesempatesOrdenados() {
        if (desempates == null) {
            return null;
        }
        Collections.sort(desempates);
        return desempates;
    }

    public boolean jaExisteFaseComOrdem(FaseConcurso fc) {
        for (FaseConcurso fase : fases) {
            if (fc.equals(fase)) {
                continue;
            }

            if (fase.getOrdem().compareTo(fc.getOrdem()) == 0) {
                return true;
            }
        }
        return false;
    }

    public EtapaConcurso getEtapaDoTipo(TipoEtapa tipo) {
        for (EtapaConcurso etapa : this.etapas) {
            if (etapa.isTipo(tipo)) {
                return etapa;
            }
        }
        return null;
    }

    public void removeEtapa(EtapaConcurso etapa) {
        delObject(this.etapas, etapa);
    }

    public boolean existeDesempateDoTipo(CriterioDesempate criterioDesempate) {
        for (DesempateConcurso desempate : desempates) {
            if (desempate.getCriterioDesempate().equals(criterioDesempate)) {
                return true;
            }
        }
        return false;
    }

    public boolean existemDesempatesCadastradosPeloUsuario() {
        return desempates != null || !desempates.isEmpty();
    }

    public boolean temPublicacao() {
        return publicacoes != null && !publicacoes.isEmpty();
    }

    public boolean estaHomologado() {
        for (EtapaConcurso etapa : this.getEtapas()) {
            if (TipoEtapa.HOMOLOGACAO.equals(etapa.getTipoEtapa())) {
                return true;
            }
        }
        return false;
    }
}
