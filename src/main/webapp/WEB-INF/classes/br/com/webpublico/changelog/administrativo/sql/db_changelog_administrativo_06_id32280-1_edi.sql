create or replace view vwvencedorlicitacao as
select vencedores.id_licitacao,
       vencedores.id_loteprocessodecompra,
       vencedores.lote,
       vencedores.id_itemprocessodecompra,
       vencedores.item,
       vencedores.quantidade,
       vencedores.preco,
       vencedores.id_proposta_vencedora
from (select l.id       as id_licitacao,
             lpc.id     as id_loteprocessodecompra,
             lpc.numero as lote,
             ipc.id     as id_itemprocessodecompra,
             ipc.numero as item,
             ipc.quantidade,
             ipf.preco,
             pf.id      as id_proposta_vencedora
      from licitacao l
               inner join certame c on c.licitacao_id = l.id
               inner join itemcertame ic on ic.certame_id = c.id
               inner join itemcertamelotepro icl on icl.itemcertame_id = ic.id
               inner join lotepropfornec lpf on lpf.id = icl.lotepropfornecedorvencedor_id
               inner join itempropfornec ipf on ipf.lotepropostafornecedor_id = lpf.id
               inner join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
               inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id
               inner join propostafornecedor pf on pf.id = lpf.propostafornecedor_id
      where l.tipoapuracao = 'LOTE'
      union all
      select l.id       as id_licitacao,
             lpc.id     as id_loteprocessodecompra,
             lpc.numero as lote,
             ipc.id     as id_itemprocessodecompra,
             ipc.numero as item,
             ipc.quantidade,
             ipf.preco,
             pf.id      as id_proposta_vencedora
      from licitacao l
               inner join certame c on c.licitacao_id = l.id
               inner join itemcertame ic on ic.certame_id = c.id
               inner join itemcertameitempro ici on ici.itemcertame_id = ic.id
               inner join itempropfornec ipf on ipf.id = ici.itempropostavencedor_id
               inner join lotepropfornec lpf on lpf.id = ipf.lotepropostafornecedor_id
               inner join propostafornecedor pf on pf.id = lpf.propostafornecedor_id
               inner join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
      where l.tipoapuracao = 'ITEM'
      union all
      select l.id        as id_licitacao,
             lpc.id      as id_loteprocessodecompra,
             lpc.numero  as lote,
             ipc.id      as id_itemprocessodecompra,
             ipc.numero  as item,
             ipc.quantidade,
             iplip.valor as preco,
             pf.id       as id_proposta_vencedora
      from licitacao l
               inner join pregao p on p.licitacao_id = l.id
               inner join itempregao ip on ip.pregao_id = p.id
               inner join itprelotpro ipl on ipl.itempregao_id = ip.id
               inner join lancepregao lpv on lpv.id = ip.lancepregaovencedor_id
               inner join propostafornecedor pf on pf.id = lpv.propostafornecedor_id
               inner join itempregaoloteitemprocesso iplip on iplip.itempregaoloteprocesso_id = ipl.id
               inner join itemprocessodecompra ipc on ipc.id = iplip.itemprocessodecompra_id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
      where l.tipoapuracao = 'LOTE'
    union all
      select l.id       as id_licitacao,
             lpc.id     as id_loteprocessodecompra,
             lpc.numero as lote,
             ipc.id     as id_itemprocessodecompra,
             ipc.numero as item,
             ipc.quantidade,
             lpv.valor  as preco,
             pf.id      as id_proposta_vencedora
      from licitacao l
               inner join pregao p on p.licitacao_id = l.id
               inner join itempregao ip on ip.pregao_id = p.id
               inner join itpreitpro ipi on ipi.itempregao_id = ip.id
               inner join lancepregao lpv on lpv.id = ip.lancepregaovencedor_id
               inner join propostafornecedor pf on pf.id = lpv.propostafornecedor_id
               inner join itemprocessodecompra ipc on ipc.id = ipi.itemprocessodecompra_id
               inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id
      ) vencedores
order by vencedores.id_licitacao, vencedores.id_loteprocessodecompra, vencedores.lote,
         vencedores.id_itemprocessodecompra, vencedores.item
