/*
 * Codigo gerado automaticamente em Tue Dec 06 15:05:05 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.singletons.CacheTributario;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class DividaFacade extends AbstractFacade<Divida> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Divida divida;
    private ConfiguracaoAcrescimos configuracaoAcrescimos;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private FormaCobrancaFacade formaCobrancaFacade;
    @EJB
    private OpcaoPagamentoFacade opcaoPagamentoFacade;
    @EJB
    private ConfiguracaoAcrescimosFacade configuracaoAcrescimosFacade;
    @EJB
    private ConfiguracaoDAMFacade configuracaoDAMFacade;
    @EJB
    private CacheTributario cacheTributario;

    public DividaFacade() {
        super(Divida.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(Divida entity) {
        super.salvar(entity);
        cacheTributario.init();
    }

    @Override
    public void salvarNovo(Divida entity) {
        super.salvarNovo(entity);
        cacheTributario.init();
    }

    @Override
    public Divida recuperar(Object id) {
        Divida d = em.find(Divida.class, id);
        d.getFormaCobrancaDividas().size();
        d.getOpcaoPagamentosDivida().size();
        d.getAcrescimos().size();
        return d;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public FormaCobrancaFacade getFormaCobrancaFacade() {
        return formaCobrancaFacade;
    }

    public OpcaoPagamentoFacade getOpcaoPagamentoFacade() {
        return opcaoPagamentoFacade;
    }

    public ConfiguracaoAcrescimosFacade getConfiguracaoAcrescimosFacade() {
        return configuracaoAcrescimosFacade;
    }

    public ConfiguracaoDAMFacade getConfiguracaoDAMFacade() {
        return configuracaoDAMFacade;
    }

    public List<Tributo> listarTributosDivida(Divida divida) {
        String hql = " select t from Tributo t where t in (select itvd.tributo from ItemValorDivida itvd left join itvd.valorDivida vd where vd.divida = :divida)";
        Query q = em.createQuery(hql);
        q.setParameter("divida", divida);
        return q.getResultList();
    }

    public List<Divida> listaDividasPorTipoCadastro(TipoCadastroTributario tipoCadastroTributario) {
        String hql = "select dv from Divida dv where dv.tipoCadastro = :tipoCadastro order by dv.descricao";
        Query q = em.createQuery(hql);
        q.setParameter("tipoCadastro", tipoCadastroTributario);
        return q.getResultList();
    }

    public List<Divida> listaDividasAtivas(String parte, TipoCadastroTributario tipo) {
        String hql = "select dv from Divida dv "
            + "  where dv.isDividaAtiva is true"
            + "  and lower(dv.descricao) like :parte";
        if (tipo != null) {
            hql += "  and dv.tipoCadastro = :tipoCadastro";
        }

        Query q = em.createQuery(hql);
        if (tipo != null) {
            q.setParameter("tipoCadastro", tipo);
        }
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");

        return q.getResultList();
    }

    public List<Divida> listaDividasQuePodemSerRevisadas(TipoCadastroTributario tipo) {
        String hql = "select dv from Divida dv "
            + " where dv.divida is not null "
            + " and coalesce(dv.permiteRevisao,0) = 1 ";
        if (tipo != null) {
            hql += " and dv.tipoCadastro = :tipoCadastro";
        }
        Query q = em.createQuery(hql);
        if (tipo != null) {
            q.setParameter("tipoCadastro", tipo);
        }
        return q.getResultList();
    }

    public List<Divida> listaDividasPorTipoCadastro(String parte, TipoCadastroTributario tipo) {
        String hql = "select dv from Divida dv "
            + "  where "
            + "  lower(dv.descricao) like :parte";
        if (tipo != null) {
            hql += "  and dv.tipoCadastro = :tipoCadastro";
        }
        Query q = em.createQuery(hql);
        if (tipo != null) {
            q.setParameter("tipoCadastro", tipo);
        }
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Divida> listaFiltrandoPorTipoDeCadastro(TipoCadastroTributario tipoCadastroTributario, String s, String... atributos) {
        String hql = "select dv from Divida dv "
            + "  inner join dv.tipoCadastro tc "
            + " where tc.tipoCadastroTributario = :tipoCadastro "
            + " and (";

        if (atributos.length != 0) {
            for (String atributo : atributos) {
                hql += "lower(" + atributo + ") like :filtro OR ";
            }
            hql = hql.substring(0, hql.length() - 3) + ")";
        } else {
            hql += "1=1)";
        }

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("tipoCadastro", tipoCadastroTributario);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public ConfiguracaoAcrescimos configuracaoVigente(Long id) {
        if (this.divida == null || !this.divida.getId().equals(id)) {
            this.divida = recuperar(id);
            configuracaoAcrescimos = configuracaoVigente(divida);
        }
        return configuracaoAcrescimos;
    }

    public ConfiguracaoAcrescimos configuracaoVigente(Divida divida) {
        if (this.divida == null || !this.divida.equals(divida)) {
            this.divida = divida;
            Query q = em.createQuery("select c.acrescimo from DividaAcrescimo c where c.divida =:divida and c.inicioVigencia <= CURRENT_DATE and (c.finalVigencia is null or c.finalVigencia >= CURRENT_DATE)");
            q.setParameter("divida", this.divida);
            q.setMaxResults(1);
            if (!q.getResultList().isEmpty()) {
                configuracaoAcrescimos = (ConfiguracaoAcrescimos) q.getResultList().get(0);
            }
        }
        if (configuracaoAcrescimos != null) {
            for (MultaConfiguracaoAcrescimo multaConfiguracaoAcrescimo : configuracaoAcrescimos.getMultas()) {
                multaConfiguracaoAcrescimo.getDiasAtraso().size();
                multaConfiguracaoAcrescimo.getIncidencias().size();
            }
            configuracaoAcrescimos.getHonorarios().size();
            configuracaoAcrescimos.getIncidencias().size();
        }
        return configuracaoAcrescimos;
    }

    public List<Divida> listaDividasDoTipoCadastro(String parte, TipoCadastroTributario tipoCadastroTributario) {
        String sql = " select d.* " +
            "        from divida d" +
            "       where lower(replace(replace(replace(d.descricao,'.',''),'-',''),'/','')) like :parte" +
            "         and tipocadastro = :tipo order by d.codigo ";

        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        q.setParameter("tipo", tipoCadastroTributario.name());

        return q.getResultList();
    }

    public List<Divida> buscarDividasTipoCadastroPorCodigoAndDescricao(String parte, TipoCadastroTributario tipoCadastro) {
        String sql = " select div.* from divida div " +
            " where (lower(replace(replace(replace(div.descricao, '.', ''), '-', ''), '/', '')) " +
            " like :parte or div.codigo like :parte ) " +
            " and div.tipocadastro = :tipo order by div.codigo ";

        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%");
        q.setParameter("tipo", tipoCadastro.name());

        List<Divida> dividas = q.getResultList();

        if(dividas != null && !dividas.isEmpty()) {
            return dividas;
        }
        return Lists.newArrayList();
    }

    public List<Divida> listaDividasDoTipoCadastro(String parte, TipoCadastroTributario tipoCadastroTributario, String idsDesconsiderados) {
        String sql = " select d.* " +
            "        from divida d" +
            "       where lower(descricao) like :parte" +
            "         and tipocadastro = :tipo";
        if (!idsDesconsiderados.isEmpty()) {
            sql += " and d.id not in(" + idsDesconsiderados + ")";
        }

        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipo", tipoCadastroTributario.name());

        return q.getResultList();
    }

    public List<Divida> listaFiltrandoDividas(String s, String... atributos) {

        String sql = "select * from Divida obj where ";
        for (String atributo : atributos) {
            sql += "lower(obj." + atributo.trim() + ") like :filtro OR ";
        }
        sql = sql.substring(0, sql.length() - 3);
        sql += " order by obj.codigo ";
        Query q = getEntityManager().createNativeQuery(sql, Divida.class);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Divida> listaDividasOrdenadoPorDescricao() {
        String sql = "select d.* from divida d order by d.descricao";
        Query q = em.createNativeQuery(sql, Divida.class);
        return q.getResultList();
    }

    public List<Divida> listaDividasDeParcelamentoOrdenadoPorDescricao() {
        String sql = "select d.* from divida d where coalesce(d.isParcelamento,0) = :isParcelamento order by d.descricao";
        Query q = em.createNativeQuery(sql, Divida.class).setParameter("isParcelamento", 1);
        return q.getResultList();
    }

    public List<Divida> buscarDividasDeDividasDiversas(String parte) {
        String sql = " select * from divida d " +
            " where exists(select 1 from tipodividadiversa tdd " +
            "              where tdd.dividacadastroimobiliario_id = d.id or tdd.dividacadastroeconomico_id = d.id or " +
            "              tdd.dividacadastrorural_id = d.id or tdd.dividacontribuintegeral_id = d.id) " +
            " and trim(lower(d.descricao)) like :parte " +
            " order by d.descricao ";
        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Divida> buscarDividas(String parte) {
        String sql = " select * from divida d " +
            " where to_char(d.codigo) like :parte or trim(lower(d.descricao)) like :parte " +
            " order by d.descricao ";
        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Divida> buscarDividasPorEntidadeAndTipoCadastroTributario(Entidade entidade,
                                                                          TipoCadastroTributario tipoCadastroTributario,
                                                                          String parte) {
        String sql = " select * from divida d " +
            " where d.entidade_id = :entidade_id " +
            "   and d.tipocadastro = :tipocadastro" +
            "   and (to_char(d.codigo) like :parte or trim(lower(d.descricao)) like :parte) " +
            " order by d.descricao ";
        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("entidade_id", entidade.getId());
        q.setParameter("tipocadastro", tipoCadastroTributario.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Divida> buscarDividasDeTaxasDiversas(String parte) {
        String sql = " select * from divida d " +
            " where exists(select 1 " +
            "                 from calculotaxasdiversas ctd " +
            "               inner join valordivida vd on vd.calculo_id = ctd.id " +
            "              where vd.divida_id = d.id) " +
            " and trim(lower(d.descricao)) like :parte " +
            " order by d.descricao ";
        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Divida> buscarDividasDeISSQNAndNFSE(String parte) {
        String sql = " select * from divida d " +
            " where (exists(select 1 " +
            "                 from calculoiss ciss " +
            "               inner join valordivida vd on vd.calculo_id = ciss.id " +
            "              where vd.divida_id = d.id) or " +
            "        exists(select 1 " +
            "                 from calculonfse cnfse " +
            "               inner join valordivida vd on vd.calculo_id = cnfse.id " +
            "              where vd.divida_id = d.id))" +
            " and trim(lower(d.descricao)) like :parte " +
            " order by d.descricao ";
        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Divida> buscarDividasDeDividaAtiva(String parte) {
        String sql = " select d.* from divida d " +
            " where d.id in (select distinct vd.divida_id from iteminscricaodividaativa iida " +
            "                inner join valordivida vd on iida.id = vd.calculo_id " +
            "                where vd.divida_id = d.id) " +
            " order by d.descricao ";
        Query q = em.createNativeQuery(sql, Divida.class);

        List<Divida> dividas = q.getResultList();

        return dividas != null ? dividas : Lists.<Divida>newArrayList();
    }

    public List<Divida> buscarDividasDeFiscalizacaoISSQN(String parte) {
        String sql = " select * from divida d " +
            " where (exists(select 1 " +
            "                 from calculofiscalizacao cf " +
            "               inner join valordivida vd on vd.calculo_id = cf.id " +
            "              where vd.divida_id = d.id) or " +
            "        exists(select 1 " +
            "                 from calculomultafiscalizacao cmf " +
            "               inner join valordivida vd on vd.calculo_id = cmf.id " +
            "              where vd.divida_id = d.id)) " +
            " and trim(lower(d.descricao)) like :parte " +
            " order by d.descricao ";
        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Divida> buscarDividasDeFiscalizacaoSecretaria(String parte) {
        String sql = " select * from divida d " +
            " where exists(select 1 " +
            "                 from calculoprocfiscalizacao cpf " +
            "               inner join valordivida vd on vd.calculo_id = cpf.id " +
            "              where vd.divida_id = d.id) " +
            " and trim(lower(d.descricao)) like :parte " +
            " order by d.descricao ";
        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Divida> buscarDividasDeParcelamentoDeDividaAtivaOrdenadoPorDescricao() {
        String sql = "select d.* from divida d " +
            "where coalesce(d.isParcelamento,0) = :isParcelamento " +
            " and coalesce(d.isDividaAtiva,0) = :isDividaAtiva " +
            "order by d.descricao";
        Query q = em.createNativeQuery(sql, Divida.class).setParameter("isParcelamento", 1).setParameter("isDividaAtiva", 1);
        return q.getResultList();
    }

    public List<Long> buscarIdsDividasComParametroFiscalizacao(Exercicio exercicio){
        String sql = " select div.ID from divida div " +
            "  inner join PARAMETROFISCALIZACAODIV pfd on pfd.DIVIDA_ID = div.ID " +
            "  inner join PARAMETROFISCALIZACAO p on p.ID = pfd.PARAMETROFISCALIZACAO_ID " +
            "  where  pfd.DIVIDA_ID is not null and p.EXERCICIO_ID = :idExercicio  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", exercicio.getId());
        if (!q.getResultList().isEmpty()){
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<Divida> buscarDividasDoExercicio(String parte) {
        String sql = " select * from divida d " +
            " where coalesce(d.isdividaativa, 0) = 0 " +
            "   and trim(lower(d.descricao)) like :parte " +
            " order by d.descricao ";
        Query q = em.createNativeQuery(sql, Divida.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

}
