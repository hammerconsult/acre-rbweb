merge into obrigacaoapagar o
using (
        select
          obr.id,
          fonteDest.id as fontederecursos_id,
          cDest.id     as contadespesa_id
        from OBRIGACAOAPAGAR obr
          inner join despesaorc desp on obr.DESPESAORC_ID = desp.ID
          inner join PROVISAOPPADESPESA provdesp on desp.PROVISAOPPADESPESA_ID = provdesp.ID
          inner join conta c on provdesp.CONTADEDESPESA_ID = c.ID
          inner join CONTAEQUIVALENTE ce on c.id = ce.CONTAORIGEM_ID
          inner join conta cDest on ce.CONTADESTINO_ID = cdest.id
          inner join fontedespesaorc fontdesp on obr.FONTEDESPESAORC_ID = fontdesp.id
          inner join PROVISAOPPAFONTE provfont on provfont.id = fontdesp.PROVISAOPPAFONTE_ID
          inner join CONTADEDESTINACAO cd on provfont.DESTINACAODERECURSOS_ID = cd.ID
          inner join FONTEDERECURSOS fonte on cd.FONTEDERECURSOS_ID = fonte.ID
          inner join FONTEDERECURSOSEQUIVALENTE fre on fonte.ID = fre.FONTEDERECURSOSORIGEM_ID
          inner join FONTEDERECURSOS fonteDest on fre.FONTEDERECURSOSDESTINO_ID = fonteDest.id
        where obr.TRANSPORTADO = 1
              and trunc(obr.DATALANCAMENTO) = to_date('01/01/2019', 'dd/mm/yyyy')
      ) ob on (ob.id = o.id)
when matched then update set o.contadespesa_id = ob.contadespesa_id, o.fontederecursos_id = ob.fontederecursos_id
