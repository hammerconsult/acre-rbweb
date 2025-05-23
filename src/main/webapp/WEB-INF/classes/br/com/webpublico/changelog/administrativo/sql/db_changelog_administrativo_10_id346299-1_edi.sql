create or replace VIEW vw_licitacao_item_pncp as
select obj.codigo                                            as cogigo,
       obj.descricao                                         as descricao,
       isol.descricaocomplementar                            as descricaoComplementar,
       lote.numero                                           as numeroLote,
       item.numero                                           as numeroItem,
       lote.numero || LPAD(item.numero, 3, '0')              as numeroItemComLote,
       item.sequencialpncp                                   as sequencialPncp,
       case
           when obj.tipoobjetocompra = 'CONSUMO' or obj.tipoobjetocompra = 'PERMANENTE_MOVEL' then 'M'
           else 'S' end                                      as materialOuServico,
       isol.quantidade                                       as quantidade,
       uni.descricao                                         as unidadeMedida,
       isol.preco                                            as valorUnitarioEstimado,
       isol.precototal                                       as valorTotal,
       coalesce(lic.id, dispensa.id)                         as idLicitacao,
       coalesce(lic.tipoavaliacao, dispensa.tipodeavaliacao) as criterioJulgamentoId,
       sol.orcamentosigiloso                                 as orcamentoSigiloso,
       'NAO_SE_APLICA'                                       as tipoBeneficioId,
       0                                                     as incentivoProdutivoBasico,
       0                                                     as aplicabilidadeMargemPreferenciaNormal,
       0                                                     as aplicabilidadeMargemPreferenciaAdicional,
       null                                                  as percentualMargemPreferenciaNormal,
       null                                                  as percentualMargemPreferenciaAdicional,
       ''                                                    as ncmNbsCodigo,
       ''                                                    as ncmNbsDescricao,
       case
           when item.situacaoitemprocessodecompra in ('AGUARDANDO', 'ADJUDICADO') then 1
           when item.situacaoitemprocessodecompra = 'HOMOLOGADO' then 2
           else 3
           end                                               as situacaoCompraItemId
from itemprocessodecompra item
         inner join itemsolicitacao isol on item.itemsolicitacaomaterial_id = isol.id
         inner join unidademedida uni on isol.unidademedida_id = uni.id
         inner join objetocompra obj on isol.objetocompra_id = obj.id
         inner join loteprocessodecompra lote on item.loteprocessodecompra_id = lote.id
         inner join processodecompra proc on lote.processodecompra_id = proc.id
         inner join solicitacaomaterial sol on proc.solicitacaomaterial_id = sol.id
         left join licitacao lic on proc.id = lic.processodecompra_id
         left join dispensadelicitacao dispensa on proc.id = dispensa.processodecompra_id
