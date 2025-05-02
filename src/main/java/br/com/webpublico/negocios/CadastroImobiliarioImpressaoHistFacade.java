package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroImobiliarioImpressaoHist;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.ws.model.WSHistoricoBCI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class CadastroImobiliarioImpressaoHistFacade extends AbstractFacade<CadastroImobiliarioImpressaoHist> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;

    public CadastroImobiliarioImpressaoHistFacade() {
        super(CadastroImobiliarioImpressaoHist.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Long retornarUltimoValorSequencialLong(String atributoSeguencial) {
        return super.retornarUltimoValorSequencialLong(atributoSeguencial);
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public WSHistoricoBCI buscarHistoricoBCIPorAutenticidade(String autenticidade) {

        String sql = "select * from cadastroimobiliariohist " +
            "where replace(autenticidade, '.', '') = :autenticidade";

        Query q = em.createNativeQuery(sql, CadastroImobiliarioImpressaoHist.class);
        q.setParameter("autenticidade", StringUtil.removeCaracteresEspeciaisSemEspaco(autenticidade));

        List<CadastroImobiliarioImpressaoHist> cadastroImobiliarioImpressaoHists = q.getResultList();
        WSHistoricoBCI wsHistoricoBCI = retornarHistoricoBCI(cadastroImobiliarioImpressaoHists);
        return wsHistoricoBCI;
    }

    private WSHistoricoBCI retornarHistoricoBCI(List<CadastroImobiliarioImpressaoHist> cadastroImobiliarioImpressaoHists) {
        CadastroImobiliarioImpressaoHist cadastroImobiliarioImpressaoHist = null;
        if (cadastroImobiliarioImpressaoHists != null && !cadastroImobiliarioImpressaoHists.isEmpty()) {
            cadastroImobiliarioImpressaoHist = cadastroImobiliarioImpressaoHists.get(0);
        }
        WSHistoricoBCI wsHistoricoBCI = null;
        if (cadastroImobiliarioImpressaoHist != null) {
            CadastroImobiliarioImpressaoHist ultimoHistorico = buscarUltimoHistorico(cadastroImobiliarioImpressaoHist.getCadastroImobiliario().getId());

            wsHistoricoBCI = new WSHistoricoBCI();
            cadastroImobiliarioImpressaoHist.setCadastroImobiliario(cadastroImobiliarioFacade.inicializarHistoricosImpressao(cadastroImobiliarioImpressaoHist.getCadastroImobiliario().getId()));
            wsHistoricoBCI.setId(cadastroImobiliarioImpressaoHist.getId());
            wsHistoricoBCI.setAutenticidade(cadastroImobiliarioImpressaoHist.getAutenticidade());
            wsHistoricoBCI.setNomeUsuario(cadastroImobiliarioImpressaoHist.getUsuarioSistema() != null ? cadastroImobiliarioImpressaoHist.getUsuarioSistema().getNome() : "");
            wsHistoricoBCI.setDataOperacao(cadastroImobiliarioImpressaoHist.getDataOperacao());
            wsHistoricoBCI.setProprietario(cadastroImobiliarioImpressaoHist.getCadastroImobiliario().getPropriedadeVigente().toString());
            wsHistoricoBCI.setInscricaoCadastral(cadastroImobiliarioImpressaoHist.getCadastroImobiliario().getInscricaoCadastral());
            wsHistoricoBCI.setStatus(cadastroImobiliarioImpressaoHist.isAuditoria() ? "Válido" : (ultimoHistorico != null &&
                !cadastroImobiliarioImpressaoHist.getId().equals(ultimoHistorico.getId())) ? "Vencido" : "Válido");
        }
        return wsHistoricoBCI;
    }

    public WSHistoricoBCI buscarHistoricoBCIPorID(Long id) {

        String sql = "select * from CADASTROIMOBILIARIOHIST " +
            "where ID = :id";

        Query q = em.createNativeQuery(sql, CadastroImobiliarioImpressaoHist.class);

        q.setParameter("id", id);
        List<CadastroImobiliarioImpressaoHist> cadastroImobiliarioImpressaoHists = q.getResultList();
        WSHistoricoBCI wsHistoricoBCI = retornarHistoricoBCI(cadastroImobiliarioImpressaoHists);
        return wsHistoricoBCI;
    }

    private CadastroImobiliarioImpressaoHist buscarUltimoHistorico(Long idCadastro) {
        String sql = " select hist.* from cadastroimobiliariohist hist " +
            " where hist.cadastroimobiliario_id = :idCadastro " +
            " and hist.dataoperacao = (select max(dataoperacao) from cadastroimobiliariohist " +
            "                          where hist.cadastroimobiliario_id = cadastroimobiliario_id) " +
            " order by hist.dataoperacao desc ";

        Query q = em.createNativeQuery(sql, CadastroImobiliarioImpressaoHist.class);
        q.setParameter("idCadastro", idCadastro);

        List<CadastroImobiliarioImpressaoHist> historicos = q.getResultList();
        return (historicos != null && !historicos.isEmpty()) ? historicos.get(0) : null;
    }
}
