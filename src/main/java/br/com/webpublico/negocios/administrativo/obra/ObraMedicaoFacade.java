package br.com.webpublico.negocios.administrativo.obra;

import br.com.webpublico.entidades.ExecucaoContrato;
import br.com.webpublico.entidades.Obra;
import br.com.webpublico.entidades.ObraMedicao;
import br.com.webpublico.entidades.SolicitacaoEmpenho;
import br.com.webpublico.negocios.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;

/**
 * Created by zaca on 28/04/17.
 */
@Stateless
public class ObraMedicaoFacade extends AbstractFacade<ObraMedicao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private DocumentosNecessariosAnexoFacade documentosNecessariosAnexoFacade;
    @EJB
    private TipoDocumentoAnexoFacade tipoDocumentoAnexoFacade;
    @EJB
    private ObraFacade obraFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;

    public ObraMedicaoFacade() {
        super(ObraMedicao.class);
    }

    @Override
    public ObraMedicao recuperar(Object id) {
        ObraMedicao selecionado = (ObraMedicao) super.recuperar(id);
        selecionado.getAnexos().size();
        selecionado.getExecucoesMedicao().size();
        return selecionado;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ObraMedicao buscarUltimaObraMedicaoPorMaiorNumeroDaObra(Obra obra) {
        String sql = " SELECT om.* " +
            "FROM OBRA ob " +
            "INNER JOIN OBRAMEDICAO om ON ob.ID = om.OBRA_ID " +
            "WHERE ob.ID = :obra_id " +
            "AND om.numero = ( " +
            "                  SELECT MAX(medicao.numero) " +
            "                  FROM obraMedicao medicao " +
            "                  WHERE medicao.obra_id = ob.id " +
            "                  )";
        Query q = getEntityManager().createNativeQuery(sql, ObraMedicao.class);
        q.setParameter("obra_id", obra.getId());
        return (ObraMedicao) q.getSingleResult();
    }

    public ObraMedicao buscarUltimaMedicaoPorObra(ObraMedicao medicao) {
        String sql = " select medicao.* from obramedicao medicao " +
            " inner join obra ob on ob.id = medicao.obra_id " +
            " where ob.id = :obraId " +
            " and (:dataInicial between medicao.datainicial and coalesce(medicao.datafinal, :dataInicial) " +
            " or :dataFinal between medicao.datainicial and coalesce(medicao.datafinal, :dataFinal)) ";
        if (medicao.getId() != null) {
            sql += " and medicao.id <> :medicaoId ";
        }
        sql += " order by medicao.datafinal desc ";
        Query q = em.createNativeQuery(sql, ObraMedicao.class);
        q.setParameter("obraId", medicao.getObra().getId());
        q.setParameter("dataInicial", medicao.getDataInicial());
        q.setParameter("dataFinal", medicao.getDataFinal());
        if (medicao.getId() != null) {
            q.setParameter("medicaoId", medicao.getId());
        }
        if (!q.getResultList().isEmpty()) {
            return (ObraMedicao) q.getResultList().get(0);
        }
        return null;
    }

    public ObraMedicao buscarMedicaoPorNumero(ObraMedicao medicao) {
        String sql = " select medicao.* from obramedicao medicao " +
            " where medicao.obra_id = :obraId " +
            "   and medicao.numero = :numeroMedicao ";
        if (medicao.getId() != null) {
            sql += " and medicao.id <> :medicaoId ";
        }
        Query q = em.createNativeQuery(sql, ObraMedicao.class);
        q.setParameter("obraId", medicao.getObra().getId());
        q.setParameter("numeroMedicao", medicao.getNumero());
        if (medicao.getId() != null) {
            q.setParameter("medicaoId", medicao.getId());
        }
        if (!q.getResultList().isEmpty()) {
            return (ObraMedicao) q.getResultList().get(0);
        }
        return null;
    }

    public boolean isObraMedicaoWithSolicitacaoEmpenho(ObraMedicao obraMedicao) {
        String sql = " select se.* " +
            "      from obraMedicao om " +
            "      inner join obramedicaoexeccontrato omExec on omExec.obramedicao_id = om.id " +
            "      inner join execucaocontrato exec on exec.id = omExec.execucaocontrato_id " +
            "      inner join execucaocontratoempenho execEmp on execEmp.execucaocontrato_id = exec.id " +
            "      inner join solicitacaoempenho se on se.id = execEmp.solicitacaoempenho_id " +
            "      inner join empenho em on em.id = execEmp.empenho_id " +
            "      where om.id = :obraMedicao_id";
        Query q = em.createNativeQuery(sql, SolicitacaoEmpenho.class);
        q.setParameter("obraMedicao_id", obraMedicao.getId());
        return q.getResultList().isEmpty();
    }

    public BigDecimal getValorTotalMedicoesPorExecucao(ExecucaoContrato execucaoContrato) {
        String sql = " select coalesce(sum(om.valortotal),0) as valor from obramedicaoexeccontrato  exec " +
            " inner join obramedicao om on om.id = exec.obramedicao_id " +
            " where exec.execucaocontrato_id = :idExecucao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", execucaoContrato.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public DocumentosNecessariosAnexoFacade getDocumentosNecessariosAnexoFacade() {
        return documentosNecessariosAnexoFacade;
    }

    public TipoDocumentoAnexoFacade getTipoDocumentoAnexoFacade() {
        return tipoDocumentoAnexoFacade;
    }

    public ObraFacade getObraFacade() {
        return obraFacade;
    }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public Long getProximoNumero(Obra obra) {
        String sql = " select nvl(max(numero),0) + 1 as CODIGO from ObraMedicao where OBRA_ID = :idObra";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObra", obra.getId());
        return ((BigDecimal) q.getSingleResult()).longValue();

    }
}
