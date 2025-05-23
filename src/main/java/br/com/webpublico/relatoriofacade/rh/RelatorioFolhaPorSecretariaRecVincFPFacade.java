package br.com.webpublico.relatoriofacade.rh;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.rh.FolhaPorSecretariaPrincipal;
import br.com.webpublico.entidadesauxiliares.rh.FolhaPorSecretariaSummary;
import br.com.webpublico.entidadesauxiliares.rh.TermoRescisaoVerba;
import br.com.webpublico.entidadesauxiliares.rh.TermoRescisaoVerbaDescontoEVantagem;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.StatusCompetencia;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRelatorioContabil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by fox_m on 06/04/2016.
 */
@Stateless
@Deprecated
public class RelatorioFolhaPorSecretariaRecVincFPFacade implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioFolhaPorSecretariaRecVincFPFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private LoteProcessamentoFacade loteProcessamentoFacade;
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
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public List<FolhaPorSecretariaPrincipal> montarConsultaLotacaoFuncionalGeral(List<HierarquiaOrganizacional> listaHierarquia, List<ParametrosRelatorios> parametros) {
        List<FolhaPorSecretariaPrincipal> retorno = Lists.newArrayList();
        montarConsultaLotacaoFuncional(parametros, retorno, listaHierarquia);
        return retorno;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public List<FolhaPorSecretariaPrincipal> montarConsultaLotacaoFuncionalPorOrgao(HierarquiaOrganizacional[] listaHierarquia, List<ParametrosRelatorios> parametros) {
        List<FolhaPorSecretariaPrincipal> retorno = Lists.newArrayList();
        montarConsultaLotacaoFuncional(parametros, retorno, Lists.newArrayList(listaHierarquia));
        return retorno;
    }

    private void montarConsultaLotacaoFuncional(List<ParametrosRelatorios> parametros, List<FolhaPorSecretariaPrincipal> retorno, List<HierarquiaOrganizacional> listaHierarquia) {
        List<ParametrosRelatorios> parametrosReport = new ArrayList<>();
        for (ParametrosRelatorios parametro : parametros) {
            parametrosReport.add(parametro);
        }
        List<FolhaPorSecretariaPrincipal> itens = montarConsultaPorLotacaoFuncional(parametrosReport, listaHierarquia);

        for (FolhaPorSecretariaPrincipal item : itens) {
            FolhaPorSecretariaPrincipal cabecalho = retornarCabecalhoJaAdicionado(retorno, item);
            if (cabecalho == null) {
                cabecalho = new FolhaPorSecretariaPrincipal();
                cabecalho.setCodigoHierarquia(item.getCodigoHierarquia());
                cabecalho.setDescricaoHierarquia(item.getDescricaoHierarquia());
                retorno.add(cabecalho);
            }
            cabecalho.getItens().add(item);
        }
    }

    private FolhaPorSecretariaPrincipal retornarCabecalhoJaAdicionado(List<FolhaPorSecretariaPrincipal> lista, FolhaPorSecretariaPrincipal item) {
        for (FolhaPorSecretariaPrincipal folha : lista) {
            if (folha.getCodigoHierarquia() != null && folha.getCodigoHierarquia().equals(item.getCodigoHierarquia())) {
                return folha;
            }
        }
        return null;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    private void preencherReportSubGrupo(List<ParametrosRelatorios> parametros, HierarquiaOrganizacional obj, FolhaPorSecretariaPrincipal item) {
        List<ParametrosRelatorios> parametrosOrgao = new ArrayList<>();
        agruparParametrosLotacao(parametrosOrgao, parametros);
        parametrosOrgao.add(new ParametrosRelatorios(" vw.codigo ", ":orgCodigo", null, OperacaoRelatorio.LIKE, obj.getCodigoSemZerosFinais() + "%", null, 1, false));
        item.setSubReportGrupo(recuperarSummaryPorLotacaoFolha(parametrosOrgao, null));
        item.setTotalServidoresGrupo(item.getItens().size());
    }

    private void preencherReportSubGrupoRecursoFP(List<ParametrosRelatorios> parametros, RecursoFP obj, FolhaPorSecretariaPrincipal item) {
        List<ParametrosRelatorios> parametrosOrgao = new ArrayList<>();
        agruparParametrosRecursoFP(parametrosOrgao, parametros);
        parametrosOrgao.add(new ParametrosRelatorios(" rec.codigo ", ":recCodigo", null, OperacaoRelatorio.LIKE, obj.getCodigo(), null, 1, false));
        item.setSubReportGrupo(recuperarSummary(parametrosOrgao, null));
        item.setTotalServidoresGrupo(item.getItens().size());
    }

    private void preencherReportSubGrupoRecursoFPAgrupado(List<ParametrosRelatorios> parametros, GrupoRecursoFP obj, FolhaPorSecretariaPrincipal item) {
        List<ParametrosRelatorios> parametrosOrgao = new ArrayList<>();
        agruparParametrosGrupoRecursoFP(parametrosOrgao, parametros);
        parametrosOrgao.add(new ParametrosRelatorios(" grupo.id ", ":grupoId", null, OperacaoRelatorio.IGUAL, obj.getId(), null, 1, false));
        item.setSubReportGrupo(recuperarSummary(parametrosOrgao, null));
        item.setTotalServidoresGrupo(item.getItens().size());
    }

    private void agruparParametrosLotacao(List<ParametrosRelatorios> parametrosOrgao, List<ParametrosRelatorios> parametros) {
        for (ParametrosRelatorios parametro : parametros) {
            if (!parametro.getNomeAtributo().contains("vw.codigo")) {
                parametrosOrgao.add(parametro);
            }
        }

    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public List<FolhaPorSecretariaPrincipal> montarConsultaRecursoFpPorOrgao(RecursoFP[] listaRecursosFp, List<ParametrosRelatorios> parametros) {
        List<FolhaPorSecretariaPrincipal> retorno = new ArrayList<>();
        for (RecursoFP obj : listaRecursosFp) {
            FolhaPorSecretariaPrincipal item = new FolhaPorSecretariaPrincipal();
            item.setDescricaoHierarquia(obj.getCodigo() + " - " + obj.getDescricao());
            List<ParametrosRelatorios> parametrosReport = new ArrayList<>();
            for (ParametrosRelatorios parametro : parametros) {
                parametrosReport.add(parametro);
            }
            parametrosReport.add(new ParametrosRelatorios(" rec.codigo ", ":recCodigo", null, OperacaoRelatorio.LIKE, obj.getCodigo(), null, 1, false));
            item.setItens(montarConsulta(parametrosReport));
            preencherReportSubGrupoRecursoFP(parametros, obj, item);
            if (!item.getItens().isEmpty()) {
                retorno.add(item);
            }
        }
        return retorno;
    }

    private void agruparParametrosRecursoFP(List<ParametrosRelatorios> parametrosOrgao, List<ParametrosRelatorios> parametros) {
        for (ParametrosRelatorios parametro : parametros) {
            if (!parametro.getNomeAtributo().contains("rec.codigo")) {
                parametrosOrgao.add(parametro);
            }
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public List<FolhaPorSecretariaPrincipal> montarConsultaGrupoRecursoFpPorOrgao(GrupoRecursoFP[] listaGrupoRecursosFp, List<ParametrosRelatorios> parametros) {
        List<FolhaPorSecretariaPrincipal> retorno = new ArrayList<>();
        for (GrupoRecursoFP obj : listaGrupoRecursosFp) {
            FolhaPorSecretariaPrincipal item = new FolhaPorSecretariaPrincipal();
            item.setDescricaoHierarquia(obj.getNome());
            List<ParametrosRelatorios> parametrosReport = new ArrayList<>();
            for (ParametrosRelatorios parametro : parametros) {
                parametrosReport.add(parametro);
            }
            parametrosReport.add(new ParametrosRelatorios(" grupo.id ", ":grupoId", null, OperacaoRelatorio.IGUAL, obj.getId(), null, 1, false));
            item.setItens(montarConsulta(parametrosReport));
            preencherReportSubGrupoRecursoFPAgrupado(parametros, obj, item);
            if (!item.getItens().isEmpty()) {
                retorno.add(item);
            }
        }
        return retorno;
    }

    private void agruparParametrosGrupoRecursoFP(List<ParametrosRelatorios> parametrosOrgao, List<ParametrosRelatorios> parametros) {
        for (ParametrosRelatorios parametro : parametros) {
            if (!parametro.getNomeAtributo().contains("grupo.id")) {
                parametrosOrgao.add(parametro);
            }
        }
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public List<FolhaPorSecretariaPrincipal> montarConsultaRecursoFpGeral(List<RecursoFP> listaRecursosFp, List<ParametrosRelatorios> parametros) {
        List<FolhaPorSecretariaPrincipal> retorno = new ArrayList<>();
        for (RecursoFP obj : listaRecursosFp) {
            FolhaPorSecretariaPrincipal item = new FolhaPorSecretariaPrincipal();
            item.setDescricaoHierarquia(obj.getCodigo() + " - " + obj.getDescricao());
            List<ParametrosRelatorios> parametrosReport = new ArrayList<>();
            for (ParametrosRelatorios parametro : parametros) {
                parametrosReport.add(parametro);
            }
            parametrosReport.add(new ParametrosRelatorios(" rec.codigo ", ":recCodigo", null, OperacaoRelatorio.LIKE, obj.getCodigo(), null, 1, false));
            item.setItens(montarConsulta(parametrosReport));
            preencherReportSubGrupoRecursoFP(parametros, obj, item);
            if (!item.getItens().isEmpty()) {
                retorno.add(item);
            }
        }
        return retorno;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public List<FolhaPorSecretariaPrincipal> montarConsultaGrupoRecursoFpGeral(List<GrupoRecursoFP> listaGrupoRecursosFp, List<ParametrosRelatorios> parametros) {
        List<FolhaPorSecretariaPrincipal> retorno = new ArrayList<>();
        for (GrupoRecursoFP obj : listaGrupoRecursosFp) {
            FolhaPorSecretariaPrincipal item = new FolhaPorSecretariaPrincipal();
            item.setDescricaoHierarquia(obj.getNome());
            List<ParametrosRelatorios> parametrosReport = new ArrayList<>();
            for (ParametrosRelatorios parametro : parametros) {
                parametrosReport.add(parametro);
            }
            parametrosReport.add(new ParametrosRelatorios(" grupo.id ", ":grupoId", null, OperacaoRelatorio.IGUAL, obj.getId(), null, 1, false));
            item.setItens(montarConsulta(parametrosReport));
            preencherReportSubGrupoRecursoFPAgrupado(parametros, obj, item);
            if (!item.getItens().isEmpty()) {
                retorno.add(item);
            }
        }
        return retorno;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    public List<FolhaPorSecretariaPrincipal> montarConsultaNaoAgrupado(List<ParametrosRelatorios> parametros) {
        List<FolhaPorSecretariaPrincipal> retorno = new ArrayList<>();
        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getRaizHierarquia(sistemaFacade.getDataOperacao());
        if (ho != null) {
            FolhaPorSecretariaPrincipal item = new FolhaPorSecretariaPrincipal();
            item.setDescricaoHierarquia(ho.getCodigo() + " - " + ho.getSubordinada().getDescricao());
            item.setItens(montarConsulta(parametros));
            if (!item.getItens().isEmpty()) {
                retorno.add(item);
            }
        }
        return retorno;
    }

    public List<FolhaPorSecretariaPrincipal> montarConsulta(List<ParametrosRelatorios> parametros) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select ");
        sb.append(" codigoRecurso, ");
        sb.append(" descricaoRecurso, ");
        sb.append(" nome, ");
        sb.append(" cpf, ");
        sb.append(" matricula, ");
        sb.append(" contrato, ");
        sb.append(" cargo, ");
        sb.append(" id_ficha, ");
        sb.append(" situacao, ");
        sb.append(" admissao, ");
        sb.append(" coalesce(cast(vinculoempregaticio as varchar(2)), ' ') as vinculoempregaticio ");
        sb.append(" from ( ");
        sb.append(" select ");
        sb.append(" rec.codigo as codigoRecurso, ");
        sb.append(" rec.descricao as descricaoRecurso, ");
        sb.append(" pf.nome, ");
        sb.append(" formatacpfcnpj(pf.cpf) as cpf, ");
        sb.append(" matricula.matricula, ");
        sb.append(" vinculo.numero as contrato, ");
        sb.append(" (trim(cargo.descricao) || ' - ' || ");
        sb.append(" coalesce((select trim(cpcs.descricao) ");
        sb.append(" || ' / ' || trim(ppcs.descricao)");
        sb.append(" from enquadramentofuncional enquadramentof");
        sb.append(" left join categoriapcs cpcs on cpcs.id = enquadramentof.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = enquadramentof.progressaopcs_id");
        sb.append(" where folha.calculadaem between enquadramentof.iniciovigencia and coalesce(enquadramentof.finalvigencia, folha.calculadaem)");
        sb.append(" and enquadramentof.contratoservidor_id = contrato.id and enquadramentof.id = (select max(id) ");
        sb.append(" from enquadramentoFuncional where folha.calculadaem between iniciovigencia and coalesce(finalvigencia, folha.calculadaem) ");
        sb.append(" and contratoServidor_id = enquadramentof.contratoServidor_id ) and rownum = 1),'') ||");
        sb.append(" coalesce((select ' - ' || cpcs.descricao || ' / ' || ppcs.descricao");
        sb.append(" from cargoconfianca cc");
        sb.append(" join enquadramentocc ecc on ecc.cargoconfianca_id = cc.id");
        sb.append(" left join categoriapcs cpcs on cpcs.id = ecc.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = ecc.progressaopcs_id");
        sb.append(" where folha.calculadaem between cc.iniciovigencia and coalesce(cc.finalvigencia, folha.calculadaem)");
        sb.append(" and folha.calculadaem between ecc.iniciovigencia and coalesce(ecc.finalvigencia, folha.calculadaem)");
        sb.append(" and cc.contratofp_id = contrato.id and cc.id = (select max(id) from CargoConfianca ");
        sb.append(" where folha.calculadaem between iniciovigencia and coalesce(finalvigencia, folha.calculadaem) and contratoFP_id = cc.contratoFP_id )  and rownum = 1),'')");
        sb.append(" ) as cargo,");
        sb.append(" ficha.id as id_ficha,");
        sb.append(" coalesce(situacao.descricao,'') as situacao,");
        sb.append(" contrato.dataAdmissao as admissao,");
        sb.append(" modalidade.CODIGO as vinculoempregaticio");
        sb.append(" from fichaFinanceiraFP ficha");
        sb.append(" inner join folhadePagamento folha on ficha.folhadepagamento_id =  folha.id");
        sb.append(" inner join vinculoFP vinculo on vinculo.id = ficha.vinculofp_id");
        sb.append(" inner join contratoFP contrato on contrato.id = vinculo.id");
        sb.append(" inner join modalidadeContratoFP modalidade on modalidade.id = contrato.modalidadeContratoFP_id");
        sb.append(" inner join situacaocontratofp situacaofp on situacaofp.contratofp_id = contrato.id ");
        sb.append(" inner join situacaofuncional situacao on situacaofp.situacaofuncional_id = situacao.id ");
        sb.append(" inner join matriculaFP matricula on matricula.id = vinculo.matriculafp_id");
        sb.append(" inner join pessoafisica pf on pf.id = matricula.pessoa_id");
        sb.append(" inner join cargo on cargo.id = contrato.cargo_id");
        sb.append(" inner join recursofp rec on rec.id = ficha.recursofp_id");
        sb.append(" inner join agrupamentorecursofp ag on ag.recursofp_id = rec.id ");
        sb.append(" inner join gruporecursofp grupo on ag.gruporecursofp_id = grupo.id ");
        sb.append(" where ficha.id is not null and coalesce(folha.EFETIVADAEM, folha.calculadaem) between situacaofp.INICIOVIGENCIA and coalesce (situacaofp.finalvigencia, sysdate)");
        sb.append(concatenaParametros(parametros, 1, " and "));
        sb.append(" union all");
        //Aposentados!!
        sb.append(" select");
        sb.append(" rec.codigo as codigoRecurso, ");
        sb.append(" rec.descricao as descricaoRecurso, ");
        sb.append(" pf.nome,");
        sb.append(" formatacpfcnpj(pf.cpf) as cpf, ");
        sb.append(" matricula.matricula,");
        sb.append(" vinculo.numero as contrato,");
        sb.append(" (trim(cargo.descricao) || ' - ' || (select trim(cpcs.descricao) || '/' || trim(ppcs.descricao)");
        sb.append(" from enquadramentofuncional enquadramentof");
        sb.append(" left join categoriapcs cpcs on cpcs.id = enquadramentof.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = enquadramentof.progressaopcs_id");
        sb.append(" where enquadramentof.contratoservidor_id = contrato.id and enquadramentof.inicioVigencia = (select max(enq.iniciovigencia)");
        sb.append(" from EnquadramentoFuncional enq where enq.contratoservidor_id = contrato.id ) and rownum = 1) ||");
        sb.append(" (select ' - ' || cpcs.descricao || '/' || ppcs.descricao");
        sb.append(" from cargoconfianca cc");
        sb.append(" join enquadramentocc ecc on ecc.cargoconfianca_id = cc.id");
        sb.append(" left join categoriapcs cpcs on cpcs.id = ecc.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = ecc.progressaopcs_id");
        sb.append(" where cc.contratofp_id = contrato.id");
        sb.append(" and cc.inicioVigencia = (select max(cco.iniciovigencia) from CargoConfianca cco where cco.contratofp_id = contrato.id)");
        sb.append(" and ecc.inicioVigencia = (select max(ecco.iniciovigencia) from EnquadramentoCC ecco where cc.id = ecco.cargoconfianca_id) and rownum = 1)) as cargo,");
        sb.append(" ficha.id as id_ficha,");
        sb.append(" coalesce(situacao.descricao,'') as situacao,");
        sb.append(" vinculo.iniciovigencia as admissao,");
        sb.append(" (select vc.codigo from contratovinculodecontrato cvc inner join vinculodecontratofp vc on vc.id = cvc.vinculodecontratofp_id");
        sb.append(" where cvc.contratofp_id = contrato.id  and cvc.iniciovigencia = (select max(cvct.iniciovigencia) from contratovinculodecontrato cvct ");
        sb.append(" where cvct.contratofp_id = contrato.id )) as vinculoempregaticio");
        sb.append(" from fichaFinanceiraFP ficha");
        sb.append(" inner join folhadePagamento folha on ficha.folhadepagamento_id =  folha.id");
        sb.append(" inner join vinculoFP vinculo on vinculo.id = ficha.vinculofp_id");
        sb.append(" inner join aposentadoria apo on apo.id = vinculo.id");
        sb.append(" inner join contratofp contrato on apo.contratofp_id = contrato.id");
        sb.append(" inner join situacaocontratofp situacaofp on situacaofp.contratofp_id = contrato.id ");
        sb.append(" inner join situacaofuncional situacao on situacaofp.situacaofuncional_id = situacao.id ");
        sb.append(" inner join matriculaFP matricula on matricula.id = vinculo.matriculafp_id");
        sb.append(" inner join pessoafisica pf on pf.id = matricula.pessoa_id");
        sb.append(" inner join cargo on cargo.id = contrato.cargo_id");
        sb.append(" inner join recursofp rec on rec.id = ficha.recursofp_id");
        sb.append(" inner join agrupamentorecursofp ag on ag.recursofp_id = rec.id ");
        sb.append(" inner join gruporecursofp grupo on ag.gruporecursofp_id = grupo.id ");
        sb.append(" where ficha.id is not null and coalesce(folha.EFETIVADAEM, folha.calculadaem) between situacaofp.INICIOVIGENCIA and coalesce (situacaofp.finalvigencia, sysdate)");
        sb.append(concatenaParametros(parametros, 1, " and "));
        sb.append(" ");
        sb.append(" union all");
        //Pensionistas
        sb.append(" select");
        sb.append(" rec.codigo as codigoRecurso, ");
        sb.append(" rec.descricao as descricaoRecurso, ");
        sb.append(" pf.nome,");
        sb.append(" formatacpfcnpj(pf.cpf) as cpf, ");
        sb.append(" matricula.matricula,");
        sb.append(" vinculo.numero as contrato,");
        sb.append(" (trim(cargo.descricao) || ' - ' || (select cpcs.descricao || '/' || ppcs.descricao");
        sb.append(" from enquadramentofuncional enquadramentof");
        sb.append(" left join categoriapcs cpcs on cpcs.id = enquadramentof.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = enquadramentof.progressaopcs_id");
        sb.append(" where enquadramentof.contratoservidor_id = contrato.id and enquadramentof.inicioVigencia = (select max(enq.iniciovigencia)");
        sb.append(" from EnquadramentoFuncional enq where enq.contratoservidor_id = contrato.id )  and rownum = 1 ))");
        sb.append(" as cargo,");
        sb.append(" ficha.id as id_ficha,");
        sb.append(" 'PENSIONISTA' as situacao,");
        sb.append(" vinculo.iniciovigencia as admissao,");
        sb.append(" (select vc.codigo from contratovinculodecontrato cvc inner join vinculodecontratofp vc on vc.id = cvc.vinculodecontratofp_id");
        sb.append(" where cvc.contratofp_id = contrato.id  and cvc.iniciovigencia = (select max(cvct.iniciovigencia) from contratovinculodecontrato cvct ");
        sb.append(" where cvct.contratofp_id = contrato.id )) as vinculoempregaticio");
        sb.append(" from fichaFinanceiraFP ficha");
        sb.append(" inner join folhadePagamento folha on ficha.folhadepagamento_id =  folha.id");
        sb.append(" inner join vinculoFP vinculo on vinculo.id = ficha.vinculofp_id");
        sb.append(" inner join Pensionista pen on pen.id = vinculo.id");
        sb.append(" inner join pensao p on p.id = pen.pensao_id");
        sb.append(" inner join contratofp contrato on p.contratofp_id = contrato.id");
        sb.append(" inner join matriculaFP matricula on matricula.id = vinculo.matriculafp_id");
        sb.append(" inner join pessoafisica pf on pf.id = matricula.pessoa_id");
        sb.append(" left join cargo on cargo.id = contrato.cargo_id");
        sb.append(" inner join recursofp rec on rec.id = ficha.recursofp_id");
        sb.append(" inner join agrupamentorecursofp ag on ag.recursofp_id = rec.id ");
        sb.append(" inner join gruporecursofp grupo on ag.gruporecursofp_id = grupo.id ");
        sb.append(" where  ficha.id is not null ");
        sb.append(concatenaParametros(parametros, 1, " and "));
        sb.append(" union all");
        //Beneficiário Pensão Judicial Tramitada e Julgada
        sb.append(" select");
        sb.append(" rec.codigo as codigoRecurso, ");
        sb.append(" rec.descricao as descricaoRecurso, ");
        sb.append(" pf.nome,");
        sb.append(" formatacpfcnpj(pf.cpf) as cpf, ");
        sb.append(" matricula.matricula,");
        sb.append(" vinculo.numero as contrato,");
        sb.append(" (select cpcs.descricao || '/' || ppcs.descricao");
        sb.append(" from enquadramentofuncional enquadramentof");
        sb.append(" left join categoriapcs cpcs on cpcs.id = enquadramentof.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = enquadramentof.progressaopcs_id");
        sb.append(" where enquadramentof.contratoservidor_id = vinculo.id and enquadramentof.inicioVigencia = (select max(enq.iniciovigencia)");
        sb.append(" from EnquadramentoFuncional enq where enq.contratoservidor_id = vinculo.id )  and rownum = 1 )");
        sb.append(" as cargo,");
        sb.append(" ficha.id as id_ficha,");
        sb.append(" coalesce((select situacao.DESCRICAO from situacaocontratofp sitc inner join situacaofuncional situacao on situacao.id = sitc.situacaofuncional_id");
        sb.append(" where sitc.contratofp_id = vinculo.id  and sitc.iniciovigencia = (select max(sit.iniciovigencia) from situacaocontratofp sit where sit.contratofp_id = vinculo.id )),'INDENIZAÇÕES') as situacao,");
        sb.append(" vinculo.iniciovigencia as admissao,");
        sb.append(" (select vc.codigo from contratovinculodecontrato cvc inner join vinculodecontratofp vc on vc.id = cvc.vinculodecontratofp_id");
        sb.append(" where cvc.contratofp_id = vinculo.id  and cvc.iniciovigencia = (select max(cvct.iniciovigencia) from contratovinculodecontrato cvct ");
        sb.append(" where cvct.contratofp_id = vinculo.id )) as vinculoempregaticio");
        sb.append(" from fichaFinanceiraFP ficha");
        sb.append(" inner join folhadePagamento folha on ficha.folhadepagamento_id =  folha.id");
        sb.append(" inner join vinculoFP vinculo on vinculo.id = ficha.vinculofp_id");
        sb.append(" inner join beneficiario ben on ben.id = vinculo.id");
        sb.append(" inner join matriculaFP matricula on matricula.id = vinculo.matriculafp_id");
        sb.append(" inner join pessoafisica pf on pf.id = matricula.pessoa_id");
        sb.append(" inner join recursofp rec on rec.id = ficha.recursofp_id ");
        sb.append(" inner join agrupamentorecursofp ag on ag.recursofp_id = rec.id ");
        sb.append(" inner join gruporecursofp grupo on ag.gruporecursofp_id = grupo.id ");
        sb.append(" where  ficha.id is not null ");
        sb.append(concatenaParametros(parametros, 1, " and "));

        sb.append(" )reg ");
        sb.append(" order by nome, codigoRecurso ");
        Query q = em.createNativeQuery(sb.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        List<Object[]> lista = q.getResultList();
        List<FolhaPorSecretariaPrincipal> retorno = new ArrayList<>();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                FolhaPorSecretariaPrincipal recurso = new FolhaPorSecretariaPrincipal();
                recurso.setCodigoRecurso((String) obj[0]);
                recurso.setDescricaoRecurso((String) obj[1]);
                recurso.setNome((String) obj[2]);
                recurso.setCpf((String) obj[3]);
                recurso.setMatricula((String) obj[4]);
                recurso.setContrato((String) obj[5]);
                recurso.setCargo((String) obj[6]);
                recurso.setIdFicha(((BigDecimal) obj[7]).longValue());
                recurso.setSituacao((String) obj[8]);
                recurso.setAdmissao((Date) obj[9]);
                recurso.setVinculoEmpregaticio((String) obj[10]);
                List<TermoRescisaoVerba> verbas = recuperarVerbas(recurso.getIdFicha());
                if (verbas != null && !verbas.isEmpty()) {
                    recurso.setVerbas(verbas);
                    retorno.add(recurso);
                }
            }
        }
        return retorno;
    }

    public List<FolhaPorSecretariaPrincipal> montarConsultaPorLotacaoFuncional(List<ParametrosRelatorios> parametros, List<HierarquiaOrganizacional> listaHierarquia) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select ");
        sb.append(" codigoHierarquia, ");
        sb.append(" descricaoHierarquia, ");
        sb.append(" nome, ");
        sb.append(" cpf, ");
        sb.append(" matricula, ");
        sb.append(" contrato, ");
        sb.append(" cargo, ");
        sb.append(" id_ficha, ");
        sb.append(" situacao, ");
        sb.append(" admissao, ");
        sb.append(" coalesce(cast(vinculoempregaticio as varchar(2)), ' ') as vinculoempregaticio ");
        sb.append(" from ( ");

        sb.append(" select ");
        sb.append(" vw.codigo as codigoHierarquia, ");
        sb.append(" vw.descricao as descricaoHierarquia, ");
        sb.append(" pf.nome, ");
        sb.append(" formatacpfcnpj(pf.cpf) as cpf,  ");
        sb.append(" matricula.matricula, ");
        sb.append(" vinculo.numero as contrato, ");
        sb.append(" (trim(cargo.descricao) || ' - ' || ");
        sb.append(" coalesce((select trim(cpcs.descricao) ");
        sb.append(" || ' / ' || trim(ppcs.descricao)");
        sb.append(" from enquadramentofuncional enquadramentof");
        sb.append(" left join categoriapcs cpcs on cpcs.id = enquadramentof.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = enquadramentof.progressaopcs_id");
        sb.append(" where trunc(folha.calculadaem) between trunc(enquadramentof.iniciovigencia) and coalesce(trunc(enquadramentof.finalvigencia), trunc(folha.calculadaem)) ");
        sb.append(" and enquadramentof.contratoservidor_id = contrato.id and enquadramentof.id = (select max(id) ");
        sb.append(" from enquadramentoFuncional where trunc(folha.calculadaem) between trunc(iniciovigencia) and coalesce(trunc(finalvigencia), trunc(folha.calculadaem)) ");
        sb.append(" and contratoServidor_id = enquadramentof.contratoServidor_id ) and rownum = 1 ),'') ||");
        sb.append(" coalesce((select ' - ' || cpcs.descricao || ' / ' || ppcs.descricao");
        sb.append(" from cargoconfianca cc");
        sb.append(" join enquadramentocc ecc on ecc.cargoconfianca_id = cc.id");
        sb.append(" left join categoriapcs cpcs on cpcs.id = ecc.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = ecc.progressaopcs_id");
        sb.append(" where trunc(folha.calculadaem) between trunc(cc.iniciovigencia) and coalesce(trunc(cc.finalvigencia), trunc(folha.calculadaem)) ");
        sb.append(" and trunc(folha.calculadaem) between trunc(ecc.iniciovigencia) and coalesce(trunc(ecc.finalvigencia), trunc(folha.calculadaem)) ");
        sb.append(" and cc.contratofp_id = contrato.id and cc.id = (select max(id) from CargoConfianca ");
        sb.append(" where trunc(folha.calculadaem) between trunc(iniciovigencia) and coalesce(trunc(finalvigencia), trunc(folha.calculadaem)) and contratoFP_id = cc.contratoFP_id ) and rownum = 1 ),'')");
        sb.append(" ) as cargo,");
        sb.append(" ficha.id as id_ficha,");
        sb.append(" coalesce(situacao.descricao,'') as situacao,");
        sb.append(" contrato.dataAdmissao as admissao,");
        sb.append(" modalidade.CODIGO as vinculoempregaticio");
        sb.append(" from fichaFinanceiraFP ficha");
        sb.append(" inner join folhadePagamento folha on ficha.folhadepagamento_id =  folha.id");
        sb.append(" inner join vinculoFP vinculo on vinculo.id = ficha.vinculofp_id");
        sb.append(" inner join contratoFP contrato on contrato.id = vinculo.id");
        sb.append(" inner join situacaocontratofp situacaofp on situacaofp.contratofp_id = contrato.id ");
        sb.append(" inner join situacaofuncional situacao on situacaofp.situacaofuncional_id = situacao.id ");
        sb.append(" inner join modalidadeContratoFP modalidade on modalidade.id = contrato.modalidadeContratoFP_id");
        sb.append(" inner join VwHierarquiaAdministrativa vw on vw.subordinada_id = ficha.unidadeorganizacional_id ");
        sb.append(" inner join matriculaFP matricula on matricula.id = vinculo.matriculafp_id");
        sb.append(" inner join pessoafisica pf on pf.id = matricula.pessoa_id");
        sb.append(" inner join cargo on cargo.id = contrato.cargo_id");
        sb.append(" where trunc(folha.calculadaem) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(folha.calculadaem)) ");
        sb.append(" and (coalesce(trunc(folha.EFETIVADAEM), trunc(folha.calculadaem)) between trunc(situacaofp.INICIOVIGENCIA) and coalesce(trunc(situacaofp.finalvigencia), sysdate))");
        sb.append(concatenaParametros(parametros, 1, " and "));
        sb.append(montarCondicaoDasHierarquias(listaHierarquia));
        sb.append(" union all");
        //Aposentados!!
        sb.append(" select");
        sb.append(" vw.codigo as codigoHierarquia, ");
        sb.append(" vw.descricao as descricaoHierarquia,");
        sb.append(" pf.nome,");
        sb.append(" formatacpfcnpj(pf.cpf) as cpf,  ");
        sb.append(" matricula.matricula,");
        sb.append(" vinculo.numero as contrato,");
        sb.append(" (trim(cargo.descricao) || ' - ' || (select trim(cpcs.descricao) || '/' || trim(ppcs.descricao)");
        sb.append(" from enquadramentofuncional enquadramentof");
        sb.append(" left join categoriapcs cpcs on cpcs.id = enquadramentof.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = enquadramentof.progressaopcs_id");
        sb.append(" where enquadramentof.contratoservidor_id = contrato.id and trunc(enquadramentof.inicioVigencia) = (select max(trunc(enq.iniciovigencia)) ");
        sb.append(" from EnquadramentoFuncional enq where enq.contratoservidor_id = contrato.id ) and rownum = 1) ||");
        sb.append(" (select ' - ' || cpcs.descricao || '/' || ppcs.descricao");
        sb.append(" from cargoconfianca cc");
        sb.append(" join enquadramentocc ecc on ecc.cargoconfianca_id = cc.id");
        sb.append(" left join categoriapcs cpcs on cpcs.id = ecc.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = ecc.progressaopcs_id");
        sb.append(" where cc.contratofp_id = contrato.id");
        sb.append(" and trunc(cc.inicioVigencia) = (select max(trunc(cco.iniciovigencia)) from CargoConfianca cco where cco.contratofp_id = contrato.id)");
        sb.append(" and trunc(ecc.inicioVigencia) = (select max(trunc(ecco.iniciovigencia)) from EnquadramentoCC ecco where cc.id = ecco.cargoconfianca_id) and rownum = 1) ) as cargo,");
        sb.append(" ficha.id as id_ficha,");
        sb.append(" coalesce(situacao.descricao,'') as situacao,");
        sb.append(" vinculo.iniciovigencia as admissao,");
        sb.append(" (select vc.codigo from contratovinculodecontrato cvc inner join vinculodecontratofp vc on vc.id = cvc.vinculodecontratofp_id");
        sb.append(" where cvc.contratofp_id = contrato.id  and trunc(cvc.iniciovigencia) = (select max(trunc(cvct.iniciovigencia)) from contratovinculodecontrato cvct ");
        sb.append(" where cvct.contratofp_id = contrato.id )  and rownum = 1) as vinculoempregaticio");
        sb.append(" from fichaFinanceiraFP ficha");
        sb.append(" inner join folhadePagamento folha on ficha.folhadepagamento_id =  folha.id");
        sb.append(" inner join vinculoFP vinculo on vinculo.id = ficha.vinculofp_id");
        sb.append(" inner join aposentadoria apo on apo.id = vinculo.id");
        sb.append(" inner join contratofp contrato on apo.contratofp_id = contrato.id");
        sb.append(" inner join situacaocontratofp situacaofp on situacaofp.contratofp_id = contrato.id ");
        sb.append(" inner join situacaofuncional situacao on situacaofp.situacaofuncional_id = situacao.id ");
        sb.append(" inner join VwHierarquiaAdministrativa vw on vw.subordinada_id = ficha.unidadeorganizacional_id ");
        sb.append(" inner join matriculaFP matricula on matricula.id = vinculo.matriculafp_id");
        sb.append(" inner join pessoafisica pf on pf.id = matricula.pessoa_id");
        sb.append(" inner join cargo on cargo.id = contrato.cargo_id");
        sb.append(" where trunc(folha.calculadaem) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(folha.calculadaem)) ");
        sb.append(" and (coalesce(trunc(folha.EFETIVADAEM), trunc(folha.calculadaem)) between trunc(situacaofp.INICIOVIGENCIA) and coalesce (trunc(situacaofp.finalvigencia), sysdate))");
        sb.append(concatenaParametros(parametros, 1, " and "));
        sb.append(montarCondicaoDasHierarquias(listaHierarquia));
        sb.append(" ");
        sb.append(" union all");
        //Pensionistas
        sb.append(" select");
        sb.append(" vw.codigo as codigoHierarquia, ");
        sb.append(" vw.descricao as descricaoHierarquia,");
        sb.append(" pf.nome,");
        sb.append(" formatacpfcnpj(pf.cpf) as cpf,  ");
        sb.append(" matricula.matricula,");
        sb.append(" vinculo.numero as contrato,");
        sb.append(" (trim(cargo.descricao) || ' - ' || (select cpcs.descricao || '/' || ppcs.descricao");
        sb.append(" from enquadramentofuncional enquadramentof");
        sb.append(" left join categoriapcs cpcs on cpcs.id = enquadramentof.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = enquadramentof.progressaopcs_id");
        sb.append(" where enquadramentof.contratoservidor_id = contrato.id and trunc(enquadramentof.inicioVigencia) = (select max(trunc(enq.iniciovigencia)) ");
        sb.append(" from EnquadramentoFuncional enq where enq.contratoservidor_id = contrato.id )  and rownum = 1 ))");
        sb.append(" as cargo,");
        sb.append(" ficha.id as id_ficha,");
        sb.append(" 'PENSIONISTA' as situacao,");
        sb.append(" vinculo.iniciovigencia as admissao,");
        sb.append(" (select vc.codigo from contratovinculodecontrato cvc inner join vinculodecontratofp vc on vc.id = cvc.vinculodecontratofp_id");
        sb.append(" where cvc.contratofp_id = contrato.id  and trunc(cvc.iniciovigencia) = (select max(trunc(cvct.iniciovigencia)) from contratovinculodecontrato cvct ");
        sb.append(" where cvct.contratofp_id = contrato.id )  and rownum = 1) as vinculoempregaticio");
        sb.append(" from fichaFinanceiraFP ficha");
        sb.append(" inner join folhadePagamento folha on ficha.folhadepagamento_id =  folha.id");
        sb.append(" inner join vinculoFP vinculo on vinculo.id = ficha.vinculofp_id");
        sb.append(" inner join Pensionista pen on pen.id = vinculo.id");
        sb.append(" inner join pensao p on p.id = pen.pensao_id");
        sb.append(" inner join contratofp contrato on p.contratofp_id = contrato.id");
        sb.append(" inner join VwHierarquiaAdministrativa vw on vw.subordinada_id = ficha.unidadeorganizacional_id ");
        sb.append(" inner join matriculaFP matricula on matricula.id = vinculo.matriculafp_id");
        sb.append(" inner join pessoafisica pf on pf.id = matricula.pessoa_id");
        sb.append(" left join cargo on cargo.id = contrato.cargo_id");
        sb.append(" where trunc(folha.calculadaem) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(folha.calculadaem)) ");
        sb.append(concatenaParametros(parametros, 1, " and "));
        sb.append(montarCondicaoDasHierarquias(listaHierarquia));
        sb.append(" union all");
        //Beneficiário Pensão Judicial Tramitada e Julgada
        sb.append(" select");
        sb.append(" vw.codigo as codigoHierarquia, ");
        sb.append(" vw.descricao as descricaoHierarquia,");
        sb.append(" pf.nome,");
        sb.append(" formatacpfcnpj(pf.cpf) as cpf,  ");
        sb.append(" matricula.matricula,");
        sb.append(" vinculo.numero as contrato,");
        sb.append(" ((select cpcs.descricao || '/' || ppcs.descricao");
        sb.append(" from enquadramentofuncional enquadramentof");
        sb.append(" left join categoriapcs cpcs on cpcs.id = enquadramentof.categoriapcs_id");
        sb.append(" left join progressaopcs ppcs on ppcs.id = enquadramentof.progressaopcs_id");
        sb.append(" where enquadramentof.contratoservidor_id = vinculo.id and trunc(enquadramentof.inicioVigencia) = (select max(trunc(enq.iniciovigencia)) ");
        sb.append(" from EnquadramentoFuncional enq where enq.contratoservidor_id = vinculo.id )  and rownum = 1))");
        sb.append(" as cargo,");
        sb.append(" ficha.id as id_ficha,");
        sb.append(" coalesce((select situacao.DESCRICAO from situacaocontratofp sitc inner join situacaofuncional situacao on situacao.id = sitc.situacaofuncional_id");
        sb.append(" where sitc.contratofp_id = vinculo.id  and trunc(sitc.iniciovigencia) = (select max(trunc(sit.iniciovigencia)) from situacaocontratofp sit where sit.contratofp_id = vinculo.id )  and rownum = 1),'-') as situacao,");
        sb.append(" vinculo.iniciovigencia as admissao,");
        sb.append(" (select vc.codigo from contratovinculodecontrato cvc inner join vinculodecontratofp vc on vc.id = cvc.vinculodecontratofp_id");
        sb.append(" where cvc.contratofp_id = vinculo.id  and trunc(cvc.iniciovigencia) = (select max(trunc(cvct.iniciovigencia)) from contratovinculodecontrato cvct ");
        sb.append(" where cvct.contratofp_id = vinculo.id )  and rownum = 1) as vinculoempregaticio");
        sb.append(" from fichaFinanceiraFP ficha");
        sb.append(" inner join folhadePagamento folha on ficha.folhadepagamento_id =  folha.id");
        sb.append(" inner join vinculoFP vinculo on vinculo.id = ficha.vinculofp_id");
        sb.append(" inner join beneficiario ben on ben.id = vinculo.id");
        sb.append(" inner join VwHierarquiaAdministrativa vw on vw.subordinada_id = ficha.unidadeorganizacional_id ");
        sb.append(" inner join matriculaFP matricula on matricula.id = vinculo.matriculafp_id");
        sb.append(" inner join pessoafisica pf on pf.id = matricula.pessoa_id");
        sb.append(" where trunc(folha.calculadaem) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(folha.calculadaem)) ");
        sb.append(concatenaParametros(parametros, 1, " and "));
        sb.append(montarCondicaoDasHierarquias(listaHierarquia));
        sb.append(" )reg ");
        sb.append(" order by codigoHierarquia, nome ");
        Query q = em.createNativeQuery(sb.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        List<Object[]> lista = q.getResultList();

        List<FolhaPorSecretariaPrincipal> retorno = new ArrayList<>();
        if (!lista.isEmpty()) {
            for (Object[] obj : lista) {
                FolhaPorSecretariaPrincipal lotacao = new FolhaPorSecretariaPrincipal();
                lotacao.setCodigoHierarquia((String) obj[0]);
                lotacao.setDescricaoHierarquia((String) obj[1]);
                lotacao.setNome((String) obj[2]);
                lotacao.setCpf((String) obj[3]);
                lotacao.setMatricula((String) obj[4]);
                lotacao.setContrato((String) obj[5]);
                lotacao.setCargo((String) obj[6]);
                lotacao.setIdFicha(((BigDecimal) obj[7]).longValue());
                lotacao.setSituacao((String) obj[8]);
                lotacao.setAdmissao((Date) obj[9]);
                lotacao.setVinculoEmpregaticio((String) obj[10]);
                List<TermoRescisaoVerba> verbas = recuperarVerbas(lotacao.getIdFicha());
                if (verbas != null && !verbas.isEmpty()) {
                    lotacao.setVerbas(verbas);
                    retorno.add(lotacao);
                }
            }
        }
        return retorno;
    }

    private String montarCondicaoDasHierarquias(List<HierarquiaOrganizacional> listaHierarquia) {
        String condicao = "";
        if (!listaHierarquia.isEmpty()) {
            condicao = " and (";
            String juncao = "";
            for (HierarquiaOrganizacional hierarquia : listaHierarquia) {
                condicao += juncao + "vw.codigo LIKE '" + hierarquia.getCodigoSemZerosFinais() + "%'";
                juncao = " or ";
            }
            condicao += ")";
        }
        return condicao;
    }

    private List<TermoRescisaoVerba> recuperarVerbas(Long fichaId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select distinct (select count(*) from dependentevinculofp dv ");
        sb.append(" join dependente d on dv.dependente_id = d.id ");
        sb.append(" join tipodependente tipo on dv.tipodependente_id = tipo.id ");
        sb.append(" where d.responsavel_id = pf.id ");
        sb.append(" and tipo.codigo in ('2','3','4','10') and trunc(folha.calculadaEm) between trunc(dv.iniciovigencia) and coalesce(trunc(dv.finalvigencia),trunc(folha.calculadaEm))) as dependentesir, ");
        sb.append(" (select count(*) from dependentevinculofp dv ");
        sb.append(" join dependente d on dv.dependente_id = d.id ");
        sb.append(" join tipodependente tipo on dv.tipodependente_id = tipo.id ");
        sb.append(" where d.responsavel_id = pf.id ");
        sb.append(" and tipo.codigo in ('1','9') and trunc(folha.calculadaEm) between trunc(dv.iniciovigencia) and coalesce(trunc(dv.finalvigencia),trunc(folha.calculadaEm)) ) as dependentessf, ");
        sb.append(" coalesce((select sum(iff.valorbasedecalculo) from itemfichafinanceirafp iff ");
        sb.append(" inner join eventofp ev on iff.eventofp_id = ev.id ");
        sb.append(" where ev.codigo = '904' ");
        sb.append(" and iff.fichafinanceirafp_id = :idFicha), 0) as basefgts, ");
        sb.append(" coalesce((select sum(iff.valorbasedecalculo) from itemfichafinanceirafp iff ");
        sb.append(" inner join eventofp ev on iff.eventofp_id = ev.id ");
        sb.append(" where ev.codigo = '901' ");
        sb.append(" and iff.fichafinanceirafp_id = :idFicha), 0) as baseirrf, ");
        sb.append(" (select coalesce(sum(coalesce(iff.valorbasedecalculo, 0)),0) from itemfichafinanceirafp iff ");
        sb.append(" inner join eventofp ev on iff.eventofp_id = ev.id ");
        sb.append(" where ev.codigo in ('891','898', '892', '895', '685', '900') ");
        sb.append(" and iff.fichafinanceirafp_id = :idFicha) as baseprev ");
        sb.append(" from FOLHADEPAGAMENTO folha  ");
        sb.append(" inner join FICHAFINANCEIRAFP ficha on ficha.FOLHADEPAGAMENTO_ID = folha.ID ");
        sb.append(" inner join VINCULOFP vinculo on vinculo.ID = ficha.VINCULOFP_ID ");
        sb.append(" inner join matriculaFP mat on mat.id = vinculo.matriculaFP_id ");
        sb.append(" inner join pessoaFisica pf on pf.id = mat.pessoa_id ");
        sb.append(" inner join ItemfichaFinanceiraFP item on item.fichafinanceiraFp_id = ficha.id ");
        sb.append(" inner join EventoFP evento on evento.id = item.eventofp_id ");
        sb.append(" where FICHA.ID = :idFicha ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idFicha", fichaId);
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        List<TermoRescisaoVerba> retorno = new ArrayList<>();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                TermoRescisaoVerba termo = new TermoRescisaoVerba();
                termo.setDependentesIR(new BigDecimal("" + obj[0]));
                termo.setDependentesSSF(new BigDecimal("" + obj[1]));
                termo.setBaseFGTS((BigDecimal) obj[2]);
                termo.setBaseIRRF((BigDecimal) obj[3]);
                termo.setBasePrev((BigDecimal) obj[4]);
                termo.setDescontos(recuperarVerbasPorTipoEventoFP(fichaId, TipoEventoFP.DESCONTO));
                termo.setVantagens(recuperarVerbasPorTipoEventoFP(fichaId, TipoEventoFP.VANTAGEM));
                retorno.add(termo);
            }
        }
        return retorno;
    }

    private boolean hasIncidenciaINSS(Long eventoID) {
        return contratoFPFacade.getEventoFPFacade().hasIncidenciaINSS(eventoID);
    }

    private boolean hasIncidenciaIRRF(Long eventoID) {
        return contratoFPFacade.getEventoFPFacade().hasIncidenciaIRRF(eventoID);
    }

    private boolean hasIncidenciaRPPS(Long eventoID) {
        return contratoFPFacade.getEventoFPFacade().hasIncidenciaRPPS(eventoID);
    }

    private List<TermoRescisaoVerbaDescontoEVantagem> recuperarVerbasPorTipoEventoFP(Long fichaId, TipoEventoFP tipoEventoFP) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select evento.codigo as codigo, ");
        sb.append(" 	   evento.descricao as descricao, ");
        sb.append("        item.valor as valor, ");
        sb.append(" 	   item.valorreferencia as referencia, ");
        sb.append(" 	   evento.id ");
        sb.append("   from itemfichafinanceirafp item ");
        sb.append("  inner join eventofp evento on evento.id = item.eventofp_id ");
        sb.append("  where item.tipoeventofp = :tipoEventoFP ");
        sb.append("    and item.fichafinanceirafp_id = :idFicha ");
        sb.append("  order by evento.codigo ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("idFicha", fichaId);
        q.setParameter("tipoEventoFP", tipoEventoFP.name());
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        List<TermoRescisaoVerbaDescontoEVantagem> retorno = new ArrayList<>();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                TermoRescisaoVerbaDescontoEVantagem termo = new TermoRescisaoVerbaDescontoEVantagem();
                termo.setCodigo((String) obj[0]);
                termo.setDescricao((String) obj[1]);
                termo.setValor((BigDecimal) obj[2]);
                termo.setReferencia((BigDecimal) obj[3]);
                termo.setInss(hasIncidenciaINSS(((BigDecimal) obj[4]).longValue()));
                termo.setIrrf(hasIncidenciaIRRF(((BigDecimal) obj[4]).longValue()));
                termo.setRpps(hasIncidenciaRPPS(((BigDecimal) obj[4]).longValue()));
                retorno.add(termo);
            }
        }
        return retorno;
    }

    public List<FolhaPorSecretariaSummary> recuperarSummary(List<ParametrosRelatorios> parametros, String codigoRecurso) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select evento.CODIGO, ");
        sb.append(" evento.DESCRICAO, ");
        sb.append(" case item.TIPOEVENTOFP  ");
        sb.append(" when 'VANTAGEM' then 'C' ");
        sb.append(" when 'DESCONTO' then 'D' ");
        sb.append(" when 'INFORMATIVO' then 'I' ");
        sb.append(" end as tiporetorno,  ");
        sb.append(" sum(coalesce(item.VALOR,0)) as valor, ");
        sb.append(" count(distinct(vinculo.ID)) as servidores ");
        sb.append(" from VINCULOFP vinculo ");
        sb.append(" inner join FICHAFINANCEIRAFP ficha on ficha.VINCULOFP_ID = vinculo.ID ");
        sb.append(" inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.ID ");
        sb.append(" inner join EVENTOFP evento on evento.ID = item.EVENTOFP_ID ");
        sb.append(" inner join folhadepagamento folha on folha.id = ficha.folhadepagamento_id ");
        sb.append(" inner join recursofp rec on rec.id = ficha.recursofp_id ");
        sb.append(" inner join agrupamentorecursofp ag on ag.recursofp_id = rec.id ");
        sb.append(" inner join gruporecursofp grupo on ag.gruporecursofp_id = grupo.id ");
        sb.append(" where  ficha.id is not null  ");
        sb.append(concatenaParametros(parametros, 1, " and "));
        sb.append(codigoRecurso != null ? " and rec.codigo like :codigoRecurso " : "");
        sb.append("  ");
        sb.append(" group by evento.CODIGO, evento.descricao, item.TIPOEVENTOFP ");
        sb.append(" order by case item.tipoEventoFP when 'VANTAGEM' then 'C' when 'DESCONTO' then 'D' else 'I' end, evento.codigo ");
        Query q = em.createNativeQuery(sb.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        if (codigoRecurso != null) {
            q.setParameter("codigoRecurso", codigoRecurso);
        }
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        List<FolhaPorSecretariaSummary> retorno = new ArrayList<>();
        if (!resultado.isEmpty()) {
            criarFolhaPorSecretariaSummary(resultado, retorno);
        }
        return retorno;
    }

    public List<FolhaPorSecretariaSummary> recuperarSummaryPorLotacaoFolha(List<ParametrosRelatorios> parametros, String codigo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select evento.CODIGO, ");
        sb.append(" evento.DESCRICAO, ");
        sb.append(" case item.TIPOEVENTOFP  ");
        sb.append(" when 'VANTAGEM' then 'C' ");
        sb.append(" when 'DESCONTO' then 'D' ");
        sb.append(" when 'INFORMATIVO' then 'I' ");
        sb.append(" end as tiporetorno,  ");
        sb.append(" sum(coalesce(item.VALOR,0)) as valor, ");
        sb.append(" count(distinct(vinculo.ID)) as servidores ");
        sb.append(" from VINCULOFP vinculo ");
        sb.append(" inner join FICHAFINANCEIRAFP ficha on ficha.VINCULOFP_ID = vinculo.ID ");
        sb.append(" inner join ITEMFICHAFINANCEIRAFP item on item.FICHAFINANCEIRAFP_ID = ficha.ID ");
        sb.append(" inner join EVENTOFP evento on evento.ID = item.EVENTOFP_ID ");
        sb.append(" inner join folhadepagamento folha on folha.id = ficha.folhadepagamento_id ");
        sb.append(" inner join VwHierarquiaAdministrativa vw on vw.subordinada_id = ficha.unidadeorganizacional_id ");
        sb.append(" where trunc(folha.calculadaem) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(folha.calculadaem)) ");
        sb.append(concatenaParametros(parametros, 1, " and "));
        sb.append(codigo != null ? " and vw.codigo like :vwCodigo " : " ");
        sb.append("  ");
        sb.append(" group by evento.CODIGO, evento.descricao, item.TIPOEVENTOFP ");
        sb.append(" order by case item.tipoEventoFP when 'VANTAGEM' then 'C' when 'DESCONTO' then 'D' else 'I' end, evento.codigo ");

        Query q = em.createNativeQuery(sb.toString());
        UtilRelatorioContabil.adicionarParametros(parametros, q);
        if (codigo != null) {
            q.setParameter("vwCodigo", codigo);
        }
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        List<FolhaPorSecretariaSummary> retorno = new ArrayList<>();
        if (!resultado.isEmpty()) {
            criarFolhaPorSecretariaSummary(resultado, retorno);
        }
        return retorno;
    }

    private void criarFolhaPorSecretariaSummary(List<Object[]> resultado, List<FolhaPorSecretariaSummary> retorno) {
        for (Object[] obj : resultado) {
            FolhaPorSecretariaSummary folha = new FolhaPorSecretariaSummary();
            folha.setCodigo((String) obj[0]);
            folha.setDescricao((String) obj[1]);
            folha.setTipoRetorno("" + obj[2]);
            folha.setValor((BigDecimal) obj[3]);
            folha.setQuantidadeServidores(new BigDecimal("" + obj[4]));
            EventoFP eventoFP = eventoFPFacade.recuperaPorCodigo(folha.getCodigo());
            if (eventoFP != null) {
                folha.setInss(Util.converterBooleanSimOuNao(eventoFPFacade.incidenciaINSS(eventoFP)));
                folha.setRpps(Util.converterBooleanSimOuNao(eventoFPFacade.incidenciaRPPS(eventoFP)));
                folha.setIrrf(Util.converterBooleanSimOuNao(eventoFPFacade.incidenciaIRRF(eventoFP)));
            }
            retorno.add(folha);
        }
    }

    public CompetenciaFP buscarUltimaCompetenciaTipoNormalEStatusAberta() {
        return competenciaFPFacade.recuperarUltimaCompetencia(StatusCompetencia.ABERTA, TipoFolhaDePagamento.NORMAL);
    }

    public String concatenaParametros(List<ParametrosRelatorios> parametros, Integer local, String clausula) {
        StringBuilder sql = new StringBuilder();
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == local) {
                sql.append(clausula + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        return sql.toString();
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public LoteProcessamentoFacade getLoteProcessamentoFacade() {
        return loteProcessamentoFacade;
    }

    public CompetenciaFPFacade getCompetenciaFPFacade() {
        return competenciaFPFacade;
    }

    public RecursoFPFacade getRecursoFPFacade() {
        return recursoFPFacade;
    }

    public GrupoRecursoFPFacade getGrupoRecursoFPFacade() {
        return grupoRecursoFPFacade;
    }
}
