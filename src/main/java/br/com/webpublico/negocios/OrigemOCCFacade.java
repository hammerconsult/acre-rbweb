/*
 * Codigo gerado automaticamente em Tue Jun 26 09:40:23 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.EntidadeOCC;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class OrigemOCCFacade extends AbstractFacade<OrigemContaContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TagOccFacade tagOCCFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private CategoriaDividaPublicaFacade categoriaDividaPublicaFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private TipoPassivoAtuarialFacade tipoPassivoAtuarialFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoContaAuxiliarFacade tipoContaAuxiliarFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrigemOCCFacade() {
        super(OrigemContaContabil.class);
    }

    public List<OrigemContaContabil> listaPorEntidadeOCC(EntidadeOCC ent) {
        String hql = "select occ from OrigemContaContabil occ"
            + " inner join occ.tagOCC tag "
            + " where tag.entidadeOCC = :ent";
        Query q = getEntityManager().createQuery(hql, OrigemContaContabil.class);
        q.setParameter("ent", ent);
        return q.getResultList();
    }

    public List<OCCConta> listaTodasContabil() {
        String hql = "from OCCConta occ order by occ.contaOrigem";
        Query q = getEntityManager().createQuery(hql);
        List<OCCConta> contas = q.getResultList();
        if (contas.isEmpty()) {
            return new ArrayList<OCCConta>();
        } else {
            return contas;
        }
    }

    public List<OCCBanco> listaTodasFinanceira() {
        String hql = "from OCCBanco ocb order by ocb.contaContabil";
        Query q = getEntityManager().createQuery(hql);
        List<OCCBanco> contas = q.getResultList();
        if (contas.isEmpty()) {
            return new ArrayList<OCCBanco>();
        } else {
            return contas;
        }
    }

    public List<OCCConta> listaOCCContaPorEntidadeOCC(Conta conta) {
        String sql = "SELECT occ.*, oc.contaorigem_id FROM origemcontacontabil occ "
            + "INNER JOIN occconta oc ON occ.id = oc.id "
            + "WHERE oc.contaorigem_id = :conta "
            + "AND occ.vigencia IS null";
        Query q = getEntityManager().createNativeQuery(sql, OCCConta.class);
        q.setParameter("conta", conta.getId());
        return q.getResultList();
    }

    public List<OCCBanco> listaOCCBancoPorEntidadeOCC(SubConta subConta) {
        String sql = "SELECT occ.*, oc.subconta_id FROM origemcontacontabil occ "
            + "INNER JOIN occbanco oc ON occ.id = oc.id "
            + "WHERE oc.subconta_id = :subconta "
            + "AND occ.vigencia IS null";
        Query q = getEntityManager().createNativeQuery(sql, OCCBanco.class);
        q.setParameter("subconta", subConta.getId());
        return q.getResultList();
    }

    public TagOccFacade getTagOCCFacade() {
        return tagOCCFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public void setGrupoMaterialFacade(GrupoMaterialFacade grupoMaterialFacade) {
        this.grupoMaterialFacade = grupoMaterialFacade;
    }

    public TipoPassivoAtuarialFacade getTipoPassivoAtuarialFacade() {
        return tipoPassivoAtuarialFacade;
    }

    public void setTipoPassivoAtuarialFacade(TipoPassivoAtuarialFacade tipoPassivoAtuarialFacade) {
        this.tipoPassivoAtuarialFacade = tipoPassivoAtuarialFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public void setClasseCredorFacade(ClasseCredorFacade classeCredorFacade) {
        this.classeCredorFacade = classeCredorFacade;
    }

    public List<OrigemContaContabil> listaOCCPorTag(TagOCC tagOCC) {
        String hql = "from OrigemContaContabil where tagOCC = :tag";
        Query q = em.createQuery(hql);
        q.setParameter("tag", tagOCC);
        return q.getResultList();
    }

    public List<EventoContabil> retornaEventosPorTag(TagOCC tagOCC) {
        String sql = "SELECT DISTINCT EC.* "
            + " FROM LCP "
            + " INNER JOIN CLP ON LCP.CLP_ID = CLP.ID "
            + " INNER JOIN ITEMEVENTOCLP ITEM ON CLP.ID = ITEM.CLP_ID "
            + " INNER JOIN EVENTOCONTABIL EC ON ITEM.EVENTOCONTABIL_ID = EC.ID "
            + " WHERE LCP.TAGOCCCREDITO_ID = :tag OR LCP.TAGOCCDEBITO_ID = :tag";
        Query q = em.createNativeQuery(sql, EventoContabil.class);
        q.setParameter("tag", tagOCC.getId());
        return q.getResultList();
    }

    public List<ClasseCredor> listaFiltrandoPorTipoClasse(String parte, TipoClasseCredor tipoClasseCredor) {
        String sql = "SELECT distinct CC.* "
            + " FROM CLASSECREDOR CC "
            + " WHERE (lower(CC.DESCRICAO) LIKE :parte OR CC.CODIGO LIKE :parte) "
            + " AND CC.TIPOCLASSECREDOR = :tipo ";
        Query q = em.createNativeQuery(sql, ClasseCredor.class);
        q.setParameter("tipo", tipoClasseCredor.name());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<ClasseCredor>();
        } else {
            return lista;
        }
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public OrigemContaContabil buscarConfiguracaoPorConta(OrigemContaContabil config) {
        return buscarConfiguracaoPorConta(config, sistemaFacade.getDataOperacao());
    }

    public OrigemContaContabil buscarConfiguracaoPorConta(OrigemContaContabil config, Date dataOperacao) {
        String sql = " SELECT OCC.*, c.* ";
        sql += " FROM ORIGEMCONTACONTABIL OCC";
        sql += " INNER JOIN OCCCONTA C ON OCC.ID = C.ID";
        sql += " WHERE OCC.TAGOCC_ID = :tag";
        sql += " AND C.contaOrigem_id = :conta";
        sql += " AND to_date(:dataVigencia, 'dd/mm/yyyy') between trunc(OCC.INICIOVIGENCIA) AND coalesce(trunc(OCC.FIMVIGENCIA), to_date(:dataVigencia, 'dd/mm/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND OCC.ID <> :idOrigem";
        }
        Query q = em.createNativeQuery(sql, OCCConta.class);
        q.setParameter("tag", config.getTagOCC().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("conta", ((OCCConta) config).getContaOrigem().getId());
        if (config.getId() != null) {
            q.setParameter("idOrigem", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (OrigemContaContabil) q.getSingleResult();
        }
        return null;
    }

    public OrigemContaContabil buscarConfiguracaoPorBanco(OrigemContaContabil config) {
        String sql = " SELECT OCC.*, B.* ";
        sql += " FROM ORIGEMCONTACONTABIL OCC";
        sql += " INNER JOIN OCCBANCO B ON OCC.ID = B.ID";
        sql += " WHERE OCC.TAGOCC_ID = :tag";
        sql += " AND B.SUBCONTA_ID = :subConta";
        sql += " AND to_date(:dataVigencia, 'dd/mm/yyyy') between trunc(OCC.INICIOVIGENCIA) AND coalesce(trunc(OCC.FIMVIGENCIA), to_date(:dataVigencia, 'dd/mm/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND OCC.ID <> :idConfig";
        }
        if (config.getId() != null) {
            sql += " AND OCC.ID <> :idConfig";
        }
        if (config.getId() != null) {
            sql += " AND OCC.ID <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, OCCBanco.class);
        q.setParameter("tag", config.getTagOCC().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("subConta", ((OCCBanco) config).getSubConta().getId());
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (OrigemContaContabil) q.getSingleResult();
        }
        return null;
    }

    public OrigemContaContabil buscarConfiguracaoPorClassePessoa(OrigemContaContabil origemocc) {

        String sql = "SELECT OCC.*, CP.* "
            + " FROM ORIGEMCONTACONTABIL OCC "
            + " INNER JOIN OCCCLASSEPESSOA CP ON OCC.ID = CP.ID "
            + " WHERE OCC.TAGOCC_ID = :tag "
            + " AND CP.CLASSEPESSOA_ID = :classePessoa "
            + " AND to_date(:dataVigencia, 'dd/mm/yyyy') between trunc(OCC.INICIOVIGENCIA) AND coalesce(trunc(OCC.FIMVIGENCIA), to_date(:dataVigencia, 'dd/mm/yyyy')) ";
        if (origemocc.getId() != null) {
            sql += " AND OCC.ID <> :idOrigem ";
        }
        Query q = em.createNativeQuery(sql, OccClassePessoa.class);
        q.setParameter("tag", origemocc.getTagOCC().getId());
        q.setParameter("classePessoa", ((OccClassePessoa) origemocc).getClassePessoa().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (origemocc.getId() != null) {
            q.setParameter("idOrigem", origemocc.getId());
        }
        if (!q.getResultList().isEmpty()) {
            return (OrigemContaContabil) q.getSingleResult();
        } else {
            return null;
        }
    }

    public OrigemContaContabil buscarConfiguracaoPorTipoPassivo(OrigemContaContabil origemocc) {
        String sql = "SELECT OCC.*, TPA.*"
            + " FROM ORIGEMCONTACONTABIL OCC "
            + " INNER JOIN OCCTIPOPASSIVOATUARIAL TPA ON TPA.ID = OCC.ID "
            + " WHERE OCC.TAGOCC_ID = :tag "
            + " AND TPA.TIPOPASSIVOATUARIAL_ID = :tipoPassivo "
            + " AND to_date(:dataVigencia, 'dd/mm/yyyy') between trunc(OCC.INICIOVIGENCIA) AND coalesce(trunc(OCC.FIMVIGENCIA), to_date(:dataVigencia, 'dd/mm/yyyy')) ";
        if (origemocc.getId() != null) {
            sql += " AND OCC.ID <> :idOrigem ";
        }
        Query consulta = em.createNativeQuery(sql, OCCTipoPassivoAtuarial.class);
        consulta.setParameter("tag", origemocc.getTagOCC().getId());
        consulta.setParameter("tipoPassivo", ((OCCTipoPassivoAtuarial) origemocc).getTipoPassivoAtuarial().getId());
        consulta.setParameter("dataVigencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (origemocc.getId() != null) {
            consulta.setParameter("idOrigem", origemocc.getId());
        }
        if (!consulta.getResultList().isEmpty()) {
            return (OrigemContaContabil) consulta.getSingleResult();
        } else {
            return null;
        }
    }

    @Override
    public void remover(OrigemContaContabil selecionado) {
        em.remove(em.merge(selecionado));
    }

    public List<OrigemContaContabil> listaOrigemContaContabilPorContaContabil(Conta contaContabilLista, EntidadeOCC entidadeOCC, TagOCC tagOCC) {
        String sqlOccBanco =
            " select occ.*, ob.subconta_id"
                + " from origemcontacontabil occ"
                + " inner join occbanco ob on occ.id = ob.id"
                + " inner join tagocc tag on occ.tagOCC_id = tag.id"
                + " inner join conta co on occ.contacontabil_id = co.id"
                + "                     or occ.containterestado_id = co.id"
                + "                     or occ.containtermunicipal_id = co.id"
                + "                     or occ.containteruniao_id = co.id"
                + "                     or occ.containtra_id = co.id"
                + " inner join contacontabil cc on cc.id = co.id "
                + " where cc.id = :conta";
        if (entidadeOCC != null) {
            sqlOccBanco += " and tag.entidadeOCC = :ent";
        }
        if (tagOCC != null) {
            sqlOccBanco += " and tag.id = :tag";
        }


        Query consultaOccBanco = em.createNativeQuery(sqlOccBanco, OCCBanco.class);
        consultaOccBanco.setParameter("conta", contaContabilLista.getId());
        if (entidadeOCC != null) {
            consultaOccBanco.setParameter("ent", entidadeOCC.name());
        }
        if (tagOCC != null) {
            consultaOccBanco.setParameter("tag", tagOCC.getId());
        }
        consultaOccBanco.setMaxResults(10);
        List<OrigemContaContabil> retorno = consultaOccBanco.getResultList();


        String sqlOccConta =
            " select occ.*, oc.contaorigem_id"
                + " from origemcontacontabil occ"
                + " inner join occconta oc on occ.id = oc.id"
                + " inner join tagocc tag on occ.tagOCC_id = tag.id"
                + " inner join conta co on occ.contacontabil_id = co.id"
                + "                     or occ.containterestado_id = co.id"
                + "                     or occ.containtermunicipal_id = co.id"
                + "                     or occ.containteruniao_id = co.id"
                + "                     or occ.containtra_id = co.id"
                + " inner join contacontabil cc on cc.id = co.id "
                + " where cc.id = :conta";
        if (entidadeOCC != null) {
            sqlOccConta += " and tag.entidadeOCC = :ent";
        }
        if (tagOCC != null) {
            sqlOccConta += " and tag.id = :tag";
        }


        Query consultaOccConta = em.createNativeQuery(sqlOccConta, OCCConta.class);
        consultaOccConta.setParameter("conta", contaContabilLista.getId());
        if (entidadeOCC != null) {
            consultaOccConta.setParameter("ent", entidadeOCC.name());
        }
        if (tagOCC != null) {
            consultaOccConta.setParameter("tag", tagOCC.getId());
        }
        consultaOccConta.setMaxResults(10);


        if (retorno == null) {
            retorno = new ArrayList<OrigemContaContabil>();
        }
        retorno.addAll(consultaOccConta.getResultList());
        return retorno;
    }

    public CategoriaDividaPublicaFacade getCategoriaDividaPublicaFacade() {
        return categoriaDividaPublicaFacade;
    }

    public OrigemContaContabil buscarConfiguracaoPorNaturezaDividaPublica(OrigemContaContabil origemcc) {
        String sql = " SELECT OCC.*, O.* ";
        sql += " FROM ORIGEMCONTACONTABIL OCC";
        sql += " INNER JOIN OCCNATUREZADIVIDAPUBLICA O ON OCC.ID = O.ID";
        sql += " WHERE OCC.TAGOCC_ID = :tag ";
        sql += " AND O.CATEGORIADIVIDAPUBLICA_ID = :categoriadividapublica";
        sql += " AND to_date(:dataVigencia, 'dd/mm/yyyy')between trunc(OCC.INICIOVIGENCIA) AND coalesce(trunc(OCC.FIMVIGENCIA), to_date(:dataVigencia, 'dd/mm/yyyy')) ";
        if (origemcc.getId() != null) {
            sql += " AND OCC.ID <> :idConfig";
        }
        if (origemcc.getId() != null) {
            sql += " AND OCC.ID <> :idConfig";
        }
        if (origemcc.getId() != null) {
            sql += " AND OCC.ID <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, OCCNaturezaDividaPublica.class);
        q.setParameter("tag", origemcc.getTagOCC().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("categoriadividapublica", ((OCCNaturezaDividaPublica) origemcc).getCategoriaDividaPublica().getId());
        if (origemcc.getId() != null) {
            q.setParameter("idConfig", origemcc.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (OrigemContaContabil) q.getSingleResult();
        }
        return null;
    }

    public OrigemContaContabil buscarConfiguracaoPorGrupoMaterial(OrigemContaContabil origemocc) {
        String sql = "SELECT OCC.*, GM.* ";
        sql += " FROM ORIGEMCONTACONTABIL OCC ";
        sql += " INNER JOIN OCCGRUPOMATERIAL GM ON OCC.ID = GM.ID ";
        sql += " WHERE OCC.TAGOCC_ID = :tag ";
        sql += " AND GM.GRUPOMATERIAL_ID = :grupoMaterial ";
        sql += " AND to_date(:dataVigencia, 'dd/mm/yyyy') between trunc(OCC.INICIOVIGENCIA) AND coalesce(trunc(OCC.FIMVIGENCIA), to_date(:dataVigencia, 'dd/mm/yyyy')) ";
        if (origemocc.getId() != null) {
            sql += " AND OCC.ID <> :idConfig ";
        }
        if (origemocc.getId() != null) {
            sql += " AND OCC.ID <> :idConfig ";
        }
        if (origemocc.getId() != null) {
            sql += " AND OCC.ID <> :idConfig ";
        }
        Query q = em.createNativeQuery(sql, OCCGrupoMaterial.class);
        q.setParameter("tag", origemocc.getTagOCC().getId());
        q.setParameter("grupoMaterial", ((OCCGrupoMaterial) origemocc).getGrupoMaterial().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (origemocc.getId() != null) {
            q.setParameter("idConfig", origemocc.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (OrigemContaContabil) q.getSingleResult();
        }
        return null;
    }

    public OrigemContaContabil buscarConfiguracaoPorOrigemRecurso(OrigemContaContabil origemcc) {
        String sql = " SELECT OCC.*, O.* ";
        sql += " FROM ORIGEMCONTACONTABIL OCC";
        sql += " INNER JOIN OCCORIGEMRECURSO O ON OCC.ID = O.ID";
        sql += " WHERE OCC.TAGOCC_ID = :tag ";
        sql += " AND O.origemSuplementacaoORC = :origem";
        sql += " AND to_date(:dataVigencia, 'dd/mm/yyyy') between trunc(OCC.INICIOVIGENCIA) AND coalesce(trunc(OCC.FIMVIGENCIA), to_date(:dataVigencia, 'dd/mm/yyyy')) ";
        if (origemcc.getId() != null) {
            sql += " AND OCC.ID <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, OCCOrigemRecurso.class);
        q.setParameter("tag", origemcc.getTagOCC().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("origem", ((OCCOrigemRecurso) origemcc).getOrigemSuplementacaoORC().name());
        if (origemcc.getId() != null) {
            q.setParameter("idConfig", origemcc.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (OrigemContaContabil) q.getSingleResult();
        }
        return null;
    }

    public OrigemContaContabil buscarConfiguracaoPorGrupoBem(OrigemContaContabil origemocc, Date dataVigencia) {
        String sql = "SELECT OCC.*, GB.* ";
        sql += " FROM ORIGEMCONTACONTABIL OCC ";
        sql += " INNER JOIN OCCGRUPOBEM GB ON OCC.ID = GB.ID ";
        sql += " WHERE OCC.TAGOCC_ID = :tag ";
        sql += " AND GB.GRUPOBEM_ID = :grupoBem ";
        sql += " AND GB.tipogrupo = :tipoGrupo ";
        sql += " AND to_date(:dataVigencia, 'dd/mm/yyyy') between trunc(OCC.INICIOVIGENCIA) AND coalesce(trunc(OCC.FIMVIGENCIA), to_date(:dataVigencia, 'dd/mm/yyyy')) ";
        if (origemocc.getId() != null) {
            sql += " AND OCC.ID <> :idConfig ";
        }
        Query q = em.createNativeQuery(sql, OCCGrupoBem.class);
        q.setParameter("tag", origemocc.getTagOCC().getId());
        q.setParameter("grupoBem", ((OCCGrupoBem) origemocc).getGrupoBem().getId());
        q.setParameter("tipoGrupo", ((OCCGrupoBem) origemocc).getTipoGrupo().name());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(dataVigencia));
        if (origemocc.getId() != null) {
            q.setParameter("idConfig", origemocc.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (OrigemContaContabil) q.getSingleResult();
        }
        return null;
    }

    public List<OrigemContaContabil> buscarOrigemContaContabil(Conta conta, Date data) {
        Query consulta = em.createNativeQuery("select occ.* from OrigemContaContabil  occ"
            + " where ("
            + "        occ.contaContabil_id = :conta or"
            + "        occ.contaInterEstado_id = :conta or"
            + "        occ.contaInterMunicipal_id = :conta or"
            + "        occ.contaInterUniao_id = :conta or"
            + "        occ.contaIntra_id = :conta"
            + " ) and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(occ.inicioVigencia) and coalesce(trunc(occ.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) "
            , OrigemContaContabil.class);
        consulta.setParameter("conta", conta.getId());
        consulta.setParameter("dataOperacao", DataUtil.getDataFormatada(data));
        return consulta.getResultList();
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public TipoContaAuxiliarFacade getTipoContaAuxiliarFacade() {
        return tipoContaAuxiliarFacade;
    }
}
