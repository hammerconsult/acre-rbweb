package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.GrupoObjetoCompra;

public class PlanoContratacaoAnualGrupoObjetoCompraVO {

    private GrupoObjetoCompra grupoObjetoCompra;
    private boolean selecionado;

    public PlanoContratacaoAnualGrupoObjetoCompraVO(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
        this.selecionado = false;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }
}
