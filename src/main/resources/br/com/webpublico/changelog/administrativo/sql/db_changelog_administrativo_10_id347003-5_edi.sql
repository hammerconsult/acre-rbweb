create or replace view vwsaldoprocessosemcontrato as
select distinct idItemProcesso                                        as idItemProcesso,
                numeroLote                                            as numeroLote,
                numeroItem                                            as numeroItem,
                idProcesso                                            as idProcesso,
                idFornecedor                                          as idFornecedor,
                idUnidade                                             as idUnidade,
                idAta                                                 as idAta,
                origemSaldo                                           as origemSaldo,
                naturezaSaldo                                         as naturezaSaldo,
                percentualDesconto                                    as percentualDesconto,
                quantidadeProcesso                                    as quantidadeProcesso,
                quantidadeAcrescimo                                   as quantidadeAcrescimo,
                valorUnitarioHomolgado                                as valorUnitarioHomolgado,
                round(quantidadeProcesso * valorUnitarioHomolgado, 2) as valorTotalHomolgado,
                valorAcrescimo                                        as valorAcrescimo,

                (select coalesce(sum(ic.quantidadetotalcontrato), 0) - coalesce(sum(ic.quantidaderescisao), 0)
                 from itemcontrato ic
                          inner join contrato c on c.id = ic.contrato_id
                          inner join unidadecontrato uc on uc.contrato_id = c.id
                     and sysdate between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), sysdate)
                     and uc.unidadeadministrativa_id = base.idUnidade
                          left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id
                          left join itemcontratoitemirp icii on icii.itemcontrato_id = ic.id
                          left join itempropfornec ipf
                                    on ipf.id = coalesce(icpf.itempropostafornecedor_id, icii.itempropostafornecedor_id)
                          left join itemcontratoitempropdisp icpd on icpd.itemcontrato_id = ic.id
                          left join itempropostafornedisp ipfd on icpd.itempropfornecdispensa_id = ipfd.id
                          inner join itemprocessodecompra itemc
                                     on itemc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id)
                 where itemc.id = base.idItemProcesso
                   and c.contratado_id = base.idFornecedor)           as quantidadeContratada,

                (select coalesce(sum(ic.valortotal), 0) - coalesce(sum(ic.valortotalrescisao), 0)
                 from itemcontrato ic
                          inner join contrato c on c.id = ic.contrato_id
                          inner join unidadecontrato uc on uc.contrato_id = c.id
                     and sysdate between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), sysdate)
                     and uc.unidadeadministrativa_id = base.idUnidade
                          left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id
                          left join itemcontratoitemirp icii on icii.itemcontrato_id = ic.id
                          left join itempropfornec ipf
                                    on ipf.id = coalesce(icpf.itempropostafornecedor_id, icii.itempropostafornecedor_id)
                          left join itemcontratoitempropdisp icpd on icpd.itemcontrato_id = ic.id
                          left join itempropostafornedisp ipfd on icpd.itempropfornecdispensa_id = ipfd.id
                          inner join itemprocessodecompra itemc
                                     on itemc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id)
                 where itemc.id = base.idItemProcesso
                   and c.contratado_id = base.idFornecedor)           as valorContratado,

                (select coalesce(sum(itemex.quantidade), 0)
                 from execucaoprocessoitem itemex
                          inner join itemprocessodecompra ipc on itemex.itemprocessocompra_id = ipc.id
                          inner join execucaoprocesso ex on ex.id = itemex.execucaoprocesso_id
                 where ipc.id = base.idItemProcesso
                   and ex.idorigem = base.idProcesso
                   and ex.fornecedor_id = base.idFornecedor)          as quantidadeExecutada,

                (select coalesce(sum(itemex.valortotal), 0)
                 from execucaoprocessoitem itemex
                          inner join itemprocessodecompra ipc on itemex.itemprocessocompra_id = ipc.id
                          inner join execucaoprocesso ex on ex.id = itemex.execucaoprocesso_id
                 where ipc.id = base.idItemProcesso
                   and ex.idorigem = base.idProcesso
                   and ex.fornecedor_id = base.idFornecedor)          as valorExecutado,

                (select coalesce(sum(itemexest.quantidade), 0)
                 from execucaoprocessoempestitem itemexest
                          inner join execucaoprocessoitem itemex on itemexest.execucaoprocessoitem_id = itemex.id
                          inner join itemprocessodecompra ipc on itemex.itemprocessocompra_id = ipc.id
                          inner join execucaoprocesso ex on ex.id = itemex.execucaoprocesso_id
                 where ipc.id in base.idItemProcesso
    and ex.idorigem = base.idProcesso
    and ex.fornecedor_id = base.idFornecedor)          as quantidadeEstornada,

    (select coalesce(sum(itemexest.valortotal), 0)
from execucaoprocessoempestitem itemexest
    inner join execucaoprocessoitem itemex on itemexest.execucaoprocessoitem_id = itemex.id
    inner join itemprocessodecompra ipc on itemex.itemprocessocompra_id = ipc.id
    inner join execucaoprocesso ex on ex.id = itemex.execucaoprocesso_id
where ipc.id in base.idItemProcesso
  and ex.idorigem = base.idProcesso
  and ex.fornecedor_id = base.idFornecedor)          as valorEstornado

