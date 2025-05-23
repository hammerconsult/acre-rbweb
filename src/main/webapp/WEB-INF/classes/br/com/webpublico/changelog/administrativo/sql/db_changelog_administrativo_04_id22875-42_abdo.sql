
insert into dotacaosolmatitem (id, dotacaosolicitacaomaterial_id,
tipocontadespesa, valor)
select hibernate_sequence.nextval, dados.id_dotacao, dados.tipocontadespesa, dados.valor from(
select dism.dotacaosolicitacaomat_id id_dotacao,
       case oc.tipoobjetocompra
          when 'PERMANENTE_IMOVEL' then 'BEM_IMOVEL'
          when 'PERMANENTE_MOVEL' then 'BEM_MOVEL'
          when 'CONSUMO' then 'BEM_ESTOQUE'
          when 'SERVICO' then 'NAO_APLICAVEL'
          else 'NAO_APLICAVEL'
       end tipocontadespesa,
       sum(disr.valorreservado) valor
   from dotitemsolmat dism
  inner join itemsolicitacao item on item.id = dism.itemsolicitacaomaterial_id
  inner join itemsolicitacaomaterial item_mat on item_mat.itemsolicitacao_id = item.id
  inner join objetocompra oc on oc.id = item_mat.objetocompra_id
  inner join dotacaoitemsolreserva disr on disr.dotitemsolicitacao_id = dism.id
group by dism.dotacaosolicitacaomat_id, case oc.tipoobjetocompra
          when 'PERMANENTE_IMOVEL' then 'BEM_IMOVEL'
          when 'PERMANENTE_MOVEL' then 'BEM_MOVEL'
          when 'CONSUMO' then 'BEM_ESTOQUE'
          when 'SERVICO' then 'NAO_APLICAVEL'
          else 'NAO_APLICAVEL'
       end
order by 1,2) dados;

insert into dotacaosolmatitemdetalhe (id, dotacaosolmatitem_id,
fontedespesaorc_id, valor)
select hibernate_sequence.nextVal,
       (select id from dotacaosolmatitem s
        where s.dotacaosolicitacaomaterial_id = dados.id_dotacao
          and s.tipocontadespesa = dados.tipocontadespesa),
       dados.fontedespesaorc_id,
       dados.valor from (
select dism.dotacaosolicitacaomat_id id_dotacao,
       case oc.tipoobjetocompra
          when 'PERMANENTE_IMOVEL' then 'BEM_IMOVEL'
          when 'PERMANENTE_MOVEL' then 'BEM_MOVEL'
          when 'CONSUMO' then 'BEM_ESTOQUE'
          when 'SERVICO' then 'NAO_APLICAVEL'
          else 'NAO_APLICAVEL'
       end tipocontadespesa,
       disr.fontedespesaorc_id,
       sum(disr.valorreservado) valor
   from dotitemsolmat dism
  inner join itemsolicitacao item on item.id = dism.itemsolicitacaomaterial_id
  inner join itemsolicitacaomaterial item_mat on item_mat.itemsolicitacao_id = item.id
  inner join objetocompra oc on oc.id = item_mat.objetocompra_id
  inner join dotacaoitemsolreserva disr on disr.dotitemsolicitacao_id = dism.id
group by dism.dotacaosolicitacaomat_id, case oc.tipoobjetocompra
          when 'PERMANENTE_IMOVEL' then 'BEM_IMOVEL'
          when 'PERMANENTE_MOVEL' then 'BEM_MOVEL'
          when 'CONSUMO' then 'BEM_ESTOQUE'
          when 'SERVICO' then 'NAO_APLICAVEL'
          else 'NAO_APLICAVEL'
       end, disr.fontedespesaorc_id
order by 1,2) dados;
