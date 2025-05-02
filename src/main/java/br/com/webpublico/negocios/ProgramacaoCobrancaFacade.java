/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.ProgramacaoCobrancaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteNotificacaoCobranca;
import br.com.webpublico.entidadesauxiliares.AssistenteProgramacaoCobranca;
import br.com.webpublico.entidadesauxiliares.ProgramacaoCobrancaVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.tributario.TipoDamEmitido;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.singletons.SingletonGeradorCodigoPorExercicio;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ProgramacaoCobrancaFacade extends AbstractFacade<ProgramacaoCobranca> {

    @EJB
    ParametroCobrancaAdministrativaFacade parametroCobrancaAdministrativaFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ServicoFacade servicoFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private SetorFacade setorFacade;
    @EJB
    private QuadraFacade quadraFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private SingletonGeradorCodigoPorExercicio singletonGeradorCodigoPorExercicio;

    public ProgramacaoCobrancaFacade() {
        super(ProgramacaoCobranca.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public ServicoFacade getServicoFacade() {
        return servicoFacade;
    }

    public CNAEFacade getCnaeFacade() {
        return cnaeFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public SetorFacade getSetorFacade() {
        return setorFacade;
    }

    public QuadraFacade getQuadraFacade() {
        return quadraFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    @Override
    public ProgramacaoCobranca recuperar(Object id) {
        ProgramacaoCobranca programacao = em.find(ProgramacaoCobranca.class, id);
        if (programacao != null) {
            Hibernate.initialize(programacao.getListaItemProgramacaoCobrancas());
            Hibernate.initialize(programacao.getListaSituacaoProgramacaoCobrancas());
            Hibernate.initialize(programacao.getCnaes());
            Hibernate.initialize(programacao.getServicos());
            Hibernate.initialize(programacao.getBairros());
            Hibernate.initialize(programacao.getLogradouros());
            Hibernate.initialize(programacao.getDividas());
            Hibernate.initialize(programacao.getAtividades());
            Hibernate.initialize(programacao.getContribuintes());
        }
        return programacao;
    }

    public ProgramacaoCobranca recuperarSimples(Object id) {
        ProgramacaoCobranca p = em.find(ProgramacaoCobranca.class, id);
        if (p != null) {
            p.getListaItemProgramacaoCobrancas().size();
            p.getListaSituacaoProgramacaoCobrancas().size();
        }
        return p;
    }

    public ProgramacaoCobranca salvarProgramacao(ProgramacaoCobranca programacao) {
        if (programacao.getCodigo() == null && programacao.getExercicio() != null) {
            programacao.setCodigo(buscarProximoCodigoPorExercicio(programacao.getExercicio()));
            programacao.setNumero(programacao.getCodigo() + "/" + programacao.getExercicio().getAno());
        }
        return em.merge(programacao);
    }

    public String ultimoNumeroMaisUm() {
        Query q = em.createNativeQuery("select coalesce(max(codigo), 0) + 1 as numero from programacaocobranca");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        String format = resultado.toString();
        if (resultado.equals(BigDecimal.ONE)) {
            format = sistemaFacade.getExercicioCorrente().getAno() + String.format("%05d", resultado);
        }
        return format;
    }

    public void geraDocumento(AssistenteNotificacaoCobranca assistente, ItemNotificacao item, TipoAcaoCobranca tipoAcaoCobranca) throws Exception {
        TipoDoctoOficial tipo = recuperaTipoDocumentoPorAcaoCobranca(item.getCadastro(), item.getContribuinte(), assistente.getExercicio(), tipoAcaoCobranca);
        if (tipo != null) {
            DocumentoOficial documentoOficial = documentoOficialFacade.geraDocumentoCobrancaAdministrativa(
                assistente,
                item,
                item.getDocumentoNotificacao() != null ? item.getDocumentoNotificacao().getDocumentoOficial() : null,
                tipo);
            if (item.getDocumentoNotificacao() == null) {
                item.setDocumentoNotificacao(new DocumentoNotificacao());
            }
            item.getDocumentoNotificacao().setDocumentoOficial(documentoOficial);
            item.getDocumentoNotificacao().setItemNotificacao(item);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado o parâmetro.");
        }
    }

    public TipoDoctoOficial recuperaTipoDocumentoPorAcaoCobranca(Cadastro cadastro, Pessoa pessoa, Exercicio exercicio, TipoAcaoCobranca acaoCobranca) {
        ParametrosCobrancaAdministrativa parametros = parametroCobrancaAdministrativaFacade.parametrosCobrancaAdministrativaPorExercicio(exercicio);
        if (cadastro != null) {
            if (cadastro instanceof CadastroImobiliario) {
                switch (acaoCobranca) {
                    case AVISO:
                        return parametros.getDoctoImobiliarioAviso();
                    case NOTIFICACAO:
                        return parametros.getDoctoImobiliarioNotificacao();
                    default:
                        return null;
                }
            } else if (cadastro instanceof CadastroEconomico) {
                switch (acaoCobranca) {
                    case AVISO:
                        return parametros.getDoctoEconomicoAviso();
                    case NOTIFICACAO:
                        return parametros.getDoctoEconomicoNotificacao();
                    default:
                        return null;
                }
            } else if (cadastro instanceof CadastroRural) {
                switch (acaoCobranca) {
                    case AVISO:
                        return parametros.getDoctoRuralAviso();
                    case NOTIFICACAO:
                        return parametros.getDoctoRuralNotificacao();
                    default:
                        return null;
                }
            }
        } else {
            if (pessoa instanceof PessoaFisica) {
                switch (acaoCobranca) {
                    case AVISO:
                        return parametros.getDoctoContribPFAviso();
                    case NOTIFICACAO:
                        return parametros.getDoctoContribPFNotificacao();
                    default:
                        return null;
                }
            } else {
                switch (acaoCobranca) {
                    case AVISO:
                        return parametros.getDoctoContribPJAviso();
                    case NOTIFICACAO:
                        return parametros.getDoctoContribPJNotificacao();
                    default:
                        return null;
                }
            }
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ParcelaValorDivida recuperaParcelaValorDivida(Long id) {
        ParcelaValorDivida pvd = em.find(ParcelaValorDivida.class, id);
        return pvd;
    }

    public SituacaoProgramacaoCobranca ultimaSituacaoProgramacaoCobranca(ProgramacaoCobranca programacaoCobranca) {
        String hql = " select sit from SituacaoProgramacaoCobranca sit " +
            " where id = (select max(id) from SituacaoProgramacaoCobranca s_sit where s_sit.programacaoCobranca.id =:id) ";
        Query q = em.createQuery(hql, SituacaoProgramacaoCobranca.class);
        q.setParameter("id", programacaoCobranca.getId());
        return (SituacaoProgramacaoCobranca) q.getSingleResult();
    }

    public ParametroCobrancaAdministrativaFacade getParametroCobrancaAdministrativaFacade() {
        return parametroCobrancaAdministrativaFacade;
    }

    public void setParametroCobrancaAdministrativaFacade(ParametroCobrancaAdministrativaFacade parametroCobrancaAdministrativaFacade) {
        this.parametroCobrancaAdministrativaFacade = parametroCobrancaAdministrativaFacade;
    }

    public List<Quadra> recuperaFaixadeQuadraporSetores(Setor setorinicial, Setor setorfinal, String filtro, String ordena) {
        String sql = "select " +
            "          qr.* " +
            "     from quadra qr " +
            "     inner join  setor st " +
            "     on qr.setor_id = st.id " +
            "     where to_number(st.codigo) between to_number(:inicial) and to_number(:final) " +
            "     and qr.codigo like :filtro  order by to_number(qr.codigo) ";
        if (!ordena.isEmpty()) {
            sql += ordena;
        }
        Query q = em.createNativeQuery(sql, Quadra.class);
        q.setParameter("inicial", setorinicial.getCodigo());
        q.setParameter("final", setorfinal.getCodigo());
        q.setParameter("filtro", "%" + filtro.trim() + "%");
        q.setMaxResults(200);
        return q.getResultList();
    }


    public List<Lote> recuperaFaixadeLoteporQuadra(Setor setorinicial, Setor setorfinal, Quadra quadrainicial, Quadra quadrafinal, String filtro) {
        String hql = " select lt from Lote lt join  lt.quadra qr join lt.setor st " +
            "      where (qr.codigo between :quadrainicial and :quadrafinal)" +
            "            and lt.codigoLote like :filtro " +
            "            and to_number(st.codigo)  between to_number(:setorinicial) and to_number(:setorfinal)";
        Query q = em.createQuery(hql, Lote.class);
        q.setParameter("quadrainicial", quadrainicial.getCodigo());
        q.setParameter("quadrafinal", quadrafinal.getCodigo());
        q.setParameter("filtro", "%" + filtro.trim() + "%");
        q.setParameter("setorinicial", setorinicial.getCodigo());
        q.setParameter("setorfinal", setorfinal.getCodigo());
        q.setMaxResults(200);
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<ProgramacaoCobrancaVO> recuperaCadastroImobiliarioFaixadeLote(ProgramacaoCobranca programacao) {
        if (programacao == null) {
            return new ArrayList<>();
        }
        String sql = " select distinct ci.id from cadastroimobiliario ci " +
            " inner join lote lt on lt.id = ci.lote_id" +
            " inner join quadra qr on lt.quadra_id = qr.id" +
            " inner join setor st on lt.setor_id = st.id" +
            " inner join testada testadas on lt.id = testadas.lote_id" +
            " inner join face face on testadas.face_id = face.id" +
            " left join logradourobairro logra_bairro on face.logradourobairro_id = logra_bairro.id" +
            " left join logradouro lg on logra_bairro.logradouro_id = lg.id" +
            " left join bairro br on logra_bairro.bairro_id = br.id ";

        String juncao = "where";
        if (programacao.getSetorInicial() != null && programacao.getSetorInicial().getId() != null && programacao.getSetorFinal() != null && programacao.getSetorFinal().getId() != null) {
            sql += juncao + " to_number(st.codigo)  between to_number(:setorinicial) and to_number(:setorfinal) ";
            juncao = "and";
        }
        if (programacao.getLoteInicial() != null && programacao.getLoteInicial().getId() != null && programacao.getLoteFinal() != null && programacao.getLoteFinal().getId() != null) {
            sql += juncao + " to_number(lt.codigoLote) between to_number(:loteinicial) and to_number(:lotefinal) ";
            juncao = "and";
        }
        if (programacao.getQuadraInicial() != null && programacao.getQuadraInicial().getId() != null && programacao.getQuadraFinal() != null && programacao.getQuadraFinal().getId() != null) {
            sql += juncao + " (qr.codigo between :quadrainicial and :quadrafinal) ";
            juncao = "and";
        }
        if (programacao.getBairros() != null && !programacao.getBairros().isEmpty()) {
            sql += juncao + "(br.id in (:idsBairros))";
            juncao = "and";
        }
        if (programacao.getLogradouros() != null && !programacao.getLogradouros().isEmpty()) {
            sql += juncao + "(lg.id in (:idsLogradouros))";
            juncao = "and";
        }
        if (programacao.getCadastroInicial() != null && !programacao.getCadastroInicial().isEmpty() && programacao.getCadastroFinal() != null && !programacao.getCadastroFinal().isEmpty()) {
            sql += juncao + " to_number(replace(replace(replace(ci.inscricaoCadastral, 'O','0'),'B','0'), 'S','0')) between :inicialInscricao and :finalInscricao ";
        }

        Query q = em.createNativeQuery(sql);

        if (programacao.getSetorInicial() != null && programacao.getSetorInicial().getId() != null && programacao.getSetorFinal() != null && programacao.getSetorFinal().getId() != null) {
            q.setParameter("setorinicial", programacao.getSetorInicial().getCodigo());
            q.setParameter("setorfinal", programacao.getSetorFinal().getCodigo());
        }
        if (programacao.getLoteInicial() != null && programacao.getLoteInicial().getId() != null && programacao.getLoteFinal() != null && programacao.getLoteFinal().getId() != null) {
            q.setParameter("loteinicial", programacao.getLoteInicial().getCodigoLote().trim());
            q.setParameter("lotefinal", programacao.getLoteFinal().getCodigoLote().trim());
        }
        if (programacao.getQuadraInicial() != null && programacao.getQuadraInicial().getId() != null && programacao.getQuadraFinal() != null && programacao.getQuadraFinal().getId() != null) {
            q.setParameter("quadrainicial", programacao.getQuadraInicial().getCodigo());
            q.setParameter("quadrafinal", programacao.getQuadraFinal().getCodigo());
        }
        if (programacao.getBairros() != null && !programacao.getBairros().isEmpty()) {
            q.setParameter("idsBairros", getIdsBairros(programacao.getBairros()));
        }
        if (programacao.getLogradouros() != null && !programacao.getLogradouros().isEmpty()) {
            q.setParameter("idsLogradouros", getIdsLogradouros(programacao.getLogradouros()));
        }
        if (programacao.getCadastroInicial() != null && !programacao.getCadastroInicial().isEmpty() && programacao.getCadastroFinal() != null && !programacao.getCadastroFinal().isEmpty()) {
            q.setParameter("inicialInscricao", programacao.getCadastroInicial());
            q.setParameter("finalInscricao", programacao.getCadastroFinal());
        }

        List lista = q.getResultList();
        List<ProgramacaoCobrancaVO> retorno = Lists.newArrayList();
        for (Object o : lista) {
            ProgramacaoCobrancaVO prog = new ProgramacaoCobrancaVO();
            prog.setIdCadastro(((BigDecimal) o).longValue());
            retorno.add(prog);
        }
        return retorno;
    }

    public List<? extends Object> recuperaFaixaValores(String filtro, ProgramacaoCobranca programacao, String entidade) {
        if (programacao == null) {
            return new ArrayList<>();
        }
        String hql = "select distinct " +
            entidade +
            " from Lote lote " +
            " join lote.quadra quadra " +
            " join lote.setor setor " +
            " join lote.testadas testadas" +
            " join testadas.face face" +
            " join face.logradouroBairro logra_bairro " +
            " join logra_bairro.logradouro logradouro " +
            " join logra_bairro.bairro bairro where 1 = 1 ";

        if (entidade.equals("bairro")) {
            hql += " and bairro.ativo = 1";
        }
        if (programacao.getSetorInicial() != null && programacao.getSetorInicial().getId() != null && programacao.getSetorFinal() != null && programacao.getSetorFinal().getId() != null) {
            hql += "and ( setor.codigo  between :setorinicial and :setorfinal ) ";
        }
        if (programacao.getLoteInicial() != null && programacao.getLoteInicial().getId() != null && programacao.getLoteFinal() != null && programacao.getLoteFinal().getId() != null) {
            hql += "and (to_number(lote.codigoLote) between to_number(:loteinicial) and to_number(:lotefinal)) ";
        }
        if (programacao.getQuadraInicial() != null && programacao.getQuadraInicial().getId() != null && programacao.getQuadraFinal() != null && programacao.getQuadraFinal().getId() != null) {
            hql += "and (quadra.codigo between :quadrainicial and :quadrafinal) ";
        }
        if (programacao.getBairros() != null && !programacao.getBairros().isEmpty()) {
            hql += "and " + "(br.id in (:idsBairros))";
        }
        if (programacao.getLogradouros() != null && !programacao.getLogradouros().isEmpty()) {
            hql += "and " + "(lg.id in (:idsLogradouros))";
        }
        if (filtro != null) {
            switch (entidade) {
                case "lote":
                    hql += " and ( lote.codigoLote like :filtro ) ";
                    break;
                case "quadra":
                    hql += " and ( to_char(quadra.codigo) like :filtro )";
                    break;
                case "setor":
                    hql += " and ( to_char(setor.codigo) like :filtro )";
                    break;
                case "bairro":
                    hql += " and ( upper(bairro.descricao) like upper(:filtro))";
                    break;
                case "logradouro":
                    hql += " and ( upper(logradouro.nome) like upper(:filtro) )";
                    break;
                default:
                    break;
            }
        }
        Query qi = em.createQuery(hql);


        if (programacao.getSetorInicial() != null && programacao.getSetorInicial().getId() != null && programacao.getSetorFinal() != null && programacao.getSetorFinal().getId() != null) {
            qi.setParameter("setorinicial", programacao.getSetorInicial().getCodigo());
            qi.setParameter("setorfinal", programacao.getSetorFinal().getCodigo());
        }
        if (programacao.getLoteInicial() != null && programacao.getLoteInicial().getId() != null && programacao.getLoteFinal() != null && programacao.getLoteFinal().getId() != null) {
            qi.setParameter("loteinicial", programacao.getLoteInicial().getCodigoLote().trim());
            qi.setParameter("lotefinal", programacao.getLoteFinal().getCodigoLote().trim());
        }
        if (programacao.getQuadraInicial() != null && programacao.getQuadraInicial().getId() != null && programacao.getQuadraFinal() != null && programacao.getQuadraFinal().getId() != null) {
            qi.setParameter("quadrainicial", programacao.getQuadraInicial().getCodigo());
            qi.setParameter("quadrafinal", programacao.getQuadraFinal().getCodigo());
        }
        if (programacao.getBairros() != null && !programacao.getBairros().isEmpty()) {
            qi.setParameter("idsBairros", getIdsBairros(programacao.getBairros()));
        }
        if (programacao.getLogradouros() != null && !programacao.getLogradouros().isEmpty()) {
            qi.setParameter("idsLogradouros", getIdsLogradouros(programacao.getLogradouros()));
        }
        if (filtro != null) {
            qi.setParameter("filtro", "%" + filtro.trim() + "%");
        }

        qi.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return qi.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<ProgramacaoCobrancaVO> recuperaCadastroEconomico(ProgramacaoCobranca programacaoCobranca) {
        String sql = "select distinct ce.id from CadastroEconomico ce " +
            " left join ce.enquadramentos enquadramento with enquadramento.fimVigencia is null ";

        String juncao = "where";
        if (programacaoCobranca.getServicos() != null && !programacaoCobranca.getServicos().isEmpty()) {
            sql += juncao + " exists (select sv from ce.servico sv where sv.id in (";
            sql += getIdsServicos(programacaoCobranca.getServicos()) + "))";
            juncao = "and";
        }
        if (programacaoCobranca.getCnaes() != null && !programacaoCobranca.getCnaes().isEmpty()) {
            sql += juncao + " exists(select ec from ce.economicoCNAE ec where ec.cnae.id in (";
            sql += getIdsCNAEs(programacaoCobranca.getCnaes()) + "))";
            juncao = "and";
        }
        if (programacaoCobranca.getAtividades() != null && !programacaoCobranca.getAtividades().isEmpty()) {
            sql += juncao + "(ce.classificacaoAtividade in (:atividades))";
            juncao = "and";
        }

        if (programacaoCobranca.getCadastroInicial() != null && !programacaoCobranca.getCadastroInicial().isEmpty() && programacaoCobranca.getCadastroFinal() != null && !programacaoCobranca.getCadastroFinal().isEmpty()) {
            sql += juncao + " (to_number(ce.inscricaoCadastral) >= :inicialInscricao and to_number(ce.inscricaoCadastral) <= :finalInscricao) ";
        }

        Query q = em.createQuery(sql);

        if (programacaoCobranca.getAtividades() != null && !programacaoCobranca.getAtividades().isEmpty()) {
            q.setParameter("atividades", getClassificacoesAtividade(programacaoCobranca.getAtividades()));
        }

        if (programacaoCobranca.getCadastroInicial() != null && !programacaoCobranca.getCadastroInicial().isEmpty() && programacaoCobranca.getCadastroFinal() != null && !programacaoCobranca.getCadastroFinal().isEmpty()) {
            q.setParameter("inicialInscricao", programacaoCobranca.getCadastroInicial());
            q.setParameter("finalInscricao", programacaoCobranca.getCadastroFinal());
        }

        List lista = q.getResultList();
        List<ProgramacaoCobrancaVO> retorno = Lists.newArrayList();
        for (Object o : lista) {
            ProgramacaoCobrancaVO prog = new ProgramacaoCobrancaVO();
            prog.setIdCadastro(((Number) o).longValue());
            retorno.add(prog);
        }
        return retorno;
    }

    public String getIdsServicos(List<ProgramacaoCobrancaServico> cobrancaServicos) {
        String ids = "";
        for (ProgramacaoCobrancaServico cd : cobrancaServicos) {
            ids += cd.getServico().getId();
            ids += ",";
        }
        if (!ids.isEmpty()) {
            ids = ids.substring(0, ids.length() - 1);
        }
        return ids;
    }

    public String getIdsCNAEs(List<ProgramacaoCobrancaCNAE> cobrancaCNAEs) {
        String ids = "";
        for (ProgramacaoCobrancaCNAE cd : cobrancaCNAEs) {
            ids += cd.getCnae().getId();
            ids += ",";
        }
        if (!ids.isEmpty()) {
            ids = ids.substring(0, ids.length() - 1);
        }
        return ids;
    }

    public List<Long> getIdsBairros(List<ProgramacaoCobrancaBairro> cobrancaBairros) {
        List<Long> ids = Lists.newArrayList();
        for (ProgramacaoCobrancaBairro pcb : cobrancaBairros) {
            ids.add(pcb.getBairro().getId());

        }
        return ids;
    }

    public List<Long> getIdsLogradouros(List<ProgramacaoCobrancaLogradouro> cobrancaLogradouros) {
        List<Long> ids = Lists.newArrayList();
        for (ProgramacaoCobrancaLogradouro pcl : cobrancaLogradouros) {
            ids.add(pcl.getLogradouro().getId());
        }
        return ids;
    }

    public List<ClassificacaoAtividade> getClassificacoesAtividade(List<ProgramacaoCobrancaClassificacaoAtividade> cobrancaAtividades) {
        List<ClassificacaoAtividade> atividades = Lists.newArrayList();
        for (ProgramacaoCobrancaClassificacaoAtividade pcca : cobrancaAtividades) {
            atividades.add(pcca.getClassificacaoAtividade());

        }
        return atividades;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<ProgramacaoCobrancaVO> recuperaCadastroRural(ProgramacaoCobranca programacao) {
        String sql = "select distinct new CadastroRural(cr.id) from CadastroRural cr ";

        String juncao = "where";
        if (programacao.getAreaInicial() != null && !programacao.getAreaInicial().isEmpty()) {
            sql += juncao + " to_number(replace(cr.arealote,'.',',')) >= to_number(:inicial) ";
            juncao = "and";
        }
        if (programacao.getAreaFinal() != null && !programacao.getAreaFinal().isEmpty()) {
            sql += juncao + " to_number(replace(cr.arealote,'.',',')) <= to_number(:final) ";
            juncao = "and";
        }
        if (programacao.getCadastroInicial() != null && !programacao.getCadastroInicial().isEmpty() && programacao.getCadastroFinal() != null && !programacao.getCadastroFinal().isEmpty()) {
            sql += juncao + " (to_number(cr.numeroincra) between :inicialIncra and :finalIncra) ";
        }
        Query q = em.createQuery(sql);
        if (programacao.getAreaInicial() != null && !programacao.getAreaInicial().isEmpty()) {
            q.setParameter("inicial", programacao.getAreaInicial().trim().replace(".", ","));
        }
        if (programacao.getAreaFinal() != null && !programacao.getAreaFinal().isEmpty()) {
            q.setParameter("final", programacao.getAreaFinal().trim().replace(".", ","));
        }
        if (programacao.getCadastroInicial() != null && !programacao.getCadastroInicial().isEmpty() && programacao.getCadastroFinal() != null && !programacao.getCadastroFinal().isEmpty()) {
            q.setParameter("inicialIncra", programacao.getCadastroInicial());
            q.setParameter("finalIncra", programacao.getCadastroFinal());
        }
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<Pessoa> recuperaPessoa(Pessoa pessoa) {
        String hql = "select new Pessoa(ps.id) from Pessoa ps ";

        String juncao = "where";
        if (pessoa != null && pessoa.getId() != null) {
            hql += juncao + " ps.id = :id";
        }
        Query q = em.createQuery(hql, Pessoa.class);
        if (pessoa != null && pessoa.getId() != null) {
            q.setParameter("id", pessoa.getId());
        }
        q.setMaxResults(10000);
        return q.getResultList();
    }

    public List<ProgramacaoCobranca> buscarListaProgramacaoSituacao(String parte, TipoSituacaoProgramacaoCobranca situacao) {
        String sql = "select prog.* from programacaocobranca prog " +
            "  where ((prog.numero like :parte) or (lower(prog.descricao) like :parte))" +
            "   and prog.ultimasituacao = :situacaoProgramado ";
        Query q = em.createNativeQuery(sql, ProgramacaoCobranca.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("situacaoProgramado", situacao.name());
        List<ProgramacaoCobranca> programacao = q.getResultList();
        if (programacao != null && !programacao.isEmpty()) {
            return programacao;
        }
        return Lists.newArrayList();
    }

    public void gerarItemProgramacaoCobranca(ProgramacaoCobranca selecionado, List<ResultadoParcela> debitoSelecionados) {
        if (debitoSelecionados != null) {
            for (ResultadoParcela resultado : debitoSelecionados) {
                ItemProgramacaoCobranca item = new ItemProgramacaoCobranca();
                item.setProgramacaoCobranca(selecionado);
                item.setParcelaValorDivida(buscarParcelaValorDivida(resultado.getIdParcela()));
                item.setValorTotal(resultado.getValorTotal());
                item.setValorJuros(resultado.getValorJuros());
                item.setValorMulta(resultado.getValorMulta());
                item.setValorCorrigido(resultado.getValorOriginal().add(resultado.getValorCorrecao()));
                selecionado.getListaItemProgramacaoCobrancas().add(item);
            }
        }
    }

    private Long buscarProximoCodigoPorExercicio(Exercicio exercicio) {
        try {
            return singletonGeradorCodigoPorExercicio.getProximoCodigoDoExercicio(ProgramacaoCobranca.class, "codigo", "exercicio", exercicio);
        } catch (Exception e) {
            return 1L;
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteProgramacaoCobranca> buscarDebitosProgramacaoCobranca(AssistenteProgramacaoCobranca assistente,
                                                                                  Map<String, BigDecimal> totaisSituacao,
                                                                                  ProgramacaoCobrancaControlador.TotalTabelaParcelas totalTabelaParcelas) {
        ProgramacaoCobranca selecionado = assistente.getSelecionado();

        if (selecionado.getTipoCadastro().equals(TipoCadastroTributario.PESSOA)) {
            pesquisarContribuinte(assistente);
        } else {
            pesquisarCadastros(assistente);
        }
        if (assistente.getResultadosParcela() != null && !assistente.getResultadosParcela().isEmpty()) {
            adicionarValores(assistente, totaisSituacao, totalTabelaParcelas);
        }
        assistente.setSelecionado(selecionado);
        return new AsyncResult<>(assistente);
    }

    private void adicionarValores(AssistenteProgramacaoCobranca assistente, Map<String, BigDecimal> totaisSituacao, ProgramacaoCobrancaControlador.TotalTabelaParcelas totalTabelaParcelas) {
        for (ResultadoParcela resultado : assistente.getResultadosParcela()) {
            BigDecimal total = resultado.getValorTotal();
            String situacaoParaProcessamento = resultado.getSituacaoParaProcessamento(assistente.getDataOperacao());

            if (totaisSituacao.containsKey(situacaoParaProcessamento)) {
                total = totaisSituacao.get(situacaoParaProcessamento);
                total = total.add(resultado.getValorTotal());
            }
            totaisSituacao.put(situacaoParaProcessamento, total);
            totalTabelaParcelas.soma(resultado);
        }
    }

    private void pesquisarContribuinte(AssistenteProgramacaoCobranca assistente) {
        if (assistente.getPessoas().size() > 1000) {
            for (List<Pessoa> pessoas : Lists.partition(assistente.getPessoas(), 1000)) {
                novaConsultaParcela(assistente, Lists.<ProgramacaoCobrancaVO>newArrayList(), pessoas);
            }
        } else {
            novaConsultaParcela(assistente, Lists.<ProgramacaoCobrancaVO>newArrayList(), assistente.getPessoas());
        }
    }

    private void novaConsultaParcela(AssistenteProgramacaoCobranca assistente, List<ProgramacaoCobrancaVO> programacaoes, List<Pessoa> pessoas) {
        ConsultaParcela consulta = new ConsultaParcela();
        adicionaParametro(consulta, assistente.getSelecionado(), programacaoes, pessoas);
        consulta.executaConsulta();
        assistente.getResultadosParcela().addAll(consulta.getResultados());
    }

    private void pesquisarCadastros(AssistenteProgramacaoCobranca assistente) {
        if (assistente.getCobrancaVOS().size() > 1000) {
            for (List<ProgramacaoCobrancaVO> programacoes : Lists.partition(assistente.getCobrancaVOS(), 1000)) {
                novaConsultaParcela(assistente, programacoes, Lists.<Pessoa>newArrayList());
            }
        } else {
            novaConsultaParcela(assistente, assistente.getCobrancaVOS(), Lists.<Pessoa>newArrayList());
        }
    }

    private void adicionaParametro(ConsultaParcela consulta, ProgramacaoCobranca selecionado, List<ProgramacaoCobrancaVO> cobrancaAuxiliaresVOS, List<Pessoa> pessoasAuxiliares) {

        if (!getIdsCadastroProgramacao(cobrancaAuxiliaresVOS).isEmpty()) {
            consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IN, getIdsCadastroProgramacao(cobrancaAuxiliaresVOS));
        }
        if (!getIdsPessoasProgramacao(pessoasAuxiliares).isEmpty()) {
            consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IN, getIdsPessoasProgramacao(pessoasAuxiliares));
        }
        if (selecionado.getExercicioInicial() != null) {
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MAIOR_IGUAL, selecionado.getExercicioInicial().getAno());
        }
        if (selecionado.getExercicioFinal() != null) {
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR_IGUAL, selecionado.getExercicioFinal().getAno());
        }
        if (selecionado.getVencimentoInicial() != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, selecionado.getVencimentoInicial());
        }
        if (selecionado.getVencimentoFinal() != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, selecionado.getVencimentoFinal());
        }
        if (selecionado.getValorInicial() != null) {
            if (selecionado.getTipoValorProgramacaoCobranca().equals(TipoValorProgramacaoCobranca.ATUALIZADO)) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_SALDO_TOTAL_ATUALIZADO, ConsultaParcela.Operador.MAIOR_IGUAL, selecionado.getValorInicial());
            } else if (selecionado.getTipoValorProgramacaoCobranca().equals(TipoValorProgramacaoCobranca.ORIGINAL_CORRECAO)) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_COM_CORRECAO, ConsultaParcela.Operador.MAIOR_IGUAL, selecionado.getValorInicial());
            } else {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_VALOR, ConsultaParcela.Operador.MAIOR_IGUAL, selecionado.getValorInicial());
            }
        }
        if (selecionado.getValorFinal() != null) {
            if (selecionado.getTipoValorProgramacaoCobranca().equals(TipoValorProgramacaoCobranca.ATUALIZADO)) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_SALDO_TOTAL_ATUALIZADO, ConsultaParcela.Operador.MENOR_IGUAL, selecionado.getValorFinal());
            } else if (selecionado.getTipoValorProgramacaoCobranca().equals(TipoValorProgramacaoCobranca.ORIGINAL_CORRECAO)) {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_COM_CORRECAO, ConsultaParcela.Operador.MENOR_IGUAL, selecionado.getValorFinal());
            } else {
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_VALOR, ConsultaParcela.Operador.MENOR_IGUAL, selecionado.getValorFinal());
            }
        }

        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.addParameter(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Operador.IGUAL, 0);
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_VALOR, ConsultaParcela.Operador.MAIOR, BigDecimal.ZERO);

        if (!selecionado.getDividas().isEmpty()) {
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, selecionado.retornaIdsDasDividasSelecionadas());
        }

        if (selecionado.getTipoDamEmitido() != null) {
            String condicao = "not";
            if (TipoDamEmitido.SIM.equals(selecionado.getTipoDamEmitido())) {
                condicao = "";
            }
            consulta.addComplementoDoWhere(" and " + condicao + " exists (select dam.id from dam dam " +
                " inner join itemdam idam on idam.dam_id = dam.id " +
                " inner join historicoimpressaodam hist on hist.dam_id = dam.id " +
                " where idam.parcela_id = vw.parcela_id)");
        }
    }

    private List<Long> getIdsCadastroProgramacao(List<ProgramacaoCobrancaVO> cobrancaAuxiliaresVOS) {
        List<Long> idsCadastro = Lists.newArrayList();
        if (cobrancaAuxiliaresVOS == null) {
            cobrancaAuxiliaresVOS = Lists.newArrayList();
        }
        for (ProgramacaoCobrancaVO cd : cobrancaAuxiliaresVOS) {
            idsCadastro.add(cd.getIdCadastro());
        }
        return idsCadastro;
    }

    private List<Long> getIdsPessoasProgramacao(List<Pessoa> pessoasAuxiliares) {
        List<Long> idsPessoas = Lists.newArrayList();
        if (pessoasAuxiliares == null) {
            pessoasAuxiliares = Lists.newArrayList();
        }
        for (Pessoa ps : pessoasAuxiliares) {
            idsPessoas.add(ps.getId());
        }
        return idsPessoas;
    }

    private ParcelaValorDivida buscarParcelaValorDivida(Long id) {
        String sql = "select pvd from ParcelaValorDivida pvd where id = :id";
        Query q = em.createQuery(sql);
        q.setParameter("id", id);
        return (ParcelaValorDivida) q.getResultList().get(0);
    }

}


