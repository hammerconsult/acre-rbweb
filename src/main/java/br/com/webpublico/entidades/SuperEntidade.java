package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidadeSemReflexao;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 29/08/13
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public abstract class SuperEntidade extends SuperEntidadeSemID implements EntidadeWebPublico, Cloneable {

    @Override
    public int hashCode() {
        return IdentidadeDaEntidadeSemReflexao.calcularHashCode(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidadeSemReflexao.calcularEquals(this, object);
    }

    public static List<Long> getIds(List<? extends SuperEntidade> registros) {
        List<Long> ids = Lists.newArrayList();
        if (registros != null && !registros.isEmpty()) {
            for (SuperEntidade registro : registros) {
                ids.add(registro.getId());
            }
        }
        return ids;
    }

    @Override
    public SuperEntidade clone() throws CloneNotSupportedException {
        SuperEntidade clone = (SuperEntidade) super.clone();
        ((EntidadeWebPublico) clone).setId(null);
        return clone;
    }
}
