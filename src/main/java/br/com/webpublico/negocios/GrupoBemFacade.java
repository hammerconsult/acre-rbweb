/*
 * Codigo gerado automaticamente em Thu Jun 16 11:59:46 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Stateless
public class GrupoBemFacade extends AbstractFacade<GrupoBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ConfigDespesaBensFacade configDespesaBensFacade;
    @EJB
    private EfetivacaoAquisicaoFacade efetivacaoAquisicaoFacade;

    public GrupoBemFacade() {
        super(GrupoBem.class);
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoBem getRaiz() {
        GrupoBem grupob = null;
        Query q = em.createQuery("from GrupoBem g where g.superior is null");
        if (q.getResultList().isEmpty()) {
        } else {
            grupob = (GrupoBem) q.getSingleResult();

        }
        return grupob;
    }

    public List<GrupoBem> getFilhosDe(GrupoBem gb) {
        try {
            Query q = em.createQuery("from GrupoBem g where g.superior = :superior");
            q.setParameter("superior", gb);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean verificarCodigoRepetido(GrupoBem selecionado, String codigo) {
        String hql = "select grupo.* from GrupoBem grupo where grupo.codigo = :codigo";
        if (selecionado.getId() != null) {
            hql += " and grupo.id <> :idGrupo";
        }
        Query q = getEntityManager().createNativeQuery(hql, GrupoBem.class);
        q.setParameter("codigo", codigo);
        if (selecionado.getId() != null) {
            q.setParameter("idGrupo", selecionado.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public String geraCodigoNovo() {
        String codigoRetorno;
        String sql = "select max(case when instr(codigo, '.') = 0 then cast(codigo as number) else 0 end) from GrupoBem ";
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

    public String geraCodigoFilho(GrupoBem grupoObjetoCompra) {
        String codigoRetorno;
        String codigoPai = grupoObjetoCompra.getCodigo();

        String hql = "select a from GrupoBem a where a.codigo = (select max(b.codigo) from GrupoBem b where b.superior = :superior)";
        Query q = em.createQuery(hql).setParameter("superior", grupoObjetoCompra);
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            String codigoInteiro = ((GrupoBem) q.getSingleResult()).getCodigo();
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

    public List<Bem> listaBemFiltrandoDescricaoIdentificacao(String parte) {
        String sql = "select b.* "
            + "       from bem b"
            + "      where lower(b.identificacao) like :parte"
            + "         or lower(b.descricao) like :parte";

        Query q = em.createNativeQuery(sql, Bem.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");

        return q.getResultList();
    }

    public List<GrupoBem> buscarGrupoBensMoveisComPontosTipoBemDiferenteDeNaoAplicavelNivel4(String parte) {
        String sql = " select * from grupobem " +
            " where  ((codigo like :parte||'%') or (upper(descricao) like '%'||:parte||'%') ) " +
            " and nivelestrutura(codigo, '.') = 4 " +
            " and (codigo like '01.01.01%' or codigo like '01.01.02%')  ";
        Query q = getEntityManager().createNativeQuery(sql, GrupoBem.class);
        q.setParameter("parte", parte.toUpperCase());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<GrupoBem> buscarGrupoBemPorTipoBem(String parte, TipoBem tipoBem) {
        String sql = "select * from grupobem "
            + "   where (replace(codigo, '.', '') like :parte "
            + "     or codigo like :parte"
            + "     or upper(descricao) like :parte)"
            + "   and tipoBem = :tipo";
        Query q = getEntityManager().createNativeQuery(sql, GrupoBem.class);
        q.setParameter("parte", "%" + parte.toUpperCase().trim() + "%");
        q.setParameter("tipo", tipoBem.name());
        q.setMaxResults(50);

        return q.getResultList();
    }

    public List<GrupoBem> buscarGrupoBemFolhasPorCodigoOrDescricao(String codigoOrDescricao) {
        String sql = "select gb.* from grupobem gb " +
            " where (replace(gb.codigo, '.', '') like :parte or gb.codigo like :parte or upper(gb.descricao) like :parte) " +
            "  and not exists(select 1 from grupobem sgb where sgb.superior_id = gb.id)" +
            "  and gb.ativoinativo = :ativo " +
            " order by codigo ";
        Query q = getEntityManager().createNativeQuery(sql, GrupoBem.class);
        q.setParameter("parte", "%" + codigoOrDescricao.trim().toUpperCase() + "%");
        q.setParameter("ativo", SituacaoCadastralContabil.ATIVO.name());
        return q.getResultList();
    }

    public List<GrupoBem> grupoBemImovel(String parte) {
        String sql = "select * from grupobem "
            + "     where codigo like '01.01.02%' "
            + "     and ((replace(codigo, '.', '') like :parte||'%') "
            + "       or codigo like :parte||'%'"
            + "       or (upper(descricao) like '%'||:parte||'%') )";
        Query q = getEntityManager().createNativeQuery(sql, GrupoBem.class);
        q.setParameter("parte", parte.toUpperCase());
        q.setMaxResults(10);

        return q.getResultList();
    }

    public List<GrupoBem> grupoBemIntangivel(String parte) {
        String sql = "select * from grupobem "
            + "     where codigo like '01.02.01%' "
            + "     and ((replace(codigo, '.', '') like :parte||'%') "
            + "        or codigo like :parte||'%'"
            + "        or (upper(descricao) like '%'||:parte||'%') )";
        Query q = getEntityManager().createNativeQuery(sql, GrupoBem.class);
        q.setParameter("parte", parte.toUpperCase());
        q.setMaxResults(10);

        return q.getResultList();
    }

    public List<GrupoBem> recuperarGrupoBemSemTipoReducao(String parte) {
        String sql = "   select grupo.* "
            + "       from grupobem grupo "
            + "      where not exists (select * " +
            "                             from tiporeducao tipo " +
            "                          where tipo.grupobem_id = grupo.id" +
            "                             and :dataAtual between iniciovigencia and coalesce(fimvigencia, :dataAtual)) "
            + "        and (lower(grupo.codigo) like :param "
            + "         or lower(grupo.descricao) like :param) "
            + "   order by grupo.id desc ";

        Query q = em.createNativeQuery(sql, GrupoBem.class);
        q.setParameter("param", "%" + parte.toLowerCase() + "%");
        q.setParameter("dataAtual", sistemaFacade.getDataOperacao(), TemporalType.DATE);
        return q.getResultList();
    }


    public List<GrupoBem> listaFiltrandoGrupoBemCodigoDescricao(String parte) {
        String sql = " SELECT GB.* FROM GRUPOBEM GB " +
            "          WHERE (LOWER(GB.DESCRICAO) LIKE :parteDesc " +
            "          OR REPLACE(GB.CODIGO,'.','') LIKE :parteCod OR GB.CODIGO LIKE :parteCod) " +
            "          ORDER BY GB.CODIGO";
        Query q = em.createNativeQuery(sql, GrupoBem.class);
        q.setParameter("parteCod", parte.trim().replace(".", "") + "%");
        q.setParameter("parteDesc", "%" + parte.trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<GrupoBem> listaFiltrandoGrupoBemCodigoDescricaoAndTipoBem(String parte, TipoBem tipoBem) {
        String sql = " SELECT GB.* FROM GRUPOBEM GB " +
            "          WHERE GB.TIPOBEM = :tipoBem " +
            "            AND (LOWER(GB.DESCRICAO) LIKE :parteDesc " +
            "                 OR REPLACE(GB.CODIGO, '.','') LIKE :parteCod OR GB.CODIGO LIKE :parteCod) ";
        Query q = em.createNativeQuery(sql, GrupoBem.class);
        q.setParameter("parteCod", parte.trim() + "%");
        q.setParameter("parteDesc", "%" + parte.trim() + "%");
        q.setParameter("tipoBem", tipoBem.name());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<HashMap> recuperaGrupoBemETipoGrupoBemPorContaDespesa(Conta conta, Date data) {

        Preconditions.checkNotNull(conta, " Conta de despesa não encontrada para recuperar o grupo patrimonial.");
        Preconditions.checkNotNull(data, " Data não encontrada para recuperar o grupo patrimonial.");

        String sql = "  select grupobem, tipogrupo " +
            "    from ( select " +
            "        gb.id as grupobem, " +
            "        config.tipogrupobem as tipogrupo " +
            "        from configdespesabens config " +
            "        inner join conta c on c.id = config.contadespesa_id " +
            "        inner join grupobem gb on gb.id = config.grupobem_id " +
            "        where config.iniciovigencia <= to_date(:data, 'dd/MM/yyyy') and (config.fimvigencia >= to_date(:data, 'dd/MM/yyyy') or config.fimvigencia is null) " +
            "        and c.id = :idConta) ";

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
                    Long idGrupoBem = ((BigDecimal) obj[0]).longValue();
                    ret.put(1, (GrupoBem) em.find(GrupoBem.class, idGrupoBem));
                    ret.put(2, TipoGrupo.valueOf((String) obj[1]));
                    retorno.add(ret);
                } catch (Exception e) {
                    throw new ExcecaoNegocioGenerica("Não foi possível  recuperar o Grupo Patrimonial. Contate o suporte. ");
                }
            }
            return retorno;
        }
    }

    public boolean verificarSeExisteVinculosComGrupoBem(GrupoBem grupoBem) {
        String sql = "select grupo.* " +
            "       from grupoobjcompragrupobem associacao " +
            "       inner join grupoobjetocompra grupoobj on grupoobj.id = associacao.grupoobjetocompra_id " +
            "       inner join grupobem grupo on associacao.grupobem_id = grupo.id " +
            "       where grupo.id = :grupobem " +
            "       and current_date between associacao.iniciovigencia and coalesce(associacao.fimvigencia, current_date) " +
            "       UNION  " +
            " select grupo.* from estadobem estado " +
            "      inner join grupobem grupo on estado.GRUPOBEM_ID = grupo.id " +
            "      where grupo.id = :grupobem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("grupobem", grupoBem.getId());
        return q.getResultList().isEmpty();
    }

    public GrupoBem recuperarGrupoPatrimonialAssociacaoGrupoObjetoCompra(ObjetoCompra obj) {
        String sql = "select gb.* from GRUPOBEM gb " +
            "inner join GRUPOOBJCOMPRAGRUPOBEM gocb on gocb.GRUPOBEM_ID = gb.id " +
            "inner join GRUPOOBJETOCOMPRA goc on goc.id = gocb.GRUPOOBJETOCOMPRA_ID " +
            "inner join objetocompra oc on oc.GRUPOOBJETOCOMPRA_ID = goc.id " +
            "where oc.id = :obj " +
            " and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(gocb.iniciovigencia) and coalesce(trunc(gocb.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))";
        Query q = em.createNativeQuery(sql, GrupoBem.class);
        q.setParameter("obj", obj.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        try {
            return (GrupoBem) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            logger.trace(e.toString());
            return null;
        }
    }
}
