call proc_insere_valorpossivel('ni_pedologia', 2, 'Inundavél-50%');
call proc_insere_valorpossivel('ni_pedologia', 3, 'Inundavél+50%');

update valoratributo
set VALORDISCRETO_ID = (select id
                        from valorpossivel
                        where trim(valor) = 'DE 11 A 30 ANOS'
                          and codigo = 2
                          and atributo_id = (select id from atributo where identificacao = 'ni_idade'))
where VALORDISCRETO_ID = (select id
                          from valorpossivel
                          where trim(valor) = 'DE 11 A 30 ANOS'
                            and codigo = 4
                            and atributo_id = (select id from atributo where identificacao = 'ni_idade'));

delete
from ipont_vpos
where valores_id = (select id
                    from valorpossivel
                    where trim(valor) = 'DE 11 A 30 ANOS'
                      and codigo = 4
                      and atributo_id = (select id from atributo where identificacao = 'ni_idade'));

delete
from valorpossivel
where id = (select id
            from valorpossivel
            where trim(valor) = 'DE 11 A 30 ANOS'
              and codigo = 4
              and atributo_id = (select id from atributo where identificacao = 'ni_idade'));

update valorpossivel
set codigo = 4
where trim(valor) = 'ATÉ 10 ANOS'
  and codigo = 5
  and atributo_id = (select id from atributo where identificacao = 'ni_idade');
