insert into tipoconta (id, CLASSEDACONTA, descricao, mascara, exercicio_id)
select HIBERNATE_SEQUENCE.nextval, tc.CLASSEDACONTA, tc.DESCRICAO, tc.MASCARA, pc.EXERCICIO_ID
from PLANODECONTAS pc
inner join tipoconta tc on pc.TIPOCONTA_ID = tc.id
where pc.EXERCICIO_ID <> tc.EXERCICIO_ID
and not exists (select 1 from tipoconta t where  t.CLASSEDACONTA = tc.CLASSEDACONTA
                                                       and t.DESCRICAO = tc.descricao
                                                       and tc.mascara = t.mascara
                                                 and t.EXERCICIO_ID = pc.EXERCICIO_ID)
