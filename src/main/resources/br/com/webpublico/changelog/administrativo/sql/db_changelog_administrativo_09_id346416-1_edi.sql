create or replace view VWVALORESITEMPROCESSO as
select distinct id_item_processo,
                numero_lote,
                numero_item,
                id_processo,
                id_fornecedor,
                id_unidade,
                id_ata,
                qtde_processo,
                vl_unit_homologado,
                percentual_desc,
                round(qtde_processo * vl_unit_homologado, 2) as vl_total_homologado,
                coalesce((select sum(coalesce(itemcont.quantidadetotalcontrato, 0))
                          from itemcontrato itemcont
                                   inner join contrato c on c.id = itemcont.contrato_id
                                   inner join unidadecontrato uc on uc.contrato_id = c.id
                              and
                                                                    sysdate between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), sysdate)
                              and uc.unidadeadministrativa_id = id_unidade
                                   left join itemcontratoitempropdisp icid on icid.itemcontrato_id = itemcont.id
                                   left join itempropostafornedisp ipfd on ipfd.id = icid.itempropfornecdispensa_id
                                   left join itemcontratoitemsolext icise on icise.itemcontrato_id = itemcont.id
                                   left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id
                                   left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = itemcont.id
                                   left join itemcontratoitemirp icii on icii.itemcontrato_id = itemcont.id
                                   left join itemcontratoadesaoataint icaa
                                             on icaa.itempropostafornecedor_id = itemcont.id
                                   left join itempropfornec ipf on ipf.id = coalesce(
                              icpf.itempropostafornecedor_id,
                              icii.itempropostafornecedor_id,
                              icaa.itempropostafornecedor_id)
                          where coalesce(ipfd.itemprocessodecompra_id, ise.itemprocessocompra_id,
                                         ipf.itemprocessodecompra_id) = id_item_processo),
                         0)                                  as qtde_contratada,

                coalesce((select coalesce(sum(itemex.quantidade), 0)
                          from execucaoprocessoitem itemex
                                   inner join execucaoprocesso exproc on exproc.id = itemex.execucaoprocesso_id
                                   left join execucaoprocessoata exata
                                             on exata.execucaoprocesso_id = exproc.id
                                                 and exata.ataregistropreco_id = id_ata
                                   left join execucaoprocessodispensa exdisp
                                             on exdisp.execucaoprocesso_id = exproc.id
                                                 and exdisp.dispensalicitacao_id = id_processo
                          where itemex.itemprocessocompra_id = id_item_processo),
                         0)                                  as qtde_exec_sem_contrato,

                coalesce((select coalesce(sum(itemest.quantidade), 0)
                          from execucaoprocessoempestitem itemest
                                   inner join execucaoprocessoitem itemex
                                              on itemex.id = itemest.execucaoprocessoitem_id
                                   inner join execucaoprocesso exproc on exproc.id = itemex.execucaoprocesso_id
                                   left join execucaoprocessoata exata
                                             on exata.execucaoprocesso_id = exproc.id
                                                 and exata.ataregistropreco_id = id_ata
                                   left join execucaoprocessodispensa exdisp
                                             on exdisp.execucaoprocesso_id = exproc.id
                                                 and exdisp.dispensalicitacao_id = id_processo
                          where itemex.itemprocessocompra_id = id_item_processo),
                         0)                                  as qtde_est_exec_sem_contrato,

                coalesce((select sum(coalesce(itemcont.valortotal, 0))
                          from itemcontrato itemcont
                                   inner join contrato c on c.id = itemcont.contrato_id
                                   inner join unidadecontrato uc on uc.contrato_id = c.id
                              and
                                                                    sysdate between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), sysdate)
                              and uc.unidadeadministrativa_id = id_unidade
                                   left join itemcontratoitempropdisp icid on icid.itemcontrato_id = itemcont.id
                                   left join itempropostafornedisp ipfd on ipfd.id = icid.itempropfornecdispensa_id
                                   left join itemcontratoitemsolext icise on icise.itemcontrato_id = itemcont.id
                                   left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id
                                   left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = itemcont.id
                                   left join itemcontratoitemirp icii on icii.itemcontrato_id = itemcont.id
                                   left join itemcontratoadesaoataint icaa
                                             on icaa.itempropostafornecedor_id = itemcont.id
                                   left join itempropfornec ipf on ipf.id = coalesce(
                              icpf.itempropostafornecedor_id,
                              icii.itempropostafornecedor_id,
                              icaa.itempropostafornecedor_id)
                          where coalesce(ipfd.itemprocessodecompra_id, ise.itemprocessocompra_id,
                                         ipf.itemprocessodecompra_id) = id_item_processo),
                         0)                                  as vl_contratado,

                coalesce((select sum(coalesce(itemex.valortotal, 0))
                          from execucaoprocessoitem itemex
                                   inner join execucaoprocesso exproc on exproc.id = itemex.execucaoprocesso_id
                                   left join execucaoprocessoata exata
                                             on exata.execucaoprocesso_id = exproc.id
                                                 and exata.ataregistropreco_id = id_ata
                                   left join execucaoprocessodispensa exdisp
                                             on exdisp.execucaoprocesso_id = exproc.id
                                                 and exdisp.dispensalicitacao_id = id_processo
                          where itemex.itemprocessocompra_id = id_item_processo),
                         0)                                  as vl_exec_sem_contrato,

                coalesce((select sum(coalesce(itemest.valortotal, 0))
                          from execucaoprocessoempestitem itemest
                                   inner join execucaoprocessoitem itemex
                                              on itemex.id = itemest.execucaoprocessoitem_id
                                   inner join execucaoprocesso exproc on exproc.id = itemex.execucaoprocesso_id
                                   left join execucaoprocessoata exata
                                             on exata.execucaoprocesso_id = exproc.id
                                                 and exata.ataregistropreco_id = id_ata
                                   left join execucaoprocessodispensa exdisp
                                             on exdisp.execucaoprocesso_id = exproc.id
                                                 and exdisp.dispensalicitacao_id = id_processo
                          where itemex.itemprocessocompra_id = id_item_processo),
                         0)                                  as vl_est_exec_sem_contrato

