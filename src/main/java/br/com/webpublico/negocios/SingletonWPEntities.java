package br.com.webpublico.negocios;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 02/09/13
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public class SingletonWPEntities {

    protected static final Logger logger = LoggerFactory.getLogger(SingletonWPEntities.class);
    private static SingletonWPEntities INSTANCE;
    private final Map<Class, ClassInfo> classes;

    private SingletonWPEntities(Set<EntityType<?>> entities) {
        this.classes = new HashMap<>();
        preencherListaDeClasses(entities);
    }

    public static SingletonWPEntities getINSTANCE(Set<EntityType<?>> entities) {
        if (INSTANCE == null) {
            INSTANCE = new SingletonWPEntities(entities);
        }

        return INSTANCE;
    }

    private void preencherListaDeClasses(Set<EntityType<?>> entities) {
        if (classes.isEmpty()) {
            for (EntityType<?> et : entities) {
                try {
                    if (et.getJavaType() != null) {
                        classes.put(et.getJavaType(), new ClassInfo(et.getJavaType()));
                    }
                } catch (Exception e) {
                    logger.error("Erro ao preencherListaDeClasses", e);
                }
            }
        }
    }

    public ClassInfo getByClass(Class classe) throws ClassNotFoundException {
        ClassInfo classInfo = classes.get(classe);

        if (classInfo == null) {
            throw new ClassNotFoundException("A classe " + classe.getName() + " não foi encontrada.");
        }

        return classInfo;
    }

    public ClassInfo getByClassName(String className) throws ClassNotFoundException {
        try {
            return getByClass(Class.forName(className));
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("A classe " + className + " não foi encontrada.");
        }
    }

    public ClassInfo getByTableName(String tableName) throws ClassNotFoundException {
        for (Map.Entry<Class, ClassInfo> entry : classes.entrySet()) {
            if (entry.getValue().tableName.equalsIgnoreCase(tableName)) {
                return entry.getValue();
            }
        }

        throw new ClassNotFoundException("A classe referente a tabela " + tableName + " não foi encontrada.");
    }

    public class ClassInfo {
        public Class classe;
        public String etiquetaValue;
        public String tableName;

        public ClassInfo(Class classe) {
            this.classe = classe;
            this.etiquetaValue = getEtiquetaValue();
            this.tableName = getTableName();

//            //System.out.println("##########################################################################################");
//            //System.out.println("####################### CRIANDO CLASS INFO " + classe.getName() + "#######################");
//            //System.out.println("####################### ETIQUETA VALUE     " + etiquetaValue + "##########################");
//            //System.out.println("####################### TABLE NAME     " + tableName + "##########################");
//            //System.out.println("##########################################################################################");
        }

        private String getEtiquetaValue() {
            Etiqueta etiqueta = (Etiqueta) classe.getAnnotation(Etiqueta.class);

            if (etiqueta != null) {
                return etiqueta.value();
            }

            return classe.getSimpleName();
        }

        private String getTableName() {
            Table table = (Table) classe.getAnnotation(Table.class);

            if (table != null) {
                return table.name();
            }

            return classe.getSimpleName();
        }
    }
}
