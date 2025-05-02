package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LevantamentoColetaDados;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.util.AssistenteBarraProgresso;

public class AssistenteLevantamentoBemImovel {

    private AssistenteBarraProgresso assistenteBarraProgresso;
    private LevantamentoColetaDados levantameno;
    private UnidadeOrganizacional unidadeAdm;
    private UnidadeOrganizacional unidadeOrc;

    public AssistenteLevantamentoBemImovel() {
        assistenteBarraProgresso = new AssistenteBarraProgresso();
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public UnidadeOrganizacional getUnidadeAdm() {
        return unidadeAdm;
    }

    public void setUnidadeAdm(UnidadeOrganizacional unidadeAdm) {
        this.unidadeAdm = unidadeAdm;
    }

    public UnidadeOrganizacional getUnidadeOrc() {
        return unidadeOrc;
    }

    public void setUnidadeOrc(UnidadeOrganizacional unidadeOrc) {
        this.unidadeOrc = unidadeOrc;
    }

    public LevantamentoColetaDados getLevantameno() {
        return levantameno;
    }

    public void setLevantameno(LevantamentoColetaDados levantameno) {
        this.levantameno = levantameno;
    }
}
