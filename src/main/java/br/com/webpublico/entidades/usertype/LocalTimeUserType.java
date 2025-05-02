package br.com.webpublico.entidades.usertype;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.joda.time.LocalTime;

import java.sql.*;

public class LocalTimeUserType extends ImmutableUserType {

    private static final long serialVersionUID = 1L;

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.TIME};
    }

    @Override
    public Class<?> returnedClass() {
        return LocalTime.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException, SQLException {
        Time value = rs.getTime(names[0]);
        if (rs.wasNull()) {
            return null;
        } else {
            return new LocalTime(value);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.TIME);
        } else {
            LocalTime localTime = (LocalTime) value;
            st.setTime(index, new Time(localTime.toDateTimeToday().getMillis()));
        }
    }

}
