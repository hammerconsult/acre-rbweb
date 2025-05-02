update itemcotacao set tipocontrole = 'QUANTIDADE' where id in (
select item.id
from lotecotacao lote
inner join itemcotacao item on item.lotecotacao_id = lote.id
inner join cotacao cot on cot.id = lote.cotacao_id
inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id
inner join loteformulariocotacao lotefc on lotefc.formulariocotacao_id = fc.id
inner join itemloteformulariocotacao itemfc on itemfc.loteformulariocotacao_id = lotefc.id
where itemfc.objetocompra_id = item.objetocompra_id
and itemfc.tipocontrole  <> item.tipocontrole
);

update itemcontrato set tipocontrole = 'QUANTIDADE' where id in (
select ic.id
from itemcontrato ic
    left join itemcontratoitempropfornec prop  on prop.itemcontrato_id = ic.id
    left join itempropfornec ipf on ipf.id = prop.itempropostafornecedor_id
    left join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id
    left join itemsolicitacao itemSol on itemSol.id = ipc.itemsolicitacaomaterial_id
    left join itemcotacao iclic on iclic.id = itemsol.itemcotacao_id
    left join lotecotacao lotelic on lotelic.id = iclic.lotecotacao_id
    left join cotacao cotlic on cotlic.id = lotelic.cotacao_id
    left join formulariocotacao fcLic on fclic.id = cotlic.formulariocotacao_id
    left join loteformulariocotacao lfclic on lfclic.formulariocotacao_id = fclic.id
    left join itemloteformulariocotacao iflic on iflic.loteformulariocotacao_id = lfclic.id
    left join itemcontratoitempropdisp propostadispensa on propostadispensa.itemcontrato_id   = ic.id
    left join itempropostafornedisp itemPropDisp on itemPropDisp.id = propostadispensa.itempropfornecdispensa_id
    left join itemprocessodecompra itemdisp on itemdisp.id = itemPropDisp.itemprocessodecompra_id
    left join itemsolicitacao itemSolDisp on itemSolDisp.id = itemdisp.itemsolicitacaomaterial_id
    left join itemcotacao icdisp on icdisp.id = itemSolDisp.itemcotacao_id
    left join lotecotacao lotedisp on lotedisp.id = icdisp.lotecotacao_id
    left join cotacao cotdisp on cotdisp.id = lotedisp.cotacao_id
    left join formulariocotacao fcdisp on fcdisp.id = cotdisp.formulariocotacao_id
    left join loteformulariocotacao lfcdisp on lfcdisp.formulariocotacao_id = fcdisp.id
    left join itemloteformulariocotacao ifdisp on ifdisp.loteformulariocotacao_id = lfcdisp.id
    left join itemcontratovigente icv on icv.itemcontrato_id = ic.id
    left join itemcotacao iccv on iccv.id = icv.itemcotacao_id
    left join lotecotacao lotecv on lotecv.id = iccv.lotecotacao_id
    left join cotacao cotcv on cotcv.id = lotecv.cotacao_id
    left join formulariocotacao fccv on fccv.id = cotcv.formulariocotacao_id
    left join loteformulariocotacao lfccv on lfccv.formulariocotacao_id = fccv.id
    left join itemloteformulariocotacao ifcv on ifcv.loteformulariocotacao_id = lfccv.id
    where coalesce(iclic.objetocompra_id, icdisp.objetocompra_id, iccv.objetocompra_id) = coalesce(iflic.objetocompra_id, ifdisp.objetocompra_id, ifcv.objetocompra_id)
    and coalesce(iclic.tipocontrole, icdisp.tipocontrole, iccv.tipocontrole) <> ic.tipocontrole
    );
