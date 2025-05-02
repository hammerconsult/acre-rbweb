/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios.rh.administracaodepagamento;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.administracaodepagamento.FichaFinanceiraFPSimulacao;
import br.com.webpublico.entidades.rh.administracaodepagamento.ItemFichaFinanceiraFPSimulacao;
import br.com.webpublico.entidadesauxiliares.rh.ServidorSalario;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author andre
 */
@Stateless
public class FichaFinanceiraFPSimulacaoFacade extends AbstractFacade<FichaFinanceiraFPSimulacao> {

    private static final Logger logger = LoggerFactory.getLogger(FichaFinanceiraFPSimulacaoFacade.class);
    private static final String NOME_IMAGEM_BRASAO_PREFEITURA = "Brasao_de_Rio_Branco.gif";
    private static final String CODIGO_VENCIMENTO = "1080";

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public FichaFinanceiraFPSimulacaoFacade() {
        super(FichaFinanceiraFPSimulacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    @Override
    public FichaFinanceiraFPSimulacao recuperar(Object id) {
        FichaFinanceiraFPSimulacao ficha = em.find(FichaFinanceiraFPSimulacao.class, id);
        ficha.getItemFichaFinanceiraFPSimulacoes().size();
        return ficha;
    }


    public FichaFinanceiraFPSimulacao recuperarFichaSemItemFicha(Object id) {
        FichaFinanceiraFPSimulacao ficha = em.find(FichaFinanceiraFPSimulacao.class, id);
        return ficha;
    }

    public List<ItemFichaFinanceiraFPSimulacao> recuperarItemFichaFinanceiraSimulacao(Integer mes, Integer ano, VinculoFP vinculoFP, TipoFolhaDePagamento tipoFolhaDePagamento) {
        Query q = em.createNativeQuery(" SELECT item.*, CASE "
            + " WHEN evento.tipoeventofp  = :informativo THEN '3' "
            + " WHEN evento.tipoeventofp  = :desconto THEN '2' "
            + " WHEN evento.tipoeventofp  = :vantagem THEN '1' "
            + " END AS ordenacao "
            + " FROM ITEMFICHASIMULACAO item "
            + " INNER JOIN FichaFinanceiraFPSimulacao ficha ON ficha.id = item.fichaFinanceiraFPSimulacao_id "
            + " INNER JOIN folhadepagamentoSimulacao folha ON folha.id = ficha.folhaDePagamentoSimulacao_id "
            + " INNER JOIN eventofp evento ON evento.id = item.eventofp_id "
            + " WHERE ficha.vinculofp_id = :vinculoFP "
            + " AND folha.mes = :mes AND folha.ano = :ano "
            + " AND folha.tipoFolhaDePagamento = :tipo "
            + " ORDER BY ordenacao, evento.codigo, item.mes, item.ano ", ItemFichaFinanceiraFPSimulacao.class);
        q.setParameter("tipo", tipoFolhaDePagamento.name());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("vantagem", TipoEventoFP.VANTAGEM.name());
        q.setParameter("desconto", TipoEventoFP.DESCONTO.name());
        q.setParameter("informativo", TipoEventoFP.INFORMATIVO.name());
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<ServidorSalario> buscarServidores(Integer mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento, HierarquiaOrganizacional hierarquiaOrganizacional) {
        List<ServidorSalario> vinculosCalculados = buscarVinculosCalculados(mes, ano, tipoFolhaDePagamento, hierarquiaOrganizacional);
        return vinculosCalculados;
    }

    private List<ServidorSalario> buscarVinculosCalculados(Integer mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento, HierarquiaOrganizacional hierarquiaOrganizacional) {
        String sql = "  select vinculofp_id, vw.codigo ||' - '||vw.descricao as orgao, " +
            "            (SELECT coalesce(sum(item1.valor),0)  " +
            "               From FichaFinanceiraFP ficha1  " +
            "               INNER JOIN folhadepagamento folha1 ON folha1.id = ficha1.folhaDePagamento_id    " +
            "               inner join ItemFichaFinanceiraFP item1 on item1.FICHAFINANCEIRAFP_ID = ficha1.id  " +
            "               inner join eventofp e on e.id = item1.EVENTOFP_ID  " +
            "               WHERE item1.mes = :mesNormal AND item1.ano = :ano " +
            "               and ficha1.vinculofp_id = x.vinculofp_id " +
            "               and item1.TIPOEVENTOFP= :vantagem  " +
            "               AND folha1.tipoFolhaDePagamento = :tipo   " +
            "             ) as vencimentoBase, " +
            "            (SELECT coalesce(sum(item1.valor),0)  " +
            "               From FichaFinanceiraFPSimulacao ficha1  " +
            "               INNER JOIN folhadepagamentoSimulacao folha1 ON folha1.id = ficha1.FOLHADEPAGAMENTOSIMULACAO_ID " +
            "               inner join ITEMFICHASIMULACAO item1 on item1.FICHAFINANCEIRAFPSIMULACAO_ID = ficha1.id  " +
            "               inner join eventofp ev on ev.id = item1.EVENTOFP_ID  " +
            "               WHERE folha1.mes = :mes AND folha1.ano = :ano " +
            "               and ficha1.vinculofp_id = x.vinculofp_id " +
            "               and item1.TIPOEVENTOFP= :vantagem  " +
            "             AND folha1.tipoFolhaDePagamento = :tipo ) as vencimentoBaseReajustado " +
            "              " +
            "             from ( " +
            "                          " +
            "               SELECT ficha.vinculofp_id " +
            "             From FichaFinanceiraFP ficha   " +
            "             INNER JOIN folhadepagamento folha ON folha.id = ficha.FOLHADEPAGAMENTO_ID   " +
            "             WHERE folha.mes = :mes AND folha.ano = :ano  " +
            "             AND folha.tipoFolhaDePagamento = :tipo and ficha.id in(select it.fichaFinanceiraFP_id from itemFichaFinanceiraFP it where it.fichaFinanceiraFP_id = ficha.id)  " +
            "              " +
            "             union  " +
            "      SELECT ficha.vinculofp_id " +
            "              " +
            "             From FichaFinanceiraFPSimulacao ficha   " +
            "             INNER JOIN folhadepagamentoSimulacao folha ON folha.id = ficha.folhaDePagamentoSimulacao_id    " +
            "             WHERE folha.mes = :mes AND folha.ano = :ano  " +
            "             AND folha.tipoFolhaDePagamento = :tipo and ficha.id in(select it.fichaFinanceiraFPSimulacao_id from ITEMFICHASIMULACAO it where it.fichaFinanceiraFPSimulacao_id = ficha.id) ) x inner join vinculofp v on v.id = x.vinculofp_id " +
            "             inner join unidadeOrganizacional un on un.id = v.UNIDADEORGANIZACIONAL_ID " +
            "             inner join VwHierarquiaAdministrativa vw on vw.subordinada_id = un.id " +
            "             where :dataOperacao between vw.inicioVigencia and coalesce(vw.fimVigencia, :dataOperacao) ";
        if (hierarquiaOrganizacional != null) {
            sql += " and vw.codigo like :codigo ";
        }
        sql += " order by vw.codigo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", tipoFolhaDePagamento.name());
        q.setParameter("ano", ano);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("vantagem", TipoEventoFP.VANTAGEM.name());
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("mesNormal", Mes.getMesToInt(mes).getNumeroMes());
        if (hierarquiaOrganizacional != null) {
            q.setParameter("codigo", hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%");
        }
        List<ServidorSalario> servidores = Lists.newArrayList();
        List lista = q.getResultList();
        for (Object o : lista) {
            Object[] objeto = (Object[]) o;
            ServidorSalario servidor = criarObjetoServidor(objeto);
            if (servidor != null) {
                servidores.add(servidor);
            }
        }
        return servidores;
    }

    private ServidorSalario criarObjetoServidor(Object[] objeto) {
        ServidorSalario servidorSalario = new ServidorSalario();
        BigDecimal idVinculo = (BigDecimal) objeto[0];
        if (idVinculo != null) {
            VinculoFP vinculoFP = em.find(VinculoFP.class, idVinculo.longValue());
            servidorSalario.setVinculoFP(vinculoFP);
            servidorSalario.setMatricula(vinculoFP.getMatriculaFP().getMatricula());
            servidorSalario.setContrato(vinculoFP.getNumero());
            servidorSalario.setNome(vinculoFP.toString());
            servidorSalario.setCargo(vinculoFP.getCargo() != null ? vinculoFP.getCargo().getDescricao() : " ");
        }
        String unidade = (String) objeto[1];
        if (unidade != null) {
            servidorSalario.setUnidade(unidade);
        }

        BigDecimal valorSemReajuste = (BigDecimal) objeto[2];
        servidorSalario.setSalarioBase(valorSemReajuste);

        BigDecimal valorReajustado = (BigDecimal) objeto[3];
        servidorSalario.setSalarioBaseReajustado(valorReajustado);
        return servidorSalario;
    }

    public List<FichaFinanceiraFPSimulacao> buscarFichasFinanceiraFPSimulacaoPorVinculoFP(VinculoFP vinculoFP) {
        Query q = this.em.createQuery("select ficha from FichaFinanceiraFPSimulacao ficha "
            + "  where ficha.vinculoFP.id = :vinculoFP "
            + "  order by ficha.folhaDePagamentoSimulacao.ano desc, ficha.folhaDePagamentoSimulacao.mes desc");
        q.setParameter("vinculoFP", vinculoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

}
