update itemloteformulariocotacao item
set item.tipobeneficio = (select lote.tipobeneficio
                          from loteformulariocotacao lote
                          where lote.id = item.loteformulariocotacao_id
                            and lote.tipobeneficio is not null)
where item.tipobeneficio is null;

update itemloteformulariocotacao item set item.tipobeneficio = 'SEM_BENEFICIO'
where item.tipobeneficio is null;

update itemcotacao item
set item.tipobeneficio = (select lote.tipobeneficio
                          from lotecotacao lote
                          where lote.id = item.lotecotacao_id
                            and lote.tipobeneficio is not null)
where item.tipobeneficio is null;

update itemcotacao item set item.tipobeneficio = 'SEM_BENEFICIO'
where item.tipobeneficio is null;
