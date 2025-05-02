package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;

import javax.faces.application.FacesMessage;
import java.util.List;
import java.util.Map;

public class AssistenteRenovacaoAlvara extends AssistenteBarraProgresso {
    private FiltroProcessoRenovacaoAlvara filtro;
    private List<CadastroEconomico> cadastros;
    private Map<CadastroEconomico, List<FacesMessage>> mapaInconsistencia;
    private ConfiguracaoTributario configuracaoTributario;

    public AssistenteRenovacaoAlvara() {
        super();
        this.cadastros = Lists.newArrayList();
        this.mapaInconsistencia = Maps.newHashMap();
    }

    public FiltroProcessoRenovacaoAlvara getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroProcessoRenovacaoAlvara filtro) {
        this.filtro = filtro;
    }

    public List<CadastroEconomico> getCadastros() {
        return cadastros;
    }

    public void setCadastros(List<CadastroEconomico> cadastros) {
        this.cadastros = cadastros;
    }

    public Map<CadastroEconomico, List<FacesMessage>> getMapaInconsistencia() {
        return mapaInconsistencia;
    }

    public void setMapaInconsistencia(Map<CadastroEconomico, List<FacesMessage>> mapaInconsistencia) {
        this.mapaInconsistencia = mapaInconsistencia;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }
}
