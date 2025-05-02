/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.ResultadoValidacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.List;

@Stateless
public class ParametrosTransitoRBTransFacade extends AbstractFacade<ParametrosTransitoRBTrans> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametrosTransitoRBTransFacade() {
        super(ParametrosTransitoRBTrans.class);
    }

    // Tornar esse método genérico também
    private boolean quantidadeMaiorDePermissoesJaExistente(Integer limitePermissoes) {
        if (recuperarNumeroDePermissoesTaxiAtivas() >= limitePermissoes) {
            return true;
        }

        return false;
    }

    // Tornar esse método genérico
    private Integer recuperarNumeroDePermissoesTaxiAtivas() {
        String hql = "select count(*) from PermissaoTaxi ptt "
            + " where ptt.finalVigencia = null";
        Query q = em.createQuery(hql);
        try {
            return (Integer) q.getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    public ResultadoValidacao salvarParametro(ParametrosTransitoRBTrans parametrosTransitoSelecionado) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        if (!validouCamposParametro(parametrosTransitoSelecionado, resultado)) {
            return resultado;
        }
        if (parametrosTransitoSelecionado.getId() == null) {
            em.persist(parametrosTransitoSelecionado);
            resultado.addInfo(null, "Parâmetro salvo com sucesso!", "");
        } else {
            em.merge(parametrosTransitoSelecionado);
            resultado.addInfo(null, "Parâmetro alterado com sucesso!", "");
        }
        return resultado;
    }

    private boolean validouCamposParametro(ParametrosTransitoRBTrans parametros, ResultadoValidacao resultado) {
        return !(parametros.isParametroFiscalizacaoRBTrans() && !validouCamposParametroFiscalizacaoRBTrans(parametros, resultado));
    }

    public boolean naoExistemCamposNulosNoParametro(ParametrosTransitoRBTrans parametros, ResultadoValidacao resultado, Class classe, String mensagem) {
        for (Field field : classe.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Etiqueta annotation = field.getAnnotation(Etiqueta.class);
                String campo = annotation.value();

                if (field.get(parametros) == null) {
                    resultado.addErro(null, mensagem, "Não foi informado o campo " + campo + ".");
                } else if (field.get(parametros) instanceof String) {
                    if (((String) field.get(parametros)).isEmpty()) {
                        resultado.addErro(null, mensagem, "Não foi informado o campo " + campo + ".");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return resultado.isValidado();
    }

    private boolean validouCamposParametroFiscalizacaoRBTrans(ParametrosTransitoRBTrans parametros, ResultadoValidacao resultado) {
        boolean validou = true;

        for (Field f : Persistencia.getAtributos(parametros.getClass())) {
            if (f.getAnnotation(Obrigatorio.class) == null) {
                continue;
            }

            String anotacao = "";
            if (f.getAnnotation(Etiqueta.class) != null) {
                anotacao = f.getAnnotation(Etiqueta.class).value();
            } else {
                anotacao = f.getName();
            }

            if (Persistencia.getAttributeValue(parametros, f.getName()) == null) {
                resultado.addWarn(null, anotacao + " inválido!", "O campo " + anotacao + " é obrigatório.", false);
                validou = false;
            } else {
                if (f.getType().equals(Integer.class)) {
                    Integer valor = (Integer) Persistencia.getAttributeValue(parametros, f.getName());
                    if (valor.intValue() <= 0) {
                        resultado.addWarn(null, anotacao + " inválido!", "O campo " + anotacao + " não pode ser menor ou igual a zero.", false);
                        validou = false;
                    }
                }
            }
        }
        return validou;
    }

    public List<Object[]> recuperarHistoricoAlteracoes(Class classe) throws IllegalArgumentException, IllegalAccessException {
        AuditReader reader = AuditReaderFactory.get(em);

        List<Object[]> lista = reader.createQuery().forRevisionsOfEntity(classe, false, true).addOrder(AuditEntity.revisionNumber().desc()).getResultList();
        for (Object[] object : lista) {


            for (Field field : Persistencia.getAtributos(object[0].getClass())) {
                field.setAccessible(true);
                if (possuiRelacionamento(field)) {
                    try {
                        Persistencia.getAttributeValue(object[0], field.getName()).toString();
                        field.set(object[0], initializeAndUnproxy(Persistencia.getAttributeValue(object[0], field.getName())));
                        //System.out.println("Campo...: " + field.getName());
                        //System.out.println("Valor...: " + field.get(object[0]));
                    } catch (Exception e) {
                        field.set(object[0], null);
                    }

                }
            }
        }
        return lista;
    }

    public List<Object[]> recuperarRevisaoClasse(Class classe, RevisaoAuditoria rev) {
        AuditReader reader = AuditReaderFactory.get(em);

        List<Object[]> lista = reader.createQuery().forRevisionsOfEntity(classe, false, true).add(AuditEntity.revisionNumber().eq(rev.getId())).getResultList();

        return lista;
    }

    public Object recuperarRevisaoAnterior(Object obj, int revisao) {
        AuditReader reader = AuditReaderFactory.get(em);

        Number numeroRevAnterior = (Number) reader.createQuery().forRevisionsOfEntity(obj.getClass(), false, true).addProjection(AuditEntity.revisionNumber().max()).add(AuditEntity.id().eq(Persistencia.getId(obj))).add(AuditEntity.revisionNumber().lt(revisao)).getSingleResult();

        if (numeroRevAnterior != null) {
            return reader.find(obj.getClass(), Persistencia.getId(obj), numeroRevAnterior);
        } else {
            return null;
        }
    }

    public boolean possuiRelacionamento(Field campo) {
        return campo.isAnnotationPresent(ManyToMany.class)
            || campo.isAnnotationPresent(ManyToAny.class)
            || campo.isAnnotationPresent(ManyToOne.class)
            || campo.isAnnotationPresent(OneToOne.class)
            || campo.isAnnotationPresent(OneToMany.class);
    }

    public ParametrosOutorgaRBTrans getParametroOutorgaRBTransVigente() {
        String hql = "from ParametrosOutorgaRBTrans";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);

        try {
            return (ParametrosOutorgaRBTrans) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public ParametrosFiscalizacaoRBTrans getParametrosFiscalizacaoRBTransVigente() {
        Query q = em.createQuery(" from ParametrosFiscalizacaoRBTrans ");
        q.setMaxResults(1);
        try {
            return (ParametrosFiscalizacaoRBTrans) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}

