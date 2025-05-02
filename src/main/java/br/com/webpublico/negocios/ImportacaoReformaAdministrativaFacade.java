package br.com.webpublico.negocios;

import br.com.webpublico.controle.ImportacaoReformaAdministrativaControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.HierarquiaOrganizacionalResponsavel;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;

@Stateless
public class ImportacaoReformaAdministrativaFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public void processarHierarquias(List<ImportacaoReformaAdministrativaControlador.NovaHierarquia> novasHierarquias, HashMap<String, HierarquiaOrganizacional> mapHierarquias) {
        for (ImportacaoReformaAdministrativaControlador.NovaHierarquia novaHierarquia : novasHierarquias) {
            if (novaHierarquia.getAntigaHierarquia() != null) {
                em.merge(novaHierarquia.getAntigaHierarquia());
            }
            String codigoSuperior = buscarCodigoSemZerosFinais(novaHierarquia.getNovaHierarquia());
            codigoSuperior = codigoSuperior.substring(0, codigoSuperior.lastIndexOf("."));
            novaHierarquia.getNovaHierarquia().setSuperior(mapHierarquias.get(codigoSuperior).getSubordinada());
            if (novaHierarquia.getNovaHierarquia().getSubordinada().getId() == null) {
                novaHierarquia.getNovaHierarquia().setSubordinada(em.merge(novaHierarquia.getNovaHierarquia().getSubordinada()));
            }
            for (HierarquiaOrganizacional hierarquiaOrcamentaria : novaHierarquia.getHierarquiasOrcamentarias()) {
                HierarquiaOrganizacionalResponsavel hierarquiaOrganizacionalResponsavel = new HierarquiaOrganizacionalResponsavel();
                hierarquiaOrganizacionalResponsavel.setHierarquiaOrganizacionalAdm(novaHierarquia.getNovaHierarquia());
                hierarquiaOrganizacionalResponsavel.setHierarquiaOrganizacionalOrc(hierarquiaOrcamentaria);
                hierarquiaOrganizacionalResponsavel.setDataInicio(novaHierarquia.getNovaHierarquia().getInicioVigencia());
                novaHierarquia.getNovaHierarquia().getHierarquiaOrganizacionalResponsavels().add(hierarquiaOrganizacionalResponsavel);
            }
            String codigo = buscarCodigoSemZerosFinais(novaHierarquia.getNovaHierarquia());
            mapHierarquias.put(codigo, em.merge(novaHierarquia.getNovaHierarquia()));
        }
    }

    public String buscarCodigoSemZerosFinais(HierarquiaOrganizacional subordinada) {
        return subordinada.getCodigoSemZerosFinais().substring(0, subordinada.getCodigoSemZerosFinais().lastIndexOf("."));
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
