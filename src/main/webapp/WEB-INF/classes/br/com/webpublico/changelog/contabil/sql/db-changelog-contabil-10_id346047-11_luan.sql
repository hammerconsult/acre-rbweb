insert into paginamenuhpaginapref
select hibernate_sequence.nextval,
       pagrelevantes.nome,
       (select id from paginamenuhorizportal where tipo = 'PADRAO'),
       pagrelevantes.paginaprefeituraportal_id,
       null, null, null, null, null
from paginarelevpaginapref pagrelevantes
;

insert into paginamenuhpaginapref
select hibernate_sequence.nextval,
       pagprincipal.nome,
       (select id from paginamenuhorizportal where tipo = 'PRINCIPAL'),
       pagprincipal.paginaprefeituraportal_id,
       null, null, null, null,
       pagprincipal.descricao
from paginaprincpaginapref pagprincipal
;

insert into paginamenuhpaginapref
values (hibernate_sequence.nextval,
        'Portal da Prefeitura',
        (select id from paginamenuhorizportal where tipo = 'PADRAO'),
        null, null, 1, 'https://www.riobranco.ac.gov.br/', 'fas fa-landmark', null
       );

insert into paginamenuhpaginapref
values (hibernate_sequence.nextval,
        'Calendário de Feriados',
        (select id from paginamenuhorizportal where tipo = 'PADRAO'),
        null, null, 2, 'http://portalcgm.riobranco.ac.gov.br/portal/servidores/calendario-de-feriados-e-pontos-facultativos-decreto/',
        'fas fa-calendar-days', null
       );

insert into paginamenuhpaginapref
values (hibernate_sequence.nextval,
        'Prestação de Contas',
        (select id from paginamenuhorizportal where tipo = 'PADRAO'),
        null, null, 3, 'anexo-contabilidade', 'fas fa-file-invoice-dollar', null
       );

insert into paginamenuhpaginapref
values (hibernate_sequence.nextval,
        'Diárias e Passagens',
        (select id from paginamenuhorizportal where tipo = 'PADRAO'),
        null, null, 4, 'diaria', 'fas fa-suitcase', null
       );

insert into paginamenuhpaginapref
values (hibernate_sequence.nextval,
        'Avisos de Licitações',
        (select id from paginamenuhorizportal where tipo = 'PADRAO'),
        null, null, 5, 'licitacao-outras', 'fas fa-gavel', null
       );

insert into paginamenuhpaginapref
values (hibernate_sequence.nextval,
        'Emendas Parlamentares',
        (select id from paginamenuhorizportal where tipo = 'PADRAO'),
        null, null, 6, null, 'fas fa-money-check-dollar', null
       );

insert into paginamenuhpaginapref
values (hibernate_sequence.nextval,
        'Estagiários',
        (select id from paginamenuhorizportal where tipo = 'PADRAO'),
        null, null, 7, 'http://portalcgm.riobranco.ac.gov.br/portal/estagiarios/demonstrativos-dos-recrutamentos/',
        'fas fa-users', null
       );

insert into paginamenuhpaginapref
values (hibernate_sequence.nextval,
        'IPTU',
        (select id from paginamenuhorizportal where tipo = 'PADRAO'),
        null, null, 8, 'https://portalcidadao.riobranco.ac.gov.br/emissao-iptu/', 'fas fa-house', null
       );

insert into paginamenuhpaginapref
values (hibernate_sequence.nextval,
        'Concurso Público',
        (select id from paginamenuhorizportal where tipo = 'PADRAO'),
        null, null, 9, 'https://www.riobranco.ac.gov.br/?page_id=88287', 'fas fa-clipboard-list', null
       );
