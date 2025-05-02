package br.com.webpublico.relatoriofacade.rh;

import br.com.webpublico.entidades.CompetenciaFP;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RelatorioSiope;
import br.com.webpublico.enums.StatusCompetencia;
import br.com.webpublico.enums.TipoDeCargo;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.UtilRelatorioContabil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 08/02/2018.
 */
@Stateless
public class RelatorioSiopeFacade {
    protected static final Logger logger = LoggerFactory.getLogger(RelatorioFolhaPorSecretariaRecVincFPFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public void setFolhaDePagamentoFacade(FolhaDePagamentoFacade folhaDePagamentoFacade) {
        this.folhaDePagamentoFacade = folhaDePagamentoFacade;
    }

    public GrupoRecursoFPFacade getGrupoRecursoFPFacade() {
        return grupoRecursoFPFacade;
    }

    public void setGrupoRecursoFPFacade(GrupoRecursoFPFacade grupoRecursoFPFacade) {
        this.grupoRecursoFPFacade = grupoRecursoFPFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public String concatenarParametros(List<ParametrosRelatorios> parametros, String clausula) {
        StringBuilder sql = new StringBuilder();
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            sql.append(clausula + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
            sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");

        }
        return sql.toString();
    }

    public List<RelatorioSiope> montarConsulta(List<ParametrosRelatorios> parametros) {
        String sql = " select" +
            " dados.mes +1," +
            " dados.cpf," +
            " dados.nome," +
            " dados.inep," +
            " dados.unidade," +
            " dados.CARGAHORARIA," +
            " dados.tipoCargo," +
            " sum(dados.vencimentoBase) as vencimentoBase," +
            " sum(dados.outrasReceitas) as outrasReceitas," +
            " sum(dados.valorBruto) as valorBruto, " +
            " dados.modalidade, " +
            " dados.matricula," +
            " dados.segmento " +
            " from (" +
            " select replace(replace(pf.cpf,'.',''),'-','') as cpf," +
            " trim(pf.nome) as nome," +
            " mat.MATRICULA || '/' || v.numero as matricula," +
            " CONTRATOFP.segmentoatuacao as segmento," +
            " ho.descricao as unidade," +
            " CARGO.CARGAHORARIA as CARGAHORARIA," +
            " CARGO.TIPODECARGO as tipoCargo," +
            " cargo.DESCRICAO as cargo," +
            " folha.mes as mes, " +
            " uo.codigoinep as inep," +
            " moda.codigo as modalidade," +
            " coalesce((select coalesce(sum(item.valor),0) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id" +
            " inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id" +
            " inner join itembasefp itemBase on itemBase.eventofp_id = item.eventofp_id " +
            " inner join basefp b on b.id = itemBase.BASEFP_ID" +
            " where ficha.vinculofp_id = v.id and ff.id = item.FICHAFINANCEIRAFP_ID and item.tipoeventofp = 'VANTAGEM' and b.codigo = 1080), 0) as vencimentoBase," +
            " coalesce((select coalesce(sum(item.valor),0) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id" +
            "           inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id" +
            "         where ficha.vinculofp_id = v.id and ff.id = item.FICHAFINANCEIRAFP_ID and item.tipoeventofp = 'VANTAGEM'" +
            "               and item.EVENTOFP_ID not in(select itemBase.eventofp_id from itembasefp itemBase" +
            "           inner join basefp b on b.id = itemBase.BASEFP_ID where item.tipoeventofp = 'VANTAGEM' and b.codigo = 1080)), 0) as outrasReceitas," +
            "(select coalesce(sum(item.valor),0) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id" +
            "           inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id" +
            "         where ficha.vinculofp_id = v.id and ff.id = item.FICHAFINANCEIRAFP_ID and item.tipoeventofp = 'VANTAGEM') as valorBruto" +
            " from vinculofp v" +
            " inner join matriculafp mat on mat.id = v.matriculafp_id" +
            " inner join contratofp on contratofp.id = v.id" +
            " inner join cargo on contratofp.CARGO_ID = cargo.id " +
            " inner join modalidadecontratofp moda on moda.id = contratofp.MODALIDADECONTRATOFP_ID " +
            " inner join pessoafisica pf on pf.id = mat.pessoa_id" +
            " inner join FichaFinanceiraFP ff on ff.vinculofp_id = v.id" +
            " inner join folhadepagamento folha on folha.id = ff.folhadepagamento_id" +
            " inner join UNIDADEORGANIZACIONAL uo on uo.id = ff.UNIDADEORGANIZACIONAL_ID" +
            "  inner join VWHIERARQUIAADMINISTRATIVA ho on (uo.id = ho.subordinada_id)" +
            "  inner join recursofp rec on rec.id = ff.recursoFP_id" +
            "  inner join agrupamentoRecursoFP agrup on agrup.recursoFP_id = rec.id  " +
            "  inner join GrupoRecursoFP grupo on agrup.grupoRecursoFP_id = grupo.id  " +
            " where trunc(folha.calculadaEM) between ho.inicioVigencia and coalesce(ho.fimVigencia, trunc(folha.calculadaEM)) " +
            " and sysdate between ho.inicioVigencia and coalesce(ho.fimVigencia, sysdate) " +
            " and moda.codigo in (1,4)" +
            "        " + concatenarParametros(parametros, "and") +
            "        ) dados " +
            " where valorBruto > 0 " +
            "        group by dados.cpf," +
            " dados.mes," +
            " dados.nome," +
            " dados.inep," +
            " dados.unidade," +
            " dados.CARGAHORARIA," +
            " dados.tipoCargo," +
            " dados.cargo," +
            " dados.modalidade, " +
            " dados.matricula, " +
            " dados.segmento " +
            " order by dados.nome";
        Query q = em.createNativeQuery(sql);
        UtilRelatorioContabil.adicionarParametros(parametros, q);

        List<Object[]> resultado = q.getResultList();
        List<RelatorioSiope> retorno = new ArrayList<>();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                RelatorioSiope relatorio = new RelatorioSiope();
                relatorio.setMes(obj[0].toString());
                relatorio.setCpf((String) obj[1]);
                relatorio.setNome((String) obj[2]);
                relatorio.setMatricula((String) obj[11]);
                relatorio.setCodigoInep((String) obj[3]);
                relatorio.setUnidade((String) obj[4]);
                if (obj[5] == null) {
                    relatorio.setCargaHoraria(null);
                } else {
                    relatorio.setCargaHoraria(Integer.parseInt(String.valueOf(obj[5])));
                }
                if (Strings.isNullOrEmpty((String) obj[6])) {
                    relatorio.setCodigoCategoria(null);
                    relatorio.setCategoriaProfissionalSiope(null);
                    relatorio.setCodigoServicoSiope(null);
                    relatorio.setTipoServicoSiope(null);
                    relatorio.setTipoDeCargo(null);
                } else {
                    relatorio.setCodigoCategoria(TipoDeCargo.valueOf((String) obj[6]).getCodigoCategoria());
                    relatorio.setCategoriaProfissionalSiope(TipoDeCargo.valueOf((String) obj[6]).getCategoriaProfissionalSiope());
                    relatorio.setCodigoServicoSiope(TipoDeCargo.valueOf((String) obj[6]).getCodigoServicoSiope());
                    relatorio.setTipoServicoSiope(TipoDeCargo.valueOf((String) obj[6]).getTipoServicoSiope());
                    relatorio.setTipoDeCargo(TipoDeCargo.valueOf((String) obj[6]));
                }
                relatorio.setVencimentoBase((BigDecimal) obj[7]);
                relatorio.setOutrasReceitas((BigDecimal) obj[8]);
                relatorio.setValorBruto((BigDecimal) obj[9]);
                relatorio.setCodigoModalidade((BigDecimal) obj[10]);
                relatorio.setSegmentoAtuacao((String) obj[12]);
                retorno.add(relatorio);
            }
        }
        return retorno;
    }


    public CompetenciaFP buscarUltimaCompetenciaTipoNormalEStatusAberta() {
        return competenciaFPFacade.recuperarUltimaCompetencia(StatusCompetencia.ABERTA, TipoFolhaDePagamento.NORMAL);
    }

    public RecursoFPFacade getRecursoFPFacade() {
        return recursoFPFacade;
    }
}
