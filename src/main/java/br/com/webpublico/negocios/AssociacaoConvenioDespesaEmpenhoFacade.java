package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConvenioDespesa;
import br.com.webpublico.entidades.Empenho;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.List;

@Stateless
public class AssociacaoConvenioDespesaEmpenhoFacade implements Serializable {

    @EJB
    private ConvenioDespesaFacade convenioDespesaFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public List<Empenho> buscarEmpenhosNormaisAndRestos(String filtro) {
        return empenhoFacade.buscarEmpenhosNormaisAndRestos(sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente(), filtro, sistemaFacade.getExercicioCorrente());
    }

    public List<ConvenioDespesa> buscarConveniosDeDespesa(String parte) {
        return convenioDespesaFacade.listaFiltrando(parte.trim(), "numConvenio", "objeto");
    }

    public Empenho recuperarEmpenho(Empenho empenho) {
        return empenhoFacade.recuperar(empenho.getId());
    }

    public void salvarEmpenho(Empenho empenho) {
        empenhoFacade.salvar(empenho);
    }
}
