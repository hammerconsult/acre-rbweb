/*
 * Codigo gerado automaticamente em Fri Mar 04 09:44:14 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoModalidadeContratoFP;
import br.com.webpublico.entidades.ModalidadeContratoFP;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Stateless
public class ConfiguracaoModalidadeContratoFPFacade extends AbstractFacade<ConfiguracaoModalidadeContratoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    @EJB
    private JornadaDeTrabalhoFacade jornadaDeTrabalhoFacade;
    @EJB
    private TipoRegimeFacade tipoRegimeFacade;
    @EJB
    private TipoPrevidenciaFPFacade tipoPrevidenciaFPFacade;
    @EJB
    private CategoriaSEFIPFacade categoriaSEFIPFacade;
    @EJB
    private TipoAdmissaoFGTSFacade tipoAdmissaoFGTSFacade;
    @EJB
    private TipoAdmissaoSEFIPFacade tipoAdmissaoSEFIPFacade;
    @EJB
    private TipoAdmissaoRAISFacade tipoAdmissaoRAISFacade;
    @EJB
    private MovimentoCAGEDFacade movimentoCAGEDFacade;
    @EJB
    private OcorrenciaSEFIPFacade ocorrenciaSEFIPFacade;
    @EJB
    private RetencaoIRRFFacade retencaoIRRFFacade;
    @EJB
    private VinculoEmpregaticioFacade vinculoEmpregaticioFacade;
    @EJB
    private NaturezaRendimentoFacade naturezaRendimentoFacade;
    @EJB
    private HorarioDeTrabalhoFacade horarioDeTrabalhoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoModalidadeContratoFPFacade() {
        super(ConfiguracaoModalidadeContratoFP.class);
    }

    public ModalidadeContratoFPFacade getModalidadeContratoFPFacade() {
        return modalidadeContratoFPFacade;
    }

    public JornadaDeTrabalhoFacade getJornadaDeTrabalhoFacade() {
        return jornadaDeTrabalhoFacade;
    }

    public TipoRegimeFacade getTipoRegimeFacade() {
        return tipoRegimeFacade;
    }

    public TipoPrevidenciaFPFacade getTipoPrevidenciaFPFacade() {
        return tipoPrevidenciaFPFacade;
    }

    public CategoriaSEFIPFacade getCategoriaSEFIPFacade() {
        return categoriaSEFIPFacade;
    }

    public TipoAdmissaoFGTSFacade getTipoAdmissaoFGTSFacade() {
        return tipoAdmissaoFGTSFacade;
    }

    public TipoAdmissaoSEFIPFacade getTipoAdmissaoSEFIPFacade() {
        return tipoAdmissaoSEFIPFacade;
    }

    public TipoAdmissaoRAISFacade getTipoAdmissaoRAISFacade() {
        return tipoAdmissaoRAISFacade;
    }

    public MovimentoCAGEDFacade getMovimentoCAGEDFacade() {
        return movimentoCAGEDFacade;
    }

    public OcorrenciaSEFIPFacade getOcorrenciaSEFIPFacade() {
        return ocorrenciaSEFIPFacade;
    }

    public RetencaoIRRFFacade getRetencaoIRRFFacade() {
        return retencaoIRRFFacade;
    }

    public VinculoEmpregaticioFacade getVinculoEmpregaticioFacade() {
        return vinculoEmpregaticioFacade;
    }

    public NaturezaRendimentoFacade getNaturezaRendimentoFacade() {
        return naturezaRendimentoFacade;
    }

    public HorarioDeTrabalhoFacade getHorarioDeTrabalhoFacade() {
        return horarioDeTrabalhoFacade;
    }

    public ConfiguracaoModalidadeContratoFP recuperarPelaModalidade(ModalidadeContratoFP modalidade) {
        Query q = em.createQuery("from ConfiguracaoModalidadeContratoFP c where c.modalidadeContrato = :modalidade")
                .setParameter("modalidade", modalidade);
        List<ConfiguracaoModalidadeContratoFP> retorno = q.getResultList();
        if (retorno.isEmpty()) {
            return null;
        }
        ConfiguracaoModalidadeContratoFP c = retorno.get(0);
        c.getConfiguracaoPrevidenciaVinculoFPs().size();
        return c;
    }

    public void salvar(Map<ModalidadeContratoFP, ConfiguracaoModalidadeContratoFP> mapa) {
        for (Map.Entry<ModalidadeContratoFP, ConfiguracaoModalidadeContratoFP> modalidade : mapa.entrySet()) {
            em.merge(modalidade.getValue());
        }
    }
}
