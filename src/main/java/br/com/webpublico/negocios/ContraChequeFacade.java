package br.com.webpublico.negocios;

import br.com.webpublico.BarCode;
import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.rh.ContraChequeItensVO;
import br.com.webpublico.entidadesauxiliares.rh.ContraChequeVO;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.AssistenteContraCheque;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.util.GeradorChaveAutenticacao;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.model.WSFichaFinanceira;
import br.com.webpublico.ws.model.WSVinculoFP;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by tharlyson on 17/01/20.
 */
@Stateless
public class ContraChequeFacade {

    private static final Logger logger = Logger.getLogger(ContraChequeFacade.class);
    private final String complementoURL = "autenticidade-de-documentos/";

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;


    public ContraChequeVO buscarContraCheque(AssistenteContraCheque assistente) {

        String sql = montarQuery(assistente);
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoVantagem", TipoEventoFP.VANTAGEM.name());
        q.setParameter("tipoDesconto", TipoEventoFP.DESCONTO.name());

        ContraChequeVO item = new ContraChequeVO();
        List<Object[]> resultado = q.getResultList();

        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                item.setNome((String) obj[0]);
                item.setMatricula((String) obj[1]);
                item.setContrato((String) obj[2]);
                item.setCpf((String) obj[3]);
                item.setDataNascimento((Date) obj[4]);
                item.setIdFicha(((BigDecimal) obj[5]).longValue());
                item.setModalidadeContrato((String) obj[6]);
                item.setReferencia((String) obj[7]);
                item.setInicioVigencia((Date) obj[8]);
                item.setLotacaoFolha((String) obj[9]);
                item.setDependenteIR(((BigDecimal) obj[10]).intValue());
                item.setDependenteSalarioFamilia(((BigDecimal) obj[11]).intValue());
                item.setCargo((String) obj[12]);
                item.setDataAdmissao((Date) obj[13]);
                item.setTipoFolha(TipoFolhaDePagamento.valueOf((String) obj[14]).getDescricao());
                item.setMesAno(Mes.getMesToInt(((BigDecimal) obj[15]).intValue()).getDescricao().substring(0, 3) + "/" + ((BigDecimal) obj[16]).intValue());
                item.setTotalBruto((BigDecimal) obj[17]);
                item.setTotalDesconto((BigDecimal) obj[18]);
                item.setTotalLiquido((BigDecimal) obj[19]);
                item.setBaseFGTS((BigDecimal) obj[20]);
                item.setBaseIRRF((BigDecimal) obj[21]);
                item.setBasePREV((BigDecimal) obj[22]);
                item.setUnidadeOrganizacional((String) obj[23]);
                item.setBanco(obj[24] != null ? (String) obj[24] : "");
                item.setAgencia(obj[25] != null ? (String) obj[25] : "");
                item.setContaBancaria(obj[26] != null ? (String) obj[26] : "");
                item.setIdVinculo(((BigDecimal) obj[27]).longValue());
                item.setAutenticidade(obj[28] != null ? GeradorChaveAutenticacao.formataChaveDeAutenticacao((String) obj[28]) : null);
                validarChaveAutenticidadeFicha(item);
                gerarQrCodeContraCheque(item);
                item.getContraChequeItens().addAll(recuperarItens(assistente));
            }
        }
        return item;
    }

    public void validarChaveAutenticidadeFicha(List<FichaFinanceiraFP> fichas) {
        for (FichaFinanceiraFP ficha : fichas) {
            if (Strings.isNullOrEmpty(ficha.getAutenticidade())) {
                gerarAutenticidade(ficha.getId());
            }
        }
    }

    private void gerarAutenticidade(Long id) {
        FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.buscarFichaFinanceiraPorId(id);
        VinculoFP vinculo = ficha.getVinculoFP();
        if (vinculo != null) {
            String assinatura = Util.dateToString(new Date()) + ficha.getId() + vinculo.getNumero() + StringUtil.retornaApenasNumeros(vinculo.getMatriculaFP().getMatricula());
            String chaveDeAutenticacao = GeradorChaveAutenticacao.geraChaveDeAutenticacao(assinatura, GeradorChaveAutenticacao.TipoAutenticacao.CONTRA_CHEQUE);

            if (Strings.isNullOrEmpty(ficha.getAutenticidade())) {
                ficha.setAutenticidade(chaveDeAutenticacao);
            }
        }
    }

    private void validarChaveAutenticidadeFicha(ContraChequeVO itemContracheque) {
        if (Strings.isNullOrEmpty(itemContracheque.getAutenticidade())) {
            itemContracheque.setAutenticidade(GeradorChaveAutenticacao.formataChaveDeAutenticacao(gerarAutenticidade(itemContracheque)));
        }
    }

    private String gerarAutenticidade(ContraChequeVO itemContraCheque) {

        FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.buscarFichaFinanceiraPorId(itemContraCheque.getIdFicha());
        VinculoFP vinculo = ficha.getVinculoFP();
        if (vinculo != null) {
            String assinatura = Util.dateToString(new Date()) + itemContraCheque.getIdFicha() + vinculo.getNumero() + StringUtil.retornaApenasNumeros(vinculo.getMatriculaFP().getMatricula());
            String chaveDeAutenticacao = GeradorChaveAutenticacao.geraChaveDeAutenticacao(assinatura, GeradorChaveAutenticacao.TipoAutenticacao.CONTRA_CHEQUE);

            if (Strings.isNullOrEmpty(ficha.getAutenticidade())) {
                ficha.setAutenticidade(chaveDeAutenticacao);
                return ficha.getAutenticidade();
            }
        }
        return "";
    }

    private void gerarQrCodeContraCheque(ContraChequeVO contraChequeVO) {
        String urlPortal = atribuirUrlPortal();
        contraChequeVO.setQrCode(Util.generateQRCodeImage(urlPortal + "/autenticidade-de-documentos/contra-cheque/" + contraChequeVO.getIdFicha() + "/", 350, 350));
    }

    public String getQrCodeContraCheque(Long id) {
        String urlPortal = atribuirUrlPortal();
        try {
            return BarCode.generateBase64QRCodePng(urlPortal + "/autenticidade-de-documentos/contra-cheque/" + id + "/", 350, 350);
        } catch (IOException e) {
            logger.error("Erro ao gerar gr code em Contracheque: ", e);
        }
        return "NÃO FOI POSSIVEL GERAR O QR PARA O VINCULO";
    }

    public String atribuirUrlPortal() {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        String urlPortal = "";
        if (configuracaoTributario != null && !Strings.isNullOrEmpty(configuracaoTributario.getUrlPortalContribuinte()) &&
            configuracaoTributario.getUrlPortalContribuinte().endsWith("/")) {
            urlPortal = StringUtils.chop(configuracaoTributario.getUrlPortalContribuinte());
        }
        return urlPortal;
    }

    private String montarQuery(AssistenteContraCheque assistente) {
        StringBuilder sql = new StringBuilder();

        sql.append("select pf.nome, ")
            .append("      matricula.matricula as matricula, ")
            .append("      matricula.matricula || '/' || vinculo.numero as contrato, ")
            .append("      pf.cpf, ")
            .append("      pf.datanascimento, ")
            .append("      ficha.id as idFicha, ")
            .append("      modalidade.descricao as modalidadecontrato, ")
            .append("       (select unique progressao.descricao from enquadramentofuncional enquadramento")
            .append("           inner join progressaopcs progressao on progressao.id = enquadramento.progressaopcs_id ")
            .append("           inner join categoriapcs categoria on categoria.id = enquadramento.categoriapcs_id ")
            .append("           inner join planocargossalarios pcs on pcs.id = categoria.planocargossalarios_id ")
            .append("               where enquadramento.contratoservidor_id = contrato.id ")
            .append("                  and folha.calculadaem  between  enquadramento.iniciovigencia and coalesce(enquadramento.finalvigencia, current_date)) as referencia,")
            .append("                  vinculo.inicioVigencia, ")
            .append("                  recurso.codigo || ' - ' || recurso.descricao as lotacao_folha, ")
            .append("       (select count(*) from dependentevinculofp dv ")
            .append("           join dependente d on dv.dependente_id = d.id")
            .append("           join tipodependente tipo on dv.tipodependente_id = tipo.id ")
            .append("               where d.responsavel_id = pf.id ")
            .append("                  and tipo.codigo in ('2', '3', '4', '10') ")
            .append("                  and to_date(to_char(folha.calculadaem, 'mm/yyyy'), 'mm/yyyy') between ")
            .append("                  to_date(to_char(dv.iniciovigencia, 'mm/yyyy'), 'mm/yyyy') ")
            .append("                  and to_date(to_char(coalesce(dv.finalvigencia, folha.calculadaem), 'mm/yyyy'), 'mm/yyyy')) as dep_ir, ")
            .append("       (select count(*) from dependentevinculofp dv")
            .append("           join dependente d on dv.dependente_id = d.id ")
            .append("           join tipodependente tipo on dv.tipodependente_id = tipo.id ")
            .append("               where d.responsavel_id = pf.id ")
            .append("                  and tipo.codigo in ('1', '9') ")
            .append("                  and to_date(to_char(folha.calculadaem, 'mm/yyyy'), 'mm/yyyy') between ")
            .append("                  to_date(to_char(dv.iniciovigencia, 'mm/yyyy'), 'mm/yyyy') ")
            .append("                  and to_date(to_char(coalesce(dv.finalvigencia, folha.calculadaem), 'mm/yyyy'), 'mm/yyyy')) as dep_sal_fam, ")
            .append("    case when (select apo.id from aposentadoria apo where apo.id = vinculo.id) is not null then 'APOSENTADO(A) ' || to_char(trunc(vinculo.iniciovigencia), 'dd/MM/yyyy')")
            .append("         else cargo.codigodocargo || ' - ' || cargo.descricao end as cargo, ")
            .append("    case when (select apo.id from aposentadoria apo where apo.id = vinculo.id) ")
            .append("                         is not null then trunc(contratoantigo.dataadmissao) ")
            .append("                                     else trunc(vinculo.iniciovigencia) end as admissao, ")
            .append("      folha.tipofolhadepagamento as tipofolha,")
            .append("      folha.mes + 1 as mes, folha.ano as ano, ")
            .append("      (select sum(item1.valor) ")
            .append("           from itemfichafinanceirafp item1 inner join eventofp ev on ev.id = item1.eventofp_id ")
            .append("           where item1.tipoeventofp = :tipoVantagem and item1.fichafinanceirafp_id = ficha.id) as totalbrutos, ")
            .append("      (select sum(item2.valor) ")
            .append("           from itemfichafinanceirafp item2 inner join eventofp ev1 on ev1.id = item2.eventofp_id ")
            .append("           where item2.tipoeventofp = :tipoDesconto and item2.fichafinanceirafp_id = ficha.id) as totaldesconto, ")
            .append("      ((select sum(item1.valor) ")
            .append("           from itemfichafinanceirafp item1 inner join eventofp ev on ev.id = item1.eventofp_id ")
            .append("           where item1.tipoeventofp = :tipoVantagem and item1.fichafinanceirafp_id = ficha.id) - ")
            .append("      (select sum(item1.valor) ")
            .append("           from itemfichafinanceirafp item1 inner join eventofp ev on ev.id = item1.eventofp_id ")
            .append("           where item1.tipoeventofp = :tipoDesconto and item1.fichafinanceirafp_id = ficha.id)) as totalliquido, ")
            .append("      coalesce((select sum(iff.valorbasedecalculo) ")
            .append("           from itemfichafinanceirafp iff ")
            .append("           inner join eventofp ev on iff.eventofp_id = ev.id ")
            .append("           where ev.codigo = '904' and iff.fichafinanceirafp_id = ficha.id), 0) as basefgts,")
            .append("      coalesce((select sum(iff.valorbasedecalculo) ")
            .append("           from itemfichafinanceirafp iff ")
            .append("           inner join eventofp ev on iff.eventofp_id = ev.id ")
            .append("           where ev.codigo = '901' and iff.fichafinanceirafp_id = ficha.id), 0) as baseirrf, ")
            .append("      (select coalesce(sum(coalesce(iff.valorbasedecalculo, 0)), 0) ")
            .append("           from itemfichafinanceirafp iff ")
            .append("           inner join eventofp ev on iff.eventofp_id = ev.id ")
            .append("           where ev.codigo in ('891','898','892','895','685','900','899') and iff.fichafinanceirafp_id = ficha.id) as baseprev, ")
            .append("       vw.descricao as orgaoOrc, ")
            .append("       banco.descricao as banco, ")
            .append("       agencia.numeroagencia || '-' || agencia.digitoverificador as agencia, ")
            .append("       conta.numeroconta || '-' || conta.digitoverificador as conta,  ")
            .append("       vinculo.id as idVinculo, ")
            .append("       ficha.autenticidade as autenticidade ")
            .append("    from vinculofp vinculo ")
            .append("        inner join matriculafp matricula on matricula.id = vinculo.matriculafp_id ")
            .append("        inner join pessoafisica pf on matricula.pessoa_id = pf.id ")
            .append("        inner join fichafinanceirafp ficha on ficha.vinculofp_id = vinculo.id ")
            .append("        inner join folhadepagamento folha on folha.id = ficha.folhadepagamento_id ")
            .append("        inner join unidadeorganizacional unid on unid.id = ficha.unidadeorganizacional_id ")
            .append("        inner join recursofp recurso on recurso.id = ficha.recursofp_id ")
            .append("        left join vwhierarquiaadministrativa vw on vw.subordinada_id = unid.id and folha.calculadaem between vw.iniciovigencia and coalesce(vw.fimvigencia, folha.calculadaem)")
            .append("         left join contratofp contrato on vinculo.id = contrato.id ")
            .append("         left join modalidadecontratofp modalidade on modalidade.id = contrato.modalidadecontratofp_id ")
            .append("         left join cargo cargo on cargo.id = contrato.cargo_id ")
            .append("         left join contacorrentebancaria conta on vinculo.contacorrente_id = conta.id ")
            .append("         left join agencia agencia on conta.agencia_id = agencia.id ")
            .append("         left join banco banco on agencia.banco_id = banco.id")
            .append("         left join aposentadoria apo on apo.id = vinculo.id ")
            .append("             left join contratofp contratoantigo on contratoantigo.id = apo.contratofp_id ")
            .append(assistente.getCondicoes());
        return sql.toString();
    }

    private List<ContraChequeItensVO> recuperarItens(AssistenteContraCheque assistente) {
        String sql = "select evento.codigo, " +
            "         evento.descricao, " +
            "         item.valorreferencia as referencia, " +
            "         item.valor, " +
            "         item.tipoeventofp," +
            "         item.mes || '/' || item.ano as competencia " +
            "      from itemfichafinanceirafp item " +
            "         inner join fichafinanceirafp ficha on item.fichafinanceirafp_id = ficha.id " +
            "         inner join folhadepagamento folha on ficha.folhadepagamento_id = folha.id " +
            "         inner join eventofp evento on evento.id = item.eventofp_id " +
            "         inner join recursofp rec on rec.id = ficha.recursofp_id " +
            "      where (item.tipoeventofp = :tipoVantagem or item.tipoeventofp = :tipoDesconto) " +
            assistente.getCondicoes().replace("where", "and") +
            "order by evento.codigo, item.tipoeventofp ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoVantagem", TipoEventoFP.VANTAGEM.name());
        q.setParameter("tipoDesconto", TipoEventoFP.DESCONTO.name());

        List<Object[]> resultados = q.getResultList();
        List<ContraChequeItensVO> retorno = Lists.newArrayList();

        if (!resultados.isEmpty()) {
            for (Object[] obj : resultados) {
                ContraChequeItensVO item = new ContraChequeItensVO();
                item.setCodigo((String) obj[0]);
                item.setDescricao((String) obj[1]);
                item.setReferencia((BigDecimal) obj[2]);
                item.setValor(obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
                item.setTipoEventoFP(TipoEventoFP.valueOf((String) obj[4]).name());
                item.setCompetencia((String) obj[5]);
                retorno.add(item);
            }
        }
        return retorno;
    }

    public WSFichaFinanceira buscarContraChequePorFichaId(Long idFicha) {

        String sql = "select ficha.* from fichafinanceirafp ficha " +
            "where ficha.id = :idFicha";

        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);
        q.setParameter("idFicha", idFicha);
        List<FichaFinanceiraFP> fichasFinanceiras = q.getResultList();
        WSFichaFinanceira wsFicha = retornarFichaFinanceira(fichasFinanceiras);
        return wsFicha;
    }

    public WSFichaFinanceira buscarContraChequePorAutenticidade(String autenticidade) {

        String sql = "select ficha.* from fichafinanceirafp ficha " +
            "where ficha.autenticidade = :autenticidade";

        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);

        q.setParameter("autenticidade", StringUtil.removeCaracteresEspeciaisSemEspaco(autenticidade));
        List<FichaFinanceiraFP> fichasFinanceiras = q.getResultList();
        WSFichaFinanceira wsFicha = retornarFichaFinanceira(fichasFinanceiras);
        return wsFicha;
    }

    private WSFichaFinanceira retornarFichaFinanceira(List<FichaFinanceiraFP> fichasFinanceiras) {
        FichaFinanceiraFP ficha = null;
        if (fichasFinanceiras != null && !fichasFinanceiras.isEmpty()) {
            ficha = fichasFinanceiras.get(0);
        }
        WSFichaFinanceira wsFicha = null;
        if (ficha != null) {
            wsFicha = new WSFichaFinanceira();
            wsFicha.setId(ficha.getId());
            wsFicha.setWsVinculoFP(new WSVinculoFP());
            wsFicha.getWsVinculoFP().setMatricula(ficha.getVinculoFP() != null ? ficha.getVinculoFP().getMatriculaFP().getMatriculaComCPF() : "");
        }
        return wsFicha;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }
}
