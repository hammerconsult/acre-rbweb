
UPDATE ITEMFICHAFINANCEIRAFP item
set VALORBASEDECALCULO = round((item.VALOR / (select vref.VALOR
                                              from VALORREFERENCIAFP vref
                                                       INNER JOIN REFERENCIAFP R on vref.REFERENCIAFP_ID = R.ID
                                              where r.CODIGO = '1'
                                                and to_date(to_char(item.mes) || '/' || to_char(item.ano), 'mm/yyyy') between vref.iniciovigencia
                                                  and coalesce(vref.finalvigencia,
                                                               to_date(to_char(item.mes) || '/' || to_char(item.ano), 'mm/yyyy')))
    * 100), 2)
where item.EVENTOFP_ID in (6190498, 6190176)
  and (select lfp.id
       from LANCAMENTOFP lfp
                inner join VINCULOFP vfp on lfp.VINCULOFP_ID = vfp.ID
                inner join FICHAFINANCEIRAFP f on vfp.ID = f.VINCULOFP_ID
                inner join ITEMFICHAFINANCEIRAFP it on f.ID = it.FICHAFINANCEIRAFP_ID
                inner join FOLHADEPAGAMENTO fp on f.FOLHADEPAGAMENTO_ID = fp.ID
       where it.id = item.id
         and fp.TIPOFOLHADEPAGAMENTO = lfp.TIPOFOLHADEPAGAMENTO
         and it.eventofp_id = lfp.eventofp_id
         and item.MES between extract(month from lfp.MESANOINICIAL)
           and coalesce(extract(month from lfp.MESANOFINAL), item.MES)
         and item.ANO between extract(year from lfp.MESANOINICIAL)
           and coalesce(extract(year from lfp.MESANOFINAL), item.ANO)
         and ROWNUM = 1) is not null
  and item.VALORBASEDECALCULO = 0;
