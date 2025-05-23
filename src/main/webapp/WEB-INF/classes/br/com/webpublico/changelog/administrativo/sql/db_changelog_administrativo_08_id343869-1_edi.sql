update eventobem set ESTADOINICIAL_ID = 10939560859 where id = 10939578781;
delete from eventobem where id = 10939578683;
delete from ITEMAPROVACAOTRANSFBEM where id = 10939578683;
delete from estadobem where id in (10939578682);
delete from APROVACAOTRANSFERENCIABEM where id = 10939578681;
update LOTEEFETTRANSFBEM set SOLICITACAOTRANSFERENCIA_ID = 10939560857 where id = 10941042470;
