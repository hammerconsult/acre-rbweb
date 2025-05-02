
package br.com.webpublico.negocios.rh.integracaoponto;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.EnvioDadosRBPonto;
import br.com.webpublico.entidades.rh.ItemEnvioDadosRBPonto;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.enums.rh.TipoEnvioDadosRBPonto;
import br.com.webpublico.enums.rh.TipoInformacaoEnvioRBPonto;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AfastamentoFacade;
import br.com.webpublico.negocios.ConcessaoFeriasLicencaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoWSRHFacade;
import com.beust.jcommander.internal.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Stateless
public class EnvioDadosRBPontoFacade extends AbstractFacade<EnvioDadosRBPonto> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    @EJB
    private ConfiguracaoWSRHFacade configuracaoWSRHFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EnvioDadosRBPontoFacade() {
        super(EnvioDadosRBPonto.class);
    }

    @Override
    public EnvioDadosRBPonto recuperar(Object id) {
        EnvioDadosRBPonto envioDadosRBPonto = super.recuperar(id);
        Hibernate.initialize(envioDadosRBPonto.getItensEnvioDadosRBPontos());
        return envioDadosRBPonto;
    }


    public Future<EnvioDadosRBPonto> salvarEnviodeDados(EnvioDadosRBPonto entity) {
        entity.setDataEnvio(new Date());
        entity = getEntityManager().merge(entity);
        return integracaoPontoFuture(entity, TipoEnvioDadosRBPonto.ENVIO_MANUAL);
    }


    public List<Afastamento> buscarAfastamentosParaEnvio(ContratoFP contratoFP, Date dataInicial, Date dataFinal, HierarquiaOrganizacional hierarquiaOrganizacional, Boolean apenasNaoEnviados) {
        String sql = "select afas.* " +
            "from afastamento afas " +
            "         inner join contratofp c on afas.CONTRATOFP_ID = c.ID " +
            "         inner join vinculofp v on c.ID = v.ID ";
        if (hierarquiaOrganizacional != null) {
            sql += "         inner join lotacaofuncional lot on v.ID = lot.VINCULOFP_ID " +
                "         inner join vwhierarquiaadministrativa vw on lot.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
                " where :dataAtual between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, :dataAtual) " +
                "  and lot.INICIOVIGENCIA = " +
                "      (select max(lotacao.inicioVigencia) from LotacaoFuncional lotacao where lotacao.VINCULOFP_ID = v.id) " +
                "  and vw.CODIGO like '" + hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%' ";
        }
        if (contratoFP != null) {
            sql += hierarquiaOrganizacional == null ? " where " : " and ";
            sql += " c.id = :contrato ";
        }
        if (dataInicial != null) {
            sql += hierarquiaOrganizacional == null && contratoFP == null ? " where " : " and ";
            sql += "  ((:inicio BETWEEN afas.inicio AND afas.termino) " +
                "    or (:fim BETWEEN afas.inicio AND afas.termino) " +
                "    or (afas.inicio between :inicio and :fim) " +
                "    or (afas.termino between :inicio and :fim)) ";
        }
        if (apenasNaoEnviados) {
            sql += hierarquiaOrganizacional == null && contratoFP == null && dataInicial == null ? " where " : " and ";
            sql += " (afas.TIPOENVIODADOSRBPONTO = :tipoEnvio or afas.TIPOENVIODADOSRBPONTO is null) ";
        }
        Query q = em.createNativeQuery(sql, Afastamento.class);
        if (apenasNaoEnviados) {
            q.setParameter("tipoEnvio", TipoEnvioDadosRBPonto.NAO_ENVIADO.name());
        }
        if (contratoFP != null) {
            q.setParameter("contrato", contratoFP.getId());
        }
        if (hierarquiaOrganizacional != null) {
            q.setParameter("dataAtual", SistemaFacade.getDataCorrente());
        }
        if (dataInicial != null) {
            q.setParameter("inicio", dataInicial);
            q.setParameter("fim", dataFinal != null ? dataFinal : SistemaFacade.getDataCorrente());
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<ConcessaoFeriasLicenca> buscarFeriasParaEnvio(ContratoFP contratoFP, Date dataInicial, Date dataFinal, HierarquiaOrganizacional hierarquiaOrganizacional, Boolean apenasNaoEnviados) {
        String sql = " select concFL.* from ConcessaoFeriasLicenca concFL " +
            "         inner join PeriodoAquisitivoFL periodoAq on periodoAq.id = concFL.periodoaquisitivofl_id " +
            "         inner join basecargo bc on bc.id = periodoAq.basecargo_id " +
            "         inner join baseperiodoaquisitivo bpa on bpa.id = bc.baseperiodoaquisitivo_id " +
            "         inner join vinculofp v on v.id = periodoAq.contratofp_id ";
        if (hierarquiaOrganizacional != null) {
            sql += "      inner join lotacaofuncional lot on v.ID = lot.VINCULOFP_ID " +
                "         inner join vwhierarquiaadministrativa vw on lot.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
                " where :dataAtual between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, :dataAtual) " +
                "  and lot.INICIOVIGENCIA = " +
                "      (select max(lotacao.inicioVigencia) from LotacaoFuncional lotacao where lotacao.VINCULOFP_ID = v.id) " +
                "  and vw.CODIGO like '" + hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%' ";
        }
        sql += hierarquiaOrganizacional != null ? " and " : " where ";
        sql += "bpa.tipoPeriodoAquisitivo = :tipoPeriodoAquisitivo ";
        if (apenasNaoEnviados) {
            sql += " and (concFL.TIPOENVIODADOSRBPONTO = :tipoEnvio or concFL.TIPOENVIODADOSRBPONTO is null) ";
        }
        if (contratoFP != null) {
            sql += " and v.id = :contrato ";
        }
        if (dataInicial != null) {
            sql += " and ((:inicio BETWEEN concFL.DATAINICIAL AND concFL.DATAFINAL) " +
                "    or (:fim BETWEEN concFL.DATAINICIAL AND concFL.DATAFINAL) " +
                "    or (concFL.DATAINICIAL between :inicio and :fim) " +
                "    or (concFL.DATAFINAL between :inicio and :fim)) ";
        }
        Query q = em.createNativeQuery(sql, ConcessaoFeriasLicenca.class);
        q.setParameter("tipoPeriodoAquisitivo", TipoPeriodoAquisitivo.FERIAS.name());
        if (apenasNaoEnviados) {
            q.setParameter("tipoEnvio", TipoEnvioDadosRBPonto.NAO_ENVIADO.name());
        }
        if (contratoFP != null) {
            q.setParameter("contrato", contratoFP.getId());
        }
        if (hierarquiaOrganizacional != null) {
            q.setParameter("dataAtual", SistemaFacade.getDataCorrente());
        }
        if (dataInicial != null) {
            q.setParameter("inicio", dataInicial);
            q.setParameter("fim", dataFinal != null ? dataFinal : SistemaFacade.getDataCorrente());
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<ItemEnvioDadosRBPonto> buscarItensEnvioDadosPontoPorIdentificador(Long id, TipoInformacaoEnvioRBPonto tipo) {
        String hql = " select item from ItemEnvioDadosRBPonto item " +
            " inner join item.envioDadosRBPonto envio " +
            " where item.identificador = :id and envio.tipoInformacaoEnvioRBPonto = :tipo";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("id", id);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }


    public void integracaoPonto(EnvioDadosRBPonto envio, TipoEnvioDadosRBPonto tipoEnvioDadosRBPonto) {
        new EnvioDadosRBPontoExecutor(configuracaoWSRHFacade).execute(envio, tipoEnvioDadosRBPonto);
    }

    public Future<EnvioDadosRBPonto> integracaoPontoFuture(EnvioDadosRBPonto envio, TipoEnvioDadosRBPonto tipoEnvioDadosRBPonto) {
        return new EnvioDadosRBPontoExecutor(configuracaoWSRHFacade).execute(envio, tipoEnvioDadosRBPonto);
    }

    public void atualizarEnvioDados(EnvioDadosRBPonto envioDadosRBPonto) {
        try {
            em.merge(envioDadosRBPonto);
        } catch (Exception e) {
            logger.error("Erro ao atualizar o Envio de dados ao Ponto {}", e.getMessage());
        }
    }

    public void atualizarFeriasAfastamento(ItemEnvioDadosRBPonto item, TipoEnvioDadosRBPonto tipoEnvioDadosRBPonto) {
        if (TipoInformacaoEnvioRBPonto.AFASTAMENTO.equals(item.getEnvioDadosRBPonto().getTipoInformacaoEnvioRBPonto())) {
            if (item.getIdentificador() != null) {
                Afastamento afastamento = afastamentoFacade.recuperar(item.getIdentificador());
                afastamento.setTipoEnvioDadosRBPonto(tipoEnvioDadosRBPonto);
                em.merge(afastamento);
            }
        } else {
            if (item.getIdentificador() != null) {
                ConcessaoFeriasLicenca concessaoFeriasLicenca = concessaoFeriasLicencaFacade.recuperar(item.getIdentificador());
                concessaoFeriasLicenca.setTipoEnvioDadosRBPonto(tipoEnvioDadosRBPonto);
                em.merge(concessaoFeriasLicenca);
            }
        }
    }
}
