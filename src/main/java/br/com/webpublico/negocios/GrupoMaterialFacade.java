
/*
 * Codigo gerado automaticamente em Thu Jun 16 11:59:46 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.DoctoFiscalLiquidacao;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

@Stateless
public class GrupoMaterialFacade extends AbstractFacade<GrupoMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private ExercicioFacade exercicioFacade;

    public GrupoMaterialFacade() {
        super(GrupoMaterial.class);
    }

    public GrupoMaterial getRaiz() {
        GrupoMaterial grupob = null;
        Query q = em.createQuery("from GrupoMaterial g where g.superior is null");
        if (q.getResultList().isEmpty()) {
        } else {
            grupob = (GrupoMaterial) q.getSingleResult();

        }
        return grupob;
    }

    public List<GrupoMaterial> getFilhosDe(GrupoMaterial gb) {
        try {
            Query q = em.createQuery("from GrupoMaterial g where g.superior = :superior");
            q.setParameter("superior", gb);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }


    public Boolean validaCodigoRepetido(GrupoMaterial selecionado, String codigo) {
        String hql = "select grupo.* from GrupoMaterial grupo where grupo.codigo = :codigo";
        if (selecionado.getId() != null) {
            hql += " and grupo.id <> :idGrupo";
        }
        Query q = getEntityManager().createNativeQuery(hql, GrupoMaterial.class);
        q.setParameter("codigo", codigo);
        if (selecionado.getId() != null) {
            q.setParameter("idGrupo", selecionado.getId());
        }
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public String geraCodigoNovo() {
        String codigoRetorno;
        String sql = "select max(case when instr(codigo, '.') = 0 then cast(codigo as number) else 0 end) as codigo from GrupoMaterial";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            String codigoInteiro = ((BigDecimal) q.getSingleResult()).toString();
            String quebrado[] = codigoInteiro.split("\\.");
            int primeiro = Integer.parseInt(quebrado[0]);
            primeiro++;
            if (primeiro < 10) {
                codigoRetorno = "0" + String.valueOf(primeiro);
            } else {
                codigoRetorno = String.valueOf(primeiro);
            }

        } else {
            codigoRetorno = "01";
        }
        return codigoRetorno;
    }

    public String geraCodigoFilho(GrupoMaterial grupoObjetoCompra) {
        String codigoRetorno;
        String codigoPai = grupoObjetoCompra.getCodigo();

        String hql = "select a from GrupoMaterial a where a.codigo = (select max(b.codigo) from GrupoMaterial b where b.superior = :superior)";
        Query q = em.createQuery(hql).setParameter("superior", grupoObjetoCompra);
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            String codigoInteiro = ((GrupoMaterial) q.getSingleResult()).getCodigo();
            String quebrado[] = codigoInteiro.split("\\.");
            String ultimoConjunto = quebrado[quebrado.length - 1];
            int ultimo = Integer.parseInt(ultimoConjunto);
            ultimo++;

            if (recuperaNivel(codigoInteiro) <= 3) {
                codigoRetorno = StringUtil.cortaOuCompletaEsquerda(ultimo + "", 2, "0");
            } else {
                codigoRetorno = StringUtil.cortaOuCompletaEsquerda(ultimo + "", 4, "0");
            }
        } else {
            if (recuperaNivel(codigoPai) < 3) {
                codigoRetorno = "01";
            } else {
                codigoRetorno = "0001";
            }
        }
        return codigoRetorno;
    }

    public int recuperaNivel(String codigoDoPai) {
        int nivel = 1;
        for (int i = 0; i < codigoDoPai.length(); i++) {
            char c = codigoDoPai.charAt(i);
            if (c == '.') {
                nivel++;
            }
        }
        return nivel;
    }

    public List<GrupoMaterial> getRaizMulti() {
        Query q = em.createQuery("from GrupoMaterial g where g.superior is null");
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<GrupoMaterial> listaFiltrando(String filtro) {
        String sql = " select obj.* from GrupoMaterial obj where (lower(obj.descricao) like :filtro " +
            "   or lower(replace(obj.codigo,'.','')) like :filtro or obj.codigo like :filtro) order by obj.codigo";
        Query q = em.createNativeQuery(sql, GrupoMaterial.class);
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public GrupoMaterial recuperarGrupoMaterialPorCodigo(String codigo) {
        String hql = " from GrupoMaterial gm "
            + "   where gm.codigo = :codigo";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {
            return (GrupoMaterial) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<GrupoMaterial> listaFiltrandoGrupoDeMaterial(String parte) {
        String sql = " SELECT * FROM GRUPOMATERIAL GM "
            + " WHERE (TRIM (REPLACE (GM.CODIGO, '.','')) LIKE :parteCod "
            + " OR LOWER(GM.DESCRICAO) LIKE LOWER (:parteDesc)) ";
        sql += " ORDER BY GM.CODIGO ";
        Query consulta = em.createNativeQuery(sql, GrupoMaterial.class);
        consulta.setParameter("parteCod", "%" + parte.replace(".", "").toLowerCase() + "%");
        consulta.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        consulta.setMaxResults(50);
        return consulta.getResultList();
    }

    public List<GrupoMaterial> listaFiltrandoGrupoDeMaterialOrigemContaContabil(String parte) {
        String sql = " SELECT * FROM GRUPOMATERIAL GM "
            + " WHERE (TRIM (REPLACE (GM.CODIGO, '.','')) LIKE :parteCod "
            + " OR LOWER(GM.DESCRICAO) LIKE LOWER (:parteDesc)) ";
        sql += " ORDER BY GM.CODIGO ";
        Query consulta = em.createNativeQuery(sql, GrupoMaterial.class);
        consulta.setParameter("parteCod", "%" + parte.replace(".", "").toLowerCase() + "%");
        consulta.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        return consulta.getResultList();
    }

    public List<GrupoMaterial> listaFiltrandoGrupoDeMaterialAtivo(String parte) {
        String sql = " SELECT * FROM GRUPOMATERIAL GM "
            + " WHERE (TRIM (REPLACE (GM.CODIGO, '.','')) LIKE :parteCod "
            + " OR LOWER(GM.DESCRICAO) LIKE LOWER (:parteDesc)) "
            + " AND GM.ATIVOINATIVO = 'ATIVO'";
        sql += " ORDER BY GM.CODIGO ";
        Query consulta = em.createNativeQuery(sql, GrupoMaterial.class);
        consulta.setParameter("parteCod", "%" + parte.replace(".", "").toLowerCase() + "%");
        consulta.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        consulta.setMaxResults(50);
        return consulta.getResultList();
    }

    public List<HashMap> recuperaGrupoMateriaETipoEstoquePorContaDespesa(Conta conta, Date data) {

        Preconditions.checkNotNull(conta, " Conta de despesa não encontrada para recuperar o grupo material.");
        Preconditions.checkNotNull(data, " Data não encontrada para recuperar o grupo material.");

        String sql = " select grupomaterial, tipoestoque " +
            "       from ( select " +
            "           gm.id as grupomaterial, " +
            "           config.tipoestoque as tipoestoque " +
            "        from configgrupomaterial config " +
            "        inner join conta c on c.id = config.contadespesa_id " +
            "        inner join grupomaterial gm on gm.id = config.grupomaterial_id " +
            "        where config.iniciovigencia <= to_date(:data, 'dd/mm/yyyy') and (config.fimvigencia >= to_date(:data, 'dd/mm/yyyy') or config.fimvigencia is null) " +
            "        and c.id = :idConta)";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idConta", conta.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<HashMap> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                try {
                    HashMap ret = new HashMap();
                    Long idGrupoMaterial = ((BigDecimal) obj[0]).longValue();
                    ret.put(1, (GrupoMaterial) em.find(GrupoMaterial.class, idGrupoMaterial));
                    ret.put(2, TipoEstoque.valueOf((String) obj[1]));
                    retorno.add(ret);
                } catch (Exception e) {
                    throw new ExcecaoNegocioGenerica("Não foi possível recuperar o Grupo Material. Contate o suporte. ");
                }
            }
            return retorno;
        }
    }

    public Map<String, BigDecimal> recuperarCodigoFormatadoComGrupoMaterial() {
        String sql = "SELECT codigo, id FROM GRUPOMATERIAL ";
        Query q = em.createNativeQuery(sql);

        List<Object[]> resultados = q.getResultList();

        Map<String, BigDecimal> codigosComGrupoMaterialId = new HashMap<>();
        String[] aFormatar;
        StringBuilder codigoFormatado = new StringBuilder();

        for (Object[] resultado : resultados) {
            String codigo = (String) resultado[0];
            BigDecimal grupoMaterialId = (BigDecimal) resultado[1];

            aFormatar = codigo.split(Pattern.quote("."));


            for (String s : aFormatar) {
                codigoFormatado.append(Integer.parseInt(s)).append(".");
            }

            codigosComGrupoMaterialId.put(codigoFormatado.toString(), grupoMaterialId);
            codigoFormatado = new StringBuilder();
        }

        return codigosComGrupoMaterialId;
    }

    public List<String> recuperarCodigoFormatado() {
        String sql = "SELECT codigo FROM GRUPOMATERIAL ";
        Query q = em.createNativeQuery(sql);

        List<String> codigos = q.getResultList();

        String[] aFormatar;
        List<String> codigosFormatados = new ArrayList<>();
        StringBuilder codigoFormatado = new StringBuilder();

        for (String s : codigos) {
            aFormatar = s.split(Pattern.quote("."));

            for (String s1 : aFormatar) {
                codigoFormatado.append(String.valueOf(Integer.valueOf(s1)) + ".");
            }
            codigosFormatados.add(codigoFormatado.toString());
            codigoFormatado = new StringBuilder();
        }
        return codigosFormatados;
    }

    public List<String> recuperarCodigoRaiz() {
        String sql = "SELECT codigo FROM GRUPOMATERIAL WHERE SUPERIOR_ID IS NULL ";
        Query q = em.createNativeQuery(sql);

        return q.getResultList();
    }

    public GrupoMaterial recuperarGrupoMaterialAssociacaoGrupoObjetoCompra(ObjetoCompra obj) {
        String sql = " select gb.* from GRUPOMATERIAL gb " +
            "         inner join ASSOCIACAOGRUOBJCOMGRUMAT gocb on gocb.GRUPOMATERIAL_ID = gb.id " +
            "         inner join GRUPOOBJETOCOMPRA goc on goc.id = gocb.GRUPOOBJETOCOMPRA_ID " +
            "         inner join objetocompra oc on oc.GRUPOOBJETOCOMPRA_ID = goc.id " +
            "         where oc.id = :obj " +
            "         and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(gocb.iniciovigencia) and coalesce(trunc(gocb.finalvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))";
        Query q = em.createNativeQuery(sql, GrupoMaterial.class);
        q.setParameter("obj", obj.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        try {
            return (GrupoMaterial) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            logger.trace(e.toString());
            return null;
        }
    }

    public List<GrupoMaterial> buscarGrupoMaterialAtivoNivelAnalitico(String parte) {
        String sql = " select * from grupomaterial gm "
            + " where (trim (replace (gm.codigo, '.','')) like :parteCod "
            + " or lower(gm.descricao) like lower (:parteDesc) "
            + "   or gm.codigo like :parteCod) "
            + "   and gm.codigo like '01.%'"
            + " and gm.ativoinativo = :ativo "
            + " order by gm.codigo ";
        Query consulta = em.createNativeQuery(sql, GrupoMaterial.class);
        consulta.setParameter("parteCod", "%" + parte.replace(".", "").toLowerCase() + "%");
        consulta.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("ativo", SituacaoCadastralContabil.ATIVO.name());
        consulta.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return consulta.getResultList();
    }

    public List<GrupoMaterial> buscarGrupoMaterialNivelAnalitico(String parte) {
        String sql = " select * from grupomaterial gm "
            + " where (trim (replace (gm.codigo, '.','')) like :parteCod "
            + " or lower(gm.descricao) like lower (:parteDesc) "
            + "   or gm.codigo like :parteCod) "
            + " and gm.ativoinativo = :ativo " +
            "   and gm.superior_id is not null "
            + " order by gm.codigo ";
        Query consulta = em.createNativeQuery(sql, GrupoMaterial.class);
        consulta.setParameter("parteCod", "%" + parte.replace(".", "").toLowerCase() + "%");
        consulta.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("ativo", SituacaoCadastralContabil.ATIVO.name());
        consulta.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return consulta.getResultList();
    }

    public List<GrupoMaterial> buscarGruposMateriaisNivelAnalitico(String parte) {
        String sql = " select * from grupomaterial gm "
            + " where (trim (replace (gm.codigo, '.','')) like :parteCod "
            + " or lower(gm.descricao) like lower (:parteDesc) "
            + "   or gm.codigo like :parteCod) "
            + "   and gm.codigo like '01.%'"
            + " order by gm.codigo ";
        Query consulta = em.createNativeQuery(sql, GrupoMaterial.class);
        consulta.setParameter("parteCod", "%" + parte.replace(".", "").toLowerCase() + "%");
        consulta.setParameter("parteDesc", "%" + parte.toLowerCase() + "%");
        consulta.setMaxResults(50);
        return consulta.getResultList();
    }


    @Override
    public List<GrupoMaterial> lista() {
        Query q = em.createQuery(" from GrupoMaterial gm where gm.ativoInativo = 'ATIVO' order by gm.codigo");
        return q.getResultList();
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }
}

