package br.com.webpublico.entidades.usertype;

import br.com.webpublico.enums.Direito;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.EnumSet;
import java.util.Set;

public class DireitosUserType extends ImmutableUserType {

    private static final long serialVersionUID = 1L;

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.SMALLINT};
    }

    @Override
    public Class<?> returnedClass() {
        return EnumSet.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException, SQLException {
        int value = rs.getInt(names[0]);
        if (rs.wasNull()) {
            return ImmutableSet.of();
        } else {
            Builder<Direito> builder = ImmutableSet.builder();
            for (Direito direito : Direito.values()) {
                if ((direito.getCodigo() & value) == direito.getCodigo()) {
                    builder.add(direito);
                }
            }
            return builder.build();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.SMALLINT);
        } else {
            int soma = 0;
            Set<Direito> direitos = (Set<Direito>) value;
            for (Direito direito : direitos) {
                soma += direito.getCodigo();
            }
            st.setInt(index, soma);
        }
    }

}
