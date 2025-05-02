update PESSOAFISICA
set nome = trim(replace(replace(replace(nome, '   ', ' '), '  ', ' '), '  ', ' '))
where id in (select pf.id
             from pessoafisica pf
             where pf.nome like '%  %'
               and exists(select 1
                          from PESSOA_PERFIL perfil
                          where perfil.PERFIL in ('PERFIL_RH', 'PERFIL_DEPENDENTE', 'PERFIL_PENSIONISTA')
                            and perfil.ID_PESSOA = pf.id));
