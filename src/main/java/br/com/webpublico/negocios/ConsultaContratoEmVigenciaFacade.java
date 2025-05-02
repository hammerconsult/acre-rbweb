package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AgrupadorGOC;
import br.com.webpublico.entidadesauxiliares.AgrupadorContratoEmVigencia;
import br.com.webpublico.entidadesauxiliares.AssistenteAgrupadorContratoEmVigencia;
import br.com.webpublico.entidadesauxiliares.ContratoEmVigencia;
import br.com.webpublico.entidadesauxiliares.ItemContratoEmVigencia;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ConsultaContratoEmVigenciaFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private AgrupadorGOCFacade agrupadorGOCFacade;

    public AssistenteAgrupadorContratoEmVigencia buscarContratos(String condicao, String dataReferencia) {
        String sql = " " +
            "   select distinct" +
            "      cont.id as contratoId," +
            "      cont.numerocontrato as numero_contrato, " +
            "      cont.numerotermo ||'/'|| ex.ano as numero_termo, " +
            "      ex.ano as exercicio, " +
            "      FORMATACPFCNPJ(coalesce(pf.cpf, pj.cnpj)) || ' - ' || coalesce(pf.nome, pj.razaosocial) as contratado, " +
            "      vwadm.codigo || ' - ' || vwadm.descricao as administrativa, " +
            "      cont.objeto as objeto, " +
            "      cont.valortotal," +
            "     coalesce(lic.NUMEROLICITACAO, disp.NUMERODADISPENSA, reg.NUMEROREGISTRO, contVig.NUMEROLICITACAO) || '/' ||  exProcesso.ano as processo, " +
            "     coalesce(lic.id, disp.id, reg.id, cont.id) as id_processo " +
            "    from contrato cont " +
            "    inner join itemcontrato ic on ic.contrato_id = cont.id " +
            getJoinsItemContratoAdequado() +
            "       inner join exercicio ex on ex.id = cont.exerciciocontrato_id " +
            "       inner join unidadecontrato uc on uc.contrato_id = cont.id " +
            "       inner join vwhierarquiaadministrativa vwadm on uc.unidadeadministrativa_id = vwadm.subordinada_id " +
            "       inner join pessoa p on cont.contratado_id = p.id " +
            "       left join pessoafisica pf on p.id = pf.id " +
            "       left join pessoajuridica pj on p.id = pj.id " +
            "       left join conlicitacao conlic ON conlic.contrato_id = cont.id " +
            "       left join licitacao lic on lic.id = conlic.licitacao_id " +
            "       left join CONDISPENSALICITACAO condisp on condisp.CONTRATO_ID = cont.id " +
            "       left join DISPENSADELICITACAO disp on condisp.DISPENSADELICITACAO_ID = disp.id " +
            "       left join CONREGPRECOEXTERNO conreg on conreg.CONTRATO_ID = cont.id " +
            "       left join REGISTROSOLMATEXT reg on reg.id = conreg.REGSOLMATEXT_ID " +
            "       left join contratosvigente contVig on contVig.contrato_id = cont.id " +
            "       left join exercicio exProcesso on exprocesso.id = coalesce(lic.EXERCICIO_ID, disp.EXERCICIODADISPENSA_ID, reg.EXERCICIOREGISTRO_ID, contVig.EXERCICIOLICITACAO_ID) " +
            "  where to_date(:dataReferencia, 'dd/mm/yyyy') between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, to_date(:dataReferencia, 'dd/mm/yyyy')) " +
            "  and to_date(:dataReferencia, 'dd/mm/yyyy') between uc.INICIOVIGENCIA and coalesce(uc.FIMVIGENCIA, to_date(:dataReferencia, 'dd/mm/yyyy')) " +
            "  and to_date(:dataReferencia, 'dd/mm/yyyy') between cont.INICIOVIGENCIA and coalesce(cont.TERMINOVIGENCIAATUAL, to_date(:dataReferencia, 'dd/mm/yyyy')) " +
            condicao;
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataReferencia", dataReferencia);
        List<Object[]> resultado = q.getResultList();

        AssistenteAgrupadorContratoEmVigencia novoAssistente = new AssistenteAgrupadorContratoEmVigencia();
        List<ContratoEmVigencia> contratos = preencherObjetosConsultaContrato(resultado);
        novoAssistente.setContratos(contratos);

        List<AgrupadorContratoEmVigencia> agrupadores = Lists.newArrayList();
        Map<AgrupadorGOC, AgrupadorContratoEmVigencia> map = new HashMap<>();

        for (ContratoEmVigencia contratoConsulta : contratos) {
            List<AgrupadorGOC> agrupadoresGOC = buscarAgrupadoresPorGrupoObjetoCompra(contratoConsulta.getItens());

            for (AgrupadorGOC agrupadorGOC : agrupadoresGOC) {
                if (!map.containsKey(agrupadorGOC)) {
                    AgrupadorContratoEmVigencia novoAgrupador = new AgrupadorContratoEmVigencia();
                    novoAgrupador.setAgrupadorGOC(agrupadorGOC);
                    ContratoEmVigencia contratoEmVigencia = novoContratoEmVigencia(contratoConsulta);

                    distribuirItensContratoPorGrupoObjetoCompra(contratoConsulta, agrupadorGOC, contratoEmVigencia);
                    novoAgrupador.getContratos().add(contratoEmVigencia);
                    agrupadores.add(novoAgrupador);
                    map.put(agrupadorGOC, novoAgrupador);
                } else {
                    AgrupadorContratoEmVigencia agrupadorMap = map.get(agrupadorGOC);
                    ContratoEmVigencia contratoEmVigencia = novoContratoEmVigencia(contratoConsulta);

                    distribuirItensContratoPorGrupoObjetoCompra(contratoConsulta, agrupadorGOC, contratoEmVigencia);
                    agrupadorMap.getContratos().add(contratoEmVigencia);
                    map.put(agrupadorGOC, agrupadorMap);
                }
            }
        }
        ordenarAgrupadores(novoAssistente);
        novoAssistente.setAgrupadores(agrupadores);
        return novoAssistente;
    }

    public List<ItemContratoEmVigencia> buscarContratos(Long idContrato) {
        String sql = " " +
            "   select " +
            "       coalesce(obj.id, obj_ise.id, obj_icv.id)                                                          as id_ob, " +
            "       coalesce(obj.codigo, obj_ise.codigo, obj_icv.codigo) || ' - ' || coalesce(obj.descricao, obj_ise.descricao, obj_icv.descricao) as objeto_compra, " +
            "       coalesce(gobj.id, gobj_ise.id, gobj_icv.id)                                                          as id_goc, " +
            "       coalesce(gobj.codigo, gobj_ise.codigo, gobj_icv.codigo) || ' - ' || coalesce(gobj.descricao, gobj_ise.descricao, gobj_icv.descricao) as grupo_objeto_compra, " +
            "       ic.quantidadetotalcontrato                                                                           as qtde_contrato, " +
            "       coalesce(ipc.numero, itcot.numero) as numero_item " +
            "       from itemcontrato ic " +
            getJoinsItemContratoAdequado() +
            "    where ic.contrato_id = :idContrato ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", idContrato);
        List<Object[]> resultado = q.getResultList();
        List<ItemContratoEmVigencia> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                ItemContratoEmVigencia item = new ItemContratoEmVigencia();
                item.setIdObjetoCompra(((BigDecimal) obj[0]).longValue());
                item.setObjetoCompra((String) obj[1]);
                item.setIdGrupoObjetoCompra(((BigDecimal) obj[2]));
                item.setGrupoObjetoCompra((String) obj[3]);
                item.setQuantidadeContrato((BigDecimal) obj[4]);
                item.setNumeroItem(obj[5] != null ? ((BigDecimal) obj[5]).longValue() : null);
                retorno.add(item);
            }
        }
        return retorno;
    }

    private String getJoinsItemContratoAdequado() {
        return "    left join itemcontratoitempropfornec icpf on ic.id = icpf.itemcontrato_id " +
            "       left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "       left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "       left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
            "       left join itemcontratoitempropdisp icpd on ic.id = icpd.itemcontrato_id " +
            "       left join itempropostafornedisp ipfd on icpd.itempropfornecdispensa_id = ipfd.id " +
            "       left join itemprocessodecompra ipc on coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) = ipc.id " +
            "       left join itemsolicitacao its on ipc.itemsolicitacaomaterial_id = its.id " +
            "       left join objetocompra obj on obj.id = its.objetocompra_id " +
            "       left join grupoobjetocompra gobj on gobj.id = obj.grupoobjetocompra_id " +
            "       left join itemcontratoitemsolext icse on ic.id = icse.itemcontrato_id " +
            "       left join itemsolicitacaoexterno ise on icse.itemsolicitacaoexterno_id = ise.id " +
            "       left join objetocompra obj_ise on ise.objetocompra_id = obj_ise.id " +
            "       left join grupoobjetocompra gobj_ise on gobj_ise.id = obj_ise.grupoobjetocompra_id " +
            "       left join itemcontratovigente icv on ic.id = icv.itemcontrato_id " +
            "       left join itemcotacao itcot on itcot.id = icv.itemcotacao_id " +
            "       left join objetocompra obj_icv on obj_icv.id = itcot.objetocompra_id " +
            "       left join grupoobjetocompra gobj_icv on gobj_icv.id = obj_icv.grupoobjetocompra_id ";
    }

    public List<ContratoEmVigencia> preencherObjetosConsultaContrato(List<Object[]> resultado) {
        List<ContratoEmVigencia> contratos = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                ContratoEmVigencia cont = new ContratoEmVigencia();
                cont.setIdContrato(((BigDecimal) obj[0]).longValue());
                cont.setNumeroContrato((String) obj[1]);
                cont.setNumeroTermo((String) obj[2]);
                cont.setExercicio(((BigDecimal) obj[3]).intValue());
                cont.setContratado((String) obj[4]);
                cont.setUnidadeAdministrativa((String) obj[5]);
                cont.setObjeto((String) obj[6]);
                cont.setValorTotal((BigDecimal) obj[7]);
                cont.setNumeroProcesso((String) obj[8]);
                cont.setIdProcesso(((BigDecimal) obj[9]).longValue());
                cont.setItens(buscarContratos(cont.getIdContrato()));
                contratos.add(cont);
            }
        }
        return contratos;
    }

    public void distribuirItensContratoPorGrupoObjetoCompra(ContratoEmVigencia contratoConsulta, AgrupadorGOC agrupadorGOC, ContratoEmVigencia contratoEmVigencia) {
        List<Number> idsGrupoObjetoCompra = agrupadorGOCFacade.buscarGruposAgrupadorGOC(agrupadorGOC);
        for (ItemContratoEmVigencia itemConsulta : contratoConsulta.getItens()) {
            for (Number idGoc : idsGrupoObjetoCompra) {
                if (itemConsulta.getIdGrupoObjetoCompra().equals(idGoc)) {
                    contratoEmVigencia.getItens().add(itemConsulta);
                }
            }
        }
    }

    public List<AgrupadorGOC> buscarAgrupadoresPorGrupoObjetoCompra(List<ItemContratoEmVigencia> itens) {
        List<Long> idsGOC = Lists.newArrayList();
        for (ItemContratoEmVigencia item : itens) {
            idsGOC.add(item.getIdGrupoObjetoCompra().longValue());
        }
        return agrupadorGOCFacade.buscarAgrupadorGOC(idsGOC);
    }

    public void ordenarAgrupadores(AssistenteAgrupadorContratoEmVigencia assistente) {
        for (AgrupadorContratoEmVigencia agrupador : assistente.getAgrupadores()) {
            Collections.sort(agrupador.getContratos());

            for (ContratoEmVigencia contrato : agrupador.getContratos()) {
                Collections.sort(contrato.getItens());
            }
        }
        Collections.sort(assistente.getContratos());
    }

    public ContratoEmVigencia novoContratoEmVigencia(ContratoEmVigencia contratoConsulta) {
        ContratoEmVigencia contratoEmVigencia = new ContratoEmVigencia();
        contratoEmVigencia.setIdContrato(contratoConsulta.getIdContrato());
        contratoEmVigencia.setNumeroContrato(contratoConsulta.getNumeroContrato());
        contratoEmVigencia.setNumeroTermo(contratoConsulta.getNumeroTermo());
        contratoEmVigencia.setExercicio(contratoConsulta.getExercicio());
        contratoEmVigencia.setContratado(contratoConsulta.getContratado());
        contratoEmVigencia.setUnidadeAdministrativa(contratoConsulta.getUnidadeAdministrativa());
        contratoEmVigencia.setObjeto(contratoConsulta.getObjeto());
        contratoEmVigencia.setValorTotal(contratoConsulta.getValorTotal());
        contratoEmVigencia.setNumeroProcesso(contratoConsulta.getNumeroProcesso());
        contratoEmVigencia.setIdProcesso(contratoConsulta.getIdProcesso());
        return contratoEmVigencia;
    }

    public List<AgrupadorContratoEmVigencia> getQuantidadeLicitadaoPorGrupoObjetoCompra(Long idGOC, List<Long> idsProcesso) {
        String sql = " " +
            "   select goc, sum(quantidade_licitada) as quantidade_licitada, sum(quantidade_contratada) as quantidade_contratada "+
            "           from (select goc.codigo || ' - ' || goc.descricao as goc, "+
            "             coalesce(sum(ipc.quantidade), 0)     as quantidade_licitada, "+
            "             coalesce(sum((select sum(ic.quantidadetotalcontrato) "+
            "                           from itemcontrato ic "+
            "                                    left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id "+
            "                                    left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "                                    left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "                                    left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id "+
            "                           where ipf.itemprocessodecompra_id = ipc.id "+
            "             )), 0)                               as quantidade_contratada "+
            "      from licitacao lic "+
            "               inner join processodecompra pc on pc.id = lic.processodecompra_id "+
            "               inner join loteprocessodecompra lote on lote.processodecompra_id = pc.id "+
            "               inner join itemprocessodecompra ipc on lote.id = ipc.LOTEPROCESSODECOMPRA_ID "+
            "               inner join itemsolicitacao its on ipc.itemsolicitacaomaterial_id = its.id "+
            "               inner join objetocompra obj on obj.id = its.objetocompra_id "+
            "               inner join grupoobjetocompra goc on goc.id = obj.grupoobjetocompra_id "+
            "               inner join agrupadorgocgrupo agrup on goc.id = agrup.grupoobjetocompra_id "+
            "      where goc.id = :idGOC "+
            "        and lic.id in (:idProcesso) "+
            "      group by goc.codigo, goc.descricao "+
            "      union all "+
            "      select goc.codigo || ' - ' || goc.descricao as goc, "+
            "             coalesce(sum(ipc.quantidade), 0)     as quantidade_licitada, "+
            "             coalesce(sum((select sum(ic.quantidadetotalcontrato) "+
            "                           from itemcontrato ic "+
            "                                    inner join itemcontratoitempropdisp icpd on ic.id = icpd.itemcontrato_id "+
            "                                    inner join itempropostafornedisp ipfd on icpd.itempropfornecdispensa_id = ipfd.id "+
            "                           where ipfd.itemprocessodecompra_id = ipc.id "+
            "             )), 0)                               as quantidade_contratada "+
            "      from dispensadelicitacao disp "+
            "               inner join processodecompra pc on pc.id = disp.processodecompra_id "+
            "               inner join loteprocessodecompra lote on lote.processodecompra_id = pc.id "+
            "               inner join itemprocessodecompra ipc on lote.id = ipc.LOTEPROCESSODECOMPRA_ID "+
            "               inner join itemsolicitacao its on ipc.itemsolicitacaomaterial_id = its.id "+
            "               inner join objetocompra obj on obj.id = its.objetocompra_id "+
            "               inner join grupoobjetocompra goc on goc.id = obj.grupoobjetocompra_id "+
            "               inner join agrupadorgocgrupo agrup on goc.id = agrup.grupoobjetocompra_id "+
            "      where goc.id = :idGOC "+
            "        and disp.id in (:idProcesso) "+
            "      group by goc.codigo, goc.descricao "+
            "      union all "+
            "      select goc.codigo || ' - ' || goc.descricao         as goc, "+
            "             coalesce(sum(ise.quantidade), 0)             as quantidade_licitada, "+
            "             coalesce(sum((select sum(ic.quantidadetotalcontrato) "+
            "                           from itemcontrato ic "+
            "                                    inner join itemcontratoitemsolext icse on ic.id = icse.itemcontrato_id "+
            "                           where icse.itemsolicitacaoexterno_id = ise.id "+
            "             )), 0)                               as quantidade_contratada "+
            "      from REGISTROSOLMATEXT reg "+
            "               inner join solicitacaomaterialext sol on sol.id = reg.SOLICITACAOMATERIALEXTERNO_ID "+
            "               inner join itemsolicitacaoexterno ise on ise.SOLICITACAOMATERIALEXTERNO_ID = sol.id "+
            "               inner join objetocompra obj on ise.objetocompra_id = obj.id "+
            "               inner join grupoobjetocompra goc on goc.id = obj.grupoobjetocompra_id "+
            "               inner join agrupadorgocgrupo agrup on goc.id = agrup.grupoobjetocompra_id "+
            "      where goc.id = :idGOC "+
            "        and reg.id in (:idProcesso) "+
            "      group by goc.codigo, goc.descricao "+
            "      union all "+
            "      select goc.codigo || ' - ' || goc.descricao         as goc, "+
            "             coalesce(sum(itcot.quantidade), 0)           as quantidade_licitada, "+
            "             coalesce(sum(ic.quantidadetotalcontrato), 0) as quantidade_contratada "+
            "      from itemcontrato ic "+
            "               inner join contrato cont on cont.id = ic.contrato_id "+
            "               inner join contratosvigente contVig on contVig.contrato_id = cont.id "+
            "               inner join itemcontratovigente icv on ic.id = icv.itemcontrato_id "+
            "               inner join itemcotacao itcot on itcot.id = icv.itemcotacao_id "+
            "               inner join objetocompra obj on obj.id = itcot.objetocompra_id "+
            "               inner join grupoobjetocompra goc on goc.id = obj.grupoobjetocompra_id "+
            "               inner join agrupadorgocgrupo agrup on goc.id = agrup.grupoobjetocompra_id "+
            "      where goc.id = :idGOC "+
            "        and cont.id in (:idProcesso) "+
            "      group by goc.codigo, goc.descricao) grupo "+
            "group by grupo.goc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idGOC", idGOC);
        q.setParameter("idProcesso", idsProcesso);
        List<Object[]> resultado = q.getResultList();
        List<AgrupadorContratoEmVigencia> grupos = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                    AgrupadorContratoEmVigencia grupo = new AgrupadorContratoEmVigencia();
                    grupo.setGrupoObjetoCompra((String) obj[0]);
                    grupo.setQuantidadeLicitada((BigDecimal) obj[1]);
                    grupo.setQuantidadeContratada((BigDecimal) obj[2]);
                    grupo.setQuantidadeDisponivel(grupo.getQuantidadeLicitada().subtract(grupo.getQuantidadeContratada()));
                    grupos.add(grupo);
            }
        }
        return grupos;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
