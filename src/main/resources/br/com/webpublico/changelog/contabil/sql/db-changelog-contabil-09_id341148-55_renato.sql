merge into REGISTROEVENTORETENCAOREINF reg
using (select distinct reg.id as id, item.CONFIGEMPREGADORESOCIAL_ID as empregador_id
       from REGISTROEVENTORETENCAOREINF reg
                inner join PagamentoReinf pr on reg.ID = pr.REGISTRO_ID
                inner join pagamento pag on pr.PAGAMENTO_ID = pag.ID
                inner join hierarquiaorganizacional orc on pag.UNIDADEORGANIZACIONAL_ID = orc.SUBORDINADA_ID
           and
                                                           to_date('31/08/2022', 'dd/mm/yyyy') between orc.INICIOVIGENCIA and coalesce(orc.FIMVIGENCIA, to_date('31/08/2022', 'dd/mm/yyyy'))
                inner join HIERARQUIAORGRESP resp
                           on resp.HIERARQUIAORGORC_ID = orc.id and
                              to_date('31/08/2022', 'dd/mm/yyyy') between resp.DATAINICIO and coalesce(resp.DATAFIM, to_date('31/08/2022', 'dd/mm/yyyy'))
                inner join HIERARQUIAORGANIZACIONAL orgaoadm
                           on resp.HIERARQUIAORGADM_ID = orgaoadm.ID and
                              to_date('31/08/2022', 'dd/mm/yyyy') between orgaoadm.INICIOVIGENCIA and coalesce(orgaoadm.FIMVIGENCIA, to_date('31/08/2022', 'dd/mm/yyyy'))
                inner join ITEMEMPREGADORESOCIAL item on orgaoadm.id = item.HIERARQUIAORGANIZACIONAL_ID) dados
on (dados.id = reg.id)
when matched then
    update
    set reg.empregador_id = dados.empregador_id
