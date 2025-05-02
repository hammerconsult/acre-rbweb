/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ConsultaPagamento;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.ExercicioFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Renato
 */
@Stateless
public class RelatorioMaioresContribuintesFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private DividaFacade dividaFacade;

    protected EntityManager getEntityManager() {
        return em;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public List<ConsultaPagamento> listaParcelaPagamentoPorFiltroETipoDeCadastro(String exercicioInicial, String exercicioFinal, Date dataPagamentoInicial, Date dataPagamentoFinal, String dividas, Boolean dividaAtiva, Boolean dividaAtivaAjuizada, Boolean doExercicio, Integer quantidadeContribuinte, ClassificacaoAtividade classificacaoAtividade) {
        StringBuilder hql = new StringBuilder("select new br.com.webpublico.entidadesauxiliares.ConsultaPagamento ")
                .append(" (pessoa.pessoa,")
                .append(" cadastro, ")
                .append(" exercicio.ano, ")
                .append(" divida, ")
                .append(" situacao.situacaoParcela, ")
                .append(" valorDivida.emissao, ")
                .append(" situacao.saldo, ")
                .append(" pv, item)   ")
                .append(" from ItemLoteBaixa item ")
                .append(" join item.loteBaixa lote ")
                .append(" join item.itemParcelas itemLoteParcela ")
                .append(" join itemLoteParcela.itemDam itemDam")
                .append(" join itemDam.parcela pv ")
                .append(" join pv.valorDivida valorDivida ")
                .append(" join valorDivida.exercicio exercicio ")
                .append(" join valorDivida.calculo calculo ")
                .append(" left join calculo.cadastro cadastro ")
                .append(" join pv.situacoes situacao ")
                .append(" join valorDivida.divida divida ")
                .append(" join valorDivida.calculo.pessoas pessoa ")
                .append(" where (lote.situacaoLoteBaixa = '").append(SituacaoLoteBaixa.BAIXADO.name())
                       .append("' or lote.situacaoLoteBaixa = '").append(SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name()).append("') ")
                .append(" and (situacao.situacaoParcela = '").append(SituacaoParcela.PAGO.name()).append("') ")
                .append(" and (situacao.id = (select max(s.id) from SituacaoParcelaValorDivida s where s.parcela = pv)) ");
        Boolean condicaoDA = false;
        if (doExercicio || dividaAtiva || dividaAtivaAjuizada) {
            hql.append(" and ( ");
        }
        if (doExercicio) {
            hql.append(" ((coalesce(pv.dividaAtiva, 0) = 0) and (coalesce(pv.dividaAtivaAjuizada ,0) = 0)) ");
            condicaoDA = true;
        }
        if (dividaAtiva) {
            if (condicaoDA) {
                hql.append(" or ");
            }
            hql.append(" (coalesce(pv.dividaAtiva, 0) = 1) ");
            condicaoDA = true;
        }
        if (dividaAtivaAjuizada) {
            if (condicaoDA) {
                hql.append(" or ");
            }
            hql.append(" (coalesce(pv.dividaAtivaAjuizada, 0) = 1) ");
        }
        if (doExercicio || dividaAtiva || dividaAtivaAjuizada) {
            hql.append(" ) ");
        }
        if (exercicioInicial != null && exercicioInicial.trim().length() > 0) {
            hql.append(" and exercicio.ano >= :exercicioInicial");
        }
        if (exercicioFinal != null && exercicioFinal.trim().length() > 0) {
            hql.append(" and exercicio.ano <= :exercicioFinal");
        }
        if (dataPagamentoInicial != null) {
            hql.append(" and (lote.dataPagamento >= :dataPagamentoInicial) ");
        }
        if (dataPagamentoFinal != null) {
            hql.append(" and (lote.dataPagamento <= :dataPagamentoFinal) ");
        }
        if (dividas != null && !dividas.isEmpty()) {
            hql.append(" and (valorDivida.divida.id in (").append(dividas).append(")) ");
        }
        if (classificacaoAtividade != null) {
            hql.append(" and exists (select 1 from CadastroEconomico bce where bce = cadastro and classificacaoAtividade = '")
                    .append(classificacaoAtividade.name()).append("')  ");
        }
        hql.append(" order by pessoa.pessoa.id ");

        Query q = em.createQuery(hql.toString());
        if (exercicioInicial != null && exercicioInicial.trim().length() > 0) {
            q.setParameter("exercicioInicial", Integer.parseInt(exercicioInicial));
        }
        if (exercicioFinal != null && exercicioFinal.trim().length() > 0) {
            q.setParameter("exercicioFinal", Integer.parseInt(exercicioFinal));
        }
        if (dataPagamentoInicial != null) {
            q.setParameter("dataPagamentoInicial", dataPagamentoInicial);
        }
        if (dataPagamentoFinal != null) {
            q.setParameter("dataPagamentoFinal", dataPagamentoFinal);
        }

        List<ConsultaPagamento> retorno = q.getResultList();
        List<ConsultaPagamento> retornoOrdenado = new ArrayList<>();
        //System.out.println("hql " + hql);
        if (!retorno.isEmpty()) {
            List<IdValor> listaIdsContribuintes = new ArrayList<>();


            for (ConsultaPagamento consulta : retorno) {
                boolean achouItem = false;
                for (IdValor item : listaIdsContribuintes) {
                    if (item.getId() == consulta.getPessoa().getId()) {
                        item.setValor(item.getValor().add(consulta.getValorPago()));
                        achouItem = true;
                    }
                }
                if (!achouItem) {
                    IdValor novoItem = null;
                    novoItem = new IdValor();
                    novoItem.setId(consulta.getPessoa().getId());
                    novoItem.setValor(consulta.getValorPago());
                    listaIdsContribuintes.add(novoItem);
                }
            }

            Collections.sort(listaIdsContribuintes, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    IdValor id1 = (IdValor) o1;
                    IdValor id2 = (IdValor) o2;
                    return id1.valor.compareTo(id2.valor) < 0 ? +1 : (id1.valor.compareTo(id2.valor) > 0 ? -1 : 0);
                }
            });

            for (IdValor idValor : listaIdsContribuintes) {
                for (ConsultaPagamento consulta : retorno) {
                    if (consulta.getPessoa().getId() == idValor.getId()) {
                        retornoOrdenado.add(consulta);
                    }
                }
            }
        }
        if (!retornoOrdenado.isEmpty()) {
            return retornoOrdenado;
        } else {
            return retorno;
        }
    }

    public List<Exercicio> completaExercicio(String parte) {
        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
    }

    public List<Divida> completaDivida(String parte) {
        return dividaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    class IdValor {

        private Long id;
        private BigDecimal valor;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }
}
