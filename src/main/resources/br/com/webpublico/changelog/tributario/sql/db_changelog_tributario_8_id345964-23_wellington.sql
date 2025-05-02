create or replace procedure proc_insere_itempontuacao_1_atributo(p_pontuacao in varchar,
                                                                 p_atributo in varchar,
                                                                 p_valorpossivel in varchar,
                                                                 p_pontos in numeric)
    is
    v_id_itempontuacao numeric;
begin
select HIBERNATE_SEQUENCE.nextval
into v_id_itempontuacao
from dual;

insert into itempontuacao (id, pontos, PONTUACAO_ID)
values (v_id_itempontuacao, p_pontos, (select id from pontuacao where identificacao = p_pontuacao));

insert into ipont_vpos (ITEMPONTUACAO_ID, VALORES_ID)
select v_id_itempontuacao, id
from valorpossivel
where atributo_id = (select id from atributo where identificacao = p_atributo)
  and valor = p_valorpossivel;
end;
