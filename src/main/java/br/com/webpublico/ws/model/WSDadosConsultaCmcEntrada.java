package br.com.webpublico.ws.model;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 15/01/14
 * Time: 09:25
 * To change this template use File | Settings | File Templates.
 */
public class WSDadosConsultaCmcEntrada {
    /*CMCNPJCONTR*/
    private String CMCNPJCONTR;
    /*CMCCHAVE*/
    private String CMCCHAVE;

    public String getCMCCHAVE() {
        return CMCCHAVE;
    }

    public void setCMCCHAVE(String CMCCHAVE) {
        this.CMCCHAVE = CMCCHAVE;
    }

    public String getCMCNPJCONTR() {
        return CMCNPJCONTR;
    }

    public void setCMCNPJCONTR(String CMCNPJCONTR) {
        this.CMCNPJCONTR = CMCNPJCONTR;
    }

    public void removerBarraPontoTracoDoCnpj() {
        if (CMCNPJCONTR != null) {
            CMCNPJCONTR = CMCNPJCONTR.replace("/", "").replace(".", "").replace("-", "");
        }
    }
}
