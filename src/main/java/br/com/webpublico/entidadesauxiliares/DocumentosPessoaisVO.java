package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by carlos on 03/04/17.
 */
public class DocumentosPessoaisVO {
    private List<CarteiraTrabalhoVO> carteirasDeTrabalho;
    private List<CarteiraVacinacaoVO> carteirasDeVacinacao;
    private List<CertidaoCasamentoVO> certidoesDeCasamento;
    private List<CertidaoNascimentoVO> certidoesDeNascimento;
    private List<HabilitacaoVO> habilitacoes;
    private List<RgVO> rgVOs;
    private List<SituacaoMilitarVO> situacoesMilitares;
    private List<TituloEleitorVO> titulosEleitoral;


    public DocumentosPessoaisVO() {
        carteirasDeTrabalho = Lists.newArrayList();
        carteirasDeVacinacao = Lists.newArrayList();
        certidoesDeCasamento = Lists.newArrayList();
        certidoesDeNascimento = Lists.newArrayList();
        habilitacoes = Lists.newArrayList();
        rgVOs = Lists.newArrayList();
        situacoesMilitares = Lists.newArrayList();
        titulosEleitoral = Lists.newArrayList();
    }

    public List<CarteiraTrabalhoVO> getCarteirasDeTrabalho() {
        return carteirasDeTrabalho;
    }

    public void setCarteirasDeTrabalho(List<CarteiraTrabalhoVO> carteirasDeTrabalho) {
        this.carteirasDeTrabalho = carteirasDeTrabalho;
    }

    public List<CarteiraVacinacaoVO> getCarteirasDeVacinacao() {
        return carteirasDeVacinacao;
    }

    public void setCarteirasDeVacinacao(List<CarteiraVacinacaoVO> carteirasDeVacinacao) {
        this.carteirasDeVacinacao = carteirasDeVacinacao;
    }

    public List<CertidaoCasamentoVO> getCertidoesDeCasamento() {
        return certidoesDeCasamento;
    }

    public void setCertidoesDeCasamento(List<CertidaoCasamentoVO> certidoesDeCasamento) {
        this.certidoesDeCasamento = certidoesDeCasamento;
    }

    public List<CertidaoNascimentoVO> getCertidoesDeNascimento() {
        return certidoesDeNascimento;
    }

    public void setCertidoesDeNascimento(List<CertidaoNascimentoVO> certidoesDeNascimento) {
        this.certidoesDeNascimento = certidoesDeNascimento;
    }

    public List<HabilitacaoVO> getHabilitacoes() {
        return habilitacoes;
    }

    public void setHabilitacoes(List<HabilitacaoVO> habilitacoes) {
        this.habilitacoes = habilitacoes;
    }

    public List<RgVO> getRgVOs() {
        return rgVOs;
    }

    public void setRgVOs(List<RgVO> rgVOs) {
        this.rgVOs = rgVOs;
    }

    public List<SituacaoMilitarVO> getSituacoesMilitares() {
        return situacoesMilitares;
    }

    public void setSituacoesMilitares(List<SituacaoMilitarVO> situacoesMilitares) {
        this.situacoesMilitares = situacoesMilitares;
    }

    public List<TituloEleitorVO> getTitulosEleitoral() {
        return titulosEleitoral;
    }

    public void setTitulosEleitoral(List<TituloEleitorVO> titulosEleitoral) {
        this.titulosEleitoral = titulosEleitoral;
    }

    public boolean hasRegistros() {
        return !titulosEleitoral.isEmpty() ||
            !carteirasDeTrabalho.isEmpty() ||
            !carteirasDeVacinacao.isEmpty() ||
            !certidoesDeCasamento.isEmpty() ||
            !certidoesDeNascimento.isEmpty() ||
            !habilitacoes.isEmpty() ||
            !rgVOs.isEmpty() ||
            !situacoesMilitares.isEmpty();
    }
}
