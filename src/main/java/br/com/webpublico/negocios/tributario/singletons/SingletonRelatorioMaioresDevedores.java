package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.entidadesauxiliares.FiltroMaioresDevedores;
import br.com.webpublico.entidadesauxiliares.MaioresDevedores;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonRelatorioMaioresDevedores implements Serializable {

    private final Logger log = LoggerFactory.getLogger(SingletonRelatorioMaioresDevedores.class);

    private Map<FiltroMaioresDevedores, List<MaioresDevedores>> cacheImpressao;
    private FiltroMaioresDevedores relatorioEmExecucao;

    public void iniciaRelatorio(FiltroMaioresDevedores relatorioEmExecucao) {
        this.relatorioEmExecucao = relatorioEmExecucao;
    }

    public void finalizaRelatorio() {
        this.relatorioEmExecucao = null;
    }

    public FiltroMaioresDevedores getRelatorioEmExecucao() {
        return relatorioEmExecucao;
    }

    public List<MaioresDevedores> getRelatorio(FiltroMaioresDevedores filtro) {
        if (cacheImpressao == null) {
            cacheImpressao = Maps.newHashMap();
        }
        if (cacheImpressao.containsKey(filtro)) {
            return cacheImpressao.get(filtro);
        }
        return Lists.newArrayList();
    }

    public void addRelatorio(FiltroMaioresDevedores filtro, List<MaioresDevedores> relatorio) {
        if (cacheImpressao == null) {
            cacheImpressao = Maps.newHashMap();
        }
        if (cacheImpressao.containsKey(filtro)) {
            return;
        }
        cacheImpressao.put(filtro, relatorio);
    }

    public List<FiltroMaioresDevedores> getFiltros(boolean parcelamento, boolean cadastroImobiliario) {
        List<FiltroMaioresDevedores> retorno = Lists.newArrayList();
        if (cacheImpressao != null) {
            for (FiltroMaioresDevedores filtroMaioresDevedores : cacheImpressao.keySet()) {
                if (filtroMaioresDevedores.getParcelamento().equals(parcelamento) && filtroMaioresDevedores.getCadastroImobiliario().equals(cadastroImobiliario)) {
                    retorno.add(filtroMaioresDevedores);
                }
            }
        }
        Collections.sort(retorno);
        return retorno;
    }

}
