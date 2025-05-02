package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.EventoRedeSim;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.negocios.tributario.services.IntegracaoRedeSimService;
import br.com.webpublico.tributario.dto.EventoRedeSimDTO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Stateless
public class SincronizacaoRedeSimFacade {

    private static final Logger log = LoggerFactory.getLogger(SincronizacaoRedeSimFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    private IntegracaoRedeSimService integracaoRedeSimService;

    public IntegracaoRedeSimService getIntegracaoRedeSimService() {
        if (integracaoRedeSimService == null) {
            integracaoRedeSimService = (IntegracaoRedeSimService) Util.getSpringBeanPeloNome("integracaoRedeSimService");
        }
        return integracaoRedeSimService;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public List<PessoaJuridica> buscarPessoasJuridicasParaSincronizacao(String cmcInicial, String cmcFinal,
                                                                        Date dataInicial, Date dataFinal) {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        String sql = "select pj.id, pj.razaosocial, pj.nomefantasia, pj.cnpj, " +
            "                pj.inscricaoestadual, p.situacaocadastralpessoa " +
            "   from pessoajuridica pj " +
            "  inner join pessoa p on p.id = pj.id " +
            "  inner join enderecocorreio ec on ec.id = p.enderecoprincipal_id " +
            "  left join cadastroeconomico ce on ce.pessoa_id = pj.id " +
            "where p.situacaocadastralpessoa = :ativo " +
            "  and valida_cpf_cnpj(pj.cnpj) = 'S' " +
            "  and lower(trim(ec.localidade)) = lower(trim(:localidade)) " +
            "  and (select situacao.SITUACAOCADASTRAL " +
            "       from SITUACAOCADASTROECONOMICO situacao " +
            "                inner join ce_situacaocadastral ce_situacao " +
            "                           on ce_situacao.situacaocadastroeconomico_id = situacao.id " +
            "       where ce_situacao.cadastroeconomico_id = ce.id " +
            "       order by situacao.ID desc fetch first 1 row only) in (:situacaoCadastro) ";
        if (!Strings.isNullOrEmpty(cmcInicial)) {
            sql += " and ce.inscricaocadastral >= :cmc_inicial ";
        }
        if (!Strings.isNullOrEmpty(cmcFinal)) {
            sql += " and ce.inscricaocadastral <= :cmc_final ";
        }
        if (dataInicial != null) {
            sql += " and trunc(pj.ultimaIntegracaoRedeSim) >= :dataInicial ";
        }
        if (dataFinal != null) {
            sql += " and trunc(pj.ultimaIntegracaoRedeSim) <= :dataFinal ";
        }
        Query query = em.createNativeQuery(sql);
        query.setParameter("ativo", SituacaoCadastralPessoa.ATIVO.name());
        query.setParameter("situacaoCadastro", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(),
            SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name(), SituacaoCadastralCadastroEconomico.INAPTA.name()));
        query.setParameter("localidade", configuracaoTributario.getCidade().getNome());
        if (!Strings.isNullOrEmpty(cmcInicial)) {
            query.setParameter("cmc_inicial", cmcInicial);
        }
        if (!Strings.isNullOrEmpty(cmcFinal)) {
            query.setParameter("cmc_final", cmcFinal);
        }
        if (dataInicial != null) {
            query.setParameter("dataInicial", dataInicial);
        }
        if (dataFinal != null) {
            query.setParameter("dataFinal", dataFinal);
        }
        List<PessoaJuridica> toReturn = Lists.newArrayList();
        List resultList = query.getResultList();
        for (Object[] obj : (List<Object[]>) resultList) {
            PessoaJuridica pessoaJuridica = new PessoaJuridica();
            pessoaJuridica.setId(((BigDecimal) obj[0]).longValue());
            pessoaJuridica.setRazaoSocial((String) obj[1]);
            pessoaJuridica.setNomeFantasia((String) obj[2]);
            pessoaJuridica.setCnpj((String) obj[3]);
            pessoaJuridica.setInscricaoEstadual((String) obj[4]);
            pessoaJuridica.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.valueOf((String) obj[5]));
            toReturn.add(pessoaJuridica);
        }
        return toReturn;
    }

