create or replace view vfornecedores_precos_licitacao as
-- RECUPERA O ÚLTIMO LANCE DE UM FORNECEDOR PARA UM ITEM PROPOSTA FORNECEDOR ###########
SELECT  uo.id as unidade_id,
        uo.descricao AS unidade,
        'PREGAO' AS origem,
        sc.id as serv_id,
        sc.descricao AS desc_serv,
        m.id as mat_id,
        m.descricao as desc_mat,
        '' AS lote,
        p.id as pessoa_id,
        pf.nome as nome,
        pj.razaosocial ,
        licforn.tipoclassificacaofornecedor as tipoclassificacao,
        pcompra.descricao AS descricao,
        lic.emitidaem as data_licitacao,
        lance.statuslancepregao as status,        
        0 as valor_lote,
        lance.valor       
        
from lancepregao lance
inner join rodadapregao rodada on lance.rodadapregao_id = rodada.id
inner join itempregao itempregao on rodada.itempregao_id = itempregao.id
inner join itpreitpro ipip on itempregao.id = ipip.itempregao_id
inner join itemprocessodecompra itempc on ipip.itemprocessodecompra_id = itempc.id
inner join itempropfornec itemprop on itempc.id = itemprop.itemprocessodecompra_id
inner join propostafornecedor prop on itemprop.propostafornecedor_id = prop.id
left join pessoa p on prop.fornecedor_id = p.id
left join pessoafisica pf on p.id = pf.id
left join pessoajuridica pj on p.id = pj.id
left join itemsolicitacaomaterial ism on  ism.itemsolicitacao_id = itempc.itemsolicitacaomaterial_id
left join itemsolicitacaoservico iss on iss.itemsolicitacao_id = itempc.itemsolicitacaomaterial_id
left join material m on m.id = ism.material_id
left join servicocompravel sc on sc.id = iss.servicocompravel_id
inner join licitacao lic on lic.id = prop.licitacao_id
inner join licitacaofornecedor licforn on licforn.empresa_id = p.id
inner join processodecompra pcompra on pcompra.id = lic.processodecompra_id
inner join unidadeorganizacional uo on uo.id = pcompra.unidadeorganizacional_id
where lance.propostafornecedor_id = itemprop.propostafornecedor_id
and licforn.licitacao_id = lic.id
and itemprop.id in (select itemprop.id from itempropfornec itemprop
                inner join propostafornecedor prop on itemprop.propostafornecedor_id = prop.id
                inner join licitacao lic on prop.licitacao_id = lic.id)
and lance.id = (select max(l.id) from lancepregao l where l.rodadapregao_id = rodada.id and l.propostafornecedor_id = prop.id)
AND rodada.ID = (SELECT MAX(r.ID) FROM rodadapregao r INNER JOIN itempregao i ON r.itempregao_id = i.ID INNER JOIN lancepregao lp ON lp.rodadapregao_id = r.ID WHERE i.ID = itempregao.ID AND lp.propostafornecedor_id = prop.ID)

union

SELECT uo.id as unidade_id,
       uo.descricao AS unidade,
       'PREGAO' AS origem,
       sc.id as serv_id,
       sc.descricao AS desc_serv,
       m.id as mat_id,
       m.descricao as desc_mat,
       lotepc.descricao AS lote ,
       p.id as pessoa_id,
       pf.nome as nome,
       pj.razaosocial as razao_social,
       licforn.tipoclassificacaofornecedor as tipoclassificacao,       
       pcompra.descricao AS descricao,
       lic.emitidaem as data_licitacao,
       lance.statuslancepregao as status,
       lance.valor as valor_lote, 
       case
          when (select v.valorfinal as vlfinal from valorfinalitemlote v
                  inner join itempregao itpregao on itpregao.id = v.itempregao_id
                  inner join lancepregao lancep on lancep.id = itpregao.lancepregaovencedor_id
                  inner join itemprocessodecompra ipc on ipc.id = v.itemprocessodecompra_id
                  inner join propostafornecedor propf on propf.id = lancep.propostafornecedor_id
                       where lancep.id = lance.id
                         and v.itemprocessodecompra_id = itempc.id) is null then 0.0
          else (select v.valorfinal as vlfinal from valorfinalitemlote v
                  inner join itempregao itpregao on itpregao.id = v.itempregao_id
                  inner join lancepregao lancep on lancep.id = itpregao.lancepregaovencedor_id
                  inner join itemprocessodecompra ipc on ipc.id = v.itemprocessodecompra_id
                  inner join propostafornecedor propf on propf.id = lancep.propostafornecedor_id
                       where lancep.id = lance.id
                         and v.itemprocessodecompra_id = itempc.id)
        end as valor_item
