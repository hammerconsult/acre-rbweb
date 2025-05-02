create or replace view vwrhfichafinanceira as 
select ficha.vinculofp_id as id_vinculo, 
       ficha.id as id_ficha,
       folha.ano, 
       folha.mes, 
       folha.tipofolhadepagamento,
       (select coalesce(sum(item.valor), 0) from itemfichafinanceirafp item where item.tipoeventofp = 'VANTAGEM' and item.fichafinanceirafp_id = ficha.id ) as vantagem,
       (select coalesce(sum(item.valor), 0) from itemfichafinanceirafp item where item.tipoeventofp = 'DESCONTO' and item.fichafinanceirafp_id = ficha.id ) as desconto,
       (select coalesce(sum(item.valor), 0) from itemfichafinanceirafp item join eventofp evento on evento.id = item.eventofp_id
                               where evento.codigo in ('151', '294') and item.fichafinanceirafp_id = ficha.id) as um_terco_ferias,
       (select coalesce(sum(item.valor), 0) from itemfichafinanceirafp item join eventofp evento on evento.id = item.eventofp_id
                               where evento.id in (select item.eventofp_id from itemgrupoexportacao item 
                                                                           join grupoexportacao grupo on grupo.id = item.grupoexportacao_id
                                                                           join moduloexportacao modulo on modulo.id = grupo.moduloexportacao_id
                                                                             where modulo.codigo = 9 and grupo.codigo = 1)
                               and item.fichafinanceirafp_id = ficha.id ) as RPPS,
        (select coalesce(sum(item.valorbasedecalculo), 0) from itemfichafinanceirafp item join eventofp evento on evento.id = item.eventofp_id
                               where evento.id in (select item.eventofp_id from itemgrupoexportacao item 
                                                                           join grupoexportacao grupo on grupo.id = item.grupoexportacao_id
                                                                           join moduloexportacao modulo on modulo.id = grupo.moduloexportacao_id
                                                                             where modulo.codigo = 9 and grupo.codigo = 1)
                               and item.fichafinanceirafp_id = ficha.id ) as baseRPPS,
        (select coalesce(sum(item.valorreferencia), 0) from itemfichafinanceirafp item join eventofp evento on evento.id = item.eventofp_id
                               where evento.id in (select item.eventofp_id from itemgrupoexportacao item 
                                                                           join grupoexportacao grupo on grupo.id = item.grupoexportacao_id
                                                                           join moduloexportacao modulo on modulo.id = grupo.moduloexportacao_id
                                                                             where modulo.codigo = 9 and grupo.codigo = 1)
                               and item.fichafinanceirafp_id = ficha.id ) as referenciaRPPS
from fichafinanceirafp ficha 
        join folhadepagamento folha on ficha.folhadepagamento_id = folha.id