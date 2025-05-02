package br.com.webpublico.entidades.usertype;

import br.com.webpublico.enums.DiaSemana;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.joda.time.DateTimeFieldType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class DiaDaSemanaUserType extends ImmutableUserType {

    private static final long serialVersionUID = 1L;

    public static final BiMap<DiaSemana, Integer> values;

    static {
        Builder<DiaSemana, Integer> builder = ImmutableBiMap.builder();
        for (DiaSemana diaDaSemana : DiaSemana.values()) {
            builder.put(diaDaSemana, diaDaSemana.getDayOfWeek().get(DateTimeFieldType.dayOfWeek()));
        }
        values = builder.build();
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.SMALLINT};
    }

    @Override
    public Class<?> returnedClass() {
        return DiaSemana.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException, SQLException {
        int value = rs.getInt(names[0]);
        if (rs.wasNull()) {
            return null;
        } else {
            DiaSemana diaDaSemana = values.inverse().get(value);
            if (diaDaSemana != null) {
                return diaDaSemana;
            } else {
                throw new IllegalArgumentException(String.format("DiaDaSemana não encontrado para código: '%d'", value));
            }
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.SMALLINT);
        } else {
            DiaSemana diaDaSemana = (DiaSemana) value;
            st.setInt(index, values.get(diaDaSemana));
        }
    }

}
