insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM)
select HIBERNATE_SEQUENCE.nextval, 'TEMPLATE DE E-MAIL', '/comum/templateemail/lista.xhtml',
       (select id from menu where LABEL = 'CADASTROS GERAIS' and CAMINHO is null),
       310 from dual
where not exists (select 1 from menu where LABEL = 'TEMPLATE DE E-MAIL')
