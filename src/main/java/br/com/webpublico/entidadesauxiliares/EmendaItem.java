package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Edi on 10/11/2015.
 */
public class EmendaItem {

    private Long idEmenda;
    private Long idBeneficiario;
    private String vereador;
    private String modalidadeEmenda;
    private String tipoRealizacao;
    private String modalidadeIntervencao;
    private String esferaGoverno;
    private String funcional;
    private String especificacaoMeta;
    private Integer quantidade;
    private String beneficiario;
    private String endereco;
    private String responsavel;
    private String justificativa;
    private String codigoConta;
    private String contaDespesa;
    private String codigoFonteRecurso;
    private String fonteRecurso;
    private String orgao;
    private String unidade;
    private String usuario;
    private String funcao;
    private String subFuncao;
    private String codigoSubAcao;
    private String descricaoSubAcao;
    private String codigoAcao;
    private String descricaoAcao;
    private BigDecimal valorAcrescimo;
    private BigDecimal valorCancelamento;
    private Date dataEmenda;
    private String exercicio;
    private List<EmendaItem> listaDeAcrescimos;
    private List<EmendaItem> listaDeCancelamentos;
    private List<EmendaItem> listaBeneficiarios;
    private List<EmendaItem> listaResponsaveis;
    private List<EmendaItem> subReport;

    public EmendaItem() {
        listaDeAcrescimos = new ArrayList<>();
        listaDeCancelamentos = new ArrayList<>();
        listaBeneficiarios = new ArrayList<>();
        listaResponsaveis = new ArrayList<>();
        subReport = new ArrayList<>();
    }

    public String getCodigoAcao() {
        return codigoAcao;
    }

    public void setCodigoAcao(String codigoAcao) {
        this.codigoAcao = codigoAcao;
    }

    public String getDescricaoAcao() {
        return descricaoAcao;
    }

    public void setDescricaoAcao(String descricaoAcao) {
        this.descricaoAcao = descricaoAcao;
    }

    public String getCodigoFonteRecurso() {
        return codigoFonteRecurso;
    }

    public void setCodigoFonteRecurso(String codigoFonteRecurso) {
        this.codigoFonteRecurso = codigoFonteRecurso;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getCodigoConta() {
        return codigoConta;
    }

    public void setCodigoConta(String codigoConta) {
        this.codigoConta = codigoConta;
    }

    public String getCodigoSubAcao() {
        return codigoSubAcao;
    }

    public void setCodigoSubAcao(String codigoSubAcao) {
        this.codigoSubAcao = codigoSubAcao;
    }

    public String getDescricaoSubAcao() {
        return descricaoSubAcao;
    }

    public void setDescricaoSubAcao(String descricaoSubAcao) {
        this.descricaoSubAcao = descricaoSubAcao;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(String subFuncao) {
        this.subFuncao = subFuncao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<EmendaItem> getListaBeneficiarios() {
        return listaBeneficiarios;
    }

    public void setListaBeneficiarios(List<EmendaItem> listaBeneficiarios) {
        this.listaBeneficiarios = listaBeneficiarios;
    }

    public List<EmendaItem> getListaResponsaveis() {
        return listaResponsaveis;
    }

    public void setListaResponsaveis(List<EmendaItem> listaResponsaveis) {
        this.listaResponsaveis = listaResponsaveis;
    }

    public Long getIdEmenda() {
        return idEmenda;
    }

    public void setIdEmenda(Long idEmenda) {
        this.idEmenda = idEmenda;
    }

    public String getVereador() {
        return vereador;
    }

    public void setVereador(String vereador) {
        this.vereador = vereador;
    }

    public String getModalidadeEmenda() {
        return modalidadeEmenda;
    }

    public void setModalidadeEmenda(String modalidadeEmenda) {
        this.modalidadeEmenda = modalidadeEmenda;
    }


    public String getModalidadeIntervencao() {
        return modalidadeIntervencao;
    }

    public void setModalidadeIntervencao(String modalidadeIntervencao) {
        this.modalidadeIntervencao = modalidadeIntervencao;
    }

    public String getEsferaGoverno() {
        return esferaGoverno;
    }

    public void setEsferaGoverno(String esferaGoverno) {
        this.esferaGoverno = esferaGoverno;
    }

    public String getFuncional() {
        return funcional;
    }

    public void setFuncional(String funcional) {
        this.funcional = funcional;
    }

    public String getEspecificacaoMeta() {
        return especificacaoMeta;
    }

    public void setEspecificacaoMeta(String especificacaoMeta) {
        this.especificacaoMeta = especificacaoMeta;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(String contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public String getFonteRecurso() {
        return fonteRecurso;
    }

    public void setFonteRecurso(String fonteRecurso) {
        this.fonteRecurso = fonteRecurso;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public BigDecimal getValorAcrescimo() {
        return valorAcrescimo;
    }

    public void setValorAcrescimo(BigDecimal valorAcrescimo) {
        this.valorAcrescimo = valorAcrescimo;
    }

    public BigDecimal getValorCancelamento() {
        return valorCancelamento;
    }

    public void setValorCancelamento(BigDecimal valorCancelamento) {
        this.valorCancelamento = valorCancelamento;
    }

    public List<EmendaItem> getListaDeCancelamentos() {
        return listaDeCancelamentos;
    }

    public void setListaDeCancelamentos(List<EmendaItem> listaDeCancelamentos) {
        this.listaDeCancelamentos = listaDeCancelamentos;
    }

    public List<EmendaItem> getListaDeAcrescimos() {
        return listaDeAcrescimos;
    }

    public void setListaDeAcrescimos(List<EmendaItem> listaDeAcrescimos) {
        this.listaDeAcrescimos = listaDeAcrescimos;
    }

    public List<EmendaItem> getSubReport() {
        return subReport;
    }

    public void setSubReport(List<EmendaItem> subReport) {
        this.subReport = subReport;
    }

    public Date getDataEmenda() {
        return dataEmenda;
    }

    public void setDataEmenda(Date dataEmenda) {
        this.dataEmenda = dataEmenda;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public String getTipoRealizacao() {
        return tipoRealizacao;
    }

    public void setTipoRealizacao(String tipoRealizacao) {
        this.tipoRealizacao = tipoRealizacao;
    }

    public Long getIdBeneficiario() {
        return idBeneficiario;
    }

    public void setIdBeneficiario(Long idBeneficiario) {
        this.idBeneficiario = idBeneficiario;
    }

}
