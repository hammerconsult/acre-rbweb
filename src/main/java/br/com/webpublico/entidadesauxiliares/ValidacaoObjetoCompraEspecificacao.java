package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.TipoBeneficio;

public class ValidacaoObjetoCompraEspecificacao {

    private ObjetoCompra objetoCompraSelecionado;
    private ObjetoCompra objetoCompraEmLista;
    private String espeficicacaoSelecionada;
    private String espeficicacaoEmLista;
    private Integer numeroLote;
    private Integer numeroItem;
    private TipoBeneficio tipoBeneficioSelecionado;
    private TipoBeneficio tipoBeneficioEmLista;

    public ValidacaoObjetoCompraEspecificacao() {
    }

    public ObjetoCompra getObjetoCompraSelecionado() {
        return objetoCompraSelecionado;
    }

    public void setObjetoCompraSelecionado(ObjetoCompra objetoCompraSelecionado) {
        this.objetoCompraSelecionado = objetoCompraSelecionado;
    }

    public ObjetoCompra getObjetoCompraEmLista() {
        return objetoCompraEmLista;
    }

    public void setObjetoCompraEmLista(ObjetoCompra objetoCompraEmLista) {
        this.objetoCompraEmLista = objetoCompraEmLista;
    }

    public String getEspeficicacaoSelecionada() {
        return espeficicacaoSelecionada;
    }

    public void setEspeficicacaoSelecionada(String espeficicacaoSelecionada) {
        this.espeficicacaoSelecionada = espeficicacaoSelecionada;
    }

    public String getEspeficicacaoEmLista() {
        return espeficicacaoEmLista;
    }

    public void setEspeficicacaoEmLista(String espeficicacaoEmLista) {
        this.espeficicacaoEmLista = espeficicacaoEmLista;
    }

    public Integer getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(Integer numeroLote) {
        this.numeroLote = numeroLote;
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    public TipoBeneficio getTipoBeneficioSelecionado() {
        return tipoBeneficioSelecionado;
    }

    public void setTipoBeneficioSelecionado(TipoBeneficio tipoBeneficioSelecionado) {
        this.tipoBeneficioSelecionado = tipoBeneficioSelecionado;
    }

    public TipoBeneficio getTipoBeneficioEmLista() {
        return tipoBeneficioEmLista;
    }

    public void setTipoBeneficioEmLista(TipoBeneficio tipoBeneficioEmLista) {
        this.tipoBeneficioEmLista = tipoBeneficioEmLista;
    }
}
