delete
from menu
where CAMINHO = '/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/lista.xhtml';

insert into menu (id, label, caminho, pai_id, ordem)
values (hibernate_sequence.nextval, 'BLOQUEIO DE TRANSFERÊNCIA DE PROPRIETÁRIO',
        '/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/lista.xhtml',
        (select id from menu where label = 'CADASTRO TÉCNICO IMOBILIÁRIO'),
        (select max(ordem) from menu where label = 'CADASTRO TÉCNICO IMOBILIÁRIO') + 10);
