insert into perfilusuario
    SELECT HIBERNATE_SEQUENCE.nextval, id, 'ROLE_USER' from USUARIOSISTEMA;

insert into perfilusuario
    values(HIBERNATE_SEQUENCE.nextval, (SELECT ID FROM USUARIOSISTEMA WHERE LOGIN = 'mga'), 'ROLE_ADMIN');
