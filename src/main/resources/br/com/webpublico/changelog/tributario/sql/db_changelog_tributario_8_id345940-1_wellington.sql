declare
v_inscricao numeric(19,0);
begin
update cadastroeconomico
set inscricaocadastral = null
where exists (select 1
              from cadastroeconomico_aud aud
                       inner join pessoafisica pf on pf.id = aud.pessoa_id
              where aud.id = cadastroeconomico.id
                and aud.inscricaocadastral is null)
   or (exists(select 1 from pessoafisica where id = cadastroeconomico.pessoa_id)
    and length(cadastroeconomico.inscricaocadastral) = 14);

select to_number(max(inscricaocadastral)) + 1
into v_inscricao
from cadastroeconomico ce
         inner join pessoafisica pf on pf.id = ce.pessoa_id;
for registro in (select ce.id
                     from cadastroeconomico ce
                              inner join pessoafisica pf on pf.id = ce.pessoa_id
                     where ce.inscricaocadastral is null
                     order by ce.id)
        loop
update cadastroeconomico
set inscricaocadastral = to_char(v_inscricao)
where id = registro.id;
v_inscricao
:= v_inscricao + 1;
end loop;
end;
