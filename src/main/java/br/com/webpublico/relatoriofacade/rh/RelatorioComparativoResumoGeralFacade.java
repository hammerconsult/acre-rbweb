package br.com.webpublico.relatoriofacade.rh;

import br.com.webpublico.controlerelatorio.rh.RelatorioComparativoResumoGeralControlador;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.rh.EventoRelatorioComparativoResumoGeralPorOrgao;
import br.com.webpublico.entidadesauxiliares.rh.OrgaoRelatorioComparativoResumoGeralPorOrgao;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.GrupoRecursoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.UtilRelatorioContabil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tharlyson on 03/04/19.
 */

@Stateless
public class RelatorioComparativoResumoGeralFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;

    public List<OrgaoRelatorioComparativoResumoGeralPorOrgao> recuperarRelatorio(List<ParametrosRelatorios> parametros, RelatorioComparativoResumoGeralControlador.Opcao opcao) {
        Query q = em.createNativeQuery(getSQLOrgaos(parametros).toString());
        q.setParameter("dataOperacao", UtilRH.getDataOperacao());
        UtilRelatorioContabil.adicionarParametrosComparandoLocais(parametros, q, 1);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<OrgaoRelatorioComparativoResumoGeralPorOrgao> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {

                OrgaoRelatorioComparativoResumoGeralPorOrgao orgao = new OrgaoRelatorioComparativoResumoGeralPorOrgao();
                orgao.setCodigo2nivel((String) obj[0]);
                orgao.setDescricao((String) obj[1]);
                orgao.setCodigoOrgao((String) obj[2]);
                orgao.setEventos(buscarEventos(parametros, orgao.getCodigo2nivel(), opcao, orgao.getCodigoOrgao()));
                if (!orgao.getEventos().isEmpty()) {
                    retorno.add(orgao);
                }
            }
            return retorno;
        }
    }

    public List<EventoRelatorioComparativoResumoGeralPorOrgao> buscarRelatorioGeral(List<ParametrosRelatorios> parametros) {
        Query q = em.createNativeQuery(getSQLEventosGeral(parametros).toString());
        UtilRelatorioContabil.adicionarParametrosComparandoLocais(parametros, q, 2, 3);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            final List<EventoRelatorioComparativoResumoGeralPorOrgao> retorno = new ArrayList<>();

            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                EventoRelatorioComparativoResumoGeralPorOrgao evento = new EventoRelatorioComparativoResumoGeralPorOrgao();
                evento.setCodigo((String) obj[0]);
                evento.setDescricao((String) obj[1]);
                evento.setTipo(getTipo((String) obj[2]));
                evento.setQuantidadeComparativoUm((BigDecimal) obj[3]);
                evento.setValorComparativoUm((BigDecimal) obj[4]);
                evento.setQuantidadeComparativoDois((BigDecimal) obj[5]);
                evento.setValorComparativoDois((BigDecimal) obj[6]);
                evento.setQuantidadeDiferenca((BigDecimal) obj[7]);
                evento.setDiferenca((BigDecimal) obj[8]);
                retorno.add(evento);
            }
            return retorno;
        }
    }


    private List<EventoRelatorioComparativoResumoGeralPorOrgao> buscarEventos(List<ParametrosRelatorios> parametros, String codigo2nivel, RelatorioComparativoResumoGeralControlador.Opcao opcao, String codigoOrgao) {
        Query q = em.createNativeQuery(getSQLEventosPorOrgao(parametros, opcao).toString());

        q.setParameter("codOrgao", codigoOrgao.substring(0, 5) + "%");

        UtilRelatorioContabil.adicionarParametrosComparandoLocais(parametros, q, 2, 3);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<EventoRelatorioComparativoResumoGeralPorOrgao> retorno = new ArrayList<>();

            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                EventoRelatorioComparativoResumoGeralPorOrgao evento = new EventoRelatorioComparativoResumoGeralPorOrgao();
                evento.setCodigo((String) obj[0]);
                evento.setDescricao((String) obj[1]);
                evento.setTipo(getTipo((String) obj[2]));
                evento.setQuantidadeComparativoUm((BigDecimal) obj[3]);
                evento.setValorComparativoUm((BigDecimal) obj[4]);
                evento.setQuantidadeComparativoDois((BigDecimal) obj[5]);
                evento.setValorComparativoDois((BigDecimal) obj[6]);
                evento.setQuantidadeDiferenca((BigDecimal) obj[7]);
                evento.setDiferenca((BigDecimal) obj[8]);
                if (RelatorioComparativoResumoGeralControlador.Opcao.SECRETARIA.equals(opcao)) {
                    evento.setGrupo((String) obj[9]);
                }
                retorno.add(evento);
            }

            return retorno;
        }
    }

    private String getTipo(String tipo) {
        if (isTipoVantagem(tipo)) {
            return "C";
        }
        if (isTipoDesconto(tipo)) {
            return "D";
        }
        if (isTipoInformativo(tipo)) {
            return "I";
        }
        return "";
    }

    private boolean isTipoInformativo(String tipo) {
        return TipoEventoFP.INFORMATIVO.name().equals(tipo);
    }

    private boolean isTipoDesconto(String tipo) {
        return TipoEventoFP.DESCONTO.name().equals(tipo);
    }

    private boolean isTipoVantagem(String tipo) {
        return TipoEventoFP.VANTAGEM.name().equals(tipo);
    }

    public StringBuilder getSQLOrgaos(List<ParametrosRelatorios> parametros) {

        StringBuilder sql = new StringBuilder();
        sql.append("    select substr(vw.codigo,4,2) as codigo_2nivel ")
            .append("        , vw.codigo || ' - ' || vw.descricao as descricao ")
            .append("        , vw.codigo as codigo ")
            .append("     from VwHierarquiaAdministrativa vw ")
            .append("    where :dataOperacao between vw.inicioVigencia and coalesce(vw.fimVigencia, :dataOperacao) ")
            .append("      and nivelestrutura(vw.codigo, '.') = 2 ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "))
            .append(" order by vw.codigo asc ");
        return sql;
    }

    private StringBuilder getSQLEventosPorOrgao(List<ParametrosRelatorios> parametros, RelatorioComparativoResumoGeralControlador.Opcao opcao) {
        StringBuilder sql = new StringBuilder();

        sql.append(" select codigo, descricao, tipo, sum(quantidadeComparativoUm) as quantidadeComparativoUm, sum(valorComparativoUm) as valorComparativoUm, ")
            .append(" sum(quantidadeComparativoDois) as quantidadeComparativoDois, sum(valorComparativoDois) as valorComparativoDois, ")
            .append(" sum(quantidadeComparativoDois)-sum(quantidadeComparativoUm) as quantidadeDiferenca, sum(valorComparativoDois)-sum(valorComparativoUm) as diferenca ");

        if (RelatorioComparativoResumoGeralControlador.Opcao.SECRETARIA.equals(opcao)) {
            sql.append(" , grupo  ");
        }
        sql.append("   from   ");

        sql.append(" ( select distinct ev.codigo as codigo ")
            .append("       , ev.descricao as descricao ")
            .append("       , item.tipoEventoFP as tipo ")
            .append("       , count(ficha.vinculofp_id) as quantidadeComparativoUm ")
            .append("       , sum(item.valor) as valorComparativoUm ")
            .append("       , 0 as quantidadeComparativoDois ")
            .append("       , 0 as valorComparativoDois ")
            .append("       , rec.descricao as grupo ");
        if (RelatorioComparativoResumoGeralControlador.Opcao.SECRETARIA.equals(opcao)) {
            sql.append("    , gru.nome as gruponome ");
        }
        sql.append("       from itemfichafinanceirafp item ")
            .append(" inner join fichafinanceirafp ficha on item.fichafinanceirafp_id = ficha.id ")
            .append(" inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id ")
            .append(" inner join vinculofp vinculo on vinculo.id = ficha.vinculofp_id ")
            .append(" inner join VWHIERARQUIAADMINISTRATIVA vw on vw.subordinada_id = ficha.unidadeorganizacional_id ")
            .append(" inner join eventofp ev on ev.id = item.eventofp_id ")
            .append(" inner join RecursoFP rec on rec.ID = ficha.recursoFP_ID ")
            .append(" inner join AgrupamentoRecursoFP agr on agr.recursoFP_ID = rec.ID ")
            .append(" inner join GrupoRecursoFP gru on gru.ID = agr.grupoRecursoFP_ID ")
            .append("      where folha.mes = :mesComparativoUm ")
            .append("        and folha.ano = :anoComparativoUm ")
            .append("        and folha.tipoFolhaDePagamento = :tipo ")
            .append("        and vw.codigo like :codOrgao ")
            .append("        and sysdate between vw.INICIOVIGENCIA and coalesce (vw.fimvigencia, sysdate) ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "));
        sql.append("  ");
        sql.append("  ");
        sql.append("  GROUP BY ev.codigo , ");
        sql.append("    ev.descricao , ");
        sql.append("    item.tipoEventoFP, ");
        sql.append("    folha.mes, ");
        sql.append("    folha.ano, ");
        sql.append("    rec.descricao ");
        if (RelatorioComparativoResumoGeralControlador.Opcao.SECRETARIA.equals(opcao)) {
            sql.append(" , gru.nome ");
        }
        sql.append("           union all ")
            .append(" select distinct ev.codigo as codigo ")
            .append("       , ev.descricao as descricao ")
            .append("       , item.tipoEventoFP as tipo ")
            .append("       , 0 as quantidadeComparativoUm ")
            .append("       , 0 as valorComparativoUm ")
            .append("       , count(ficha.vinculofp_id) as quantidadeComparativoDois ")
            .append("       , sum(item.valor) as valorComparativoDois ")
            .append("       , rec.descricao as grupo ");
        if (RelatorioComparativoResumoGeralControlador.Opcao.SECRETARIA.equals(opcao)) {
            sql.append("          , gru.nome as gruponome ");
        }
        sql.append("       from itemfichafinanceirafp item ")
            .append(" inner join fichafinanceirafp ficha on item.fichafinanceirafp_id = ficha.id ")
            .append(" inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id ")
            .append(" inner join vinculofp vinculo on vinculo.id = ficha.vinculofp_id ")
            .append(" inner join VWHIERARQUIAADMINISTRATIVA vw on vw.subordinada_id = ficha.unidadeorganizacional_id ")
            .append(" inner join eventofp ev on ev.id = item.eventofp_id ")
            .append(" inner join RecursoFP rec on rec.ID = ficha.recursoFP_ID ")
            .append(" inner join AgrupamentoRecursoFP agr on agr.recursoFP_ID = rec.ID ")
            .append(" inner join GrupoRecursoFP gru on gru.ID = agr.grupoRecursoFP_ID ")
            .append("       where folha.mes = :mesComparativoDois ")
            .append("       and folha.ano = :anoComparativoDois ")
            .append("       and folha.tipofolhadepagamento = :tipo ")
            .append("        and vw.codigo like :codOrgao ")
            .append("        and sysdate between vw.INICIOVIGENCIA and coalesce (vw.fimvigencia, sysdate) ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "));
        sql.append("  ");
        sql.append("  GROUP BY ev.codigo , ");
        sql.append("    ev.descricao , ");
        sql.append("    item.tipoEventoFP, ");
        sql.append("    folha.mes, ");
        sql.append("    folha.ano, ");
        sql.append("    rec.descricao ");

        if (RelatorioComparativoResumoGeralControlador.Opcao.SECRETARIA.equals(opcao)) {
            sql.append(" , gru.nome ");
        }
        sql.append("  ) ");
        sql.append("  GROUP BY codigo, ");
        sql.append("  descricao, ");
        sql.append("  tipo ");
        if (RelatorioComparativoResumoGeralControlador.Opcao.SECRETARIA.equals(opcao)) {
            sql.append(" , grupo ");
        }
        sql.append("  order by case tipo when 'VANTAGEM' then 'C' when 'DESCONTO' then 'D' else 'I' end, codigo ");

        return sql;
    }

    private StringBuilder getSQLEventosGeral(List<ParametrosRelatorios> parametros) {
        StringBuilder sql = new StringBuilder();

        sql.append("select codigo, descricao, tipo, sum(quantidadeComparativoUm) as quantidadeComparativoUm, sum(valorComparativoUm) as valorComparativoUm,")
            .append("sum(quantidadeComparativoDois) as quantidadeComparativoDois, sum(valorComparativoDois) as valorComparativoDois,")
            .append("sum(quantidadeComparativoDois)-sum(quantidadeComparativoUm) as quantidadeDiferenca, sum(valorComparativoDois)-sum(valorComparativoUm) as diferenca from (")
            .append("select distinct ev.codigo as codigo ")
            .append("       , ev.descricao as descricao ")
            .append("       , item.tipoEventoFP as tipo ")
            .append("       , count(ficha.vinculofp_id) as quantidadeComparativoUm ")
            .append("       , sum(item.valor) as valorComparativoUm ")
            .append("       , 0 as quantidadeComparativoDois ")
            .append("       , 0 as valorComparativoDois ")
            .append("       from itemfichafinanceirafp item ")
            .append(" inner join fichafinanceirafp ficha on item.fichafinanceirafp_id = ficha.id ")
            .append(" inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id ")
            .append(" inner join vinculofp vinculo on vinculo.id = ficha.vinculofp_id ")
            .append(" inner join VWHIERARQUIAADMINISTRATIVA vw on vw.subordinada_id = ficha.unidadeorganizacional_id ")
            .append(" inner join eventofp ev on ev.id = item.eventofp_id ")
            .append(" inner join RecursoFP rec on rec.ID = ficha.recursoFP_ID ")
            .append(" inner join AgrupamentoRecursoFP agr on agr.recursoFP_ID = rec.ID ")
            .append(" inner join GrupoRecursoFP gru on gru.ID = agr.grupoRecursoFP_ID ")
            .append("   where folha.mes = :mesComparativoUm ")
            .append("   and folha.ano = :anoComparativoUm ")
            .append("   and folha.tipofolhadepagamento = :tipo ")
            .append("        and sysdate between vw.INICIOVIGENCIA and coalesce (vw.fimvigencia, sysdate) ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 3, " and "))
            .append("   group by ev.codigo ")
            .append("   , ev.descricao ")
            .append("   , item.tipoEventoFP ")
            .append("           union all ")
            .append("select distinct ev.codigo as codigo ")
            .append("       , ev.descricao as descricao ")
            .append("       , item.tipoEventoFP as tipo ")
            .append("       , 0 as quantidadeComparativoUm ")
            .append("       , 0 as valorComparativoUm ")
            .append("       , count(ficha.vinculofp_id) as quantidadeComparativoDois ")
            .append("       , sum(item.valor) as valorComparativoDois ")
            .append("       from itemfichafinanceirafp item ")
            .append(" inner join fichafinanceirafp ficha on item.fichafinanceirafp_id = ficha.id ")
            .append(" inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id ")
            .append(" inner join vinculofp vinculo on vinculo.id = ficha.vinculofp_id ")
            .append(" inner join VWHIERARQUIAADMINISTRATIVA vw on vw.subordinada_id = ficha.unidadeorganizacional_id ")
            .append(" inner join eventofp ev on ev.id = item.eventofp_id ")
            .append(" inner join RecursoFP rec on rec.ID = ficha.recursoFP_ID ")
            .append(" inner join AgrupamentoRecursoFP agr on agr.recursoFP_ID = rec.ID ")
            .append(" inner join GrupoRecursoFP gru on gru.ID = agr.grupoRecursoFP_ID ")
            .append("       where folha.mes = :mesComparativoDois ")
            .append("       and folha.ano = :anoComparativoDois ")
            .append("       and folha.tipofolhadepagamento = :tipo ")
            .append("        and sysdate between vw.INICIOVIGENCIA and coalesce (vw.fimvigencia, sysdate) ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 2, " and "))
            .append("   group by ev.codigo ")
            .append("   , ev.descricao ")
            .append("   , item.tipoEventoFP ")
            .append("   ) group by codigo, descricao, tipo ")
            .append("   order by case tipo when 'VANTAGEM' then 'C' when 'DESCONTO' then 'D' else 'I' end, codigo ");
        return sql;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public GrupoRecursoFPFacade getGrupoRecursoFPFacade() {
        return grupoRecursoFPFacade;
    }

}
