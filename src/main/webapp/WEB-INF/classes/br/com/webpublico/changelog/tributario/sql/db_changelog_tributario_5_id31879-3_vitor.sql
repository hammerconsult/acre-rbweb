update VALORATRIBUTO
set VALORDISCRETO_ID =6192304
where id in (
    select id
    from VALORATRIBUTO
    where VALORDISCRETO_ID is null
      and id in (select ATRIBUTOS_ID
                 from LOTE_VALORATRIBUTO
                 where ATRIBUTOS_KEY = 6192081));

update VALORATRIBUTO
set VALORDISCRETO_ID =6192119
where id in (
    select id
    from VALORATRIBUTO
    where VALORDISCRETO_ID is null
      and id in (select ATRIBUTOS_ID
                 from LOTE_VALORATRIBUTO
                 where ATRIBUTOS_KEY = 6192082));

update VALORATRIBUTO
set VALORDISCRETO_ID =6192129
where id in (
    select id
    from VALORATRIBUTO
    where VALORDISCRETO_ID is null
      and id in (select ATRIBUTOS_ID
                 from LOTE_VALORATRIBUTO
                 where ATRIBUTOS_KEY = 6192084));

update VALORATRIBUTO
set VALORDISCRETO_ID =6192139
where id in (
    select id
    from VALORATRIBUTO
    where VALORDISCRETO_ID is null
      and id in (select ATRIBUTOS_ID
                 from LOTE_VALORATRIBUTO
                 where ATRIBUTOS_KEY = 6192086));
