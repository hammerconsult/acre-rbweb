/*
 * Codigo gerado automaticamente em Fri May 13 15:37:08 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CertidaoDividaAtiva;
import br.com.webpublico.enums.Operador;
import br.com.webpublico.enums.TipoCadastroTributario;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateful
public class CertidaoDividaAtivaDataModel extends LazyDataModel<CertidaoDividaAtiva> implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private RecuperadorFacade recuperadorFacade;
    private Map<RecuperadorFacade.FiltroCampoOperador, Object> mapaParametrosExternos;
    private Boolean primeiraPesquisa;

    public Map<RecuperadorFacade.FiltroCampoOperador, Object> getMapaParametrosExternos() {
        return mapaParametrosExternos;
    }

    public void setMapaParametrosExternos(Map<RecuperadorFacade.FiltroCampoOperador, Object> mapaParametrosExternos) {
        this.mapaParametrosExternos = mapaParametrosExternos;
    }

    public Boolean getPrimeiraPesquisa() {
        return primeiraPesquisa;
    }

    public void setPrimeiraPesquisa(Boolean primeiraPesquisa) {
        this.primeiraPesquisa = primeiraPesquisa;
    }

    private String montarSelect() {
        String select = " select distinct cda ";
        return select;
    }

    private String montarSelectCount() {
        String select = " select count(distinct cda) ";
        return select;
    }

    private String montarFrom() {
        String from = " from CertidaoDividaAtiva cda " +
                " left join cda.certidoesLegadas legadas ";
        return from;
    }

    private Map<String, String> montarWhere(Map<RecuperadorFacade.FiltroCampoOperador, Object> params) {
        Map<String, String> retorno = new HashMap<>();
        String where = params == null || params.isEmpty() ? "" : "   where ";
        int x = 1;
        for (RecuperadorFacade.FiltroCampoOperador filtro : params.keySet()) {
            String query = filtro.getCampo();
            String nomeParam = filtro.getCampo().replace(".", "_") + x;
            if (filtro.isCampoEhComplemento()) {
                where += filtro.getComplementoQuery().replace("?", ":" + nomeParam) + " and ";
            } else if (!Operador.LIKE.equals(filtro.getOperador()) && !Operador.NOT_LIKE.equals(filtro.getOperador())) {
                where += "cda." + query + " " + filtro.getOperador().getOperador() + " :" + nomeParam + " and ";
            } else {
                where += " to_char(lower(cda." + query + ")) " + filtro.getOperador().getOperador() + " :" + nomeParam + " and ";
            }
            x++;
        }
        where = where != null && where.trim().length() > 0 ? where.substring(0, where.length() - 4) : where;
        retorno.put("WHERE", where);
        return retorno;
    }

    private String montarOrderBy() {
        String orderBy = "";
        return orderBy;
    }

    private Map<String, Object> getParametros(Map<RecuperadorFacade.FiltroCampoOperador, Object> params) {
        Map<String, Object> parametros = new HashMap<String, Object>();
        int x = 1;
        for (RecuperadorFacade.FiltroCampoOperador filtro : params.keySet()) {
            Object valor = params.get(filtro);
            if (filtro.getOperador().equals(Operador.LIKE) || filtro.getOperador().equals(Operador.NOT_LIKE)) {
                valor = "%" + valor + "%";
            }
            parametros.put(filtro.getCampo().replace(".", "_") + x, valor);
            x++;
        }
        return parametros;
    }

    @Override
    public List<CertidaoDividaAtiva> load(int i, int i2, String sortField, SortOrder sortOrder, Map<String, String> stringMap) {
        if (mapaParametrosExternos == null || mapaParametrosExternos.isEmpty()) {
            setRowCount(0);
            return null;
        }

        if (primeiraPesquisa) {
            i = 0;
            primeiraPesquisa = false;
        }

        String select = montarSelect();
        String selectCount = montarSelectCount();
        String from = montarFrom();
        stringMap = montarWhere(mapaParametrosExternos);
        String where = stringMap.get("WHERE");
        stringMap.remove("WHERE");
        Map<String, Object> parametros = getParametros(mapaParametrosExternos);
        String orderBy = montarOrderBy();

        Map<String, Object> dados = recuperadorFacade.getResultadoDe(selectCount, select, from, where, orderBy, parametros, i, i2);

        if (dados != null) {
            setRowCount(Integer.parseInt("" + dados.get("COUNT")));
        }

        return (List<CertidaoDividaAtiva>) dados.get("DADOS");
//        try {
//            List<CertidaoDividaAtiva> lista = (List<CertidaoDividaAtiva>) dados.get("DADOS");
//            if (lista != null && !lista.isEmpty()) {
//                for (CertidaoDividaAtiva certidao : lista) {
//                    certidao.getItensCertidaoDividaAtiva().size();
//                    List<ItemCertidaoDividaAtiva> item = certidao.getItensCertidaoDividaAtiva();
//                    for (ItemCertidaoDividaAtiva aux : item) {
//                        aux.getItemInscricaoDividaAtiva().getItensInscricaoDividaParcelas().size();
//                    }
//                }
//                return lista;
//            }
//            return null;
//        } catch (Exception e) {
//            return null;
//        }
    }

    @Override
    public void setRowIndex(int rowIndex) {
        if (rowIndex == -1 || getPageSize() == 0) {
            super.setRowIndex(-1);
        } else {
            super.setRowIndex(rowIndex % getPageSize());
        }
    }

    public String getComplementoDivida(Operador operador) {
        return " cda in (select certidao from ItemCertidaoDividaAtiva icda " +
                " join icda.certidao certidao " +
                " join icda.itemInscricaoDividaAtiva iteminscricao " +
                "   where iteminscricao.divida " + operador.getOperador() + " ?) ";
    }

    public String getComplementoNumeroCdaLegada(Operador operador) {
        return " legadas.numeroLegado " + operador.getOperador() + " ? ";
    }

    public String getComplementoExericioCdaLegada(Operador operador) {
        return " legadas.anoLegado " + operador.getOperador() + " ? ";
    }

    public String getComplementoExercicioDivida(Operador operador) {
        return " cda in (select certidao from ItemCertidaoDividaAtiva icda " +
                " join icda.certidao certidao " +
                " join icda.itemInscricaoDividaAtiva iteminscricao " +
                " join iteminscricao.inscricaoDividaAtiva inscricao " +
                "   where inscricao.exercicio.ano " + operador.getOperador() + " ?) ";
    }

    public String getComplementoCadastroInicial(TipoCadastroTributario tipoCadastro) {
        StringBuffer hql = new StringBuffer(" cda.cadastro in (");
        if (TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastro)) {
            hql.append(" select cadI from CadastroImobiliario cadI where cadI.inscricaoCadastral >= ? ");
        }
        if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastro)) {
            hql.append(" select cadE from CadastroEconomico cadE where cadE.inscricaoCadastral >= ? ");
        }
        if (TipoCadastroTributario.RURAL.equals(tipoCadastro)) {
            hql.append(" select cadR from CadastroRural cadR where to_char(cadR.codigo) >= ? ");
        }
        return hql.append(" ) ").toString();
    }

    public String getComplementoCadastroFinal(TipoCadastroTributario tipoCadastro) {
        StringBuffer hql = new StringBuffer(" cda.cadastro in (");
        if (TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastro)) {
            hql.append(" select cadI from CadastroImobiliario cadI where cadI.inscricaoCadastral <= ? ");
        }
        if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastro)) {
            hql.append(" select cadE from CadastroEconomico cadE where cadE.inscricaoCadastral <= ? ");
        }
        if (TipoCadastroTributario.RURAL.equals(tipoCadastro)) {
            hql.append(" select cadR from CadastroRural cadR where to_char(cadR.codigo) <= ? ");
        }

        return hql.append(" ) ").toString();
    }

    public String getComplementoProcessoJudicial() {
        return "cda in ( select p.certidaoDividaAtiva from ProcessoJudicialCDA p " +
            " where p.processoJudicial.situacao = 'ATIVO' and " +
            " replace(replace(replace(p.processoJudicial.numeroProcessoForum,'.',''),'-',''),'/','') like ? )";
    }
}
