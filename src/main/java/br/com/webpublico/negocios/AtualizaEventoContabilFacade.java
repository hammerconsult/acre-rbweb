package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.util.Persistencia;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/05/14
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AtualizaEventoContabilFacade {

    protected static final Logger logger = LoggerFactory.getLogger(AtualizaEventoContabilFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public void limparEM() {
        em.clear();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void salvarObjetoReprocessamento(Object o) {
        try {
            if (Persistencia.getId(o) == null) {
                em.persist(o);
            } else {
                o = em.merge(o);
            }
        } catch (Exception e) {

        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @NotFound(action = NotFoundAction.IGNORE)
    public Object salvarContaAuxiliar(Object o) {
        try {
            return em.merge(o);
        } catch (Exception e) {
            return null;
        }
    }

    public void processar(Object objeto) {
        String virgula = ", ";
        String sql = "";
        try {
            String tabela = getNomeTabela(objeto);

            sql = "update " + tabela + " set ";
            List<ObjetoParametro> atributos = getAtributos(objeto, EventoContabil.class);
            if (!atributos.isEmpty()) {

                for (ObjetoParametro atributo : atributos) {
                    sql += " " + atributo.getNomeAtributo() + "_id = :" + atributo.getNomeParametro();
                    sql += virgula;
                }

                sql = sql.substring(0, (sql.length()) - virgula.length());
                sql += " where id = :id";

                Query consulta = em.createNativeQuery(sql);
                for (ObjetoParametro atributo : atributos) {
                    consulta.setParameter(atributo.getNomeParametro(), Persistencia.getId(atributo.getValor()));
                }
                consulta.setParameter("id", Persistencia.getId(objeto));
                consulta.executeUpdate();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ExcecaoNegocioGenerica("AtualizaEventoContabilFacade erro..: " + sql);
        }
    }

    private String getNomeTabela(Object objeto) {
        if (objeto.getClass().isAnnotationPresent(Table.class)) {
            Table annotation = objeto.getClass().getAnnotation(Table.class);
            return annotation.name();
        }
        if (objeto.getClass().isAnnotationPresent(Entity.class)) {
            Entity annotation = objeto.getClass().getAnnotation(Entity.class);
            if (!annotation.name().trim().isEmpty()) {
                return annotation.name();
            } else {
                return objeto.getClass().getSimpleName();
            }

        }
        return objeto.getClass().getSimpleName();
    }

    private List<ObjetoParametro> getAtributos(Object objeto, Class classeAtributo) {
        List<ObjetoParametro> retorno = new ArrayList<>();
        int contador = 1;
        for (Field field : objeto.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().equals(classeAtributo)) {
                try {
                    if (Persistencia.getId(field.get(objeto)) != null) {
                        retorno.add(new ObjetoParametro(field.getName(), field.getName() + contador, field.get(objeto)));
                        contador++;
                    }
                } catch (Exception e) {

                }
            }
        }
        return retorno;
    }

    public class ObjetoParametro {
        private String nomeAtributo;
        private String nomeParametro;
        private Object valor;

        public ObjetoParametro(String nomeAtributo, String nomeParametro, Object valor) {
            this.nomeAtributo = nomeAtributo;
            this.nomeParametro = nomeParametro;
            this.valor = valor;
        }

        public String getNomeAtributo() {
            return nomeAtributo;
        }

        public void setNomeAtributo(String nomeAtributo) {
            this.nomeAtributo = nomeAtributo;
        }

        public String getNomeParametro() {
            return nomeParametro;
        }

        public void setNomeParametro(String nomeParametro) {
            this.nomeParametro = nomeParametro;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }
    }
}
