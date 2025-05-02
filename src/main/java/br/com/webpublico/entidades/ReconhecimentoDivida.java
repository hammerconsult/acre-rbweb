package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoReconhecimentoDivida;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Entity
@GrupoDiagrama(nome = "Contabil")
@Audited
@Etiqueta("Reconhecimento de Dívida de Exercícios Anteriores")
public class ReconhecimentoDivida extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String numero;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private SituacaoReconhecimentoDivida situacaoReconhecimentoDivida;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Reconhecimento")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Date dataReconhecimento;
    @ManyToOne
    private UnidadeOrganizacional unidadeAdministrativa;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrcamentaria;
    @ManyToOne
    @Etiqueta("Responsável/Ordenador da Despesa")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private PessoaFisica responsavel;
    @ManyToOne
    @Etiqueta("Fornecedor")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Pessoa fornecedor;
    @Length(maximo = 3000)
    @Etiqueta("Objeto")
    @Obrigatorio
    private String objeto;
    @Length(maximo = 3000)
    @Etiqueta("Justificativa")
    @Obrigatorio
    private String justificativa;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @OneToMany(mappedBy = "reconhecimentoDivida", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DoctoHabilitacaoReconhecimentoDivida> documentosHabilitacao;

    @OneToMany(mappedBy = "reconhecimentoDivida", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ItemReconhecimentoDivida> itens;

    @OneToMany(mappedBy = "reconhecimentoDivida", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PublicacaoReconhecimentoDivida> publicacoes;

    @OneToMany(mappedBy = "reconhecimentoDivida", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ParecerReconhecimentoDivida> pareceres;

    @OneToMany(mappedBy = "reconhecimentoDivida", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<HistoricoReconhecimentoDivida> historicos;

    public ReconhecimentoDivida() {
        super();
        documentosHabilitacao = Lists.newArrayList();
        itens = Lists.newArrayList();
        publicacoes = Lists.newArrayList();
        pareceres = Lists.newArrayList();
        historicos = Lists.newArrayList();
        situacaoReconhecimentoDivida = SituacaoReconhecimentoDivida.EM_ELABORACAO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public SituacaoReconhecimentoDivida getSituacaoReconhecimentoDivida() {
        return situacaoReconhecimentoDivida;
    }

    public void setSituacaoReconhecimentoDivida(SituacaoReconhecimentoDivida situacaoReconhecimentoDivida) {
        this.situacaoReconhecimentoDivida = situacaoReconhecimentoDivida;
    }

    public Date getDataReconhecimento() {
        return dataReconhecimento;
    }

    public void setDataReconhecimento(Date dataReconhecimento) {
        this.dataReconhecimento = dataReconhecimento;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public List<DoctoHabilitacaoReconhecimentoDivida> getDocumentosHabilitacao() {
        return documentosHabilitacao;
    }

    public void setDocumentosHabilitacao(List<DoctoHabilitacaoReconhecimentoDivida> documentosHabilitacao) {
        this.documentosHabilitacao = documentosHabilitacao;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public List<ItemReconhecimentoDivida> getItens() {
        return itens;
    }

    public void setItens(List<ItemReconhecimentoDivida> itens) {
        this.itens = itens;
    }

    public List<PublicacaoReconhecimentoDivida> getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(List<PublicacaoReconhecimentoDivida> publicacoes) {
        this.publicacoes = publicacoes;
    }

    public List<ParecerReconhecimentoDivida> getPareceres() {
        return pareceres;
    }

    public void setPareceres(List<ParecerReconhecimentoDivida> pareceres) {
        this.pareceres = pareceres;
    }

    public List<HistoricoReconhecimentoDivida> getHistoricos() {
        if (historicos != null && !historicos.isEmpty()) {
            Collections.sort(historicos, new Comparator<HistoricoReconhecimentoDivida>() {
                @Override
                public int compare(HistoricoReconhecimentoDivida o1, HistoricoReconhecimentoDivida o2) {
                    return o1.getDataHistorico().compareTo(o2.getDataHistorico());
                }
            });
        }
        return historicos;
    }

    public void setHistoricos(List<HistoricoReconhecimentoDivida> historicos) {
        this.historicos = historicos;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    @Override
    public String toString() {
        return "Nº " + numero + " Data: " + DataUtil.getDataFormatada(dataReconhecimento) + " - " + fornecedor;
    }
}