from 
lancepregao lance
inner join rodadapregao rodada on lance.rodadapregao_id = rodada.id
inner join itempregao itempregao on rodada.itempregao_id = itempregao.id
inner join valorfinalitemlote valfinal on valfinal.itempregao_id = itempregao.id
inner join itemprocessodecompra itempc on itempc.id = valfinal.itemprocessodecompra_id
inner join itprelotpro iplp on itempregao.id = iplp.itempregao_id
inner join loteprocessodecompra lotepc on iplp.loteprocessodecompra_id = lotepc.id
inner join lotepropfornec loteprop on lotepc.id = loteprop.loteprocessodecompra_id
inner join propostafornecedor prop on loteprop.propostafornecedor_id = prop.id
 left join pessoa p on prop.fornecedor_id = p.id
 left join pessoafisica pf on p.id = pf.id
 left join pessoajuridica pj on p.id = pj.id
 left join itemsolicitacaomaterial ism on  ism.itemsolicitacao_id = itempc.itemsolicitacaomaterial_id
 left join itemsolicitacaoservico iss on iss.itemsolicitacao_id = itempc.itemsolicitacaomaterial_id
 left join material m on m.id = ism.material_id
 left join servicocompravel sc on sc.id = iss.servicocompravel_id
inner join licitacao lic on lic.id = prop.licitacao_id
inner join licitacaofornecedor licforn on licforn.empresa_id = p.id
inner join processodecompra pcompra on pcompra.id = lic.processodecompra_id
inner join unidadeorganizacional uo on uo.id = pcompra.unidadeorganizacional_id
where lance.propostafornecedor_id = loteprop.propostafornecedor_id 
and licforn.licitacao_id = lic.id
and loteprop.id in (select loteprop.id
                      from lotepropfornec loteprop
                inner join propostafornecedor prop on loteprop.propostafornecedor_id = prop.id
                inner join licitacao lic on prop.licitacao_id = lic.id)
and lance.id = (select max(l.id) from lancepregao l where l.rodadapregao_id = rodada.id and l.propostafornecedor_id = prop.id)
and rodada.id = (select max(r.id) from rodadapregao r inner join itempregao i on r.itempregao_id = i.id inner join lancepregao lp on lp.rodadapregao_id = r.id where i.id = itempregao.id and lp.propostafornecedor_id = prop.id)

UNION

SELECT DISTINCT 
       uo.id as unidade_id,
       uo.descricao AS unidade,
       'MAPA_COMPARATIVO' AS origem,
       sc.id as serv_id,
       sc.descricao AS desc_serv,
       mat.id as mat_id,
       mat.descricao as desc_mat,
       lpdc.descricao AS lote,
       p.id as pessoa_id,
       pf.nome as nome,
       pj.razaosocial as razaosocial, 
       lf.tipoclassificacaofornecedor as tipoclassificacao,
       pcompra.descricao as descricao,
       lic.emitidaem as data_licitacao,
       case
            when icip.itempropostavencedor_id = ipf.id then 'VENCEDOR'
            when iclp.LOTEPROPFORNECEDORVENCEDOR_ID = lpf.id then 'VENCEDOR'
            else 'PERDEDOR'
       end as status,
       0 as valor_lote,
       ipf.preco          
          
                from itempropfornec ipf
          inner join lotepropfornec lpf on lpf.id = ipf.lotepropostafornecedor_id
          inner join propostafornecedor prop on prop.id = lpf.propostafornecedor_id
          inner join licitacao lic on prop.licitacao_id = lic.id
          inner join licitacaofornecedor lf on lf.licitacao_id = lic.id
          inner join pessoa p on p.id = lf.empresa_id
           left join pessoafisica pf on pf.id = p.id
           left join pessoajuridica pj on pj.id = p.id
          inner join itemprocessodecompra ipdc on ipdc.id = ipf.itemprocessodecompra_id
          inner join loteprocessodecompra lpdc on lpdc.id = ipdc.loteprocessodecompra_id
          inner join processodecompra pcompra on pcompra.id = lpdc.processodecompra_id
          inner join unidadeorganizacional uo on uo.id = pcompra.unidadeorganizacional_id
          inner join itemsolicitacao isol on isol.id = ipdc.itemsolicitacaomaterial_id
           left join itemsolicitacaomaterial ism on ism.itemsolicitacao_id = isol.id
           left join material mat on mat.id = ism.material_id
           left join itemsolicitacaoservico iss on iss.itemsolicitacao_id = isol.id
           left join servicocompravel sc on sc.id = iss.servicocompravel_id
          inner join certame cert on cert.licitacao_id = lic.id
          inner join itemcertame ic on ic.certame_id = cert.id
          left join itemcertameitempro icip on icip.itempropostavencedor_id = ipf.id
          left join itemcertamelotepro iclp on iclp.lotepropfornecedorvencedor_id = lpf.id
               where prop.fornecedor_id = p.id                 
            ORDER BY 1,2,3,4,5,6
;