merge into mensagemcontribuinte
    using (
        select aud.id as m_id, max(us.id) as u_id
        from mensagemcontribuinte_aud aud
                 inner join revisaoauditoria rev on rev.id = aud.REV
                 inner join usuariosistema us on us.login = rev.usuario
        group by aud.id)
    on (m_id = mensagemcontribuinte.id)
    when matched then
        update set enviadapor_id = u_id
