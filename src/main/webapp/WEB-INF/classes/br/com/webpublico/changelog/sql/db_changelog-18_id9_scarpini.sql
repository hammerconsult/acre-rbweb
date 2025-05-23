update valorpossivel vp set codigo = SUBSTR(vp.valor, 0, INSTR(vp.valor, '-')-1)  where codigo is null;
update valorpossivel vp set codigo = 1 where trim(lower(valor)) = 'sim'  and codigo is null;
update valorpossivel vp set codigo = 2 where trim(lower(valor)) = 'não'  and codigo is null;
update valorpossivel vp set valor = trim(SUBSTR(vp.valor, INSTR(vp.valor, '-') +1));