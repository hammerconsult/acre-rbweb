merge into usuarioweb uw
    using
        (select suw.id as usuario_id,
                max(coalesce(pf.id, pj.id)) as pessoa_id
         from usuarioweb suw
                  left join pessoafisica pf on pf.cpf = suw.login
                  left join pessoajuridica pj on pj.cnpj = suw.login
         where suw.pessoa_id is null and (pf.id is not null or pj.id is not null)
         group by suw.id) dados
    on (dados.usuario_id = uw.id)
    when matched then
        update set uw.pessoa_id = dados.pessoa_id
