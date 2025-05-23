/*
 * Codigo gerado automaticamente em Tue Mar 01 15:53:31 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.enums.Operacoes;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateful
public class AtributoFacade extends AbstractFacade<Atributo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<ClasseDoAtributo, List> atributosPorClasse;
    @EJB
    private ValorPossivelFacade valorPossivelFacade;

    public AtributoFacade() {
        super(Atributo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ValorPossivelFacade getValorPossivelFacade() {
        return valorPossivelFacade;
    }

    public List<Atributo> listaAtributosPorClasse(ClasseDoAtributo classeDoAtributo) {

        if (atributosPorClasse == null) {
            atributosPorClasse = new EnumMap<ClasseDoAtributo, List>(ClasseDoAtributo.class);
        }
        if (atributosPorClasse.containsKey(classeDoAtributo)) {
            return atributosPorClasse.get(classeDoAtributo);
        }
        Query q = em.createQuery("from Atributo atributo where atributo.ativo = 1 AND atributo.classeDoAtributo=:classe order by atributo.sequenciaapresentacao");

        q.setParameter("classe", classeDoAtributo);
        atributosPorClasse.put(classeDoAtributo, q.getResultList());
        return atributosPorClasse.get(classeDoAtributo);
    }

    public Map<Atributo, ValorAtributo> novoMapaAtributoValorAtributo(ClasseDoAtributo classeDoAtributo) {
        Map<Atributo, ValorAtributo> atributos = new HashMap<Atributo, ValorAtributo>();
        try {
            for (Atributo at : listaAtributosPorClasse(classeDoAtributo)) {
                ValorAtributo vatri = new ValorAtributo();
                vatri.setAtributo(at);
                atributos.put(at, vatri);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return atributos;
    }

    public void inicializarCaracteristicasDoBCI(CadastroImobiliario bci) {
        try {
            if (bci.getCaracteristicasBci().isEmpty()) {
                for (Atributo at : listaAtributosPorClasse(ClasseDoAtributo.CADASTRO_IMOBILIARIO)) {
                    CaracteristicasBci caracteristica = new CaracteristicasBci();
                    caracteristica.setCadastroImobiliario(bci);
                    caracteristica.setAtributo(at);
                    bci.getCaracteristicasBci().add(caracteristica);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void inicializarCaracteristicasDoLote(Lote lote) {
        try {
            for (Atributo at : listaAtributosPorClasse(ClasseDoAtributo.LOTE)) {
                CaracteristicasLote caracteristica = new CaracteristicasLote();
                caracteristica.setLote(lote);
                caracteristica.setAtributo(at);
                lote.getCaracteristicasLote().add(caracteristica);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void inicializarCaracteristicasDaConstrucao(Construcao construcao) {
        try {
            for (Atributo at : listaAtributosPorClasse(ClasseDoAtributo.CONSTRUCAO)) {
                CaracteristicasConstrucao caracteristica = new CaracteristicasConstrucao();
                caracteristica.setConstrucao(construcao);
                caracteristica.setAtributo(at);
                construcao.getCaracteristicasConstrucao().add(caracteristica);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ConstrucaoAlvara inicializarCaracteristicasDaConstrucaoDoAlvara(ConstrucaoAlvara construcao) {
        try {
            for (Atributo at : listaAtributosPorClasse(ClasseDoAtributo.CONSTRUCAO)) {
                CaracteristicasAlvaraConstrucao caracteristica = new CaracteristicasAlvaraConstrucao();
                caracteristica.setConstrucaoAlvara(construcao);
                caracteristica.setAtributo(at);
                construcao.getCaracteristicas().add(caracteristica);
            }
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
        return construcao;
    }

    public void completaAtributos(Map<Atributo, ValorAtributo> atributos, ClasseDoAtributo classeDoAtributo) {
        if (atributos == null) {
            atributos = new HashMap<Atributo, ValorAtributo>();
        }
        for (Atributo at : listaAtributosPorClasse(classeDoAtributo)) {
            if (!atributos.containsKey(at)) {
                ValorAtributo vatri = new ValorAtributo();
                vatri.setAtributo(at);
                atributos.put(at, vatri);
            }
        }
    }

    @Override
    public Atributo recuperar(Object id) {
        Atributo pf = em.find(Atributo.class, id);
        pf.getValoresPossiveis().size();
        return pf;
    }

    @Override
    public void salvarNovo(Atributo entity) {
        entity.setDataRegistro(SistemaFacade.getDataCorrente());
        super.salvarNovo(entity);
    }

    public List<Atributo> listaAtributosPorClasseTabela(ClasseDoAtributo classeDoAtributo) {

        if (atributosPorClasse == null) {
            atributosPorClasse = new EnumMap<ClasseDoAtributo, List>(ClasseDoAtributo.class);
        }
        if (atributosPorClasse.containsKey(classeDoAtributo)) {
            return atributosPorClasse.get(classeDoAtributo);
        }
        Query q = em.createQuery("from Atributo atributo where atributo.ativo = 1 AND atributo.classeDoAtributo=:classe AND atributo.componenteVisual = 'COMBO' or atributo.componenteVisual = 'RADIO' order by atributo.sequenciaapresentacao");

        q.setParameter("classe", classeDoAtributo);
        atributosPorClasse.put(classeDoAtributo, q.getResultList());
        return atributosPorClasse.get(classeDoAtributo);
    }

    public boolean concultaIdentificacao(Atributo atributo, Operacoes operacao) {
        String hql;
        if (operacao == Operacoes.EDITAR) {
            hql = "from Atributo a where a.identificacao = :identificacao and a != :atributo";

        } else {
            hql = "from Atributo a where a.identificacao = :identificacao";
        }
        Query q = em.createQuery(hql);
        q.setParameter("identificacao", atributo.getIdentificacao());
        if (operacao == Operacoes.EDITAR) {
            q.setParameter("atributo", atributo);
        }
        if (q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Atributo recuperaAtributoComIdentificacao(String identificacao) {
        Query q = em.createQuery("from Atributo a where coalesce(a.ativo, 1) = 1 and "
                + "lower(a.identificacao) = :identificacao");
        q.setParameter("identificacao", identificacao.trim().toLowerCase());
        List<Atributo> retorno = q.getResultList();
        if (retorno.isEmpty()) {
            return null;
        } else {
            retorno.get(0).getValoresPossiveis().size();
            return retorno.get(0);
        }
    }

    public List<Atributo> buscarAtributoPorClasse(ClasseDoAtributo classeDoAtributo, String parte) {
        return em.createQuery(" select a from Atributo a " +
                        " where a.classeDoAtributo = :classeDoAtributo" +
                        "  and coalesce(a.ativo, 0) = 1 " +
                        "  and lower(trim(a.nome)) like :parte ")
                .setParameter("classeDoAtributo", classeDoAtributo)
                .setParameter("parte", "%" + parte.trim().toLowerCase() + "%")
                .getResultList();
    }
}
