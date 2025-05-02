create or replace procedure proc_atualizar_fator_valorpossivel(p_identificao in varchar,
                                                               p_codigo in integer,
                                                               p_fator in numeric)
is
begin
update valorpossivel set fator = p_fator
where atributo_id = (select id from atributo where identificacao = p_identificao)
  and codigo = p_codigo;
end;
