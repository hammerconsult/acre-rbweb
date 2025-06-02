package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ProcessoAdministrativoJudicial;
import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.entidades.rh.saudeservidor.RiscoOcupacional;
import br.com.webpublico.entidadesauxiliares.rh.esocial.VWContratoFP;
import br.com.webpublico.enums.FinalidadeCedenciaContratoFP;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.enums.rh.esocial.TipoCessao2231;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.enums.rh.esocial.TipoOperacaoESocial;
import br.com.webpublico.enums.rh.esocial.TipoEventoFPEmpregador;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.esocial.UtilEsocial;
import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Strings;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Stateless
public class RegistroESocialFacade extends AbstractFacade<RegistroESocial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private ESocialService eSocialService;

    @EJB
    private NotificacaoEmailEsocialFacade notificacaoEmailEsocialFacade;

    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    public RegistroESocialFacade() {
        super(RegistroESocial.class);
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public NotificacaoEmailEsocialFacade getNotificacaoEmailEsocialFacade() {
        return notificacaoEmailEsocialFacade;
    }

    @PostConstruct
    public void init() {
        eSocialService = (ESocialService) Util.getSpringBeanPeloNome("eSocialService");
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<EventoESocialDTO> getEventoPorIdentificador(Long id) {
        try {
            return eSocialService.getEventosPorIdEsocial(id.toString());
        } catch (Exception e) {
            logger.debug("Não foi possível buscar o histórico do esocial para o contrato");
            return null;
        }
    }

    public List<Long> recuperarIds() {
        String sql = "select identificador from registroesocial";
        Query q = em.createNativeQuery(sql);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            List<BigDecimal> retorno = resultList;
            List<Long> ids = Lists.newArrayList();
            for (BigDecimal id : retorno) {
                ids.add(id.longValue());
            }
            return ids;
        }
        return null;
    }

    public List<RegistroESocial> buscarRegistroEsocialPorTipoAndIdentificador(TipoArquivoESocial tipoEsocial,
                                                                              String identificador,
                                                                              Integer quantidadeRegistros) {
        String sql = "select * from registroesocial" +
            " where tipoarquivoesocial = :tipoarquivo and idesocial like :id " +
            " and situacao in (:situacao) order by dataintegracao desc";
        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("tipoarquivo", tipoEsocial.name());
        q.setParameter("id", "%" + identificador + "%");
        q.setParameter("situacao", com.google.common.collect.Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
        List resultList = q.getResultList();

        if (quantidadeRegistros != null) {
            q.setMaxResults(quantidadeRegistros);
        }
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public boolean hasEventoEnviado(TipoArquivoESocial tipoEsocial, String
        identificador, ConfiguracaoEmpregadorESocial empregador) {
        String sql = "select * from REGISTROESOCIAL where TIPOARQUIVOESOCIAL = :tipoArquivo ";
        if (!Strings.isNullOrEmpty(identificador)) {
            sql += "and idEsocial like :id ";
        }
        if (empregador != null) {
            sql += "and empregador_id = :empregador ";
        }
        sql += " and situacao in (:situacao) order by dataIntegracao desc";
        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("tipoArquivo", tipoEsocial.name());
        if (!Strings.isNullOrEmpty(identificador)) {
            q.setParameter("id", "%" + identificador);
        }
        if (empregador != null) {
            q.setParameter("empregador", empregador.getId());
        }

        q.setParameter("situacao", com.google.common.collect.Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
        return !q.getResultList().isEmpty();
    }

    public String getMatriculaS2200ProcessadoSucesso(VinculoFP vinculoFP) {
        String matricula = null;
        List<RegistroESocial> registroESocials2200 = buscarRegistroEsocialPorTipoAndIdentificador(TipoArquivoESocial.S2200, vinculoFP.getId().toString(), null);
        if (registroESocials2200 == null) {
            return matricula;
        }
        List<RegistroESocial> s3000 = buscarRegistroEsocialPorTipoAndIdentificador(TipoArquivoESocial.S3000, vinculoFP.getId().toString(), 1);
        String tipoS3000 = null;
        TipoArquivoESocial tipoExclusao = null;
        try {
            if (s3000 != null) {
                tipoS3000 = getConteudoXML(s3000.get(0), "/eSocial/evtExclusao/infoExclusao/tpEvento");
                tipoExclusao = TipoArquivoESocial.valueOf(tipoS3000.replace("-", ""));
            }

        } catch (Exception e) {
            logger.error("Não foi possível ler o tipo de evento de exclusao");
        }
        boolean s3000UltimoComSucesso = false;
        if (s3000 != null && TipoArquivoESocial.S2200.equals(tipoExclusao)) {
            for (RegistroESocial s2200 : registroESocials2200) {
                if ((s3000.get(0).getDataIntegracao().compareTo(s2200.getDataIntegracao()) >= 0)) {
                    s3000UltimoComSucesso = true;
                    break;
                }
            }
        }
        if (s3000UltimoComSucesso) {
            return null;
        }
        try {
            matricula = getConteudoXML(registroESocials2200.get(0), "/eSocial/evtAdmissao/vinculo/matricula");
        } catch (Exception e) {
            logger.error("Não foi possível ler a matricula do registro do esocial");
            return null;
        }
        return matricula;
    }

    public String getConteudoXML(RegistroESocial registroEsocial, String path) throws
        IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        String matricula;
        Document doc = Util.inicializarDOM(registroEsocial.getXml());
        XPath xPath = XPathFactory.newInstance().newXPath();
        matricula = xPath.compile(path).evaluate(doc);
        return matricula;
    }


    public String getIDRegistroConcatenadaComExclusao(Long idEsocial, TipoArquivoESocial tipoArquivoESocial) {
        List<RegistroESocial> registroESocialRecuperado = buscarRegistroEsocialPorTipoAndIdentificador(tipoArquivoESocial, idEsocial.toString(), null);
        if (registroESocialRecuperado == null) {
            return idEsocial.toString();
        }
        List<RegistroESocial> s3000 = buscarRegistroEsocialPorTipoAndIdentificador(TipoArquivoESocial.S3000, idEsocial.toString(), 1);
        String tipoS3000 = null;
        TipoArquivoESocial tipoExclusao = null;
        try {
            if (s3000 != null) {
                tipoS3000 = getConteudoXML(s3000.get(0), "/eSocial/evtExclusao/infoExclusao/tpEvento");
                tipoExclusao = TipoArquivoESocial.valueOf(tipoS3000.replace("-", ""));
            }
        } catch (Exception e) {
            logger.error("Não foi possível ler o tipo de evento de exclusao");
        }
        boolean s3000UltimoComSucesso = false;
        if (s3000 != null && tipoArquivoESocial.equals(tipoExclusao)) {
            for (RegistroESocial s2200 : registroESocialRecuperado) {
                if ((s3000.get(0).getDataIntegracao().compareTo(s2200.getDataIntegracao()) >= 0)) {
                    s3000UltimoComSucesso = true;
                    break;
                }
            }
        }
        if (s3000UltimoComSucesso) {
            return idEsocial.toString().concat("EXCLUSAO:").concat(s3000.get(0).getId().toString());
        }
        return idEsocial.toString();
    }

    public List<EventoFP> buscarEventoFPParaEnvioEsocial(ConfiguracaoEmpregadorESocial config, String
        codigoEventoFP, Boolean somenteNaoEnviados, Date dataReferencia, TipoEventoFPEmpregador tipoEventoFPEmpregador) {
        String sql = "select evento.* from EventoFPEmpregador eventoempregador " +
            " inner join eventofp evento on eventoempregador.EVENTOFP_ID = evento.ID " +
            " where  :dataReferencia between INICIOVIGENCIA and coalesce(FIMVIGENCIA, :dataReferencia) ";

        if (!Strings.isNullOrEmpty(codigoEventoFP)) {
            sql += " and evento.codigo = :codigoEventoFP  ";
        }

        if (tipoEventoFPEmpregador != null) {
            sql += " and tipoEventoFPEmpregador = :tipoEvento";
        }

        if (somenteNaoEnviados) {
            sql += " and not exists (" +
                "    select * from REGISTROESOCIAL registro ";
            if (TipoEventoFPEmpregador.PADRAO.equals(tipoEventoFPEmpregador)) {
                sql += " where registro.IDESOCIAL = TO_CHAR(evento.ID) ";
            } else if (TipoEventoFPEmpregador.ALTERNATIVO.equals(tipoEventoFPEmpregador)) {
                sql += " where (registro.IDESOCIAL = TO_CHAR(evento.ID) || 'D' or registro.IDESOCIAL = TO_CHAR(evento.ID) || 'C') ";
            } else {
                sql += " where (registro.IDESOCIAL = TO_CHAR(evento.ID) || 'DT') ";
            }
            sql += " and registro.TIPOARQUIVOESOCIAL = 'S1010'" +
                " and registro.SITUACAO = :situacao " +
                " and registro.EMPREGADOR_ID = :config) ";
        }
        sql += " and eventoempregador.ENTIDADE_ID = :entidade" +
            " order by to_number(evento.CODIGO)";
        Query q = em.createNativeQuery(sql, EventoFP.class);
        q.setParameter("dataReferencia", dataReferencia);
        if (somenteNaoEnviados) {
            q.setParameter("situacao", SituacaoESocial.PROCESSADO_COM_SUCESSO.name());
            q.setParameter("config", config.getId());
        }
        if (!Strings.isNullOrEmpty(codigoEventoFP)) {
            q.setParameter("codigoEventoFP", codigoEventoFP);
        }
        if (tipoEventoFPEmpregador != null) {
            q.setParameter("tipoEvento", tipoEventoFPEmpregador.name());
        }
        q.setParameter("entidade", config.getEntidade().getId());

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public List<EventoFP> buscarEventoFPParaEnvioEsocialS1010(ConfiguracaoEmpregadorESocial config, String
        codigoEventoFP, Boolean somenteNaoEnviados, Date dataReferencia, TipoOperacaoESocial tipoOperacaoESocial) {
        String sql = "select evento.codigo as codigoEvento, " +
            " case " +
            "           when (select 1 " +
            "                 from registroesocial registro " +
            "                 where registro.tipoarquivoesocial = :tipoarquivo " +
            "                   and registro.identificadorwp like evento.id " +
            "                   and registro.situacao in (:situacoes) " +
            "                   and registro.empregador_id = :config " +
            "                   and rownum = 1 ) is not null then 'ALTERACAO' " +
            "           else 'INCLUSAO' end as tipoOperacao" +
            " from EventoFPEmpregador eventoempregador " +
            "         inner join eventofp evento on eventoempregador.EVENTOFP_ID = evento.ID " +
            " where :dataReferencia between INICIOVIGENCIA and coalesce(FIMVIGENCIA, :dataReferencia) and";

        if (!Strings.isNullOrEmpty(codigoEventoFP)) {
            sql += "  evento.codigo = :codigoEventoFP and ";
        }

        if (somenteNaoEnviados) {
            sql += " not exists (" +
                "    select * from REGISTROESOCIAL registro" +
                "    where registro.IDESOCIAL = evento.ID and" +
                "          registro.TIPOARQUIVOESOCIAL = :tipoarquivo " +
                " and registro.SITUACAO in (:situacoes) " +
                " and registro.EMPREGADOR_ID = :config) AND ";
        }
        sql += " eventoempregador.ENTIDADE_ID = :entidade" +
            " order by to_number(evento.CODIGO)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataReferencia", dataReferencia);
        q.setParameter("config", config.getId());
        q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
        q.setParameter("tipoarquivo", TipoArquivoESocial.S1010.name());
        q.setParameter("entidade", config.getEntidade().getId());
        if (!Strings.isNullOrEmpty(codigoEventoFP)) {
            q.setParameter("codigoEventoFP", codigoEventoFP);
        }

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            List<EventoFP> retorno = Lists.newArrayList();
            for (Object[] obj : (List<Object[]>) resultList) {
                EventoFP eventoFP = eventoFPFacade.recuperaPorCodigo((String) obj[0]);
                eventoFP.setTipoOperacaoESocial(tipoOperacaoESocial != null ? tipoOperacaoESocial : TipoOperacaoESocial.valueOf(String.valueOf(obj[1])));
                retorno.add(eventoFP);
            }
            return retorno;
        }
        return null;
    }


    public List<Cargo> buscarCargoParaEnvioEsocial(Entidade entidade, Boolean somenteNaoEnviados, TipoPCS... tipos) {
        String sql = "select cargo.*" +
            " from cargo" +
            " inner join CARGOEMPREGADORESOCIAL cargoemp on cargo.ID = cargoemp.CARGO_ID" +
            " inner join CONFIGEMPREGADORESOCIAL config on cargoemp.EMPREGADOR_ID = config.ID " +
            " where ";
        if (somenteNaoEnviados) {
            sql += " not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.IDESOCIAL = cargo.ID" +
                "                   and registro.TIPOARQUIVOESOCIAL = 'S1030'" +
                "                   and registro.SITUACAO = :situacao " +
                "    ) and ";
        }
        sql += " config.ENTIDADE_ID = :entidade";
        Query q = em.createNativeQuery(sql, Cargo.class);
        if (somenteNaoEnviados) {
            q.setParameter("situacao", SituacaoESocial.PROCESSADO_COM_SUCESSO.name());
        }
        q.setParameter("entidade", entidade.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public List<HorarioDeTrabalho> buscarHorarioTrabalhoParaEnvioEsocial(Entidade entidade, Boolean
        somenteNaoEnviados, TipoPCS... tipos) {
        String sql = "select horario.*" +
            "from HORARIODETRABALHO horario ";
        if (somenteNaoEnviados) {
            sql += "where not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.IDESOCIAL = horario.ID" +
                "                   and registro.TIPOARQUIVOESOCIAL = 'S1050'" +
                "                   and registro.SITUACAO = :situacao " +
                "    ) ";
        }
        Query q = em.createNativeQuery(sql, HorarioDeTrabalho.class);
        if (somenteNaoEnviados) {
            q.setParameter("situacao", SituacaoESocial.PROCESSADO_COM_SUCESSO.name());
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public List<ProcessoAdministrativoJudicial> buscarProcessoJudicialAdministrativo(Entidade entidade, Boolean
        somenteNaoEnviados, TipoPCS... tipos) {
        String sql = "select processo.* " +
            " from PROCESSOADMJUDICIAL processo " +
            " inner join CONFIGEMPREGADORESOCIAL config on processo.configuracaoEmpregador_id = config.ID " +
            " where ";
        if (somenteNaoEnviados) {
            sql += " not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.IDESOCIAL = processo.ID" +
                "                   and registro.TIPOARQUIVOESOCIAL = 'S1070'" +
                "                   and registro.SITUACAO = :situacao " +
                "    ) and";
        }
        sql += "  config.ENTIDADE_ID = :entidade";
        Query q = em.createNativeQuery(sql, ProcessoAdministrativoJudicial.class);
        q.setParameter("entidade", entidade.getId());

        if (somenteNaoEnviados) {
            q.setParameter("situacao", SituacaoESocial.PROCESSADO_COM_SUCESSO.name());
        }

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public List<VWContratoFP> buscarContratoFPPorTipoArquivo(ConfiguracaoEmpregadorESocial empregadorESocial,
                                                             Boolean somenteNaoEnviados, TipoArquivoESocial tipoArquivoESocial,
                                                             Date inicioContrato, Date fimContrato, HierarquiaOrganizacional ho,
                                                             Date dataSistema) {
        logger.debug("buscando servidores S2200");

        String sql = " select contrato.id, pf.nome, mat.matricula || '/' || vinculo.numero, vinculo.iniciovigencia " +
            " from vinculofp vinculo " +
            " inner join contratofp contrato on vinculo.id = contrato.id " +
            " inner join matriculafp mat on vinculo.matriculafp_id = mat.id " +
            " inner join pessoafisica pf on mat.pessoa_id = pf.id " +
            " where  vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";

        if (inicioContrato != null || fimContrato != null) {
            if (inicioContrato != null) {
                sql += " and vinculo.INICIOVIGENCIA >= :inicioContrato";
            }
            if (fimContrato != null) {
                sql += " and coalesce(vinculo.FINALVIGENCIA, :dataAtual) <= :fimContrato";
            }
        } else {
            sql += " and :dataAtual between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA, :dataAtual)";
        }

        if (somenteNaoEnviados) {
            sql += " and not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.IDESOCIAL like to_char('%' || contrato.id|| '%')" +
                "                   and registro.TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    ) ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("unidades", ho != null ? ho.getSubordinada().getId() : montarIdOrgaoEmpregador(empregadorESocial));
        if (inicioContrato == null || fimContrato != null) {
            q.setParameter("dataAtual", dataSistema);
        }

        if (inicioContrato != null) {
            q.setParameter("inicioContrato", inicioContrato);
        }
        if (fimContrato != null) {
            q.setParameter("fimContrato", fimContrato);
        }

        if (somenteNaoEnviados) {
            q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", tipoArquivoESocial.name());
        }
        List<Object[]> resultado = q.getResultList();
        List<VWContratoFP> contratosFPs = Lists.newArrayList();
        logger.info("Quantidade servidores S2200: " + resultado.size());

        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                VWContratoFP contrato = new VWContratoFP();
                contrato.setId(Long.parseLong(obj[0].toString()));
                contrato.setNome((String) obj[1]);
                contrato.setMatricula((String) obj[2]);
                contrato.setInicioVigencia((Date) obj[3]);
                contratosFPs.add(contrato);
            }

            return contratosFPs;
        }
        return null;
    }

    public List<Aposentadoria> buscarBeneficiarioPorTipoArquivo(ConfiguracaoEmpregadorESocial
                                                                    empregadorESocial, Boolean somenteNaoEnviados, TipoArquivoESocial tipoArquivoESocial) {
        String sql = " select contrato.id from vinculofp vinculo " +
            " inner join aposentadoria contrato on vinculo.id = contrato.id " +
            " where :dataAtual between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA, :dataAtual) " +
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";
        if (somenteNaoEnviados) {
            sql += " and not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.IDESOCIAL like to_char('%' || contrato.id|| '%')" +
                "                   and registro.TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    ) ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        q.setParameter("dataAtual", UtilRH.getDataOperacao());

        if (somenteNaoEnviados) {
            q.setParameter("situacoes", com.google.common.collect.Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", tipoArquivoESocial.name());
        }
        List resultList = q.getResultList();
        List<Aposentadoria> contratosFPs = com.google.common.collect.Lists.newArrayList();

        if (!resultList.isEmpty()) {
            for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
                contratosFPs.add(em.find(Aposentadoria.class, id.longValue()));
            }
            return contratosFPs;
        }
        return null;
    }

    public List<CedenciaContratoFP> buscarCedenciasEsocial(String parte, ConfiguracaoEmpregadorESocial empregadorESocial, Boolean
        somenteNaoEnviados, Date dataInicial, Date dataFinal, TipoCessao2231 tipoCessao2231) {
        String sql = " select cedencia.id " +
            " from cedenciacontratofp cedencia " +
            " inner join vinculofp vinculo on cedencia.contratofp_id = vinculo.id " +
            " inner join matriculafp matricula on vinculo.matriculafp_id = matricula.id " +
            " inner join pessoafisica pf on matricula.pessoa_id = pf.id " +
            " where vinculo.unidadeorganizacional_id in (:unidades) " +
            "  and cedencia.finalidadecedenciacontratofp = :finalidade ";
        if (parte != null) {
            sql += " and ((matricula.matricula like :parte or pf.nome like :parte))";
        }
        if (TipoCessao2231.INI_CESSAO.equals(tipoCessao2231)) {
            sql += " and cedencia.datacessao between :datainicial and :datafinal";
        } else {
            sql += " and exists(select retorno.id " +
                "             from retornocedenciacontratofp retorno " +
                "             where retorno.cedenciacontratofp_id = cedencia.id " +
                "               and retorno.dataretorno between :datainicial and :datafinal)";
        }
        if (somenteNaoEnviados) {
            sql += TipoCessao2231.FIM_CESSAO.equals(tipoCessao2231) ? " and (" : " and ";
            sql += " not exists(select *" +
                "                 from registroesocial registro" +
                "                 where registro.idesocial = cedencia.id" +
                "                   and registro.tipoarquivoesocial = :tipoArquivoESocial " +
                "                   and registro.situacao in (:situacoes) " +
                "    ) ";
            sql += TipoCessao2231.FIM_CESSAO.equals(tipoCessao2231) ? " or cedencia.enviadofimcessaoesocial = 0) " : "";
        }
        Query q = em.createNativeQuery(sql);
        if (parte != null) {
            q.setParameter("parte", "%" + parte.toUpperCase() + "%");
        }
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        q.setParameter("finalidade", FinalidadeCedenciaContratoFP.EXTERNA.name());
        q.setParameter("datainicial", dataInicial);
        q.setParameter("datafinal", dataFinal);

        if (somenteNaoEnviados) {
            q.setParameter("situacoes", com.google.common.collect.Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", TipoArquivoESocial.S2231.name());
        }
        List resultList = q.getResultList();
        List<CedenciaContratoFP> cedencias = com.google.common.collect.Lists.newArrayList();

        if (!resultList.isEmpty()) {
            for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
                cedencias.add(em.find(CedenciaContratoFP.class, id.longValue()));
            }
            return cedencias;
        }
        return null;
    }

    public List<ExoneracaoRescisao> buscarExoneracaoPorEmpregador(ConfiguracaoEmpregadorESocial empregadorESocial,
                                                                  Boolean somenteNaoEnviados,
                                                                  TipoArquivoESocial tipoArquivoESocial, Date dataInicial,
                                                                  Date dataFinal, Date inicioContrato, Date fimContrato,
                                                                  Date dataSistema,
                                                                  HierarquiaOrganizacional hierarquia) {
        String sql = " select exoneracao.* from EXONERACAORESCISAO exoneracao " +
            " inner join vinculofp vinculo on exoneracao.VINCULOFP_ID = vinculo.ID " +
            " inner join contratofp contrato on vinculo.id = contrato.id " +
            " where vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";
        if (dataInicial != null) {
            sql += " and exoneracao.dataRescisao >= :dataInicial";
        }
        if (dataFinal != null) {
            sql += " and exoneracao.dataRescisao <= :dataFinal";
        }
        if (inicioContrato != null || fimContrato != null) {
            if (inicioContrato != null) {
                sql += " and vinculo.INICIOVIGENCIA >= :inicioContrato";
            }
            if (fimContrato != null) {
                sql += " and coalesce(vinculo.FINALVIGENCIA, :dataAtual) <= :fimContrato";
            }
        }
        if (somenteNaoEnviados) {
            sql += " and not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.identificadorWP  = exoneracao.id " +
                "                   and registro.TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    ) ";
        }
        Query q = em.createNativeQuery(sql, ExoneracaoRescisao.class);
        q.setParameter("unidades", hierarquia != null ? hierarquia.getSubordinada() : montarIdOrgaoEmpregador(empregadorESocial));
        if (dataInicial != null) {
            q.setParameter("dataInicial", dataInicial);
        }
        if (dataFinal != null) {
            q.setParameter("dataFinal", dataFinal);
        }

        if (somenteNaoEnviados) {
            q.setParameter("situacoes", com.google.common.collect.Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", tipoArquivoESocial.name());
        }

        if (fimContrato != null) {
            q.setParameter("dataAtual", dataSistema);
            q.setParameter("fimContrato", fimContrato);
        }
        List resultList = q.getResultList();

        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public List<ContratoFP> recuperaContratoTipoPCSEsocial(String parte, ConfiguracaoEmpregadorESocial
        empregadorESocial, HierarquiaOrganizacional hierarquiaOrganizacional) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select distinct new ContratoFP(contrato.id, matricula.matricula||'/'||contrato.numero||' - '||pf.nome) ");
        hql.append(" from ContratoFP contrato ");
        hql.append(" join contrato.matriculaFP matricula ");
        hql.append(" join matricula.pessoa pf " +
            " WHERE  contrato.unidadeOrganizacional in (:unidades) and");
        hql.append(" ((lower(pf.nome) like :filtro) or");
        hql.append(" (lower(pf.cpf) like :filtro) or");
        hql.append(" (lower(matricula.matricula) like :filtro)) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("unidades", hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getSubordinada() : montarOrgaoEmpregador(empregadorESocial));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public List<ExoneracaoRescisao> buscarExoneracaoEsocial(String parte, ConfiguracaoEmpregadorESocial
        empregadorESocial, HierarquiaOrganizacional hierarquia) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select  exoneracao ");
        hql.append(" from ExoneracaoRescisao exoneracao ");
        hql.append(" join exoneracao.vinculoFP contrato ");
        hql.append(" join contrato.matriculaFP matricula ");
        hql.append(" join matricula.pessoa pf " +
            " WHERE  contrato.unidadeOrganizacional in (:unidades) and");
        hql.append(" ((lower(pf.nome) like :filtro) or");
        hql.append(" (lower(pf.cpf) like :filtro) or");
        hql.append(" (lower(matricula.matricula) like :filtro)) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("unidades", hierarquia != null ? hierarquia.getSubordinada() : montarOrgaoEmpregador(empregadorESocial));
        q.setMaxResults(50);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    private List<Long> montarIdOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<Long> ids = com.google.common.collect.Lists.newArrayList();
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            ids.add(item.getUnidadeOrganizacional().getId());
        }
        return ids;
    }

    private List<UnidadeOrganizacional> montarOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<UnidadeOrganizacional> unidades = com.google.common.collect.Lists.newArrayList();
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            unidades.add(item.getUnidadeOrganizacional());
        }
        return unidades;
    }


    public List<ASO> buscarASO(ConfiguracaoEmpregadorESocial empregadorESocial, Boolean
        somenteNaoEnviados, TipoArquivoESocial tipoArquivoESocial) {
        String sql = " select aso.* from aso " +
            "inner join vinculofp vinculo on ASO.CONTRATOFP_ID = vinculo.ID " +
            "inner join matriculafp mat on vinculo.MATRICULAFP_ID = mat.ID " +
            "inner join pessoafisica pf on mat.PESSOA_ID = pf.ID" +
            " where :dataAtual between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA, :dataAtual) " +
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";
        if (somenteNaoEnviados) {
            sql += " and not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.IDESOCIAL like to_char('%' || vinculo.id|| '%')" +
                "                   and registro.TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    ) ";
        }
        Query q = em.createNativeQuery(sql, ASO.class);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        q.setParameter("dataAtual", UtilRH.getDataOperacao());

        if (somenteNaoEnviados) {
            q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", tipoArquivoESocial.name());
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }


    public List<RiscoOcupacional> buscaRiscoOcupacional(ConfiguracaoEmpregadorESocial empregadorESocial, Boolean
        somenteNaoEnviados, TipoArquivoESocial tipoArquivoESocial) {
        String sql = " select risco.* from RISCOOCUPACIONAL risco" +
            " inner join vinculofp vinculo on risco.VINCULOFP_ID = vinculo.ID" +
            " where vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";
        if (somenteNaoEnviados) {
            sql += " and not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.IDESOCIAL like to_char('%' || vinculo.id|| '%')" +
                "                   and registro.TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    ) ";
        }
        Query q = em.createNativeQuery(sql, RiscoOcupacional.class);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        if (somenteNaoEnviados) {
            q.setParameter("situacoes", com.google.common.collect.Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", tipoArquivoESocial.name());
        }

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public List<ContratoFP> buscarContratoFPPorTipoArquivoSemEnvioEsocial(ConfiguracaoEmpregadorESocial
                                                                              empregadorESocial,
                                                                          Boolean somenteNaoEnviados, TipoArquivoESocial tipoArquivoESocial,
                                                                          Date inicioObrigatoriedade
    ) {
        String sql = " select contrato.id from vinculofp vinculo " +
            " inner join contratofp contrato on vinculo.id = contrato.id " +
            " where  vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)" +
            " and coalesce(FINALVIGENCIA, sysdate) >= :inicioObrigatoriedade ";


        if (somenteNaoEnviados) {
            sql += " and not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.IDESOCIAL like to_char('%' || contrato.id|| '%')" +
                "                   and registro.TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    ) ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        q.setParameter("inicioObrigatoriedade", DataUtil.getDataHoraMinutoSegundoZerado(inicioObrigatoriedade));

        if (somenteNaoEnviados) {
            q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", tipoArquivoESocial.name());
        }
        List resultList = q.getResultList();
        List<ContratoFP> contratosFPs = Lists.newArrayList();

        if (!resultList.isEmpty()) {
            for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
                contratosFPs.add(em.find(ContratoFP.class, id.longValue()));
            }
            return contratosFPs;
        }
        return null;
    }

    public List<Afastamento> buscarAfastamentos(ConfiguracaoEmpregadorESocial empregadorESocial, ContratoFP contratoFP,
                                                Boolean somenteNaoEnviados, TipoArquivoESocial tipoArquivoESocial,
                                                Date inicio, Date termino,
                                                Date dataSistema) {
        String sql = " select afastamento.*  " +
            " from afastamento afastamento " +
            " inner join contratofp contrato on afastamento.contratofp_id = contrato.id" +
            " inner join vinculofp vinculo  on vinculo.id = contrato.id " +
            " where  vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";

        if (contratoFP != null) {
            sql += " and contrato.id = :idContrato";
        }
        if (inicio != null || termino != null) {
            if (inicio != null) {
                sql += " and afastamento.inicio >= :inicio";
            }
            if (termino != null) {
                sql += " and coalesce(afastamento.termino, :dataAtual) <= :fim";
            }
        }

        if (somenteNaoEnviados) {
            sql += " and not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.IDENTIFICADORWP = to_char(contrato.id) " +
                "                   and registro.TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    ) ";
        }
        sql += " order by afastamento.inicio";
        Query q = em.createNativeQuery(sql, Afastamento.class);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        if (contratoFP != null) {
            q.setParameter("idContrato", contratoFP.getId());
        }

        if (inicio != null) {
            q.setParameter("inicio", inicio);
        }
        if (termino != null) {
            q.setParameter("fim", termino);
            q.setParameter("dataAtual", dataSistema);
        }

        if (somenteNaoEnviados) {
            q.setParameter("situacoes", com.google.common.collect.Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", tipoArquivoESocial.name());
        }
        List resultList = q.getResultList();

        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    public VinculoFP getVinculoFPFichaFinanceiraFP(String identificadorWP) {
        String sql = "select vinculo.id from fichafinanceirafp ficha " +
            " inner join vinculofp vinculo on ficha.vinculofp_id = vinculo.id " +
            " where ficha.id like :identificadorwp ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("identificadorwp", identificadorWP.split("/")[0]);
        List result = q.getResultList();
        if (result == null || result.isEmpty())
            return null;

        BigDecimal idVinculo = (BigDecimal) result.get(0);
        if (idVinculo != null)
            return em.find(VinculoFP.class, idVinculo.longValue());

        return null;
    }

    public VinculoFP getVinculoFpPorTabelaAndColuna(String identificadorWP, String tabela, String coluna) {
        String sql = "select " + coluna + " from " + tabela +
            " where id like :identificadorwp ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("identificadorwp", identificadorWP);
        List result = q.getResultList();
        if (result == null || result.isEmpty())
            return null;

        BigDecimal idVinculo = (BigDecimal) result.get(0);
        if (idVinculo != null)
            return em.find(VinculoFP.class, idVinculo.longValue());

        return null;
    }

    public PrestadorServicos getPrestadorServicosFichaRPA(String identificadorWP) {
        String sql = " select prestador.* from ficharpa ficha " +
            "                  inner join prestadorservicos prestador on ficha.prestadorservicos_id = prestador.id " +
            " where ficha.id like :identificadorwp ";
        Query q = em.createNativeQuery(sql, PrestadorServicos.class);
        q.setParameter("identificadorwp", identificadorWP);
        List result = q.getResultList();
        if (!result.isEmpty()) {
            return (PrestadorServicos) result.get(0);
        }
        return null;
    }
    public void removerRegistroEsocial(RegistroESocial registroExclusao) {
        try {
            String numeroReciboEventoExcluido = getConteudoXML(registroExclusao, "/eSocial/evtExclusao/infoExclusao/nrRecEvt");
            List<RegistroESocial> registros = getRegistroESocialPorRecibo(numeroReciboEventoExcluido);
            if (registros != null) {
                for (RegistroESocial registro : registros) {
                    em.remove(registro);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar numero do recibo do evento de exclusão S3000. {}", e.getMessage());
            logger.debug("Detalhes do erro ao busca recibo do evento de exclusão S3000. ", e);
        }
    }

    private List<RegistroESocial> getRegistroESocialPorRecibo(String recibo) {
        String sql = "select * from registroesocial where recibo = :recibo ";
        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("recibo", recibo);
        List result = q.getResultList();
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result;
    }

    public List<RegistroESocial> getRegistroEsocialPagamentosSemDescricao() {
        String sql = "select * from registroesocial where descricao is null and tipoarquivoesocial in :tipos " +
            " order by dataintegracao desc  ";
        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("tipos", getTipoArquivoEsocialPagamento());
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<RegistroESocial> getRegistroEsocialComVinculoFP() {
        String sql = "select * from registroesocial where descricao is null and identificadorwp is not null" +
            " and tipoarquivoesocial in :tipos " +
            " order by dataintegracao desc  ";
        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("tipos", getTipoArquivoEsocialVinculoFP());
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<RegistroESocial> getRegistroEsocialEventoFPSemDescricao() {
        String sql = "select * from registroesocial where descricao is null and tipoarquivoesocial = :tipos " +
            " order by dataintegracao desc  ";
        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("tipos", TipoClasseESocial.S1010.name());
        q.setMaxResults(500);
        return q.getResultList();
    }

    private List<String> getTipoArquivoEsocialPagamento() {
        List<String> itens = Lists.newArrayList();
        for (TipoClasseESocial tipoClasseESocial : UtilEsocial.getTipoEventoPagamento()) {
            itens.add(tipoClasseESocial.name());
        }
        return itens;
    }

    private List<String> getTipoArquivoEsocialVinculoFP() {
        List<String> itens = Lists.newArrayList();
        for (TipoClasseESocial tipoClasseESocial : UtilEsocial.getTipoEventoVinculoFP()) {
            itens.add(tipoClasseESocial.name());
        }
        return itens;
    }

    public void atualizarDescricaoRegistroEsocial(RegistroESocial registroESocial) {
        em.createNativeQuery(" update RegistroESocial set descricao = :descricao" +
                "                 where id = :idRegistro ")
            .setParameter("descricao", registroESocial.getDescricao())
            .setParameter("idRegistro", registroESocial.getId())
            .executeUpdate();
    }

    public List<RegistroESocial> buscarRegistroEsocialPorTipoIdentificadorWPEmpregador(TipoArquivoESocial tipoEsocial, String identificador,
                                                                              Integer quantidadeRegistros, ConfiguracaoEmpregadorESocial empregador) {
        String sql = "select * from registroesocial" +
            " where tipoarquivoesocial = :tipoarquivo " +
            " and identificadorwp like :identificador " +
            " and situacao in (:situacao) " +
            " and empregador_id = :empregador" +
            " order by dataintegracao desc";
        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("tipoarquivo", tipoEsocial.name());
        q.setParameter("identificador", "%" + identificador + "%");
        q.setParameter("situacao", com.google.common.collect.Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
        q.setParameter("empregador", empregador.getId());
        List resultList = q.getResultList();

        if (quantidadeRegistros != null) {
            q.setMaxResults(quantidadeRegistros);
        }
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }


    public RegistroESocial getRegistroEsocialPorIdentificadorWP(String identificadorWP) {
        String sql = "select registro.* from registroesocial registro " +
            " where (CASE " +
            "          WHEN INSTR(registro.identificadorwp, '/') > 0 THEN " +
            "              SUBSTR(registro.identificadorwp, 1, INSTR(registro.identificadorwp, '/') - 1) " +
            "          ELSE " +
            "              registro.identificadorwp " +
            "       END) = :identificadorwp";

        Query q = em.createNativeQuery(sql, RegistroESocial.class);
        q.setParameter("identificadorwp", identificadorWP);
        List result = q.getResultList();
        if (result == null || result.isEmpty())
            return null;

        return (RegistroESocial) result.get(0);
    }

    public List<TipoArquivoESocial> getRegistrosEsocialPorIdentificadorWP(String identificadorWP) {
        String sql = "select distinct registro.tipoarquivoesocial " +
            " from registroesocial registro " +
            " where registro.identificadorwp like :identificadorwp" +
            " and registro.situacao in (:situacoes)";

        Query q = em.createNativeQuery(sql);
        q.setParameter("identificadorwp", "%" + identificadorWP + "%");
        q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
        List<String> result = q.getResultList();
        if (result != null || !result.isEmpty()) {
            List<TipoArquivoESocial> tipos = Lists.newArrayList();
            for (String s : result) {
                tipos.add(TipoArquivoESocial.valueOf(s));
            }
            return tipos;
        }
        return Lists.newArrayList();
    }

    public void criarEventoESocial(JsonNode jsonNode) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String cnpjEmpregador = jsonNode.get("empregador").get("infoCadastro").get("nrInsc").asText();
        RegistroESocial registroESocial = new RegistroESocial();
        registroESocial.setSituacao(SituacaoESocial.valueOf(jsonNode.get("situacao").asText()));
        registroESocial.setDataRegistro(format.parse(jsonNode.get("dataRegistro").asText()));
        registroESocial.setEmpregador(null);
        registroESocial.setTipoArquivoESocial(TipoArquivoESocial.valueOf(jsonNode.get("tipoArquivo").asText()));
        registroESocial.setXml(jsonNode.get("xml").asText());
        registroESocial.setIdentificador(jsonNode.get("id").asLong());
        registroESocial.setRecibo(
            jsonNode.get("reciboEntrega") != null && !jsonNode.get("reciboEntrega").asText().equals("null")
                ? jsonNode.get("reciboEntrega").asText()
                : null
        );
        registroESocial.setCodigoResposta(jsonNode.get("codigoResposta").asInt());
        registroESocial.setDescricaoResposta(jsonNode.get("descricaoResposta").asText());
        registroESocial.setOperacao(br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial.valueOf(jsonNode.get("operacao").asText()));
        registroESocial.setDataIntegracao(new Date());
        registroESocial.setMesApuracao(jsonNode.get("mesApur").asInt());
        registroESocial.setAnoApuracao(jsonNode.get("anoApur").asInt());
        registroESocial.setIdentificadorWP(jsonNode.get("identificadorWP").asText());
        JsonNode classeWPNode = jsonNode.get("classeWP");
        if (classeWPNode != null && !classeWPNode.isNull()) {
            String classeWP = classeWPNode.asText();
            try {
                registroESocial.setClasseWP(ClasseWP.valueOf(classeWP));
            } catch (IllegalArgumentException e) {
                logger.error("Valor inválido para ClasseWP: {}", classeWP, e);
            }
        }
        registroESocial.setDescricao(jsonNode.get("descricao").asText());
        if (!Strings.isNullOrEmpty(jsonNode.get("idESocial").asText())) {
            registroESocial.setIdESocial(StringUtil.removerEspacoEmBranco(jsonNode.get("idESocial").asText()));
        }

        ArrayNode ocorrenciasNode = (ArrayNode) jsonNode.get("ocorrencias");

        for (JsonNode ocorrenciaNode : ocorrenciasNode) {
            registroESocial.setObservacao(ocorrenciaNode.get("descricao").asText().concat("<br />"));
            registroESocial.setCodigoOcorrencia(ocorrenciaNode.get("codigo").asInt());
            registroESocial.setLocalizacao(ocorrenciaNode.get("localizacao").asText());
        }

        if (TipoArquivoESocial.S3000.equals(registroESocial.getTipoArquivoESocial()) &&
            SituacaoESocial.PROCESSADO_COM_SUCESSO.equals(registroESocial.getSituacao())) {
            processarEventosS3000(registroESocial);
        }
        salvarRegistroEsocialJDBC(registroESocial, cnpjEmpregador);
    }

    private void processarEventosS3000(RegistroESocial registroExclusao) {
        removerRegistroEsocial(registroExclusao);
    }

    private void salvarRegistroEsocialJDBC(RegistroESocial registroESocial, String cpnj) {
        ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.buscarConfiguracaoEmpregadorPorCNPJ(cpnj);
        configuracaoEmpregadorESocialFacade.getJdbcRegistroESocialDAO().insert(registroESocial, config.getId());
    }
}
