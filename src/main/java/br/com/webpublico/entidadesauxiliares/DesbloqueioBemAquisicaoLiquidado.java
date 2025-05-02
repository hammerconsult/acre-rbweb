package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Aquisicao;
import com.google.common.collect.Lists;

import java.util.List;

public class DesbloqueioBemAquisicaoLiquidado {

    private Aquisicao aquisicao;
    private DesbloqueioBemAquisicaoDoctoAquisicao documentoLiquidacaoSelecionado;
    private List<DesbloqueioBemAquisicaoDoctoAquisicao> documentosAquisicao;

    public DesbloqueioBemAquisicaoLiquidado() {
        documentosAquisicao = Lists.newArrayList();
    }

    public Aquisicao getAquisicao() {
        return aquisicao;
    }

    public void setAquisicao(Aquisicao aquisicao) {
        this.aquisicao = aquisicao;
    }

    public DesbloqueioBemAquisicaoDoctoAquisicao getDocumentoLiquidacaoSelecionado() {
        return documentoLiquidacaoSelecionado;
    }

    public void setDocumentoLiquidacaoSelecionado(DesbloqueioBemAquisicaoDoctoAquisicao documentoLiquidacaoSelecionado) {
        this.documentoLiquidacaoSelecionado = documentoLiquidacaoSelecionado;
    }

    public List<DesbloqueioBemAquisicaoDoctoAquisicao> getDocumentosAquisicao() {
        return documentosAquisicao;
    }

    public void setDocumentosAquisicao(List<DesbloqueioBemAquisicaoDoctoAquisicao> documentosAquisicao) {
        this.documentosAquisicao = documentosAquisicao;
    }
}
