package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DesdobramentoEmpenho;
import br.com.webpublico.entidades.Empenho;

public class EmpenhoVO {

    private Empenho empenho;
    private DesdobramentoEmpenho desdobramentoEmpenho;

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public DesdobramentoEmpenho getDesdobramentoEmpenho() {
        return desdobramentoEmpenho;
    }

    public void setDesdobramentoEmpenho(DesdobramentoEmpenho desdobramentoEmpenho) {
        this.desdobramentoEmpenho = desdobramentoEmpenho;
    }
}
