insert into nfse_user ( id, login, password_hash, first_name, last_name, email, image_url, activated, lang_key, activation_key, reset_key, created_by, created_date, reset_date, last_modified_by, last_modified_date, dicasenha
) values ( hibernate_sequence.nextval, 'admin', '$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC', 'Administrator', 'Administrator', 'admin@localhost', '', '1', 'pt-br', NULL, NULL, 'daniel.pereira', current_date, NULL, '', NULL, NULL);

insert into nfse_authority ( name
) values ('ROLE_ADMIN' );

insert into nfse_authority ( name
) values ('ROLE_USER' );

insert into nfse_user_authority ( user_id, authority_name
) values (hibernate_sequence.currval,'ROLE_ADMIN' );

insert into nfse_user_authority ( user_id, authority_name
) values (hibernate_sequence.currval,'ROLE_USER' );
