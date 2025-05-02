insert into execucaocontratoitem (id, execucaocontrato_id, tipocontadespesa, valor)
select hibernate_sequence.nextVal, dados.* from (
select iec.execucaocontrato_id,
       case oc.tipoobjetocompra
          when 'PERMANENTE_IMOVEL' then 'BEM_IMOVEL'
          when 'PERMANENTE_MOVEL' then 'BEM_MOVEL'
          when 'CONSUMO' then 'BEM_ESTOQUE'
          when 'SERVICO' then 'NAO_APLICAVEL'
          else 'NAO_APLICAVEL'
       end tipocontadespesa,
       sum(iecr.valorreservado) valor
   from itemexecucaocontrato_old iec
  inner join itemcontrato ic on ic.id = iec.itemcontrato_id
  inner join itemcontratoitempropfornec icipf on icipf.itemcontrato_id = ic.id
  inner join itempropfornec ipf on ipf.id = icipf.itempropostafornecedor_id
  inner join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id
  inner join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id
  inner join itemsolicitacaomaterial itsm on itsm.itemsolicitacao_id = its.id
  inner join objetocompra oc on oc.id = itsm.objetocompra_id
  inner join itemexeccontratoreserva_old iecr on iecr.itemexecucaocontrato_id = iec.id
group by iec.execucaocontrato_id, case oc.tipoobjetocompra
          when 'PERMANENTE_IMOVEL' then 'BEM_IMOVEL'
          when 'PERMANENTE_MOVEL' then 'BEM_MOVEL'
          when 'CONSUMO' then 'BEM_ESTOQUE'
          when 'SERVICO' then 'NAO_APLICAVEL'
          else 'NAO_APLICAVEL'
       end) dados;

insert into execucaocontratoitemfonte (id, execucaocontratoitem_id, fontedespesaorc_id, valor)
select hibernate_sequence.nextVal,
       (select eci.id
          from execucaocontratoitem eci
        where eci.execucaocontrato_id = dados.execucaocontrato_id
          and eci.tipocontadespesa = dados.tipocontadespesa),
       dados.fontedespesaorc_id,
       dados.valor from (
select iec.execucaocontrato_id,
       case oc.tipoobjetocompra
          when 'PERMANENTE_IMOVEL' then 'BEM_IMOVEL'
          when 'PERMANENTE_MOVEL' then 'BEM_MOVEL'
          when 'CONSUMO' then 'BEM_ESTOQUE'
          when 'SERVICO' then 'NAO_APLICAVEL'
          else 'NAO_APLICAVEL'
       end tipocontadespesa,
       iecr.fontedespesaorc_id,
       sum(iecr.valorreservado) valor
   from itemexecucaocontrato_old iec
  inner join itemcontrato ic on ic.id = iec.itemcontrato_id
  inner join itemcontratoitempropfornec icipf on icipf.itemcontrato_id = ic.id
  inner join itempropfornec ipf on ipf.id = icipf.itempropostafornecedor_id
  inner join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id
  inner join itemsolicitacao its on its.id = ipc.itemsolicitacaomaterial_id
  inner join itemsolicitacaomaterial itsm on itsm.itemsolicitacao_id = its.id
  inner join objetocompra oc on oc.id = itsm.objetocompra_id
  inner join itemexeccontratoreserva_old iecr on iecr.itemexecucaocontrato_id = iec.id
group by iec.execucaocontrato_id, case oc.tipoobjetocompra
          when 'PERMANENTE_IMOVEL' then 'BEM_IMOVEL'
          when 'PERMANENTE_MOVEL' then 'BEM_MOVEL'
          when 'CONSUMO' then 'BEM_ESTOQUE'
          when 'SERVICO' then 'NAO_APLICAVEL'
          else 'NAO_APLICAVEL'
       end,
       iecr.fontedespesaorc_id) dados;
