package br.com.webpublico.negocios.rh.administracaodepagamento;

import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.administracaodepagamento.AnaliseConformidadeFP;
import br.com.webpublico.entidades.rh.administracaodepagamento.VinculoAnaliseConformidadeFP;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.administracaopagamento.TipoVinculoAnaliseConformidade;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.Util;
import com.beust.jcommander.internal.Lists;
import org.hibernate.Hibernate;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;


@Stateless
public class AnaliseConformidadeFPFacade extends AbstractFacade<AnaliseConformidadeFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AnaliseConformidadeFPFacade() {
        super(AnaliseConformidadeFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AnaliseConformidadeFP recuperar(Object id) {
        AnaliseConformidadeFP entity = super.recuperar(id);
        Hibernate.initialize(entity.getVinculosAnalise());
        return entity;
    }


    public List<VinculoAnaliseConformidadeFP> buscarVinculos(AnaliseConformidadeFP analise) {
        Object[] obj = buscarQuantidadeVinculosAtivos(analise.getMes(), analise.getAno());
        int quantConcursados = (analise.getPercentualAmostra().multiply((BigDecimal) obj[0])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantCargoComissao = (analise.getPercentualAmostra().multiply((BigDecimal) obj[1])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantAgentePolitico = (analise.getPercentualAmostra().multiply((BigDecimal) obj[2])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantContratoTemporario = (analise.getPercentualAmostra().multiply((BigDecimal) obj[3])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantConselheiroTutelar = (analise.getPercentualAmostra().multiply((BigDecimal) obj[4])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantCargoEletivo = (analise.getPercentualAmostra().multiply((BigDecimal) obj[5])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantPrestadorServico = (analise.getPercentualAmostra().multiply((BigDecimal) obj[6])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantFuncaoCoordenacao = (analise.getPercentualAmostra().multiply((BigDecimal) obj[7])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantAcessoCargoComissao = (analise.getPercentualAmostra().multiply((BigDecimal) obj[8])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantAcessoFuncaoGratificada = (analise.getPercentualAmostra().multiply((BigDecimal) obj[9])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantAposentado = (analise.getPercentualAmostra().multiply((BigDecimal) obj[10])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantPensionista = (analise.getPercentualAmostra().multiply((BigDecimal) obj[11])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        int quantEstagiario = (analise.getPercentualAmostra().multiply((BigDecimal) obj[12])).divide(new BigDecimal(100)).setScale(0, RoundingMode.UP).intValue();
        Boolean buscaTodosVinculos = buscarAnaliseConformidadeFPPorAno(analise.getAno()).size() >= 9;

        List<VinculoAnaliseConformidadeFP> retorno = Lists.newArrayList();
        retorno.addAll(buscarVinculosPorModalidade(analise, analise.getMes(), analise.getAno(), TipoVinculoAnaliseConformidade.CONCURSADO, quantConcursados, buscaTodosVinculos));
        retorno.addAll(buscarVinculosPorModalidade(analise, analise.getMes(), analise.getAno(), TipoVinculoAnaliseConformidade.CARGO_EM_COMISSAO, quantCargoComissao, buscaTodosVinculos));
        retorno.addAll(buscarVinculosPorModalidade(analise, analise.getMes(), analise.getAno(), TipoVinculoAnaliseConformidade.AGENTE_POLITICO, quantAgentePolitico, buscaTodosVinculos));
        retorno.addAll(buscarVinculosPorModalidade(analise, analise.getMes(), analise.getAno(), TipoVinculoAnaliseConformidade.CONTRATO_TEMPORARIO, quantContratoTemporario, buscaTodosVinculos));
        retorno.addAll(buscarVinculosPorModalidade(analise, analise.getMes(), analise.getAno(), TipoVinculoAnaliseConformidade.CONSELHEIRO_TUTELAR, quantConselheiroTutelar, buscaTodosVinculos));
        retorno.addAll(buscarVinculosPorModalidade(analise, analise.getMes(), analise.getAno(), TipoVinculoAnaliseConformidade.CARGO_ELETIVO, quantCargoEletivo, buscaTodosVinculos));
        retorno.addAll(buscarVinculosPorModalidade(analise, analise.getMes(), analise.getAno(), TipoVinculoAnaliseConformidade.PRESTADOR_DE_SERVICO, quantPrestadorServico, buscaTodosVinculos));
        retorno.addAll(buscarVinculosPorModalidade(analise, analise.getMes(), analise.getAno(), TipoVinculoAnaliseConformidade.FUNCAO_DE_COORDENACAO, quantFuncaoCoordenacao, buscaTodosVinculos));
        retorno.addAll(buscarVinculosAposentados(analise, analise.getMes(), analise.getAno(), quantAposentado, buscaTodosVinculos));
        retorno.addAll(buscarVinculosEstaiarios(analise, analise.getMes(), analise.getAno(), quantEstagiario, buscaTodosVinculos));
        retorno.addAll(buscarVinculosPensionistas(analise, analise.getMes(), analise.getAno(), quantPensionista, buscaTodosVinculos));
        retorno.addAll(buscarVinculosFuncaoGratificada(analise, analise.getMes(), analise.getAno(), quantAcessoFuncaoGratificada, buscaTodosVinculos));
        retorno.addAll(buscarVinculosCargoComissao(analise, analise.getMes(), analise.getAno(), quantAcessoCargoComissao, buscaTodosVinculos));
        return retorno;
    }


    public List<VinculoAnaliseConformidadeFP> buscarVinculosPorModalidade(AnaliseConformidadeFP analise, Integer mes, Integer ano,
                                                                          TipoVinculoAnaliseConformidade tipoVinculo, int quantidade, Boolean buscaTodosVinculos) {
        String sql = " select v.id " +
            " from vinculofp v " +
            "         inner join contratofp c on v.ID = c.ID " +
            "         inner join modalidadecontratofp modalidade on c.MODALIDADECONTRATOFP_ID = modalidade.ID " +
            " where modalidade.CODIGO = :modalidade " +
            "  and to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(v.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "    and to_date(to_char(coalesce(v.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy') " +
            "  and v.id not in (select vinculoConf.VINCULOFP_ID " +
            "                   from vinculoanaliseconffp vinculoConf " +
            "                            inner join analiseconformidadefp analise " +
            "                                       on vinculoConf.ANALISECONFORMIDADEFP_ID = analise.ID " +
            "                   where analise.ano = :ano " +
            "                     and vinculoConf.VINCULOFP_ID = v.id) " +
            "  and v.id not in (select fg.CONTRATOFP_ID from FUNCAOGRATIFICADA fg " +
            "                       where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(fg.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                           and to_date(to_char(coalesce(fg.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy') and fg.CONTRATOFP_ID = v.id) " +
            " and v.id not in (select cc.CONTRATOFP_ID from CARGOCONFIANCA cc " +
            "                   where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                       and to_date(to_char(coalesce(cc.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy') and cc.CONTRATOFP_ID = v.id)" +
            " and v.id in (select distinct ficha.VINCULOFP_ID " +
            "                   from fichafinanceirafp ficha " +
            "                   inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                   where  folha.TIPOFOLHADEPAGAMENTO = :tipoFolha " +
            "                       and folha.mes = :mes " +
            "                       and folha.ano = :ano " +
            "                       and ficha.VINCULOFP_ID = v.id)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("modalidade", tipoVinculo.getCodigo());
        q.setParameter("data", Util.criaDataPrimeiroDiaMesJoda(mes, ano).toDate());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL.name());
        List<VinculoAnaliseConformidadeFP> retorno = Lists.newArrayList();
        List<BigDecimal> ids = (List<BigDecimal>) q.getResultList();
        if (ids != null && !ids.isEmpty()) {
            Collections.shuffle(ids);
            if (buscaTodosVinculos) {
                for (BigDecimal id : ids) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, id.longValue()), tipoVinculo));
                }
            } else {
                for (int i = 0; i < quantidade; i++) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, ids.get(i).longValue()), tipoVinculo));
                }
            }
        }
        return retorno;
    }

    public List<VinculoAnaliseConformidadeFP> buscarVinculosAposentados(AnaliseConformidadeFP analise, Integer mes, Integer ano, int quantidade, Boolean buscaTodosVinculos) {
        String sql = "select v.id  " +
            "from vinculofp v  " +
            "inner join aposentadoria apo on apo.ID = v.id  " +
            "where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(v.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy')  " +
            "    and to_date(to_char(coalesce(v.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy')  " +
            "  and v.id not in (select vinculoConf.VINCULOFP_ID  " +
            "                   from vinculoanaliseconffp vinculoConf  " +
            "                            inner join analiseconformidadefp analise  " +
            "                                       on vinculoConf.ANALISECONFORMIDADEFP_ID = analise.ID  " +
            "                   where analise.ano = :ano  " +
            "                     and vinculoConf.VINCULOFP_ID = v.id) " +
            " and v.id in (select distinct ficha.VINCULOFP_ID " +
            "                   from fichafinanceirafp ficha " +
            "                   inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                   where  folha.TIPOFOLHADEPAGAMENTO = :tipoFolha " +
            "                       and folha.mes = :mes " +
            "                       and folha.ano = :ano " +
            "                       and ficha.VINCULOFP_ID = v.id)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", Util.criaDataPrimeiroDiaMesJoda(mes, ano).toDate());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL.name());
        List<VinculoAnaliseConformidadeFP> retorno = Lists.newArrayList();
        List<BigDecimal> ids = (List<BigDecimal>) q.getResultList();
        if (ids != null && !ids.isEmpty()) {
            Collections.shuffle(ids);
            if (buscaTodosVinculos) {
                for (BigDecimal id : ids) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, id.longValue()), TipoVinculoAnaliseConformidade.APOSENTADO));
                }
            } else {
                for (int i = 0; i < quantidade; i++) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, ids.get(i).longValue()), TipoVinculoAnaliseConformidade.APOSENTADO));
                }
            }
        }
        return retorno;
    }


    public List<VinculoAnaliseConformidadeFP> buscarVinculosEstaiarios(AnaliseConformidadeFP analise, Integer mes, Integer ano, int quantidade, Boolean buscaTodosVinculos) {
        String sql = "select v.id  " +
            "from vinculofp v  " +
            "         inner join ESTAGIARIO e on e.id = v.id  " +
            "where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(v.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy')  " +
            "    and to_date(to_char(coalesce(v.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy')  " +
            "  and v.id not in (select vinculoConf.VINCULOFP_ID  " +
            "                   from vinculoanaliseconffp vinculoConf  " +
            "                            inner join analiseconformidadefp analise  " +
            "                                       on vinculoConf.ANALISECONFORMIDADEFP_ID = analise.ID  " +
            "                   where analise.ano = :ano  " +
            "                     and vinculoConf.VINCULOFP_ID = v.id) " +
            " and v.id in (select distinct ficha.VINCULOFP_ID " +
            "                   from fichafinanceirafp ficha " +
            "                   inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                   where  folha.TIPOFOLHADEPAGAMENTO = :tipoFolha " +
            "                       and folha.mes = :mes " +
            "                       and folha.ano = :ano " +
            "                       and ficha.VINCULOFP_ID = v.id)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", Util.criaDataPrimeiroDiaMesJoda(mes, ano).toDate());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL.name());
        List<VinculoAnaliseConformidadeFP> retorno = Lists.newArrayList();
        List<BigDecimal> ids = (List<BigDecimal>) q.getResultList();
        if (ids != null && !ids.isEmpty()) {
            Collections.shuffle(ids);
            if (buscaTodosVinculos) {
                for (BigDecimal id : ids) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, id.longValue()), TipoVinculoAnaliseConformidade.ESTAGIARIO));
                }
            } else {
                for (int i = 0; i < quantidade; i++) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, ids.get(i).longValue()), TipoVinculoAnaliseConformidade.ESTAGIARIO));
                }
            }
        }
        return retorno;
    }

    public List<VinculoAnaliseConformidadeFP> buscarVinculosPensionistas(AnaliseConformidadeFP analise, Integer mes, Integer ano, int quantidade, Boolean buscaTodosVinculos) {
        String sql = "select v.id  " +
            "from vinculofp v  " +
            "         inner join pensionista p on p.id = v.id  " +
            "where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(v.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy')  " +
            "    and to_date(to_char(coalesce(v.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy')  " +
            "  and v.id not in (select vinculoConf.VINCULOFP_ID  " +
            "                   from vinculoanaliseconffp vinculoConf  " +
            "                            inner join analiseconformidadefp analise  " +
            "                                       on vinculoConf.ANALISECONFORMIDADEFP_ID = analise.ID  " +
            "                   where analise.ano = :ano  " +
            "                     and vinculoConf.VINCULOFP_ID = v.id) " +
            " and v.id in (select distinct ficha.VINCULOFP_ID " +
            "                   from fichafinanceirafp ficha " +
            "                   inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                   where  folha.TIPOFOLHADEPAGAMENTO = :tipoFolha " +
            "                       and folha.mes = :mes " +
            "                       and folha.ano = :ano " +
            "                       and ficha.VINCULOFP_ID = v.id)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", Util.criaDataPrimeiroDiaMesJoda(mes, ano).toDate());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL.name());
        List<VinculoAnaliseConformidadeFP> retorno = Lists.newArrayList();
        List<BigDecimal> ids = (List<BigDecimal>) q.getResultList();
        if (ids != null && !ids.isEmpty()) {
            Collections.shuffle(ids);
            if (buscaTodosVinculos) {
                for (BigDecimal id : ids) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, id.longValue()), TipoVinculoAnaliseConformidade.PENSIONISTA));
                }
            } else {
                for (int i = 0; i < quantidade; i++) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, ids.get(i).longValue()), TipoVinculoAnaliseConformidade.PENSIONISTA));
                }
            }
        }
        return retorno;
    }

    public List<VinculoAnaliseConformidadeFP> buscarVinculosFuncaoGratificada(AnaliseConformidadeFP analise, Integer mes, Integer ano, int quantidade, Boolean buscaTodosVinculos) {
        String sql = " select v.id " +
            " from vinculofp v " +
            "         inner join contratofp c on v.ID = c.ID " +
            "         inner join FUNCAOGRATIFICADA func on c.ID = func.CONTRATOFP_ID " +
            " where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(v.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "    and to_date(to_char(coalesce(v.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy') " +
            "  and to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(func.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "    and to_date(to_char(coalesce(func.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy') " +
            "  and v.id not in (select vinculoConf.VINCULOFP_ID " +
            "                   from vinculoanaliseconffp vinculoConf " +
            "                            inner join analiseconformidadefp analise " +
            "                                       on vinculoConf.ANALISECONFORMIDADEFP_ID = analise.ID " +
            "                   where analise.ano = :ano " +
            "                     and vinculoConf.VINCULOFP_ID = v.id) " +
            " and v.id in (select distinct ficha.VINCULOFP_ID " +
            "                   from fichafinanceirafp ficha " +
            "                   inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                   where  folha.TIPOFOLHADEPAGAMENTO = :tipoFolha " +
            "                       and folha.mes = :mes " +
            "                       and folha.ano = :ano " +
            "                       and ficha.VINCULOFP_ID = v.id)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", Util.criaDataPrimeiroDiaMesJoda(mes, ano).toDate());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL.name());
        List<VinculoAnaliseConformidadeFP> retorno = Lists.newArrayList();
        List<BigDecimal> ids = (List<BigDecimal>) q.getResultList();
        if (ids != null && !ids.isEmpty()) {
            Collections.shuffle(ids);
            if (buscaTodosVinculos) {
                for (BigDecimal id : ids) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, id.longValue()), TipoVinculoAnaliseConformidade.ACESSO_FUNCAO_GRATIFICADA));
                }
            } else {
                for (int i = 0; i < quantidade; i++) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, ids.get(i).longValue()), TipoVinculoAnaliseConformidade.ACESSO_FUNCAO_GRATIFICADA));
                }
            }
        }
        return retorno;
    }

    public List<VinculoAnaliseConformidadeFP> buscarVinculosCargoComissao(AnaliseConformidadeFP analise, Integer mes, Integer ano, int quantidade, Boolean buscaTodosVinculos) {
        String sql = "select v.id " +
            "from vinculofp v " +
            "         inner join contratofp c on v.ID = c.ID " +
            "         inner join CARGOCONFIANCA cc on c.ID = cc.CONTRATOFP_ID " +
            " where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(v.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "    and to_date(to_char(coalesce(v.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy') " +
            "  and to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "    and to_date(to_char(coalesce(cc.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy') " +
            "  and v.id not in (select vinculoConf.VINCULOFP_ID " +
            "                   from vinculoanaliseconffp vinculoConf " +
            "                            inner join analiseconformidadefp analise " +
            "                                       on vinculoConf.ANALISECONFORMIDADEFP_ID = analise.ID " +
            "                   where analise.ano = :ano " +
            "                     and vinculoConf.VINCULOFP_ID = v.id) " +
            " and v.id in (select distinct ficha.VINCULOFP_ID " +
            "                   from fichafinanceirafp ficha " +
            "                   inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                   where  folha.TIPOFOLHADEPAGAMENTO = :tipoFolha " +
            "                       and folha.mes = :mes " +
            "                       and folha.ano = :ano " +
            "                       and ficha.VINCULOFP_ID = v.id) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", Util.criaDataPrimeiroDiaMesJoda(mes, ano).toDate());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL.name());
        List<VinculoAnaliseConformidadeFP> retorno = Lists.newArrayList();
        List<BigDecimal> ids = (List<BigDecimal>) q.getResultList();
        if (ids != null && !ids.isEmpty()) {
            Collections.shuffle(ids);
            if (buscaTodosVinculos) {
                for (BigDecimal id : ids) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, id.longValue()), TipoVinculoAnaliseConformidade.ACESSO_CARGO_EM_COMISSAO));
                }
            } else {
                for (int i = 0; i < quantidade; i++) {
                    retorno.add(new VinculoAnaliseConformidadeFP(analise, em.find(VinculoFP.class, ids.get(i).longValue()), TipoVinculoAnaliseConformidade.ACESSO_CARGO_EM_COMISSAO));
                }
            }
        }
        return retorno;
    }

    public Object[] buscarQuantidadeVinculosAtivos(Integer mes, Integer ano) {
        String sql = "select count((select 1 " +
            "              from contratofp c " +
            "                       inner join modalidadecontratofp modalidade on c.MODALIDADECONTRATOFP_ID = modalidade.ID " +
            "              where modalidade.CODIGO = 1 " +
            "                and c.id = vinculo.id " +
            "                and c.id not in (select cc.CONTRATOFP_ID " +
            "                                 from CARGOCONFIANCA cc " +
            "                                 where to_date(to_char( :data, 'mm/yyyy'), 'mm/yyyy') between to_date( " +
            "                                         to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(cc.FINALVIGENCIA,  :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and cc.CONTRATOFP_ID = c.id) " +
            "                and c.id not in (select fg.CONTRATOFP_ID " +
            "                                 from FUNCAOGRATIFICADA fg " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(fg.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(fg.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and fg.CONTRATOFP_ID = c.id))) concursado, " +
            "       count((select 1 " +
            "              from contratofp c " +
            "                       inner join modalidadecontratofp modalidade on c.MODALIDADECONTRATOFP_ID = modalidade.ID " +
            "              where modalidade.CODIGO = 2 " +
            "                and c.id = vinculo.id " +
            "                and c.id not in (select cc.CONTRATOFP_ID " +
            "                                 from CARGOCONFIANCA cc " +
            "                                 where to_date(to_char( :data, 'mm/yyyy'), 'mm/yyyy') between to_date( " +
            "                                         to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(cc.FINALVIGENCIA,  :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and cc.CONTRATOFP_ID = c.id) " +
            "                and c.id not in (select fg.CONTRATOFP_ID " +
            "                                 from FUNCAOGRATIFICADA fg " +
            "                                 where to_date(to_char( :data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(fg.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(fg.FINALVIGENCIA,  :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and fg.CONTRATOFP_ID = c.id))) cargoComissao, " +
            "       count((select 1 " +
            "              from contratofp c " +
            "                       inner join modalidadecontratofp modalidade on c.MODALIDADECONTRATOFP_ID = modalidade.ID " +
            "              where modalidade.CODIGO = 3 " +
            "                and c.id = vinculo.id " +
            "                and c.id not in (select cc.CONTRATOFP_ID " +
            "                                 from CARGOCONFIANCA cc " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date( " +
            "                                         to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(cc.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and cc.CONTRATOFP_ID = c.id) " +
            "                and c.id not in (select fg.CONTRATOFP_ID " +
            "                                 from FUNCAOGRATIFICADA fg " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(fg.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(fg.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and fg.CONTRATOFP_ID = c.id))) AgentePolitico, " +
            "       count((select 1 " +
            "              from contratofp c " +
            "                       inner join modalidadecontratofp modalidade on c.MODALIDADECONTRATOFP_ID = modalidade.ID " +
            "              where modalidade.CODIGO = 4 " +
            "                and c.id = vinculo.id " +
            "                and c.id not in (select cc.CONTRATOFP_ID " +
            "                                 from CARGOCONFIANCA cc " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date( " +
            "                                         to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(cc.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and cc.CONTRATOFP_ID = c.id) " +
            "                and c.id not in (select fg.CONTRATOFP_ID " +
            "                                 from FUNCAOGRATIFICADA fg " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(fg.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(fg.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and fg.CONTRATOFP_ID = c.id))) ContratoTemporario, " +
            "       count((select 1 " +
            "              from contratofp c " +
            "                       inner join modalidadecontratofp modalidade on c.MODALIDADECONTRATOFP_ID = modalidade.ID " +
            "              where modalidade.CODIGO = 6 " +
            "                and c.id = vinculo.id " +
            "                and c.id not in (select cc.CONTRATOFP_ID " +
            "                                 from CARGOCONFIANCA cc " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date( " +
            "                                         to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(cc.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and cc.CONTRATOFP_ID = c.id) " +
            "                and c.id not in (select fg.CONTRATOFP_ID " +
            "                                 from FUNCAOGRATIFICADA fg " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(fg.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(fg.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and fg.CONTRATOFP_ID = c.id))) ConselheiroTutelar, " +
            "       count((select 1 " +
            "              from contratofp c " +
            "                       inner join modalidadecontratofp modalidade on c.MODALIDADECONTRATOFP_ID = modalidade.ID " +
            "              where modalidade.CODIGO = 7 " +
            "                and c.id = vinculo.id " +
            "                and c.id not in (select cc.CONTRATOFP_ID " +
            "                                 from CARGOCONFIANCA cc " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date( " +
            "                                         to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(cc.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and cc.CONTRATOFP_ID = c.id) " +
            "                and c.id not in (select fg.CONTRATOFP_ID " +
            "                                 from FUNCAOGRATIFICADA fg " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(fg.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(fg.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and fg.CONTRATOFP_ID = c.id))) CargoEletivo, " +
            "       count((select 1 " +
            "              from contratofp c " +
            "                       inner join modalidadecontratofp modalidade on c.MODALIDADECONTRATOFP_ID = modalidade.ID " +
            "              where modalidade.CODIGO = 8 " +
            "                and c.id = vinculo.id " +
            "                and c.id not in (select cc.CONTRATOFP_ID " +
            "                                 from CARGOCONFIANCA cc " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date( " +
            "                                         to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(cc.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and cc.CONTRATOFP_ID = c.id) " +
            "                and c.id not in (select fg.CONTRATOFP_ID " +
            "                                 from FUNCAOGRATIFICADA fg " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(fg.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(fg.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and fg.CONTRATOFP_ID = c.id))) PrestadorServico, " +
            "       count((select 1 " +
            "              from contratofp c " +
            "                       inner join modalidadecontratofp modalidade on c.MODALIDADECONTRATOFP_ID = modalidade.ID " +
            "              where modalidade.CODIGO = 9 " +
            "                and c.id = vinculo.id " +
            "                and c.id not in (select cc.CONTRATOFP_ID " +
            "                                 from CARGOCONFIANCA cc " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date( " +
            "                                         to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(cc.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and cc.CONTRATOFP_ID = c.id) " +
            "                and c.id not in (select fg.CONTRATOFP_ID " +
            "                                 from FUNCAOGRATIFICADA fg " +
            "                                 where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(fg.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                                     and to_date(to_char(coalesce(fg.FINALVIGENCIA, :data), 'mm/yyyy'), " +
            "                                                 'mm/yyyy') " +
            "                                   and fg.CONTRATOFP_ID = c.id))) FuncaoCoordenacao, " +
            "       count((select 1 " +
            "              from contratofp c " +
            "                       inner join CARGOCONFIANCA cc on c.ID = cc.CONTRATOFP_ID " +
            "              where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(cc.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                  and to_date(to_char(coalesce(cc.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy') " +
            "                and c.ID = vinculo.id and ROWNUM = 1))                           AcessoCargoComissao, " +
            "       count((select 1 " +
            "              from contratofp c " +
            "                       inner join FUNCAOGRATIFICADA func on c.ID = func.CONTRATOFP_ID " +
            "              where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(func.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "                  and to_date(to_char(coalesce(func.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy') " +
            "                and c.id = vinculo.id and ROWNUM = 1))                           AcessoFuncaoGratificada, " +
            "       count((select 1 " +
            "              from aposentadoria apo " +
            "              where apo.id = vinculo.id))                         Aposentado, " +
            "       count((select 1 " +
            "              from PENSIONISTA pen " +
            "              where pen.id = vinculo.id))                         Pensionista, " +
            "       count((select 1 " +
            "              from ESTAGIARIO e " +
            "              where e.id = vinculo.id))                           Estagiario " +
            "from (select v.id id " +
            "      from vinculofp v " +
            "      where to_date(to_char(:data, 'mm/yyyy'), 'mm/yyyy') between to_date(to_char(v.INICIOVIGENCIA, 'mm/yyyy'), 'mm/yyyy') " +
            "          and to_date(to_char(coalesce(v.FINALVIGENCIA, :data), 'mm/yyyy'), 'mm/yyyy') " +
            "          and v.id in (select distinct ficha.VINCULOFP_ID " +
            "                           from fichafinanceirafp ficha " +
            "                           inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "                           where  folha.TIPOFOLHADEPAGAMENTO = :tipoFolha " +
            "                               and folha.mes = :mes " +
            "                               and folha.ano = :ano " +
            "                               and ficha.VINCULOFP_ID = v.id)) vinculo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", Util.criaDataPrimeiroDiaMesJoda(mes, ano).toDate());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL.name());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (Object[]) resultList.get(0);
        }
        return null;
    }

    public List<AnaliseConformidadeFP> buscarAnaliseConformidadeFPPorAno(Integer ano) {
        Query q = em.createQuery(" from AnaliseConformidadeFP analise " +
            " where analise.ano = : ano ");
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    public AnaliseConformidadeFP buscarAnaliseConformidadeFPPorMesAndAno(Integer mes, Integer ano) {
        Query q = em.createQuery(" from AnaliseConformidadeFP analise " +
            " where analise.ano = : ano " +
            " and analise.mes = :mes ");
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        if (!q.getResultList().isEmpty()) {
            return (AnaliseConformidadeFP) q.getSingleResult();
        }
        return null;
    }

    public void atualizarVinculoAnaliseConformidadeFP(VinculoAnaliseConformidadeFP entity){
        String sql = "update vinculoanaliseconffp set RESPONSAVELAUDITORIA_ID = :responsavel " +
            "where id = :id ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("responsavel", entity.getresponsavelAuditoria().getId());
        q.setParameter("id", entity.getId());

        q.executeUpdate();

    }
}

