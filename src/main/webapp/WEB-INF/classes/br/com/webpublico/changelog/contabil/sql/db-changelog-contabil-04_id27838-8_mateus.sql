merge into obrigacaoapagar o
using (
        select
          obr.id,
          fonte.id as fontederecursos_id,
          c.id     as contadespesa_id
        from OBRIGACAOAPAGAR obr
          inner join despesaorc desp on obr.DESPESAORC_ID = desp.ID
          inner join PROVISAOPPADESPESA provdesp on desp.PROVISAOPPADESPESA_ID = provdesp.ID
          inner join conta c on provdesp.CONTADEDESPESA_ID = c.ID
          inner join fontedespesaorc fontdesp on obr.FONTEDESPESAORC_ID = fontdesp.id
          inner join PROVISAOPPAFONTE provfont on provfont.id = fontdesp.PROVISAOPPAFONTE_ID
          inner join CONTADEDESTINACAO cd on provfont.DESTINACAODERECURSOS_ID = cd.ID
          inner join FONTEDERECURSOS fonte on cd.FONTEDERECURSOS_ID = fonte.ID
        where obr.TRANSPORTADO = 0
              and trunc(obr.DATALANCAMENTO) > to_date('01/01/2019', 'dd/mm/yyyy')
      ) ob on (ob.id = o.id)
when matched then update set o.contadespesa_id = ob.contadespesa_id, o.fontederecursos_id = ob.fontederecursos_id
