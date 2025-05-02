update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '101'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;

update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '102'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;

update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '133'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;

update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '163'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '357'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;

update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '263'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '363'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '396'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '600'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '353'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '354'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '355'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '356'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo = '369'
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
update FICHAFINANCEIRAFP
set DIASTRABALHADOS = (select item.VALORREFERENCIA
                       from ITEMFICHAFINANCEIRAFP item
                                inner join eventofp e on item.EVENTOFP_ID = e.ID
                       where item.FICHAFINANCEIRAFP_ID = FICHAFINANCEIRAFP.id
                         and e.codigo in ('275', '280', '290')
                         and ROWNUM = 1)
where DIASTRABALHADOS is null;
