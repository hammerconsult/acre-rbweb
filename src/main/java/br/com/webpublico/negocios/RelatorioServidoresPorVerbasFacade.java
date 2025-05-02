package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioServidoresPorVerbas;
import br.com.webpublico.enums.TipoCalculoFP;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

@Stateless
@Deprecated
public class RelatorioServidoresPorVerbasFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;

    public List<Long> getIdsVerba(List<EventoFP> verbas) {
        List<Long> ids = Lists.newArrayList();
        if (verbas != null && !verbas.isEmpty()) {
            for (EventoFP verba : verbas) {
                ids.add(verba.getId());
            }
        }
        return ids;
    }

    public String getFiltrosSqlComuns(FiltroRelatorioServidoresPorVerbas filtroRelatorio) {
        String filtros = "";
        if (filtroRelatorio.getMes() != null) {
            filtros += " and folha.mes = " + (filtroRelatorio.getMes() - 1);
        }
        if (filtroRelatorio.getAno() != null) {
            filtros += " and folha.ano = " + filtroRelatorio.getAno();
        }
        if (filtroRelatorio.getTipoFolhaDePagamento() != null) {
            filtros += " and folha.tipofolhadepagamento = " + "'" + filtroRelatorio.getTipoFolhaDePagamento().name() + "'";
        }
        if (filtroRelatorio.getVersao() != null) {
            filtros += " and folha.versao = " + filtroRelatorio.getVersao();
        }
        if (filtroRelatorio.getHierarquiaAdministrativa() != null) {
            filtros += " and ho.codigo like " + "'" + filtroRelatorio.getHierarquiaAdministrativa().getCodigoSemZerosFinais() + "%" + "'";
        }
        if (filtroRelatorio.getTipoEventoFP() != null) {
            filtros += " and evento.TIPOEVENTOFP = " + "'" + filtroRelatorio.getTipoEventoFP().name() + "'";
        }
        if (filtroRelatorio.getTipoEventoFPFicha() != null) {
            filtros += " and item.TIPOEVENTOFP = " + "'" + filtroRelatorio.getTipoEventoFPFicha().name() + "'";
        }
        if (filtroRelatorio.getNaoExibirRetroacao()) {
            filtros += " and item.TIPOCALCULOFP = " + "'" + TipoCalculoFP.NORMAL.name() + "'";
        }
        return filtros;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public EventoFPFacade getEventoFPFacade() {
        return eventoFPFacade;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfiguracaoDeRelatorioFacade getConfiguracaoDeRelatorioFacade() {
        return configuracaoDeRelatorioFacade;
    }
}
