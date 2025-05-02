package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoCardapio;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Cardápio")
public class Cardapio extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoCardapio situacaoCardapio;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Cadastro")
    private Date dataCadastro;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Programa de Alimentação")
    private ProgramaAlimentacao programaAlimentacao;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Obrigatorio
    @OneToOne
    @Etiqueta("Nutricionista Responsável")
    private PessoaFisica nutricionista;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Inicial")
    private Date dataInicial;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Final")
    private Date dataFinal;

    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @Etiqueta("Observação")
    private String observacao;

    @OneToMany(mappedBy = "cardapio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Agenda")
    private List<CardapioAgenda> diasSemana;

    @Etiqueta("Requisições de Compra")
    @OneToMany(mappedBy = "cardapio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardapioRequisicaoCompra> requisicoesCompra;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public SituacaoCardapio getSituacaoCardapio() {
        return situacaoCardapio;
    }

    public void setSituacaoCardapio(SituacaoCardapio situacaoCardapio) {
        this.situacaoCardapio = situacaoCardapio;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public ProgramaAlimentacao getProgramaAlimentacao() {
        return programaAlimentacao;
    }

    public void setProgramaAlimentacao(ProgramaAlimentacao programaAlimentacao) {
        this.programaAlimentacao = programaAlimentacao;
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

    public PessoaFisica getNutricionista() {
        return nutricionista;
    }

    public void setNutricionista(PessoaFisica nutricionista) {
        this.nutricionista = nutricionista;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<CardapioAgenda> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(List<CardapioAgenda> diasSemana) {
        this.diasSemana = diasSemana;
    }

    public List<CardapioRequisicaoCompra> getRequisicoesCompra() {
        return requisicoesCompra;
    }

    public void setRequisicoesCompra(List<CardapioRequisicaoCompra> requisicoesCompra) {
        this.requisicoesCompra = requisicoesCompra;
    }

    @Override
    public String toString() {
        return numero + " - " + programaAlimentacao.getNome() + " (" + DataUtil.getDataFormatada(dataInicial) + " à " + DataUtil.getDataFormatada(dataFinal) + ")";
    }
}
