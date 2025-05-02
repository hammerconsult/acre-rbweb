package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.util.AssistenteBarraProgresso;

public class TipoAssistenteAnaliseContabil extends AssistenteBarraProgresso {

    private TipoAnaliseContabil tipoAnaliseContabil;
    private String mensagemBarraProcesso;

    public TipoAnaliseContabil getTipoAnaliseContabil() {
        return tipoAnaliseContabil;
    }

    public void setTipoAnaliseContabil(TipoAnaliseContabil tipoAnaliseContabil) {
        this.tipoAnaliseContabil = tipoAnaliseContabil;
    }

    public String getMensagemBarraProcesso() {
        return mensagemBarraProcesso;
    }

    public void setMensagemBarraProcesso(String mensagemBarraProcesso) {
        this.mensagemBarraProcesso = mensagemBarraProcesso;
    }
}
