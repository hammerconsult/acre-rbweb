update EVENTOFP set CONSIGNACAO = 1, EVENTOFPAGRUPADOR_ID = (select ev.ID from EVENTOFP ev where ev.CODIGO = 935) where CODIGO = 935;
update EVENTOFP set CONSIGNACAO = 1, EVENTOFPAGRUPADOR_ID = (select ev.ID from EVENTOFP ev where ev.CODIGO = 936) where CODIGO = 936;
