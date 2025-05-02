package br.com.webpublico.negocios;

import br.com.webpublico.DateUtils;
import br.com.webpublico.StringUtils;
import br.com.webpublico.entidadesauxiliares.CadastroEconomicoDuplicadoDTO;
import br.com.webpublico.entidadesauxiliares.FiltroUnificacaoCadastroEconomicoLote;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.viewobjects.PessoaDuplicadaDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Stateless
public class UnificacaoCadastroEconomicoLoteFacade implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(UnificacaoCadastroEconomicoLoteFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnificacaoCadastroEconomicoLoteFacade unificacaoCadastroEconomicoLoteFacade;


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<List<CadastroEconomicoDuplicadoDTO>> buscarCadastrosDuplicados(AssistenteBarraProgresso assistenteBarraProgresso,
                                                                                 FiltroUnificacaoCadastroEconomicoLote filtro) {
        List<CadastroEconomicoDuplicadoDTO> cadastrosDuplicados = Lists.newArrayList();
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Buscando Cadastros Econômicos duplicados, essa busca pode ser demorada.");

        String sql = " select   " +
            "    ce.id as id,   " +
            "    ce.abertura,   " +
            "    ce.inscricaocadastral as inscricao, " +
            "    case when pf.id is not null then 'FISICA' else 'JURIDICA' end as tipo,   " +
            "    coalesce(pf.cpf, pj.cnpj) as cpf_cnpj,   " +
            "    coalesce(pf.nome, pj.razaosocial) as nome_razaosocial,   " +
            "    sce.situacaocadastral as situacao, " +
            "    ef.tipoissqn, " +
            "    " + (filtro.getConsultarMovimento() ? "funcexistemovcmc(ce.id)" : "null") + " as movimento " +
            "   from cadastroeconomico ce " +
            "  left join pessoafisica pf on pf.id = ce.pessoa_id " +
            "  left join pessoajuridica pj on pj.id = ce.pessoa_id " +
            "  inner join situacaocadastroeconomico sce on sce.id = (select max(rel.situacaocadastroeconomico_id)" +
            "                                                           from ce_situacaocadastral rel " +
            "                                                        where rel.cadastroeconomico_id = ce.id) " +
            "  left join enquadramentofiscal ef on ef.cadastroeconomico_id = ce.id and ef.fimvigencia is null " +
            " where ce.inscricaocadastral in ( select d_ce.inscricaocadastral " +
            "                                     from cadastroeconomico d_ce " +
            "                                    left join pessoafisica d_pf on d_pf.id = d_ce.pessoa_id " +
            "                                    left join pessoajuridica d_pj on d_pj.id = d_ce.pessoa_id " +
            "                                    left join enquadramentofiscal d_ef on d_ef.cadastroeconomico_id = d_ce.id and d_ef.fimvigencia is null " +
            "                                  where valida_cpf_cnpj(coalesce(d_pf.cpf, d_pj.cnpj)) = 'S' " +
            "                                  group by d_ce.inscricaocadastral  " +
             "                                 having count(1) > 1)  ";
        if (!Strings.isNullOrEmpty(filtro.getInscricaoCadastral())) {
            sql += " and ce.inscricaocadastral = :inscricao_cadastral ";
        }
        if (filtro.getDataCadastro() != null) {
            sql += " and exists (select 1 " +
                "                   from cadastroeconomico s_ce " +
                "                  left join pessoafisica s_pf on s_pf.id = s_ce.pessoa_id " +
                "                  left join pessoajuridica s_pj on s_pj.id = s_ce.pessoa_id " +
                "               where valida_cpf_cnpj(coalesce(s_pf.cpf, s_pj.cnpj)) = 'S' " +
                "                 and s_ce.inscricaocadastral = ce.inscricaocadastral " +
                "                 and s_ce.abertura >= :data_cadastro ) ";
        }
        sql += " order by ce.inscricaocadastral ";
        Query q = em.createNativeQuery(sql);
        if (!Strings.isNullOrEmpty(filtro.getInscricaoCadastral())) {
            q.setParameter("inscricao_cadastral", filtro.getInscricaoCadastral().trim());
        }
        if (filtro.getDataCadastro() != null) {
            q.setParameter("data_cadastro", DateUtils.dataSemHorario(filtro.getDataCadastro()));
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            for (Object[] o : (List<Object[]>) resultList) {
                AtomicInteger index = new AtomicInteger(0);
                CadastroEconomicoDuplicadoDTO dto = new CadastroEconomicoDuplicadoDTO();
                dto.setId(((Number) o[index.getAndIncrement()]).longValue());
                dto.setDataCadastro(((Date) o[index.getAndIncrement()]));
                dto.setInscricaoCadastral((String) o[index.getAndIncrement()]);
                dto.setTipoPessoa(TipoPessoa.valueOf((String) o[index.getAndIncrement()]));
                dto.setCpfCnpj((String) o[index.getAndIncrement()]);
                dto.setNomeRazaoSocial((String) o[index.getAndIncrement()]);
                if (o[index.get()] != null && !Strings.isNullOrEmpty((String) o[index.get()])) {
                    dto.setSituacao(SituacaoCadastralCadastroEconomico.valueOf((String) o[index.get()]));
                }
                index.incrementAndGet();
                if (o[index.get()] != null && !Strings.isNullOrEmpty((String) o[index.get()])) {
                    dto.setTipoIssqn(TipoIssqn.valueOf((String) o[index.get()]));
                }
                index.incrementAndGet();
                if (o[index.get()] != null) {
                    dto.setPossuiMovimento(((Number) o[index.getAndIncrement()]).intValue() == 1);
                }
                cadastrosDuplicados.add(dto);
            }
        }
        return new AsyncResult<>(cadastrosDuplicados);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future unificarCadastrosEconomicosDuplicados(AssistenteBarraProgresso assistenteBarraProgresso,
                                                        List<CadastroEconomicoDuplicadoDTO> cadastrosDuplicados) {
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Preparando unificação dos cadastros econômicos duplicados...");

        Map<CadastroEconomicoDuplicadoDTO, List<CadastroEconomicoDuplicadoDTO>> mapaCadastroDuplicado = montarMapaUnificacao(cadastrosDuplicados);
        List<String> updates = buscarUpdatesTransferenciaCadastro();

        assistenteBarraProgresso.setDescricaoProcesso("Unificando cadastros econômicos duplicados...");
        Set<CadastroEconomicoDuplicadoDTO> cadastrosCorretos = mapaCadastroDuplicado.keySet();
        assistenteBarraProgresso.setTotal(cadastrosCorretos.size());
        for (CadastroEconomicoDuplicadoDTO cadastroCorreto : cadastrosCorretos) {
            try {
                unificacaoCadastroEconomicoLoteFacade.unificarCadastro(updates, cadastroCorreto, mapaCadastroDuplicado.get(cadastroCorreto));
            } catch (Exception e) {
                assistenteBarraProgresso.adicionarLogHtml("Não foi possível unificar os cadastros do CNPJ " + cadastroCorreto.getCpfCnpj() +
                    ". O sistema encontrou restrições para a unificação. Erro " + e.getMessage() + ". ");
                logger.error("Erro ao unificar os cadastros para o cadastro econômico {}. Erro: {}", cadastroCorreto.getId(), e.getMessage());
                logger.debug("Detalhes do erro ao unificar os cadastros para o cadastro econômico {}.", cadastroCorreto.getId(), e);
            }
            assistenteBarraProgresso.conta();
        }
        assistenteBarraProgresso.setDescricaoProcesso("Unificação de cadastro econômico concluída.");
        return new AsyncResult(null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 10)
    public void unificarCadastro(List<String> updates,
                                 CadastroEconomicoDuplicadoDTO cadastroCorreto,
                                 List<CadastroEconomicoDuplicadoDTO> cadastrosIncorretos) {
        transferirMovimentos(updates, cadastroCorreto, cadastrosIncorretos);
        deletarCadastrosIncorretos(cadastrosIncorretos);
    }

    private void deletarCadastrosIncorretos(List<CadastroEconomicoDuplicadoDTO> cadastrosIncorretos) {
        for (CadastroEconomicoDuplicadoDTO cadastroIncorreto : cadastrosIncorretos) {
            deletarCadastroEconomicoIncorreto(cadastroIncorreto);
        }
    }

    private void transferirMovimentos(List<String> updates,
                                      CadastroEconomicoDuplicadoDTO cadastroCorreto,
                                      List<CadastroEconomicoDuplicadoDTO> cadastrosIncorretos) {
        for (String update : updates) {
            for (CadastroEconomicoDuplicadoDTO cadastroIncorreto : cadastrosIncorretos) {
                transferirMovimentos(update, cadastroCorreto, cadastroIncorreto);
            }
        }
    }

    private void deletarCadastroEconomicoIncorreto(CadastroEconomicoDuplicadoDTO cadastroIncorreto) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("proc_deletar_cadastro_economico");
        query.registerStoredProcedureParameter("p_id", Long.class, ParameterMode.IN);
        query.setParameter("p_id", cadastroIncorreto.getId());
        query.execute();
    }

    private void transferirMovimentos(String update,
                                      CadastroEconomicoDuplicadoDTO cadastroCorreto,
                                      CadastroEconomicoDuplicadoDTO cadastroIncorreto) {
        em.createNativeQuery(update)
            .setParameter("cadastro_correto", cadastroCorreto.getId())
            .setParameter("cadastro_incorreto", cadastroIncorreto.getId())
            .executeUpdate();
    }

    private Map<CadastroEconomicoDuplicadoDTO, List<CadastroEconomicoDuplicadoDTO>> montarMapaUnificacao(List<CadastroEconomicoDuplicadoDTO> cadastrosDuplicados) {
        Map<CadastroEconomicoDuplicadoDTO, List<CadastroEconomicoDuplicadoDTO>> mapa = Maps.newHashMap();
        for (CadastroEconomicoDuplicadoDTO cadastroDuplicado : cadastrosDuplicados) {
            if (cadastroDuplicado.getCorreto()) {
                mapa.put(cadastroDuplicado, new ArrayList<>());
            }
        }
        for (CadastroEconomicoDuplicadoDTO cadastroDuplicado : cadastrosDuplicados) {
            if (!cadastroDuplicado.getCorreto()) {
                CadastroEconomicoDuplicadoDTO cadastroCorreto = getCadastroDuplicadoCorreto(mapa, cadastroDuplicado.getInscricaoCadastral());
                if (cadastroCorreto != null) {
                    mapa.get(cadastroCorreto).add(cadastroDuplicado);
                }
            }
        }
        return mapa;
    }

    private CadastroEconomicoDuplicadoDTO getCadastroDuplicadoCorreto(Map<CadastroEconomicoDuplicadoDTO, List<CadastroEconomicoDuplicadoDTO>> mapa, String inscricaoCadastral) {
        for (CadastroEconomicoDuplicadoDTO cadastroDuplicado : mapa.keySet()) {
            if (cadastroDuplicado.getInscricaoCadastral().equals(inscricaoCadastral)) {
                return cadastroDuplicado;
            }
        }
        return null;
    }

    public List<String> buscarUpdatesTransferenciaCadastro() {
        List<String> updates = Lists.newArrayList();
        String sql = "  select cc.TABLE_NAME, cc.COLUMN_NAME   " +
            "    from user_constraints c  " +
            "   inner join user_cons_columns cc on cc.CONSTRAINT_NAME = c.CONSTRAINT_NAME  " +
            "   inner join user_constraints cr on cr.CONSTRAINT_NAME = c.r_CONSTRAINT_NAME  " +
            " where cr.TABLE_NAME IN ('CADASTRO', 'CADASTROECONOMICO')  " +
            "   and cc.TABLE_NAME not in ('CADASTRO', 'CADASTROECONOMICO', 'HISTORICO', 'ECONOMICOCNAE', 'CADASTROECONOMICO_SERVICO',  " +
            "   'CE_VALORATRIBUTOS', 'TIPOPROCESSOECONOMICO', 'CE_SITUACAOCADASTRAL', 'CE_REGISTROJUNTAS', 'SOCIEDADECADASTROECONOMICO',  " +
            "   'CE_ARQUIVOS', 'BCE_CARACFUNCIONAMENTO', 'PLACAAUTOMOVELCMC', 'ISENCAOCADASTROECONOMICO', 'ENDERECOCADASTROECONOMICO',  " +
            "   'ENQUADRAMENTOFISCAL', 'CADASTROECONOMICOHIST', 'ENQUADRAMENTOAMBIENTAL') ";
        Query q = em.createNativeQuery(sql);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                updates.add(" update " + obj[0] + " set " + obj[1] + " = :cadastro_correto where " + obj[1] + " = :cadastro_incorreto ");
            }
        }
        return updates;
    }
}
