package br.com.webpublico.nfse.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultaServicoDeclaradoDTO implements Serializable {

    private Integer offset;
    private Integer limit;
    private List<ParametroQuery> parametrosQuery;
    private String orderBy;

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<ParametroQuery> getParametrosQuery() {
        return parametrosQuery;
    }

    public void setParametrosQuery(List<ParametroQuery> parametrosQuery) {
        this.parametrosQuery = parametrosQuery;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