from (select distinct coalesce(ipc.quantidade, 1)          as quantidadeProcesso,
    case
    when lic.tipoavaliacao = 'MAIOR_DESCONTO' then
    case ic.tipocontrole
    when 'VALOR' then its.preco
    else round(its.preco - ((iplv.percentualdesconto / 100) * its.preco), 2)
    end
    else iplv.valor
    end                              as valorUnitarioHomolgado,
    coalesce(iplv.percentualdesconto, 0) as percentualDesconto,
    0                                    as quantidadeAcrescimo,
    0                                    as valorAcrescimo,

    ipc.id                               as idItemProcesso,
    ipc.numero                           as numeroItem,
    lpc.numero                           as numeroLote,
    lic.id                               as idProcesso,
    case
    when ata.id is not null then ata.unidadeorganizacional_id
    else pc.unidadeorganizacional_id
    end                              as idUnidade,
    coalesce(ata.id, null)               as idAta,
    'ATA_REGISTRO_PRECO'                 as origemSaldo,
    'ORIGINAL'                           as naturezaSaldo,
    pes.id                               as idFornecedor
    from itempregao ip
    inner join pregao p on p.id = ip.pregao_id
    inner join licitacao lic on lic.id = p.licitacao_id
    inner join processodecompra pc on pc.id = lic.processodecompra_id
    inner join itempregaolancevencedor iplv on iplv.id = ip.itempregaolancevencedor_id
    inner join lancepregao lp on lp.id = iplv.lancepregao_id
    inner join propostafornecedor prop on prop.id = lp.propostafornecedor_id
    inner join pessoa pes on pes.id = prop.fornecedor_id
    inner join itpreitpro ipip on ipip.itempregao_id = ip.id
    inner join itemprocessodecompra ipc on ipc.id = ipip.itemprocessodecompra_id
    inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
    inner join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id
    inner join itemcotacao ic on ic.id = its.itemcotacao_id
    inner join licitacaofornecedor licfor on licfor.licitacao_id = lic.id
    inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
    inner join itempropfornec ipf on ipf.itemprocessodecompra_id = ipc.id
    left join ataregistropreco ata on ata.licitacao_id = lic.id
    where licfor.empresa_id = prop.fornecedor_id
    and sfl.tiposituacao = 'HOMOLOGADA'
    and lic.tipoapuracao = 'ITEM'
    union all
    select distinct coalesce(ipc.quantidade, 1)              as quantidadeProcesso,
    item.valor                               as valorUnitarioHomolgado,
    coalesce(iplv.percentualdesconto, 0)     as percentualDesconto,
    0                                        as quantidadeAcrescimo,
    0                                        as valorAcrescimo,
    ipc.id                                   as idItemProcesso,
    ipc.numero                               as numeroItem,
    lpc.numero                               as numeroLote,
    lic.id                                   as idProcesso,
    case
    when ata.id is not null then ata.unidadeorganizacional_id
    else pc.unidadeorganizacional_id end as idUnidade,
    coalesce(ata.id, null)                   as idAta,
    'ATA_REGISTRO_PRECO'                     as origemSaldo,
    'ORIGINAL'                               as naturezaSaldo,
    pes.id                                   as idFornecedor
    from itempregao ip
    inner join pregao p on p.id = ip.pregao_id
    inner join licitacao lic on lic.id = p.licitacao_id
    inner join processodecompra pc on pc.id = lic.processodecompra_id
    inner join itempregaolancevencedor iplv on iplv.id = ip.itempregaolancevencedor_id
    inner join lancepregao lp on lp.id = iplv.lancepregao_id
    inner join propostafornecedor prop on prop.id = lp.propostafornecedor_id
    inner join pessoa pes on pes.id = prop.fornecedor_id
    inner join itprelotpro iplp on iplp.itempregao_id = ip.id
    inner join itempregaoloteitemprocesso item on item.itempregaoloteprocesso_id = iplp.id
    inner join itemprocessodecompra ipc on ipc.id = item.itemprocessodecompra_id
    inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
    inner join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id
    inner join itemcotacao ic on ic.id = its.itemcotacao_id
    inner join licitacaofornecedor licfor on licfor.licitacao_id = lic.id
    inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
    inner join itempropfornec ipf on ipf.itemprocessodecompra_id = ipc.id
    left join ataregistropreco ata on ata.licitacao_id = lic.id
    where licfor.empresa_id = prop.fornecedor_id
    and sfl.tiposituacao = 'HOMOLOGADA'
    and lic.tipoapuracao = 'LOTE'
    union all
    select distinct coalesce(ipc.quantidade, 1)              as quantidadeProcesso,
    ipf.preco                                as valorUnitarioHomolgado,
    coalesce(ipf.percentualdesconto, 0)      as percentualDesconto,
    0                                        as quantidadeAcrescimo,
    0                                        as valorAcrescimo,
    ipc.id                                   as idItemProcesso,
    ipc.numero                               as numeroItem,
    lpc.numero                               as numeroLote,
    lic.id                                   as idProcesso,
    case
    when ata.id is not null then ata.unidadeorganizacional_id
    else pc.unidadeorganizacional_id end as idUnidade,
    coalesce(ata.id, null)                   as idAta,
    'ATA_REGISTRO_PRECO'                     as origemSaldo,
    'ORIGINAL'                               as naturezaSaldo,
    pes.id                                   as idFornecedor
    from itemcertame ic
    inner join certame c on c.id = ic.certame_id
    inner join licitacao lic on lic.id = c.licitacao_id
    inner join processodecompra pc on pc.id = lic.processodecompra_id
    inner join itemcertameitempro icip on icip.itemcertame_id = ic.id
    inner join itempropfornec ipf on icip.itempropostavencedor_id = ipf.id
    inner join itemprocessodecompra ipc on icip.itemprocessodecompra_id = ipc.id
    inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
    inner join propostafornecedor prop on prop.id = ipf.propostafornecedor_id
    inner join pessoa pes on pes.id = prop.fornecedor_id
    inner join licitacaofornecedor licfor on licfor.licitacao_id = lic.id
    inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
    left join ataregistropreco ata on ata.licitacao_id = lic.id
    where ic.situacaoItemCertame = 'VENCEDOR'
    and licfor.empresa_id = prop.fornecedor_id
    and sfl.tiposituacao = 'HOMOLOGADA'
    union all
    select distinct coalesce(ipc.quantidade, 1)              as quantidadeProcesso,
    ipf.preco                                as valorUnitarioHomolgado,
    coalesce(lpf.percentualdesconto, 0)      as percentualDesconto,
    0                                        as quantidadeAcrescimo,
    0                                        as valorAcrescimo,
    ipc.id                                   as idItemProcesso,
    ipc.numero                               as numeroItem,
    lpc.numero                               as numeroLote,
    lic.id                                   as idProcesso,
    case
    when ata.id is not null then ata.unidadeorganizacional_id
    else pc.unidadeorganizacional_id end as idUnidade,
    coalesce(ata.id, null)                   as idAta,
    'ATA_REGISTRO_PRECO'                     as origemSaldo,
    'ORIGINAL'                               as naturezaSaldo,
    pes.id                                   as idFornecedor
    from itemcertame ic
    inner join certame c on c.id = ic.certame_id
    inner join licitacao lic on lic.id = c.licitacao_id
    inner join processodecompra pc on pc.id = lic.processodecompra_id
    inner join itemcertamelotepro iclp on iclp.itemcertame_id = ic.id
    inner join lotepropfornec lpf on lpf.id = iclp.lotepropfornecedorvencedor_id
    inner join itempropfornec ipf on ipf.lotepropostafornecedor_id = lpf.id
    inner join itemprocessodecompra ipc on ipf.itemprocessodecompra_id = ipc.id
    inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
    inner join propostafornecedor prop on prop.id = ipf.propostafornecedor_id
    inner join pessoa pes on pes.id = prop.fornecedor_id
    inner join licitacaofornecedor licfor on licfor.licitacao_id = lic.id
    inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
    inner join itempropfornec ipf on ipf.itemprocessodecompra_id = ipc.id
    left join ataregistropreco ata on ata.licitacao_id = lic.id
    where ic.situacaoItemCertame = 'VENCEDOR'
    and licfor.empresa_id = prop.fornecedor_id
    and sfl.tiposituacao = 'HOMOLOGADA'
    union all
    select distinct coalesce(ipc.quantidade, 1)              as quantidadeProcesso,
    ipf.preco                                as valorUnitarioHomolgado,
    coalesce(ipf.percentualdesconto, 0)      as percentualDesconto,
    0                                        as quantidadeAcrescimo,
    0                                        as valorAcrescimo,
    ipc.id                                   as idItemProcesso,
    ipc.numero                               as numeroItem,
    lpc.numero                               as numeroLote,
    lic.id                                   as idProcesso,
    case
    when ata.id is not null then ata.unidadeorganizacional_id
    else pc.unidadeorganizacional_id end as idUnidade,
    coalesce(ata.id, null)                   as idAta,
    'ATA_REGISTRO_PRECO'                     as origemSaldo,
    'ORIGINAL'                               as naturezaSaldo,
    pes.id                                   as idFornecedor
    from itemmapacomtecpre imc
    inner join mapacomtecprec mapa on mapa.id = imc.mapacomtecnicapreco_id
    inner join licitacao lic on lic.id = mapa.licitacao_id
    inner join processodecompra pc on pc.id = lic.processodecompra_id
    inner join itemmacotecprecitemproc icip on icip.itemmapacomtecnicapreco_id = imc.id
    inner join itempropfornec ipf on icip.itempropostavencedor_id = ipf.id
    inner join itemprocessodecompra ipc on ipf.itemprocessodecompra_id = ipc.id
    inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
    inner join propostafornecedor prop on prop.id = ipf.propostafornecedor_id
    inner join pessoa pes on pes.id = prop.fornecedor_id
    inner join licitacaofornecedor licfor on licfor.licitacao_id = lic.id
    inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
    left join ataregistropreco ata on ata.licitacao_id = lic.id
    where imc.situacaoitem = 'VENCEDOR'
    and licfor.empresa_id = prop.fornecedor_id
    and sfl.tiposituacao = 'HOMOLOGADA'
    union all
    select distinct coalesce(ipc.quantidade, 1)              as quantidadeProcesso,
    ipf.preco                                as valorUnitarioHomolgado,
    coalesce(lpf.percentualdesconto, 0)      as percentualDesconto,
    0                                        as quantidadeAcrescimo,
    0                                        as valorAcrescimo,
    ipc.id                                   as idItemProcesso,
    ipc.numero                               as numeroItem,
    lpc.numero                               as numeroLote,
    lic.id                                   as idProcesso,
    case
    when ata.id is not null then ata.unidadeorganizacional_id
    else pc.unidadeorganizacional_id end as idUnidade,
    coalesce(ata.id, null)                   as idAta,
    'ATA_REGISTRO_PRECO'                     as origemSaldo,
    'ORIGINAL'                               as naturezaSaldo,
    pes.id                                   as idFornecedor
    from itemmapacomtecpre imc
    inner join mapacomtecprec mapa on mapa.id = imc.mapacomtecnicapreco_id
    inner join licitacao lic on lic.id = mapa.licitacao_id
    inner join processodecompra pc on pc.id = lic.processodecompra_id
    inner join itemmacotecprecloteproc iclp on iclp.itemmapacomtecnicapreco_id = imc.id
    inner join lotepropfornec lpf on lpf.id = iclp.lotepropostavencedor_id
    inner join itempropfornec ipf on ipf.lotepropostafornecedor_id = lpf.id
    inner join itemprocessodecompra ipc on ipf.itemprocessodecompra_id = ipc.id
    inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
    inner join propostafornecedor prop on prop.id = ipf.propostafornecedor_id
    inner join pessoa pes on pes.id = prop.fornecedor_id
    inner join licitacaofornecedor licfor on licfor.licitacao_id = lic.id
    inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
    left join ataregistropreco ata on ata.licitacao_id = lic.id
    where imc.situacaoitem = 'VENCEDOR'
    and licfor.empresa_id = prop.fornecedor_id
    and sfl.tiposituacao = 'HOMOLOGADA'
    union all
    select distinct coalesce(ipc.quantidade, 1)          as quantidadeProcesso,
    item.preco                           as valorUnitarioHomolgado,
    0                                    as percentualDesconto,
    0                                    as quantidadeAcrescimo,
    0                                    as valorAcrescimo,
    ipc.id                               as idItemProcesso,
    ipc.numero                           as numeroItem,
    lpc.numero                           as numeroLote,
    disp.id                              as idProcesso,
    pc.unidadeorganizacional_id          as idUnidade,
    null                                 as idAta,
    'DISPENSA_LICITACAO_INEXIGIBILIDADE' as origemSaldo,
    'ORIGINAL'                           as naturezaSaldo,
    pes.id                               as idFornecedor
    from dispensadelicitacao disp
    inner join processodecompra pc on pc.id = disp.processodecompra_id
    inner join fornecedordisplic fdl on fdl.dispensadelicitacao_id = disp.id
    inner join propostafornecedordispensa prop on prop.id = fdl.propostafornecedordispensa_id
    inner join lotepropostafornedisp lote on lote.propostafornecedordispensa_id = prop.id
    inner join itempropostafornedisp item on item.lotepropostafornedisp_id = lote.id
    inner join itemprocessodecompra ipc on ipc.id = item.itemprocessodecompra_id
    inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
    inner join pessoa pes on pes.id = fdl.pessoa_id
    left join execucaoprocessodispensa exdisp on exdisp.dispensalicitacao_id = disp.id
    left join execucaoprocesso exproc on exdisp.execucaoprocesso_id = exproc.id
    and exproc.origem in ('ATA_REGISTRO_PRECO', 'DISPENSA_LICITACAO_INEXIGIBILIDADE')
    and exproc.idorigem = disp.id
    and exproc.fornecedor_id = pes.id
    union all
    select distinct 0                            as quantidadeProcesso,
    item.valorunitario           as valorUnitarioHomolgado,
    0                            as percentualDesconto,
    coalesce(item.quantidade, 0) as quantidadeAcrescimo,
    coalesce(item.valortotal, 0) as valorAcrescimo,
    ipc.id                       as idItemProcesso,
    ipc.numero                   as numeroItem,
    lpc.numero                   as numeroLote,
    ac.id                        as idProcesso,
    ata.unidadeorganizacional_id as idUnidade,
    ata.id                       as idAta,
    ac.tipoalteracaocontratual   as origemSaldo,
    'ACRESCIMO'                  as naturezaSaldo,
    pes.id                       as idFornecedor
    from movimentoalteracaocontitem item
    inner join movimentoalteracaocont mov on mov.id = item.movimentoalteracaocont_id
    inner join alteracaocontratual ac on ac.id = mov.alteracaocontratual_id
    inner join alteracaocontratualata aca on aca.alteracaocontratual_id = ac.id
    inner join ataregistropreco ata on ata.id = aca.ataregistropreco_id
    inner join licitacao lic on lic.id = ata.licitacao_id
    inner join processodecompra pc on pc.id = lic.processodecompra_id
    inner join itemprocessodecompra ipc on item.itemprocessocompra_id = ipc.id
    inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
    inner join pessoa pes on pes.id = item.fornecedor_id
    left join execucaoprocesso exproc on exproc.idorigem = ac.id
    and exproc.fornecedor_id = item.fornecedor_id
    and exproc.origem in ('ADITIVO', 'APOSTILAMENTO')
    and ac.situacao <> 'EM_ELABORACAO') base;
