create
or replace procedure proc_insere_atributo_cadastro(p_inscricao in varchar, p_id_atributo in numeric, p_valor_atributo in varchar)
    is
    v_classe_atributo varchar(100);
    v_id_valorpossivel
numeric;
    v_id_lote
numeric;
    v_id
numeric;
begin
select classedoatributo
into v_classe_atributo
from atributo
where id = p_id_atributo;
select id
into v_id_valorpossivel
from valorpossivel
where atributo_id = p_id_atributo
  and lower(valor) = lower(p_valor_atributo);

if
(lower(v_classe_atributo) = 'lote') then
select lote_id
into v_id_lote
from cadastroimobiliario
where inscricaocadastral = p_inscricao;

select count(1)
into v_id
from valoratributo va
         inner join lote_valoratributo lva on lva.atributos_id = va.id
    and lva.atributos_key = p_id_atributo
where lva.lote_id = v_id_lote;

if
(v_id = 0) then
            dbms_output.put_line('insert');
select hibernate_sequence.nextval
into v_id
from dual;

insert into valoratributo (id, atributo_id, valordiscreto_id)
values (v_id, p_id_atributo, v_id_valorpossivel);

insert into lote_valoratributo (id, lote_id, atributos_id, atributos_key)
values (hibernate_sequence.nextval, v_id_lote, v_id, p_id_atributo);
else
            dbms_output.put_line('update');
update valoratributo
set valordiscreto_id = v_id_valorpossivel
where id = (select lva.atributos_id
            from lote_valoratributo lva
            where lva.lote_id = v_id_lote
              and lva.atributos_key = p_id_atributo);
end if;
end if;
    if
(lower(v_classe_atributo) = 'construcao') then
        for construcao in (select c.id
                           from construcao c
                                    inner join cadastroimobiliario ci on ci.id = c.imovel_id
                           where ci.inscricaocadastral = p_inscricao)
            loop
select count(1)
into v_id
from valoratributo va
         inner join construcao_valoratributo cva on cva.atributos_id = va.id
    and cva.atributos_key = p_id_atributo
where cva.construcao_id = construcao.id;

if
(v_id = 0) then
                    dbms_output.put_line('insert');
select hibernate_sequence.nextval
into v_id
from dual;

insert into valoratributo (id, atributo_id, valordiscreto_id)
values (v_id, p_id_atributo, v_id_valorpossivel);

insert into construcao_valoratributo (id, construcao_id, atributos_id, atributos_key)
values (hibernate_sequence.nextval, construcao.id, v_id, p_id_atributo);
else
                    dbms_output.put_line('insert');
update valoratributo
set valordiscreto_id = v_id_valorpossivel
where id = (select cva.atributos_id
            from construcao_valoratributo cva
            where cva.construcao_id = construcao.id
              and cva.atributos_key = p_id_atributo);

end if;
end loop;
end if;
exception
    when others then
        dbms_output.put_line('erro ao inserir atributo ' || p_id_atributo || ' com valor ' || p_valor_atributo ||
                             ' para o cadastro ' || p_inscricao || sqlerrm);
end;
