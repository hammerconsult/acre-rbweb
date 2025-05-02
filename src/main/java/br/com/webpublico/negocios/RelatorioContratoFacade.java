package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FiscalExternoContrato;
import br.com.webpublico.entidades.FiscalInternoContrato;
import br.com.webpublico.entidadesauxiliares.RelatorioContrato;
import br.com.webpublico.entidadesauxiliares.RelatorioContratoAditivo;
import br.com.webpublico.entidadesauxiliares.RelatorioContratoExecucao;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.TipoAlteracaoContratual;
import br.com.webpublico.enums.TipoTermoAlteracaoContratual;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class RelatorioContratoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private RegistroSolicitacaoMaterialExternoFacade registroSolicitacaoMaterialExternoFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;

    public List<RelatorioContrato> buscarContratos(String filtros, String dataReferencia) {
        String sql = " select coalesce(lic.NUMEROLICITACAO, disp.NUMERODADISPENSA, reg.NUMEROREGISTRO, contVig.NUMEROLICITACAO) || '/' ||  exProcesso.ano as processo, " +
            "        cont.VALORTOTAL, " +
            "        vwadm.codigo || ' - ' || vwadm.descricao as administrativa, " +
            "           (select vworc.codigo || ' - ' || vworc.descricao " +
            "               from hierarquiaorgresp " +
            "               inner join vwhierarquiaadministrativa vwadm on vwadm.id = hierarquiaorgresp.hierarquiaorgadm_id " +
            "               inner join vwhierarquiaorcamentaria vworc on vworc.id = hierarquiaorgresp.hierarquiaorgorc_id " +
            "                     where to_date(:dataReferencia, 'dd/mm/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(trunc(vwadm.fimvigencia), to_date(:dataReferencia, 'dd/mm/yyyy')) " +
            "                       and to_date(:dataReferencia, 'dd/mm/yyyy') between trunc(vworc.iniciovigencia) and coalesce(trunc(vworc.fimvigencia), to_date(:dataReferencia, 'dd/mm/yyyy')) " +
            "                       and to_date(:dataReferencia, 'dd/mm/yyyy') between trunc(hierarquiaorgresp.datainicio) and coalesce(trunc(hierarquiaorgresp.datafim), to_date(:dataReferencia, 'dd/mm/yyyy')) " +
            "                       and vwadm.subordinada_id = uc.unidadeadministrativa_id " +
            "                       and rownum = 1 ) as orcamentaria, " +
            "        FORMATACPFCNPJ(coalesce(pf.cpf, pj.cnpj)) || ' - ' || coalesce(pf.nome, pj.razaosocial) as contratado, " +
            "        cont.INICIOVIGENCIA, " +
            "        cont.TERMINOVIGENCIA as terminoVigencia, " +
            "        coalesce(lic.MODALIDADELICITACAO, disp.MODALIDADE, reg.modalidadeCarona, contVig.modalidade) as modalidade, " +
            "        cont.OBJETO, " +
            "        cont.numerocontrato || ' - ' || cont.numerotermo ||'/'|| ex.ano || ' - ' ||cont.OBJETO AS CONTRATO, " +
            "        cont.numerocontrato || ' - ' || cont.numerotermo ||'/'|| ex.ano, " +
            "        mat.matricula || '/' || vinc.numero || ' - ' || prpf.nome  as responsavel, " +
            "        coalesce(lic.formadepagamento, disp.formadepagamento) as forma_pagamento,  " +
            "        cont.id as contratoId," +
            "        cont.valoratualcontrato, " +
            "        (select coalesce(sum(emp.valor),0) as valor_empenhado from execucaocontrato execucao " +
            "           inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id= execucao.id " +
            "           inner join empenho emp on emp.id = exemp.empenho_id " +
            "           where execucao.contrato_id = cont.id)  " +
            " from contrato cont " +
            "   inner join unidadecontrato uc on uc.contrato_id = cont.id " +
            "   inner join vwhierarquiaadministrativa vwadm on uc.unidadeadministrativa_id = vwadm.subordinada_id " +
            "   inner join exercicio ex on ex.id = cont.EXERCICIOCONTRATO_ID " +
            "   left join contratofp cfp on cfp .id  = cont.responsavelprefeitura_id " +
            "   left join vinculofp vinc on vinc.id = cfp.id " +
            "   left join matriculafp mat on mat.id = vinc.matriculafp_id " +
            "   left join pessoa presp on presp.id = mat.pessoa_id " +
            "   left join pessoafisica prpf on prpf.id = coalesce(presp.id, cont.responsavelprefeiturapf_id) " +
            "   left join contratosvigente contVig on contVig.contrato_id = cont.id " +
            "   left join conlicitacao conlic on conlic.contrato_id = cont.id " +
            "   left join licitacao lic on lic.id = conlic.licitacao_id " +
            "   left join CONREGPRECOEXTERNO conreg on conreg.CONTRATO_ID = cont.id " +
            "   left join REGISTROSOLMATEXT reg on reg.id = conreg.REGSOLMATEXT_ID " +
            "   left join CONDISPENSALICITACAO condisp on condisp.CONTRATO_ID = cont.id " +
            "   left join DISPENSADELICITACAO disp on condisp.DISPENSADELICITACAO_ID = disp.id " +
            "   left join exercicio exProcesso on exprocesso.id = coalesce(lic.EXERCICIO_ID, disp.EXERCICIODADISPENSA_ID, reg.EXERCICIOREGISTRO_ID, contVig.EXERCICIOLICITACAO_ID) " +
            "   inner join pessoa p on cont.CONTRATADO_ID = p.id " +
            "   left join pessoafisica pf on p.id = pf.id " +
            "   left join pessoajuridica pj on p.id = pj.id " +
            "  where to_date(:dataReferencia, 'dd/mm/yyyy') between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, to_date(:dataReferencia, 'dd/mm/yyyy')) " +
            "  and to_date(:dataReferencia, 'dd/mm/yyyy') between uc.INICIOVIGENCIA and coalesce(uc.FIMVIGENCIA, to_date(:dataReferencia, 'dd/mm/yyyy')) " +
            filtros +
            "  order by to_number(cont.numerocontrato) desc, to_number(cont.numerotermo) desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataReferencia", dataReferencia);
        List<Object[]> resultado = q.getResultList();
        List<RelatorioContrato> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                RelatorioContrato rel = new RelatorioContrato();
                rel.setProcesso((String) obj[0]);
                rel.setValorContrato((BigDecimal) obj[1]);
                rel.setUnidadeAdministrativa((String) obj[2]);
                rel.setUnidadeOrcamentaria((String) obj[3]);
                rel.setFornecedor((String) obj[4]);
                rel.setInicio((Date) obj[5]);
                rel.setTermino((Date) obj[6]);
                rel.setModalidade(ModalidadeLicitacao.valueOf((String) obj[7]).getDescricao());
                rel.setObjeto((String) obj[8]);
                rel.setContrato((String) obj[9]);
                rel.setNumeroContrato((String) obj[10]);
                rel.setResponsavelUnidade((String) obj[11]);
                rel.setFormaPagamento(obj[12] != null ? (String) obj[12] : "");
                rel.setIdContrato(((BigDecimal) obj[13]).longValue());
                rel.setValorAtualContrato((BigDecimal) obj[14]);
                rel.setValorEmpenhado((BigDecimal) obj[15]);
                rel.setFiscalContrato(buscarFiscaisContrato(rel.getIdContrato()));
                rel.setExecucoes(buscarExecucoes(rel.getIdContrato()));
                rel.setAditivos(buscarAditivos(rel.getIdContrato()));
                retorno.add(rel);
            }
        }
        return retorno;
    }

    private String buscarFiscaisContrato(Long idContrato) {
        String sqlExt = " select fc.*, fcext.* from fiscalcontrato fc " +
            "           inner join fiscalexternocontrato fcext on fcext.id = fc.id " +
            "           where fc.contrato_id = :idContrato ";
        Query qExt = em.createNativeQuery(sqlExt, FiscalExternoContrato.class);
        qExt.setParameter("idContrato", idContrato);
        List<FiscalExternoContrato> fiscaisExterno = qExt.getResultList();

        String sqlInt = " select fc.*, fcint.* from fiscalcontrato fc " +
            "           inner join fiscalinternocontrato fcint on fcint.id = fc.id " +
            "           where fc.contrato_id = :idContrato ";
        Query qInt = em.createNativeQuery(sqlInt, FiscalInternoContrato.class);
        qInt.setParameter("idContrato", idContrato);
        List<FiscalInternoContrato> fiscaisInterno = qInt.getResultList();
        String descricao = "";
        for (FiscalExternoContrato fiscalExterno : fiscaisExterno) {
            descricao += fiscalExterno.getContratoFiscal() + ", ";
        }
        for (FiscalInternoContrato fiscal : fiscaisInterno) {
            descricao += fiscal.getServidor() != null ? fiscal.getServidor().getMatriculaFP().getPessoa() : fiscal.getServidorPF() + ", ";
        }
        return descricao.length() > 1 ? descricao.substring(0, descricao.length() - 2) : descricao;
    }

    private List<RelatorioContratoExecucao> buscarExecucoes(Long idContrato) {
        String sql = " select desp.CODIGO || ' - ' || conta.codigo || ' / ' || fr.codigo || ' - ' || fr.descricao as fonteDespesa, " +
            "        exec.NUMERO as numeroExecucao, " +
            "        coalesce(SUM(emp.valor), 0) as valorEmpenhado, " +
            "        exec.VALOR as valorTotal, " +
            "        execFont.VALOR as valor, " +
            "        sum(coalesce((select sum(est.valor) from empenhoestorno est where est.empenho_id = emp.id), 0)) as estorno, " +
            "        sum((select coalesce(sum(est.valor), 0) as total " +
            "                 from empenhoestorno est " +
            "                   inner join empenho resto on resto.id = est.empenho_id " +
            "                   inner join empenho original on original.id = resto.empenho_id " +
            "                 where original.id = emp.id)) as cancelamentoDeResto," +
            "        exec.id " +
            "   from execucaoContrato exec " +
            "  inner join execucaocontratoempenho execemp on exec.id = execemp.EXECUCAOCONTRATO_ID " +
            "  inner join EXECUCAOCONTRATOTIPO exectipo on exectipo.EXECUCAOCONTRATO_ID = exec.id " +
            "  inner join execucaocontratotipofonte execfont on exectipo.id = execfont.EXECUCAOCONTRATOTIPO_ID " +
            "   left join empenho emp on execemp.EMPENHO_ID = emp.id and emp.FONTEDESPESAORC_ID = execfont.FONTEDESPESAORC_ID " +
            "  inner join fontedespesaorc fontDesp on execfont.FONTEDESPESAORC_ID = fontdesp.id " +
            "  inner join provisaoppafonte provFont on fontDesp.PROVISAOPPAFONTE_ID = provFont.id " +
            "  inner join CONTADEDESTINACAO cd on provFont.DESTINACAODERECURSOS_ID = cd.id " +
            "  inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_ID = fr.id " +
            "  inner join despesaorc desp on fontDesp.DESPESAORC_ID = desp.id " +
            "  inner join provisaoppadespesa provDesp on desp.PROVISAOPPADESPESA_ID = provDesp.id " +
            "  inner join conta conta on provDesp.CONTADEDESPESA_ID = conta.id " +
            "  where exec.CONTRATO_ID = :idContrato " +
            "  group by desp.CODIGO, " +
            "           conta.codigo, " +
            "           fr.codigo, " +
            "           fr.descricao, " +
            "           exec.VALOR, " +
            "           exec.numero, " +
            "           execFont.VALOR," +
            "           exec.id  " +
            "  order by exec.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", idContrato);
        List<Object[]> resultado = q.getResultList();
        List<RelatorioContratoExecucao> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                RelatorioContratoExecucao rel = new RelatorioContratoExecucao();
                rel.setFonteDespesa((String) obj[0]);
                rel.setNumero(((BigDecimal) obj[1]).toString());
                rel.setValorTotal((BigDecimal) obj[3]);
                rel.setValor((BigDecimal) obj[4]);
                rel.setEstornoEmpenho((BigDecimal) obj[5]);
                rel.setCancelamentoRestosAPagar((BigDecimal) obj[6]);
                rel.setValorEmpenhado(((BigDecimal) obj[2])
                    .subtract(rel.getEstornoEmpenho())
                    .subtract(rel.getCancelamentoRestosAPagar()));
                rel.setValorAEmpenhar(rel.getValorTotal().subtract(rel.getValorEmpenhado()));
                rel.setIdExecucao(((BigDecimal) obj[7]).longValue());
                retorno.add(rel);
            }
        }
        return retorno;
    }

    public List<RelatorioContratoAditivo> buscarAditivos(Long idContrato) {
        String sql = " " +
            "   select " +
            "     ad.datalancamento, " +
            "     ad.numero || ' - ' || ad.numerotermo || '/' || ex.ano || ' - ' || ad.justificativa as aditivo, " +
            "     ad.tipotermo," +
            "     ad.id " +
            "   from alteracaocontratual ad " +
            "     inner join exercicio ex on ex.id = ad.exercicio_id " +
            "    where ad.contrato_id = :idContrato " +
            "    and ad.tipoalteracaocontratual = :tipoAditivo " +
            "   order by ad.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", idContrato);
        q.setParameter("tipoAditivo", TipoAlteracaoContratual.ADITIVO.name());
        List<RelatorioContratoAditivo> toReturn = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                RelatorioContratoAditivo item = new RelatorioContratoAditivo();
                item.setDataLancamento((Date) obj[0]);
                item.setAditivo((String) obj[1]);
                item.setTipo(TipoTermoAlteracaoContratual.valueOf((String) obj[2]).getDescricao());
                item.setIdAditivo(((BigDecimal) obj[3]).longValue());
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public RegistroSolicitacaoMaterialExternoFacade getRegistroSolicitacaoMaterialExternoFacade() {
        return registroSolicitacaoMaterialExternoFacade;
    }

    public DispensaDeLicitacaoFacade getDispensaDeLicitacaoFacade() {
        return dispensaDeLicitacaoFacade;
    }
}
