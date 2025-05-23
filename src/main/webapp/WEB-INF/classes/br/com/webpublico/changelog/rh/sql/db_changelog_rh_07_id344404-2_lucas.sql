update PRESTADORSERVICOS p
set p.DATACADASTRO = (select revisao.DATAHORA
                      from PRESTADORSERVICOS prestador
                               inner join PRESTADORSERVICOS_AUD auditoria on auditoria.id = prestador.ID
                               inner join revisaoauditoria revisao on auditoria.rev = revisao.id
                      where auditoria.REVTYPE = 0
                        and prestador.ID = p.id)
where p.id in (select prestador.id
               from PRESTADORSERVICOS prestador
                        inner join PRESTADORSERVICOS_AUD auditoria on auditoria.id = prestador.ID
                        inner join revisaoauditoria revisao on auditoria.rev = revisao.id
               where auditoria.REVTYPE = 0);
