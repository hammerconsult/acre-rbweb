package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoContribuicaoMelhoria;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by William on 30/06/2016.
 */
@Stateless
public class ContribuicaoMelhoriaLancamentoFacade extends AbstractFacade<ContribuicaoMelhoriaLancamento> {

    ItemCalculoContribuicaoMelhoria itemCalculoContribuicaoMelhoria = new ItemCalculoContribuicaoMelhoria();
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GeraValorDividaContribuicaoMelhoria geraDebito;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;

    public ContribuicaoMelhoriaLancamentoFacade() {
        super(ContribuicaoMelhoriaLancamento.class);
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ContribuicaoMelhoriaLancamento recuperar(Object id) {
        ContribuicaoMelhoriaLancamento contribuicaoMelhoriaLancamento = em.find(ContribuicaoMelhoriaLancamento.class, id);
        contribuicaoMelhoriaLancamento.getItens().size();
        return contribuicaoMelhoriaLancamento;
    }

    public List<CadastroImobiliario> buscarCadastrosImibilariosPorEdital(ContribuicaoMelhoriaEdital contribuicaoMelhoriaEdital, String parte) {
        String sql = " select c.*, ci.* from ContribuicaoMelhoriaLancamento cml " +
            " inner join ContribuicaoMelhoriaLancamentoItem cmli on cml.id = cmli.contribuicaoMelhoriaLancamento_id " +
            " inner join cadastro c on cmli.cadastroimobiliario_id = c.id " +
            " inner join cadastroimobiliario ci on c.id = ci.id " +
            " inner join ContribuicaoMelhoriaEdital cme on cml.edital_id = cme.id " +
            " where cme.id = :edital_id and ci.codigo like :parte " +
            " and ci.ativo = 1" +
            " order by ci.codigo ";
        Query q = em.createNativeQuery(sql, CadastroImobiliario.class);
        q.setParameter("edital_id", contribuicaoMelhoriaEdital.getId());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return (List<CadastroImobiliario>) q.getResultList();
    }


    public ContribuicaoMelhoriaLancamento salvarContribuicaoMelhoria(ContribuicaoMelhoriaLancamento entity) {
        entity = em.merge(entity);
        return entity;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public Future<AssistenteBarraProgresso> efetivar(List<ContribuicaoMelhoriaItem> itens,
                                                     ContribuicaoMelhoriaLancamento entity,
                                                     AssistenteBarraProgresso assistenteBarraProgresso) {
        for (ContribuicaoMelhoriaItem contribuicaoMelhoriaItem : itens) {
            assistenteBarraProgresso.conta();
            gerarDebito(entity, contribuicaoMelhoriaItem, assistenteBarraProgresso);
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }


    public boolean verificarSeCadastroJaFoiLancadoParaOEdital(ContribuicaoMelhoriaEdital edital, CadastroImobiliario cadastroImobiliario) {
        String sql = " select contribuicao.id from contribuicaomelhorialanc  contribuicao " +
            " inner join ContribuicaoMelhoriaItem item on item.CONTRIBUICAOMELHORIALANC_ID = contribuicao.id " +
            " where situacao = :situacao and item.CADASTROIMOBILIARIO_ID = :cadastroImobiliario and contribuicao.EDITAL_ID = :edital ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", SituacaoContribuicaoMelhoria.EFETIVADO.name());
        q.setParameter("edital", edital.getId());
        q.setParameter("cadastroImobiliario", cadastroImobiliario.getId());
        return q.getResultList().isEmpty();
    }

    @Asynchronous
    public Future<AssistenteBarraProgresso> gerarDebitoValorDivida(List<CalculoContribuicaoMelhoria> calculo,
                                                                   AssistenteBarraProgresso assistenteBarraProgresso) throws Exception {
        for (CalculoContribuicaoMelhoria calc : calculo) {
            assistenteBarraProgresso.conta();
            geraDebito.geraDebito(calc);
        }
        return new AsyncResult<>(assistenteBarraProgresso);
    }


    public List<CalculoContribuicaoMelhoria> recuperarCalculoContribuicaoMelhoria(ContribuicaoMelhoriaLancamento contribuicaoMelhoria) {
        String hql = "from CalculoContribuicaoMelhoria  where contribuicaoMelhoria = :contribuicao ";
        Query q = em.createQuery(hql);
        q.setParameter("contribuicao", contribuicaoMelhoria);
        return q.getResultList();
    }


    public void gerarDebito(ContribuicaoMelhoriaLancamento contribuicaoMelhoriaLancamento, ContribuicaoMelhoriaItem item, AssistenteBarraProgresso assistente) {
        try {
            gerarProcessoCalculo(contribuicaoMelhoriaLancamento, item, assistente);
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public ProcessoCalculoContribuicaoMelhoria gerarProcessoCalculo(ContribuicaoMelhoriaLancamento contribuicaoMelhoriaLancamento, ContribuicaoMelhoriaItem item, AssistenteBarraProgresso assistente) {
        CalculoContribuicaoMelhoria calculoContribuicaoMelhoria = new CalculoContribuicaoMelhoria();
        criarCalculo(calculoContribuicaoMelhoria, contribuicaoMelhoriaLancamento, item);
        ProcessoCalculoContribuicaoMelhoria processoCalculo = new ProcessoCalculoContribuicaoMelhoria();
        calculoContribuicaoMelhoria.setProcessoCalcContMelhoria(processoCalculo);
        criarProcessoCalculo(processoCalculo, calculoContribuicaoMelhoria, assistente);
        return em.merge(processoCalculo);
    }


    private void criarCalculo(CalculoContribuicaoMelhoria calculo, ContribuicaoMelhoriaLancamento contribuicaoMelhoriaLancamento, ContribuicaoMelhoriaItem item) {
        calculo.setContribuicaoMelhoria(contribuicaoMelhoriaLancamento);
        calculo.setDataCalculo(new Date());
        calculo.setSimulacao(Boolean.FALSE);
        calculo.setValorEfetivo(item.getAreaAtingida());
        calculo.setValorReal(calculo.getValorEfetivo());

        for (Propriedade propriedade : cadastroImobiliarioFacade.recuperarProprietariosVigentes(item.getCadastroImobiliario())) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setPessoa(propriedade.getPessoa());
            calculoPessoa.setCalculo(calculo);
            calculo.getPessoas().add(calculoPessoa);
        }
        calculo.setCadastro(item.getCadastroImobiliario());
        if (calculo.getPessoas() == null) {
            calculo.setPessoas(Lists.<CalculoPessoa>newArrayList());
        }
        criarItemDoCalculo(calculo);
    }

    private void criarItemDoCalculo(CalculoContribuicaoMelhoria calculo) {
        ItemCalculoContribuicaoMelhoria item = new ItemCalculoContribuicaoMelhoria();
        ConfiguracaoTributario configuracao = configuracaoTributarioFacade.retornaUltimo();
        if (configuracao.getTributoContribuicaoMelhoria() != null) {
            item.setValor(calculo.getValorEfetivo());
            item.setTributo(configuracao.getTributoContribuicaoMelhoria());
            item.setCalculoContribuicao(calculo);
            calculo.getItensCalculoContribuicaoMelhorias().add(item);
        }
    }

    private void criarProcessoCalculo(ProcessoCalculoContribuicaoMelhoria processoCalculo, CalculoContribuicaoMelhoria calculoContribuicaoMelhoria, AssistenteBarraProgresso assistente) {
        processoCalculo.getCalculos().add(calculoContribuicaoMelhoria);
        processoCalculo.setCompleto(Boolean.TRUE);
        processoCalculo.setDataLancamento(calculoContribuicaoMelhoria.getDataCalculo());
        processoCalculo.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaContribuicaoMelhoria());
        processoCalculo.setExercicio(assistente.getExercicio());
    }
}
