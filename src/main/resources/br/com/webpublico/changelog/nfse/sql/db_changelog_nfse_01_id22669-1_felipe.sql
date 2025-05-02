insert into nfse_authority ( name
) values ('ROLE_CONTADOR' );

insert into nfse_user_authority ( user_id, authority_name
) values ((select id from nfse_user where login = 'admin'),'ROLE_CONTADOR' );
