create or replace VIEW vw_licitacao_item_pncp as
select obj.CODIGO,
       obj.DESCRICAO,
       isol.DESCRICAOCOMPLEMENTAR,
       lote.numero || LPAD (item.NUMERO,3, '0') as numero,
       case
           when obj.TIPOOBJETOCOMPRA = 'CONSUMO' or obj.TIPOOBJETOCOMPRA = 'PERMANENTE_MOVEL' then 'M'
           else 'S' end                    as materialOuServico,
       isol.QUANTIDADE,
       uni.DESCRICAO                       as unidademedida,
       isol.PRECO,
       isol.PRECOTOTAL,
       coalesce(lic.id, dispensa.id)                              as idLicitacao,
       coalesce(lic.TIPOAVALIACAO, dispensa.TIPODEAVALIACAO)                   as criterioJulgamento,
       sol.ORCAMENTOSIGILOSO,
       'NAO_SE_APLICA' as tipobeneficio,
       0 as incentivoProdutivo,
       0 as aplicabilidadeMargemPreferenciaNormal,
       0 as aplicabilidadeMargemPreferenciaAdicional,
       null as percentualMargemPreferenciaNormal,
       null as percentualMargemPreferenciaAdicional,
       '' as ncmNbsCodigo,
       '' as ncmNbsDescricao
from ITEMPROCESSODECOMPRA item
         inner join itemsolicitacao isol on item.ITEMSOLICITACAOMATERIAL_ID = isol.ID
         inner join UNIDADEMEDIDA uni on isol.UNIDADEMEDIDA_ID = uni.ID
         inner join OBJETOCOMPRA obj on isol.OBJETOCOMPRA_ID = obj.ID
         inner join loteprocessodecompra lote on item.LOTEPROCESSODECOMPRA_ID = lote.ID
         inner join processodecompra proc on lote.PROCESSODECOMPRA_ID = proc.ID
         inner join SOLICITACAOMATERIAL sol on proc.SOLICITACAOMATERIAL_ID = sol.ID
         left join licitacao lic on proc.ID = lic.PROCESSODECOMPRA_ID
         left join DISPENSADELICITACAO dispensa on proc.ID = dispensa.PROCESSODECOMPRA_ID
