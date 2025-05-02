package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;
import java.util.List;

public class ParametroQuery implements Serializable {

    private String juncao;
    private List<ParametroQueryCampo> parametroQueryCampos;

    public ParametroQuery() {
    }

    public ParametroQuery(List<ParametroQueryCampo> parametroQueryCampos) {
        this(" and ", parametroQueryCampos);
    }

    public ParametroQuery(String juncao, List<ParametroQueryCampo> parametroQueryCampos) {
        this.juncao = juncao;
        this.parametroQueryCampos = parametroQueryCampos;
    }

    public String getJuncao() {
        return juncao;
    }

    public void setJuncao(String juncao) {
        this.juncao = juncao;
    }

    public List<ParametroQueryCampo> getParametroQueryCampos() {
        return parametroQueryCampos;
    }

    public void setParametroQueryCampos(List<ParametroQueryCampo> parametroQueryCampos) {
        this.parametroQueryCampos = parametroQueryCampos;
    }
}
