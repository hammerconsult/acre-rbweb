insert into CONFIGCONTABILFONTE values (hibernate_sequence.nextval,
                                        (select id from CONFIGURACAOCONTABIL),
                                        (select fr.id from fontederecursos fr
                                                               inner join exercicio ex on fr.EXERCICIO_ID = ex.ID
                                         where ex.ANO = 2022 and fr.CODIGO = '101'));

insert into CONFIGCONTABILFONTE values (hibernate_sequence.nextval,
                                        (select id from CONFIGURACAOCONTABIL),
                                        (select fr.id from fontederecursos fr
                                                               inner join exercicio ex on fr.EXERCICIO_ID = ex.ID
                                         where ex.ANO = 2023 and fr.CODIGO = '101'));
insert into CONFIGCONTABILFONTE values (hibernate_sequence.nextval,
                                        (select id from CONFIGURACAOCONTABIL),
                                        (select fr.id from fontederecursos fr
                                                               inner join exercicio ex on fr.EXERCICIO_ID = ex.ID
                                         where ex.ANO = 2024 and fr.CODIGO = '1500'));
