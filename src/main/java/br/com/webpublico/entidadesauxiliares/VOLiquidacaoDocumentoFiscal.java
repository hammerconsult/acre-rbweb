package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DoctoFiscalLiquidacao;
import br.com.webpublico.entidades.LiquidacaoDoctoFiscal;

public class VOLiquidacaoDocumentoFiscal {

    private LiquidacaoDoctoFiscal liquidacaoDoctoFiscal;
    private DoctoFiscalLiquidacao doctoFiscalLiquidacao;
    private Boolean selecionado;
    private String mensagem;

    public LiquidacaoDoctoFiscal getLiquidacaoDoctoFiscal() {
        return liquidacaoDoctoFiscal;
    }

    public void setLiquidacaoDoctoFiscal(LiquidacaoDoctoFiscal liquidacaoDoctoFiscal) {
        this.liquidacaoDoctoFiscal = liquidacaoDoctoFiscal;
    }

    public DoctoFiscalLiquidacao getDoctoFiscalLiquidacao() {
        return doctoFiscalLiquidacao;
    }

    public void setDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        this.doctoFiscalLiquidacao = doctoFiscalLiquidacao;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
