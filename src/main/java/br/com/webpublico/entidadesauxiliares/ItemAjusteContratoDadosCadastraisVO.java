package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AjusteContratoDadosCadastrais;
import br.com.webpublico.util.Util;

public class ItemAjusteContratoDadosCadastraisVO {

    private AjusteContratoDadosCadastrais ajusteAlteracaoDados;
    private AjusteContratoDadosCadastrais ajusteDadosOriginais;

    public ItemAjusteContratoDadosCadastraisVO() {
    }

    public AjusteContratoDadosCadastrais getAjusteAlteracaoDados() {
        return ajusteAlteracaoDados;
    }

    public void setAjusteAlteracaoDados(AjusteContratoDadosCadastrais ajusteAlteracaoDados) {
        this.ajusteAlteracaoDados = ajusteAlteracaoDados;
    }

    public AjusteContratoDadosCadastrais getAjusteDadosOriginais() {
        return ajusteDadosOriginais;
    }

    public void setAjusteDadosOriginais(AjusteContratoDadosCadastrais ajusteDadosOriginais) {
        this.ajusteDadosOriginais = ajusteDadosOriginais;
    }

    public boolean hasAlteracaoObjeto(Object object){
        return Util.isNotNull(object);
    }
}
