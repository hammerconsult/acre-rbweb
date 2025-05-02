package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.controlerelatorio.ConfiguracaoProtocoloFacade;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VoProcesso;
import br.com.webpublico.entidadesauxiliares.administrativo.FiltroListaProtocolo;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.negocios.tributario.singletons.SingletonProcesso;
import br.com.webpublico.negocios.tributario.singletons.SingletonProtocolo;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ProcessoFacade extends AbstractFacade<Processo> {

    private static final Logger logger = LoggerFactory.getLogger(ProcessoFacade.class);

    @EJB
    private SituacaoTramiteFacade situacaoTramiteFacade;
    @EJB
    private TramiteFacade TramiteFacade;
    @EJB
    private AssuntoFacade assuntoFacade;
    @EJB
    private SubAssuntoFacade subAssuntoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ConfiguracaoProtocoloFacade configuracaoProtocoloFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private DocumentoFacade documentoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonHierarquiaOrganizacional singletonHO;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonProtocolo singletonProtocolo;
    @EJB
    private SingletonProcesso singletonProcesso;
    @EJB
    private LeitorWsConfig leitorWsConfig;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;

    public ProcessoFacade() {
        super(Processo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Processo recuperarSimples(Object id) {
        Processo p = em.find(Processo.class, id);
        p.getTramites().size();
        return p;
    }

    @Override
    public Processo recuperar(Object id) {
        Processo p = em.find(Processo.class, id);
        p.getTramites().size();
        p.getArquivos().size();
        p.getDocumentoProcesso().size();
        p.getProcessosSubordinados().size();
        p.getProcessosEnglobados().size();
        p.getPessoa().getEnderecos().size();
        p.getRotas().size();
        recuperarProcessoHistorico(p);
        return p;
    }

    public ProcessoEnglobado recuperarProcessoHistorico(Object id) {
        ProcessoEnglobado p = em.find(ProcessoEnglobado.class, id);
        p.getProcesso().getHistoricos().size();
        p.getEnglobado().getHistoricos().size();
        return p;
    }

    private void recuperarProcessoHistorico(Processo processo) {
        processo.getHistoricos().size();
        for (ProcessoHistorico processoHistorico : processo.getHistoricos()) {
            if (processoHistorico != null && processoHistorico.getUnidadeOrganizacional() != null) {
                HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                    TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                    processoHistorico.getUnidadeOrganizacional(),
                    sistemaFacade.getDataOperacao());
                processoHistorico.setHierarquiaOrganizacional(hierarquia);
            }
        }
    }

    public Processo salvarProcesso(Processo processo) {
        processo = em.merge(processo);
        recuperarFilhos(processo);
        return processo;
    }

    public Processo salvarProcessoRetornando(Processo processo) {
        return em.merge(processo);
    }

    public void atualizaSituacaoProcesso(Processo proc) throws ExcecaoNegocioGenerica {
        try {
            Tramite tra = null;
            for (Tramite t : proc.getTramites()) {
                t.setStatus(false);
                if (t.getDataConclusao() == null) {
                    t.setDataConclusao(new Date());
                }
                if (t.getStatus()) {
                    tra = t;
                }
            }
            if (tra == null) {
                tra = proc.getTramites().get(proc.getTramites().size() - 1);
            }
            proc.setTramiteFinalizador(tra);
            em.merge(proc);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível alterar a situação do processo.");
        }
    }

    public List<Processo> buscarProcessoPessoa(Pessoa pessoa) {
        String hql = "from Processo p where p.pessoa = :paramPessoa order by p.numero desc";
        Query q = em.createQuery(hql);
        q.setParameter("paramPessoa", pessoa);
        return q.getResultList();
    }

    public List<Arquivo> listaArquivosUpload(Long processo) {
        StringBuilder sql = new StringBuilder("select aq.* from Processo p ")
            .append(" inner join Tramite tr on tr.processo_id = p.id")
            .append(" inner join Tramite_Arquivo ta on ta.tramite_id = tr.id ")
            .append(" inner join Arquivo aq on ta.arquivos_id = aq.id ")
            .append(" where p.id = :param");
        Query q = em.createNativeQuery(sql.toString(), Processo.class);
        q.setParameter("param", processo);
        return q.getResultList();
    }

    public List<Processo> getListaProtocoloFiltrando(String parametro) {
        List<Processo> retorno = null;
        if (parametro == null || parametro.equals("")) {
            String sql = "select p.* from Processo p where p.protocolo = :protocolo order by p.numero desc";
            Query q = em.createNativeQuery(sql, Processo.class);
            q.setParameter("protocolo", Boolean.TRUE);
            retorno = q.getResultList();
        } else {
            StringBuilder sql = new StringBuilder("select p.* from Processo p ")
                .append(" inner join DocumentoProcesso docp on docp.processo_id = p.id ")
                .append(" inner join Pessoa pe on p.pessoa_id = pe.id ")
                .append(" inner join PessoaFisica pf on pe.id = pf.id ")
                .append(" where p.protocolo = :protocolo ")
                .append(" and docp.numeroDocumento like :param ")
                .append(" order by p.numero desc");
            Query q = em.createNativeQuery(sql.toString(), Processo.class);
            q.setParameter("param", "%" + parametro.toLowerCase().trim() + "%");
            q.setParameter("protocolo", Boolean.TRUE);
            retorno = q.getResultList();
        }
        return retorno;
    }

    public List<Processo> getListaProcessoFiltrando(String parametro) {
        List<Processo> retorno = null;
        if (parametro == null || parametro.equals("")) {
            String sql = "select p.* from Processo p where p.protocolo = :protocolo order by p.numero desc";
            Query q = em.createNativeQuery(sql, Processo.class);
            q.setParameter("protocolo", Boolean.FALSE);
            retorno = q.getResultList();
        } else {
            StringBuilder sql = new StringBuilder("select p.* from Processo p ")
                .append(" inner join DocumentoProcesso docp on docp.processo_id = p.id ")
                .append(" inner join Documento doc on docp.documento_id = doc.id ")
                .append(" inner join Pessoa pe on p.pessoa_id = pe.id ")
                .append(" inner join PessoaFisica pf on pe.id = pf.id ")
                .append(" where p.protocolo = :protocolo ")
                .append(" and docp.numerodocumento like :param ")
                .append(" order by p.numero desc");
            Query q = em.createNativeQuery(sql.toString(), Processo.class);
            q.setParameter("param", "%" + parametro.toLowerCase().trim() + "%");
            q.setParameter("protocolo", Boolean.FALSE);
            retorno = q.getResultList();
        }
        return retorno;
    }

    public List<Processo> listaProcessosAbertos(UsuarioSistema usuarioLogado) {
        StringBuilder sql = new StringBuilder("select p.* from processo p ")
            .append(" inner join tramite t on t.processo_id = p.id ")
            .append(" where p.protocolo = 0 ")
            .append(" and ((p.situacao = '" + SituacaoProcesso.GERADO.name() + "' and p.uocadastro_id in (select uo.id from UsuarioSistema usuSis inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id where ususis.id = :usuario and usuuni.gestorProtocolo = 1)) ")
            .append(" or (p.situacao = '" + SituacaoProcesso.EMTRAMITE.name() + "' and (select tra.unidadeorganizacional_id from tramite tra where tra.processo_id = p.id and tra.aceito = 1 and tra.indice = (select max(tra2.indice) from tramite tra2 where tra2.processo_id = p.id)) in (select uo.id from UsuarioSistema usuSis inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id where ususis.id = :usuario and usuuni.gestorProtocolo = 1))) ")
            .append(" order by p.id desc");
        Query q = em.createNativeQuery(sql.toString(), Processo.class);
        q.setParameter("usuario", usuarioLogado.getId());
        List<Processo> listaReturn = new ArrayList<>(new HashSet<>((ArrayList<Processo>) q.getResultList()));
        Collections.sort(listaReturn);
        return listaReturn;

    }

    public List<DocumentoProcesso> listaDocumentosProcesso(Processo pro) {
        Query q = em.createNativeQuery("select dp.* from DocumentoProcesso dp where dp.processo_id = :pro", DocumentoProcesso.class);
        q.setParameter("pro", pro.getId());
        return q.getResultList();
    }

    public Processo salvarProcessoProtocolo(Processo entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(gerarCodigo(entity));
        }
        for (Processo p : entity.getProcessosSubordinados()) {
            if (p.getNumero() == null) {
                p.setNumero(gerarCodigo(p));
            }
        }
        Processo p = getEntityManager().merge(entity);
        p.getTramites().size();
        p.getProcessosEnglobados().size();
        p.getPessoa().getEnderecos().size();
        return p;
    }

    public Processo salvarArquivosProcesso(Processo entity) {
        Processo p = getEntityManager().merge(entity);
        p.getArquivos().size();
        p.getHistoricos().size();
        return p;
    }

    public Processo recuperarProcessoPorNumeroAndAno(Integer numero, Integer ano) {
        Query q = em.createQuery("from Processo p where p.ano = :ano and p.numero = :numero ");
        q.setParameter("ano", ano);
        q.setParameter("numero", numero);
        q.setMaxResults(1);
        try {
            return (Processo) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("A Consulta não retornou resultado");
        }
    }

    public List<Processo> filtraAutoCompleteProcesso(Integer numero, Integer ano) {
        StringBuilder hql = new StringBuilder("from Processo p");
        if (numero != null && !numero.toString().isEmpty()) {
            hql.append(" where p.ano = :ano or p.numero = :numero");
        }
        Query q = em.createQuery(hql.toString());
        if (numero != null && !numero.toString().isEmpty()) {
            q.setParameter("ano", ano);
            q.setParameter("numero", numero);
        }
        q.setMaxResults(30);
        return q.getResultList();
    }

    public List<Processo> filtraAutoCompleteProcesso(Integer numero, Integer ano, String parte) {
        StringBuilder hql = new StringBuilder("select p from Processo p where p.protocolo = :protocolo ");
        if (numero != null && !numero.toString().isEmpty()) {
            hql.append(" and ((p.ano = :ano or p.numero = :numero) ");
            hql.append(" or (lower(p.assunto) like :parte)) ");
        } else {
            hql.append(" and (lower(p.assunto) like :parte) ");
        }
        hql.append(" order by p.ano desc, p.numero desc");
        Query q = em.createQuery(hql.toString());
        if (numero != null) {
            if (!numero.toString().isEmpty()) {
                q.setParameter("ano", ano);
                q.setParameter("numero", numero);
            }
        }
        q.setParameter("protocolo", true);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(30);
        return q.getResultList();
    }

    public br.com.webpublico.negocios.TramiteFacade getTramiteFacade() {
        return TramiteFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public LeitorWsConfig getLeitorWsConfig() {
        return leitorWsConfig;
    }

    public AssuntoFacade getAssuntoFacade() {
        return assuntoFacade;
    }

    public ConfiguracaoProtocoloFacade getConfiguracaoProtocoloFacade() {
        return configuracaoProtocoloFacade;
    }

    public DocumentoFacade getDocumentoFacade() {
        return documentoFacade;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SituacaoTramiteFacade getSituacaoTramiteFacade() {
        return situacaoTramiteFacade;
    }

    public SubAssuntoFacade getSubAssuntoFacade() {
        return subAssuntoFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SingletonHierarquiaOrganizacional getSingletonHO() {
        return singletonHO;
    }

    public DocumentoOficial geraDocumento(TipoDocumentoProtocolo tipoDocumento, Processo processo, Tramite tramite, Pessoa p) {
        ConfiguracaoProtocolo config = configuracaoProtocoloFacade.retornaUltima();
        try {
            if (tipoDocumento.equals(TipoDocumentoProtocolo.CAPA_PROCESSO)) {
                return documentoOficialFacade.geraDocumentoProtocolo(processo, null, null, null, p, config.getTipoDoctoCapaProcesso());
            } else if (tipoDocumento.equals(TipoDocumentoProtocolo.CAPA_PROTOCOLO)) {
                return documentoOficialFacade.geraDocumentoProtocolo(processo, null, null, null, p, config.getTipoDoctoCapaProtocolo());
            } else if (tipoDocumento.equals(TipoDocumentoProtocolo.TRAMITE_PROCESSO)) {
                return documentoOficialFacade.geraDocumentoProtocolo(processo, tramite, null, null, p, config.getTipoDoctoTramiteProcesso());
            } else if (tipoDocumento.equals(TipoDocumentoProtocolo.PROCESSO_WEB)) {
                return documentoOficialFacade.geraDocumentoProtocoloWeb(processo, null, null, null, p, config.getTipoDoctoProcessoWeb());
            } else if (tipoDocumento.equals(TipoDocumentoProtocolo.PROTOCOLO_WEB)) {
                return documentoOficialFacade.geraDocumentoProtocoloWeb(processo, null, null, null, p, config.getTipoDoctoProtocoloWeb());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Integer geraSenha() {
        int valor;
        String valorStr = "";
        for (int i = 0; i < 5; i++) {
            valor = 1 + (int) (Math.random() * 9);
            valorStr = valorStr + valor;
        }
        return Integer.parseInt(valorStr);
    }

    public DocumentoOficial getDocumentoProtocoloPorSenha(String senha) {
        Query q = em.createQuery("from Processo where senha = :senha");
        q.setParameter("senha", Integer.parseInt(senha));
        List<Processo> listaProcesso = q.getResultList();
        logger.debug("lista " + listaProcesso);
        if (listaProcesso.isEmpty()) {
            return null;
        }

        Processo p = (Processo) listaProcesso.get(0);
        if (p.getProtocolo()) {
            logger.debug("PROTOCOLO_WEB ");
            return geraDocumento(TipoDocumentoProtocolo.PROTOCOLO_WEB, p, null, p.getPessoa());
        } else {
            logger.debug("PROCESSO_WEB ");
            return geraDocumento(TipoDocumentoProtocolo.PROCESSO_WEB, p, null, p.getPessoa());
        }
    }

    public Processo getDocumentoProtocoloPorSenhaNumeroEAno(String senha, Integer numero, Integer ano) {
        Query q = em.createQuery("from Processo where senha = :senha and numero = :numero and ano = :ano");
        q.setParameter("senha", Integer.parseInt(senha));
        q.setParameter("numero", numero);
        q.setParameter("ano", ano);
        List<Processo> listaProcesso = q.getResultList();
        if (listaProcesso.isEmpty()) {
            return null;
        }
        Processo p = listaProcesso.get(0);
        recuperarFilhos(p);
        return p;
    }

    private void recuperarFilhos(Processo processo) {
        processo.getProcessosSubordinados().size();
        processo.getTramites().size();
        processo.getDocumentoProcesso().size();
        processo.getArquivos().size();
        if (processo.getArquivos() != null && !processo.getArquivos().isEmpty()) {
            for (ProcessoArquivo processoArquivo : processo.getArquivos()) {
                processoArquivo.getArquivo().getPartes().size();
            }
        }
        for (Processo filho : processo.getProcessosSubordinados()) {
            recuperarFilhos(filho);
        }
    }

    public List<VoProcesso> buscarProcessosGerados(FiltroListaProtocolo filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder("select distinct pro.id, pro.numero, pro.ano, pro.dataRegistro, " +
            " (select max(t.dataRegistro) from tramite t where t.processo_id = pro.id),  " +
            " coalesce(pf.nome, pj.razaoSocial), pro.assunto, pro.tipoProcessoTramite, pro.situacao, " +
            " sa.nome as nomeSub, ass.nome from Processo pro ");
        Query q = getQueryProcessosGerados(filtro, inicio, max, sql);
        return montaListaProcessos(q.getResultList());
    }

    public Integer quantidadeTotalDeProcessosGerados(FiltroListaProtocolo filtro) {
        StringBuilder sql = new StringBuilder("select count(distinct  pro.id) from Processo pro ");
        Query q = getQueryProcessosGerados(filtro, 0, 0, sql);
        return ((BigDecimal) q.getSingleResult()).intValue();
    }

    private Query getQueryProcessosGerados(FiltroListaProtocolo filtro, int inicio, int max, StringBuilder sql) {
        sql.append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" left join subAssunto sa on sa.id = pro.subAssunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" where (pro.uoCadastro_id in (select uo.id from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario and usuuni.gestorProtocolo = :gestor)  ")
            .append(" or (pro.uoCadastro_id = :unidade and pro.confidencial = :confidencial)) ")
            .append(" and pro.protocolo = :protocolo ");
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql.append(" and pro.numero = :numero ");
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            sql.append(" and pro.ano = :ano ");
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
        }
        sql.append(" order by pro.ano desc, pro.numero desc");
        Query q = em.createNativeQuery(sql.toString());
        if (max != 0) {
            q.setMaxResults(max);
            q.setFirstResult(inicio);
        }
        q.setParameter("usuario", filtro.getUsuarioSistema().getId());
        q.setParameter("unidade", filtro.getUnidadeOrganizacional().getId());
        q.setParameter("gestor", filtro.getGestor());
        q.setParameter("confidencial", filtro.getConfidencial());
        q.setParameter("protocolo", filtro.getProtocolo());
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            q.setParameter("numero", filtro.getNumero().trim());
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            q.setParameter("ano", filtro.getAno());
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            q.setParameter("solicitante", "%" + filtro.getSolicitante().toLowerCase() + "%");
        }
        return q;
    }

    public List<VoProcesso> buscarProcessosProtocoloEncaminhados(FiltroListaProtocolo filtro, int inicio, int max) {
        String campoUnidade = "unidadeorganizacional_id";
        if (!Strings.isNullOrEmpty(filtro.getDestinoExterno())) {
            campoUnidade = "origem_id";
        }
        StringBuilder sql = new StringBuilder("select distinct pro.id, pro.numero, pro.ano, pro.dataRegistro, " +
            " (select max(t.dataRegistro) from tramite t where t.processo_id = pro.id),  " +
            " coalesce(pf.nome, pj.razaoSocial), pro.assunto, pro.tipoProcessoTramite, pro.situacao," +
            " sa.nome as nomeSub, ass.nome from processo pro ");
        Query q = getQueryProcessoProtocoloEncaminhamentoMultiplos(filtro, inicio, max, campoUnidade, sql);
        return montaListaProcessos(q.getResultList());
    }

    private Query getQueryProcessoProtocoloEncaminhamentoMultiplos(FiltroListaProtocolo filtro, int inicio, int max, String campoUnidade, StringBuilder sql) {
        sql.append(" inner join tramite tra on tra.processo_id = pro.id ")
            .append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" left join subAssunto sa on sa.id = pro.subAssunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" where (tra.").append(campoUnidade).append(" in (select uo.id from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario and usuuni.gestorProtocolo = :gestor)) ")
            .append(" and tra.id <> coalesce(pro.tramiteFinalizador_id,0) ")
            .append(" and pro.protocolo = :protocolo ");
        if (filtro.getUnidadeOrganizacional() != null || (!Strings.isNullOrEmpty(filtro.getDestinoExterno()))) {
            sql.append(" and pro.situacao in (:situacao) ");
        } else {
            sql.append(" and pro.situacao <> :situacao ");
        }
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql.append(" and pro.numero = :numero ");
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            sql.append(" and pro.ano = :ano ");
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            sql.append(" and exists (select tr.id from tramite tr where tr.processo_id = pro.id and tr.unidadeorganizacional_id = :unidade and coalesce(tr.aceito,0) = 0) ");
            sql.append(" and (tra.indice = (select max(tr.indice) from Tramite tr where tr.processo_id = pro.id)) ");
            sql.append(" and (exists (select * from UsuarioUnidadeOrganizacio uni where uni.gestorProtocolo = :gestor and uni.unidadeOrganizacional_id = pro.uoCadastro_id and uni.usuarioSistema_id = :usuario)");
            sql.append(" or exists (select * from Tramite tr inner join UsuarioUnidadeOrganizacio uni on uni.unidadeOrganizacional_id = tr.unidadeOrganizacional_id where uni.gestorProtocolo = :gestor and uni.usuarioSistema_id = :usuario and tr.processo_id = pro.id and tr.indice = tra.indice -1))");

        } else if (!Strings.isNullOrEmpty(filtro.getDestinoExterno())) {
            sql.append(" and exists (select tr.id from tramite tr where tr.processo_id = pro.id and lower(tr.destinoExterno) like :destino and coalesce(tr.aceito,0) = 0) ");
        }
        sql.append(" order by pro.ano desc, pro.numero desc");
        Query q = em.createNativeQuery(sql.toString());
        if (max != 0) {
            q.setMaxResults(max);
            q.setFirstResult(inicio);
        }
        q.setParameter("usuario", filtro.getUsuarioSistema().getId());
        q.setParameter("gestor", filtro.getGestor());
        q.setParameter("protocolo", filtro.getProtocolo());
        if (filtro.getUnidadeOrganizacional() != null || (!Strings.isNullOrEmpty(filtro.getDestinoExterno()))) {
            q.setParameter("situacao", Lists.newArrayList(SituacaoProcesso.GERADO.name(), SituacaoProcesso.EMTRAMITE.name()));
        } else {
            q.setParameter("situacao", SituacaoProcesso.GERADO.name());
        }
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            q.setParameter("numero", filtro.getNumero().trim());
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            q.setParameter("ano", filtro.getAno());
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            q.setParameter("solicitante", "%" + filtro.getSolicitante().toLowerCase() + "%");
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("unidade", filtro.getUnidadeOrganizacional().getId());
        } else if (!Strings.isNullOrEmpty(filtro.getDestinoExterno())) {
            q.setParameter("destino", "%" + filtro.getDestinoExterno().toLowerCase() + "%");
        }
        return q;
    }

    public Integer quantidadeDeProcessosEncaminhados(FiltroListaProtocolo filtro) {
        String campoUnidade = "unidadeorganizacional_id";
        if (!Strings.isNullOrEmpty(filtro.getDestinoExterno())) {
            campoUnidade = "origem_id";
        }
        StringBuilder sql = new StringBuilder("select count(distinct pro.id) from processo pro ");
        Query q = getQueryProcessoProtocoloEncaminhamentoMultiplos(filtro, 0, 0, campoUnidade, sql);
        return ((BigDecimal) q.getSingleResult()).intValue();
    }


    public List<VoProcesso> buscarProtocolosEncaminhadosParaGuiRecebimentoMultiplo(FiltroListaProtocolo filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder("select distinct pro.id, pro.numero, pro.ano, pro.dataRegistro, " +
            " (select max(t.dataRegistro) from tramite t where t.processo_id = pro.id),  " +
            " coalesce(pf.nome, pj.razaoSocial), pro.assunto, pro.tipoProcessoTramite, pro.situacao, " +
            " sa.nome as nomeSub, ass.nome from processo pro ");
        Query q = getQueryProtocolosEncaminhadosParaGuiRecebimentoMultiplo(filtro, inicio, max, sql);
        return montaListaProcessos(q.getResultList());
    }

    public Integer quantidadeProtocolosEncaminhadosParaGuiRecebimentoMultiplo(FiltroListaProtocolo filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder(" select count(distinct pro.id) from processo pro ");
        Query q = getQueryProtocolosEncaminhadosParaGuiRecebimentoMultiplo(filtro, inicio, max, sql);
        return ((BigDecimal) q.getSingleResult()).intValue();
    }

    private Query getQueryProtocolosEncaminhadosParaGuiRecebimentoMultiplo(FiltroListaProtocolo filtro, int inicio, int max, StringBuilder sql) {
        sql.append(" inner join tramite tra on tra.processo_id = pro.id ")
            .append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" left join subassunto sa on sa.id = pro.subassunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" where pro.protocolo = :gestor ")
            .append("   and pro.situacao in (:situacoes) ")
            .append("   and (exists (select 1 ")
            .append("       from usuariounidadeorganizacio uni ")
            .append("       where uni.gestorprotocolo = :gestor ")
            .append("         and uni.unidadeorganizacional_id = pro.uocadastro_id ")
            .append("         and uni.usuariosistema_id = :usuario ")
            .append("         and pro.situacao in (:situacoes)) or exists (select 1 ")
            .append("      from tramite tr ")
            .append("       inner join usuariounidadeorganizacio uni on uni.unidadeorganizacional_id = tr.unidadeorganizacional_id ")
            .append("       where uni.gestorprotocolo = :gestor ")
            .append("         and uni.usuariosistema_id = :usuario ")
            .append("         and tr.processo_id = pro.id ")
            .append("         and tr.indice = tra.indice -1 ")
            .append("         and pro.situacao = :situacaoEmTramite)) ");
        if (filtro.getUnidadeOrganizacional() != null) {
            sql.append(" and tra.unidadeorganizacional_id = :unidade ");
            sql.append(" and tra.indice = (select max(tr.indice) from tramite tr where tr.processo_id = pro.id) ");
        } else if (!Strings.isNullOrEmpty(filtro.getDestinoExterno())) {
            sql.append(" and exists (select tr.id from tramite tr where tr.processo_id = pro.id and trim(lower(tr.destinoExterno)) like :destino and coalesce(tr.aceito,0) = 0) ");
        }
        sql.append(" order by pro.ano desc, pro.numero desc");
        Query q = em.createNativeQuery(sql.toString());
        if (max != 0) {
            q.setMaxResults(max);
            q.setFirstResult(inicio);
        }
        q.setParameter("usuario", filtro.getUsuarioSistema().getId());
        q.setParameter("gestor", filtro.getGestor());
        q.setParameter("situacoes", Lists.newArrayList(SituacaoProcesso.GERADO.name(), SituacaoProcesso.EMTRAMITE.name()));
        q.setParameter("situacaoEmTramite", SituacaoProcesso.EMTRAMITE.name());
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("unidade", filtro.getUnidadeOrganizacional().getId());
        } else if (!Strings.isNullOrEmpty(filtro.getDestinoExterno())) {
            q.setParameter("destino", "%" + filtro.getDestinoExterno().toLowerCase().trim() + "%");
        }
        return q;
    }

    public Integer quantidadeDeEncaminhamentoMultiplo(UsuarioSistema usuario, String numero, String ano, String solicitante, boolean protocolo, UnidadeOrganizacional unidadeAtual, String destinoAtual) {
        String campoUnidade = "unidadeorganizacional_id";
        if (destinoAtual != null && !"".equals(destinoAtual)) {
            campoUnidade = "origem_id";
        }
        StringBuilder sql = new StringBuilder("select count(distinct pro.id) from processo pro ")
            .append(" inner join tramite tra on tra.processo_id = pro.id ")
            .append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" left join subAssunto sa on sa.id = pro.subAssunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" where (tra.").append(campoUnidade).append(" in (select uo.id from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario and usuuni.gestorProtocolo = :gestor)) ")
            .append(" and tra.id <> coalesce(pro.tramiteFinalizador_id,0) ")
            .append(" and pro.protocolo = :protocolo ");
        if (unidadeAtual != null || (destinoAtual != null && !"".equals(destinoAtual))) {
            sql.append(" and pro.situacao in (:situacao) ");
        } else {
            sql.append(" and pro.situacao <> :situacao ");
        }
        if (numero != null) {
            if (!"".equals(numero.trim())) {
                sql.append(" and pro.numero = :numero ");
            }
        }
        if (ano != null) {
            if (!"".equals(ano.trim())) {
                sql.append(" and pro.ano = :ano ");
            }
        }
        if (solicitante != null) {
            if (!"".equals(solicitante.trim())) {
                sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                    .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
            }
        }
        if (unidadeAtual != null) {
            sql.append(" and exists (select tr.id from tramite tr where tr.processo_id = pro.id and tr.unidadeorganizacional_id = :unidade and coalesce(tr.aceito,0) = 0) ");
            sql.append(" and (tra.indice = (select max(tr.indice) from Tramite tr where tr.processo_id = pro.id)) ");
            sql.append(" and (exists (select * from UsuarioUnidadeOrganizacio uni where uni.gestorProtocolo = :gestor and uni.unidadeOrganizacional_id = pro.uoCadastro_id and uni.usuarioSistema_id = :usuario)");
            sql.append(" or exists (select * from Tramite tr inner join UsuarioUnidadeOrganizacio uni on uni.unidadeOrganizacional_id = tr.unidadeOrganizacional_id where uni.gestorProtocolo = :gestor and uni.usuarioSistema_id = :usuario and tr.processo_id = pro.id and tr.indice = tra.indice -1))");

        } else if (destinoAtual != null && !"".equals(destinoAtual)) {
            sql.append(" and exists (select tr.id from tramite tr where tr.processo_id = pro.id and lower(tr.destinoExterno) like :destino and coalesce(tr.aceito,0) = 0) ");
        }

        sql.append(" order by pro.ano desc, pro.numero desc");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("usuario", usuario.getId());
        q.setParameter("gestor", "1");
        if (unidadeAtual != null || (destinoAtual != null && !"".equals(destinoAtual))) {
            q.setParameter("situacao", Lists.newArrayList(SituacaoProcesso.GERADO.name(), SituacaoProcesso.EMTRAMITE.name()));
        } else {
            q.setParameter("situacao", SituacaoProcesso.GERADO.name());
        }
        if (protocolo) {
            q.setParameter("protocolo", "1");
        } else {
            q.setParameter("protocolo", "0");
        }
        if (numero != null) {
            if (!"".equals(numero)) {
                q.setParameter("numero", numero);
            }
        }
        if (ano != null) {
            if (!"".equals(ano)) {
                q.setParameter("ano", ano);
            }
        }
        if (solicitante != null) {
            if (!"".equals(solicitante.trim())) {
                q.setParameter("solicitante", "%" + solicitante.toLowerCase() + "%");
            }
        }
        if (unidadeAtual != null) {
            q.setParameter("unidade", unidadeAtual.getId());
        } else if (destinoAtual != null && !"".equals(destinoAtual)) {
            q.setParameter("destino", "%" + destinoAtual.toLowerCase() + "%");
        }
        return ((BigDecimal) q.getSingleResult()).intValue();
    }

    public List<VoProcesso> buscarProcessosFinalizados(FiltroListaProtocolo filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder("select distinct pro.id, pro.numero, pro.ano, pro.dataRegistro, " +
            " (select max(t.dataRegistro) from tramite t where t.processo_id = pro.id),  " +
            " coalesce(pf.nome, pj.razaoSocial), pro.assunto, pro.tipoProcessoTramite, pro.situacao, sa.nome as nomeSub, ass.nome from processo pro ");
        Query q = getQueryProcessosFinalizados(filtro, inicio, max, sql);
        return montaListaProcessos(q.getResultList());
    }

    public Integer quantidadeDeProcessosFinalizados(FiltroListaProtocolo filtro) {
        StringBuilder sql = new StringBuilder("select count(distinct pro.id) from processo pro ");
        Query q = getQueryProcessosFinalizados(filtro, 0, 0, sql);
        return ((BigDecimal) q.getSingleResult()).intValue();
    }


    private Query getQueryProcessosFinalizados(FiltroListaProtocolo filtro, int inicio, int max, StringBuilder sql) {
        sql.append(" inner join tramite tra on tra.processo_id = pro.id ")
            .append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" left join subAssunto sa on sa.id = pro.subAssunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" where (coalesce(tra.unidadeorganizacional_id, tra.origem_id) in (select uo.id from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario and usuuni.gestorProtocolo = :gestor)) ")
            .append(" and pro.protocolo = :protocolo ")
            .append(" and tra.id = coalesce(pro.tramiteFinalizador_id,0) ")
            .append(" and pro.situacao = :situacao ");
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql.append(" and pro.numero = :numero ");
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            sql.append(" and pro.ano = :ano ");
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
        }
        sql.append(" order by pro.ano desc, pro.numero desc");
        Query q = em.createNativeQuery(sql.toString());
        if (max != 0) {
            q.setMaxResults(max);
            q.setFirstResult(inicio);
        }
        q.setParameter("usuario", filtro.getUsuarioSistema().getId());
        q.setParameter("gestor", filtro.getGestor());
        q.setParameter("situacao", SituacaoProcesso.FINALIZADO.name());
        q.setParameter("protocolo", filtro.getProtocolo());
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            q.setParameter("numero", filtro.getNumero().trim());
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            q.setParameter("ano", filtro.getAno());
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            q.setParameter("solicitante", "%" + filtro.getSolicitante().toLowerCase() + "%");
        }
        return q;
    }

    public List<VoProcesso> buscarProcessosArquivados(FiltroListaProtocolo filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder("select distinct pro.id, pro.numero, pro.ano, pro.dataRegistro, " +
            " (select max(t.dataRegistro) from tramite t where t.processo_id = pro.id),  " +
            " coalesce(pf.nome, pj.razaoSocial), pro.assunto, pro.tipoProcessoTramite, pro.situacao, " +
            " sa.nome as nomeSub, ass.nome from processo pro ");
        Query q = getQueryProcessosArquivados(filtro, inicio, max, sql);
        return montaListaProcessos(q.getResultList());
    }

    public Integer quantidadeDeProcessosArquivados(FiltroListaProtocolo filtro) {
        StringBuilder sql = new StringBuilder("select count(distinct pro.id) from processo pro ");
        Query q = getQueryProcessosArquivados(filtro, 0, 0, sql);
        return ((BigDecimal) q.getSingleResult()).intValue();
    }

    private Query getQueryProcessosArquivados(FiltroListaProtocolo filtro, int inicio, int max, StringBuilder sql) {
        sql.append(" inner join tramite tra on tra.processo_id = pro.id ")
            .append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" left join subAssunto sa on sa.id = pro.subAssunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" where (coalesce(tra.unidadeorganizacional_id, tra.origem_id) in (select uo.id from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario and usuuni.gestorProtocolo = :gestor)) ")
            .append(" and pro.protocolo = :protocolo ")
            .append(" and tra.id = coalesce(pro.tramiteFinalizador_id,0) ")
            .append(" and (pro.situacao = :deferido_arquivado ")
            .append(" or pro.situacao = :indeferido_arquivado ")
            .append(" or pro.situacao = :arquivado) ");
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql.append(" and pro.numero = :numero ");
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            sql.append(" and pro.ano = :ano ");
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
        }
        sql.append(" order by pro.ano desc, pro.numero desc");
        Query q = em.createNativeQuery(sql.toString());
        if (max != 0) {
            q.setMaxResults(max);
            q.setFirstResult(inicio);
        }
        q.setParameter("usuario", filtro.getUsuarioSistema().getId());
        q.setParameter("gestor", filtro.getGestor());
        q.setParameter("deferido_arquivado", SituacaoProcesso.DEFERIDOARQUIVADO.name());
        q.setParameter("indeferido_arquivado", SituacaoProcesso.INDEFERIDOARQUIVADO.name());
        q.setParameter("arquivado", SituacaoProcesso.ARQUIVADO.name());
        q.setParameter("protocolo", filtro.getProtocolo());
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            q.setParameter("numero", filtro.getNumero().trim());
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            q.setParameter("ano", filtro.getAno());
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            q.setParameter("solicitante", "%" + filtro.getSolicitante().toLowerCase() + "%");
        }
        return q;
    }

    private List<VoProcesso> montaListaProcessos(List<Object[]> lista) {
        List<VoProcesso> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            VoProcesso pro = new VoProcesso();
            pro.setId(((BigDecimal) obj[0]).longValue());
            pro.setNumero(((BigDecimal) obj[1]).intValue());
            pro.setAno(((BigDecimal) obj[2]).intValue());
            pro.setDataRegistro((Date) obj[3]);
            pro.setDataUltimoTramite((Date) obj[4]);
            pro.setNomeAutorRequerente((String) obj[5]);
            pro.setAssunto((String) obj[6]);
            pro.setTipoProcessoTramite(TipoProcessoTramite.valueOf((String) obj[7]));
            pro.setSituacao(SituacaoProcesso.valueOf((String) obj[8]));
            pro.setNomeAssuntoProcesso(obj[9] != null ? (String) obj[9] : "" + " - " + obj[10] != null ? (String) obj[10] : "");
            retorno.add(pro);
        }
        return retorno;
    }

    public void novoProcesso(Processo pr, SistemaControlador sistemaControlador) {
        pr.setSituacao(SituacaoProcesso.GERADO);
        pr.setResponsavelCadastro(sistemaControlador.getUsuarioCorrente());
        pr.setUoCadastro(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        pr.setTramites(new ArrayList<Tramite>());
        pr.setDocumentoProcesso(new ArrayList<DocumentoProcesso>());
        pr.setExercicio(exercicioFacade.getExercicioPorAno(LocalDate.now().getYear()));
        pr.setAno(LocalDate.now().getYear());
        pr.setNumero(null);
        pr.setDataRegistro(LocalDate.now().toDate());
        pr.setTipoProcessoTramite(TipoProcessoTramite.INTERNO);
    }

    public List<Processo> filtraProcesso(Integer numero, Integer ano) {
        StringBuilder hql = new StringBuilder("from Processo p");
        if (numero != null && !numero.toString().isEmpty()) {
            hql.append(" where p.numero like :numero");
        }
        if (ano != null) {
            hql.append(" and p.ano = :ano");
        } else {
            hql.append(" or p.ano = :numero");
        }
        Query q = em.createQuery(hql.toString());
        if (numero != null && !numero.toString().isEmpty()) {
            q.setParameter("numero", numero);
        }
        if (ano != null) {
            q.setParameter("ano", ano);
        }
        q.setMaxResults(30);
        return q.getResultList();
    }

    public Processo salvarArquivos(Processo processo) {
        for (ProcessoArquivo pa : processo.getArquivos()) {
            if (pa.getArquivo().getId() == null) {
                pa.setArquivo(em.merge(pa.getArquivo()));
            }
        }
        return processo;
    }

    public Integer gerarCodigo(Processo processo) {
        if (processo.getProtocolo()) {
            return singletonProtocolo.getProximoNumero(processo.getAno());
        } else {
            return singletonProcesso.getProximoNumero(processo.getAno());
        }
    }

    private List<Tramite> tramitesAceitosNaoFinalizados(boolean protocolo) {
        String sql = "select tra.* from tramite tra " +
            "inner join processo pro on pro.id = tra.processo_id " +
            "where pro.protocolo = :protocolo and tra.status = :status and tra.aceito = :aceito";
        Query q = em.createNativeQuery(sql, Tramite.class);
        if (protocolo) {
            q.setParameter("protocolo", 1);
        } else {
            q.setParameter("protocolo", 0);
        }
        q.setParameter("status", 1);
        q.setParameter("aceito", 1);
        return q.getResultList();

    }

    public void lancaNotificacoesDosPrazosDosProcessos() {
        NotificacaoService.getService().marcarComoLida(TipoNotificacao.PROTOCOLO);

        List<Tramite> tramites = tramitesAceitosNaoFinalizados(false);
        for (Tramite tramite : tramites) {
            if (tramite.getSituacaoTramite() == null || !tramite.getSituacaoTramite().getParaPrazo()) {
                Date dataControle = tramite.getDataContinuacaoPausa() != null ? tramite.getDataContinuacaoPausa() : tramite.getDataAceite();
                if (dataControle != null) {
                    int dias = 0;
                    if (TipoPrazo.DIAS.equals(tramite.getTipoPrazo())) {
                        dias = DataUtil.diasEntreDate(DataUtil.adicionaDias(new Date(), tramite.getPrazo().intValue()), dataControle);
                    } else if (TipoPrazo.HORAS.equals(tramite.getTipoPrazo())) {
                        Date dataPrazo = DataUtil.adicionaHoras(dataControle, tramite.getPrazo().intValue());
                        int horas = DataUtil.horasEntre(new Date(), dataPrazo);
                        if (horas > 24) {
                            dias = (horas / 24) + 1;
                        } else if (horas > 0) {
                            dias = 1;
                        }
                    }
                    String mensagem = "";
                    Notificacao.Gravidade gravidade = Notificacao.Gravidade.ATENCAO;
                    if (dias > 1 && dias <= 3) {
                        gravidade = Notificacao.Gravidade.ATENCAO;
                        mensagem = "Faltam " + dias + " dias para vencer o prazo do processo " + tramite.getProcesso().getNumero() + "/" + tramite.getProcesso().getAno() + " na unidade " + tramite.getUnidadeOrganizacional().getDescricao();
                    } else {
                        if (dias == 1) {
                            gravidade = Notificacao.Gravidade.ERRO;
                            mensagem = "Falta " + dias + " dia para vencer o prazo do processo " + tramite.getProcesso().getNumero() + "/" + tramite.getProcesso().getAno() + " na unidade " + tramite.getUnidadeOrganizacional().getDescricao();
                        } else if (dias <= 0) {
                            gravidade = Notificacao.Gravidade.ERRO;
                            mensagem = "O prazo do processo " + tramite.getProcesso().getNumero() + "/" + tramite.getProcesso().getAno() + " está vencido na unidade " + tramite.getUnidadeOrganizacional().getDescricao();
                        }
                    }
                    if (!"".equals(mensagem)) {
                        Notificacao not = em.merge(geraNotificacao(mensagem, "/tramite/tramitar/" + tramite.getId() + "/", gravidade, tramite.getUnidadeOrganizacional()));
                    }
                }
            }
        }
    }

    private Notificacao geraNotificacao(String mensagem, String link, Notificacao.Gravidade gravidade, UnidadeOrganizacional unidadeOrganizacional) {
        return new Notificacao(TipoNotificacao.PROTOCOLO.getDescricao(), mensagem, link, gravidade, unidadeOrganizacional, TipoNotificacao.PROTOCOLO);
    }

    public void removerNotificacaoDoTramite(Tramite tramite) {
        NotificacaoService.getService().marcarNotificaoComoLidaPorIdDoLink(tramite.getId());
    }

    public void removerNotificacaoDoProcesso(Processo processo) {
        NotificacaoService.getService().marcarNotificaoComoLidaPorIdDoLink(processo.getId());
    }

    public void lancaNotificacoesDosProcessosNaoFinalizados() {
        for (Processo processo : buscarProcessosNaoFinalizados()) {
            Notificacao not = em.merge(geraNotificacao("O processo " + processo.getNumero() + "/" + processo.getAno() + " não foi finalizado", "/processo/gerir/" + processo.getId() + "/", Notificacao.Gravidade.ERRO, processo.getUoCadastro()));
        }
    }

    public List<Processo> buscarProcessosNaoFinalizados() {
        String sql = "select * " +
            "           from processo p " +
            "        where p.protocolo = 0 " +
            "          and p.situacao in ('" + SituacaoProcesso.GERADO.name() + "','" + SituacaoProcesso.EMTRAMITE.name() + "') " +
            "          and not exists (" +
            "                          select tra.id " +
            "                            from tramite tra " +
            "                          where tra.processo_id = p.id " +
            "                            and tra.status = 1  " +
            "                          )";

        Query q = em.createNativeQuery(sql, Processo.class);
        return q.getResultList();
    }

    public List<Processo> buscarProcessoPorNumeroEAno(Integer numero, Integer ano) {
        StringBuilder hql = new StringBuilder("select p from Processo p");
        hql.append(" where p.protocolo = :protocolo ");
        if (numero != null && !numero.toString().isEmpty()) {
            hql.append(" and ").append(" p.numero = :numero");
        }
        if (ano != null) {
            hql.append(" and ").append(" p.ano = :ano");
        }
        Query q = em.createQuery(hql.toString());
        if (numero != null && !numero.toString().isEmpty()) {
            q.setParameter("numero", numero);
        }
        if (ano != null) {
            q.setParameter("ano", ano);
        }
        q.setParameter("protocolo", true);
        q.setMaxResults(30);
        return q.getResultList();
    }

    public Processo buscarProcessoPeloEnglobado(Processo englobado) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select proc.* from processoenglobado proceng ")
            .append(" inner join processo proc on proceng.processo_id = proc.id ")
            .append(" where proceng.englobado_id = :englobadoId ")
            .append("  and proceng.status = :status ");
        Query q = em.createNativeQuery(sql.toString(), Processo.class);
        q.setParameter("englobadoId", englobado.getId());
        q.setParameter("status", StatusProcessoEnglobado.INCORPORADO.name());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (Processo) q.getSingleResult();
    }

    public ProcessoEnglobado buscarProcessoEnglobado(Long idProcesso) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select proceng.* from processoenglobado proceng ")
            .append(" where proceng.englobado_id = :englobadoId ");
        Query q = em.createNativeQuery(sql.toString(), ProcessoEnglobado.class);
        q.setParameter("englobadoId", idProcesso);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (ProcessoEnglobado) q.getSingleResult();
    }

    public boolean isUsuarioGestorUnidadeDestino(UsuarioSistema usuarioSistema, UnidadeOrganizacional unidadeOrganizacional) {
        StringBuilder sql = new StringBuilder("select 1 from UsuarioSistema usuSis ")
            .append(" inner join UsuarioUnidadeOrganizacio usuuni on ususis.id = usuuni.usuarioSistema_id ")
            .append(" inner join UnidadeOrganizacional uo on usuuni.unidadeOrganizacional_id = uo.id ")
            .append(" where ususis.id = :usuario and usuuni.gestorProtocolo = :gestor  ")
            .append("  and uo.id = :unidade ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("usuario", usuarioSistema.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("gestor", 1);
        return !q.getResultList().isEmpty();
    }

    public void salvarProcessoEnglobadoProtocoloDesmembrado(ProcessoEnglobado processoEnglobado, Tramite ultimoTramiteEnglobado) {
        processoEnglobado.setStatus(StatusProcessoEnglobado.DESMEMBRADO);
        Processo processo = recuperar(processoEnglobado.getProcesso().getId());
        Processo englobado = recuperar(processoEnglobado.getEnglobado().getId());
        englobado.setSituacao(SituacaoProcesso.EMTRAMITE);

        String descricao = Processo.PROTOCOLO_DESMENBRADO + " do " + processo.toString() + " - Motivo: " + processoEnglobado.getMotivoDesmembramento();
        ProcessoHistorico processoHistorico = englobado.criarHistoricoProcesso(descricao, ultimoTramiteEnglobado.getUnidadeOrganizacional(), sistemaFacade.getUsuarioCorrente());
        englobado.getHistoricos().add(processoHistorico);
        englobado = salvarProcesso(englobado);
        processoEnglobado.setEnglobado(englobado);
        salvarProcesso(processo);
        em.merge(processoEnglobado);
    }

    public void salvarReaberturaProtocolo(Processo processo, Tramite tramite) {
        String descricao = Processo.PROTOCOLO_REABERTO + " - Responsável: " + tramite.getUsuarioTramite() + " - Motivo: " + tramite.getMotivo();
        ProcessoHistorico processoHistorico = processo.criarHistoricoProcesso(descricao, tramite.getUnidadeOrganizacional(), tramite.getUsuarioTramite());
        em.merge(tramite);
        em.merge(processoHistorico);
        em.merge(processo);
    }

    public List<Tramite> buscarTramitesDoProcesso(Long idProcesso) {
        String sql = "select tr.* from Tramite tr where tr.processo_id = :idProcesso" +
            " order by tr.indice";
        return em.createNativeQuery(sql, Tramite.class).setParameter("idProcesso", idProcesso).getResultList();
    }

    public List<VoProcesso> atualizarUnidadeAtualProcessos(List<VoProcesso> processos) {
        for (VoProcesso processo : processos) {
            List<Tramite> tramites = buscarTramitesDoProcesso(processo.getId());
            for (Tramite tramite : tramites) {
                processo.setUnidadeAtual(tramite.getDestinoTramite());
            }
        }
        return processos;
    }

    public boolean hasTramiteMaisNovo(Processo processo, int indice) {
        try {
            String sql = "select t.id from Tramite t where t.processo_id = :idProcesso and t.indice > :indice";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idProcesso", processo.getId());
            q.setParameter("indice", indice);
            return !q.getResultList().isEmpty();
        } catch (Exception ex) {
            logger.error("Erro ao buscar o trâmite mais recente: {}", ex);
            return false;
        }
    }

    public Long recuperarIdRevisaoHistoricoProcesso(ProcessoHistorico processoHistorico) {
        StringBuilder sql = new StringBuilder();

        sql.append(" select rev.id from processo_aud aud ")
            .append(" inner join revisaoauditoria rev on rev.id = aud.rev ")
            .append(" where rev.id = (select rev from processohistorico_aud paud where id = :idProcessoHistorico and paud.revtype = 0) ");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idProcessoHistorico", processoHistorico.getId());

        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }




    public List<Processo> buscarProcessosComTramiteUnidadeOrigemNula() {
        String sql = " select p.* from processo p " +
            " inner join tramite t on t.processo_id = p.id " +
            " where t.origem_id is null and rownum <= 500 ";
        Query q = em.createNativeQuery(sql, Processo.class);
        List<Processo> processos = (List<Processo>) q.getResultList();
        for (Processo processo : processos) {
            processo.getTramites().size();
        }
        return q.getResultList();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public Future<AssistenteBarraProgresso> corrigirUnidadeOrigemTramites(List<Processo> processos, AssistenteBarraProgresso assistente) {

        assistente.zerarContadoresProcesso();
        assistente.setTotal(processos.size());
        assistente.setDescricaoProcesso("Inserindo Unidade Origem ao Trâmite...");
        for (Processo processo : processos) {
            Collections.sort(processo.getTramites());
            for (Tramite tramite : processo.getTramites()) {
                if (tramite.getProcesso().equals(processo)) {
                    if (tramite.getIndice() == 0) {
                        if (tramite.getOrigem() == null) {
                            tramite.setOrigem(processo.getUoCadastro());
                            em.merge(tramite);
                        }
                    } else {
                        if (tramite.getOrigem() == null) {
                            Tramite tramiteAnterior = processo.getTramites().get(tramite.getIndice() - 1);
                            tramite.setOrigem(tramiteAnterior.getUnidadeOrganizacional());
                            em.merge(tramite);
                        }
                    }
                }
            }
            assistente.conta();
        }
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("");
        return new AsyncResult<>(assistente);
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public List<VoProcesso> buscarProcessosCancelados(FiltroListaProtocolo filtro, int inicio, Integer max) {
        StringBuilder sql = new StringBuilder("select distinct pro.id, pro.numero, pro.ano, pro.dataRegistro, " +
            " (select max(t.dataRegistro) from tramite t where t.processo_id = pro.id),  " +
            " coalesce(pf.nome, pj.razaoSocial), pro.assunto, pro.tipoProcessoTramite, pro.situacao, sa.nome as nomeSub, ass.nome from processo pro ");

        Query q = getQueryProcessosCancelados(filtro, inicio, max, sql);
        List<Processo> lista = q.getResultList();

        return montaListaProcessos(q.getResultList());
    }

    private Query getQueryProcessosCancelados(FiltroListaProtocolo filtro, int inicioConsulta, Integer max, StringBuilder sql) {

        sql.append(" inner join tramite tra on tra.processo_id = pro.id ")
            .append(" left join pessoafisica pf on pf.id = pro.pessoa_id ")
            .append(" left join pessoajuridica pj on pj.id = pro.pessoa_id ")
            .append(" left join subAssunto sa on sa.id = pro.subAssunto_id ")
            .append(" left join assunto ass on ass.id = sa.assunto_id ")
            .append(" where pro.situacao = :situacao");

        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            sql.append(" and pro.numero = :numero ");
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            sql.append(" and pro.ano = :ano ");
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            sql.append(" and (pro.pessoa_id in(select pf.id from PessoaFisica pf where lower(pf.nome) like :solicitante or pf.cpf like :solicitante) ")
                .append(" or pro.pessoa_id in(select pj.id from PessoaJuridica pj where lower(pj.razaoSocial) like :solicitante or pj.cnpj like :solicitante))");
        }
        sql.append(" order by pro.ano desc, pro.numero desc");
        Query q = em.createNativeQuery(sql.toString());
        if (max != 0) {
            q.setMaxResults(max);
            q.setFirstResult(inicioConsulta);
        }

        q.setParameter("situacao", SituacaoProcesso.CANCELADO.name());
        if (!Strings.isNullOrEmpty(filtro.getNumero())) {
            q.setParameter("numero", filtro.getNumero().trim());
        }
        if (!Strings.isNullOrEmpty(filtro.getAno())) {
            q.setParameter("ano", filtro.getAno());
        }
        if (!Strings.isNullOrEmpty(filtro.getSolicitante())) {
            q.setParameter("solicitante", "%" + filtro.getSolicitante().toLowerCase() + "%");
        }
        return q;
    }

    public Integer quantidadeDeProcessosCancelados(FiltroListaProtocolo filtro) {
        StringBuilder sql = new StringBuilder("select count(distinct pro.id) from processo pro ");
        Query q = getQueryProcessosCancelados(filtro, 0, 0, sql);
        return ((BigDecimal) q.getSingleResult()).intValue();
    }
}
