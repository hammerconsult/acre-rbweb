/*
 * Codigo gerado automaticamente em Fri May 13 15:37:08 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;


public class HierarquiaOrganizacionalDataModel extends LazyDataModel<HierarquiaOrganizacional> implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(HierarquiaOrganizacionalDataModel.class);
    private Date dataOperacao;
    private TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional;
    protected static final String CHAVE = "enganar(";
    private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private RecuperadorFacade recuperadorFacade;

    public HierarquiaOrganizacionalDataModel(Date dataOperacao, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional, RecuperadorFacade rf) {
        this.dataOperacao = dataOperacao;
        this.tipoHierarquiaOrganizacional = tipoHierarquiaOrganizacional;
        this.recuperadorFacade = rf;
    }

    private String randomString(int len) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public void setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        this.tipoHierarquiaOrganizacional = tipoHierarquiaOrganizacional;
    }

    private String montarSelect() {
        String select = " select ho ";
        return select;
    }

    private String montarFrom() {
        String from = " from HierarquiaOrganizacional ho ";
        return from;
    }

    private String montarWhere(Map<String, String> params) {
        String where = "   where :dataOperacao between ho.inicioVigencia and coalesce(ho.fimVigencia, :dataOperacao)" +
                " and ho.tipoHierarquiaOrganizacional = :tipoHierarquiaOrganizacional";
        for (String s : params.keySet()) {
            String query = s;
            if (query.contains(CHAVE)) {
                query = query.replace("\\'", "'");
                query = query.replace(CHAVE, "");
                query = query.substring(0, query.length() - 2);
                query = query.substring(1, query.length());
                String nomeParam = randomString(10);
                where += " and " + query + " like :" + nomeParam;

                params.put(nomeParam, params.get(s));
                params.remove(s);
            } else {
                where += " and to_char(lower(ho." + s + ")) like :" + s.replace(".", "_");
            }
        }
        return where;
    }

    private String montarOderBy() {
        String orderBy = " order by ho.codigo ";
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
    public List<HierarquiaOrganizacional> load(int i, int i2, String sortField, SortOrder sortOrder, Map<String, String> stringMap) {
        logger.debug("Chamou load em [{}]", this);
        logger.debug("Data Operação: [{}]", this.dataOperacao);
        logger.debug("Tipo Hierarquia Organizacional: [{}]", this.tipoHierarquiaOrganizacional);
        String select = montarSelect();
        logger.debug("SELECT: [{}]", select);
        String from = montarFrom();
        logger.debug("FROM: [{}]", from);
        String where = montarWhere(stringMap);
        logger.debug("WHERE: [{}]", where);
        Map<String, Object> parametros = getParametros(stringMap);
        String orderBy = montarOderBy();
        logger.debug("ORDER BY: [{}]", orderBy);

        parametros.put("dataOperacao", dataOperacao);
        parametros.put("tipoHierarquiaOrganizacional", tipoHierarquiaOrganizacional);
        for (String nomeParametro : parametros.keySet()) {
            logger.debug("Parâmetro [{}]: [{}]", nomeParametro, parametros.get(nomeParametro));
        }
        Map<String, Object> dados = recuperadorFacade.getResultadoDe("select count(ho)", select, from, where, orderBy, parametros, i, i2);
        if (dados != null) {
            setRowCount(Integer.parseInt("" + dados.get("COUNT")));
        }
        logger.debug("dados.size: [{}]", (dados == null || dados.isEmpty() ? "0" : dados.size()));
        List<HierarquiaOrganizacional> retorno = (List<HierarquiaOrganizacional>)dados.get("DADOS");
        logger.debug("Retorno: [{}]", retorno);
        return retorno; //(List<HierarquiaOrganizacional>) dados.get("DADOS");
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
