update AVALIACAOSOLCOMPRA ava set ava.USUARIOSISTEMA_ID = (
    select us.id from USUARIOSISTEMA us where LOGIN = (
        select rev.USUARIO from AVALIACAOSOLCOMPRA_aud aud
        inner join revisaoauditoria rev on rev.id = aud.REV
        where aud.ID = ava.id
    )
);
