package br.com.webpublico.entidadesauxiliares.contabil;


import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.SubSistema;

public class GeradorContaAuxiliarDTO {


    private Long idUnidadeOrganizacional;
    private SubSistema financeiroPermanente;
    private Integer dividaConsolidada;
    private Long idContaDeDestinacao;
    private Long idExercicioAtual;
    private String naturezaReceita;
    private Long idConta;
    private String classificacaoFuncional;
    private String naturezaDespesa;
    private Integer es;
    private Long idContaInscricao;
    private Integer anoInscricaoResto;
    private Long idExercicioOrigem;
    //objetos
    private UnidadeOrganizacional unidadeOrganizacional;
    private Conta contaDeDestinacao;
    private Conta conta;
    private Conta contaInscricao;
    private Exercicio exercicioAtual;
    private Exercicio exercicioOriginal;

    public GeradorContaAuxiliarDTO() {
    }

    public GeradorContaAuxiliarDTO(UnidadeOrganizacional unidadeOrganizacional, ContaDeDestinacao contaDeDestinacao) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.idUnidadeOrganizacional = unidadeOrganizacional != null ? unidadeOrganizacional.getId() : null;
        this.idContaDeDestinacao = contaDeDestinacao != null ? contaDeDestinacao.getId() : null;
    }

    public GeradorContaAuxiliarDTO(UnidadeOrganizacional unidadeOrganizacional, Conta contaDeDestinacao, Conta conta, String naturezaReceita, Exercicio exercicioAtual) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.conta = conta;
        this.naturezaReceita = naturezaReceita;
        this.exercicioAtual = exercicioAtual;
        this.idUnidadeOrganizacional = unidadeOrganizacional != null ? unidadeOrganizacional.getId() : null;
        this.idContaDeDestinacao = contaDeDestinacao != null ? contaDeDestinacao.getId() : null;
        this.idExercicioAtual = exercicioAtual != null ? exercicioAtual.getId() : null;
        this.naturezaReceita = naturezaReceita;
        this.idConta = conta != null ? conta.getId() : null;
    }

    public GeradorContaAuxiliarDTO(UnidadeOrganizacional unidadeOrganizacional, ContaDeDestinacao contaDeDestinacao, Exercicio exercicioAtual) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.exercicioAtual = exercicioAtual;
        this.idUnidadeOrganizacional = unidadeOrganizacional != null ? unidadeOrganizacional.getId() : null;
        this.idContaDeDestinacao = contaDeDestinacao != null ? contaDeDestinacao.getId() : null;
        this.idExercicioAtual = exercicioAtual != null ? exercicioAtual.getId() : null;
    }

    public GeradorContaAuxiliarDTO(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.idUnidadeOrganizacional = unidadeOrganizacional != null ? unidadeOrganizacional.getId() : null;
    }

    public GeradorContaAuxiliarDTO(UnidadeOrganizacional unidadeOrganizacional,
                                   String classificacaoFuncional,
                                   Conta contaDeDestinacao,
                                   Conta contaDespesa,
                                   Integer es,
                                   Integer anoInscricaoResto,
                                   Conta contaInscricao,
                                   Exercicio exercicioAtual,
                                   Exercicio exercicioOriginal,
                                   SubSistema financeiroPermanente,
                                   Integer dividaConsolidada,
                                   String naturezaReceita,
                                   String naturezaDespesa
    ) {

        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaDeDestinacao = contaDeDestinacao;
        this.conta = contaDespesa;
        this.contaInscricao = contaInscricao;
        this.naturezaReceita = naturezaReceita;
        this.exercicioAtual = exercicioAtual;
        this.exercicioOriginal = exercicioOriginal;

        this.idUnidadeOrganizacional = unidadeOrganizacional != null ? unidadeOrganizacional.getId() : null;
        this.financeiroPermanente = financeiroPermanente;
        this.dividaConsolidada = dividaConsolidada;
        this.idContaDeDestinacao = contaDeDestinacao != null ? contaDeDestinacao.getId() : null;
        this.idConta = contaDespesa != null ? contaDespesa.getId() : null;
        this.idExercicioAtual = exercicioAtual != null ? exercicioAtual.getId() : null;
        this.naturezaReceita = naturezaReceita;
        this.classificacaoFuncional = classificacaoFuncional;
        this.naturezaDespesa = naturezaDespesa;
        this.es = es;
        this.idContaInscricao = contaInscricao != null ? contaInscricao.getId() : null;
        this.anoInscricaoResto = anoInscricaoResto;
        this.idExercicioOrigem = exercicioOriginal != null ? exercicioOriginal.getId() : null;
    }


    public Long getIdUnidadeOrganizacional() {
        return idUnidadeOrganizacional;
    }

    public void setIdUnidadeOrganizacional(Long idUnidadeOrganizacional) {
        this.idUnidadeOrganizacional = idUnidadeOrganizacional;
    }

    public SubSistema getFinanceiroPermanente() {
        return financeiroPermanente;
    }

    public void setFinanceiroPermanente(SubSistema financeiroPermanente) {
        this.financeiroPermanente = financeiroPermanente;
    }

    public Integer getDividaConsolidada() {
        return dividaConsolidada;
    }

    public void setDividaConsolidada(Integer dividaConsolidada) {
        this.dividaConsolidada = dividaConsolidada;
    }

    public Long getIdContaDeDestinacao() {
        return idContaDeDestinacao;
    }

    public void setIdContaDeDestinacao(Long idContaDeDestinacao) {
        this.idContaDeDestinacao = idContaDeDestinacao;
    }

    public Long getIdExercicioAtual() {
        return idExercicioAtual;
    }

    public void setIdExercicioAtual(Long idExercicioAtual) {
        this.idExercicioAtual = idExercicioAtual;
    }

    public String getNaturezaReceita() {
        return naturezaReceita;
    }

    public void setNaturezaReceita(String naturezaReceita) {
        this.naturezaReceita = naturezaReceita;
    }

    public Long getIdConta() {
        return idConta;
    }

    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }

    public String getClassificacaoFuncional() {
        return classificacaoFuncional;
    }

    public void setClassificacaoFuncional(String classificacaoFuncional) {
        this.classificacaoFuncional = classificacaoFuncional;
    }

    public String getNaturezaDespesa() {
        return naturezaDespesa;
    }

    public void setNaturezaDespesa(String naturezaDespesa) {
        this.naturezaDespesa = naturezaDespesa;
    }

    public Integer getEs() {
        return es;
    }

    public void setEs(Integer es) {
        this.es = es;
    }

    public Long getIdContaInscricao() {
        return idContaInscricao;
    }

    public void setIdContaInscricao(Long idContaInscricao) {
        this.idContaInscricao = idContaInscricao;
    }

    public Integer getAnoInscricaoResto() {
        return anoInscricaoResto;
    }

    public void setAnoInscricaoResto(Integer anoInscricaoResto) {
        this.anoInscricaoResto = anoInscricaoResto;
    }

    public Long getIdExercicioOrigem() {
        return idExercicioOrigem;
    }

    public void setIdExercicioOrigem(Long idExercicioOrigem) {
        this.idExercicioOrigem = idExercicioOrigem;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Conta getContaInscricao() {
        return contaInscricao;
    }

    public void setContaInscricao(Conta contaInscricao) {
        this.contaInscricao = contaInscricao;
    }

    public Exercicio getExercicioAtual() {
        return exercicioAtual;
    }

    public void setExercicioAtual(Exercicio exercicioAtual) {
        this.exercicioAtual = exercicioAtual;
    }

    public Exercicio getExercicioOriginal() {
        return exercicioOriginal;
    }

    public void setExercicioOriginal(Exercicio exercicioOriginal) {
        this.exercicioOriginal = exercicioOriginal;
    }
}
