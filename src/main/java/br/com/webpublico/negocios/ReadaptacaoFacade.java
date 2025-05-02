package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.ProvimentoFP;
import br.com.webpublico.entidades.Readaptacao;
import br.com.webpublico.entidades.TipoProvimento;
import br.com.webpublico.util.UtilRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ReadaptacaoFacade extends AbstractFacade<Readaptacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReadaptacaoFacade() {
        super(Readaptacao.class);
    }

    @Override
    public Readaptacao recuperar(Object id) {
        Readaptacao readaptacao = super.recuperar(id);
        readaptacao.getOpcoes().size();
        readaptacao.getVigenciaReadaptacoes().size();
        return readaptacao;
    }

    @Override
    public void salvarNovo(Readaptacao entity) {

        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setVinculoFP(entity.getContratoFP());
        provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.READAPTACAO));
        provimentoFP.setDataProvimento(UtilRH.getDataOperacao());
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(entity.getContratoFP()));
        provimentoFPFacade.salvarNovo(provimentoFP);

        provimentoFP = (ProvimentoFP) provimentoFPFacade.recuperar(ProvimentoFP.class, provimentoFP.getId());

        ContratoFP contratoFP = (ContratoFP) contratoFPFacade.recuperar(ContratoFP.class, entity.getContratoFP().getId());
        contratoFP.setProvimentoFP(provimentoFP);
        contratoFPFacade.salvar(contratoFP);

        super.salvarNovo(entity);
    }
}
