/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Maps;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author fabio
 */
@Stateless
public class CalculoFiscalizacaoFacade extends AbstractFacade<CalculoFiscalizacao> {

    private static final int TAMANHO_LINHA = 89;
    private static final int QUEBRA_LINHA = 1;
    private static final int CONCATENA_LINHA = 2;
    private static final int TAMANHO_DESCRICAO_VALOR = 67;
    @EJB
    private AcaoFiscalFacade acaoFiscalFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private GeraValorDividaFiscal geraDebitoFiscalizacao;
    @EJB
    private GeraValorDividaMultaFiscal geraDebitoMulta;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ParametroFiscalizacaoFacade parametroFiscalizacaoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CalculoFiscalizacaoFacade() {
        super(CalculoFiscalizacao.class);
    }

    public GeraValorDividaFiscal getGeraDebitoFiscalizacao() {
        return geraDebitoFiscalizacao;
    }

    public GeraValorDividaMultaFiscal getGeraDebitoMulta() {
        return geraDebitoMulta;
    }

    public BigDecimal retornaValorIssDevidoSomado(AutoInfracaoFiscal auto) {
        BigDecimal valorIssDevido = BigDecimal.ZERO;
        for (LancamentoContabilFiscal lcf : auto.getRegistro().getLancamentosContabeis()) {
            valorIssDevido = valorIssDevido.add(lcf.getIssDevido());
        }
        return valorIssDevido;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public CalculoFiscalizacao recuperar(Object id) {
        CalculoFiscalizacao calculo = em.find(CalculoFiscalizacao.class, id);
        calculo.getItensCalculoFiscalizacao().size();
        return calculo;
    }

    public ProcessoCalculoFiscal lancaCalculoFiscalizacao(AutoInfracaoFiscal autoInfracaoFiscal) {
        AcaoFiscal acaoFiscal = autoInfracaoFiscal.getRegistro().getAcaoFiscal();
        Exercicio exercicio = exercicioFacade.recuperarExercicioPeloAno(autoInfracaoFiscal.getAno());
        if (acaoFiscal != null) {
            try {
                ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
                if (configuracaoTributario.getDividaAutoInfracaoISS() != null) {
                    CalculoFiscalizacao calculo = recuperarCalculoDoAuto(autoInfracaoFiscal);
                    if (calculo == null) {
                        calculo = new CalculoFiscalizacao();
                        calculo.setCadastro(acaoFiscal.getCadastroEconomico());
                        calculo.setExercicio(exercicio);
                        calculo.setIsento(false);
                        calculo.setAutoInfracaoFiscal(autoInfracaoFiscal);

                        CalculoPessoa p = new CalculoPessoa();
                        p.setCalculo(calculo);
                        p.setPessoa(acaoFiscal.getCadastroEconomico().getPessoa());
                        calculo.getPessoas().add(p);

                        ProcessoCalculoFiscal processoCalculoFiscal = new ProcessoCalculoFiscal();
                        processoCalculoFiscal.setDataLancamento(new Date());
                        processoCalculoFiscal.setExercicio(exercicio);
                        processoCalculoFiscal.setDivida(configuracaoTributario.getDividaAutoInfracaoISS());
                        calculo.setProcessoCalculoFiscal(processoCalculoFiscal);
                        processoCalculoFiscal.getCalculos().add(calculo);
                    }
                    calculo.setDataCalculo(new Date());
                    calculo.setVencimento(autoInfracaoFiscal.getVencimento());
                    calculo.setValorEfetivo(autoInfracaoFiscal.getValorTotal());
                    calculo.setValorReal(calculo.getValorEfetivo());

                    calculo.getItensCalculoFiscalizacao().clear();
                    geraItens(autoInfracaoFiscal, configuracaoTributario, calculo);
                    calculo = em.merge(calculo);
                    return calculo.getProcessoCalculoFiscal();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return null;
    }

    private void geraItens(AutoInfracaoFiscal autoInfracaoFiscal, ConfiguracaoTributario configuracaoTributario, CalculoFiscalizacao calculo) {
        Map<Tributo, BigDecimal> mapa = Maps.newHashMap();
        mapa.put(configuracaoTributario.getTributoISS(), autoInfracaoFiscal.getValorAutoInfracaoCorrigido());
        if (autoInfracaoFiscal.getJuros().compareTo(BigDecimal.ZERO) > 0
            && configuracaoTributario.getTributoISS().getTributoJuros() != null) {
            if (mapa.containsKey(configuracaoTributario.getTributoISS().getTributoJuros())) {
                mapa.put(configuracaoTributario.getTributoISS().getTributoJuros(), mapa.get(configuracaoTributario.getTributoISS().getTributoJuros()).add(autoInfracaoFiscal.getJuros()));
            } else {
                mapa.put(configuracaoTributario.getTributoISS().getTributoJuros(), autoInfracaoFiscal.getJuros());
            }
        } else if (autoInfracaoFiscal.getJuros().compareTo(BigDecimal.ZERO) > 0
            && configuracaoTributario.getTributoISS().getTributoJuros() == null) {
            mapa.put(configuracaoTributario.getTributoISS(), mapa.get(configuracaoTributario.getTributoISS()).add(autoInfracaoFiscal.getJuros()));
        }
        if (autoInfracaoFiscal.getMulta().compareTo(BigDecimal.ZERO) > 0
            && configuracaoTributario.getTributoISS().getTributoMulta() != null) {
            if (mapa.containsKey(configuracaoTributario.getTributoISS().getTributoMulta())) {
                mapa.put(configuracaoTributario.getTributoISS().getTributoMulta(), mapa.get(configuracaoTributario.getTributoISS().getTributoMulta()).add(autoInfracaoFiscal.getMulta()));
            } else {
                mapa.put(configuracaoTributario.getTributoISS().getTributoMulta(), autoInfracaoFiscal.getMulta());
            }
        } else if (autoInfracaoFiscal.getMulta().compareTo(BigDecimal.ZERO) > 0
            && configuracaoTributario.getTributoISS().getTributoMulta() == null) {
            mapa.put(configuracaoTributario.getTributoISS(), mapa.get(configuracaoTributario.getTributoISS()).add(autoInfracaoFiscal.getMulta()));
        }
        // Comentado por solicitação da Suzane, não gera mais tributo de correção,
        // o valor da correção está junto com o valor do auto de infração.
        // (após validação pode remover)
//        if (autoInfracaoFiscal.getCorrecao().compareTo(BigDecimal.ZERO) > 0
//                && configuracaoTributario.getTributoISS().getTributoCorrecaoMonetaria() != null) {
//            if (mapa.containsKey(configuracaoTributario.getTributoISS().getTributoCorrecaoMonetaria())) {
//                mapa.put(configuracaoTributario.getTributoISS().getTributoCorrecaoMonetaria(), mapa.get(configuracaoTributario.getTributoISS().getTributoCorrecaoMonetaria()).add(autoInfracaoFiscal.getCorrecao()));
//            } else {
//                mapa.put(configuracaoTributario.getTributoISS().getTributoCorrecaoMonetaria(), autoInfracaoFiscal.getCorrecao());
//            }
//        } else if (autoInfracaoFiscal.getCorrecao().compareTo(BigDecimal.ZERO) > 0
//                && configuracaoTributario.getTributoISS().getTributoCorrecaoMonetaria() == null) {
//            mapa.put(configuracaoTributario.getTributoISS(), mapa.get(configuracaoTributario.getTributoISS()).add(autoInfracaoFiscal.getCorrecao()));
//        }
        for (Tributo t : mapa.keySet()) {
            geraItemCalculo(mapa.get(t), t, calculo);
        }
    }

    private void geraItemCalculo(BigDecimal valor, Tributo tributo, CalculoFiscalizacao calculo) {
        ItensCalculoFiscalizacao itensCalculoFiscalizacao = new ItensCalculoFiscalizacao();
        itensCalculoFiscalizacao.setCalculoFiscalizacao(calculo);
        itensCalculoFiscalizacao.setTributo(tributo);
        itensCalculoFiscalizacao.setValor(valor);
        calculo.getItensCalculoFiscalizacao().add(itensCalculoFiscalizacao);
    }

    public CalculoMultaFiscalizacao recuperarCalculoMultaDoAuto(AutoInfracaoFiscal autoInfracaoFiscal) {
        String hql = "select calc from CalculoMultaFiscalizacao calc where calc.autoInfracaoFiscal = :auto";
        Query q = em.createQuery(hql, CalculoMultaFiscalizacao.class).setParameter("auto", autoInfracaoFiscal);
        if (!q.getResultList().isEmpty()) {
            CalculoMultaFiscalizacao clc = (CalculoMultaFiscalizacao) q.getResultList().get(0);
            clc.getItemCalculoMultaFiscal().size();
            return clc;
        }
        return null;
    }

    public CalculoFiscalizacao recuperarCalculoDoAuto(AutoInfracaoFiscal autoInfracaoFiscal) {
        String hql = "select calc from CalculoFiscalizacao calc where calc.autoInfracaoFiscal = :auto";
        Query q = em.createQuery(hql, CalculoFiscalizacao.class).setParameter("auto", autoInfracaoFiscal);
        if (!q.getResultList().isEmpty()) {
            CalculoFiscalizacao clc = (CalculoFiscalizacao) q.getResultList().get(0);
            clc.getItensCalculoFiscalizacao().size();
            return clc;
        }
        return null;
    }

    public List<Long> buscarIdsCalculoFiscalizacaoPorAutoInfracao(AutoInfracaoFiscal autoInfracaoFiscal) {
        String sql = " " +
            " SELECT idCalculo " +
            " FROM ( " +
            "      SELECT cf.id AS idCalculo " +
            "       FROM CALCULOFISCALIZACAO cf " +
            "      WHERE cf.AUTOINFRACAOFISCAL_ID = :idAutoInflacao " +
            "  UNION ALL " +
            "      SELECT cmf.id AS idCalculo " +
            "       FROM CALCULOMULTAFISCALIZACAO cmf " +
            "      WHERE cmf.AUTOINFRACAOFISCAL_ID = :idAutoInflacao " +
            " )";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAutoInflacao", autoInfracaoFiscal.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public ProcessoCalculoMultaFiscal lancaCalculoMultaFiscalizacao(AutoInfracaoFiscal autoInfracaoFiscal) {
        AcaoFiscal acaoFiscal = acaoFiscalFacade.recuperarPorAutoInfracao(autoInfracaoFiscal);
        Exercicio exercicio = exercicioFacade.recuperarExercicioPeloAno(autoInfracaoFiscal.getAno());
        CadastroEconomico cadastroEconomico = acaoFiscal != null ? acaoFiscal.getCadastroEconomico() : autoInfracaoFiscal.getCadastroEconomico();
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();

        if (configuracaoTributario.getDividaMultaFiscalizacao() != null) {
            CalculoMultaFiscalizacao calculo = recuperarCalculoMultaDoAuto(autoInfracaoFiscal);
            RegistroLancamentoContabil reg = em.find(RegistroLancamentoContabil.class, autoInfracaoFiscal.getRegistro().getId());
            if (calculo == null) {
                calculo = new CalculoMultaFiscalizacao();
                ProcessoCalculoMultaFiscal processoCalculoMultaFiscal = new ProcessoCalculoMultaFiscal();
                processoCalculoMultaFiscal.setDataLancamento(new Date());
                processoCalculoMultaFiscal.setExercicio(exercicio);
                processoCalculoMultaFiscal.setDivida(configuracaoTributario.getDividaMultaFiscalizacao());

                calculo.setCadastro(cadastroEconomico);
                calculo.setProcessoCalculoMultaFiscal(processoCalculoMultaFiscal);
                calculo.setDataCalculo(new Date());
                calculo.setExercicio(exercicio);
                calculo.setIsento(false);
                calculo.setAutoInfracaoFiscal(autoInfracaoFiscal);
                calculo.setVencimento(autoInfracaoFiscal.getVencimento());
                CalculoPessoa p = new CalculoPessoa();
                p.setCalculo(calculo);
                p.setPessoa(cadastroEconomico.getPessoa());
                calculo.getPessoas().add(p);

                calculo = gerarItensCalculoMulta(calculo, reg);
                processoCalculoMultaFiscal.getCalculoMultaFiscalizacao().add(calculo);
                return em.merge(processoCalculoMultaFiscal);
            } else {
                calculo.setVencimento(autoInfracaoFiscal.getVencimento());
                calculo = gerarItensCalculoMulta(calculo, reg);
                calculo = em.merge(calculo);
                return calculo.getProcessoCalculoMultaFiscal();
            }

        }

        return null;
    }

    private CalculoMultaFiscalizacao gerarItensCalculoMulta(CalculoMultaFiscalizacao calculo, RegistroLancamentoContabil reg) {
        BigDecimal total = BigDecimal.ZERO;
        calculo.getItemCalculoMultaFiscal().clear();
        for (LancamentoMultaFiscal lancamentoMultaFiscal : reg.getMultas()) {
            ItemCalculoMultaFiscal item = new ItemCalculoMultaFiscal();
            item.setCalculoMultaFiscalizacao(calculo);
            if (lancamentoMultaFiscal.getMultaFiscalizacao() != null) {
                item.setTributo(lancamentoMultaFiscal.getMultaFiscalizacao().getTributo());
            }
            item.setValor(lancamentoMultaFiscal.getValorMulta());
            calculo.getItemCalculoMultaFiscal().add(item);
            total = total.add(item.getValor());
        }
        calculo.setValorEfetivo(total);
        calculo.setValorReal(total);
        return calculo;
    }

    public ValorDivida retornaValorDividaDoCalculo(Calculo calculo) {
        Query q = em.createQuery("from ValorDivida vd where vd.calculo = :calculo");
        q.setParameter("calculo", calculo);
        List<ValorDivida> resultado = q.getResultList();
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    public CalculoFiscalizacao retornaCalculoDoAutoInfracao(AutoInfracaoFiscal autoInfracaoFiscal) {
        Query q = em.createQuery("select c from CalculoFiscalizacao c where c.autoInfracaoFiscal = :auto");
        q.setParameter("auto", autoInfracaoFiscal);
        List<CalculoFiscalizacao> resultado = q.getResultList();
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    public BigDecimal calculaDescontos(BigDecimal valorTotal) {
        ParametroFiscalizacao param = parametroFiscalizacaoFacade.recuperarParametroFiscalizacao(sistemaFacade.getExercicioCorrente());
        BigDecimal totalPorcentagem = (param.getDescontoDeMulta() == null ? BigDecimal.ZERO : param.getDescontoDeMulta()).add((param.getDescontoMultaTrintaDias() == null ? BigDecimal.ZERO : param.getDescontoMultaTrintaDias()));
        return (valorTotal.multiply(totalPorcentagem)).divide(new BigDecimal(100));
    }

    private String adicionaMensagemDAM(String mensagem) {
        StringBuilder sb = new StringBuilder("");
        if (mensagem != null) {
            sb.append(mensagem);
        }
        return sb.toString();
    }

    private String adicionaValor(String texto, BigDecimal valor, String simbolo, boolean insereNoFinal) {
        StringBuilder sb = new StringBuilder(texto);
        while (sb.length() < TAMANHO_DESCRICAO_VALOR) {
            sb.append(" ");
        }
        //espaço para completar a coluna com 44 caracteres
        int tamanhoEspaco = 9;
        for (int i = 0; i < (tamanhoEspaco - simbolo.length()); i++) {
            sb.append(" ");
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        String valorString = df.format(valor);
        int espacos = 14 - valorString.length();
        if (espacos > 0) {
            for (int i = 0; i < espacos; i++) {
                valorString = " " + valorString;
            }
        }
        if (insereNoFinal) {
            sb.append(valorString).append(simbolo);
        } else {
            sb.append(simbolo).append(valorString);
        }
        sb.append(" ");
        return sb.toString();
    }

    private String adicionaLinha(String texto, int operacao) {
        String retorno;
        if (operacao == CONCATENA_LINHA) {
            retorno = texto.substring(0, TAMANHO_LINHA > texto.length() ? texto.length() : TAMANHO_LINHA);
        } else {
            int tamanhoLinhaAtual = 0;
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < texto.length(); i++) {
                if (i >= texto.length()) {
                    break;
                }
                sb.append(texto.charAt(i));
                tamanhoLinhaAtual++;
                if (tamanhoLinhaAtual >= TAMANHO_LINHA) {
                    sb.append("\n");
                    tamanhoLinhaAtual = 0;
                }
            }
            retorno = sb.toString();
        }
        return retorno;
    }

    private List<ItensCalculoFiscalizacao> recuperarItensCalculoFiscalizacao(CalculoFiscalizacao calculo) {
        String hql = "from ItensCalculoFiscalizacao i where i.calculoFiscalizacao = :calculo";
        Query q = em.createQuery(hql);
        q.setParameter("calculo", calculo);
        return q.getResultList();
    }

    private List<ItemCalculoMultaFiscal> recuperarItemCalculoMultaFiscal(CalculoMultaFiscalizacao calculo) {
        String hql = "from ItemCalculoMultaFiscal i where i.calculoMultaFiscalizacao = :calculo";
        Query q = em.createQuery(hql);
        q.setParameter("calculo", calculo);
        return q.getResultList();
    }

    private BigDecimal retornaValorTotalMultas(RegistroLancamentoContabil reg) {
        BigDecimal total = BigDecimal.ZERO;
        reg = em.find(RegistroLancamentoContabil.class, reg.getId());
        for (LancamentoMultaFiscal lancamentoMultaFiscal : reg.getMultas()) {
            total = total.add(lancamentoMultaFiscal.getValorMulta());
        }
        return total;
    }

    public CalculoMultaFiscalizacao retornaCalculoMultaDoAutoInfracao(AutoInfracaoFiscal auto) {
        Query q = em.createQuery("select c from CalculoMultaFiscalizacao c where c.autoInfracaoFiscal = :auto");
        q.setParameter("auto", auto);
        List<CalculoMultaFiscalizacao> resultado = q.getResultList();
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }

    }

    private String recuperaDescricaoMulta(AcaoFiscal acaoFiscal, ItemCalculoMultaFiscal item) {
        AcaoFiscal acao = acaoFiscalFacade.recuperar(acaoFiscal.getId());
        for (RegistroLancamentoContabil reg : acao.getLancamentosContabeis()) {
            for (LancamentoMultaFiscal lancamentoMultaFiscal : reg.getMultas()) {
                if (lancamentoMultaFiscal.getValorMulta().compareTo(item.getValor()) == 0) {
                    return lancamentoMultaFiscal.getMultaFiscalizacao().getArtigo();
                }
            }
        }
        return "";
    }
}
