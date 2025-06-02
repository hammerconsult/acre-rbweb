package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.consultasql.ColunaView;
import br.com.webpublico.entidades.comum.consultasql.ObjetoView;
import br.com.webpublico.entidades.comum.consultasql.View;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.FiltroSaldoProcessoLicitatorioVO;
import br.com.webpublico.entidadesauxiliares.HistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.SaldoProcessoLicitatorioVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.SituacaoCotacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Stateless
public class HistoricoProcessoLicitatorioFacade implements Serializable {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PregaoFacade pregaoFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private SaldoProcessoLicitatorioFacade saldoProcessoLicitatorioFacade;

    public List<HistoricoProcessoLicitatorio> buscarHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtro) {
        String sql = "";
        if (filtro.getTipoMovimento().isAtaRegisrtoPrecoExterna()) {
            sql = "select 'SOLICITACAO_ADESAO_EXTERNA'       as tipo_processo," +
                "       solad.id                             as id_movimento," +
                "       cast(solad.DATASOLICITACAO as date)  as data_mov," +
                "       solad.NUMERO || '/' || ex.ano        as numero," +
                "       'Concluída'                        as situacao," +
                "       resp.RAZAOSOCIAL                   as responsavel," +
                "       ho.codigo || ' - ' || ho.descricao as unidade," +
                "       1                                  as ordenacao" +
                " from solicitacaomaterialext solad" +
                "         left join registrosolmatext reg on reg.solicitacaomaterialexterno_id = solad.id" +
                "         inner join EXERCICIO ex on solad.EXERCICIO_ID = ex.ID" +
                "         left join pessoajuridica resp on reg.PESSOAJURIDICA_ID = resp.ID" +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = solad.UNIDADEORGANIZACIONAL_ID" +
                "         left join CONREGPRECOEXTERNO crpe on reg.ID = crpe.REGSOLMATEXT_ID" +
                "         left join contrato cont on crpe.CONTRATO_ID = cont.ID" +
                " where solad.TIPOSOLICITACAOREGISTROPRECO = :tipoAdesao" +
                "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia)," +
                "                                                                                        to_date(:dataOperacao, 'dd/MM/yyyy'))" +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia" + filtro.getCondicaoSql() +
                " union all" +
                " select 'ADESAO_EXTERNA'                     as tipo_processo," +
                "       reg.id                               as id_movimento," +
                "       cast(reg.DATAREGISTROCARONA as date) as data_mov," +
                "       reg.NUMEROREGISTRO || '/' || ex.ano  as numero," +
                "       'Concluída'                          as situacao," +
                "       resp.RAZAOSOCIAL                     as responsavel," +
                "       ho.codigo || ' - ' || ho.descricao   as unidade," +
                "       2                                    as ordenacao" +
                " from registrosolmatext reg" +
                "         left join solicitacaomaterialext solad on reg.SOLICITACAOMATERIALEXTERNO_ID = solad.ID" +
                "         inner join EXERCICIO ex on reg.EXERCICIOREGISTRO_ID = ex.ID" +
                "         left join pessoajuridica resp on reg.PESSOAJURIDICA_ID = resp.ID" +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = reg.UNIDADEORGANIZACIONAL_ID" +
                "         left join CONREGPRECOEXTERNO crpe on reg.ID = crpe.REGSOLMATEXT_ID" +
                "         left join contrato cont on crpe.CONTRATO_ID = cont.ID" +
                " where solad.TIPOSOLICITACAOREGISTROPRECO = :tipoAdesao" +
                "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia)," +
                "                                                                                        to_date(:dataOperacao, 'dd/MM/yyyy'))" +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia" + filtro.getCondicaoSql() +
                " union all" +
                " select 'CONTRATO'                                                        as tipo_processo," +
                "       cont.id                                                           as id_movimento," +
                "       cast(cont.datalancamento as date)                                 as data_mov," +
                "       cont.numerocontrato || ' - ' || cont.numerotermo || '/' || ex.ano as numero," +
                "       cont.situacaocontrato                                             as situacao," +
                "       resp.RAZAOSOCIAL                                                  as responsavel," +
                "       ho.codigo || ' - ' || ho.descricao                                as unidade," +
                "       3                                                                 as ordenacao" +
                " from contrato cont" +
                "         inner join unidadecontrato uc on uc.contrato_id = cont.id" +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = uc.unidadeadministrativa_id" +
                "         inner join exercicio ex on ex.id = cont.exerciciocontrato_id" +
                "         inner join CONREGPRECOEXTERNO crpe on cont.ID = crpe.CONTRATO_ID" +
                "         inner join REGISTROSOLMATEXT reg on crpe.REGSOLMATEXT_ID = reg.id" +
                "         inner join solicitacaomaterialext solad on reg.SOLICITACAOMATERIALEXTERNO_ID = solad.ID" +
                "         left join pessoajuridica resp on reg.PESSOAJURIDICA_ID = resp.ID" +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia),to_date(:dataOperacao, 'dd/MM/yyyy'))" +
                "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql();

        } else if (filtro.getTipoMovimento().isContratoVigente()) {
            sql = "select 'FORMULARIO_COTACAO'               as tipo_processo, " +
                "       fc.id                              as id_movimento, " +
                "       cast(fc.dataformulario as date)    as data_mov, " +
                "       fc.numero || '/' || ex.ano         as numero, " +
                "       'Concluído'                        as situacao, " +
                "       resp.nome                          as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao as unidade, " +
                "       1                                  as ordenacao " +
                "from formulariocotacao fc " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = fc.unidadeorganizacional_id " +
                "         inner join cotacao cot on cot.formulariocotacao_id = fc.id " +
                "         inner join exercicio ex on ex.id = fc.exercicio_id " +
                "         left join usuariosistema usu on usu.id = fc.usuariocriacao_id " +
                "         left join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), " +
                "                                                                                        to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and ho.tipohierarquiaorganizacional  = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                "select 'COTACAO'                          as tipo_processo, " +
                "       cot.id                             as id_movimento, " +
                "       cast(cot.datacotacao as date)      as data_mov, " +
                "       cot.numero || '/' || ex.ano        as numero, " +
                "       cot.situacao                       as situacao, " +
                "       resp.nome                          as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao as unidade, " +
                "       2                                  as ordenacao " +
                " from cotacao cot " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = cot.unidadeorganizacional_id " +
                "         inner join exercicio ex on ex.id = cot.exercicio_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join usuariosistema usu on usu.id = fc.usuariocriacao_id " +
                "         left join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), " +
                "                                                                                        to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and ho.tipohierarquiaorganizacional  = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                " select 'CONTRATO_VIGENTE'                                                 as tipo_processo, " +
                "       cont.id                                                             as id_movimento, " +
                "       cast(cont.datalancamento as date)                                   as data_mov, " +
                "       cont.numerocontrato || ' - ' || cont.numerotermo || '/' || ex.ano   as numero, " +
                "       cont.SITUACAOCONTRATO                                               as situacao, " +
                "       resp.nome                                                           as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao                                  as unidade, " +
                "       3                                                                   as ordenacao " +
                " from contrato cont " +
                "         inner join unidadecontrato uc on uc.contrato_id = cont.id " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = uc.unidadeadministrativa_id " +
                "         inner join exercicio ex on ex.id = cont.exerciciocontrato_id " +
                "         inner join CONTRATOSVIGENTE cv on cont.ID = cv.CONTRATO_ID " +
                "         inner join cotacao cot on cot.id = cv.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         inner join usuariosistema usu on fc.USUARIOCRIACAO_ID = usu.ID " +
                "         inner join pessoafisica resp on resp.id = usu.PESSOAFISICA_ID " +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), " +
                "                                                                                        to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), " +
                "                                                                                        to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and ho.tipohierarquiaorganizacional  = :tipoHierarquia" + filtro.getCondicaoSql();
        } else {
            sql = "select distinct * " +
                " from (select 'IRP'                                                      as tipo_processo, " +
                "             irp.id                                                      as id_movimento, " +
                "             cast(irp.datalancamento as date)                            as data_mov, " +
                "             irp.numero || '/' || extract(year from irp.datalancamento)  as numero, " +
                "             irp.situacaoirp                                             as situacao, " +
                "             resp.nome                                                   as responsavel, " +
                "             ho.codigo || ' - ' || ho.descricao                          as unidade, " +
                "             1                                                           as ordenacao " +
                "      from intencaoregistropreco irp " +
                "               inner join unidadeirp unid on unid.intencaoregistropreco_id = irp.id " +
                "               inner join hierarquiaorganizacional ho on ho.subordinada_id = unid.unidadegerenciadora_id " +
                "               left join formulariocotacao fc on fc.intencaoregistropreco_id = irp.id " +
                "               left join cotacao cot on cot.formulariocotacao_id = fc.id " +
                "               left join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                "               left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "               left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "               left join exercicio ex on ex.id = fc.exercicio_id " +
                "               left join usuariosistema usu on usu.id = irp.responsavel_id " +
                "               left join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "               left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID" +
                "               left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "               left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "               left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "               left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "               left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "               left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "      where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "        and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(unid.iniciovigencia) and coalesce(trunc(unid.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "        and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                "      union all " +
                "      select 'FORMULARIO_COTACAO'               as tipo_processo, " +
                "             fc.id                              as id_movimento, " +
                "             cast(fc.dataformulario as date)   as data_mov, " +
                "             fc.numero || '/' || ex.ano         as numero, " +
                "             'Concluído'                        as situacao, " +
                "             resp.nome                          as responsavel, " +
                "             ho.codigo || ' - ' || ho.descricao as unidade, " +
                "             2                                  as ordenacao " +
                "      from formulariocotacao fc " +
                "               inner join hierarquiaorganizacional ho on ho.subordinada_id = fc.unidadeorganizacional_id " +
                "               left join cotacao cot on cot.formulariocotacao_id = fc.id " +
                "               left join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                "               left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "               left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "               inner join exercicio ex on ex.id = fc.exercicio_id " +
                "               left join usuariosistema usu on usu.id = fc.usuariocriacao_id " +
                "               left join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "               left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID" +
                "               left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "               left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "               left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "               left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "               left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "               left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "      where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "        and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                "      union all " +
                "      select 'COTACAO'                          as tipo_processo, " +
                "             cot.id                             as id_movimento, " +
                "             cast(cot.datacotacao as date)      as data_mov, " +
                "             cot.numero || '/' || ex.ano        as numero, " +
                "             cot.situacao                       as situacao, " +
                "             resp.nome                          as responsavel, " +
                "             ho.codigo || ' - ' || ho.descricao as unidade, " +
                "             3                                  as ordenacao " +
                "      from cotacao cot " +
                "               left join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                "               inner join hierarquiaorganizacional ho on ho.subordinada_id = cot.unidadeorganizacional_id " +
                "               left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "               left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "               inner join exercicio ex on ex.id = cot.exercicio_id " +
                "               inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "               left join usuariosistema usu on usu.id = fc.usuariocriacao_id " +
                "               left join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "               left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID" +
                "               left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "               left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "               left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "               left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "               left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "               left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "      where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "        and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                "      union all " +
                "      select 'SOLICITACAO_COMPRA'               as tipo_processo, " +
                "             sol.id                             as id_movimento, " +
                "             cast(sol.datasolicitacao as date)  as data_mov, " +
                "             sol.numero || '/' || ex.ano        as numero, " +
                "             ava.tipostatussolicitacao          as situacao, " +
                "             resp.nome                          as responsavel, " +
                "             ho.codigo || ' - ' || ho.descricao as unidade, " +
                "             4                                  as ordenacao " +
                "      from solicitacaomaterial sol " +
                "               inner join avaliacaosolcompra ava on ava.solicitacao_id = sol.id " +
                "               inner join hierarquiaorganizacional ho on ho.subordinada_id = sol.unidadeorganizacional_id " +
                "               left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "               left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "               left join usuariosistema usu on usu.id = sol.usuariocriacao_id " +
                "               left join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "               inner join exercicio ex on ex.id = sol.exercicio_id " +
                "               inner join cotacao cot on cot.id = sol.cotacao_id " +
                "               inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "               left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID" +
                "               left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "               left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "               left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "               left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "               left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "               left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "      where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "        and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
                "        and ava.id = (select max(avali.id) from avaliacaosolcompra avali where avali.solicitacao_id = sol.id) " + filtro.getCondicaoSql() +
                "      union all " +
                "      select 'AVALIACAO_SOLICITACAO_COMPRA'     as tipo_processo, " +
                "             ava.id                             as id_movimento, " +
                "             cast(ava.dataavaliacao as date)    as data_mov, " +
                "             sol.numero || '/' || ex.ano        as numero, " +
                "             ava.tipostatussolicitacao          as situacao, " +
                "             resp.nome                          as responsavel, " +
                "             ho.codigo || ' - ' || ho.descricao as unidade, " +
                "             5                                  as ordenacao " +
                "      from avaliacaosolcompra ava " +
                "               inner join solicitacaomaterial sol on sol.id = ava.solicitacao_id " +
                "               inner join hierarquiaorganizacional ho on ho.subordinada_id = sol.unidadeorganizacional_id " +
                "               left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "               left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "               left join usuariosistema usu on usu.id = ava.usuariosistema_id " +
                "               left join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "               inner join exercicio ex on ex.id = sol.exercicio_id " +
                "               inner join cotacao cot on cot.id = sol.cotacao_id " +
                "               inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "               left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "               left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "               left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "               left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "               left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "               left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "      where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "        and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
                "        and ava.tipostatussolicitacao <> 'AGUARDANDO_AVALIACAO' " + filtro.getCondicaoSql() +
                "      union all " +
                "      select 'RESERVA_SOLICITACAO_COMPRA'       as tipo_processo, " +
                "             dot.id                             as id_movimento, " +
                "             cast(dot.realizadaem as date)      as data_mov, " +
                "             dot.codigo || '/' || ex.ano        as numero, " +
                "             'Concluída'                        as situacao, " +
                "             resp.nome                          as responsavel, " +
                "             ho.codigo || ' - ' || ho.descricao as unidade, " +
                "             6                                  as ordenacao " +
                "      from dotsolmat dot " +
                "               inner join solicitacaomaterial sol on sol.id = dot.solicitacaomaterial_id " +
                "               inner join hierarquiaorganizacional ho on ho.subordinada_id = sol.unidadeorganizacional_id " +
                "               left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "               left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "               left join usuariosistema usu on usu.id = sol.usuariocriacao_id " +
                "               left join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "               inner join exercicio ex on ex.id = sol.exercicio_id " +
                "               inner join cotacao cot on cot.id = sol.cotacao_id " +
                "               inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "               left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "               left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "               left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "               left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "               left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "               left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "      where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "        and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                "      union all " +
                "      select 'PROCESSO_COMPRA'                  as tipo_processo, " +
                "             pc.id                              as id_movimento, " +
                "             cast(pc.dataprocesso as date)      as data_mov, " +
                "             pc.numero || '/' || ex.ano         as numero, " +
                "             'Concluído'                        as situacao, " +
                "             resp.nome                          as responsavel, " +
                "             ho.codigo || ' - ' || ho.descricao as unidade, " +
                "             7                                  as ordenacao " +
                "      from processodecompra pc " +
                "               inner join usuariosistema usu on usu.id = pc.usuariosistema_id " +
                "               inner join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "               inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "               inner join exercicio ex on ex.id = pc.exercicio_id " +
                "               inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "               inner join cotacao cot on cot.id = sol.cotacao_id " +
                "               inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "               left join dispensadelicitacao disp on pc.id = disp.processodecompra_id " +
                "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "               left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID" +
                "               left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "               left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "               left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "               left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "               left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "               left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "      where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "        and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                "      union all " +
                "      select 'DISPENSA_LICITACAO_INEXIGIBILIDADE'   as tipo_processo, " +
                "       disp.id                                as id_movimento, " +
                "       cast(disp.datadadispensa as date)      as data_mov, " +
                "       disp.numerodadispensa || '/' || ex.ano as numero, " +
                "       disp.situacao                          as situacao, " +
                "       resp.nome                              as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao     as unidade, " +
                "       8                                      as ordenacao " +
                "   from processodecompra pc " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "         inner join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join licitacao lic on lic.processodecompra_id = pc.id " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join statuslicitacao status on status.licitacao_id = lic.id " +
                "            and status.id = (select max(s.id) from StatusLicitacao s where s.licitacao_id = status.licitacao_id) " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         left join usuariosistema usu on usu.id = coalesce(disp.usuariosistema_id, pc.usuariosistema_id) " +
                "         left join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "         inner join exercicio ex on ex.id = coalesce(lic.exercicio_id, disp.exerciciodadispensa_id) " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "          left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "       where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "       and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                "   select 'LICITACAO'                          as tipo_processo, " +
                "       lic.id                               as id_movimento, " +
                "       cast(lic.emitidaem as date)          as data_mov, " +
                "       lic.numerolicitacao || '/' || ex.ano as numero, " +
                "       status.tiposituacaolicitacao         as situacao, " +
                "       resp.nome                            as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao   as unidade, " +
                "       8                                    as ordenacao " +
                "   from processodecompra pc " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "         inner join licitacao lic on lic.processodecompra_id = pc.id " +
                "            and lic.MODALIDADELICITACAO <> :credenciamento " +
                "         inner join statuslicitacao status on status.licitacao_id = lic.id " +
                "            and status.id = (select max(s.id) from StatusLicitacao s where s.licitacao_id = status.licitacao_id) " +
                "         inner join usuariosistema usu on usu.id = pc.usuariosistema_id " +
                "         inner join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "            and ava.id = (select max(avamax.id) from avaliacaosolcompra avamax where avamax.solicitacao_id = sol.id) " +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "          left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "   where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "   and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                "   select 'CREDENCIAMENTO'                     as tipo_processo, " +
                "       lic.id                               as id_movimento, " +
                "       cast(lic.emitidaem as date)          as data_mov, " +
                "       lic.numerolicitacao || '/' || ex.ano as numero, " +
                "       status.tiposituacaolicitacao         as situacao, " +
                "       resp.nome                            as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao   as unidade, " +
                "       8                                    as ordenacao " +
                "   from processodecompra pc " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "         inner join licitacao lic on lic.processodecompra_id = pc.id " +
                "            and lic.MODALIDADELICITACAO = :credenciamento " +
                "         inner join pessoafisica resp on resp.id = lic.responsavel_id " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                "         inner join statuslicitacao status on status.licitacao_id = lic.id " +
                "            and status.id = (select max(s.id) from StatusLicitacao s where s.licitacao_id = status.licitacao_id) " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "          left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "       where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "       and ho.tipohierarquiaorganizacional = :tipoHierarquia" + filtro.getCondicaoSql() +
                " union all " +
                " select 'PARECER_LICITACAO'               as tipo_processo," +
                "       pl.id                              as id_movimento, " +
                "       cast(pl.dataparecer as date)       as data_mov, " +
                "       pl.numero || '/' || ex.ano         as numero, " +
                "       case when pl.deferido = 1 then 'Deferido' " +
                "           else 'Indeferido' end          as situacao, " +
                "       resp.nome                          as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao as unidade, " +
                "       9                                  as ordenacao " +
                " from parecerlicitacao pl " +
                "         inner join licitacao lic on lic.id = pl.licitacao_id " +
                "            and lic.MODALIDADELICITACAO <> :credenciamento " +
                "         inner join processodecompra pc on pc.id = lic.processodecompra_id " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         inner join pessoafisica resp on resp.id = pl.pessoa_id " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID" +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID  " +
                "         left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "         left join certame cert on lic.id = cert.licitacao_id " +
                "         left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "         left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "         left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                " select distinct 'RECURSO'                                                                               as tipo_processo, " +
                "                rec.id                                                                                  as id_movimento, " +
                "                cast(rec.DATARECURSO as date)                                                           as data_mov, " +
                "                rec.numero || '/' || extract(year from rec.DATARECURSO)                                 as numero, " +
                "                (select sl.TIPOSITUACAOLICITACAO " +
                "                 from RECURSOSTATUS rs " +
                "                 inner join STATUSLICITACAO sl " +
                "                 on rs.STATUSLICITACAO_ID = sl.ID and sl.DATASTATUS = (select max(sl1.DATASTATUS) " +
                "                 from STATUSLICITACAO sl1 " +
                "                 inner join RECURSOSTATUS x on sl1.ID = x.STATUSLICITACAO_ID " +
                "                 where x.RECURSOLICITACAO_ID = rec.id))                                                 as situacao, " +
                "                resp.nome                                                                               as responsavel, " +
                "                ho.codigo || ' - ' || ho.descricao                                                      as unidade, " +
                "                10                                                                                      as ordenacao " +
                " from RECURSOLICITACAO rec  " +
                "         inner join licitacao lic on rec.LICITACAO_ID = lic.ID " +
                "         inner join processodecompra pc on pc.id = lic.processodecompra_id " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         inner join pessoafisica resp on rec.JULGADOR_ID = resp.ID " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "         left join certame cert on lic.id = cert.licitacao_id " +
                "         left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "         left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "         left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                " and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                " select case when lic.TIPOAPURACAO = 'ITEM' then 'PREGAO_POR_ITEM' " +
                "       else 'PREGAO_POR_LOTE' end                                      as tipo_processo, " +
                "       pg.id                                                           as id_movimento, " +
                "       cast(pg.realizadoem as date)                                    as data_mov, " +
                "       lic.numerolicitacao || '/' || ex.ano                            as numero, " +
                "       'Concluído'                                                     as situacao, " +
                "       resp.nome                                                       as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao                              as unidade, " +
                "       11                                                              as ordenacao " +
                " from pregao pg " +
                "         inner join licitacao lic on lic.id = pg.licitacao_id " +
                "         inner join processodecompra pc on pc.id = lic.processodecompra_id " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         inner join usuariosistema usu on usu.id = pc.usuariosistema_id " +
                "         inner join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID" +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "         left join certame cert on lic.id = cert.licitacao_id " +
                "         left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "         left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "         left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                "select distinct 'MAPA_COMPARATVO'                  as tipo_processo, " +
                "       cert.id                            as id_movimento, " +
                "       cast(cert.DATAREALIZADO as date)   as data_mov, " +
                "       lic.numero || '/' || ex.ano        as numero, " +
                "       'Concluído'                        as situacao, " +
                "       resp.nome                          as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao as unidade, " +
                "       12                                 as ordenacao " +
                " from CERTAME cert " +
                "         left join licitacao lic on cert.LICITACAO_ID = lic.ID " +
                "         inner join processodecompra pc on pc.id = lic.processodecompra_id " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         inner join usuariosistema usu on usu.id = pc.usuariosistema_id " +
                "         inner join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "         left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "         left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "         left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "         left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), " +
                "                                                                                         to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce( " +
                "        trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                "select 'MAPA_COMPARATVO_TECNICA_PRECO'    as tipo_processo, " +
                "       matec.id                           as id_movimento, " +
                "       cast(matec.DATA as date)           as data_mov, " +
                "       lic.numero || '/' || ex.ano        as numero, " +
                "       'Concluído'                        as situacao, " +
                "       resp.nome                          as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao as unidade, " +
                "       13                                 as ordenacao " +
                "from MAPACOMTECPREC matec " +
                "         left join licitacao lic on matec.LICITACAO_ID = lic.ID " +
                "         inner join processodecompra pc on pc.id = lic.processodecompra_id " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         inner join usuariosistema usu on usu.id = pc.usuariosistema_id " +
                "         inner join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "         left join certame cert on lic.id = cert.licitacao_id " +
                "         left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "         left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "         left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), " +
                "                                                                                         to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce( " +
                "        trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia" + filtro.getCondicaoSql() +
                " union all " +
                " select 'REPACTUACAO_PRECO'               as tipo_processo, " +
                "       repac.id                           as id_movimento, " +
                "       cast(ata.DATAINICIO as date)       as data_mov, " +
                "       repac.numeroparecer                as numero, " +
                "       repac.PARECERREPACTUACAO           as situacao, " +
                "       resp.nome                          as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao as unidade, " +
                "       14                                 as ordenacao " +
                " from REPACTUACAOPRECO repac " +
                "         inner join ataregistropreco ata on repac.ATAREGISTROPRECO_ID = ata.ID " +
                "         inner join licitacao lic on ata.LICITACAO_ID = lic.ID " +
                "         inner join processodecompra pc on pc.id = lic.processodecompra_id " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         inner join usuariosistema usu on usu.id = pc.usuariosistema_id " +
                "         inner join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "         left join certame cert on lic.ID = cert.LICITACAO_ID " +
                "         left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "         left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), " +
                "                                                                                         to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce( " +
                "        trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                " select 'PROXIMO_VENCEDOR_LICITACAO'          as tipo_processo, " +
                "       pvl.id                                as id_movimento, " +
                "       cast(pvl.DATALANCAMENTO as date)      as data_mov, " +
                "       pvl.numero || '/' || ex.ano           as numero, " +
                "       'Concluído'                           as situacao, " +
                "       resp.nome                             as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao    as unidade, " +
                "       15                                    as ordenacao " +
                " from PROXIMOVENCEDORLICITACAO pvl " +
                "         inner join licitacao lic on pvl.LICITACAO_ID = lic.ID " +
                "         left join processodecompra pc on pc.id = lic.processodecompra_id " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = pc.unidadeorganizacional_id " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         inner join usuariosistema usu on usu.id = pc.usuariosistema_id " +
                "         inner join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "         left join certame cert on lic.ID = cert.LICITACAO_ID " +
                "         left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "         left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), " +
                "                                                                                         to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) " +
                "  and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                " select 'ATA_REGISTRO_PRECO'              as tipo_processo, " +
                "       ata.id                             as id_movimento, " +
                "       cast(ata.datainicio as date)       as data_mov, " +
                "       ata.numero || '/' || ex.ano        as numero, " +
                "       'Concluído'                        as situacao, " +
                "       resp.nome                          as responsavel, " +
                "       ho.codigo || ' - ' || ho.descricao as unidade, " +
                "       16                                 as ordenacao " +
                " from ataregistropreco ata " +
                "         inner join licitacao lic on lic.id = ata.licitacao_id " +
                "         inner join processodecompra pc on pc.id = lic.processodecompra_id " +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = ata.unidadeorganizacional_id " +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " +
                "         inner join usuariosistema usu on usu.id = pc.usuariosistema_id " +
                "         inner join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID" +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "         left join certame cert on lic.id = cert.licitacao_id " +
                "         left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "         left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "         left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "         left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                " select 'ADESAO_INTERNA'                    as tipo_processo," +
                "       solad.id                            as id_movimento," +
                "       cast(solad.DATASOLICITACAO as date) as data_mov," +
                "       solad.NUMERO || '/' || ex.ano       as numero," +
                "       'Concluída'                         as situacao," +
                "       resp.nome                           as responsavel," +
                "       ho.codigo || ' - ' || ho.descricao  as unidade," +
                "       17                                  as ordenacao " +
                " from SOLICITACAOMATERIALEXT solad" +
                "        left join ATAREGISTROPRECO ata on ata.ID = solad.ATAREGISTROPRECO_ID" +
                "         inner join licitacao lic on lic.id = ata.licitacao_id" +
                "         inner join processodecompra pc on pc.id = lic.processodecompra_id" +
                "         inner join hierarquiaorganizacional ho on ho.subordinada_id = solad.unidadeorganizacional_id" +
                "         left join conlicitacao cl on cl.licitacao_id = lic.id" +
                "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id" +
                "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id" +
                "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id)" +
                "         inner join usuariosistema usu on usu.id = pc.usuariosistema_id" +
                "         inner join pessoafisica resp on resp.id = usu.pessoafisica_id" +
                "         inner join exercicio ex on solad.EXERCICIO_ID = ex.ID" +
                "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id" +
                "         inner join cotacao cot on cot.id = sol.cotacao_id" +
                "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id" +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id" +
                "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id" +
                "         left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "         left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "         left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "         left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "         left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "         left join certame cert on lic.id = cert.licitacao_id " +
                "         left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "         left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "         left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "         left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                " where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia)," +
                "       to_date(:dataOperacao, 'dd/MM/yyyy'))" +
                "  and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " union all " +
                " select 'CONTRATO'                                                        as tipo_processo, " +
                "             cont.id                                                           as id_movimento, " +
                "             cast(cont.datalancamento as date)                                 as data_mov, " +
                "             cont.numerocontrato || ' - ' || cont.numerotermo || '/' || ex.ano as numero, " +
                "             cont.situacaocontrato                                             as situacao, " +
                "             resp.nome                                                         as responsavel, " +
                "             ho.codigo || ' - ' || ho.descricao                                as unidade, " +
                "             18                                                                as ordenacao " +
                "      from contrato cont " +
                "               inner join unidadecontrato uc on uc.contrato_id = cont.id " +
                "               inner join hierarquiaorganizacional ho on ho.subordinada_id = uc.unidadeadministrativa_id " +
                "               inner join exercicio ex on ex.id = cont.exerciciocontrato_id " +
                "               left join conlicitacao cl on cl.contrato_id = cont.id " +
                "               left join licitacao lic on cl.licitacao_id = lic.id " +
                "               left join condispensalicitacao cd on cd.contrato_id = cont.id " +
                "               left join dispensadelicitacao disp on cd.dispensadelicitacao_id = disp.id " +
                "               left join processodecompra pc on pc.id = coalesce(lic.processodecompra_id, disp.processodecompra_id) " +
                "               left join usuariosistema usu on usu.id = pc.usuariosistema_id " +
                "               left join pessoafisica resp on resp.id = usu.pessoafisica_id " +
                "               left join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                "               left join cotacao cot on cot.id = sol.cotacao_id " +
                "               left join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                "               left join avaliacaosolcompra ava on sol.ID = ava.SOLICITACAO_ID " +
                "               left join dotsolmat dot on sol.ID = dot.SOLICITACAOMATERIAL_ID " +
                "               left join parecerlicitacao pl on lic.ID = pl.LICITACAO_ID " +
                "               left join ataregistropreco ata on lic.ID = ata.LICITACAO_ID " +
                "               left join pregao pg on lic.ID = pg.LICITACAO_ID " +
                "               left join certame cert on lic.id = cert.licitacao_id " +
                "               left join MAPACOMTECPREC matec on lic.ID = matec.LICITACAO_ID " +
                "               left join REPACTUACAOPRECO repac on ata.ID = repac.ATAREGISTROPRECO_ID " +
                "               left join PROXIMOVENCEDORLICITACAO pvl on lic.ID = pvl.LICITACAO_ID " +
                "               left join SOLICITACAOMATERIALEXT solad on ata.ID = solad.ATAREGISTROPRECO_ID " +
                "               left join CONREGPRECOEXTERNO conreg on conreg.CONTRATO_ID = cont.id " +
                "               left join registrosolmatext reg on conreg.REGSOLMATEXT_ID = reg.ID " +
                "               left join RECURSOLICITACAO rec on lic.ID = rec.LICITACAO_ID " +
                "      where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "        and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "        and ho.tipohierarquiaorganizacional = :tipoHierarquia " + filtro.getCondicaoSql() +
                " ) order by ordenacao ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (filtro.getTipoMovimento().isAtaRegisrtoPrecoExterna()) {
            q.setParameter("tipoAdesao", TipoSolicitacaoRegistroPreco.EXTERNA.name());
        }
        if (!filtro.getTipoMovimento().isAtaRegisrtoPrecoExterna()) {
            q.setParameter("credenciamento", ModalidadeLicitacao.CREDENCIAMENTO.name());
        }
        List<Object[]> objetos = q.getResultList();
        List<HistoricoProcessoLicitatorio> historico = Lists.newArrayList();
        for (Object[] o : objetos) {
            HistoricoProcessoLicitatorio hist = new HistoricoProcessoLicitatorio();
            hist.setTipoMovimento(TipoMovimentoProcessoLicitatorio.valueOf((String) o[0]));
            hist.setId(((BigDecimal) o[1]).longValue());
            hist.setData((Date) o[2]);
            hist.setNumero((String) o[3]);
            hist.setSituacao((String) o[4]);
            hist.setResponsavel((String) o[5]);
            hist.setUnidadeOrganizacional((String) o[6]);
            buscarComplementoAndDetalhesProcesso(hist);
            historico.add(hist);
        }
        return historico;
    }

    public void buscarComplementoAndDetalhesProcesso(HistoricoProcessoLicitatorio historico) {
        switch (historico.getTipoMovimento()) {
            case IRP:
                buscarDadosIrp(historico);
                buscarDadosParticipantesIrp(historico);
                historico.setSituacao(SituacaoIRP.valueOf(historico.getSituacao()).getDescricao());
                historico.setGeraRelatorio(true);
                break;
            case FORMULARIO_COTACAO:
                buscarDadosFormulario(historico);
                historico.setGeraRelatorio(true);
                break;
            case COTACAO:
                buscarDadosCotacao(historico);
                buscarDadosFornecedoresCotacao(historico);
                historico.setSituacao(SituacaoCotacao.valueOf(historico.getSituacao()).getDescricao());
                historico.setGeraRelatorio(true);
                break;
            case SOLICITACAO_COMPRA:
                buscarDadosSolicitacaoCompra(historico);
                historico.setSituacao(TipoStatusSolicitacao.valueOf(historico.getSituacao()).getDescricao());
                historico.setGeraRelatorio(true);
                break;
            case AVALIACAO_SOLICITACAO_COMPRA:
                buscarDadosAvaliacaoSolicitacaoCompra(historico);
                historico.setSituacao(TipoStatusSolicitacao.valueOf(historico.getSituacao()).getDescricao());
                historico.setGeraRelatorio(true);
                break;
            case RESERVA_SOLICITACAO_COMPRA:
                buscarDadosReservaSolicitacaoCompra(historico);
                break;
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                buscarDadosDispensa(historico);
                historico.setSituacao(SituacaoDispensaDeLicitacao.valueOf(historico.getSituacao()).getDescricao());
                break;
            case LICITACAO:
                buscarDadosLicitacao(historico);
                buscarDadosParticipantesLicitacao(historico);
                buscarDadosPropostasLicitacao(historico);
                historico.setSituacao(TipoSituacaoLicitacao.valueOf(historico.getSituacao()).getDescricao());
                break;
            case CREDENCIAMENTO:
                buscarDadosCredenciamento(historico);
                buscarDadosParticipantesLicitacao(historico);
                buscarDadosPropostasLicitacao(historico);
                historico.setSituacao(TipoSituacaoLicitacao.valueOf(historico.getSituacao()).getDescricao());
                break;
            case PROCESSO_COMPRA:
                buscarDadosProcessoCompra(historico);
                break;
            case PARECER_LICITACAO:
                buscarDadosParecerLicitacao(historico);
                break;
            case PREGAO_POR_ITEM:
            case PREGAO_POR_LOTE:
                buscarDadosPregaoPorItem(historico);
                historico.setGeraRelatorio(true);
                break;
            case ATA_REGISTRO_PRECO:
                buscarDadosAtaRegistroPreco(historico);
                break;
            case CONTRATO:
            case CONTRATO_VIGENTE:
                buscarDadosContrato(historico);
                buscarDadosExecucaoContrato(historico);
                historico.setSituacao(SituacaoContrato.valueOf(historico.getSituacao()).getDescricao());
                break;
            case ADESAO_INTERNA:
                buscarDadosAdesaoInterna(historico);
                break;
            case ADESAO_EXTERNA:
                buscarDadosAdesaoExterna(historico);
                break;
            case SOLICITACAO_ADESAO_EXTERNA:
                buscarDadosSolicitacaoAdesaoExterna(historico);
                break;
            case RECURSO:
                buscarDadosRecursoLicitacao(historico);
                historico.setSituacao(TipoSituacaoLicitacao.valueOf(historico.getSituacao()).getDescricao());
                break;
            case MAPA_COMPARATVO:
                buscarDadosMapaComparativo(historico);
                break;
            case MAPA_COMPARATVO_TECNICA_PRECO:
                buscarDadosMapaComparativoTecnicaPreco(historico);
                break;
            case REPACTUACAO_PRECO:
                buscarDadosRepactuacaoPreco(historico);
                historico.setSituacao(ParecerRepactuacaoPreco.valueOf(historico.getSituacao()).getDescricao());
                break;
            case PROXIMO_VENCEDOR_LICITACAO:
                buscarDadosProximoVencedorLicitacao(historico);
                break;
        }
    }

    private static class ColunaConsulta {
        private String nomeColuna;
        private Class classe;

        public ColunaConsulta(String nomeColuna, Class classe) {
            this.nomeColuna = nomeColuna;
            this.classe = classe;
        }

        public String getNomeColuna() {
            return nomeColuna;
        }

        public void setNomeColuna(String nomeColuna) {
            this.nomeColuna = nomeColuna;
        }

        public Class getClasse() {
            return classe;
        }

        public void setClasse(Class classe) {
            this.classe = classe;
        }
    }

    private void buscarDadosExecucaoContrato(HistoricoProcessoLicitatorio historico) {
        String sql = "" +
            " select ex.numero                          as numero, " +
            "        ex.datalancamento                  as data_lancamento, " +
            "        vw.codigo || ' - ' || vw.descricao as unidade,  " +
            "        ex.valor                           as valor " +
            " from execucaocontrato ex " +
            "   inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = ex.unidadeorcamentaria_id " +
            " where to_date(:dataoperacao, 'dd/mm/yyyy') between trunc(vw.iniciovigencia) and coalesce(trunc(vw.fimvigencia), to_date(:dataoperacao, 'dd/mm/yyyy')) " +
            "   and ex.contrato_id = :idObjeto " +
            " order by ex.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        q.setParameter("dataoperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List<Object> objetos = q.getResultList();
        historico.getViewDetalhes().add(criarViewTitulo("Execuções"));
        historico.getViewDetalhes().add(criarViewColunas(objetos, getColunasExecucaoContrato()));
    }

    private static List<ColunaConsulta> getColunasExecucaoContrato() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Número", Integer.class));
        colunas.add(new ColunaConsulta("Data de Lançamento", Date.class));
        colunas.add(new ColunaConsulta("Unidade Orçamentária", String.class));
        colunas.add(new ColunaConsulta("Valor (R$)", BigDecimal.class));
        return colunas;
    }

    private void buscarDadosContrato(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) || ' - ' || coalesce(pf.nome, pj.razaosocial) as fornecedor, " +
            "        c.iniciovigencia                                           as inicio_vigencia, " +
            "        c.terminovigenciaatual                                           as termino_vigencia, " +
            "        c.dataassinatura                                                 as assinatura, " +
            "        c.valortotal                                             as valor, " +
            "        c.saldoatualcontrato                                     as saldo " +
            " from contrato c " +
            "         inner join pessoa p on p.id = c.contratado_id " +
            "         left join pessoafisica pf on pf.id = p.id " +
            "         left join pessoajuridica pj on pj.id = p.id " +
            " where c.id = :idObjeto " +
            " order by c.numerotermo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasContrato()));
    }

    private static List<ColunaConsulta> getColunasContrato() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Fornecedor", String.class));
        colunas.add(new ColunaConsulta("Inicío de Vigência", Date.class));
        colunas.add(new ColunaConsulta("Término de Vigência", Date.class));
        colunas.add(new ColunaConsulta("Assinatura", Date.class));
        colunas.add(new ColunaConsulta("Valor (R$)", BigDecimal.class));
        colunas.add(new ColunaConsulta("Saldo (R$)", BigDecimal.class));
        return colunas;
    }

    private void buscarDadosAdesaoInterna(HistoricoProcessoLicitatorio historico) {
        String sql = "select solad.OBJETO as objeto " +
            "from SOLICITACAOMATERIALEXT solad " +
            "where solad.TIPOSOLICITACAOREGISTROPRECO = :tipoAdesao and solad.id = :idObjeto " +
            "order by solad.DATASOLICITACAO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        q.setParameter("tipoAdesao", TipoSolicitacaoRegistroPreco.INTERNA.name());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasAdesaoInterna()));
    }

    private static List<ColunaConsulta> getColunasAdesaoInterna() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Objeto", String.class));
        return colunas;
    }

    private void buscarDadosAdesaoExterna(HistoricoProcessoLicitatorio historico) {
        String sql = "select reg.TIPOAVALIACAO as tipo_avaliacao, " +
            "         reg.TIPOAPURACAO         as tipo_apuracao, " +
            "         reg.OBJETO               as objeto " +
            "from registrosolmatext reg  where reg.id = :idObjeto " +
            "order by reg.DATAAUTORIZACAOCARONA ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasAdesaoExterna()));
    }

    private static List<ColunaConsulta> getColunasAdesaoExterna() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Tipo Avaliação ", TipoAvaliacaoLicitacao.class));
        colunas.add(new ColunaConsulta("Tipo Apuração ", TipoApuracaoLicitacao.class));
        colunas.add(new ColunaConsulta("Objeto ", String.class));
        return colunas;
    }

    private void buscarDadosSolicitacaoAdesaoExterna(HistoricoProcessoLicitatorio historico) {
        String sql = "select solad.OBJETO as objeto " +
            "from SOLICITACAOMATERIALEXT solad " +
            "where solad.TIPOSOLICITACAOREGISTROPRECO = :tipoAdesao and solad.id = :idObjeto " +
            "order by solad.DATASOLICITACAO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        q.setParameter("tipoAdesao", TipoSolicitacaoRegistroPreco.EXTERNA.name());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasSolicitacaoAdesaoExterna()));
    }

    private static List<ColunaConsulta> getColunasSolicitacaoAdesaoExterna() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Objeto ", String.class));
        return colunas;
    }

    private void buscarDadosRecursoLicitacao(HistoricoProcessoLicitatorio historico) {
        String sql = " select rec.MOTIVO " +
            " from RECURSOLICITACAO rec " +
            " where rec.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasRecursoLicitacao()));
    }

    private static List<ColunaConsulta> getColunasRecursoLicitacao() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Motivo ", String.class));
        return colunas;
    }

    private void buscarDadosMapaComparativo(HistoricoProcessoLicitatorio historico) {
        String sql = " select lic.TIPOAPURACAO," +
            " lic.RESUMODOOBJETO " +
            " from certame cert " +
            " left join licitacao lic on cert.LICITACAO_ID = lic.ID " +
            " where cert.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasMapaComparativo()));
    }

    private static List<ColunaConsulta> getColunasMapaComparativo() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Tipo de Apuração ", TipoApuracaoLicitacao.class));
        colunas.add(new ColunaConsulta("Resumo do Objeto ", String.class));
        return colunas;
    }

    private void buscarDadosMapaComparativoTecnicaPreco(HistoricoProcessoLicitatorio historico) {
        String sql = " select lic.RESUMODOOBJETO " +
            " from MAPACOMTECPREC matec " +
            " left join licitacao lic on matec.LICITACAO_ID = lic.ID " +
            " where matec.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasMapaComparativoTecnicaPreco()));
    }

    private static List<ColunaConsulta> getColunasMapaComparativoTecnicaPreco() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Resumo do Objeto ", String.class));
        return colunas;
    }

    private void buscarDadosRepactuacaoPreco(HistoricoProcessoLicitatorio historico) {
        String sql = " select repac.JUSTIFICATIVADOPEDIDO, " +
            "       repac.JUSTIFICATIVADOPARECER " +
            "       from REPACTUACAOPRECO repac  " +
            " where repac.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasRepactuacaoPreco()));
    }

    private static List<ColunaConsulta> getColunasRepactuacaoPreco() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Justificativa do Pedido ", String.class));
        colunas.add(new ColunaConsulta("Justificativa do Parecer ", String.class));
        return colunas;
    }

    private void buscarDadosProximoVencedorLicitacao(HistoricoProcessoLicitatorio historico) {
        String sql = " select coalesce(FORMATACPFCNPJ(pf.CPF), FORMATACPFCNPJ(pj.CNPJ)) || ' - '||  coalesce(pf.NOME, pj.RAZAOSOCIAL) " +
            "    from PROXIMOVENCEDORLICITACAO pvl " +
            "         inner join propostafornecedor pfor on pvl.VENCEDORATUAL_ID = pfor.ID " +
            "         inner join pessoa p on pfor.FORNECEDOR_ID = p.ID " +
            "         left join pessoafisica pf on p.ID = pf.ID " +
            "         left join pessoajuridica pj on p.ID = pj.ID " +
            " where pvl.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasProximoVencedorLicitacao()));
    }

    private static List<ColunaConsulta> getColunasProximoVencedorLicitacao() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Vencedor Atual ", String.class));
        return colunas;
    }

    private void buscarDadosAtaRegistroPreco(HistoricoProcessoLicitatorio historico) {
        String sql = "" +
            " select ata.datavencimento as vencimento, " +
            "        ata.gerenciadora   as gerenciarora," +
            "        ata.id as id_ata " +
            " from ataregistropreco ata " +
            " where ata.id = :idObjeto " +
            " order by ata.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object[]> resultList = q.getResultList();
        List<Object> objetos = Lists.newArrayList();
        for (Object[] obj : resultList) {
            Object[] ata = new Object[3];
            ata[0] = (Date) obj[0];
            ata[1] = (BigDecimal) obj[1];

            AtaRegistroPreco ataRegistroPreco = em.find(AtaRegistroPreco.class, ((BigDecimal) obj[2]).longValue());
            FiltroSaldoProcessoLicitatorioVO filtroSaldoVO = new FiltroSaldoProcessoLicitatorioVO(TipoExecucaoProcesso.ATA_REGISTRO_PRECO);
            filtroSaldoVO.setProcessoIrp(licitacaoFacade.isLicitacaoIRP(ataRegistroPreco.getLicitacao()));
            filtroSaldoVO.setTipoAvaliacaoLicitacao(ataRegistroPreco.getLicitacao().getTipoAvaliacao());
            filtroSaldoVO.setIdProcesso(ataRegistroPreco.getLicitacao().getId());
            filtroSaldoVO.setUnidadeOrganizacional(ataRegistroPreco.getUnidadeOrganizacional());
            SaldoProcessoLicitatorioVO saldoProcesso = saldoProcessoLicitatorioFacade.getSaldoProcesso(filtroSaldoVO);
            ata[2] = saldoProcesso.getValorAtualAta();
            objetos.add(ata);
        }
        historico.setView(criarViewColunas(objetos, getColunasAtaRegistroPreco()));
    }

    private static List<ColunaConsulta> getColunasAtaRegistroPreco() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Data de Vencimento", Date.class));
        colunas.add(new ColunaConsulta("Gerenciadora", Boolean.class));
        colunas.add(new ColunaConsulta("Valor (R$)", BigDecimal.class));
        return colunas;
    }

    private void buscarDadosPregaoPorItem(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) || ' - ' || coalesce(pf.nome, pj.razaosocial) as vencedor, " +
            "        ipc.numero || ' - ' || oc.descricao                                                     as item, " +
            "        lic.tipoapuracao                                                                        as tipo_apuracao, " +
            "        lv.valor                                                                                as valor_lance" +
            " from pregao pg " +
            "         inner join itempregao ip on ip.pregao_id = pg.id " +
            "         inner join licitacao lic on lic.id = pg.licitacao_id " +
            "         inner join itempregaolancevencedor lv on lv.id = ip.itempregaolancevencedor_id " +
            "         inner join lancepregao lance on lance.id = lv.lancepregao_id " +
            "         inner join propostafornecedor prop on prop.id = lance.propostafornecedor_id " +
            "         inner join pessoa p on p.id = prop.fornecedor_id " +
            "         left join pessoafisica pf on pf.id = p.id " +
            "         left join pessoajuridica pj on pj.id = p.id " +
            "         left join itpreitpro item on item.itempregao_id = ip.id " +
            "         left join itprelotpro lote on lote.itempregao_id = ip.id " +
            "         left join itempregaoloteitemprocesso iplote on iplote.itempregaoloteprocesso_id = lote.id " +
            "         inner join itemprocessodecompra ipc on ipc.id = coalesce(item.itemprocessodecompra_id, iplote.itemprocessodecompra_id) " +
            "         inner join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
            "         inner join objetocompra oc on oc.id = itemsol.objetocompra_id " +
            " where pg.id = :idObjeto " +
            " order by ipc.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.getViewDetalhes().add(criarViewTitulo("Vencedores"));
        historico.getViewDetalhes().add(criarViewColunas(objetos, getColunasPregaoPorItem()));
    }

    private static List<ColunaConsulta> getColunasPregaoPorItem() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Vencedor", String.class));
        colunas.add(new ColunaConsulta("Item", String.class));
        colunas.add(new ColunaConsulta("Tipo de Apuração", TipoApuracaoLicitacao.class));
        colunas.add(new ColunaConsulta("Lance Vencedor", BigDecimal.class));
        return colunas;
    }

    private void buscarDadosParecerLicitacao(HistoricoProcessoLicitatorio historico) {
        String sql = " select pl.observacoes from parecerlicitacao pl " +
            "         where pl.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasParecerLicitacao()));
    }

    private static List<ColunaConsulta> getColunasParecerLicitacao() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Observações", String.class));
        return colunas;
    }

    private void buscarDadosCredenciamento(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select lic.emitidaem               as aberta_en, " +
            "        lic.numero                 as numero_processo, " +
            "        lic.resumodoobjeto         as objeto, " +
            "        lic.localdeentrega         as local_entrega, " +
            "        lic.valormaximo            as valor " +
            " from licitacao lic" +
            " where lic.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasCredenciamento()));
    }

    private static List<ColunaConsulta> getColunasCredenciamento() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Data", Timestamp.class));
        colunas.add(new ColunaConsulta("Número do Processo", Integer.class));
        colunas.add(new ColunaConsulta("Resumo do Objeto", String.class));
        colunas.add(new ColunaConsulta("Local de Entrega", String.class));
        colunas.add(new ColunaConsulta("Valor (R$)", BigDecimal.class));
        return colunas;
    }

    private void buscarDadosLicitacao(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select lic.modalidadelicitacao    as modalidade, " +
            "        lic.naturezadoprocedimento as natureza, " +
            "        lic.abertaem               as aberta_en, " +
            "        lic.numero                 as numero_processo, " +
            "        lic.resumodoobjeto         as objeto, " +
            "        lic.localdeentrega         as local_entrega, " +
            "        lic.valormaximo            as valor " +
            " from licitacao lic" +
            " where lic.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasLicitacao()));
    }

    private static List<ColunaConsulta> getColunasLicitacao() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Modalidade", ModalidadeLicitacao.class));
        colunas.add(new ColunaConsulta("Natureza do Procedimento", TipoNaturezaDoProcedimentoLicitacao.class));
        colunas.add(new ColunaConsulta("Aberta em", Timestamp.class));
        colunas.add(new ColunaConsulta("Número do Processo", Integer.class));
        colunas.add(new ColunaConsulta("Resumo do Objeto", String.class));
        colunas.add(new ColunaConsulta("Local de Entrega", String.class));
        colunas.add(new ColunaConsulta("Valor (R$)", BigDecimal.class));
        return colunas;
    }

    private void buscarDadosParticipantesLicitacao(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select lf.codigo                                                                              as codigo, " +
            "       FORMATACPFCNPJ(coalesce(pf.cpf, pj.cnpj)) || ' - ' || coalesce(pf.nome, pj.razaosocial) as forneceddor," +
            "       lf.tipoclassificacaofornecedor                                                          as classificacao " +
            " from licitacaofornecedor lf " +
            "         inner join pessoa p on p.id = lf.empresa_id " +
            "         left join pessoafisica pf on pf.id = p.id " +
            "         left join pessoajuridica pj on pj.id = p.id " +
            " where lf.licitacao_id = :idObjeto" +
            " order by lf.codigo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.getViewDetalhes().add(criarViewTitulo(historico.getTipoMovimento().isLicitacao() ? "Participantes" : "Credenciados"));
        historico.getViewDetalhes().add(criarViewColunas(objetos, getColunasParticipantesLicitacao(historico.getTipoMovimento())));
    }

    private static List<ColunaConsulta> getColunasParticipantesLicitacao(TipoMovimentoProcessoLicitatorio tipoMov) {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Código", Integer.class));
        colunas.add(new ColunaConsulta(tipoMov.isLicitacao() ? "Participante" : "Credenciado", String.class));
        colunas.add(new ColunaConsulta("Classificação", TipoClassificacaoFornecedor.class));
        return colunas;
    }

    private void buscarDadosPropostasLicitacao(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select prop.dataproposta                                                                       as data_proposta, " +
            "       formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) || ' - ' || coalesce(pf.nome, pj.razaosocial) as forneceddor, " +
            "       prop.validadedaproposta || ' - ' || prop.tipoprazovalidade                              as validade " +
            " from propostafornecedor prop " +
            "         inner join pessoa p on p.id = prop.fornecedor_id " +
            "         left join pessoafisica pf on pf.id = p.id " +
            "         left join pessoajuridica pj on pj.id = p.id " +
            " where prop.licitacao_id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.getViewDetalhes().add(criarViewTitulo(historico.getTipoMovimento().isLicitacao() ? "Propostas dos Fornecedores" : "Propostas dos Credenciados"));
        historico.getViewDetalhes().add(criarViewColunas(objetos, getColunasParticipantesPropostasLicitacao(historico.getTipoMovimento())));
    }

    private static List<ColunaConsulta> getColunasParticipantesPropostasLicitacao(TipoMovimentoProcessoLicitatorio tipoMov) {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Data da Proposta", Date.class));
        colunas.add(new ColunaConsulta(tipoMov.isLicitacao() ? "Fornecedor" : "Credenciado", String.class));
        colunas.add(new ColunaConsulta("Validade", String.class));
        return colunas;
    }

    private void buscarDadosDispensa(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select dl.modalidade      as modalidade, " +
            "        dl.resumodoobjeto  as objeto, " +
            "        dl.localdeentrega  as local_entrega, " +
            "        dl.valormaximo     as valor " +
            " from dispensadelicitacao dl " +
            " where dl.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasDispensa()));
    }

    private static List<ColunaConsulta> getColunasDispensa() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Modalidade", ModalidadeLicitacao.class));
        colunas.add(new ColunaConsulta("Resumo do Objeto", String.class));
        colunas.add(new ColunaConsulta("Local de Entrega", String.class));
        colunas.add(new ColunaConsulta("Valor (R$)", BigDecimal.class));
        return colunas;
    }

    private void buscarDadosProcessoCompra(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select pc.tipoprocessodecompra as tipo_processo, " +
            "        pc.tiposolicitacao      as tipo_solicitacao, " +
            "        pc.descricao            as descricao " +
            " from processodecompra pc " +
            " where pc.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasProcessoCompra()));
    }

    private static List<ColunaConsulta> getColunasProcessoCompra() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Tipo de Processo", TipoProcessoDeCompra.class));
        colunas.add(new ColunaConsulta("Tipo de Solicitação", TipoSolicitacao.class));
        colunas.add(new ColunaConsulta("Descrição", String.class));
        return colunas;
    }

    private void buscarDadosReservaSolicitacaoCompra(HistoricoProcessoLicitatorio historico) {
        String sql = " select " +
            "       item.tipoobjetocompra                      as tipo_objeto, " +
            "       ex.ano                                     as exercicio, " +
            "       cdesp.codigo || ' - ' || cdesp.descricao   as conta_despesa, " +
            "       cd.codigo || ' - ' || cd.descricao         as destinacao_recurso, " +
            "       fonte.valor                                as valor " +
            "   from dotsolmat dot " +
            "         inner join dotacaosolmatitem item on item.dotacaosolicitacaomaterial_id = dot.id " +
            "         inner join dotacaosolmatitemfonte fonte on fonte.dotacaosolmatitem_id = item.id " +
            "         inner join fontedespesaorc fdo on fdo.id = fonte.fontedespesaorc_id " +
            "         inner join provisaoppafonte ppf on ppf.id = fdo.provisaoppafonte_id " +
            "         inner join conta cd on cd.id = ppf.destinacaoderecursos_id " +
            "         inner join despesaorc do on do.id = fdo.despesaorc_id " +
            "         inner join provisaoppadespesa ppd on ppd.id = do.provisaoppadespesa_id " +
            "         inner join conta cdesp on cdesp.id = ppd.contadedespesa_id " +
            "         inner join exercicio ex on ex.id = fonte.exercicio_id " +
            "   where dot.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();

        historico.getViewDetalhes().add(criarViewTitulo("Reservas"));
        historico.getViewDetalhes().add(criarViewColunas(objetos, getColunasReservaSolicitacaoCompra()));
    }

    private static List<ColunaConsulta> getColunasReservaSolicitacaoCompra() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Tipo Obj. de Compra", TipoObjetoCompra.class));
        colunas.add(new ColunaConsulta("Exercício", Integer.class));
        colunas.add(new ColunaConsulta("Conta de Despesa", String.class));
        colunas.add(new ColunaConsulta("Destinação de Recurso", String.class));
        colunas.add(new ColunaConsulta("Valor (R$)", BigDecimal.class));
        return colunas;
    }

    private void buscarDadosAvaliacaoSolicitacaoCompra(HistoricoProcessoLicitatorio historico) {
        String sql = " select " +
            "             ava.motivo        as motivo " +
            "           from avaliacaosolcompra ava " +
            "         where ava.id = :idObjeto  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasAvaliacaoSolicitacaoCompra()));
    }

    private static List<ColunaConsulta> getColunasAvaliacaoSolicitacaoCompra() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Motivo", String.class));
        return colunas;
    }

    private void buscarDadosSolicitacaoCompra(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select sol.tiposolicitacao                                       as tipo_sol, " +
            "       sol.modalidadelicitacao                                    as modalidade, " +
            "       sol.tipoavaliacao                                          as tipo_avaliacao, " +
            "       sol.tipoapuracao                                           as tipo_apuracao, " +
            "       sol.descricao                                              as objeto, " +
            "       sol.vigencia || ' ' || lower(sol.tipoprazodevigencia)      as prazo_vigencia " +
            " from solicitacaomaterial sol " +
            " where sol.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasSolicitacaoCompra()));
    }

    private static List<ColunaConsulta> getColunasSolicitacaoCompra() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Tipo", TipoSolicitacao.class));
        colunas.add(new ColunaConsulta("Modalidade", ModalidadeLicitacao.class));
        colunas.add(new ColunaConsulta("Avaliação", TipoAvaliacaoLicitacao.class));
        colunas.add(new ColunaConsulta("Apuração", TipoApuracaoLicitacao.class));
        colunas.add(new ColunaConsulta("Descrição", String.class));
        colunas.add(new ColunaConsulta("Vigência", String.class));
        return colunas;
    }

    private void buscarDadosCotacao(HistoricoProcessoLicitatorio historico) {
        String sql = " select cot.validadecotacao as data, " +
            "                 cot.descricao       as objeto " +
            "          from cotacao cot where cot.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasCotacao()));
    }

    private static List<ColunaConsulta> getColunasCotacao() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Validade", Date.class));
        colunas.add(new ColunaConsulta("Objeto", String.class));
        return colunas;
    }

    private void buscarDadosFornecedoresCotacao(HistoricoProcessoLicitatorio historico) {
        String sql = "" +
            " select distinct coalesce(pfis.nome, pjur.razaosocial) || ' - ' || coalesce(FORMATACPFCNPJ(pfis.cpf), FORMATACPFCNPJ(pjur.cnpj)) as fornecedor " +
            " from cotacao cot" +
            "         inner join LOTECOTACAO on cot.ID = LOTECOTACAO.COTACAO_ID" +
            "         inner join ITEMCOTACAO on LOTECOTACAO.ID = ITEMCOTACAO.LOTECOTACAO_ID" +
            "         inner join VALORCOTACAO on ITEMCOTACAO.ID = VALORCOTACAO.ITEMCOTACAO_ID" +
            "         inner join pessoa p on VALORCOTACAO.FORNECEDOR_ID = p.ID" +
            "         left join pessoafisica pfis on p.ID = pfis.ID" +
            "         left join pessoajuridica pjur on p.ID = pjur.ID" +
            " where cot.id = :idObjeto";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.getViewDetalhes().add(criarViewTitulo("Fornecedores"));
        historico.getViewDetalhes().add(criarViewColunas(objetos, getColunasFornecedoresCotacao()));
    }

    private static List<ColunaConsulta> getColunasFornecedoresCotacao() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Fornecedor", String.class));
        return colunas;
    }

    private void buscarDadosIrp(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select " +
            "    irp.tipoapuracaolicitacao as tipo_apuracao, " +
            "    irp.fimvigencia fim_vigencia, " +
            "    irp.descricao as descricao " +
            "from intencaoregistropreco irp " +
            "where irp.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasIrp()));
    }

    private static List<ColunaConsulta> getColunasIrp() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Tipo de Apuração", TipoApuracaoLicitacao.class));
        colunas.add(new ColunaConsulta("Final de Vigência", Date.class));
        colunas.add(new ColunaConsulta("Objeto", String.class));
        return colunas;
    }

    private void buscarDadosParticipantesIrp(HistoricoProcessoLicitatorio historico) {
        String sql = " " +
            " select part.numero                        as numero, " +
            "       vw.codigo || ' - ' || vw.descricao as participane, " +
            "       part.datainteresse                 as data_interesse, " +
            "       part.situacao                      as situacao " +
            " from participanteirp part " +
            "         inner join unidadeparticipanteirp unid on unid.participanteirp_id = part.id " +
            "         inner join vwhierarquiaadministrativa vw on vw.subordinada_id = unid.unidadeparticipante_id " +
            " where part.intencaoregistrodepreco_id = :idObjeto " +
            "  and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(vw.iniciovigencia) and coalesce(trunc(vw.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            " order by part.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List<Object> objetos = q.getResultList();
        historico.getViewDetalhes().add(criarViewTitulo("Participantes"));
        historico.getViewDetalhes().add(criarViewColunas(objetos, getColunasParticipantesIrp()));
    }

    private static List<ColunaConsulta> getColunasParticipantesIrp() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Número", Integer.class));
        colunas.add(new ColunaConsulta("Participante", String.class));
        colunas.add(new ColunaConsulta("Data de Interesse", Date.class));
        colunas.add(new ColunaConsulta("Situação", SituacaoIRP.class));
        return colunas;
    }

    private void buscarDadosFormulario(HistoricoProcessoLicitatorio historico) {
        String sql = " select fc.tiposolicitacao," +
            "                 fc.tipoapuracaolicitacao," +
            "                 fc.objeto as objeto  " +
            "          from formulariocotacao fc " +
            "          where fc.id = :idObjeto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjeto", historico.getId());
        List<Object> objetos = q.getResultList();
        historico.setView(criarViewColunas(objetos, getColunasFormulario()));
    }

    private static List<ColunaConsulta> getColunasFormulario() {
        List<ColunaConsulta> colunas = Lists.newArrayList();
        colunas.add(new ColunaConsulta("Tipo de Solicitação", TipoSolicitacao.class));
        colunas.add(new ColunaConsulta("Tipo de Apuração", TipoApuracaoLicitacao.class));
        colunas.add(new ColunaConsulta("Objeto", String.class));
        return colunas;
    }

    private View criarViewTitulo(String titulo) {
        View view = new View();
        ColunaView colunaTitulo = new ColunaView(null, titulo, String.class, false, null);
        colunaTitulo.setTitulo(true);
        view.getColunas().add(colunaTitulo);
        return view;
    }

    private View criarViewColunas(List<Object> objetos, List<ColunaConsulta> colunas) {
        View view = new View();
        for (Object objeto : objetos) {
            List<ColunaView> colunaViews = Lists.newArrayList();
            if (objeto instanceof Object[]) {
                Object[] array = (Object[]) objeto;
                int posicao = 0;
                for (Object o : array) {
                    ColunaView colunaView = preparaInstanciaColunaView(o, colunas.get(posicao));
                    colunaViews.add(colunaView);
                    posicao++;
                }
            } else {
                ColunaView coluna = preparaInstanciaColunaView(objeto, colunas.get(0));
                colunaViews.add(coluna);
            }
            if (view.getColunas().isEmpty()) {
                view.getColunas().addAll(colunaViews);
            }
            ObjetoView objetoView = new ObjetoView(objeto, colunaViews);
            view.getObjetos().add(objetoView);
        }
        return view;
    }

    private ColunaView preparaInstanciaColunaView(Object o, ColunaConsulta coluna) {
        if (o == null) {
            return new ColunaView(" - ", coluna.getNomeColuna(), String.class, false, o);
        }
        ColunaView colunaView = new ColunaView(o, coluna.getNomeColuna(), coluna.getClasse(), false, o);
        if (colunaView.getClasse().equals(BigDecimal.class)) {
            colunaView.setValor(Util.getValorRemovendoRS(new BigDecimal(((Number) o).doubleValue())));
        }
        if (colunaView.getClasse().equals(BigDecimal.class) && coluna.getNomeColuna().contains("Número")) {
            colunaView.setValor(Util.getValorSemPontoEVirgulas(new BigDecimal(((Number) o).intValue())));
        }
        if (colunaView.getClasse().equals(Date.class)) {
            colunaView.setValor(DataUtil.getDataFormatada((Date) o));
        }
        if (colunaView.getClasse().equals(Timestamp.class)) {
            colunaView.setValor(DataUtil.getDataFormatadaDiaHora((Date) o));
        }
        if (colunaView.getClasse().equals(Boolean.class)) {
            colunaView.setValor(((BigDecimal) o).compareTo(BigDecimal.ONE) == 0 ? "Sim" : "Não");
        }
        if (colunaView.getClasse().equals(String.class)) {
            String string = (String) colunaView.getValor();
            string = string.length() > 100 ? string.substring(0, 100) + "..." : string;
            colunaView.setValor(string);
        }
        if (colunaView.getClasse().isEnum()) {
            try {
                for (Field field : colunaView.getClasse().getDeclaredFields()) {
                    if (field.getName().equals(o.toString())) {
                        Enum anEnum = Enum.valueOf((Class<? extends Enum>) colunaView.getClasse(), field.getName());
                        colunaView.setValor(anEnum.toString());
                    }
                }
            } catch (Exception e) {
                colunaView.setValor(o.toString());
            }
        }
        return colunaView;
    }

    public Object recuperarObjetoParaRelatorio(HistoricoProcessoLicitatorio historico) {
        switch (historico.getTipoMovimento()) {
            case IRP:
                return em.find(IntencaoRegistroPreco.class, historico.getId());
            case FORMULARIO_COTACAO:
                return em.find(FormularioCotacao.class, historico.getId());
            case COTACAO:
                return em.find(Cotacao.class, historico.getId());
            case SOLICITACAO_COMPRA:
                return em.find(SolicitacaoMaterial.class, historico.getId());
            case AVALIACAO_SOLICITACAO_COMPRA:
                return em.find(AvaliacaoSolicitacaoDeCompra.class, historico.getId());
            case PREGAO_POR_ITEM:
                Pregao pregao = em.find(Pregao.class, historico.getId());
                pregao.setListaDeItemPregao(pregaoFacade.buscarItensPregao(pregao));
                return pregao;
            case PREGAO_POR_LOTE:
                return em.find(Pregao.class, historico.getId());
            default:
                return null;
        }
    }
}