    public Map<Long, SituacaoCadastralCadastroEconomico> buscarSituacaoCmcPeloIdDaPessoa(List<Long> idsPessoa) {
        Map<Long, SituacaoCadastralCadastroEconomico> retorno = Maps.newLinkedHashMap();
        if (idsPessoa == null || idsPessoa.isEmpty()) return retorno;
        String sql = "select * " +
            "from (select cmc.pessoa_id                                            as idpessoa, " +
            "             (select scmc.situacaocadastral " +
            "              from situacaocadastroeconomico scmc " +
            "                       inner join ce_situacaocadastral cesit on scmc.id = cesit.situacaocadastroeconomico_id " +
            "              where cesit.cadastroeconomico_id = cmc.id " +
            "              order by scmc.dataregistro desc fetch first 1 row only) as situacao " +
            "      from cadastroeconomico cmc " +
            "      where cmc.pessoa_id in :idPessoa) " +
            "where situacao in (:situacoes) ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idPessoa", idsPessoa);
        query.setParameter("situacoes", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(),
            SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name(),
            SituacaoCadastralCadastroEconomico.INAPTA.name()));
        List<Object[]> resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                try {
                    retorno.put(((BigDecimal) obj[0]).longValue(), SituacaoCadastralCadastroEconomico.valueOf(obj[1].toString()));
                } catch (Exception ex) {
                    log.error("Não foi possivel adicionar o situação do cmc referente à pessoa {}", ((BigDecimal) obj[0]).longValue(), ex);
                }
            }
        }
        return retorno;
    }

    @Asynchronous
    @TransactionAttribute(value = TransactionAttributeType.NEVER)
    public Future sincronizar(AssistenteBarraProgresso assistente,
                              List<PessoaJuridica> pessoasJuridicas,
                              boolean atualizarEnquadramentoFiscal) {
        if (pessoasJuridicas != null && !pessoasJuridicas.isEmpty()) {
            for (PessoaJuridica pessoaJuridica : pessoasJuridicas) {
                sincronizarPessoaJuridica(assistente.getUsuarioSistema(), assistente.getUsuarioBancoDados(),
                    pessoaJuridica, atualizarEnquadramentoFiscal);
                assistente.conta();
            }
        }
        return new AsyncResult(null);
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public void sincronizarPessoaJuridica(UsuarioSistema usuarioSistema,
                                          String usuarioBancoDados,
                                          PessoaJuridica pessoaJuridica,
                                          boolean atualizarEnquadramentoFiscal) {
        try {
            log.debug("Iniciou sincronizacao da pessoa juridica. {}", pessoaJuridica.getCnpj());
            EventoRedeSimDTO eventoRedeSimDTO = getIntegracaoRedeSimService().getIntegracaoRedeSimFacade()
                .buscarRedeSimPorCnpj(pessoaJuridica.getCnpj(), usuarioBancoDados, true);
            if (eventoRedeSimDTO != null) {
                EventoRedeSim eventoRedeSim = integracaoRedeSimService.getIntegracaoRedeSimFacade().registrarEventoRedeSim(pessoaJuridica, eventoRedeSimDTO);
                integracaoRedeSimService.integrarEventoRedeSim(eventoRedeSim, usuarioSistema, usuarioBancoDados,
                    false, atualizarEnquadramentoFiscal);
            }
            log.debug("Sincronizou pessoa juridica. {}", pessoaJuridica.getCnpj());
        } catch (Exception e) {
            log.debug("Erro ao sincronizar pessoa juridica com a REDESIM.", e);
            log.error("Erro ao sincronizar pessoa juridica com a REDESIM. Erro: {}", e.getMessage());
        }
    }
}
