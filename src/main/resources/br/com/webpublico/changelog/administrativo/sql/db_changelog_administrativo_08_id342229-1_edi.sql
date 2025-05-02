--irp
update itemintencaoregistropreco
set TIPOCONTROLE = 'VALOR',
    QUANTIDADE   = 1
where id in (select item.id
             from licitacao lic
                      inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                      inner join solicitacaomaterial sol on sol.id = pc.SOLICITACAOMATERIAL_ID
                      inner join cotacao cot on cot.id = sol.COTACAO_ID
                      inner join formulariocotacao fc on fc.id = cot.FORMULARIOCOTACAO_ID
                      inner join INTENCAOREGISTROPRECO irp on irp.id = fc.INTENCAOREGISTROPRECO_ID
                      inner join LOTEINTENCAOREGISTROPRECO lote on lote.INTENCAOREGISTROPRECO_ID = irp.id
                      inner join itemintencaoregistropreco item on item.LOTEINTENCAOREGISTROPRECO_ID = lote.id
             where lic.id = 10841258427);

--formulário
update itemloteformulariocotacao
set TIPOCONTROLE = 'VALOR',
    QUANTIDADE   = 1
where id in (select ifc.id
             from licitacao lic
                      inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                      inner join solicitacaomaterial sol on sol.id = pc.SOLICITACAOMATERIAL_ID
                      inner join cotacao cot on cot.id = sol.COTACAO_ID
                      inner join formulariocotacao fc on fc.id = cot.FORMULARIOCOTACAO_ID
                      inner join loteformulariocotacao lfc on lfc.FORMULARIOCOTACAO_ID = fc.id
                      inner join itemloteformulariocotacao ifc on ifc.LOTEFORMULARIOCOTACAO_ID = lfc.id
             where lic.id = 10841258427);

--cotação
update itemcotacao
set tipocontrole  = 'VALOR',
    QUANTIDADE = 1,
    VALORUNITARIO = VALORTOTAL
where id in (select icot.id
             from licitacao lic
                      inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                      inner join solicitacaomaterial sol on sol.id = pc.SOLICITACAOMATERIAL_ID
                      inner join cotacao cot on cot.id = sol.COTACAO_ID
                      inner join lotecotacao lcot on lcot.COTACAO_ID = cot.id
                      inner join itemcotacao icot on icot.LOTECOTACAO_ID = lcot.id
             where lic.id = 10841258427);

--solicitação de compra
update itemsolicitacao
set QUANTIDADE = 1,
    PRECO = PRECOTOTAL
where id in (select item.id
             from licitacao lic
                      inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                      inner join solicitacaomaterial sol on sol.id = pc.SOLICITACAOMATERIAL_ID
                      inner join LOTESOLICITACAOMATERIAL lote on lote.SOLICITACAOMATERIAL_ID = sol.id
                      inner join itemsolicitacao item on item.LOTESOLICITACAOMATERIAL_ID = lote.id
             where lic.id = 10841258427);


--proposta
update ITEMPROPFORNEC itemp
set itemp.PRECO = itemp.PRECO * (select item.QUANTIDADE
                                 from licitacao lic
                                          inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                                          inner join loteprocessodecompra lote on lote.PROCESSODECOMPRA_ID = pc.id
                                          inner join itemprocessodecompra item on item.LOTEPROCESSODECOMPRA_ID = lote.id
                                          inner join itempropfornec ipf on ipf.ITEMPROCESSODECOMPRA_ID = item.id
                                 where lic.id = 10841258427
                                   and itemp.id = ipf.id)
where id in (select ipf.id
             from licitacao lic
                      inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                      inner join loteprocessodecompra lote on lote.PROCESSODECOMPRA_ID = pc.id
                      inner join itemprocessodecompra item on item.LOTEPROCESSODECOMPRA_ID = lote.id
                      inner join itempropfornec ipf on ipf.ITEMPROCESSODECOMPRA_ID = item.id
             where lic.id = 10841258427)
  and itemp.PRECO > 0;


--update lance pregão
update LANCEPREGAO lanc
set lanc.VALOR = lanc.VALOR * (select ipc.QUANTIDADE
                               from RODADAPREGAO rp
                                        inner join lancepregao lp on lp.RODADAPREGAO_ID = rp.id
                                        inner join itempregao ip on ip.id = rp.ITEMPREGAO_ID
                                        inner join itpreitpro ipip on ipip.ITEMPREGAO_ID = ip.id
                                        inner join itemprocessodecompra ipc on ipip.ITEMPROCESSODECOMPRA_ID = ipc.id
                               where lp.id = lanc.id
                                 and ip.PREGAO_ID = 10850851555)
where lanc.id in (select lp.id
                  from lancepregao lp
                           inner join RODADAPREGAO rod on rod.id = lp.RODADAPREGAO_ID
                           inner join ITEMPREGAO ip on ip.id = rod.ITEMPREGAO_ID
                  where ip.PREGAO_ID =  10850851555);

--update item vencedor pregão
update ITEMPREGAOLANCEVENCEDOR item
set item.VALOR = item.VALOR * (select ipc.QUANTIDADE
                               from itempregao ip
                                        inner join itpreitpro ipip on ipip.ITEMPREGAO_ID = ip.id
                                        inner join itemprocessodecompra ipc on ipip.ITEMPROCESSODECOMPRA_ID = ipc.id
                               where ip.ITEMPREGAOLANCEVENCEDOR_ID = item.id
                                 and ip.PREGAO_ID = 10850851555)
where item.id in (select iplv.id
                  from itempregaolancevencedor iplv
                           inner join itempregao ip on ip.ITEMPREGAOLANCEVENCEDOR_ID = iplv.id
                  where ip.PREGAO_ID = 10850851555);



--item status fornecedor
update ITEMSTATUSFORNECEDORLICIT isf
set isf.VALORUNITARIO = isf.VALORUNITARIO * (select item.QUANTIDADE
                                             from licitacao lic
                                                      inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                                                      inner join loteprocessodecompra lote on lote.PROCESSODECOMPRA_ID = pc.id
                                                      inner join itemprocessodecompra item on item.LOTEPROCESSODECOMPRA_ID = lote.id
                                                      inner join ITEMSTATUSFORNECEDORLICIT itemst on itemst.ITEMPROCESSOCOMPRA_ID = item.id
                                             where lic.id = 10841258427
                                               and itemst.id = isf.id)
where isf.id in (select itemst.id
                 from licitacao lic
                          inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                          inner join loteprocessodecompra lote on lote.PROCESSODECOMPRA_ID = pc.id
                          inner join itemprocessodecompra item on item.LOTEPROCESSODECOMPRA_ID = lote.id
                          inner join ITEMSTATUSFORNECEDORLICIT itemst on itemst.ITEMPROCESSOCOMPRA_ID = item.id
                 where lic.id = 10841258427);


--processo de compra
update itemprocessodecompra
set QUANTIDADE = 1
where id in (select item.id
             from licitacao lic
                      inner join processodecompra pc on pc.id = lic.PROCESSODECOMPRA_ID
                      inner join loteprocessodecompra lote on lote.PROCESSODECOMPRA_ID = pc.id
                      inner join itemprocessodecompra item on item.LOTEPROCESSODECOMPRA_ID = lote.id
             where lic.id = 10841258427);
