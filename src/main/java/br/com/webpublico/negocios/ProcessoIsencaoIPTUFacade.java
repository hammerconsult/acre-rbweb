package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.enums.TipoLancamentoIsencaoIPTU;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoIptuDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoTributario;
import br.com.webpublico.singletons.SingletonGeradorCodigoPorExercicio;
import br.com.webpublico.util.DetailProcessAsync;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ProcessoIsencaoIPTUFacade extends AbstractFacade<ProcessoIsencaoIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private CategoriaIsencaoIPTUFacade categoriaIsencaoIPTUFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ProcessoIsencaoIPTU processo;
    private CadastroImobiliario cadastroImobiliario;
    private List<IsencaoCadastroImobiliario> isencoes;
    @EJB
    private SingletonGeradorCodigoPorExercicio singletonGeradorCodigoPorExercicio;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ParecerProcIsencaoIPTUFacade isencaoIPTUFacade;

    public ProcessoIsencaoIPTUFacade() {
        super(ProcessoIsencaoIPTU.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(ProcessoIsencaoIPTU processo) {
        if (processo.getId() != 0 && processo.getIsencoes() != null && !processo.getIsencoes().isEmpty()) {
            for (IsencaoCadastroImobiliario ins : processo.getIsencoes()) {
                ins.setInicioVigencia(processo.getDataLancamento());
                ins.setFinalVigencia(processo.getValidade());
                salvar(ins);
            }
        }
        super.salvar(processo);
    }

    public void salvar(IsencaoCadastroImobiliario isencao) {
        em.merge(isencao);
    }

    @Override
    public ProcessoIsencaoIPTU recuperar(Object id) {
        return inicializar(super.recuperar(id), true);
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return inicializar((ProcessoIsencaoIPTU) super.recuperar(entidade, id), true);
    }

    public ProcessoIsencaoIPTU recuperarMaisSimples(ProcessoIsencaoIPTU processoIsencaoIPTU) {
        return inicializar(super.recuperar(processoIsencaoIPTU.getId()), false);
    }

    public ProcessoIsencaoIPTU inicializar(ProcessoIsencaoIPTU p, boolean tudo) {
        Hibernate.initialize(p.getArquivos());
        Hibernate.initialize(p.getIsencoes());
        Hibernate.initialize(p.getCadastrosNaoEnquadrados());
        Hibernate.initialize(p.getCadastrosEnquadrados());
        for (IsencaoCadastroImobiliario ins : p.getIsencoes()) {
            Hibernate.initialize(ins.getCadastroImobiliario().getPropriedade());
        }
        if (tudo) {
            for (CadastroIsencaoIPTU cadastrosEnquadrado : p.getCadastrosEnquadrados()) {
                Hibernate.initialize(cadastrosEnquadrado.getCadastro().getPropriedade());
            }
        }
        return p;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<CadastroImobiliario> buscarCadastros(CategoriaIsencaoIPTU cat, String inicio, String fim, boolean enquadrar) {
        String and = montarWhere(cat, inicio, fim, enquadrar);
        String sql = "select cadastros.id, cadastros.inscricao, cadastros.AREALOTE, cadastros.areaconstruida, cadastros.qntimoveis, cadastros.fracaoIdeal, " +
            " coalesce(pf.nome, pj.razaosocial) as nome, " +
            " coalesce(pf.cpf, pj.cnpj) as cpfcnpj " +
            " from (select " +
            " ci.id as ID, " +
            " ci.inscricaocadastral AS INSCRICAO, " +
            " lote.AREALOTE AS AREALOTE, " +
            " lote.CODIGOLOTE as lote, " +
            " coalesce((select sum(construcao.areaconstruida) from construcao where imovel_id = ci.id and coalesce(cancelada,0) <> 1),0) as areaconstruida, " +
            " ci.ATIVO as ativo, " +
            " (select sum(bci.valor) from eventocalculoconstrucao bci " +
            "            inner join eventoconfiguradobci conf on conf.id = bci.eventocalculo_id " +
            "            inner join eventocalculo evento on evento.id = conf.eventocalculo_id " +
            "            inner join construcao cons on bci.construcao_id = cons.id " +
            "            where cons.imovel_id = ci.id and evento.identificacao = 'fracaoIdeal') as fracaoIdeal, " +
            " coalesce((select count(prop.id) from PROPRIEDADE prop " +
            "            inner join cadastroimobiliario cad on cad.id = prop.imovel_id " +
            "            where prop.pessoa_id = propriedade.pessoa_id and prop.finalvigencia is null and cad.ativo = 1),0) as qntimoveis, " +
            " pessoa.id as pessoa, " +
            " (select valor from (select sum(coalesce(item.valorreal, 0)) as valor, c.datacalculo as dtcalc, " +
            "                                       rank() over (partition by ex.ano order by c.datacalculo desc) rnk " +
            "                                from calculoiptu calc_iptu " +
            "                                         inner join itemcalculoiptu item on calc_iptu.id = item.calculoiptu_id " +
            "                                         inner join calculo c on calc_iptu.id = c.id " +
            "                                         inner join valordivida vd on c.id = vd.calculo_id " +
            "                                         inner join exercicio ex on vd.exercicio_id = ex.id " +
            "                                where calc_iptu.cadastroimobiliario_id = ci.id " +
            "                                  and ex.ano in ( " + (sistemaFacade.getExercicioCorrente().getAno()) + ", " +
                    (sistemaFacade.getExercicioCorrente().getAno() - 1) + ") group by ex.ano, c.datacalculo) " +
            "             where rnk = 1 order by dtcalc desc fetch first 1 rows only) as valorUltimo " +
            " from CADASTROIMOBILIARIO ci " +
            " inner join lote on lote.id = ci.LOTE_ID " +
            " left join propriedade on propriedade.IMOVEL_ID = ci.id " +
            " and propriedade.INICIOVIGENCIA <= SYSDATE AND COALESCE(propriedade.FINALVIGENCIA,SYSDATE) >= SYSDATE " +
            " left join pessoa on pessoa.id = propriedade.PESSOA_ID " +
            " where ci.inscricaocadastral >= '" + inicio + "' and ci.inscricaocadastral <= '" + fim + "' "+
            " ) cadastros " +
            " inner join pessoa p on p.id = cadastros.pessoa " +
            " left join pessoafisica pf on pf.id = p.id " +
            " left join pessoajuridica pj on pj.id = p.id " +
            " WHERE ATIVO = 1 " + and;
        Query q = em.createNativeQuery(sql);
        List<CadastroImobiliario> cadastros = Lists.newArrayList();
        List<Object[]> listaArrayObjetos = q.getResultList();
        for (int i = 0; i < listaArrayObjetos.size(); i++) {
            Object[] ob = listaArrayObjetos.get(i);
            CadastroImobiliario ci = new CadastroImobiliario();
            ci.setId(((BigDecimal) ob[0]).longValue());
            ci.setInscricaoCadastral((String) ob[1]);
            ci.setAreaLote((BigDecimal) ob[2]);
            ci.setAreaTotalConstruida((BigDecimal) ob[3]);
            ci.setQuantidadeImoveisDoProprietario(((BigDecimal) ob[4]).intValue());
            ci.setPropriedade(cadastroImobiliarioFacade.recuperarProprietariosVigentes(ci));
            cadastros.add(ci);
        }
        return cadastros;
    }

    public String montarWhere(CategoriaIsencaoIPTU cat, String inicio, String fim, boolean enquadrar) {
        String and = montarCondicoesPorCategoria(cat, enquadrar);
        if (inicio != null && !inicio.isEmpty()) {
            and += " and INSCRICAO >= '" + inicio + "'";
        }
        if (fim != null && !fim.isEmpty()) {
            and += " and INSCRICAO <= '" + fim + "' ";
        }
        return and;
    }

    public String montarWhereProcessoSalvo(ProcessoIsencaoIPTU processo) {
        String ids = " and (cadastros.id in (";
        int qtde = 1;
        for (CadastroIsencaoIPTU cadastrosEnquadrado : processo.getCadastrosEnquadrados()) {
            if (qtde < 1000) {
                ids += cadastrosEnquadrado.getCadastro().getId() + ",";
            } else {
                ids = ids.substring(0, ids.length() - 1);

                qtde = 1;
                ids += ") or cadastros.id in (";
                ids += cadastrosEnquadrado.getCadastro().getId() + ",";
            }
            qtde++;
        }
        ids = ids.substring(0, ids.length() - 1);
        ids += "))";
        return ids;
    }

    private String montarCondicoesPorCategoria(CategoriaIsencaoIPTU cat, boolean enquadrar) {
        List<String> operacoes = Lists.newArrayList();
        String operador = enquadrar ? " <= " : " > ";
        String and = "";
        if (cat != null) {
            if (cat.getAreaLimiteConstrucao() != null) {
                operacoes.add(" coalesce(areaconstruida,0) " + operador + cat.getAreaLimiteConstrucao());
            }
            if (cat.getAreaLimiteTerreno() != null) {
                operacoes.add(" coalesce(arealote,0) " + operador + cat.getAreaLimiteTerreno());
            }
            if (cat.getQtdeImoveisContribuinte() != null) {
                operacoes.add(" coalesce(qntimoveis,0) " + operador + cat.getQtdeImoveisContribuinte());
            }
            if (cat.getTipoImovel() != null) {
                String exists = (TipoImovel.PREDIAL.equals(cat.getTipoImovel()) ? "" : " not ") + " exists ";
                operacoes.add(exists + " (select const.id from construcao const where coalesce(const.cancelada, 0) <> 1 " +
                    " and const.imovel_id = cadastros.id) ");
            }
            if (cat.getValorInicialUltimoIptuAtivo() != null && cat.getValorFinalUltimoIptuAtivo() != null) {
                operacoes.add(" valorUltimo between " + cat.getValorInicialUltimoIptuAtivo() + " and " + cat.getValorFinalUltimoIptuAtivo());

            }
        }
        for (String operacao : operacoes) {
            and += operacao + (enquadrar ? " and" : " or");
        }
        if (!and.isEmpty()) {
            and = " and ( " + and.substring(0, and.length() - 3) + ") ";
        }
        return and;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<DocumentoOficial> recuperaTodosDocumentos(ProcessoIsencaoIPTU processo, UsuarioSistema usu, Exercicio exercicio, String ip) throws UFMException, AtributosNulosException {
        List<DocumentoOficial> documentos = Lists.newArrayList();
        em.flush();
        processo = em.find(ProcessoIsencaoIPTU.class, processo.getId());
        for (IsencaoCadastroImobiliario isencao : processo.getIsencoes()) {
            if (IsencaoCadastroImobiliario.Situacao.CANCELADO.equals(isencao.getSituacao()) ||
                isencao.getProcessoIsencaoIPTU().getCategoriaIsencaoIPTU() == null ||
                    isencao.getProcessoIsencaoIPTU().getCategoriaIsencaoIPTU().getTipoDoctoOficial() == null) {
                continue;
            }
            DocumentoOficial documentoOficial = documentoOficialFacade.geraDocumentoIsencaoIPTU(isencao, isencao.getDocumentoOficial(), isencao.getCadastroImobiliario(), isencao.getProcessoIsencaoIPTU().getCategoriaIsencaoIPTU().getTipoDoctoOficial(), usu, exercicio, ip);
            isencao.setDocumentoOficial(documentoOficial);
            em.merge(isencao);
            documentos.add(documentoOficial);
        }
        return documentos;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public GerenciadorProcessoIsencaoIPTU criarIsencoesCadastros(GerenciadorProcessoIsencaoIPTU gerenciador) {
        isencoes = Lists.newArrayList();
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        JdbcCalculoIptuDAO calculoDAO = (JdbcCalculoIptuDAO) ap.getBean("jdbcCalculoIptuDAO");
        processo = gerenciador.getProcessoIsencaoIPTU();
        for (CadastroImobiliario ci : gerenciador.getCadastroImobiliarios()) {
            cadastroImobiliario = ci;
            gerenciador.conta();
            adicionarIsencaoAoProcesso();
            if (isencoes.size() >= 50) {
                calculoDAO.persisteIsencaoIPTU(isencoes);
                isencoes.clear();
            }
        }
        if (!isencoes.isEmpty()) {
            calculoDAO.persisteIsencaoIPTU(isencoes);
        }
        return gerenciador;
    }

    private void adicionarIsencaoAoProcesso() {
        IsencaoCadastroImobiliario isencao = new IsencaoCadastroImobiliario();
        isencao.setCadastroImobiliario(cadastroImobiliario);
        isencao.setProcessoIsencaoIPTU(processo);
        isencao.setFinalVigencia(processo.getValidade());
        isencao.setInicioVigencia(new Date());
        isencao.setSituacao(IsencaoCadastroImobiliario.Situacao.EM_ABERTO);
        isencao.setPercentual(processo.getCategoriaIsencaoIPTU().getPercentual());
        if (processo.getCategoriaIsencaoIPTU().getTipoLancamentoIsencaoIPTU().equals(TipoLancamentoIsencaoIPTU.ISENTO_IPTU)
            || processo.getCategoriaIsencaoIPTU().getTipoLancamentoIsencaoIPTU().equals(TipoLancamentoIsencaoIPTU.ISENTO_IPTU_TSU)) {
            isencao.setLancaImposto(Boolean.FALSE);
        }
        if (processo.getCategoriaIsencaoIPTU().getTipoLancamentoIsencaoIPTU().equals(TipoLancamentoIsencaoIPTU.ISENTO_TSU)
            || processo.getCategoriaIsencaoIPTU().getTipoLancamentoIsencaoIPTU().equals(TipoLancamentoIsencaoIPTU.ISENTO_IPTU_TSU)) {
            isencao.setLancaTaxa(Boolean.FALSE);
        }
        if (processo.getIsencoes().isEmpty()) {
            isencao.setSequencia(((BigDecimal) em.createNativeQuery("select count(i.id)+1 from IsencaoCadastroImobiliario i where i.cadastroimobiliario_id = :id").setParameter("id", cadastroImobiliario.getId()).getSingleResult()).longValue());
        } else {
            isencao.setSequencia(processo.getIsencoes().get(processo.getIsencoes().size() - 1).getSequencia() + 1);
        }
        isencao.setTipoLancamentoIsencao(processo.getCategoriaIsencaoIPTU().getTipoLancamentoIsencaoIPTU());
        isencoes.add(isencao);
    }

    public Long buscarUltimoCodigo(Exercicio exercicio) {
        try {
            return singletonGeradorCodigoPorExercicio.getProximoCodigoDoExercicio(ProcessoIsencaoIPTU.class, "numero", "exercicioprocesso_id", exercicio);
        } catch (Exception e) {
            return 1L;
        }
    }

    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public ProcessoIsencaoIPTU salvarRetornando(ProcessoIsencaoIPTU processoIsencao){
        return em.merge(processoIsencao);
    }

    public boolean existeCodigo(ProcessoIsencaoIPTU processo) {
        String juncao = "";
        if (processo.getId() != null) {
            juncao = " and p.id != :id";
        }
        Query q = em.createNativeQuery("select p.* from ProcessoIsencaoIPTU p " +
            " inner join exercicio ex on ex.id = p.exercicioprocesso_id" +
            " WHERE p.numero = :numero and ex.ano = :exercicio" + juncao);
        if (processo.getId() != null) {
            q.setParameter("id", processo.getId());
        }

        q.setParameter("numero", processo.getNumero());
        q.setParameter("exercicio", processo.getExercicioProcesso().getAno());
        return !q.getResultList().isEmpty();
    }

    public List<IsencaoCadastroImobiliario> listaIsencoesPorCadastro(CadastroImobiliario cadastro) throws ExcecaoNegocioGenerica {
        Query q = em.createQuery("select i from IsencaoCadastroImobiliario i where i.cadastroImobiliario = :cadastro order by i.sequencia desc").setParameter("cadastro", cadastro);
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Este cadastro não possui isenção de IPTU");
        }
        return lista;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public CategoriaIsencaoIPTUFacade getCategoriaIsencaoIPTUFacade() {
        return categoriaIsencaoIPTUFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public ProcessoIsencaoIPTU atualizar(ProcessoIsencaoIPTU selecionado) {
        return em.merge(selecionado);
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public IsencaoCadastroImobiliario recuperaIsencao(Long id) {
        return em.find(IsencaoCadastroImobiliario.class, id);
    }

    public CadastroIsencaoIPTU recuperarCadastro(Long id) {
        return em.find(CadastroIsencaoIPTU.class, id);
    }

    public ParecerProcIsencaoIPTUFacade getIsencaoIPTUFacade() {
        return isencaoIPTUFacade;
    }

    public boolean isencaoEfetivada(IsencaoCadastroImobiliario isencaoCadastroImobiliario) {
        String sql = "select iptu.id from CalculoIPTU iptu " +
            " where iptu.isencaoCadastroImobiliario_id = :isencao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("isencao", isencaoCadastroImobiliario);
        return !q.getResultList().isEmpty();
    }

    public IsencaoCadastroImobiliario buscarIsencaoPorIdCalculo(Long idCalculo) {
        Query q = em.createQuery("from CalculoIPTU where id = :idCalculo");
        q.setParameter("idCalculo", idCalculo);
        List retorno = q.getResultList();
        if (retorno.isEmpty()) {
            return null;
        }
        return ((CalculoIPTU) retorno.get(0)).getIsencaoCadastroImobiliario();
    }

    public List<ProcessoIsencaoIPTU> buscarPorNumeroAndExercicioAndDescricaoCategoria(String parte) {
        String hql = "select pro from ProcessoIsencaoIPTU pro        "
            + "   inner join pro.categoriaIsencaoIPTU cat            "
            + "   inner join pro.exercicioProcesso ex                "
            + "        where lower(to_char(pro.numero)) like :filtro "
            + "           or lower(to_char(ex.ano))     like :filtro "
            + "           or lower(cat.descricao)       like :filtro "
            + "     order by ex.ano desc, pro.numero asc             ";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);

        List processos = q.getResultList();
        if (processos == null || processos.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhum Processo de Isenção de IPTU encontrado com filtro informado!");
        }

        return processos;
    }

    public List<CadastroImobiliario> retirarCadastrosDeProprietariosDuplicadosDeAcordoComCategoria(List<CadastroImobiliario> cadastrosEnquadrados, CategoriaIsencaoIPTU categoria) {
        Map<Pessoa, List<CadastroImobiliario>> mapaProprietarios = Maps.newHashMap();
        List<CadastroImobiliario> removidos = Lists.newArrayList();
        for (CadastroImobiliario cadastro : cadastrosEnquadrados) {
            if (!cadastro.getPropriedadeVigente().isEmpty()) {
                Pessoa proprietario = cadastro.getPropriedadeVigente().get(0).getPessoa();
                if (mapaProprietarios.containsKey(proprietario)) {
                    mapaProprietarios.get(proprietario).add(cadastro);
                } else {
                    mapaProprietarios.put(proprietario, Lists.newArrayList(cadastro));
                }
            } else {
                logger.error("Cadastro sem proprietario: " + cadastro.getInscricaoCadastral());
            }
        }
        if (categoria.getQtdeImoveisContribuinte() != null) {
            for (Pessoa pessoa : mapaProprietarios.keySet()) {
                while (mapaProprietarios.get(pessoa).size() > categoria.getQtdeImoveisContribuinte()) {
                    removidos.add(mapaProprietarios.get(pessoa).remove(mapaProprietarios.get(pessoa).size() - 1));
                }
            }
        }
        for (CadastroImobiliario removido : removidos) {
            cadastrosEnquadrados.remove(removido);
        }
        return removidos;
    }

    public static class GerenciadorProcessoIsencaoIPTU implements DetailProcessAsync {
        private static final String formatoDataHora = "%d:%2$TM:%2$TS%n";
        private Integer total;
        private Integer contador;
        private ProcessoIsencaoIPTU processoIsencaoIPTU;
        private List<CadastroImobiliario> cadastroImobiliarios;
        private Long decorrido, tempo;
        private Double qntoFalta;
        private String usuario;
        private String descricao;

        public GerenciadorProcessoIsencaoIPTU(String usuario,
                                              String descricao,
                                              List<CadastroImobiliario> cadastroImobiliarios,
                                              ProcessoIsencaoIPTU processoIsencaoIPTU) {
            this.usuario = usuario;
            this.descricao = descricao;
            this.total = cadastroImobiliarios.size();
            this.cadastroImobiliarios = cadastroImobiliarios;
            this.processoIsencaoIPTU = processoIsencaoIPTU;
            tempo = System.currentTimeMillis();
            contador = 0;
        }

        public Double getPorcentagem() {
            if (contador == null || total == null) {
                return 0d;
            }
            return (contador.doubleValue() / total.doubleValue()) * 100;
        }

        @Override
        public String getUsuario() {
            return usuario;
        }

        public void setUsuario(String usuario) {
            this.usuario = usuario;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public Integer getTotal() {
            return total;
        }

        @Override
        public Double getPorcentagemExecucao() {
            return getPorcentagem();
        }

        public Integer getContador() {
            return contador;
        }

        public ProcessoIsencaoIPTU getProcessoIsencaoIPTU() {
            return processoIsencaoIPTU;
        }

        public void setProcessoIsencaoIPTU(ProcessoIsencaoIPTU processoIsencaoIPTU) {
            this.processoIsencaoIPTU = processoIsencaoIPTU;
        }

        public List<CadastroImobiliario> getCadastroImobiliarios() {
            return cadastroImobiliarios;
        }

        public String getTempoDecorrido() {
            long HOUR = TimeUnit.HOURS.toMillis(1);

            decorrido = (System.currentTimeMillis() - tempo);

            return String.format(formatoDataHora, decorrido / HOUR, decorrido % HOUR);
        }

        public String getTempoEstimado() {
            long HOUR = TimeUnit.HOURS.toMillis(1);
            long unitario = (System.currentTimeMillis() - tempo) / (contador + 1);
            qntoFalta = (unitario * (total - contador.doubleValue()));

            return String.format(formatoDataHora, qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
        }

        public synchronized void conta() {
            contador++;
        }
    }

    private List<Long> buscarIdsProcessosInsencaoDeferidosVencidos(){
        String sql = " select distinct pi.id " +
            " from ProcessoIsencaoIPTU pi " +
            " inner join IsencaoCadastroImobiliario ici on pi.ID = ici.PROCESSOISENCAOIPTU_ID " +
            "  where pi.SITUACAO in (:situacao) and trunc(:dataOperacao) > trunc(ici.FINALVIGENCIA) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", new Date());
        q.setParameter("situacao", Lists.newArrayList(ProcessoIsencaoIPTU.Situacao.ABERTO.name(), ProcessoIsencaoIPTU.Situacao.EFETIVADO.name(),
            ProcessoIsencaoIPTU.Situacao.DEFERIDO.name()));
        List<Object> lista = q.getResultList();
        List<Long> ids = Lists.newArrayList();
        for (Object o : lista) {
            ids.add(((BigDecimal) o).longValue());
        }
        return ids;
    }

    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public void atualizarSituacaoProcessoIsencaoIPTU() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        JdbcCalculoIptuDAO calculoDAO = (JdbcCalculoIptuDAO) ap.getBean("jdbcCalculoIptuDAO");

        List<Long> ids = buscarIdsProcessosInsencaoDeferidosVencidos();
        for (Long id : ids) {
            calculoDAO.alterarSituacaoProcessoIsencao(id, ProcessoIsencaoIPTU.Situacao.VENCIDO);
            calculoDAO.alterarSituacaoIsencao(id, IsencaoCadastroImobiliario.Situacao.VENCIDO);
        }
    }

    public CadastroIsencaoIPTU buscarProcessoPorCadastro(List<Long> cadastros, Integer exercicio){
        List<List<Long>> partition = Lists.partition(cadastros, 999);
        for (List<Long> longs : partition) {
            String sql = "select cad.* from CadastroIsencaoIPTU cad " +
                " inner join ProcessoIsencaoIPTU pro on pro.id = cad.processo_id " +
                " inner join exercicio ex on pro.exercicioreferencia_id = ex.id" +
                " where cad.cadastro_id in :cadastros " +
                " and pro.situacao = :situacao ";
            if(exercicio != null) {
                sql += " and ex.ano = :exercicio ";
            }
            Query q = em.createNativeQuery(sql, CadastroIsencaoIPTU.class);
            q.setParameter("cadastros", longs);
            q.setParameter("situacao", ProcessoIsencaoIPTU.Situacao.EFETIVADO.name());
            if(exercicio != null) {
                q.setParameter("exercicio", exercicio);
            }
            List resultList = q.getResultList();
            if (!resultList.isEmpty()) {
                return (CadastroIsencaoIPTU) resultList.get(0);
            }
        }
        return null;
    }

}
