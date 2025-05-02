merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 1
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 2
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 3
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 4
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 5
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 6
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 7
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 8
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 9
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes =  10
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 11
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2019 and iff.mes = 12
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 1
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 2
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 3
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 4
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 5
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 6
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 7
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 8
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 9
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes =  10
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 11
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.ANO = 2020 and iff.mes = 12
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

merge into ITEMFICHAFINANCEIRAFP item using (
    select 'SEQ' || iff.id as sequencial, iff.id as itemId
    from FICHAFINANCEIRAFP ficha
             inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID
             inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.ID
    where iff.sequencial = '1'
) itemf on (itemf.itemId = item.id)
when matched then update set item.SEQUENCIAL = itemf.sequencial;

