package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.*;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.singletons.SingletonGeradorCodigoPorExercicio;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ProcessoLicenciamentoAmbientalFacade extends AbstractFacade<ProcessoLicenciamentoAmbiental> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DocumentacaoLicenciamentoAmbientalFacade documentacaoLicenciamentoAmbientalFacade;
    @EJB
    private DividaLicenciamentoAmbientalFacade dividaLicenciamentoAmbientalFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private UnidadeMedidaAmbientalFacade unidadeMedidaAmbientalFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private GeraValorDividaLicenciamentoAmbiental geraValorDividaLicenciamentoAmbiental;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private SingletonGeradorCodigoPorExercicio singletonGeradorCodigoPorExercicio;
    @EJB
    private TecnicoLicenciamentoAmbientalFacade tecnicoLicenciamentoAmbientalFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private AssuntoLicenciamentoAmbientalFacade assuntoLicenciamentoAmbientalFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ControleLicenciamentoAmbientalFacade controleLicenciamentoAmbientalFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ParametroLicenciamentoAmbientalFacade parametroLicenciamentoAmbientalFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ParametroLicenciamentoAmbientalFacade getParametroLicenciamentoAmbientalFacade() {
        return parametroLicenciamentoAmbientalFacade;
    }

    public DividaLicenciamentoAmbientalFacade getDividaLicenciamentoAmbientalFacade() {
        return dividaLicenciamentoAmbientalFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public UnidadeMedidaAmbientalFacade getUnidadeMedidaAmbientalFacade() {
        return unidadeMedidaAmbientalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public CNAEFacade getCnaeFacade() {
        return cnaeFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public AssuntoLicenciamentoAmbientalFacade getAssuntoLicenciamentoAmbientalFacade() {
        return assuntoLicenciamentoAmbientalFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ControleLicenciamentoAmbientalFacade getControleLicenciamentoAmbientalFacade() {
        return controleLicenciamentoAmbientalFacade;
    }

    public ProcessoLicenciamentoAmbientalFacade() {
        super(ProcessoLicenciamentoAmbiental.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ProcessoLicenciamentoAmbiental recuperar(Object id) {
        ProcessoLicenciamentoAmbiental processo = super.recuperar(id);
        if (processo == null) return null;
        if (processo.getDocumentos() != null) {
            Hibernate.initialize(processo.getDocumentos());
        }
        if (processo.getProcessoUnidadeMedidas() != null) {
            Hibernate.initialize(processo.getProcessoUnidadeMedidas());
        }
        if (processo.getRequerente() != null && processo.getRequerente().getPessoa() != null) {
            Hibernate.initialize(processo.getRequerente().getPessoa().getEnderecoscorreio());
        }
        if (processo.getAssinaturaDiretor() != null) {
            processo.getAssinaturaDiretor().getSecretario().setArquivo(arquivoFacade.recuperaDependencias(processo.getAssinaturaDiretor().getSecretario().getArquivo().getId()));
        }
        if (processo.getAssinaturaSecretario() != null) {
            processo.getAssinaturaSecretario().getSecretario().setArquivo(arquivoFacade.recuperaDependencias(processo.getAssinaturaSecretario().getSecretario().getArquivo().getId()));
        }
        Hibernate.initialize(processo.getCnaes());
        Hibernate.initialize(processo.getHistoricosAlteracoes());
        Hibernate.initialize(processo.getTecnicos());
        processo.setAssuntoLicenciamentoAmbiental(assuntoLicenciamentoAmbientalFacade.recuperar(processo.getAssuntoLicenciamentoAmbiental().getId()));
        return processo;
    }

    public void inicializarDocumentosObrigatorios(ProcessoLicenciamentoAmbiental processo) {
        processo.setDocumentos(new ArrayList<>());
        DocumentacaoLicenciamentoAmbiental documentacaoLicenciamento = documentacaoLicenciamentoAmbientalFacade.recuperarUltimo();
        if (documentacaoLicenciamento != null) {
            for (DocumentoLicenciamentoAmbiental documento : documentacaoLicenciamento.getDocumentos()) {
                if (processo.getAssuntoLicenciamentoAmbiental() != null &&
                    (documento.getAtivo()
                        && (documento.getAssuntoLicenciamentoAmbiental() == null
                        || documento.getAssuntoLicenciamentoAmbiental().getId()
                        .equals(processo.getAssuntoLicenciamentoAmbiental().getId())))) {
                    processo.getDocumentos().add(new ProcessoLicenciamentoAmbientalDocumento(documento, processo));
                }
            }
        }
    }

    @Override
    public void preSave(ProcessoLicenciamentoAmbiental entidade) {
        entidade.validarCamposObrigatorios();
        if (entidade.getStatus() == null) {
            entidade.setStatus(StatusLicenciamentoAmbiental.ABERTO);
        }
        if (entidade.getId() == null) {
            entidade.setSequencial(getProximoCodigo(entidade.getExercicio()));
        }
        if (entidade.getTecnicos().isEmpty()) {
            inserirTecnicoProcessoLicenciamento(entidade, sistemaFacade.getUsuarioCorrente());
        }
    }

    public Long getProximoCodigo(Exercicio exercicio) {
        return singletonGeradorCodigoPorExercicio.getProximoCodigoDoExercicio(ProcessoLicenciamentoAmbiental.class,
            "sequencial", "exercicio_id", exercicio);
    }

    public List<ProcessoLicenciamentoAmbiental> buscarProcessosPorParte(String parte) {
        return em.createNativeQuery(" select p.* from processolicenciamentoambiental p " +
                "  inner join assuntolicenciamentoambiental a on a.id = p.assuntolicenciamentoambiental_id " +
                "  inner join processolicenciamentoambientalpessoa r on r.id = p.requerente_id " +
                "  left join pessoafisica pf on pf.id = r.pessoa_id " +
                "  left join pessoajuridica pj on pj.id = r.pessoa_id " +
                " where lower(a.descricao) like :parte " +
                "  or coalesce(pf.cpf, pj.cnpj) like :parte " +
                "  or lower(coalesce(pf.nome, pj.razaosocial)) like :parte " +
                " order by p.id desc ", ProcessoLicenciamentoAmbiental.class)
            .setParameter("parte", "%" + parte.trim().toLowerCase() + "%")
            .getResultList();
    }

    public ControleLicenciamentoAmbiental buscarUltimoControle(ProcessoLicenciamentoAmbiental processo) {
        List resultList = em.createNativeQuery(" select c.* from controlelicenciamentoambiental c " +
                " where c.processo_id = :processo_id " +
                " order by c.datahora desc " +
                " fetch first 1 row only ", ControleLicenciamentoAmbiental.class)
            .setParameter("processo_id", processo.getId())
            .getResultList();
        return !resultList.isEmpty() ? (ControleLicenciamentoAmbiental) resultList.get(0) : null;
    }

    public void definirStatusAtual(ProcessoLicenciamentoAmbiental processo, StatusLicenciamentoAmbiental status) {
        processo = recuperar(processo.getId());
        processo.setStatus(status);
        gravarHistoricoSituacao(processo);
        em.merge(processo);
    }

    private void gravarHistoricoSituacao(ProcessoLicenciamentoAmbiental processo) {
        HistoricoProcessoLicenciamentoAmbiental historico = new HistoricoProcessoLicenciamentoAmbiental();
        historico.setDataAlteracao(new Date());
        historico.setProcessoLicenciamentoAmbiental(processo);
        historico.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        historico.setStatus(processo.getStatus());
        processo.getHistoricosAlteracoes().add(historico);
    }

    private boolean podeGerarDebito(List<ControleLicenciamentoAmbiental> pareceres, ProcessoLicenciamentoAmbiental processo) {
        AssuntoLicenciamentoAmbiental asssunto = assuntoLicenciamentoAmbientalFacade.recuperar(processo.getAssuntoLicenciamentoAmbiental().getId());
        if (processo.getProcessoCalculoLicenciamentoAmbiental() != null) return false;

        if (pareceres.isEmpty() && asssunto.getSituacoesEmissaoDebito().size() == 1
            && StatusLicenciamentoAmbiental.ABERTO.equals(asssunto.getSituacoesEmissaoDebito().get(0).getStatus())
            && processo.getStatus().equals(StatusLicenciamentoAmbiental.ABERTO)) return true;

        for (SituacaoEmissaoDebitoLicenciamentoAmbiental situacao : asssunto.getSituacoesEmissaoDebito()) {
            boolean temNoParecer = false;
            boolean temNaRevisao = false;
            for (ControleLicenciamentoAmbiental parecer : pareceres) {
                if (situacao.getStatus().equals(parecer.getStatus())) {
                    temNoParecer = true;
                    break;
                }
                for (RevisaoControleLicenciamentoAmbiental revisao : parecer.getRevisoes()) {
                    if (situacao.getStatus().equals(revisao.getSituacao())) {
                        temNaRevisao = true;
                        break;
                    }
                }
            }
            if (!temNoParecer && !temNaRevisao && !StatusLicenciamentoAmbiental.ABERTO.equals(situacao.getStatus())) {
                return false;
            }
        }
        return true;
    }

    public void gerarDebitoParecer(ProcessoLicenciamentoAmbiental processo, UsuarioSistema usuarioSistema) throws Exception {
        List<ControleLicenciamentoAmbiental> pareceresDoProcesso = controleLicenciamentoAmbientalFacade.buscarControlesPorProcesso(processo);
        if (!podeGerarDebito(pareceresDoProcesso, processo)) return;
        DividaLicenciamentoAmbiental dividaLicenciamento = dividaLicenciamentoAmbientalFacade.recuperarUltimo();
        if (dividaLicenciamento == null) {
            throw new ValidacaoException("Nenhuma dívida de licenciamento ambiental encontrada.");
        }
        ProcessoCalculoLicenciamentoAmbiental processoCalculo =
            criarProcessoCalculoLicenciamentoAmbiental(processo.getExercicio(),
                usuarioSistema, processo, dividaLicenciamento.getDivida(), null, null);
        processoCalculo = em.merge(processoCalculo);
        geraValorDividaLicenciamentoAmbiental.gerarDebito(processoCalculo, false);
        atribuirProcessoCalculoAoProcesso(processo.getId(), processoCalculo.getId(), false);
    }

    public void gerarDebito(ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental,
                            UsuarioSistema usuarioSistema, boolean taxaExpediente) throws Exception {
        DividaLicenciamentoAmbiental dividaLicenciamentoAmbiental = dividaLicenciamentoAmbientalFacade.recuperarUltimo();
        if (dividaLicenciamentoAmbiental == null) {
            throw new ValidacaoException("Nenhuma dívida de licenciamento ambiental encontrada.");
        }
        if (taxaExpediente && dividaLicenciamentoAmbiental.getDividaTaxaExpediente() == null) {
            throw new ValidacaoException("Nenhuma dívida da taxa de expediente encontrada.");
        }
        List<ControleLicenciamentoAmbiental> pareceresDoProcesso = controleLicenciamentoAmbientalFacade.buscarControlesPorProcesso(processoLicenciamentoAmbiental);
        if (!taxaExpediente && !podeGerarDebito(pareceresDoProcesso, processoLicenciamentoAmbiental)) return;
        if (taxaExpediente && processoLicenciamentoAmbiental.getProcessoCalculoTaxaExpediente() != null) return;
        Divida divida = taxaExpediente ? dividaLicenciamentoAmbiental.getDividaTaxaExpediente() : dividaLicenciamentoAmbiental.getDivida();
        Tributo tributo = taxaExpediente ? dividaLicenciamentoAmbiental.getTributoTaxaExpediente() : null;
        BigDecimal valorTaxaExpediente = taxaExpediente ? dividaLicenciamentoAmbiental.getValorTaxaExpediente() : null;
        ProcessoCalculoLicenciamentoAmbiental processoCalculo =
            criarProcessoCalculoLicenciamentoAmbiental(processoLicenciamentoAmbiental.getExercicio(),
                usuarioSistema, processoLicenciamentoAmbiental, divida, tributo, valorTaxaExpediente);
        processoCalculo = em.merge(processoCalculo);
        geraValorDividaLicenciamentoAmbiental.gerarDebito(processoCalculo, false);
        atribuirProcessoCalculoAoProcesso(processoLicenciamentoAmbiental.getId(), processoCalculo.getId(), taxaExpediente);
    }

    private void atribuirProcessoCalculoAoProcesso(Long idProcesso, Long idProcessoCalculo, boolean taxaExpediente) {
        String colunaParaAtualizar = taxaExpediente ? "processocalculotaxaexpediente_id" : "processocalculolicenciamentoambiental_id";
        String sql = "update processolicenciamentoambiental set " + colunaParaAtualizar + " = :idProcessoCalculo where id = :idProcesso and " + colunaParaAtualizar + " is null";
        em.createNativeQuery(sql)
            .setParameter("idProcessoCalculo", idProcessoCalculo)
            .setParameter("idProcesso", idProcesso)
            .executeUpdate();
    }

    private ProcessoCalculoLicenciamentoAmbiental criarProcessoCalculoLicenciamentoAmbiental(Exercicio exercicio,
                                                                                             UsuarioSistema usuarioSistema,
                                                                                             ProcessoLicenciamentoAmbiental processo,
                                                                                             Divida divida, Tributo tributo, BigDecimal valorTaxaExpediente) {
        ProcessoCalculoLicenciamentoAmbiental processoCalculo = new ProcessoCalculoLicenciamentoAmbiental();
        processoCalculo.setExercicio(exercicio);
        processoCalculo.setUsuarioSistema(usuarioSistema);
        processoCalculo.setDivida(divida);
        processoCalculo.setDataLancamento(new Date());
        processoCalculo.setDescricao("Processo de Licenciamento Ambiental: " + processo);
        processoCalculo.getCalculos().add(criarCalculoLicenciamentoAmbiental(processo, processoCalculo, tributo, valorTaxaExpediente));
        return processoCalculo;
    }

    private CalculoLicenciamentoAmbiental criarCalculoLicenciamentoAmbiental(ProcessoLicenciamentoAmbiental processo, ProcessoCalculoLicenciamentoAmbiental processoCalculo, Tributo tributo, BigDecimal valorEmReais) {
        CalculoLicenciamentoAmbiental calculo = new CalculoLicenciamentoAmbiental();
        calculo.setProcessoCalculoLicenciamentoAmbiental(processoCalculo);
        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setCalculo(calculo);
        calculoPessoa.setPessoa(processo.getRequerente().getPessoa());
        calculo.getPessoas().add(calculoPessoa);
        calculo.getItensCalculo().addAll(criarItemCalculoLicenciamentoAmbiental(processo, calculo, tributo, valorEmReais));
        calculo.somarValores();
        return calculo;
    }

    private List<ItemCalculoLicenciamentoAmbiental> criarItemCalculoLicenciamentoAmbiental(ProcessoLicenciamentoAmbiental processo, CalculoLicenciamentoAmbiental calculo, Tributo tributo, BigDecimal valorEmReais) {
        AssuntoLicenciamentoAmbiental assunto = processo.getAssuntoLicenciamentoAmbiental();
        if (assunto.getTributo() == null) {
            throw new ValidacaoException("Tributo não definido para o assunto " +
                assunto.getDescricao());
        }
        List<ItemCalculoLicenciamentoAmbiental> itens = Lists.newArrayList();
        if (tributo != null && valorEmReais != null && valorEmReais.compareTo(BigDecimal.ZERO) > 0) {
            ItemCalculoLicenciamentoAmbiental itemCalculo = new ItemCalculoLicenciamentoAmbiental();
            itemCalculo.setCalculoLicenciamentoAmbiental(calculo);
            itemCalculo.setTributo(tributo);
            itemCalculo.setValorUFM(BigDecimal.ZERO);
            itemCalculo.setValor(valorEmReais);
            itens.add(itemCalculo);
        } else {
            for (ProcessoLicenciamentoAmbientalUnidadeMedida unidadeMedidaProcesso : processo.getProcessoUnidadeMedidas()) {
                ItemCalculoLicenciamentoAmbiental itemCalculo = new ItemCalculoLicenciamentoAmbiental();
                itemCalculo.setCalculoLicenciamentoAmbiental(calculo);
                itemCalculo.setTributo(assunto.getTributo());
                itemCalculo.setValorUFM(unidadeMedidaProcesso.getValorUfmCategoria());
                try {
                    BigDecimal valorEmReaisCategoria = moedaFacade.converterToRealPorExercicio(itemCalculo.getValorUFM(),
                        calculo.getProcessoCalculo().getExercicio());
                    itemCalculo.setValor(unidadeMedidaProcesso.getValorUnidadeMedidaAmbiental().multiply(valorEmReaisCategoria));
                    itens.add(itemCalculo);
                } catch (UFMException e) {
                    throw new ValidacaoException(e.getMessage());
                }
            }
        }
        return itens;
    }

    public List<ProcessoCalculoLicenciamentoAmbiental> recuperarProcessoCalculo(ProcessoLicenciamentoAmbiental processo) {
        List<ProcessoCalculoLicenciamentoAmbiental> retorno = Lists.newArrayList();
        if (processo.getProcessoCalculoTaxaExpediente() != null) {
            ProcessoCalculoLicenciamentoAmbiental processoCalculo = em.find(ProcessoCalculoLicenciamentoAmbiental.class, processo.getProcessoCalculoTaxaExpediente().getId());
            Hibernate.initialize(processoCalculo.getCalculos());
            retorno.add(processoCalculo);
        }
        if (processo.getProcessoCalculoLicenciamentoAmbiental() != null) {
            ProcessoCalculoLicenciamentoAmbiental processoCalculo = em.find(ProcessoCalculoLicenciamentoAmbiental.class, processo.getProcessoCalculoLicenciamentoAmbiental().getId());
            Hibernate.initialize(processoCalculo.getCalculos());
            retorno.add(processoCalculo);
        }
        return retorno;
    }

    public Long buscarUltimoSequencialTecnicoAtribuido() {
        Query query = em.createNativeQuery(" select coalesce(t.sequencial, 0) as sequencial " +
            "   from tecnicoprocessolicenciamentoambiental tpla" +
            "  inner join tecnicolicenciamentoambiental t on t.id = tpla.tecnicolicenciamentoambiental_id " +
            " where coalesce(tpla.principal, 0) = 1 " +
            " order by tpla.dataregistro desc " +
            " fetch first 1 row only ");
        List resultList = query.getResultList();
        return !resultList.isEmpty() ? ((Number) resultList.get(0)).longValue() : 0;
    }

    public void inserirTecnicoProcessoLicenciamento(ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental,
                                                    UsuarioSistema usuarioSistema) {
        processoLicenciamentoAmbiental.setTecnicos(Lists.newArrayList());
        Long ultimoSequencialTecnicoAtribuido = buscarUltimoSequencialTecnicoAtribuido();
        TecnicoLicenciamentoAmbiental tecnico = tecnicoLicenciamentoAmbientalFacade
            .getProximoTecnicoAtivoPorSequencial(ultimoSequencialTecnicoAtribuido + 1);
        if (tecnico != null) {
            TecnicoProcessoLicenciamentoAmbiental tecnicoProcesso = new TecnicoProcessoLicenciamentoAmbiental();
            tecnicoProcesso.setDataRegistro(new Date());
            tecnicoProcesso.setUsuarioRegistro(usuarioSistema);
            tecnicoProcesso.setProcessoLicenciamentoAmbiental(processoLicenciamentoAmbiental);
            tecnicoProcesso.setTecnicoLicenciamentoAmbiental(tecnico);
            tecnicoProcesso.setPrincipal(Boolean.TRUE);
            processoLicenciamentoAmbiental.getTecnicos().add(tecnicoProcesso);
        }
    }

    public ProcessoLicenciamentoAmbiental gerarDocumentoOficial(ProcessoLicenciamentoAmbiental processo) throws AtributosNulosException, UFMException {
        processo = recuperar(processo.getId());
        if (processo.getAssuntoLicenciamentoAmbiental().getTipoDoctoOficial() == null) {
            throw new ValidacaoException("Não foi configurado o Tipo de Documento Oficial para o Assunto " +
                processo.getAssuntoLicenciamentoAmbiental().getDescricao());
        }
        processo.setDocumentoOficial(null);
        DocumentoOficial documentoOficial = documentoOficialFacade.gerarDocumento(processo,
            processo.getDocumentoOficial(), null, processo.getRequerente().getPessoa(),
            processo.getAssuntoLicenciamentoAmbiental().getTipoDoctoOficial());
        processo.setDocumentoOficial(documentoOficial);
        return em.merge(processo);
    }

    public void emitirDocumentoOficial(DocumentoOficial documentoOficial) {
        documentoOficialFacade.emiteDocumentoOficial(documentoOficial);
    }

    public ControleLicenciamentoAmbiental buscarUltimoControlePorStatus(ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental,
                                                                        StatusLicenciamentoAmbiental status) {
        try {
            return (ControleLicenciamentoAmbiental) em.createNativeQuery(" select * " +
                    "   from controlelicenciamentoambiental c " +
                    " where c.id = (select max(s.id)" +
                    "                  from controlelicenciamentoambiental s " +
                    "               where s.processo_id = :processo_id" +
                    "                 and s.status = :status) ", ControleLicenciamentoAmbiental.class)
                .setParameter("processo_id", processoLicenciamentoAmbiental.getId())
                .setParameter("status", status.name())
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void finalizarProcessoPortal(ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental) throws Exception {
        processoLicenciamentoAmbiental.setSequencial(getProximoCodigo(processoLicenciamentoAmbiental.getExercicio()));
        inserirTecnicoProcessoLicenciamento(processoLicenciamentoAmbiental, null);
        salvar(processoLicenciamentoAmbiental);
        gerarDebito(processoLicenciamentoAmbiental, null, false);
    }

    public void salvarFlagMostrarNoPortal(AnexoControleLicenciamentoAmbiental anexo, boolean adicionar) {
        String sql = "update anexocontrolelicenciamentoambiental anexo set mostrarnoportalcontribuinte = :valor where id = :idAnexo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("valor", adicionar);
        q.setParameter("idAnexo", anexo.getId());
        q.executeUpdate();
    }
}
