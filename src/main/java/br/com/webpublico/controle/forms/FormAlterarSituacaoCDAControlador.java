package br.com.webpublico.controle.forms;


import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoAlteracaoCertidaoDA;
import br.com.webpublico.enums.TipoCadastroTributario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FormAlterarSituacaoCDAControlador implements Serializable {

    private TipoAlteracaoCertidaoDA tipoAlteracaoCertidaoDA;
    private TipoCadastroTributario tipoCadastro;
    private Divida divida;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private String cadastroInicial;
    private String cadastroFinal;
    private Pessoa pessoa;
    private List<CertidaoDividaAtiva> certidoesDividaAtiva;
    private CertidaoDividaAtiva[] certidoesSelecionadas;
    private List<InscricaoDividaParcela> listaParcelas;
    private String nrProtocolo;
    private Integer anoProtocolo;
    private String motivo;
    private Long numeroCdaInicial;
    private Long numeroCdaFinal;

    public FormAlterarSituacaoCDAControlador() {
        this.limparDados();
    }

    public void limparDados() {
        this.tipoAlteracaoCertidaoDA = null;
        this.tipoCadastro = null;
        this.certidoesDividaAtiva = new ArrayList<>();
        this.certidoesSelecionadas = null;
        this.listaParcelas = new ArrayList<>();
        this.exercicioInicial = null;
        this.exercicioFinal = null;
        this.nrProtocolo = "";
        this.motivo = "";
        this.numeroCdaInicial = null;
        this.numeroCdaFinal = null;
        this.limparFiltro();
    }

    public void limparFiltro() {
        this.divida = null;
        this.cadastroInicial = "1";
        this.cadastroFinal = "999999999999999999";
        this.pessoa = null;

    }

    public TipoAlteracaoCertidaoDA getTipoAlteracaoCertidaoDA() {
        return tipoAlteracaoCertidaoDA;
    }

    public void setTipoAlteracaoCertidaoDA(TipoAlteracaoCertidaoDA tipoAlteracaoCertidaoDA) {
        this.tipoAlteracaoCertidaoDA = tipoAlteracaoCertidaoDA;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<CertidaoDividaAtiva> getCertidoesDividaAtiva() {
        return certidoesDividaAtiva;
    }

    public void setCertidoesDividaAtiva(List<CertidaoDividaAtiva> certidoesDividaAtiva) {
        this.certidoesDividaAtiva = certidoesDividaAtiva;
    }

    public CertidaoDividaAtiva[] getCertidoesSelecionadas() {
        return certidoesSelecionadas;
    }

    public void setCertidoesSelecionadas(CertidaoDividaAtiva[] certidoesSelecionadas) {
        this.certidoesSelecionadas = certidoesSelecionadas;
    }

    public List<InscricaoDividaParcela> getListaParcelas() {
        return listaParcelas;
    }

    public void setListaParcelas(List<InscricaoDividaParcela> listaParcelas) {
        this.listaParcelas = listaParcelas;
    }

    public String getNrProtocolo() {
        return nrProtocolo;
    }

    public Integer getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(Integer anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public void setNrProtocolo(String nrProtocolo) {
        this.nrProtocolo = nrProtocolo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getNumeroCdaInicial() {
        return numeroCdaInicial;
    }

    public void setNumeroCdaInicial(Long numeroCdaInicial) {
        this.numeroCdaInicial = numeroCdaInicial;
    }

    public Long getNumeroCdaFinal() {
        return numeroCdaFinal;
    }

    public void setNumeroCdaFinal(Long numeroCdaFinal) {
        this.numeroCdaFinal = numeroCdaFinal;
    }
}
