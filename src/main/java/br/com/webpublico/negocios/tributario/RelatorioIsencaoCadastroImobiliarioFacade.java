package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioIsencaoCadastroImobiliario;
import br.com.webpublico.enums.TipoLancamentoIsencaoIPTU;
import br.com.webpublico.enums.tributario.TipoCategoriaIsencaoIPTU;
import br.com.webpublico.negocios.AtributoFacade;
import br.com.webpublico.negocios.ProcessoIsencaoIPTUFacade;
import br.com.webpublico.util.SQLUtil;
import br.com.webpublico.webreportdto.dto.WebReportUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.OrigemIsencaoIPTU;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Wellington Abdo on 24/08/2016.
 */
@Stateless
public class RelatorioIsencaoCadastroImobiliarioFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProcessoIsencaoIPTUFacade processoIsencaoIPTUFacade;
    @EJB
    private AtributoFacade atributoFacade;

    public ProcessoIsencaoIPTUFacade getProcessoIsencaoIPTUFacade() {
        return processoIsencaoIPTUFacade;
    }

    private String criarCriterioFiltro(String descricaoFiltro, Object valor) {
        StringBuilder filtro = new StringBuilder();
        if (valor != null && !valor.toString().trim().isEmpty()) {
            filtro.append(" ").append(descricaoFiltro).append(": ").append(valor).append("; ");
        }
        return filtro.toString();
    }

    public void montarWhere(RelatorioDTO dto,
                            FiltroRelatorioIsencaoCadastroImobiliario filtro,
                            String inSetores) {
        StringBuilder where = new StringBuilder();
        StringBuilder whereProcessoIsencao = new StringBuilder();

        StringBuilder filtros = new StringBuilder();
        Map<String, Map<String, String>> filtrosExcel = Maps.newLinkedHashMap();

        if (filtro.getSetorInicial() != null && !filtro.getSetorInicial().isEmpty()) {
            filtros.append(criarCriterioFiltro("Setor Inicial", filtro.getSetorInicial()));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Setor Inicial", filtro.getSetorInicial());
        }

        if (filtro.getSetorFinal() != null && !filtro.getSetorFinal().isEmpty()) {
            filtros.append(criarCriterioFiltro("Setor Final", filtro.getSetorFinal()));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Setor Final", filtro.getSetorFinal());
        }

        where.append(" and (setor.id in ").append(inSetores).append(") ");

        if (filtro.getExercicioInicial() != null) {
            where.append(SQLUtil.criarCondicaoWhere("and", "exercicio.ano", ">=", filtro.getExercicioInicial().getAno()));
            filtros.append(criarCriterioFiltro("Exercício Inicial", filtro.getExercicioInicial()));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Exercício Inicial", filtro.getExercicioInicial().toString());
        }

        if (filtro.getExercicioFinal() != null) {
            where.append(SQLUtil.criarCondicaoWhere("and", "exercicio.ano", "<=", filtro.getExercicioFinal().getAno()));
            filtros.append(criarCriterioFiltro("Exercício Final", filtro.getExercicioFinal()));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Exercício Final", filtro.getExercicioFinal().toString());
        }

        if (filtro.getInscricaoInicial() != null && !filtro.getInscricaoInicial().isEmpty()) {
            where.append(SQLUtil.criarCondicaoWhere("and", "ci.inscricaoCadastral", ">=", filtro.getInscricaoInicial()));
            filtros.append(criarCriterioFiltro("Inscrição Inicial", filtro.getInscricaoInicial()));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Inscrição Inicial", filtro.getInscricaoInicial());
        }

        if (filtro.getInscricaoFinal() != null && !filtro.getInscricaoFinal().isEmpty()) {
            where.append(SQLUtil.criarCondicaoWhere("and", "ci.inscricaoCadastral", "<=", filtro.getInscricaoFinal()));
            filtros.append(criarCriterioFiltro("Inscrição Final", filtro.getInscricaoFinal()));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Inscrição Final", filtro.getInscricaoFinal());
        }

        if (!filtro.getUsuarios().isEmpty()) {
            where.append(SQLUtil.criarCondicaoWhere("and", "usuario.id", "in", filtro.getUsuarios()));
            filtros.append(criarCriterioFiltro("Usuários", filtro.getLoginUsuariosSelecionados()));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Usuários", filtro.getLoginUsuariosSelecionados());
        }

        if (filtro.getOrigemIsencao() != null) {
            filtros.append(criarCriterioFiltro("Origem de Isenção de IPTU", filtro.getOrigemIsencao().getDescricao()));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Origem de Isenção de IPTU", filtro.getOrigemIsencao().getDescricao());
        }

        if (OrigemIsencaoIPTU.TODOS.equals(filtro.getOrigemIsencao()) || OrigemIsencaoIPTU.PROCESSO_ISENCAO.equals(filtro.getOrigemIsencao())) {
            whereProcessoIsencao.append(" and isencao.situacao <> '").append(IsencaoCadastroImobiliario.Situacao.CANCELADO).append("'");

            if (filtro.getProcesso() != null) {
                whereProcessoIsencao.append(SQLUtil.criarCondicaoWhere("and", "processoisencao.id", "=", filtro.getProcesso()));
                filtros.append(criarCriterioFiltro("Processo", filtro.getProcesso()));
                WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Processo", filtro.getProcesso().toString());
            }
            if (filtro.getTipoCategoriaIsencaoIPTU() != null) {
                whereProcessoIsencao.append(SQLUtil.criarCondicaoWhere("and", "categoria.tipoCategoriaIsencaoIPTU", "=", filtro.getTipoCategoriaIsencaoIPTU().name()));
                filtros.append(criarCriterioFiltro("Categoria", filtro.getTipoCategoriaIsencaoIPTU()));
                WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Categoria", filtro.getTipoCategoriaIsencaoIPTU().getDescricao());
            }
            if (filtro.getCategoria() != null) {
                whereProcessoIsencao.append(SQLUtil.criarCondicaoWhere("and", "categoria.id", "=", filtro.getCategoria()));
                filtros.append(criarCriterioFiltro("Categoria de Isenção", filtro.getCategoria()));
                WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Categoria de Isenção", filtro.getCategoria().toString());
            }

            if (FiltroRelatorioIsencaoCadastroImobiliario.TipoEfetivacao.EFETIVADOS.equals(filtro.getTipoEfetivacao())) {
                whereProcessoIsencao.append(" and iptu.id is not null ");
            } else if (FiltroRelatorioIsencaoCadastroImobiliario.TipoEfetivacao.NAO_EFETIVADOS.equals(filtro.getTipoEfetivacao())) {
                whereProcessoIsencao.append(" and iptu.id is null ");
            }

            if (filtro.getTipoEfetivacao() != null) {
                filtros.append(criarCriterioFiltro("Efetivados", filtro.getTipoEfetivacao().getDescricao()));
                WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Efetivados", filtro.getTipoEfetivacao().getDescricao());
            }
        }

        if (filtro.getValoresPatrimonio() != null && !filtro.getValoresPatrimonio().isEmpty()) {
            List<Long> ids = filtro.getValoresPatrimonio().stream()
                .map(ValorPossivel::getId).collect(Collectors.toList());
            String wherePatrimonio = " and exists (select 1 " +
                "   from lote_valoratributo lva " +
                "  inner join valoratributo va on va.id = lva.atributos_id " +
                "  inner join valorpossivel vp on vp.id = va.valordiscreto_id " +
                "  inner join atributo on atributo.id = lva.atributos_key " +
                "  inner join lote on lote.id = lva.lote_id " +
                "  inner join cadastroimobiliario cad on cad.lote_id = lote.id " +
                " where cad.id = ci.id " +
                "   and vp.id in ( " + StringUtils.join(ids, ",") + ")) ";
            where.append(wherePatrimonio);
            filtros.append(criarCriterioFiltro("Patrimônio",
                StringUtils.join(filtro.getValoresPatrimonio().stream().map(ValorPossivel::getDescricao)
                .collect(Collectors.toList()), ", ")));
        }

        filtro.setCriteriosUtilizados(filtros.toString());
        filtro.setCriteriosUtilizadosExcel(filtrosExcel);
        dto.adicionarParametro("where", where.toString());
        dto.adicionarParametro("where_processo_isencao", whereProcessoIsencao.toString());
    }

    public List<Setor> buscarSetoresParaFiltrarRelatorio(FiltroRelatorioIsencaoCadastroImobiliario filtro) {
        String hql = "select s from Setor s ";
        String juncao = " where ";
        if (filtro.getSetorInicial() != null && !"".equals(filtro.getSetorInicial().trim())) {
            hql = hql + juncao + " to_number(s.codigo) >= :inicial ";
            juncao = " and ";
        }
        if (filtro.getSetorFinal() != null && !"".equals(filtro.getSetorFinal().trim())) {
            hql = hql + juncao + " to_number(s.codigo) <= :final ";
        }
        hql = hql + " order by s.codigo";
        Query q = this.em.createQuery(hql);
        if (filtro.getSetorInicial() != null && !"".equals(filtro.getSetorInicial().trim())) {
            q.setParameter("inicial", filtro.getSetorInicial());
        }
        if (filtro.getSetorFinal() != null && !"".equals(filtro.getSetorFinal().trim())) {
            q.setParameter("final", filtro.getSetorFinal());
        }
        return q.getResultList();
    }

    public List<ValorPossivel> getValoresPossiveisPatrimonio() {
        Atributo patrimonio = atributoFacade.recuperaAtributoComIdentificacao("patrimonio");
        return patrimonio != null ? patrimonio.getValoresPossiveis() : Lists.newArrayList();
    }
}