from (select distinct coalesce(ipc.quantidade, 1)              as qtde_processo,
                      case
                          when l.tipoavaliacao = 'MAIOR_DESCONTO'
                              then case ic.tipocontrole
                                       when 'VALOR' then its.preco
                                       else round(its.preco - ((iplv.percentualdesconto / 100) * its.preco), 2) end
                          else iplv.valor end                  as vl_unit_homologado,
                      coalesce(iplv.percentualdesconto, 0)     as percentual_desc,
                      ipc.id                                   as id_item_processo,
                      ipc.numero                               as numero_item,
                      lpc.numero                               as numero_lote,
                      l.id                                     as id_processo,
                      case
                          when ata.id is not null then ata.unidadeorganizacional_id
                          else pc.unidadeorganizacional_id end as id_unidade,
                      coalesce(ata.id, null)                   as id_ata,
                      pes.id                                   as id_fornecedor
      from itempregao ip
               inner join pregao p on p.id = ip.pregao_id
               inner join licitacao l on l.id = p.licitacao_id
               inner join processodecompra pc on pc.id = l.processodecompra_id
               left join ataregistropreco ata on ata.licitacao_id = l.id
               inner join itempregaolancevencedor iplv on iplv.id = ip.itempregaolancevencedor_id
               inner join lancepregao lp on lp.id = iplv.lancepregao_id
               inner join propostafornecedor prop on prop.id = lp.propostafornecedor_id
               inner join pessoa pes on pes.id = prop.fornecedor_id
               inner join itpreitpro ipip on ipip.itempregao_id = ip.id
               inner join itemprocessodecompra ipc on ipc.id = ipip.itemprocessodecompra_id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
               inner join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id
               inner join itemcotacao ic on ic.id = its.itemcotacao_id
               inner join licitacaofornecedor licfor on licfor.licitacao_id = l.id
               inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
               inner join itempropfornec ipf on ipf.itemprocessodecompra_id = ipc.id
      where licfor.empresa_id = prop.fornecedor_id
        and sfl.tiposituacao = 'HOMOLOGADA'
      union all
      select distinct coalesce(ipc.quantidade, 1)              as qtde_processo,
                      item.valor                               as vl_unit_homologado,
                      coalesce(iplv.percentualdesconto, 0)     as percentual_desc,
                      ipc.id                                   as id_item_processo,
                      ipc.numero                               as numero_item,
                      lpc.numero                               as numero_lote,
                      l.id                                     as id_processo,
                      case
                          when ata.id is not null then ata.unidadeorganizacional_id
                          else pc.unidadeorganizacional_id end as id_unidade,
                      coalesce(ata.id, null)                   as id_ata,
                      pes.id                                   as id_fornecedor
      from itempregao ip
               inner join pregao p on p.id = ip.pregao_id
               inner join licitacao l on l.id = p.licitacao_id
               inner join processodecompra pc on pc.id = l.processodecompra_id
               left join ataregistropreco ata on ata.licitacao_id = l.id
               inner join itempregaolancevencedor iplv on iplv.id = ip.itempregaolancevencedor_id
               inner join lancepregao lp on lp.id = iplv.lancepregao_id
               inner join propostafornecedor prop on prop.id = lp.propostafornecedor_id
               inner join pessoa pes on pes.id = prop.fornecedor_id
               inner join itprelotpro iplp on iplp.itempregao_id = ip.id
               inner join itempregaoloteitemprocesso item
                          on item.itempregaoloteprocesso_id = iplp.id
               inner join itemprocessodecompra ipc on ipc.id = item.itemprocessodecompra_id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
               inner join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id
               inner join itemcotacao ic on ic.id = its.itemcotacao_id
               inner join licitacaofornecedor licfor on licfor.licitacao_id = l.id
               inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
               inner join itempropfornec ipf on ipf.itemprocessodecompra_id = ipc.id
      where licfor.empresa_id = prop.fornecedor_id
        and sfl.tiposituacao = 'HOMOLOGADA'
      union all
      select distinct coalesce(ipc.quantidade, 1)              as qtde_processo,
                      ipf.preco                                as vl_unit_homologado,
                      coalesce(ipf.percentualdesconto, 0)      as percentual_desc,
                      ipc.id                                   as id_item_processo,
                      ipc.numero                               as numero_item,
                      lpc.numero                               as numero_lote,
                      lic.id                                   as id_processo,
                      case
                          when ata.id is not null then ata.unidadeorganizacional_id
                          else pc.unidadeorganizacional_id end as id_unidade,
                      coalesce(ata.id, null)                   as id_ata,
                      pes.id                                   as id_fornecedor
      from itemcertame ic
               inner join certame c on c.id = ic.certame_id
               inner join licitacao lic on lic.id = c.licitacao_id
               inner join processodecompra pc on pc.id = lic.processodecompra_id
               left join ataregistropreco ata on ata.licitacao_id = lic.id
               inner join itemcertameitempro icip on icip.itemcertame_id = ic.id
               inner join itempropfornec ipf on icip.itempropostavencedor_id = ipf.id
               inner join itemprocessodecompra ipc on icip.itemprocessodecompra_id = ipc.id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
               inner join propostafornecedor prop on prop.id = ipf.propostafornecedor_id
               inner join pessoa pes on pes.id = prop.fornecedor_id
               inner join licitacaofornecedor licfor on licfor.licitacao_id = lic.id
               inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
      where ic.situacaoItemCertame = 'VENCEDOR'
        and licfor.empresa_id = prop.fornecedor_id
        and sfl.tiposituacao = 'HOMOLOGADA'
      union all
      select distinct coalesce(ipc.quantidade, 1)              as qtde_processo,
                      ipf.preco                                as vl_unit_homologado,
                      coalesce(lpf.percentualdesconto, 0)      as percentual_desc,
                      ipc.id                                   as id_item_processo,
                      ipc.numero                               as numero_item,
                      lpc.numero                               as numero_lote,
                      lic.id                                   as id_processo,
                      case
                          when ata.id is not null then ata.unidadeorganizacional_id
                          else pc.unidadeorganizacional_id end as id_unidade,
                      coalesce(ata.id, null)                   as id_ata,
                      pes.id                                   as id_fornecedor
      from itemcertame ic
               inner join certame c on c.id = ic.certame_id
               inner join licitacao lic on lic.id = c.licitacao_id
               inner join processodecompra pc on pc.id = lic.processodecompra_id
               left join ataregistropreco ata on ata.licitacao_id = lic.id
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
      where ic.situacaoItemCertame = 'VENCEDOR'
        and licfor.empresa_id = prop.fornecedor_id
        and sfl.tiposituacao = 'HOMOLOGADA'
      union all
      select distinct coalesce(ipc.quantidade, 1)              as qtde_processo,
                      ipf.preco                                as vl_unit_homologado,
                      coalesce(ipf.percentualdesconto, 0)      as percentual_desc,
                      ipc.id                                   as id_item_processo,
                      ipc.numero                               as numero_item,
                      lpc.numero                               as numero_lote,
                      lic.id                                   as id_processo,
                      case
                          when ata.id is not null then ata.unidadeorganizacional_id
                          else pc.unidadeorganizacional_id end as id_unidade,
                      coalesce(ata.id, null)                   as id_ata,
                      pes.id                                   as id_fornecedor
      from itemmapacomtecpre imc
               inner join mapacomtecprec mapa on mapa.id = imc.mapacomtecnicapreco_id
               inner join licitacao lic on lic.id = mapa.licitacao_id
               inner join processodecompra pc on pc.id = lic.processodecompra_id
               left join ataregistropreco ata on ata.licitacao_id = lic.id
               inner join itemmacotecprecitemproc icip on icip.itemmapacomtecnicapreco_id = imc.id
               inner join itempropfornec ipf on icip.itempropostavencedor_id = ipf.id
               inner join itemprocessodecompra ipc on ipf.itemprocessodecompra_id = ipc.id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
               inner join propostafornecedor prop on prop.id = ipf.propostafornecedor_id
               inner join pessoa pes on pes.id = prop.fornecedor_id
               inner join licitacaofornecedor licfor on licfor.licitacao_id = lic.id
               inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
      where imc.situacaoitem = 'VENCEDOR'
        and licfor.empresa_id = prop.fornecedor_id
        and sfl.tiposituacao = 'HOMOLOGADA'
      union all
      select distinct coalesce(ipc.quantidade, 1)              as qtde_processo,
                      ipf.preco                                as vl_unit_homologado,
                      coalesce(lpf.percentualdesconto, 0)      as percentual_desc,
                      ipc.id                                   as id_item_processo,
                      ipc.numero                               as numero_item,
                      lpc.numero                               as numero_lote,
                      lic.id                                   as id_processo,
                      case
                          when ata.id is not null then ata.unidadeorganizacional_id
                          else pc.unidadeorganizacional_id end as id_unidade,
                      coalesce(ata.id, null)                   as id_ata,
                      pes.id                                   as id_fornecedor
      from itemmapacomtecpre imc
               inner join mapacomtecprec mapa on mapa.id = imc.mapacomtecnicapreco_id
               inner join licitacao lic on lic.id = mapa.licitacao_id
               inner join processodecompra pc on pc.id = lic.processodecompra_id
               left join ataregistropreco ata on ata.licitacao_id = lic.id
               inner join itemmacotecprecloteproc iclp on iclp.itemmapacomtecnicapreco_id = imc.id
               inner join lotepropfornec lpf on lpf.id = iclp.lotepropostavencedor_id
               inner join itempropfornec ipf on ipf.lotepropostafornecedor_id = lpf.id
               inner join itemprocessodecompra ipc on ipf.itemprocessodecompra_id = ipc.id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
               inner join propostafornecedor prop on prop.id = ipf.propostafornecedor_id
               inner join pessoa pes on pes.id = prop.fornecedor_id
               inner join licitacaofornecedor licfor on licfor.licitacao_id = lic.id
               inner join statusfornecedorlicitacao sfl on sfl.licitacaofornecedor_id = licfor.id
      where imc.situacaoitem = 'VENCEDOR'
        and licfor.empresa_id = prop.fornecedor_id
        and sfl.tiposituacao = 'HOMOLOGADA'
      union all
      select distinct coalesce(ipc.quantidade, 1) as qtde_processo,
                      item.preco                  as vl_unit_homologado,
                      0                           as percentual_desc,
                      ipc.id                      as id_item_processo,
                      ipc.numero                  as numero_item,
                      lpc.numero                  as numero_lote,
                      disp.id                     as id_processo,
                      pc.unidadeorganizacional_id as id_unidade,
                      null                        as id_ata,
                      pes.id                      as id_fornecedor
      from dispensadelicitacao disp
               inner join processodecompra pc on pc.id = disp.processodecompra_id
               inner join fornecedordisplic fdl on fdl.dispensadelicitacao_id = disp.id
               inner join propostafornecedordispensa prop
                          on prop.id = fdl.propostafornecedordispensa_id
               inner join lotepropostafornedisp lote on lote.propostafornecedordispensa_id = prop.id
               inner join itempropostafornedisp item on item.lotepropostafornedisp_id = lote.id
               inner join itemprocessodecompra ipc on ipc.id = item.itemprocessodecompra_id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
               inner join pessoa pes on pes.id = fdl.pessoa_id
      union all
      select distinct coalesce(ipc.quantidade, 1) as qtde_processo,
                      ipf.preco                   as vl_unit_homologado,
                      0                           as percentual_desc,
                      ipc.id                      as id_item_processo,
                      ipc.numero                  as numero_item,
                      lpc.numero                  as numero_lote,
                      lic.id                      as id_processo,
                      pc.unidadeorganizacional_id as id_unidade,
                      null                        as id_ata,
                      pes.id                      as id_fornecedor
      from licitacao lic
               inner join processodecompra pc on pc.id = lic.processodecompra_id
               inner join licitacaofornecedor lf on lf.licitacao_id = lic.id
               inner join propostafornecedor prop on prop.licitacaofornecedor_id = lf.id
               inner join lotepropfornec lote on lote.propostafornecedor_id = prop.id
               inner join itempropfornec ipf on ipf.lotepropostafornecedor_id = lote.id
               inner join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
               inner join pessoa pes on pes.id = lf.empresa_id
               inner join statuslicitacao st on st.licitacao_id = lic.id
          and trunc(st.datastatus) =
              (select max(trunc(st1.datastatus))
               from statuslicitacao st1
               where st1.licitacao_id = lic.id)
      where lic.modalidadelicitacao = 'CREDENCIAMENTO')
