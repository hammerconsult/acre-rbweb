package br.com.webpublico.nfse.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.Query;
import java.util.Iterator;


public class PesquisaGenericaNfseUtil {

    private static final Logger logger = LoggerFactory.getLogger(PesquisaGenericaNfseUtil.class);

    public static String montarSelect(String selectFields) {
        return " ";
    }

    public static String montarFrom(String table) {
        return " from " + table;
    }

    public static String montarWherePorRegistro(){
        return " where id = :id";
    }

    public static String montarWhere(String searchFields, String query) {
        if (StringUtils.isBlank(searchFields) || StringUtils.isBlank(query)) {
            return "";
        }

        String where = " where (";
        String[] fields = searchFields.split(",");
        for (String field : fields) {
            where += " to_char(lower(" + field + ")) like :busca or";
        }
        where = where.substring(0, where.length() - 3);
        where += ") ";

        return where;
    }

    public static String montarOrdem(Pageable pageable) {
        if (pageable.getSort() == null)
            return "";

        if (!pageable.getSort().iterator().hasNext())
            return "";

        String orderBy = "";

        Iterator<Sort.Order> sortIterator = pageable.getSort().iterator();
        while (sortIterator.hasNext()) {
            Sort.Order order = sortIterator.next();
            orderBy += order.getProperty() + " " + order.getDirection().name().toLowerCase() + ", ";
        }
        orderBy = orderBy.substring(0, orderBy.length() - 2);

        orderBy = orderBy.length() > 0 ? " order by " + orderBy : orderBy;

        return orderBy;

    }

    public static Query atribuirPaginacao(Query q, Pageable pageable) {
        q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        q.setMaxResults(pageable.getPageSize());
        return q;
    }

    public static Query atribuirParametroDeBuscaPorId(Query q, Long id){
        if (id == null)
            return q;

        q.setParameter("id",id);
        return q;
    }

    public static Query atribuirParametroDeBusca(Query q, String query) {
        if (!StringUtils.isBlank(query)) {
            q.setParameter("busca", "%" + query.trim().toLowerCase() + "%");
        }

        return q;
    }
}
