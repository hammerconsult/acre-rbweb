package br.com.webpublico.negocios;

import br.com.webpublico.entidadesauxiliares.VOAtributo;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class RelatorioCadastroImobiliarioFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private LoteamentoFacade loteamentoFacade;
    @EJB
    private CondominioFacade condominioFacade;
    @EJB
    private ServicoUrbanoFacade servicoUrbanoFacade;
    @EJB
    private CategoriaIsencaoIPTUFacade categoriaIsencaoIPTUFacade;
    @EJB
    private ValorPossivelFacade valorPossivelFacade;

    public RelatorioCadastroImobiliarioFacade() {
    }

    public EntityManager getEm() {
        return em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public LoteamentoFacade getLoteamentoFacade() {
        return loteamentoFacade;
    }

    public CondominioFacade getCondominioFacade() {
        return condominioFacade;
    }

    public ServicoUrbanoFacade getServicoUrbanoFacade() {
        return servicoUrbanoFacade;
    }

    public CategoriaIsencaoIPTUFacade getCategoriaIsencaoIPTUFacade() {
        return categoriaIsencaoIPTUFacade;
    }

    public ValorPossivelFacade getValorPossivelFacade() {
        return valorPossivelFacade;
    }

    public List<VOAtributo> buscarCaracteristicasConstrucao(String parte, String identificacao) {
        String sql = " select distinct vp.id, vp.codigo, vp.valor " +
            " from construcao_valoratributo const_valor" +
            " inner join valoratributo va on va.id = const_valor.atributos_id " +
            " inner join valorpossivel vp on vp.id = va.valordiscreto_id " +
            " inner join construcao const on const.id = const_valor.construcao_id " +
            " inner join atributo atr on const_valor.atributos_key = atr.id " +
            " where atr.identificacao = :identificacao " +
            " and lower(vp.codigo || ' - ' || vp.valor) like :valor " +
            " order by vp.codigo ";

        return preencherVOAtributo(parte, identificacao, sql);
    }

    public List<VOAtributo> buscarCaracteristicasTerreno(String parte, String identificacao) {
        String sql = " select distinct vp.id, vp.codigo, vp.valor " +
            " from lote_valoratributo lote_valor " +
            " inner join atributo atr on lote_valor.atributos_key = atr.id " +
            " inner join valoratributo va on va.id = lote_valor.atributos_id " +
            " inner join valorpossivel vp on atr.id = vp.atributo_id and va.valordiscreto_id = vp.id " +
            " where atr.identificacao = :identificacao " +
            " and lower(vp.codigo || ' - ' || vp.valor) like :valor " +
            " order by vp.codigo ";

        return preencherVOAtributo(parte, identificacao, sql);
    }

    private List<VOAtributo> preencherVOAtributo(String parte, String identificacao, String sql) {
        Query q = em.createNativeQuery(sql);
        q.setParameter("identificacao", identificacao);
        q.setParameter("valor", "%" + parte.toLowerCase().trim() + "%");

        List<Object[]> resultado = q.getResultList();
        List<VOAtributo> atributos = Lists.newArrayList();

        for (Object[] obj : resultado) {
            VOAtributo atributo = new VOAtributo();
            atributo.setId(((BigDecimal) obj[0]).longValue());
            atributo.setCodigo(((BigDecimal) obj[1]).longValue());
            atributo.setValor((String) obj[2]);

            atributos.add(atributo);
        }

        return atributos;
    }

    public List<BigDecimal> aliquotasDisponiveis(String identificacao) {
        String sql = " select distinct (ev.valor * 100) from eventoconfiguradobci " +
            " inner join eventocalculobci ev on eventoconfiguradobci.id = ev.eventocalculo_id " +
            " inner join eventocalculo evcalc on eventoconfiguradobci.eventocalculo_id = evcalc.id " +
            " where lower(evcalc.identificacao) = :identificacao" +
            " and ev.valor > 0 order by (ev.valor * 100) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("identificacao", identificacao.toLowerCase().trim());

        List<BigDecimal> valores = q.getResultList();

        return valores != null ? valores : Lists.<BigDecimal>newArrayList();
    }
}
