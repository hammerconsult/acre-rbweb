package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.ws.model.WsAutenticaAlvara;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 29/01/15
 * Time: 22:02
 * To change this template use File | Settings | File Templates.
 */
public class AutenticaAlvara {
    private TipoAlvara tipoAlvara;
    private Boolean provisorio;
    private Date dataEmissao;
    private String incricaoCadastral;
    private String assinaturaParaAutenticar;

    public AutenticaAlvara(WsAutenticaAlvara wsAutenticaAlvara) {
        this.setTipoAlvara(wsAutenticaAlvara.getTipoAlvara());
        this.setDataEmissao(wsAutenticaAlvara.getDataEmissao());
        this.setIncricaoCadastral(wsAutenticaAlvara.getInscricaoCadastral());
        this.setProvisorio(wsAutenticaAlvara.getProvisorio());
        this.setAssinaturaParaAutenticar(wsAutenticaAlvara.getAssinaturaParaAutenticar());
    }

    public AutenticaAlvara() {
        this.provisorio = false;
    }

    public String getAssinaturaParaAutenticar() {
        return assinaturaParaAutenticar;
    }

    public void setAssinaturaParaAutenticar(String assinaturaParaAutenticar) {
        this.assinaturaParaAutenticar = assinaturaParaAutenticar;
    }

    public String getIncricaoCadastral() {
        return incricaoCadastral;
    }

    public void setIncricaoCadastral(String incricaoCadastral) {
        this.incricaoCadastral = incricaoCadastral;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public Boolean getProvisorio() {
        return provisorio;
    }

    public void setProvisorio(Boolean provisorio) {
        this.provisorio = provisorio;
    }
}
