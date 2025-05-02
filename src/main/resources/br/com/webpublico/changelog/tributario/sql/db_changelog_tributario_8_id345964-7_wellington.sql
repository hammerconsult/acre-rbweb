create
or replace procedure proc_insere_itempontuacao(p_pontuacao in varchar,
                                                      p_atributo1 in varchar,
                                                      p_valorpossivel1 in varchar,
                                                      p_atributo2 in varchar,
                                                      p_valorpossivel2 in varchar,
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
where atributo_id in (select id from atributo where identificacao in (p_atributo1, p_atributo2))
  and valor in (p_valorpossivel1, p_valorpossivel2);
end;
