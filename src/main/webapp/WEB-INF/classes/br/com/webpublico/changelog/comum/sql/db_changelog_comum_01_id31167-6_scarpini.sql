delete from CONFIGURACAOWEBSERVICE where TIPO = 'EMAIL';
delete from configuracaoemail;

INSERT INTO configuracaoemail (id, host, port, username, email, password, protocol, tls)
values (HIBERNATE_SEQUENCE.nextval,
        'email-smtp.us-east-1.amazonaws.com',
        '587',
        'AKIAYCWDKEISDUCZ4VEH',
        'noreply@webpublico.com.br',
        'BNnHgZDU5Yyvt2crpA5L7NYFtMHXWd8mEUNkisUCKOTo',
        'smtp', 1);

