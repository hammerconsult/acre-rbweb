create or replace procedure proc_insere_valorpossivel(p_identificacao in varchar,
                                                      p_codigo in integer,
                                                      p_valor in varchar)
    is
begin
    insert into valorpossivel (id, atributo_id, dataregistro, codigo, valor)
    select hibernate_sequence.nextval,
           (select id from atributo where identificacao = p_identificacao),
           current_date,
           p_codigo,
           p_valor
    from dual
    where not exists (select 1
                      from valorpossivel
                      where atributo_id = (select id from atributo where identificacao = p_identificacao)
                        and codigo = p_codigo);
end;
