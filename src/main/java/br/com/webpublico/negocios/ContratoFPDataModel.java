/*
 * Codigo gerado automaticamente em Fri May 13 15:37:08 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.*;

@Stateless
public class ContratoFPDataModel extends LazyDataModel<ContratoFP> implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private RecuperadorFacade recuperadorFacade;
    protected static final String CHAVE = "enganar(";
    private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String randomString(int len) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    private String montarSelect() {
        String select = " select contrato ";
        return select;
    }

    private String montarSelectCount() {
        String select = " select count(contrato) ";
        return select;
    }

    private String montarFrom() {
        String from = " from ContratoFP contrato ";
        return from;
    }

    private Map<String, String> montarWhere(Map<String, String> params) {
        Map<String, String> retorno = new HashMap<>();
        String where = params == null || params.isEmpty() ?  "" : "   where ";
        for (String s : params.keySet()) {
            String query = s;
            if (query.contains(CHAVE)) {
                query = query.replace("\\'", "'");
                query = query.replace(CHAVE, "");
                query = query.substring(0, query.length() - 2);
                query = query.substring(1, query.length());
                String nomeParam = randomString(10);
                where += query + " like :" + nomeParam+" and";

                retorno.put(nomeParam, params.get(s));
            } else {
                where += " to_char(lower(contrato." + s + ")) like :" + s.replace(".", "_")+" and ";
                retorno.put(s, params.get(s));
            }
        }
        where = where != null && where.trim().length() > 0 ? where.substring(0, where.length() - 4) : where;
        retorno.put("WHERE", where);
        return retorno;
    }

    private String montarOrderBy() {
        String orderBy = "";
        return orderBy;
    }

    private Map<String, Object> getParametros(Map<String, String> params) {
        Map<String, Object> parametros = new HashMap<String, Object>();
        for (String s : params.keySet()) {
            parametros.put(s.replace(".", "_"), "%" + ((String) params.get(s)).toLowerCase() + "%");
        }
        return parametros;
    }

    @Override
    public List<ContratoFP> load(int i, int i2, String sortField, SortOrder sortOrder, Map<String, String> stringMap) {
        String select = montarSelect();
        String selectCount = montarSelectCount();
        String from = montarFrom();
        stringMap = montarWhere(stringMap);
        String where = stringMap.get("WHERE");
        stringMap.remove("WHERE");
        Map<String, Object> parametros = getParametros(stringMap);
        String orderBy = montarOrderBy();

        Map<String, Object> dados = recuperadorFacade.getResultadoDe(selectCount, select, from, where, orderBy, parametros, i, i2);

        if (dados != null) {
            setRowCount(Integer.parseInt("" + dados.get("COUNT")));
        }

        List<ContratoFP> contratos =(List<ContratoFP>) dados.get("DADOS");
        return contratos;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        if (rowIndex == -1 || getPageSize() == 0) {
            super.setRowIndex(-1);
        } else {
            super.setRowIndex(rowIndex % getPageSize());
        }
    }
}
