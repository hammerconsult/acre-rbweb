create
or replace procedure proc_insere_pontuacao(p_identificacao in varchar,
                                                  p_atributo1 in varchar,
                                                  p_atributo2 in varchar)
    is
    v_id_pontuacao numeric;
    v_id_atributo1
numeric;
    v_id_atributo2
numeric;
begin
begin
select p.id
into v_id_pontuacao
from pontuacao p
where p.IDENTIFICACAO = p_identificacao;
exception
    when others then
        DBMS_OUTPUT.PUT_LINE('pontuação não registrada');
end;
delete
from ipont_vpos
where itempontuacao_id in (select item.id
                           from itempontuacao item
                           where item.PONTUACAO_ID = v_id_pontuacao);

delete
from itempontuacao item
where pontuacao_id = v_id_pontuacao;

delete
from PONTUACAO_ATRIBUTO
where PONTUACAO_ID = v_id_pontuacao;

delete
from pontuacao p
where p.id = v_id_pontuacao;

select HIBERNATE_SEQUENCE.nextval
into v_id_pontuacao
from dual;

select id
into v_id_atributo1
from atributo
where identificacao = p_atributo1;
select id
into v_id_atributo2
from atributo
where identificacao = p_atributo2;

insert into pontuacao (ID, EXERCICIO_ID, IDENTIFICACAO, TIPOPONTOIPTU, UTILIZAPONTOPREDIAL)
values (v_id_pontuacao, (select id from exercicio where ano = 2024), p_identificacao, 'VALOR_VENAL', 0);

insert into pontuacao_atributo (PONTUACAO_ID, ATRIBUTOS_ID)
values (v_id_pontuacao, v_id_atributo1);
insert into pontuacao_atributo (PONTUACAO_ID, ATRIBUTOS_ID)
values (v_id_pontuacao, v_id_atributo2);
end;
