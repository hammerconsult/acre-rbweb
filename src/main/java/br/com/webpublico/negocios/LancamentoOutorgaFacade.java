/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.LancamentoOutorgaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.exception.LancamentoOutorgaException;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MensagemValidacao;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cheles
 */
@Stateless
public class LancamentoOutorgaFacade extends AbstractFacade<LancamentoOutorga> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ParametroOutorgaFacade parametroOutorgaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GeraValorDividaLancamentoOutorga geraDebito;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private ContribuinteDebitoOutorgaFacade contribuinteDebitoOutorgaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;


    public LancamentoOutorgaFacade() {
        super(LancamentoOutorga.class);
    }

    public GeraValorDividaLancamentoOutorga getGeraDebito() {
        return geraDebito;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public LancamentoOutorga recuperar(LancamentoOutorga lancamentoOutorga) {
        LancamentoOutorga lancamento = em.find(LancamentoOutorga.class, lancamentoOutorga.getId());

        lancamento.getListaDeProcessoCalculoLancamentoOutorga().size();
        lancamento.getArquivos().size();
        for (ProcessoCalculoLancamentoOutorga processo : lancamento.getListaDeProcessoCalculoLancamentoOutorga()) {
            processo.getListaDeCalculoLancamentoOutorga().size();
        }

        return lancamento;
    }

    public List<CadastroEconomico> contribuintesCadastrados(String parte) {
        String hql = "  select c  from ContribuinteDebitoOutorga  ce join ce.cadastroEconomico c "
            + " where  c.pessoa in (select pj from PessoaJuridica pj "
            + "                    where lower(pj.razaoSocial) like :parte "
            + "                       or lower(pj.nomeFantasia) like :parte "
            + "                       or lower(pj.cnpj) like :parte "
            + "                       or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte)) "
            + " or c.inscricaoCadastral like :parte ";

        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }


    private void validarOpcoesPagamento(Divida divida) throws IllegalArgumentException {
        String sql = "select op.* from OpcaoPagamentoDivida op " +
            "   where op.divida_id = :divida " +
            "     and trunc(:dataAtual) between trunc(coalesce(op.inicioVigencia, current_date)) " +
            "     and trunc(coalesce(op.finalVigencia, current_date))";
        Query q = em.createNativeQuery(sql, OpcaoPagamentoDivida.class);
        q.setParameter("divida", divida.getId());
        q.setParameter("dataAtual", new Date());
        List<OpcaoPagamentoDivida> opcoes = q.getResultList();
        if (opcoes.isEmpty()) {
            FacesUtil.addError("Não foi possível continuar!", "Nenhuma opção de pagamento válida para a dívida selecionada");
            throw new IllegalArgumentException("Nenhuma opção de pagamento válida para a dívida selecionada.");
        }
    }

    public ProcessoCalculoLancamentoOutorga salvarNovo(LancamentoOutorga lancamento, OutorgaIPO outorgaIPO) {
        ProcessoCalculoLancamentoOutorga processo = null;
        try {
            processo = gerarDebitos(lancamento, outorgaIPO);
            if (processo != null) {
                for (ArquivoLancamentoOutorga a : lancamento.getArquivos()) {
                    if (a.getArquivo().getId() == null) {
                        if (!a.getExcluido()) {
                            salvarArquivo((UploadedFile) a.getFile(), a.getArquivo());
                        }
                    }
                }
                lancamento.setListaDeProcessoCalculoLancamentoOutorga(new ArrayList<ProcessoCalculoLancamentoOutorga>());
                lancamento.getListaDeProcessoCalculoLancamentoOutorga().add(processo);
                validarOpcoesPagamento(processo.getDivida());
                BigDecimal resultadoMultiplicacao = (lancamento.getPassageiroTranspEquiv().multiply(lancamento.getValorDaTarifa()));
                BigDecimal porcentagem = new BigDecimal(5).divide(new BigDecimal(100));
                lancamento.setValorISSCorrespondente(resultadoMultiplicacao.multiply(porcentagem));
                processo.setLancamentoOutorga(lancamento);
                lancamento = em.merge(lancamento);
                processo = lancamento.getListaDeProcessoCalculoLancamentoOutorga().get(0);
            }
        } catch (Exception ex) {
            logger.error("Erro ao gerar processo de calculo do lançamento de outorga: {}", ex);
        }
        return processo;
    }

    public void gerarDebito(ProcessoCalculoLancamentoOutorga processoCalculo) throws Exception {
        geraDebito.geraDebito(processoCalculo);
    }

    public void salvarArquivo(UploadedFile uploadedFile, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = uploadedFile;
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            arq.setTamanho(arquivoRecebido.getSize());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public LancamentoOutorga salvarLancamento(LancamentoOutorga lancamento) {
        for (ArquivoLancamentoOutorga a : lancamento.getArquivos()) {
            if (a.getArquivo().getId() == null) {
                if (!a.getExcluido()) {
                    salvarArquivo((UploadedFile) a.getFile(), a.getArquivo());
                }
            }
        }

        try {
            lancamento = em.merge(lancamento);
        } catch (Exception ex) {
            logger.error("Erro ao salvar o lançamento de outorga: {}", ex);
        }
        return lancamento;
    }

    private ProcessoCalculoLancamentoOutorga gerarDebitos(LancamentoOutorga lancamento, OutorgaIPO outorgaIPO) {
        ParametrosOutorgaRBTrans parametro = parametroOutorgaFacade.getParametroOutorgaRBTransVigente();

        BigDecimal totalPagarEmReais = calcularValorDoLancamento(lancamento, parametro, outorgaIPO);
        if (!totalPagarEmReais.equals(null)) {
            CalculoLancamentoOutorga calculo = new CalculoLancamentoOutorga();
            calculo.setValorEfetivo(totalPagarEmReais);
            calculo.setValorReal(totalPagarEmReais);
            calculo.setTributo(parametro.getTributo());
            calculo.setDataCalculo(new Date());
            calculo.setCadastro(lancamento.getCmc());
            calculo.setSimulacao(false);

            CalculoPessoa cp = new CalculoPessoa();
            cp.setCalculo(calculo);
            cp.setPessoa(lancamento.getCmc().getPessoa());
            calculo.getPessoas().add(cp);

            ProcessoCalculoLancamentoOutorga processo = new ProcessoCalculoLancamentoOutorga();
            processo.setLancamentoOutorga(lancamento);
            processo.setDivida(parametro.getDivida());
            processo.setExercicio(lancamento.getExercicio());
            processo.setListaDeCalculoLancamentoOutorga(new ArrayList<CalculoLancamentoOutorga>());
            processo.getListaDeCalculoLancamentoOutorga().add(calculo);

            calculo.setProcCalcLancamentoOutorga(processo);
            lancamento.setValorOutorga(totalPagarEmReais);
            return calculo.getProcCalcLancamentoOutorga();
        }
        return null;
    }

    private BigDecimal calcularValorDoLancamento(LancamentoOutorga lancamento, ParametrosOutorgaRBTrans parametro, OutorgaIPO outorgaIPO) {
        boolean temOutorgaIpoParaOExercicioEMes = false;
        BigDecimal percentualOutorga = new BigDecimal(0);
        ContribuinteDebitoOutorga contribuinteDebitoOutorga = contribuinteDebitoOutorgaFacade.recuperarContribuinteCalculoOutorga(lancamento);
        if (outorgaIPO != null) {
            percentualOutorga = outorgaIPO.getPercentual();
            temOutorgaIpoParaOExercicioEMes = true;
        } else {
            for (OutorgaIPO ipo : contribuinteDebitoOutorga.getListaIpo()) {
                if (contribuinteDebitoOutorga.getExercicio().equals(lancamento.getExercicio()) && ipo.getMes().equals(lancamento.getMes())) {
                    percentualOutorga = ipo.getPercentual();
                    temOutorgaIpoParaOExercicioEMes = true;
                }
            }
        }

        if (!temOutorgaIpoParaOExercicioEMes) {
            FacesUtil.addError("Não foi possível continuar!", " Percentual da Outorga não encontrado para Mês e Ano informado. Por favor, verifique o cadastro de outorga do C.M.C.");
        } else {
            BigDecimal valor = lancamento.getPassageiroTranspEquiv();
            BigDecimal tarifa = lancamento.getTarifaOutorga().getValor();
            BigDecimal percentual = percentualOutorga;
            lancamento.setPercentualDaOutorga(percentual);
            lancamento.setValorDaTarifa(tarifa);
            BigDecimal total = valor.multiply(tarifa).multiply(percentual);
            return total.divide(new BigDecimal(100));
        }

        return null;
    }

    public CalculoLancamentoOutorga recuperaValorDoLancamento(LancamentoOutorga lancamentoOutorga) {
        Query q = em.createQuery(" select calc from CalculoLancamentoOutorga calc where calc.procCalcLancamentoOutorga.lancamentoOutorga = :lancamento");
        q.setParameter("lancamento", lancamentoOutorga);
        if (!q.getResultList().isEmpty()) {
            return (CalculoLancamentoOutorga) q.getResultList().get(0);
        }

        return null;

    }

    public ParcelaValorDivida recuperaParcelaValorDivida(Long id) {
        String hql = "select pvd from ParcelaValorDivida pvd where pvd.id = :id";
        Query q = em.createQuery(hql, ParcelaValorDivida.class);
        q.setParameter("id", id);
        return (ParcelaValorDivida) q.getSingleResult();
    }

    public void pagarParcela(ParcelaValorDivida parcela, BigDecimal valorTotal) {
        SituacaoParcelaValorDivida situacao = new SituacaoParcelaValorDivida();
        situacao.setSituacaoParcela(SituacaoParcela.ESTORNADO);
        situacao.setDataLancamento(new Date());
        situacao.setParcela(parcela);
        situacao.setSaldo(valorTotal);
        parcela.getSituacoes().add(situacao);
        em.merge(parcela);
    }

    public ParcelaValorDivida recuperaParcelaValorDividaEstorno(Object id) {
        ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, id);
        parcela.getSituacoes().size();
        parcela.getItensParcelaValorDivida().size();
        return parcela;
    }


}
