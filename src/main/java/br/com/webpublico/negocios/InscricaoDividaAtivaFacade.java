/*
 * Codigo gerado automaticamente em Fri May 25 15:50:33 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.SingletonConcorrenciaParcela;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteDividaAtiva;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.consultaparcela.DTO.ValorTipoTributo;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.rowmapper.ValorPorTributoRowMapper;
import br.com.webpublico.negocios.tributario.services.ServiceIntegracaoTributarioContabil;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.ResultadoValidacao;
import br.com.webpublico.util.UltimoLinhaDaPaginaDoLivroDividaAtiva;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class InscricaoDividaAtivaFacade extends CalculoExecutorDepoisDePagar<InscricaoDividaAtiva> {

    ServiceIntegracaoTributarioContabil service;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private LivroDividaAtivaFacade livroDividaAtivaFacade;
    @EJB
    private TermoInscricaoDividaAtivaFacade termoInscricaoDividaAtivaFacade;
    @EJB
    private GeraValorDividaDividaAtiva geraDebito;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private JdbcDividaAtivaDAO dao;
    private Map<Long, Divida> dividas;
    private Long numeroDoTermo;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlan;
    @EJB
    private SingletonConcorrenciaParcela concorrenciaParcela;
    @EJB
    private ArrecadacaoFacade arrecadacaoFacade;
    @Resource
    private SessionContext sessionContext;

    public InscricaoDividaAtivaFacade() {
        super(InscricaoDividaAtiva.class);
    }

    private JdbcDividaAtivaDAO getDAO() {
        if (dao == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            dao = (JdbcDividaAtivaDAO) ap.getBean("dividaAtivaDAO");
        }
        return dao;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public ContratoRendasPatrimoniaisFacade getContratoRendasPatrimoniaisFacade() {
        return contratoRendasPatrimoniaisFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public LivroDividaAtivaFacade getLivroDividaAtivaFacade() {
        return livroDividaAtivaFacade;
    }

    public TermoInscricaoDividaAtivaFacade getTermoInscricaoDividaAtivaFacade() {
        return termoInscricaoDividaAtivaFacade;
    }

    public GeraValorDividaDividaAtiva getGeraDebito() {
        return geraDebito;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return cadastroRuralFacade.listaFiltrandoPorCodigo(parte.trim());
    }

    public List<ContratoRendasPatrimoniais> completaContratolRendasPatrimonial(String parte) {
        return contratoRendasPatrimoniaisFacade.listaFiltrando(parte.trim(), "numeroContrato");
    }

    public List<Divida> completaDivida(String parte) {
        return dividaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public Long recuperaProximoNumeroInscricaoDividaAtiva() {
        Query consulta = em.createQuery("select max(p.numero) from InscricaoDividaAtiva p");
        if (consulta.getSingleResult() != null) {
            return (Long) consulta.getSingleResult() + 1;
        }
        return (long) 1;
    }

    public BigDecimal retornaSaldoDaParcela(ParcelaValorDivida parcela) {
        return consultaDebitoFacade.getUltimaSituacao(parcela).getSaldo();
    }

    public String retornaUltimaSituacaoDaParcela(ParcelaValorDivida parcela) {
        return consultaDebitoFacade.getUltimaSituacao(parcela).getSituacaoParcela().getDescricao();
    }

    public String retornaUltimaReferenciaDaParcela(ParcelaValorDivida parcela) {
        return consultaDebitoFacade.getUltimaSituacao(parcela).getReferencia();
    }

    public List<Divida> buscarDividasPorTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        Query consulta = em.createQuery("select d from "
            + "Divida d where d.tipoCadastro = :tipo"
            + " and d.divida is not null");
        consulta.setParameter("tipo", tipoCadastroTributario);
        return consulta.getResultList();
    }

    @Override
    public InscricaoDividaAtiva recuperar(Object id) {
        InscricaoDividaAtiva inscricao = em.find(InscricaoDividaAtiva.class, id);
        inscricao.getDividaAtivaDividas().size();
        return inscricao;
    }

    public List<ItemInscricaoDividaAtiva> recuperarItens(List<ResultadoPesquisaDividaAtiva> resultados) {
        List<ItemInscricaoDividaAtiva> lista = Lists.newArrayList();
        for (ResultadoPesquisaDividaAtiva resultadoPesquisa : resultados) {
            lista.add(em.find(ItemInscricaoDividaAtiva.class, resultadoPesquisa.getItemInscricaoId()));
        }
        return lista;
    }

    public List<ItemInscricaoDividaAtiva> recuperarItens(InscricaoDividaAtiva selecionado) {
        return em.createQuery("from ItemInscricaoDividaAtiva  item where item.inscricaoDividaAtiva = :inscricao")
            .setParameter("inscricao", selecionado).getResultList();
    }

    public InscricaoDividaAtiva processar(InscricaoDividaAtiva inscricaoDividaAtiva) {
        inscricaoDividaAtiva.setSituacaoInscricaoDividaAtiva(SituacaoInscricaoDividaAtiva.FINALIZADO);

        for (ItemInscricaoDividaAtiva item : inscricaoDividaAtiva.getItensInscricaoDividaAtivas()) {
            for (InscricaoDividaParcela inscricaoDividaParcela : item.getItensInscricaoDividaParcelas()) {
                ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, inscricaoDividaParcela.getParcelaValorDivida().getId());
                parcela.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA, parcela, parcela.getUltimaSituacao().getSaldo()));
                em.merge(parcela);
            }
            ValorDivida vd = item.getItensInscricaoDividaParcelas().get(0).getParcelaValorDivida().getValorDivida();
            vd = em.find(ValorDivida.class, vd.getId());
            for (ParcelaValorDivida parcela : vd.getParcelaValorDividas()) {
                if (parcela.getOpcaoPagamento().getPromocional()) {
                    parcela.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.CANCELADA_INSCRICAO_DIVIDA_ATIVA, parcela, parcela.getUltimaSituacao().getSaldo()));
                    em.merge(parcela);
                }
            }
        }
        inscricaoDividaAtiva = em.merge(inscricaoDividaAtiva);
        return inscricaoDividaAtiva;
    }

    public InscricaoDividaAtiva merge(InscricaoDividaAtiva esteSelecionado) {
        return em.merge(esteSelecionado);
    }

    public InscricaoDividaAtiva salva(InscricaoDividaAtiva esteSelecionado) {
        ResultadoValidacao result = validarProcesso(esteSelecionado);
        if (result.isValidado()) {
            try {
                esteSelecionado = em.merge(esteSelecionado);
                result.limpaMensagens();
                result.addInfo(null, "Salvo com sucesso!", "");
            } catch (Exception ex) {
                result.limpaMensagens();
                result.addErro(null, "Erro ao tentar salvar o Processo de Parcelamento!", ex.getMessage());
            }
        }
        return esteSelecionado;
    }

    public InscricaoDividaAtiva salvaSemValidar(InscricaoDividaAtiva selecionado) {
        return em.merge(selecionado);
    }

    public ResultadoValidacao validarProcesso(InscricaoDividaAtiva selecionado) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        final String summary = "Não foi possível continuar!";
        if (selecionado.getExercicio() == null || selecionado.getExercicio().getId() == null) {
            resultado.addErro(null, summary, "O Exercício deve ser informado.");
        }
        if (selecionado.getTipoCadastroTributario() == null) {
            resultado.addErro(null, summary, "O Tipo de Cadastro deve ser informado.");
        }
        if ((selecionado.getCadastroInicial() == null || selecionado.getCadastroInicial().trim().isEmpty()) && (selecionado.getCadastroFinal() == null || selecionado.getCadastroFinal().trim().isEmpty())) {
            resultado.addErro(null, summary, "Cadastro incial ou final dever ser informado.");
        }
        if (selecionado.getDataInscricao() == null) {
            resultado.addErro(null, summary, "Informe a data de Inscrição.");
        }
        if (selecionado.getVencimentoInicial() != null && selecionado.getVencimentoFinal() != null) {
            if (selecionado.getVencimentoInicial().compareTo(selecionado.getVencimentoFinal()) > 0) {
                resultado.addErro(null, summary, "Não é permitido data de vencimento inicial maior que data data de vencimento final.");
            }
        }

        if (selecionado.getItensInscricaoDividaAtivas() == null || selecionado.getItensInscricaoDividaAtivas().isEmpty()) {
            resultado.addErro(null, summary, "É necessário que informe no minímo um cadastro para salvar a inscrição em Dívida Ativa.");
        }
        return resultado;
    }

    public List<InscricaoDividaAtiva> completaInscricaoDividaAtivaFinalizada(String parte) {
        Query consulta = em.createQuery("select inscricao from InscricaoDividaAtiva inscricao"
            + " where (to_char(inscricao.numero) like :parte" +
            " or to_char(inscricao.exercicio.ano) like :parte)"
            + " and inscricao.situacaoInscricaoDividaAtiva = 'FINALIZADO'");
        consulta.setParameter("parte", "%" + parte + "%");
        consulta.setMaxResults(10);
        return consulta.getResultList();
    }

    private void verificarSeExistemParcelasDiferentesDeAberto(List<ParcelaValorDivida> novasParcelas) throws ExcecaoNegocioGenerica {
        if (hasParcelaPaga(novasParcelas)) {
            throw new ExcecaoNegocioGenerica("Existe(m) parcela(s) paga(s)!");
        }
    }

    private void cancelarCertidoesDoItem(ItemInscricaoDividaAtiva item) {
        List<CertidaoDividaAtiva> certidoes = recuperarCertidoesDoItemInscricao(item);
        for (CertidaoDividaAtiva certidaoDividaAtiva : certidoes) {
            certidaoDividaAtiva.setSituacaoCertidaoDA(SituacaoCertidaoDA.CANCELADA);
            em.merge(certidaoDividaAtiva);
        }
    }

    private void cancelarParcelasNovas(List<ParcelaValorDivida> novasParcelas) {
        for (ParcelaValorDivida parcela : novasParcelas) {
            SituacaoParcelaValorDivida ultimaSituacao = parcela.getUltimaSituacao();
            parcela.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.CANCELAMENTO, parcela, ultimaSituacao.getSaldo()));
            parcela = em.merge(parcela);
        }
    }

    public List<ParcelaValorDivida> recuperarParcelasItemInscricaoDividaAtiva(ItemInscricaoDividaAtiva item) {
        Query consulta = em.createNativeQuery("SELECT parcela.* FROM parcelavalordivida parcela"
            + " INNER JOIN valordivida vd ON parcela.valordivida_id = vd.id"
            + " INNER JOIN calculo cal ON vd.calculo_id = cal.id"
            + " INNER JOIN iteminscricaodividaativa item ON cal.id = item.id"
            + " WHERE cal.id = :item"
            + "  ", ParcelaValorDivida.class);
        consulta.setParameter("item", item.getId());
        return consulta.getResultList();

    }

    private boolean hasParcelaPaga(List<ParcelaValorDivida> novasParcelas) {
        for (ParcelaValorDivida parcelaValorDivida : novasParcelas) {
            SituacaoParcelaValorDivida situacao = parcelaValorDivida.getUltimaSituacao();
            if (situacao.getSituacaoParcela().isPago()) {
                return true;
            }
        }
        return false;
    }

    private List<CertidaoDividaAtiva> recuperarCertidoesDoItemInscricao(ItemInscricaoDividaAtiva item) {
        Query consulta = em.createQuery("select certidao from CertidaoDividaAtiva certidao " +
            " join certidao.itensCertidaoDividaAtiva item where item.itemInscricaoDividaAtiva = :item");
        consulta.setParameter("item", item);
        return consulta.getResultList();
    }

    private List<CalculoPessoa> recuperarPessoas(ItemInscricaoDividaAtiva item) {
        List<CalculoPessoa> calculoPessoas = new ArrayList<CalculoPessoa>();
        Cadastro cadastro = item.getCadastro();
        List<Pessoa> pessoas = pessoaFacade.recuperaPessoasDoCadastro(cadastro);
        for (Pessoa pessoa : pessoas) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(item);
            calculoPessoa.setPessoa(pessoa);
            calculoPessoas.add(calculoPessoa);
        }
        return calculoPessoas;
    }

    public List<ItemInscricaoDividaAtiva> recuperarItemInscricaoDivida(InscricaoDividaAtiva inscricao) {
        Query consulta = em.createQuery("select item from ItemInscricaoDividaAtiva item "
            + "where item.inscricaoDividaAtiva = :inscricao"
            + " and (item.situacao = 'ATIVO' or item.situacao is null)");
        consulta.setParameter("inscricao", inscricao);
        return consulta.getResultList();
    }

    public ItemInscricaoDividaAtiva recuperarItemInscricaoDivida(Long idItemInscricaoDividaAtiva) {
        ItemInscricaoDividaAtiva item = em.find(ItemInscricaoDividaAtiva.class, idItemInscricaoDividaAtiva);
        return item;
    }

    public void alterarSituacaoDaInscricoes(List<InscricaoDividaAtiva> inscricoes, CancelamentoInscricaoDA cancelamento) {
        for (InscricaoDividaAtiva inscricaoDividaAtiva : inscricoes) {
            inscricaoDividaAtiva = em.find(InscricaoDividaAtiva.class, inscricaoDividaAtiva.getId());
            inscricaoDividaAtiva = cancelarSituacaoDaInscricao(inscricaoDividaAtiva, cancelamento);
            em.merge(inscricaoDividaAtiva);
        }
    }

    private InscricaoDividaAtiva cancelarSituacaoDaInscricao(InscricaoDividaAtiva inscricaoDividaAtiva, CancelamentoInscricaoDA cancelamentoInscricaoDA) {
        inscricaoDividaAtiva.setSituacaoInscricaoDividaAtiva(SituacaoInscricaoDividaAtiva.CANCELADO);
        inscricaoDividaAtiva.setCancelamentoInscricaoDA(cancelamentoInscricaoDA);
        return inscricaoDividaAtiva;
    }

    public List<InscricaoDividaParcela> listaInscricaoDividaParcela(ItemInscricaoDividaAtiva item) {
        Query q = em.createQuery("from InscricaoDividaParcela idp where idp.itemInscricaoDividaAtiva = :item");
        q.setParameter("item", item);
        return q.getResultList();
    }

    public ItemInscricaoDividaAtiva retornarValorParaCadaItem(ItemInscricaoDividaAtiva item) {
        BigDecimal valor = BigDecimal.ZERO;
        for (InscricaoDividaParcela inscricaoDividaParcela : item.getItensInscricaoDividaParcelas()) {
            valor = valor.add(retornaSaldoDaParcela(inscricaoDividaParcela.getParcelaValorDivida()));
        }
        item.setValorReal(valor);
        item.setValorEfetivo(valor);
        return item;
    }

    public List<Divida> retornarDividasDoSelecionado(InscricaoDividaAtiva inscricaoDividaAtiva) {
        List<Divida> retorno = new ArrayList<Divida>();
        for (DividaAtivaDivida dividaAtivaDivida : inscricaoDividaAtiva.getDividaAtivaDividas()) {
            retorno.add(dividaAtivaDivida.getDivida());
        }
        return retorno;
    }

    @Asynchronous
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public void consultarParcelas(AssistenteDividaAtiva assistente, InscricaoDividaAtiva inscricaoDividaAtiva) {
        Query q = em.createNativeQuery(assistente.getSQL());
        adicionarValoresParametros(inscricaoDividaAtiva, q);
        List<Object[]> listaArrayObjetos = q.getResultList();

        List<ResultadoParcela> listaEntidadeNaoMapeada = new ArrayList<>();
        for (Object[] ob : listaArrayObjetos) {
            ResultadoParcela resultado = new ResultadoParcela();
            resultado.setIdParcela(((BigDecimal) ob[0]).longValue());
            resultado.setIdValorDivida(((BigDecimal) ob[1]).longValue());
            List<ValorTipoTributo> valoresTipoTributo = buscarValorTipoTributo(resultado.getIdParcela());
            for (ValorTipoTributo valorTipoTributo : valoresTipoTributo) {
                if (valorTipoTributo.getTipoTributo().isImposto()) {
                    resultado.setValorImposto(valorTipoTributo.getValor());
                } else {
                    resultado.setValorTaxa(valorTipoTributo.getValor());
                }
            }
            resultado.setIdCadastro(ob[2] != null ? ((BigDecimal) ob[2]).longValue() : null);
            resultado.setIdDivida(((BigDecimal) ob[3]).longValue());
            resultado.setIdPessoa(((BigDecimal) ob[4]).longValue());
            resultado.setIdOpcaoPagamento(((BigDecimal) ob[5]).longValue());
            resultado.setVencimento((Date) ob[6]);
            resultado.setTipoCalculo((String) ob[7]);
            resultado.setIdCalculo(((BigDecimal) ob[8]).longValue());
            resultado.setParcela((String) ob[9]);
            resultado.setDivida((String) ob[10]);

            resultado.setTipoCadastro(assistente.getTipoCadastroTributario().getDescricao());
            resultado.setSituacao(SituacaoParcela.EM_ABERTO.name());
            resultado.setReferencia((String) ob[11]);
            resultado.setExercicio(((BigDecimal) ob[12]).intValue());
            resultado.setSomaNoDemonstrativo(true);
            resultado.setCadastro((String) ob[13]);

            if (!listaEntidadeNaoMapeada.contains(resultado)) {
                listaEntidadeNaoMapeada.add(resultado);
            }
        }
        assistente.finalizaConsulta(listaEntidadeNaoMapeada);
    }

    private List<ValorTipoTributo> buscarValorTipoTributo(Long idParcela) {
        String sql = "SELECT tr.tipoTributo, SUM(ipvd.valor) FROM ItemParcelaValorDivida ipvd " +
            "   INNER JOIN ItemValorDivida ivd ON ivd.id = ipvd.itemValorDivida_id " +
            "   INNER JOIN Tributo tr ON tr.id = ivd.tributo_id AND tr.tipoTributo in (:imposto,:taxa) " +
            "    WHERE ipvd.PARCELAVALORDIVIDA_ID = :idParcela " +
            " group by tr.tipoTributo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);
        q.setParameter("imposto", Tributo.TipoTributo.IMPOSTO.name());
        q.setParameter("taxa", Tributo.TipoTributo.TAXA.name());
        List<Object[]> resultList = q.getResultList();
        List<ValorTipoTributo> retorno = Lists.newArrayList();
        for (Object[] obj : resultList) {
            retorno.add(new ValorTipoTributo(obj));
        }
        return retorno;
    }

    private void adicionarValoresParametros(InscricaoDividaAtiva inscricaoDividaAtiva, Query q) {
        q.setParameter("situacaoAberto", SituacaoParcela.EM_ABERTO.name());
        if (TipoCadastroTributario.PESSOA.equals(inscricaoDividaAtiva.getTipoCadastroTributario())
            && inscricaoDividaAtiva.getContribuinte() != null) {
            q.setParameter("idPessoa", inscricaoDividaAtiva.getContribuinte().getId());
        }
        if (TipoCadastroTributario.IMOBILIARIO.equals(inscricaoDividaAtiva.getTipoCadastroTributario())) {
            q.setParameter("inscricaoInicial", inscricaoDividaAtiva.getCadastroInicial().trim());
            q.setParameter("inscricaoFinal", inscricaoDividaAtiva.getCadastroFinal().trim());
        }
        if (TipoCadastroTributario.ECONOMICO.equals(inscricaoDividaAtiva.getTipoCadastroTributario())) {
            q.setParameter("inscricaoInicial", inscricaoDividaAtiva.getCadastroInicial().trim());
            q.setParameter("inscricaoFinal", inscricaoDividaAtiva.getCadastroFinal().trim());
        }
        if (TipoCadastroTributario.RURAL.equals(inscricaoDividaAtiva.getTipoCadastroTributario())) {
            q.setParameter("inscricaoInicial", inscricaoDividaAtiva.getCadastroInicial());
            q.setParameter("inscricaoFinal", inscricaoDividaAtiva.getCadastroFinal());
        }
        if (inscricaoDividaAtiva.getExercicio() != null) {
            q.setParameter("exercicioInicial", inscricaoDividaAtiva.getExercicio().getAno());
        }
        if (inscricaoDividaAtiva.getExercicioFinal() != null) {
            q.setParameter("exercicioFinal", inscricaoDividaAtiva.getExercicioFinal().getAno());
        }
        if (inscricaoDividaAtiva.getVencimentoInicial() != null) {
            q.setParameter("vencimentoInicial", inscricaoDividaAtiva.getVencimentoInicial());
        }
        if (inscricaoDividaAtiva.getVencimentoFinal() != null) {
            q.setParameter("vencimentoFinal", inscricaoDividaAtiva.getVencimentoFinal());
        }
        List<Long> listaDivida = Lists.newArrayList();
        for (Divida d : retornarDividasDoSelecionado(inscricaoDividaAtiva)) {
            listaDivida.add(d.getId());
        }
        if (!listaDivida.isEmpty()) {
            q.setParameter("listaIdDivida", listaDivida);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void inscreverDividaAtiva(InscricaoDividaAtiva inscricao, AssistenteDividaAtiva assistente) {
        try {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            service = (ServiceIntegracaoTributarioContabil) ap.getBean("serviceIntegracaoTributarioContabil");
            dividas = new HashMap<>();
            List<ItemInscricaoDividaAtiva> inscritos = Lists.newArrayList();
            Map<Long, List<ResultadoParcela>> mapaDividaPorParcela = montarMapaParcelasAgrupadas(assistente);
            for (Long idValorDivida : mapaDividaPorParcela.keySet()) {
                processarInscricao(assistente, inscricao, inscritos, idValorDivida, mapaDividaPorParcela);
            }
            salvarItens(inscritos);
            assistente.finalizaInscricao();
        } catch (Exception e) {
            logger.error("Erro ao inscreverDividaAtiva: ", e);
            assistente.finalizarInscricaoComErro(e.getMessage());
        } finally {
            liberarParcelas(assistente);
        }
    }


    private void liberarParcelas(AssistenteDividaAtiva assistente) {
        if (assistente != null) {
            if (assistente.getParcelas() != null && !assistente.getParcelas().isEmpty()) {
                for (ResultadoParcela item : assistente.getParcelas()) {
                    concorrenciaParcela.unLock(item.getIdParcela());
                }
            }
        }
    }

//    @Asynchronous
//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//    public void integraDividaAtiva(AssistenteDividaAtiva assistente) {
//        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
//        ServiceIntegracaoTributarioContabil service = (ServiceIntegracaoTributarioContabil) ap.getBean("serviceIntegracaoTributarioContabil");
//        this.assistente = assistente;
//        mapaDividaPorParcela = montaMapaParcelasAgrupadas();
//        for (Long idValorDivida : mapaDividaPorParcela.keySet()) {
//            try {
//                ValorDivida valorDivida = recuperarValorDividaPorId(idValorDivida);
//                valorDivida.getItemValorDividas().size();
//                service.criarItemIntegracaoValorDivida(valorDivida, TipoIntegracao.DIVIDA_ATIVA);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void processarInscricao(AssistenteDividaAtiva assistente, InscricaoDividaAtiva inscricao, List<ItemInscricaoDividaAtiva> inscritos, Long idValorDivida, Map<Long, List<ResultadoParcela>> mapaDividaPorParcela) {
        ValorDivida vd = new ValorDivida();
        vd.setId(idValorDivida);
        ItemInscricaoDividaAtiva item = gerarItemInscricao(assistente, inscricao, idValorDivida, mapaDividaPorParcela);
        if (item.getItensInscricaoDividaParcelas().size() > 0) {
            inscricao.getItensInscricaoDividaAtivas().add(item);
            inscritos.add(item);
        }
        if (inscritos.size() >= 50) {
            salvarItens(inscritos);
            inscritos.clear();
        }
    }

    private void geraInscricaoParcela(ItemInscricaoDividaAtiva item, ResultadoParcela resultado) {
        InscricaoDividaParcela inscriacaoParcela = new InscricaoDividaParcela();
        inscriacaoParcela.setIdParcela(resultado.getIdParcela());
        //TODO arrumar esse tipo de calculo para pegar o valor corretamente
        inscriacaoParcela.setTipoCalculo(Calculo.TipoCalculo.valueOf(resultado.getTipoCalculo()));
        inscriacaoParcela.setSaldoParcela(resultado.getValorOriginal());
        inscriacaoParcela.setItemInscricaoDividaAtiva(item);
        item.getItensInscricaoDividaParcelas().add(inscriacaoParcela);
    }

    private ItemInscricaoDividaAtiva gerarItemInscricao(AssistenteDividaAtiva assistente, InscricaoDividaAtiva inscricao, Long idValorDivida, Map<Long, List<ResultadoParcela>> mapaDividaPorParcela) {
        ItemInscricaoDividaAtiva item = new ItemInscricaoDividaAtiva();
        item.setItensInscricaoDividaParcelas(new ArrayList<InscricaoDividaParcela>());
        item.setInscricaoDividaAtiva(inscricao);
        Exercicio exercicio = exercicioFacade.recuperarExercicioPeloAno(mapaDividaPorParcela.get(idValorDivida).get(0).getExercicio());
        item.setExercicio(exercicio != null ? exercicio : inscricao.getExercicio());
        item.setResultadoParcela(mapaDividaPorParcela.get(idValorDivida).get(0));
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ResultadoParcela resultado : mapaDividaPorParcela.get(idValorDivida)) {
            verificarParcelaBloqueada(resultado);
            bloquearParcela(resultado);
            assistente.conta();
            geraInscricaoParcela(item, resultado);
            valorTotal = valorTotal.add(resultado.getValorOriginal());
        }
        if (item.getResultadoParcela() != null && item.getResultadoParcela().getSd() != null) {
            item.setSubDivida(item.getResultadoParcela().getSd().longValue());
        }
        item.setValorEfetivo(valorTotal);
        item.setValorReal(valorTotal);
        item.setDivida(em.find(Divida.class, item.getResultadoParcela().getIdDivida()));

        List<BigDecimal> pessoas = buscarIdsDasPessoasPeloIdDoCalculo(item.getResultadoParcela().getIdCalculo());

        for (BigDecimal pessoaId : pessoas) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(item);
            calculoPessoa.setIdPessoa(Long.parseLong("" + pessoaId));
            item.getPessoas().add(calculoPessoa);
        }

        criarValorDivida(item);
        return item;
    }

    public List<BigDecimal> buscarIdsDasPessoasPeloIdDoCalculo(Long calculoId) {
        String sql = "select distinct cp.pessoa_id from calculopessoa cp where cp.calculo_id = :calculo_id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("calculo_id", calculoId);
        return q.getResultList();
    }

    private void bloquearParcela(ResultadoParcela item) {
        concorrenciaParcela.lock(item.getId());
    }

    private void verificarParcelaBloqueada(ResultadoParcela item) {
        if (concorrenciaParcela.isLocked(item.getId())) {
            throw new ValidacaoException("Existe(m) parcela(s) selecionada(s) que já está(ão) sendo processada(s).");
        }
    }

    private void salvarItens(List<ItemInscricaoDividaAtiva> itens) {
        if (!itens.isEmpty()) {
            JdbcDividaAtivaDAO dao = getDAO();
            dao.persisteItemInscricaoDividaAtiva(itens);
        }
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void gerarLivroDA(InscricaoDividaAtiva inscricao, AssistenteDividaAtiva assistente) {
        Set<Long> dividas = new HashSet<>();
        numeroDoTermo = termoInscricaoDividaAtivaFacade.recuperaProximoCodigoTermoInscricaoDividaAtiva();

        for (ItemInscricaoDividaAtiva item : inscricao.getItensInscricaoDividaAtivas()) {
            dividas.add(item.getResultadoParcela().getIdDivida());
        }
        for (Long id : dividas) {
            Divida divida = getDAO().findDividaById(id);
            LivroDividaAtiva livro = getDAO().findLivroByDividaAndExercicio(id, inscricao.getExercicio().getAno());
            if (livro == null) {
                livro = criarNovoLivro(inscricao.getExercicio(), divida.getNrLivroDividaAtiva(), divida.getTipoCadastro());
            }
            gerarItensDoLivro(assistente, inscricao, livro, divida);
            salvarLivro(livro);
        }
        assistente.finalizaGeracaoLivro();
    }

    private void salvarLivro(LivroDividaAtiva livro) {
        if (livro.getId() == null) {
            getDAO().persisteLivroDividaAtiva(livro);
        } else {
            getDAO().mergeLivroDividaAtiva(livro);
        }
    }

    private Map<Long, List<ResultadoParcela>> montarMapaParcelasAgrupadas(AssistenteDividaAtiva assistente) {
        Map<Long, List<ResultadoParcela>> mapa = new HashMap<>();
        for (ResultadoParcela parcela : assistente.getParcelas()) {
            if (mapa.containsKey(parcela.getIdValorDivida())) {
                mapa.get(parcela.getIdValorDivida()).add(parcela);
            } else {
                mapa.put(parcela.getIdValorDivida(), new ArrayList<ResultadoParcela>());
                mapa.get(parcela.getIdValorDivida()).add(parcela);
            }
        }
        return mapa;
    }

    private void gerarItensDoLivro(AssistenteDividaAtiva assistente, InscricaoDividaAtiva inscricao, LivroDividaAtiva livro, Divida divida) {
        ItemLivroDividaAtiva itemLivro = null;
        for (ItemLivroDividaAtiva item : livro.getItensLivros()) {
            if (item.getIdInscricaoDividaAtiva().equals(inscricao.getId())) {
                itemLivro = item;
            }
        }
        if (itemLivro == null) {
            itemLivro = gerarItemDoLivro(inscricao, livro);
        }
        Long sequencia = 1L;
        Long linha = 1L;
        Long pagina = 1L;
        if (livro.getId() != null) {
            UltimoLinhaDaPaginaDoLivroDividaAtiva ultima = getDAO().findUltimaLinhaByLivro(livro.getId());
            if (ultima != null) {
                linha = ultima.getLinha() + 1;
                pagina = ultima.getPagina();
                sequencia = ultima.getSequencia() + 1;
                if (linha > livro.QUANTIDADE_LINHA_POR_PAGINA) {
                    linha = 1l;
                    pagina++;
                }
            }
        }
        for (ItemInscricaoDividaAtiva itemInscricao : inscricao.getItensInscricaoDividaAtivas()) {
            if (itemInscricao.getResultadoParcela().getIdDivida().equals(divida.getId())) {
                gerarLinhaDoLivro(assistente, itemLivro, sequencia, linha, pagina, itemInscricao);
                linha++;
                sequencia++;
                if (linha > livro.QUANTIDADE_LINHA_POR_PAGINA) {
                    linha = 1l;
                    pagina++;
                }
            }
        }
        livro.setTotalPaginas(pagina);
    }

    private void gerarLinhaDoLivro(AssistenteDividaAtiva assistente, ItemLivroDividaAtiva itemLivro, Long sequencia, Long linha, Long pagina, ItemInscricaoDividaAtiva itemInscricao) {
        LinhaDoLivroDividaAtiva linhaLivro = new LinhaDoLivroDividaAtiva();
        linhaLivro.setSequencia(sequencia);
        linhaLivro.setItemInscricaoDividaAtiva(itemInscricao);
        linhaLivro.setNumeroDaLinha(linha);
        linhaLivro.setPagina(pagina);
        linhaLivro.setItemLivroDividaAtiva(itemLivro);
        linhaLivro.setTermoInscricaoDividaAtiva(gerarTermoInscricao());
        itemLivro.getLinhasDoLivro().add(linhaLivro);
        assistente.contaGeracao();
    }

    public TermoInscricaoDividaAtiva gerarTermoInscricao() {
        TermoInscricaoDividaAtiva termo = new TermoInscricaoDividaAtiva();
        termo.setNumero(String.valueOf(numeroDoTermo));
        numeroDoTermo++;
        termo.setDocumentoOficial(null);
        return termo;
    }

    private ItemLivroDividaAtiva gerarItemDoLivro(InscricaoDividaAtiva inscricao, LivroDividaAtiva livro) {
        ItemLivroDividaAtiva itemLivro;
        itemLivro = new ItemLivroDividaAtiva();
        itemLivro.setInscricaoDividaAtiva(inscricao);
        itemLivro.setLivroDividaAtiva(livro);
        itemLivro.setProcessado(true);
        livro.getItensLivros().add(itemLivro);
        return itemLivro;
    }

    private LivroDividaAtiva criarNovoLivro(Exercicio exercicio, String numero, TipoCadastroTributario tipoCadastro) {
        LivroDividaAtiva livro = new LivroDividaAtiva();
        livro.setTipoCadastroTributario(tipoCadastro);
        livro.setTotalPaginas(0l);
        livro.setTipoOrdenacao(LivroDividaAtiva.TipoOrdenacao.NUMERICA);
        livro.setNumero(Long.parseLong(numero));
        livro.setSequencia(0l);
        livro.setExercicio(exercicio);
        return livro;
    }

    private void criarValorDivida(ItemInscricaoDividaAtiva item) {
        ValorDivida vd = new ValorDivida();
        vd.setCalculo(item);
        vd.setValor(item.getValorReal());
        vd.setEmissao(new Date());
        if (!dividas.containsKey(item.getResultadoParcela().getIdDivida())) {
            dividas.put(item.getResultadoParcela().getIdDivida(), getDAO().findDividaDestinoByDivida(item.getResultadoParcela().getIdDivida()));
        }
        vd.setDivida(dividas.get(item.getResultadoParcela().getIdDivida()));
        vd.setExercicio(item.getExercicio());
        criarItensValorDivida(vd, item);
        criarParcela(vd, item);
        item.setValorDivida(vd);
    }

    private void criarParcela(ValorDivida vd, ItemInscricaoDividaAtiva item) {
        ParcelaValorDivida pvd = new ParcelaValorDivida();
        pvd.setValorDivida(vd);
        pvd.setValor(vd.getValor());
        pvd.setDataRegistro(new Date());
        pvd.setDividaAtiva(true);
        pvd.setOpcaoPagamento(new OpcaoPagamento(item.getResultadoParcela().getIdOpcaoPagamento()));
        pvd.setVencimento(item.getResultadoParcela().getVencimento());
        pvd.setPercentualValorTotal(new BigDecimal("100"));
        pvd.setSequenciaParcela("1");
        pvd.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, pvd, item.getResultadoParcela().getValorOriginal()));
        vd.getParcelaValorDividas().add(pvd);
        gerarItemParcela(pvd);
    }

    private void gerarItemParcela(ParcelaValorDivida pvd) {
        for (ItemValorDivida item : pvd.getValorDivida().getItemValorDividas()) {
            ItemParcelaValorDivida ipvd = new ItemParcelaValorDivida();
            ipvd.setValor(item.getValor());
            ipvd.setItemValorDivida(item);
            ipvd.setParcelaValorDivida(pvd);
            pvd.getItensParcelaValorDivida().add(ipvd);
        }
    }

    private void criarItensValorDivida(ValorDivida vd, ItemInscricaoDividaAtiva itemInscricao) {
        Map<Long, BigDecimal> mapa = Maps.newHashMap();
        for (InscricaoDividaParcela inscricaoDividaParcela : itemInscricao.getItensInscricaoDividaParcelas()) {
            //Long idParcela = inscricaoDividaParcela.getIdParcela();
            List<ValorPorTributoRowMapper.ValorPorTributo> valoresPorTributoByParcela = getDAO().findValoresPorTributoByParcela(inscricaoDividaParcela.getIdParcela(), inscricaoDividaParcela.getTipoCalculo());
            for (ValorPorTributoRowMapper.ValorPorTributo valorPorTributo : valoresPorTributoByParcela) {
                BigDecimal valor = valorPorTributo.getValor();
                if (mapa.containsKey(valorPorTributo.getTributo().getId())) {
                    valor = valor.add(mapa.get(valorPorTributo.getTributo().getId()));
                }
                mapa.put(valorPorTributo.getTributo().getId(), valor);
            }
        }
        for (Long id : mapa.keySet()) {
            ItemValorDivida item = new ItemValorDivida();
            item.setValor(mapa.get(id));
            item.setTributo(new Tributo(id));
            item.setValorDivida(vd);
            vd.getItemValorDividas().add(item);
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteCancelamentoDA> cancelarInscricoesDividaAtiva(List<ItemInscricaoDividaAtiva> itens, AssistenteCancelamentoDA assistente) {
        for (ItemInscricaoDividaAtiva item : itens) {
            try {
                cancelarItem(item, assistente.getUsuarioSistema());
                assistente.conta();
            } catch (Exception e) {
                logger.error("Erro ao cancelar a inscricao em D.A.: ", e);
                assistente.addError(e.getMessage());
            }
        }
        return new AsyncResult<>(assistente);
    }


    private void cancelarItem(ItemInscricaoDividaAtiva item, UsuarioSistema usuarioSistema) {
        item = em.find(ItemInscricaoDividaAtiva.class, item.getId());
        List<ParcelaValorDivida> novasParcelas = recuperarParcelasItemInscricaoDividaAtiva(item);
        if (novasParcelas.isEmpty() || novasParcelas == null) {
            throw new ExcecaoNegocioGenerica("Não foram encontrado parcelas para o cadastro " + item.getCadastro().getNumeroCadastro() + " .");
        }
        verificarItemInscricaoDividaAtivaAjuizado(item, usuarioSistema);
        verificarSeExistemParcelasDiferentesDeAberto(novasParcelas);
        cancelarParcelasNovas(novasParcelas);
        cancelarCertidoesDoItem(item);
        abrirParcelasDoItemDeInscricaoDividaAtiva(item);
        cancelarItemInscricaoDividaAtiva(item);
    }

    private void cancelarItemInscricaoDividaAtiva(ItemInscricaoDividaAtiva item) {
        item.setSituacao(ItemInscricaoDividaAtiva.Situacao.CANCELADO);
        em.merge(item);
    }

    private void abrirParcelasDoItemDeInscricaoDividaAtiva(ItemInscricaoDividaAtiva item) {
        for (InscricaoDividaParcela inscricaoDividaParcela : item.getItensInscricaoDividaParcelas()) {
            ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, inscricaoDividaParcela.getParcelaValorDivida().getId());
            SituacaoParcelaValorDivida ultimaSituacaoParcela = consultaDebitoFacade.getUltimaSituacao(parcela);
            if (ultimaSituacaoParcela == null || (SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA.equals(ultimaSituacaoParcela.getSituacaoParcela()) ||
                SituacaoParcela.CANCELAMENTO.equals(ultimaSituacaoParcela.getSituacaoParcela()) ||
                SituacaoParcela.CANCELADO_RECALCULO.equals(ultimaSituacaoParcela.getSituacaoParcela()))) {
                em.persist(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcela, parcela.getUltimaSituacao().getSaldo()));
                for (ResultadoParcela resultadoParcela : new ConsultaParcela()
                    .addParameter(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Operador.IGUAL, parcela.getValorDivida().getId())
                    .addParameter(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Operador.IGUAL, !parcela.getOpcaoPagamento().getPromocional())
                    .addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, Arrays.asList(SituacaoParcela.CANCELAMENTO, SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA, SituacaoParcela.CANCELADO_RECALCULO))
                    .addComplementoDoWhere(" and not exists(select 1 from parcelavalordivida pvd " +
                        "                                     inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
                        "                                   where pvd.valordivida_id = " + parcela.getValorDivida().getId() + " " +
                        "                                     and spvd.situacaoparcela in ('" + SituacaoParcela.PAGO.name() + "', '" + SituacaoParcela.BAIXADO.name() + "')) ")
                    .executaConsulta().getResultados()) {
                    ParcelaValorDivida pvd = em.find(ParcelaValorDivida.class, resultadoParcela.getIdParcela());
                    em.persist(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, pvd, pvd.getUltimaSituacao().getSaldo()));
                }
            }
        }
    }

    private void verificarItemInscricaoDividaAtivaAjuizado(ItemInscricaoDividaAtiva item, UsuarioSistema usuarioSistema) {
        List<ParcelaValorDivida> parcelasValorDivida = recuperarParcelasItemInscricaoDividaAtiva(item);
        for (ParcelaValorDivida pvd : parcelasValorDivida) {
            if (pvd.getDividaAtivaAjuizada() && !hasPermissaoParaCancelarAjuizada(usuarioSistema)) {
                String referenciaItem = item.getCadastro() != null ? "O cadastro '" + item.getCadastro() + "' " : "O contribuinte '" + item.getPessoa() + "' ";
                throw new ExcecaoNegocioGenerica(referenciaItem + " na inscrição em dívida ativa '" +
                    item.getInscricaoDividaAtiva().getNumero() + "/" + item.getInscricaoDividaAtiva().getExercicio().getAno() + "' contém parcelas ajuizadas");
            }
        }
        parcelasValorDivida.clear();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void depoisDePagar(Calculo calculo) {
        ItemInscricaoDividaAtiva itemInscricaoDividaAtiva = em.find(ItemInscricaoDividaAtiva.class, calculo.getId());
        CertidaoDividaAtiva cda = certidaoDividaAtivaFacade.recuperaCertidao(itemInscricaoDividaAtiva);
        if (cda != null) {
            List<ResultadoParcela> resultadoParcelas = certidaoDividaAtivaFacade.recuperaParcelasDaCertidaoDaView(cda);
            boolean pagas = true;
            for (ResultadoParcela result : resultadoParcelas) {
                if (result.getSituacaoNameEnum().equals(SituacaoParcela.EM_ABERTO.name())) {
                    pagas = false;
                    break;
                }
            }
            if (pagas) {
                cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.QUITADA);
                cda = em.merge(cda);
                comunicaSofPlan.alterarCDA(Lists.newArrayList(cda));
            }
        }
        Calculo calculoOriginal = arrecadacaoFacade.buscarCalculoOriginalDaDividaAtiva(calculo);
        if (calculoOriginal != null) {
            sessionContext.getBusinessObject(calculoOriginal.getTipoCalculo().getExecutorDepoisDePagar())
                .depoisDePagar(calculoOriginal);
        }
    }

    public String numeroProcessoJudicialCda(Long idParcela) {
        if (idParcela != null) {
            String sql = " select numero_ajuizamento_parcela(:parcela) from dual ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("parcela", idParcela);
            String retorno = "";
            List<String> lista = q.getResultList();
            for (String o : lista) {
                retorno += o + ", ";
            }
            if (!retorno.isEmpty()) {
                retorno = retorno.substring(0, retorno.length() - 2);
            }
            return retorno.replaceAll("null", "");
        } else {
            return "";
        }
    }

    public boolean todosOsDebitosAjuizadosDoProcessoEstaoSelecionados(ProcessoParcelamento processoParcelamento, List<ResultadoParcela> resultadoParcelas) {
        Map<String, List<ResultadoParcela>> mapa = Maps.newHashMap();
        for (ResultadoParcela resultadoParcela : resultadoParcelas) {
            if (!mapa.containsKey(resultadoParcela.getNumeroProcessoAjuizamento())) {
                mapa.put(resultadoParcela.getNumeroProcessoAjuizamento(), new ArrayList<ResultadoParcela>());
            }
            mapa.get(resultadoParcela.getNumeroProcessoAjuizamento()).add(resultadoParcela);
        }

        List<Long> idDividas = Lists.newArrayList();
        ParamParcelamento paramParcelamento = em.find(ParamParcelamento.class, processoParcelamento.getParamParcelamento().getId());
        for (ParamParcelamentoDivida paramParcelamentoDivida : paramParcelamento.getDividas()) {
            idDividas.add(paramParcelamentoDivida.getDivida().getId());
        }
        for (String s : mapa.keySet()) {
            List<Long> ids = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : mapa.get(s)) {
                ids.add(resultadoParcela.getIdParcela());
            }
            String sql = " select distinct  vw.parcela_id                                                                                                                                                                                                                                                                                                                                                                                                                                     \n" +
                "from vwconsultadedebitos vw ";

            String where = " where vw.situacaoparcela = :situacao " +
                " and vw.parcela_id not in (:ids) " +
                " and vw.divida_id in (:idDividas)" +
                " and numero_ajuizamento_parcela(vw.parcela_id) = :processo ";

            if (processoParcelamento.getCadastro() != null) {
                where += " and vw.cadastro_id = :cadastro";
            } else {
                where += " and vw.pessoa = :cadastro";
            }
            Query q = em.createNativeQuery(sql + where);
            q.setParameter("situacao", SituacaoParcela.EM_ABERTO.name());
            q.setParameter("processo", s);
            q.setParameter("ids", ids);
            q.setParameter("idDividas", idDividas);
            if (processoParcelamento.getCadastro() != null) {
                q.setParameter("cadastro", processoParcelamento.getCadastro().getId());
            } else {
                q.setParameter("cadastro", processoParcelamento.getPessoa().getId());
            }
            return q.getResultList().isEmpty();
        }
        return false;
    }

    // todo - Depois do merge para o produção, fazer a integração com a SoftPlan chamar esse método
    public String buscaAutoInfracao(Long idItemInscricaoDividaAtiva) {
        String sql = "select ai.numero from parcelavalordivida pvd  " +
            "inner join inscricaodividaparcela idp on idp.parcelavalordivida_id = pvd.id " +
            "inner join iteminscricaodividaativa iida on iida.id = idp.iteminscricaodividaativa_id " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "left join calculomultafiscalizacao multa on multa.id = vd.calculo_id " +
            "left join calculofiscalizacao fisc on fisc.id = vd.calculo_id " +
            "inner join autoinfracaofiscal ai on ai.id = coalesce(multa.autoinfracaofiscal_id,fisc.autoinfracaofiscal_id) " +
            "where iida.id = :id";
        Query q = em.createNativeQuery(sql).setParameter("id", idItemInscricaoDividaAtiva);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList().get(0).toString();
        }
        return "0";
    }

    public List<Divida> recuperarDividasDosItensDeIncricao(InscricaoDividaAtiva inscricao) {
        String hql = "select distinct item.divida from ItemInscricaoDividaAtiva item " +
            " where item.inscricaoDividaAtiva = :inscricao ";
        return em.createQuery(hql).setParameter("inscricao", inscricao).getResultList();
    }

    public InscricaoDividaAtiva recuperaInscricaoPorItem(Long id) {
        return (InscricaoDividaAtiva) em.createNativeQuery("select ida.* from inscricaoDividaAtiva ida " +
            "inner join itemInscricaoDividaAtiva iida on iida.inscricaoDividaAtiva_id = ida.id " +
            "where iida.id = :idItem", InscricaoDividaAtiva.class).setParameter("idItem", id).getResultList().get(0);
    }

    public String getJuncao(StringBuilder sb) {
        if (sb.toString().length() > "where".length()) {
            return "and";
        } else {
            return "where";
        }
    }

    public List<VOConsultaDividaAtiva> consultaInscricoes(ObjetoPesquisaInscricao obj) {
        StringBuilder sql = new StringBuilder("select * from vwconsultadividaativa vw ");
        StringBuilder condicao = new StringBuilder();

        if (obj.getInscricaoExercicio() != null) {
            condicao.append(getJuncao(condicao)).append(" vw.anoinscricao = :inscricaoExercicio ");
        }

        if (obj.getInscricaoNumero() != null) {
            condicao.append(getJuncao(condicao)).append(" vw.numeroinscricao = :inscricaoNumero ");
        }

        if (obj.getVencimentoParcelaDividaAtiva() != null) {
            condicao.append(getJuncao(condicao)).append(" vw.vencimento = :vencimentoParcelaDividaAtiva ");
        }

        if (obj.getCertidaoNumero() != null) {
            condicao.append(getJuncao(condicao)).append(" vw.numerocertidao = :certidaoNumero ");
        }

        if (obj.getCertidaoExercicio() != null) {
            condicao.append(getJuncao(condicao)).append(" vw.exercicio_cda = :certidaoExercicio ");
        }

        if (obj.getNumeroAjuizamento() != null
            && !obj.getNumeroAjuizamento().trim().isEmpty()) {
            condicao.append(getJuncao(condicao)).append(" vw.numeroajuizamento = :numeroAjuizamento ");
        }

        if (obj.getCodigoCadastro() != null
            && !obj.getCodigoCadastro().trim().isEmpty()) {
            condicao.append(getJuncao(condicao)).append(" vw.inscricaocadastral = :codigoCadastro ");
        }

        if (obj.getNomeRazaoSocial() != null
            && !obj.getNomeRazaoSocial().trim().isEmpty()) {
            condicao.append(getJuncao(condicao)).append(" trim(lower(vw.nomerazao)) like trim(lower(:nomeRazaoSocial)) ");
        }

        if (obj.getCpfCnpj() != null
            && !obj.getCpfCnpj().trim().isEmpty()) {
            condicao.append(getJuncao(condicao)).append(" trim(replace(replace(replace(vw.cpfcnpj,'.',''),'-',''),'/','')) = trim(replace(replace(replace(:cpfCnpj,'.',''),'-',''),'/','')) ");
        }

        System.out.println(sql.toString() + condicao.toString());
        Query q = em.createNativeQuery(sql.append(condicao).append(" order by anoinscricao desc, vw.id_inscricao asc").toString());

        if (obj.getInscricaoExercicio() != null) {
            q.setParameter("inscricaoExercicio", obj.getInscricaoExercicio());
        }

        if (obj.getInscricaoNumero() != null) {
            q.setParameter("inscricaoNumero", obj.getInscricaoNumero());
        }

        if (obj.getVencimentoParcelaDividaAtiva() != null) {
            q.setParameter("vencimentoParcelaDividaAtiva", obj.getVencimentoParcelaDividaAtiva());
        }

        if (obj.getCertidaoNumero() != null) {
            q.setParameter("certidaoNumero", obj.getCertidaoNumero());
        }

        if (obj.getCertidaoExercicio() != null) {
            q.setParameter("certidaoExercicio", obj.getCertidaoExercicio());
        }

        if (obj.getNumeroAjuizamento() != null
            && !obj.getNumeroAjuizamento().trim().isEmpty()) {
            q.setParameter("numeroAjuizamento", obj.getNumeroAjuizamento());
        }

        if (obj.getCodigoCadastro() != null
            && !obj.getCodigoCadastro().trim().isEmpty()) {
            q.setParameter("codigoCadastro", obj.getCodigoCadastro());
        }

        if (obj.getNomeRazaoSocial() != null
            && !obj.getNomeRazaoSocial().trim().isEmpty()) {
            q.setParameter("nomeRazaoSocial", "%" + obj.getNomeRazaoSocial() + "%");
        }

        if (obj.getCpfCnpj() != null
            && !obj.getCpfCnpj().trim().isEmpty()) {
            q.setParameter("cpfCnpj", obj.getCpfCnpj());
        }


        return preencheListaConsulta(q.getResultList());
    }

    public List<VOConsultaDividaAtiva> preencheListaConsulta(List<Object[]> objs) {
        List<VOConsultaDividaAtiva> lista = new ArrayList<>();
        if (objs != null) {
            for (Object[] obj : objs) {
                lista.add(new VOConsultaDividaAtiva(obj));
            }
        }
        return lista;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ResultadoParcela buscarParcelaDividaAtivaDaParcelaOriginal(Long idParcela) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addComplementoJoin(" join ItemInscricaoDividaAtiva itens on itens.id = vw.calculo_id ");
        consultaParcela.addComplementoJoin(" join InscricaoDividaParcela itensParc on itensparc.iteminscricaodividaativa_id = itens.id ");
        consultaParcela.addComplementoDoWhere(" where itensparc.parcelavalordivida_id = " + idParcela);
        consultaParcela.executaConsulta();
        if (!consultaParcela.getResultados().isEmpty()) {
            return consultaParcela.getResultados().get(0);
        }
        return null;
    }

    public List<InscricaoDividaParcela> recuperarParcelasInscritasPorInscricao(InscricaoDividaAtiva inscricaoDividaAtiva) {
        List<InscricaoDividaParcela> retorno = Lists.newArrayList();
        if (inscricaoDividaAtiva != null) {
            String sql = "select parc.* from inscricaodividaativa ins " +
                "inner join iteminscricaodividaativa item on item.inscricaodividaativa_id = ins.id " +
                "inner join inscricaodividaparcela parc on parc.iteminscricaodividaativa_id = item.id " +
                "where ins.id = :idInscricao";
            Query q = em.createNativeQuery(sql, InscricaoDividaParcela.class);
            q.setParameter("idInscricao", inscricaoDividaAtiva.getId());
            q.setMaxResults(1000);
            retorno = q.getResultList();
            for (InscricaoDividaParcela parc : retorno) {
                parc.setReferenciaParcela(parc.getParcelaValorDivida().getUltimaSituacao().getReferencia());
            }
        }
        return retorno;
    }

    public String montarSelectCountCancelamento() {
        return "select distinct  item.id ";
    }

    public List<ResultadoPesquisaDividaAtiva> buscarItensInscricao(FiltroPesquisaDividaAtiva filtros, Integer firstResult) {
        String select = montarSelectCancelamento() + montarFromCancelamento() + montarCondicaoCancelamento(filtros) + montarOrdenacaoCancelamento();
        Query qSelect = em.createNativeQuery(select);
        qSelect.setFirstResult(firstResult);
        if (filtros.getMaxResults() != null) {
            qSelect.setMaxResults(filtros.getMaxResults());
        }
        List<ResultadoPesquisaDividaAtiva> listaEntidadeNaoMapeada = new ArrayList<>();
        List<Object[]> listaArrayObjetos = qSelect.getResultList();
        int i;
        for (i = 0; i < listaArrayObjetos.size(); i++) {
            Object[] ob = listaArrayObjetos.get(i);
            ResultadoPesquisaDividaAtiva resultado = new ResultadoPesquisaDividaAtiva();
            resultado.setCadastro((String) ob[0]);
            resultado.setNomeRazaoSocial((String) ob[1]);
            resultado.setCpfCnpj((String) ob[2]);
            resultado.setDivida((String) ob[3]);
            resultado.setExercicio((BigDecimal) ob[4]);
            resultado.setDataInscricao((Date) ob[5]);
            resultado.setTermo((BigDecimal) ob[6]);
            resultado.setLivro((BigDecimal) ob[7]);
            resultado.setPagina((BigDecimal) ob[8]);
            resultado.setLinha((BigDecimal) ob[9]);
            resultado.setItemInscricaoId((BigDecimal) ob[10]);
            resultado.setAjuizada(((BigDecimal) ob[11]).compareTo(BigDecimal.ZERO) != 0 ? true : false);
            resultado.setValor((BigDecimal) ob[12]);
            listaEntidadeNaoMapeada.add(resultado);
        }
        return listaEntidadeNaoMapeada;
    }

    public String montarSelectCancelamento() {
        String select = "select distinct coalesce(ci.inscricaocadastral, ce.inscricaocadastral, to_char(cr.codigo)) as cadastro " +
            ", coalesce(pf.nome, pj.razaosocial) as nome " +
            ",coalesce(pf.cpf, pj.cnpj) as cpf_cpnj " +
            ", divida.descricao as divida " +
            ", exercicio.ano as exercicio " +
            ", inscricao.DATAINSCRICAO  as data_inscricao " +
            ", termo.NUMERO as termo " +
            ", livro.NUMERO as livro " +
            ", linha.PAGINA as pagina " +
            ", linha.SEQUENCIA " +
            ", item.id as id_item " +
            ", coalesce(pvdOriginado.dividaAtivaAjuizada,0) as ajuizada " +
            ", sum(pvd.VALOR) as valor ";
        return select;
    }

    public String montarFromCancelamento() {
        String from =
            " from ITEMINSCRICAODIVIDAATIVA item " +
                " inner join INSCRICAODIVIDAATIVA inscricao on inscricao.id = item.INSCRICAODIVIDAATIVA_ID " +
                " inner join processocalculo proc on proc.id = inscricao.id " +
                " inner join InscricaoDividaParcela itemparcela on itemparcela.itemInscricaoDividaAtiva_id = item.id " +
                " inner join parcelavalordivida pvd on pvd.id = itemparcela.parcelaValorDivida_id " +
                " inner join valordivida vd on vd.id = pvd.valordivida_id " +
                " inner join calculo cal on cal.id = item.id " +
                " inner join valordivida vdOriginado on vdOriginado.calculo_id = cal.id " +
                " inner join parcelavalordivida pvdOriginado on pvdOriginado.valordivida_id = vdOriginado.id " +
                " inner JOIN situacaoparcelavalordivida spvd ON spvd.id = pvdOriginado.situacaoAtual_id " +
                " inner join divida divida on divida.id = vd.divida_id " +
                " inner join divida dividaOriginado on dividaOriginado.id = vdOriginado.divida_id " +
                " left join cadastroimobiliario ci on ci.id = cal.cadastro_id " +
                " left join cadastroeconomico ce on ce.id = cal.cadastro_id " +
                " left join cadastrorural cr on cr.id = cal.cadastro_id " +
                " inner join pessoa p on p.id = item.PESSOA_ID " +
                " left join pessoafisica pf on pf.id = p.id " +
                " left join pessoajuridica pj on pj.id = p.id " +
                " inner join exercicio on exercicio.id = proc.EXERCICIO_ID " +
                " left join LINHADOLIVRODIVIDAATIVA linha on linha.ITEMINSCRICAODIVIDAATIVA_ID = item.id " +
                " left join ITEMLIVRODIVIDAATIVA itemlivro on itemlivro.ID = linha.ITEMLIVRODIVIDAATIVA_ID " +
                " left join LIVRODIVIDAATIVA livro on livro.id = itemlivro.LIVRODIVIDAATIVA_ID " +
                " left join TERMOINSCRICAODIVIDAATIVA termo on termo.id = linha.TERMOINSCRICAODIVIDAATIVA_ID ";
        return from;
    }

    public String montarCondicaoCancelamento(FiltroPesquisaDividaAtiva filtros) {
        String juncao = " and ";
        String aliasCadastro = "";
        String condicao = "spvd.situacaoParcela = '" + SituacaoParcela.EM_ABERTO.name() + "'";
        if (filtros.getTipoCadastro() != null) {
            switch (filtros.getTipoCadastro()) {
                case IMOBILIARIO:
                    aliasCadastro = " ci.inscricaoCadastral ";
                    break;
                case ECONOMICO:
                    aliasCadastro = " ce.inscricaoCadastral ";
                    break;
                case RURAL:
                    aliasCadastro = " to_char(cr.codigo) ";
                    break;

            }
            if (filtros.getCadastroInicial() != null && !filtros.getCadastroInicial().trim().isEmpty()) {
                condicao += juncao + aliasCadastro + " >= '" + filtros.getCadastroInicial() + "'";
                juncao = " and ";

            }
            if (filtros.getCadastroFinal() != null && !filtros.getCadastroInicial().trim().isEmpty()) {
                condicao += juncao + aliasCadastro + " <= '" + filtros.getCadastroFinal() + "'";
                juncao = " and ";
            }
            if (filtros.getPessoa() != null) {
                condicao += juncao + "p.id = " + filtros.getPessoa().getId();
                juncao = " and ";
            }
        }
        if (filtros.getDatatInicio() != null) {
            condicao += juncao + "inscricao.DATAINSCRICAO  >= '" + Util.formatterDiaMesAno.format(filtros.getDatatInicio()) + "'";
            juncao = " and ";
        }
        if (filtros.getDataFim() != null) {
            condicao += juncao + "inscricao.DATAINSCRICAO  <= '" + Util.formatterDiaMesAno.format(filtros.getDataFim()) + "'";
            juncao = " and ";
        }
        if (filtros.getExericioInicial() != null) {
            condicao += juncao + "exercicio.ano >= " + filtros.getExericioInicial();
            juncao = " and ";
        }
        if (filtros.getExercicioFinal() != null) {
            condicao += juncao + "exercicio.ano <= " + filtros.getExercicioFinal();
            juncao = " and ";
        }
        if (filtros.getTermoInicial() != null) {
            condicao += juncao + "termo.numero >= " + filtros.getTermoInicial();
            juncao = " and ";
        }
        if (filtros.getTermoFinal() != null) {
            condicao += juncao + "termo.numero <= " + filtros.getTermoFinal();
            juncao = " and ";
        }
        if (filtros.getLivro() != null) {
            condicao += juncao + "livro.numero = " + filtros.getLivro();
            juncao = " and ";
        }
        if (filtros.getSituacao() != null) {
            condicao += juncao + " item.situacao = '" + filtros.getSituacao() + "'";
            juncao = " and ";
        }

        int ajuizada = filtros.getAjuizada() ? 1 : 0;
        condicao += juncao + " coalesce(pvdOriginado.dividaAtivaAjuizada,0) = " + ajuizada;
        juncao = " and ";

        String stringDivida = "";
        for (Divida d : filtros.getDividas()) {
            stringDivida += d.getId() + ", ";
        }
        if (!stringDivida.isEmpty()) {
            stringDivida = stringDivida.substring(0, stringDivida.length() - 2);
            condicao += juncao + " (divida.id in (" + stringDivida + ") or dividaOriginado.id in (" + stringDivida + "))";
        }
        condicao += juncao + " (linha.id is null or linha.id = (select max(ln.id) from LINHADOLIVRODIVIDAATIVA ln where ln.ITEMINSCRICAODIVIDAATIVA_ID = item.id)) ";
        return " where " + condicao;
    }

    public String montarOrdenacaoCancelamento() {
        return " group by COALESCE(ci.inscricaocadastral, ce.inscricaocadastral, TO_CHAR(cr.codigo)), " +
            "  COALESCE(pf.nome, pj.razaosocial), " +
            "  COALESCE(pf.cpf, pj.cnpj), " +
            "  divida.descricao, " +
            "  exercicio.ano, " +
            "  inscricao.DATAINSCRICAO, " +
            "  termo.NUMERO, " +
            "  livro.NUMERO, " +
            "  linha.PAGINA, " +
            "  linha.SEQUENCIA, " +
            "  item.id, " +
            "  COALESCE(pvdOriginado.dividaAtivaAjuizada,0) order by item.id desc ";
    }

    public boolean hasPermissaoParaCancelarAjuizada(UsuarioSistema usuarioSistema) {
        return getUsuarioSistemaFacade().validaAutorizacaoUsuario(
            usuarioSistema,
            AutorizacaoTributario.PERMITIR_CANCELAR_INSCRICAO_DIVIDA_ATIVA_AJUIZADA);
    }

    public ProcessoDeProtesto buscarProcessoAtivoDaParcela(Long idParcela) {
        return consultaDebitoFacade.buscarProcessoAtivoDaParcela(idParcela);
    }

    public boolean hasDividaAtivaPagoPorParcelamentoEmAberto(Exercicio exercicio, Cadastro cadastro, Divida divida) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, exercicio.getAno());
        consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, cadastro.getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, divida.getDivida().getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO,
            br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, SituacaoParcela.PAGO_PARCELAMENTO);
        return !consultaParcela.executaConsulta().getResultados().isEmpty();
    }
}
