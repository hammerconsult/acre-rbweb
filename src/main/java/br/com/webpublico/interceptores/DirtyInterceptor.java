package br.com.webpublico.interceptores;

import br.com.webpublico.entidades.SuperEntidadeDetectaAlteracao;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;

public class DirtyInterceptor extends EmptyInterceptor {

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (entity instanceof SuperEntidadeDetectaAlteracao) {
            ((SuperEntidadeDetectaAlteracao) entity).setModificado(true);
        }
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
}
