/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
@Stateless
public class PontosFacade extends AbstractFacade<Pontuacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PontosFacade() {
        super(Pontuacao.class);
    }

    @Override
    public Pontuacao recuperar(Object id) {
        Pontuacao p = em.find(Pontuacao.class, id);
        p.getItens().size();
        p.getAtributos().size();
        return p;
    }

    public void novaPontuacao(Pontuacao novaPontuacao) throws ExcecaoNegocioGenerica {
        em.persist(novaPontuacao);
        if (novaPontuacao.getAtributos().size() == 1) {
            Atributo atributoN1 = novaPontuacao.getAtributos().get(0);
            for (ValorPossivel vp : atributoN1.getValoresPossiveis()) {
                ItemPontuacao ip = new ItemPontuacao();
                ip.setPontuacao(novaPontuacao);
                ip.setPontos(BigDecimal.ONE);
                ip.setValores(new ArrayList<ValorPossivel>());
                ip.getValores().add(vp);
                em.persist(ip);
            }
        } else if (novaPontuacao.getAtributos().size() == 2) {
            Atributo atributoN1 = novaPontuacao.getAtributos().get(0);
            Atributo atributoN2 = novaPontuacao.getAtributos().get(1);
            for (ValorPossivel vp1 : atributoN1.getValoresPossiveis()) {
                for (ValorPossivel vp2 : atributoN2.getValoresPossiveis()) {
                    ItemPontuacao ip = new ItemPontuacao();
                    ip.setPontuacao(novaPontuacao);
                    ip.setPontos(BigDecimal.ONE);
                    ip.setValores(new ArrayList<ValorPossivel>());
                    ip.getValores().add(vp1);
                    ip.getValores().add(vp2);
                    em.persist(ip);
                }
            }
        } else if (novaPontuacao.getAtributos().size() == 3) {
            Atributo atributoN1 = novaPontuacao.getAtributos().get(0);
            Atributo atributoN2 = novaPontuacao.getAtributos().get(1);
            Atributo atributoN3 = novaPontuacao.getAtributos().get(2);
            for (ValorPossivel vp1 : atributoN1.getValoresPossiveis()) {
                for (ValorPossivel vp2 : atributoN2.getValoresPossiveis()) {
                    for (ValorPossivel vp3 : atributoN3.getValoresPossiveis()) {
                        ItemPontuacao ip = new ItemPontuacao();
                        ip.setPontuacao(novaPontuacao);
                        ip.setPontos(BigDecimal.ONE);
                        ip.setValores(new ArrayList<ValorPossivel>());
                        ip.getValores().add(vp1);
                        ip.getValores().add(vp2);
                        ip.getValores().add(vp3);
                        em.persist(ip);
                    }
                }
            }
        } else {
            throw new ExcecaoNegocioGenerica("A pontuação dever conter um, dois ou três níveis");
        }
    }

    public List<Pontuacao> lista() {
        Query q = em.createQuery("from Pontuacao p");
        List<Pontuacao> lista = q.getResultList();
        for (Pontuacao p : lista) {
            p.getAtributos().size();
        }
        return lista;
    }

    public List<ItemPontuacao> listaItensPontuacao(Pontuacao pontuacao) {
        Query q = em.createQuery("from ItemPontuacao ip where ip.pontuacao=:pontuacao");
        q.setParameter("pontuacao", pontuacao);
        List<ItemPontuacao> lista = q.getResultList();
        for (ItemPontuacao ip : lista) {
            ip.getValores().size();
        }
        return lista;
    }

    public List<Atributo> quantidadeAtributos(Pontuacao pontuacao) {
        String sql = "select atr.* from atributo atr inner join pontuacao_atributo ptt on ptt.atributos_id = atr.id where ptt.pontuacao_id = :pont";
        Query q = em.createNativeQuery(sql, Atributo.class);
        q.setParameter("pont", pontuacao.getId());
        return q.getResultList();
    }

    public ItemPontuacao recuperaItemPontuacao(Pontuacao pontuacao, ValorPossivel vp1, ValorPossivel vp2) {
        Query q = em.createQuery("from ItemPontuacao ip where ip.pontuacao=:pontuacao and :vp1 in elements(ip.valores) and :vp2 in elements(ip.valores)");
        q.setParameter("pontuacao", pontuacao);
        q.setParameter("vp1", vp1);
        q.setParameter("vp2", vp2);
        ItemPontuacao p = (ItemPontuacao) q.getSingleResult();
        p.getValores().size();
        return p;
    }

    public boolean consultaIdentificacao(Pontuacao p) {
        String hql = "from Pontuacao a where a.identificacao = :identificacao and a.exercicio = :exercicio";
        if (p.getId() != null) {
            hql += " and a != :p";
        }
        Query q = em.createQuery(hql);
        q.setParameter("identificacao", p.getIdentificacao());
        q.setParameter("exercicio", p.getExercicio());
        if (p.getId() != null) {
            q.setParameter("p", p);
        }
        return q.getResultList().isEmpty();
    }

    public List<Pontuacao> listaPontuacoesPorExercicio(Exercicio exercicio) {
        String hql = "select a from Pontuacao a where a.exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        return q.getResultList();
    }

    public List<ValorPossivel> listaValoresPossiveisPorItem(ItemPontuacao itemPontuacao) {
        String hql = "select vp from ItemPontuacao item join item.valores vp where item = :item";
        Query q = em.createQuery(hql);
        q.setParameter("item", itemPontuacao);
        return q.getResultList();
    }

    public void duplicar(Pontuacao pontuacao, String identificacao, Exercicio exercicio) {
        Pontuacao duplicado = new Pontuacao();
        duplicado.setExercicio(exercicio);
        duplicado.setIdentificacao(identificacao);
        duplicado.setTipoPontoIPTU(pontuacao.getTipoPontoIPTU());

        List<ItemPontuacao> itensDuplicar = new ArrayList<>();
        List<ItemPontuacao> listaItemPontuacao = listaItensPontuacao(pontuacao);
        for (ItemPontuacao ip : listaItemPontuacao) {
            ItemPontuacao novoItem = new ItemPontuacao();
            novoItem.setPontos(ip.getPontos());
            novoItem.setValores(ip.getValores());
            novoItem.setPontuacao(duplicado);
            itensDuplicar.add(novoItem);
        }

        List<Atributo> atributos = quantidadeAtributos(pontuacao);
        for (Atributo att : atributos) {
            duplicado.getAtributos().add(att);
        }

        duplicado.setItens(itensDuplicar);
        salvar(duplicado);
    }
}
